package com.example.spark.dialogs

import android.animation.ObjectAnimator
import android.content.Context
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.DialogFragment
import com.example.spark.R
import com.example.spark.databinding.FragmentOpenHabitDialogBinding
import com.example.spark.model.Habit
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class OpenHabitDialogFragment(
	position: Int,
	habitList: ArrayList<Habit>
) : DialogFragment() {

	private var _binding: FragmentOpenHabitDialogBinding? = null
	private val binding get() = _binding!!

	private var habit = habitList[position]
	private val dbRef = Firebase.database.getReference()

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = FragmentOpenHabitDialogBinding.inflate(
			layoutInflater,
			container,
			false
		)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
		super.onViewCreated(view, savedInstanceState)

		habit.habitName?.let {
			dbRef.child("Habits")
				.child(it)
				.child("habitProgressNow")
				.addValueEventListener(object : ValueEventListener {
					override fun onDataChange(snapshot: DataSnapshot) {
						progressTV.text = snapshot.value.toString() + "/" + habit.habitProgress
						daysRemaining.text =
							"Days remaining: " + ((habit.habitProgress?.toInt()
								?: 0) - snapshot.value.toString()
								.toInt()).toString()

						circularProgressIndicator.max = (habit.habitProgress?.toInt() ?: 0) * 1000
						ObjectAnimator.ofInt(
							circularProgressIndicator,
							"progress",
							snapshot.value.toString().toInt() * 1000
						).setDuration(1700)
							.start()
					}

					override fun onCancelled(error: DatabaseError) {
					}
				})
		}

		habitName.text = habit.habitName
		progressTV.text = habit.habitProgressNow + "/" + habit.habitProgress
		statusTV.text = "Status: " + habit.status

		circularProgressIndicator.max = (habit.habitProgress?.toInt() ?: 0) * 1000
		ObjectAnimator.ofInt(
			circularProgressIndicator,
			"progress",
			(habit.habitProgressNow?.toInt() ?: 0) * 1000
		).setDuration(1700)
			.start()

		daysRemaining.text =
			"Days Remaining: " + ((habit.habitProgress?.toInt()
				?: 0) - habit.habitProgressNow?.toInt()!!)

		progressTV.setOnLongClickListener {
			if (statusTV.text.equals("Status: inactive")) {
				Toast.makeText(context, "Hey maybe next time!", Toast.LENGTH_SHORT).show()
			} else {
				val sharedPref = context?.getSharedPreferences("Shared", Context.MODE_PRIVATE)
				val lastTime = sharedPref?.getInt("today day: ${habit.habitName}", 1)
				val calendar = Calendar.getInstance()
				val today =
					("${calendar.get(Calendar.YEAR)}" + "${calendar.get(Calendar.DAY_OF_YEAR)}").toInt()

				if (today != lastTime && habit.habitProgressNow != habit.habitProgress) {
					habit.habitName?.let { it1 ->
						dbRef.child("Habits")
							.child(it1)
							.child("habitProgressNow")
							.setValue((habit.habitProgressNow!!.toInt() + 1).toString())
					}

					habit.habitName?.let { it1 ->
						dbRef.child("Current")
							.child(it1)
							.child("habitProgressNow")
							.setValue((habit.habitProgressNow!!.toInt() + 1).toString())
					}

					sharedPref?.edit(commit = true) {
						putInt("today day: ${habit.habitName}", today)
					}
				} else {

					if (today == lastTime && habit.habitProgressNow != habit.habitProgress) {
						Toast.makeText(
							context,
							"Hey you already clicked today",
							Toast.LENGTH_SHORT
						).show()
					} else {
						habit.habitName?.let { it1 ->
							dbRef.child("Habits")
								.child(it1)
								.child("status")
								.setValue("completed")
						}

						Toast.makeText(
							context,
							"You successfully achieved your goal good job!",
							Toast.LENGTH_LONG
						).show()
					}
				}
			}

			true
		}

		closeDialog.setOnClickListener { dismiss() }

		editmoney.setOnClickListener {
			val inflate = layoutInflater
				.inflate(R.layout.material_edittext, null, false)

			MaterialAlertDialogBuilder(requireContext())
				.setMessage("How much money do you spend daily?")
				.setView(inflate)
				.setNegativeButton("Cancel") { dialog, _ ->
					dialog.dismiss()
				}
				.setPositiveButton("Apply") { dialog, _ ->
					val editText: TextInputEditText = inflate.findViewById(R.id.text_field)
					habit.habitName?.let { it1 ->
						dbRef.child("Current")
							.child(it1)
							.child("dailyUseMoney")
							.setValue(editText.text.toString())
					}
					dialog.dismiss()
				}.show()
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}