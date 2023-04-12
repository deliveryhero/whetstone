package com.deliveryhero.whetstone.sample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.deliveryhero.whetstone.Whetstone
import com.deliveryhero.whetstone.activity.ContributesActivityInjector
import com.deliveryhero.whetstone.compose.injectedViewModel
import com.deliveryhero.whetstone.sample.databinding.ActivityMainBinding
import com.deliveryhero.whetstone.viewmodel.injectedViewModel
import javax.inject.Inject

@ContributesActivityInjector
class MainActivity : AppCompatActivity() {

    private val viewModel by injectedViewModel<MainViewModel>()
    private val serviceIntent by lazy { Intent(this, MainService::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        Whetstone.inject(this)
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.composeView.setContent {
            MainScreen(
                onClick = {
                    startActivity(Intent(this, BasicActivity::class.java))
                }
            )
        }

        Toast.makeText(this, viewModel.getHelloWorld(), Toast.LENGTH_SHORT).show()
        startService(serviceIntent)
        val request = OneTimeWorkRequest.from(MainWorker::class.java)
        WorkManager.getInstance(this).enqueue(request)
    }

    override fun onDestroy() {
        stopService(serviceIntent)
        WorkManager.getInstance(this).cancelAllWork()
        super.onDestroy()
    }
}

@Composable
fun MainScreen(
    viewModel: MainViewModel? = injectedViewModel(),
    onClick: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onClick?.invoke() },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Compose")
        Text(text = viewModel?.getHelloWorld().orEmpty())
    }
}


@ContributesActivityInjector
class BasicActivity: Activity() {

    @Inject lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        Whetstone.inject(this)
        super.onCreate(savedInstanceState)
        val message = "${viewModel.getHelloWorld()} from Basic activity"
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}
