package com.edwardstock.inputfield

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ScrollView
import androidx.annotation.DimenRes
import androidx.annotation.Px
import androidx.core.widget.NestedScrollView

internal fun View.onMeasured(block: (View) -> Unit) {
    if (measuredWidth > 0 && measuredHeight > 0) {
        block(this)
    } else {
        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                block(this@onMeasured)
                viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }
}

internal fun View.setMargin(@Px left: Int, @Px top: Int, @Px right: Int, @Px bottom: Int) {
    val lp: ViewGroup.MarginLayoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
    lp.leftMargin = left
    lp.topMargin = top
    lp.rightMargin = right
    lp.bottomMargin = bottom
    this.layoutParams = lp
}

internal fun View.setMarginPx(@Px margin: Int) {
    setMargin(margin, margin, margin, margin)
}

internal fun View.setMarginRes(@DimenRes resId: Int) {
    setMarginPx(context.resources.getDimension(resId).toInt())
}

internal fun View.setPaddingPx(@Px padding: Int) {
    setPadding(padding, padding, padding, padding)
}

internal fun View.setPaddingDp(padding: Float) {
    setPaddingPx((padding / context.resources.displayMetrics.density).toInt())
}

internal fun View.setPaddingRes(@DimenRes resId: Int) {
    setPaddingPx(context.resources.getDimension(resId).toInt())
}

internal fun View.setPaddingRect(rect: Rect?) {
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
internal inline fun <reified T> View.findParentOfType(): T? {
    var p = parent
    while (p != null && p !is T) {
        p = p.parent
    }
    return p as T?
}

/**
 * Scroll down the minimum needed amount to show [descendant] in full. More
 * precisely, reveal its bottom.
 */
internal fun ViewGroup.scrollDownTo(descendant: View) {
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
internal fun ViewGroup.howFarDownIs(descendant: View): Int? {
    val bottom = Rect().also {
        // See https://stackoverflow.com/a/36740277/1916449
        descendant.getDrawingRect(it)
        offsetDescendantRectToMyCoords(descendant, it)
    }.bottom
    return (bottom - height - scrollY).takeIf { it > 0 }
}