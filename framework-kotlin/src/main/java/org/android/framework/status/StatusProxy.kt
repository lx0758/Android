package org.android.framework.status

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

class StatusProxy(
    private val statusView: StatusView,
    private val lifecycleOwner: LifecycleOwner,
    private val statusLiveData: StatusLiveData,
) {

    private val statusObserver = Observer<StatusEvent> {
        if (it.statusStrategy.loading) {
            when(it.event) {
                StatusEvent.ON_LOADING -> {
                    statusView.loadingShow(it.message, it.action)
                }
                StatusEvent.ON_SUCCEED, StatusEvent.ON_FAILED_ERROR, StatusEvent.ON_FAILED_NODATA, StatusEvent.ON_FAILED_NONETWORK -> {
                    statusView.loadingDismiss()
                }
            }
        }

        if (it.statusStrategy.switch) {
            when(it.event) {
                StatusEvent.ON_LOADING -> {
                    statusView.switchLoading()
                }
                StatusEvent.ON_SUCCEED -> {
                    statusView.switchSucceed()
                }
                StatusEvent.ON_FAILED_ERROR -> {
                    statusView.switchFailedOfError(it.action)
                }
                StatusEvent.ON_FAILED_NODATA -> {
                    statusView.switchFailedOfNoData(it.action)
                }
                StatusEvent.ON_FAILED_NONETWORK -> {
                    statusView.switchFailedOfNetwork(it.action)
                }
            }
        }

        if (!it.statusStrategy.switch && (it.event == StatusEvent.ON_FAILED_ERROR || it.event == StatusEvent.ON_FAILED_NODATA || it.event == StatusEvent.ON_FAILED_NONETWORK)) {
            statusView.errorShow(it.message)
        }
    }

    init {
        statusLiveData.observe(lifecycleOwner, statusObserver)
    }
}