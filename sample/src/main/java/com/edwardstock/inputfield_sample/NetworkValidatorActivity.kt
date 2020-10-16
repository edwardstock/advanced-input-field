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
import com.edwardstock.inputfield.form.validators.BaseValidator
import io.reactivex.Single
import io.reactivex.SingleEmitter
import java.util.concurrent.TimeUnit
import kotlin.random.Random

/**
 * Advanced InputField. 2020
 * @author Eduard Maximovich (edward.vstock@gmail.com)
 */
class NetworkValidatorActivity : AppCompatActivity() {

    internal class NetworkDummyValidator @JvmOverloads constructor(
        errorMessage: CharSequence = "Value is invalid",
        required: Boolean = true
    ) : BaseValidator(errorMessage, required) {

        override fun validate(value: CharSequence?): Single<Boolean> {
            return Single
                .create { emitter: SingleEmitter<Boolean> ->
                    if (value.toString() == "wow" || value.toString() == "one") {
                        Log.d("OnInputChanged", "validator validate value $value: true")
                        emitter.onSuccess(true)
                    } else {
                        Log.d("OnInputChanged", "validator validate value $value: false")
                        emitter.onSuccess(false)
                    }
                }
                .delay(Random.nextLong(100, 1000), TimeUnit.MILLISECONDS)
            // fake delay, simulate networking
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network_validator)

        val inputGroup = InputGroup()

        val input: InputField = findViewById(R.id.input_email)
        val action: Button = findViewById(R.id.action_submit)


        inputGroup.addInput(input)
        inputGroup.addValidator(input, NetworkDummyValidator())
    }
}