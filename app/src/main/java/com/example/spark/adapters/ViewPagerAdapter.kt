package com.example.spark.adapters

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.spark.fragments.BadHabits
import com.example.spark.fragments.GoodHabits

class ViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

	override fun getItemCount(): Int = 2

	override fun createFragment(position: Int): Fragment =
		when (position) {
			0 -> BadHabits()
			1 -> GoodHabits()
			else -> BadHabits()
		}
}