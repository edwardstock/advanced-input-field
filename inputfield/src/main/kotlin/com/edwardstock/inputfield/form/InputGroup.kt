/*
 * Copyright (C) by Eduard Maximovich. 2022
 * @link <a href="https://github.com/edwardstock">Profile</a>
 *
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.edwardstock.inputfield.form

import android.content.Context
import android.text.InputFilter
import android.widget.TextView
import androidx.core.view.isVisible
import com.edwardstock.inputfield.InputField
import com.edwardstock.inputfield.OnFormValidateListener
import com.edwardstock.inputfield.OnTextChangedListener
import com.edwardstock.inputfield.R
import com.edwardstock.inputfield.form.validators.CompareValidator
import com.edwardstock.inputfield.form.validators.Validator
import kotlinx.coroutines.*
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaField

/**
 * Auxiliary class for grouping input fields and validators
 */
data class InputGroupData(
    val inputWrapper: InputWrapper,
    val validators: MutableList<Validator>,
    val errorView: TextView? = null,
    val required: Boolean = false
) {
    fun hasExternalErrorView(): Boolean = errorView != null

    fun setError(error: CharSequence?) {
        if (errorView != null) {
            errorView.isVisible = true
            errorView.text = error
        } else {
            inputWrapper.inputField.error = error
        }

    }

    fun clearError() {
        if (errorView != null) {
            errorView.isVisible = false
            errorView.text = null
        } else {
            inputWrapper.inputField.error = null
        }
    }
}

