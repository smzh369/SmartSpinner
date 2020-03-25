package com.zerlings.library

import android.R.attr
import android.R.attr.textColor
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.annotation.Nullable
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v4.view.animation.LinearOutSlowInInterpolator
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ListPopupWindow


class SmartSpinner(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : AppCompatTextView(context, attrs, defStyleAttr) {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, android.R.attr.textViewStyle)

    constructor(context: Context) : this(context, null)

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
    private var popupWindow: ListPopupWindow
    private var adapter: SmartSpinnerBaseAdapter? = null

    private var onItemClickListener: OnItemClickListener? = null
    private var onItemSelectedListener: OnItemSelectedListener? = null
    private val onSpinnerItemSelectedListener: OnSpinnerItemSelectedListener? = null

    private var isArrowHidden = false
    private var textTint = 0
    private var backgroundSelector = 0
    private var arrowDrawableTint = 0
    private var displayHeight = 0
    private var parentVerticalOffset = 0
    private var dropDownListPaddingBottom = 0
    @DrawableRes
    private var arrowDrawableResId = 0
    private val spinnerTextFormatter: SpinnerTextFormatter = SimpleSpinnerTextFormatter()
    private var selectedTextFormatter: SpinnerTextFormatter = SimpleSpinnerTextFormatter()
    private var horizontalAlignment: PopUpTextAlignment? = null

    @Nullable
    private var arrowAnimator: ObjectAnimator? = null

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SmartSpinner)
        gravity = Gravity.CENTER_VERTICAL or Gravity.START
        isClickable = true
        setBackgroundResource(backgroundSelector)
        textTint = typedArray.getColor(R.styleable.SmartSpinner_textTint, getDefaultTextColor(context))
        setTextColor(textTint)
        popupWindow = ListPopupWindow(context)
        popupWindow.setOnItemClickListener{ parent, view, position, id ->
            // The selected item is not displayed within the list, so when the selected position is equal to
            // the one of the currently selected item it gets shifted to the next item.
            var position = position
            if (position >= selectedIndex && position < adapter.getCount()) {
                position++
            }
            selectedIndex = position
            if (onSpinnerItemSelectedListener != null) {
                onSpinnerItemSelectedListener.onItemSelected(
                    this@SmartSpinner,
                    view,
                    position,
                    id
                )
            }
            onItemClickListener?.onItemClick(parent, view, position, id)
            onItemSelectedListener?.onItemSelected(parent, view, position, id)
            adapter.setSelectedIndex(position)
            setTextInternal(adapter.getItemInDataset(position))
            dismissDropDown()
        }

        popupWindow.isModal = true

        popupWindow.setOnDismissListener {
            if (!isArrowHidden) {
                animateArrow(false)
            }
        }

        isArrowHidden = typedArray.getBoolean(R.styleable.SmartSpinner_hideArrow, false)
        arrowDrawableTint = typedArray.getColor(R.styleable.SmartSpinner_arrowTint, ResourcesCompat.getColor(resources, android.R.color.black, null))
        arrowDrawableResId = typedArray.getResourceId(R.styleable.SmartSpinner_arrowDrawable, R.drawable.arrow)
        dropDownListPaddingBottom = typedArray.getDimensionPixelSize(R.styleable.SmartSpinner_dropDownListPaddingBottom, 0)
        horizontalAlignment = PopUpTextAlignment.fromId(
            typedArray.getInt(
                R.styleable.SmartSpinner_popupTextAlignment,
                PopUpTextAlignment.CENTER.ordinal()
            )
        )

        val entries = typedArray.getTextArray(R.styleable.SmartSpinner_entries)
        if (entries != null) {
            attachDataSource(entries.toList())
        }

        typedArray.recycle()

        measureDisplayHeight()
    }

    private fun measureDisplayHeight() {
        displayHeight = context.resources.displayMetrics.heightPixels
    }

    private fun getParentVerticalOffset(): Int {
        if (parentVerticalOffset > 0) {
            return parentVerticalOffset
        }
        val locationOnScreen = IntArray(2)
        getLocationOnScreen(locationOnScreen)
        return locationOnScreen[VERTICAL_OFFSET].also { parentVerticalOffset = it }
    }

    override fun onDetachedFromWindow() {
        arrowAnimator?.cancel()
        super.onDetachedFromWindow()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            onVisibilityChanged(this, visibility)
        }
    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        arrowDrawable = initArrowDrawable(arrowDrawableTint)
        setArrowDrawableOrHide(arrowDrawable)
    }

    private fun initArrowDrawable(drawableTint: Int): Drawable? {
        if (arrowDrawableResId == 0) return null
        var drawable = ContextCompat.getDrawable(context, arrowDrawableResId)
        if (drawable != null) { // Gets a copy of this drawable as this is going to be mutated by the animator
            drawable = DrawableCompat.wrap(drawable).mutate()
            if (drawableTint != Int.MAX_VALUE && drawableTint != 0) {
                DrawableCompat.setTint(drawable, drawableTint)
            }
        }
        return drawable
    }

    private fun setArrowDrawableOrHide(drawable: Drawable?) {
        setCompoundDrawablesWithIntrinsicBounds(null, null, if (!isArrowHidden && drawable != null) drawable else null, null)
    }

    private fun getDefaultTextColor(context: Context): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(attr.textColorPrimary, typedValue, true)
        val typedArray = context.obtainStyledAttributes(typedValue.data, intArrayOf(attr.textColorPrimary))
        val defaultTextColor = typedArray.getColor(0, Color.BLACK)
        typedArray.recycle()
        return defaultTextColor
    }

    fun getItemAtPosition(position: Int): Any? {
        return adapter.getItemInDataset(position)
    }

    fun getSelectedItem(): Any? {
        return adapter.getItemInDataset(selectedIndex)
    }

    fun getSelectedIndex(): Int {
        return selectedIndex
    }

    fun setArrowDrawable(@DrawableRes @ColorRes drawableId: Int) {
        arrowDrawableResId = drawableId
        arrowDrawable = initArrowDrawable(R.drawable.arrow)
        setArrowDrawableOrHide(arrowDrawable)
    }

    fun setArrowDrawable(drawable: Drawable) {
        arrowDrawable = drawable
        setArrowDrawableOrHide(arrowDrawable)
    }

    private fun setTextInternal(item: Any) {
        text = if (selectedTextFormatter != null) (selectedTextFormatter.format(item)) else item.toString()
    }

    /**
     * Set the default spinner item using its index
     *
     * @param position the item's position
     */
    fun setSelectedIndex(position: Int) {
        if (adapter != null) {
            if (position >= 0 && position <= adapter.getCount()) {
                adapter.setSelectedIndex(position)
                selectedIndex = position
                setTextInternal(selectedTextFormatter.format(adapter.getItemInDataset(position)).toString())
            } else {
                throw IllegalArgumentException("Position must be lower than adapter count!")
            }
        }
    }


    @Deprecated("use setOnSpinnerItemSelectedListener instead.")
    fun addOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }


    @Deprecated("use setOnSpinnerItemSelectedListener instead.")
    fun setOnItemSelectedListener(onItemSelectedListener: OnItemSelectedListener?) {
        this.onItemSelectedListener = onItemSelectedListener
    }

    fun <T> attachDataSource(list: List<T>) {
        adapter = SmartSpinnerAdapter(
            context,
            list,
            textColor,
            backgroundSelector,
            spinnerTextFormatter,
            horizontalAlignment
        )
        setAdapterInternal<Any>(adapter)
    }

    fun setAdapter(adapter: ListAdapter?) {
        this.adapter = SmartSpinnerAdapterWrapper(
            context, adapter, textColor, backgroundSelector,
            spinnerTextFormatter, horizontalAlignment
        )
        setAdapterInternal<Any>(this.adapter)
    }

    fun getPopUpTextAlignment(): PopUpTextAlignment? {
        return horizontalAlignment
    }

    private fun <T> setAdapterInternal(adapter: SmartSpinnerBaseAdapter<T>?) {
        if (adapter.getCount() >= 0) { // If the adapter needs to be set again, ensure to reset the selected index as well
            selectedIndex = 0
            popupWindow.setAdapter(adapter)
            setTextInternal(adapter.getItemInDataset(selectedIndex))
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isEnabled && event.action == MotionEvent.ACTION_UP) {
            if (!popupWindow.isShowing && adapter.getCount() > 0) {
                showDropDown()
            } else {
                dismissDropDown()
            }
        }
        return super.onTouchEvent(event)
    }

    private fun animateArrow(shouldRotateUp: Boolean) {
        val start = if (shouldRotateUp) 0 else MAX_LEVEL
        val end = if (shouldRotateUp) MAX_LEVEL else 0
        arrowAnimator = ObjectAnimator.ofInt(arrowDrawable, "level", start, end)
        arrowAnimator?.interpolator = LinearOutSlowInInterpolator()
        arrowAnimator?.start()
    }

    fun dismissDropDown() {
        if (!isArrowHidden) {
            animateArrow(false)
        }
        popupWindow.dismiss()
    }

    fun showDropDown() {
        if (!isArrowHidden) {
            animateArrow(true)
        }
        popupWindow.anchorView = this
        popupWindow.show()
        val listView: ListView? = popupWindow.listView
        if (listView != null) {
            listView.setVerticalScrollBarEnabled(false)
            listView.setHorizontalScrollBarEnabled(false)
            listView.setVerticalFadingEdgeEnabled(false)
            listView.setHorizontalFadingEdgeEnabled(false)
        }
    }


    private fun getPopUpHeight(): Int {
        return Math.max(verticalSpaceBelow(), verticalSpaceAbove())
    }

    private fun verticalSpaceAbove(): Int {
        return getParentVerticalOffset()
    }

    private fun verticalSpaceBelow(): Int {
        return displayHeight - getParentVerticalOffset() - measuredHeight
    }

    fun setTintColor(@ColorRes resId: Int) {
        if (arrowDrawable != null && !isArrowHidden) {
            DrawableCompat.setTint(arrowDrawable!!, ContextCompat.getColor(context, resId))
        }
    }

    fun setArrowTintColor(resolvedColor: Int) {
        if (arrowDrawable != null && !isArrowHidden) {
            DrawableCompat.setTint(arrowDrawable!!, resolvedColor)
        }
    }

    fun hideArrow() {
        isArrowHidden = true
        setArrowDrawableOrHide(arrowDrawable)
    }

    fun showArrow() {
        isArrowHidden = false
        setArrowDrawableOrHide(arrowDrawable)
    }

    fun isArrowHidden(): Boolean {
        return isArrowHidden
    }

    fun setDropDownListPaddingBottom(paddingBottom: Int) {
        dropDownListPaddingBottom = paddingBottom
    }

    fun getDropDownListPaddingBottom(): Int {
        return dropDownListPaddingBottom
    }

    fun setSpinnerTextFormatter(spinnerTextFormatter: SpinnerTextFormatter?) {
        this.spinnerTextFormatter = spinnerTextFormatter
    }

    fun setSelectedTextFormatter(textFormatter: SpinnerTextFormatter?) {
        selectedTextFormatter = textFormatter
    }


    fun performItemClick(position: Int, showDropdown: Boolean) {
        if (showDropdown) showDropDown()
        setSelectedIndex(position)
    }

    /**
     * only applicable when popup is shown .
     * @param view
     * @param position
     * @param id
     */
    fun performItemClick(view: View?, position: Int, id: Int) {
        showDropDown()
        val listView: ListV iew? = popupWindow.listView
        if (listView != null) {
            listView.performItemClick(view, position, id)
        }
    }

    fun getOnSpinnerItemSelectedListener(): OnSpinnerItemSelectedListener? {
        return onSpinnerItemSelectedListener
    }

    fun setOnSpinnerItemSelectedListener(onSpinnerItemSelectedListener: OnSpinnerItemSelectedListener?) {
        this.onSpinnerItemSelectedListener = onSpinnerItemSelectedListener
    }
}