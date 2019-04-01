package com.cocoba.notification.util

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat

/**
 * Created by Chandra on 23/02/18.
 * Need some help?
 * Contact me at y.pristyan.chandra@gmail.com
 */
fun Context.getColorFromRes(color: Int): Int {
    return Color.parseColor("#" + Integer.toHexString(ContextCompat.getColor(this, color)))
}