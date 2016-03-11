package cn.com.navia.sdk.utils;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import cn.com.navia.sdk.exceptions.LocaterException;

public class LocaterUtil {
	private static final String TAG = LocaterUtil.class.getSimpleName();
	private static final int BUFFER_SIZE = 4096;

	public static void unzipSpecs(File specZipFile, File specUnZipDir) throws LocaterException{
		if ((!specUnZipDir.exists() || specUnZipDir.list().length < 1) && specZipFile.exists()) {
			LocaterUtil.unzip(specZipFile, specUnZipDir);
		}
	}
	
	public static File getSpecUnZipDir(File specZipFile) {
		return  new File(specZipFile.getParentFile(), LocaterUtil.getFileNameNoEx(specZipFile.getName()));
	}
	
	public static boolean send2Msg(Messenger msger, int what, Object content) {
		boolean ret = false;
		try {
			if (msger != null) {
				Message msg = Message.obtain();
				msg.what = what;
				msg.obj = content;
				msger.send(msg);
				ret = true;
			}else{
				Log.w(TAG, "msger is NULL");
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return ret;
	}

	@SuppressLint("NewApi")
	public static boolean put2SharedPreferences(SharedPreferences shared, String k, String v) {
		Set<String> set = shared.getStringSet(k, new HashSet<String>());
		set.add(v);
		
		Editor editor = shared.edit();
		editor.remove(k) .putStringSet(k, set);
		
		Log.w(TAG, "put2SharedPreferences k:"+k+" v:"+ Arrays.toString(set.toArray()));
		return editor.commit();
	}

	@SuppressLint("NewApi")
	public static Set<String> get4SharedPreferences(SharedPreferences shared, String k) {
		Set<String> set = shared.getStringSet(k, new HashSet<String>());
		Log.w(TAG, "get4SharedPreferences k:"+k+" v:"+set);
		return set;
	}

	public static void zip(String[] files, String zipFile) throws IOException {
		BufferedInputStream origin = null;
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));
		try {
			byte data[] = new byte[BUFFER_SIZE];

			for (int i = 0; i < files.length; i++) {
				FileInputStream fi = new FileInputStream(files[i]);
				origin = new BufferedInputStream(fi, BUFFER_SIZE);
				try {
					ZipEntry entry = new ZipEntry(files[i].substring(files[i].lastIndexOf("/") + 1));
					out.putNextEntry(entry);
					int count;
					while ((count = origin.read(data, 0, BUFFER_SIZE)) != -1) {
						out.write(data, 0, count);
					}
				} finally {
					origin.close();
				}
			}
		} finally {
			out.close();
		}
	}

	public static void unzip(File zipFile, File f) throws LocaterException {
		try {
			if (!f.exists() || !f.isDirectory()) {
				f.mkdirs();
			}
			ZipInputStream zin = new ZipInputStream(new FileInputStream(zipFile));
			try {
				ZipEntry ze = null;
				while ((ze = zin.getNextEntry()) != null) {
					File unzipFile = new File(f.getParent() , ze.getName());
					if (ze.isDirectory()) {
						File unzipDir = unzipFile;
						if (!unzipDir.isDirectory()) {
							unzipDir.mkdirs();
						}
					} else {
						FileOutputStream fout = new FileOutputStream(unzipFile, false);
						try {
							for (int c = zin.read(); c != -1; c = zin.read()) {
								fout.write(c);
							}
						} finally {
							fout.close();
						}
					}
				}
			} finally {
				zin.close();
			}
		} catch (Exception e) {
			throw new LocaterException("unzip:" + zipFile, e);
		}
	}

	public int upZipFile(File zipFile, String folderPath, Charset charset) throws ZipException, IOException {
		ZipFile zfile = new ZipFile(zipFile);
		Enumeration<? extends ZipEntry> zList = zfile.entries();
		ZipEntry ze = null;
		byte[] buf = new byte[1024];
		while (zList.hasMoreElements()) {
			ze = (ZipEntry) zList.nextElement();
			if (ze.isDirectory()) {
				String dirstr = folderPath + "/" + ze.getName();
				// // dirstr.trim();
				// dirstr = new String(dirstr.getBytes("8859_1"), charset);
				File f = new File(dirstr);
				f.mkdir();
				continue;
			}
			OutputStream os = new BufferedOutputStream(new FileOutputStream(new File(folderPath, ze.getName())));
			InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
			int readLen = 0;
			while ((readLen = is.read(buf, 0, 1024)) != -1) {
				os.write(buf, 0, readLen);
			}
			is.close();
			os.close();
		}
		zfile.close();
		return 0;
	}

	/*
	 * Java文件操作 获取文件扩展名
	 */
	public static String getExtensionName(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length() - 1))) {
				return filename.substring(dot + 1);
			}
		}
		return filename;
	}

	/*
	 * Java文件操作 获取不带扩展名的文件名
	 */
	public static String getFileNameNoEx(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length()))) {
				return filename.substring(0, dot);
			}
		}
		return filename;
	}

	public static void deletes(File specUnZipDir) {
		if(specUnZipDir.isDirectory()){
			File[] listFiles = specUnZipDir.listFiles();
			for(File f : listFiles ){
				deletes(f);
			}
		}else{
			specUnZipDir.delete();
		}
	}
}
