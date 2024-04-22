package org.android.framework.status

class StatusEvent(
    val event: Int,
    val statusStrategy: StatusStrategy,
    val action: (() -> Unit)? = null,
    val message: String? = null,
) {

    companion object {
        val ON_LOADING = 1
        val ON_SUCCEED = 2
        val ON_FAILED_ERROR = 3
        val ON_FAILED_NODATA = 4
        val ON_FAILED_NONETWORK = 5
    }
}