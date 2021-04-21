package com.mr.ahmedlearninapp.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.mr.ahmedlearninapp.*
import kotlinx.android.synthetic.main.test_list_fragment.*

class TestListFragment : Fragment() {

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
        val root = inflater.inflate(R.layout.test_list_fragment, container, false)
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

        SplashActivity.database.reference.child("exams")
            .addListenerForSingleValueEvent(object :
                ValueEventListener {

                override fun onCancelled(error: DatabaseError) {
                    progress.visibility = View.GONE
                    rv_lessons.visibility = View.VISIBLE
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val t: GenericTypeIndicator<HashMap<String, Exam>> =
                            object : GenericTypeIndicator<HashMap<String, Exam>>() {}

                        val lessons = snapshot.getValue(t)?.values
                        if (lessons != null) {
                            val teamLessons = lessons.filter { it.team == team }
                            rv_lessons.adapter = ExamsAdapter(teamLessons)
                        }
                        progress.visibility = View.GONE
                        rv_lessons.visibility = View.VISIBLE
                    } catch (e: Exception) {
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
        fun newInstance(): TestListFragment {
            return TestListFragment()
        }
    }
}