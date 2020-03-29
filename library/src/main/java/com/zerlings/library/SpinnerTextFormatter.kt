package com.zerlings.library

import android.text.Spannable

interface SpinnerTextFormatter<T> {
    fun format(item: T): Spannable?
}