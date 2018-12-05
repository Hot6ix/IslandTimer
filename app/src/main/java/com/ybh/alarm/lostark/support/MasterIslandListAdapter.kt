package com.ybh.lostark.islandtimer.support

import android.content.Context
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ybh.lostark.islandtimer.R
import com.ybh.lostark.islandtimer.etc.Island
import com.ybh.lostark.islandtimer.etc.TimerItem
import com.ybh.lostark.islandtimer.fragments.MasterFragment
import com.ybh.lostark.islandtimer.utils.MediaCursor
import kotlinx.android.synthetic.main.master_timer_list_item.view.*

class MasterIslandListAdapter(private val context: Context, private var list: ArrayList<TimerItem>): RecyclerView.Adapter<MasterIslandListAdapter.ViewHolder>() {

    private var mListener: OnListInteractionListener? = null
    private var islands = context.resources.getStringArray(R.array.island_list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.master_timer_list_item, parent, false))
    }

    override fun getItemCount(): Int = islands.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val islandName = islands[holder.adapterPosition]
        val island = MediaCursor.getIslandByName(context, islandName)

        val icon = MediaCursor.getIconResource(island)
        holder.icon.setImageResource(icon)

        holder.island.text = islandName
        holder.itemView.setOnClickListener {
            mListener?.onItemClick(holder.itemView, island)
        }

        val timerItem = list.find { it.island == island }
        if(timerItem != null) {
            val isEnabled = timerItem.switch
            holder.switch.isChecked = isEnabled
            changeView(holder, isEnabled)

            holder.switch.setOnCheckedChangeListener { view, bool ->
                // fix bug when using onCheckedChangeListener in RecyclerView adapter
                if(view.isPressed) {
                    changeView(holder, bool)
                    mListener?.onItemCheckedChange(timerItem.island, bool)
                }
            }
        }
        else {
            // Island not set
            changeView(holder, false)
            holder.switch.isEnabled = false
        }
    }

    fun setOnItemClickListener(listener: OnListInteractionListener) {
        this.mListener = listener
    }

    private fun changeView(holder: ViewHolder, enabled: Boolean) {
        if(enabled) {
            holder.island.setTextColor(Color.BLACK)
            holder.icon.colorFilter = null
        }
        else {
            holder.island.setTextColor(Color.GRAY)

            // make icon black and white
            val matrix = ColorMatrix()
            matrix.setSaturation(0f)
            val filter = ColorMatrixColorFilter(matrix)
            holder.icon.colorFilter = filter
        }
    }

    fun updateList(sortBy: Int) {
        when(sortBy) {
            MasterFragment.SORT_BY_ALPHABET -> {
                islands.sortBy { it }
            }
            MasterFragment.SORT_BY_ENABLED -> {
                islands.sortByDescending { islandName ->
                    list.first { MediaCursor.getIslandName(context, it.island) == islandName  }.switch
                }
            }
        }
        notifyItemRangeChanged(0, islands.size)
    }

    interface OnListInteractionListener {
        fun onItemClick(view: View, island: Island)
        fun onItemCheckedChange(island: Island, bool: Boolean)
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var island: TextView = view.master_timer_island
        var icon : ImageView = view.master_timer_island_icon
        var switch: Switch = view.master_timer_switch
    }
}