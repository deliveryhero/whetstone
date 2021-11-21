package com.deliveryhero.whetstone.scope

import com.deliveryhero.whetstone.DefineInjectorBinding

/**
 * Scope marker class for bindings that should exist for the life of an [android.app.Application].
 */
@DefineInjectorBinding
public class ApplicationScope private constructor()
