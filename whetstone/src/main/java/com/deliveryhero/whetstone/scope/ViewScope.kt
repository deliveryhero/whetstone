package com.deliveryhero.whetstone.scope

import com.deliveryhero.whetstone.DefineInjectorBinding

/**
 * Scope marker class for bindings that should exist for the life of an [android.view.View].
 */
@DefineInjectorBinding
public class ViewScope private constructor()
