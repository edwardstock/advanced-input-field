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
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.StyleRes

internal class AndroidAttributeResolver(
    context: Context,
    @StyleRes styleRes: Int
) : AutoCloseable {
    private val attrs = intArrayOf(
        android.R.attr.gravity, // 0
        android.R.attr.layout_width, // 1
        android.R.attr.layout_height, // 2
        android.R.attr.padding, // 3
        android.R.attr.paddingStart, // 4
        android.R.attr.paddingEnd, // 5
        android.R.attr.paddingTop, // 6
        android.R.attr.paddingBottom, // 7
        android.R.attr.clickable, // 8
        android.R.attr.focusable, // 9
        android.R.attr.background, // 10
        android.R.attr.layout_marginEnd, // 11
        android.R.attr.layout_marginStart, // 12
        android.R.attr.layout_marginTop, // 13
        android.R.attr.layout_marginBottom, // 14
        android.R.attr.layout_margin, // 15
        android.R.attr.foreground, // 16
        android.R.attr.tint, // 17
        android.R.attr.tintMode, // 18
    )

    val typedArray = context.obtainStyledAttributes(styleRes, attrs)

    fun hasGravity(): Boolean {
        return typedArray.hasValue(0)
    }

    fun getGravity(def: Int = Gravity.CENTER): Int {
        return typedArray.getInt(0, def)
    }

    fun hasWidth(): Boolean {
        return typedArray.hasValue(1)
    }

    fun getWidth(def: Int = ViewGroup.LayoutParams.WRAP_CONTENT): Int {
        return typedArray.getLayoutDimension(1, def)
    }

    fun hasHeight(): Boolean {
        return typedArray.hasValue(2)
    }

    fun getHeight(def: Int = ViewGroup.LayoutParams.WRAP_CONTENT): Int {
        return typedArray.getLayoutDimension(2, def)
    }

    fun hasPadding(): Boolean {
        return typedArray.hasValue(3)
    }

    fun hasPaddingSided(): Boolean {
        return typedArray.hasValue(4) ||
                typedArray.hasValue(5) ||
                typedArray.hasValue(6) ||
                typedArray.hasValue(7)
    }

    fun hasPaddingLeft(): Boolean {
        return typedArray.hasValue(4)
    }

    fun hadPaddingRight(): Boolean {
        return typedArray.hasValue(5)
    }

    fun hasPaddingTop(): Boolean {
        return typedArray.hasValue(6)
    }

    fun hasPaddingBottom(): Boolean {
        return typedArray.hasValue(7)
    }

    fun getPaddingSided(mutableRect: Rect) {
        if (typedArray.hasValue(4))
            mutableRect.left = typedArray.getDimensionPixelSize(4, 0)

        if (typedArray.hasValue(5))
            mutableRect.right = typedArray.getDimensionPixelSize(5, 0)

        if (typedArray.hasValue(6))
            mutableRect.top = typedArray.getDimensionPixelSize(6, 0)

        if (typedArray.hasValue(7))
            mutableRect.bottom = typedArray.getDimensionPixelSize(7, 0)
    }

    fun getPadding(): Int {
        return typedArray.getDimensionPixelSize(3, 0)
    }

    fun hasClickable(): Boolean {
        return typedArray.hasValue(8)
    }

    fun getClickable(): Boolean {
        return typedArray.getBoolean(8, false)
    }

    fun hasFocusable(): Boolean {
        return typedArray.hasValue(9)
    }

    fun getFocusable(): Boolean {
        return typedArray.getBoolean(9, false)
    }

    fun hasMarginSided(): Boolean {
        return typedArray.hasValue(11) ||
                typedArray.hasValue(12) ||
                typedArray.hasValue(13) ||
                typedArray.hasValue(14)
    }

    fun hasMargin(): Boolean {
        return typedArray.hasValue(15)
    }

    fun getMarginSided(mutableRect: Rect) {
        if (typedArray.hasValue(11))
            mutableRect.left = typedArray.getDimensionPixelSize(11, 0)

        if (typedArray.hasValue(12))
            mutableRect.right = typedArray.getDimensionPixelSize(12, 0)

        if (typedArray.hasValue(13))
            mutableRect.top = typedArray.getDimensionPixelSize(13, 0)

        if (typedArray.hasValue(14))
            mutableRect.bottom = typedArray.getDimensionPixelSize(14, 0)
    }


    fun getMargin(): Int {
        return typedArray.getDimensionPixelSize(15, 0)
    }

    fun hasForeground(): Boolean {
        return typedArray.hasValue(16)
    }

    fun getForeground(): Drawable? {
        return typedArray.getDrawable(16)
    }

    fun hasBackground(): Boolean {
        return typedArray.hasValue(10)
    }

    fun getBackground(): Drawable? {
        return typedArray.getDrawable(10)
    }

    fun hasTintColor(): Boolean {
        return typedArray.hasValue(17)
    }

    fun getTintColor(): Int {
        return typedArray.getColor(17, Color.TRANSPARENT)
    }

    fun hasTintMode(): Boolean {
        return typedArray.hasValue(18)
    }

    fun getTintMode(): PorterDuff.Mode {
        return typedArray.getInt(18, PorterDuff.Mode.SRC_IN.ordinal).let {
            PorterDuff.Mode.values()[it]
        }
    }

    override fun close() {
        typedArray.recycle()
    }
}

internal fun Context.resolveAttributes(@StyleRes styleRes: Int, block: AndroidAttributeResolver.() -> Unit) {
    AndroidAttributeResolver(this, styleRes).use(block)
}

internal fun ViewGroup.applyAttributes(context: Context, styleRes: Int) {
    context.resolveAttributes(styleRes) {
        if (layoutParams is LinearLayout.LayoutParams) {
            layoutParams = (layoutParams as LinearLayout.LayoutParams).apply {
                gravity = getGravity()
            }
        }

        val paddingRect = Rect(
            paddingLeft,
            paddingTop,
            paddingRight,
            paddingBottom,
        )
        if (hasPadding()) {
            paddingRect.left = getPadding()
            paddingRect.top = getPadding()
            paddingRect.right = getPadding()
            paddingRect.bottom = getPadding()
        }
        if (hasPaddingSided()) {
            getPaddingSided(paddingRect)
        }
        setPaddingRect(paddingRect)

        if (layoutParams is ViewGroup.MarginLayoutParams) {
            if (hasMargin()) {
                val lp = layoutParams as ViewGroup.MarginLayoutParams
                lp.marginStart = getMargin()
                lp.marginEnd = getMargin()
                lp.topMargin = getMargin()
                lp.bottomMargin = getMargin()
                layoutParams = lp
            }
            if (hasMarginSided()) {
                val lp = layoutParams as LinearLayout.LayoutParams
                val marginRect = Rect(
                    lp.marginStart,
                    lp.topMargin,
                    lp.marginEnd,
                    lp.bottomMargin,
                )
                getMarginSided(marginRect)
                lp.marginStart = marginRect.left
                lp.marginEnd = marginRect.right
                lp.topMargin = marginRect.top
                lp.bottomMargin = marginRect.bottom
                layoutParams = lp
            }
        }

        isClickable = getClickable()
        isFocusable = getFocusable()

        if (hasBackground()) {
            background = getBackground()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (hasForeground()) {
                foreground = getForeground()
            }
        }
    }
}