package com.mr.ahmedlearninapp

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import kotlinx.android.synthetic.main.activity_lesson_details.*

 const val API_KEY = "AIzaSyA-Htjc6KZ-itEcfjhylnB944BrkhZycBM"
 const val URL_VIDEO = "video url"
 const val LESSON_ID = "lesson url"
 const val URL_PDF = "pdf url"
 const val URL_EXAM = "exam url"

class LessonDetailsActivity : YouTubeBaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_lesson_details)

        val videoId = intent?.getStringExtra(URL_VIDEO)?.substringAfter("be/")
        val lessonId = intent?.getStringExtra(LESSON_ID)
        val pdfUrl = intent?.getStringExtra(URL_PDF)
        val examUrl = intent?.getStringExtra(URL_EXAM)


        video.setOnClickListener {
            val intent = Intent(this, VideoActivity::class.java)
            intent.putExtra(URL_VIDEO, videoId)
            startActivity(intent)
        }

        pdf.setOnClickListener {

        }

        exam.setOnClickListener {

        }
    }
}
