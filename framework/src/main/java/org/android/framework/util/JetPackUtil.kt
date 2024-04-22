package org.android.framework.util

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

@Suppress("UNCHECKED_CAST")
object JetPackUtil {

    private val FACTORY: ViewModelProvider.Factory = ViewModelProvider.NewInstanceFactory()

    fun <VM : ViewModel> getVM(
        viewModelStoreOwner: ViewModelStoreOwner,
        vmClazz: Class<VM>
    ): VM {
        return ViewModelProvider(viewModelStoreOwner, FACTORY).get(vmClazz)
    }

    fun <VM : ViewModel> getVM(viewModelStoreOwner: ViewModelStoreOwner): VM {
        try {
            val vmClazz: Class<VM> = findViewModelClass(viewModelStoreOwner) as Class<VM>
            return getVM(viewModelStoreOwner, vmClazz)
        } catch (ignore: Exception) {}
        throw RuntimeException(viewModelStoreOwner.javaClass.getName() + " viewmodel can't find")
    }

    fun <VB : ViewBinding?> getVB(activity: Activity): VB {
        try {
            val vbClazz: Class<*> = findViewBindingClass(activity)
            val methods = vbClazz.declaredMethods
            for (method in methods) {
                val params = method.parameterTypes
                if (params.size == 1 && params[0] == LayoutInflater::class.java) {
                    return method.invoke(null, LayoutInflater.from(activity)) as VB
                }
            }
        } catch (ignore: Exception) {}
        throw RuntimeException(activity.javaClass.getName() + " viewbinding can't find")
    }

    fun <VB : ViewBinding?> getVB(
        fragment: Fragment,
        layoutInflater: LayoutInflater,
        viewGroup: ViewGroup?
    ): VB {
        try {
            val vbClazz: Class<*> = findViewBindingClass(fragment)
            val methods = vbClazz.declaredMethods
            for (method in methods) {
                val params = method.parameterTypes
                if (params.size == 3 && params[0] == LayoutInflater::class.java && params[1] == ViewGroup::class.java) {
                    return method.invoke(null, layoutInflater, viewGroup, false) as VB
                }
            }
        } catch (ignore: Exception) {}
        throw RuntimeException(fragment.javaClass.getName() + " viewbinding can't find")
    }

    private fun findViewModelClass(any: Any): Class<*> {
        var clazz: Class<*>? = any.javaClass
        while (clazz != null) {
            val classType = clazz.genericSuperclass
            if (classType !is ParameterizedType) {
                clazz = clazz.superclass
                continue
            }

            val types = classType.actualTypeArguments
            if (types.isEmpty()) {
                clazz = clazz.superclass
                continue
            }

            var vmClazz: Class<ViewModel>? = null
            for (type in types) {
                if (ViewModel::class.java.isAssignableFrom((type as Class<*>))) {
                    vmClazz = type as Class<ViewModel>
                    break
                }
            }
            if (vmClazz == null) {
                clazz = clazz.superclass
                continue
            }

            return vmClazz
        }
        throw ClassNotFoundException()
    }

    private fun findViewBindingClass(any: Any): Class<*> {
        var clazz: Class<*>? = any.javaClass
        while (clazz != null) {
            val classType = clazz.genericSuperclass
            if (classType !is ParameterizedType) {
                clazz = clazz.superclass
                continue
            }

            val types = classType.actualTypeArguments
            if (types.isEmpty()) {
                clazz = clazz.superclass
                continue
            }

            var vbClazz: Class<ViewBinding>? = null
            for (type in types) {
                if (ViewBinding::class.java.isAssignableFrom((type as Class<*>))) {
                    vbClazz = type as Class<ViewBinding>
                    break
                }
            }
            if (vbClazz == null) {
                clazz = clazz.superclass
                continue
            }

            return vbClazz
        }
        throw ClassNotFoundException()
    }
}