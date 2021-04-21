package com.mr.ahmedlearninapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.mr.ahmedlearninapp.SplashActivity.Companion.database
import kotlinx.android.synthetic.main.activity_add_exam.*
import java.util.*
import kotlin.collections.HashMap

class AddExamActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    private var startTime = false
    private var myDay = 0
    var myMonth: Int = 0
    var myYear: Int = 0
    var myHour: Int = 0
    var myMinute: Int = 0
    var exam = Exam()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_exam)

        title = getString(R.string.add_new_lesson)

        val terms = resources.getStringArray(R.array.terms).toMutableList()
        termSpinner.attachDataSource(terms)

        val teams = resources.getStringArray(R.array.teams).toMutableList()
        teamSpinner.attachDataSource(teams)

        saveBut.setOnClickListener {
            val name = lesson_name.text.toString()
            val url = lesson_exam.text.toString()
            val term = termSpinner.selectedIndex
            val team = teamSpinner.selectedIndex

            when {
                name.isEmpty() -> lesson_name.error = getString(R.string.required)
                url.isEmpty() -> lesson_exam.error = getString(R.string.required)
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
                    val exam1 = exam.copy(
                        name = name,
                        url = url,
                        term = term,
                        team = team
                    )
                    getLastExams(exam1)
                }
            }
        }

        exam_date.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                showPicker()
                startTime = true
            }
        }
        date_time.setOnClickListener {
            showPicker()
            startTime = true
        }
        exam_date.setOnClickListener {
            showPicker()
            startTime = true
        }

        exam_time_limit.setOnFocusChangeListener { v, hasFocus ->
            exam_time_limit.keyListener = null
            if(hasFocus){
                val calendar: Calendar = Calendar.getInstance()
                myHour = calendar.get(Calendar.HOUR)
                myMinute = calendar.get(Calendar.MINUTE)
                val timePickerDialog = TimePickerDialog(
                    this, this, myHour, myMinute,
                    DateFormat.is24HourFormat(this)
                )
                timePickerDialog.show()
                startTime = false
            }
        }
        time_limit.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            myHour = calendar.get(Calendar.HOUR)
            myMinute = calendar.get(Calendar.MINUTE)
            val timePickerDialog = TimePickerDialog(
                this, this, myHour, myMinute,
                DateFormat.is24HourFormat(this)
            )
            timePickerDialog.show()
            startTime = false
        }
        exam_time_limit.setOnClickListener {
            exam_time_limit.keyListener = null
            val calendar: Calendar = Calendar.getInstance()
            myHour = calendar.get(Calendar.HOUR)
            myMinute = calendar.get(Calendar.MINUTE)
            val timePickerDialog = TimePickerDialog(
                this, this, myHour, myMinute,
                DateFormat.is24HourFormat(this)
            )
            timePickerDialog.show()
            startTime = false
        }
    }


    private fun getLastExams(exam: Exam) {
        database.reference.child("exams")
            .addListenerForSingleValueEvent(object :
                ValueEventListener {

                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val t: GenericTypeIndicator<HashMap<String,Exam>> =
                        object : GenericTypeIndicator<HashMap<String,Exam>>() {}

                    val exams = snapshot.getValue(t)?.values?.sortedBy { it.id }
                    if (!exams.isNullOrEmpty()) {
                        saveNewExam(exam, exams.last().id + 1)
                    } else
                        saveNewExam(exam, 0)
                }

            })
    }

    private fun saveNewExam(
        exam: Exam, id: Int
    ) {
        database.reference.child("exams").child("_$id").setValue(exam.copy(id = id))
            .addOnCompleteListener {
                saveProgress.visibility = View.GONE
                saveBut.visibility = View.VISIBLE

                it.addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "تم اضافة الامتحان بنجاح",
                        Toast.LENGTH_SHORT
                    ).show()
                    onBackPressed()
                }

                it.addOnFailureListener {
                    it.printStackTrace()
                }
            }
    }

    private fun showPicker() {
        exam_date.keyListener = null
        val now: Calendar = Calendar.getInstance()
        val dpd = DatePickerDialog(
            this, this,
            now.get(Calendar.YEAR),  // Initial year selection
            now.get(Calendar.MONTH),  // Initial month selection
            now.get(Calendar.DAY_OF_MONTH) // Inital day selection
        )
        dpd.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        myDay = dayOfMonth
        myYear = year
        myMonth = month

        exam = exam.copy(day = myDay, year = myYear, month = myMonth)

        val calendar: Calendar = Calendar.getInstance()
        myHour = calendar.get(Calendar.HOUR)
        myMinute = calendar.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(
            this, this, myHour, myMinute,
            DateFormat.is24HourFormat(this)
        )
        timePickerDialog.show()

    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        myHour = hourOfDay
        myMinute = minute

        exam = if (startTime) {
            exam_date.setText("$myDay/$myMonth/$myYear      $myHour:$myMinute")
            exam.copy(startHour = myHour, startMin = myMinute)

        } else {
            exam_time_limit.setText("$myHour:$myMinute")
            exam.copy(endHour = myHour, endMin = myMinute)
        }

    }
}

data class Exam(
    val id: Int = 0,
    val name: String = "",
    val url: String = "",
    val team: Int = 0,
    val term: Int = 0,
    val day: Int = 0,
    val month: Int = 0,
    val year: Int = 0,
    val startHour: Int = 0,
    val startMin: Int = 0,
    val endHour: Int = 0,
    val endMin: Int = 0,
    val haveEndTime: Boolean = false
)