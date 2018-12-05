package com.ybh.lostark.islandtimer.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ybh.lostark.islandtimer.R
import com.ybh.lostark.islandtimer.etc.TimerItem
import com.ybh.lostark.islandtimer.models.MainViewModel
import com.ybh.lostark.islandtimer.support.DetailScheduleAdapter
import com.ybh.lostark.islandtimer.utils.AlarmController
import com.ybh.lostark.islandtimer.utils.DatabaseCursor
import com.ybh.lostark.islandtimer.utils.MediaCursor
import kotlinx.android.synthetic.main.fragment_detail.*


class DetailFragment : androidx.fragment.app.Fragment(), DetailScheduleAdapter.OnItemClickListener,
    View.OnClickListener {

    private lateinit var mScheduleAdapter: DetailScheduleAdapter
    private lateinit var mDatabaseCursor: DatabaseCursor
    private lateinit var mSelectedItem: MutableLiveData<TimerItem>
    private lateinit var mMainModel: MainViewModel

    private var mIsEditMode = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)

        mDatabaseCursor = DatabaseCursor(context!!)

        // get main model
        mMainModel = activity?.run {
            ViewModelProviders.of(this).get(MainViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        // set item
        mSelectedItem = mMainModel.getSelectedItem()

        mIsEditMode = !mSelectedItem.value!!.isEmptyItem()

        mSelectedItem.observe(this, Observer {
            mMainModel.setTimetable(context!!, it.island)
            (activity as AppCompatActivity).run {
                supportActionBar?.title = MediaCursor.getIslandName(context!!, it.island)
            }

            val schedule = mSelectedItem.value!!.schedule
            if(schedule.isEmpty()) detail_empty_timetable.visibility = View.VISIBLE
            mScheduleAdapter = DetailScheduleAdapter(context!!, mMainModel.getTimetable(), schedule)
            mScheduleAdapter.setOnItemClickListener(this)

            detail_timetable.apply {
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                adapter = mScheduleAdapter
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            }

            // set notification time
            if(it.notification < 0) {
                detail_notification_spinner.setSelection(2) // 10 min
                it.notification = 2 // 10 min
            }
            else detail_notification_spinner.setSelection(it.notification)
        })

        // init notification time spinner
        val notificationAdapter = ArrayAdapter.createFromResource(context!!, R.array.notification_time_text, R.layout.detail_notification_spinner)
        notificationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        detail_notification_spinner.adapter = notificationAdapter
        detail_notification_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mSelectedItem.value!!.notification = position
            }
        }

        detail_schedule_add.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()

        mScheduleAdapter.notifyDataSetChanged()
        if(mSelectedItem.value!!.schedule.isNotEmpty() && detail_empty_timetable.visibility == View.VISIBLE) {
            detail_empty_timetable.visibility = View.GONE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_detail, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_detail_save -> {
                val selected = mSelectedItem.value!!
                if(selected.schedule.isEmpty()) {
                    // schedule is empty
                    Toast.makeText(context, getString(R.string.empty_schedule), Toast.LENGTH_SHORT).show()
                }
                else {
                    selected.switch = true
                    if(mIsEditMode) {
                        AlarmController.cancelAlarm(context!!, selected)
                        mDatabaseCursor.updateTimer(selected)
                    }
                    else {
                        mDatabaseCursor.addTimer(selected)
                    }
                    AlarmController.scheduleAlarm(context!!, selected)
                    activity?.run {
                        supportFragmentManager.popBackStack()
                    }
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onClick(view: View) {
        when(view.id) {
            R.id.detail_schedule_add -> {
                var merged = 0
                mSelectedItem.value!!.schedule.forEach { item ->
                    merged += item.days.filter { i -> i == 1 }.count()
                }
                if(merged < 7) {
                    activity?.run {
                        mMainModel.setSelectedScheduleIndex(-1)
                        supportFragmentManager
                            .beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                            .replace(R.id.fragment_container, ScheduleFragment())
                            .addToBackStack(null)
                            .commit()
                    }
                }
                else {
                    Toast.makeText(context, getString(R.string.full_schedule), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onItemClick(view: View, index: Int) {
        when(view.id) {
            R.id.detail_schedule_remove -> {
                // click to remove
                if(mSelectedItem.value!!.schedule.isEmpty()) {
                    detail_empty_timetable.visibility = View.VISIBLE
                }
            }
            R.id.schedule_layout -> {
                activity?.run {
                    mMainModel.setSelectedScheduleIndex(index)
                    supportFragmentManager
                        .beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.fragment_container, ScheduleFragment())
                        .addToBackStack(null)
                        .commit()
                }
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = DetailFragment()
    }
}
