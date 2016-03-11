/**
 * BitmapDecode.java
 * 
 * @Description: 
 * 
 * @File: BitmapDecode.java
 * 
 * @Package nlsde.tools
 * 
 * @Author chaos
 * 
 * @Date 2014-12-4下午1:08:53
 * 
 * @Version V1.0
 */
package nlsde.tools;

import java.io.InputStream;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * @author Administrator
 *
 */
public class BitmapDecode {

	public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

        // Calculate ratios of height and width to requested height and width
        final int heightRatio = Math.round((float) height / (float) reqHeight);
        final int widthRatio = Math.round((float) width / (float) reqWidth);

        // Choose the smallest ratio as inSampleSize value, this will guarantee
        // a final image with both dimensions larger than or equal to the
        // requested height and width.
        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
    }

    return inSampleSize;
}
	public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
	        int reqWidth, int reqHeight) {

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeResource(res, resId, options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeResource(res, resId, options);
	}

	public static Bitmap readBitMap(Context context, int resId){  
		      BitmapFactory.Options opt = new BitmapFactory.Options();  
		      opt.inPreferredConfig = Bitmap.Config.RGB_565;   
		     opt.inPurgeable = true;  
		     opt.inInputShareable = true;  
		        //獲取資源圖片  
		     InputStream is = context.getResources().openRawResource(resId);  
		         return BitmapFactory.decodeStream(is,null,opt);  
		 }
}
