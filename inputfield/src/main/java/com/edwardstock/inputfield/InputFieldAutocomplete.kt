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

package com.edwardstock.inputfield

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.IdRes
import androidx.annotation.Px
import com.airbnb.paris.annotations.Attr
import com.airbnb.paris.annotations.Styleable
import com.google.android.material.textfield.MaterialAutoCompleteTextView

/**
 * Advanced InputField. 2020
 * @author Eduard Maximovich (edward.vstock@gmail.com)
 */
@Styleable("InputFieldAutocomplete")
class InputFieldAutocomplete @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : InputField(context, attrs, defStyleAttr) {

    init {
        com.edwardstock.inputfield.Paris.style(this).apply(attrs)
    }

    override fun getLayoutId(): Int {
        return R.layout.aif_input_autocomplete_field
    }

    override val input: MaterialAutoCompleteTextView
        get() = super.mInput as MaterialAutoCompleteTextView

    var dropdownAnchor: Int
        get() = input.dropDownAnchor
        @Attr(R2.styleable.InputFieldAutocomplete_android_dropDownAnchor)
        set(@IdRes v) {
            input.dropDownAnchor = v
        }

    var dropDownHeight: Int
        get() = input.dropDownHeight
        set(@Px v) {
            input.dropDownHeight = v
        }

    var dropDownWidth: Int
        get() = input.dropDownWidth
        set(@Px v) {
            input.dropDownWidth = v
        }

    var dropDownVerticalOffset: Int
        get() = input.dropDownVerticalOffset
        @Attr(R2.styleable.InputFieldAutocomplete_android_dropDownVerticalOffset)
        set(v) {
            input.dropDownVerticalOffset = v
        }

    var dropDownHorizontalOffset: Int
        get() = input.dropDownHorizontalOffset
        @Attr(R2.styleable.InputFieldAutocomplete_android_dropDownHorizontalOffset)
        set(v) {
            input.dropDownHorizontalOffset = v
        }


}