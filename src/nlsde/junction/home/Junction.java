package nlsde.junction.home;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import nlsde.junction.R;
import nlsde.junction.animation.AnimActivity;
import nlsde.junction.home.function.AirportTrackActivity;
import nlsde.junction.home.function.BikeMapActivity;
import nlsde.junction.home.function.CoachSearchActivity;
import nlsde.junction.home.function.IndoorMap2;
import nlsde.junction.home.function.SubWayActivity;
import nlsde.junction.home.indoormap.SearchActivity;
import nlsde.junction.list.List_All;
import nlsde.junction.net.JunctionHttp;
import nlsde.junction.searchbar.BusAirport;
import nlsde.junction.searchbar.BusSearch;
import nlsde.junction.topbar.TopBar;
import nlsde.junction.topbar.TopBarClickListener;
import nlsde.tools.AsyncImageLoader;
import nlsde.tools.BitmapDecode;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.LayoutParams;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

/**
 * Junction.java
 * 
 * @Description: 主页
 * 
 * @File: Junction.java
 * 
 * @Package nlsde.junction.list
 * 
 * @Author chaos
 * 
 * @Date 2014年9月23日下午5:26:07
 * 
 * @Version V1.0
 */
public class Junction extends Activity implements TopBarClickListener ,OnPageChangeListener {

	TopBar topBar;
	private static final String TAG = Junction.class.getSimpleName();
	//枢纽编号
	public static int hid=1;
	private int junctionhid=0;
	private Bitmap[] bitmaps = new Bitmap[DATA.length];
	private ViewGroup group;
	//网络图片地址  
    private String imgUrl = "http://"; 
	private int i;

	/** 
     * ViewPager 
     */  
    private ViewPager viewPager;  
      
    /** 
     * 装点点的ImageView数组 
     */  
    private ImageView[] tips;  
      
    /** 
     * 装ImageView数组 
     */  
    private ImageView[] mImageViews;  
      
    /** 
     * 图片资源id 
     */  
    private ArrayList<Bitmap> imgIdArray ;  
	//for test  
    private  AsyncImageLoader  loader; 

	private StaggeredGridView gridView;
    private View header ;
    private LayoutInflater inflater;
    private MyAdapter myAdapter;
    private ImageAdapter imageAdapter;
    private Handler handler= new Handler();
    private Runnable runnable= new Runnable() {
		
		@Override
		public void run() {
			changeJunction(Junction.hid);
			
		}
	};
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
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		inflater = LayoutInflater.from(getApplicationContext());
		header= inflater.inflate(R.layout.listviewheader, null);
		setTheme(android.R.style.Theme_Translucent_NoTitleBar);
		setContentView(R.layout.junctions);
		 loader = new AsyncImageLoader(getApplicationContext());  
	        //将图片缓存至外部文件中  
	        loader.setCache2File(true); //false  
	        //设置外部缓存文件夹  
	        loader.setCachedDir(this.getCacheDir().getAbsolutePath());  
	     // 读取SharedPreferences中需要的数据
			SharedPreferences preferences = getSharedPreferences(
					"hid", MODE_WORLD_READABLE);

