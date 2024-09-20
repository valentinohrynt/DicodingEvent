package com.inoo.dicodingevent.util

import java.text.SimpleDateFormat
import java.util.Locale

object simpleDateUtil {
    fun formatDateTime(input: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault())

        val date = inputFormat.parse(input)
        return outputFormat.format(date)
    }
}