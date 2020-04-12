package com.edwardstock.inputfield.form.validators


/**
 * Advanced InputField. 2020
 * @author Eduard Maximovich (edward.vstock@gmail.com)
 */
class CompareValidatorTest {

//    @Test
//    fun testLazyRequired() {
//        val testSource = "some_string"
//        val testTarget = "another_string"
//        val validator = CompareValidator({testSource}, true)
//
//        assertEquals("Incorrect data", validator.errorMessage)
//        assertTrue(validator.validate(testSource))
//
//        assertFalse(validator.validate(testTarget))
//        assertFalse(validator.validate("wtf"))
//        assertTrue(validator(testSource))
//        assertFalse(validator("some string"))
//
//        assertTrue(validator.isRequired)
//    }
//
//    @Test
//    fun testLazyInvalid() {
//        val testTarget = "another_string"
//        val validator = CompareValidator({null}, true)
//
//        assertFalse(validator(testTarget))
//        assertTrue(validator.isRequired)
//    }
//
//    @Test
//    fun testEditTextRefGetText() {
//        //comparable: LazyString?, required: Boolean
//        val testSource: CharSequence = "some_string"
//        val testTarget: CharSequence = "another_string"
//
//        val retEditable: Editable = MockEditable(testSource)
//        val editText = mock(EditText::class.java)
//        `when`(editText.text).thenReturn(retEditable)
//
//        val validator = CompareValidator("Invalid text", true, editText)
//
//        assertFalse(validator.validate(testTarget))
//        assertTrue(validator(testSource))
//        assertEquals("Invalid text", validator.errorMessage)
//
//        val validatorNoRequired = CompareValidator("Invalid text", editText)
//        assertFalse(validatorNoRequired(testTarget))
//        assertTrue(validatorNoRequired(testSource))
//        assertEquals("Invalid text", validator.errorMessage)
//        assertTrue(validatorNoRequired.isRequired)
//    }
//
//    @Test
//    fun testInputFieldRefGetText() {
//
//        val testSource: CharSequence = "some_string"
//        val testTarget: CharSequence = "another_string"
//
//        val inputField = mock<MockInputField> {
//            on { it.getText() } doReturn MockEditable(testSource)
//        }
//
//        val validator = CompareValidator("Invalid text", true, inputField)
//
//        assertFalse(validator.validate(testTarget))
//        assertTrue(validator(testSource))
//        assertEquals("Invalid text", validator.errorMessage)
//
//        val validatorNoRequired = CompareValidator("Invalid text", inputField)
//        assertFalse(validatorNoRequired(testTarget))
//        assertTrue(validatorNoRequired(testSource))
//        assertEquals("Invalid text", validator.errorMessage)
//        assertTrue(validatorNoRequired.isRequired)
//
//    }
//
//    @Test
//    fun testTextInputLayoutGetText() {
//        val testSource: CharSequence = "some_string"
//        val testTarget: CharSequence = "another_string"
//
//        val editText = mock<EditText> {
//            on { it.text } doReturn MockEditable(testSource)
//        }
//
//        val textInputLayout = mock<TextInputLayout> {
//            on { getEditText() } doReturn editText
//        }
//
//        val validator = CompareValidator("Invalid text", true, textInputLayout)
//
//        assertFalse(validator.validate(testTarget))
//        assertTrue(validator(testSource))
//        assertEquals("Invalid text", validator.errorMessage)
//
//        val validatorNoRequired = CompareValidator("Invalid text", textInputLayout)
//        assertFalse(validatorNoRequired(testTarget))
//        assertTrue(validatorNoRequired(testSource))
//        assertEquals("Invalid text", validator.errorMessage)
//        assertTrue(validatorNoRequired.isRequired)
//    }
}