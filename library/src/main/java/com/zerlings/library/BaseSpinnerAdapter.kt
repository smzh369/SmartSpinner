package com.zerlings.library

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class BaseSpinnerAdapter<T, VH: BaseViewHolder> internal constructor(
    @LayoutRes private val layoutResId: Int,
    protected var dataList: MutableList<T>,
    protected var selectedPosition: Int,
    protected val selectedColor: Int
    ) : RecyclerView.Adapter<VH>(){

    private lateinit var onItemClickListener: (BaseSpinnerAdapter<T, VH>, View, Int, T) -> Unit

    protected open fun setOnItemClick(view: View, position: Int, item: T) = onItemClickListener(this, view, position, item)

    fun setOnItemClickListener(listener: (BaseSpinnerAdapter<T, VH>, View, Int, T) -> Unit) {
        onItemClickListener = listener
    }

    fun setData(dataList: MutableList<T>){
        this.dataList = dataList
        notifyDataSetChanged()
    }

    @Suppress("UNCHECKED_CAST")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        val holder = BaseViewHolder(view)
        return holder as VH
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.itemView.setOnClickListener { setOnItemClick(holder.itemView, position, dataList[position]) }
    }
}