package com.example.viewpager2test.presenter

import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.analytics.AnalyticsListener
import androidx.media3.exoplayer.hls.DefaultHlsDataSourceFactory
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.LoadEventInfo
import androidx.media3.exoplayer.source.MediaLoadData
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.trackselection.ExoTrackSelection
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import java.io.IOException

/**
 * Catch ExoPlayer View
 */
class CatchPlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : PlayerView(context, attrs, defStyleAttr) {

    /*private lateinit var catchData: CatchData
    private val vodUrl: String
        get() = catchData.files[filePosition].file*/

    private lateinit var exoPlayer: ExoPlayer

    private lateinit var gDetector: GestureDetector

    /**
     * ViewPager 위치
     */
    private var isPosition: Int = -1

    /**
     * 플레이 재생 준비 딜레이 측정기
     */
    private var playerDelay: Long = 0

    private var iCatchPlayer: CatchStoryPlayerListener? = null

    private var catchSeekbar: AppCompatSeekBar? = null

    var start: Long = 0

    private lateinit var logListener: LogListener

    /**
     * 확대모드로 할지 여부 (9:16 사이즈 영상)
     */
    // private var videoZoomState: Boolean = false

    /**
     * 현재 파일 재생 위치
     */
    private var filePosition = 0

    private var exoTouchListener: OnTouchListener = OnTouchListener { v, event ->
        gDetector.onTouchEvent(event)
        v.performClick()
        true
    }

    /**
     * Exoplayer 터치 감지용 리스너
     */
    private var mOnSimpleOnGestureListener: GestureDetector.SimpleOnGestureListener =
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                // Timber.tag("LEO_XXXX").d("onSingleTapUp()")
                val x: Float = e.x
                when (e.action) {
                    MotionEvent.ACTION_UP -> {
                        // Timber.tag("LEO_XXXX").d("MotionEvent.ACTION_UP")
                        var isRight = false
                        val parentView: View = iCatchPlayer?.getPlayerView()!!

                        val widthQuarter = parentView.measuredWidth / 3
                        if (x < widthQuarter) {
                            // Timber.tag("LEO_XXXX").d("왼쪽")
                            // click(false)
                        } else if (x > widthQuarter * 2) {
                            // Timber.tag("LEO_XXXX").d("오른쪽")
                            // click(true)
                            isRight = true
                        }

                        iCatchPlayer?.apply {
                            onClick(isRight)
                        }
                    }
                }

