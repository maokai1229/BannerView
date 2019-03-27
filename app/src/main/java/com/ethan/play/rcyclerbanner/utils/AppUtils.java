package com.ethan.play.rcyclerbanner.utils;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.StatFs;
import android.util.Log;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;

public class AppUtils {

    public static void sendToMail(Context context, String mailAddress) {
        Uri uri = Uri.parse("mailto:" + mailAddress);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private static class LocaleContext extends ContextWrapper {

        public LocaleContext(Context base) {
            super(base);
        }
    }

    @SuppressWarnings("deprecation")
    public static Locale getSystemLocaleLegacy(Configuration config) {
        return config.locale;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static Locale getSystemLocale(Configuration config) {
        return config.getLocales().get(0);
    }

    @SuppressWarnings("deprecation")
    public static void setSystemLocaleLegacy(Configuration config, Locale locale) {
        config.locale = locale;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static void setSystemLocale(Configuration config, Locale locale) {
        config.setLocale(locale);
    }


    private static void updateConfiguration(Configuration configuration, Locale locale) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(locale);
        } else {
            configuration.locale = locale;
        }
    }

    public static String GetSettingLanguage(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String country = Locale.getDefault().getCountry();
        String languate = locale.getLanguage();
        if (!StringUtils.isEmpty(country)) {
            if (country.equals("CN"))
                return languate;
            else
                return languate + "-" + country;
        }
        return languate;
    }


    public static boolean checkIsChinese(String lang) {
        if (StringUtils.isEquals(lang,"zh")
                ||StringUtils.isEquals(lang,"zh-TW")
                ||StringUtils.isEquals(lang,"zh-HK")
                ||StringUtils.isEquals(lang,"zh-CN")
                ||StringUtils.isEquals(lang,"zh-SG")
                ||StringUtils.isEquals(lang,"zh-MO")
                ){
            return true;
        }else
            return false;
    }

    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {

                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }

    /**
     * 将得到的int类型的IP转换为String类型
     *
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }



    public static ComponentName getComponentNameFromResolveInfo(ResolveInfo info) {
        if (info.activityInfo != null) {
            return new ComponentName(info.activityInfo.packageName, info.activityInfo.name);
        } else {
            return new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name);
        }
    }

    public static Date getInstallTime(PackageManager pm, String packageName) {
        return firstNonNull(installTimeFromPackageManager(pm, packageName), apkUpdateTime(pm, packageName));
    }

    private static Date apkUpdateTime(PackageManager packageManager, String packageName) {
        try {
            ApplicationInfo info = packageManager.getApplicationInfo(packageName, 0);
            File apkFile = new File(info.sourceDir);
            return apkFile.exists() ? new Date(apkFile.lastModified()) : null;
        } catch (NameNotFoundException e) {
            return null; // package not found
        }
    }

    private static Date installTimeFromPackageManager(PackageManager packageManager, String packageName) {
        // API level 9 and above have the "firstInstallTime" field.
        // Check for it with reflection and return if present.
        try {
            PackageInfo info = packageManager.getPackageInfo(packageName, 0);
            Field field = PackageInfo.class.getField("firstInstallTime");
            long timestamp = field.getLong(info);
            return new Date(timestamp);
        } catch (NameNotFoundException e) {
            return null; // package not found
        } catch (IllegalAccessException e) {
        } catch (NoSuchFieldException e) {
        } catch (IllegalArgumentException e) {
        } catch (SecurityException e) {
        }
        // field wasn't found
        return null;
    }

    private static Date firstNonNull(Date... dates) {
        for (Date date : dates)
            if (date != null)
                return date;
        return null;
    }

    /**
     * 通过包名打开应用
     *
     * @return
     */
    public static boolean openApp(Context context, String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            Intent intent = packageManager.getLaunchIntentForPackage(packageName);
            if (intent != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                if (context != null) {
                    context.startActivity(intent);
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    public static boolean isInstallApp(Context context, String packageName) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 64);
            return info != null;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
//            e.printStackTrace();
        }
        return false;
    }

    public static void openUrl(Context context, String url) {
        try {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
           // ToastUtils.createToast().show(context, "open url failed");
        }
    }

    public static boolean needUpdate(Context context, String packageName, int versionCode) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 64);
            if (info != null) {
                if (info.versionCode >= versionCode) {
                    return false;
                }
                return true;
            }
            return false;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取版本号
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        int verCode = -1;
        try {
            verCode = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {

        }
        return verCode;
    }

    /**
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        String versionName=null;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }


    public static void installApk(Context context, String path) {
        // TODO Auto-generated method stub
        File file = new File(path);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static void uninstall(Context context, String packageName) {
        if (context != null && !StringUtils.isEmpty(packageName)) {
            Intent intent = new Intent(Intent.ACTION_DELETE);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("package:" + packageName));
            context.startActivity(intent);
        }
    }

    /**
     * 查看手机剩余空间大小
     *
     * @return
     */
    public static long getPhoneAvailableSize(Context context) {
        long phoneAvailableSize = 0;
        if (context != null) {
            StatFs statFs = new StatFs(context.getFilesDir().getAbsolutePath());
            phoneAvailableSize = (statFs.getBlockSize() * (long) statFs.getAvailableBlocks());
        }
        return phoneAvailableSize;
    }

