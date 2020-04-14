package com.zerlings.library

import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class BaseSpinnerAdapter<T, VH: BaseViewHolder> internal constructor(
    @LayoutRes private val layoutResId: Int,
    protected var dataList: MutableList<T>
    ) : RecyclerView.Adapter<VH>(){

    protected var selectedPosition: Int = -1

    @ColorInt
    protected var selectedColor: Int = 0
    @DrawableRes
    protected var selectedBackground: Int = 0

    private var itemHeight: Int = 1

    internal lateinit var onItemClickListener: (View, Int) -> Unit

    internal fun setData(dataList: MutableList<T>){
        this.dataList = dataList
    }

    internal fun getData(): MutableList<T> = dataList

    internal fun setItemHeight(height: Int){
        itemHeight = height
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        val holder = BaseViewHolder(view)
        return holder as VH
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.itemView.layoutParams.height = itemHeight
        holder.itemView.setOnClickListener {
            onItemClickListener(holder.itemView, position)
        }
    }

    internal fun setSelectedPosition(position: Int){
        selectedPosition = position
    }

    internal fun setSelectedColor(@ColorInt color: Int){
        selectedColor = color
    }

    internal fun setSelectedBackground(@DrawableRes backgroundColor: Int){
        selectedBackground = backgroundColor
    }
}