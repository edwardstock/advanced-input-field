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

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.edwardstock.inputfield.InputField
import com.edwardstock.inputfield.form.InputGroup
import com.edwardstock.inputfield.form.validators.DecimalValidator
import com.edwardstock.inputfield.form.validators.EmailValidator

/**
 * Advanced InputField. 2020
 * @author Eduard Maximovich (edward.vstock@gmail.com)
 */
class ExtrasActivity : AppCompatActivity() {
    private lateinit var email: InputField
    private lateinit var value: InputField
    private lateinit var action: Button
    private val inputGroup = InputGroup()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_extras)
        email = findViewById(R.id.email)
        value = findViewById(R.id.value)
        action = findViewById(R.id.action)

        inputGroup.addInput(email, value)
        inputGroup.addValidator(email, EmailValidator())
        inputGroup.addValidator(value, DecimalValidator())

        inputGroup.addFormValidateListener {
            Log.d("Extras", "Form valid: $it")
            action.isEnabled = it
        }

        if (intent.hasExtra("email")) {
            email.setText(intent.getStringExtra("email"))
        }
        if (intent.hasExtra("value")) {
            value.setText(intent.getStringExtra("value"))
        }
    }
}