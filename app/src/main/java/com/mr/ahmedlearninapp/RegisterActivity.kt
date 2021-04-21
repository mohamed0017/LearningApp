package com.mr.ahmedlearninapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.mr.ahmedlearninapp.SplashActivity.Companion.database
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {

    private val sharedPreferences by lazy { getSharedPreferences("main", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        title = getString(R.string.new_account)

        val teams = resources.getStringArray(R.array.teams).toMutableList()
        registerTeamSpinner.attachDataSource(teams)

        registerBut.setOnClickListener {
            val name = registerName.text.toString()
            val phone = registerPhone.text.toString()
            val email = registerEmail.text.toString()
            val pass = registerPass.text.toString()
            val team = registerTeamSpinner.selectedIndex

            when {
                name.isEmpty() -> registerName.error = getString(R.string.required)
                phone.length < 10 || !phone.isValidPhone() -> registerPhone.error = getString(R.string.required)
                pass.isEmpty() -> registerPass.error = getString(R.string.required)
                team == 0 -> Toast.makeText(
                    this@RegisterActivity,
                    "اهتر السنة الدراسية",
                    Toast.LENGTH_SHORT
                ).show()
                else -> {
                    registerProgress.visibility = View.VISIBLE
                    registerBut.visibility = View.INVISIBLE
                    // Write a message to the database
                    val user = User(
                        name = name,
                        phone = phone,
                        email = email,
                        password = pass,
                        team = team
                    )
                    database.reference.child("users").child(phone)
                        .addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {
                                registerProgress.visibility = View.GONE
                                registerBut.visibility = View.VISIBLE
                                error.message
                            }

                            override fun onDataChange(snapshot: DataSnapshot) {
                                val value = snapshot.getValue<Any>()
                                if (value == null) {
                                    saveNewUser(database, user)
                                } else {
                                    registerProgress.visibility = View.GONE
                                    registerBut.visibility = View.VISIBLE
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        "المستخدم موجود بالفعل حاول تسجيل الدخول ",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                        })

                }
            }
        }
    }

    private fun saveNewUser(
        database: FirebaseDatabase,
        user: User
    ) {
        database.reference.child("users").child(user.phone).setValue(user).addOnCompleteListener {
            registerProgress.visibility = View.GONE
            registerBut.visibility = View.VISIBLE

            it.addOnSuccessListener {
                sharedPreferences.edit().apply {
                    putString(USER_PHONE,user.phone)
                    putString(USER_NAME,user.name)
                    putInt(TEAM,user.team)
                    putString(TYPE,user.type)
                    putString(User_PROFILE, Gson().toJson(user))
                    apply()
                }
                startActivity(Intent(this, NotActiveUserActivity::class.java))
                finish()
            }

            it.addOnFailureListener {
                it.printStackTrace()
            }
        }
    }
}

 data  class User(
    val name: String = "",
    val phone: String = "",
    val email: String  = "",
    val password: String = "",
    val team: Int = 0,
    val type: String = "client",
    val expiryAt: String = "",
    val deviceId: String = "",
    val day: Int = 0,
    val month: Int = 0,
    val year: Int = 0,
    val time : Long = 0,
    val completedExamsIds : List<Int> = emptyList()
)