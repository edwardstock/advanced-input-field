package com.edwardstock.inputfield.form.validators

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * Advanced InputField. 2020
 * @author Eduard Maximovich (edward.vstock@gmail.com)
 */
class EmptyValidatorTest {

    @Test
    fun testSimple() {
        val validator = EmptyValidator("Bad ass")
        validator.setIsRequired(true)

        assertTrue(validator("test string").blockingGet())
        assertTrue(validator("test_string").blockingGet())
        assertFalse(validator("").blockingGet())
        assertFalse(validator(null).blockingGet())
        assertTrue(validator(" ").blockingGet())
        assertEquals("Bad ass", validator.errorMessage)
        assertTrue(validator.isRequired)
    }

    @Test
    fun testAnother() {
        val validator = EmptyValidator("Another message", false)

        assertTrue(validator("test string").blockingGet())
        assertTrue(validator("test_string").blockingGet())
        assertFalse(validator("").blockingGet())
        assertFalse(validator(null).blockingGet())
        assertTrue(validator(" ").blockingGet())
        assertEquals("Another message", validator.errorMessage)
        assertFalse(validator.isRequired)
    }

    @Test
    fun testDefaultCtor() {
        val validator = EmptyValidator()

        assertTrue(validator("test string").blockingGet())
        assertTrue(validator("test_string").blockingGet())
        assertFalse(validator("").blockingGet())
        assertFalse(validator(null).blockingGet())
        assertTrue(validator(" ").blockingGet())
        assertEquals("Value can't be empty", validator.errorMessage)
        assertTrue(validator.isRequired)
    }
}