package com.deliveryhero.whetstone.compiler

import org.jetbrains.kotlin.name.FqName

internal object FqNames {
    @JvmField val APPLICATION_COMPONENT = FqName("com.deliveryhero.whetstone.app.ApplicationComponent")
    @JvmField val APPLICATION_SCOPE = FqName("com.deliveryhero.whetstone.app.ApplicationScope")
    @JvmField val CONTRIBUTES_APP = FqName("com.deliveryhero.whetstone.app.ContributesAppInjector")
    @JvmField val CONTRIBUTES_INJECTOR = FqName("com.deliveryhero.whetstone.injector.ContributesInjector")
    @JvmField val AUTO_INJECTOR = FqName("com.deliveryhero.whetstone.meta.AutoInjectorBinding")
    @JvmField val AUTO_INSTANCE = FqName("com.deliveryhero.whetstone.meta.AutoInstanceBinding")
    @JvmField val SINGLE_IN = FqName("com.deliveryhero.whetstone.SingleIn")
    @JvmField val APPLICATION = FqName("android.app.Application")
}
