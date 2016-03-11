/**
 * Airport_bus.java
 * 
 * @Description: 
 * 
 * @File: Airport_bus.java
 * 
 * @Package nlsde.junction.home.function
 * 
 * @Author chaos
 * 
 * @Date 2014-12-16下午2:25:44
 * 
 * @Version V1.0
 */
package nlsde.junction.home.function;

import nlsde.junction.R;
import nlsde.junction.home.Junction;
import nlsde.junction.home.function.Airport_bus.ImageAdapter;
import nlsde.junction.net.JunctionHttp;
import nlsde.junction.topbar.TopBar;
import nlsde.junction.topbar.TopBarClickListener;
import nlsde.tools.DensityUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * @author Administrator
 *
 */
public class Airport_bus extends Activity implements TopBarClickListener{
	private TopBar topBar;
	private TextView busnum, busstart, busstop, extratext, busstarttime,
			busstoptime, buslocation, bus_first, bus_second, bus_third;
	private RelativeLayout bus2_locationLayout;
	private Intent intent;
	private String[] busstateStrings = {};
	private Button refreshButton,bus2_left,bus2_right,bus2_youjiantou;
	private String route, updown, hid, extra;
	private String startStation, stopStation, starttime, stoptime;
	private int current;
	private Gallery gallery;
	private String real="0";
	private String buslocationString;
	private String buspoi="";
	private int width;
	private LinearLayout bus2bodyLayout;
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.airport_bus);
		width = getWindowManager().getDefaultDisplay().getWidth();
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		initView();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		topBar = (TopBar) findViewById(R.id.topbar_bus2);
		topBar.setLeftButton(R.drawable.fanhui);
		topBar.setTopBarClickListener(this);
		busnum = (TextView) findViewById(R.id.busnum2);
		busstart = (TextView) findViewById(R.id.bus2_start);
		busstop = (TextView) findViewById(R.id.bus2_stop);
		busstarttime = (TextView) findViewById(R.id.bus2_starttime);
		busstoptime = (TextView) findViewById(R.id.bus2_stoptime);
		buslocation = (TextView) findViewById(R.id.bus2_locationdetail);
		bus_first = (TextView) findViewById(R.id.bus2first);
		bus_second = (TextView) findViewById(R.id.bus2second);
		bus_third = (TextView) findViewById(R.id.bus2third);
		extratext = (TextView) findViewById(R.id.bus2_extra);
		refreshButton = (Button) findViewById(R.id.bus2_detailrefresh);
		bus2_youjiantou = (Button)findViewById(R.id.bus2_youjiantou);
		bus2_locationLayout = (RelativeLayout) findViewById(R.id.bus2_location);
		bus2_left = (Button)findViewById(R.id.bus2_left);
		bus2bodyLayout= (LinearLayout)findViewById(R.id.airport_body);
		bus2_left.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
			
			}
		});
		intent = getIntent();

		route = intent.getExtras().getString("bus_num");
		updown = intent.getExtras().getString("updown");
		startStation = intent.getExtras().getString("bus_startstation");
		stopStation = intent.getExtras().getString("bus_stopstation");
		starttime = intent.getExtras().getString("bus_starttime");
		stoptime = intent.getExtras().getString("bus_stoptime");
		topBar.setTitle(route + "");
		busnum.setText(route);
		busstart.setText(startStation);
		busstop.setText(stopStation);
		busstarttime.setText(starttime);
		busstoptime.setText(stoptime);
		if(getextra()){
		getBusRealTime();
		gallery = (Gallery) findViewById(R.id.bus2_gallery);
		gallery.setAdapter(new ImageAdapter(this));
		if ((DensityUtil.px2dip(getApplicationContext(), width) - 60)/busstateStrings.length < 20) {
			linedpi=20;
		}
		else {
			linedpi=(DensityUtil.px2dip(getApplicationContext(), width) - 60)/busstateStrings.length;
		}
		if(linedpi==20){
			gallery.setSelection((DensityUtil.px2dip(getApplicationContext(), width) - 60)/20/2);
		}else {
			gallery.setSelection(busstateStrings.length/2);
		}
		if(real.equals("0")){
			refreshButton.setBackgroundResource(R.drawable.refresh_gray);
		}else {
		//	refreshButton.setOnTouchListener(new MyOnTouchListener());
			refreshButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
					rotateAnimation.reset();
				 	rotateAnimation.setDuration(1000);              //持续时间  
		            refreshButton.setAnimation(rotateAnimation);            //设置动画  
		            rotateAnimation.startNow(); 
		            refreshButton.invalidate();//启动动画  
		            getBusRealTime();

			}
		});
		}
		}
	}
	final Animation rotateAnimation = new       
		     RotateAnimation(360f,0f,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);  
		                                                        //设置旋转变化动画对象  
	/**
	 * @return 
	 * 
	 */
	private boolean getextra() {
		JSONObject jsonObject;
		try {
			jsonObject = JunctionHttp.getStatlist(Junction.hid, updown, route
					+ "");

			if (jsonObject.getInt("ret") == -7) {
				JunctionHttp.init();
				jsonObject = JunctionHttp.getStatlist(Junction.hid, updown,
						route + "");
			}
			JSONArray jsonArray = jsonObject.getJSONObject("data")
					.getJSONArray("list");
			current = jsonObject.getJSONObject("data").getInt("curSeq");
			
			busstateStrings = new String[jsonArray.length()];
			for (int i = 0; i < jsonArray.length(); i++) {
				busstateStrings[i] = jsonArray.getJSONObject(i).getString(
						"name");
			}
			extra = jsonObject.getJSONObject("data").getString("extra");
			buslocationString = jsonObject.getJSONObject("data").getString(
					"addr");
			buspoi = jsonObject.getJSONObject("data").getString("poiId");
			startStation = jsonObject.getJSONObject("data").getString("start");
			stopStation = jsonObject.getJSONObject("data").getString("end");
			starttime = jsonObject.getJSONObject("data").getString("first");
			stoptime = jsonObject.getJSONObject("data").getString("last");
			real = jsonObject.getJSONObject("data").getString("real");
			extratext.setText(extra);
			topBar.setTitle(route + "");
			busnum.setText(route);
			busstart.setText(startStation);
			busstop.setText(stopStation);
			busstarttime.setText(starttime);
			busstoptime.setText(stoptime);
			buslocation.setText(buslocationString);
			if(!buspoi.equals("")){
				bus2_locationLayout.setOnClickListener(new OnClickListener() {// 打开地图

					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent(Airport_bus.this,
								IndoorMap2.class);
						intent.putExtra("poiId", buspoi);
					
						intent.putExtra("toilet", " 1");
						
						
						startActivity(intent);

					}
				});
			}else {
				bus2_youjiantou.setBackgroundDrawable(null);
			}
			return true;
		} catch (Exception e) {
			bus2bodyLayout.removeAllViews();
			Toast.makeText(getApplicationContext(), R.string.neterror, Toast.LENGTH_LONG).show();
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * 获取实时数据
	 */
	private void getBusRealTime() {
		if (real.equals("1")) {
			try {

				JSONObject jsonObject_detail = JunctionHttp.getBusstatus(
						Junction.hid, updown, route + "");
				if (jsonObject_detail.getInt("ret") >= 0) {
					JSONObject busdataJsonObject = jsonObject_detail
							.getJSONObject("data");
					if (!busdataJsonObject.getString("bus1").equals("")) {
						bus_first.setText(busdataJsonObject.getString("bus1"));
					}
					if (!busdataJsonObject.getString("bus2").equals("")) {
						bus_second.setText(busdataJsonObject.getString("bus2"));
					}
					if (!busdataJsonObject.getString("bus3").equals("")) {
						bus_third.setText(busdataJsonObject.getString("bus3"));
					}
				} else if (jsonObject_detail.getInt("ret") == -7) {
					JunctionHttp.init();
					jsonObject_detail = JunctionHttp.getBusstatus(Junction.hid,
							updown, route + "");
					JSONObject busdataJsonObject = jsonObject_detail
							.getJSONObject("data");
					if (!busdataJsonObject.getString("bus1").equals("")) {
						bus_first.setText(busdataJsonObject.getString("bus1"));
					}
					if (!busdataJsonObject.getString("bus2").equals("")) {
						bus_second.setText(busdataJsonObject.getString("bus2"));
					}
					if (!busdataJsonObject.getString("bus3").equals("")) {
						bus_third.setText(busdataJsonObject.getString("bus3"));
					}
				}else {
					
						bus_first.setText("暂无数据");
						bus_second.setText("暂无数据");
						bus_third.setText("暂无数据");
					
				}
				// firstTextView.setText(jsonObject.getString(""));
				Log.v("...", jsonObject_detail.toString());
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), R.string.neterror, Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
		}
	}
	private int linedpi;
	public class ImageAdapter extends BaseAdapter {

		Context mContext; // 上下文对象
		

		// 构造方法
		public ImageAdapter(Context context) {
			this.mContext = context;
		}

		// 获取图片的个数
		public int getCount() {
			return busstateStrings.length;
		}

		// 获取图片在库中的位置
		public Object getItem(int position) {
			return busstateStrings[position];
		}

		// 获取图片在库中的位置
		public long getItemId(int position) {
			return position;
		}

		// 获取适配器中指定位置的视图对象
		@SuppressWarnings("deprecation")
		public View getView(int position, View convertView, ViewGroup parent) {
			
			LinearLayout linearLayout = new LinearLayout(Airport_bus.this);
			linearLayout.setOrientation(LinearLayout.VERTICAL);
			linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
			ImageView imageView = new ImageView(Airport_bus.this);
			imageView.setBackgroundResource(R.drawable.yuandian);
			imageView.setLayoutParams(new Gallery.LayoutParams(DensityUtil
					.dip2px(Airport_bus.this, linedpi), DensityUtil.dip2px(
					Airport_bus.this, 10)));
			linearLayout.addView(imageView);
			if (current == position + 1) {
				TextView textView = new TextView(Airport_bus.this);
				textView.setText(busstateStrings[position]);
				textView.setTextColor(Color.WHITE);
				textView.setLayoutParams(new Gallery.LayoutParams(DensityUtil
						.dip2px(Airport_bus.this, 20),
						DensityUtil
						.dip2px(Airport_bus.this, 140)));
				textView.setGravity(Gravity.CENTER_HORIZONTAL);
				textView.setBackgroundColor(Color.rgb(171, 228, 251));
				Drawable img_on, img_off;
				Resources res = getResources();
				img_off = res.getDrawable(R.drawable.sanjiao_red);
				//调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
				img_off.setBounds(0, 0, img_off.getMinimumWidth(), img_off.getMinimumHeight());
				textView.setCompoundDrawables(null, null, null,img_off ); //设置左图标
				linearLayout.addView(textView);
//				ImageView imageView_arr = new ImageView(Airport_bus.this);
//				imageView_arr.setBackgroundResource(R.drawable.sanjiao_red);
//				imageView_arr.setLayoutParams(new Gallery.LayoutParams(
//						DensityUtil.dip2px(Airport_bus.this, 20), DensityUtil
//								.dip2px(Airport_bus.this, 10)));
//				linearLayout.addView(imageView_arr);
			} else {
				TextView textView = new TextView(Airport_bus.this);
				textView.setText(busstateStrings[position]);
				textView.setTextColor(Color.WHITE);
				textView.setGravity(Gravity.CENTER_HORIZONTAL);
				textView.setLayoutParams(new Gallery.LayoutParams(DensityUtil
						.dip2px(Airport_bus.this, 20), DensityUtil
						.dip2px(Airport_bus.this, 150)));
				linearLayout.addView(textView);
			}

			return linearLayout;
		}


	}


	@Override
	public void leftBtnClick() {
		
		topBar.setLeftButton(R.drawable.fanhui_click);
		this.finish();
	}

	
	@Override
	public void rightBtnClick() {
		
	}

}
