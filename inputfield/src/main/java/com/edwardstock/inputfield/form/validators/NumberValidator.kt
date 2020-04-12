package com.edwardstock.inputfield.form.validators

import io.reactivex.Single

/**
 * Advanced InputField. 2020
 * @author Eduard Maximovich <edward.vstock@gmail.com>
 */
open class NumberValidator(
    private var min: Long = 0,
    private var max: Long = Long.MAX_VALUE
) : BaseValidator() {

    init {
        _errorMessage = "Invalid number format"
    }

    override fun validate(value: CharSequence?): Single<Boolean> {
        if ((value == null || value.isEmpty())) {
            return Single.just(false)
        }

        val v: Long
        try {
            v = value.toString().toLong()
        } catch (e: NumberFormatException) {
            return Single.just(false)
        }

        //todo: handle overflow
        if (v < min || v > max) {
            return Single.just(false)
        }

        return Single.just(true)
    }
}