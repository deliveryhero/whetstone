package com.deliveryhero.whetstone.logging

import android.app.Activity
import android.app.Application
import android.app.Service
import android.view.View

public sealed class InjectedComponent {
    public class InjectedActivity(public val activity: Activity) : InjectedComponent()
    public class InjectedService(public val service: Service) : InjectedComponent()
    public class InjectedView(public val view: View) : InjectedComponent()
    public class InjectedApplication(public val app: Application) : InjectedComponent()
}
