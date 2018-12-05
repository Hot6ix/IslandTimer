package com.ybh.lostark.islandtimer.fragments


import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.ybh.lostark.islandtimer.R
import com.ybh.lostark.islandtimer.etc.ScheduleItem
import com.ybh.lostark.islandtimer.etc.TimerItem
import com.ybh.lostark.islandtimer.interfaces.OnItemClickListener
import com.ybh.lostark.islandtimer.models.MainViewModel
import com.ybh.lostark.islandtimer.support.ScheduleDaysAdapter
import com.ybh.lostark.islandtimer.support.ScheduleTimetableAdapter
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

        // init days and timetable
        mSelectedDays = IntArray(7)
        mSelectedTime = IntArray(mTimetable!!.size)

        // get schedule list
        mSelectedItem = mMainModel.getSelectedItem()
        schedule_title.text = getString(R.string.schedule_dialog_title_add)

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
        }

        // get selected island
        val selected = mMainModel.getSelectedItem()

        // change timetable if island change
        selected.observe(this, Observer {
            mTimetableAdapter = ScheduleTimetableAdapter(context!!, mTimetable!!, scheduleItem?.timetable)
            mTimetableAdapter.setOnItemClickListener(this)
            schedule_timetable.layoutManager = GridLayoutManager(context, 4)
            schedule_timetable.adapter = mTimetableAdapter
        })

        // prevent to set duplicate days
        val used = IntArray(7)
        mSelectedItem.value!!.schedule.forEach {
            it.days.forEachIndexed { index, i ->
                if(i == 1) used[index] = i
            }
        }

        mDaysAdapter = ScheduleDaysAdapter(context!!, used, scheduleItem?.days)
        mDaysAdapter.setOnItemClickListener(this)
        schedule_days.layoutManager = GridLayoutManager(context, 7)
        schedule_days.adapter = mDaysAdapter
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
    }
}
