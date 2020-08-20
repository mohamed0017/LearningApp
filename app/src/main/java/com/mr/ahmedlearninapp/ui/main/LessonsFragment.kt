package com.mr.ahmedlearninapp.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.mr.ahmedlearninapp.Lesson
import com.mr.ahmedlearninapp.LessonsAdapter
import com.mr.ahmedlearninapp.R
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

        val database = Firebase.database
        getDataForActiveUser(database)

    }

    private fun getDataForActiveUser(database: FirebaseDatabase) {
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
                    val t: GenericTypeIndicator<ArrayList<Lesson>> =
                        object : GenericTypeIndicator<ArrayList<Lesson>>() {}

                    val lessons = snapshot.getValue(t)
                    progress.visibility = View.GONE

                    rv_lessons.visibility = View.VISIBLE
                    if (lessons != null) {
                        val teamLessons = lessons.filter { it.team == team }
                        rv_lessons.adapter = LessonsAdapter(teamLessons)
                    }
                }

            })
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
