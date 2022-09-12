package com.deliveryhero.whetstone.compose

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.deliveryhero.whetstone.Whetstone
import com.deliveryhero.whetstone.app.ApplicationComponent

@Composable
internal fun applicationComponent(): ApplicationComponent {
    val context = LocalContext.current
    return remember(context) {
        val app = context.applicationContext as Application
        Whetstone.fromApplication(app)
    }
}
