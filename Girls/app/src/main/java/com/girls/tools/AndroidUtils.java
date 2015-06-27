package com.girls.tools;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.DisplayMetrics;

public class AndroidUtils {
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static int getScreenHeight(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.heightPixels;
	}

	public static int getScreenWidth(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.widthPixels;
	}

	public static Bitmap rotate(Bitmap bmp, float degree) {
		int bmpWidth = bmp.getWidth();
		int bmpHeight = bmp.getHeight();
		Matrix matrix = new Matrix();
		if (degree < 0) {
			matrix.setRotate(degree);
		}
		if (degree > 0) {
			matrix.setRotate(degree);
		}
		if (degree == 0) {
			matrix.postRotate(0);
		}
		try {
			Bitmap resizeBmp = Bitmap.createBitmap(bmp, 0, 0, bmpWidth,
					bmpHeight, matrix, true);
			return resizeBmp;
		} catch (OutOfMemoryError e) {
			return bmp;
		}
	}

	public static Bitmap ReadBitmapById(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	public static Bitmap scale(Bitmap bmp, float scalex, float scaley) {
		int bmpWidth = bmp.getWidth();
		int bmpHeight = bmp.getHeight();
		Matrix matrix = new Matrix();
		matrix.postScale(scalex, scaley);
		Bitmap resizeBmp = Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeight,
				matrix, true);
		return resizeBmp;
	}

	public static ProgressDialog getProgressDialog(Context context) {
		ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(true);
		return progressDialog;
	}

	public static ProgressDialog getProgressDialog(Context context, int stringId) {
		ProgressDialog progressDialog = getProgressDialog(context);
		progressDialog.setMessage(context.getString(stringId));
		return progressDialog;
	}

	public static ProgressDialog getProgressDialog(Context context, String msg) {
		ProgressDialog progressDialog = getProgressDialog(context);
		progressDialog.setMessage(msg);
		return progressDialog;
	}

	/**
	 * 
	 * @return An array, array[0] is total memory size (MB). array[1] is free
	 *         memory size (MB).
	 */
	public static long[] getSystemMemory() {
		long[] memInfoSizes = null;
		Method _readProclines = null;
		try {
			Class procClass;
			procClass = Class.forName("android.os.Process");
			Class parameterTypes[] = new Class[] { String.class,
					String[].class, long[].class };
			_readProclines = procClass.getMethod("readProcLines",
					parameterTypes);
			Object arglist[] = new Object[3];
			final String[] memInfoFields = new String[] { "MemTotal:",
					"MemFree:" };
			memInfoSizes = new long[memInfoFields.length];
			memInfoSizes[0] = 30;
			memInfoSizes[1] = -30;
			arglist[0] = new String("/proc/meminfo");
			arglist[1] = memInfoFields;
			arglist[2] = memInfoSizes;
			if (_readProclines != null) {
				_readProclines.invoke(null, arglist);
				for (int i = 0; i < memInfoSizes.length; i++) {
					// Log.d(LOG_TAG,
					// mMemInfoFields[i]+" : "+mMemInfoSizes[i]/1024);
					memInfoSizes[i] = memInfoSizes[i] / 1024;
				}
			}
			if (memInfoSizes[0] == 30 && memInfoSizes[1] == -30) {
				memInfoSizes[0] = 0;
				memInfoSizes[1] = 0;
			}
		} catch (ClassNotFoundException e) {
			memInfoSizes = null;
			e.printStackTrace();
		} catch (SecurityException e) {
			memInfoSizes = null;
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			memInfoSizes = null;
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			memInfoSizes = null;
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			memInfoSizes = null;
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			memInfoSizes = null;
			e.printStackTrace();
		}

		return memInfoSizes;
	}

	public static String getFilePathBaseDir(String filePath) {
		if (filePath == null || filePath.length() <= 0)
			return null;

		int lastPos = filePath.lastIndexOf("/");
		if (lastPos > 0) {
			String dirStr = null;
			try {
				dirStr = filePath.substring(0, lastPos);
			} catch (IndexOutOfBoundsException e) {
				return null;
			}
			return dirStr;
		}
		return null;
	}
}
