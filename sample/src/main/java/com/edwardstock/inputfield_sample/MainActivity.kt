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

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.edwardstock.inputfield.InputField
import com.edwardstock.inputfield.InputFieldAutocomplete
import com.edwardstock.inputfield.form.DecimalInputFilter
import com.edwardstock.inputfield.form.InputGroup
import com.edwardstock.inputfield.form.validators.CompareValidator
import com.edwardstock.inputfield.form.validators.EmailValidator
import com.edwardstock.inputfield.form.validators.LengthValidator

class MainActivity : AppCompatActivity() {

    val values: HashMap<String, String> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val inputGroup = InputGroup()

        val email: InputField = findViewById(R.id.input_email)
        val name: InputFieldAutocomplete = findViewById(R.id.input_name)
        val password: InputField = findViewById(R.id.input_password)
        val passwordRepeat: InputField = findViewById(R.id.input_password_repeat)
        val amount: InputField = findViewById(R.id.input_message)
        val actionSubmit: Button = findViewById(R.id.action_submit)
        val actionNoExtras: Button = findViewById(R.id.action_extras_empty)
        val actionWithExtras: Button = findViewById(R.id.action_extras_filled)
        val actionNetworkValidator: Button = findViewById(R.id.action_network_validator)

        actionNetworkValidator.setOnClickListener {
            startActivity(Intent(this, NetworkValidatorActivity::class.java))
        }

        actionNoExtras.setOnClickListener {
            startActivity(Intent(this, ExtrasActivity::class.java))
        }
        actionWithExtras.setOnClickListener {
            val intent = Intent(this, ExtrasActivity::class.java)
            intent.putExtra("email", "abc@def.com")
            intent.putExtra("value", "10")
            startActivity(intent)
        }

        val intent = Intent(this, ExtrasActivity::class.java)
        intent.putExtra("email", "abc@def.com")
        intent.putExtra("value", "10")
        startActivity(intent)

        inputGroup.setup {
            add(name)
            add(email, EmailValidator())
            add(password, LengthValidator(4).apply { errorMessage = "Password length should be from 4 symbols" })
            add(passwordRepeat, CompareValidator(password).apply { errorMessage = "Passwords must be equals" })
            add(amount)
        }
        inputGroup.addFilter(amount, DecimalInputFilter(amount))

        val nameAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1)
        nameAdapter.addAll(
            "Jahn",
            "Jane",
            "Jaseph",
            "Jamal",
            "Jakie",
            "James",
            "Jessica",
            "Smith",
            "Kara",
            "Sam",
            "Vasya"
        )
        nameAdapter.notifyDataSetChanged()
        name.input.setAdapter(nameAdapter)


        inputGroup.addValidateRelation(passwordRepeat, password)

        inputGroup.addFormValidateListener {
            actionSubmit.isEnabled = it
            Log.d("Form", "Form is valid: $it")
        }
        inputGroup.addTextChangedListener { input, valid ->
//            Log.d("Form","Field ${input.fieldName} is valid: $valid")
            if (valid) {
                values[input.fieldName!!] = input.text!!.toString()
            }
//            Log.d("Form", values.toString())
        }

        amount.setOnSuffixImageClickListener {
            if (!amount.text.isNullOrEmpty()) {
                amount.text = null
                return@setOnSuffixImageClickListener
            }
            amount.inputOverlayVisible = true

            amount.input.clearFocus()
            amount.textAllCaps
        }

        amount.inputOverlay?.setOnClickListener {
            amount.inputOverlayVisible = false
            amount.input.requestFocus()
        }


    }
}