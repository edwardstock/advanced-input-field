package com.edwardstock.inputfield.form.validators

import io.reactivex.Single

/**
 * Advanced InputField. 2020
 * @author Eduard Maximovich <edward.vstock@gmail.com>
 */

internal typealias LazyString = () -> CharSequence?

abstract class BaseValidator @kotlin.jvm.JvmOverloads constructor(
    protected var _errorMessage: CharSequence = "Incorrect data",
    protected var _isRequired: Boolean = true
) {

    open var errorMessage: CharSequence
        get() = _errorMessage
        set(v) {
            _errorMessage = v
        }

    open var isRequired: Boolean
        get() = _isRequired
        set(v) {
            _isRequired = v
        }

    open fun setErrorMessage(errorMessage: CharSequence): BaseValidator {
        _errorMessage = errorMessage
        return this
    }

    open fun setIsRequired(isRequired: Boolean): BaseValidator {
        _isRequired = isRequired
        return this
    }

    abstract fun validate(value: CharSequence?): Single<Boolean>
    operator fun invoke(value: CharSequence?): Single<Boolean> {
        return validate(value)
    }
}
