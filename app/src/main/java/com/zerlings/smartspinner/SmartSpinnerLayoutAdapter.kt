package com.zerlings.smartspinner

import android.graphics.Color
import android.support.annotation.LayoutRes
import android.support.v4.content.res.ResourcesCompat
import android.view.View
import com.zerlings.library.BaseSpinnerLayoutAdapter
import com.zerlings.library.BaseViewHolder
import com.zerlings.library.SmartSpinnerLayout
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.item_spinner_layout.view.*

class SmartSpinnerLayoutAdapter @JvmOverloads constructor(dataList: MutableList<Fruit>, @LayoutRes layoutResId: Int = R.layout.item_spinner_layout) : BaseSpinnerLayoutAdapter<Fruit>(layoutResId, dataList) {

    override fun onBind(holder: BaseViewHolder, position: Int) {
        holder.itemView.apply {
            item_text.text = dataList[position].title
            item_icon.setImageResource(dataList[position].imgResId)
            if (position == selectedPosition){
                item_hook.visibility = View.VISIBLE
                item_text.setTextColor(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))
                setBackgroundResource(R.color.colorAccent)
            }else {
                item_hook.visibility = View.GONE
                item_text.setTextColor(Color.BLACK)
                setBackgroundResource(R.color.light_gray)
            }
        }
    }

    override fun onRefresh(view: SmartSpinnerLayout<*>, position: Int) {
        view.spinner_text.text = if (position == -1) "fruit" else dataList[position].title
        view.spinner_text.setTextColor(if (position == presetPosition) Color.BLACK else ResourcesCompat.getColor(view.resources, R.color.colorPrimary, null))
    }
}