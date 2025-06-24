package com.example.spark.fragments

import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spark.adapters.HabitRecyclerViewAdapter
import com.example.spark.adapters.SwipeGesture
import com.example.spark.databinding.FragmentBadHabitsBinding
import com.example.spark.model.Habit

class BadHabits : Fragment() {

	private var _binding: FragmentBadHabitsBinding? = null
	private val binding get() = _binding!!

	private val habitList = arrayListOf<Habit>()

	private lateinit var habitAdapter: HabitRecyclerViewAdapter

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = FragmentBadHabitsBinding.inflate(layoutInflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
		super.onViewCreated(view, savedInstanceState)

		val calendar = Calendar.getInstance()
		val today =
			("${calendar.get(Calendar.YEAR)}" + "${calendar.get(Calendar.DAY_OF_YEAR)}").toInt()

		val swipeGesture = object : SwipeGesture(requireContext()) {
			override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
				when (direction) {
					ItemTouchHelper.RIGHT -> {
						val habitObjectToRemove = habitAdapter
							.getItem(viewHolder.bindingAdapterPosition)
						habitAdapter.deleteItem(viewHolder.bindingAdapterPosition)
						TODO("Add db logic later")
					}
				}
			}
		}

		val touchHelper = ItemTouchHelper(swipeGesture)
		touchHelper.attachToRecyclerView(rvHabit)

		habitAdapter = HabitRecyclerViewAdapter(habitList)

		rvHabit.adapter = habitAdapter
		rvHabit.layoutManager = LinearLayoutManager(requireContext())

		TODO("Add db logic later")
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}