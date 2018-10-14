package com.liux.android.tool;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Locale;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Created by Liux on 2017/9/13.
 */

public class Logger {
    private static final String TAG = "[Logger]";
    private static final String SEPARATOR = System.getProperty("line.separator");
    private static final String DIVIDE_1 = "================================================================================================";
    private static final String DIVIDE_2 = "------------------------------------------------------------------------------------------------";

    public static boolean DEBUG = true;

    public static int v(String msg) {
        if (!DEBUG) return -1;
        String stackTrace = getStackTrace();
        String info = formatText(stackTrace, msg);
        return Log.v(TAG, info);
    }

    public static int v(String msg, Throwable tr) {
        if (!DEBUG) return -1;
        String stackTrace = getStackTrace();
        String info = formatText(stackTrace, msg);
        return Log.v(TAG, info, tr);
    }

    public static int d(String msg) {
        if (!DEBUG) return -1;
        String stackTrace = getStackTrace();
        String info = formatText(stackTrace, msg);
        return Log.d(TAG, info);
    }

    public static int d(String msg, Throwable tr) {
        if (!DEBUG) return -1;
        String stackTrace = getStackTrace();
        String info = formatText(stackTrace, msg);
        return Log.d(TAG, info, tr);
    }

    public static int i(String msg) {
        if (!DEBUG) return -1;
        String stackTrace = getStackTrace();
        String info = formatText(stackTrace, msg);
        return Log.i(TAG, info);
    }

    public static int i(String msg, Throwable tr) {
        if (!DEBUG) return -1;
        String stackTrace = getStackTrace();
        String info = formatText(stackTrace, msg);
        return Log.i(TAG, info, tr);
    }

    public static int w(String msg) {
        if (!DEBUG) return -1;
        String stackTrace = getStackTrace();
        String info = formatText(stackTrace, msg);
        return Log.w(TAG, info);
    }

    public static int w(String msg, Throwable tr) {
        if (!DEBUG) return -1;
        String stackTrace = getStackTrace();
        String info = formatText(stackTrace, msg);
        return Log.w(TAG, info, tr);
    }

    public static int e(String msg) {
        if (!DEBUG) return -1;
        String stackTrace = getStackTrace();
        String info = formatText(stackTrace, msg);
        return Log.e(TAG, info);
    }

    public static int e(String msg, Throwable tr) {
        if (!DEBUG) return -1;
        String stackTrace = getStackTrace();
        String info = formatText(stackTrace, msg);
        return Log.e(TAG, info, tr);
    }

    /**
     * 取运行栈信息
     * @return
     */
    private static String getStackTrace() {
        StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
        StackTraceElement stackTraceElement = stackTraceElements[2];

        String className = stackTraceElement.getClassName();
        String[] classNameInfo = className.split("\\.");
        if (classNameInfo.length > 0) {
            className = classNameInfo[classNameInfo.length - 1];
        }
        if (className.contains("$")) {
            className = className.split("\\$")[0];
        }

        return String.format(
                Locale.CHINA,
                "%s" + SEPARATOR + "%s.%s(%s.java:%d)",
                Thread.currentThread(),
                stackTraceElement.getClassName(),
                stackTraceElement.getMethodName(),
                className,
                stackTraceElement.getLineNumber()
        );
    }

    /**
     * 格式化文本
     * @param text
     * @return
     */
    private static String formatText(String stackTrace, String text) {
        text = formatJson(text);
        text = formatXml(text);
        return DIVIDE_1 + SEPARATOR +
                stackTrace + SEPARATOR +
                DIVIDE_2 + SEPARATOR +
                text + SEPARATOR +
                DIVIDE_1;
    }

    /**
     * 格式化 Json
     * @param json
     * @return
     */
    private static String formatJson(String json) {
        try {
            if (json.startsWith("{")) {
                json = new JSONObject(json).toString(4);
            } else if (json.startsWith("[")) {
                json = new JSONArray(json).toString(4);
            }
        } catch (JSONException e) {

        }
        return json;
    }

    /**
     * 格式化 Xml
     * @param xml
     * @return
     */
    private static String formatXml(String xml) {
        try {
            if (!xml.startsWith("<")) {
                return xml;
            }
            Source xmlInput = new StreamSource(new StringReader(xml));
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.transform(xmlInput, xmlOutput);
            xml = xmlOutput.getWriter().toString().replaceFirst(">", ">" + SEPARATOR);
        } catch (Exception e) {

        }
        return xml;
    }
}
