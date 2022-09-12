package com.deliveryhero.whetstone.sample

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.deliveryhero.whetstone.compose.injectedViewModel
import com.deliveryhero.whetstone.sample.databinding.ActivityMainBinding
import com.deliveryhero.whetstone.viewmodel.injectedViewModel

public class MainActivity : AppCompatActivity() {

    private val viewModel by injectedViewModel<MainViewModel>()
    private val serviceIntent by lazy { Intent(this, MainService::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.composeView.setContent { MainScreen() }

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
fun MainScreen(viewModel: MainViewModel = injectedViewModel()) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Compose")
        Text(text = viewModel.getHelloWorld())
    }
}
