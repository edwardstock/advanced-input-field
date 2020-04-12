package com.edwardstock.inputfield.form.validators

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


/**
 * Advanced InputField. 2020
 * @author Eduard Maximovich (edward.vstock@gmail.com)
 */
class CustomValidatorTest {

    @Test
    fun testSimple() {
        val validator = CustomValidator {
            it == "test string"
        }.setErrorMessage("Bad ass")

        assertTrue(validator("test string").blockingGet())
        assertFalse(validator("test_string").blockingGet())
        assertEquals("Bad ass", validator.errorMessage)
        assertTrue(validator.isRequired)
    }

    @Test
    fun testUseInterface() {
        val validator = CustomValidator(object : CustomValidator.Validator {
            override fun validate(oldValue: CharSequence?): Boolean {
                return oldValue == "test string"
            }
        }).apply {
            errorMessage = "invalid"
            isRequired = false
        }

        assertTrue(validator("test string").blockingGet())
        assertFalse(validator("test_string").blockingGet())
        assertEquals("invalid", validator.errorMessage)
        assertFalse(validator.isRequired)
    }

    @Test
    fun testStandardMessage() {
        val validator = CustomValidator {
            it == "test string"
        }

        assertTrue(validator("test string").blockingGet())
        assertFalse(validator("test_string").blockingGet())
        assertEquals("Values are not equals", validator.errorMessage)
        assertTrue(validator.isRequired)
    }
}