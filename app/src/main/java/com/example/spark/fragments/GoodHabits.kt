package com.example.spark.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.spark.R
import com.example.spark.databinding.FragmentGoodHabitsBinding
import com.example.spark.model.Habit
import com.example.spark.model.Money
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class GoodHabits : Fragment() {

	private var _binding: FragmentGoodHabitsBinding? = null
	private val binding get() = _binding!!

	private val dbRef = Firebase.database.getReference()
	private val times = arrayListOf<Int>()
	private val habitMoneyArray = arrayListOf<Double>()
	private val timesSpentList = arrayListOf<Int>()
	private val moneySavingsList = arrayListOf<Double>()

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = FragmentGoodHabitsBinding.inflate(layoutInflater, container, false)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		binding.apply {
			dbRef.child("Habits").addValueEventListener(object : ValueEventListener {
				override fun onDataChange(snapshot: DataSnapshot) {
					currentSavings.text = ""
					var timesXMoney = 0.0
					var dailyMoney = 0.0

					for (postSnapshot in snapshot.children) {
						times.clear()
						habitMoneyArray.clear()

						val habitObject = postSnapshot.getValue(Habit::class.java)

						if (habitObject != null) {
							habitObject.habitProgressNow?.toInt()?.let { times.add(it) }
							habitObject.dailyUseMoney?.let { habitMoneyArray.add(it.toDouble()) }
							dailyMoney += habitMoneyArray.sum()
							moneySpentPerDay.text = dailyMoney.toString() + " Gel"
							monthlySaving.text = (dailyMoney.toInt() * 30).toString() + " Gel"
							yearlySaving.text = (dailyMoney.toInt() * 30 * 12).toString() + " Gel"

							for (i in 0 until times.size) {
								timesXMoney += times[i].toDouble() * habitMoneyArray[i]
							}
						}
					}
				}

				override fun onCancelled(error: DatabaseError) {
				}
			})

			dbRef.child("Current")
				.addValueEventListener(object : ValueEventListener {
					override fun onDataChange(snapshot: DataSnapshot) {
						var timesXMoney = 0.0

						for (postSnapshot in snapshot.children) {
							moneySavingsList.clear()
							timesSpentList.clear()

							val current = postSnapshot.getValue(Money::class.java)

							if (current != null) {
								current.habitProgressNow?.let { timesSpentList.add(it.toInt()) }
								current.dailyUseMoney?.let { moneySavingsList.add(it.toDouble()) }
								for (i in 0 until times.size) {
									timesXMoney += timesSpentList[i].toDouble() * moneySavingsList[i]
								}
							}
						}
						currentSavings.text = timesXMoney.toString() + " Gel"
					}

					override fun onCancelled(error: DatabaseError) {
					}
				})

			editSpentMoney.setOnClickListener {
				val dialogView = layoutInflater
					.inflate(R.layout.good_habits_edit_money, null, false)
				val alertDialog = AlertDialog.Builder(requireContext())
					.setView(dialogView)
					.setTitle("Bad Habit")
					.show()
				val submitBtn = alertDialog.findViewById<Button>(R.id.submit)
				val cancelBtn = alertDialog.findViewById<TextView>(R.id.cancelButton)

				submitBtn?.setOnClickListener {
					alertDialog.dismiss()
					val editMoney = alertDialog.findViewById<EditText>(R.id.editMoney)
						?.text.toString()

					moneySpentPerDay.text = "$editMoney Gel"
					monthlySaving.text = (editMoney.toInt() * 30).toString() + "Gel"
					yearlySaving.text = (editMoney.toInt() * 30 * 12).toString() + "Gel"
				}

				cancelBtn?.setOnClickListener { alertDialog.dismiss() }
			}
		}
	}

	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
	}
}