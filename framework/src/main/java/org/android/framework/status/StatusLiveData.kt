package org.android.framework.status

import androidx.lifecycle.LiveData

class StatusLiveData : LiveData<StatusEvent>() {

    fun post(event: Int, statusStrategy: StatusStrategy, message: String? = null, action: (() -> Unit)? = null) {
        postValue(
            StatusEvent(event, statusStrategy, action, message)
        )
    }
}