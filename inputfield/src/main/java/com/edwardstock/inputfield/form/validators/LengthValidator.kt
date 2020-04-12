package com.edwardstock.inputfield.form.validators

import io.reactivex.Single

/**
 * Advanced InputField. 2020
 * @author Eduard Maximovich <edward.vstock@gmail.com>
 */
open class LengthValidator : BaseValidator {
    private var mMin = -1
    private var mMax = Int.MAX_VALUE

    constructor(min: Int) {
        mMin = min
    }

    constructor(min: Int, max: Int) {
        mMin = min
        mMax = max
    }

    override fun validate(value: CharSequence?): Single<Boolean> {
        return Single.fromCallable {
            if (mMin == 0 && (value == null || value.isEmpty())) {
                true
            } else {
                value != null && value.length >= mMin && value.length <= mMax
            }
        }
    }
}