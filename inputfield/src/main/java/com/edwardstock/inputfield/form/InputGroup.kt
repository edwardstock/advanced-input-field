/*
 * Copyright (C) by Eduard Maximovich. 2020
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

import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.edwardstock.inputfield.InputField
import com.edwardstock.inputfield.form.validators.BaseValidator
import com.google.android.material.textfield.TextInputLayout
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * Advanced InputField. 2020
 * @author Eduard Maximovich <edward.vstock@gmail.com>
 */
open class InputGroup(
    private val lifecycleOwner: LifecycleOwner? = null
) : LifecycleObserver {
    protected val inputs: MutableMap<String, InputWrapper> = HashMap()
    protected val extErrorViews: MutableMap<String, TextView?> = HashMap()
    protected val inputValidators: MutableMap<String, MutableList<BaseValidator>> = ConcurrentHashMap()
    protected val textWatchers: MutableList<OnTextChangedListener> = ArrayList()
    protected val validFormListeners: MutableList<OnFormValidateListener> = ArrayList()
    protected val requiredInputs: MutableList<String> = ArrayList()
    protected val validateRelations: MutableMap<String, String> = HashMap(2)
    protected val validMap: MutableMap<String, Boolean> = HashMap()
    protected val disposables: CompositeDisposable = CompositeDisposable()

    protected val globalFormValidHandler: OnTextChangedListener = object : OnTextChangedListener {
        override fun onTextChanged(input: InputWrapper, valid: Boolean) {
            validMap[input.fieldName!!] = valid

            // Check one by one required inputs for validity
            requiredInputs.forEach {
                // if one of is invalid, notify about it
                if (!validMap.safeGet(it, false)) {
                    notifyFormValidListeners(false)
                    return
                }
            }

            // then if modified some not required field but it's fucked up, checking it out
            if (validMap.size > requiredInputs.size) {
                validMap.filter { !requiredInputs.contains(it.key) }.forEach {
                    if (!validMap.safeGet(it.key, false)) {
                        notifyFormValidListeners(false)
                        return
                    }
                }
            }

            // if we reached this case, it means all required fields are valid, so check not required firstly modified
            if (!valid) {
                notifyFormValidListeners(false)
                return
            }

            notifyFormValidListeners(true)
        }
    }

    protected val inputFlow: PublishSubject<String> = PublishSubject.create()
    protected var validateScheduler: Scheduler = Schedulers.io()
    private var handleInput = true

    init {
        @Suppress("LeakingThis")
        lifecycleOwner?.lifecycle?.addObserver(this)
        inputFlow
            .debounce(200, TimeUnit.MILLISECONDS)
            .subscribe(::onInputFlow)
            .handleDisposable()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    internal fun onLifecycleStop() {
        disposables.dispose()
        disposables.clear()
    }

    class Builder(private val ig: InputGroup) {
        fun add(inputLayout: TextInputLayout, vararg validator: BaseValidator?): Builder {
            ig.addInput(inputLayout)
            ig.addValidator(inputLayout, *validator)
            return this
        }

        fun add(input: InputField?, vararg validator: BaseValidator?): Builder {
            ig.addInput(input)
            ig.addValidator(input, *validator)
            return this
        }

        fun add(input: EditText?, vararg validator: BaseValidator?): Builder {
            ig.addInput(input)
            ig.addValidator(input, *validator)
            return this
        }

        fun add(input: InputWrapper?, vararg validator: BaseValidator?): Builder {
            ig.addInput(input)
            ig.addValidator(input, *validator)
            return this
        }

    }

    var clearErrorBeforeValidate: Boolean = false
    var enableInputDebounce: Boolean = true
    var appendErrorOnValidation: Boolean = true
    var enableAutoHideError: Boolean = false

    fun addFormValidateListener(listener: (Boolean) -> Unit) {
        validFormListeners.add(object : OnFormValidateListener {
            override fun onValid(valid: Boolean) {
                listener(valid)
            }
        })
    }

    fun addFormValidateListener(listener: OnFormValidateListener): InputGroup {
        validFormListeners.add(listener)
        return this
    }

    fun removeInput(input: InputWrapper?) {
        if (input?.fieldName == null) return

        val fieldName: String = input.fieldName!!

        inputs.remove(fieldName)
        extErrorViews.remove(fieldName)
        inputValidators.remove(fieldName)
        validMap.remove(fieldName)
        requiredInputs.remove(fieldName)

        val it = validateRelations.iterator()
        while (it.hasNext()) {
            val v = it.next()
            if (v.key == fieldName || v.value == fieldName) {
                it.remove()
            }
        }
    }

    fun removeAll() {
        inputs.clear()
        extErrorViews.clear()
        inputValidators.clear()
        textWatchers.clear()
        validFormListeners.clear()
        validMap.clear()
        requiredInputs.clear()
        validateRelations.clear()
    }

    fun setup(builder: Builder.() -> Unit): Builder {
        return Builder(this).apply(builder)
    }


    fun addInput(inputLayout: TextInputLayout): InputGroup {
        inputLayout.editText?.let {
            addInput(InputWrapper(it))
        }
        return this
    }

    fun addInput(vararg input: TextInputLayout?): InputGroup {
        input.filterNotNull().forEach { addInput(it) }
        return this
    }

    fun addInput(vararg input: InputField?): InputGroup {
        input.filterNotNull().forEach { addInput(InputWrapper(it)) }
        return this
    }

    fun addInput(vararg input: EditText?): InputGroup {
        input.filterNotNull().forEach { addInput(InputWrapper(it)) }
        return this
    }

    fun addInput(vararg input: InputWrapper?): InputGroup {
        input.filterNotNull().forEach { addInput(it) }
        return this
    }

    private fun postUiThread(runnable: () -> Unit) {
        Handler(Looper.getMainLooper()).post { runnable() }
    }

    fun onInputFlow(fieldName: String) {
        val input = inputs[fieldName]!!

        if (textWatchers.isEmpty()) {
            validate(fieldName, true).subscribe { valid ->
                postUiThread { globalFormValidHandler.onTextChanged(input, valid) }
            }.handleDisposable()
            return
        }

        if (clearErrorBeforeValidate) {
            setError(fieldName, null)
        }

        textWatchers.forEach {
            // if we have relations for current input, get it, and trigger validate() function on it
            if (validateRelations.hasValidKey(fieldName)) {
                // mark we have relations, to trigger onTextChanged for that input

                val relatedInput = getRelatedInput(fieldName)!!
                validate(relatedInput.fieldName, true).subscribe { valid ->
                    it.onTextChanged(relatedInput, valid)
                    postUiThread {
                        globalFormValidHandler.onTextChanged(relatedInput, valid)
                    }

//                            internalTextListener.onTextChanged(getInput(validateRelations[fieldName])!!, valid)
                }
            }
            // validate source input
            validate(fieldName, true).subscribe { valid ->
                // trigger textChanged
                it.onTextChanged(input, valid)

                postUiThread { globalFormValidHandler.onTextChanged(input, valid) }
            }
        }
    }

    fun addInput(input: InputWrapper): InputGroup {
        val fieldName = input.fieldName
            ?: throw IllegalArgumentException("Input view must have string tag with field name or app:fieldName=\"name\" for InputField")
        inputs[fieldName] = input

        input.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!handleInput) return

                if (enableInputDebounce) {
                    inputFlow.onNext(fieldName)
                } else {
                    onInputFlow(fieldName)
                }

            }

            override fun afterTextChanged(s: Editable) {}
        })
        return this
    }

    internal fun <K, V> Map<K, V>.hasValidKey(key: K?): Boolean {
        if (key == null) return false
        return this.containsKey(key) && this[key] != null
    }

    internal fun View.tagString(): String? {
        return if (this.tag != null && tag is String) tag as String else null
    }

    internal fun View.tagStringSafe(hasVal: (String) -> Unit) {
        val v = tagString()
        if (v != null) {
            hasVal(v)
        }
    }

    fun setErrorView(input: EditText?, errorView: TextView?): InputGroup {
        if (input == null || errorView == null) return this

        input.tagStringSafe {
            if (inputs.hasValidKey(it) && inputs[it]!!.isInputField) {
                // set own view for InputField
                inputs[it]!!.inputField.errorView = errorView
                return@tagStringSafe
            }
            extErrorViews[it] = errorView
        }

        return this
    }

    fun setErrorView(input: InputWrapper, errorView: TextView?): InputGroup {
        input.fieldName?.let {
            if (inputs.hasValidKey(it) && inputs[it]!!.isInputField) {
                inputs[it]!!.inputField.errorView = errorView
                return@let
            }
            extErrorViews[it] = errorView
        }

        return this
    }

    fun setErrorView(inputLayout: TextInputLayout?, errorView: TextView?): InputGroup {
        return if (inputLayout == null || inputLayout.editText == null) {
            this
        } else {
            setErrorView(inputLayout.editText, errorView)
        }
    }

    fun validate(withError: Boolean = true): Single<Boolean> {
        val allValidators = inputValidators.keys
            .map { validate(it, withError) }
            .toList()

        return Observable.fromIterable(allValidators)
            .flatMap { validator -> validator.map { if (it) 1 else 0 }.toObservable() }
            .reduce { t1: Int, t2: Int -> t1 + t2 }
            .map { it == inputValidators.size }
            .toSingle()
    }

    fun addTextChangedListener(listener: OnTextChangedListener): InputGroup {
        textWatchers.add(listener)
        return this
    }

    fun addTextChangedListener(listener: (input: InputWrapper, valid: Boolean) -> Unit) {
        textWatchers.add(object : OnTextChangedListener {
            override fun onTextChanged(input: InputWrapper, valid: Boolean) {
                listener(input, valid)
            }
        })
    }

    fun addFilter(inputLayout: TextInputLayout, filter: InputFilter?): InputGroup {
        return addFilter(inputLayout.editText, filter)
    }

    fun addFilter(editText: EditText?, filter: InputFilter?): InputGroup {
        return addFilter(InputWrapper(editText!!), filter)
    }

    fun addFilter(field: InputField?, filter: InputFilter?): InputGroup {
        return addFilter(InputWrapper(field!!), filter)
    }

    fun addFilter(editText: InputWrapper?, filter: InputFilter?): InputGroup {
        if (editText == null || filter == null) return this

        val newFilters = editText.filters.copyOf(editText.filters.size + 1)
        newFilters[editText.filters.size] = filter
        @Suppress("UNCHECKED_CAST")
        editText.filters = newFilters as Array<InputFilter>
        return this
    }

    fun addValidator(input: TextInputLayout?, validator: BaseValidator): InputGroup {
        input?.let {
            addValidator(input.editText, validator)
        }
        return this
    }

    fun addValidator(input: TextInputLayout?, vararg validator: BaseValidator?): InputGroup {
        input?.let {
            validator.filterNotNull().forEach { addValidator(input, it) }
        }
        return this
    }

    fun addValidator(input: InputField?, vararg validator: BaseValidator?): InputGroup {
        input?.let {
            validator.filterNotNull().forEach { addValidator(InputWrapper(input), it) }
        }
        return this
    }

    fun addValidator(input: EditText?, vararg validator: BaseValidator?): InputGroup {
        input?.let {
            validator.filterNotNull().forEach { addValidator(input, it) }
        }
        return this
    }

    fun addValidator(input: EditText?, baseValidator: BaseValidator): InputGroup {
        input?.let {
            addValidator(InputWrapper(input), baseValidator)
        }
        return this
    }

    fun addValidator(input: InputWrapper?, vararg validator: BaseValidator?): InputGroup {
        input?.let {
            validator.filterNotNull().forEach { addValidator(input, it) }
        }
        return this
    }

    fun addValidator(input: InputWrapper?, validator: BaseValidator): InputGroup {
        input?.fieldName?.let {
            if (!inputValidators.containsKey(it)) {
                inputValidators[it] = ArrayList()
            }
            inputValidators[it]!!.add(validator)
            if (validator.isRequired && !requiredInputs.contains(it)) {
                requiredInputs.add(it)
            }
        }

        return this
    }

    fun cancelValidation() {
        disposables.dispose()
    }

    fun reset() {
        notifyFormValidListeners(requiredInputs.size == 0)
        clearData()
        disposables.dispose()
        clearErrors()
    }

    fun hideErrors() {
        inputs.forEach {
            when {
                it.value.hasParentTextInputLayout -> {
                    val textInputLayout = it.value.parentTextInputLayout
                    textInputLayout.isErrorEnabled = false
                }
                extErrorViews.hasValidKey(it.key) -> {
                    val errorView = extErrorViews[it.key]!!
                    errorView.post {
                        errorView.visibility = View.GONE
                    }
                }
                it.value.isInputField -> {
                    it.value.post {
                        it.value.inputField.errorEnabled = false
                    }
                }
                else -> {
                }
            }
        }
    }

    fun clearErrors() {
        for ((key) in inputs) {
            setError(key, null)
        }
    }

    fun clearData() {
        handleInput = false
        inputs.forEach { it.value.text = null }
        handleInput = true
    }

    fun setOnEditorActionListener(listener: OnEditorActionListener?) {
        for ((_, v) in inputs) {
            v.setOnEditorActionListener(listener)
        }
    }

    fun addError(fieldName: String, message: CharSequence?) {
        if (!inputs.hasValidKey(fieldName) || message == null) {
            return
        }

        val input = getInput(fieldName)!!

        when {
            input.hasParentTextInputLayout -> {
                val textInputLayout = input.parentTextInputLayout
                textInputLayout.post {
                    var targetErr = textInputLayout.error?.toString() ?: ""
                    if (targetErr.isNotEmpty()) {
                        textInputLayout.isErrorEnabled = true
                        targetErr += "\n"
                    }
                    targetErr += message.toString()
                    textInputLayout.error = targetErr
                }
            }
            extErrorViews.hasValidKey(fieldName) -> {
                val errorView = extErrorViews[fieldName]!!
                errorView.post {
                    if (errorView.text.isNotEmpty()) {
                        errorView.append("\n")
                    }
                    errorView.append(message)
                    errorView.visibility = View.VISIBLE
                }
            }
            else -> {
                input.post {
                    if (input.error == null || input.error!!.isEmpty()) {
                        input.error = message
                    } else {
                        input.error = "${input.error}\n${message}"
                    }
                }
            }
        }
    }

    fun setError(fieldName: String?, message: CharSequence?) {
        if (!inputs.hasValidKey(fieldName)) {
            return
        }

        val targetMessage = if (message == null || message.isEmpty()) null else message
        val input = getInput(fieldName)!!

        when {
            input.hasParentTextInputLayout -> {
                val textInputLayout = input.parentTextInputLayout
                textInputLayout.post {
                    textInputLayout.error = targetMessage
                    if (targetMessage != null && !textInputLayout.isErrorEnabled) {
                        textInputLayout.isErrorEnabled = true
                    }
                }
            }
            extErrorViews.hasValidKey(fieldName) -> {
                val errorView = extErrorViews[fieldName]!!
                errorView.post {
                    errorView.text = targetMessage
                    if (targetMessage != null && errorView.visibility != View.VISIBLE) {
                        errorView.visibility = View.VISIBLE
                    }
                }
            }
            else -> {
                input.post {
                    input.error = targetMessage
//                    if(targetMessage != null) {
//                        input.inputField.errorEnabled = true
//                    } else if(enableAutoHideError) {
//                        input.inputField.errorEnabled = false
//                    }
                }
            }
        }
    }

    /**
     * @param f1 field 1
     * @param f2 field 2
     * @return
     * @see .addValidateRelation
     */
    fun addValidateRelation(f1: TextInputLayout, f2: TextInputLayout): InputGroup {
        return addValidateRelation(f1.editText, f2.editText)
    }

    fun addValidateRelation(f1: EditText?, f2: EditText?): InputGroup {
        return addValidateRelation(InputWrapper(f1!!), InputWrapper(f2!!))
    }

    fun addValidateRelation(f1: InputField?, f2: InputField?): InputGroup {
        return addValidateRelation(InputWrapper(f1!!), InputWrapper(f2!!))
    }

    /**
     * Triggers [.validate] when one of this inputs changed
     * Example:
     * inputPassword
     * inputPasswordRepeat
     * if inputPassword was changed, validator will triggered for both fields (instead of only for editing field (default behavior))
     * and if inputPasswordRepeat was changed, validator will triggered for both fields
     * @param f1 field 1 Order no matters
     * @param f2 field 2
     * @return
     */
    fun addValidateRelation(f1: InputWrapper, f2: InputWrapper): InputGroup {
        if (f1.fieldName == null || f2.fieldName == null) throw IllegalStateException("Fields must have names")
        validateRelations[f1.fieldName!!] = f2.fieldName!!
        validateRelations[f2.fieldName!!] = f1.fieldName!!
        return this
    }

    fun getInput(fieldName: String?): InputWrapper? {
        if (!inputs.hasValidKey(fieldName)) return null
        return inputs[fieldName]
    }

    fun getRelatedInput(fieldName: String?): InputWrapper? {
        if (!validateRelations.hasValidKey(fieldName)) return null
        val relatedFieldName = validateRelations[fieldName]!!
        return getInput(relatedFieldName)
    }

    protected fun validate(fieldName: String?, withError: Boolean): Single<Boolean> {
        if (!inputValidators.hasValidKey(fieldName) || !inputs.hasValidKey(fieldName)) {
            return Single.just(true)
        }
        val input = getInput(fieldName)!!
        val originValue: CharSequence? = input.text

        if (appendErrorOnValidation) {
            setError(fieldName!!, null)
        }

        val validators = ArrayList(inputValidators[fieldName]!!)
        return Observable.fromIterable(validators)
            .subscribeOn(validateScheduler)
            // make observables from list of field validators
            .flatMap { validator ->
                validator.validate(originValue)
                    // convert valid bool value to 1, and invalid to 0, than we'll compare valid results with the validators
                    .map { valid ->
                        if (withError && !valid) {

                            if (appendErrorOnValidation && !valid) {
                                addError(fieldName!!, validator.errorMessage)
                            } else {
                                setError(fieldName!!, if (valid) null else validator.errorMessage)
                            }
                        }

                        if (valid || validator.isWarning) 1 else 0
                    }
                    .toObservable()
            }
            // count valid results
            .reduce { t1: Int, t2: Int -> t1 + t2 }
            // compare valid results with the number of validators
            .map {
                val countWarnings = inputValidators[fieldName]!!.filter { v -> v.isWarning }.count()
                val res = it == inputValidators[fieldName]!!.size

                // hide error only if no warnings and 100% validators are in valid state
                if (res && countWarnings == 0) {
                    setError(fieldName!!, null)
                }
                res
            }
            .observeOn(AndroidSchedulers.mainThread())
            .toSingle()
    }

    private fun notifyFormValidListeners(valid: Boolean) {
        validFormListeners.forEach { it.onValid(valid) }
    }

    private fun <K, V> Map<K, V>.safeGet(key: K, defValue: V): V {
        if (!hasValidKey(key)) return defValue
        return get(key)!!
    }

    private fun Disposable.handleDisposable() {
        this@InputGroup.disposables.add(this)
    }

    interface OnFormValidateListener {
        fun onValid(valid: Boolean)
    }

    interface OnTextChangedListener {
        fun onTextChanged(input: InputWrapper, valid: Boolean)
    }
}