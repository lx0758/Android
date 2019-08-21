package com.liux.android.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.provider.ContactsContract;
import androidx.core.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * date：2018/11/28 15:08
 * author：Liux
 * email：lx0758@qq.com
 * description：用来读取设备隐私信息的工具类
 */

public class PrivacyUtil {

    @SuppressLint("MissingPermission")
    public static List<String> getImeis(Context context) {
        List<String> result = new ArrayList<>();
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            if(telephonyManager != null){
                for (int i = 0; i < 4; i++) {
                    String imei = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        imei = telephonyManager.getImei(i);
                    }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        imei = telephonyManager.getDeviceId(i);
                    }else {
                        imei = telephonyManager.getDeviceId();
                    }
                    if(imei != null && !result.contains(imei)) result.add(imei);
                }
            }
        } catch (Exception ignore) {}
        return result;
    }

    /**
     * 读取所有可能存在的联系人, 并去重
     * @param context
     * @return
     */
    public static List<Contact> readContactsAll(Context context) {
        List<Contact> contacts = new LinkedList<>();
        contacts.addAll(PrivacyUtil.readContactsForPhone(context));
        contacts.addAll(PrivacyUtil.readContactsForSIM(context));

        List<Contact> contactList = new LinkedList<>();
        for (PrivacyUtil.Contact contact : contacts) {
            if (!contactList.contains(contact)) {
                contactList.add(contact);
            }
        }
        return contactList;
    }

    /**
     * 读取手机联系人
     * @param context
     * @return
     */
    public static List<Contact> readContactsForPhone(Context context) {
        return readContacts(
                context,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        );
    }

    /**
     * 读取SIM卡联系人(所有可能的情况)
     * @param context
     * @return
     */
    public static List<Contact> readContactsForSIM(Context context) {
        List<Contact> contactList = new LinkedList<>();

        contactList.addAll(readContacts(
                context,
                Uri.parse("content://icc/adn"),
                "name",
                "number"
        ));
        contactList.addAll(readContacts(
                context,
                Uri.parse("content://icc/adn/subId/0"),
                "name",
                "number"
        ));
        contactList.addAll(readContacts(
                context,
                Uri.parse("content://icc/adn/subId/1"),
                "name",
                "number"
        ));

        contactList.addAll(readContacts(
                context,
                Uri.parse("content://icc/pbr"),
                "name",
                "number"
        ));
        contactList.addAll(readContacts(
                context,
                Uri.parse("content://icc/pbr/subId/0"),
                "name",
                "number"
        ));
        contactList.addAll(readContacts(
                context,
                Uri.parse("content://icc/pbr/subId/1"),
                "name",
                "number"
        ));

        contactList.addAll(readContacts(
                context,
                Uri.parse("content://sim/adn"),
                "name",
                "number"
        ));
        return contactList;
    }

    private static List<Contact> readContacts(Context context, Uri uri, String nameColumnName, String phoneColumnName) {
        List<Contact> contactList = new LinkedList<>();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) return contactList;

        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor == null || !cursor.moveToFirst()) return contactList;

            int nameColumnIndex = cursor.getColumnIndex(nameColumnName);
            int phoneColumnIndex = cursor.getColumnIndex(phoneColumnName);

            do {
                String name = cursor.getString(nameColumnIndex);
                String phone = cursor.getString(phoneColumnIndex);
                Contact contact = new Contact(name, handlerPhone(phone));
                // 一个联系人有多个号码的时候只读取第一个,电话号码为空的时候不读取
                if (!contactList.contains(contact) && !TextUtils.isEmpty(contact.phone)) {
                    contactList.add(contact);
                }
            } while (cursor.moveToNext());
        } catch (Exception ignore) {} finally {
            if (cursor != null) cursor.close();
        }
        return contactList;
    }

    /**
     * 读取通话记录
     * @param context
     * @return
     */
    @SuppressLint("MissingPermission")
    public static List<Call> readCallLog(Context context) {
        List<Call> callList = new LinkedList<>();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) return callList;

        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " desc");
            if (cursor == null || !cursor.moveToFirst()) return callList;

            int nameColumn = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
            int numberColumn = cursor.getColumnIndex(CallLog.Calls.NUMBER);
            int typeColumn = cursor.getColumnIndex(CallLog.Calls.TYPE);
            int dateColumn = cursor.getColumnIndex(CallLog.Calls.DATE);
            int durationColumn = cursor.getColumnIndex(CallLog.Calls.DURATION);
            int newColumn = cursor.getColumnIndex(CallLog.Calls.NEW);

            do {
                String name = cursor.getString(nameColumn);
                String phone = cursor.getString(numberColumn);
                if (TextUtils.isEmpty(name)) name = readContactsName(context, phone);
                callList.add(new Call(
                        name,
                        handlerPhone(phone),
                        cursor.getInt(typeColumn),
                        cursor.getLong(dateColumn),
                        cursor.getLong(durationColumn),
                        cursor.getInt(newColumn)
                ));
            } while (cursor.moveToNext());
        } catch (Exception ignore) {} finally {
            if (cursor != null) cursor.close();
        }
        return callList;
    }

    /**
     * 读取短信
     * @param context
     * @return
     */
    public static List<SMS> readSMS(Context context) {
        List<SMS> smsList = new LinkedList<>();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) return smsList;

        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(Uri.parse("content://sms/"), null, null, null, "date desc");
            if (cursor == null || !cursor.moveToFirst()) return smsList;

            int nameColumn = cursor.getColumnIndex("person");
            int phoneNumberColumn = cursor.getColumnIndex("address");
            int smsbodyColumn = cursor.getColumnIndex("body");
            int dateColumn = cursor.getColumnIndex("date");
            int typeColumn = cursor.getColumnIndex("type");


            do {
                String name = cursor.getString(nameColumn);
                String phone = cursor.getString(phoneNumberColumn);
                if (TextUtils.isEmpty(name)) name = readContactsName(context, phone);
                smsList.add(new SMS(
                        name,
                        handlerPhone(phone),
                        cursor.getString(smsbodyColumn),
                        cursor.getString(dateColumn),
                        cursor.getString(typeColumn)
                ));
            } while (cursor.moveToNext());
        } catch (Exception ignore) {} finally {
            if (cursor != null) cursor.close();
        }
        return smsList;
    }

    /**
     * 读取已安装的App
     * @param context
     * @param hasSystemApps
     * @return
     */
    public static List<App> readApps(Context context, boolean hasSystemApps) {
        PackageManager packageManager = context.getPackageManager();
        List<App> apps = new LinkedList<>();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);

        for (PackageInfo packageInfo : packageInfos) {
            if (!hasSystemApps && (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) continue;
            apps.add(new App(
                    packageInfo.applicationInfo.loadLabel(packageManager).toString(),
                    packageInfo.applicationInfo.packageName,
                    packageInfo.firstInstallTime,
                    packageInfo.lastUpdateTime,
                    Build.VERSION.SDK_INT >= 28 ? packageInfo.getLongVersionCode() :packageInfo.versionCode,
                    packageInfo.versionName
            ));
        }
        return apps;
    }

    /**
     * 在通讯录通过电话号码查询姓名
     * @param context
     * @param phone
     * @return
     */
    private static String readContactsName(Context context, String phone) {
        String name = null;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            Cursor contactsCursor = context.getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.NUMBER + "='" + phone + "'",
                    null,
                    null
            );
            if (contactsCursor != null) {
                if (contactsCursor.moveToFirst()) {
                    int displayNameColumnIndex = contactsCursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                    name = contactsCursor.getString(displayNameColumnIndex);
                }
                contactsCursor.close();
            }
        }
        return name;
    }

    /**
     * 统一手机号码格式
     * @param phone
     * @return
     */
    private static String handlerPhone(String phone) {
        if (phone == null) return "";
        if (phone.startsWith("+86") && phone.length() > 3) {
            phone = phone.substring(3, phone.length());
        }
        if (phone.startsWith("+") && phone.length() > 1) {
            phone = phone.substring(1, phone.length());
        }
        // 替换所有空格和横线
        phone = phone.replaceAll(" ", "");
        phone = phone.replaceAll("-", "");
        return phone;
    }

    /**
     * 联系人信息封装
     */
    public static class Contact {
        public String name;
        public String phone;

        public Contact(String name, String phone) {
            this.name = name;
            this.phone = phone;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Contact contact = (Contact) o;
            if (name != null ? !name.equals(contact.name) : contact.name != null)
                return false;
            return phone != null ? phone.equals(contact.phone) : contact.phone == null;
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (phone != null ? phone.hashCode() : 0);
            return result;
        }
    }

    /**
     * 通话记录封装
     */
    public static class Call {
        // 姓名
        public String name;
        // 电话号码
        public String mobile;
        // 通话类型 1是拒接 呼出是2  未接是3
        public int type;
        // 时间
        public long date;
        // 通话时间
        public long duration;
        // 未处理的
        public int _new;

        public Call(String name, String mobile, int type, long date, long duration, int _new) {
            this.name = name;
            this.mobile = mobile;
            this.type = type;
            this.date = date;
            this.duration = duration;
            this._new = _new;
        }
    }

    public static class SMS {

        // 发送短信人的姓名
        public String name;
        // 发送短信的电话号码
        public String mobile;
        // 短信内容
        public String content;
        // 发送短信的日期和时间
        public String date;
        // 短信类型1是接收到的，2是已发出 3是草稿 6发送中
        public String type;

        public SMS(String name, String mobile, String content, String date, String type) {
            this.name = name;
            this.mobile = mobile;
            this.content = content;
            this.date = date;
            this.type = type;
        }
    }

    public static class App {

        // 名称
        public String name;
        // 包名
        public String packageName;
        // 首次安装时间
        public long firstInstallTime;
        // 最后升级时间
        public long lastUpdateTime;
        // 版本号
        public long versionCode;
        // 版本名
        public String versionName;

        public App(String name, String packageName, long firstInstallTime, long lastUpdateTime, long versionCode, String versionName) {
            this.name = name;
            this.packageName = packageName;
            this.firstInstallTime = firstInstallTime;
            this.lastUpdateTime = lastUpdateTime;
            this.versionCode = versionCode;
            this.versionName = versionName;
        }
    }
}
