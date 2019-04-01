package com.cocoba.notification.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.net.HttpURLConnection
import java.net.URL

/**
 * Created by Chandra on 01/04/19.
 * Need some help?
 * Contact me at y.pristyan.chandra@gmail.com
 */
fun String?.getBitmapFromUrl(): Bitmap? {
    if (this == null) return null
    return try {
        val url = URL(this)
        val connection = url.openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()
        val input = connection.inputStream
        BitmapFactory.decodeStream(input)

    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}