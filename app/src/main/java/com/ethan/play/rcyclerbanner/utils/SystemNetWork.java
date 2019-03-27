package com.ethan.play.rcyclerbanner.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.DisplayMetrics;
import android.view.WindowManager;



import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

/**
 * 网络信息获取
 *
 * @author
 */
public class SystemNetWork {

    // WIFI
    public static final String NETWORK_TYPE_WIFI = "wifi";
    // GPRS
    public static final String NETWORK_TYPE_GPRS = "gprs";
    // 移动
    public static final String NETWORK_TYPE_CMWAP = "cmwap";
    public static final String NETWORK_TYPE_CMNET = "cmnet";
    // 联通
    public static final String NETWORK_TYPE_3GWAP = "3gwap";
    public static final String NETWORK_TYPE_3GNET = "3gnet";
    public static final String NETWORK_TYPE_UNIWAP = "uniwap";
    public static final String NETWORK_TYPE_UNINET = "uninet";
    // 电信
    public static final String NETWORK_TYPE_CTWAP = "ctwap";
    public static final String NETWORK_TYPE_CTNET = "ctnet";
    // 其他扩展类型
    public static final String NETWORK_TYPE_MOBILE_DUN = "dun";
    public static final String NETWORK_TYPE_MOBILE_HIPRI = "hipri";
    public static final String NETWORK_TYPE_MOBILE_MMS = "mms";
    public static final String NETWORK_TYPE_MOBILE_SUPL = "supl";
    public static final String NETWORK_TYPE_WIMAX = "wimax";
    // 无网络
    public static final String NETWORK_TYPE_NONE = "unknow";
    // SIM卡类型
    public static final String CHINA_MOBILE = "mobile";// "移动";
    public static final String CHINA_UNICOM = "unicom";// "联通";
    public static final String CHINA_TELECOM = "telecom";// "电信";
    public static final String UNKNOW_TYPE = "unknow";// "未知";
    // 当前正在使用的APN
    private static Uri PREFERRED_APN_URI = Uri.parse("content://telephony/carriers/preferapn");
    private static Uri PREFERRED_APN_URI2 = Uri.parse("content://telephony/carriers/preferapn2");

    /**
     * 获取代理
     */
    public static Proxy getProxy(Context context) {
        Proxy proxy = null;
        try {
            if (!getNetworkInfo(context).equals(NETWORK_TYPE_WIFI)) {
                String proxyHost = android.net.Proxy.getDefaultHost();
                int proxyPort = android.net.Proxy.getDefaultPort();
                if (proxyHost != null && proxyHost.length() > 0 && proxyPort != -1) {
                    proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
                }
            }
        } catch (Exception e) {
        }
        return proxy;
    }

    /**
     * 检测当前网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        boolean available = false;
        ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = conManager.getActiveNetworkInfo();
        if (network != null && network.isAvailable() && network.isConnected())// 当前网络可用并且已经连接
        {
            available = true;
        }
        return available;
    }

    /**
     * 判断当前是否为wap网络
     *
     * @param context
     * @return
     */
    public static boolean isCurrentWapNetwork(Context context) {
        boolean iswap = false;
        String networkInfo = getNetworkInfo(context);
        if (networkInfo.equals(NETWORK_TYPE_CMWAP) || networkInfo.equals(NETWORK_TYPE_3GWAP)
                || networkInfo.equals(NETWORK_TYPE_UNIWAP) || networkInfo.equals(NETWORK_TYPE_CTWAP)) {
            iswap = true;
        }
        return iswap;
    }

    public static boolean isCurrentWifiNetwork(Context context) {
        boolean isWifi = false;
        String networkInfo = getNetworkInfo(context);
        if (networkInfo.equals(NETWORK_TYPE_WIFI)) {
            isWifi = true;
        }
        return isWifi;
    }

    /**
     * 获取当前正在使用的APN
     *
     * @param context
     * @return
     */
    public static String getCurrentAPN(Context context) {
        String apn = null;
        try {
            Cursor cursor = context.getContentResolver().query(PREFERRED_APN_URI, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                apn = cursor.getString(cursor.getColumnIndex("apn"));
                cursor.close();
                cursor = null;
            } else {
                cursor = context.getContentResolver().query(PREFERRED_APN_URI2, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    apn = cursor.getString(cursor.getColumnIndex("apn"));
                    cursor.close();
                    cursor = null;
                }
            }
        } catch (Exception e) {

        }
        return apn;
    }

