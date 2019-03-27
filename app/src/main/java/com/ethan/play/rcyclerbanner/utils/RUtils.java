package com.ethan.play.rcyclerbanner.utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.Field;

/**
 * 获取资源的id
 */

public class RUtils {
    private static int id(Context context, String type, String name) {
        try {
            Resources res = context.getResources();
            return res.getIdentifier(name, type, context.getPackageName());
        } catch (Exception localException) {
            localException.printStackTrace();
            Log.e("getIdByReflection error", localException.getMessage());
        }

        return 0;
    }

    public static int id(Context context, String name) {
        return id(context, "id", name);
    }

    public static int color(Context context, String name) {
        return id(context, "color", name);
    }

    public static int font(Context context, String name) {
        return id(context, "color", name);
    }

    public static int layout(Context context, String name) {
        return id(context, "layout", name);
    }

    public static int drawable(Context context, String name) {
        return id(context, "drawable", name);
    }

    public static int mipmap(Context context, String name) {
        return id(context, "drawable", name);
    }

    public static int anim(Context context, String name) {
        return id(context, "anim", name);
    }

    public static int string(Context context, String name) {
        return id(context, "string", name);
    }

    public static int style(Context context, String name) {
        return id(context, "style", name);
    }

    public static int dimen(Context context, String name) {
        return id(context, "dimen", name);
    }

    /**
     * 对于context.getResources().getIdentifier无法获取的数据,或者数组 资源反射值
     */
    private static Object getResourceId(Context context, String name,
                                        String type) {
        String className = context.getPackageName() + ".R";
        try {
            Class<?> cls = Class.forName(className);
            for (Class<?> childClass : cls.getClasses()) {
                String simple = childClass.getSimpleName();
                if (simple.equals(type)) {
                    for (Field field : childClass.getFields()) {
                        String fieldName = field.getName();
                        if (fieldName.equals(name)) {
                            return field.get(null);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * context.getResources().getIdentifier无法获取到styleable的数据
     *
     * @param name
     * @return
     * @paramcontext
     */
    public static int styleable(Context context, String name) {
        return ((Integer) getResourceId(context, name, "styleable")).intValue();
    }

    /**
     * 获取styleable的ID号数组
     *
     * @param name
     * @return
     * @paramcontext
     */
    public static int[] styleablearray(Context context, String name) {
        return (int[]) getResourceId(context, name, "styleable");
    }

    public static int colorHex(Context context, String name) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getResources().getColor(color(context, name), context.getTheme());
        } else {
            return context.getResources().getColor(color(context, name));
        }

    }

}
