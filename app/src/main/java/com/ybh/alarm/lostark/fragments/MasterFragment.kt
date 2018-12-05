package com.ybh.lostark.islandtimer.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ybh.lostark.islandtimer.R
import com.ybh.lostark.islandtimer.etc.Island
import com.ybh.lostark.islandtimer.etc.TimerItem
import com.ybh.lostark.islandtimer.models.MainViewModel
import com.ybh.lostark.islandtimer.support.MasterIslandListAdapter
import com.ybh.lostark.islandtimer.utils.AlarmController
import com.ybh.lostark.islandtimer.utils.DatabaseCursor
import com.ybh.lostark.islandtimer.utils.MediaCursor
import kotlinx.android.synthetic.main.fragment_master.*
import java.util.*
import kotlin.Comparator

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
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = MasterFragment()

        const val SORT_BY_KEY = "SORT_BY"
        const val SORT_BY_ALPHABET = 0
        const val SORT_BY_ENABLED = 1
    }
}
