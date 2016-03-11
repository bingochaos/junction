package nlsde.junction;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import nlsde.baidu.BaseMap;
import nlsde.junction.home.Junction;
import nlsde.junction.list.Junction_List;
import nlsde.junction.more.MoreInfo;
import nlsde.junction.net.JunctionHttp;
import nlsde.junction.topbar.TopBar;
import nlsde.junction.topbar.TopBarClickListener;
import nlsde.tools.BitmapDecode;
import nlsde.tools.DensityUtil;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends TabActivity implements OnTabChangeListener {
	private static final String TAG = Main.class.getSimpleName();
	private TabHost tabHost;
	// 当前选中的Tab标号

	private int mCurSelectTabIndex = 0;
	// 默认选中第一个tab页 移动标志操作
	private final int INIT_SELECT = 0;
	// 滑动动画执行时间
	private final int DELAY_TIME = 500;
	private RelativeLayout tips_junctionLayout;

	@Override
	protected void onPause() {
		MobclickAgent.onPause(this);
		super.onPause();
	}

	@Override
	protected void onResume() {
		MobclickAgent.onResume(this);
		super.onResume();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tabhost);
		JunctionHttp.ver = getVersion();
		JunctionHttp.mac = getMacAddress();
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		tabHost = (TabHost) findViewById(android.R.id.tabhost);
		tabHost.setup();
		TabHost.TabSpec spec;
		Intent intent;

		intent = new Intent().setClass(this, Junction.class);
		spec = tabHost.newTabSpec("首页")
				.setIndicator(composeLayout("首页", R.drawable.home_click))
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, Junction_List.class);
		spec = tabHost.newTabSpec("枢纽圈")
				.setIndicator(composeLayout("枢纽圈", R.drawable.shuniuquan))
				.setContent(intent);
		tabHost.addTab(spec);

		intent = new Intent().setClass(this, MoreInfo.class);
		spec = tabHost.newTabSpec("更多")
				.setIndicator(composeLayout("更多", R.drawable.gengduo))
				.setContent(intent);
		tabHost.addTab(spec);
		tabHost.setCurrentTab(0);
		tabHost.setOnTabChangedListener(this);
		// 设置TabHost的背景颜色
		tabHost.setBackgroundColor(Color.BLACK);
		initCurSelectTab();
		// moveTopSelect(0);
		tips_junctionLayout = (RelativeLayout) findViewById(R.id.tips_junctions);
		tips_junctionLayout.setBackgroundDrawable(new BitmapDrawable(
				BitmapDecode.readBitMap(getApplicationContext(),
						R.drawable.tips_bg)));
		SharedPreferences sharedata = getSharedPreferences("fristrun_tips", 0);
		Boolean isSoupon = sharedata.getBoolean("isSoupon", true);
		if (isSoupon) {
			tips_junctionLayout.setVisibility(View.VISIBLE);
		} else {
			tips_junctionLayout.setVisibility(View.GONE);
		}
		tips_junctionLayout.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				tips_junctionLayout.setVisibility(View.GONE);
				Editor sharedata = getSharedPreferences("fristrun_tips", 0)
						.edit();
				sharedata.putBoolean("isSoupon", false);
				sharedata.commit();
			}
		});

	}

	/**
	 * 初始化选中Tab覆盖图片的Handler
	 */
	private Handler initSelectTabHandle = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case INIT_SELECT:
				moveTopSelect(INIT_SELECT);
				textViews.get(mCurSelectTabIndex).setTextColor(0xff1eb1f8);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	/**
	 * 初始化选中Tab覆盖图片
	 */
	public void initCurSelectTab() {
		// 默认选中移动图片位置
		Message msg = new Message();
		msg.what = INIT_SELECT;
		initSelectTabHandle.sendMessageDelayed(msg, DELAY_TIME);
	}

	// 存放Tab页中ImageView信息
	public List<ImageView> imageList = new ArrayList<ImageView>();
	public List<TextView> textViews = new ArrayList<TextView>();

	/**
	 * Tab页改变
	 */
	public void onTabChanged(String tabId) {
		// 设置所有选项卡的图片为未选中图片
		imageList.get(0).setImageDrawable(
				getResources().getDrawable(R.drawable.home));
		imageList.get(1).setImageDrawable(
				getResources().getDrawable(R.drawable.shuniuquan));
		imageList.get(2).setImageDrawable(
				getResources().getDrawable(R.drawable.gengduo));
		textViews.get(0).setTextColor(Color.GRAY);
		textViews.get(1).setTextColor(Color.GRAY);
		textViews.get(2).setTextColor(Color.GRAY);
		if (tabId.equalsIgnoreCase("首页")) {
			imageList.get(0).setImageDrawable(
					getResources().getDrawable(R.drawable.home_click));
			textViews.get(0).setTextColor(0xff1eb1f8);
			// 移动底部背景图片
			moveTopSelect(0);
		} else if (tabId.equalsIgnoreCase("枢纽圈")) {
			imageList.get(1).setImageDrawable(
					getResources().getDrawable(R.drawable.shuniuquan_click));
			textViews.get(1).setTextColor(0xff1eb1f8);
			// 移动底部背景图片
			moveTopSelect(1);
		} else if (tabId.equalsIgnoreCase("更多")) {
			imageList.get(2).setImageDrawable(
					getResources().getDrawable(R.drawable.gengduo_click));
			textViews.get(2).setTextColor(0xff1eb1f8);
			// 移动底部背景图片
			moveTopSelect(2);
		}
	}

	/**
	 * 移动tab选中标识图片
	 * 
	 * @param selectIndex
	 * @param curIndex
	 */
	public void moveTopSelect(int selectIndex) {
		View topSelect = (View) findViewById(R.id.tab_top_select);

		// 起始位置中心点
		int startMid = ((View) getTabWidget().getChildAt(mCurSelectTabIndex))
				.getLeft()
				+ ((View) getTabWidget().getChildAt(mCurSelectTabIndex))
						.getWidth() / 2;
		// 起始位置左边位置坐标
		int startLeft = startMid - topSelect.getWidth() / 2;

		// 目标位置中心点
		int endMid = ((View) getTabWidget().getChildAt(selectIndex)).getLeft()
				+ ((View) getTabWidget().getChildAt(selectIndex)).getWidth()
				/ 2;
		// 目标位置左边位置坐标
		int endLeft = endMid - topSelect.getWidth() / 2;

		TranslateAnimation animation = new TranslateAnimation(startLeft,
				endLeft - topSelect.getLeft(), 0, 0);
		animation.setDuration(200);
		animation.setFillAfter(true);
		topSelect.bringToFront();
		topSelect.startAnimation(animation);

		// 改变当前选中按钮索引
		mCurSelectTabIndex = selectIndex;

		Log.i("fs", "endMid  " + endMid + "  startLeft  " + startLeft
				+ "  endLeft" + (endLeft - topSelect.getLeft()));
	}

	/**
	 * 这个设置Tab标签本身的布局，需要TextView和ImageView不能重合 s:是文本显示的内容 i:是ImageView的图片位置
	 */
	public View composeLayout(String s, int i) {
		// 定义一个LinearLayout布局
		LinearLayout layout = new LinearLayout(this);
		// 设置布局垂直显示
		layout.setOrientation(LinearLayout.VERTICAL);
		ImageView iv = new ImageView(this);
		imageList.add(iv);
		iv.setImageResource(i);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		lp.setMargins(0, 0, 0, 0);
		lp.height = DensityUtil.dip2px(getApplicationContext(), 30);
		layout.setBackgroundColor(Color.WHITE);// Color.rgb(248, 247, 247)
		layout.addView(iv, lp);
		// 定义TextView
		TextView tv = new TextView(this);
		tv.setGravity(Gravity.CENTER);
		textViews.add(tv);
		tv.setSingleLine(true);
		tv.setText(s);
		tv.setTextColor(Color.GRAY);
		tv.setTextSize(15);
		layout.addView(tv, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		return layout;
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
	 * 菜单、返回键响应
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitBy2Click(); // 调用双击退出函数
		}
		return false;
	}

	/**
	 * 双击退出函数
	 */
	private static Boolean isExit = false;

	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true; // 准备退出
			Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false; // 取消退出
				}
			}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

		} else {
			finish();
			System.exit(0);
		}
	}
}
