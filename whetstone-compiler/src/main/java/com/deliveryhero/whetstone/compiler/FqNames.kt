package com.deliveryhero.whetstone.compiler

import com.squareup.kotlinpoet.ClassName

internal object FqNames {
    const val APPLICATION_COMPONENT = "com.deliveryhero.whetstone.app.ApplicationComponent"
    const val APPLICATION_SCOPE = "com.deliveryhero.whetstone.app.ApplicationScope"
    const val CONTRIBUTES_APP = "com.deliveryhero.whetstone.app.ContributesAppInjector"
    const val CONTRIBUTES_INJECTOR = "com.deliveryhero.whetstone.injector.ContributesInjector"
    const val AUTO_INJECTOR = "com.deliveryhero.whetstone.meta.AutoInjectorBinding"
    const val AUTO_INSTANCE = "com.deliveryhero.whetstone.meta.AutoInstanceBinding"
    const val SINGLE_IN_SIMPLE = "SingleIn"
    const val SINGLE_IN_PACKAGE = "com.deliveryhero.whetstone"
    const val APPLICATION = "android.app.Application"

    val APPLICATION_COMPONENT_CN = ClassName("com.deliveryhero.whetstone.app", "ApplicationComponent")
    val APPLICATION_SCOPE_CN = ClassName("com.deliveryhero.whetstone.app", "ApplicationScope")
    val SINGLE_IN_CN = ClassName(SINGLE_IN_PACKAGE, SINGLE_IN_SIMPLE)
    val APPLICATION_CN = ClassName("android.app", "Application")
}
