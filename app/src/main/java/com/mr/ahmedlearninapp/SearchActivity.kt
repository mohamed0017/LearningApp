package com.mr.ahmedlearninapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.activity_search.name
import kotlinx.android.synthetic.main.activity_search.phone
import java.util.*

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        title = getString(R.string.search)
        et_search.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(et_search.text.toString())
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    private fun performSearch(text: String) {
        if (text.isEmpty()) {
            et_search.error = getString(R.string.required)
        } else {
            searchForUser(text)
        }
    }

    private fun searchForUser(text: String) {
        val database = Firebase.database
        database.reference.child("users").child(text)
            .addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {

                    val value = snapshot.getValue(User::class.java)
                    if (value == null) {
                        container.visibility = View.GONE

                        Toast.makeText(
                            this@SearchActivity,
                            "مستخدم غير مسجل لدينا ",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val calender = Calendar.getInstance()
                        val oldCalender = Calendar.getInstance()
                        container.visibility = View.VISIBLE
                        name.text = value.name
                        phone.text = value.phone
                        team.text = resources.getStringArray(R.array.teams)[value.team]
                        expiryAt.text = "${getString(R.string.expiry_date)}  ${value.expiryAt}"
                        oldCalender.set(value.year, value.month, value.day)
                        if (calender.after(oldCalender))
                            status.text = "${getString(R.string.status)}  انتهي الاشتراك "
                        else
                            status.text = "${getString(R.string.status)}  اشتراك فعال "

                        activation.setOnClickListener {
                            calender.add(Calendar.DAY_OF_MONTH, 30)
                            val today =
                                "${calender.get(Calendar.DAY_OF_MONTH)}/${calender.get(Calendar.MONTH) + 1}/${calender.get(
                                    Calendar.YEAR
                                )}"
                            updateUser(
                                database,
                                value.copy(
                                    expiryAt = today,
                                    day = calender.get(Calendar.DAY_OF_MONTH),
                                    month = calender.get(Calendar.MONTH),
                                    year = calender.get(Calendar.YEAR)
                                )
                            )
                        }

                        resetDevice.setOnClickListener {
                            updateUser(database, value.copy(deviceId = ""))
                        }

                        resetPassword.setOnClickListener {
                            updateUser(database, value.copy(password = ""))

                        }
                    }
                }

            })
    }

    private fun updateUser(
        database: FirebaseDatabase,
        user: User
    ) {
        database.reference.child("users").child(user.phone).setValue(user).addOnCompleteListener {

            it.addOnSuccessListener {
                Toast.makeText(
                    this@SearchActivity,
                    getString(R.string.update_success),
                    Toast.LENGTH_SHORT
                ).show()
                searchForUser(user.phone)
            }

            it.addOnFailureListener {
                Toast.makeText(
                    this@SearchActivity,
                    getString(R.string.error),
                    Toast.LENGTH_SHORT
                ).show()
                //  it.printStackTrace()
            }
        }
    }
}
