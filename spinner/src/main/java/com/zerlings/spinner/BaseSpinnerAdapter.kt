package com.zerlings.spinner

import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class BaseSpinnerAdapter<T, VH: BaseViewHolder> internal constructor(
    @LayoutRes private val layoutResId: Int,
    protected var dataList: MutableList<T>
    ) : RecyclerView.Adapter<VH>(){

    protected var selectedPosition: Int = -1

    internal lateinit var onItemClickListener: (View, Int) -> Unit

    internal fun setData(dataList: MutableList<T>){
        this.dataList = dataList
    }

    internal fun getData(): MutableList<T> = dataList

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        val holder = BaseViewHolder(view)
        return holder as VH
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.itemView.setOnClickListener {
            onItemClickListener(holder.itemView, position)
        }
    }

    internal fun setSelectedPosition(position: Int){
        selectedPosition = position
    }
}