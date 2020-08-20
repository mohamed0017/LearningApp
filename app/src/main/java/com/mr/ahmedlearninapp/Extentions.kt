package com.mr.ahmedlearninapp

fun String.isValidPhone() = android.util.Patterns.PHONE.matcher(this).matches()

