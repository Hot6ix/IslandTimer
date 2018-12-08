package com.ybh.alarm.lostark.models

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ybh.alarm.lostark.etc.Island
import com.ybh.alarm.lostark.etc.TimerItem
import com.ybh.alarm.lostark.utils.MediaCursor

class MainViewModel: ViewModel() {

    // Master <-> Detail
    private var selectedItem = MutableLiveData<TimerItem>()
    private var selectedScheduleIndex: Int? = null
    private var timetable: Array<String>? = null

    fun getSelectedItem(): MutableLiveData<TimerItem> {
        return selectedItem
    }

    fun setSelectedItem(item: TimerItem) {
        selectedItem.value = item
    }

    fun getTimetable(): Array<String>? = timetable

    fun setTimetable(context: Context, island: Island) {
        timetable = MediaCursor.getIslandSchedule(context, island)
    }

    fun getSelectedScheduleIndex(): Int? {
        return selectedScheduleIndex
    }

    fun setSelectedScheduleIndex(index: Int) {
        this.selectedScheduleIndex = index
    }
}