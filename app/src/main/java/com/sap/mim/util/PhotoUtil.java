package com.sap.mim.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class PhotoUtil {

    public static Bitmap getBitmap(byte[] photo) {
        return BitmapFactory.decodeByteArray(photo, 0, photo.length);// 从字节数组解码位图
    }

}
