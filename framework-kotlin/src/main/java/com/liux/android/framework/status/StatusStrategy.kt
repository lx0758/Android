package com.liux.android.framework.status

enum class StatusStrategy(
    val loading: Boolean,
    val switch: Boolean,
) {
    NONE(false, false),
    ONLY_LOADING(true, false),
    ONLY_SWITCH(false, true),
    ;
}