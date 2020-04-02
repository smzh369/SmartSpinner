package com.zerlings.library

import android.support.annotation.ColorInt
import android.support.annotation.LayoutRes
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.item_simple_spinner.view.*

class SimpleSpinnerAdapter(
    @LayoutRes layoutResId: Int,
    stringList: MutableList<String>,
    selectedPosition: Int,
    @ColorInt selectedColor: Int,
    private val itemWidth: Int,
    private val itemHeight: Int,
    private val startPadding: Int,
    @ColorInt private val textColor: Int,
    private val textSize: Float,
    private val gravity: Int
) : BaseSpinnerAdapter<String, BaseViewHolder>(layoutResId, stringList, selectedPosition, selectedColor) {

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.itemView.apply {
            val params = layoutParams
            params.width = itemWidth
            params.height = itemHeight
            layoutParams = params
            ll_simple.setPaddingRelative(startPadding,0,0,0)
            ll_simple.gravity = gravity
            tv_simple.setTextColor(textColor)
            tv_simple.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
            tv_simple.setTextColor(selectedColor)}
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

}