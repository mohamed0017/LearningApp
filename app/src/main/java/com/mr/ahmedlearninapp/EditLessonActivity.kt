package com.mr.ahmedlearninapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.mr.ahmedlearninapp.SplashActivity.Companion.database
import kotlinx.android.synthetic.main.activity_add_lesson.*

class EditLessonActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_lesson)
        title = getString(R.string.edit_new_lesson)

        val terms = resources.getStringArray(R.array.terms).toMutableList()
        termSpinner.attachDataSource(terms)

        val teams = resources.getStringArray(R.array.teams).toMutableList()
        teamSpinner.attachDataSource(teams)
        val lessonId = intent.getIntExtra(LESSON_ID, 0)

        database.reference.child("lessons").child("_$lessonId")
            .addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val lessonDetails = snapshot.getValue<Lesson>() ?: return
                    lesson_name.setText(lessonDetails.name)
                    lesson_url.setText(lessonDetails.url)
                    lesson_pdf.setText(lessonDetails.pdf)
                    lesson_exam.setText(lessonDetails.examUrl)
                    teamSpinner.selectedIndex = lessonDetails.team
                    termSpinner.selectedIndex = lessonDetails.term

                }

            })

        saveBut.setOnClickListener {
            val name = lesson_name.text.toString()
            val url = lesson_url.text.toString()
            val pdf = lesson_pdf.text.toString()
            val term = termSpinner.selectedIndex
            val team = teamSpinner.selectedIndex
            val examUrl = lesson_exam.text.toString()

            when {
                name.isEmpty() -> lesson_name.error = getString(R.string.required)
                url.isEmpty() -> lesson_url.error = getString(R.string.required)
                pdf.isEmpty() -> lesson_pdf.error = getString(R.string.required)
                term == 0 -> Toast.makeText(
                    this,
                    "اهتر الترم الدراسي ",
                    Toast.LENGTH_SHORT
                ).show()
                team == 0 -> Toast.makeText(
                    this,
                    "اهتر السنة الدراسية ",
                    Toast.LENGTH_SHORT
                ).show()
                else -> {
                    saveProgress.visibility = View.VISIBLE
                    saveBut.visibility = View.INVISIBLE
                    // Write a message to the database
                    val lesson = Lesson(
                        name = name,
                        url = url,
                        pdf = pdf,
                        term = term,
                        team = team,
                        examUrl = examUrl
                    )
                    saveNewLesson(database, lesson)

                }
            }

        }
    }

    private fun saveNewLesson(
        database: FirebaseDatabase,
        lesson: Lesson
    ) {
        val lessonId = intent.getIntExtra(LESSON_ID,0)
        database.reference.child("lessons").child("_$lessonId").setValue(lesson.copy(id = lessonId))
            .addOnCompleteListener {
                saveProgress.visibility = View.GONE
                saveBut.visibility = View.VISIBLE

                it.addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "تم اضافة الدرس بنجاح",
                        Toast.LENGTH_SHORT
                    ).show()
                    onBackPressed()
                }

                it.addOnFailureListener {
                    it.printStackTrace()
                }
            }
    }
}