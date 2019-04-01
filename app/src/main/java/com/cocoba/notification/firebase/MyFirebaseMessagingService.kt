package com.cocoba.notification.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.RingtoneManager
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.RemoteInput
import android.util.Log
import com.cocoba.notification.MainActivity
import com.cocoba.notification.R
import com.cocoba.notification.util.getBitmapFromUrl
import com.cocoba.notification.util.getColorFromRes
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        const val CHAT_REPLY = "NotificationReplay"
        const val NOTIFICATION_ID = "NotificationID"
        const val NOTIFICATION_TAG = "NotificationTag"

        private const val TAG = "MyFirebaseMsgService"
        private const val CHAT_REQUEST_CODE = 111
    }

    override fun onNewToken(token: String?) {
        super.onNewToken(token)
        Log.d(TAG, "onNewToken : $token")

        token?.let {
            Log.e(TAG, it)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        sendNotification(
                remoteMessage?.notification?.clickAction,
                remoteMessage?.data
        )
    }

    private fun sendNotification(action: String?, data: Map<String, String>?) {
        val notificationId = Random.nextInt(from = 101, until = 1000)

        val title = data?.get("title")
        val message = data?.get("message")

        val intent = Intent(this, MainActivity::class.java)
        var notificationTag = System.currentTimeMillis().toString()

        intent.action = action
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
                this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT
        )

        var image: Bitmap? = null
        if (data?.containsKey("image") == true) {
            image = data["image"].getBitmapFromUrl()
        }

        val channelId = getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.round_message_black_24)
                .setContentTitle(title)
                .setContentText(message)
                .setSound(defaultSoundUri)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setColor(getColorFromRes(R.color.colorPrimary))

        image?.let {
            notificationBuilder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(it))
        }


        if (data?.contains("action") == true && data["action"] == "chat") {
            notificationTag = "CHAT_NOTIFICATION_TAG_" + System.currentTimeMillis()
            val chatId = data["chat_id"]
            val chatIntent = Intent(this, ChatNotificationReceiver::class.java)
            chatIntent.putExtra(NOTIFICATION_ID, notificationId)
            chatIntent.putExtra(NOTIFICATION_TAG, notificationTag)
            chatIntent.putExtra("chat_id", chatId)

            val pendingChatIntent = PendingIntent.getBroadcast(
                    applicationContext,
                    CHAT_REQUEST_CODE,
                    chatIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT)

            val remoteInput = RemoteInput.Builder(CHAT_REPLY)
                    .setLabel("Please enter your message")
                    .build()

            val actionButton = NotificationCompat.Action.Builder(
                    R.drawable.round_message_black_24, "Reply", pendingChatIntent)
                    .addRemoteInput(remoteInput)
                    .build()

            val dismissIntent = PendingIntent.getBroadcast(
                    this,
                    CHAT_REQUEST_CODE,
                    Intent(this, DismissNotificationReceiver::class.java).apply {
                        putExtra(NOTIFICATION_ID, notificationId)
                        putExtra(NOTIFICATION_TAG, notificationTag)
                    },
                    PendingIntent.FLAG_CANCEL_CURRENT)

            notificationBuilder
                    .setRemoteInputHistory(emptyArray())
                    .addAction(actionButton)
                    .addAction(
                            android.R.drawable.ic_menu_close_clear_cancel,
                            "Dismiss",
                            dismissIntent)
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                    channelId,
                    "General",
                    NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(
                notificationTag,
                notificationId,
                notificationBuilder.build())
    }

}