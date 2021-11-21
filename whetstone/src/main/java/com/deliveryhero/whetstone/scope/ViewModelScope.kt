package com.deliveryhero.whetstone.scope

import androidx.lifecycle.ViewModel
import com.deliveryhero.whetstone.DefineInstanceBinding

/**
 * Scope marker class for bindings that should exist for the life of a [ViewModel].
 */
@DefineInstanceBinding(ViewModel::class)
public class ViewModelScope private constructor()
