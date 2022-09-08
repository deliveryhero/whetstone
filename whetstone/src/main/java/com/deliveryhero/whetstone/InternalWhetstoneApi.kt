package com.deliveryhero.whetstone

@Retention(value = AnnotationRetention.BINARY)
@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message = "This is an internal API that should not be used directly. No compatibility guarantees " +
        "are provided. It is recommended to report your use-case of internal API so stable API" +
        " could be provided instead."
)
public annotation class InternalWhetstoneApi
