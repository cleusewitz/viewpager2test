package com.example.viewpager2test.presenter.adapter

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.viewpager2test.MainActivity
import com.example.viewpager2test.presenter.StoryFragment
import com.example.viewpager2test.presenter.StoryListFragment

class StoryViewPagerAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    private var colorList: ArrayList<String> = arrayListOf()
    lateinit var listener: MainActivity.ViewModelCountListener
    lateinit var storyListener: StoryFragment.StoryListener
    var parentPosition: Int = 0

    override fun getItemCount(): Int {
        return colorList.size
    }

    override fun createFragment(position: Int): Fragment {
        val data = colorList[position]
        Log.d("StoryViewPagerAdapter", "createFragment() - position: $position, color: $data")
        return StoryListFragment.newInstance(
            parentPosition,
            position,
            data,
            //listener,
            //storyListener
        )
    }

    fun setData(position: Int, data: ArrayList<String>, listener: MainActivity.ViewModelCountListener, storyListener: StoryFragment.StoryListener) {
        Log.d("StoryViewPagerAdapter", "setData() - listener: $listener")
        this.parentPosition = position
        colorList = data
        this.listener = listener
        this.storyListener = storyListener
        notifyDataSetChanged()
    }
}
