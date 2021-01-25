package com.zerlings.spinner

import androidx.annotation.LayoutRes

abstract class BaseSpinnerLayoutAdapter<T>(@LayoutRes layoutResId: Int, dataList: MutableList<T>) : BaseSpinnerAdapter<T, BaseViewHolder>(layoutResId, dataList){

    protected var presetPosition: Int = -1

    internal fun setPresetPosition(position: Int){
        presetPosition = position
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        onBind(holder, position)
    }

    protected abstract fun onBind(holder: BaseViewHolder, position: Int)

    abstract fun onRefresh(view: SmartSpinnerLayout<*>, position: Int)

    override fun getItemCount(): Int {
        return dataList.size
    }
}