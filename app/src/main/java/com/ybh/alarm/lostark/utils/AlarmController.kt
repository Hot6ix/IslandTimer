package com.ybh.lostark.islandtimer.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.ybh.lostark.islandtimer.R
import com.ybh.lostark.islandtimer.etc.TimerItem
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Comparator

object AlarmController {

    fun scheduleAlarm(context: Context, item: TimerItem) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val today = Calendar.getInstance()
        val notificationTimeValues = context.resources.getIntArray(R.array.notification_time)
        val islandSchedule = MediaCursor.getIslandSchedule(context, item.island)

        var todayApproximateTime: Calendar? = null
        val actualTime = Calendar.getInstance()
        var approximateTime = Calendar.getInstance().apply {
            set(Calendar.YEAR, 9999)
        }
        item.schedule.forEach { scheduleItem ->
            scheduleItem.days.mapIndexed { index, i ->
                if(i == 1) index + 1
                else null
            }.filterNotNull().forEachIndexed { _, day ->
                val converted = scheduleItem.timetable.mapIndexed { timeIndex, i ->
                    if (i == 1) islandSchedule[timeIndex]
                    else null
                }.filterNotNull().map {
                    Calendar.getInstance().apply {
                        time = SimpleDateFormat("HH시 mm분", Locale.getDefault()).parse(it)
                        set(Calendar.SECOND, 0)
                        set(Calendar.YEAR, today.get(Calendar.YEAR))
                        set(Calendar.MONTH, today.get(Calendar.MONTH))
                        set(Calendar.DATE, today.get(Calendar.DATE))
                    }
                }

                // apply notification time and add days
                converted.forEach {
                    it.apply {
                        if(get(Calendar.DAY_OF_WEEK) != day) {
                            while(get(Calendar.DAY_OF_WEEK) != day) add(Calendar.DAY_OF_YEAR, 1)
                        }

                        // find today approximate time
                        if(get(Calendar.DAY_OF_WEEK) == today.get(Calendar.DAY_OF_WEEK)) {
                            if(after(today)) {
                                if(todayApproximateTime == null) {
                                    todayApproximateTime = Calendar.getInstance().apply {
                                        this.time = it.time
                                    }
                                }

                                val diffA = timeInMillis - todayApproximateTime!!.timeInMillis
                                val diffB = todayApproximateTime!!.timeInMillis - today.timeInMillis
                                if(diffA < diffB) {
                                    todayApproximateTime!!.time = time
                                }
                            }
                        }

                        // subtract notification time
                        add(Calendar.MINUTE, -notificationTimeValues[item.notification])
                    }
                }

                val candidate = converted
                .filter { it.after(today) }
                .sortedBy { it.time }
                .firstOrNull { it.after(today) }

                if(candidate != null) {
                    if(approximateTime.after(candidate)) {
                        approximateTime = candidate
                    }
                    if(candidate.get(Calendar.DAY_OF_WEEK) == today.get(Calendar.DAY_OF_WEEK)) {
                        return@forEach
                    }
                }
            }
        }

        // show notification if time passed from expected time
        if(todayApproximateTime != null) {
            if(todayApproximateTime!!.before(approximateTime) && todayApproximateTime!!.after(today)) {
                NotificationManager.showNotification(context, item, todayApproximateTime!!.time)
            }
        }

        actualTime.apply {
            time = approximateTime.time
            add(Calendar.MINUTE, notificationTimeValues[item.notification])
        }

        val intent = Intent(context, AlarmReceiver::class.java).let {
            val bundle = Bundle()
            bundle.putSerializable(AlarmReceiver.TIMER_ISLAND, item.island)
            bundle.putLong(AlarmReceiver.TIMER_ACTUAL_TIME, actualTime.timeInMillis)
            it.putExtra(AlarmReceiver.EXTRA_BUNDLE, bundle)

            PendingIntent.getBroadcast(context, item.id!!, it, PendingIntent.FLAG_UPDATE_CURRENT)
        }

//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, approximateTime.timeInMillis, intent)
        Log.d("tagggg", "Alarm set [Island : ${item.island}, ${approximateTime.time}]")
    }

    fun cancelAlarm(context: Context, item: TimerItem) {
        val intent = Intent(context, AlarmReceiver::class.java).let {
            it.putExtra(AlarmReceiver.TIMER_ISLAND, item.island)
            PendingIntent.getBroadcast(context, item.id!!, it, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(intent)
        Log.d("tagggg", "Alarm cancelled [Island : ${item.island}]")
    }

    fun scheduleRepeating(context: Context, item: TimerItem) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val today = Calendar.getInstance()
        val schedule = Calendar.getInstance()

        val notificationTimeValues = context.resources.getIntArray(R.array.notification_time)

        val islandSchedule = MediaCursor.getIslandSchedule(context, item.island)

        // convert to time list
        val converted = islandSchedule.map {
            val date = SimpleDateFormat("HH시 mm분", Locale.getDefault()).parse(it)
            schedule.apply {
                time = date
            }

            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, schedule.get(Calendar.HOUR_OF_DAY))
                set(Calendar.MINUTE, schedule.get(Calendar.MINUTE))
                set(Calendar.SECOND, 0)
            }

            if(cal.before(today)) cal.add(Calendar.DAY_OF_YEAR, 1)
            cal
        }

        // update time for notification
//        val notificationTimeValues = context.resources.getIntArray(R.array.notification_time)
        val expected = converted.map {
            val tmpCal = Calendar.getInstance()
            tmpCal.apply {
                time = it.time
                add(Calendar.MINUTE, -notificationTimeValues[item.notification])
            }
        }.sortedBy { it.time }

        // find approximate time
        val firstSchedule = expected.first { it.after(today) }

        // get approximate schedule
        val approximateSchedule = converted.toList().sortedWith(Comparator<Calendar> { calA, calB ->
            val a = Math.abs(calA.timeInMillis - today.timeInMillis)
            val b = Math.abs(calB.timeInMillis - today.timeInMillis)
            if (a < b) -1 else 1
        }).first()

        // show notification if time passed from expected time
        if(approximateSchedule.before(firstSchedule) && approximateSchedule.after(today)) {
            NotificationManager.showNotification(context, item, approximateSchedule.time)
        }

        val intent = Intent(context, AlarmReceiver::class.java).let {
            val bundle = Bundle()
            bundle.putSerializable(AlarmReceiver.TIMER_ISLAND, item.island)
            it.putExtra(AlarmReceiver.EXTRA_BUNDLE, bundle)

            PendingIntent.getBroadcast(context, item.id!!, it, PendingIntent.FLAG_UPDATE_CURRENT)
        }
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60000, intent)
        val interval = MediaCursor.getIslandInterval(context, item.island)
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, firstSchedule!!.timeInMillis, interval, intent)
        Log.d("tagggg", "Repeating alarm set [Island : ${item.island}, ${firstSchedule.time}]")
    }

}