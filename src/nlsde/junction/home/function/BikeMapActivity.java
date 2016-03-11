/**
 * BikeMapActivity.java
 * 
 * @Description: 
 * 
 * @File: BikeMapActivity.java
 * 
 * @Package nlsde.junction.home.function
 * 
 * @Author chaos
 * 
 * @Date 2014-12-4下午1:25:34
 * 
 * @Version V1.0
 */
package nlsde.junction.home.function;

import nlsde.junction.R;
import nlsde.junction.net.JunctionHttp;
import nlsde.junction.topbar.TopBar;
import nlsde.junction.topbar.TopBarClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Projection;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

/**
 * @author chaos
 * 
 */
public class BikeMapActivity extends Activity implements TopBarClickListener,
		OnClickListener {

	private static String TAG = BikeMapActivity.class.getSimpleName();
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private LinearLayout mMarkerInfoLy;
	private TopBar topBar;
	private Intent intent;
	private JSONObject junctionlist_json;
	private Projection projection;
	private ProgressDialog dialog; 
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
		setTheme(android.R.style.Theme_Translucent_NoTitleBar);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.bikemap);
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		mMapView = (MapView) findViewById(R.id.mapView_bike);
		mBaiduMap = mMapView.getMap();
		try {
			intent = getIntent();
			junctionlist_json = new JSONObject(intent.getStringExtra("bike"));
			LatLng p;
			p = new LatLng(junctionlist_json.getJSONArray("list")
					.getJSONObject(0).getDouble("lat"), junctionlist_json
					.getJSONArray("list").getJSONObject(0).getDouble("lon"));
			// mMapView = new MapView(BikeMapActivity.this,
			// new BaiduMapOptions().mapStatus(new MapStatus.Builder()
			// .target(p).build()));
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
			MapStatus mMapStatus = new MapStatus.Builder().target(p).zoom(16)
					.build();
			// 定义MapStatusUpdate对象，以便描述地图状态将要发生的变化

			MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory
					.newMapStatus(mMapStatus);
			mBaiduMap.setMapStatus(mMapStatusUpdate);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		intent = getIntent();
		mMarkerInfoLy = (LinearLayout) findViewById(R.id.bike_marker_info);
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				// 获得marker中的数据
				BikeMarkerInfo info = (BikeMarkerInfo) marker.getExtraInfo()
						.get("info");

				try {
					JSONObject jsonObject = JunctionHttp.getBikeStatus(info
							.getStateCode());
					if (jsonObject.getInt("ret") == 0) {
						info.setBike_total(jsonObject.getJSONObject("data")
								.getInt("totalNum"));
						info.setBike_residual(jsonObject.getJSONObject("data")
								.getInt("lockedNum"));
					} else if (jsonObject.getInt("ret") == -7) {
						JunctionHttp.init();
						jsonObject = JunctionHttp.getBikeStatus(info
								.getStateCode());
						info.setBike_total(jsonObject.getJSONObject("data")
								.getInt("totalNum"));
						info.setBike_residual(jsonObject.getJSONObject("data")
								.getInt("lockedNum"));
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				InfoWindow mInfoWindow;
				// 生成一个TextView用户在地图中显示InfoWindow
				TextView location = new TextView(getApplicationContext());
				location.setGravity(Gravity.CENTER_HORIZONTAL);
				location.setTextSize(10);
				location.setTextColor(Color.WHITE);

				location.setBackgroundResource(R.drawable.pin_blue);
				location.setPadding(0, 0, 0, 0);
				location.setText(info.getNumber() + "");
				// 将marker所在的经纬度的信息转化成屏幕上的坐标
				final LatLng ll = marker.getPosition();

				Point p = mBaiduMap.getProjection().toScreenLocation(ll);
				Log.e(TAG, "--!" + p.x + " , " + p.y);

				LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
				// 为弹出的InfoWindow添加点击事件
				mInfoWindow = new InfoWindow(location, llInfo, 0);

				mBaiduMap.showInfoWindow(mInfoWindow);
				// 设置详细信息布局为可见
				mMarkerInfoLy.setVisibility(View.VISIBLE);
				// 根据商家信息为详细信息布局设置信息
				popupInfo(mMarkerInfoLy, info);
				return true;
			}
		});

		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				return false;
			}

			@Override
			public void onMapClick(LatLng arg0) {
				mMarkerInfoLy.setVisibility(View.GONE);
				mBaiduMap.hideInfoWindow();

			}
		});

		topBar = (TopBar) findViewById(R.id.topbar_bikemap);
		topBar.setTitle("自行车");
		topBar.setLeftButton(R.drawable.fanhui);
		topBar.setTopBarClickListener(this);

		try {

			JSONArray junctionlist_Array = junctionlist_json
					.getJSONArray("list");
			for (int i = 0; i < junctionlist_Array.length(); i++) {
				BikeMarkerInfo markerInfo = new BikeMarkerInfo();
				markerInfo.setLatitude(junctionlist_Array.getJSONObject(i)
						.getDouble("lat"));
				markerInfo.setLongitude(junctionlist_Array.getJSONObject(i)
						.getDouble("lon"));
				markerInfo.setBike_net_location(junctionlist_Array
						.getJSONObject(i).getString("name"));
				markerInfo.setBike_net_info(junctionlist_Array.getJSONObject(i)
						.getString("address"));
				markerInfo.setStateCode(junctionlist_Array.getJSONObject(i)
						.getString("statCode"));
				markerInfo.setNumber(i + 1);
				//drawText(mBaiduMap, markerInfo);
				drawMarker(mBaiduMap, markerInfo);

			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void drawText(BaiduMap baiduMap, BikeMarkerInfo location) {
		// 定义文字所显示的坐标点
		LatLng llText = new LatLng(location.getLatitude(),
				location.getLongitude());
		// 构建文字Option对象，用于在地图上添加文字
		OverlayOptions textOption = new TextOptions().fontSize(24)
				.fontColor(Color.WHITE).text(location.getNumber() + "")
				.position(llText);
		// 在地图上添加该文字对象并显示
		baiduMap.addOverlay(textOption);
	}

	private void drawMarker(BaiduMap baiduMap, BikeMarkerInfo markerInfo) {
		// 定义Maker坐标点
		LatLng point = new LatLng(markerInfo.getLatitude(),
				markerInfo.getLongitude());
		// 生成一个TextView用户在地图中显示InfoWindow
		TextView location = new TextView(getApplicationContext());
		location.setGravity(Gravity.CENTER_HORIZONTAL);
		location.setTextSize(10);
		location.setTextColor(Color.WHITE);

		location.setBackgroundResource(R.drawable.pin_red);
		location.setPadding(0, 0, 0, 0);
		location.setText(markerInfo.getNumber() + "");
		location.setDrawingCacheEnabled(true);
		location.measure(
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		location.layout(0, 0, location.getMeasuredWidth(),
				location.getMeasuredHeight());
		// 构建Marker图标
		BitmapDescriptor bitmap = BitmapDescriptorFactory.fromBitmap(location
				.getDrawingCache());

		// 构建MarkerOption，用于在地图上添加Marker
		OverlayOptions option = new MarkerOptions().position(point)
				.icon(bitmap).title(markerInfo.getNumber() + "");

		Marker marker = (Marker) (baiduMap.addOverlay(option));
		Bundle bundle = new Bundle();
		bundle.putSerializable("info", markerInfo);
		marker.setExtraInfo(bundle);
		// 在地图上添加Marker，并显示
	}

	protected void popupInfo(LinearLayout mMarkerLy, BikeMarkerInfo info) {
		Bike_ViewHolder viewHolder = null;
		if (mMarkerLy.getTag() == null) {
			viewHolder = new Bike_ViewHolder();
			viewHolder.bike_Net_LocationtTextView = (TextView) findViewById(R.id.bike_net);
			viewHolder.bike_totalTextView = (TextView) findViewById(R.id.bike_total);
			viewHolder.bike_residualTextView = (TextView) findViewById(R.id.bike_residual);
			viewHolder.bike_net_infoTextView = (TextView) findViewById(R.id.bike_net_info);
			viewHolder.bike_refreshButton = (Button) findViewById(R.id.bike_update);
			viewHolder.bike_cancelButton = (Button) findViewById(R.id.bike_update_cancel);

			mMarkerLy.setTag(viewHolder);
		}
		viewHolder = (Bike_ViewHolder) mMarkerLy.getTag();
		viewHolder.bike_Net_LocationtTextView.setText(info
				.getBike_net_location());
		viewHolder.bike_totalTextView.setText(info.getBike_total() + "");
		viewHolder.bike_residualTextView.setText(info.getBike_residual() + "");
		viewHolder.bike_net_infoTextView.setText(info.getBike_net_info());
		viewHolder.bike_refreshButton.setOnClickListener(this);
		viewHolder.bike_cancelButton.setOnClickListener(this);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nlsde.junction.topbar.TopBarClickListener#leftBtnClick()
	 */
	@Override
	public void leftBtnClick() {
		this.finish();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nlsde.junction.topbar.TopBarClickListener#rightBtnClick()
	 */
	@Override
	public void rightBtnClick() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.bike_update_cancel:
			mMarkerInfoLy.setVisibility(View.GONE);
			break;
		case R.id.bike_update:
			 dialog = ProgressDialog.show(this, null, "正在加载，请稍候...", true, false); 
	         //启动一个处理loading业务的线程  
			 new LoadingThread(this).start();  
			 break;
		default:
			break;
		}

	}
	private class LoadingThread extends Thread {
		private BikeMapActivity activity;

		public LoadingThread(BikeMapActivity act) {
			activity = act;
		}

		public void run() {
			
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			activity.mHandler.sendEmptyMessage(0);
		}
	}
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (dialog.isShowing())
				dialog.dismiss();
		}
	};
}
