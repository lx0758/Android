package com.liux.android.example.http;

import java.io.Serializable;

public class TestBean implements Serializable {

    private long id;
    private String name;

    public long getId() {
        return id;
    }

    public TestBean setId(long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public TestBean setName(String name) {
        this.name = name;
        return this;
    }
}
