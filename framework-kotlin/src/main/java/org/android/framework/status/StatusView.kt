package org.android.framework.status

interface StatusView {

    fun loadingShow(message: String?, cancel: (() -> Unit)?)
    fun loadingDismiss()

    fun switchLoading()
    fun switchSucceed()
    fun switchFailedOfError(retry: (() -> Unit)?)
    fun switchFailedOfNoData(retry: (() -> Unit)?)
    fun switchFailedOfNetwork(retry: (() -> Unit)?)

    fun errorShow(message: String?)
}