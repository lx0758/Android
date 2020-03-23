package com.liux.android.example.list;

public class Bean {

    private Object object;
    private boolean selected;

    public Bean(Object object) {
        this.object = object;
    }

    public Object getObject() {
        return object;
    }

    public Bean setObject(Object object) {
        this.object = object;
        return this;
    }

    public boolean isSelected() {
        return selected;
    }

    public Bean setSelected(boolean selected) {
        this.selected = selected;
        return this;
    }
}
