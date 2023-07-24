package com.example.viewpager2test.presenter

import android.view.ViewGroup

/**
 * Catch 플레이어 연동 리스너
 */
interface CatchStoryPlayerListener {
    /**
     * 화면 터치
     */
    fun onClick(isRight: Boolean)

    /**
     * 더블터치 (UP 체크용)
     */
    fun onDoubleClick(x: Float, y: Float)

    /**
     * seeking 체크
     */
    fun onSeekTime(progress: Long, max: Long)

    /**
     * 플레이어가 다 돌고 end 를 찍으면 힛트 로그를 발송하도록
     */
    fun onHitLog()

    /**
     * 플레이어가 플레이중인지 여부 반환
     */
    fun isPlayerPlay(isPlaying: Boolean)

    fun getPlayerView(): ViewGroup

    /**
     * 다음 VOD 재생
     */
    fun onNextVod()
}
