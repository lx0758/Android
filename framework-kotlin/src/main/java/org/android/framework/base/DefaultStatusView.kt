package org.android.framework.base

import android.app.ProgressDialog
import android.text.TextUtils
import android.widget.Toast
import org.android.framework.status.StatusView

class DefaultStatusView(
    private val progressDialog: ProgressDialog
) : StatusView {

    override fun loadingShow(message: String?, cancel: (() -> Unit)?) {
        progressDialog.setOnCancelListener { cancel?.invoke() }
        progressDialog.setCancelable(cancel != null)
        progressDialog.setMessage(message ?: "请稍候...")
        progressDialog.show()
    }

    override fun loadingDismiss() {
        progressDialog.dismiss()
    }

    override fun switchLoading() {
        progressDialog.setOnCancelListener(null)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("加载中...")
        progressDialog.show()
    }

    override fun switchSucceed() {
        progressDialog.dismiss()
    }

    override fun switchFailedOfError(retry: (() -> Unit)?) {
        progressDialog.dismiss()
    }

    override fun switchFailedOfNoData(retry: (() -> Unit)?) {
        progressDialog.dismiss()
    }

    override fun switchFailedOfNetwork(retry: (() -> Unit)?) {
        progressDialog.dismiss()
    }

    override fun errorShow(message: String?) {
        if (!TextUtils.isEmpty(message)) Toast.makeText(progressDialog.context, message, Toast.LENGTH_LONG).show()
    }
}