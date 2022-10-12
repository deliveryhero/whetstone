package com.deliveryhero.whetstone.logging

import android.app.Activity
import android.app.Service

public sealed class InjectedComponent {
    public class InjectedActivity(public val activity: Activity) : InjectedComponent()
    public class InjectedService(public val service: Service) : InjectedComponent()
}