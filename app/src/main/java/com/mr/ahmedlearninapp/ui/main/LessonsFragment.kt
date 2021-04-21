package com.mr.ahmedlearninapp.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.database.*
import com.mr.ahmedlearninapp.*
import com.mr.ahmedlearninapp.R
import com.mr.ahmedlearninapp.SplashActivity.Companion.database
import kotlinx.android.synthetic.main.lessons_list_fragment.*

class LessonsFragment : Fragment() {

    private val sharedPreferences by lazy {
        context?.getSharedPreferences(
            "main",
            Context.MODE_PRIVATE
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.lessons_list_fragment, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getDataForActiveUser()

    }

    private fun getDataForActiveUser() {
        progress.visibility = View.VISIBLE
        rv_lessons.visibility = View.GONE
        val team = sharedPreferences?.getInt(com.mr.ahmedlearninapp.TEAM, 3) ?: 3

        database.reference.child("lessons")
            .addValueEventListener(object :
                ValueEventListener {

                override fun onCancelled(error: DatabaseError) {
                    progress.visibility = View.GONE
                    rv_lessons.visibility = View.VISIBLE
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val t: GenericTypeIndicator<HashMap<String, Lesson>> =
                            object : GenericTypeIndicator<HashMap<String, Lesson>>() {}
                        val lessons = snapshot.getValue(t)?.values
                        if (!lessons.isNullOrEmpty()) {
                            add_lesson.setOnClickListener {
                                val intent = Intent(requireContext(), AddLessonActivity::class.java)
                                val sortedLessons = lessons.sortedBy { it.id }
                                intent.putExtra("itemId", (sortedLessons.last().id + 1))
                                startActivity(intent)
                            }
                            val teamLessons = lessons.filter { it.team == team }.sortedBy { it.id }
                            rv_lessons.adapter = LessonsAdapter(teamLessons)
                        } else {
                            add_lesson.setOnClickListener {
                                val intent = Intent(requireContext(), AddLessonActivity::class.java)
                                intent.putExtra("itemId", "0")
                                startActivity(intent)
                            }
                        }
                        progress.visibility = View.GONE
                        rv_lessons.visibility = View.VISIBLE
                    } catch (e: Exception) {
                    }
                }

            })
    }

    override fun onResume() {
        super.onResume()
        if (sharedPreferences?.getString(TYPE, "client") == "admin")
            add_lesson.visibility = View.VISIBLE
    }

    companion object {

        private const val TEAM = "team"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(): LessonsFragment {
            return LessonsFragment()
            //.apply {
            //arguments = Bundle().apply {
            //   putInt(TEAM, team)
            // }
        }
    }
}
