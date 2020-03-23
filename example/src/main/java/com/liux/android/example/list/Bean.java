package com.liux.android.example.list;

import com.liux.android.list.helper.SelectBean;

public class Bean implements SelectBean {

    private Object object;

    public Bean(Object object) {
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    private boolean selected;

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
