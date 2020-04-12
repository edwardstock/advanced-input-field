package com.edwardstock.inputfield.form.validators

/**
 * Advanced InputField. 2020
 * @author Eduard Maximovich <edward.vstock@gmail.com>
 */
open class DecimalValidator : RegexValidator(PATTERN) {
    init {
        _errorMessage = "Invalid decimal"
    }

    companion object {
        private const val PATTERN = "^(\\d*)([.,])?(\\d{1,18})$"
    }
}