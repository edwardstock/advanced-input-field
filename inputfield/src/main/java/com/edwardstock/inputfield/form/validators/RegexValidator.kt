package com.edwardstock.inputfield.form.validators

import io.reactivex.Single
import java.util.concurrent.Callable
import java.util.regex.Pattern

/**
 * Advanced InputField. 2020
 * @author Eduard Maximovich <edward.vstock@gmail.com>
 */
open class RegexValidator : BaseValidator {
    private val mPattern: String

    constructor(pattern: String) {
        mPattern = pattern
    }

    constructor(pattern: Pattern) {
        mPattern = pattern.pattern()
    }

    override fun validate(value: CharSequence?): Single<Boolean> {
        return Single.fromCallable(object : Callable<Boolean> {
            override fun call(): Boolean {
                if (!isRequired && (value == null || value.isEmpty())) {
                    return true
                }
                val result = value?.toString() ?: ""
                return result.matches(mPattern.toRegex())
            }
        })
    }
}