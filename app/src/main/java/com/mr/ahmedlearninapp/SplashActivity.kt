package com.mr.ahmedlearninapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

const val USER_PHONE = "phone"
const val USER_NAME = "name"
const val TEAM = "team"
const val TYPE = "type"

class SplashActivity : AppCompatActivity() {

    private val sharedPreferences by lazy { getSharedPreferences("main", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            if (sharedPreferences.getString(USER_PHONE, "") == "")
                startActivity(Intent(this, OnBoardingActivity::class.java))
            else {
                if (sharedPreferences.getString(TYPE, "") == "admin")
                    startActivity(Intent(this, AdminMainActivity::class.java))
                else
                    startActivity(Intent(this, LessonsActivity::class.java))
            }
            finish()

        }, 600)

    }
}
