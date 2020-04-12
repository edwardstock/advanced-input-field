package com.edwardstock.inputfield.paris

import android.graphics.Typeface
import android.text.method.DigitsKeyListener
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import com.airbnb.paris.annotations.AfterStyle
import com.airbnb.paris.annotations.Attr
import com.airbnb.paris.annotations.Styleable
import com.airbnb.paris.proxies.BaseProxy
import com.airbnb.paris.styles.Style
import com.edwardstock.inputfield.R2

/**
 * Advanced InputField. 2020
 * @author Eduard Maximovich <edward.vstock@gmail.com>
 */
@Styleable("IF_EditText")
class IFEditTextProxy(view: EditText) : BaseProxy<IFEditTextProxy, EditText>(view) {
    private var oldTypeface: Typeface? = null

    @Attr(R2.styleable.IF_EditText_android_inputType)
    fun setInputType(inputType: Int) {
        if ((inputType and EditorInfo.TYPE_TEXT_VARIATION_PASSWORD) == EditorInfo.TYPE_TEXT_VARIATION_PASSWORD) {
            oldTypeface = view.typeface
        }

        view.inputType = inputType

    }

    @Attr(R2.styleable.IF_EditText_android_digits)
    fun setDigits(digits: String?) {
        if (!digits.isNullOrEmpty()) {
            view.keyListener = DigitsKeyListener.getInstance(digits)
        }
    }

    @AfterStyle
    fun afterStyle(@Suppress("UNUSED_PARAMETER") style: Style?) {
        view.isSingleLine =
            (view.inputType and EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE) != EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE

        oldTypeface?.let {
            view.typeface = oldTypeface
        }
    }


}