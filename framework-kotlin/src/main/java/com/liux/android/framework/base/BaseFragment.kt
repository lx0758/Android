package com.liux.android.framework.base

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.liux.android.framework.status.StatusProxy
import com.liux.android.framework.status.StatusView
import com.liux.android.framework.util.JetPackUtil

abstract class BaseFragment<VB : ViewBinding, VM : BaseViewModel> : Fragment() {

    protected lateinit var viewBinding: VB
    protected lateinit var viewModel: VM

    private lateinit var statusView: StatusView
    private lateinit var statusProxy: StatusProxy

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = JetPackUtil.getVB(this, inflater, container)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = JetPackUtil.getVM(this)

        statusView = createStatusView()
        statusProxy = StatusProxy(statusView, this, viewModel.status)
    }

    open fun createStatusView(): StatusView {
        return DefaultStatusView(ProgressDialog(requireActivity()))
    }
}