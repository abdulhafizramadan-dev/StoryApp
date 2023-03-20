package com.ahr.storyapp.helper

import android.text.format.DateUtils
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

fun TextView.setLocalDateFormat(timestamp: String) {
    val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
    val date = sdf.parse(timestamp) as Date
    text = DateUtils.getRelativeTimeSpanString(date.time)
}