package com.mr.ahmedlearninapp

import android.app.Application
import android.content.Context
import com.mr.ahmedlearninapp.LocaleHelper.ARABIC_LANGUAGE

class App : Application() {

    override fun onCreate() {
        super.onCreate()

    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(LocaleHelper.setLocale(base,ARABIC_LANGUAGE))
    }
}