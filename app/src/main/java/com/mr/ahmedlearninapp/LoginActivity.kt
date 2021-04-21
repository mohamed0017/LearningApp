package com.mr.ahmedlearninapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.mr.ahmedlearninapp.SplashActivity.Companion.database
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val sharedPreferences by lazy { getSharedPreferences("main", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        title = getString(R.string.login)

        loginBut.setOnClickListener {

            val phone = loginPhone.text.toString()
          //  val pass = loginPass.text.toString()

            if (phone.length < 10 || !phone.isValidPhone() ) {
                loginPhone.error = getString(R.string.required)
                return@setOnClickListener
            }/* else if (pass.isEmpty()) {
                loginPass.error = getString(R.string.required)
                return@setOnClickListener
            }*/

            loginProgress.visibility = View.VISIBLE
            loginBut.visibility = View.INVISIBLE

            database.reference.child("users").child(phone)
                .addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        loginProgress.visibility = View.GONE
                        loginBut.visibility = View.VISIBLE
                        //   error.message
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        loginProgress.visibility = View.GONE
                        loginBut.visibility = View.VISIBLE
                        val value = snapshot.getValue(User::class.java)
                        if (value != null /*&& value.password == pass*/) {
                            sharedPreferences.edit().apply {
                                putString(USER_PHONE, value.phone)
                                putString(USER_NAME, value.name)
                                putInt(TEAM, value.team)
                                putString(TYPE, value.type)
                                putString(User_PROFILE, Gson().toJson(value))
                                apply()
                            }
                            if (value.type == "admin") {
                                startActivity(
                                    Intent(
                                        this@LoginActivity,
                                        AdminMainActivity::class.java
                                    )
                                )
                                finish()
                            } else {
                                startActivity(
                                    Intent(
                                        this@LoginActivity,
                                        NotActiveUserActivity::class.java
                                    )
                                )
                                finish()
                            }

                        } else {
                            Toast.makeText(
                                this@LoginActivity,
                                "كلمة السر او كلمة المرور خطآ",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                })
        }

        loginRegister.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            finish()

        }
    }


}
