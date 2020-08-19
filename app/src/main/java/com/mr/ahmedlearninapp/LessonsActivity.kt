package com.mr.ahmedlearninapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList


class LessonsActivity : AppCompatActivity() {

    private val sharedPreferences by lazy { getSharedPreferences("main", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val database = Firebase.database

        database.reference.child("phone")
            .addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                        val number = snapshot.getValue(String::class.java) ?: "+201221596213"

                        whats.setOnClickListener {
                            try {
                                val url = "https://api.whatsapp.com/send?phone=$number"
                                val i = Intent(Intent.ACTION_VIEW)
                                i.data = Uri.parse(url)
                                startActivity(i)
                            } catch (e: Exception) {
                            }

                        }

                        call.setOnClickListener {
                            try {
                                val intent = Intent(Intent.ACTION_DIAL)
                                intent.data = Uri.parse("tel:$number")
                                startActivity(intent)
                            } catch (e: Exception) {
                            }

                        }
                    }

            })


    }

    override fun onResume() {
        super.onResume()

        val database = Firebase.database

        if (sharedPreferences.getString(TYPE, "client") == "admin") {
            add_lesson.visibility = View.VISIBLE
            getDataForActiveUser(database)
        } else {
            progress.visibility = View.VISIBLE
            database.reference.child("users").child(sharedPreferences.getString(USER_PHONE, "")!!)
                .addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        progress.visibility = View.GONE
                        not_active_view.visibility = View.VISIBLE
                        rv_lessons.visibility = View.GONE
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        progress.visibility = View.GONE

                        val value = snapshot.getValue(User::class.java)
                        if (value != null) {
                            val calender = Calendar.getInstance()
                            val oldCalender = Calendar.getInstance()
                            oldCalender.set(value.year, value.month, value.day)
                            if (calender.after(oldCalender)) {
                                not_active_view.visibility = View.VISIBLE
                                rv_lessons.visibility = View.GONE
                            } else {
                                not_active_view.visibility = View.GONE
                                getDataForActiveUser(database)
                            }
                        } else {
                            not_active_view.visibility = View.VISIBLE
                            rv_lessons.visibility = View.GONE
                        }
                    }

                })
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
                    val t: GenericTypeIndicator<ArrayList<Lesson>> =
                        object : GenericTypeIndicator<ArrayList<Lesson>>() {}

                    val lessons = snapshot.getValue(t)
                    progress.visibility = View.GONE

                    rv_lessons.visibility = View.VISIBLE
                    if (lessons != null) {
                        add_lesson.setOnClickListener {
                            val intent = Intent(this@LessonsActivity, AddLessonActivity::class.java)
                            intent.putExtra("listSize", lessons.size.toString())
                            startActivity(intent)
                        }
                        val teamLessons = lessons.filter { it.team == team }
                        rv_lessons.adapter = LessonsAdapter(teamLessons)
                    }else{
                        add_lesson.setOnClickListener {
                            val intent = Intent(this@LessonsActivity, AddLessonActivity::class.java)
                            intent.putExtra("listSize",  "0")
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
