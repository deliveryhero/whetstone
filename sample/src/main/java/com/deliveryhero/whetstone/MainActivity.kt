package com.deliveryhero.whetstone

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.deliveryhero.whetstone.injector.ContributesInjector
import com.deliveryhero.whetstone.scope.ActivityScope
import javax.inject.Inject

@ContributesInjector(ActivityScope::class)
public class MainActivity : AppCompatActivity() {

    @Inject
    public lateinit var dependency: MainDependency

    override fun onCreate(savedInstanceState: Bundle?) {
        Whetstone.inject(activity = this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Toast.makeText(this, dependency.getUpdatedHelloWorld(), Toast.LENGTH_SHORT).show()
    }
}
