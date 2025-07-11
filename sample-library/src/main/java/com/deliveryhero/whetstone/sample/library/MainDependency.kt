package com.deliveryhero.whetstone.sample.library

import javax.inject.Inject

public class MainDependency @Inject constructor() {

    public fun getMessage(title: String): String = "$title message!"
}
