package com.liux.android.example.list;

import com.liux.android.list.adapter.state.IStateBean;

import java.util.HashMap;
import java.util.Map;

public class Bean implements IStateBean {

    private Object object;

    public Bean(Object object) {
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    public Bean setObject(Object o) {
        this.object = o;
        return this;
    }

    private Map<String, Object> state = new HashMap<>();

    @Override
    public Object getState(String key) {
        return state.get(key);
    }

    @Override
    public void putState(String key, Object tag) {
        state.put(key, tag);
    }
}
