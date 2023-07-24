package com.example.viewpager2test.presenter

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.example.viewpager2test.MainActivity
import com.example.viewpager2test.R
import com.example.viewpager2test.databinding.FragmentStoryBinding
import com.example.viewpager2test.databinding.FragmentStoryListBinding

class StoryListFragment : BaseDataBindingFragment<FragmentStoryListBinding>(R.layout.fragment_story_list) {

    var color: String = ""
    lateinit var listener: MainActivity.ViewModelCountListener
    lateinit var storyListener: StoryFragment.StoryListener
    var position = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("LEO", "StoryListFragment()::onViewCreated() - position: $position, color: $color, this:$this")

        if (color.isNotEmpty()) {
            binding.vColor.setBackgroundColor(Color.parseColor(color))
        }

//        binding.run {
//            playerCatch.setICatchPlayer(object : CatchStoryPlayerListener {
//                override fun onClick(isRight: Boolean) {
//                    // 좌우 터치 여부 값
//                    if (::storyListener.isInitialized) {
//                        storyListener.setPageMove(isRight)
//                    }
//                }
//
//                override fun onDoubleClick(x: Float, y: Float) {
//                    // onBigUp(x, y)
//                    // iCatchViewPager?.onNextCatch(getPosition().toInt())
//                }
//
//                @SuppressLint("SetTextI18n")
//                override fun onSeekTime(progress: Long, max: Long) {
//                    // tvPlayTime.text = "$progress / $max"
//                }
//
//                override fun onHitLog() {
//                }
//
//                override fun isPlayerPlay(isPlaying: Boolean) {
//
//                }
//
//                override fun getPlayerView(): ViewGroup {
//                    return binding.constraintLayout
//                }
//
//                override fun onNextVod() {
//
//                }
//            })
//
//            playerCatch.prepare()
//        }

        if (::listener.isInitialized) {
            listener.onPlus()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("LEO", "StoryListFragment()::onDestroyView() - position: $position, this:$this")

        if (::listener.isInitialized) {
            listener.onMinus()
        }
    }

    companion object {
        fun newInstance(
            parentPosition: Int,
            position: Int = 0,
            color: String = "",
            //listener: MainActivity.ViewModelCountListener,
            //storyListener: StoryFragment.StoryListener
        ): StoryListFragment {
            Log.d("LEO", "parentPosition: $parentPosition, listPosition: $position, color: $color")
            return StoryListFragment().apply {
                this.position = position
                this.color = color
                // this.listener = listener
                //this.storyListener = storyListener
            }
        }
    }
}
