package com.example.spark.dialogs

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.spark.databinding.FragmentOpenHabitDialogBinding
import com.example.spark.model.Habit

class OpenHabitDialogFragment(
	position: Int,
	habitList: ArrayList<Habit>
) : DialogFragment() {

	private var _binding: FragmentOpenHabitDialogBinding? = null
	private val binding get() = _binding!!

	private var habit = habitList[position]

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

		habitName.text = habit.habitName
		progressTV.text = habit.habitProgressNow + "/" + habit.habitProgress
		statusTV.text = "Status: " + habit.status

		circularProgressIndicator.max = habit.habitProgress.toInt() * 1000
		ObjectAnimator.ofInt(
			circularProgressIndicator,
			"progress",
			habit.habitProgressNow.toInt() * 1000
		).setDuration(1700)
			.start()

		daysRemaining.text =
			"Days Remaining: " + (habit.habitProgress.toInt() - habit.habitProgressNow.toInt())

		progressTV.setOnLongClickListener {
			if (statusTV.text.equals("Status: inactive")) {
				Toast.makeText(context, "Hay maybe next time!", Toast.LENGTH_SHORT).show()
			}

			true
		}

		closeDialog.setOnClickListener { dismiss() }
		TODO("Add db logic later")
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}