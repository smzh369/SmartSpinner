package com.zerlings.library

import android.R.attr
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.SimpleAdapter
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

    private var selectedIndex = 0
    private var arrowDrawable: Drawable? = null
    private var isArrowHidden = false
    private var textTint = 0
    private var selectedTint = 0
    private var leftPadding = 0
    private var rightPadding = 0
    private var topPadding = 0
    private var bottomPadding = 0
    //private var backgroundSelector = 0
    private var arrowDrawableTint = 0
    private var displayHeight = 0
    //private var parentVerticalOffset = 0
    //private var dropDownListPaddingBottom = 0
    @DrawableRes
    private var arrowDrawableResId = 0
    private var popupWindow: PopupWindow
    private var recyclerView: RecyclerView

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SmartSpinner)
        val defaultPadding = resources.getDimensionPixelSize(R.dimen.default_padding)
        leftPadding = typedArray.getDimensionPixelSize(R.styleable.SmartSpinner_android_paddingLeft, defaultPadding)
        rightPadding = typedArray.getDimensionPixelSize(R.styleable.SmartSpinner_android_paddingRight, defaultPadding)
        topPadding = typedArray.getDimensionPixelSize(R.styleable.SmartSpinner_android_paddingTop, defaultPadding)
        bottomPadding = typedArray.getDimensionPixelSize(R.styleable.SmartSpinner_android_paddingBottom, defaultPadding)
        setPadding(leftPadding, topPadding, rightPadding, bottomPadding)
        gravity = Gravity.CENTER_VERTICAL or Gravity.START
        isClickable = true
        textTint = typedArray.getColor(R.styleable.SmartSpinner_android_textColor, getDefaultTextColor(context))
        selectedTint = typedArray.getColor(R.styleable.SmartSpinner_selectedColor, getDefaultSelectedColor(context))
        setTextColor(textTint)
        isArrowHidden = typedArray.getBoolean(R.styleable.SmartSpinner_hideArrow, false)
        arrowDrawableTint = typedArray.getColor(R.styleable.SmartSpinner_arrowTint, ResourcesCompat.getColor(resources, android.R.color.black, null))
        arrowDrawableResId = typedArray.getResourceId(R.styleable.SmartSpinner_arrowDrawable, R.drawable.arrow)
        val entries = typedArray.getTextArray(R.styleable.SmartSpinner_android_entries)
        val popupView = View.inflate(context, R.layout.window_spinner, null)
        recyclerView = popupView.rcv
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = RecyclerView.Adapter
        popupWindow = PopupWindow(popupView, typedArray.getDimensionPixelSize(R.styleable.SmartSpinner_android_layout_width, ViewGroup.LayoutParams.WRAP_CONTENT), ViewGroup.LayoutParams.WRAP_CONTENT )
        typedArray.recycle()
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
}