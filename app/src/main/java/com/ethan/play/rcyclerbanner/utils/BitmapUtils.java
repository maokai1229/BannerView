package com.ethan.play.rcyclerbanner.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BitmapUtils {
    public static final int CHOOSE_FROM_CAMERA = 734;
    public static final int CHOOSE_FROM_PHOTO_CODE = 735;
    public static final int CUT_HEAD_CODE = 736;
    private static File cameraTempFile;

    // decode这个图片并且按比例缩放以减少内存消耗，虚拟机对每张图片的缓存大小也是有限制的
    public static Bitmap decodeFile(File f, boolean isCompress) {
        try {
            if (isCompress) {
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(new FileInputStream(f), null, o);

                final int REQUIRED_SIZE = 100;
                int width_tmp = o.outWidth, height_tmp = o.outHeight;
                int scale = 1;
                while (true) {
                    if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                        break;
                    width_tmp /= 2;
                    height_tmp /= 2;
                    scale *= 2;
                }
                BitmapFactory.Options o2 = new BitmapFactory.Options();
                o2.inSampleSize = scale;
                return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
            } else {
                return BitmapFactory.decodeStream(new FileInputStream(f));
            }
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    /**
     * 转换成圆角
     *
     * @param bmp
     * @param roundPx
     * @return
     */
    public static Bitmap convertToRoundedCorner(Bitmap bmp, float roundPx) {
        if (roundPx == 0)
            return bmp;
        Bitmap newBmp = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.ARGB_8888);
        // 得到画布
        Canvas canvas = new Canvas(newBmp);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // 第二个和第三个参数一样则画的是正圆的一角，否则是椭圆的一角
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bmp, rect, rect, paint);
        return newBmp;
    }

    /**
     * 放大/缩小图片到指定大小
     *
     * @param src        原图
     * @param widthSize  放大/缩小宽度
     * @param heightSize 放大/缩小高度
     * @return 放大/缩小之后的图片
     */
    public static Bitmap setBitmapToCastSize(Bitmap src, float widthSize, float heightSize) {
        if (widthSize <= 0 || heightSize <= 0) {
            return src;
        }
        return Bitmap.createScaledBitmap(src, (int) widthSize, (int) heightSize, false);
    }

    /**
     * 获取图片宽高
     *
     * @param cts
     * @param drawableId
     * @return
     */
    public static Point getBitmapSize(Context cts, int drawableId) {
        Point size = new Point();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeResource(cts.getResources(), drawableId, options);
        size.x = options.outWidth;
        size.y = options.outHeight;
        bitmap.recycle();
        bitmap = null;
        return size;
    }

    public static Bitmap drawableToBitamp(Drawable drawable) {
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap getImageThumbnail(Bitmap bitmap, int width, int height) {
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * 根据指定的图像路径和大小来获取缩略图 此方法有两点好处： 1.
     * 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
     * 第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。 2.
     * 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使 用这个工具生成的图像不会被拉伸。
     *
     * @param imagePath 图像的路径
     * @param width     指定输出图像的宽度
     * @param height    指定输出图像的高度 生成的缩略图
     */

    public static Bitmap getImageThumbnail(String imagePath, int width, int height) {
        // imagePath = "/storage/emulated/0/DCIM/Camera/20140508_152012.jpg";
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // 设为 false
        // 计算缩放比
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = Math.max(0, Math.max(beWidth, beHeight));
        options.inSampleSize = be;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    public static void ChooseHeadFromPhoto(Activity context) {
        Intent intentPick = new Intent(Intent.ACTION_PICK, null);
        intentPick.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
        context.startActivityForResult(intentPick, CHOOSE_FROM_PHOTO_CODE);
    }

    public static void ChooseHeadFromPhoto(Fragment context) {
        Intent intentPick = new Intent(Intent.ACTION_PICK, null);
        intentPick.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
        context.startActivityForResult(intentPick, CHOOSE_FROM_PHOTO_CODE);
    }

    public static String GetImagePathByUri(Context context, Uri selectedImage) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        if (cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            return picturePath;
        }
        return null;
    }



    public static void StartCameraZoom(Fragment activity, int size) {
        StartPhotoZoom(activity, Uri.fromFile(cameraTempFile), size);
    }

    public static void StartPhotoZoom(Fragment activity, Uri uri, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", true);

        activity.startActivityForResult(intent, CUT_HEAD_CODE);
    }

    public static void StartPhotoZoom(Activity activity, Uri uri, int size) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", size);
        intent.putExtra("outputY", size);
        intent.putExtra("return-data", true);

        activity.startActivityForResult(intent, CUT_HEAD_CODE);
    }

    public static Bitmap GetChooseBitmap(Intent picdata) {
        Bundle bundle = picdata.getExtras();
        if (bundle != null) {
            Bitmap photo = bundle.getParcelable("data");
            return photo;
        }
        return null;
    }

    public static Drawable getDrawableFromResource(Context context, int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getResources().getDrawable(resId, null);
        } else {
            return context.getResources().getDrawable(resId);
        }
    }
}
