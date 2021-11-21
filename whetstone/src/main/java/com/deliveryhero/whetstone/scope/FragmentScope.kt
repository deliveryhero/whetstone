package com.deliveryhero.whetstone.scope

import androidx.fragment.app.Fragment
import com.deliveryhero.whetstone.DefineInstanceBinding

/**
 * Scope marker class for bindings that should exist for the life of a [Fragment].
 */
@DefineInstanceBinding(Fragment::class)
public class FragmentScope private constructor()
