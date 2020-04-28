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

package com.edwardstock.inputfield.mocks

import android.text.Editable
import android.text.InputFilter

/**
 * Advanced InputField. 2020
 * @author Eduard Maximovich (edward.vstock@gmail.com)
 */
class MockEditable(private var str: CharSequence = "") : Editable {

    override fun setSpan(what: Any?, start: Int, end: Int, flags: Int) {
        TODO("Not yet implemented")
    }

    override fun toString(): String {
        return str.toString()
    }

    override fun insert(where: Int, text: CharSequence?, start: Int, end: Int): Editable {
        return this
    }

    override fun insert(where: Int, text: CharSequence?): Editable {
        return this
    }

    override fun <T : Any?> getSpans(start: Int, end: Int, type: Class<T>?): Array<T>? {
        return null
    }

    override fun clear() {

    }

    override fun getFilters(): Array<InputFilter>? {
        return null
    }

    override fun removeSpan(what: Any?) {
    }

    override fun nextSpanTransition(start: Int, limit: Int, type: Class<*>?): Int {
        return 0
    }

    override fun append(text: CharSequence?): Editable {
        return this
    }

    override fun append(text: CharSequence?, start: Int, end: Int): Editable {
        return this
    }

    override fun append(text: Char): Editable {
        return this
    }

    override fun getSpanEnd(tag: Any?): Int {
        return 0
    }

    override fun replace(st: Int, en: Int, source: CharSequence?, start: Int, end: Int): Editable {
        return this
    }

    override fun replace(st: Int, en: Int, text: CharSequence?): Editable {
        return this
    }

    override fun getChars(start: Int, end: Int, dest: CharArray?, destoff: Int) {
    }

    override fun get(index: Int): Char {
        return str[index]
    }

    override fun clearSpans() {
    }

    override fun getSpanStart(tag: Any?): Int {
        return 0
    }

    override fun delete(st: Int, en: Int): Editable {
        return this
    }

    override fun setFilters(filters: Array<out InputFilter>?) {
    }

    override fun getSpanFlags(tag: Any?): Int {
        return 0
    }

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        return str
    }

    override val length: Int
        get() = str.length
}