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
class DecimalInputFilter(
    private val comparable: () -> String,
    private val decimals: Int = 18
) : DigitsKeyListener(false, true) {

    @JvmOverloads
    constructor(input: EditText, decimals: Int = 18) :
            this({ input.text?.toString() ?: "" }, decimals)

    @JvmOverloads
    constructor(input: InputField, decimals: Int = 18) :
            this({ input.text?.toString() ?: "" }, decimals)

    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        val tmp: String = comparable()

        val sourceLen = source?.length ?: 0
        val sourceSafe = source ?: ""

        if (sourceLen > 1) {
            return source.toString().replace("[^0-9\\.]".toRegex(), "")
        }



        when {
            sourceLen > 1 -> return source.toString().replace("[^0-9\\.]".toRegex(), "")
            source == "," -> return ""
            source == "." && tmp.isEmpty() -> return "0."
            tmp == "0" && source != "." -> return ""
            source == "." && tmp.contains(".") -> return ""
        }

        val ptIndex = tmp.indexOf(".")

        if (ptIndex == -1) {
            if (tmp == "0" && source == ".") {
                return source
            }
            return if (tmp.isNotEmpty() && (tmp[0] == '0' && dstart > 0 || tmp[0] != '0' && source == "0" && dstart == 0)) {
                ""
            } else {
                source
            }
        }
        if (ptIndex >= dstart) {
            if (tmp[0] == '.') {
                return source
            }
            if (tmp[0] == '0' && dstart > 0 || tmp[0] != '0' && source == "0" && dstart == 0) {
                return ""
            }
        } else if (ptIndex < dstart) {
            val decimals = tmp.substring(ptIndex + 1)
            if (decimals.length >= this.decimals) {
                return ""
            }
        }
        return super.filter(source, start, end, dest, dstart, dend)
    }
}