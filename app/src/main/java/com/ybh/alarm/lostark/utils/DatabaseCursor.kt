package com.ybh.alarm.lostark.utils

import android.content.ContentValues
import android.content.Context
import com.ybh.alarm.lostark.etc.Island
import com.ybh.alarm.lostark.etc.TimerItem
import java.util.*

class DatabaseCursor(context: Context) {

    private var mDatabaseManager = DatabaseManager(context)

    fun getTimers(): ArrayList<TimerItem> {
        val db = mDatabaseManager.readableDatabase
        val cursor = db.query(DatabaseManager.TABLE_NAME, null, null, null, null, null, null)

        val list = ArrayList<TimerItem>()
        if(cursor.count > 0) {
            while(cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndex(DatabaseManager.COLUMN_ID))
                val island = Island.valueOf(cursor.getString(cursor.getColumnIndex(DatabaseManager.COLUMN_ISLAND)))
                val schedule = cursor.getString(cursor.getColumnIndex(DatabaseManager.COLUMN_SCHEDULE))
                val notification = cursor.getInt(cursor.getColumnIndex(DatabaseManager.COLUMN_NOTIFICATION))
                val switch = cursor.getInt(cursor.getColumnIndex(DatabaseManager.COLUMN_SWITCH))

                val item = TimerItem(
                    id,
                    island,
                    MediaCursor.stringToSchedule(schedule),
                    notification,
                    switch != 0
                )
                list.add(item)
            }
        }

        cursor.close()
        db.close()

        return list
    }

    fun getTimer(expectedIsland: Island): TimerItem? {
        val db = mDatabaseManager.readableDatabase
        val cursor = db.query(DatabaseManager.TABLE_NAME, null, "${DatabaseManager.COLUMN_ISLAND} = ?", arrayOf(expectedIsland.toString()), null, null, null)

        var item: TimerItem? = null
        if(cursor.count > 0) {
            while(cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndex(DatabaseManager.COLUMN_ID))
                val island = Island.valueOf(cursor.getString(cursor.getColumnIndex(DatabaseManager.COLUMN_ISLAND)))
                val schedule = cursor.getString(cursor.getColumnIndex(DatabaseManager.COLUMN_SCHEDULE))
                val notification = cursor.getInt(cursor.getColumnIndex(DatabaseManager.COLUMN_NOTIFICATION))
                val switch = cursor.getInt(cursor.getColumnIndex(DatabaseManager.COLUMN_SWITCH))

                item = TimerItem(
                    id,
                    island,
                    MediaCursor.stringToSchedule(schedule),
                    notification,
                    switch != 0
                )
            }
        }

        cursor.close()
        db.close()

        return item
    }

    fun addTimer(item: TimerItem) {
        val db = mDatabaseManager.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseManager.COLUMN_ISLAND, item.island.toString())
            put(DatabaseManager.COLUMN_SCHEDULE, MediaCursor.scheduleToString(item.schedule))
            put(DatabaseManager.COLUMN_NOTIFICATION, item.notification)
            put(DatabaseManager.COLUMN_SWITCH, item.switch)
        }
        item.id = db.insert(DatabaseManager.TABLE_NAME, null, values).toInt()
        db.close()
    }

    fun updateTimer(item: TimerItem) {
        val db = mDatabaseManager.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseManager.COLUMN_ISLAND, item.island.toString())
            put(DatabaseManager.COLUMN_SCHEDULE, MediaCursor.scheduleToString(item.schedule))
            put(DatabaseManager.COLUMN_NOTIFICATION, item.notification)
            put(DatabaseManager.COLUMN_SWITCH, item.switch)
        }
        db.update(DatabaseManager.TABLE_NAME, values, "${DatabaseManager.COLUMN_ISLAND} = ?", arrayOf(item.island.toString()))
        db.close()
    }

    fun deleteAll() {
        val db = mDatabaseManager.writableDatabase
        db.delete(DatabaseManager.TABLE_NAME, null, null)
        db.close()
    }

}