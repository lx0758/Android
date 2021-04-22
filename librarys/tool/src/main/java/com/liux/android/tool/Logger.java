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
    private static final String DIVIDE_0 = "┃";
    private static final String DIVIDE_1 = "┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";
    private static final String DIVIDE_2 = "┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";
    private static final String DIVIDE_3 = "┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";

    public static boolean DEBUG = true;

    public static void v(String msg) {
        if (!DEBUG) return;
        print(msg, null, Log.VERBOSE);
    }

    public static void v(String msg, Throwable tr) {
        if (!DEBUG) return;
        print(msg, tr, Log.VERBOSE);
    }

    public static void d(String msg) {
        if (!DEBUG) return;
        print(msg, null, Log.DEBUG);
    }

    public static void d(String msg, Throwable tr) {
        if (!DEBUG) return;
        print(msg, tr, Log.DEBUG);
    }

    public static void i(String msg) {
        if (!DEBUG) return;
        print(msg, null, Log.INFO);
    }

    public static void i(String msg, Throwable tr) {
        if (!DEBUG) return;
        print(msg, tr, Log.INFO);
    }

    public static void w(String msg) {
        if (!DEBUG) return;
        print(msg, null, Log.WARN);
    }

    public static void w(String msg, Throwable tr) {
        if (!DEBUG) return;
        print(msg, tr, Log.WARN);
    }

    public static void e(String msg) {
        if (!DEBUG) return;
        print(msg, null, Log.ERROR);
    }

    public static void e(String msg, Throwable tr) {
        if (!DEBUG) return;
        print(msg, tr, Log.ERROR);
    }

    public static void a(String msg) {
        if (!DEBUG) return;
        print(msg, null, Log.ASSERT);
    }

    public static void a(String msg, Throwable tr) {
        if (!DEBUG) return;
        print(msg, tr, Log.ASSERT);
    }

    /**
     * 取运行栈信息
     * @return
     */
    private static String getStackTrace() {
        StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
        StackTraceElement stackTraceElement = stackTraceElements[3];

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
     * @param msg
     * @param tr
     * @param priority
     * @return
     */
    private synchronized static void print(String msg, Throwable tr, int priority) {
        String stackTrace = getStackTrace();

        msg = formatJson(msg);
        msg = formatXml(msg);
        msg = msg + '\n' + Log.getStackTraceString(tr);

        Log.println(priority, TAG, DIVIDE_1);
        String[] stackTraces = stackTrace.split("\n");
        for (String trace : stackTraces) {
            Log.println(priority, TAG, DIVIDE_0 + trace);
        }
        Log.println(priority, TAG, DIVIDE_2);
        String[] msgs = msg.split("\n");
        for (String m : msgs) {
            Log.println(priority, TAG, DIVIDE_0 + m);
        }
        Log.println(priority, TAG, DIVIDE_3);
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
