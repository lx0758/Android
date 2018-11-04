package com.liux.android.downloader.core;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DownloaderUtil {

    /**
     * 将Headers信息转化为JSON文本
     * @param map
     * @return
     */
    public static String headers2json(Map<String, List<String>> map) {
        if (map == null) return null;
        return new JSONObject(map).toString();
    }

    /**
     * 将JSON文本转化为Headers信息
     * @param string
     * @return
     */
    public static Map<String, List<String>> json2headers(String string) {
        if (string == null || string.isEmpty()) return null;
        Map<String, List<String>> headers = null;
        try {
            JSONObject jsonObject = new JSONObject(string);

            Iterator<String> iterator = jsonObject.keys();
            if (!iterator.hasNext()) return null;

            headers = new HashMap<>();
            while (iterator.hasNext()) {
                String name = iterator.next();
                List<String> values = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(jsonObject.getString(name));
                for (int i = 0; i < jsonArray.length(); i++) {
                    values.add(jsonArray.getString(i));
                }
                headers.put(name, values);
            }
        } catch (JSONException ignore) {}

        return headers;
    }
}
