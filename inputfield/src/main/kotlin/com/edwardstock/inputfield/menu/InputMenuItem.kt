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

package com.edwardstock.inputfield.menu

import android.content.Context
import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.edwardstock.inputfield.OnSuffixMenuItemClickListener

/**
 * @param iconTintColor (color value) this tint will override style's tinting
 * @param iconTintColorRes (resource value) this tint will override style's tinting
 */
data class InputMenuItem(
    val itemId: Int = ViewCompat.generateViewId(),
    val title: CharSequence? = null,
    val titleRes: Int = 0,
    val icon: Drawable? = null,
    val iconRes: Int = 0,

    val iconTintColor: Int? = null,
    val iconTintColorRes: Int = 0,
    val iconTintMode: PorterDuff.Mode = PorterDuff.Mode.SRC_IN,

    val isVisible: Boolean = true,
    val isEnabled: Boolean = true,
    val showAsAction: Int = MenuItem.SHOW_AS_ACTION_IF_ROOM,

    val onMenuItemClickListener: OnSuffixMenuItemClickListener? = null,

    // just to support default MenuItem
    val groupId: Int = 0,
    val order: Int = 0
) {

    fun getTintColorFilter(context: Context): ColorFilter? {
        val color = when {
            iconTintColor != null -> iconTintColor
            iconTintColorRes != 0 -> ContextCompat.getColor(context, iconTintColorRes)
            else -> null
        } ?: return null

        return PorterDuffColorFilter(color, iconTintMode)
    }

    fun getIconDrawable(context: Context, applyTint: Boolean = false): Drawable? {
        return when {
            icon != null -> icon
            iconRes != 0 -> ContextCompat.getDrawable(context, iconRes)
            else -> null
        }?.apply {
            if (applyTint) {
                colorFilter = getTintColorFilter(context)
            }
        }
    }

    fun hasTintColor(): Boolean {
        return iconTintColor != null || iconTintColorRes != 0
    }
}