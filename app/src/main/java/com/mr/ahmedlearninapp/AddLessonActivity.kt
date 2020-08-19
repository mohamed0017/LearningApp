package com.mr.ahmedlearninapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_add_lesson.*
import java.util.*

class AddLessonActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_lesson)

        title = getString(R.string.add_new_lesson)
        val database = Firebase.database

        val terms = resources.getStringArray(R.array.terms).toMutableList()
        termSpinner.attachDataSource(terms)

        val teams = resources.getStringArray(R.array.teams).toMutableList()
        teamSpinner.attachDataSource(teams)

        saveBut.setOnClickListener {
            val name = lesson_name.text.toString()
            val url = lesson_url.text.toString()
            val pdf = lesson_pdf.text.toString()
            val term = termSpinner.selectedIndex
            val team = teamSpinner.selectedIndex

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
                        team = team
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

        database.reference.child("lessons").child(intent.getStringExtra("listSize") ?: getId()).setValue(lesson).addOnCompleteListener {
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

data class Lesson(
    val name: String = "",
    val url: String = "",
    val pdf: String  = "",
    val team: Int  = 0,
    val term: Int  = 0
)

fun getId(): String {
    val calendar = Calendar.getInstance()
   return "${calendar.get(Calendar.YEAR)}${calendar.get(Calendar.MONTH)}${calendar.get(Calendar.DAY_OF_MONTH)}${calendar.get(Calendar.HOUR)}${ calendar.get(Calendar.MINUTE)}${calendar.get(Calendar.SECOND)}"
}