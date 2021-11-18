package com.deliveryhero.whetstone

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import javax.inject.Inject

@ContributesAndroidBinding(boundType = View::class)
public class MainView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : AppCompatTextView(context, attrs) {

    @Inject
    public lateinit var dependency: MainDependency

    init {
        if (!isInEditMode) {
            Whetstone.inject(view = this)
            text = dependency.getMessage("View")
        }
    }
}
