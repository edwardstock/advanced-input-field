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

package com.edwardstock.inputfield.form

import android.text.Spanned
import android.text.SpannedString
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import java.util.Locale
import kotlin.test.assertEquals
import kotlin.test.expect

/**
 * Advanced InputField. 2023
 * @author Eduard Maximovich (edward.vstock@gmail.com)
 */
class DecimalInputFilterTest {

    @Test
    fun testValues() {
        val valuesMap = mapOf(
            Pair("100#100", "100100"),
            Pair("#131i414819", "131414819"),
            Pair("00000", "0."),
            Pair("0.0111.00", "0.011100"),
            Pair("00.1111", "0.1111"),
            Pair(".", "0."),
            Pair("0", "0."),
            Pair("0,", "0."),
            Pair("0.,", "0."),
            Pair("06365", "0.6365"),
            Pair("0.0006365", "0.0006365"),
            Pair(".0006365", "0.0006365"),
            Pair("100", "100"),
            Pair("-100", "100"),
            Pair("(5 + 5) = 10", "5510"),
            Pair("100.100", "100.100"),
            Pair("100,100", "100.100")
        )

        valuesMap.forEach { (input, expected) ->
            val result = DecimalInputFilter(Locale.getDefault(), 18).filter(
                input, 0, input.length, SpannedString(""), 0, input.length
            )
            assertEquals(expected, result, "source value=\"${input}\" expected=\"${expected}\" result=\"${result}\"")
        }
    }

    private fun sequentialFilter(src: String, decimals: Int = 18): String {
        val numberQueue = src.toCharArray()
        var dest = ""
        val filter = DecimalInputFilter(decimals)

        var offset = 0
        for ((i, input) in numberQueue.withIndex()) {
            val spanned = mockk<Spanned>()
            every { spanned.toString() } returns dest
            every { spanned[any()] } answers {
                dest[arg(0)]
            }
            every { spanned.length } returns dest.length
            every { spanned.subSequence(any(), any()) } answers {
                dest.subSequence(arg(0), arg(1))
            }
            val res = filter.filter(
                input.toString(), 0, 1, spanned, i + offset, i + offset
            )
            if (res.isNotEmpty()) {
                if (res.length > 1) {
                    offset++
                }
                dest += res
            }
        }

        return dest
    }

    private fun CharSequence.toSpannedMock(): Spanned {
        val spanned = mockk<Spanned>()
        every { spanned.toString() } returns toString()
        every { spanned[any()] } answers {
            get(arg(0))
        }
        every { spanned.length } returns length
        every { spanned.subSequence(any(), any()) } answers {
            subSequence(arg(0), arg(1))
        }
        return spanned
    }

    /**
     * Entering values one by one has a bit different behavior
     */
    @Test
    fun testEnteringValues() {
        val valuesMap = mapOf(
            Pair("100#100", "100100"),
            Pair("#131i414819", "131414819"),
            Pair("00000", "0.0000"),
            Pair("0.0111.00", "0.011100"),
            Pair("00.1111", "0.01111"),
            Pair(".", "0."),
            Pair("0", "0."),
            Pair("0,", "0."),
            Pair("0.,", "0."),
            Pair("06365", "0.6365"),
            Pair("0.0006365", "0.0006365"),
            Pair(".0006365", "0.0006365"),
            Pair("100", "100"),
            Pair("-100", "100"),
            Pair("(5 + 5) = 10", "5510"),
            Pair("100.100", "100.100"),
            Pair("100,100", "100.100")
        )

        valuesMap.forEach { (input, expected) ->
            val result = sequentialFilter(input)
            assertEquals(expected, result, "source value=\"${input}\" expected=\"${expected}\" result=\"${result}\"")
        }
    }

    @Test
    fun testReplacePartNoDot() {
        val source = "0.11112222".toSpannedMock()
        val value = "99"
        val filter = DecimalInputFilter(Locale.getDefault(), 10)
        val insertPosition = 2
        val result = filter.filter(value, 0, value.length, source, insertPosition, value.length + 2)
        expect(value) { result }
        val modifiedSource = source.toString().toCharArray().toMutableList()
        for ((i, r) in result.withIndex()) {
            modifiedSource.add(insertPosition + i, r)
        }

        expect("0.9911112222") { String(modifiedSource.toCharArray()) }
    }

    @Test
    fun testReplacePartWithDotLimitedTo10() {
        val source = "0.11112222".toSpannedMock()
        val value = "99.99"
        val filter = DecimalInputFilter(Locale.getDefault(), 10)
        val insertPosition = 2
        val result = filter.filter(value, 0, value.length, source, insertPosition, value.length + 2)
        expect("99") { result }
        val modifiedSource = source.toString().toCharArray().toMutableList()
        for ((i, r) in result.withIndex()) {
            modifiedSource.add(insertPosition + i, r)
        }

        expect("0.9911112222") { String(modifiedSource.toCharArray()) }
    }

    @Test
    fun testReplacePartWithDotLimitedTo11() {
        val source = "0.11112222".toSpannedMock()
        val value = "99.99"
        val filter = DecimalInputFilter(Locale.getDefault(), 11)
        val insertPosition = 2
        val result = filter.filter(value, 0, value.length, source, insertPosition, value.length + 2)
        expect("999") { result }
        val modifiedSource = source.toString().toCharArray().toMutableList()
        for ((i, r) in result.withIndex()) {
            modifiedSource.add(insertPosition + i, r)
        }

        expect("0.99911112222") { String(modifiedSource.toCharArray()) }
    }

