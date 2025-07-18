package com.example.spark.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.spark.databinding.FragmentCreateDialogBinding
import com.example.spark.model.Habit
import com.example.spark.model.Money
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class CreateHabitDialogFragment : DialogFragment() {

	private var _binding: FragmentCreateDialogBinding? = null
	private val binding get() = _binding!!

	private val dbRef = Firebase.database.getReference()

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = FragmentCreateDialogBinding.inflate(layoutInflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
		super.onViewCreated(view, savedInstanceState)

		tiet.setOnFocusChangeListener { viewOnFocus, _ ->
			val dateRangePicker = MaterialDatePicker.Builder
				.dateRangePicker()
				.setTitleText("Select dates")
				.build()

			if (viewOnFocus.isFocused)
				dateRangePicker.show(parentFragmentManager, "tag")

			dateRangePicker.addOnPositiveButtonClickListener {
				val startDate = it.first
				val endDate = it.second
				val msDiff = endDate - startDate
				val daysDiff = TimeUnit.MILLISECONDS.toDays(msDiff)
				binding.tiet.setText(daysDiff.toString())
				binding.tiet.clearFocus()
			}
		}

		saveTV.setOnClickListener {
			val habitName = habitet.editText?.text.toString()
			val habitProgress = tiet.text.toString()
			val dailyUseMoney = money.editText?.text.toString()
			val status = "active"
			val progressNow = "0"

			if (habitName.isNotEmpty() && habitProgress.isNotEmpty() && dailyUseMoney.isNotEmpty()) {
				val habitObject = Habit(
					habitName,
					habitProgress,
					dailyUseMoney,
					status,
					progressNow
				)
				val savings = Money(dailyUseMoney, progressNow)

				dbRef.child("Habits")
					.child(habitName)
					.setValue(habitObject)

				dbRef.child("Current")
					.child(habitName)
					.setValue(savings)

				dismiss()
			}

			exitIV.setOnClickListener { dismiss() }
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}