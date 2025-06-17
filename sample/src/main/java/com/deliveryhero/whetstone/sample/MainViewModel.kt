package com.deliveryhero.whetstone.sample

import androidx.lifecycle.ViewModel
import com.deliveryhero.whetstone.viewmodel.ContributesViewModel
import javax.inject.Inject

@ContributesViewModel
class MainViewModel @Inject constructor(
    private val dependency: MainDependency
) : ViewModel() {
    fun getHelloWorld(): String = dependency.getMessage("Hello world")
}
