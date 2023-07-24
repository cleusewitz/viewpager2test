package com.example.viewpager2test

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.example.viewpager2test.databinding.ActivityMainBinding
import com.example.viewpager2test.presenter.SwipeFragment

class MainActivity : FragmentActivity() {

    private lateinit var binding: ActivityMainBinding
    var count = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragment = SwipeFragment().apply {
        }
        fragment.setEventListener(object : ViewModelCountListener {
            override fun onPlus() {
                Log.e("LEO", "onPlus()")
                binding.tvCount.text = (++count).toString()
            }

            override fun onMinus() {
                Log.e("LEO", "onMinus() - count: $count")
                binding.tvCount.text = (--count).toString()
            }
        })
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.fcv_main,
                fragment
            )
            .addToBackStack(null)
            .commit()
    }

    interface ViewModelCountListener {
        fun onPlus()
        fun onMinus()
    }
}
