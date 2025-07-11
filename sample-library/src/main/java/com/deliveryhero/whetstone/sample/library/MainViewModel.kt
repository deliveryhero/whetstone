package com.deliveryhero.whetstone.sample.library

import androidx.lifecycle.ViewModel
import com.deliveryhero.whetstone.viewmodel.ContributesViewModel
import javax.inject.Inject

@ContributesViewModel
public class MainViewModel @Inject constructor(
    private val dependency: MainDependency
) : ViewModel() {

    public fun getHelloWorld(): String = dependency.getMessage("Hello world")
}
