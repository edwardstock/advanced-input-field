package com.edwardstock.inputfield.form.validators

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


/**
 * Advanced InputField. 2020
 * @author Eduard Maximovich (edward.vstock@gmail.com)
 */
class LengthValidatorTest {

    @Test
    fun testMinOnly() {
        val validator = LengthValidator(1)

        assertEquals(validator.errorMessage, "Minimum length: 1")

        val nilTarget: String? = null
        assertFalse(validator(nilTarget).blockingGet())
        val emptyTarget = ""
        assertFalse(validator(emptyTarget).blockingGet())
        val equalTarget = "v"
        assertTrue(validator(equalTarget).blockingGet())
        val greaterTarget = "v2agagagag"
        assertTrue(validator(greaterTarget).blockingGet())
    }

    @Test
    fun testZeroIsValidForNullOrEmpty() {
        val validator = LengthValidator(0)

        assertEquals(validator.errorMessage, "Minimum length: 0")
        val nilTarget: String? = null
        assertTrue(validator(nilTarget).blockingGet())
        val emptyTarget = ""
        assertTrue(validator(emptyTarget).blockingGet())
        val equalTarget = "v"
        assertTrue(validator(equalTarget).blockingGet())
        val greaterTarget = "v2agagagag"
        assertTrue(validator(greaterTarget).blockingGet())
    }

    @Test
    fun testMinMax() {
        val validator = LengthValidator(1, 4)
        assertEquals(validator.errorMessage, "Minimum length: 1, maximum: 4")
        val emptyTarget = ""
        assertFalse(validator(emptyTarget).blockingGet())
        val equalTarget = "v"
        assertTrue(validator(equalTarget).blockingGet())
        val greaterTarget = "v2agagagag"
        assertFalse(validator(greaterTarget).blockingGet())
        val maxLenTarget = "v123"
        assertTrue(validator(maxLenTarget).blockingGet())
    }
}