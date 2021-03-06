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

import io.reactivex.Single

/**
 * Advanced InputField. 2020
 * @author Eduard Maximovich <edward.vstock@gmail.com>
 */

internal typealias LazyString = () -> CharSequence?

abstract class BaseValidator @kotlin.jvm.JvmOverloads constructor(
    protected var _errorMessage: CharSequence = "Incorrect data",
    protected var _isRequired: Boolean = true,
    protected var _isWarning: Boolean = false
) {

    open var errorMessage: CharSequence
        get() = _errorMessage
        set(v) {
            _errorMessage = v
        }

    open var isRequired: Boolean
        get() = _isRequired
        set(v) {
            _isRequired = v
        }

    /**
     * This field used to run validation on field, but don't mark as error if value is not validated
     */
    open var isWarning: Boolean
        get() = _isWarning
        set(v) {
            _isWarning = v
        }

    abstract fun validate(value: CharSequence?): Single<Boolean>
    operator fun invoke(value: CharSequence?): Single<Boolean> {
        return validate(value)
    }
}
