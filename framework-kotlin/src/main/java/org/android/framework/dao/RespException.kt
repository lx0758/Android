package org.android.framework.dao

open class RespException(
    val iResp: IResp<*>
) : RuntimeException(iResp.message())