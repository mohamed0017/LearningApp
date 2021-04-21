package com.mr.ahmedlearninapp

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

const val USER_PHONE = "phone"
const val USER_NAME = "name"
const val TEAM = "team"
const val TYPE = "type"
const val User_PROFILE = "User_PROFILE"

class SplashActivity : AppCompatActivity() {

    companion object {
        val database = Firebase.database
    }

    private val sharedPreferences by lazy { getSharedPreferences("main", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        mustUpdate()

    }

    private fun mustUpdate() {

        database.reference.child("current_version")
            .addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val result = snapshot.getValue(Int::class.java) ?: 1

                    if (BuildConfig.VERSION_CODE < result) {
                        val dialog = AlertDialog.Builder(this@SplashActivity)
                        dialog.setCancelable(true)
                        dialog.setTitle(R.string.alert)
                        dialog.setMessage(R.string.must_update)
                        dialog.setPositiveButton(
                            R.string.update
                        ) { dialog1, which ->
                            dialog1.dismiss()
                            val appPackageName =
                                packageName // getPackageName() from Context or Activity object

                            try {
                                startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("market://details?id=$appPackageName")
                                    )
                                )
                            } catch (anfe: ActivityNotFoundException) {
                                startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                                    )
                                )
                            }
                        }
                        dialog.show()
                    } else {

                        Handler().postDelayed({
                            if (sharedPreferences.getString(USER_PHONE, "") == "")
                                startActivity(
                                    Intent(
                                        this@SplashActivity,
                                        OnBoardingActivity::class.java
                                    )
                                )
                            else {
                                if (sharedPreferences.getString(TYPE, "") == "admin")
                                    startActivity(
                                        Intent(
                                            this@SplashActivity,
                                            AdminMainActivity::class.java
                                        )
                                    )
                                else
                                    startActivity(
                                        Intent(
                                            this@SplashActivity,
                                            NotActiveUserActivity::class.java
                                        )
                                    )
                            }
                            finish()

                        }, 600)

                    }
                }

            })
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

}
