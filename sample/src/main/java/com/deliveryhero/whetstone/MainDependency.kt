package com.deliveryhero.whetstone

import javax.inject.Inject

public class MainDependency @Inject constructor() {
    public fun getToast(): String = "Activity Toast!"
    public fun getUpdatedHelloWorld(): String = "Updated Hello World!"
}
