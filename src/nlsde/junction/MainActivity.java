package nlsde.junction;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import nlsde.junction.net.JunctionHttp;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.update.UmengUpdateAgent;

public class MainActivity extends Activity {
	private static final String TAG = MainActivity.class.getSimpleName();
	private Intent intent = null;
	private ImageView imageView;
	// 是否是第一次使用
	private boolean isFirstUse;
	private String ASSETS_NAME = "configs.zip";
	private String DB_PATH = Environment.getExternalStorageDirectory() + "/";
	private String DB_TOPATH = Environment.getExternalStorageDirectory() + "/";
	private String DB_NAME = "configs.zip";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		MobclickAgent.updateOnlineConfig( getApplicationContext() );
		AnalyticsConfig.enableEncrypt(true);
		PushAgent mPushAgent = PushAgent.getInstance(getApplicationContext());
		mPushAgent.enable();
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		UmengUpdateAgent.update(this);
		// setContentView(R.layout.activity_main);

		// imageView = (ImageView)findViewById(R.id.loginscreen);
		// imageView.setImageBitmap(BitmapDecode.decodeSampledBitmapFromResource(getResources(),
		// R.drawable.loginscreen,
		// getWindowManager().getDefaultDisplay().getWidth(),
		// getWindowManager().getDefaultDisplay().getHeight()));

