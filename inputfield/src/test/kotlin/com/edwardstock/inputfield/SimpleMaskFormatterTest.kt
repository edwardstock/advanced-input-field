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

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals


class SimpleMaskFormatterTest {

    @Test
    fun `should format input using mask`() {
        // given
        val formatter = SimpleMaskFormatter("(###) ###-####")

        // when
        val result = formatter.format("1234567890")

        // then
        assertEquals("(123) 456-7890", result)
    }

    @Test
    fun `should format input with stable placeholders using mask`() {
        // given
        val formatter = SimpleMaskFormatter("(###) ###-####", stablePlaceholders = true)

        // when
        val result = formatter.format("123")

        // then
        assertEquals("(123) ___-____", result)
    }

    @Test
    fun `should format input with stable placeholders using mask and placeholder char`() {
        // given
        val formatter = SimpleMaskFormatter("(###) ### ####", stablePlaceholders = true, stablePlaceholderChar = '-')

        // when
        val result = formatter.format("123")

        // then
        assertEquals("(123) --- ----", result)
    }

    @Test
    fun `should format input with spaces using mask`() {
        // given
        val formatter = SimpleMaskFormatter("(###) ###-####")

        // when
        val result = formatter.format("123 456 7890")

        // then
        assertEquals("(123) 456-7890", result)
    }

    @Test
    fun `should format input with extra chars using mask`() {
        // given
        val formatter = SimpleMaskFormatter("(###) ###-####")

        // when
        val result = formatter.format("1234567890 ext. 123")

        // then
        assertEquals("(123) 456-7890", result)
    }

    @Test
    fun `should format input with extra chars using mask and stable placeholders`() {
        // given
        val formatter = SimpleMaskFormatter("(###) ###-####", stablePlaceholders = true)

        // when
        val result = formatter.format("1234567890 ext. 123")

        // then
        assertEquals("(123) 456-7890", result)
    }

    @Test
    fun `should format empty input using mask with stable placeholders`() {
        // given
        val formatter = SimpleMaskFormatter("(###) ###-####", stablePlaceholders = true)

        // when
        val result = formatter.format("")

        // then
        assertEquals("(___) ___-____", result)
    }

    @Test
    fun `should format input using mask with different placeholder chars`() {
        // given
        val formatter = SimpleMaskFormatter("(***) ***-****", maskChar = '*', stablePlaceholderChar = '-')

        // when
        val result = formatter.format("1234567890")

        // then
        assertEquals("(123) 456-7890", result)
    }

    @Test
    fun `should format input with spaces using mask with different placeholder chars`() {
        // given
        val formatter = SimpleMaskFormatter("(###) ###-####", maskChar = '#', stablePlaceholderChar = '-')

        // when
        val result = formatter.format("123 456 7890")

        // then
        assertEquals("(123) 456-7890", result)
    }


}