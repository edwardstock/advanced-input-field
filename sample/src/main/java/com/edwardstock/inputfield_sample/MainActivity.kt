package com.edwardstock.inputfield_sample

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.edwardstock.inputfield.InputField
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
        val password: InputField = findViewById(R.id.input_password)
        val passwordRepeat: InputField = findViewById(R.id.input_password_repeat)
        val message: InputField = findViewById(R.id.input_message)
        val actionSubmit: Button = findViewById(R.id.action_submit)
        val actionReset: Button = findViewById(R.id.action_clear)

        inputGroup.setup {
            add(email, EmailValidator())
            add(password, LengthValidator(4).setErrorMessage("Password length should be from 4 symbols"))
            add(passwordRepeat, CompareValidator(password).setErrorMessage("Passwords must be equals"))
            add(message, CompareValidator("mx").apply {
                errorMessage = "Message should be \"mx\""
                isRequired = false
            })
        }

        inputGroup.addValidateRelation(passwordRepeat, password)

        actionReset.setOnClickListener {
            inputGroup.reset()
        }

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

        message.setOnSuffixImageClickListener {
            if (!message.text.isNullOrEmpty()) {
                message.text = null
                return@setOnSuffixImageClickListener
            }
            message.inputOverlayVisible = true

            message.input.clearFocus()
            message.textAllCaps
        }

        message.inputOverlay?.setOnClickListener {
            message.inputOverlayVisible = false
            message.input.requestFocus()
        }


    }
}