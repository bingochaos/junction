/**
 * IndoorMap2.java
 * 
 * @Description: 
 * 
 * @File: IndoorMap2.java
 * 
 * @Package nlsde.junction.home.function
 * 
 * @Author chaos
 * 
 * @Date 2014-12-22下午12:40:03
 * 
 * @Version V1.0
 */
package nlsde.junction.home.function;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import nlsde.junction.R;
import nlsde.junction.home.Junction;
import nlsde.junction.home.indoormap.FloorSelectActivity;
import nlsde.junction.home.indoormap.NavigateActivivy;
import nlsde.junction.home.indoormap.PoiPoint;
import nlsde.junction.home.indoormap.SearchActivity;
import nlsde.junction.home.indoormap.ShopDetailActivity;
import nlsde.junction.net.JunctionHttp;
import nlsde.tools.AsyncImageLoader;
import nlsde.tools.BitmapDecode;
import nlsde.tools.DensityUtil;

import org.apache.cordova.api.CordovaInterface;
import org.apache.cordova.api.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.navia.sdk.ISDKManager;
import cn.com.navia.sdk.SDKManager;
import cn.com.navia.sdk.activity.UpdatesAdapter;
import cn.com.navia.sdk.bean.SpectrumInfo;
import cn.com.navia.sdk.locater.services.MsgType;
import cn.edu.buaa.nlsde.wlan.beans.LocationInfo;
import cn.edu.buaa.nlsde.wlan.util.CoordinateUtil;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

/**
 * @author chaos
 * 
 */
