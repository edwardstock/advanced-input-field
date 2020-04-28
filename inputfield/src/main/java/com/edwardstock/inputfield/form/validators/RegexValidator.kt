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
import java.util.concurrent.Callable
import java.util.regex.Pattern

/**
 * Advanced InputField. 2020
 * @author Eduard Maximovich <edward.vstock@gmail.com>
 */
open class RegexValidator : BaseValidator {
    private val mPattern: String

    init {
        _errorMessage = "Invalid data format"
    }

    constructor(pattern: String) {
        mPattern = pattern
    }

    constructor(pattern: Pattern) {
        mPattern = pattern.pattern()
    }

    override fun validate(value: CharSequence?): Single<Boolean> {
        return Single.fromCallable(object : Callable<Boolean> {
            override fun call(): Boolean {
                if (value == null) {
                    return false
                }
                val result = value.toString()
                return result.matches(mPattern.toRegex())
            }
        })
    }
}