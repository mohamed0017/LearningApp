package com.mr.ahmedlearninapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.mr.ahmedlearninapp.ui.active_users.ActiveStudentsActivity
import kotlinx.android.synthetic.main.activity_admin_main.*

class AdminMainActivity : AppCompatActivity() {

    private val sharedPreferences by lazy { getSharedPreferences("main", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_main)

        title = "الصفحة الرئيسية"
        first.setOnClickListener {
            openLessons(1)
        }

        scound.setOnClickListener {
            openLessons(2)
        }

        thired.setOnClickListener {
            openLessons(3)
        }

        search.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        add_exam.setOnClickListener {
            val intent = Intent(this, AddExamActivity::class.java)
            startActivity(intent)
        }

        users.setOnClickListener {
            val intent = Intent(this, ActiveStudentsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun openLessons(team: Int) {
        sharedPreferences?.edit()?.putInt(TEAM, team)?.apply()
        val intent = Intent(this, StudentHomeActivity::class.java)
        intent.putExtra(TEAM, team)
        startActivity(intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)

        // return true so that the menu pop up is opened
        // return true so that the menu pop up is opened
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                sharedPreferences.edit().apply {
                    putString(USER_PHONE, "")
                    putString(USER_NAME, "")
                    putInt(TEAM, 0)
                    putString(TYPE, "")
                    apply()
                }
                startActivity(Intent(this, OnBoardingActivity::class.java))
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