		// 注册 SDK 广播监听者
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);

		mReceiver = new SDKReceiver();
		registerReceiver(mReceiver, iFilter);
		unregisterReceiver(mReceiver);
		intent = new Intent(MainActivity.this, Main.class);
		// 系统会为需要启动的activity寻找与当前activity不同的task;
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// 创建一个新的线程来显示欢迎动画，指定时间后结束，跳转至指定界面
		JunctionHttp.ver = getVersion();
		JunctionHttp.mac = getMacAddress();
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					JunctionHttp.init();
					// 实例化SharedPreferences对象（第一步）
					SharedPreferences mySharedPreferences = getSharedPreferences(
							"junction", Activity.MODE_PRIVATE);
					// 实例化SharedPreferences.Editor对象（第二步）
					SharedPreferences.Editor editor_url = mySharedPreferences
							.edit();
					// 用putString的方法保存数据
					editor_url.putString("baseurl", JunctionHttp.base_url);
					// 提交当前数据
					editor_url.commit();
					// 读取SharedPreferences中需要的数据
					SharedPreferences preferences = getSharedPreferences(
							"isFirstUse", MODE_WORLD_READABLE);

					isFirstUse = preferences.getBoolean("isFirstUse", true);
					if (isFirstUse) {
						
						try {
							copyDataBase();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						// 解压文件
						String path = DB_PATH + DB_NAME;
						File zipFile = new File(path);
						try {
							upZipFile(zipFile, DB_TOPATH);
						} catch (ZipException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// 实例化Editor对象
						Editor editor = preferences.edit();
						// 存入数据
						editor.putBoolean("isFirstUse", false);
						// 提交修改
						editor.commit();
						startActivity(new Intent(MainActivity.this,
								GuideActivity.class));
					} else {
						startActivity(new Intent(MainActivity.this, Main.class));
					}

					
					Thread.sleep(2000);
					
					finish();
				} catch (Exception e) {
					SharedPreferences mySharedPreferences = getSharedPreferences(
							"junction", Activity.MODE_PRIVATE);

					JunctionHttp.base_url = mySharedPreferences.getString(
							"baseurl", "http://");
					e.printStackTrace();
					getApplicationContext().startActivity(intent);
					finish();
				}

			}
		}).start();

	}
	private void copyDataBase() throws IOException {
		// Path to the just created empty db
		String outFileName = DB_PATH + DB_NAME;
		// 判断目录是否存在。如不存在则创建一个目录
		File file = new File(DB_PATH);
		if (!file.exists()) {
			file.mkdirs();
		}
		file = new File(outFileName);
		if (!file.exists()) {
			file.createNewFile();
		}
		// Open your local db as the input stream
		InputStream myInput = this.getAssets().open(ASSETS_NAME);
		// Open the empty db as the output stream128
		OutputStream myOutput = new FileOutputStream(outFileName);
		// transfer bytes from the inputfile to the outputfile130
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}
		// Close the streams136
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	/**
	 * 解压缩一个文件
	 * 
	 * @param zipFile
	 *            要解压的压缩文件
	 * @param folderPath
	 *            解压缩的目标目录
	 * @throws IOException
	 *             当解压缩过程出错时抛出
	 */
	/**
	 * 解压缩功能. 将zipFile文件解压到folderPath目录下.
	 * 
	 * @throws Exception
	 */
	public int upZipFile(File zipFile, String folderPath) throws ZipException,
			IOException {
		// public static void upZipFile() throws Exception{
		ZipFile zfile = new ZipFile(zipFile);
		Enumeration zList = zfile.entries();
		ZipEntry ze = null;
		byte[] buf = new byte[1024];
		while (zList.hasMoreElements()) {
			ze = (ZipEntry) zList.nextElement();
			if (ze.isDirectory()) {
				Log.d("upZipFile", "ze.getName() = " + ze.getName());
				String dirstr = folderPath + ze.getName();
				// dirstr.trim();
				dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
				Log.d("upZipFile", "str = " + dirstr);
				File f = new File(dirstr);
				f.mkdir();
				continue;
			}
			Log.d("upZipFile", "ze.getName() = " + ze.getName());
			OutputStream os = new BufferedOutputStream(new FileOutputStream(
					getRealFileName(folderPath, ze.getName())));
			InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
			int readLen = 0;
			while ((readLen = is.read(buf, 0, 1024)) != -1) {
				os.write(buf, 0, readLen);
			}
			is.close();
			os.close();
		}
		zfile.close();
		Log.d("upZipFile", "finishssssssssssssssssssss");
		deleteFile(zipFile);
		return 0;
	}

	/**
	 * 给定根目录，返回一个相对路径所对应的实际文件名.
	 * 
	 * @param baseDir
	 *            指定根目录
	 * @param absFileName
	 *            相对路径名，来自于ZipEntry中的name
	 * @return java.io.File 实际的文件
	 */
	public static File getRealFileName(String baseDir, String absFileName) {
		String[] dirs = absFileName.split("/");
		File ret = new File(baseDir);
		String substr = null;
		if (dirs.length > 1) {
			for (int i = 0; i < dirs.length - 1; i++) {
				substr = dirs[i];
				try {
					// substr.trim();
					substr = new String(substr.getBytes("8859_1"), "GB2312");

				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ret = new File(ret, substr);

			}
			Log.d("upZipFile", "1ret = " + ret);
			if (!ret.exists())
				ret.mkdirs();
			substr = dirs[dirs.length - 1];
			try {
				// substr.trim();
				substr = new String(substr.getBytes("8859_1"), "GB2312");
				Log.d("upZipFile", "substr = " + substr);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			ret = new File(ret, substr);
			Log.d("upZipFile", "2ret = " + ret);
			return ret;
		}
		return ret;
	}

	public void deleteFile(File file) {
		if (file.exists()) { // 判断文件是否存在
			if (file.isFile()) { // 判断是否是文件
				file.delete(); // delete()方法 你应该知道 是删除的意思;
			} else if (file.isDirectory()) { // 否则如果它是一个目录
				File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
				for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
					this.deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
				}
			}
			file.delete();
		} else {
		Log.d("file", "文件不存在");
		}
	}
	/**
	 * @return
	 */
	private String getVersion() {
		try {
			PackageInfo packageInfo = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			return packageInfo.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			Log.e(TAG, "Package name not found", e);
		}
		;
		return null;
	}

	// 设备mac

	public String getMacAddress() {
		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		WifiInfo info = wifi.getConnectionInfo();

		return info.getMacAddress();
	}

	/**
	 * 构造广播监听类，监听 SDK key 验证以及网络异常广播
	 */
	public class SDKReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			Log.d(TAG, "action: " + s);
			TextView text = (TextView) findViewById(R.id.text);
			text.setTextColor(Color.RED);
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				text.setText("key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置");
			} else if (s
					.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				text.setText("网络出错");
			}
		}
	}

	private SDKReceiver mReceiver;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {

		super.onDestroy();
	}

}
