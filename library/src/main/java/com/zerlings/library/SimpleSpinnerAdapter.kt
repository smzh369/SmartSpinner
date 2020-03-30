package com.zerlings.library

import android.support.annotation.ColorInt
import android.support.annotation.LayoutRes
import android.util.TypedValue
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_simple_spinner.view.*

class SimpleSpinnerAdapter(
    @LayoutRes layoutResId: Int,
    stringList: ArrayList<String>,
    selectedPosition: Int,
    @ColorInt selectedColor: Int,
    private val leftPadding: Int,
    private val topPadding: Int,
    private val rightPadding: Int,
    private val bottomPadding: Int,
    @ColorInt private val textColor: Int,
    private val textSize: Float,
    private val gravity: Int
) : BaseSpinnerAdapter<String, BaseViewHolder>(layoutResId, stringList, selectedPosition, selectedColor) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val viewHolder = super.onCreateViewHolder(parent, viewType)
        viewHolder.itemView.apply {
            ll_simple.setPadding(leftPadding, topPadding, rightPadding, bottomPadding)
            ll_simple.gravity = gravity
            tv_simple.setTextColor(textColor)
            tv_simple.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.itemView.tv_simple.setTextColor(selectedColor)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

}