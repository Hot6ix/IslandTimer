package com.ybh.lostark.islandtimer.support

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayout
import com.ybh.lostark.islandtimer.R
import com.ybh.lostark.islandtimer.etc.ScheduleItem
import com.ybh.lostark.islandtimer.utils.MediaCursor
import kotlinx.android.synthetic.main.detail_schedule_item.view.*

class DetailScheduleAdapter(context: Context, private val timetable: Array<String>?, private val list: ArrayList<ScheduleItem>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mListener: OnItemClickListener? = null
    private var mDays = context.resources.getStringArray(R.array.day_of_week)
    private val mInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(mInflater.inflate(R.layout.detail_schedule_item, parent, false))
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val viewHolder = holder as ViewHolder

        if(holder.scheduleDays.childCount > 0) holder.scheduleDays.removeAllViews()

        list[position].days
            .mapIndexed { index, i -> if(i == 1) mDays[index] else null }
            .filterNotNull()
            .forEach {
                val view = mInflater.inflate(R.layout.chip_item, null, false)
                view.findViewById<TextView>(R.id.chip_item).apply {
                    text = it
                    (this.background as GradientDrawable).setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                }
                holder.scheduleDays.addView(view)
            }

        if(timetable != null) {
            if(holder.scheduleTimetable.childCount > 0) holder.scheduleTimetable.removeAllViews()

            list[position].timetable
                .mapIndexed { index, i ->
                    if (i == 1) timetable[index]
                    else null
                }
                .filterNotNull()
                .forEach {
                    val view = mInflater.inflate(R.layout.chip_item, null, false)
                    view.findViewById<TextView>(R.id.chip_item).apply {
                        text = it
                        (this.background as GradientDrawable).setColor(ContextCompat.getColor(context, R.color.colorAccent))
                    }

                    holder.scheduleTimetable.addView(view)
                }
        }

        viewHolder.scheduleRemove.setOnClickListener {
            list.removeAt(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)
            notifyItemRangeChanged(holder.adapterPosition, itemCount)
            mListener?.onItemClick(it, holder.adapterPosition)
        }

        viewHolder.itemView.setOnClickListener {
            mListener?.onItemClick(it, holder.adapterPosition)
        }
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, index: Int)
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var scheduleDays: FlexboxLayout = view.detail_schedule_days_layout
        var scheduleTimetable: FlexboxLayout = view.detail_schedule_timetable_layout
        var scheduleRemove: ImageButton = view.detail_schedule_remove
    }
}