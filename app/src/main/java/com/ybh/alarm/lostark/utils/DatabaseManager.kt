package com.ybh.lostark.islandtimer.utils

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseManager(context: Context): SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_ISLAND TEXT NOT NULL," +
                "$COLUMN_SCHEDULE TEXT," +
                "$COLUMN_NOTIFICATION INTEGER," +
                "$COLUMN_SWITCH INTEGER);")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    companion object {
        const val DB_NAME = "timers.db"
        const val DB_VERSION = 1

        const val TABLE_NAME = "timer"
        const val COLUMN_ID = "id"
        const val COLUMN_ISLAND = "island"
        const val COLUMN_SCHEDULE = "schedule"
        const val COLUMN_NOTIFICATION = "notification"
        const val COLUMN_SWITCH = "switch"
    }
}