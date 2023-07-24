package com.example.viewpager2test.presenter

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.example.viewpager2test.MainActivity
import com.example.viewpager2test.R
import com.example.viewpager2test.databinding.FragmentSwipeBinding
import com.example.viewpager2test.presenter.adapter.CatchViewPagerAdapter

class SwipeFragment : BaseDataBindingFragment<FragmentSwipeBinding>(R.layout.fragment_swipe) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("SwipeFragment", "onViewCreated()")
        val sectionsPagerAdapter = CatchViewPagerAdapter(this)
        sectionsPagerAdapter.setData(
            arrayListOf(
                "#ff0000",
                "#ff00ff",
                "#ffff00",
                "#ee0000",
                "#ee00ee",
                "#eeee00",
                "#dd0000",
                "#dd00dd",
                "#dddd00",
            ),
            listener
        )
        val viewPager: ViewPager2 = binding.viewPager2
        viewPager.adapter = sectionsPagerAdapter
    }

    private lateinit var listener: MainActivity.ViewModelCountListener
    fun setEventListener(listener: MainActivity.ViewModelCountListener) {
        Log.d("SwipeFragment", "setEventListener()")
        this.listener = listener
    }
}
