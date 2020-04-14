package com.zerlings.library

import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.annotation.LayoutRes
import android.util.TypedValue
import kotlinx.android.synthetic.main.spinner_simple_item.view.*

class SmartSpinnerAdapter<T:CharSequence>(
    @LayoutRes layoutResId: Int,
    stringList: MutableList<T>,
    private val startPadding: Int,
    private val endPadding: Int,
    @DrawableRes private val menuBackground: Int,
    @ColorInt private val textColor: Int,
    private val textSize: Float,
    private val gravity: Int
) : BaseSpinnerAdapter<T, BaseViewHolder>(layoutResId, stringList) {

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.itemView.apply {
            tv_simple.setPaddingRelative(startPadding,0,endPadding,0)
            tv_simple.gravity = gravity
            tv_simple.setBackgroundResource(menuBackground)
            tv_simple.setTextColor(textColor)
            tv_simple.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
            tv_simple.text = dataList[position]
            if (position == selectedPosition){
                tv_simple.setBackgroundResource(selectedBackground)
                tv_simple.setTextColor(selectedColor)
            }
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

}