package com.edwardstock.inputfield.form.validators

import java.util.regex.Pattern

/**
 * Advanced InputField. 2020
 * @author Eduard Maximovich <edward.vstock@gmail.com>
 */
open class EmailValidator : RegexValidator(VALID_EMAIL_ADDRESS_REGEX) {
    init {
        _errorMessage = "Invalid E-Mail address"
    }

    companion object {
        private val VALID_EMAIL_ADDRESS_REGEX = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
        )
    }

}