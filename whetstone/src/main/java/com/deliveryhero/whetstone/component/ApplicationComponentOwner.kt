package com.deliveryhero.whetstone.component

/**
 * Interface that should be implemented by the [android.app.Application] to supply
 * the instance of the root [ApplicationComponent].
 *
 * It's strongly recommended that [applicationComponent] always returns the same
 * instance across multiple invocations.
 */
public interface ApplicationComponentOwner {

    public val applicationComponent: ApplicationComponent
}
