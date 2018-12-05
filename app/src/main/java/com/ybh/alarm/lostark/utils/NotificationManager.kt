package com.ybh.lostark.islandtimer.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.ybh.lostark.islandtimer.R
import com.ybh.lostark.islandtimer.etc.TimerItem
import java.text.SimpleDateFormat
import java.util.*

object NotificationManager {

    fun showNotification(context: Context, item: TimerItem, time: Date) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(AlarmReceiver.NOTIFICATION_CHANNEL_ID, AlarmReceiver.NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH).apply {
                enableVibration(true)
            }
            manager.createNotificationChannel(channel)
        }

        val islandName = MediaCursor.getIslandName(context, item.island)
        val formattedTime = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).format(time)
        val builder = NotificationCompat.Builder(context, AlarmReceiver.NOTIFICATION_CHANNEL_ID).apply {
            setSmallIcon(MediaCursor.getIconResource(item.island))
            setContentTitle(context.resources.getString(R.string.notification_title, formattedTime, islandName))
            priority = NotificationCompat.PRIORITY_MAX
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        }
        manager.notify(AlarmReceiver.NOTIFICATION_ID, builder.build())
    }
}