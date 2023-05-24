package com.zerlings.smartspinner

import android.graphics.Color
import androidx.annotation.LayoutRes
import androidx.core.content.res.ResourcesCompat
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.zerlings.spinner.BaseSpinnerLayoutAdapter
import com.zerlings.spinner.BaseViewHolder
import com.zerlings.spinner.SmartSpinnerLayout

class SmartSpinnerLayoutAdapter @JvmOverloads constructor(dataList: MutableList<PayType>, @LayoutRes layoutResId: Int = R.layout.item_spinner_layout) : BaseSpinnerLayoutAdapter<PayType>(layoutResId, dataList) {

    override fun onBind(holder: BaseViewHolder, position: Int) {
        holder.itemView.apply {
            val item_text = findViewById<TextView>(R.id.item_text)
            val item_icon = findViewById<ImageView>(R.id.item_icon)
            val item_hook = findViewById<ImageView>(R.id.item_hook)
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
        val spinner_text = view.findViewById<TextView>(R.id.spinner_text)
        spinner_text.text = if (position == -1) "paytype" else dataList[position].title
        spinner_text.setTextColor(if (position == presetPosition) Color.BLACK else ResourcesCompat.getColor(view.resources, R.color.colorPrimary, null))
    }
}