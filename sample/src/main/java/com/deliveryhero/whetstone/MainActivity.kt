package com.deliveryhero.whetstone

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.deliveryhero.whetstone.injector.ContributesInjector
import com.deliveryhero.whetstone.scope.ActivityScope
import com.deliveryhero.whetstone.viewmodel.ViewModelFactoryProducer
import com.deliveryhero.whetstone.viewmodel.createViewModelFactory
import javax.inject.Inject

@ContributesInjector(ActivityScope::class)
public class MainActivity : AppCompatActivity() {

    @Inject
    public lateinit var viewModelFactoryProducer: ViewModelFactoryProducer
    private val viewModel by viewModels<MainViewModel> {
        viewModelFactoryProducer.createViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Whetstone.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Toast.makeText(this, viewModel.getHelloWorld(), Toast.LENGTH_SHORT).show()
    }
}
