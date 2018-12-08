package com.ybh.alarm.lostark.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import com.ybh.alarm.lostark.etc.Island
import java.util.*

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                or PowerManager.ACQUIRE_CAUSES_WAKEUP
                or PowerManager.ON_AFTER_RELEASE, TAG)

        if(!powerManager.isInteractive)
            wakeLock.acquire(10000)

        val bundle = intent.getBundleExtra(EXTRA_BUNDLE)

        val island = bundle.getSerializable(TIMER_ISLAND) as Island
        val actualTime = Calendar.getInstance().apply {
            timeInMillis = bundle.getLong(TIMER_ACTUAL_TIME)
        }

        val dbCursor = DatabaseCursor(context)
        val item = dbCursor.getTimer(island)

        NotificationManager.showNotification(context, item!!, actualTime)
        AlarmController.scheduleAlarm(context, item)

        if(wakeLock.isHeld)
            wakeLock.release()
    }

    companion object {
        const val EXTRA_BUNDLE = "EXTRA_BUNDLE"
        const val TIMER_ISLAND = "TIMER_ISLAND"
        const val TIMER_ACTUAL_TIME = "TIMER_ACTUAL_TIME"
        const val TAG = "com.ybh.alarm.lostark:POWER"
    }
}
