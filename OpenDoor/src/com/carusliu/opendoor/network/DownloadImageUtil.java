package com.carusliu.opendoor.network;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import com.carusliu.opendoor.sysconstants.FileStorage;
import com.carusliu.opendoor.tool.DebugLog;

public class DownloadImageUtil {

	public static File downloadImage(final String fileUrl) {

		DebugLog.logd(" downloadImage   -----fileUrl: " + fileUrl);

		byte[] data = null;
		File f = null;
		try {

			String picPath = FileStorage.PICTURE_CACHE_DIR;
			File pahtFile = new File(picPath);
			if (!pahtFile.exists()) {
				pahtFile.mkdirs();
			}

			String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

			f = new File(picPath + fileName);

			if (f.exists() && f.isFile()) {
				data = getFileImageByte(f);
			} else {
				data = getNetImage(fileUrl);
				if (f.createNewFile()) {
					saveFile(f, data);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return f;
	}

	public static byte[] readStream(InputStream inStream) {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		try {
			byte[] buffer = new byte[1024];
			int len = 0;

			while ((len = inStream.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}
			outStream.close();
			inStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return outStream.toByteArray();
	}

	private static void saveFile(File f, byte[] data) {
		FileOutputStream fOut = null;
		try {
			if (!f.exists() && f.isFile()) {
				f.createNewFile();
			}
			fOut = new FileOutputStream(f);
			fOut.write(data);
			fOut.flush();
			fOut.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static byte[] getNetImage(String fileUrl) {
		DebugLog.logd("loadImage  fileUrl -----:" + fileUrl);
		InputStream is = null;
		byte[] data = null;
		try {
			URL imageUrl = new URL(fileUrl);
			URLConnection connection = (URLConnection) imageUrl
					.openConnection();
			connection.setConnectTimeout(10 * 1000);
			is = connection.getInputStream();
			data = readStream(is);
			is.close();
			return data;
		} catch (IOException e) {
			e.printStackTrace();
		}
		DebugLog.logd("loadImage  bytes count:" + data.length);
		return data;
	}

	private static byte[] getFileImageByte(File f) {
		byte[] data = null;
		try {
			BufferedInputStream in = new BufferedInputStream(
					new FileInputStream(f));
			ByteArrayOutputStream out = new ByteArrayOutputStream(1024 * 3);

			byte[] temp = new byte[1024];
			int size = 0;

			while ((size = in.read(temp)) != -1) {
				out.write(temp, 0, size);
			}
			data = out.toByteArray();
			in.close();
			out.close();
			return data;
		} catch (IOException e) {
			e.printStackTrace();
		}

		DebugLog.logd("Readed fielimage bytes count:" + data.length);

		return data;

	}

}
