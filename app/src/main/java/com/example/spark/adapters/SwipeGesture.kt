package com.example.spark.adapters

import android.content.Context
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.spark.R
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

abstract class SwipeGesture(
	context: Context
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

	private val deleteColor = ContextCompat.getColor(context, R.color.delete)
	private val deleteIcon = R.drawable.baseline_delete_24

	override fun onMove(
		recyclerView: RecyclerView,
		viewHolder: RecyclerView.ViewHolder,
		target: RecyclerView.ViewHolder
	): Boolean = false

	override fun onChildDraw(
		c: Canvas,
		recyclerView: RecyclerView,
		viewHolder: RecyclerView.ViewHolder,
		dX: Float,
		dY: Float,
		actionState: Int,
		isCurrentlyActive: Boolean
	) {
		RecyclerViewSwipeDecorator.Builder(
			c,
			recyclerView,
			viewHolder,
			dX,
			dY,
			actionState,
			isCurrentlyActive
		).addSwipeRightBackgroundColor(deleteColor)
			.addSwipeRightActionIcon(deleteIcon)
			.create()
			.decorate()

		super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
	}
}