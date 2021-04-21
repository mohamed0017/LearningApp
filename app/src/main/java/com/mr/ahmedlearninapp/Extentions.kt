package com.mr.ahmedlearninapp

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

fun String.isValidPhone() = android.util.Patterns.PHONE.matcher(this).matches()



inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commit()
}