    /**
     * 获取当前网络类型
     *
     * @param context
     * @return
     */
    public static int getNetworkType(Context context) {
        int networkType = TelephonyManager.NETWORK_TYPE_UNKNOWN;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        networkType = tm.getNetworkType();
        return networkType;
    }

    /**
     * 获取当前网络类型
     *
     * @param context
     * @return
     */
    public static String getNetworkInfo(Context context) {
        ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = conManager.getActiveNetworkInfo();
        String mNetworkInfo = NETWORK_TYPE_NONE;
        if (network != null && network.isAvailable())// && network.isConnected()
        // //&&
        // network.isAvailable()
        {
            int type = network.getType();
            switch (type) {
                case ConnectivityManager.TYPE_WIFI:
                    mNetworkInfo = NETWORK_TYPE_WIFI;
                    break;

                case ConnectivityManager.TYPE_MOBILE:
                case 15:// 兼容马维尔平台方案
                    // String apnName = getCurrentAPN(context);
                    // if(apnName!=null)
                    // {
                    // mNetworkInfo = apnName.toLowerCase();
                    // }
                    mNetworkInfo = NETWORK_TYPE_GPRS;
                    break;

                case ConnectivityManager.TYPE_MOBILE_DUN:
                    mNetworkInfo = NETWORK_TYPE_MOBILE_DUN;
                    break;

                case ConnectivityManager.TYPE_MOBILE_HIPRI:
                    mNetworkInfo = NETWORK_TYPE_MOBILE_HIPRI;
                    break;

                case ConnectivityManager.TYPE_MOBILE_MMS:
                    mNetworkInfo = NETWORK_TYPE_MOBILE_MMS;
                    break;

                case ConnectivityManager.TYPE_MOBILE_SUPL:
                    mNetworkInfo = NETWORK_TYPE_MOBILE_SUPL;
                    break;

                case ConnectivityManager.TYPE_WIMAX:
                    mNetworkInfo = NETWORK_TYPE_WIMAX;
                    break;

                default:
                    mNetworkInfo = NETWORK_TYPE_NONE;
                    break;
            }
        }
        return mNetworkInfo;
    }

    /**
     * 获取手机唯一编号
     */
    private static String imei;// 手机设备唯一编号

