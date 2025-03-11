package com.example.weather


import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView


class RecyclerViewItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.right = space / 4  // ✅ Reduce spacing on the right
        outRect.left = space / 4   // ✅ Reduce spacing on the left
    }
}