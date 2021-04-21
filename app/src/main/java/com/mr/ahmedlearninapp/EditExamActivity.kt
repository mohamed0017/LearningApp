package com.mr.ahmedlearninapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.mr.ahmedlearninapp.SplashActivity.Companion.database
import com.google.firebase.database.ktx.getValue
import kotlinx.android.synthetic.main.activity_edit_exam.*
import java.util.*

class EditExamActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener {

    private val examId by lazy { intent?.getIntExtra(EXAM_ID, 0) }
    private var startTime = false
    private var myDay = 0
    var myMonth: Int = 0
    var myYear: Int = 0
    var myHour: Int = 0
    var myMinute: Int = 0
    var exam = Exam()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_exam)

        title = getString(R.string.edit_new_lesson)

        val terms = resources.getStringArray(R.array.terms).toMutableList()
        termSpinner.attachDataSource(terms)

        val teams = resources.getStringArray(R.array.teams).toMutableList()
        teamSpinner.attachDataSource(teams)

        database.reference.child("exams").child("_$examId")
            .addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val examDetails = snapshot.getValue<Exam>() ?: return
                    exam = examDetails
                    lesson_name.setText(examDetails.name)
                    lesson_exam.setText(examDetails.url)
                    exam_date.setText("${examDetails.day}/${examDetails.month}/${examDetails.year}   ${examDetails.startHour}:${examDetails.startMin}")
                    if (exam.endHour != 0) exam_time_limit.setText("${examDetails.endHour}:${examDetails.endMin}")
                    teamSpinner.selectedIndex = examDetails.team
                    termSpinner.selectedIndex = examDetails.term

                }

            })

        exam_date.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
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
            if (hasFocus) {
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

        saveBut.setOnClickListener {
            exam = exam.copy(name = lesson_name.text.toString(), url = lesson_exam.text.toString(),
                team = teamSpinner.selectedIndex, term = termSpinner.selectedIndex)
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
                    val exam1 = exam.copy(
                        name = name,
                        url = url,
                        term = term,
                        team = team
                    )
                    editExam(exam1)
                }
            }
        }
    }

    private fun editExam(
        exam: Exam
    ) {
        database.reference.child("exams").child("_$examId").setValue(exam.copy(id = examId ?: 0))
            .addOnCompleteListener {
                saveProgress.visibility = View.GONE
                saveBut.visibility = View.VISIBLE

                it.addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "تم تعديل الامتحان بنجاح",
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
