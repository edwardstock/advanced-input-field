package com.edwardstock.inputfield.form.validators

import io.reactivex.Single

/**
 * Advanced InputField. 2020
 * @author Eduard Maximovich <edward.vstock@gmail.com>
 */
open class LengthValidator : BaseValidator {
    private val _min: Int
    private val _max: Int

    constructor(min: Int) {
        _min = min
        _max = Int.MAX_VALUE
        _errorMessage = "Minimum length: $_min"
    }

    constructor(min: Int, max: Int) {
        _min = min
        _max = max
        _errorMessage = "Minimum length: $_min, maximum: $_max"
    }

    override fun validate(value: CharSequence?): Single<Boolean> {
        val res = if (_min == 0 && (value == null || value.isEmpty())) {
            true
        } else {
            value != null && value.length >= _min && value.length <= _max
        }
        return Single.just(res)
    }
}