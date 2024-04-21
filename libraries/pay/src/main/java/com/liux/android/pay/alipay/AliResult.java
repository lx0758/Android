package com.liux.android.pay.alipay;

import java.util.Map;

public class AliResult {
    private String resultStatus;
    private String result;
    private String memo;

    public AliResult(String resultString) {
        if (resultString == null || resultString.isEmpty()) return;

        String[] resultParams = resultString.split(";");
        for (String resultParam : resultParams) {
            if (resultParam.startsWith("resultStatus")) {
                resultStatus = gatValue(resultParam, "resultStatus");
            } else if (resultParam.startsWith("result")) {
                result = gatValue(resultParam, "result");
            } else if (resultParam.startsWith("memo")) {
                memo = gatValue(resultParam, "memo");
            }
        }
    }

    public AliResult(Map<String, String> resultMap) {
        if (resultMap == null || resultMap.isEmpty()) return;

        for (Map.Entry<String, String> entry : resultMap.entrySet()) {
            if ("resultStatus".equals(entry.getKey())) {
                resultStatus = entry.getValue();
            } else if ("result".equals(entry.getKey())) {
                result = entry.getValue();
            } else if ("memo".equals(entry.getKey())) {
                memo = entry.getValue();
            }
        }
    }

    @Override
    public String toString() {
        return "resultStatus={" + resultStatus + "};memo={" + memo + "};result={" + result + "}";
    }

    private String gatValue(String content, String key) {
        String prefix = key + "={";
        return content.substring(content.indexOf(prefix) + prefix.length(), content.lastIndexOf("}"));
    }

    public String getResultStatus() {
        return resultStatus;
    }

    public String getMemo() {
        return memo;
    }

    public String getResult() {
        return result;
    }
}