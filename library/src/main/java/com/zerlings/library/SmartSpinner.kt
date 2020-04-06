package com.zerlings.library

import android.R.attr
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.PopupWindow
import kotlinx.android.synthetic.main.window_spinner.view.*

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
    private var selectedIndex = -1
    private var arrowDrawable: Drawable? = null
    private var isArrowHidden = false
    @ColorInt
    private val textTint: Int
    @ColorInt
    private val selectedTint: Int
    @ColorInt
    private val headBackground: Int
    @ColorInt
    private val menuBackground: Int
    @ColorInt
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
    private val popupWindow: PopupWindow
    private val recyclerView: RecyclerView
    private val entries: Array<CharSequence>?
    private var adapter: BaseSpinnerAdapter<*, *>? = null
    private var onSpinnerItemSelectedListener: ((View, Int) -> Unit)? = null
    private var onSpinnerResetListener: (() -> Unit)? = null

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SmartSpinner)
        menuPaddingStart = typedArray.getDimensionPixelSize(R.styleable.SmartSpinner_menuPaddingStart, 0)
        menuPaddingEnd = typedArray.getDimensionPixelSize(R.styleable.SmartSpinner_menuPaddingEnd, 0)
        menuWidth = typedArray.getDimensionPixelSize(R.styleable.SmartSpinner_menuWidth, -3)
        gravity = typedArray.getInt(R.styleable.SmartSpinner_textAlignment, Gravity.START) or Gravity.CENTER_VERTICAL
        isClickable = true
        textTint = typedArray.getColor(R.styleable.SmartSpinner_textColor, getDefaultTextColor(context))
        selectedTint = typedArray.getColor(R.styleable.SmartSpinner_selectedColor, getDefaultSelectedColor(context))
        setTextColor(textTint)
        headBackground = typedArray.getColor(R.styleable.SmartSpinner_backgroundColor, Color.WHITE)
        setBackgroundColor(headBackground)
        menuBackground = typedArray.getColor(R.styleable.SmartSpinner_menuBackground, headBackground)
        selectedBackground = typedArray.getColor(R.styleable.SmartSpinner_selectedBackground, Color.GRAY)
        spinnerTextSize = typedArray.getDimension(R.styleable.SmartSpinner_textSize, resources.getDimension(R.dimen.default_text_size))
        setTextSize(TypedValue.COMPLEX_UNIT_PX, spinnerTextSize)
        presetText = typedArray.getString(R.styleable.SmartSpinner_presetText)
        isArrowHidden = typedArray.getBoolean(R.styleable.SmartSpinner_hideArrow, false)
        arrowDrawableTint = typedArray.getColor(R.styleable.SmartSpinner_arrowTint, ResourcesCompat.getColor(resources, android.R.color.black, null))
        arrowDrawableResId = typedArray.getResourceId(R.styleable.SmartSpinner_arrowDrawable, R.drawable.arrow)
        entries = typedArray.getTextArray(R.styleable.SmartSpinner_entries)
        text = presetText ?: entries?.get(0)
        val popupView = View.inflate(context, R.layout.window_spinner, null)
        recyclerView = popupView.rcv
        recyclerView.layoutManager = LinearLayoutManager(context)
        popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            isFocusable = true
            isOutsideTouchable = true
            isTouchable = true
        }
        typedArray.recycle()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isEnabled && event.action == MotionEvent.ACTION_UP) {
            if (adapter == null){
                setAdapter(SimpleSpinnerAdapter(R.layout.item_simple_spinner, entries?.toMutableList() ?: ArrayList(), height, menuPaddingStart, menuPaddingEnd, menuBackground, textTint, textSize, gravity))
            }
            if (!popupWindow.isShowing && (adapter as BaseSpinnerAdapter).itemCount > 0) {
                popupWindow.showAsDropDown(this)
            } else {
                popupWindow.dismiss()
            }
        }
        return super.onTouchEvent(event)
    }

    fun setAdapter(baseAdapter: BaseSpinnerAdapter<*,*>){
        popupWindow.width = if (menuWidth == -3) width else menuWidth
        adapter = baseAdapter
        recyclerView.adapter = adapter
        (adapter as BaseSpinnerAdapter).apply {
            setSelectedPosition(selectedIndex)
            setSelectedColor(selectedTint)
            setSelectedBackground(selectedBackground)
            setOnItemClickListener {view, position ->
                selectedIndex = position
                if (adapter is SimpleSpinnerAdapter){
                    text = (adapter as SimpleSpinnerAdapter).getData()[position]
                }
                onSpinnerItemSelectedListener?.let { it(view, position) }
                popupWindow.dismiss()
            }
        }
        resetSpinner()
    }

    fun resetSpinner(resetText: String? = null){
        if (presetText != null){
            selectedIndex = -1
            text = presetText
        }else{
            selectedIndex = 0
            text = (adapter as? SimpleSpinnerAdapter)?.getData()?.get(0) ?: resetText
        }
        onSpinnerResetListener?.let { it() }
    }

    fun setIndex(index: Int, isSelected: Boolean = false, indexText: String? = null){
        selectedIndex = index
        text = (adapter as? SimpleSpinnerAdapter)?.getData()?.get(0) ?: indexText
    }

    private fun getDefaultTextColor(context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(attr.textColorPrimary, typedValue, true)
        val typedArray = context.obtainStyledAttributes(typedValue.data, intArrayOf(attr.textColorPrimary))
        val defaultTextColor = typedArray.getColor(0, Color.BLACK)
        typedArray.recycle()
        return defaultTextColor
    }

    private fun getDefaultSelectedColor(context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(attr.textColorPrimary, typedValue, true)
        val typedArray = context.obtainStyledAttributes(typedValue.data, intArrayOf(attr.textColorSecondary))
        val defaultSelectedColor = typedArray.getColor(0, Color.BLUE)
        typedArray.recycle()
        return defaultSelectedColor
    }

    fun setOnSpinnerItemSelectedListener(listener: (View, Int) -> Unit){
        onSpinnerItemSelectedListener = listener
    }

    fun setOnSpinnerResetListener(listener: () -> Unit){
        onSpinnerResetListener = listener
    }
}