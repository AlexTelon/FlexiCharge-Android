package com.flexicharge.bolt

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

public class SpacesItemDecoration(space: Int) : RecyclerView.ItemDecoration() {
    private var space = space

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = space
        outRect.right = space
        outRect.bottom = space
    }
}