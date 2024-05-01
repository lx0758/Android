package com.liux.android.tool;

import android.util.Log;

import org.json.JSONArray;
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

    public static final int LEVEL_NONE = 0;
    public static final int LEVEL_BASIC = 1;
    public static final int LEVEL_DETAIL = 2;

    private static final String SEPARATOR = System.getProperty("line.separator");
    private static final String DIVIDE_0 = "┃";
    private static final String DIVIDE_1 = "┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";
    private static final String DIVIDE_2 = "┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";
    private static final String DIVIDE_3 = "┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";

    private static int sLevel = LEVEL_BASIC;
    private static int sElementIndex = 3;
    private static boolean sFormatJson = true;
    private static boolean sFormatXml = true;
    private static boolean sPrintStackTrace = true;

    public static void setLevel(int level) {
        sLevel = level;
    }

    public static void setElementIndex(int elementIndex) {
        sElementIndex = elementIndex;
    }

    public static void setFormatJson(boolean formatJson) {
        sFormatJson = formatJson;
    }

    public static void setFormatXml(boolean formatXml) {
        sFormatXml = formatXml;
    }

    public static void setPrintStackTrace(boolean printStackTrace) {
        sPrintStackTrace = printStackTrace;
    }

    public static void v(String tag, String msg) {
        print(Log.VERBOSE, tag, msg, null);
    }

    public static void v(String tag, String msg, Throwable tr) {
        print(Log.VERBOSE, tag, msg, tr);
    }

    public static void d(String tag, String msg) {
        print(Log.DEBUG, tag, msg, null);
    }

    public static void d(String tag, String msg, Throwable tr) {
        print(Log.DEBUG, tag, msg, tr);
    }

    public static void i(String tag, String msg) {
        print(Log.INFO, tag, msg, null);
    }

    public static void i(String tag, String msg, Throwable tr) {
        print(Log.INFO, tag, msg, tr);
    }

    public static void w(String tag, String msg) {
        print(Log.WARN, tag, msg, null);
    }

    public static void w(String tag, String msg, Throwable tr) {
        print(Log.WARN, tag, msg, tr);
    }

    public static void e(String tag, String msg) {
        print(Log.ERROR, tag, msg, null);
    }

    public static void e(String tag, String msg, Throwable tr) {
        print(Log.ERROR, tag, msg, tr);
    }

    public static void a(String tag, String msg) {
        print(Log.ASSERT, tag, msg, null);
    }

    public static void a(String tag, String msg, Throwable tr) {
        print(Log.ASSERT, tag, msg, tr);
    }

    public synchronized static void print(int priority, String tag, String msg, Throwable tr) {
        switch (sLevel) {
            case LEVEL_NONE: {
                break;
            }
            case LEVEL_DETAIL: {
                String stackTrace = getStackTrace();

                if (sFormatJson) {
                    msg = tryFormatJson(msg);
                }
                if (sFormatXml) {
                    msg = tryFormatXml(msg);
                }
                if (sPrintStackTrace && tr != null) {
                    msg = msg + SEPARATOR + Log.getStackTraceString(tr);
                }

                Log.println(priority, tag, DIVIDE_1);
                String[] stackTraces = stackTrace.split(SEPARATOR);
                for (String trace : stackTraces) {
                    Log.println(priority, tag, DIVIDE_0 + trace);
                }
                Log.println(priority, tag, DIVIDE_2);
                String[] msgs = msg.split(SEPARATOR);
                for (String m : msgs) {
                    Log.println(priority, tag, DIVIDE_0 + m);
                }
                Log.println(priority, tag, DIVIDE_3);
                break;
            }
            case LEVEL_BASIC:
            default: {
                if (sFormatJson) {
                    msg = tryFormatJson(msg);
                }
                if (sFormatXml) {
                    msg = tryFormatXml(msg);
                }
                if (sPrintStackTrace && tr != null) {
                    msg = msg + SEPARATOR + Log.getStackTraceString(tr);
                }
                Log.println(priority, tag, msg);
                break;
            }
        }
    }

    /**
     * 取运行栈信息
     * @return
     */
    private static String getStackTrace() {
        StackTraceElement[] stackTraceElements = new Throwable().getStackTrace();
        StackTraceElement stackTraceElement = stackTraceElements[sElementIndex];
        return String.format(
                Locale.getDefault(),
                "%s" + SEPARATOR + "%s#%s(%s:%d)",
                Thread.currentThread(),
                stackTraceElement.getClassName(),
                stackTraceElement.getMethodName(),
                stackTraceElement.getFileName(),
                stackTraceElement.getLineNumber()
        );
    }

    /**
     * 格式化 Json
     * @param json
     * @return
     */
    private static String tryFormatJson(String json) {
        try {
            if (json.startsWith("{")) {
                json = new JSONObject(json).toString(4);
            } else if (json.startsWith("[")) {
                json = new JSONArray(json).toString(4);
            }
        } catch (Exception ignored) {}
        return json;
    }

    /**
     * 格式化 Xml
     * @param xml
     * @return
     */
    private static String tryFormatXml(String xml) {
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
        } catch (Exception ignored) {}
        return xml;
    }
}
