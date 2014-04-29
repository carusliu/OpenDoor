package com.carusliu.opendoor.tool;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.json.JSONObject;

import com.carusliu.opendoor.application.AppApplication;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Base64;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

public class Tool {

	private static String mUserAgent = null;

	public static String getExternalStoragePath() {
		boolean bExists = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		String path = null;
		if (bExists) {
			path = Environment.getExternalStorageDirectory().getAbsolutePath();

			if (path == null) {
				return "/";
			}
			if ("/mnt/flash".equalsIgnoreCase(path)) {
				path = "/mnt/sdcard";
				File file = new File(path);
				if (!file.exists()) {
					path = "/sdcard";
					file = new File(path);
					if (!file.exists()) {
						path = "/";
					}
				}
			}
			return path;
		} else {
			return "/";
		}
	}

	public static String getFileDir() {
		String dir = getExternalStoragePath() + "/carserver/";
		File file = new File(dir);
		if (!file.exists()) {
			file.mkdirs();// 创建文件�?
		}
		return dir;
	}

	public static void makeServerDir() {
		String dir = getExternalStoragePath() + "/carserver/";
		File file = new File(dir);
		if (!file.exists()) {
			file.mkdir();// 创建文件�?
		}
		File file1 = new File(dir + "temp");
		if (!file1.exists()) {
			file1.mkdir();
		}
	}

	/**
	 * USER_AGENT format cargard android 1.0.0
	 * */
	public static String getUserAgent() {
		if (null == mUserAgent) {
			mUserAgent = "cargard android " + getVersionName();
		}

		return mUserAgent;
	}

