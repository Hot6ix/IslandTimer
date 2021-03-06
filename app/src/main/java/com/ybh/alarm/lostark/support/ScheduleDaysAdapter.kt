package com.ybh.alarm.lostark.support

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.ybh.alarm.lostark.R
import com.ybh.alarm.lostark.interfaces.OnItemClickListener
import kotlinx.android.synthetic.main.schedule_days_item.view.*

class ScheduleDaysAdapter(private val context: Context, private val used: IntArray? = null, private val selected: IntArray? = null): RecyclerView.Adapter<ScheduleDaysAdapter.ViewHolder>() {

    private val days = context.resources.getStringArray(R.array.day_of_week)
    private var mListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleDaysAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.schedule_days_item, parent, false))
    }

    override fun getItemCount(): Int = days.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if(used != null) {
            holder.itemView.isEnabled = used[holder.adapterPosition] != 1
        }
        if(selected != null) {
            if(selected[holder.adapterPosition] == 1) {
                holder.itemView.isEnabled = true
                holder.day.isSelected = true
            }
            else {
                holder.day.isSelected = false
            }
        }
        holder.day.text = days[position]
        holder.day.setOnClickListener {
            it.isSelected = !it.isSelected
            mListener?.onItemClick(holder.itemView, position)
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.mListener = listener
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var day: Button = view.schedule_day
    }
}