package com.ybh.lostark.islandtimer.etc

data class ScheduleItem(var days: IntArray, var timetable: IntArray) {

    override fun toString(): String {
        return "${days.contentToString()}|${timetable.contentToString()}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ScheduleItem

        if (!days.contentEquals(other.days)) return false
        if (!timetable.contentEquals(other.timetable)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = days.contentHashCode()
        result = 31 * result + timetable.contentHashCode()
        return result
    }
}