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

package com.edwardstock.inputfield.paris

import android.graphics.Typeface
import android.text.method.DigitsKeyListener
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import com.airbnb.paris.annotations.AfterStyle
import com.airbnb.paris.annotations.Attr
import com.airbnb.paris.annotations.Styleable
import com.airbnb.paris.proxies.BaseProxy
import com.airbnb.paris.styles.Style
import com.edwardstock.inputfield.R2

/**
 * Advanced InputField. 2020
 * @author Eduard Maximovich <edward.vstock@gmail.com>
 */
@Styleable("IF_EditText")
class IFEditTextProxy(view: EditText) : BaseProxy<IFEditTextProxy, EditText>(view) {
    private var oldTypeface: Typeface? = null

    @Attr(R2.styleable.IF_EditText_android_inputType)
    fun setInputType(inputType: Int) {
        if ((inputType and EditorInfo.TYPE_TEXT_VARIATION_PASSWORD) == EditorInfo.TYPE_TEXT_VARIATION_PASSWORD) {
            oldTypeface = view.typeface
        }

        view.inputType = inputType

    }

    @Attr(R2.styleable.IF_EditText_android_digits)
    fun setDigits(digits: String?) {
        if (!digits.isNullOrEmpty()) {
            view.keyListener = DigitsKeyListener.getInstance(digits)
        }
    }

    @AfterStyle
    fun afterStyle(@Suppress("UNUSED_PARAMETER") style: Style?) {
        view.isSingleLine =
            (view.inputType and EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE) != EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE

        oldTypeface?.let {
            view.typeface = oldTypeface
        }
    }


}