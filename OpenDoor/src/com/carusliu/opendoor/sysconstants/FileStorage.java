package com.carusliu.opendoor.sysconstants;

import java.io.File;

import android.os.Environment;

public class FileStorage {
    public static final String ARECORD_NAME = "Android" + File.separator + "data" + File.separator
            + "com.renren.secret" + File.separator + "files";
    public static final String SECOND_NAME = "pic";
    public static final String CACHE_DIR_NAME = "pic_cache";
    public static final String DB_DIR_NAME = "db";
	public static final String ARECORD_DB_DIR = File.separator + ARECORD_NAME
			+ File.separator + DB_DIR_NAME + File.separator;
	private static final String PICTURE_DIR_NAME = "picture";

	public static final String ARECORD_CACHE_DIR = Environment
			.getExternalStorageDirectory()
			+ File.separator
			+ ARECORD_NAME
			+ File.separator + CACHE_DIR_NAME + File.separator;
	public static final String PICTURE_CACHE_DIR = Environment
			.getExternalStorageDirectory()
			+ File.separator
			+ ARECORD_NAME
			+ File.separator + PICTURE_DIR_NAME + File.separator;
}
