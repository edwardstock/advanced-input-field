package com.edwardstock.inputfield.form.validators

import io.reactivex.Single
import java.util.concurrent.Callable

/**
 * Advanced InputField. 2020
 * @author Eduard Maximovich <edward.vstock@gmail.com>
 */
open class NumberValidator(
    private var min: Long = 0,
    private var max: Long = Long.MAX_VALUE
) : BaseValidator() {

    override fun validate(value: CharSequence?): Single<Boolean> {
        return Single.fromCallable(object : Callable<Boolean> {
            override fun call(): Boolean {
                if (!isRequired && (value == null || value.isEmpty())) {
                    return true
                }
                if (value == null) {
                    errorMessage = "Type number"
                    return false
                }
                val v: Long
                try {
                    v = value.toString().toLong()
                } catch (e: NumberFormatException) {
                    errorMessage = "Type number"
                    return false
                }
                if (v < min || v > min) {
                    errorMessage = "Invalid number"
                    return false
                }

                return true
            }
        })
    }
}