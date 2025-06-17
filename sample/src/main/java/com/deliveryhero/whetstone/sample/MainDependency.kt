package com.deliveryhero.whetstone.sample

import javax.inject.Inject

class MainDependency @Inject constructor() {
    fun getMessage(title: String): String = "$title message!"
}
