package com.mr.ahmedlearninapp

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import kotlinx.android.synthetic.main.activity_lesson_details.*

 const val API_KEY = "AIzaSyA-Htjc6KZ-itEcfjhylnB944BrkhZycBM"
 const val URL_VIDEO = "video url"

class LessonDetailsActivity : YouTubeBaseActivity() {

    var mOnInitialisedListener: YouTubePlayer.OnInitializedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)

        setContentView(R.layout.activity_lesson_details)

        val videoId = intent?.getStringExtra(URL_VIDEO)?.substringAfter("be/")
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