                return super.onSingleTapUp(e)
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                // Timber.tag("LEO_XXXX").d("onSingleTapConfirmed()")
                return super.onSingleTapConfirmed(e)
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                // Timber.tag("LEO_XXXX").d("onDoubleTap()")
                doubleClick(e.x, e.y)
                return super.onDoubleTap(e)
            }
        }

    private val exoAnalyticsListener: AnalyticsListener = object : AnalyticsListener {

        override fun onPlaybackStateChanged(
            eventTime: AnalyticsListener.EventTime,
            state: Int,
        ) {
            /*Timber.i(
                "onPlayerStateChanged [$isPosition] , playbackState $state",
            )*/

            when (state) {
                Player.STATE_IDLE -> {
                    // Timber.i("ExoPlayerListener::onPlayerStateChanged [$isPosition] -> IDLE")
                }

                Player.STATE_BUFFERING -> {
                    /*Timber.i(
                        "ExoPlayerListener::onPlayerStateChanged [$isPosition] -> BUFFERING...",
                    )*/
                    playerDelay = System.currentTimeMillis()
                }

                Player.STATE_READY -> {
                    if (0 < playerDelay) {
                        playerDelay = System.currentTimeMillis() - playerDelay
                        /*Timber.i(
                            "ExoPlayerListener::onPlayerStateChanged [$isPosition] ->>> READY - playerDelay:[$playerDelay]",
                        )*/
                        playerDelay = 0
                    }
                    /*Timber.i(
                        "ExoPlayerListener::onPlayerStateChanged [$isPosition] ->>> READY - Duration: " + exoPlayer.duration,
                    )*/
                    onPlayReady()
                }

                Player.STATE_ENDED -> {
                    /*Timber.i(
                        "ExoPlayerListener::onPlayerStateChanged [$isPosition] -> Playback Ended.",
                    )*/
                    logListener.onVoutLog("end")
                    exoPlayer.seekToDefaultPosition()
                    // TODO: 비지니스로직이 view 에 구현 되어 있음 수정 필요
                    checkNextVod() // 다음영상 있는지 체크
                    resume(true) // 영상 끝나면 다시 재생
                }
            }

            super.onPlaybackStateChanged(eventTime, state)
        }
    }

    /**
     * 재생준비 완료 호출
     */
    fun onPlayReady() {
        // Timber.i("[$isPosition] onPlayReady()")

        // Catch 플레이 이어보기
        /*if (isPosition == 0 && catchData.changeSecond > 0 && catchData.changeSecond < exoPlayer.duration) {
            exoPlayer.seekTo(catchData.changeSecond * 1000L)
            catchData.changeSecond = 0
        }*/

        updateSeek()
    }

    /**
     * Double Click
     */
    fun doubleClick(x: Float, y: Float) {
        // iCatchPlayer?.onDoubleClick(x, y)
    }

    /**
     * Seekbar 설정
     */
    fun initSeekBar() {
        catchSeekbar?.let {
            it.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar,
                    progress: Int,
                    fromUser: Boolean,
                ) {
                    /*iCatchPlayer?.onSeekTime(
                        NUtils.getStringForTime(progress.toLong() * 1000),
                        NUtils.getStringForTime(seekBar.max.toLong() * 1000),
                    )*/
                }

                override fun onStartTrackingTouch(seekBar: SeekBar) {}

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                    if (!isPause()) {
                        logListener.onVoutLog("seek")
                        seekTo(seekBar.progress * 1000L)
                        start = getExoPosition()
                    } else {
                        seekTo(seekBar.progress * 1000L)
                    }
                }
            })

            it.setOnTouchListener(
                OnTouchListener { v, event ->
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        // catchListViewModel.setProgressDownState(true)
                    }
                    if (event.action == MotionEvent.ACTION_UP) {
                        // catchListViewModel.setProgressDownState(false)
                    }

                    it.requestLayout()
                    v.performClick()
                    return@OnTouchListener false
                },
            )


            // if (catchData.files.size > 1) {
            // it.setVisible(false)
            // }
        }
    }

    /**
     * Click pause / resume (not Restart)
     */
    fun click() {
        /*Timber.i("[$isPosition] playerControllerClick() playState:[${isPlaying()}]")
        if (isPlaying()) {
            pause(restart = false, log = true)
            iCatchPlayer?.apply {
                onClick(false)
            }
        } else {
            resume()
            iCatchPlayer?.apply {
                onClick(true)
            }
        }*/
    }

    fun setSeekBar(tb: AppCompatSeekBar) {
        this.catchSeekbar = tb
    }

    fun setICatchPlayer(listener: CatchStoryPlayerListener) {
        this.iCatchPlayer = listener
    }

    fun setPosition(Position: Int) {
        isPosition = Position
    }

    /*fun setData(data: CatchData) {
        this.catchData = data
    }*/

    /**
     * 플레이 순서
     * initPlayer > prepare > play
     */
    fun initPlayer() {
        // Timber.i("[$isPosition] initPlayer()")

        useController = false // set to true or false to see controllers
        setOnTouchListener(exoTouchListener)

        // videoSizeCheck()
        videoResizeMode()

        requestFocus()

        if (!::gDetector.isInitialized) {
            gDetector = GestureDetector(context, mOnSimpleOnGestureListener)
        }

        if (!::exoPlayer.isInitialized) {
            val videoTrackSelectionFactory: ExoTrackSelection.Factory =
                AdaptiveTrackSelection.Factory(
                    5_000,
                    AdaptiveTrackSelection.DEFAULT_MAX_DURATION_FOR_QUALITY_DECREASE_MS,
                    AdaptiveTrackSelection.DEFAULT_MIN_DURATION_TO_RETAIN_AFTER_DISCARD_MS,
                    AdaptiveTrackSelection.DEFAULT_BANDWIDTH_FRACTION,
                )

            val loadControl = DefaultLoadControl.Builder()
                .setBufferDurationsMs(
                    2_000,
                    5_000,
                    800,
                    2_000,
                )
                .setPrioritizeTimeOverSizeThresholds(true)
                .setTargetBufferBytes(C.LENGTH_UNSET)
                .build()

            val bandwidthMeter: DefaultBandwidthMeter = DefaultBandwidthMeter.Builder(context)
                .setInitialBitrateEstimate(
                    C.NETWORK_TYPE_4G,
                    DefaultBandwidthMeter.DEFAULT_INITIAL_BITRATE_ESTIMATE,
                )
                .setInitialBitrateEstimate(
                    C.NETWORK_TYPE_5G_SA,
                    DefaultBandwidthMeter.DEFAULT_INITIAL_BITRATE_ESTIMATE,
                )
                .setInitialBitrateEstimate(
                    C.NETWORK_TYPE_5G_NSA,
                    DefaultBandwidthMeter.DEFAULT_INITIAL_BITRATE_ESTIMATE,
                )
                .build()

            exoPlayer = ExoPlayer.Builder(context)
                .setTrackSelector(DefaultTrackSelector(context, videoTrackSelectionFactory))
                .setLoadControl(loadControl)
                .setBandwidthMeter(bandwidthMeter)
                .build()

            exoPlayer.addAnalyticsListener(exoAnalyticsListener)

            // Bind the player to the view.
            player = exoPlayer
        }
    }

    /**
     * 화면 비율 변경
     *
     * RESIZE_MODE_ZOOM == 태블릿 (핏)
     * RESIZE_MODE_FIT == 모바일 (확대)
     *
     */
    fun videoResizeMode() {
        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        // Timber.i("[$isPosition] videoResizeMode resizeMode:[$resizeMode]")
    }

    fun removeSeekBarCallback() {
        removeCallbacks(updateSeekRunnable)
    }

    /**
     * 플레이 준비
     */
    fun prepare() {
        // Timber.i("[$isPosition] prepare()")
        initPlayer()
        initSeekBar()
        setPlayerUrl()
    }

    /**
     * Seek
     */
    fun seekTo(newPosition: Long) {
        if (isPause() || isPlaying()) {
            try {
                exoPlayer.seekTo(newPosition)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Play 상태값 반환
     */
    fun isPlaying(): Boolean = exoPlayer.isPlaying

    /**\
     * 재생
     */
    fun resume(restart: Boolean = false) {
        /*Timber.i(
            "[$isPosition] resume() playbackState : ${exoPlayer.playbackState}, restart: $restart",
        )*/
        // Play
        try {
            // Workaround : 간혈적으로 buffering 상태가 복구가 안되는 경우 가 생김
            // 이 때 isLoading 중이라면 네트워크 상황에 따른 buffering 이 진행중이라 판단하고 그게 아니라면 복구 로직을 실행함.
            if (exoPlayer.playbackState == Player.STATE_BUFFERING && !exoPlayer.isLoading) {
                // recovery
                // Timber.w("[$isPosition] recovery")
                exoPlayer.stop()
                exoPlayer.prepare()
            }
            start = getExoPosition()
            exoPlayer.play()
            updateSeek()
        } catch (e: Exception) {
            // Timber.e("Catch Player Error:$e")
        }
    }

    /**
     * 일시 정지
     */
    fun pause(restart: Boolean, log: Boolean) {
        /*Timber.i(
            "[$isPosition] pause() restart: $restart, log:[$log]",
        )*/
        try {
            if (log) {
                logListener.onVoutLog("pause")
            }
            if (restart) {
                exoPlayer.seekToDefaultPosition()
            }
            exoPlayer.pause()
        } catch (e: Exception) {
            // Timber.e("Exception : ", e)
        }
    }

    /**
     * 정지
     */
    fun stop() {
        // Timber.i("[$isPosition] view stop:")
        try {
            exoPlayer.pause()
            exoPlayer.stop()
            exoPlayer.seekTo(0)
            // Timber.i("stop -> ${exoPlayer.playbackState}")
        } catch (e: java.lang.Exception) {
            // Timber.e("Exception : ", e)
        }
    }

    /**
     * 릴리즈
     */
    fun release() {
        // 액티비티 종료시 처리

        // sendVOUT("exit")
        stop()
        releaseInternal()
        removeSeekBarCallback()
        // Timber.i("[$isPosition] view release:")
    }

    private fun releaseInternal() {
        try {
            exoPlayer.removeAnalyticsListener(exoAnalyticsListener)
            exoPlayer.clearVideoSurface()
            exoPlayer.clearAuxEffectInfo()
            exoPlayer.seekToDefaultPosition()
            exoPlayer.stop()
            exoPlayer.release()
        } catch (e: java.lang.Exception) {
            // Timber.e("Exception : ", e)
        }
    }

    private fun isPause() =
        exoPlayer.playbackState == Player.STATE_READY && !exoPlayer.playWhenReady

    /**
     * 다음 영상 체크
     *
     * isNext : 다음 영상 url 가져오기
     */
    private fun checkNextVod() {
        /*Timber.i(
            "checkNextVod filePosition = $filePosition, catchData.files.size = ${catchData.files.size}",
        )*/
        /*if (catchData.files.size > 1) {
            filePosition++
            if (filePosition == catchData.files.size) {
                filePosition = 0
                // iCatchPlayer?.onHitLog()
            }
            setPlayerUrl()
        } else {
            // iCatchPlayer?.onHitLog()
        }*/
    }

    private fun setPlayerUrl() {
        try {
            // Timber.i("vodUrl = $vodUrl")
            /*if (vodUrl.contains(".m3u8")) {
                val videoSource: MediaSource = createHlsMediaSource(vodUrl)
                exoPlayer.setMediaSource(videoSource)
                exoPlayer.prepare()
            } else {
                val videoSource: MediaSource = createMediaSource(vodUrl)
                exoPlayer.setMediaSource(videoSource)
                exoPlayer.prepare()
            }*/
        } catch (e: Exception) {
            // Timber.e("setPlayerUrl Error:$e")
        }
    }

    private fun createMediaSource(videoUrl: String): MediaSource {
        return if (videoUrl.endsWith(".m3u8")) {
            createHlsMediaSource(videoUrl)
        } else {
            createHttpMediaSource(videoUrl)
        }
    }

    private fun createHttpMediaSource(videoUrl: String): MediaSource {
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
        return ProgressiveMediaSource.Factory(httpDataSourceFactory)
            .createMediaSource(MediaItem.fromUri(videoUrl))
    }

    private fun createHlsMediaSource(videoUrl: String): MediaSource {
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
        val hlsDataSourceFactory = DefaultHlsDataSourceFactory(httpDataSourceFactory)
        return HlsMediaSource.Factory(hlsDataSourceFactory)
            .createMediaSource(MediaItem.fromUri(videoUrl))
    }

    private val updateSeekRunnable: Runnable = Runnable {
        updateSeek()
    }

    private fun updateSeek() {
        val duration = if (exoPlayer.duration >= 0) exoPlayer.duration else 0
        val position = exoPlayer.currentPosition

        updateSeekUi(duration, position)

        val state = exoPlayer.playbackState

        removeSeekBarCallback()
        if (state != Player.STATE_IDLE && state != Player.STATE_ENDED) {
            postDelayed(updateSeekRunnable, 1000)
        }
    }

    /**
     * 프로그래스바 움직임 ui 적용
     */
    private fun updateSeekUi(duration: Long, position: Long) {
        /*if (!catchListViewModel.isProgressDownState()) {
            val progress: Int = (position / 1000).toInt()
            val max: Int = (duration / 1000).toInt()
            catchSeekbar?.progress = progress
            catchSeekbar?.max = max
        }*/
    }

    fun getDuration(): Long {
        return exoPlayer.duration
    }

    fun getPosition(): Int {
        return catchSeekbar?.progress ?: 0
    }

    fun getExoPosition(): Long {
        val position = exoPlayer.currentPosition
        return if (position > 0) {
            position / 1000
        } else {
            0
        }
    }

    fun logListener(logListener: LogListener) {
        this.logListener = logListener
    }

    interface LogListener {
        fun onVoutLog(state: String)
    }

    companion object {
        private val TAG: String = CatchPlayerView::class.java.simpleName
    }
}
