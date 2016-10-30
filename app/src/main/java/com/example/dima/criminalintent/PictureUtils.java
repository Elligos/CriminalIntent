package com.example.dima.criminalintent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

/**
 * Created by Dima on 09.10.2016.
 */

public class PictureUtils {
    //получить изображение с заданными размерами
    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;// позволяет получить значения ширины (outWidth),
            //+ высоты (outHeight) и MIME-типа (outMimeType) без выделения памяти под изображение
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        //задать параметр сжатия (д. б. кратен двум, либо будет округлен до ближайшего подходящего
        //+ значения)
        int inSampleSize = 1;
        if((srcHeight > destHeight) || (srcWidth > destWidth)){
            if(srcWidth > srcHeight){
                inSampleSize = Math.round(srcHeight / destHeight);
            }
            else{
                inSampleSize = Math.round(srcWidth / destWidth);
            }
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        //TODO: операция сжатия довольно ресурсоемкая, возможно стоит вынести в отдельный поток
        return BitmapFactory.decodeFile(path, options);
    }

    //получить изображение под размеры активности
    public static Bitmap getScaledBitmap(String path, Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return getScaledBitmap(path, size.x, size.y);
    }
}
