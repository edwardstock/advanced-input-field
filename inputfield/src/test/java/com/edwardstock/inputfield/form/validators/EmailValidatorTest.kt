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
class EmailValidatorTest {

    @Test
    fun testSimple() {
        val emails = listOf(
            "vasya@pupkin.com",
            "johnas_smithus@bofa.us",
            "mc.viper@sendmail.hell",
            "bmw_motorrad@daimler.ag",
            "ul_o_n_g_l_o_n_g@mail.com",
            "abc.def.ghi@netscape.com",
            "0xDEADBEEF@gmail.com",
            "wtf-this-is-wrong@server.com"
        )

        val invalidEmails = listOf(
            "hey\$you@whocares.com",
            "@gmail.com",
            "example.net",
            "124567",
            "qwerty",
            "some/server@server.net"
        )

        val validator = EmailValidator().apply { errorMessage = "Invalid Email" }

        for (v in emails) {
            assertTrue(validator(v).blockingGet())
            assertEquals("Invalid Email", validator.errorMessage)
        }

        for (v in invalidEmails) {
            assertFalse(validator(v).blockingGet(), "Invalid invalid value: \"$v\"")
            assertEquals("Invalid Email", validator.errorMessage)
        }
    }
}