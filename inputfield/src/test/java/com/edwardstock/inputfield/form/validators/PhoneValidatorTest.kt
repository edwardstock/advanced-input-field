package com.edwardstock.inputfield.form.validators


import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Advanced InputField. 2020
 * @author Eduard Maximovich (edward.vstock@gmail.com)
 */
class PhoneValidatorTest {

    @Test
    fun testPhone() {
        val validator = PhoneValidator()

        val target1 = "+79627894512"
        assertTrue(validator(target1).blockingGet())
        val target2 = "2424"
        assertTrue(validator(target2).blockingGet())
        val target3: String? = null
        assertFalse(validator(target3).blockingGet())
        val target4 = ""
        assertFalse(validator(target4).blockingGet())

    }
}