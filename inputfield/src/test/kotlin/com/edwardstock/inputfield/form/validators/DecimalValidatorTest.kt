/*
 * Copyright (C) by Eduard Maximovich. 2023
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

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.expect

/**
 * Advanced InputField. 2023
 * @author Eduard Maximovich (edward.vstock@gmail.com)
 */
class DecimalValidatorTest {

    @Test
    fun testValidInteger() = runTest {
        val validator = DecimalValidator()
        expect(Validator.State.Valid) { validator.validate("123") }
    }

    @Test
    fun testInvalidNegativeInteger() = runTest {
        val validator = DecimalValidator()
        expect(Validator.State.Invalid) { validator.validate("-123") }
    }

    @Test
    fun testValidDecimalWithDotWithoutFraction() = runTest {
        val validator = DecimalValidator()
        expect(Validator.State.Valid) { validator.validate("123.") }
    }

    @Test
    fun testValidDecimalStartFromDot() = runTest {
        val validator = DecimalValidator()
        expect(Validator.State.Valid) { validator.validate(".123") }
    }

    @Test
    fun testValidDecimal() = runTest {
        val validator = DecimalValidator()
        expect(Validator.State.Valid) { validator.validate("11.123") }
    }

    @Test
    fun testInvalidEmptyString() = runTest {
        val validator = DecimalValidator()
        expect(Validator.State.Invalid) { validator.validate("") }
    }

    @Test
    fun testInvalidNullString() = runTest {
        val validator = DecimalValidator()
        expect(Validator.State.Invalid) { validator.validate(null) }
    }

    @Test
    fun testInvalidNaN() = runTest {
        val validator = DecimalValidator()
        expect(Validator.State.Invalid) { validator.validate("hello world") }
    }
}