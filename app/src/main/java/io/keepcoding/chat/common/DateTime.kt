package io.keepcoding.chat.common

import java.text.SimpleDateFormat
import java.util.*

fun getDateTime(time: Long): String {
    val date = Date(time)
    val format = SimpleDateFormat("dd.MM.yyyy HH:mm")
    return format.format(date)
}