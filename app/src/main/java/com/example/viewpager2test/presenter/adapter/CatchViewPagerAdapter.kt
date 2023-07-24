package com.example.viewpager2test.presenter.adapter

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.viewpager2test.MainActivity
import com.example.viewpager2test.presenter.StoryFragment

class CatchViewPagerAdapter(fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    private var colorList: ArrayList<String> = arrayListOf()
    lateinit var listener: MainActivity.ViewModelCountListener

    override fun getItemCount(): Int {
        return colorList.size
    }

    override fun createFragment(position: Int): Fragment {
        Log.d("CatchViewPagerAdapter", "createFragment() - listener: $listener")
        val data = colorList[position]
        return StoryFragment.newInstance(
            data,
            listener
        )
    }

    fun setData(data: ArrayList<String>, listener: MainActivity.ViewModelCountListener) {
        Log.d("CatchViewPagerAdapter", "setData() - listener: $listener")
        colorList = data
        this.listener = listener
        notifyDataSetChanged()
    }
}
