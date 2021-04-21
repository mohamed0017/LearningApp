package com.mr.ahmedlearninapp.ui.active_users

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.mr.ahmedlearninapp.R
import com.mr.ahmedlearninapp.SplashActivity.Companion.database
import com.mr.ahmedlearninapp.User
import kotlinx.android.synthetic.main.activity_active_students.*
import java.util.*
import kotlin.collections.HashMap

class ActiveStudentsActivity : AppCompatActivity() {

    private var allUsers: List<User> = listOf()
    private var teamUsers: List<User> = listOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_active_students)

        val teams = resources.getStringArray(R.array.teams).toMutableList()
        teamSpinner.attachDataSource(teams)

        getAllUsers()

        teamSpinner.setOnSpinnerItemSelectedListener { parent, view, position, id ->
            when (position) {
                1 -> {
                    teamUsers = allUsers.filter { it.team == 1 }
                    updateUsersList()
                }
                2 -> {
                    teamUsers = allUsers.filter { it.team == 2 }
                    updateUsersList()
                }
                3 -> {
                    teamUsers = allUsers.filter { it.team == 3 }
                    updateUsersList()
                }
            }
        }
    }

    private fun updateUsersList() {
        rv_students.adapter =
            ActivateUsersAdapter(teamUsers, object : ActivateUsersAdapter.OnClickListener {
                override fun onClick(user: User) {
                    val calender = Calendar.getInstance()
                    val oldCalender = Calendar.getInstance()
                    oldCalender.set(user.year, user.month, user.day)

                    calender.add(Calendar.DAY_OF_MONTH, 30)
                    val today =
                        "${calender.get(Calendar.DAY_OF_MONTH)}/${calender.get(Calendar.MONTH) + 1}/${calender.get(
                            Calendar.YEAR
                        )}"
                    updateUser(
                        user.copy(
                            expiryAt = today,
                            day = calender.get(Calendar.DAY_OF_MONTH),
                            month = calender.get(Calendar.MONTH),
                            year = calender.get(Calendar.YEAR)
                        )
                    )

                }

            })
    }

    private fun updateUser(
        user: User
    ) {
        database.reference.child("users").child(user.phone).setValue(user).addOnCompleteListener {

            it.addOnSuccessListener {

            }

            it.addOnFailureListener {
                Toast.makeText(
                    this,
                    getString(R.string.error),
                    Toast.LENGTH_SHORT
                ).show()
                //  it.printStackTrace()
            }
        }
    }

    private fun getAllUsers() {
        database.reference.child("users")
            .addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val t: GenericTypeIndicator<HashMap<String, User>> =
                        object : GenericTypeIndicator<HashMap<String, User>>() {}

                    val users = snapshot.getValue(t)?.values
                    if (users != null) {
                        allUsers = users.toList()
                        teamSpinner.selectedIndex = 1
                        teamUsers = allUsers.filter { it.team == 1 }
                        updateUsersList()
                    }
                }

            })
    }

}
