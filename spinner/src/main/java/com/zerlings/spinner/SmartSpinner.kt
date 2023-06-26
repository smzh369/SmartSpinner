package com.zerlings.spinner

import android.R.attr
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.cardview.widget.CardView
import com.zerlings.smartspinner.R

class SmartSpinner @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = attr.textViewStyle) : AppCompatTextView(context, attrs, defStyleAttr) {

    private val menuTextGravity: Int
    private val menuPaddingStart: Int
    private val menuPaddingEnd: Int
    private val menuWidth: Int
    private val menuHeight: Int
    private val itemHeight: Int
    private val menuOffsetX: Int
    private val menuOffsetY: Int
    private val menuRadius: Float
    private val menuElevation: Float
    private val presetIndex: Int
    private val presetText: String?
    private val spinnerTextSize: Float
    private val arrowDrawable: Drawable
    private val isArrowHidden: Boolean
    private val showSelectedColor: Boolean
    @ColorInt
    private val textTint: Int
    @ColorInt
    private val menuTextTint: Int
    @ColorInt
    private val selectedTint: Int
    @DrawableRes
    private val headBackground: Int
    @DrawableRes
    private val menuBackground: Int
    @DrawableRes
    private val itemBackground: Int
    @DrawableRes
    private val selectedBackground: Int
    @ColorInt
    private val arrowTint: Int
    @DrawableRes
    private val arrowResId: Int
    private val dropDownMenu: PopupWindow
    private val recyclerView: RecyclerView
    private val entries: Array<CharSequence>?
    private var selectedIndex = -1
    private var adapter: SmartSpinnerAdapter<CharSequence>? = null
    private var onItemSelectedListener: ((View, Int) -> Unit)? = null
    private var onSpinnerResetListener: (() -> Unit)? = null
    private var initialized = false

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SmartSpinner)
        //setMenuPaddingAndLocation
        menuPaddingStart = typedArray.getDimensionPixelSize(R.styleable.SmartSpinner_menuPaddingStart, 0)
        menuPaddingEnd = typedArray.getDimensionPixelSize(R.styleable.SmartSpinner_menuPaddingEnd, 0)
        menuWidth = typedArray.getLayoutDimension(R.styleable.SmartSpinner_menuWidth, -3)
        menuHeight = typedArray.getLayoutDimension(R.styleable.SmartSpinner_menuHeight, -1)
        menuOffsetX = typedArray.getDimensionPixelSize(R.styleable.SmartSpinner_menuOffsetX, 0)
        menuOffsetY = typedArray.getDimensionPixelSize(R.styleable.SmartSpinner_menuOffsetY, 0)
        //setTextStyle
        spinnerTextSize = typedArray.getDimension(R.styleable.SmartSpinner_textSize, resources.getDimension(R.dimen.default_text_size))
        setTextSize(TypedValue.COMPLEX_UNIT_PX, spinnerTextSize)
        textTint = typedArray.getColor(R.styleable.SmartSpinner_textColor, Color.BLACK)
        menuTextTint = typedArray.getColor(R.styleable.SmartSpinner_menuTextColor, textTint)
        selectedTint = typedArray.getColor(R.styleable.SmartSpinner_selectedColor, Color.CYAN)
        setTextColor(textTint)
        gravity = typedArray.getInt(R.styleable.SmartSpinner_textAlignment, Gravity.START) or Gravity.CENTER_VERTICAL
        menuTextGravity = typedArray.getInt(R.styleable.SmartSpinner_menuTextAlignment, gravity) or Gravity.CENTER_VERTICAL
        //setBackground
        headBackground = typedArray.getResourceId(R.styleable.SmartSpinner_spinnerBackground, R.color.light_gray)
        setBackgroundResource(headBackground)
        menuBackground = typedArray.getResourceId(R.styleable.SmartSpinner_menuBackground, headBackground)
        itemBackground = typedArray.getResourceId(R.styleable.SmartSpinner_optionBackground, R.color.transparent)
        selectedBackground = typedArray.getResourceId(R.styleable.SmartSpinner_selectedBackground, menuBackground)
        itemHeight = typedArray.getDimensionPixelSize(R.styleable.SmartSpinner_optionHeight, 0)
        //setPreset
        presetText = typedArray.getString(R.styleable.SmartSpinner_presetText)
        presetIndex = if (presetText != null) -1 else typedArray.getInt(R.styleable.SmartSpinner_presetIndex, 0)
        entries = typedArray.getTextArray(R.styleable.SmartSpinner_entries) ?: context.resources.getTextArray(R.array.spinner_default)
        text = presetText ?: entries[0]
        showSelectedColor = typedArray.getBoolean(R.styleable.SmartSpinner_showSelectedColor, false)
        //setArrow
        isArrowHidden = typedArray.getBoolean(R.styleable.SmartSpinner_hideArrow, false)
        arrowTint = typedArray.getColor(R.styleable.SmartSpinner_arrowTint, Color.BLACK)
        arrowResId = typedArray.getResourceId(R.styleable.SmartSpinner_arrowDrawable, R.drawable.arrow)
        arrowDrawable = ContextCompat.getDrawable(getContext(), arrowResId)!!
        DrawableCompat.setTint(arrowDrawable, arrowTint)
        setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, if (!isArrowHidden) arrowDrawable else null, null)
        compoundDrawablePadding = typedArray.getDimensionPixelSize(R.styleable.SmartSpinner_arrowPadding, 100)
        //setPopupMenu
        menuRadius = typedArray.getDimension(R.styleable.SmartSpinner_menuRadius, 0f)
        menuElevation = typedArray.getDimension(R.styleable.SmartSpinner_menuElevation, 0f)
        val popupView = View.inflate(context, R.layout.spinner_menu, null)
        popupView.findViewById<CardView>(R.id.cv).radius = menuRadius
        recyclerView = popupView.findViewById(R.id.rcv)
        recyclerView.setBackgroundResource(menuBackground)
        recyclerView.layoutManager = LinearLayoutManager(context)
        if (typedArray.getBoolean(R.styleable.SmartSpinner_showItemDivider, false)){
            val dividerColor = typedArray.getColor(R.styleable.SmartSpinner_dividerColor, Color.LTGRAY)
            val dividerPadding = typedArray.getDimensionPixelSize(R.styleable.SmartSpinner_dividerPadding, 0)
            val dividerHeight = typedArray.getDimensionPixelSize(R.styleable.SmartSpinner_dividerHeight, dip2px(context, 1f))
            recyclerView.addItemDecoration(BaseSpinnerDivider(context, dividerColor, dividerPadding, dividerHeight))
        }
        setAdapter(SmartSpinnerAdapter(R.layout.spinner_simple_item, entries.toMutableList(), menuPaddingStart, menuPaddingEnd,  menuTextTint, selectedTint, itemBackground, selectedBackground, textSize, menuTextGravity))
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
        if (isEnabled && event.action == MotionEvent.ACTION_UP) {
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

    private fun setAdapter(baseAdapter: SmartSpinnerAdapter<CharSequence>){
        adapter = baseAdapter
        recyclerView.adapter = adapter
        adapter!!.apply {
            setSelectedPosition(selectedIndex)
            onItemClickListener = {view, position ->
                setSelectedIndex(view, position)
                dropDownMenu.dismiss()
            }
        }
        reset()
    }

    fun reset(){
        setSelectedIndex(this, presetIndex, false)
        if (onSpinnerResetListener != null)  {
            onSpinnerResetListener!!.invoke()
        } else if (presetIndex != -1) {
            onItemSelectedListener?.invoke(this, presetIndex)
        }
    }

    fun setSelectedIndex(view: View, position: Int, selected: Boolean = true){
        if (position >= adapter!!.itemCount) return
        selectedIndex = position
        adapter?.setSelectedPosition(position)
        text = if (position == -1) presetText else adapter?.getData()?.get(position)
        setTextColor(if (position != presetIndex && showSelectedColor) selectedTint else textTint)
        if (selected && position != -1){
            onItemSelectedListener?.invoke(view, position)
        }
    }

    fun setDataSource(dataSource: MutableList<CharSequence>){
        adapter?.setData(dataSource)
        setSelectedIndex(this, presetIndex, false)
    }

    private fun resizeDropDownMenu(){
        dropDownMenu.width = if (menuWidth == -3) width else menuWidth
        if (height*(adapter?.getData()?.size ?: 0) > menuHeight && menuHeight != -1) dropDownMenu.height = menuHeight
        (adapter as SmartSpinnerAdapter).apply {
            setItemHeight(if (itemHeight != 0) itemHeight else height)
        }
    }

    fun getItemAtPosition(position: Int): CharSequence? {
        return adapter?.getData()?.get(position)
    }

    fun getSelectedItem(): CharSequence? {
        return if (text == presetText) null else text
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