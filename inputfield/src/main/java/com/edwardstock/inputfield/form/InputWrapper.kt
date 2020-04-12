package com.edwardstock.inputfield.form

import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.view.ViewParent
import android.widget.EditText
import android.widget.TextView
import com.edwardstock.inputfield.InputField
import com.google.android.material.textfield.TextInputLayout

/**
 * Advanced InputField. 2020
 * @author Eduard Maximovich <edward.vstock@gmail.com>
 */
class InputWrapper {
    private val field: View
    private val input: EditText
    private val cls: Class<*>

    constructor(a: EditText) {
        field = a
        input = a
        cls = a.javaClass
    }

    constructor(b: InputField) {
        field = b
        input = b.input
        cls = b.javaClass
    }

    val isInputField: Boolean
        get() = this.field is InputField

    val inputField: InputField
        get() = this.field as InputField

    val editText: EditText
        get() = input

    var filters: Array<InputFilter>
        get() = input.filters
        set(v) {
            input.filters = v
        }

    var error: CharSequence?
        get() {
            return when (this.field) {
                is EditText -> this.field.error
                is InputField -> this.field.error
                else -> null
            }
        }
        set(v) {
            when (this.field) {
                is EditText -> {
                    this.field.error = v
                }
                is InputField -> {
                    this.field.error = v
                }
            }
        }

    var text: Editable?
        get() = input.text
        set(v) {
            input.text = v
        }

    fun requestFocus() {
        field.requestFocus()
    }

    fun post(runnable: () -> Unit) {
        post(Runnable { runnable() })
    }

    fun post(runnable: Runnable) {
        field.post(runnable)
    }

    fun postDelayed(runnable: Runnable, timeout: Long) {
        field.postDelayed(runnable, timeout)
    }

    fun setOnEditorActionListener(l: TextView.OnEditorActionListener?) {
        input.setOnEditorActionListener(l)
    }

    fun getParent(): ViewParent? {
        return field.parent
    }

    fun addTextChangedListener(textWatcher: TextWatcher) {
        input.addTextChangedListener(textWatcher)
    }

    var tag: Any?
        get() = this.field.tag
        set(v) {
            this.field.tag = v
        }

    val id: Int
        get() = this.field.id

    fun hasFieldName(): Boolean {
        return field.tag != null && field.tag is String
    }

    var fieldName: String?
        get() {
            return when (this.field) {
                is EditText -> input.tag as String
                is InputField -> inputField.fieldName
                else -> null
            }
        }
        set(v) {
            when (this.field) {
                is EditText -> input.tag = v
                is InputField -> inputField.fieldName = v
            }
        }

    val hasParentTextInputLayout: Boolean
        get() = getParent() != null && getParent()!!.parent is TextInputLayout

    val parentTextInputLayout: TextInputLayout
        get() = getParent()!!.parent as TextInputLayout

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is InputWrapper) return false

        if (field != other.field) return false
        if (input != other.input) return false
        if (cls != other.cls) return false

        return true
    }

    override fun hashCode(): Int {
        var result = field.hashCode()
        result = 31 * result + input.hashCode()
        result = 31 * result + cls.hashCode()
        return result
    }
}