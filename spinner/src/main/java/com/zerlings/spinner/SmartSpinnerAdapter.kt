package com.zerlings.spinner

import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import android.util.TypedValue
import kotlinx.android.synthetic.main.spinner_simple_item.view.*

class SmartSpinnerAdapter<T:CharSequence>(
    @LayoutRes layoutResId: Int,
    stringList: MutableList<T>,
    private val startPadding: Int,
    private val endPadding: Int,
    @ColorInt private val textColor: Int,
    @ColorInt private var selectedColor: Int = 0,
    @DrawableRes private val itemBackground: Int,
    @DrawableRes private var selectedBackground: Int = 0,
    private val textSize: Float,
    private val gravity: Int
) : BaseSpinnerAdapter<T, BaseViewHolder>(layoutResId, stringList) {

    private var itemHeight: Int = 1

    internal fun setItemHeight(height: Int){
        itemHeight = height
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.itemView.apply {
            layoutParams.height = itemHeight
            tv_simple.setPaddingRelative(startPadding,0,endPadding,0)
            tv_simple.gravity = gravity
            tv_simple.setBackgroundResource(itemBackground)
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