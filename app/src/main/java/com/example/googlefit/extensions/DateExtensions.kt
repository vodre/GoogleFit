package com.example.googlefit.extensions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Pattern: dd/MMMM/yy
 */
fun Date.formatToViewDateDefault(): String {
    val sdf = SimpleDateFormat("dd/MMMM/yy", Locale.getDefault())
    return sdf.format(this)
}

/**
 * HH:mm a
 */
fun Date.formatToViewTimeDefault(): String {
    val sdf = SimpleDateFormat("HH:mm a", Locale.getDefault())
    return sdf.format(this)
}
