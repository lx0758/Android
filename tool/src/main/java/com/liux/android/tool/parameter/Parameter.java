package com.liux.android.tool.parameter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Param注解,当页面间传递参数时，使用此注解注解字段后，当进入页面的时候自动取值，被回收时自动保存该值
 * Created by LuoHaifeng on 2017/3/8.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Parameter {
    String value();
}
