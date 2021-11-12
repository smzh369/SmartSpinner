package com.zerlings.spinner

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.annotation.DrawableRes
import kotlinx.android.synthetic.main.spinner_menu.view.*

class SmartSpinnerLayout<T: Any> @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ConstraintLayout(context, attrs, defStyleAttr){

    private val menuWidth: Int
    private val menuHeight: Int
    private val menuOffsetX: Int
    private val menuOffsetY: Int
    private val menuRadius: Float
    private val menuElevation: Float
    private val presetIndex: Int
    @DrawableRes
    private val headBackground: Int
    @DrawableRes
    private val menuBackground: Int
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
        menuWidth = typedArray.getLayoutDimension(R.styleable.SmartSpinnerLayout_menuWidth, -3)
        menuHeight = typedArray.getLayoutDimension(R.styleable.SmartSpinnerLayout_menuHeight, -1)
        menuOffsetX = typedArray.getDimensionPixelSize(R.styleable.SmartSpinnerLayout_menuOffsetX, 0)
        menuOffsetY = typedArray.getDimensionPixelSize(R.styleable.SmartSpinnerLayout_menuOffsetY, 0)
        //setBackground
        headBackground = typedArray.getResourceId(R.styleable.SmartSpinnerLayout_spinnerBackground, R.color.light_gray)
        setBackgroundResource(headBackground)
        menuBackground = typedArray.getResourceId(R.styleable.SmartSpinnerLayout_menuBackground, headBackground)
        //setPreset
        presetIndex = typedArray.getInt(R.styleable.SmartSpinnerLayout_presetIndex, -1)
        //setPopupMenu
        menuRadius = typedArray.getDimension(R.styleable.SmartSpinnerLayout_menuRadius, 0f)
        menuElevation = typedArray.getDimension(R.styleable.SmartSpinnerLayout_menuElevation, 0f)
        val popupView = View.inflate(context, R.layout.spinner_menu, null)
        popupView.cv.radius = menuRadius
        recyclerView = popupView.rcv
        recyclerView.setBackgroundResource(menuBackground)
        recyclerView.layoutManager = LinearLayoutManager(context)
        if (typedArray.getBoolean(R.styleable.SmartSpinnerLayout_showItemDivider, false)){
            val dividerColor = typedArray.getColor(R.styleable.SmartSpinnerLayout_dividerColor, Color.LTGRAY)
            val dividerPadding = typedArray.getDimensionPixelSize(R.styleable.SmartSpinnerLayout_dividerPadding, 0)
            val dividerHeight = typedArray.getDimensionPixelSize(R.styleable.SmartSpinnerLayout_dividerHeight, dip2px(context, 1f))
            recyclerView.addItemDecoration(BaseSpinnerDivider(context, dividerColor, dividerPadding, dividerHeight))
        }
        dropDownMenu = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            isFocusable = true
            isOutsideTouchable = true
            isTouchable = true
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dropDownMenu.elevation = menuElevation
        }
        isClickable = true
        typedArray.recycle()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isEnabled && event.action == MotionEvent.ACTION_UP && adapter != null) {
            if (!initialized){
                resizeDropDownMenu()
                initialized = true
            }
            if (!dropDownMenu.isShowing && adapter!!.itemCount > 0) {
                adapter!!.notifyDataSetChanged()
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
        if (position >= adapter!!.itemCount) return
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
        if (height*(adapter?.getData()?.size ?: 0) > menuHeight && menuHeight != -1) dropDownMenu.height = menuHeight
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

    private fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }
}