package com.edwardstock.inputfield.form.validators

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


/**
 * Advanced InputField. 2020
 * @author Eduard Maximovich (edward.vstock@gmail.com)
 */
class DecimalValidatorTest {

    @Test
    fun testSimple() {
        val decList: List<String> = listOf(
            "100", "200", "200.524", "300,626", "1", "0", ".052"
        )
        val invalidDecList: List<String> = listOf(
            "", " ", "abc", "1e-5", "15e5", "\n"
        )

        var validator = DecimalValidator().apply { errorMessage = "Invalid number" }
        for (v in decList) {
            assertTrue(validator(v).blockingGet())
            assertEquals("Invalid number", validator.errorMessage)
        }

        // checking another error message
        validator = DecimalValidator().apply {
            errorMessage = "Is this a number?"
            isRequired = false
        }
        for (v in invalidDecList) {
            assertFalse(validator(v).blockingGet(), "Valid invalid number: \"$v\"")
            assertEquals("Is this a number?", validator.errorMessage)
            assertFalse(validator.isRequired)
        }
    }
}