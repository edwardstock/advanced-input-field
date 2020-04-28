/*
 * Copyright (C) by Eduard Maximovich. 2020
 * @link <a href="https://github.com/edwardstock">Profile</a>
 *
 * The MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

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