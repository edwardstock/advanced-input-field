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

package com.edwardstock.inputfield_sample

import android.view.View
import android.widget.EditText
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.jupiter.api.Test


/**
 * Advanced InputField. 2020
 * @author Eduard Maximovich (edward.vstock@gmail.com)
 */

class DecimalInputFilterTest {

    @Rule
    var activityRule: ActivityTestRule<TestActivity> = ActivityTestRule(TestActivity::class.java)

    @Before
    @Throws(Exception::class)
    fun setUp() {

    }

    @Test
    fun testOneByOneTyping() {
        activityRule.launchActivity(null)
        val testValues = mapOf(
            Pair("100#100", "100100"),
            Pair("#131i414819", "131414819"),
            Pair("00000", "0.0000"),
            Pair("0.0111.00", "0.011100"),
            Pair("00.1111", "0.01111"),
            Pair(".", "0."),
            Pair("0", "0"),
            Pair("06365", "0.6365"),
            Pair("100", "100"),
            Pair("0", "0"),
            Pair("-100", "100"),
            Pair("(5 + 5) = 10", "5510"),
            Pair("100.100", "100.100"),
            Pair("100,100", "100.100")
        )

        val inputInteraction = onView(withId(R.id.decimal))

        for (el in testValues) {
            for (c in el.key) {
                inputInteraction.perform(typeText(c.toString()))
            }
            inputInteraction.check(
                matches(
                    withText(
                        el.value
                    )
                )
            )


            inputInteraction.perform(clearText())
        }
    }

    @Test
    fun testPastingDecimal() {
        activityRule.launchActivity(null)
        val testValues = mapOf(
            Pair("100#100", "100100"),
            Pair("#131i414819", "131414819"),
            Pair("00000", "0.0000"),
            Pair("0.0111.00", "0.011100"),
            Pair("00.1111", "0.01111"),
            Pair(".", ""), //!!!!
            Pair("0", "0"),
            Pair("06365", "0.6365"),
            Pair("100", "100"),
            Pair("-100", "100"),
            Pair("(5 + 5) = 10", "5510"),
            Pair("100.100", "100.100"),
            Pair("100,100", "100.100")
        )

        val inputInteraction = onView(withId(R.id.decimal))

        var i = 0
        for (el in testValues) {
            System.out.println("Testing $i item: \"${el.key}\" -> \"${el.value}\"")
            inputInteraction.perform(replaceText(el.key))
            Thread.sleep(200)
            inputInteraction.check(matches(withText(el.value)))

            i++
        }
    }

    companion object {
        fun pasteText(text: String): ViewAction {
            return object : ViewAction {
                override fun getDescription(): String {
                    return "Paste text directly onto edittext"
                }

                override fun getConstraints(): Matcher<View> {
                    return allOf(isDisplayed(), isAssignableFrom(EditText::class.java))
                }

                override fun perform(uiController: UiController?, view: View?) {
                    if (view == null) {
                        return
                    }
                    val field = view as EditText
                    field.setText(text)
                }

            }
        }
    }

}