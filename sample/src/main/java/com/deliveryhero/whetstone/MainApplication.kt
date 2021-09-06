package com.deliveryhero.whetstone

import android.app.Application

public class MainApplication : Application() {

    override fun onCreate() {
        Whetstone.initialize { DaggerGeneratedApplicationComponent.factory().create(this) }
        super.onCreate()
    }
}