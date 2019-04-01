package com.cocoba.notification.firebase

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent



/**
 * Created by Chandra on 01/04/19.
 * Need some help?
 * Contact me at y.pristyan.chandra@gmail.com
 */
class DismissNotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val notificationId = intent?.getIntExtra(MyFirebaseMessagingService.NOTIFICATION_ID, 0)
        val notificationTag = intent?.getStringExtra(MyFirebaseMessagingService.NOTIFICATION_TAG)

        if (notificationId != null && notificationTag != null) {
            val manager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.cancel(notificationTag, notificationId)
        }
    }
}