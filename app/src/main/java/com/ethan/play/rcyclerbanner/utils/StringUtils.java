package com.ethan.play.rcyclerbanner.utils;

import android.text.Html;
import android.text.Spanned;
import android.text.SpannedString;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    private static final DecimalFormat DF = new DecimalFormat("0.00");

    /**
     * byte(字节)根据长度转成kb(千字节)和mb(兆字节)
     *
     * @param bytes
     * @return
     */
    public static String bytes2kb(long bytes) {
        BigDecimal filesize = new BigDecimal(bytes);
        BigDecimal megabyte = new BigDecimal(1024 * 1024);
        BigDecimal gbbyte = new BigDecimal(1024 * 1024 * 1024);
        float returnValue = filesize.divide(gbbyte, 2, BigDecimal.ROUND_UP).floatValue();
        if (returnValue > 1)
            return (returnValue + "G");
        returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP).floatValue();
        if (returnValue > 1)
            return (returnValue + "M");
        BigDecimal kilobyte = new BigDecimal(1024);
        returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP).floatValue();
        return (returnValue + "K");
    }

    /**
     * byte(字节)根据长度转成kb(千字节)和mb(兆字节)
     *
     * @param bytes
     * @return
     */
    public static String bytes2kb(String size) {
        if (StringUtils.isEmpty(size))
            return "未知";
        long bytes = Long.valueOf(size);
        BigDecimal filesize = new BigDecimal(bytes);
        BigDecimal megabyte = new BigDecimal(1024 * 1024);
        float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP).floatValue();
        if (returnValue > 1)
            return (returnValue + "M");
        BigDecimal kilobyte = new BigDecimal(1024);
        returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP).floatValue();
        return (returnValue + "K");
    }

    public static String getDownloadPerSize(long finished, long total) {
        return DF.format((float) finished / (1024 * 1024)) + "M/" + DF.format((float) total / (1024 * 1024)) + "M";
    }

    public static boolean isBlank(String str) {
        return (str == null || str.trim().length() == 0 || isEquals("null", str.toLowerCase()));
    }

    public static boolean isEmpty(String str) {
        return (str == null || str.length() == 0 || str.equals("null"));
    }

    public static boolean isEquals(String actual, String expected) {
        if (isEmpty(actual) || isEmpty(expected))
            return false;
        return actual.equals(expected);
    }

    public static String nullStrToEmpty(String str) {
        return (str == null ? "" : str);
    }

    public static String capitalizeFirstLetter(String str) {
        if (isEmpty(str)) {
            return str;
        }

        char c = str.charAt(0);
        return (!Character.isLetter(c) || Character.isUpperCase(c)) ? str
                : new StringBuilder(str.length()).append(Character.toUpperCase(c)).append(str.substring(1)).toString();
    }

    public static String utf8Encode(String str) {
        if (!isEmpty(str) && str.getBytes().length != str.length()) {
            try {
                return URLEncoder.encode(str, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return str;
    }

    public static String FormatTime(long time, String formatRule) {
        SimpleDateFormat format = new SimpleDateFormat(formatRule, Locale.ENGLISH);
        format.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        Date d1 = new Date(time);
        return format.format(d1);
    }

    public static String utf8Encode(String str, String defultReturn) {
        if (!isEmpty(str) && str.getBytes().length != str.length()) {
            try {
                return URLEncoder.encode(str, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                return defultReturn;
            }
        }
        return str;
    }

    public static String getHrefInnerHtml(String href) {
        if (isEmpty(href)) {
            return "";
        }

        String hrefReg = ".*<[\\s]*a[\\s]*.*>(.+?)<[\\s]*/a[\\s]*>.*";
        Pattern hrefPattern = Pattern.compile(hrefReg, Pattern.CASE_INSENSITIVE);
        Matcher hrefMatcher = hrefPattern.matcher(href);
        if (hrefMatcher.matches()) {
            return hrefMatcher.group(1);
        }
        return href;
    }

    public static String htmlEscapeCharsToString(String source) {
        return StringUtils.isEmpty(source) ? source
                : source.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("&amp;", "&").replaceAll("&quot;",
                "\"");
    }

    public static String fullWidthToHalfWidth(String s) {
        if (isEmpty(s)) {
            return s;
        }

        char[] source = s.toCharArray();
        for (int i = 0; i < source.length; i++) {
            if (source[i] == 12288) {
                source[i] = ' ';
            } else if (source[i] >= 65281 && source[i] <= 65374) {
                source[i] = (char) (source[i] - 65248);
            } else {
                source[i] = source[i];
            }
        }
        return new String(source);
    }

    public static String halfWidthToFullWidth(String s) {
        if (isEmpty(s)) {
            return s;
        }

        char[] source = s.toCharArray();
        for (int i = 0; i < source.length; i++) {
            if (source[i] == ' ') {
                source[i] = (char) 12288;
            } else if (source[i] >= 33 && source[i] <= 126) {
                source[i] = (char) (source[i] + 65248);
            } else {
                source[i] = source[i];
            }
        }
        return new String(source);
    }

    public static boolean isRightPassword(String password) {
        String passwordRegex2 = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])[a-zA-Z\\d]{6,20}$";
        Pattern compile = Pattern.compile(passwordRegex2);
        Matcher matcher = compile.matcher(password);
        return matcher.matches();
    }

    public static boolean isMatcherPassword(String password) {
        boolean matcher = false;
        if (password.length() < 6)
            return false;
        for (int i = 65; i <= 90; i++) {
            String str = new String(new char[]{(char) i});
            if (password.contains(str)) {
                matcher = true;
                break;
            }
        }
        if (matcher) {
            matcher = !matcher;
            for (int i = 97; i <= 122; i++) {
                String str = new String(new char[]{(char) i});
                if (password.contains(str)) {
                    matcher = true;
                    break;
                }
            }
            if (matcher) {
                matcher = !matcher;
                for (int i = 0; i <= 9; i++) {
                    if (password.contains(String.valueOf(i))) {
                        matcher = true;
                        break;
                    }
                }
            }
        }
        // Pattern p =
        // Pattern.compile("(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])[a-zA-Z0-9]{6,20}");
        // Matcher m = p.matcher(password);
        // return m.matches();
        return matcher;
    }

    public static boolean isMobileNum(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9])|(17[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    public static boolean isEmailAddress(String emailAddress) {
        if (StringUtils.isEmpty(emailAddress))
            return false;
        Pattern p = Pattern
                .compile("^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$");
        Matcher m = p.matcher(emailAddress);
        return m.matches();
    }


    /**
     * @param strPart 字符串
     * @return 16进制字符串
     * @throws
     * @Title:string2HexString
     * @Description:字符串转16进制字符串
     */
    public static String String2HexString(String strPart) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < strPart.length(); i++) {
            int ch = (int) strPart.charAt(i);
            String strHex = Integer.toHexString(ch);
            hexString.append(strHex);
        }
        return hexString.toString();
    }

    public static Spanned GetHtmlSpanned(String text) {
        if (isBlank(text))
            return new SpannedString("");
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY);
            } else {
                return Html.fromHtml(text);
            }
        } catch (Exception e) {
            return new SpannedString(text);
        }
    }

    public static String getValueByUrl(String url, String name) {
        String result = "";
        int index = url.indexOf("?");
        String temp = url.substring(index + 1);
        String[] keyValue = temp.split("&");
        for (String str : keyValue) {
            if (str.contains(name)) {
                result = str.replace(name + "=", "");
                break;
            }
        }
        return result;
    }

    public static String getDoubleDecimalStr(double number) {
        return DF.format(number);
    }

}
