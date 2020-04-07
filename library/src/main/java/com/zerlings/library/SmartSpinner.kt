package com.zerlings.library

import android.R.attr
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import kotlinx.android.synthetic.main.spinner_menu.view.*

class SmartSpinner @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = attr.textViewStyle) : AppCompatTextView(context, attrs, defStyleAttr) {

    companion object {
        private const val MAX_LEVEL = 10000
        private const val VERTICAL_OFFSET = 1
        private const val INSTANCE_STATE = "instance_state"
        private const val SELECTED_INDEX = "selected_index"
        private const val IS_POPUP_SHOWING = "is_popup_showing"
        private const val IS_ARROW_HIDDEN = "is_arrow_hidden"
        private const val ARROW_DRAWABLE_RES_ID = "arrow_drawable_res_id"
    }
    private val menuPaddingStart: Int
    private val menuPaddingEnd: Int
    private val menuWidth: Int
    private val menuOffsetX: Int
    private val menuOffsetY: Int
    private val presetIndex: Int
    private var selectedIndex = -1
    private var arrowDrawable: Drawable? = null
    private var isArrowHidden = false
    @ColorInt
    private val textTint: Int
    @ColorInt
    private val selectedTint: Int
    @DrawableRes
    private val headBackground: Int
    @DrawableRes
    private val menuBackground: Int
    @DrawableRes
    private val selectedBackground: Int
    private val spinnerTextSize: Float
    private val presetText: String?
    //private var backgroundSelector = 0
    private val arrowDrawableTint: Int
    //private var displayHeight = 0
    //private var parentVerticalOffset = 0
    //private var dropDownListPaddingBottom = 0
    @DrawableRes
    private val arrowDrawableResId: Int
    private val dropDownMenu: PopupWindow
    private val recyclerView: RecyclerView
    private val entries: Array<CharSequence>?
    private var adapter: SimpleSpinnerAdapter<CharSequence>? = null
    private var onItemSelectedListener: ((View, Int) -> Unit)? = null
    private var onSpinnerResetListener: (() -> Unit)? = null
    private var initialized = false
    private val colorChange: Boolean

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SmartSpinner)
        menuPaddingStart = typedArray.getDimensionPixelSize(R.styleable.SmartSpinner_menuPaddingStart, 0)
        menuPaddingEnd = typedArray.getDimensionPixelSize(R.styleable.SmartSpinner_menuPaddingEnd, 0)
        menuWidth = typedArray.getLayoutDimension(R.styleable.SmartSpinner_menuWidth, -3)
        menuOffsetX = typedArray.getDimensionPixelSize(R.styleable.SmartSpinner_menuOffsetX, 0)
        menuOffsetY = typedArray.getDimensionPixelSize(R.styleable.SmartSpinner_menuOffsetY, 0)
        gravity = typedArray.getInt(R.styleable.SmartSpinner_textAlignment, Gravity.START) or Gravity.CENTER_VERTICAL
        isClickable = true
        textTint = typedArray.getColor(R.styleable.SmartSpinner_textColor, Color.BLACK)
        selectedTint = typedArray.getColor(R.styleable.SmartSpinner_selectedColor, Color.CYAN)
        setTextColor(textTint)
        headBackground = typedArray.getResourceId(R.styleable.SmartSpinner_spinnerBackground, R.color.light_gray)
        setBackgroundResource(headBackground)
        menuBackground = typedArray.getResourceId(R.styleable.SmartSpinner_menuBackground, headBackground)
        selectedBackground = typedArray.getResourceId(R.styleable.SmartSpinner_selectedBackground, menuBackground)
        spinnerTextSize = typedArray.getDimension(R.styleable.SmartSpinner_textSize, resources.getDimension(R.dimen.default_text_size))
        setTextSize(TypedValue.COMPLEX_UNIT_PX, spinnerTextSize)
        presetText = typedArray.getString(R.styleable.SmartSpinner_presetText)
        presetIndex = if (presetText != null) -1 else typedArray.getInt(R.styleable.SmartSpinner_presetIndex, 0)
        colorChange = typedArray.getBoolean(R.styleable.SmartSpinner_colorChange, false)
        isArrowHidden = typedArray.getBoolean(R.styleable.SmartSpinner_hideArrow, false)
        arrowDrawableTint = typedArray.getColor(R.styleable.SmartSpinner_arrowTint, ResourcesCompat.getColor(resources, android.R.color.black, null))
        arrowDrawableResId = typedArray.getResourceId(R.styleable.SmartSpinner_arrowDrawable, R.drawable.arrow)
        entries = typedArray.getTextArray(R.styleable.SmartSpinner_entries)
        text = presetText ?: entries?.get(0)
        val popupView = View.inflate(context, R.layout.spinner_menu, null)
        recyclerView = popupView.rcv
        recyclerView.layoutManager = LinearLayoutManager(context)
        setAdapter(SimpleSpinnerAdapter(R.layout.spinner_simple_item, entries?.toMutableList() ?: ArrayList(), menuPaddingStart, menuPaddingEnd, menuBackground, textTint, textSize, gravity))
        dropDownMenu = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            isFocusable = true
            isOutsideTouchable = true
            isTouchable = true
        }
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

    fun setAdapter(baseAdapter: SimpleSpinnerAdapter<CharSequence>){
        adapter = baseAdapter
        recyclerView.adapter = adapter
        adapter!!.apply {
            setSelectedPosition(selectedIndex)
            setSelectedColor(selectedTint)
            setSelectedBackground(selectedBackground)
            onItemClickListener = {view, position ->
                setSelectedIndex(view, position)
                notifyDataSetChanged()
                dropDownMenu.dismiss()
            }
        }
        reset()
    }

    fun reset(){
        setSelectedIndex(this, presetIndex, false)
        onSpinnerResetListener?.let { it() } ?: if (presetIndex != -1) { onItemSelectedListener?.let { it(this, presetIndex) } }
    }

    fun setSelectedIndex(view: View, position: Int, selected: Boolean = true){
        selectedIndex = position
        adapter?.setSelectedPosition(position)
        text = if (position == -1) presetText else adapter?.getData()?.get(position)
        setTextColor(if (position != presetIndex && colorChange) selectedTint else textTint)
        if (selected && position != -1){
            onItemSelectedListener?.let { it(view, position) }
        }
    }

    fun setDataSource(dataSource: MutableList<CharSequence>){
        adapter?.setData(dataSource)
        setSelectedIndex(this, presetIndex, false)
    }

    private fun resizeDropDownMenu(){
        dropDownMenu.width = if (menuWidth == -3) width else menuWidth
        (adapter as SimpleSpinnerAdapter).apply {
            setItemHeight(height)
            notifyDataSetChanged()
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
}