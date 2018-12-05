package com.ybh.lostark.islandtimer.etc

data class TimerItem(
    var id: Int? = null,
    var island: Island = Island.NONE,
    var schedule: ArrayList<ScheduleItem> = ArrayList(),
    var notification: Int = -1,
    var switch: Boolean = false) {

    override fun toString(): String {
        return "id = $id, island = $island, schedule = $schedule, notification = $notification, switch = $switch"
    }

    fun isEmptyItem(): Boolean {
        return id == null
    }
}