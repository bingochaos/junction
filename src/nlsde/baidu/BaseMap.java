package nlsde.baidu;

import nlsde.junction.R;
import nlsde.junction.home.Junction;
import nlsde.junction.list.List_All;
import nlsde.junction.topbar.TopBar;
import nlsde.junction.topbar.TopBarClickListener;
import nlsde.tools.DensityUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;


/**
 * 演示MapView的基本用法
 */
public class BaseMap extends Activity implements TopBarClickListener {
	private static final String TAG = BaseMap.class.getSimpleName();
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private LocationClient locationClient;
	private BDLocation location=new BDLocation();
	private MyLocationListenner myLocationListenner=new MyLocationListenner();
	private LocationMode tempMode = LocationMode.Hight_Accuracy;
	private String tempcoor="gcj02";
	private TopBar topBar;
	private RelativeLayout mMarkerInfoLy;
	private static int hid;
	 @Override
	protected void onPause() {
		 MobclickAgent.onPause(this);
		 mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		MobclickAgent.onResume(this);
		mMapView.onResume();
		super.onResume();
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(android.R.style.Theme_Translucent_NoTitleBar);
		SDKInitializer.initialize(getApplicationContext());
		Intent intent = getIntent();
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		if (intent.hasExtra("x") && intent.hasExtra("y")) {
			// 当用intent参数时，设置中心点为指定点
			Bundle b = intent.getExtras();
			LatLng p = new LatLng(b.getDouble("y"), b.getDouble("x"));
			mMapView = new MapView(this,
					new BaiduMapOptions().mapStatus(new MapStatus.Builder()
							.target(p).build()));
		} else {
			mMapView = new MapView(BaseMap.this, new BaiduMapOptions());
		}
		// //百度定位代码
		// tempMode=LocationMode.Hight_Accuracy;
		// tempcoor="gcj02";
		// locationClient=new LocationClient(this);
		// initLocation();
		// locationClient.registerLocationListener(myLocationListenner);
		// locationClient.start();
		//
		setContentView(R.layout.home);
		mMapView = (MapView) findViewById(R.id.mapView);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		mMarkerInfoLy = (RelativeLayout) findViewById(R.id.id_marker_info);
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				// 获得marker中的数据
				MarkerInfo info = (MarkerInfo) marker.getExtraInfo()
						.get("info");
				hid = info.getHid();
				InfoWindow mInfoWindow;
				// 生成一个TextView用户在地图中显示InfoWindow
				TextView location = new TextView(getApplicationContext());
				location.setGravity(Gravity.CENTER);
				location.setBackgroundResource(R.drawable.location_tips);
				location.setPadding(30, 30, 30, 80);
				location.setText(info.getName());
				location.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						Junction.hid = hid;
						BaseMap.this.finish();
					}
				});
				// 将marker所在的经纬度的信息转化成屏幕上的坐标
				final LatLng ll = marker.getPosition();
				Point p = mBaiduMap.getProjection().toScreenLocation(ll);
				Log.e(TAG, "--!" + p.x + " , " + p.y);
				p.y -= 47;
				LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
				// 为弹出的InfoWindow添加点击事件
				mInfoWindow = new InfoWindow(location, llInfo, 0);
				mBaiduMap.showInfoWindow(mInfoWindow);
				// 设置详细信息布局为可见
				// mMarkerInfoLy.setVisibility(View.VISIBLE);
				// //根据商家信息为详细信息布局设置信息
				// popupInfo(mMarkerInfoLy, info);
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

		topBar = (TopBar) findViewById(R.id.topbar_basemap);
		topBar.setTitle("室外地图");
		topBar.setLeftButton(R.drawable.fanhui);
		topBar.setRightButton(R.drawable.anniu);
		topBar.setTopBarClickListener(this);

		try {
			if (intent.getExtras() != null) {
				JSONObject junctionlist_json = new JSONObject(
						intent.getStringExtra("junction"));
				JSONArray junctionlist_Array = junctionlist_json.getJSONObject(
						"data").getJSONArray("list");
				for (int i = 0; i < junctionlist_Array.length(); i++) {
					MarkerInfo markerInfo = new MarkerInfo();
					markerInfo.setLatitude(junctionlist_Array.getJSONObject(i)
							.getDouble("lat"));
					markerInfo.setLongitude(junctionlist_Array.getJSONObject(i)
							.getDouble("lon"));
					markerInfo.setName(junctionlist_Array.getJSONObject(i)
							.getString("name"));
					markerInfo.setHid(junctionlist_Array.getJSONObject(i)
							.getInt("hid"));
					drawMarker(mBaiduMap, markerInfo);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	protected void popupInfo(RelativeLayout mMarkerLy, MarkerInfo info)  
    {  
        ViewHolder viewHolder = null;  
        if (mMarkerLy.getTag() == null)  
        {  
            viewHolder = new ViewHolder();  
            viewHolder.infoImg = (ImageView) mMarkerLy  
                    .findViewById(R.id.info_img);  
            viewHolder.infoName = (TextView) mMarkerLy  
                    .findViewById(R.id.info_name);  
            viewHolder.infoDistance = (TextView) mMarkerLy  
                    .findViewById(R.id.info_distance);  
       
  
            mMarkerLy.setTag(viewHolder);  
        }  
        viewHolder = (ViewHolder) mMarkerLy.getTag();  
        viewHolder.infoImg.setImageResource(info.getImgId());  
        viewHolder.infoDistance.setText(info.getDistance());  
        viewHolder.infoName.setText(info.getName());  
        viewHolder.infoZan.setText(info.getHid() + "");  
    }  
	private void initLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(tempMode);//设置定位模式
		option.setCoorType(tempcoor);//返回的定位结果是百度经纬度，默认值gcj02
		int span=1000;
		try {
			span = Integer.valueOf(5000);
		} catch (Exception e) {
			// TODO: handle exception
		}
		option.setScanSpan(span);//设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);
		locationClient.setLocOption(option);
		
	}
	public class MyLocationListenner implements BDLocationListener {  
        @Override  
        //接收位置信息  
        public void onReceiveLocation(BDLocation location) {  
            if (location == null)  
                return ;  
            StringBuffer sb = new StringBuffer(256);  
            sb.append("time : ");  
            sb.append(location.getTime());  
            sb.append("\nreturn code : ");  
            sb.append(location.getLocType());  
            sb.append("\nlatitude : ");  
            sb.append(location.getLatitude());  
            sb.append("\nlontitude : ");  
            sb.append(location.getLongitude());  
            sb.append("\nradius : ");  
            sb.append(location.getRadius()); 
            if (location.getLocType() == BDLocation.TypeGpsLocation){  
                sb.append("\nspeed : ");  
                sb.append(location.getSpeed());  
                sb.append("\nsatellite : ");  
                sb.append(location.getSatelliteNumber());  
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){  
                /** 
                 * 格式化显示地址信息 
                 */  
                sb.append("\naddr : ");  
                sb.append(location.getAddrStr());  
            }  
            sb.append("\nsdk version : ");  
            sb.append(locationClient.getVersion());  
            sb.append("\nisCellChangeFlag : ");  
            sb.append(location.isCellChangeFlag());  
            
           // mTv.setText(sb.toString());  
            Log.i(TAG, sb.toString());  
        }  
	}

	


	@Override
	protected void onDestroy() {
		super.onDestroy();
		// activity 销毁时同时销毁地图控件
		mMapView.onDestroy();
//		locationClient.unRegisterLocationListener(myLocationListenner);
//		locationClient.stop();
	}
	private void drawMarker(BaiduMap baiduMap,MarkerInfo markerInfo){
		//定义Maker坐标点  
		LatLng point = new LatLng(markerInfo.getLatitude(), markerInfo.getLongitude());  
		//构建Marker图标  
		BitmapDescriptor bitmap = BitmapDescriptorFactory  
		    .fromResource(R.drawable.pin_blue);  
		//构建MarkerOption，用于在地图上添加Marker  
		OverlayOptions option = new MarkerOptions()  
		    .position(point)  
		    .icon(bitmap)
		    ; 
		Marker marker = (Marker) (baiduMap.addOverlay(option));  
        Bundle bundle = new Bundle();  
        bundle.putSerializable("info", markerInfo);  
        marker.setExtraInfo(bundle); 
		//在地图上添加Marker，并显示  
	}
	
	@SuppressWarnings("unused")
	private void drawText(BaiduMap baiduMap,BDLocation location ){
		//定义文字所显示的坐标点  
		LatLng llText = new LatLng(location.getAltitude(), location.getLongitude());  
		//构建文字Option对象，用于在地图上添加文字  
		OverlayOptions textOption = new TextOptions()  
		    .bgColor(0xAAFFFF00)  
		    .fontSize(24)  
		    .fontColor(0xFFFF00FF)  
		    .text("百度地图SDK")  
		    .rotate(-30)  
		    .position(llText);  
		//在地图上添加该文字对象并显示  
		baiduMap.addOverlay(textOption);
	}
	
	@Override
	public void leftBtnClick() {
		topBar.setLeftButton(R.drawable.fanhui_click);
		this.finish();
		
	}
	
	@Override
	public void rightBtnClick() {
		topBar.setRightButton(R.drawable.anniu_click);
		Intent intent = new Intent(BaseMap.this,List_All.class);
		startActivity(intent);
		this.finish();
		
	}
	
}