    /**
     * 查看手机总空间大小
     *
     * @return
     */
    public static long getPhoneTotalSize(Context context) {
        long phoneTotalSize = 0;
        if (context != null) {
            StatFs statFs = new StatFs(context.getFilesDir().getAbsolutePath());
            phoneTotalSize = (statFs.getBlockSize() * (long) statFs.getBlockCount());
        }
        return phoneTotalSize;
    }

    @SuppressWarnings("deprecation")
    public static void restartApplication(Context context) {
//		Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
//		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//		context.startActivity(intent);

        ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        am.restartPackage(context.getPackageName());
    }

    public static String getSHA1Key(Context context, String packageName) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            Signature[] signs = packageInfo.signatures;
            Signature sign = signs[0];
            return parseSignature(sign.toByteArray());
        } catch (Exception e) {
        }
        return "";
    }

    private static String parseSignature(byte[] signature) {
        try {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(signature));
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] publicKey = md.digest(cert.getEncoded());
            String hexPublicKey = byte2HexFormatted(publicKey);
            return hexPublicKey;
        } catch (CertificateException e) {
        } catch (NoSuchAlgorithmException e) {
        }
        return "";
    }

    private static String byte2HexFormatted(byte[] arr) {
        StringBuilder str = new StringBuilder(arr.length * 2);
        for (int i = 0; i < arr.length; i++) {
            String h = Integer.toHexString(arr[i]);
            int l = h.length();
            if (l == 1)
                h = "0" + h;
            if (l > 2)
                h = h.substring(l - 2, l);
            str.append(h.toUpperCase());
            if (i < (arr.length - 1))
                str.append(':');
        }
        return str.toString();
    }

    public static void SetTextViewDrawableEnd(TextView tv, Resources resources, int resId) {
        Drawable drawableEnd;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawableEnd = resources.getDrawable(resId, null);
        } else {
            drawableEnd = resources.getDrawable(resId);
        }
        drawableEnd.setBounds(0, 0, drawableEnd.getMinimumWidth(), drawableEnd.getMinimumHeight());
        tv.setCompoundDrawablePadding(5);
        tv.setCompoundDrawables(null, null, drawableEnd, null);
    }

    public static void CopyClip(Context context, String label, String text) {
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData = ClipData.newPlainText(label, text);
        cm.setPrimaryClip(mClipData);
    }

    public static int GetAppVersionCode(Context context, String packageName) {
        try {
            int versionCode = context.getPackageManager()
                    .getPackageInfo(packageName, 0).versionCode;
            return versionCode;
        } catch (Exception e) {
            return 0;
        }
    }

    public static String GetLauncherClassName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setPackage(context.getPackageName());
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        ResolveInfo info = packageManager
                .resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (info == null) {
            info = packageManager.resolveActivity(intent, 0);
        }
        return info.activityInfo.name;

    }

    public static void CreateShortcutIconInHomeScreen(Context context, Class<?> cls, String name, int resId) {

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent());
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
        addIntent.putExtra("duplicate", false);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(
                        context, resId));
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(context, cls));
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        context.sendBroadcast(addIntent);
    }

    /**
     * 删除当前应用的桌面快捷方式
     *
     * @param context
     */
    public static void DelShortcut(Context context, String name) {
        Intent shortcut = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");
        // 快捷方式名称
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
        Intent shortcutIntent = context.getPackageManager()
                .getLaunchIntentForPackage(context.getPackageName());
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        context.sendBroadcast(shortcut);
    }

    /**
     * 判断当前应用在桌面是否有桌面快捷方式
     *
     * @param context
     */
    public static boolean hasShortcut(Context context) {
        boolean result = false;
        String title = null;
        try {
            final PackageManager pm = context.getPackageManager();
            title = pm.getApplicationLabel(
                    pm.getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA)).toString();
        } catch (Exception e) {

        }
        final String uriStr;
        if (Build.VERSION.SDK_INT < 8) {
            uriStr = "content://com.android.launcher.settings/favorites?notify=true";
        } else if (Build.VERSION.SDK_INT < 19) {
            uriStr = "content://com.android.launcher2.settings/favorites?notify=true";
        } else {
            uriStr = "content://com.android.launcher3.settings/favorites?notify=true";
        }
        final Uri CONTENT_URI = Uri.parse(uriStr);
        final Cursor c = context.getContentResolver().query(CONTENT_URI, null,
                "title=?", new String[]{title}, null);
        if (c != null && c.getCount() > 0) {
            result = true;
        }
        return result;
    }


    public static String getMetaData(Context context, String metaDataName) {
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            String msg = appInfo.metaData.getString(metaDataName);
            return msg;
        } catch (NameNotFoundException e) {
        }
        return "";
    }

    public static boolean checkDeviceHasNavigationBar(Context context) {

        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
        }
        return hasNavigationBar;
    }

}
