package com.mr.ahmedlearninapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.mr.ahmedlearninapp.SplashActivity.Companion.database
import java.util.*

class ExamsAdapter(private val exams: List<Exam>) :
    RecyclerView.Adapter<ExamsAdapter.ViewHolder>() {

    // holder class to hold reference
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        //get view reference
        var name = view.findViewById(R.id.name) as TextView
        var editIcon = view.findViewById(R.id.edit) as ImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // create view holder to hold reference
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.exam_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //set values
        holder.name.text = exams[position].name
        val sharedPreferences by lazy {
            holder.itemView.context.getSharedPreferences(
                "main",
                Context.MODE_PRIVATE
            )
        }

        holder.editIcon.visibility =
            if (sharedPreferences.getString(TYPE, "") == "admin") View.VISIBLE else View.GONE
        holder.editIcon.setOnClickListener {
            val intent = Intent(holder.itemView.context, EditExamActivity::class.java)
            intent.putExtra(EXAM_ID, exams[position].id)
            holder.itemView.context.startActivity(intent)
        }
        holder.itemView.setOnClickListener {
            getUserProfileAndCheckStatusToOpen(
                holder.itemView.context,
                exams[position].id,
                exams,
                holder,
                position
            )

        }
    }

    override fun getItemCount(): Int {
        return exams.size
    }

    private fun getUserProfileAndCheckStatusToOpen(
        context: Context,
        examId: Int,
        exams: List<Exam>,
        holder: ViewHolder,
        position: Int
    ) {
        val sharedPreferences by lazy { context.getSharedPreferences("main", Context.MODE_PRIVATE) }
        database.reference.child("users").child(sharedPreferences.getString(USER_PHONE, "")!!)
            .addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.getValue(User::class.java)
                    if (value?.completedExamsIds?.contains(examId) == true) {
                        Toast.makeText(
                            context,
                            " انت امتحنت الامتحان دة قبل كدا ",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        checkExamStatus(exams, holder, position)
                    }
                }

            })
    }

    private fun checkExamStatus(
        exams: List<Exam>,
        holder: ViewHolder,
        position: Int
    ) {
        val calendarStartExam = Calendar.getInstance()
        val calendarEndExam = Calendar.getInstance()
        val currentCalendar = Calendar.getInstance()
        calendarStartExam.set(
            exams[position].year, exams[position].month, exams[position].day,
            exams[position].startHour, exams[position].startMin
        )

        calendarEndExam.set(
            exams[position].year, exams[position].month, exams[position].day,
            exams[position].endHour, exams[position].endMin
        )

        when {
            exams[position].endHour == 0 && exams[position].endMin == 0 -> {
                val intent = Intent(holder.itemView.context, WebViewActivity::class.java)
                intent.putExtra("exam_id", exams[position].id)
                intent.putExtra("exam_url", exams[position].url)
                intent.putExtra("isFromUnits", false)
                intent.putExtra("endHour", exams[position].endHour)
                intent.putExtra("endMin", exams[position].endMin)
                holder.itemView.context.startActivity(intent)
            }
            currentCalendar.before(calendarStartExam) -> {
                val startDate =
                    "${this.exams[position].year}/${this.exams[position].month}/${this.exams[position].day}"
                val startTime =
                    "${this.exams[position].startHour} : ${this.exams[position].startMin}"
                Toast.makeText(
                    holder.itemView.context,
                    "  الاختبار سوف يبدآ يوم $startDate   الساعة $startTime ",
                    Toast.LENGTH_SHORT
                ).show()
            }
            currentCalendar.time.after(calendarEndExam.time) -> {
                Toast.makeText(
                    holder.itemView.context,
                    holder.itemView.context.getString(R.string.finish_exam),
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
                val intent = Intent(holder.itemView.context, WebViewActivity::class.java)
                intent.putExtra("exam_id", exams[position].id)
                intent.putExtra("exam_url", exams[position].url)
                intent.putExtra("isFromUnits", true)
                intent.putExtra("endHour", exams[position].endHour)
                intent.putExtra("endMin", exams[position].endMin)
                holder.itemView.context.startActivity(intent)
            }
        }

    }
}