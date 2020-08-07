package com.mr.ahmedlearninapp

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class LessonsActivity : AppCompatActivity() {

    private val sharedPreferences by lazy { getSharedPreferences("main", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        title = getString(R.string.lessons)
        val database = Firebase.database
        val team =
            if (intent.getIntExtra(TEAM, 0) != 0) intent.getIntExtra(TEAM, 0)
            else sharedPreferences.getInt(TEAM, 0)

        database.reference.child("lessons")
            .addValueEventListener(object :
                ValueEventListener {

                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val t: GenericTypeIndicator<Map<String, Lesson>> =
                        object : GenericTypeIndicator<Map<String, Lesson>>() {}

                    val lessons = snapshot.getValue(t)
                    if (lessons != null) {
                        val teamLessons = lessons.values.filter { it.team == team }
                        rv_lessons.adapter = LessonsAdapter(teamLessons)
                    }
                }

            })
    }
}
