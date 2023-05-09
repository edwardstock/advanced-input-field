package com.edwardstock.inputfield

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Parcelable
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.annotation.*
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.getIntOrThrow
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.core.widget.TextViewCompat
import com.edwardstock.inputfield.menu.InputMenuItem

/**
 * Advanced InputField. 2020
 * @author Eduard Maximovich <edward.vstock@gmail.com>
 */

open class InputField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    internal val b: InputFieldViewHolder<AppCompatEditText>

    var autoVisibleError: Boolean = true

    private var _isPasswordVisible = false
    var isPasswordVisible: Boolean
        get() = _isPasswordVisible
        set(v) {
            _isPasswordVisible = v
            if (!_isPasswordVisible) {
                input.transformationMethod = PasswordTransformationMethod()
            } else {
                input.transformationMethod = HideReturnsTransformationMethod()
            }
        }

    var fieldName: String?
        get() {
            val tag = input.tag ?: tag
            return tag as? String
        }
        set(v) {
            input.tag = v
        }

    var errorEnabled: Boolean
        get() = errorView.isVisible
        set(visible) {
            errorView.isVisible = visible
        }

    var labelEnabled: Boolean
        get() = b.label.isVisible
        set(enabled) {
            b.label.isVisible = enabled

        }

    open val input: EditText get() = b.input
    val inputOverlay: ViewStub get() = inputOverlayView

    var inputOverlayVisible: Boolean
        get() = b.inputOverlay.isVisible
        set(visible) {
            input.isFocusable = !visible
            input.isClickable = !visible
            inputOverlay.isVisible = visible
        }

    val hintTextColor: ColorStateList get() = input.hintTextColors

    var textAllCaps: Boolean
        @RequiresApi(Build.VERSION_CODES.P)
        get() = input.isAllCaps
        set(v) {
            input.isAllCaps = v
        }

    var maxLines: Int
        get() = input.maxLines
        set(v) {
            input.maxLines = v
        }

    private var isSingleLineCompat: Boolean = false
    var isSingleLine: Boolean
        get() {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                input.isSingleLine
            } else {
                isSingleLineCompat
            }
        }
        set(v) {
            isSingleLineCompat = v
            input.isSingleLine = v
        }

    var label: CharSequence?
        get() = labelView.text
        set(v) {
            labelView.text = v
        }

    var text: Editable?
        get() = input.text
        set(value) {
            input.text = value
        }

    val suffixMenu: SuffixMenuContainer get() = b.inputSuffixRoot

    var inputBackground: Drawable?
        get() = input.background
        set(v) {
            input.background = v
        }

    var imeOptions: Int
        get() = input.imeOptions
        set(v) {
            input.imeOptions = v
        }

    var inputType: Int
        get() = input.inputType
        set(v) {
            val oldTypeface: Typeface? = input.typeface
            input.inputType = v
            if (input.typeface != oldTypeface) {
                input.typeface = oldTypeface
            }
        }

    val isKindOfPassword: Boolean
        get() {
            val isTextPass = checkInputTypeFlag(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD)
            val isNumPass = checkInputTypeFlag(EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD)
            return isTextPass || isNumPass
        }

    var hint: CharSequence?
        get() = input.hint
        set(v) {
            input.hint = v
        }

    var maxLength: Int
        get() {
            val filter = filters.firstOrNull { it is LengthFilter } as LengthFilter?
            return filter?.max ?: -1
        }
        set(v) {
            val f: MutableList<InputFilter> = filters.filter { it !is LengthFilter }.toMutableList()
            f.add(LengthFilter(v))
            filters = f.toTypedArray()
        }

    var maskFormatter: SimpleMaskFormatter? = null
        set(v) {
            field = v?.apply {
                input.addTextChangedListener(textWatcher(input))
            }
        }

    val labelView: TextView get() = b.label
    val inputOverlayView: ViewStub get() = b.inputOverlay
    var inputOverlayInflatedView: View? = null
    val errorView: TextView get() = b.error

    private val parentScrollView by lazy(LazyThreadSafetyMode.NONE) {
        findParentOfType<ScrollView>() ?: findParentOfType<NestedScrollView>()
    }

    var filters: Array<InputFilter>
        get() = input.filters
        set(f) {
            input.filters = f
        }


    var error: CharSequence?
        get() = errorView.text
        set(text) {
            errorView.text = text
            if (autoVisibleError && !errorEnabled && text != null) {
                errorEnabled = true
            }
            if (text != null) {
                scrollToError()
            }
        }

    @Suppress("UNCHECKED_CAST")
    internal open fun <T : TextView> createViewHolder(
        context: Context,
        labelStyleRes: Int,
        inputStyle: Int,
        suffixMenuStyle: Int,
        overlayStyle: Int,
        errorStyle: Int,
    ): InputFieldViewHolder<T> {
        return InputFieldViewHolder(
            context,
            inputCreator = { AppCompatEditText(it) as T },
            rootLayoutStyle = 0,
            labelStyle = labelStyleRes,
            inputStyle = inputStyle,
            inputSuffixStyle = suffixMenuStyle,
            inputOverlayStyle = overlayStyle,
            errorStyle = errorStyle,
        )
    }

    fun setMask(mask: String) {
        maskFormatter = SimpleMaskFormatter(mask).apply {
            input.addTextChangedListener(textWatcher(input))
        }
    }

    fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            input.transformationMethod = PasswordTransformationMethod()
        } else {
            input.transformationMethod = HideReturnsTransformationMethod()
        }
        isPasswordVisible = !isPasswordVisible
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        input.isEnabled = enabled
        inputOverlayView.isEnabled = enabled
        labelView.isEnabled = enabled
        errorView.isEnabled = enabled
    }

    fun setInputOverlay(@LayoutRes layoutRes: Int) {
        inputOverlayView.layoutResource = layoutRes
        inputOverlayInflatedView = inputOverlayView.inflate()
    }

    @SuppressLint("ResourceType")
    fun setInputOverlay(@LayoutRes layoutRes: Int, onInflatedView: (View) -> Unit) {
        if (layoutRes > 0) {
            b.inputOverlay.layoutResource = layoutRes
            inputOverlayInflatedView = inputOverlayView.inflate()
            onInflatedView(inputOverlayInflatedView!!)
        }
    }

    override fun setFocusable(focusable: Boolean) {
        super.setFocusable(focusable)
        input.isFocusable = focusable
    }

    override fun setFocusableInTouchMode(focusableInTouchMode: Boolean) {
        super.setFocusableInTouchMode(focusableInTouchMode)
        input.isFocusableInTouchMode = focusableInTouchMode
    }

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        isFocusable = false
        input.setOnClickListener(l)
        labelView.setOnClickListener(l)
        errorView.setOnClickListener(l)
    }

    fun setSuffixMenuItemStyle(@StyleRes styleRes: Int) {
        b.inputSuffixRoot.setItemStyle(styleRes)
    }

    fun setOnSuffixMenuItemClickListener(onMenuItemClickListener: OnSuffixMenuItemClickListener) {
        b.inputSuffixRoot.setItemClickListener(onMenuItemClickListener)
    }

    fun setSuffixMenuMoreIcon(@DrawableRes drawableRes: Int) {
        b.inputSuffixRoot.setMoreIcon(drawableRes)
    }

    fun setSuffixMenu(@MenuRes menuRes: Int) {
        b.inputSuffixRoot.setSuffixMenu(menuRes)
    }

    fun setSuffixMenu(@MenuRes menuRes: Int, onMenuItemClickListener: OnSuffixMenuItemClickListener) {
        b.inputSuffixRoot.setSuffixMenu(menuRes, onMenuItemClickListener)
    }

    fun setSuffixMenu(@MenuRes menuRes: Int, onCreateMenu: (Menu) -> Unit, onMenuItemClickListener: OnSuffixMenuItemClickListener) {
        b.inputSuffixRoot.setSuffixMenu(menuRes, onCreateMenu, onMenuItemClickListener)
    }

    fun setSuffixMenu(menuItems: List<InputMenuItem>) {
        b.inputSuffixRoot.setSuffixMenu(menuItems)
    }

    fun setLabelTextAppearance(@StyleRes resId: Int) {
        TextViewCompat.setTextAppearance(labelView, resId)
    }

    fun setErrorTextAppearance(@StyleRes resId: Int) {
        TextViewCompat.setTextAppearance(errorView, resId)
    }

    fun setInputTextAppearance(@StyleRes resId: Int) {
        TextViewCompat.setTextAppearance(input, resId)
    }

    fun setHintTextColor(hintTextColor: Int) {
        input.setHintTextColor(hintTextColor)
    }

    fun setHintTextColor(hintTextColor: ColorStateList) {
        input.setHintTextColor(hintTextColor)
    }

    fun setInputMinHeight(@Px minHeight: Int) {
        input.minHeight = minHeight
    }

    @SuppressLint("ResourceType")
    fun setInputMinHeightRes(@DimenRes resId: Int) {
        val dimens: Int = resources.getDimension(resId).toInt()
        input.minHeight = dimens
    }

    fun setTextColor(@ColorInt color: Int) {
        input.setTextColor(color)
    }

    fun setTextColor(color: ColorStateList) = input.setTextColor(color)

    fun setTextColorRes(@ColorRes color: Int) {
        if (color == 0) return

        try {
            input.setTextColor(ContextCompat.getColorStateList(context, color))
        } catch (e: Throwable) {
            input.setTextColor(ContextCompat.getColor(context, color))
        }

    }

    fun setTextSize(@Px textSize: Int) {
        input.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
    }

    fun setTextSize(unit: Int, size: Float) {
        input.setTextSize(unit, size)
    }

    fun setTextAllCaps(@BoolRes resId: Int) {
        input.isAllCaps = resources.getBoolean(resId)
    }

    fun setFontFamily(@FontRes fontRes: Int) {
        val typeface = ResourcesCompat.getFont(context, fontRes)
        input.typeface = typeface
    }

    /**
     * Hack to force disable keyboard suggestions and autocorrection
     */
    fun setInputDisableSuggestions(disable: Boolean) {
        if (disable) {
            inputType = inputType or (InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
        }
    }

    override fun setNextFocusDownId(@IdRes nextFocusDownId: Int) {
        input.nextFocusDownId = nextFocusDownId
    }

    fun setLabelTextColor(color: Int) {
        labelView.setTextColor(color)
    }

    fun setLabelTextColor(color: ColorStateList) {
        labelView.setTextColor(color)
    }

    fun setLabelTextSize(size: Float) {
        labelView.textSize = size
    }

    fun setLabelTextSize(unit: Int, size: Float) {
        labelView.setTextSize(unit, size)
    }

    fun setErrorTextColor(color: Int) {
        errorView.setTextColor(color)
    }

    fun setErrorTextColor(color: ColorStateList) {
        errorView.setTextColor(color)
    }

    fun setInputBackgroundRes(@DrawableRes res: Int) {
        input.setBackgroundResource(res)
    }

    fun setDigits(digits: String) {
        input.keyListener = DigitsKeyListener.getInstance(digits)
    }

    fun setText(text: CharSequence?, bufferType: TextView.BufferType) {
        input.setText(text, bufferType)
    }

    fun setText(@StringRes resId: Int) {
        input.setText(resId)
    }

    fun setText(text: CharSequence?) {
        input.setText(text)
    }

    fun setSelection(index: Int) = input.setSelection(index)
    fun setSelection(start: Int, stop: Int) = input.setSelection(start, stop)

    fun setHint(@StringRes resId: Int) {
        hint = resources.getString(resId)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setAutofillHints(autofillHints: String) {
        input.setAutofillHints(autofillHints)
    }

    fun setOnEditorActionListener(listener: TextView.OnEditorActionListener?) {
        input.setOnEditorActionListener(listener)
    }

    fun addTextChangedListener(textWatcher: TextWatcher) {
        input.addTextChangedListener(textWatcher)
    }

    override fun onSaveInstanceState(): Parcelable? {
        val parcel = super.onSaveInstanceState()
        return InputFieldSavedState(parcel, text)
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state !is InputFieldSavedState) {
            super.onRestoreInstanceState(state)
        } else {
            super.onRestoreInstanceState(state.superState)
            if (!state.text.isNullOrEmpty()) {
                setText(state.text)
            }
        }
    }

    fun addTextChangedSimpleListener(listener: (CharSequence?) -> Unit) {
        input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                listener(s)
            }
        })
    }

    fun scrollToError() {
        // Wait a bit for other UI changes
        parentScrollView?.postDelayed({
            parentScrollView!!.scrollDownTo(errorView)
        }, 160)
    }

    private fun checkInputTypeFlag(flag: Int): Boolean {
        return (inputType and flag) == flag
    }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.InputField, defStyleAttr, defStyleRes)
        val labelStyleRes = a.getResourceId(R.styleable.InputField_labelStyle, 0)
        val suffixMenuStyle = a.getResourceId(R.styleable.InputField_suffixMenuStyle, 0)
        val errorStyle = a.getResourceId(R.styleable.InputField_errorStyle, 0)
        val overlayStyle = a.getResourceId(R.styleable.InputField_overlayStyle, 0)
        val inputStyle = a.getResourceId(R.styleable.InputField_inputStyle, 0)
        b = createViewHolder(context, labelStyleRes, inputStyle, suffixMenuStyle, overlayStyle, errorStyle)
        b.inputSuffixRoot.setInputGetter { input }
        addView(b.rootLayout)

        setInputMinHeight((48 / context.resources.displayMetrics.density).toInt())
        setHintTextColor(R.color.aif_grey)
        inputBackground = ContextCompat.getDrawable(context, R.drawable.aif_edittext_states)
        labelEnabled = true
        setLabelTextAppearance(R.style.InputFieldLabelTextAppearance)
        setErrorTextAppearance(R.style.InputFieldErrorTextAppearance)
        setInputTextAppearance(R.style.InputFieldInputTextAppearance)
        isSaveEnabled = true
        input.isSaveEnabled = false

        // setting mask before getting android_text
        if (a.hasValue(R.styleable.InputField_inputMask)) {
            val mask = a.getString(R.styleable.InputField_inputMask)
            val maskChar = a.getString(R.styleable.InputField_inputMaskChar)?.get(0) ?: '#'
            val maskStablePlaceholders = a.getBoolean(R.styleable.InputField_inputMaskStablePlaceholders, false)
            val maskStablePlaceholderChar = a.getString(R.styleable.InputField_inputMaskStablePlaceholderChar)?.get(0) ?: ' '
            mask?.let {
                maskFormatter = SimpleMaskFormatter(mask, maskChar, maskStablePlaceholders, maskStablePlaceholderChar)
            }
        }

        if (a.hasValue(R.styleable.InputField_android_enabled)) {
            isEnabled = a.getBoolean(R.styleable.InputField_android_enabled, true)
        }
        if (a.hasValue(R.styleable.InputField_android_inputType)) {
            inputType = a.getInt(R.styleable.InputField_android_inputType, 0)

            val isTextPass = checkInputTypeFlag(EditorInfo.TYPE_TEXT_VARIATION_PASSWORD)
            val isNumPass = checkInputTypeFlag(EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD)

            _isPasswordVisible = (!isTextPass && !isNumPass)
        }
        if (a.hasValue(R.styleable.InputField_android_imeOptions)) {
            imeOptions = a.getInt(R.styleable.InputField_android_imeOptions, 0)
        }
        if (a.hasValue(R.styleable.InputField_android_digits)) {
            val digits = a.getString(R.styleable.InputField_android_digits)
            digits?.let(::setDigits)
        }
        if (a.hasValue(R.styleable.InputField_android_text)) {
            val text = a.getString(R.styleable.InputField_android_text)
            setText(text)
        }
        if (a.hasValue(R.styleable.InputField_android_hint)) {
            hint = a.getString(R.styleable.InputField_android_hint)
        }
        if (a.hasValue(R.styleable.InputField_android_textSize)) {
            a.getDimensionPixelSize(R.styleable.InputField_android_textSize, 0).let {
                setTextSize(TypedValue.COMPLEX_UNIT_PX, it.toFloat())
            }
        }
        if (a.hasValue(R.styleable.InputField_android_fontFamily)) {
            a.getResourceId(R.styleable.InputField_android_fontFamily, 0).let {
                setFontFamily(it)
            }
        }
        if (a.hasValue(R.styleable.InputField_android_textColor)) {
            val stateList = a.getColorStateList(R.styleable.InputField_android_textColor)
            if (stateList != null) {
                setTextColor(stateList)
            } else {
                setTextColor(a.getColor(R.styleable.InputField_android_textColor, 0))
            }
        }
        if (a.hasValue(R.styleable.InputField_android_textColorHint)) {
            val stateList = a.getColorStateList(R.styleable.InputField_android_textColorHint)
            if (stateList != null) {
                setHintTextColor(stateList)
            } else {
                setHintTextColor(a.getColor(R.styleable.InputField_android_textColorHint, 0))
            }
        }
        if (a.hasValue(R.styleable.InputField_android_maxLength)) {
            maxLength = a.getInt(R.styleable.InputField_android_maxLength, 0)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && a.hasValue(R.styleable.InputField_android_autofillHints)) {
            a.getString(R.styleable.InputField_android_autofillHints)?.let {
                setAutofillHints(it)
            }
        }
        if (a.hasValue(R.styleable.InputField_android_textAllCaps)) {
            textAllCaps = a.getBoolean(R.styleable.InputField_android_textAllCaps, false)
        }
        if (a.hasValue(R.styleable.InputField_android_nextFocusDown)) {
            nextFocusDownId = a.getResourceId(R.styleable.InputField_android_nextFocusDown, 0)
        }
        if (a.hasValue(R.styleable.InputField_android_focusable)) {
            isFocusable = a.getBoolean(R.styleable.InputField_android_focusable, true)
        }
        if (a.hasValue(R.styleable.InputField_android_focusableInTouchMode)) {
            isFocusableInTouchMode = a.getBoolean(R.styleable.InputField_android_focusableInTouchMode, true)
        }
        if (a.hasValue(R.styleable.InputField_android_maxLines)) {
            maxLines = a.getIntOrThrow(R.styleable.InputField_android_maxLines)
        }
        if (a.hasValue(R.styleable.InputField_android_singleLine)) {
            isSingleLine = a.getBoolean(R.styleable.InputField_android_singleLine, false)
        }
        if (a.hasValue(R.styleable.InputField_fieldName)) {
            fieldName = a.getString(R.styleable.InputField_fieldName)
        }
        if (a.hasValue(R.styleable.InputField_suffixMenuItemStyle)) {
            setSuffixMenuItemStyle(a.getResourceId(R.styleable.InputField_suffixMenuItemStyle, 0))
        }
        if (a.hasValue(R.styleable.InputField_suffixMenu)) {
            setSuffixMenu(a.getResourceId(R.styleable.InputField_suffixMenu, 0))
        }
        if (a.hasValue(R.styleable.InputField_suffixMenuMoreIcon)) {
            setSuffixMenuMoreIcon(a.getResourceId(R.styleable.InputField_suffixMenuMoreIcon, 0))
        }
        if (a.hasValue(R.styleable.InputField_label)) {
            label = a.getString(R.styleable.InputField_label)
        }
        if (a.hasValue(R.styleable.InputField_labelEnabled)) {
            labelEnabled = a.getBoolean(R.styleable.InputField_labelEnabled, true)
        }
        if (a.hasValue(R.styleable.InputField_labelTextAppearance)) {
            setLabelTextAppearance(a.getResourceId(R.styleable.InputField_labelTextAppearance, 0))
        }
        if (a.hasValue(R.styleable.InputField_errorEnabled)) {
            errorEnabled = a.getBoolean(R.styleable.InputField_errorEnabled, true)
        }
        if (a.hasValue(R.styleable.InputField_errorTextAppearance)) {
            setErrorTextAppearance(a.getResourceId(R.styleable.InputField_errorTextAppearance, 0))
        }
        if (a.hasValue(R.styleable.InputField_inputTextAppearance)) {
            setInputTextAppearance(a.getResourceId(R.styleable.InputField_inputTextAppearance, 0))
        }
        if (a.hasValue(R.styleable.InputField_inputOverlay)) {
            setInputOverlay(a.getResourceId(R.styleable.InputField_inputOverlay, 0))
        }
        if (a.hasValue(R.styleable.InputField_inputOverlayVisible)) {
            inputOverlayVisible = a.getBoolean(R.styleable.InputField_inputOverlayVisible, false)
        }
        if (a.hasValue(R.styleable.InputField_inputMinHeight)) {
            val res = a.getResourceId(R.styleable.InputField_inputMinHeight, 0)
            if (res == 0) {
                setInputMinHeight(a.getDimensionPixelSize(R.styleable.InputField_inputMinHeight, 0))
            } else {
                setInputMinHeightRes(res)
            }
        }
        if (a.hasValue(R.styleable.InputField_inputBackground)) {
            inputBackground = a.getDrawable(R.styleable.InputField_inputBackground)
        }
        if (a.hasValue(R.styleable.InputField_inputDisableSuggestions)) {
            setInputDisableSuggestions(a.getBoolean(R.styleable.InputField_inputDisableSuggestions, false))
        }
        if (a.hasValue(R.styleable.InputField_passwordVisible)) {
            isPasswordVisible = a.getBoolean(R.styleable.InputField_passwordVisible, false)
        }
        a.recycle()
    }
}