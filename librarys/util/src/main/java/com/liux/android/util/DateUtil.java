package com.liux.android.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Liux on 2016/5/23.
 * E-Mail:lx0758@qq.com
 */
public class DateUtil {
    private final String TAG = "DateUtil";

    /**
     * 取当前时间戳字符串(毫秒级)
     * @return
     */
    public static String getUnixTime() {return String.valueOf(System.currentTimeMillis());}

    /**
     * 取指定时间的时间戳字符串(毫秒级)
     * @param date
     * @return
     */
    public static String getUnixTime(Date date) {return String.valueOf(date.getTime());}

    /**
     * 字符串格式化为时间
     * 默认格式 yyyy-MM-dd HH:mm:ss
     * @param string
     * @return
     */
    public static Date string2date(String string) {
        return string2date(string, null);
    }

    /**
     * 按指定格式将字符串格式化为时间
     * 默认格式 yyyy-MM-dd HH:mm:ss
     * @param string
     * @param format
     * @return
     */
    public static Date string2date(String string, String format) {
        Date date = null;
        try {
            date = new SimpleDateFormat((format == null || format.isEmpty()) ? "yyyy-MM-dd HH:mm:ss" : format, Locale.getDefault()).parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 时间转换为字符串
     * yyyy-MM-dd HH:mm:ss
     * @param date
     * @return
     */
    public static String date2string(Date date) {
        return date2string(date, null);
    }

    /**
     * 时间转换为字符串
     * @param date
     * @param format
     * @return
     */
    public static String date2string(Date date, String format) {
        String s = null;
        try {
            s = new SimpleDateFormat((format == null || format.isEmpty()) ? "yyyy-MM-dd HH:mm:ss" : format, Locale.getDefault()).format(date);
        } catch (Exception ignore) {}
        return s;
    }

    /**
     * 时间转换为字符串
     * 4月21日 (今天)
     * @param date
     * @return
     */
    public static String date2string2(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        int i = getDifferDay(calendar.getTime(), null);
        switch (i){
            case -2:
                return calendar.get(Calendar.MONTH) + 1 + " 月 " + calendar.get(Calendar.DAY_OF_MONTH) + " 日 (前天)";
            case -1:
                return calendar.get(Calendar.MONTH) + 1 + " 月 " + calendar.get(Calendar.DAY_OF_MONTH) + " 日 (昨天)";
            case 0:
                return calendar.get(Calendar.MONTH) + 1 + " 月 " + calendar.get(Calendar.DAY_OF_MONTH) + " 日 (今天)";
            case 1:
                return calendar.get(Calendar.MONTH) + 1 + " 月 " + calendar.get(Calendar.DAY_OF_MONTH) + " 日 (明天)";
            case 2:
                return calendar.get(Calendar.MONTH) + 1 + " 月 " + calendar.get(Calendar.DAY_OF_MONTH) + " 日 (后天)";
            default:
                return calendar.get(Calendar.MONTH) + 1 + " 月 " + calendar.get(Calendar.DAY_OF_MONTH) + " 日";
        }
    }

    /**
     * 时间转换为字符串
     * 今天 18:25
     * @param date
     * @return
     */
    public static String date2string3(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int i = getDifferDay(calendar.getTime(), null);
        switch (i){
            case -2:
                return String.format(Locale.getDefault(), "前天 %02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            case -1:
                return String.format(Locale.getDefault(), "昨天 %02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            case 0:
                return String.format(Locale.getDefault(), "今天 %02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            case 1:
                return String.format(Locale.getDefault(), "明天 %02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            case 2:
                return String.format(Locale.getDefault(), "后天 %02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            default:
                return String.format(Locale.getDefault(), "%02d月%02d日 %02d:%02d", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        }
    }

    /**
     * 时间转换为字符串
     * 04月21日 (今天) 18:25
     * @param date
     * @return
     */
    public static String date2string4(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int i = getDifferDay(calendar.getTime(), null);
        switch (i){
            case -2:
                return String.format(Locale.getDefault(), "%02d月%02d日(前天) %02d:%02d", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            case -1:
                return String.format(Locale.getDefault(), "%02d月%02d日(昨天) %02d:%02d", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            case 0:
                return String.format(Locale.getDefault(), "%02d月%02d日(今天) %02d:%02d", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            case 1:
                return String.format(Locale.getDefault(), "%02d月%02d日(明天) %02d:%02d", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            case 2:
                return String.format(Locale.getDefault(), "%02d月%02d日(后天) %02d:%02d", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            default:
                return String.format(Locale.getDefault(), "%02d月%02d日 %02d:%02d", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        }
    }

    /**
     * 取相差天数
     * @param old
     * @param now
     * @return
     */
    public static int getDifferDay(Date old ,Date now) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(old);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long date_old = calendar.getTimeInMillis();

        calendar.setTime(now == null ? new Date() : now);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long date_now = calendar.getTimeInMillis();

        return (int) ((date_old - date_now)/(1000*3600*24));
    }

    /**
     * 取过去时间描述字符串
     * @param date
     * @param compare
     * @return
     */
    public static String getPassTimeString(Date date, Date compare) {
        Calendar dc = Calendar.getInstance();
        dc.setTime(date);

        Calendar cc = Calendar.getInstance();
        if (compare != null) cc.setTime(compare);

        long s = cc.getTimeInMillis() - dc.getTimeInMillis();
        if (dc.get(Calendar.YEAR) < cc.get(Calendar.YEAR)) {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date);
        } else if (dc.get(Calendar.DAY_OF_YEAR) < cc.get(Calendar.DAY_OF_YEAR) - 2) {
            return new SimpleDateFormat("MM-dd", Locale.getDefault()).format(date);
        } else if (dc.get(Calendar.DAY_OF_YEAR) < cc.get(Calendar.DAY_OF_YEAR) - 1) {
            return new SimpleDateFormat("前天 HH:mm", Locale.getDefault()).format(date);
        } else if (dc.get(Calendar.DAY_OF_YEAR) < cc.get(Calendar.DAY_OF_YEAR)) {
            return new SimpleDateFormat("昨天 HH:mm", Locale.getDefault()).format(date);
        } else if (s > 1000L * 60 * 15) {
            return new SimpleDateFormat("今天 HH:mm", Locale.getDefault()).format(date);
        } else if (s > 1000L * 60) {
            return s / (1000L * 60) + "分钟前";
        } else {
            return "刚刚";
        }
    }

    /**
     * 取过去时间描述字符串
     * @param date
     * @param compare
     * @return
     */
    public static String getPassTimeString2(Date date, Date compare) {
        long dl, cl;
        if (date != null) {
            dl = date.getTime();
        } else {
            dl = System.currentTimeMillis();
        }
        if (compare != null) {
            cl = compare.getTime();
        } else {
            cl = System.currentTimeMillis();
        }

        return String.format(Locale.getDefault(), "%.1f小时", Math.abs(dl - cl) / (1000.0 * 3600.0));
    }
}