    public static String getIMEI(Context context) {
        if (imei == null && context != null) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            imei = tm.getDeviceId();// 手机串号
            if (imei == null || imei.length() == 0) {
                imei = "000000000000000";
            }
        }
        return imei;
    }

    /**
     * 获取SIM卡唯一编号
     */
    private static String imsi;// SIM卡唯一编号

    public static String getIMSI(Context context) {
        if (imsi == null && context != null) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            imsi = tm.getSubscriberId();
            if (imsi == null || imsi.length() == 0) {
                imsi = "000000000000000";
            }
        }
        // Utils.log("imsi : " + imsi);
        return imsi;
    }

    /**
     * 获取手机型号
     */
    private static String model;// 手机型号

    public static String getModel() {
        if (model == null) {
            model = android.os.Build.MODEL;
        }
        return model;
    }

    /**
     * 获取Android系统版本
     */
    private static String androidRelease;// Android系统版本

    public static String getAndroidRelease() {
        if (androidRelease == null) {
            androidRelease = android.os.Build.VERSION.RELEASE;
        }
        return androidRelease;
    }

    /**
     * 获取Android系统API版本号
     */
    private static int androidSDKINT;// Android系统API版本号

    public static int getAndroidSDKINT() {
        if (androidSDKINT == 0) {
            androidSDKINT = android.os.Build.VERSION.SDK_INT;
        }
        return androidSDKINT;
    }

    /**
     * 获取手机号码
     */
    private static String phoneNumber;// 手机号码

    public static String getPhoneNumber(Context context) {
        if (phoneNumber == null) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            phoneNumber = tm.getLine1Number();
        }
        return phoneNumber;
    }

    /**
     * 获取运营商
     */
    private static String cardType;// 运营商

    public static String getCardType(Context context) {
        if (cardType == null) {
            String imsi = getIMSI(context);
            cardType = UNKNOW_TYPE;
            if (imsi != null) {
                if (imsi.startsWith("46000") || imsi.startsWith("46002") || imsi.startsWith("46007")) {
                    cardType = CHINA_MOBILE;// 中国移动
                } else if (imsi.startsWith("46001")) {
                    cardType = CHINA_UNICOM;// 中国联通
                } else if (imsi.startsWith("46003")) {
                    cardType = CHINA_TELECOM;// 中国电信
                }
            }
        }
        return cardType;
    }

    /**
     * 获取MAC地址
     */
    private static String mac;// MAC地址

    public static String getMAC(Context context) {
        if (mac == null) {
            WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            if (info != null) {
                mac = info.getMacAddress();
            }
        }
        return mac;
    }

    /**
     * 获取屏幕宽度
     */
    private static int mScreenWidthPixels;// 宽

    public static int getScreenWidth(Context context) {
        if (mScreenWidthPixels <= 0) {
            setScreenInfomation(context);
        }
        return mScreenWidthPixels;
    }

    /**
     * 获取屏幕高度
     */
    private static int mScreenHeightPixels;// 高

    public static int getScreenHeight(Context context) {
        if (mScreenHeightPixels <= 0) {
            setScreenInfomation(context);
        }
        return mScreenHeightPixels;
    }

    /**
     * 获取屏幕分辨率
     */
    private static String screen;// 屏幕分辨率

    public static String getScreen(Context context) {
        if (screen == null) {
            screen = getScreenWidth(context) + "x" + getScreenHeight(context);
        }
        return screen;
    }

    /**
     * 获取屏幕尺寸
     */
    private static String screenSize;// 屏幕分辨率

    public static String getScreenSize(Context context) {
        if (screenSize == null) {
            screenSize = String.format("%.1f",
                    Math.sqrt((Math.pow(getScreenWidth(context), 2) + Math.pow(getScreenHeight(context), 2)))
                            / getDensityDpi(context));
        }
        return screenSize;
    }

    /**
     * 获取屏幕密度
     */
    private static float density;// 屏幕密度

    public static float getDensity(Context context) {
        if (density <= 0f) {
            setScreenInfomation(context);
        }
        return density;
    }

    /**
     * 获取屏幕密度系数
     */
    private static int densityDpi;// 屏幕密度系数

    public static int getDensityDpi(Context context) {
        if (densityDpi <= 0) {
            setScreenInfomation(context);
        }
        return densityDpi;
    }

    /**
     * 设置当前屏幕分辨率
     */
    public static void setScreenInfomation(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        mScreenWidthPixels = dm.widthPixels;
        mScreenHeightPixels = dm.heightPixels;
        if (mScreenHeightPixels > 0 && mScreenHeightPixels > 0) {
            screen = mScreenWidthPixels + "x" + mScreenHeightPixels;
        }
        density = dm.density;
        densityDpi = dm.densityDpi;
    }

    /**
     * 获取当前SIM卡的状态
     *
     * @param context
     * @return
     */
    public static int getSimState(Context context) {
        int simState = TelephonyManager.SIM_STATE_UNKNOWN;
        try {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            simState = tm.getSimState();// sim卡当前状态
        } catch (Exception e) {
        }
        return simState;
    }

    /**
     * 获取手机CellId
     *
     * @param context
     * @return
     */
    private static int cellId;

    public static int getCellId(Context context) {
        if (cellId == 0) {
            cellId = -1;
            if (context != null) {
                int ACCESS_FINE_LOCATION_PERMISSION = context
                        .checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);
                int ACCESS_COARSE_LOCATION_PERMISSION = context
                        .checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION);
                if (ACCESS_FINE_LOCATION_PERMISSION == PackageManager.PERMISSION_GRANTED
                        || ACCESS_COARSE_LOCATION_PERMISSION == PackageManager.PERMISSION_GRANTED) {
                    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    CellLocation cellLocation = tm.getCellLocation();
                    if (cellLocation != null) {
                        if (cellLocation instanceof GsmCellLocation) {
                            GsmCellLocation gsmCellLocation = (GsmCellLocation) cellLocation;
                            cellId = gsmCellLocation.getCid();
                            lac = gsmCellLocation.getLac();
                        } else if (cellLocation instanceof CdmaCellLocation) {
                            CdmaCellLocation cdma = (CdmaCellLocation) cellLocation;
                            cellId = cdma.getBaseStationId();
                            lac = cdma.getNetworkId();
                        }
                    }
                }
            }
        }
        return cellId;
    }

    /**
     * 获取手机Lac
     *
     * @param context
     * @return
     */
    private static int lac;

    public static int getLac(Context context) {
        if (lac == 0) {
            lac = -1;
            if (context != null) {
                int ACCESS_FINE_LOCATION_PERMISSION = context
                        .checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);
                int ACCESS_COARSE_LOCATION_PERMISSION = context
                        .checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION);
                if (ACCESS_FINE_LOCATION_PERMISSION == PackageManager.PERMISSION_GRANTED
                        || ACCESS_COARSE_LOCATION_PERMISSION == PackageManager.PERMISSION_GRANTED) {
                    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                    CellLocation cellLocation = tm.getCellLocation();
                    if (cellLocation != null) {
                        if (cellLocation instanceof GsmCellLocation) {
                            GsmCellLocation gsmCellLocation = (GsmCellLocation) cellLocation;
                            lac = gsmCellLocation.getLac();
                            cellId = gsmCellLocation.getCid();
                        } else if (cellLocation instanceof CdmaCellLocation) {
                            CdmaCellLocation cdma = (CdmaCellLocation) cellLocation;
                            lac = cdma.getNetworkId();
                            cellId = cdma.getBaseStationId();
                        }
                    }
                }
            }
        }
        return lac;
    }

    /**
     * 获取当前应用包名
     */
    private static String packageName;

    public static String getPackageName(Context context) {
        if (packageName == null && context != null) {
            packageName = context.getPackageName();
        }
        return packageName;
    }

    /**
     * 获取当前应用版本名称
     */
    private static String versionName;

    /**
     * 获取manifest中的MetaData
     *
     * @param context
     * @param packageName
     * @param key
     * @return
     */
    public static String getMetaData(Context context, String packageName, String key) {
        String value = null;
        ApplicationInfo appInfo;
        try {
            appInfo = context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            if (appInfo != null) {
                Bundle metaData = appInfo.metaData;
                if (metaData != null) {
                    if (metaData.containsKey(key)) {
                        value = metaData.getString(key);
                        if (value == null) {
                            int valueInt = metaData.getInt(key, -1);
                            if (valueInt != -1) {
                                value = String.valueOf(valueInt);
                            }
                        }
                    }
                }
            }
        } catch (NameNotFoundException e) {
        } catch (Exception e) {
        }
        return value;
    }

    public static String CHANNEL_ID = "channel";

    /**
     * 获取应用的安装路径（/data/data/<packageName>下或者/system/app/下）
     *
     * @param packageName
     * @return
     */
    public static String getAppSourcePath(Context context, String packageName) {
        if (packageName == null || packageName.trim().equals("")) {
            return null;
        }
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            if (info == null) {
                return null;
            }
            return info.sourceDir;
        } catch (NameNotFoundException e) {
            return null;
        }
    }

    /**
     * 判断软件是否为内置安装
     *
     * @param context
     * @param packageName
     * @return
     */
    public static String getAppInternal(Context context, String packageName) {
        String installPath = getAppSourcePath(context, packageName);
        if (installPath != null) {
            if (installPath.startsWith("/system/app") || installPath.startsWith("/system/priv-app")) {
                return "1";
            } else {
                return "0";
            }
        } else {
            return "-1";
        }
    }

    /**
     * 判断软件是否为内置安装
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isAppInternal(Context context, String packageName) {
        String installPath = getAppSourcePath(context, packageName);
        if (installPath != null) {
            if (installPath.startsWith("/system/app")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 求字符串的子串高效方法
     *
     * @param source
     * @param start
     * @param startInc
     * @param end
     * @param endInc
     * @return
     */
    public static String subString(String source, String start, Integer startInc, String end, Integer endInc) {
        String result = null;
        if (source == null || start == null || end == null) {
            return result;
        }
        if (startInc == null) {
            startInc = start.length();
        }
        if (endInc == null) {
            endInc = end.length();
        }
        int index1 = source.indexOf(start);
        int index2 = 0;
        if (index1 >= 0) {
            index1 += startInc;
            index2 = source.indexOf(end, index1);
            index2 += endInc;
            int length = source.length();
            if (index1 >= 0 && index2 <= length && index2 > index1) {
                result = source.substring(index1, index2);
            } else if (index1 == index2) {
                result = "";
            }
        }
        return result;
    }

    /**
     * 求URL的主机地址
     *
     * @param url
     * @return
     */
    public static String getXHost(String url) {
        String xHost = null;
        if (url.toLowerCase().startsWith("http://")) {
            url = "http://" + url.substring(7);
            xHost = subString(url, "http://", null, "/", 0);
            if (xHost == null) {
                xHost = subString(url, "http://", 0, "?", 0);
                if (xHost == null) {
                    xHost = url.substring(7);
                }
            }
        } else if (url.toLowerCase().startsWith("www.")) {
            url = "www." + url.substring(4);
            xHost = subString(url, "www.", 0, "/", 0);
            if (xHost == null) {
                xHost = subString(url, "www.", 0, "?", 0);
                if (xHost == null) {
                    xHost = url.substring(4);
                }
            }
        }
        return xHost;
    }

    public static String MD5(String src) {

        try {
            char[] Hex = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(src.getBytes());
            byte[] bytes = md.digest();
            // ..双字节16进制转换
            char[] chars = new char[bytes.length * 2];
            int j = 0;
            for (int i = 0; i < bytes.length; i++) {
                byte b = bytes[i];
                // ..高四位
                chars[j++] = Hex[b >> 4 & 0x0f];
                // ..低四位
                chars[j++] = Hex[b & 0x0f];
            }
            return new String(chars);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 过滤以下信息 com.android (android系统) com.mediatek (MTK相关) com.google (谷歌相关)
     * com.spreadst (展讯相关) com.spreadtrum (展讯相关)
     *
     * @param context
     * @param isFlagSystem
     * @return
     */
    public static String getInstallAppPackageName(Context context, boolean isFlagSystem) {
        String packageName = "";
        List<PackageInfo> packages = context.getPackageManager()
                .getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        // 循环获取用户手机上所安装的软件信息
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            // 不是系统区应用
            if (!isFlagSystem) {
                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    packageName += packageInfo.packageName;
                    packageName += "_" + packageInfo.versionCode + ",";
                }
            } // 系统区应用
            else {
                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {

                    if (!(packageInfo.packageName.startsWith("com.android")
                            || packageInfo.packageName.startsWith("com.mediatek")
                            || packageInfo.packageName.startsWith("com.google")
                            || packageInfo.packageName.startsWith("com.spreadst")
                            || packageInfo.packageName.startsWith("com.spreadtrum"))) {
                        packageName += packageInfo.packageName;
                        packageName += "_" + packageInfo.versionCode + ",";
                    }
                }
            }
        }
        if (packageName.endsWith(",")) {
            packageName = packageName.substring(0, packageName.length() - 1);
        }
        return packageName;
    }

    /**
     * 获取安装信息
     *
     * @return
     */
    public static AppInfo getInstallAppInfo(Context context) {
        String appName = "";
        String versionCode = "";
        String packageName = "";

        List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
        // 循环获取用户手机上所安装的软件信息
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                appName += packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
                versionCode += (packageInfo.versionName == null ? "1.0" : packageInfo.versionName);
                packageName += packageInfo.packageName;
                appName += ",";
                versionCode += ",";
                packageName += ",";
            }
        }
        if (appName.endsWith(",")) {
            appName = appName.substring(0, appName.length() - 1);
        }
        if (packageName.endsWith(",")) {
            packageName = packageName.substring(0, packageName.length() - 1);
        }
        if (versionCode.endsWith(",")) {
            versionCode = versionCode.substring(0, versionCode.length() - 1);
        }
        AppInfo appInfo = new AppInfo();
        appInfo.setAppName(appName);
        appInfo.setPackageName(packageName);
        appInfo.setVersionCode(versionCode);
        return appInfo;
    }

    public static class AppInfo {
        private String appName;
        private String versionCode;
        private String packageName;

        public String getAppName() {
            return appName;
        }

        public void setAppName(String appName) {
            this.appName = appName;
        }

        public String getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(String versionCode) {
            this.versionCode = versionCode;
        }

        public String getPackageName() {
            return packageName;
        }

        public void setPackageName(String packageName) {
            this.packageName = packageName;
        }
    }

    // /**
    // * 获取基本参数
    // *
    // * @param porsche
    // */
    // public static List<BasicNameValuePair> getSimpleParam(Context context) {
    //
    // List<BasicNameValuePair> param = null;
    // if (context != null) {
    // param = new ArrayList<BasicNameValuePair>();
    // /** 用户渠道ID */
    // param.add(new BasicNameValuePair("channelId", getChannelId(context,
    // getPackageName(context))));
    // /** 手机唯一imei标识码 */
    // param.add(new BasicNameValuePair("imei", getIMEI(context)));
    // /** SIM卡唯一标识码 */
    // param.add(new BasicNameValuePair("imsi", getIMSI(context)));
    // /** mid */
    // param.add(new BasicNameValuePair(
    // "mid", /* new DeviceIDFactory(context).getMID()) */
    // MobileInfo.getInstance().getDeviceID()));
    // /** token验证 */
    // param.add(new BasicNameValuePair("token", MD5(getIMEI(context) +
    // getIMSI(context))));
    // /** 手机型号 */
    // param.add(new BasicNameValuePair("phoneModel", getModel()));
    // /** 应用安装包名 */
    // param.add(new BasicNameValuePair("packageName",
    // getPackageName(context)));
    // /** 应用版本名称 */
    // param.add(new BasicNameValuePair("verName", getVersionName(context)));
    // /** 应用版本号 */
    // param.add(new BasicNameValuePair("version", getVersionCode(context) +
    // ""));
    // /** 手机cell ID */
    // param.add(new BasicNameValuePair("cellId", getCellId(context) + ""));
    // /** 手机lac */
    // param.add(new BasicNameValuePair("lac", getLac(context) + ""));
    // /** 网络类型 */
    // param.add(new BasicNameValuePair("network", getNetworkInfo(context)));
    // /** 手机网络制式 */
    // param.add(new BasicNameValuePair("netType", getNetworkType(context) +
    // ""));
    // /** 系统版本 */
    // param.add(new BasicNameValuePair("sysVersion", getAndroidRelease()));
    // /** 系统api版本 */
    // param.add(new BasicNameValuePair("sysSdkInt", getAndroidSDKINT() + ""));
    // /** SIM卡类型 */
    // param.add(new BasicNameValuePair("simType", getCardType(context)));
    // }
    // return param;
    // }

    // /**
    // * 获取标准参数
    // *
    // * @param porsche
    // */
    // public static List<BasicNameValuePair> getBasePostParam(Context context)
    // {
    //
    // List<BasicNameValuePair> param = null;
    // if (context != null) {
    //
    // param = new ArrayList<BasicNameValuePair>();
    // // ..添加基础参数
    // param.addAll(getSimpleParam(context));
    //
    // /** 手机号码 */
    // param.add(new BasicNameValuePair("phoneNumber",
    // getPhoneNumber(context)));
    // /** 手机空间(512/1024 代表剩余/总空间) */
    // param.add(new BasicNameValuePair("phoneSpace",
    // Utils.getPhoneAvailableSize(context) + "/" +
    // Utils.getPhoneTotalSize(context)));
    // /** SD卡空间(512/1024 代表剩余/总空间) */
    // param.add(new BasicNameValuePair("SdCardSpace",
    // String.valueOf(Utils.getSDCardAvailableSize()) + "/" +
    // String.valueOf(Utils.getSDCardTotalSize())));
    // /** 安装内置区域(1为系统区，0为其他区域) */
    // param.add(new BasicNameValuePair("installType", getAppInternal(context,
    // getPackageName(context))));
    // }
    // return param;
    // }


}
