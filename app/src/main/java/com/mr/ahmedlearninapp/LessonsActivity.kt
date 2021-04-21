package com.mr.ahmedlearninapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.mr.ahmedlearninapp.SplashActivity.Companion.database
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.collections.ArrayList

class LessonsActivity : AppCompatActivity() {

    private val sharedPreferences by lazy { getSharedPreferences("main", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        if (sharedPreferences.getString(TYPE, "client") == "admin") {
            add_lesson.visibility = View.VISIBLE
            getDataForActiveUser(database)
        }

    }

    private fun getDataForActiveUser(database: FirebaseDatabase) {
        progress.visibility = View.VISIBLE
        rv_lessons.visibility = View.GONE
        val team =
            if (intent.getIntExtra(TEAM, 0) != 0) intent.getIntExtra(TEAM, 0)
            else sharedPreferences.getInt(TEAM, 0)

        title = when (team) {
            1 -> getString(R.string.lessons_first)
            2 -> getString(R.string.lessons_secound)
            else -> getString(R.string.lessons_thired)
        }

        database.reference.child("lessons")
            .addValueEventListener(object :
                ValueEventListener {

                override fun onCancelled(error: DatabaseError) {
                    progress.visibility = View.GONE
                    rv_lessons.visibility = View.VISIBLE
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val t: GenericTypeIndicator<HashMap<String, Lesson>> =
                        object : GenericTypeIndicator<HashMap<String, Lesson>>() {}

                    val lessons = snapshot.getValue(t)?.values
                    progress.visibility = View.GONE

                    rv_lessons.visibility = View.VISIBLE
                    if (!lessons.isNullOrEmpty()) {
                        add_lesson.setOnClickListener {
                            val intent = Intent(this@LessonsActivity, AddLessonActivity::class.java)
                            val sortedLessons = lessons.sortedBy { it.id }
                            intent.putExtra("itemId", (sortedLessons.last().id + 1))
                            startActivity(intent)
                        }
                        val teamLessons = lessons.filter { it.team == team }.sortedBy { it.id }
                        rv_lessons.adapter = LessonsAdapter(teamLessons)
                    } else {
                        add_lesson.setOnClickListener {
                            val intent = Intent(this@LessonsActivity, AddLessonActivity::class.java)
                            intent.putExtra("itemId", "0")
                            startActivity(intent)
                        }
                    }
                }

            })
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
