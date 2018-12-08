package com.ybh.alarm.lostark.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.ybh.alarm.lostark.R
import com.ybh.alarm.lostark.etc.TimerItem
import java.text.SimpleDateFormat
import java.util.*
import android.media.AudioAttributes



object NotificationManager {

    private const val NOTIFICATION_GROUP_ID = "com.ybh.alarm.lostark.INCOMING_ISLAND"
    private const val NOTIFICATION_GROUP_NAME = "com.ybh.alarm.lostark.GROUP"
    private const val NOTIFICATION_CHANNEL_ID = "LOSTARK"
    private const val NOTIFICATION_CHANNEL_NAME = "LOSTARK_NOTI"

    private lateinit var mNotificationManager: NotificationManager
    private lateinit var mNotificationGroup: NotificationChannelGroup
    private lateinit var mNotificationChannel: NotificationChannel

    fun showNotification(context: Context, item: TimerItem, expected: Calendar) {
        if(!::mNotificationManager.isInitialized)
            mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(!::mNotificationGroup.isInitialized) {
                // notification group
                mNotificationGroup = NotificationChannelGroup(NOTIFICATION_GROUP_ID, NOTIFICATION_GROUP_NAME)
                mNotificationManager.createNotificationChannelGroup(mNotificationGroup)
            }

            if(!::mNotificationChannel.isInitialized) {

                val attributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build()

                // notification channel
                mNotificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
                    enableVibration(true)
                    setSound(Uri.parse("android.resource://${context.packageName}/${R.raw.lostark_ringtone}"), attributes)
                    lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                    importance = NotificationManager.IMPORTANCE_HIGH
                }
                mNotificationManager.createNotificationChannel(mNotificationChannel)
            }
        }

        val islandName = MediaCursor.getIslandName(context, item.island)
        val formattedTime = SimpleDateFormat("a h시 mm분", Locale.getDefault()).format(expected.time)
        val timeout = expected.timeInMillis - Calendar.getInstance().timeInMillis

        val islandNotification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID).apply {
            setSmallIcon(MediaCursor.getIconResource(item.island))
//            setLargeIcon(BitmapFactory.decodeResource(context.resources, MediaCursor.getIconResource(item.island)))
            setContentTitle(context.resources.getString(R.string.notification_title, formattedTime, islandName))
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setVibrate(longArrayOf(0, 250, 250, 250))
            setGroup(NOTIFICATION_GROUP_ID)
            priority = NotificationCompat.PRIORITY_MAX
            setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_SUMMARY)
        }

        val summaryNotification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID).apply {
            setSmallIcon(R.drawable.ic_action_add)
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            setGroupSummary(true)
            setGroup(NOTIFICATION_GROUP_ID)
            setTimeoutAfter(timeout)
            priority = NotificationCompat.PRIORITY_MAX
        }

        mNotificationManager.notify(item.id!!, islandNotification.build())
        mNotificationManager.notify(0, summaryNotification.build())
    }
}