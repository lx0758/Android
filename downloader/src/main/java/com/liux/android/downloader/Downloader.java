package com.liux.android.downloader;

import java.util.List;
import java.util.Map;

public class Downloader {

    public static void config(Config config) {

    }

    public static Task createTask(String url) {
        return createTask(url, "GET", null);
    }

    public static Task createTask(String url, String method) {
        return createTask(url, method, null);
    }

    public static Task createTask(String url, Map<String, List<String>> header) {
        return createTask(url, "GET", header);
    }

    public static Task createTask(String url, String method, Map<String, List<String>> header) {
        return null;
    }

    public static Task getTask(long taskId) {
        return null;
    }

    public static List<Task> getAllTasks() {
        return null;
    }

    public static void startAllTasks() {

    }

    public static void stopAllTasks() {

    }

    public static void resetAllTasks() {

    }

    public static void deleteAllTasks() {

    }

    public static void registerTaskCallback(TaskCallback taskCallback) {

    }

    public static void unregisterTaskCallback(TaskCallback taskCallback) {

    }
}
