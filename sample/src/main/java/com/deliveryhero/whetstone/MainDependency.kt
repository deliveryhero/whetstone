package com.deliveryhero.whetstone

import androidx.lifecycle.ViewModel
import javax.inject.Inject

@ContributesAndroidBinding
public class MainViewModel @Inject constructor(
    private val dependency: MainDependency
) : ViewModel() {
    public fun getHelloWorld(): String = dependency.getMessage("Hello world")
}

public class MainDependency @Inject constructor() {
    public fun getMessage(title: String): String = "$title message!"
}
