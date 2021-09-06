package com.deliveryhero.whetstone

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.deliveryhero.whetstone.injector.ContributesInjector
import com.deliveryhero.whetstone.scope.ActivityScope
import javax.inject.Inject

@ContributesInjector(ActivityScope::class)
public class MainActivity : AppCompatActivity() {

    @Inject
    public lateinit var helloWorldFactory: HelloWorldFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        Whetstone.inject(activity = this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<TextView>(R.id.hello_world_text_view).text = helloWorldFactory.getText()
    }
}

public class HelloWorldFactory @Inject constructor() {
    public fun getText(): String = "Updated Hello World!"
}
