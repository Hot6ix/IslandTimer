package com.ybh.alarm.lostark

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity


class WakefulActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
            or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
        )

        finish()

//        val island = intent.getSerializableExtra(AlarmReceiver.TIMER_ISLAND) as Island
//        val actualTime = Calendar.getInstance().apply {
//            timeInMillis = intent.getLongExtra(AlarmReceiver.TIMER_ACTUAL_TIME, 0)
//        }
//
//        val dbCursor = DatabaseCursor(applicationContext)
//        val item = dbCursor.getTimer(island)
//
//        Toast.makeText(applicationContext, "test", Toast.LENGTH_SHORT).apply {
//            setGravity(Gravity.TOP, 0, 0)
//        }.show()
//
//        NotificationManager.showNotification(applicationContext, item!!, actualTime)
//        AlarmController.scheduleAlarm(applicationContext, item)=

//        val island = intent.getSerializableExtra(AlarmReceiver.TIMER_ISLAND) as Island
//        val actualTime = Calendar.getInstance().apply {
//            timeInMillis = intent.getLongExtra(AlarmReceiver.TIMER_ACTUAL_TIME, 0)
//        }
//
//        val dbCursor = DatabaseCursor(applicationContext)
//        val item = dbCursor.getTimer(island)
//        NotificationManager.showNotification(applicationContext, item!!, actualTime)
//        AlarmController.scheduleAlarm(applicationContext, item)
    }
}
