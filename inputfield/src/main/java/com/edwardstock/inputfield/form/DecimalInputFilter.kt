package com.edwardstock.inputfield.form

import android.text.Spanned
import android.text.method.DigitsKeyListener
import android.widget.EditText

/**
 * Advanced InputField. 2020
 * @author Eduard Maximovich <edward.vstock@gmail.com>
 */
class DecimalInputFilter(
    private val mView: Lazy<EditText?>,
    private val mDecimals: Int
) : DigitsKeyListener(false, true) {

    constructor(txtView: Lazy<EditText?>) : this(txtView, 18)

    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dstart: Int,
        dend: Int
    ): CharSequence {
        val tmp: String = mView.value?.text.toString()

        if (source.length > 1) {
            return source.toString().replace("[^0-9\\.]".toRegex(), "")
        }

        when {
            source.length > 1 -> return source.toString().replace("[^0-9\\.]".toRegex(), "")
            source == "," -> return ""
            source == "." && tmp.isEmpty() -> return "0."
            tmp == "0" && source != "." -> return ""
            source == "." && tmp.contains(".") -> return ""
        }

        val ptIndex = tmp.indexOf(".")

        if (ptIndex == -1) {
            if (tmp == "0" && source == ".") {
                return source
            }
            return if (tmp.isNotEmpty() && (tmp[0] == '0' && dstart > 0 || tmp[0] != '0' && source == "0" && dstart == 0)) {
                ""
            } else source
        }
        if (ptIndex >= dstart) {
            if (tmp[0] == '.') {
                return source
            }
            if (tmp[0] == '0' && dstart > 0 || tmp[0] != '0' && source == "0" && dstart == 0) {
                return ""
            }
        } else if (ptIndex < dstart) {
            val decimals = tmp.substring(ptIndex + 1)
            if (decimals.length >= mDecimals) {
                return ""
            }
        }
        return super.filter(source, start, end, dest, dstart, dend)
    }


}