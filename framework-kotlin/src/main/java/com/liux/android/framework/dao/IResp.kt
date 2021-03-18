package com.liux.android.framework.dao

interface IResp<T> {

    fun code(): Int

    fun message(): String?

    fun data(): T?

    fun isSuccessful(): Boolean

    fun isEmpty(): Boolean
}