package com.example.mobiledevelopment

import android.app.Application
import com.example.mobiledevelopment.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BugsApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@BugsApplication)
            modules(appModule)
        }
    }
}