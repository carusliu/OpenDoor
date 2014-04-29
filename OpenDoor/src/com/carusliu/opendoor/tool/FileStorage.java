/**
 * PictureStorage.java
 * 2012-2-9
 */
package com.carusliu.opendoor.tool;

import java.io.File;
import android.os.Environment;

/**
 * 文件的存储路�?
 * 
 * @author chuan.deng 2013-8-9 上午10:39:04
 */
public interface FileStorage {
	public static final String ARECORD_NAME = "Android" + File.separator
			+ "data" + File.separator + "carserver" + File.separator + "files";
	public static final String CACHE_DIR_NAME = "pic_cache";
	public static final String HTML_DIR_NAME = "bless_html_cache";
	public static final String PICTURE_DIR_NAME = "picture";

	public static final String ARECORD_CACHE_DIR = Environment
			.getExternalStorageDirectory()
			+ File.separator
			+ ARECORD_NAME
			+ File.separator + CACHE_DIR_NAME + File.separator;

	public static final String HTML_CACHE_DIR = Environment
			.getExternalStorageDirectory()
			+ File.separator
			+ ARECORD_NAME
			+ File.separator + HTML_DIR_NAME + File.separator;

	public static final String PICTURE_CACHE_DIR = Environment
			.getExternalStorageDirectory()
			+ File.separator
			+ ARECORD_NAME
			+ File.separator + PICTURE_DIR_NAME + File.separator;

}
