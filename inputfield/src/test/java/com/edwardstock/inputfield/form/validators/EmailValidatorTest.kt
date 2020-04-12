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