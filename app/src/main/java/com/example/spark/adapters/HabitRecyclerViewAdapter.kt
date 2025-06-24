package com.example.spark.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.spark.databinding.ItemHabitBinding
import com.example.spark.model.Habit

class HabitRecyclerViewAdapter(
	private val habitList: ArrayList<Habit>
) : RecyclerView.Adapter<HabitRecyclerViewAdapter.HabitViewHolder>() {

	inner class HabitViewHolder(binding: ItemHabitBinding) : RecyclerView.ViewHolder(binding.root)

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
		val view = ItemHabitBinding.inflate(
			LayoutInflater.from(parent.context),
			parent,
			false
		)
		return HabitViewHolder(view)
	}

	override fun getItemCount(): Int = habitList.size

	override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
		TODO("Not yet implemented")
	}

	fun getItem(position: Int): Habit = habitList[position]

	fun deleteItem(position: Int) {
		habitList.removeAt(position)
		notifyDataSetChanged()
	}
}