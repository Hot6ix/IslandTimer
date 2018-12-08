package com.ybh.alarm.lostark.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ybh.alarm.lostark.R
import com.ybh.alarm.lostark.etc.Island
import com.ybh.alarm.lostark.etc.TimerItem
import com.ybh.alarm.lostark.models.MainViewModel
import com.ybh.alarm.lostark.support.MasterIslandListAdapter
import com.ybh.alarm.lostark.utils.AlarmController
import com.ybh.alarm.lostark.utils.DatabaseCursor
import kotlinx.android.synthetic.main.fragment_master.*
import java.util.*

class MasterFragment : androidx.fragment.app.Fragment(), MasterIslandListAdapter.OnListInteractionListener {

    private lateinit var mMainModel: MainViewModel
    private lateinit var mDatabaseCursor: DatabaseCursor
    private lateinit var mTimerList: ArrayList<TimerItem>
    private lateinit var mTimerListAdapter: MasterIslandListAdapter
    private lateinit var mPreference: SharedPreferences

    private var mDetailFragment = DetailFragment.newInstance()
    private var mSortBy: Int = SORT_BY_ALPHABET

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_master, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)

        mDatabaseCursor = DatabaseCursor(context!!)
        mTimerList = mDatabaseCursor.getTimers()

        // sort
        mPreference = PreferenceManager.getDefaultSharedPreferences(context)
        mSortBy = mPreference.getInt(SORT_BY_KEY, SORT_BY_ALPHABET)

        mTimerListAdapter = MasterIslandListAdapter(context!!, mTimerList)
        mTimerListAdapter.setOnItemClickListener(this)
        master_timer_list.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        master_timer_list.adapter = mTimerListAdapter

        mMainModel = activity?.run {
            ViewModelProviders.of(this).get(MainViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
    }

    override fun onResume() {
        super.onResume()

        mTimerListAdapter.updateList(mSortBy)
        (activity as AppCompatActivity).run {
            supportActionBar?.title = getString(R.string.app_name)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_master, menu)
        if(mSortBy == SORT_BY_ALPHABET) {
            menu.findItem(R.id.action_master_sort_by_name).isChecked = true
        }
        else {
            menu.findItem(R.id.action_master_sort_by_enabled).isChecked = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.isChecked = true
        return when(item.itemId) {
            R.id.action_master_sort_by_name -> {
                mSortBy = SORT_BY_ALPHABET
                mTimerListAdapter.updateList(mSortBy)

                mPreference
                    .edit()
                    .putInt(SORT_BY_KEY,  mSortBy)
                    .apply()
                true
            }
            R.id.action_master_sort_by_enabled -> {
                mSortBy = SORT_BY_ENABLED
                mTimerListAdapter.updateList(mSortBy)

                mPreference
                    .edit()
                    .putInt(SORT_BY_KEY,  mSortBy)
                    .apply()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    override fun onItemClick(view: View, island: Island) {
        val item = mTimerList.find { it.island == island }
        if(item != null) {
            // timer exists
            mMainModel.setSelectedItem(item)
        }
        else {
            // timer not exist
            mMainModel.setSelectedItem(TimerItem(island = island))
        }
        activity?.run {
            supportFragmentManager
                .beginTransaction()
                .addToBackStack(null)
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container, mDetailFragment)
                .commit()
        }
    }

    override fun onItemCheckedChange(island: Island, bool: Boolean) {
        val item = mTimerList.find { it.island == island }

        item?.apply {
            switch = bool
            mDatabaseCursor.updateTimer(item)

            if(bool) {
                AlarmController.scheduleAlarm(context!!, item)
                Toast.makeText(context, resources.getString(R.string.alarm_set_message), Toast.LENGTH_SHORT).show()
            }
            else {
                AlarmController.cancelAlarm(context!!, item)
                Toast.makeText(context, resources.getString(R.string.alarm_cancel_message), Toast.LENGTH_SHORT).show()
            }
        }

        if(mSortBy == SORT_BY_ENABLED)
            mTimerListAdapter.updateList(mSortBy)
    }

    companion object {
        @JvmStatic
        fun newInstance() = MasterFragment()

        const val SORT_BY_KEY = "SORT_BY"
        const val SORT_BY_ALPHABET = 0
        const val SORT_BY_ENABLED = 1
    }
}
