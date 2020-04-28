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