	public static String getVersionName() {
		PackageManager packageManager = AppApplication.getInstance()
				.getPackageManager();
		PackageInfo packInfo = null;
		try {
			packInfo = packageManager.getPackageInfo(AppApplication
					.getInstance().getPackageName(),
					PackageManager.GET_CONFIGURATIONS);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		return (packInfo != null) ? packInfo.versionName : "";
	}

	public static String optNotNullString(JSONObject json, String name) {
		return json.isNull(name) ? "" : json.optString(name);
	}

	public static int optNotNullInt(JSONObject json, String name) {
		return json.isNull(name) ? 0 : json.optInt(name);
	}

	/**
	 * 判断网络连接是否存在
	 * 
	 **/
	public static boolean hasInternet(Context context) {

		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo info = manager.getActiveNetworkInfo();

		if (info == null || !info.isConnected()) {
			return false;
		}
		if (info.isRoaming()) {
			return true;
		}
		return true;
	}

	public static void showCalender(Context context, Date date,
			final TextView et) {
		if (date == null) {
			date = new Date();
		}
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		final Calendar cd = Calendar.getInstance();
		cd.setTime(date);
		new TimePickerDialog(context, new OnTimeSetListener() {

			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				// TODO Auto-generated method stub

				cd.set(Calendar.HOUR_OF_DAY, hourOfDay);
				cd.set(Calendar.MINUTE, minute);
				Date d = cd.getTime();

				et.setText(sdf.format(d));
			}
		}, cd.get(Calendar.HOUR_OF_DAY), cd.get(Calendar.MINUTE), true).show();
		new DatePickerDialog(context, new OnDateSetListener() {
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				cd.set(Calendar.YEAR, year);
				cd.set(Calendar.MONTH, monthOfYear);
				cd.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				Date d = cd.getTime();

				et.setText(sdf.format(d));
			}
		}, cd.get(Calendar.YEAR), cd.get(Calendar.MONTH),
				cd.get(Calendar.DAY_OF_MONTH)).show();

	}

	/**
	 * bitmap进行base64编码
	 * 
	 * @param bitmap
	 * @return
	 */
	public static String bitmapToBase64(Bitmap bitmap) {

		String result = null;
		ByteArrayOutputStream baos = null;
		try {
			if (bitmap != null) {
				baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

				baos.flush();
				baos.close();

				byte[] bitmapBytes = baos.toByteArray();
				result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) {
					baos.flush();
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 读取sd卡的图片文件
	 * 
	 * @param path
	 * @return
	 */
	public static Bitmap buildBitma4File(String path) {

		File baseFile = new File(path);

		if (baseFile != null && baseFile.exists()) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			return BitmapFactory.decodeFile(path, options);
		} else {
			return null;
		}

	}

	/**
	 * 将base64编码的图片数据解码成图片
	 * 
	 * @param base64Data
	 * @return
	 */
	public static Bitmap buildBitma4Base64(String base64Data) {
		byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	}

	public static Bitmap optimizeBitmap(Bitmap bitmap, int width, int height) {

		int nWidth = bitmap.getWidth();
		int nHeight = bitmap.getHeight();
		if (nWidth <= width || nHeight <= height) {
			return bitmap;
		}
		Matrix matrix = new Matrix();
		// float scaleWidth = ((float) width / nWidth);
		// float scaleHeight = ((float) height / nHeight);
		// matrix.postScale(scaleWidth, scaleHeight);
		float scaleWidth = ((float) width / nWidth);
		float scaleHeight = ((float) height / nHeight);
		float scale = scaleWidth >= scaleHeight ? scaleWidth : scaleHeight;
		matrix.postScale(scale, scale);
		return Bitmap.createBitmap(bitmap, 0, 0, nWidth, nHeight, matrix, true);
	}

	/**
	 * 压缩图片的大�?（高宽）
	 * 
	 * @param image
	 * @return
	 */
	public static Bitmap compressBitmap(Bitmap image) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if (baos.toByteArray().length / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图�?
			baos.reset();
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();

		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int width = newOpts.outWidth;
		int height = newOpts.outHeight;

		float STANDARD_HEIGHT = 960f;
		;
		float STANDARD_WIDTH = 640f;

		int be = 1;
		be = (int) ((width / STANDARD_WIDTH + height / STANDARD_HEIGHT) / 2);
		newOpts.inSampleSize = be;
		isBm = new ByteArrayInputStream(baos.toByteArray());
		image = BitmapFactory.decodeStream(isBm, null, newOpts);
		return image;
	}
	
	//Add by DX
	public static Bitmap compressBitmap(String imagePath) {

		BitmapFactory.Options opts = new BitmapFactory.Options();
		// 设置inJustDecodeBounds为true
		opts.inJustDecodeBounds = true;
		// 使用decodeFile方法得到图片的宽和高
		BitmapFactory.decodeFile(imagePath, opts);
			
		int width = opts.outWidth;
		int height = opts.outHeight;
		float STANDARD_HEIGHT = 1280f;
		float STANDARD_WIDTH = 720f;

		int be = 1;
		be = (int) ((width / STANDARD_WIDTH + height / STANDARD_HEIGHT) / 2);
		opts.inSampleSize = be;
		opts.inJustDecodeBounds = false;
		Bitmap image = BitmapFactory.decodeFile(imagePath, opts);
		return image;
	}

	/**
	 * 压缩图片的大�?
	 * 
	 * @param image
	 * @return
	 */

	public static Bitmap optimizeBitmap(Bitmap bitmap, int width) {

		int nWidth = bitmap.getWidth();
		int nHeight = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) width / nWidth);
		if (scaleWidth > 1) {
			return bitmap;
		} else {
			matrix.postScale(scaleWidth, scaleWidth);
			return Bitmap.createBitmap(bitmap, 0, 0, nWidth, nHeight, matrix,
					true);
		}
	}

	public static File saveMyBitmap(Bitmap bitmap) {

		String picPath = FileStorage.PICTURE_CACHE_DIR;
		File pahtFile = new File(picPath);
		if (!pahtFile.exists()) {
			pahtFile.mkdirs();
		}
		String picName = UUID.randomUUID().toString() + ".png";
		File f = new File(picPath + picName);
		FileOutputStream fOut = null;
		try {
			if (!f.exists() && f.isFile()) {
				f.createNewFile();
			}
			fOut = new FileOutputStream(f);
		} catch (Exception e) {
			e.printStackTrace();
		}
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);

		try {
			fOut.flush();
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return f;
	}

	public static String dateFormat(long timestamp) {
		Date date = new Date(timestamp);
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
		return sdFormat.format(date);
	}

	/**
	 * 获取版本�?
	 */
	public static String getVersionCode(Context context) {
		String ver = "1.0";
		try {
			PackageManager packageManager = context.getPackageManager();
			PackageInfo info = packageManager.getPackageInfo(
					context.getPackageName(), 0);

			if (info.versionCode > 0) {
				ver = String.valueOf(info.versionCode);
			} else if (info.versionName != null) {
				ver = info.versionName;
			}
		} catch (Exception e) {
		}

		return ver;
	}

	public static boolean isIntentAvailable(Context context, Intent intent) {
		final PackageManager packageManager = context.getPackageManager();
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
				PackageManager.MATCH_DEFAULT_ONLY);

		return list.size() > 0;
	}



	public static Bitmap rotateBitmap(Bitmap bitmap) {
		if (bitmap != null && bitmap.getWidth() > bitmap.getHeight()) {
			final Matrix matrix = new Matrix();
			matrix.setRotate(90);
			try {
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
						bitmap.getHeight(), matrix, true);
			} catch (OutOfMemoryError ex) {
				System.gc();
				ex.printStackTrace();
				DebugLog.loge("error =" + ex.toString());
			}
		}
		return bitmap;
	}
	
	public static boolean isHttpUrl(String url) {
		String header = "http";
		int indexOf = url.indexOf(header);
		if (indexOf == 0) {
			return true;
		}
		return false;
	}
	
	/**
     * 根据手机的分辨率�?dp 的单�?转成�?px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
    
    /*
     * 转换编码格式
     */
    public static String getString(String oldStr,String oldEncode,String encode){
        String newStr = "";
        try {
            newStr = new String(oldStr.getBytes(oldEncode),encode);
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return newStr;
    }
}
