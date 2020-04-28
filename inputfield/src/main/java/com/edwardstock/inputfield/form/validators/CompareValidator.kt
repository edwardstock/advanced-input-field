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

import android.widget.EditText
import com.edwardstock.inputfield.InputField
import com.google.android.material.textfield.TextInputLayout
import io.reactivex.Single

/**
 * Advanced InputField. 2020
 * @author Eduard Maximovich <edward.vstock@gmail.com>
 */
open class CompareValidator : BaseValidator {
    private val mComparable: LazyString

    @JvmOverloads
    constructor(
        comparable: String,
        errorMessage: CharSequence = "Values are not equals",
        required: Boolean = true
    ) :
            super(errorMessage, required) {
        mComparable = { comparable }
    }

    @JvmOverloads
    constructor(
        comparable: LazyString,
        errorMessage: CharSequence = "Values are not equals",
        required: Boolean = true
    ) :
            super(errorMessage, required) {
        mComparable = comparable
    }

    @JvmOverloads
    constructor(
        editText: EditText,
        errorMessage: CharSequence = "Values are not equals",
        required: Boolean = true
    ) : super(errorMessage, required) {
        mComparable = { editText.text.toString() }
    }

    @JvmOverloads
    constructor(input: InputField, errorMessage: CharSequence = "Values are not equals", required: Boolean = true) :
            super(errorMessage, required) {
        mComparable = { input.text.toString() }
    }

    @JvmOverloads
    constructor(
        inputLayout: TextInputLayout,
        errorMessage: CharSequence = "Values are not equals",
        required: Boolean = true
    ) : super(errorMessage, required) {
        mComparable = { inputLayout.editText?.text.toString() }
    }

    override fun validate(value: CharSequence?): Single<Boolean> {
        val valid = mComparable()?.equals(value) ?: false

        return Single.just(valid)
    }
}