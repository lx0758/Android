package com.liux.android.pay.unionpay;

/**
 * Created by Liux on 2017/10/24.
 */

public class UnionResult {
    private String result;
    private String data;

    public UnionResult(String result) {
        this.result = result;
    }

    public UnionResult(String result, String data) {
        this.result = result;
        this.data = data;
    }

    @Override
    public String toString() {
        return "UnionResult{" +
                "result='" + result + '\'' +
                ", data='" + data + '\'' +
                '}';
    }

    public String getResult() {
        return result;
    }

    public String getData() {
        return data;
    }
}
