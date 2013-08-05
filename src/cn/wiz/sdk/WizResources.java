package cn.wiz.sdk;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

/**
 * @author zwy
 * @E-Mail 1010482327@qq.com
 * @version create date 2012-10-12 am 10:48:44
 * @Message :
 */
public class WizResources {

	public static String[] getStringArray(Context ctx, int arrId) {
		return ctx.getResources().getStringArray(arrId);
	}

	public static String getResString(Context ctx, int strId, int oldId) {

		return ctx.getString(strId, ctx.getString(oldId));
	}

	public static int getInt(Context ctx, int resId) {
		String res = ctx.getString(resId);
		return Integer.parseInt(res);
	}

	public static Bitmap getBitmap(Context ctx, int iconId) {
		return BitmapFactory.decodeResource(ctx.getResources(), iconId);
	}

	public static Drawable getDrawable(Context ctx, int iconId) {
		return ctx.getResources().getDrawable(iconId);
	}
}
