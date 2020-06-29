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

package com.edwardstock.inputfield

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.InputType
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.ViewStub
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.core.widget.TextViewCompat
import com.airbnb.paris.annotations.Attr
import com.airbnb.paris.annotations.Styleable
import com.airbnb.paris.annotations.StyleableChild

/**
 * Advanced InputField. 2020
 * @author Eduard Maximovich <edward.vstock@gmail.com>
 */
@Suppress("LeakingThis")
@SuppressLint("NonConstantResourceId")
@Styleable("InputField")
open class InputField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    @StyleableChild(R2.styleable.InputField_labelStyle)
    internal var mLabelText: TextView? = null
    internal var mLabelExtView: LabelView? = null

    @StyleableChild(R2.styleable.InputField_errorStyle)
    internal var mErrorText: TextView? = null
    internal var mErrorExtView: ErrorView? = null

    @StyleableChild(R2.styleable.InputField_inputStyle)
    internal val mInput: EditText by lazy { findViewById<EditText>(R.id.aif_input) }
    internal val mSuffixRoot: ViewGroup by lazy { findViewById<ViewGroup>(R.id.aif_input_suffix_wrapper) }

    @StyleableChild(R2.styleable.InputField_suffixTextStyle)
    internal val mSuffixText: TextView by lazy { findViewById<TextView>(R.id.aif_input_suffix_text) }

    @StyleableChild(R2.styleable.InputField_suffixImageStyle)
    internal val mSuffixImage: ImageView by lazy { findViewById<ImageView>(R.id.aif_input_suffix_image) }
    internal val mInputOverlay: ViewStub by lazy { findViewById<ViewStub>(R.id.aif_input_overlay) }
    internal var mSuffixType: SuffixType = SuffixType.None
    internal var mOverlayView: View? = null
    internal var mInputSourcePadding: Rect? = null
    internal var mInputTargetPadding: Rect? = null
    internal var mAutoVisibleError: Boolean = true

    init {
        inflate(context, getLayoutId(), this)
        mLabelText = findViewById(R.id.aif_label)
        mErrorText = findViewById(R.id.aif_error)

        com.edwardstock.inputfield.Paris.style(this).apply(attrs)
    }

    @LayoutRes
    protected open fun getLayoutId(): Int {
        return R.layout.aif_input_field
    }

    open var fieldName: String?
        get() {
            return if (input.tag != null) {
                input.tag as String?
            } else {
                tag as String?
            }
        }
        @Attr(R2.styleable.InputField_fieldName)
        set(v) {
            input.tag = v
        }

    open var autoVisibleError: Boolean
        get() = mAutoVisibleError
        set(v) {
            mAutoVisibleError = v
        }

    enum class SuffixType(val value: Int) {
        None(-1),
        Text(0),
        Image(1);
    }

    @Attr(R2.styleable.InputField_android_enabled)
    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        input.isEnabled = enabled
        inputOverlay?.isEnabled = enabled
        labelView?.isEnabled = enabled
        errorView?.isEnabled = enabled
    }

    @SuppressLint("ResourceType")
    @Attr(R2.styleable.InputField_inputOverlay)
    open fun setInputOverlay(@LayoutRes layoutRes: Int) {
        if (layoutRes > 0) {
            mInputOverlay.layoutResource = layoutRes
            mOverlayView = mInputOverlay.inflate()
        }
    }

    @SuppressLint("ResourceType")
    open fun setInputOverlay(@LayoutRes layoutRes: Int, onInflatedView: (View) -> Unit) {
        if (layoutRes > 0) {
            mInputOverlay.layoutResource = layoutRes
            mOverlayView = mInputOverlay.inflate()
            onInflatedView(mOverlayView!!)
        }
    }

    val inputOverlay: View?
        get() = mOverlayView

    var inputOverlayVisible: Boolean
        get() = mInputOverlay.visibility == View.VISIBLE
        @Attr(R2.styleable.InputField_inputOverlayVisible)
        set(visible) {
            mInputOverlay.visibility = if (visible) View.VISIBLE else View.GONE
        }

    @Attr(R2.styleable.InputField_suffixImageWidth)
    fun setSuffixImageWidth(@Px width: Int) {
        val lp: ViewGroup.LayoutParams = mSuffixImage.layoutParams
        lp.width = width
        mSuffixImage.layoutParams = lp
    }

    @Attr(R2.styleable.InputField_suffixImageHeight)
    fun setSuffixImageHeight(@Px height: Int) {
        val lp: ViewGroup.LayoutParams = mSuffixImage.layoutParams
        lp.height = height
        mSuffixImage.layoutParams = lp
    }

    fun setSuffixImagePadding(@Px left: Int, @Px top: Int, @Px right: Int, @Px bottom: Int) {
        mSuffixImage.setPadding(left, top, right, bottom)
    }

    fun setSuffixImagePadding(rect: Rect) {
        mSuffixImage.setPadding(rect)
    }

    @Attr(R2.styleable.InputField_suffixImagePadding)
    fun setSuffixImagePadding(@Px padding: Int) {
        mSuffixImage.setPadding(padding)
    }

    @Attr(R2.styleable.InputField_suffixImagePadding)
    fun setSuffixImagePaddingRes(@DimenRes resId: Int) {
        mSuffixImage.setPaddingRes(resId)
    }

    fun setSuffixImageMargin(@Px left: Int, @Px top: Int, @Px right: Int, @Px bottom: Int) {
        mSuffixImage.setMargin(left, top, right, bottom)
    }

    @Attr(R2.styleable.InputField_suffixImageMargin)
    fun setSuffixImageMargin(@Px margin: Int) {
        mSuffixImage.setMargin(margin)
    }

    @Attr(R2.styleable.InputField_suffixImageMargin)
    fun setSuffixImageMarginRes(@DimenRes resId: Int) {
        mSuffixImage.setMarginRes(resId)
    }

    @Attr(R2.styleable.InputField_suffixType)
    internal fun setSuffixType(value: Int) {
        mSuffixType = SuffixType.None
        for (s in SuffixType.values()) {
            if (s.value == value) {
                setSuffixType(s)
                break
            }
        }
    }

    @Attr(R2.styleable.InputField_suffixImageSrc)
    fun setSuffixImageSrc(@DrawableRes resId: Int) {
        mSuffixImage.setImageResource(resId)
    }

    @Attr(R2.styleable.InputField_suffixImageTint)
    fun setSuffixImageTint(@ColorInt color: Int) {
        mSuffixImage.setColorFilter(color, PorterDuff.Mode.SRC_IN)
    }

    @Attr(R2.styleable.InputField_suffixText)
    fun setSuffixText(text: CharSequence?) {
        mSuffixText.text = text
    }

    fun setSuffixType(suffixType: SuffixType) {
        mSuffixType = suffixType

        mInputSourcePadding = input.getPaddingRect()
        mInputTargetPadding = input.getPaddingRect()

        when (mSuffixType) {
            SuffixType.Text -> {
                mSuffixImage.visibility = View.GONE
                mSuffixRoot.visibility = View.VISIBLE
                mSuffixText.visibility = View.VISIBLE
                waitInflatingAndSetPadding(mSuffixRoot)
            }
            SuffixType.Image -> {
                mSuffixText.visibility = View.GONE
                mSuffixRoot.visibility = View.VISIBLE
                mSuffixImage.visibility = View.VISIBLE
                waitInflatingAndSetPadding(mSuffixRoot)
            }
            else -> {
                mSuffixRoot.visibility = View.GONE
                mSuffixText.visibility = View.GONE
                mSuffixImage.visibility = View.GONE
                input.setPadding(mInputSourcePadding)
            }
        }
    }

    private fun waitInflatingAndSetPadding(view: ViewGroup) {
        if (view.width == 0) {
            view.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    mInputTargetPadding = mInputSourcePadding
                    mInputTargetPadding!!.right += view.width
                    input.setPadding(mInputTargetPadding)
                    view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        } else {
            mInputTargetPadding = mInputSourcePadding
            mInputTargetPadding!!.right -= view.width
            input.setPadding(mInputTargetPadding)
        }
    }

    fun getSuffixType(): SuffixType {
        return mSuffixType
    }

    fun setOnSuffixImageClickListener(listener: OnClickListener) {
        mSuffixImage.setOnClickListener(listener)
    }

    fun setOnSuffixImageClickListener(listener: (view: ImageView) -> Unit) {
        setOnSuffixImageClickListener(OnClickListener { v -> listener(v as ImageView) })
    }

    fun setOnSuffixTextClickListener(listener: OnClickListener) {
        mSuffixText.setOnClickListener(listener)
    }

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        input.setOnClickListener(l)
        labelView?.setOnClickListener(l)
        errorView?.setOnClickListener(l)
    }

    @Attr(R2.styleable.InputField_suffixTextAppearance)
    fun setSuffixTextAppearance(@StyleRes resId: Int) {
        TextViewCompat.setTextAppearance(mSuffixText, resId)
    }

    @Attr(R2.styleable.InputField_labelTextAppearance)
    fun setLabelTextAppearance(@StyleRes resId: Int) {
        if (mLabelText == null) return
        TextViewCompat.setTextAppearance(mLabelText!!, resId)
    }

    @Attr(R2.styleable.InputField_errorTextAppearance)
    fun setErrorTextAppearance(@StyleRes resId: Int) {
        if (mErrorText == null) return
        TextViewCompat.setTextAppearance(mErrorText!!, resId)
    }

    @Attr(R2.styleable.InputField_inputTextAppearance)
    fun setInputTextAppearance(@StyleRes resId: Int) {
        TextViewCompat.setTextAppearance(input, resId)
    }

    @Attr(R2.styleable.InputField_android_textColorHint)
    fun setHintTextColor(hintTextColor: Int) {
        input.setHintTextColor(hintTextColor)
    }

    @Attr(R2.styleable.InputField_inputMinHeight)
    fun setInputMinHeight(@Px minHeight: Int) {
        input.minHeight = minHeight
    }

    @SuppressLint("ResourceType")
    @Attr(R2.styleable.InputField_inputMinHeight)
    fun setInputMinHeightRes(@DimenRes resId: Int) {
        if (resId > 0) {
            val dimens: Int = resources.getDimension(resId).toInt()
            input.minHeight = dimens
        }
    }

    var hintTextColor: ColorStateList
        get() = input.hintTextColors
        set(value) = input.setHintTextColor(value)

    @Attr(R2.styleable.InputField_android_textColor)
    fun setTextColor(@ColorInt color: Int) {
        input.setTextColor(color)
    }

    @Attr(R2.styleable.InputField_android_textColor)
    fun setTextColorRes(@ColorRes color: Int) {
        if (color == 0) return

        try {
            input.setTextColor(ContextCompat.getColorStateList(context, color))
        } catch (e: Throwable) {
            input.setTextColor(ContextCompat.getColor(context, color))
        }

    }

    @Attr(R2.styleable.InputField_android_textSize)
    fun setTextSize(@Px textSize: Int) {
        input.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())
    }

    fun setTextSize(unit: Int, size: Float) {
        input.setTextSize(unit, size)
    }

    @SuppressLint("ResourceType")
    @Attr(R2.styleable.InputField_android_textAllCaps)
    fun setTextAllCaps(@BoolRes resId: Int) {
        if (resId > 0) {
            input.isAllCaps = resources.getBoolean(resId)
        }
    }

    var textAllCaps: Boolean
        @RequiresApi(Build.VERSION_CODES.P)
        get() = input.isAllCaps
        @Attr(R2.styleable.InputField_android_textAllCaps)
        set(v) {
            input.isAllCaps = v
        }

    var maxLines: Int
        get() = input.maxLines
        @Attr(R2.styleable.InputField_android_maxLines)
        set(v) {
            input.maxLines = v
        }

    @Attr(R2.styleable.InputField_android_singleLine)
    fun setSingleLine(singleLine: Boolean) {
        input.isSingleLine = singleLine
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun isSingleLine(): Boolean {
        return input.isSingleLine
    }

    /**
     * Hack to force disable keyboard suggestions and autocorrection
     */
    @Attr(R2.styleable.InputField_inputDisableSuggestions)
    fun setInputDisableSuggestions(disable: Boolean) {
        if (disable) {
            input.inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        }
    }

    @Attr(R2.styleable.InputField_android_nextFocusDown)
    override fun setNextFocusDownId(@IdRes nextFocusDownId: Int) {
        input.nextFocusDownId = nextFocusDownId
    }

    fun setLabelTextColor(color: Int) {
        mLabelText?.setTextColor(color)
    }

    fun setLabelTextColor(color: ColorStateList) {
        mLabelText?.setTextColor(color)
    }

    fun setLabelTextSize(size: Float) {
        mLabelText?.textSize = size
    }

    fun setLabelTextSize(unit: Int, size: Float) {
        mLabelText?.setTextSize(unit, size)
    }

    fun setErrorTextColor(color: Int) {
        mErrorText?.setTextColor(color)
    }

    fun setErrorTextColor(color: ColorStateList) {
        mErrorText?.setTextColor(color)
    }


    @Attr(R2.styleable.InputField_inputBackground)
    fun setInputBackgroundRes(@DrawableRes res: Int) {
        input.setBackgroundResource(res)
    }

    var inputBackground: Drawable?
        get() = input.background
        set(v) {
            input.background = v
        }

    var imeOptions: Int
        get() = input.imeOptions
        @Attr(R2.styleable.InputField_android_imeOptions)
        set(v) {
            input.imeOptions = v
        }

    var inputType: Int
        get() = input.inputType
        @Attr(R2.styleable.InputField_android_inputType)
        set(v) {
            var oldTypeface: Typeface? = null
            if ((v and EditorInfo.TYPE_TEXT_VARIATION_PASSWORD) == EditorInfo.TYPE_TEXT_VARIATION_PASSWORD) {
                oldTypeface = input.typeface
            }
            input.inputType = v
            input.isSingleLine = (v and EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE) != EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE

            if (oldTypeface != null) {
                input.transformationMethod = PasswordTransformationMethod.getInstance()
                input.typeface = oldTypeface
            }
        }

    @Attr(R2.styleable.InputField_android_digits)
    fun setDigits(digits: String) {
        input.keyListener = DigitsKeyListener.getInstance(digits)
    }

    open var label: CharSequence?
        get() = mLabelText?.text
        @Attr(R2.styleable.InputField_label)
        set(v) {
            mLabelText?.text = v
        }

    open var text: Editable?
        get() = input.text
        set(value) {
            input.text = value
        }

    fun setText(text: CharSequence?, bufferType: TextView.BufferType) {
        input.setText(text, bufferType)
    }

    fun setText(@StringRes resId: Int) {
        input.setText(resId)
    }

    @Attr(R2.styleable.InputField_android_text)
    fun setText(text: CharSequence?) = input.setText(text)

    fun setSelection(index: Int) = input.setSelection(index)
    fun setSelection(start: Int, stop: Int) = input.setSelection(start, stop)

    var hint: CharSequence?
        get() = input.hint
        @Attr(R2.styleable.InputField_android_hint)
        set(v) {
            input.hint = v
        }

    var maxLength: Int
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        get() {
            val filters = input.filters
            for (f in filters) {
                if (f is LengthFilter) {
                    return f.max
                }
            }
            return -1
        }
        @Attr(R2.styleable.InputField_android_maxLength)
        set(v) {
            val filters = arrayOfNulls<InputFilter>(1)
            filters[0] = LengthFilter(v)
            input.filters = filters
        }

    @RequiresApi(Build.VERSION_CODES.O)
    @Attr(R2.styleable.InputField_android_autofillHints)
    fun setAutofillHints(autofillHints: String) {
        input.setAutofillHints(autofillHints)
    }

    var errorEnabled: Boolean
        get() = errorView?.visibility == View.VISIBLE
        @Attr(R2.styleable.InputField_errorEnabled)
        set(enabled) {
            errorView?.visibility = if (enabled) View.VISIBLE else View.GONE
        }

    var labelEnabled: Boolean
        get() = labelView?.visibility == View.VISIBLE
        @Attr(R2.styleable.InputField_labelEnabled)
        set(enabled) {
            labelView?.visibility = if (enabled) View.VISIBLE else View.GONE
        }

    fun setOnEditorActionListener(listener: TextView.OnEditorActionListener?) {
        input.setOnEditorActionListener(listener)
    }


    open val input: EditText
        get() = mInput

    var labelView: TextView?
        get() = mLabelText
        set(v) {
            mLabelText?.visibility = GONE
            mLabelText = v
        }

    var labelExtView: LabelView?
        get() = mLabelExtView
        set(v) {
            mLabelText?.visibility = GONE
            mLabelText = null
            mLabelExtView = v
        }

    var errorExtView: ErrorView?
        get() = mErrorExtView
        set(v) {
            mErrorText?.visibility = View.GONE
            mErrorText = null
            mErrorExtView = v
        }

    var errorView: TextView?
        get() = mErrorText
        set(v) {
            mErrorText?.visibility = GONE
            mErrorText = v
        }

    var filters: Array<InputFilter>
        get() = input.filters
        set(f) {
            input.filters = f
        }

    var error: CharSequence?
        get() = mErrorText?.text
        set(text) {
            mErrorText?.text = text
            if (mAutoVisibleError && !errorEnabled && text != null) {
                errorEnabled = true
            }

            if (mErrorText != null && text != null) {
                scrollToError()
            }
        }

    fun addTextChangedListener(textWatcher: TextWatcher) {
        input.addTextChangedListener(textWatcher)
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

    internal fun View.setMargin(@Px left: Int, @Px top: Int, @Px right: Int, @Px bottom: Int) {
        val lp: MarginLayoutParams = this.layoutParams as MarginLayoutParams
        lp.leftMargin = left
        lp.topMargin = top
        lp.rightMargin = right
        lp.bottomMargin = bottom
        this.layoutParams = lp
    }

    internal fun View.setMargin(@Px margin: Int) {
        setMargin(margin, margin, margin, margin)
    }

    internal fun View.setMarginRes(@DimenRes resId: Int) {
        setMargin(context.resources.getDimension(resId).toInt())
    }

    internal fun View.setPadding(@Px padding: Int) {
        super.setPadding(padding, padding, padding, padding)
    }

    internal fun View.setPaddingRes(@DimenRes resId: Int) {
        setPadding(context.resources.getDimension(resId).toInt())
    }

    internal fun View.setPadding(rect: Rect?) {
        if (rect == null) return
        this.setPadding(rect.left, rect.top, rect.right, rect.bottom)
    }

    internal fun View.getPaddingRect(): Rect {
        return Rect(
            paddingLeft,
            paddingTop,
            paddingRight,
            paddingBottom
        )
    }

    /**
     * Find the closest ancestor of the given type.
     */
    private inline fun <reified T> View.findParentOfType(): T? {
        var p = parent
        while (p != null && p !is T) {
            p = p.parent
        }
        return p as T?
    }

    private val scrollView by lazy(LazyThreadSafetyMode.NONE) {
        findParentOfType<ScrollView>() ?: findParentOfType<NestedScrollView>()
    }

    fun scrollToError() {
        // Wait a bit (like 10 frames) for other UI changes to happen
        scrollView?.postDelayed({
            scrollView!!.scrollDownTo(mErrorText!!)
        }, 160)
    }

    /**
     * Scroll down the minimum needed amount to show [descendant] in full. More
     * precisely, reveal its bottom.
     */
    private fun ViewGroup.scrollDownTo(descendant: View) {
        // Could use smoothScrollBy, but it sometimes over-scrolled a lot
        howFarDownIs(descendant)?.let {
            if (it == 0) return@let
            when (this) {
                is ScrollView -> {
                    this.smoothScrollBy(0, it)
                }
                is NestedScrollView -> {
                    this.smoothScrollBy(0, it)
                }
                else -> {
                    scrollBy(0, it)
                }
            }
        }
    }

    /**
     * Calculate how many pixels below the visible portion of this [ViewGroup] is the
     * bottom of [descendant].
     *
     * In other words, how much you need to scroll down, to make [descendant]'s bottom
     * visible.
     */
    private fun ViewGroup.howFarDownIs(descendant: View): Int? {
        val bottom = Rect().also {
            // See https://stackoverflow.com/a/36740277/1916449
            descendant.getDrawingRect(it)
            offsetDescendantRectToMyCoords(descendant, it)
        }.bottom
        return (bottom - height - scrollY).takeIf { it > 0 }
    }

    /**
     * You can use this interface to define your own error view and handle error while InputField received it
     */
    interface ErrorView {
        fun setError(error: CharSequence?)
        fun getError(): CharSequence?
    }

    /**
     * This interface is intended for custom label text handling
     */
    interface LabelView {
        fun setText(text: CharSequence?)
        fun getText(): CharSequence?
    }
}