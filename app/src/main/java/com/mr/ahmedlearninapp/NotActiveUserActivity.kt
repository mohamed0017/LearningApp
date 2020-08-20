package com.mr.ahmedlearninapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.android.synthetic.main.not_active_user_view.*
import java.util.*

class NotActiveUserActivity : AppCompatActivity() {

    private val sharedPreferences by lazy { getSharedPreferences("main", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.not_active_user_view)

        supportActionBar?.hide()
        val database = Firebase.database
        getPhone(database)
        checkIfUserActive(database)
    }

    private fun checkIfUserActive(database: FirebaseDatabase) {
        not_active_view_content.visibility = View.GONE
        progress.visibility = View.VISIBLE
        database.reference.child("users").child(sharedPreferences.getString(USER_PHONE, "")!!)
            .addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    supportActionBar?.show()
                    progress.visibility = View.GONE
                    not_active_view_content.visibility = View.VISIBLE
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    progress.visibility = View.GONE
                    val value = snapshot.getValue(User::class.java)
                    if (value != null) {
                        val calender = Calendar.getInstance()
                        val oldCalender = Calendar.getInstance()
                        oldCalender.set(value.year, value.month, value.day)
                        if (calender.after(oldCalender)) {
                            supportActionBar?.show()
                            not_active_view_content.visibility = View.VISIBLE
                        } else {
                            startActivity(
                                Intent(
                                    this@NotActiveUserActivity,
                                    StudentHomeActivity::class.java
                                )
                            )
                            finish()
                        }
                    } else {
                        supportActionBar?.show()
                        not_active_view_content.visibility = View.VISIBLE
                    }
                }

            })
    }

    private fun getPhone(database: FirebaseDatabase) {

        database.reference.child("phone")
            .addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val number = snapshot.getValue(String::class.java) ?: "+201221596213"

                    whats.setOnClickListener {
                        try {
                            val url = "https://api.whatsapp.com/send?phone=$number"
                            val i = Intent(Intent.ACTION_VIEW)
                            i.data = Uri.parse(url)
                            startActivity(i)
                        } catch (e: Exception) {
                        }

                    }

                    call.setOnClickListener {
                        try {
                            val intent = Intent(Intent.ACTION_DIAL)
                            intent.data = Uri.parse("tel:$number")
                            startActivity(intent)
                        } catch (e: Exception) {
                        }

                    }
                }

            })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)

        // return true so that the menu pop up is opened
        // return true so that the menu pop up is opened
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                sharedPreferences.edit().apply {
                    putString(USER_PHONE, "")
                    putString(USER_NAME, "")
                    putInt(TEAM, 0)
                    putString(TYPE, "")
                    apply()
                }
                startActivity(Intent(this, OnBoardingActivity::class.java))
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
