package org.android.framework.util;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.viewbinding.ViewBinding;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class JetPackUtil {

    private static final ViewModelProvider.Factory FACTORY = new ViewModelProvider.NewInstanceFactory();

    public static <VM extends ViewModel> VM getVM(ViewModelStoreOwner viewModelStoreOwner, Class<VM> vmClazz) {
        return new ViewModelProvider(viewModelStoreOwner, FACTORY).get(vmClazz);
    }

    public static <VB extends ViewBinding> VB getVB(Activity activity) {
        try {
            Class vbClazz = findViewBindingClass(activity);
            Method[] methods = vbClazz.getDeclaredMethods();
            for (Method method : methods) {
                Class<?>[] params = method.getParameterTypes();
                if (params.length == 1 && params[0] == LayoutInflater.class) {
                    return (VB) method.invoke(null, LayoutInflater.from(activity));
                }
            }
        } catch (Exception ignore) {}
        throw new RuntimeException(activity.getClass().getName() + " viewbinding can't find");
    }

    public static <VB extends ViewBinding> VB getVB(Fragment fragment, LayoutInflater layoutInflater, ViewGroup viewGroup) {
        try {
            Class vbClazz = findViewBindingClass(fragment);
            Method[] methods = vbClazz.getDeclaredMethods();
            for (Method method : methods) {
                Class<?>[] params = method.getParameterTypes();
                if (params.length == 3 && params[0] == LayoutInflater.class && params[1] == ViewGroup.class) {
                    return (VB) method.invoke(null, layoutInflater, viewGroup, false);
                }
            }
        } catch (Exception ignore) {}
        throw new RuntimeException(fragment.getClass().getName() + " viewbinding can't find");
    }

    private static Class findViewBindingClass(Object o) throws ClassNotFoundException {
        for (Class clazz = o.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            Type classType = clazz.getGenericSuperclass();
            if (!(classType instanceof ParameterizedType)) continue;

            Type[] types = ((ParameterizedType) classType).getActualTypeArguments();
            if (types.length < 1) continue;

            Class vbClazz = null;
            for (Type genericType : types) {
                if (ViewBinding.class.isAssignableFrom((Class<?>) genericType)) {
                    vbClazz = (Class) genericType;
                    break;
                }
            }
            if (vbClazz != null) return vbClazz;
        }
        throw new ClassNotFoundException();
    }
}
