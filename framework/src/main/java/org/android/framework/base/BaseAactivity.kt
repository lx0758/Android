package org.android.framework.base

import android.app.ProgressDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import org.android.framework.DefaultStatusView
import org.android.framework.status.StatusProxy
import org.android.framework.status.StatusView
import org.android.framework.util.JetPackUtil

abstract class BaseAactivity<VB : ViewBinding, VM : BaseViewModel> : AppCompatActivity() {

    protected lateinit var viewBinding: VB
    protected lateinit var viewModel: VM

    private lateinit var statusView: StatusView
    private lateinit var statusProxy: StatusProxy

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = JetPackUtil.getVB(this)
        setContentView(viewBinding.root)

        viewModel = JetPackUtil.getVM(this)

        statusView = createStatusView()
        statusProxy = StatusProxy(statusView, this, viewModel.status)
    }

    open fun createStatusView(): StatusView {
        return DefaultStatusView(ProgressDialog(this))
    }
}