public class IndoorMap2 extends Activity implements OnClickListener,
		CordovaInterface {
	protected static final int LocationMSG = 100;

	private static Logger logger = LoggerFactory.getLogger(IndoorMap2.class);
	final UIHandler UIhandler = new UIHandler(new SoftReference<IndoorMap2>(
			this));
	private static ISDKManager sdkManager;
	private PoiPoint start, stop, currenpoint;
	private Button backButton, searchButton, floorButton;
	private AsyncImageLoader loader;
	private static WebView webView;
	private boolean islocation = false, isfollow = false;
	private boolean isNavigate = false;
	// 声明一个对话框
	private PopupWindow popupWindow_toolbar, popupWindow_detail,
			popupWindow_choosepoint, popupWindow_cancelnavigate;
	private View contentView_toolbar, contentView_detail,
			contentView_choosepoint, contentView_cancelnavigate;
	private Intent intent, intent_get;
	private static LocationInfo currentlocation;
	private String click_poiid = "";
	private String floor, floorId;
	private int shopid;
	private JSONArray buslistArray;
	private String[] buslit, buslist;
	private String[] busundowm;
	private Button zoomMax, zoomMin;
	private LinearLayout linerlayout;
	// toolbar
	private ImageButton naviagetButton, locationButton;
	// detail
	private ImageView shop_logo;
	private TextView shop_name, shop_info, indoormap2_choose_name,
			indoormap2_choose_detail;
	private Button shop_getdetail, shop_setstart, shop_setend, shop_cancel,
			indoormap2_ok, indoormap2_cancel;
	private RelativeLayout tips_indoormapLayout;
	private final ExecutorService threadPool = Executors.newCachedThreadPool();

	@Override
	protected void onPause() {
		MobclickAgent.onPause(this);
		super.onPause();
	}

	@Override
	protected void onResume() {
		MobclickAgent.onResume(this);
		new Thread() {
			public void run() {
				mHandler.post(showPop);
			}
		}.start();
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.indoormap2);
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		initView();
		initpop();
		if (sdkManager != null) {
			boolean init = sdkManager.init();
			logger.info("sdkManager init=>{}", init);
		}

		loader = new AsyncImageLoader(getApplicationContext());
		// 将图片缓存至外部文件中
		loader.setCache2File(false); // false
		// 设置外部缓存文件夹
		loader.setCachedDir(this.getCacheDir().getAbsolutePath());
		tips_indoormapLayout = (RelativeLayout) findViewById(R.id.tips_indoormap);

	}

	private static int IndoorMapState;
	private static final int NOMARL = 0;
	private static final int NAVIGATEING = 1;
	private static final int NAVIGATE_FINISHED = 2;
	private static int IntentType;
	private static final int INTENT_CHOOSE_START = 0;
	private static final int INTENT_CHOOSE_END = 1;
	private static final int INTENT_NAVIGATE = 2;
	private static final int INTENT_NONE = 3;
	JSONObject jsonObject_clickpoi;
	private Runnable logoRunnable;

	private final Handler mHandler = new Handler();

	// 构建Runnable对象，在runnable中更新界面
	Runnable runnableUi = new Runnable() {
		@Override
		public void run() {
			if (!click_poiid.equals("")) {
				// webView.loadUrl("javascript:cleanHighLightPoi();");
				showPop_Detail();
				// webView.loadUrl("javascript:indoorMap.setFloor(\"" + floorId
				// + "\", map);");
				webView.loadUrl("javascript:changeFloor(\"" + floorId + "\");");
				webView.loadUrl("javascript:highlightPoiById(\"" + click_poiid
						+ "\");");
			}
		}

	};
	Runnable runnableClick = new Runnable() {
		@Override
		public void run() {
			webView.loadUrl("javascript:cleanLastHighLightPoi();");
			// webView.loadUrl("javascript:indoorMap.setFloor(\"" + floorId
			// + "\", map);");
			//
			showPop_Detail();
		}

	};
	Runnable showPop = new Runnable() {
		@Override
		public void run() {
			if (IndoorMapState == NOMARL && click_poiid.equals("")) {
				if (popupWindow_toolbar.isShowing())
					popupWindow_toolbar.dismiss();
			}
		}

	};
	Runnable showpopr_detail = new Runnable() {

		@Override
		public void run() {
			popupWindow_detail.showAtLocation(backButton,
					Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);

		}
	};
	Runnable cancel_detail = new Runnable() {

		@Override
		public void run() {
			if (popupWindow_detail.isShowing()) {
				popupWindow_detail.dismiss();
			}

		}
	};
	Runnable showpoprRunnable = new Runnable() {

		@Override
		public void run() {
			try {
				indoormap2_choose_name.setText(jsonObject_clickpoi
						.getString("name"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!popupWindow_choosepoint.isShowing())
				popupWindow_choosepoint.showAtLocation(backButton,
						Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);

		}
	};
	Runnable cancelpopRunnable = new Runnable() {

		@Override
		public void run() {
			if (popupWindow_choosepoint.isShowing()) {
				popupWindow_choosepoint.dismiss();
			}

		}
	};

	/**
	 * 初始化控件
	 */
	private void initView() {
		start = new PoiPoint();
		stop = new PoiPoint();
		// //sdk manager
		sdkManager = SDKManager.getInstance(getApplicationContext(), null,
				UIhandler);
		//
		if (sdkManager == null) {
			Toast.makeText(getApplicationContext(), "初始化失败", Toast.LENGTH_SHORT).show();
			// System.exit(1);
			return;
		}
		webView = (WebView) this.findViewById(R.id.indoormap2_web);
		webView.getSettings().setLightTouchEnabled(true);
		webView.getSettings().setJavaScriptEnabled(true);
		try {
			JSONObject jsonObject_hublist=JunctionHttp.gethublist();
			
			if (Junction.hid == 1) {
				webView.loadUrl("http://"+jsonObject_hublist.getJSONObject("data").getJSONArray("list").getJSONObject(0).getString("mapUrl"));
			} else {
				webView.loadUrl("http://"+jsonObject_hublist.getJSONObject("data").getJSONArray("list").getJSONObject(1).getString("mapUrl"));

			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// webView.loadUrl("http://192.168.0.114:8080/MallUI/jsp/mapview.jsp?id=1");
		webView.addJavascriptInterface(new WebToAndroid(this), "androidShow");
		webView.setWebChromeClient(new WebChromeClient());
		webView.requestFocus();
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				new Thread() {
					public void run() {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						mHandler.post(runnableUi);
					}
				}.start();
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				view.destroy();
				linerlayout.removeAllViews();
				floorButton.setClickable(false);
				searchButton.setClickable(false);
				Toast.makeText(getApplicationContext(), R.string.neterror,
						Toast.LENGTH_SHORT).show();
				super.onReceivedError(view, errorCode, description, failingUrl);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// 开始
				super.onPageStarted(view, url, favicon);
			}

		});

		backButton = (Button) findViewById(R.id.indoormap2_back);
		backButton.setOnClickListener(this);
		searchButton = (Button) findViewById(R.id.indoormap2_search);
		searchButton.setOnClickListener(this);
		floorButton = (Button) findViewById(R.id.indoormap2_choosefloor);
		floorButton.setOnClickListener(this);
		zoomMax = (Button) findViewById(R.id.indoormap2_zoomMax);
		zoomMax.setOnClickListener(this);
		zoomMin = (Button) findViewById(R.id.indoormap2_zoomMin);
		zoomMin.setOnClickListener(this);
		linerlayout = (LinearLayout) findViewById(R.id.zoom);
		IndoorMapState = NOMARL;
		logoRunnable = new Runnable() {
			public void run() {

				if (logoBitmap != null)
					shop_logo.setImageBitmap(logoBitmap);

			}

		};

		initpop();
		IndoorMap2.this.runOnUiThread(logoRunnable);

		intent_get = getIntent();
		if (intent_get.hasExtra("poiId") && intent_get.hasExtra("toilet")) {
			click_poiid = intent_get.getExtras().getString("poiId");
			try {
				JSONObject jsonObject = JunctionHttp.getpoi2floor(click_poiid);
				floor = jsonObject.getJSONObject("data").getString("label");
				floorButton.setText(floor);
				floorId = jsonObject.getJSONObject("data").getString("floorId");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			floor = "F1";
			floorButton.setText(floor);
		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		if (intent.hasExtra("choosepoint_type")) {
			start = (PoiPoint) intent.getSerializableExtra("start");
			stop = (PoiPoint) intent.getSerializableExtra("stop");
			switch (intent.getExtras().getInt("choosepoint_type")) {
			case 1:
				IndoorMapState = NAVIGATEING;
				IntentType = INTENT_CHOOSE_START;
				// webView.loadUrl("javascript:cleanHighLightPoi();");
				break;
			case 2:
				IndoorMapState = NAVIGATEING;
				IntentType = INTENT_CHOOSE_END;
				// webView.loadUrl("javascript:cleanHighLightPoi();");
				break;
			case 5:
				IndoorMapState = NOMARL;
				IntentType = INTENT_NAVIGATE;
				// webView.loadUrl("javascript:cleanHighLightPoi();");
				click_poiid = "";
				// webView.loadUrl("javascript:indoorMap.setFloor(\"" +
				// start.getFloorId()
				// + "\", map);");
				webView.loadUrl("javascript:changeFloor(\""
						+ start.getFloorId() + "\");");
				floor = start.getFloorId().substring(
						start.getFloorId().length() - 2,
						start.getFloorId().length());

				floorButton.setText(floor);
				// webView.loadUrl("javascript:navi(['39.94118354613706','116.42884395477816'],'Floor1',['39.940474022317034','116.42905642496169'],'Floor1');");
				webView.loadUrl("javascript:navi([\"" + start.getClat()
						+ "\",\"" + start.getClon() + "\"],\"" + ""
						+ start.getMapName() + "\",[\"" + stop.getClat()
						+ "\",\"" + stop.getClon() + "\"],\""
						+ stop.getMapName() + "\");");
				// if (!popupWindow_cancelnavigate.isShowing())
				// popupWindow_cancelnavigate.showAtLocation(backButton,
				// Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 80);
				if (IndoorMapState == NOMARL) {
					popupWindow_toolbar.showAtLocation(backButton,
							Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 80);
				}
				break;
			default:
				break;
			}
		} else if (intent.hasExtra("poiId") && intent.hasExtra("toilet")) {
			click_poiid = intent.getExtras().getString("poiId");
			try {
				JSONObject jsonObject = JunctionHttp.getpoi2floor(click_poiid);
				floor = jsonObject.getJSONObject("data").getString("label");
				// if (Junction.hid == 1) {
				// webView.loadUrl("http://115.29.149.25/jsp/mapview.jsp?id=1");
				// } else {
				// webView.loadUrl("http://115.29.149.25/jsp/mapview.jsp?id=2");
				//
				// }
				floorButton.setText(floor);
				floorId = jsonObject.getJSONObject("data").getString("floorId");
				mHandler.post(runnableUi);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (intent.hasExtra("poiId")) {
			click_poiid = intent.getExtras().getString("poiId");
			JSONObject jsonObject;
			try {
				jsonObject = JunctionHttp.getpoi2floor(click_poiid);

				floor = jsonObject.getJSONObject("data").getString("label");
				// if (Junction.hid == 1) {
				// webView.loadUrl("http://115.29.149.25/jsp/mapview.jsp?id=1");
				// } else {
				// webView.loadUrl("http://115.29.149.25/jsp/mapview.jsp?id=2");
				//
				// }
				floorButton.setText(floor);
				floorId = jsonObject.getJSONObject("data").getString("floorId");
				mHandler.post(runnableUi);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		super.onNewIntent(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		try {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				this.finish();
				return true;
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;

	}

	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);

		tips_indoormapLayout.setBackgroundDrawable(new BitmapDrawable(
				BitmapDecode.readBitMap(getApplicationContext(),
						R.drawable.tips_bg)));
		SharedPreferences sharedata = getSharedPreferences("fristrun_tips", 0);
		Boolean isSoupon = sharedata.getBoolean("isIndoormap_tips", true);
		if (hasFocus) {
			if (isSoupon) {
				tips_indoormapLayout.setVisibility(View.VISIBLE);
			} else if (hasFocus) {
				tips_indoormapLayout.setVisibility(View.GONE);
				// 有焦点的时候，让你的PopupWindow显示出来
				if (hasFocus && !click_poiid.equals("")) {
					// mHandler.post(runnableUi);
				} else if (hasFocus && IndoorMapState == NOMARL) {
					popupWindow_toolbar.showAtLocation(backButton,
							Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 80);

				}
			}
		} else if (!hasFocus) {
			if (popupWindow_toolbar.isShowing())
				popupWindow_toolbar.dismiss();
		}
		tips_indoormapLayout.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				tips_indoormapLayout.setVisibility(View.GONE);
				Editor sharedata = getSharedPreferences("fristrun_tips", 0)
						.edit();
				sharedata.putBoolean("isIndoormap_tips", false);
				sharedata.commit();
				// 有焦点的时候，让你的PopupWindow显示出来
				if (!click_poiid.equals("")) {
					// mHandler.post(runnableUi);
				} else if (IndoorMapState == NOMARL) {
					popupWindow_toolbar.showAtLocation(backButton,
							Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 80);
				}
			}
		});

	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {

		return false;
	}

	/**
	 * indoormap2_toolbar
	 */
	private void initpop() {
		// 初始化路线，定位
		popupWindow_toolbar = new PopupWindow(null, DensityUtil.dip2px(
				getApplicationContext(), 250), DensityUtil.dip2px(
				getApplicationContext(), 40));
		contentView_toolbar = LayoutInflater.from(getApplicationContext())
				.inflate(R.layout.indoormap2_toolbar, null);
		contentView_toolbar.getBackground().setAlpha(0);
		popupWindow_toolbar.setContentView(contentView_toolbar);
		naviagetButton = (ImageButton) contentView_toolbar
				.findViewById(R.id.indoormap2_navigate);
		naviagetButton.setOnClickListener(this);
		locationButton = (ImageButton) contentView_toolbar
				.findViewById(R.id.indoormap2_location);
		locationButton.setOnClickListener(this);

		// detail 商家公交信息
		popupWindow_detail = new PopupWindow(null, LayoutParams.FILL_PARENT,
				DensityUtil.dip2px(getApplicationContext(), 120));
		contentView_detail = LayoutInflater.from(getApplicationContext())
				.inflate(R.layout.shoppoidetail, null);
		// contentView_detail.setBackgroundColor(Color.WHITE);
		popupWindow_detail.setContentView(contentView_detail);
		shop_logo = (ImageView) contentView_detail
				.findViewById(R.id.shop_poi_logo);
		shop_name = (TextView) contentView_detail
				.findViewById(R.id.shop_poi_name);
		shop_info = (TextView) contentView_detail
				.findViewById(R.id.shop_poi_detail);
		shop_getdetail = (Button) contentView_detail
				.findViewById(R.id.shop_poi_detailbutton);
		shop_setstart = (Button) contentView_detail
				.findViewById(R.id.shop_poi_setstart);
		shop_setend = (Button) contentView_detail
				.findViewById(R.id.shop_poi_setend);
		shop_cancel = (Button) contentView_detail
				.findViewById(R.id.shop_poi_cancel);
		shop_cancel.setOnClickListener(this);
		shop_setstart.setOnClickListener(this);
		shop_setend.setOnClickListener(this);
		// 选点确定
		popupWindow_choosepoint = new PopupWindow(null,
				LayoutParams.FILL_PARENT, DensityUtil.dip2px(
						getApplicationContext(), 50));
		// 加载PopupWindow的布局文件
		contentView_choosepoint = LayoutInflater.from(getApplicationContext())
				.inflate(R.layout.indoormap2_choosepoint_ok, null);

		// 设置PopupWindow的背景颜色
		// contentView_choosepoint.setBackgroundColor(Color.WHITE);

		// 为自定义的对话框设置自定义布局
		popupWindow_choosepoint.setContentView(contentView_choosepoint);
		indoormap2_choose_name = (TextView) contentView_choosepoint
				.findViewById(R.id.indoormap2_choosepoint_name);
		// indoormap2_choose_detail=
		// (TextView)contentView_choosepoint.findViewById(R.id.indoormap2_choosepoint_detail);

		indoormap2_ok = (Button) contentView_choosepoint
				.findViewById(R.id.indoormap2_chooseok);
		indoormap2_ok.setOnClickListener(this);
		// 退出规划
		popupWindow_cancelnavigate = new PopupWindow(null,
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		// 加载PopupWindow的布局文件
		contentView_cancelnavigate = LayoutInflater.from(
				getApplicationContext()).inflate(R.layout.indoormap2_cancel,
				null);
		// 设置PopupWindow的背景颜色
		contentView_cancelnavigate.setBackgroundColor(Color.WHITE);

		// 为自定义的对话框设置自定义布局
		popupWindow_cancelnavigate.setContentView(contentView_cancelnavigate);
		indoormap2_cancel = (Button) contentView_cancelnavigate
				.findViewById(R.id.indoormap2_cancelnavigate);
		indoormap2_cancel.setOnClickListener(this);
	}

	class WebToAndroid {
		Context mContext;

		public WebToAndroid(Context mContext) {
			this.mContext = mContext;
		}

		@JavascriptInterface
		public void show(String s) {
			try {
				jsonObject_clickpoi = new JSONObject(s).getJSONObject("result");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			poiClicked(jsonObject_clickpoi.toString());
			// click_poiid=s;
			// Toast.makeText(droidGap.getApplicationContext(), s,
			// Toast.LENGTH_SHORT).show();
		}

		@JavascriptInterface
		public void alert(String s) {
			alert(s);
		}

		@JavascriptInterface
		public void highlight(String s) {
			try {
				jsonObject_clickpoi = new JSONObject(s);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// poiClicked(s);
			// click_poiid=s;
			// Toast.makeText(droidGap.getApplicationContext(), s,
			// Toast.LENGTH_SHORT).show();
		}

		@JavascriptInterface
		public String forAlert(String s) {
			return "java_" + s;
		}
	}

	/*
	 * 监听所有按钮
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.indoormap2_back:
			if (IndoorMapState == NAVIGATEING) {
				Intent intent_navigate = new Intent(IndoorMap2.this,
						NavigateActivivy.class);
				intent_navigate.putExtra("navigatetype", 3);
				Bundle bundle = new Bundle();
				bundle.putSerializable("start", start);
				bundle.putSerializable("stop", stop);
				mHandler.post(cancelpopRunnable);
				click_poiid = "";
				intent_navigate.putExtras(bundle);
				startActivityForResult(intent_navigate, 0);

			} else {
				IndoorMap2.this.finish();
			}

			break;

		case R.id.indoormap2_search:
			Intent searchIntent = new Intent(IndoorMap2.this,
					SearchActivity.class);
			startActivity(searchIntent);
			break;
		case R.id.indoormap2_choosefloor:
			Intent choosefloorIntent = new Intent(IndoorMap2.this,
					FloorSelectActivity.class);
			choosefloorIntent.putExtra("floor", floor);
			click_poiid = "";
			startActivityForResult(choosefloorIntent, FLOORCHOOSE);
			break;

		case R.id.indoormap2_navigate:
			if (isNavigate == false) {
				// naviagetButton
				// .setBackgroundResource(R.drawable.indoormap_barleft_click);//
				// 进入路线规划
				// isNavigate = true;
				webView.loadUrl("javascript:cleanRoute();");
				start = new PoiPoint();
				stop = new PoiPoint();
				Intent intent_navigate = new Intent(IndoorMap2.this,
						NavigateActivivy.class);
				intent_navigate.putExtra("navigatetype", 0);
				Bundle bundle = new Bundle();
				bundle.putSerializable("start", start);
				bundle.putSerializable("stop", stop);
				intent_navigate.putExtras(bundle);
				startActivityForResult(intent_navigate, 0);
				popupWindow_toolbar.dismiss();
			} else {

				naviagetButton
						.setBackgroundResource(R.drawable.indoormap_barleft);// 取消路线规划
				isNavigate = false;
			}
			break;

		case R.id.indoormap2_location:
			if (Junction.hid == 1) {
				if (islocation == false) {// 开启定位
					islocation = true;
					try {
						//sdkManager.downSpectrum(6, 1);
						sdkManager.loadLocalUpdates();
					} catch (RemoteException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					logger.info("sdkmanager download", "down");
					
					locationButton
							.setBackgroundResource(R.drawable.indoormap_barright_click);// 进入路线规划
					isfollow = true;
					Toast.makeText(getApplicationContext(), "正在启动定位，请勿进行其他操作",
							Toast.LENGTH_SHORT).show();
				} else {
					if (isfollow == false) {// 是否跟随
						locationButton
								.setBackgroundResource(R.drawable.indoormap_barright_click);// 进入路线规划
						isfollow = true;
						Toast.makeText(getApplicationContext(), "正在跟随",
								Toast.LENGTH_SHORT).show();
					} else {
						locationButton
								.setBackgroundResource(R.drawable.indoormap_barright);// 取消路线规划
						isfollow = false;
						Toast.makeText(getApplicationContext(), "停止跟随",
								Toast.LENGTH_SHORT).show();
					}
				}
			} else {
				Toast.makeText(getApplicationContext(), "尚未支持定位，敬请期待！",
						Toast.LENGTH_LONG).show();
			}

			break;
		case R.id.shop_poi_cancel:
			mHandler.post(cancel_detail);
			if (!popupWindow_toolbar.isShowing())
				popupWindow_toolbar.showAtLocation(backButton,
						Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 80);
			// webView.loadUrl("javascript:cleanHighLightPoi();");
			break;
		case R.id.shop_poi_setstart:
			Intent intent_navigate_start = new Intent(IndoorMap2.this,
					NavigateActivivy.class);
			intent_navigate_start.putExtra("navigatetype", 1);
			try {
				JSONObject jsonObject = JunctionHttp.getpoi2floor(click_poiid);
				start.setCaption(jsonObject_clickpoi.getString("name") + " "
						+ jsonObject.getJSONObject("data").getString("name"));
				start.setFloorId(jsonObject.getJSONObject("data").getString(
						"floorId"));
				start.setMapName(jsonObject.getJSONObject("data").getString(
						"mapName"));
				start.setClat(jsonObject_clickpoi.getString("clat"));
				start.setClon(jsonObject_clickpoi.getString("clon"));
				Bundle bundle_start = new Bundle();
				bundle_start.putSerializable("start", start);
				// bundle_start.putSerializable("stop", stop);

				intent_navigate_start.putExtras(bundle_start);
				startActivityForResult(intent_navigate_start, 1);
				mHandler.post(cancel_detail);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case R.id.shop_poi_setend:
			Intent intent_navigate_end = new Intent(IndoorMap2.this,
					NavigateActivivy.class);
			intent_navigate_end.putExtra("navigatetype", 2);
			try {
				JSONObject jsonObject = JunctionHttp.getpoi2floor(click_poiid);
				stop.setCaption(jsonObject_clickpoi.getString("name") + " "
						+ jsonObject.getJSONObject("data").getString("name"));
				stop.setFloorId(jsonObject.getJSONObject("data").getString(
						"floorId"));
				stop.setMapName(jsonObject.getJSONObject("data").getString(
						"mapName"));
				stop.setClat(jsonObject_clickpoi.getString("clat"));
				stop.setClon(jsonObject_clickpoi.getString("clon"));
				Bundle bundle_end = new Bundle();
				// bundle_end.putSerializable("start", start);
				bundle_end.putSerializable("stop", stop);

				intent_navigate_end.putExtras(bundle_end);
				startActivityForResult(intent_navigate_end, 1);
				mHandler.post(cancel_detail);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;
		case R.id.indoormap2_chooseok:
			if (IntentType == INTENT_CHOOSE_START) {
				Intent intent_navigate_start_ok = new Intent(IndoorMap2.this,
						NavigateActivivy.class);
				intent_navigate_start_ok.putExtra("navigatetype", 3);
				try {
					JSONObject jsonObject = JunctionHttp
							.getpoi2floor(click_poiid);
					start.setCaption(jsonObject_clickpoi.getString("name")
							+ " "
							+ jsonObject.getJSONObject("data")
									.getString("name"));
					start.setFloorId(jsonObject.getJSONObject("data")
							.getString("floorId"));
					start.setMapName(jsonObject.getJSONObject("data")
							.getString("mapName"));
					start.setClat(jsonObject_clickpoi.getString("clat"));
					start.setClon(jsonObject_clickpoi.getString("clon"));
					Bundle bundle_start = new Bundle();
					bundle_start.putSerializable("start", start);
					bundle_start.putSerializable("stop", stop);

					intent_navigate_start_ok.putExtras(bundle_start);
					startActivityForResult(intent_navigate_start_ok, 1);
					mHandler.post(cancelpopRunnable);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (IntentType == INTENT_CHOOSE_END) {
				Intent intent_navigate_end_ok = new Intent(IndoorMap2.this,
						NavigateActivivy.class);
				intent_navigate_end_ok.putExtra("navigatetype", 3);
				try {
					JSONObject jsonObject = JunctionHttp
							.getpoi2floor(click_poiid);
					stop.setCaption(jsonObject_clickpoi.getString("name")
							+ " "
							+ jsonObject.getJSONObject("data")
									.getString("name"));
					stop.setFloorId(jsonObject.getJSONObject("data").getString(
							"floorId"));
					stop.setMapName(jsonObject.getJSONObject("data").getString(
							"mapName"));
					stop.setClat(jsonObject_clickpoi.getString("clat"));
					stop.setClon(jsonObject_clickpoi.getString("clon"));
					Bundle bundle_end = new Bundle();
					bundle_end.putSerializable("start", start);
					bundle_end.putSerializable("stop", stop);

					intent_navigate_end_ok.putExtras(bundle_end);
					startActivityForResult(intent_navigate_end_ok, 1);
					mHandler.post(cancelpopRunnable);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			break;
		case R.id.indoormap2_cancelnavigate:
			popupWindow_cancelnavigate.dismiss();
			// webView.loadUrl("javascript:cleanRoute();");
			start = new PoiPoint();
			stop = new PoiPoint();
			IndoorMapState = NOMARL;
			break;
		case R.id.indoormap2_zoomMax:
			zoomMax.setBackgroundResource(R.drawable.zoommax_click);
			webView.loadUrl("javascript:map.zoomIn();");
			zoomMax.setBackgroundResource(R.drawable.zoommax);
			break;
		case R.id.indoormap2_zoomMin:
			zoomMin.setBackgroundResource(R.drawable.zoommin_click);
			webView.loadUrl("javascript:map.zoomOut();");
			zoomMin.setBackgroundResource(R.drawable.zoommin);
			break;
		default:
			break;
		}

	}

	private static int FLOORCHOOSE = 100;

	/*
	 * floorchoose
	 */
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// Log.v(TAG, arg0 + " " + arg1);
		switch (arg1) {
		case RESULT_OK:
			// Log.v(TAG, arg2.getExtras().getString("floorId"));
			floor = arg2.getExtras().getString("label");
			floorButton.setText(arg2.getExtras().getString("label"));
			// webView.loadUrl("javascript:indoorMap.setFloor(\""
			// + arg2.getExtras().getString("floorId") + "\", map);");

			webView.loadUrl("javascript:changeFloor(\""
					+ arg2.getExtras().getString("floorId") + "\");");
			floorId = arg2.getExtras().getString("floorId");
			mHandler.post(cancel_detail);
			if (!popupWindow_toolbar.isShowing())
				popupWindow_toolbar.showAtLocation(backButton,
						Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 80);
			break;
		case 4:
			if (!popupWindow_toolbar.isShowing())
				popupWindow_toolbar.showAtLocation(backButton,
						Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 80);
			IndoorMapState = NOMARL;
			// ////webView.loadUrl("javascript:cleanHighLightPoi();");
			break;
		case 2:
			if (!popupWindow_toolbar.isShowing())
				popupWindow_toolbar.showAtLocation(backButton,
						Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 80);

			break;
		default:
			break;
		}
		// super.onActivityResult(arg0, arg1, arg2);
	}

	/**
	 * poi的响应事件
	 */
	public void poiClicked(String s) {
		try {

			click_poiid = jsonObject_clickpoi.getString("id");

			switch (IndoorMapState) {
			case NOMARL:
				new Thread() {
					public void run() {
						mHandler.post(runnableClick);
					}
				}.start();

				break;
			case NAVIGATEING:
				showPop_ChoosePoint();
				break;
			case NAVIGATE_FINISHED:

				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 确认规划点
	 */
	private void showPop_ChoosePoint() {

		mHandler.post(showpoprRunnable);

	}

	/**
	 * 显示店铺详情
	 */
	public void showPop_Detail() {
		try {
			JSONObject jsonObject = JunctionHttp.getPoiSelected(click_poiid);

			if (jsonObject.getInt("ret") == 0) {
				switch (jsonObject.getJSONObject("data").getInt("type")) {
				case 1:
					loadshopdetail(jsonObject);
					shop_getdetail.setClickable(false);
					shop_getdetail.setTextColor(0xff8e8e8e);
					break;
				case 2:
					loadshopdetail(jsonObject);
					shopid = jsonObject.getJSONObject("data").getInt("shopId");
					shop_getdetail.setTextColor(0xff000000);
					shop_getdetail.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent intent_shop = new Intent(IndoorMap2.this,
									ShopDetailActivity.class);
							intent_shop.putExtra("shopId", shopid);
							intent_shop.putExtra("poiId", click_poiid);
							startActivity(intent_shop);

						}
					});
					break;
				case 3:
					loadshopdetail(jsonObject);
					shop_getdetail.setTextColor(0xff000000);
					buslistArray = jsonObject.getJSONObject("data")
							.getJSONArray("options");
					shop_getdetail.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							buslit = new String[buslistArray.length()];
							buslist = new String[buslistArray.length()];
							busundowm = new String[buslistArray.length()];
							for (int i = 0; i < buslit.length; i++) {
								try {
									buslit[i] = buslistArray.getJSONObject(i)
											.getString("caption");
									buslist[i] = buslistArray.getJSONObject(i)
											.getString("route");
									busundowm[i] = buslistArray
											.getJSONObject(i).getString(
													"updown");
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							new AlertDialog.Builder(IndoorMap2.this,
									R.style.AlertDialogCustom)
									.setTitle("公交列表")
									.setItems(
											buslit,
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													Intent intent_bus = new Intent(
															IndoorMap2.this,
															BusActivity2.class);
													intent_bus.putExtra(
															"route",
															buslist[which]);
													intent_bus.putExtra(
															"updown",
															busundowm[which]);
													startActivity(intent_bus);
												}
											}).setNegativeButton("取消", null)

									.show();

						}
					});
					break;
				default:
					break;
				}
			} else if (jsonObject.getInt("ret") == -7) {
				JunctionHttp.init();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Bitmap logoBitmap;

	/**
	 * @throws Exception
	 * 
	 */
	private void loadshopdetail(JSONObject jsonObject) throws Exception {
		shop_name.setText(jsonObject.getJSONObject("data").getString("name"));
		shop_info.setText(jsonObject.getJSONObject("data").getString("info"));
		loader.downloadImage("http://"
				+ jsonObject.getJSONObject("data").getString("logo"), true,
				new AsyncImageLoader.ImageCallback() {
					@Override
					public void onImageLoaded(Bitmap bitmap, String imageUrl) {
						if (bitmap != null) {
							logoBitmap = bitmap;
							shop_logo.post(logoRunnable);
						} else {
							// 下载失败，设置默认图片
						}
					}
				});
		if (!popupWindow_detail.isShowing())
			mHandler.post(showpopr_detail);

	}

	public static class UIHandler extends Handler {

		private SoftReference<IndoorMap2> activityRef;
		private TextView location_time;

		public UIHandler(SoftReference<IndoorMap2> activity) {
			this.activityRef = activity;
		}

		public IndoorMap2 getActivity() {
			return activityRef.get();
		}

		@Override
		public void handleMessage(Message msg) {

			Log.i(this.getClass().getSimpleName(), "msg.what:" + msg.what);
			Log.i(this.getClass().getSimpleName(), "msg.obj:" + msg.obj);

			IndoorMap2 mainActivity = getActivity();

			if (mainActivity == null) {
				logger.error("activityRef.get() == NULL");
				return;
			}

			if (msg.what == MsgType.SERVER_SPECS.getId()
					&& msg.obj instanceof List) {

				List<SpectrumInfo> specs = (List<SpectrumInfo>) msg.obj;
				// setLatestUpdates2ListView(specs, (ListView)
				// mainActivity.findViewById(R.id.latest_updates_list_view));

			} else if (msg.what == MsgType.LOCAL_SUCCESS.getId()
					&& msg.obj instanceof LinkedHashMap) {

				LinkedHashMap<Integer, SpectrumInfo> zipSpecs = (LinkedHashMap<Integer, SpectrumInfo>) msg.obj;

				SpectrumInfo spectrumInfo = zipSpecs.get(6);
				if(zipSpecs.containsKey(6)){
					sdkManager.startLocater(6);
				}else {
					try {						
						Toast.makeText(getActivity().getApplicationContext(),"第一次定位请耐心等候", Toast.LENGTH_SHORT).show();

						sdkManager.downSpectrum(6, 1);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				
				// setLocalSpecs2ListView( zipSpecs.values() , (ListView)
				// mainActivity
				// .findViewById(R.id.local_updates_list_view));

			} else if (msg.what == MsgType.LOCATION.getId()) {
				LocationInfo loc = (LocationInfo) msg.obj;
				if (loc != null) {
					logger.info("location: map:{} x:{} y:{} places:{}",
							loc.getMapid(), loc.getX(), loc.getY(),
							loc.getPlaceList());
					System.out.println(loc.toString());
					CoordinateUtil.LatLng latLng = loc.getLatLng();
					if (latLng != null) {

						// clean
						String js = "javascript:map.removeLayer(Marker);";
						webView.loadUrl(js);

						// animMarker
						if (currentlocation != null) {
							js = ("javascript:animMarker(["
									+ currentlocation.getLatLng().getLat()
									+ ","
									+ currentlocation.getLatLng().getLng()
									+ "],[" + loc.getLatLng().getLat() + ","
									+ loc.getLatLng().getLng() + "]);");
						} else {
							js = ("javascript:animMarker(["
									+ loc.getLatLng().getLat() + ","
									+ loc.getLatLng().getLng() + "],["
									+ loc.getLatLng().getLat() + ","
									+ loc.getLatLng().getLng() + "]);");
						}
						webView.loadUrl(js);
						currentlocation = loc;
						//Toast.makeText(getActivity().getApplicationContext(), currentlocation.toString(), Toast.LENGTH_LONG).show();
					}

				} else if (msg.what == MsgType.DOWNLOAD_ERROR.getId()) {

					Toast.makeText(mainActivity.getApplicationContext(),
							msg.obj + "", Toast.LENGTH_SHORT).show();
				} else if (msg.what == MsgType.ERR_SDK.getId()) {

					Toast.makeText(mainActivity.getApplicationContext(),
							msg.obj + "", Toast.LENGTH_SHORT).show();
				}
			} else if (msg.what == MsgType.DOWNLOAD_SUCCESS.getId()) {
				sdkManager.startLocater(6);
			} else if (msg.what == MsgType.DOWNLOAD_ERROR.getId()) {
				Toast.makeText(mainActivity.getApplicationContext(),
						"暂时无法定位，请稍后再试", Toast.LENGTH_LONG).show();
			}

			logger.info("msg.what:{}", msg.what);
		}

		private void setLocalSpecs2ListView(Collection<SpectrumInfo> zipSpecs,
				ListView localListView) {
			setLatestUpdates2ListView(zipSpecs, localListView);
		}

		private void setLatestUpdates2ListView(Collection<SpectrumInfo> specs,
				ListView serverLatestView) {
			final IndoorMap2 activity = getActivity();
			if (activity == null) {
				return;
			}

			serverLatestView.setAdapter(new UpdatesAdapter(
					new ArrayList<SpectrumInfo>(specs),
					new UpdatesAdapter.OnClick() {
						@Override
						public void click(int position, SpectrumInfo info) {
							ISDKManager _sdkManager = activity.getSDKManager();
							if (_sdkManager != null) {
								try {
									_sdkManager.downSpectrum(info
											.getUpdateItem().getBuilding_id(),
											info.getUpdateItem().getVersion());
								} catch (RemoteException e) {
									e.printStackTrace();
								}
							}
						}
					}, activity));
		}

	};

	public ISDKManager getSDKManager() {
		return this.sdkManager;
	}

	@Override
	public void onDestroy() {
		//
		if (webView != null) {
			webView.destroy();
		}
		if (sdkManager != null) {
			boolean destroy = sdkManager.destroy();
			logger.info("sdkManager init=>{}", destroy);
		}
		super.onDestroy();
	}

	@Override
	public Activity getActivity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ExecutorService getThreadPool() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object onMessage(String arg0, Object arg1) {
		// TODO Auto-generated method stub1
		return null;
	}

	@Override
	public void setActivityResultCallback(CordovaPlugin arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startActivityForResult(CordovaPlugin arg0, Intent arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
