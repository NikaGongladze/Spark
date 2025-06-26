package com.example.spark.adapters

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.example.spark.databinding.ItemHabitBinding
import com.example.spark.model.Habit

class HabitRecyclerViewAdapter(
	private val habitList: ArrayList<Habit>
) : RecyclerView.Adapter<HabitRecyclerViewAdapter.HabitViewHolder>() {

	inner class HabitViewHolder(val binding: ItemHabitBinding) :
		RecyclerView.ViewHolder(binding.root)

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
		val view = ItemHabitBinding.inflate(
			LayoutInflater.from(parent.context),
			parent,
			false
		)
		return HabitViewHolder(view)
	}

	override fun getItemCount(): Int = habitList.size

	override fun onBindViewHolder(holder: HabitViewHolder, position: Int) = with(holder.binding) {
		val currentHabit = habitList[position]

		habitName.text = currentHabit.habitName
		habitProgress.text = currentHabit.habitProgressNow + "/" + currentHabit.habitProgress
		activeStatus.text = currentHabit.status

		val param = status.layoutParams as MarginLayoutParams

		if (activeStatus.text.toString() == "completed")
			param.setMargins(0, 0, 100, 0)
		else if (activeStatus.text.toString() == "inactive")
			param.setMargins(0, 0, 60, 0)
		else
			param.setMargins(0, 0, 0, 0)

		status.layoutParams = param

		habitProgressBar.max = currentHabit.habitProgress.toInt() * 1000
		ObjectAnimator.ofInt(
			habitProgressBar,
			"progress",
			currentHabit.habitProgressNow.toInt() * 1000
		).setDuration(1400)
			.start()

		daysRemaining.text =
			"Days remaining:" + (currentHabit.habitProgress.toInt() - currentHabit.habitProgressNow.toInt())
	}

	fun getItem(position: Int): Habit = habitList[position]

	fun deleteItem(position: Int) {
		habitList.removeAt(position)
		notifyDataSetChanged()
	}
}