package com.ybh.alarm.lostark.fragments


import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.ybh.alarm.lostark.R
import com.ybh.alarm.lostark.etc.ScheduleItem
import com.ybh.alarm.lostark.etc.TimerItem
import com.ybh.alarm.lostark.interfaces.OnItemClickListener
import com.ybh.alarm.lostark.models.MainViewModel
import com.ybh.alarm.lostark.support.ScheduleDaysAdapter
import com.ybh.alarm.lostark.support.ScheduleTimetableAdapter
import com.ybh.alarm.lostark.utils.SpaceItemDecoration
import kotlinx.android.synthetic.main.fragment_schedule.*

class ScheduleFragment : Fragment(), OnItemClickListener {

    private lateinit var mTimetableAdapter: ScheduleTimetableAdapter
    private lateinit var mDaysAdapter: ScheduleDaysAdapter
    private lateinit var mSelectedDays: IntArray
    private lateinit var mSelectedTime: IntArray
    private lateinit var mMainModel: MainViewModel
    private lateinit var mSelectedItem: MutableLiveData<TimerItem>

    private var mTimetable: Array<String>? = null
    private var mIsEditMode = false
    private var mIsSelectedAll = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_schedule, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)

        // get main model
        mMainModel = activity?.run {
            ViewModelProviders.of(this).get(MainViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        // get island timetable
        mTimetable = mMainModel.getTimetable()

        // get schedule list
        mSelectedItem = mMainModel.getSelectedItem()
        schedule_title.text = getString(R.string.schedule_dialog_title_add)

        mSelectedDays = IntArray(7)
        mSelectedTime = IntArray(mTimetable!!.size)

        // get schedule item when user click a schedule
        // if selectedScheduleIndex is not null, enable edit mode and default selected values
        val selectedScheduleIndex = mMainModel.getSelectedScheduleIndex()
        var scheduleItem: ScheduleItem? = null
        if(selectedScheduleIndex != null && selectedScheduleIndex > -1) {
            scheduleItem = mSelectedItem.value!!.schedule[selectedScheduleIndex]
            mSelectedDays = scheduleItem.days.copyOf()
            mSelectedTime = scheduleItem.timetable.copyOf()
            mIsEditMode = true
            schedule_title.text = getString(R.string.schedule_dialog_title_edit)
            if(!scheduleItem.days.contains(0)) mIsSelectedAll = true
        }

        // init days and timetable
        if(savedInstanceState != null) {
            mSelectedDays = savedInstanceState.getIntArray(KEY_DAYS)!!
            mSelectedTime = savedInstanceState.getIntArray(KEY_TIMETABLE)!!
        }

        // get selected island
        val selected = mMainModel.getSelectedItem()

        // change timetable if island change
        selected.observe(this, Observer {
            mTimetableAdapter = ScheduleTimetableAdapter(context!!, mTimetable!!, mSelectedTime)
            mTimetableAdapter.setOnItemClickListener(this)

            schedule_timetable.layoutManager = GridLayoutManager(context, 4)
            schedule_timetable.adapter = mTimetableAdapter
            schedule_timetable.addItemDecoration(SpaceItemDecoration(context!!, 2))
        })

        // prevent to set duplicate days
        val used = IntArray(7)
        mSelectedItem.value!!.schedule.forEach {
            if(scheduleItem != it) {
                it.days.forEachIndexed { index, i ->
                    if(i == 1) used[index] = i
                }
            }
        }

        mDaysAdapter = ScheduleDaysAdapter(context!!, used, mSelectedDays)
        mDaysAdapter.setOnItemClickListener(this)
        schedule_days.layoutManager = GridLayoutManager(context, 7)
        schedule_days.adapter = mDaysAdapter
        schedule_days.addItemDecoration(SpaceItemDecoration(context!!, 2))

        schedule_select_all.setOnClickListener {
            for(i in 0 until mSelectedDays.size) {
                if(used[i] != 1) {
                    if(mIsSelectedAll) mSelectedDays[i] = 0 // deselect all
                    else mSelectedDays[i] = 1 // select all available days
                }
            }
            for(i in 0 until mSelectedTime.size) {
                if(mIsSelectedAll) mSelectedTime[i] = 0
                else mSelectedTime[i] = 1
            }
            mIsSelectedAll = !mIsSelectedAll
            mDaysAdapter.notifyItemRangeChanged(0, mSelectedDays.size)
            mTimetableAdapter.notifyItemRangeChanged(0, mSelectedTime.size)
        }
        schedule_select_all.setOnLongClickListener {
            Toast.makeText(context, getString(R.string.select_all), Toast.LENGTH_SHORT).show()
            true
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putIntArray(KEY_DAYS, mSelectedDays)
        outState.putIntArray(KEY_TIMETABLE, mSelectedTime)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_schedule, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_schedule_save -> {
                if(mSelectedDays.contains(1) && mSelectedTime.contains(1)) {
                    if(mIsEditMode) {
                        mSelectedItem.value!!.schedule[mMainModel.getSelectedScheduleIndex()!!] = ScheduleItem(mSelectedDays, mSelectedTime)
                    }
                    else {
                        mSelectedItem.value!!.schedule.add(ScheduleItem(mSelectedDays, mSelectedTime))
                    }
                    activity?.run {
                        supportFragmentManager.popBackStack()
                    }
                }
                else Toast.makeText(context, getString(R.string.schedule_invalid), Toast.LENGTH_SHORT).show()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onItemClick(view: View, index: Int) {
        when(view.id) {
            R.id.schedule_day -> {
                mSelectedDays[index] =
                        if(mSelectedDays[index] > 0) 0
                        else 1
                mIsSelectedAll = mSelectedDays.find { it == 0 } == null
            }
            R.id.schedule_time -> {
                mSelectedTime[index] =
                        if(mSelectedTime[index] > 0) 0
                        else 1
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ScheduleFragment()

        const val KEY_DAYS = "KEY_DAYS"
        const val KEY_TIMETABLE = "KEY_TIMETABLE"
    }
}
