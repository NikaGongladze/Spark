package com.example.spark.fragments

import android.content.Context
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.edit
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.spark.adapters.HabitRecyclerViewAdapter
import com.example.spark.adapters.SwipeGesture
import com.example.spark.databinding.FragmentBadHabitsBinding
import com.example.spark.dialogs.CreateHabitDialogFragment
import com.example.spark.dialogs.OpenHabitDialogFragment
import com.example.spark.model.Habit
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class BadHabits : Fragment() {

	private var _binding: FragmentBadHabitsBinding? = null
	private val binding get() = _binding!!

	private val habitList = arrayListOf<Habit>()
	private val dbRef = Firebase.database.getReference()

	private lateinit var habitAdapter: HabitRecyclerViewAdapter

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = FragmentBadHabitsBinding.inflate(layoutInflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		val calendar = Calendar.getInstance()
		val today =
			("${calendar.get(Calendar.YEAR)}" + "${calendar.get(Calendar.DAY_OF_YEAR)}").toInt()
		val sharedPrefs = context?.getSharedPreferences("Shared", Context.MODE_PRIVATE)

		val swipeGesture = object : SwipeGesture(requireContext()) {
			override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
				when (direction) {
					ItemTouchHelper.RIGHT -> {
						val habitObjectToRemove = habitAdapter
							.getItem(viewHolder.bindingAdapterPosition)
						sharedPrefs?.edit(commit = true) {
							remove("today day: ${habitObjectToRemove.habitName}")
						}
						habitAdapter.deleteItem(viewHolder.bindingAdapterPosition)

						habitObjectToRemove.habitName?.let {
							dbRef.child("Habits")
								.child(it)
								.removeValue()
						}
					}
				}
			}
		}

		binding.apply {
			val touchHelper = ItemTouchHelper(swipeGesture)
			touchHelper.attachToRecyclerView(rvHabit)

			habitAdapter = HabitRecyclerViewAdapter(habitList)

			rvHabit.adapter = habitAdapter
			rvHabit.layoutManager = LinearLayoutManager(requireContext())

			dbRef.child("Habits").addValueEventListener(object : ValueEventListener {
				override fun onDataChange(snapshot: DataSnapshot) {
					habitList.clear()
					for (postSnapshot in snapshot.children) {
						val habitObject = postSnapshot.getValue(Habit::class.java)

						if (habitObject != null) {
							if (today - sharedPrefs?.getInt(
									"today day: ${habitObject.habitName}", today
								)!! >= 2 && habitObject.habitProgressNow != habitObject.habitProgress
							) {
								habitObject.status = "inactive"

								habitObject.habitName?.let {
									dbRef.child("Habits")
										.child(it)
										.child("status")
										.setValue("inactive")
								}
							}
						}
						habitList.add(habitObject!!)
					}
					habitAdapter.notifyDataSetChanged()
				}

				override fun onCancelled(error: DatabaseError) {
				}
			})

			createHabitButton.setOnClickListener { showDialog(CreateHabitDialogFragment()) }
			habitAdapter.setOnItemClickListener(object :
				HabitRecyclerViewAdapter.OnItemClickListener {
				override fun onItemClick(position: Int): String {
					showDialog(OpenHabitDialogFragment(position, habitList))
					return position.toString()
				}
			})
		}
	}

	fun showDialog(dialogFragment: DialogFragment) {
		val fragmentManager = parentFragmentManager
		val transaction = fragmentManager.beginTransaction()

		transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
		transaction.add(android.R.id.content, dialogFragment)
			.addToBackStack(null)
			.commit()
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}