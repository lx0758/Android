package org.android.framework.base

import android.text.TextUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alibaba.fastjson.JSONException
import org.android.framework.dao.IResp
import org.android.framework.dao.RespException
import org.android.framework.status.StatusEvent
import org.android.framework.status.StatusLiveData
import org.android.framework.status.StatusStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.await
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel : ViewModel() {

    val status = StatusLiveData()

    fun <T> execute(
        coroutineContext: CoroutineContext = Dispatchers.IO,
        statusStrategy: StatusStrategy = StatusStrategy.NONE,
        job: suspend (() -> T),
        succeed: (T) -> Unit,
        failed: ((throwable: Throwable) -> Boolean)? = null,
    ) {
        viewModelScope.launch(coroutineContext) {
            try {
                status.post(StatusEvent.ON_LOADING, statusStrategy) { cancel() }
                val result = job.invoke()
                status.post(StatusEvent.ON_SUCCEED, statusStrategy)
                succeed.invoke(result)
            } catch (throwable: Throwable) {
                if (failed?.invoke(throwable) == true) return@launch
                var event = StatusEvent.ON_FAILED_ERROR
                var message = throwable.message
                when {
                    throwable is HttpException || throwable.javaClass.name.startsWith("java.net") -> {
                        event = StatusEvent.ON_FAILED_NONETWORK
                        message = "网络连接失败,请检查网络连接"
                    }
                    throwable is RespException -> {
                        if (throwable.iResp.isSuccessful() && throwable.iResp.isEmpty()) event = StatusEvent.ON_FAILED_NODATA
                        message = if (!TextUtils.isEmpty(throwable.iResp.message())) throwable.iResp.message()!! else "服务调用失败"
                    }
                    throwable is JSONException -> {
                        message = "解析数据异常"
                    }
                }
                status.post(event, statusStrategy, message) {
                    execute(coroutineContext, statusStrategy, job, succeed, failed)
                }
            }
        }
    }

    suspend fun <T> Call<out IResp<T>>.check(retryCount: Int = 1): IResp<T> {
        var count = 1
        var iResp: IResp<T>
        while (true) {
            try {
                iResp = clone().await()
                break
            } catch (e: Exception) {
                if (count >= retryCount) throw e
                count ++
            }
        }

        if (!iResp.isSuccessful()) throw RespException(iResp)

        return iResp
    }

    suspend fun <T> Call<out IResp<T>>.tryCheck(retryCount: Int = 1): IResp<T>? {
        return try {
            check(retryCount)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun <T> Call<out IResp<T>>.data(checkEmpty: Boolean = false, retryCount: Int = 1): T {
        val iResp = check(retryCount)
        if (checkEmpty && iResp.isEmpty()) throw RespException(iResp)
        return iResp.data()!!
    }

    suspend fun <T> Call<out IResp<T>>.tryData(checkEmpty: Boolean = false, retryCount: Int = 1): T? {
        return try {
            data(checkEmpty, retryCount)
        } catch (e: Exception) {
            null
        }
    }
}