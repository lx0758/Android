package com.liux.android.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Liux on 2016/5/23.
 * E-Mail:lx0758@qq.com
 */
public class DateUtil {
    private final String TAG = "DateUtil";

    /**
     * 取当前时间戳文本(毫秒级)
     * @return
     */
    public static String getUnixTime() {return String.valueOf(System.currentTimeMillis());}

    /**
     * 取指定时间的时间戳文本(毫秒级)
     * @param date
     * @return
     */
    public static String getUnixTime(Date date) {return String.valueOf(date.getTime());}

    /**
     * 文本格式化为时间
     * 默认格式 yyyy-MM-dd HH:mm:ss
     * @param string
     * @return
     */
    public static Date string2date(String string) {
        return string2date(string, null);
    }

    /**
     * 按指定格式将文本格式化为时间
     * 默认格式 yyyy-MM-dd HH:mm:ss
     * @param string
     * @param format
     * @return
     */
    public static Date string2date(String string, String format) {
        Date d = null;
        try {
            d = new SimpleDateFormat(format == null || format.isEmpty() ? "yyyy-MM-dd HH:mm:ss" : format).parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }

    /**
     * 时间转换为文本
     * 2017-04-21 18:23:21
     * @param time
     * @return
     */
    public static String date2string(Date time) {
        String s = null;
        try {
            s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * 时间转换为文本
     * 2017-04-21
     * @param time
     * @return
     */
    public static String date2string1(Date time) {
        String s = null;
        try {
            s = new SimpleDateFormat("yyyy-MM-dd").format(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * 时间转换为文本
     * 4月21日 (今天)
     * @param time
     * @return
     */
    public static String date2string2(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
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
     * 时间转换为文本
     * 今天 18:25
     * @param time
     * @return
     */
    public static String date2string3(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        int i = getDifferDay(calendar.getTime(), null);
        switch (i){
            case -2:
                return String.format("前天 %02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            case -1:
                return String.format("昨天 %02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            case 0:
                return String.format("今天 %02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            case 1:
                return String.format("明天 %02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            case 2:
                return String.format("后天 %02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            default:
                return String.format("%02d月%02d日 %02d:%02d", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        }
    }

    /**
     * 时间转换为文本
     * 04月21日 (今天) 18:25
     * @param time
     * @return
     */
    public static String date2string4(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        int i = getDifferDay(calendar.getTime(), null);
        switch (i){
            case -2:
                return String.format("%02d月%02d日(前天) %02d:%02d", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            case -1:
                return String.format("%02d月%02d日(昨天) %02d:%02d", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            case 0:
                return String.format("%02d月%02d日(今天) %02d:%02d", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            case 1:
                return String.format("%02d月%02d日(明天) %02d:%02d", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            case 2:
                return String.format("%02d月%02d日(后天) %02d:%02d", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
            default:
                return String.format("%02d月%02d日 %02d:%02d", calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
        }
    }

    /**
     * 时间转换为文本
     * 4-21 18:25
     * @param time
     * @return
     */
    public static String date2string5(Date time) {
        String s = null;
        try {
            s = new SimpleDateFormat("MM-dd HH:mm").format(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * 时间转换为文本
     * 4月28日 18:25
     * @param time
     * @return
     */
    public static String date2string6(Date time) {
        String s = null;
        try {
            s = new SimpleDateFormat("MM月dd日 HH:mm").format(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * 时间转换为文本
     * 18:25
     * @param time
     * @return
     */
    public static String date2string7(Date time) {
        String s = null;
        try {
            s = new SimpleDateFormat("HH:mm").format(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * 时间转换为文本
     * 05-30 18:25
     * @param time
     * @return
     */
    public static String date2string8(Date time) {
        String s = null;
        try {
            s = new SimpleDateFormat("MM-dd HH:mm").format(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * 时间转换为文本
     * 2017-05-30 18:25
     * @param time
     * @return
     */
    public static String date2string9(Date time) {
        String s = null;
        try {
            s = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    /**
     * 时间转换为文本
     * 2017年11月27日
     * @param time
     * @return
     */
    public static String date2string10(Date time) {
        String s = null;
        try {
            s = new SimpleDateFormat("yyyy年MM月dd日").format(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
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
        long time_old = calendar.getTimeInMillis();

        calendar.setTime(now == null ? new Date() : now);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long time_now = calendar.getTimeInMillis();
        int day = (int) ((time_old - time_now)/(1000*3600*24));

        return day;
    }

    /**
     * 取过去时间描述文本
     * @param time
     * @param compare
     * @return
     */
    public static String getPassTimeString(Date time, Date compare) {
        Calendar c = Calendar.getInstance();
        c.setTime(time);

        Calendar cc = Calendar.getInstance();
        if (compare != null) cc.setTime(compare);

        long s = cc.getTimeInMillis() - c.getTimeInMillis();
        if (c.get(Calendar.YEAR) < cc.get(Calendar.YEAR)) {
            return new SimpleDateFormat("yyyy-MM-dd").format(time);
        } else if (c.get(Calendar.DAY_OF_YEAR) < cc.get(Calendar.DAY_OF_YEAR) - 2) {
            return new SimpleDateFormat("MM-dd").format(time);
        } else if (c.get(Calendar.DAY_OF_YEAR) < cc.get(Calendar.DAY_OF_YEAR) - 1) {
            return new SimpleDateFormat("前天 HH:mm").format(time);
        } else if (c.get(Calendar.DAY_OF_YEAR) < cc.get(Calendar.DAY_OF_YEAR)) {
            return new SimpleDateFormat("昨天 HH:mm").format(time);
        } else if (s > 1000l*60*15){
            return new SimpleDateFormat("今天 HH:mm").format(time);
        } else if (s > 1000l*60) {
            return s/(1000l*60) + "分钟前";
        } else {
            return "刚刚";
        }
    }

    /**
     * 取过去时间描述文本
     * @param time
     * @param compare
     * @return
     */
    public static String getPassTimeString2(Date time, Date compare) {
        long t1, t2;
        if (time != null) {
            t1 = time.getTime();
        } else {
            t1 = System.currentTimeMillis();
        }
        if (compare != null) {
            t2 = compare.getTime();
        } else {
            t2 = System.currentTimeMillis();
        }

        return String.format("%.1f小时", (t1 - t2) / (1000.0 * 3600.0));
    }
}
