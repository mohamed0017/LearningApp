package com.mr.ahmedlearninapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

const val USER_PHONE = "phone"
const val USER_NAME = "phone"
const val TEAM = "team"
const val TYPE = "type"

class SplashActivity : AppCompatActivity() {

  private  val sharedPreferences by lazy { getSharedPreferences("main", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            if (sharedPreferences.getString(USER_PHONE, "") == "")
                startActivity(Intent(this, LoginActivity::class.java))
            else
                startActivity(Intent(this, LessonsActivity::class.java))

        }, 600)

    }
}