class InputGroup(
    context: Context,
    private val targetScope: CoroutineScope? = null,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
) {
    private val scope: CoroutineScope
    private val resources = context.resources
    var requiredLabelSuffix: CharSequence? = null

    init {
        scope = (targetScope ?: CoroutineScope(SupervisorJob())) + dispatcher
    }

    class Builder(private val group: InputGroup) {
        fun add(input: InputField, vararg validator: Validator): Builder {
            group.addInput(input, validator.toList())
            return this
        }

        fun add(input: InputField, required: Boolean, vararg validator: Validator): Builder {
            group.addInput(input, validator.toList(), required)
            return this
        }

        fun add(input: InputField, required: Boolean, errorView: TextView?, vararg validator: Validator): Builder {
            group.addInput(input, validator.toList(), required, errorView)
            return this
        }

        fun add(input: InputWrapper, vararg validator: Validator): Builder {
            group.addInput(input, validator.toList())
            return this
        }

        fun add(input: InputWrapper, required: Boolean, vararg validator: Validator): Builder {
            group.addInput(input, validator.toList(), required)
            return this
        }

        fun add(input: InputWrapper, required: Boolean, errorView: TextView?, vararg validator: Validator): Builder {
            group.addInput(input, validator.toList(), required, errorView)
            return this
        }
    }

    val inputs: MutableMap<String, InputGroupData> = HashMap()
    private var requiredCount = 0
    private val validStates: MutableMap<String, Boolean> = HashMap()
    private val inputData: MutableMap<String, String?> = HashMap()
    private val relatedInputs: MutableMap<String, String> = HashMap()

    private val textChangedListeners = ArrayList<OnTextChangedListener>()
    private val formValidateListeners = ArrayList<OnFormValidateListener>()

    /**
     * Setup input group using DSL builder
     * @param builder - builder
     * @return builder
     */
    fun setup(builder: Builder.() -> Unit): Builder {
        return Builder(this).apply(builder)
    }

    /**
     * Return input's auxiliary information
     * @see InputGroupData
     */
    fun getInputData(input: InputField): InputGroupData? {
        return input.fieldName?.let {
            inputs[it]
        }
    }

    /**
     * Mark input as required or not
     *
     * This method will trigger validation
     *
     * @param input - input field
     * @param required - is input required
     * @return this
     */
    fun setInputRequired(input: InputField, required: Boolean) {
        input.fieldName?.let { fieldName ->
            inputs[fieldName]?.let { groupData ->
                val newIgp = groupData.copy(required = required)
                inputs[fieldName] = newIgp
                requiredCount += if (required) 1 else -1
                validStates[fieldName] = !required

                if (required) {
                    setLabelSuffix(input)
                } else {
                    removeLabelSuffix(input)
                }

                scope.launch {
                    validate(fieldName, true)
                }
            }
        }
    }

    /**
     * Add to label suffix for required inputs
     * For example: "Email *" or "Email (required)" where "*" or "(required)" is suffix
     * Works only for [InputField]
     *
     * @param suffix - label suffix
     */
    fun setRequiredLabelSuffix(suffixRes: Int) {
        requiredLabelSuffix = resources.getString(suffixRes)
    }

    private fun setLabelSuffix(input: InputField) {
        if (requiredLabelSuffix.isNullOrBlank()) return

        val oldLabel = input.label
        if (oldLabel?.contains(requiredLabelSuffix!!) == false) {
            input.label = "$oldLabel $requiredLabelSuffix"
        }
    }

    private fun removeLabelSuffix(input: InputField) {
        if (requiredLabelSuffix.isNullOrBlank()) return

        val oldLabel = input.label
        if (oldLabel?.contains(requiredLabelSuffix!!) == true) {
            input.label = oldLabel.toString().replace(" ${requiredLabelSuffix!!}", "")
        }
    }

    /**
     * Add input to group
     *
     * @param input - input field
     * @param validators - list of validators
     * @param required - is input required
     * @param errorView - error view, if null, then input field will be used, otherwise all errors will be displayed in errorView
     * @return this
     */
    fun addInput(
        input: InputField,
        validators: List<Validator> = emptyList(),
        required: Boolean = false,
        errorView: TextView? = null
    ): InputGroup = addInput(InputWrapper(input), validators, required, errorView)

    /**
     * Add input to group
     *
     * @param input - input field
     * @param validators - list of validators
     * @param required - is input required
     * @param errorView - error view, if null, then input field will be used, otherwise all errors will be displayed in errorView
     * @return this
     * @see InputWrapper
     */
    fun addInput(
        input: InputWrapper,
        validators: List<Validator> = emptyList(),
        required: Boolean = false,
        errorView: TextView? = null
    ): InputGroup {
        assert(input.fieldName != null) { "Input field name is null" }
        val fieldName = input.fieldName!!

        val igp = InputGroupData(
            inputWrapper = input,
            validators = validators.toMutableList(),
            errorView = errorView,
            required = required,
        )

        if (required && input.isInputField && !requiredLabelSuffix.isNullOrBlank()) {
            setLabelSuffix(input.inputField)
        }

        input.inputField.addTextChangedSimpleListener {
            scope.launch {
                onInputChanged(fieldName, it)
            }
        }
        inputs[fieldName] = igp
        inputData[fieldName] = null

        requiredCount += if (required) 1 else 0
        validStates[fieldName] = !required
        return this
    }

    /**
     * Add validator for input
     *
     * @param input - input field
     * @param validator - validator
     * @return this
     * @throws IllegalStateException if input fieldName is null
     */
    fun addValidator(input: InputField, validator: Validator): InputGroup {
        getInput(input).validators.add(validator)
        return this
    }

    /**
     * Connect two inputs with compare validator
     *
     * If changed one input, another will be validated too
     *
     * @param input1 - input 1
     * @param input2 - input 2
     * @return this
     * @throws IllegalStateException if any of input field name is null
     */
    fun addValidateRelation(input1: InputField, input2: InputField): InputGroup {
        addValidator(input1, CompareValidator(input2))
        addValidator(input2, CompareValidator(input1))
        val f1 = input1.fieldName ?: throw IllegalStateException("Input 1 fieldName is null")
        val f2 = input2.fieldName ?: throw IllegalStateException("Input 2 fieldName is null")
        relatedInputs[f1] = f2
        relatedInputs[f2] = f1
        return this
    }

    /**
     * Add android's input filter [InputFilter]
     * @param field - input field
     * @param filter - input filter
     * @return this
     */
    fun addFilter(field: InputField, filter: InputFilter): InputGroup {
        return addFilter(InputWrapper(field), filter)
    }

    /**
     * Add android's input filter [InputFilter]
     * @param input - input field wrapper
     * @param filter - input filter
     * @return this
     */
    fun addFilter(input: InputWrapper, filter: InputFilter): InputGroup {
        val newFilters: MutableList<InputFilter> = input.filters.toMutableList()
        newFilters[input.filters.size] = filter
        input.filters = newFilters.toTypedArray()
        return this
    }

    /**
     * Remove android's input filter [InputFilter]
     * @param field - input field
     * @param filter - input filter
     * @return this
     */
    fun removeFilter(input: InputWrapper, filter: InputFilter): InputGroup {
        val newFilters = input.filters.filter { it != filter }.toTypedArray()
        input.filters = newFilters
        return this
    }

    /**
     * Remove android's input filter [InputFilter]
     * @param field - input field
     * @param filter - input filter
     * @return this
     */
    fun removeFilter(field: InputField, filter: InputFilter): InputGroup {
        return removeFilter(InputWrapper(field), filter)
    }

    /**
     * Remove all android's input filters
     * @param input - input field wrapper
     * @return this
     */
    fun removeAllFilters(input: InputWrapper): InputGroup {
        input.filters = emptyArray()
        return this
    }

    /**
     * Remove all android's input filters
     * @param field - input field
     * @return this
     */
    fun removeAllFilters(field: InputField): InputGroup {
        return removeAllFilters(InputWrapper(field))
    }

    /**
     * Add form validate listener [OnFormValidateListener]
     * @param listener - listener to add
     * @return this
     */
    fun addFormValidateListener(listener: OnFormValidateListener): InputGroup {
        formValidateListeners.add(listener)
        return this
    }

    /**
     * Remove form validate listener
     * @param listener - listener to remove
     * @return this
     */
    fun removeFormValidateListener(listener: OnFormValidateListener): InputGroup {
        formValidateListeners.remove(listener)
        return this
    }

    /**
     * Add text changed listener
     * @param listener - listener to add
     * @return this
     */
    fun addTextChangedListener(listener: OnTextChangedListener): InputGroup {
        textChangedListeners.add(listener)
        return this
    }

    /**
     * Remove text changed listener
     * @param listener - listener to remove
     * @return this
     */
    fun removeTextChangedListener(listener: OnTextChangedListener): InputGroup {
        textChangedListeners.remove(listener)
        return this
    }

    /**
     * Return the validation result of all inputs
     * @param notifyIsFormValid - notify listeners about form validation result
     * @return true if all inputs are valid
     */
    fun isValidForm(notifyIsFormValid: Boolean = false): Boolean {
        val valid = validStates.values.all { it }
        if (notifyIsFormValid) {
            formValidateListeners.forEach { it(valid) }
        }
        return valid
    }

    /**
     * Simulate that all inputs changed
     * Calls [onInputChanged] for each input
     */
    suspend fun triggerChanged() {
        inputs.forEach { entry ->
            onInputChanged(entry.key, entry.value.inputWrapper.inputField.text)
        }
    }

    /**
     * Validate all inputs
     * @param notifyIsFormValid - notify listeners about form validation result
     * @return true if all inputs are valid
     * @throws IllegalStateException if input with name not found
     * @see [validate(String, Boolean)]
     */
    suspend fun validate(notifyIsFormValid: Boolean = false): Boolean {
        var validCount = 0
        inputs.forEach { entry ->
            if (validate(entry.key, notifyIsFormValid)) {
                validCount++
            }
        }
        formValidateListeners.forEach { it(isValidForm()) }
        return validCount == inputs.size
    }

    /**
     * Validate specified input by it's fieldName
     * @param fieldName - input field name
     * @param notifyIsFormValid - notify listeners about form validation result
     * @return true if input is valid
     * @throws IllegalStateException if input with name not found
     */
    suspend fun validate(fieldName: String, notifyIsFormValid: Boolean = false): Boolean {
        val item = inputs[fieldName] ?: throw IllegalStateException("Input with name $fieldName not found")
        var invalidCount = 0
        if (item.validators.isEmpty() && item.required) {
            if (item.inputWrapper.inputField.text.isNullOrEmpty()) {
                invalidCount++
                val label = item.inputWrapper.inputField.label ?: resources.getString(R.string.aif_input_group_default_value_label)
                item.setError(resources.getString(R.string.aif_input_group_value_required, label))
            } else {
                item.clearError()
            }
        } else {
            for (v in item.validators) {
                // ignore non required empty values
                if (!item.required && item.inputWrapper.inputField.text.isNullOrEmpty()) {
                    continue
                }

                val value = item.inputWrapper.inputField.text
                if (value.isNullOrEmpty()) {
                    val label = item.inputWrapper.inputField.label ?: resources.getString(R.string.aif_input_group_default_value_label)
                    item.setError(resources.getString(R.string.aif_input_group_value_required, label))
                    invalidCount++
                } else {
                    val validState = v.validate(value)
                    if (!validState.isAcceptable()) {
                        invalidCount++
                        item.setError(v.errorMessage)
                    } else {
                        item.clearError()
                    }
                }
            }
        }

        validStates[fieldName] = invalidCount == 0

        if (notifyIsFormValid) {
            formValidateListeners.forEach { it(isValidForm()) }
        }

        return invalidCount == 0
    }

    /**
     * Reset all inputs
     * It clears all errors and sets valid state to false for required inputs
     * Also if input has external error view it will be hidden
     */
    fun reset() {
        inputs.forEach { entry ->
            entry.value.clearError()
            if (entry.value.hasExternalErrorView()) {
                entry.value.errorView?.isVisible = false
            }
            if (entry.value.required) {
                validStates[entry.key] = false
            }
        }
    }

    /**
     * Set error to input by fieldName
     * @param fieldName String used to find input
     * @param error String used to set error text
     * @throws IllegalStateException if input with [fieldName] not found
     */
    fun setError(fieldName: String, error: String?) {
        val item = inputs[fieldName] ?: throw IllegalStateException("Input with name $fieldName not found")
        item.setError(error)
    }

    /**
     * Set error to input by fieldName
     * @param fieldName String used to find input
     * @param error Throwable [Throwable.message] used to set error text, make sure it's not null
     */
    fun setError(fieldName: String, error: Throwable?) {
        val item = inputs[fieldName] ?: throw IllegalStateException("Input with name $fieldName not found")
        item.setError(error?.message)
    }

    /**
     * Set error to input by [InputField]
     * @param input InputField
     * @param error String used to set error text
     * @throws IllegalArgumentException if [InputField.fieldName] is null
     */
    fun setError(input: InputField, error: String?) {
        val fieldName = input.fieldName ?: throw IllegalArgumentException("Input fieldName is null")
        setError(fieldName, error)
    }

    /**
     * Set error to input by [InputField]
     * @param input InputField
     * @param error Throwable [Throwable.message] used to set error text, make sure it's not null
     * @throws IllegalArgumentException if [InputField.fieldName] is null
     */
    fun setError(input: InputField, error: Throwable?) {
        val fieldName = input.fieldName ?: throw IllegalArgumentException("Input fieldName is null")
        setError(fieldName, error)
    }

    /**
     * Get all values from inputs that already have been changed or filled with [setValues]
     */
    fun getValues(): Map<String, String?> {
        return inputData
    }

    /**
     * Get value from input by fieldName or null if not found
     */
    fun getValue(fieldName: String): String? {
        return inputData[fieldName]
    }

    /**
     * Fill inputs with the data [Map<String,String?>]
     * This method does not validate inputs
     * Every key should be corresponding to fieldName [InputWrapper.fieldName]
     * @param data Map<String,String?> data to fill inputs
     */
    fun setValues(data: Map<String, String?>) {
        data.forEach { entry ->
            val input = inputs[entry.key]
            input?.inputWrapper?.inputField?.setText(entry.value)
        }
    }

    /**
     * Map values from data object to form inputs
     * it supports only straight mapping, no nested objects
     */
    fun mapValuesFromData(data: Any?) {
        data?.javaClass?.kotlin?.memberProperties?.forEach {
            val fieldName = it.name
            val input = inputs[fieldName]

            input?.let { _input ->
                val value = if (formMapperConverters.containsKey(it.returnType.classifier)) {
                    formMapperConverters[it.returnType.classifier]!!.mapToString(it.get(data))
                } else {
                    it.get(data)?.toString()
                }
                _input.inputWrapper.inputField.setText(value)
            }
        }
    }

    /**
     * Map values from form inputs to data object
     * Target [T] should have a primary constructor with map<String,Any?> parameter and variables should be delegated to this map
     * ```
     * class User(val data: Map<String,Any?>) {
     *     val name: String by data
     *     val lastName: String? by data
     *     // etc
     * }
     * ```
     * @see mapValuesToDataMap
     */
    fun <T : Any> mapValuesToData(clazz: KClass<T>): T {
        return clazz.primaryConstructor?.call(mapValuesToDataMap(clazz))
            ?: throw IllegalStateException("No primary constructor with Map<String,Any?> found for class ${clazz.qualifiedName}")
    }


    /**
     * Maps inputs data to a map of [String] to [Any?] values.
     *
     * The map of values is obtained from a given class, using the Kotlin Reflection API to obtain the properties.
     * The data types of the values in the returned map are determined by the class' properties' return types,
     * and are transformed from the corresponding [String] values in the input, according to
     * the property's return type.
     *
     * If a property is annotated with the [Transient] annotation, it will be skipped and not included in the resulting map.
     *
     * @param clazz The class to map the values from.
     * @return A map of [String] to [Any?] values, representing the properties of the class with corresponding input data.
     * @throws IllegalStateException If a property's return type is not supported, or if a value in the input can't be transformed to the expected data type.
     */
    fun <T : Any> mapValuesToDataMap(clazz: KClass<T>): Map<String, Any?> {
        val out = mutableMapOf<String, Any?>()

        clazz.memberProperties.forEach { property ->
            val isTransient = property.javaField?.isAnnotationPresent(Transient::class.java) ?: false
            if (!isTransient) {
                val value = inputData[property.name]
                if (value != null) {
                    val result = when (property.returnType.classifier) {
                        Byte::class -> value.toInt().toByte()
                        Char::class -> value[0]
                        String::class -> value
                        Long::class -> value.toLong()
                        Int::class -> value.toInt()
                        Boolean::class -> value.toBoolean()
                        Float::class -> value.toFloat()
                        Double::class -> value.toDouble()
                        else -> {
                            if (formMapperConverters.containsKey(property.returnType.classifier)) {
                                formMapperConverters[property.returnType.classifier]?.mapFromString(value)
                            } else {
                                throw IllegalStateException("No mapper found for type ${property.returnType.classifier}")
                            }
                        }
                    }
                    out[property.name] = result
                } else {
                    if (property.returnType.isMarkedNullable) {
                        out[property.name] = null
                    } else {
                        val defaultValue = when (property.returnType.classifier) {
                            Byte::class -> 0.toByte()
                            Char::class -> 0.toChar()
                            String::class -> ""
                            Long::class -> 0L
                            Int::class -> 0
                            Boolean::class -> false
                            Float::class -> 0.0f
                            Double::class -> 0.0
                            else -> {
                                if (formMapperConverters.containsKey(property.returnType.classifier)) {
                                    formMapperConverters[property.returnType.classifier]?.mapDefaultValue()
                                } else {
                                    throw IllegalStateException("No default mapper found for non-nullable type ${property.returnType.classifier}")
                                }
                            }
                        }
                        out[property.name] = defaultValue
                    }

                }
            }
        }
        return out
    }

    /**
     * Release all resources
     * Using this method is not necessary, but it can be useful if you want to release resources
     * After releasing, if targetScope was not given to constructor, you can't use this instance anymore
     */
    fun release() {
        if (targetScope == null) {
            scope.cancel()
        }

        inputs.clear()
        validStates.clear()
        relatedInputs.clear()
        formValidateListeners.clear()
        textChangedListeners.clear()
    }

    /**
     * Do all the things related to input change:
     * - validate input
     * - update related inputs
     * - notify listeners
     * - update valid state
     *
     * @param fieldName String name of the input
     * @param value CharSequence? value of the input
     * @throws IllegalStateException if input with specified fieldName not found
     */
    private suspend fun onInputChanged(fieldName: String, value: CharSequence?) {
        val item = inputs[fieldName] ?: throw IllegalStateException("Input with name $fieldName not found")
        var invalidCount = 0
        val defaultLabel = resources.getString(R.string.aif_input_group_default_value_label)
        if (item.validators.isEmpty() && item.required) {
            if (item.inputWrapper.inputField.text.isNullOrEmpty()) {
                invalidCount++
                val label = item.inputWrapper.inputField.label ?: defaultLabel
                item.setError(resources.getString(R.string.aif_input_group_value_required, label))
            } else {
                item.clearError()
            }
        } else {
            for (v in item.validators) {
                // ignore non required empty values
                if (!item.required && value.isNullOrEmpty()) {
                    continue
                }

                if (value.isNullOrEmpty()) {
                    val label = item.inputWrapper.inputField.label ?: defaultLabel
                    item.setError(resources.getString(R.string.aif_input_group_value_required, label))
                    invalidCount++
                } else {
                    val validState = v.validate(value)
                    if (!validState.isAcceptable()) {
                        invalidCount++
                        item.setError(v.errorMessage)
                    } else {
                        item.clearError()
                    }
                }
            }
        }

        if (relatedInputs.containsKey(fieldName)) {
            validate(relatedInputs[fieldName]!!)
        }

        val valid = invalidCount == 0
        validStates[fieldName] = valid
        if (valid) {
            inputData[fieldName] = value?.toString()
        } else {
            inputData[fieldName] = null
        }

        textChangedListeners.forEach { it(fieldName, value, valid) }
        formValidateListeners.forEach { it(isValidForm()) }
    }


    private fun getInput(input: InputField): InputGroupData {
        val fieldName = input.fieldName!!
        return inputs[fieldName] ?: throw IllegalStateException("Input with name $fieldName not found")
    }

    companion object {
        private val formMapperConverters = HashMap<KClass<*>, FormMapper<Any>>()

        /**
         * Add a [FormMapper] for a given class.
         *
         * @param clazz The class to add the mapper for.
         * @param formMapper The mapper to add.
         */
        @Suppress("UNCHECKED_CAST")
        fun addMapConverter(clazz: KClass<*>, formMapper: FormMapper<*>) {
            formMapperConverters[clazz] = formMapper as FormMapper<Any>
        }
    }

}