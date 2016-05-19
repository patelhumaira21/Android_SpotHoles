/**
 * File Name : ImageUtils.java
 * Created by: Humaira Patel
 * Date: 02/4/2016
 *
 */
package edu.sdsu.cs.cs646.assign4;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import java.io.ByteArrayOutputStream;

/**
 * This class contains the utility functions for the image.
 *
 */
public class ImageUtils {

    // destination width and height.
    private static final int IMAGE_WIDTH = 700;
    private static final int IMAGE_HEIGHT = 700;


    /**
     * This function returns a scaled bitmap depending on the
     * width and height of the bitmap and ones defined above.
     *
     * @param path
     * @return
     */
    public static Bitmap getScaledBitmap(String path) {
        // get the options
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        // scale the image and return the scaled bitmap
        int inSampleSize = 1;
        if (srcHeight > IMAGE_HEIGHT || srcWidth > IMAGE_WIDTH) {
            inSampleSize = (srcWidth > srcHeight) ? Math.round(srcHeight / IMAGE_HEIGHT)
                    : Math.round(srcWidth / IMAGE_WIDTH);
        }
        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;
        return BitmapFactory.decodeFile(path, options);
    }

    /**
     *  This funcion converts the bitmap image to the base64
     *  enccoded string.
     *
     * @param bitmap
     * @return
     */
    public static String getEncoded64Img(Bitmap bitmap) {
        byte[] byteFormat = compressImage(bitmap);
        // get the base 64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);
        return imgString;
    }

    /**
     *
     * This method compresses the bitmap image and returns it in the form of
     * a byte array.
     *
     * @param bitmap
     * @return
     */
    public static byte[] compressImage(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }
}


