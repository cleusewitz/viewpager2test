package com.example.viewpager2test.presenter

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.example.viewpager2test.MainActivity
import com.example.viewpager2test.R
import com.example.viewpager2test.databinding.FragmentStoryBinding
import com.example.viewpager2test.presenter.adapter.StoryViewPagerAdapter
import com.example.viewpager2test.presenter.adapter.SwipeViewPagerAdapter
import java.text.FieldPosition

class StoryFragment : BaseDataBindingFragment<FragmentStoryBinding>(R.layout.fragment_story) {

    var colors: ArrayList<String> = arrayListOf()
    lateinit var listener: MainActivity.ViewModelCountListener
    var position: Int = 0

    interface StoryListener {
        fun setPageMove(isRight: Boolean)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("LEO", "StoryFragment()::onViewCreated() - position: $position, this:$this")

        if (::listener.isInitialized) {
            listener.onPlus()
        }

        if (colors.isNotEmpty()) {
            binding.vColor.setBackgroundColor(Color.parseColor(colors[0]))
        }

        binding.text.text = position.toString()

        /*val sectionsPagerAdapter = StoryViewPagerAdapter(this)
        val viewPager: ViewPager2 = binding.viewPager2
        viewPager.adapter = sectionsPagerAdapter
        // viewPager.offscreenPageLimit = 1
        sectionsPagerAdapter.setData(
            position,
            colors,
            listener,
            object : StoryListener {
                override fun setPageMove(isRight: Boolean) {
                    if (isRight) {
                        viewPager.setCurrentItem(viewPager.currentItem + 1, true)
                    } else {
                        viewPager.setCurrentItem(viewPager.currentItem - 1, true)
                    }
                }
            }
        )*/
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("LEO", "StoryFragment()::onDestroyView() - position: $position, this:$this")

        if (::listener.isInitialized) {
            listener.onMinus()
        }
    }

    companion object {
        fun newInstance(
            position: Int,
            colors: ArrayList<String>,
            listener: MainActivity.ViewModelCountListener
        ): StoryFragment {
            Log.d("LEO", "newInstance() - StoryPosition: $position, color: $colors, listener: $listener")
            return StoryFragment().apply {
                this.position = position
                this.colors = colors
                this.listener = listener
            }
        }
    }
}
