package com.edwardstock.inputfield.form.validators

import io.reactivex.Single

/**
 * Advanced InputField. 2020
 * @author Eduard Maximovich <edward.vstock@gmail.com>
 */
open class EmptyValidator : BaseValidator() {

    override fun validate(value: CharSequence?): Single<Boolean> {
        return Single.just(
            value != null && value.isNotEmpty()
        )
    }
}