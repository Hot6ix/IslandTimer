package com.ybh.lostark.islandtimer.models

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ybh.lostark.islandtimer.etc.Island
import com.ybh.lostark.islandtimer.etc.TimerItem
import com.ybh.lostark.islandtimer.utils.MediaCursor

class MainViewModel: ViewModel() {

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

    fun clearNewItem() {
        selectedScheduleIndex = -1
        selectedItem.value = null
    }
}