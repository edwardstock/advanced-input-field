package com.edwardstock.inputfield.form.validators

import android.widget.EditText
import com.edwardstock.inputfield.InputField
import com.google.android.material.textfield.TextInputLayout
import io.reactivex.Single

/**
 * Advanced InputField. 2020
 * @author Eduard Maximovich <edward.vstock@gmail.com>
 */
open class CompareValidator : BaseValidator {
    private val mComparable: LazyString

    @JvmOverloads
    constructor(
        comparable: String,
        errorMessage: CharSequence = "Values are not equals",
        required: Boolean = true
    ) :
            super(errorMessage, required) {
        mComparable = { comparable }
    }

    @JvmOverloads
    constructor(
        comparable: LazyString,
        errorMessage: CharSequence = "Values are not equals",
        required: Boolean = true
    ) :
            super(errorMessage, required) {
        mComparable = comparable
    }

    @JvmOverloads
    constructor(
        editText: EditText,
        errorMessage: CharSequence = "Values are not equals",
        required: Boolean = true
    ) : super(errorMessage, required) {
        mComparable = { editText.text.toString() }
    }

    @JvmOverloads
    constructor(input: InputField, errorMessage: CharSequence = "Values are not equals", required: Boolean = true) :
            super(errorMessage, required) {
        mComparable = { input.text.toString() }
    }

    @JvmOverloads
    constructor(
        inputLayout: TextInputLayout,
        errorMessage: CharSequence = "Values are not equals",
        required: Boolean = true
    ) : super(errorMessage, required) {
        mComparable = { inputLayout.editText?.text.toString() }
    }

    override fun validate(value: CharSequence?): Single<Boolean> {
        val valid = mComparable()?.equals(value) ?: false

        return Single.just(valid)
    }
}