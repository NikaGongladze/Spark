package com.example.spark

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.spark.adapters.ViewPagerAdapter
import com.example.spark.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

	private lateinit var binding: ActivityMainBinding
	private lateinit var vpAdapter: ViewPagerAdapter

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)
		ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
			val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
			v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
			insets
		}

		supportActionBar?.hide()

		binding.apply {

			vpAdapter = ViewPagerAdapter(this@MainActivity)
			viewPager.adapter = vpAdapter

			viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
				override fun onPageSelected(position: Int) {
					super.onPageSelected(position)
					when (position) {
						0 -> navView.menu.findItem(R.id.menuBadHabits).isChecked = true
						1 -> navView.menu.findItem(R.id.menuGoodHabits).isChecked = true
						2 -> navView.menu.findItem(R.id.menuUglyHabit).isChecked = true
					}
				}
			})

			navView.setOnItemSelectedListener {
				when (it.itemId) {
					R.id.menuBadHabits -> {
						viewPager.currentItem = 0
						return@setOnItemSelectedListener true
					}
					R.id.menuGoodHabits -> {
						viewPager.currentItem = 1
						return@setOnItemSelectedListener true
					}
					R.id.menuUglyHabit -> {
						viewPager.currentItem = 2
						return@setOnItemSelectedListener true
					}
				}
				false
			}
		}
	}
}