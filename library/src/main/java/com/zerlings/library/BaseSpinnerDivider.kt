package com.zerlings.library

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ItemDecoration
import android.view.View

/**
 * @param context
 * @param color       分割线颜色
 * @param inset       分割线缩进值
 */
class BaseSpinnerDivider @JvmOverloads constructor(context: Context, @ColorInt color: Int, private val inset: Int = 0, dpHeight: Float = 0.5f) : ItemDecoration() {

    private val divider: Drawable = ColorDrawable(color)

    private val dividerHeight = dip2px(context, dpHeight)

    private val paint: Paint = Paint()

    init {
        paint.color = ResourcesCompat.getColor(context.resources, R.color.white, null)
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
    }

    override fun onDraw(c: Canvas, parent: RecyclerView) = drawVertical(c, parent)

    private fun drawVertical(c: Canvas, parent: RecyclerView) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        val childCount = parent.childCount
        for (i in 0 until childCount - 1) {
            val child: View = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top: Int = child.bottom + params.bottomMargin
            val bottom = top + dividerHeight
            divider.setBounds(left + inset, top, right - inset, bottom)
            divider.draw(c)
        }
    }

    //由于Divider也有宽高，每一个Item需要向下或者向右偏移
    override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
        if (itemPosition > 0) {
            outRect.set(0, dividerHeight, 0, 0)
        }
    }

    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }
}
