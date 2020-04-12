package com.edwardstock.inputfield.form.validators

import android.util.Patterns

/**
 * Advanced InputField. 2020
 * @author Eduard Maximovich <edward.vstock@gmail.com>
 */
open class PhoneValidator : RegexValidator(PHONE_PATTERN) {
    init {
        _errorMessage = "Invalid phone number"
    }

    companion object {
        private val PHONE_PATTERN = Patterns.PHONE
    }
}