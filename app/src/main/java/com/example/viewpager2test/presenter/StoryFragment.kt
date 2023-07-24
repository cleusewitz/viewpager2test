package com.example.viewpager2test.presenter

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.viewpager2test.MainActivity
import com.example.viewpager2test.R
import com.example.viewpager2test.databinding.FragmentStoryBinding

class StoryFragment : BaseDataBindingFragment<FragmentStoryBinding>(R.layout.fragment_story) {

    var color: String = ""
    lateinit var listener: MainActivity.ViewModelCountListener
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("LEO", "onViewCreated()")
        binding.vColor.setBackgroundColor(Color.parseColor(color))

        if (::listener.isInitialized) {
            listener.onPlus()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        if (::listener.isInitialized) {
            listener.onMinus()
        }
    }

    companion object {
        fun newInstance(
            color: String,
            listener: MainActivity.ViewModelCountListener
        ): StoryFragment {
            Log.d("LEO", "color: $color, listener: $listener")
            return StoryFragment().apply {
                this.color = color
                this.listener = listener
            }
        }
    }
}
