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

import android.text.Editable
import android.widget.EditText
import com.edwardstock.inputfield.mocks.MockEditable
import com.edwardstock.inputfield.mocks.MockInputField
import com.google.android.material.textfield.TextInputLayout
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


/**
 * Advanced InputField. 2020
 * @author Eduard Maximovich (edward.vstock@gmail.com)
 */
class CompareValidatorTest {

    @Test
    fun testLazyRequired() {
        val testSource = "some_string"
        val testTarget = "another_string"
        val validator = CompareValidator({ testSource })

        assertEquals("Values are not equals", validator.errorMessage)
        assertTrue(validator.validate(testSource).blockingGet())

        assertFalse(validator.validate(testTarget).blockingGet())
        assertFalse(validator.validate("wtf").blockingGet())
        assertTrue(validator(testSource).blockingGet())
        assertFalse(validator("some string").blockingGet())

        assertTrue(validator.isRequired)
    }

    @Test
    fun testLazyInvalid() {
        val testTarget = "another_string"
        val validator = CompareValidator({ null })

        assertFalse(validator(testTarget).blockingGet())
        assertTrue(validator.isRequired)
    }

    @Test
    fun testSimpleStringValid() {
        val testTarget = "another_string"
        val validator = CompareValidator(testTarget)

        assertTrue(validator(testTarget).blockingGet())
        assertTrue(validator.isRequired)
    }

    @Test
    fun testEditTextRefGetText() {
        //comparable: LazyString?, required: Boolean
        val testSource: CharSequence = "some_string"
        val testTarget: CharSequence = "another_string"

        val retEditable: Editable = MockEditable(testSource)

        val editText = mock<EditText> {
            on { it.text } doReturn retEditable
        }

        val validator = CompareValidator(editText).apply {
            errorMessage = "Invalid text"
        }

        assertFalse(validator(testTarget).blockingGet())
        assertTrue(validator(testSource).blockingGet())
        assertEquals("Invalid text", validator.errorMessage)

        val validatorNoRequired = CompareValidator(editText).apply { errorMessage = "Invalid text" }
        assertFalse(validatorNoRequired(testTarget).blockingGet())
        assertTrue(validatorNoRequired(testSource).blockingGet())
        assertEquals("Invalid text", validator.errorMessage)
        assertTrue(validatorNoRequired.isRequired)
    }

    @Test
    fun testInputFieldRefGetText() {

        val testSource: CharSequence = "some_string"
        val testTarget: CharSequence = "another_string"

        val inputField = mock<MockInputField> {
            on { it.getText() } doReturn MockEditable(testSource)
        }

        val validator = CompareValidator(inputField).apply { errorMessage = "Invalid text" }

        assertFalse(validator.validate(testTarget).blockingGet())
        assertTrue(validator(testSource).blockingGet())
        assertEquals("Invalid text", validator.errorMessage)

        val validatorNoRequired = CompareValidator(inputField).apply { errorMessage = "Invalid text" }
        assertFalse(validatorNoRequired(testTarget).blockingGet())
        assertTrue(validatorNoRequired(testSource).blockingGet())
        assertEquals("Invalid text", validator.errorMessage)
        assertTrue(validatorNoRequired.isRequired)

    }

    @Test
    fun testTextInputLayoutGetText() {
        val testSource: CharSequence = "some_string"
        val testTarget: CharSequence = "another_string"

        val editText = mock<EditText> {
            on { it.text } doReturn MockEditable(testSource)
        }

        val textInputLayout = mock<TextInputLayout> {
            on { getEditText() } doReturn editText
        }

        val validator = CompareValidator(textInputLayout).apply { errorMessage = "Invalid text" }

        assertFalse(validator.validate(testTarget).blockingGet())
        assertTrue(validator(testSource).blockingGet())
        assertEquals("Invalid text", validator.errorMessage)

        val validatorNoRequired = CompareValidator(textInputLayout).apply { errorMessage = "Invalid text" }
        assertFalse(validatorNoRequired(testTarget).blockingGet())
        assertTrue(validatorNoRequired(testSource).blockingGet())
        assertEquals("Invalid text", validator.errorMessage)
        assertTrue(validatorNoRequired.isRequired)
    }
}