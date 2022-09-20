package com.deliveryhero.whetstone

import com.deliveryhero.whetstone.meta.ContributesInstanceMeta

@Deprecated("Legacy API", ReplaceWith("com.deliveryhero.whetstone.meta.ContributesInstanceMeta"))
@OptIn(InternalWhetstoneApi::class)
public typealias AutoScopedBinding = ContributesInstanceMeta
