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
class EmailValidatorTest {

    @Test
    fun testValidEmail() = runTest {
        val validator = EmailValidator()
        expect(Validator.State.Valid) { validator.validate("aaa@bbb.ccc") }
    }

    @Test
    fun testValidEmailWithSpecialSymbols() = runTest {
        val validator = EmailValidator()
        expect(Validator.State.Valid) { validator.validate("aaa.bbb.ccc+eee_111@bbb.ccc") }
    }

    @Test
    fun testInvalidEmail() = runTest {
        val validator = EmailValidator()
        expect(Validator.State.Invalid) { validator.validate("aaa@bbb") }
    }

    @Test
    fun testEmptyEmail() = runTest {
        val validator = EmailValidator()
        expect(Validator.State.Invalid) { validator.validate("") }
    }

    @Test
    fun testNullEmail() = runTest {
        val validator = EmailValidator()
        expect(Validator.State.Invalid) { validator.validate(null) }
    }

    @Test
    fun testEmailWithSpaces() = runTest {
        val validator = EmailValidator()
        expect(Validator.State.Invalid) { validator.validate("aaa bbb@ccc.com") }
    }

    @Test
    fun testEmailWithoutAtSymbol() = runTest {
        val validator = EmailValidator()
        expect(Validator.State.Invalid) { validator.validate("aaabbb.com") }
    }

    @Test
    fun testEmailWithValid_ccTLD_Domain() = runTest {
        val validator = EmailValidator()
        expect(Validator.State.Valid) { validator.validate("aaa@bbb.xn--fiqs8s") }
    }

    @Test
    fun testEmailWithInvalidTLD() = runTest {
        val validator = EmailValidator()
        expect(Validator.State.Invalid) { validator.validate("aaa@bbb.c") }
    }

}