package com.sina.notesmvi.util

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun logger(input: Any?) {
    Log.e("APPLICATION LOG:", input.toString())
}

 fun getCurrentDate(): String {
    val time = Date()
    val formatter = SimpleDateFormat("yyyy-mm-dd HH-mm", Locale.getDefault())
    return formatter.format(time)
}