    @Test
    fun testReplacePartWithDotLimitedTo12() {
        val source = "0.11112222".toSpannedMock()
        val value = "99.99"
        val filter = DecimalInputFilter(Locale.getDefault(), 12)
        val insertPosition = 2
        val result = filter.filter(value, 0, value.length, source, insertPosition, value.length + 2)
        expect("9999") { result }
        val modifiedSource = source.toString().toCharArray().toMutableList()
        for ((i, r) in result.withIndex()) {
            modifiedSource.add(insertPosition + i, r)
        }

        expect("0.999911112222") { String(modifiedSource.toCharArray()) }
    }

    @Test
    fun testReplacePartNothingCanBeInserted() {
        val source = "0.11112222".toSpannedMock()
        val value = "99.99"
        val filter = DecimalInputFilter(Locale.getDefault(), 8)
        val insertPosition = 2
        val result = filter.filter(value, 0, value.length, source, insertPosition, value.length + 2)
        expect("") { result }
        val modifiedSource = source.toString().toCharArray().toMutableList()
        for ((i, r) in result.withIndex()) {
            modifiedSource.add(insertPosition + i, r)
        }

        expect("0.11112222") { String(modifiedSource.toCharArray()) }
    }

    @Test
    fun testReplacePartNothingCanBeInsertedBeforeDot() {
        val source = "0.11112222".toSpannedMock()
        val value = "99.99"
        val filter = DecimalInputFilter(Locale.getDefault(), 20)
        val insertPosition = 0
        val result = filter.filter(value, 0, value.length, source, insertPosition, value.length + 2)
        expect("") { result }
        val modifiedSource = source.toString().toCharArray().toMutableList()
        for ((i, r) in result.withIndex()) {
            modifiedSource.add(insertPosition + i, r)
        }

        expect("0.11112222") { String(modifiedSource.toCharArray()) }
    }

    @Test
    fun testReplacePartNothingCanBeInsertedToDot() {
        val source = "0.11112222".toSpannedMock()
        val value = "99.99"
        val filter = DecimalInputFilter(Locale.getDefault(), 20)
        val insertPosition = 1
        val result = filter.filter(value, 0, value.length, source, insertPosition, value.length + 2)
        expect("") { result }
        val modifiedSource = source.toString().toCharArray().toMutableList()
        for ((i, r) in result.withIndex()) {
            modifiedSource.add(insertPosition + i, r)
        }

        expect("0.11112222") { String(modifiedSource.toCharArray()) }
    }

    @Test
    fun testInsertDecimalValueIntoIntegerValue() {
        val source = "11112222".toSpannedMock()
        val value = "99.99"
        val filter = DecimalInputFilter(Locale.getDefault(), 20)
        val insertPosition = 2
        val result = filter.filter(value, 0, value.length, source, insertPosition, value.length + 2)
        expect("9999") { result }
        val modifiedSource = source.toString().toCharArray().toMutableList()
        for ((i, r) in result.withIndex()) {
            modifiedSource.add(insertPosition + i, r)
        }

        expect("119999112222") { String(modifiedSource.toCharArray()) }
    }

    @Test
    fun testInsertDecimalValueIntoIntegerValueWithoutDecimalsLimit() {
        val source = "11112222".toSpannedMock()
        val value = "99.99"
        val filter = DecimalInputFilter(Locale.getDefault(), -1)
        val insertPosition = 2
        val result = filter.filter(value, 0, value.length, source, insertPosition, value.length + 2)
        expect("9999") { result }
        val modifiedSource = source.toString().toCharArray().toMutableList()
        for ((i, r) in result.withIndex()) {
            modifiedSource.add(insertPosition + i, r)
        }

        expect("119999112222") { String(modifiedSource.toCharArray()) }
    }

    @Test
    fun testInsertDecimalValueIntoDecimalValueWithoutDecimalsLimit() {
        val source = "1111.2222".toSpannedMock()
        val value = "99.99"
        val filter = DecimalInputFilter(Locale.getDefault(), -1)
        val insertPosition = 5
        val result = filter.filter(value, 0, value.length, source, insertPosition, value.length + 2)
        expect("9999") { result }
        val modifiedSource = source.toString().toCharArray().toMutableList()
        for ((i, r) in result.withIndex()) {
            modifiedSource.add(insertPosition + i, r)
        }

        expect("1111.99992222") { String(modifiedSource.toCharArray()) }
    }

    @Test
    fun testLimit4DecimalsReplace() {
        val value = "0.11112222"
        val filter = DecimalInputFilter(Locale.getDefault(), 4)
        val result = filter.filter(value, 0, value.length, SpannedString(""), 0, value.length)

        expect("0.1111") { result }
    }

    @Test
    fun testLimit8DecimalsReplace() {
        val value = "0.11112222"
        val filter = DecimalInputFilter(Locale.getDefault(), 8)
        val result = filter.filter(value, 0, value.length, SpannedString(""), 0, value.length)

        expect("0.11112222") { result }
    }

    @Test
    fun testLimit4DecimalsSequential() {
        val value = "0.11112222"
        val result = sequentialFilter(value, 4)

        expect("0.1111") { result }
    }

    @Test
    fun testLimit8DecimalsSequential() {
        val value = "0.11112222"
        val result = sequentialFilter(value, 8)

        expect("0.11112222") { result }
    }

    @Test
    fun testNoLimitDecimalsSequential() {
        val value = "0.111122223333444455556666"
        val result = sequentialFilter(value, -1)

        expect("0.111122223333444455556666") { result }
    }
}