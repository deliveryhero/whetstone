package com.deliveryhero.whetstone.compiler

import org.jetbrains.kotlin.name.FqName

internal object FqNames {
    val ACTIVITY = FqName("android.app.Activity")
    val ACTIVITY_SCOPE = FqName("com.deliveryhero.whetstone.scope.ActivityScope")
    val APPCOMPAT_ACTIVITY = FqName("androidx.appcompat.app.AppCompatActivity")
    val APPLICATION = FqName("android.app.Application")
    val APPLICATION_SCOPE = FqName("com.deliveryhero.whetstone.scope.ApplicationScope")
    val COMPONENT_ACTIVITY = FqName("androidx.activity.ComponentActivity")
    val CONTRIBUTES_ANDROID_BINDING = FqName("com.deliveryhero.whetstone.ContributesAndroidBinding")
    val CONTRIBUTES_INJECTOR = FqName("com.deliveryhero.whetstone.injector.ContributesInjector")
    val CORE_COMPONENT_ACTIVITY = FqName("androidx.core.app.ComponentActivity")
    val DEFINE_INJECTOR_BINDING = FqName("com.deliveryhero.whetstone.DefineInjectorBinding")
    val DEFINE_INSTANCE_BINDING = FqName("com.deliveryhero.whetstone.DefineInstanceBinding")
    val DIALOG_FRAGMENT = FqName("androidx.fragment.app.DialogFragment")
    val FRAGMENT = FqName("androidx.fragment.app.Fragment")
    val FRAGMENT_ACTIVITY = FqName("androidx.fragment.app.FragmentActivity")
    val FRAGMENT_SCOPE = FqName("com.deliveryhero.whetstone.scope.FragmentScope")
    val INTENT_SERVICE = FqName("android.app.IntentService")
    val LISTENABLE_WORKER = FqName("androidx.work.ListenableWorker")
    val SERVICE = FqName("android.app.Service")
    val SERVICE_SCOPE = FqName("com.deliveryhero.whetstone.scope.ServiceScope")
    val VIEW = FqName("android.view.View")
    val VIEWMODEL = FqName("androidx.lifecycle.ViewModel")
    val VIEWMODEL_SCOPE = FqName("com.deliveryhero.whetstone.scope.ViewModelScope")
    val VIEW_GROUP = FqName("android.view.ViewGroup")
    val VIEW_SCOPE = FqName("com.deliveryhero.whetstone.scope.ViewScope")
    val WORKER = FqName("androidx.work.Worker")
    val WORKER_SCOPE = FqName("com.deliveryhero.whetstone.workmanager.WorkerScope")
}
