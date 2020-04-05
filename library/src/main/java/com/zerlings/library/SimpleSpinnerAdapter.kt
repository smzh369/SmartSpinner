package com.zerlings.library

import android.support.annotation.ColorInt
import android.support.annotation.LayoutRes
import android.util.TypedValue
import kotlinx.android.synthetic.main.item_simple_spinner.view.*

class SimpleSpinnerAdapter(
    @LayoutRes layoutResId: Int,
    stringList: MutableList<CharSequence>,
    selectedPosition: Int,
    @ColorInt selectedColor: Int,
    private val itemHeight: Int,
    private val startPadding: Int,
    private val endPadding: Int,
    @ColorInt private val textColor: Int,
    private val textSize: Float,
    private val gravity: Int
) : BaseSpinnerAdapter<CharSequence, BaseViewHolder>(layoutResId, stringList, selectedPosition, selectedColor) {

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.itemView.apply {
            layoutParams.height = itemHeight
            ll_simple.setPaddingRelative(startPadding,0,endPadding,0)
            ll_simple.gravity = gravity
            tv_simple.setTextColor(textColor)
            tv_simple.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
            tv_simple.setTextColor(selectedColor)
            tv_simple.text = dataList[position]
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

}