/*
 * Copyright (C) by Eduard Maximovich. 2023
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

import android.content.Context
import android.graphics.PorterDuffColorFilter
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.edwardstock.inputfield.menu.InputMenuItem

/**
 * Advanced InputField. 2023
 * @author Eduard Maximovich (edward.vstock@gmail.com)
 */
class SuffixMenuContainer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    val roomSpace = 3

    private val moreIconViewId: Int by lazy { ViewCompat.generateViewId() }
    private var itemStyle: Int = R.style.InputFieldSuffixMenuItemStyle
    private var inputGetter: (() -> View)? = null
    private var moreIcon: Int = R.drawable.aif_ic_more

    private val input: View get() = inputGetter?.invoke() ?: throw IllegalStateException("Input view is null")
    private var overflowPopupMenu: PopupMenu? = null
    private var globalMenuItemClickListener: OnSuffixMenuItemClickListener? = null

    internal var inputSourcePadding: Rect? = null
    internal var inputTargetPadding: Rect? = null

    private var lastMainState: MutableMap<Int, InputMenuItem> = mutableMapOf()
    private var lastPopupState: MutableMap<Int, InputMenuItem> = mutableMapOf()

    val items: List<InputMenuItem> get() = (lastMainState.values.toList() + lastPopupState.values.toList())

    private var addedToMainCount = 0

    internal fun setInputGetter(inputGetter: () -> View) {
        this.inputGetter = inputGetter
    }

    private val stubPopup: PopupMenu by lazy(LazyThreadSafetyMode.NONE) {
        PopupMenu(context, null)
    }

    private fun getSuffixMenu(): Menu {
        return stubPopup.menu
    }

    internal fun reset() {
        addedToMainCount = 0
        removeAllViews()
        getSuffixMenu().clear()
        overflowPopupMenu?.menu?.clear()
    }

    fun setItemClickListener(onMenuItemClickListener: OnSuffixMenuItemClickListener) {
        globalMenuItemClickListener = onMenuItemClickListener
    }

    fun setSuffixMenu(@MenuRes menuRes: Int) {
        setSuffixMenu(menuRes, {}, { _, _ -> })
    }

    fun setSuffixMenu(@MenuRes menuRes: Int, onMenuItemClickListener: OnSuffixMenuItemClickListener) {
        setSuffixMenu(menuRes, {}, onMenuItemClickListener)
    }

    fun setSuffixMenu(menuRes: Int, onCreateMenu: (Menu) -> Unit, onMenuItemClickListener: OnSuffixMenuItemClickListener) {
        val suffixMenu = getSuffixMenu()
        val menuInflater = MenuInflater(context)
        menuInflater.inflate(menuRes, suffixMenu)
        onCreateMenu(suffixMenu)

        val menuItems = mutableListOf<InputMenuItem>()
        for (i in 0 until suffixMenu.size()) {
            val item = suffixMenu.getItem(i)
            val menuItem = InputMenuItem(
                itemId = item.itemId,
                title = item.title,
                icon = item.icon,
                isEnabled = item.isEnabled,
                isVisible = item.isVisible,
                onMenuItemClickListener = onMenuItemClickListener
            )
            menuItems.add(menuItem)
        }
        setSuffixMenu(menuItems)
    }

    private fun renderMainItems(menuItems: List<InputMenuItem>) {
        for (item in menuItems) {
            // save state
            lastMainState[item.itemId] = item

            val imageView = createImageIconView()
            renderMainItem(imageView, item)

            addView(imageView)
            addedToMainCount++
        }
    }

    private fun renderMainItem(imageView: ImageView, item: InputMenuItem) {
        imageView.id = item.itemId.takeIf { it != View.NO_ID } ?: ViewCompat.generateViewId()
        imageView.isEnabled = item.isEnabled
        imageView.contentDescription = when {
            item.titleRes != 0 -> resources.getString(item.titleRes)
            else -> item.title
        }
        imageView.setOnClickListener {
            globalMenuItemClickListener?.invoke(this, item)
            item.onMenuItemClickListener?.invoke(this, item)
        }

        imageView.setImageDrawable(item.getIconDrawable(context, true))

        // apply default attributes from style, also tint globally
        imageView.applyAttributes(itemStyle, !item.hasTintColor())
    }

    private fun renderPopupItems(popupMenuItems: List<InputMenuItem>) {
        if (popupMenuItems.isEmpty()) return

        val showMoreImageView = createImageIconView()
        // more icon could be tinted only by style
        val iconDrawable = if (moreIcon != 0) ContextCompat.getDrawable(context, moreIcon) else null
        showMoreImageView.setImageDrawable(iconDrawable)
        showMoreImageView.applyAttributes(itemStyle)
        showMoreImageView.id = moreIconViewId

        // todo: make custom popup window
        overflowPopupMenu = PopupMenu(context, showMoreImageView).apply {
            popupMenuItems.forEach { menuItem ->
                // save state
                lastPopupState[menuItem.itemId] = menuItem

                val title = if (menuItem.titleRes != 0) {
                    resources.getString(menuItem.titleRes)
                } else {
                    menuItem.title
                }
                menu.add(
                    menuItem.groupId,
                    menuItem.itemId,
                    menuItem.order,
                    title
                )?.apply {
                    icon = menuItem.getIconDrawable(context)
                    isEnabled = menuItem.isEnabled
                    isVisible = menuItem.isVisible
                    setOnMenuItemClickListener {
                        globalMenuItemClickListener?.invoke(this@SuffixMenuContainer, menuItem)
                        menuItem.onMenuItemClickListener?.invoke(this@SuffixMenuContainer, menuItem)
                        true
                    }
                }
            }
        }
        showMoreImageView.setOnClickListener { overflowPopupMenu?.show() }
        addView(showMoreImageView)

    }

    fun setSuffixMenu(menuItems: List<InputMenuItem>) {
        // cleanup previous menu
        reset()

        if (inputSourcePadding == null) {
            inputSourcePadding = input.getPaddingRect()
        }
        inputTargetPadding = input.getPaddingRect()

        val mainItems = mutableListOf<InputMenuItem>()
        val popupItems = mutableListOf<InputMenuItem>()

        // `menuItems` is the original list of menu items
        val visibleItems = menuItems.filter { it.isVisible }

        visibleItems.forEach {
            when (it.showAsAction) {
                MenuItem.SHOW_AS_ACTION_ALWAYS -> {
                    mainItems.add(it)
                }

                MenuItem.SHOW_AS_ACTION_IF_ROOM -> {
                    if (mainItems.size < roomSpace) {
                        mainItems.add(it)
                    } else {
                        popupItems.add(it)
                    }
                }

                else -> popupItems.add(it)
            }
        }

        renderMainItems(mainItems)
        renderPopupItems(popupItems)

        if (isInEditMode) {
            inputTargetPadding = Rect(inputSourcePadding).apply { right = maxOf(width, measuredWidth) }
            input.setPaddingRect(inputTargetPadding)
        } else {
            if (menuItems.isNotEmpty()) {
                onMeasured { view ->
                    check(inputSourcePadding != null) { "inputSourcePadding is null" }
                    inputTargetPadding = Rect(inputSourcePadding).apply {
                        right = view.width
                    }
                    input.setPaddingRect(inputTargetPadding)
                }
            } else {
                input.setPaddingRect(inputSourcePadding)
            }
        }
    }


    private fun createImageIconView(): ImageView {
        val viewLayoutParams = LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER_VERTICAL
        }

        val ctx = ContextThemeWrapper(context, itemStyle)
        return ImageView(ctx).apply {
            layoutParams = viewLayoutParams
        }
    }

    fun setMoreIcon(drawableRes: Int) {
        moreIcon = drawableRes
    }

    fun setItemStyle(styleRes: Int) {
        itemStyle = styleRes
    }

    fun notifyItemChanged(item: InputMenuItem) {
        if (lastMainState.containsKey(item.itemId) || lastPopupState.containsKey(item.itemId)) {
            if (!item.isVisible) {
                lastMainState.remove(item.itemId)
                lastPopupState.remove(item.itemId)
                removeView(findViewById(item.itemId))
                addedToMainCount--
                if (lastPopupState.isNotEmpty() && addedToMainCount < roomSpace) {
                    val lastPopupItem = lastPopupState.values.last()
                    lastPopupState.remove(lastPopupItem.itemId)
                    renderMainItem(findViewById(lastPopupItem.itemId), lastPopupItem)
                    if (lastPopupState.isEmpty()) {
                        overflowPopupMenu?.dismiss()
                        removeView(findViewById(moreIconViewId))
                    }
                }
            } else {
                lastMainState[item.itemId] = item
                setSuffixMenu((lastMainState + lastPopupState).values.toList())
            }
        } else {
            // item not found, add it to lastPopupState if there's space
            if (addedToMainCount < roomSpace) {
                lastPopupState[item.itemId] = item
                renderMainItem(findViewById(item.itemId), item)
                addedToMainCount++
            }
        }
    }

    private fun ImageView.applyAttributes(suffixMenuItemStyle: Int, applyTint: Boolean = true) {
        AndroidAttributeResolver(context, suffixMenuItemStyle).use { attrs ->
            layoutParams = LayoutParams(
                attrs.getWidth(),
                attrs.getHeight()
            ).apply {
                gravity = attrs.getGravity(Gravity.CENTER_VERTICAL)
            }

            if (applyTint && attrs.hasTintColor() && drawable != null) {
                drawable.colorFilter = PorterDuffColorFilter(attrs.getTintColor(), attrs.getTintMode())
            }

            val paddingRect = Rect(
                paddingLeft,
                paddingTop,
                paddingRight,
                paddingBottom,
            )
            if (attrs.hasPadding()) {
                paddingRect.left = attrs.getPadding()
                paddingRect.top = attrs.getPadding()
                paddingRect.right = attrs.getPadding()
                paddingRect.bottom = attrs.getPadding()
            }
            if (attrs.hasPaddingSided()) {
                attrs.getPaddingSided(paddingRect)
            }
            setPaddingRect(paddingRect)

            if (attrs.hasMargin()) {
                val lp = layoutParams as LayoutParams
                lp.marginStart = attrs.getMargin()
                lp.marginEnd = attrs.getMargin()
                lp.topMargin = attrs.getMargin()
                lp.bottomMargin = attrs.getMargin()
                layoutParams = lp
            }
            if (attrs.hasMarginSided()) {
                val lp = layoutParams as LayoutParams
                val marginRect = Rect(
                    lp.marginStart,
                    lp.topMargin,
                    lp.marginEnd,
                    lp.bottomMargin,
                )
                attrs.getMarginSided(marginRect)
                lp.marginStart = marginRect.left
                lp.marginEnd = marginRect.right
                lp.topMargin = marginRect.top
                lp.bottomMargin = marginRect.bottom
                layoutParams = lp
            }

            isClickable = attrs.getClickable()
            isFocusable = attrs.getFocusable()

            if (attrs.hasBackground()) {
                background = attrs.getBackground()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                foreground = if (attrs.hasForeground()) {
                    attrs.getForeground()
                } else {
                    ContextCompat.getDrawable(context, R.drawable.aif_ripple_suffix)
                }
            }
        }
    }
}