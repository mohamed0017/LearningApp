package com.mr.ahmedlearninapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import kotlinx.android.synthetic.main.activity_lesson_details.*

const val API_KEY = "AIzaSyA-Htjc6KZ-itEcfjhylnB944BrkhZycBM"
const val URL_VIDEO = "video url"
const val LESSON_ID = "lesson url"
const val LESSON_NAME = "lesson name"
const val URL_PDF = "pdf url"
const val URL_EXAM = "exam url"
const val EXAM_ID = "exam id"

class LessonDetailsActivity : AppCompatActivity() {

    private val sharedPreferences by lazy { getSharedPreferences("main", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lesson_details)

        val videoId = intent?.getStringExtra(URL_VIDEO)?.substringAfter("be/")
        val lessonId = intent?.getIntExtra(LESSON_ID, 0)
        val pdfUrl = intent?.getStringExtra(URL_PDF)
        val examUrl = intent?.getStringExtra(URL_EXAM)
        val lessonName = intent?.getStringExtra(LESSON_NAME)

        title = lessonName

        if (sharedPreferences.getString(TYPE, "client") == "admin")
            edit.visibility = View.VISIBLE
        else
            edit.visibility = View.GONE

        video.setOnClickListener {
            val intent = Intent(this, VideoActivity::class.java)
            intent.putExtra(URL_VIDEO, videoId)
            startActivity(intent)
        }

        pdf.setOnClickListener {
            if (!pdfUrl.isNullOrEmpty()) {
                val intent = Intent(this, RemotePDFActivity::class.java)
                intent.putExtra("filePath", pdfUrl)
                intent.putExtra(LESSON_NAME, lessonName)
                startActivity(intent)
            }
        }

        exam.setOnClickListener {
            if (!examUrl.isNullOrEmpty()) {
                val intent = Intent(this, WebViewActivity::class.java)
                intent.putExtra("exam_url", examUrl)
                intent.putExtra("isFromUnits", false)
                startActivity(intent)
            }
        }

        edit.setOnClickListener {
            if (lessonId != 0) {
                val intent = Intent(this, EditLessonActivity::class.java)
                intent.putExtra(LESSON_ID, lessonId)
                startActivity(intent)
            }
        }
    }
}
