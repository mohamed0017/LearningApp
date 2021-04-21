package com.mr.ahmedlearninapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.mr.ahmedlearninapp.SplashActivity.Companion.database
import im.delight.android.webview.AdvancedWebView
import kotlinx.android.synthetic.main.activity_web_view.*
import java.util.*
import java.util.Calendar.HOUR
import java.util.Calendar.MINUTE

class WebViewActivity : AppCompatActivity(), AdvancedWebView.Listener {

    private val isFromUnits by lazy { intent.getBooleanExtra("isFromUnits", false) }
    private val endHour by lazy { intent.getIntExtra("endHour", 0) }
    private val endMin by lazy { intent.getIntExtra("endMin", 0) }
    private val examId by lazy { intent.getIntExtra("exam_id", 0) }
    private lateinit var mainHandler: Handler
    private val sharedPreferences by lazy { getSharedPreferences("main", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
      /*  window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )*/
        mainHandler = Handler(Looper.getMainLooper())

        webview.setDesktopMode(true)
        webview.invokeZoomPicker()
        webview.setListener(this, this);
        webview.setMixedContentAllowed(false);
        webview.loadUrl(intent.getStringExtra("exam_url"))

    }

    private val updateTextTask = object : Runnable {
        override fun run() {
            minusOneSecond()
            mainHandler.postDelayed(this, 1000 * 60)
        }
    }

    fun minusOneSecond() {

        if (isFromUnits) {
            val currentCalendar = Calendar.getInstance()
            val currentHour = currentCalendar.get(HOUR)
            val currentMin = currentCalendar.get(MINUTE)

            if (endHour == currentHour && currentMin == endMin - 5) {
                val errorMessage = getString(R.string.finish_exam_error)

                val dialog = AlertDialog.Builder(this)
                dialog.setCancelable(false)
                dialog.setTitle(R.string.alert)
                dialog.setMessage(errorMessage.replace("#@", "5 دقائق من الان"))
                dialog.setNegativeButton(
                    R.string.ok
                ) { dialog1, which -> dialog1.dismiss() }
                dialog.show()

            } else if (endHour == currentHour && currentMin >= endMin - 1) {
                val errorMessage = getString(R.string.finish_exam_error)
                val dialog = AlertDialog.Builder(this)
                dialog.setCancelable(false)
                dialog.setTitle(R.string.alert)
                dialog.setMessage(errorMessage.replace("#@", " دقيقة من الان"))
                dialog.setNegativeButton(
                    R.string.ok
                ) { dialog1, which -> dialog1.dismiss() }
                dialog.show()

            } else if (currentHour == endHour && currentMin > endMin) {
                showErrorDialog()
            } else if (currentHour > endHour) {
                showErrorDialog()
            }
            /*    Handler().postDelayed({
                    val dialog = AlertDialog.Builder(this)
                    dialog.setCancelable(false)
                    dialog.setTitle(R.string.alert)
                    dialog.setMessage(R.string.finish_exam)
                    dialog.setNegativeButton(
                        R.string.ok
                    ) { dialog1, which -> finish() }
                    dialog.show()
                }, 10000)*/
        }

    }

    private fun showErrorDialog() {
        val errorMessage = getString(R.string.finish_exam)
        val dialog = AlertDialog.Builder(this)
        dialog.setCancelable(false)
        dialog.setTitle(R.string.alert)
        dialog.setMessage(errorMessage)
        dialog.setNegativeButton(
            R.string.ok
        ) { dialog1, which -> finish() }
        dialog.show()
    }

    @SuppressLint("NewApi")
    override fun onResume() {
        super.onResume()
        webview.onResume()
        if (isFromUnits)
            mainHandler.post(updateTextTask)
        // ...
    }

    @SuppressLint("NewApi")
    override fun onPause() {
        webview.onPause()
        if (isFromUnits)
            mainHandler.removeCallbacks(updateTextTask)
        // ...
        super.onPause()
    }

    override fun onDestroy() {
        webview.onDestroy()
        // ...
        super.onDestroy()
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        intent: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, intent)
        webview.onActivityResult(requestCode, resultCode, intent)
    }

    override fun onBackPressed() {
        //  if (!webview.onBackPressed()) {
        //    return
        //}
        // ...
        super.onBackPressed()
    }

    override fun onPageStarted(url: String?, favicon: Bitmap?) {
        Log.e("onPageStarted", url.toString())
    }

    override fun onPageFinished(url: String?) {
        if (url?.contains("/formResponse") == true && examId != 0) {
            saveUserSubmitAnswer()
        }
    }

    override fun onPageError(
        errorCode: Int,
        description: String?,
        failingUrl: String?
    ) {
    }

    override fun onDownloadRequested(
        url: String?,
        suggestedFilename: String?,
        mimeType: String?,
        contentLength: Long,
        contentDisposition: String?,
        userAgent: String?
    ) {
    }

    override fun onExternalPageRequest(url: String?) {

    }

    private fun saveUserSubmitAnswer() {
        val user =
            Gson().fromJson<User>(sharedPreferences.getString(User_PROFILE, ""), User::class.java)
        val phone = sharedPreferences.getString(USER_PHONE, "0")
        if (user != null)
            updateUserProfile(user, phone)
        else
            database.reference.child("users").child(phone.toString())
                .addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val value = snapshot.getValue(User::class.java) ?: return
                        updateUserProfile(value, phone)
                    }

                })
    }

    private fun updateUserProfile(user: User, phone: String?) {
        database.reference.child("users").child("$phone")
            .setValue(user.copy(completedExamsIds = user.completedExamsIds.plus(examId)))
            .addOnCompleteListener {
                it.addOnSuccessListener {
                }

                it.addOnFailureListener {
                    it.printStackTrace()
                }
            }
    }
}
