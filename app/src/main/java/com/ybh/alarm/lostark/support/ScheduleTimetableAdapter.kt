package com.ybh.lostark.islandtimer.support

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.ybh.lostark.islandtimer.R
import com.ybh.lostark.islandtimer.interfaces.OnItemClickListener
import kotlinx.android.synthetic.main.schedule_timetable_item.view.*

class ScheduleTimetableAdapter(private val context: Context, private val timetable: Array<String>, private val selected: IntArray? = null): RecyclerView.Adapter<ScheduleTimetableAdapter.ViewHolder>() {

    private var mListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.schedule_timetable_item, parent, false))
    }

    override fun getItemCount(): Int = timetable.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(selected != null) {
            if(selected[position] == 1) {
                holder.itemView.isEnabled = true
                holder.time.isSelected = true
            }
        }
        holder.time.text = timetable[position]
        holder.time.setOnClickListener {
            it.isSelected = !it.isSelected
            mListener?.onItemClick(holder.itemView, position)
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mListener = listener
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var time: Button = view.schedule_time
    }
}