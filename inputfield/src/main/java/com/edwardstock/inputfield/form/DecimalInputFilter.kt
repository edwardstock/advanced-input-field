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

package com.edwardstock.inputfield.form

import android.text.Spanned
import android.text.method.DigitsKeyListener
import android.widget.EditText
import com.edwardstock.inputfield.InputField

/**
 * Advanced InputField. 2020
 * @author Eduard Maximovich <edward.vstock@gmail.com>
 */
class DecimalInputFilter @JvmOverloads constructor(
    private val comparable: () -> String,
    private val decimals: Int = 18
) : DigitsKeyListener(false, true) {

    @JvmOverloads
    constructor(input: EditText, decimals: Int = 18) :
            this({ input.text?.toString() ?: "" }, decimals)

    @JvmOverloads
    constructor(input: InputField, decimals: Int = 18) :
            this({ input.text?.toString() ?: "" }, decimals)

    private fun filterInternal(current: String, source: String, start: Int, end: Int, dstart: Int, dend: Int): String {
        var src = source
        if (src == ",") {
            src = "."
        }

        val dotIndex = current.indexOf(".")
        val hasDot = dotIndex != -1


        if (hasDot && (src == "." || src == ",")) {
            return ""
        }
        if (current.isEmpty() && (src == "." || src == ",")) {
            return "0."
        }

        if (hasDot) {
            val decimals = current.substring(dotIndex + 1)
            if (decimals.length >= this.decimals && dstart > dotIndex) {
                return ""
            }
        }

        if (current == "0") {
            if (dstart == 0 && src == "0") {
                return src
            }
            if (src != "." && src != "," && src != "") {
                return ".$src"
            }
        }

        if (src == "0" && current.isNotEmpty() && current[0] == '0' && dstart <= dotIndex) {
            return ""
        }

        return src
    }

    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {

        if (source == null) {
            return null
        }

        val current: String = comparable()

        var src = source.toString()
        // if nothing is this is digits with dot or
        val baseRegex = "^[0-9\\.\\,]+$".toRegex()
        if (!src.matches(baseRegex)) {
            src = src.replace("([^0-9\\.\\,])".toRegex(), "")
        }

        if (src.length > 1) {
            val sb = StringBuilder()
            var pos = 0
            for (c in src) {
                sb.append(
                    filterInternal(
                        sb.toString(),
                        c.toString(),
                        0, 1,
                        pos, pos + 1
                    )
                )
                pos++
            }
            return sb.toString()
        }

        return filterInternal(current, src, start, end, dstart, dend)
    }
}