			hid = preferences.getInt("hid", 1); 
		// 初始化
		initeViews();

	}

	/**
	 * 初始化
	 */
	private void initeViews() {

		group = (ViewGroup)header.findViewById(R.id.juncation_list_dot);  
        viewPager = (ViewPager)header. findViewById(R.id.junction_list_img);  

		topBar = (TopBar) findViewById(R.id.topbar_junctions);
		topBar.setTopBarClickListener(Junction.this);
		topBar.setLeftButton(R.drawable.anniu);
		topBar.setRightButton(R.drawable.dingweiditu);
		
		
		gridView = (StaggeredGridView) findViewById(R.id.grid_view);
		width = (this.getWindowManager().getDefaultDisplay().getWidth() - 10 * 4) / 2;
		// Log.v(TAG,this.getWindowManager().getDefaultDisplay().getWidth()-8*4+""
		// );
		gridView.addHeaderView(header);
		myAdapter= new MyAdapter();
		imageAdapter =new ImageAdapter();
		gridView.setAdapter(myAdapter);
		gridView.setOnScrollListener(new OnScrollListener(){

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}
			
		});
		runOnUiThread(runnable);
		handler.post(runnable);
	}

	/**
	 * 
	 */
	private void initBanner() {
		imgIdArray = new ArrayList<Bitmap>();
		
        try {
			JSONObject jsonObject = JunctionHttp.getmainBanner(Junction.hid);
			Log.v(TAG, jsonObject.toString());	
		imgUrl= "http://"+jsonObject.getJSONObject("data").getString("base");
		JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("list");
		Log.v(TAG, jsonArray.toString());
		mImageViews = new ImageView[jsonArray.length()];
		//bitmaps = new Bitmap[imgIdArray.length];
		//将图片装载到数组中
		
		for(int i=0; i<mImageViews.length; i++){
			ImageView imageView = new ImageView(this);
			mImageViews[i] = imageView;
			//bitmaps[i] = BitmapDecode.readBitMap(getApplicationContext(), imgIdArray[i]);
			Log.v(TAG, "设置图片");
			//imageView.setImageBitmap(imgIdArray.get(0));
		}
		for ( i= 0; i < jsonArray.length(); i++) {
			loader.downloadImage(imgUrl
					+ jsonArray.getJSONObject(i).getString("pic"),
					true, new AsyncImageLoader.ImageCallback() {
						@Override
						public void onImageLoaded(Bitmap bitmap,
								String imageUrl) {
							if (bitmap != null) {
								mImageViews[imgIdArray.size()].setImageBitmap(bitmap);
								imgIdArray.add(bitmap);
							} else {
								mImageViews[imgIdArray.size()].setImageBitmap(BitmapDecode.readBitMap(getApplicationContext(), R.drawable.default_banner));
								imgIdArray.add(bitmap);
							}
							imageAdapter.notifyDataSetChanged();
						}
					});
		}
		
		//将点点加入到ViewGroup中
		group.removeAllViews();
		tips = new ImageView[jsonArray.length()];
		for(int i=0; i<tips.length; i++){
			ImageView imageView = new ImageView(this);
	    	imageView.setLayoutParams(new LayoutParams(30,30));
	    	imageView.setPadding(10, 10, 10, 10);
	    	tips[i] = imageView;
	    	if(i == 0){
	    		tips[i].setBackgroundResource(R.drawable.dot_selected);
	    	}else{
	    		tips[i].setBackgroundResource(R.drawable.dot_none);
	    	}
	    	
	    	 group.addView(imageView);
		}
		
	
			jsonObject.toString();
		}  catch (Exception e) {
			//将点点加入到ViewGroup中
			tips = new ImageView[3];
			for(int i=0; i<tips.length; i++){
				ImageView imageView = new ImageView(this);
		    	imageView.setLayoutParams(new LayoutParams(10,10));
		    	tips[i] = imageView;
		    	if(i == 0){
		    		tips[i].setBackgroundResource(R.drawable.dot_selected);
		    	}else{
		    		tips[i].setBackgroundResource(R.drawable.dot_none);
		    	}
		    	
		    	 group.addView(imageView);
			}
			
			//bitmaps = new Bitmap[imgIdArray.length];
			//将图片装载到数组中
			mImageViews = new ImageView[3];
			for(int i=0; i<mImageViews.length; i++){
				ImageView imageView = new ImageView(this);
				mImageViews[i] = imageView;
				//bitmaps[i] = BitmapDecode.readBitMap(getApplicationContext(), imgIdArray[i]);
				imageView.setImageBitmap(BitmapDecode.readBitMap(getApplicationContext(), R.drawable.default_banner));
			e.printStackTrace();
			}
		}
        
    
    		

    		//设置监听，主要是设置点点的背景
    		viewPager.setOnPageChangeListener(this);
    		//设置ViewPager的默认项, 设置为长度的100倍，这样子开始就能往左滑动
    		viewPager.setCurrentItem((mImageViews.length) * 100);
	}
	          
		

	public int width;

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return DATA.length;
		}

		@Override
		public Object getItem(int position) {
			return DATA[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = new ImageView(Junction.this);
				LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);
				convertView.setLayoutParams(lp);
			}

			ImageView imageView = (ImageView) convertView;
			imageView.setId(position);
			bitmaps[position]=BitmapDecode.readBitMap(getApplicationContext(), DATA[position]);
			imageView.setImageBitmap(bitmaps[position]);
			LayoutParams lp = (LayoutParams) imageView.getLayoutParams();

			lp.height = (int) (height[position] * width / 284);
			imageView.setLayoutParams(lp);
			imageView.setOnClickListener(new ItemClickListener());
			// imageView.getId();
			// Log.v(TAG,
			// imageView.getWidth()+" "+imageView.getHeight()+" "+lp.width+" "+lp.height);
			// TextView view = (TextView) convertView;
			// view.setText(DATA[position]);
			// view.setBackgroundColor(COLOR[position % 5]);
			// view.setGravity(Gravity.BOTTOM);
			// view.setTextColor(Color.WHITE);
			// LayoutParams lp = (LayoutParams) view.getLayoutParams();
			// lp.height = (int) (getPositionRatio(position) * 200);
			// view.setLayoutParams(lp);
			return imageView;
		}

	}

	private static final int mapl_h = 239;
	private static final int subway_h = 170;
	private static final int keliuliang_h = 268;
	private static final int bus_h = 146;
	private static final int taxi_h = 204;
	private static final int toilet_h = 150;
	private static final int changtuqiche_h = 208;
	private static final int jichangjiaotong_h = 268;
	private static final int zixingche_h = 205;

	private static int[] DATA = new int[] { R.drawable.shineidaohang,
			R.drawable.subway,R.drawable.jichangjiaotong_red, R.drawable.bus, 
			R.drawable.zixingche, R.drawable.toilet

	};
	private static int[] height = new int[] { mapl_h,subway_h, jichangjiaotong_h, bus_h,zixingche_h,
			  toilet_h };
	private static  int[] DATA_DZM = new int[] { R.drawable.shineidaohang,
		R.drawable.subway,R.drawable.jichangjiaotong_red, R.drawable.bus, 
		R.drawable.zixingche, R.drawable.toilet

	};
	private static  int[] height_DZM = new int[] { mapl_h,subway_h, jichangjiaotong_h, bus_h,zixingche_h,
		  toilet_h };

	private static  int[] DATA_SH = new int[] { R.drawable.shineidaohang,
		R.drawable.subway,R.drawable.jichangjiaotong, R.drawable.bus, 
		R.drawable.changtuqiche, R.drawable.toilet

	};
	private static  int[] height_SH = new int[] { mapl_h,subway_h, jichangjiaotong_h, bus_h,changtuqiche_h,
		  toilet_h };
	// 滑动图片数据适配器
	

	// 功能列表监听事件
	public class ItemClickListener implements OnClickListener {

		@Override
		public void onClick(View arg0) {
			Log.v(TAG, arg0.getId() + "");
			switch (arg0.getId()) {
			case 0:
				Intent intent = new Intent(Junction.this, IndoorMap2.class);
				startActivity(intent);
				break;
			case 1:
				Intent intent_sub = new Intent(Junction.this,
						SubWayActivity.class);
				startActivity(intent_sub);
				break;
			case 2:
				if(Junction.hid==1){
					Intent intent_airport = new Intent(Junction.this,
						AirportTrackActivity.class);
				startActivity(intent_airport);	
				}else if(Junction.hid==2){
					Intent intent_bus = new Intent(Junction.this, BusAirport.class);
					startActivity(intent_bus);
				}
			
				break;
			case 3:
				Intent intent_bus = new Intent(Junction.this, BusSearch.class);
				startActivity(intent_bus);
				
					
		
				break;
				
			case 4:
				if(hid==1){
				Intent intent_bike = new Intent(Junction.this,BikeMapActivity.class);
				JSONObject jsonObject_bike;
				try {
					jsonObject_bike = JunctionHttp.getBikelist(hid);
					if (jsonObject_bike.getInt("ret")==0) {
					intent_bike.putExtra("bike", jsonObject_bike.getJSONObject("data").toString());
				}
				
				startActivity(intent_bike);
				} catch (Exception e) {
					Toast.makeText(Junction.this, R.string.neterror, Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}}
				else if(hid==2) {
					Intent intent_coach = new Intent(Junction.this,CoachSearchActivity.class);
					startActivity(intent_coach);
				}
				
				break;
			case 5:
				Intent intent_indoormap= new Intent(Junction.this, IndoorMap2.class);
				startActivity(intent_indoormap);
				Intent intent_toilet =new Intent(Junction.this,SearchActivity.class);
				intent_toilet.putExtra("type", 1);
				startActivity(intent_toilet);
				
				break;
			case 6:
				
				break;

			default:
				break;
			}

		}

	}

	@Override
	public void leftBtnClick() {
		Intent intent = new Intent(Junction.this, List_All.class);
		startActivity(intent);

	}


	
	@Override
	protected void onDestroy() {
		if(bitmaps.length>0){
			for(Bitmap bitmap:bitmaps){
				bitmap.recycle();
			}
		}
		super.onDestroy();
	}

	@Override
	protected void onRestart() {

		handler.post(runnable);
		super.onRestart();
	}

	@Override
	public void rightBtnClick() {// 定位当前枢纽站
		Intent intent = new Intent(Junction.this, AnimActivity.class);
		startActivity(intent);
		

	}
	public void changeJunction(int hid) {
		if(junctionhid!=hid){
			junctionhid=hid;
		if(hid==1){
			topBar.setTitle("东直门");
			DATA=DATA_DZM;
			height=height_DZM;		
		}else if(hid==2){
			topBar.setTitle("四惠");
			DATA=DATA_SH;
			height=height_SH;
		}else {
			topBar.setTitle("宋家庄");
		}
		initBanner();
		// 读取SharedPreferences中需要的数据
		SharedPreferences preferences = getSharedPreferences(
				"hid", MODE_WORLD_READABLE);
		// 实例化Editor对象
		Editor editor = preferences.edit();
		// 存入数据
		editor.putInt("hid", hid);
		// 提交修改
		editor.commit();
		//设置Adapter
		viewPager.setAdapter(imageAdapter);
		gridView.setAdapter(myAdapter);
		imageAdapter.notifyDataSetChanged();
		myAdapter.notifyDataSetChanged();
		}
	}
	/**
	 * @author Administrator
	 *
	 */
	public class ImageAdapter extends PagerAdapter {

		 @Override  
	        public int getCount() {  
	            return Integer.MAX_VALUE;  
	        }  
	  
	        @Override  
	        public boolean isViewFromObject(View arg0, Object arg1) {  
	            return arg0 == arg1;  
	        }  
	  
	        @Override  
	        public void destroyItem(View container, int position, Object object) {  
	          //  ((ViewPager)container).removeView(mImageViews[position % mImageViews.length]);  
	              
	        }  
	  
	        /** 
	         * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键 
	         */  
	        @Override  
	        public Object instantiateItem(View container, int position) {  
	        	 try {    
	                 ((ViewPager)container).addView(mImageViews[position % mImageViews.length], 0);  
	             }catch(Exception e){  
	                 //handler something  
	             }    
	            return mImageViews[position % mImageViews.length];  
	        }  
	          
	          
		}
	@Override  
    public void onPageScrollStateChanged(int arg0) {  
          
    }  
  
    @Override  
    public void onPageScrolled(int arg0, float arg1, int arg2) {  
          
    }  
  
    @Override  
    public void onPageSelected(int arg0) {  
        setImageBackground(arg0 % mImageViews.length);  
    }  
      
    /** 
     * 设置选中的tip的背景 
     * @param selectItems 
     */  
    private void setImageBackground(int selectItems){  
        for(int i=0; i<tips.length; i++){  
            if(i == selectItems){  
                tips[i].setBackgroundResource(R.drawable.dot_selected);  
            }else{  
                tips[i].setBackgroundResource(R.drawable.dot_none);  
            }  
        }  
    }  
	
}
