package com.ybh.lostark.islandtimer.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ybh.lostark.islandtimer.etc.Island
import java.util.*

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val bundle = intent.getBundleExtra(EXTRA_BUNDLE)
        val island = bundle.getSerializable(TIMER_ISLAND) as Island
        val actualTime = Calendar.getInstance().apply {
            timeInMillis = bundle.getLong(TIMER_ACTUAL_TIME)
        }

        val dbCursor = DatabaseCursor(context)
        val item = dbCursor.getTimer(island)
        NotificationManager.showNotification(context, item!!, actualTime.time)
        AlarmController.scheduleAlarm(context, item)
    }

    companion object {
        const val EXTRA_BUNDLE = "EXTRA_BUNDLE"
        const val TIMER_ISLAND = "TIMER_ISLAND"
        const val TIMER_ACTUAL_TIME = "TIMER_ACTUAL_TIME"
        const val NOTIFICATION_ID = 31331
        const val NOTIFICATION_CHANNEL_ID = "LOSTARK"
        const val NOTIFICATION_CHANNEL_NAME = "LOSTARK_NOTI"
    }
}
