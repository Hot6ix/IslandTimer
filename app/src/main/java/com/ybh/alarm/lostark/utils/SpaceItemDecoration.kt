package com.ybh.alarm.lostark.utils

import android.content.Context
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpaceItemDecoration(private val context: Context, private val dp: Int): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val dpVal = ((dp * context.resources.displayMetrics.density) + 0.5f).toInt()

        outRect.set(dpVal, dpVal, dpVal, dpVal)
    }

}