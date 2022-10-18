package com.deliveryhero.whetstone.event

public sealed class InjectedComponent {
    public class Activity(public val activity: android.app.Activity) : InjectedComponent()
    public class Service(public val service: android.app.Service) : InjectedComponent()
    public class View(public val view: android.view.View) : InjectedComponent()
    public class Application(public val app: android.app.Application) : InjectedComponent()
}
