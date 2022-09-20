package com.deliveryhero.whetstone.sample

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.deliveryhero.whetstone.Whetstone
import com.deliveryhero.whetstone.view.ContributesView
import javax.inject.Inject

@ContributesView
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
