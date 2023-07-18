package com.riceball.gpknives;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Region;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class Utils {

    public static Context m_context;
    public static void init(Context context) {
        m_context = context;
    }

    /**
     * 获取手机型号
     * @return 手机型号
     */
    public static String getSystemModel() {
        return android.os.Build.MODEL;
    }

    public static String getUUID() {
        final String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid;
    }

    public static String getImei() {
        try {
            //实例化TelephonyManager对象
            TelephonyManager telephonyManager = (TelephonyManager) m_context.getSystemService(Context.TELEPHONY_SERVICE);
            String imei;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                imei = telephonyManager.getImei();
            }
            else {
                imei = telephonyManager.getDeviceId();
            }
            //获取IMEI号
//            String imei = telephonyManager.getDeviceId();
            //在次做个验证，也不是什么时候都能获取到的啊
            if (imei == null) {
                imei = "";
            }
            return imei;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getStringMD5(String sourceStr) {
        if (TextUtils.isEmpty(sourceStr)) {
            return "";
        }
        String s = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            //这两行代码的作用是：
            // 将bytes数组转换为BigInterger类型。1，表示 +，即正数。
            BigInteger bigInt = new BigInteger(1, md.digest(sourceStr.getBytes()));
            // 通过format方法，获取32位的十六进制的字符串。032,代表高位补0 32位，X代表十六进制的整形数据。
            //为什么是32位？因为MD5算法返回的时一个128bit的整数，我们习惯于用16进制来表示，那就是32位。
            s = String.format("%032x", bigInt);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return s;
    }

    public static String getImeiAfterMD5() {
        String imei = Utils.getImei();
        String imeiMd5 = Utils.getStringMD5(imei);

        return imeiMd5;
    }

    /**
     * Android  6.0 之前（不包括6.0）
     * 必须的权限  <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
     * @param context
     * @return
     */
    private static String getMacDefault(Context context) {
        String mac = "02:00:00:00:00:00";
        if (context == null) {
            return mac;
        }

        // WifiManager wifi = (WifiManager) context.getApplicationContext()
        //         .getSystemService(Context.WIFI_SERVICE);

        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifi == null) {
            return mac;
        }
        WifiInfo info = null;
        try {
            info = wifi.getConnectionInfo();
        } catch (Exception e) {
        }
        if (info == null) {
            return null;
        }
//        mac = info.getMacAddress();
        mac = info.getMacAddress();
        if (!TextUtils.isEmpty(mac)) {
            mac = mac.toUpperCase(Locale.ENGLISH);
        }
        return mac;
    }

    /**
     * Android 6.0（包括） - Android 7.0（不包括）
     * @return
     */
    private static String getMacFromFile() {
        String WifiAddress = "02:00:00:00:00:00";
        try {
            WifiAddress = new BufferedReader(new FileReader(new File("/sys/class/net/wlan0/address"))).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return WifiAddress;
    }

    /**
     * 遍历循环所有的网络接口，找到接口是 wlan0
     * 必须的权限 <uses-permission android:name="android.permission.INTERNET" />
     * @return
     */
    private static String getMacFromHardware() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

    /**
     * 获取MAC地址
     */
    public static String getMacAddress() {
        String mac = "02:00:00:00:00:00";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mac = getMacDefault(m_context);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            mac = getMacFromFile();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mac = getMacFromHardware();
        }
        mac = mac.replace(":", "");
        Log.i("Utils", "getMacAddress: " + mac);
        return mac;
    }

    public static String getAndroidID() {
        String id = Settings.Secure.getString(
                m_context.getContentResolver(),
                Settings.Secure.ANDROID_ID
        );
        Log.i("Utils", "getAndroidID: " + id);
        return id == null ? "" : id;
    }

    public static String getCountry() {
        String countryCode = m_context.getResources().getConfiguration().locale.getCountry();
        return countryCode;
    }

    public static String getLanguageCode() {
        String languageCode = m_context.getResources().getConfiguration().locale.getLanguage();
        return languageCode;
    }

    public static void openStoreComment() {
//        String mAddress = "market://details?id=" + m_context.getPackageName();
//        Intent marketIntent = new Intent("android.intent.action.VIEW");
//        marketIntent.setData(Uri.parse(mAddress ));
//        m_context.startActivity(marketIntent);

        // you can also use BuildConfig.APPLICATION_ID
        String appId = m_context.getPackageName();
        Intent rateIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=" + appId));
        boolean marketFound = false;

        // find all applications able to handle our rateIntent
        final List<ResolveInfo> otherApps = m_context.getPackageManager()
                .queryIntentActivities(rateIntent, 0);
        for (ResolveInfo otherApp: otherApps) {
            // look for Google Play application
            if (otherApp.activityInfo.applicationInfo.packageName
                    .equals("com.android.vending")) {

                ActivityInfo otherMainActivity = otherApp.activityInfo;
                ComponentName componentName = new ComponentName(
                        otherMainActivity.applicationInfo.packageName,
                        otherMainActivity.name
                );
                // make sure it does NOT open in the stack of your activity
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // task reparenting if needed
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                // if the Google Play was already open in a search result
                //  this make sure it still go to the app page you requested
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // this make sure only the Google Play app is allowed to
                // intercept the intent
                rateIntent.setComponent(componentName);
                try {
                    marketFound = true;
                    m_context.startActivity(rateIntent);
                } catch (android.content.ActivityNotFoundException anfe) {
//                    break;
                    marketFound = false;
                }
                break;
            }
        }

        // if GP not present on device, open web browser
        if (!marketFound) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id="+appId));
            m_context.startActivity(webIntent);
        }
    }

    //获取内存信息
    public static String getMaxMemoryInfo() {
        Runtime rt = Runtime.getRuntime();
        long maxMemory = rt.maxMemory();
        Log.e("XMemory", "Dalvik MaxMemory:" + Long.toString(maxMemory / (1024 * 1024)));
        ActivityManager activityManager = (ActivityManager) m_context.getSystemService(Context.ACTIVITY_SERVICE);
        // Log.e("XMemory", "Dalvik MemoryClass:" + Long.toString(activityManager.getMemoryClass()));
        // Log.e("XMemory", "Dalvik LargeMemoryClass:" + Long.toString(activityManager.getLargeMemoryClass()));

        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(info);
        String ramStr = info.totalMem / (1024 * 1024 * 1024) + "G";
        Log.e("XMemory", "系统总内存:" + ramStr);
        // Log.e("XMemory", "系统总内存:" + (info.totalMem / (1024 * 1024 * 1024)) + "G");
        // Log.e("XMemory", "系统剩余内存:" + (info.availMem / (1024 * 1024)) + "M");
        // Log.e("XMemory", "系统是否处于低内存运行：" + info.lowMemory);
        // Log.e("XMemory", "系统剩余内存低于" + (info.threshold / (1024 * 1024)) + "M时为低内存运行");
        return ramStr;
    }

    //获取网络状态
    public static String getNetstat() {
        try {
            ConnectivityManager cm = (ConnectivityManager) m_context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isAvailable() && activeNetworkInfo.getState() == NetworkInfo.State.CONNECTED) {
                if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    return "mobile";
                } else if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    WifiManager wm = (WifiManager) m_context.getSystemService(Context.WIFI_SERVICE);
                    int ip = wm.getConnectionInfo().getIpAddress();
                    if (ip == 0) {
                        return "wifi";
                    } else {
                        return "wifi:" + ((ip & 0xff) + "." + (ip >> 8 & 0xff) + "." + (ip >> 16 & 0xff) + "." + (ip >> 24 & 0xff));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "offline";
    }
}
