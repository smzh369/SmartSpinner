package com.zerlings.library

import android.text.Spannable
import android.text.SpannableString


class SimpleSpinnerTextFormatter : SpinnerTextFormatter<Any?> {

    override fun format(item: Any?): Spannable? {
        return SpannableString(item.toString())
    }
}