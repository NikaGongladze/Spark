package com.example.spark.adapters

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.spark.fragments.BadHabits
import com.example.spark.fragments.GoodHabits
import com.example.spark.fragments.UglyHabits

class ViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

	override fun getItemCount(): Int = 3

	override fun createFragment(position: Int): Fragment =
		when (position) {
			0 -> BadHabits()
			1 -> GoodHabits()
			2 -> UglyHabits()
			else -> BadHabits()
		}
}