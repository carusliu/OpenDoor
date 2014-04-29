package com.carusliu.opendoor.application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.WeakHashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.webkit.CacheManager;

@SuppressWarnings("deprecation")
public class AppCache {

	/**
	 * Log or request TAG
	 */
	public static final String TAG = "AppCache";

	private static final int CACHE_TIME = 60 * 60000;// 缓存失效时间

	public static final int PAGE_SIZE = 20;// 默认分页大小

	/**
	 * A singleton instance of the AppCache class for easy access in other
	 * places
	 */
	private static AppCache sInstance;

	private ImageCache mImageCache = new ImageCache();

	private Context mContext = AppApplication.getInstance();

	/**
	 * @return AppCache singleton instance
	 */
	public static synchronized AppCache getInstance() {
		if (sInstance == null) {
			sInstance = new AppCache();
		}
		return sInstance;
	}

	/*
	 * public User getSelfUserInfo() { return mSelfUserInfo; }
	 * 
	 * public void setSelfUserInfo(User mSelfUserInfo) { this.mSelfUserInfo =
	 * mSelfUserInfo; }
	 */

	public ImageCache getImageCache() {
		return mImageCache;
	}

	public class ImageCache extends WeakHashMap<String, Bitmap> {

		public boolean isCached(String url) {
			return containsKey(url) && get(url) != null;
		}

	}

	/**
	 * 判断缓存数据是否可读
	 * 
	 * @param cachefile
	 * @return
	 */
	public boolean isReadDataCache(String cachefile) {
		return readObject(cachefile) != null;
	}

	/**
	 * 判断缓存是否存在
	 * 
	 * @param cachefile
	 * @return
	 */
	private boolean isExistDataCache(String cachefile) {
		boolean exist = false;
		File data = mContext.getFileStreamPath(cachefile);
		if (data.exists())
			exist = true;
		return exist;
	}

	/**
	 * 判断缓存是否失效
	 * 
	 * @param cachefile
	 * @return
	 */
	public boolean isCacheDataFailure(String cachefile) {
		boolean failure = false;
		File data = mContext.getFileStreamPath(cachefile);
		if (data.exists()
				&& (System.currentTimeMillis() - data.lastModified()) > CACHE_TIME)
			failure = true;
		else if (!data.exists())
			failure = true;
		return failure;
	}

	/**
	 * 清除app缓存
	 */
	public void clearAppCache() {
		// 清除webview缓存
		File file = CacheManager.getCacheFileBaseDir();
		if (file != null && file.exists() && file.isDirectory()) {
			for (File item : file.listFiles()) {
				item.delete();
			}
			file.delete();
		}
		mContext.deleteDatabase("webview.db");
		mContext.deleteDatabase("webview.db-shm");
		mContext.deleteDatabase("webview.db-wal");
		mContext.deleteDatabase("webviewCache.db");
		mContext.deleteDatabase("webviewCache.db-shm");
		mContext.deleteDatabase("webviewCache.db-wal");

		// 清除数据缓存
		clearCacheFolder(mContext.getFilesDir(), System.currentTimeMillis());
		clearCacheFolder(mContext.getCacheDir(), System.currentTimeMillis());
	}

	/**
	 * 清除缓存目录
	 * 
	 * @param dir
	 *            目录
	 * @param numDays
	 *            当前系统时间
	 * @return
	 */
	private int clearCacheFolder(File dir, long curTime) {
		int deletedFiles = 0;
		if (dir != null && dir.isDirectory()) {
			try {
				for (File child : dir.listFiles()) {
					if (child.isDirectory()) {
						deletedFiles += clearCacheFolder(child, curTime);
					}
					if (child.lastModified() < curTime) {
						if (child.delete()) {
							deletedFiles++;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return deletedFiles;
	}

	/**
	 * 保存磁盘缓存
	 * 
	 * @param key
	 * @param value
	 * @throws IOException
	 */
	public void setDiskCache(String key, String value) throws IOException {
		FileOutputStream fos = null;
		try {
			fos = mContext.openFileOutput("cache_" + key + ".data",
					Context.MODE_PRIVATE);
			fos.write(value.getBytes());
			fos.flush();
		} finally {
			try {
				fos.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 获取磁盘缓存数据
	 * 
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public String getDiskCache(String key) throws IOException {
		FileInputStream fis = null;
		try {
			fis = mContext.openFileInput("cache_" + key + ".data");
			byte[] datas = new byte[fis.available()];
			fis.read(datas);
			return new String(datas);
		} finally {
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 保存对象
	 * 
	 * @param ser
	 * @param file
	 * @throws IOException
	 */
	public boolean saveObject(Serializable ser, String file) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = mContext.openFileOutput(file, Context.MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(ser);
			oos.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				oos.close();
			} catch (Exception e) {
			}
			try {
				fos.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 读取对象
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public Serializable readObject(String file) {
		if (!isExistDataCache(file))
			return null;
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = mContext.openFileInput(file);
			ois = new ObjectInputStream(fis);
			return (Serializable) ois.readObject();
		} catch (FileNotFoundException e) {
		} catch (Exception e) {
			e.printStackTrace();
			// 反序列化失败 - 删除缓存文件
			if (e instanceof InvalidClassException) {
				File data = mContext.getFileStreamPath(file);
				data.delete();
			}
		} finally {
			try {
				ois.close();
			} catch (Exception e) {
			}
			try {
				fis.close();
			} catch (Exception e) {
			}
		}
		return null;
	}

	public static String getKey() {
		return "8a28a27a9a523bc557053054bb5a19c3";
	}

}
