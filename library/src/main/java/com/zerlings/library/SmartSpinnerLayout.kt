package com.zerlings.library

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import kotlinx.android.synthetic.main.spinner_menu.view.*

class SmartSpinnerLayout<T: Any> @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr){

    private val menuWidth: Int
    private val menuOffsetX: Int
    private val menuOffsetY: Int
    private val presetIndex: Int
    private val dropDownMenu: PopupWindow
    private val recyclerView: RecyclerView
    private var selectedIndex = -1
    private var adapter: BaseSpinnerLayoutAdapter<T>? = null
    private var onItemSelectedListener: ((View, Int) -> Unit)? = null
    private var onSpinnerResetListener: (() -> Unit)? = null
    private var initialized = false

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SmartSpinnerLayout)
        //setMenuPaddingAndLocation
        menuWidth = typedArray.getLayoutDimension(R.styleable.SmartSpinnerLayout_layoutMenuWidth, -3)
        menuOffsetX = typedArray.getDimensionPixelSize(R.styleable.SmartSpinnerLayout_layoutMenuOffsetX, 0)
        menuOffsetY = typedArray.getDimensionPixelSize(R.styleable.SmartSpinnerLayout_layoutMenuOffsetY, 0)
        //setPreset
        presetIndex = typedArray.getInt(R.styleable.SmartSpinnerLayout_layoutPresetIndex, -1)
        //setPopupMenu
        val popupView = View.inflate(context, R.layout.spinner_menu, null)
        recyclerView = popupView.rcv
        recyclerView.layoutManager = LinearLayoutManager(context)
        dropDownMenu = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            isFocusable = true
            isOutsideTouchable = true
            isTouchable = true
        }

        isClickable = true
        typedArray.recycle()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isEnabled && event.action == MotionEvent.ACTION_UP) {
            if (!initialized){
                resizeDropDownMenu()
                initialized = true
            }
            if (!dropDownMenu.isShowing && adapter!!.itemCount > 0) {
                dropDownMenu.showAsDropDown(this, menuOffsetX, menuOffsetY)
            } else {
                dropDownMenu.dismiss()
            }
        }
        return super.onTouchEvent(event)
    }

    fun setAdapter(baseAdapter: BaseSpinnerLayoutAdapter<T>){
        adapter = baseAdapter
        recyclerView.adapter = adapter
        adapter!!.apply {
            setSelectedPosition(selectedIndex)
            onItemClickListener = {view, position ->
                setSelectedIndex(view, position)
                notifyDataSetChanged()
                dropDownMenu.dismiss()
            }
        }
        setSelectedIndex(this, presetIndex, false)
    }

    fun reset(){
        setSelectedIndex(this, presetIndex, false)
        onSpinnerResetListener?.invoke() ?: if (presetIndex != -1) { onItemSelectedListener?.invoke(this, presetIndex) }
    }

    fun setSelectedIndex(view: View, position: Int, selected: Boolean = true){
        selectedIndex = position
        adapter?.setSelectedPosition(position)
        adapter?.onRefresh(this, position)
        if (selected && position != -1){
            onItemSelectedListener?.invoke(view, position)
        }
    }

    fun setDataSource(dataSource: MutableList<T>){
        adapter?.setData(dataSource)
        setSelectedIndex(this, presetIndex, false)
    }

    private fun resizeDropDownMenu(){
        dropDownMenu.width = if (menuWidth == -3) width else menuWidth
        adapter!!.notifyDataSetChanged()
    }

    fun getItemAtPosition(position: Int): T? {
        return adapter?.getData()?.get(position)
    }

    fun getSelectedItem(): T? {
        return if (selectedIndex == -1) null else adapter?.getData()?.get(selectedIndex)
    }

    fun getSelectedIndex(): Int {
        return selectedIndex
    }

    fun setOnItemSelectedListener(listener: (View, Int) -> Unit){
        onItemSelectedListener = listener
    }

    fun setOnSpinnerResetListener(listener: () -> Unit){
        onSpinnerResetListener = listener
    }
}