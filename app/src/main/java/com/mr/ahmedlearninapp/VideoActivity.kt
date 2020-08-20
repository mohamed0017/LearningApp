package com.mr.ahmedlearninapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import kotlinx.android.synthetic.main.activity_video.*

class VideoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

        val videoId = intent?.getStringExtra(URL_VIDEO)?.substringAfter("be/")

        var mOnInitialisedListener: YouTubePlayer.OnInitializedListener? = null

        mOnInitialisedListener = object : YouTubePlayer.OnInitializedListener {
            override fun onInitializationSuccess(
                provider: YouTubePlayer.Provider,
                youTubePlayer: YouTubePlayer,
                b: Boolean
            ) {
                youTubePlayer.loadVideo(videoId)
                youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL)
            }

            override fun onInitializationFailure(
                provider: YouTubePlayer.Provider,
                youTubeInitializationResult: YouTubeInitializationResult
            ) {
            }
        }

        youtubePlay.initialize(API_KEY, mOnInitialisedListener)
    }
}
