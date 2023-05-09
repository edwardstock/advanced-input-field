/*
 * Copyright (C) by Eduard Maximovich. 2023
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
import android.view.ContextThemeWrapper
import android.view.ViewStub
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible

internal class InputFieldViewHolder<InputView>(
    context: Context,
    inputCreator: (ContextThemeWrapper) -> InputView,
    rootLayoutStyle: Int = 0,
    labelStyle: Int = 0,
    inputStyle: Int = 0,
    inputSuffixStyle: Int = 0,
    inputOverlayStyle: Int = 0,
    errorStyle: Int = 0
) where InputView : TextView {
    val rootLayout: ConstraintLayout
    val label: TextView
    val input: InputView
    val inputSuffixRoot: SuffixMenuContainer
    val inputOverlay: ViewStub
    val error: TextView

    init {
        val rootLayoutTheme = ContextThemeWrapper(context, rootLayoutStyle)
        rootLayout = ConstraintLayout(rootLayoutTheme).apply {
            id = ViewCompat.generateViewId()
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val labelTheme = ContextThemeWrapper(context, labelStyle)
        label = AppCompatTextView(labelTheme).apply {
            id = ViewCompat.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            }
        }

        val inputTheme = ContextThemeWrapper(context, inputStyle)
        input = inputCreator(inputTheme).apply {
            id = ViewCompat.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topToBottom = label.id
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            }
        }


        val inputSuffixTheme = ContextThemeWrapper(context, inputSuffixStyle)
        inputSuffixRoot = SuffixMenuContainer(inputSuffixTheme).apply {
            id = ViewCompat.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
            ).apply {
                topToTop = input.id
                bottomToBottom = input.id
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            }
            orientation = LinearLayout.HORIZONTAL
        }
        inputSuffixRoot.applyAttributes(inputSuffixTheme, inputSuffixStyle)

        val inputOverlayTheme = ContextThemeWrapper(context, inputOverlayStyle)
        inputOverlay = ViewStub(inputOverlayTheme).apply {
            id = ViewCompat.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT
            ).apply {
                topToTop = input.id
                bottomToBottom = input.id
                startToStart = input.id
                endToEnd = input.id
            }
            isVisible = false
        }

        val errorTheme = ContextThemeWrapper(context, errorStyle)
        error = TextView(errorTheme).apply {
            id = ViewCompat.generateViewId()
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topToBottom = input.id
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            }
            setTextColor(ContextCompat.getColor(context, R.color.aif_error_color))
            isVisible = false
        }

        rootLayout.addView(label)
        rootLayout.addView(input)
        rootLayout.addView(inputSuffixRoot)
        rootLayout.addView(inputOverlay)
        rootLayout.addView(error)
    }
}