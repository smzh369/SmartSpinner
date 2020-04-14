package com.zerlings.library

import android.support.annotation.LayoutRes

abstract class BaseSpinnerLayoutAdapter<T>(@LayoutRes layoutResId: Int, dataList: MutableList<T>) : BaseSpinnerAdapter<T, BaseViewHolder>(layoutResId, dataList){

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        onBind(holder, position)
    }

    protected abstract fun onBind(holder: BaseViewHolder, position: Int)

    abstract fun onRefresh(view: SmartSpinnerLayout<T>, position: Int)

    override fun getItemCount(): Int {
        return dataList.size
    }
}