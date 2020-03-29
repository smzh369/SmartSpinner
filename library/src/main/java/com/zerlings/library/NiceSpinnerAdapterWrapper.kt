package com.zerlings.library

import android.content.Context
import android.widget.ListAdapter


/*
 * Copyright (C) 2015 Angelo Marchesin.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
class NiceSpinnerAdapterWrapper internal constructor(
    context: Context?,
    private val baseAdapter: ListAdapter,
    textColor: Int,
    backgroundSelector: Int,
    spinnerTextFormatter: SpinnerTextFormatter<*>?,
    horizontalAlignment: PopUpTextAlignment?
) : NiceSpinnerBaseAdapter(
    context,
    textColor,
    backgroundSelector,
    spinnerTextFormatter,
    horizontalAlignment
) {
    val count: Int
        get() = baseAdapter.count - 1

    fun getItem(position: Int): Any {
        return baseAdapter.getItem(if (position >= selectedIndex) position + 1 else position)
    }

    fun getItemInDataset(position: Int): Any {
        return baseAdapter.getItem(position)
    }

}