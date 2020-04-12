package com.edwardstock.inputfield.form.validators

import java.util.regex.Pattern

/**
 * Advanced InputField. 2020
 * @author Eduard Maximovich <edward.vstock@gmail.com>
 */
open class PhoneValidator : RegexValidator(PHONE_PATTERN) {
    init {
        _errorMessage = "Invalid phone number"
    }

    companion object {
        private val PHONE_PATTERN = Pattern.compile( // sdd = space, dot, or dash
            "(\\+[0-9]+[\\- \\.]*)?" // +<digits><sdd>*
                    + "(\\([0-9]+\\)[\\- \\.]*)?" // (<digits>)<sdd>*
                    + "([0-9][0-9\\- \\.]+[0-9])"
        ) // <digit><digit|sdd>+<digit>

    }
}