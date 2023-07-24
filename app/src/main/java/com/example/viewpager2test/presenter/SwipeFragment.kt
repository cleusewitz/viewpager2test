package com.example.viewpager2test.presenter

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.example.viewpager2test.MainActivity
import com.example.viewpager2test.R
import com.example.viewpager2test.databinding.FragmentSwipeBinding
import com.example.viewpager2test.presenter.adapter.SwipeViewPagerAdapter
import java.util.ArrayList

class SwipeFragment : BaseDataBindingFragment<FragmentSwipeBinding>(R.layout.fragment_swipe) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("LEO", "SwipeFragment()::onViewCreated() - this:$this")
        val sectionsPagerAdapter = SwipeViewPagerAdapter(this)
        val viewPager: ViewPager2 = binding.viewPager2

        // viewPager.offscreenPageLimit = 1
        sectionsPagerAdapter.setData(
            arrayListOf<ArrayList<String>>().apply {
                add(
                    arrayListOf(
                        "#ffff0000",
                    )
                )
                add(
                    arrayListOf(
                        "#ddff0000",
                    )
                )
                add(
                    arrayListOf(
                        "#ccff0000",
                    )
                )
                add(
                    arrayListOf(
                        "#bbff0000",
                    )
                )
                add(
                    arrayListOf(
                        "#aaff0000",
                    )
                )
                add(
                    arrayListOf(
                        "#99ff0000",
                    )
                )
            },
            listener
        )
        viewPager.adapter = sectionsPagerAdapter
    }

    private lateinit var listener: MainActivity.ViewModelCountListener
    fun setEventListener(listener: MainActivity.ViewModelCountListener) {
        this.listener = listener
    }
}
