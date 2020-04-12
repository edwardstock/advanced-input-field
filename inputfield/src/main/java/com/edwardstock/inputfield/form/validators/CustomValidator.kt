package com.edwardstock.inputfield.form.validators

import io.reactivex.Single

/**
 * Advanced input field. 2020
 * @author Eduard Maximovich (edward.vstock@gmail.com)
 */
@Suppress("IfThenToElvis")
open class CustomValidator : BaseValidator {
    private val mValidator: Validator

    init {
        _errorMessage = "Values are not equals"
    }

    constructor(validator: (CharSequence?) -> Boolean) {
        mValidator = object : Validator {
            override fun validate(oldValue: CharSequence?): Boolean {
                return validator(oldValue)
            }
        }
    }

    constructor(validator: Validator) {
        mValidator = validator
    }

    override fun validate(value: CharSequence?): Single<Boolean> {
        return Single.just(mValidator.validate(value))
    }

    interface Validator {
        fun validate(oldValue: CharSequence?): Boolean
    }

}