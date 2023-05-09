/*
 * Copyright (C) by Eduard Maximovich. 2022
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

import android.os.Build
import android.text.Spanned
import android.text.method.DigitsKeyListener
import android.util.Log
import androidx.annotation.DeprecatedSinceApi
import androidx.annotation.RequiresApi
import java.util.Locale

/**
 * Decimal input filter or key listener
 */
class DecimalInputFilter : DigitsKeyListener {
    private val decimals: Int

    /**
     * @param decimals number of decimals after dot
     */
    @RequiresApi(Build.VERSION_CODES.O)
    constructor(locale: Locale, decimals: Int = 18) : super(locale, false, true) {
        this.decimals = decimals
    }

    /**
     * @param decimals number of decimals after dot
     */
    @Suppress("DEPRECATION")
    @DeprecatedSinceApi(26)
    constructor(decimals: Int) : super(false, true) {
        this.decimals = decimals
    }

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        insertTo: Int,
        dend: Int
    ): CharSequence {
        val source = source.filter { it.isDigit() || it == '.' || it == ',' }.toString().replace(",", ".")
        val destDot = dest.indexOf('.')
        Log.d(
            "Filter", "source=\"$source\", start=$start, end=$end, dest=$dest, dstart=$insertTo, dend=$dend" +
                    "\ndestDot=$destDot"
        )
        return if (source.length == 1) {
            val beforeDot = if (destDot != -1) dest.substring(0, destDot) else dest
            if (decimals != -1 && destDot != -1 && dest.length - destDot > decimals && insertTo > destDot) {
                return ""
            }

            return when (source[0]) {
                '0' -> {
                    when {
                        insertTo == 0 && destDot == -1 -> "0."
                        insertTo == 0 && dest[0] == '0' -> ""
                        beforeDot == "0" && insertTo <= destDot -> ""
                        dest.length == 1 && dest[0] == '0' -> ".$source"
                        else -> source
                    }
                }

                '.' -> {
                    when {
                        insertTo == 0 && destDot == -1 -> "0."
                        destDot == -1 -> source
                        else -> ""
                    }
                }

                else -> {
                    when {
                        dest.length == 1 && dest[0] == '0' -> ".$source"
                        beforeDot == "0" && insertTo > 0 && insertTo <= destDot -> ""
                        else -> source
                    }
                }
            }
        } else if (source.length > 1) {
            val isReplacement = dest.isEmpty() || insertTo == 0 && dend == dest.length
            if (isReplacement) {
                var sourceDot = source.indexOf('.')
                // has dot
                if (sourceDot != -1 && sourceDot != 0) {
                    var preparedTarget = source.replace(",", ".")
                        .filterIndexed { index, c ->
                            if (c == '.') {
                                index == sourceDot
                            } else true
                        }
                    sourceDot = preparedTarget.indexOf('.')
                    val sourceBeforeDot = preparedTarget.substring(0, sourceDot)
                    var countZeroesBeforeDot = 0
                    while (countZeroesBeforeDot < source.length && preparedTarget[countZeroesBeforeDot] == '0') {
                        countZeroesBeforeDot++
                    }
                    if (countZeroesBeforeDot > 1) {
                        val lastZeroIndex = sourceBeforeDot.lastIndexOf('0')
                        preparedTarget = preparedTarget.substring(lastZeroIndex)
                    }
                    sourceDot = preparedTarget.indexOf('.')
                    if (decimals != -1) {
                        val sourceAfterDot = preparedTarget.substring(sourceDot + 1)
                        if (sourceAfterDot.length > decimals) {
                            preparedTarget = sourceBeforeDot + "." + sourceAfterDot.substring(0, decimals)
                        }
                    }
                    preparedTarget
                } else {
                    // handling case: 000123 -> 0.123
                    // also case: 100100 -> 100100
                    if (source[0] == '0') {
                        var i = 0
                        while (i < source.length && source[i] == '0') {
                            i++
                        }
                        val upTo = if (decimals != -1) i + decimals else source.length
                        "0." + source.substring(i, minOf(source.length, upTo))
                    } else if (source[0] == '.') {
                        "0$source"
                    } else {
                        source
                    }
                }
            } else {
                if (insertTo <= destDot) {
                    ""
                } else {
                    val withoutDot = source.replace(".", "")
                    if (destDot == -1) {
                        withoutDot
                    } else {
                        if (decimals != -1) {
                            if (dest.length - 1 - destDot + withoutDot.length > decimals) {
                                return withoutDot.substring(0, decimals - (dest.length - 1 - destDot))
                            } else {
                                withoutDot
                            }
                        } else {
                            withoutDot
                        }
                    }
                }
            }
        } else {
            source
        }
    }
}