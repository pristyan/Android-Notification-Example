package com.cocoba.notification.firebase

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.support.v4.app.NotificationCompat
import android.support.v4.app.RemoteInput
import com.cocoba.notification.R
import com.cocoba.notification.util.getColorFromRes
import org.jetbrains.anko.toast


/**
 * Created by Chandra on 01/04/19.
 * Need some help?
 * Contact me at y.pristyan.chandra@gmail.com
 */
class ChatNotificationReceiver : BroadcastReceiver() {

    companion object {
        private const val DISMISS_DELAY = 2000L
    }

    private val Context.notificationManager
        get() = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override fun onReceive(context: Context?, intent: Intent?) {
        Handler().postDelayed({
            val messageToSend = intent.getReplyMessage()
            val chatId = intent?.getStringExtra("chat_id")

            val notificationId = intent?.getIntExtra(MyFirebaseMessagingService.NOTIFICATION_ID, 0)
            val notificationTag = intent?.getStringExtra(MyFirebaseMessagingService.NOTIFICATION_TAG)

            notificationId?.let { id ->
                notificationTag?.let { tag ->
                    context?.createSuccessNotification(tag, id)
                }
            }

            context?.toast("to : $chatId\n$messageToSend")
        }, DISMISS_DELAY)
    }

    private fun Intent?.getReplyMessage(): CharSequence? {
        val remoteInput = RemoteInput.getResultsFromIntent(this)
        return remoteInput?.getCharSequence(MyFirebaseMessagingService.CHAT_REPLY)
    }

    private fun Context.createSuccessNotification(notificationTag: String, notificationId: Int) {
        val channelId = getString(R.string.default_notification_channel_id)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.round_message_black_24)
                .setContentText("Pesan Terkirim")
                .setColor(getColorFromRes(R.color.colorPrimary))

        notificationManager.notify(notificationTag, notificationId, notificationBuilder.build())

        Handler().postDelayed({
            notificationManager.cancel(notificationTag, notificationId)
        }, DISMISS_DELAY)
    }
}