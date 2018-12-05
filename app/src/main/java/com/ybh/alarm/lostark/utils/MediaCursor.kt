package com.ybh.lostark.islandtimer.utils

import android.content.Context
import android.util.Log
import com.ybh.lostark.islandtimer.R
import com.ybh.lostark.islandtimer.etc.Island
import com.ybh.lostark.islandtimer.etc.ScheduleItem
import java.util.ArrayList

object MediaCursor {

    fun getIslandName(context: Context, island: Island): String {
        val list = context.resources.getStringArray(R.array.island_list)
        return when(island) {
            Island.DOOKI -> list[0]
            Island.NEW_MOON -> list[1]
            Island.SPEEDA -> list[2]
            Island.SLEEPING_SONG -> list[3]
            Island.HALLUCINATION -> list[4]
            Island.NONE -> "Unknown"
        }
    }

    fun getIslandByName(context: Context, name: String): Island {
        val list = context.resources.getStringArray(R.array.island_list)
        return when(name) {
            list[0] -> Island.DOOKI
            list[1] -> Island.NEW_MOON
            list[2] -> Island.SPEEDA
            list[3] -> Island.SLEEPING_SONG
            list[4] -> Island.HALLUCINATION
            else -> Island.NONE
        }
    }

    fun getIslandSchedule(context: Context, island: Island): Array<String> {
        return when(island) {
            Island.DOOKI -> context.resources.getStringArray(R.array.dooki)
            Island.SLEEPING_SONG -> context.resources.getStringArray(R.array.sleeping_song)
            Island.SPEEDA -> context.resources.getStringArray(R.array.speeda)
            Island.HALLUCINATION -> context.resources.getStringArray(R.array.hallucination)
            Island.NEW_MOON -> context.resources.getStringArray(R.array.new_moon)
            Island.NONE -> throw Exception("Wrong island")
        }
    }

    fun getIslandInterval(context: Context, island: Island): Long {
        val intervals = context.resources.getIntArray(R.array.island_interval)
        return when(island) {
            Island.DOOKI -> intervals[0].toLong()
            Island.SLEEPING_SONG -> intervals[1].toLong()
            Island.SPEEDA -> intervals[2].toLong()
            Island.HALLUCINATION -> intervals[3].toLong()
            Island.NEW_MOON -> intervals[4].toLong()
            Island.NONE -> throw Exception("Wrong island")
        }
    }

    fun getIconResource(island: Island): Int {
        return when(island) {
            Island.DOOKI -> R.drawable.dooki
            Island.NEW_MOON -> R.drawable.new_moon
            Island.SLEEPING_SONG -> R.drawable.sleeping_song
            Island.SPEEDA -> R.drawable.speeda
            Island.HALLUCINATION -> R.drawable.hallucination
            Island.NONE -> throw Exception("Wrong island")
        }
    }

    fun scheduleToString(schedule: ArrayList<ScheduleItem>): String {
        return schedule.joinToString("||")
    }

    fun stringToSchedule(schedule: String): ArrayList<ScheduleItem> {
        val list = ArrayList<ScheduleItem>()

        schedule.split("||").forEach {
            // Single schedule item
            var days: IntArray? = null
            var time: IntArray? = null
            it.split("|").forEachIndexed { index, s ->
                val result = s.removePrefix("[").removeSuffix("]").split(",").mapIndexed { _, b -> b.trim().toInt() }.toIntArray()
                if(index == 0) {
                    // Days
                    days = result
                }
                else {
                    // Time
                    time = result
                }
            }

            list.add(ScheduleItem(days!!, time!!))
        }

        return list
    }

}