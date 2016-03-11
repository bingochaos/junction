package nlsde.junction.list;




import java.util.ArrayList;
import java.util.HashMap;

import nlsde.junction.R;
import nlsde.junction.home.Junction;
import nlsde.junction.home.function.CoachListActivity;
import nlsde.junction.net.JunctionHttp;
import nlsde.junction.searchbar.ListViewLoadMore;
import nlsde.junction.searchbar.ListViewLoadMore.IsLoadingListener;
import nlsde.junction.topbar.TopBar;
import nlsde.tools.AsyncImageLoader;
import nlsde.tools.BitmapDecode;
import nlsde.tools.DensityUtil;
import nlsde.tools.RefreshableView;
import nlsde.tools.RefreshableView.PullToRefreshListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.SimpleAdapter.ViewBinder;

public class Junction_List extends Activity implements OnPageChangeListener,IsLoadingListener{

	
	protected static final int SCROLL = 0;
	private static String TAG = Junction_List.class.getSimpleName();
	private TopBar topBar;
	private ArrayList<HashMap<String, Object>>   listItems;    //存放文字、图片信息
	private SimpleAdapter listItemAdapter;           //适配器    
	private RefreshableView refreshableView; 
	private Bitmap[] bitmaps;
	private ListViewLoadMore listView;
	private ImageAdapter imageadapter;
	private int[] itemids= new int[100];
	private ViewGroup group;
	private Handler handler = new Handler();
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
   // private int[] ids={R.drawable.dzm001,R.drawable.dzm002,R.drawable.dzm003};
    private View header ;
    private LayoutInflater inflater;
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
	Runnable bannerRunnable=new Runnable() {
		
		@Override
		public void run() {
			initBanner();
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.junction_list);
         
		inflater = LayoutInflater.from(getApplicationContext());
		header= inflater.inflate(R.layout.listviewheader, null);
       
        loader = new AsyncImageLoader(getApplicationContext());  
        //将图片缓存至外部文件中  
        loader.setCache2File(true); //false  
        //设置外部缓存文件夹  
        loader.setCachedDir(this.getCacheDir().getAbsolutePath());  
          
		initView();
		
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		initlist();
		super.onStart();
	}

	/**
	 * 初始化控件
	 */
	boolean mOnTouch;
	private int page=1;
	private void initView() {
		topBar = (TopBar)findViewById(R.id.topbar_list);
		listView =(ListViewLoadMore)findViewById(R.id.junction_list);
		//topBar.setLeftButton(R.drawable.fanhui);
		topBar.setTitle("枢纽圈");
		group = (ViewGroup)header.findViewById(R.id.juncation_list_dot);  
        viewPager = (ViewPager)header. findViewById(R.id.junction_list_img);  
        initBanner();
       

		refreshableView = (RefreshableView) findViewById(R.id.refreshable_junctions);
		refreshableView.setOnRefreshListener(new PullToRefreshListener() {
			@Override
			public void onRefresh() {
				try {
					page=1;
					handler.post(bannerRunnable);				
					initlist();
				
				} catch (Exception e) {
					e.printStackTrace();
				}
				refreshableView.finishRefreshing();
			}
		}, 2);
		listItems = new ArrayList<HashMap<String, Object>>();
//        for(int i=0;i<10;i++)    {   
//            HashMap<String, Object> map = new HashMap<String, Object>();   
//            map.put("ItemTitle", "item: "+i);    //文字
//            map.put("ItemImage", R.drawable.dongzhimen);   //图片   
//          //  map.put("itembutton", "1分钟");
//            map.put("ItemHead", "东直门");
//            listItems.add(map);   
//        }   
        //生成适配器的Item和动态数组对应的元素   
        listItemAdapter = new SimpleAdapter(this,listItems,   // listItems数据源    
                R.layout.junction_list_item,  //ListItem的XML布局实现   
                new String[] {"ItemTitle", "ItemImage","itembutton","ItemHead"},     //动态数组与ImageItem对应的子项          
                new int[ ] {R.id.ItemTitle, R.id.ItemImage,R.id.itembotton,R.id.Item_head}      //list_item.xml布局文件里面的一个ImageView的ID,一个TextView 的ID   
        );   
		listItemAdapter.setViewBinder(new ViewBinder() {

			@Override
			public boolean setViewValue(View view, Object data,
					String textRepresentation) {
				if (view instanceof ImageView && data instanceof Bitmap) {
					ImageView i = (ImageView) view;
					i.setImageBitmap((Bitmap) data);
					return true;
				}
				return false;
			}
		}
        );
		
		listView.addHeaderView(header);
		listView.setAdapter(listItemAdapter);
		listView.setOnLoadingListener(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Intent intent =new Intent(Junction_List.this,Ad.class);
				intent.putExtra("itemId", itemids[position-1]);
				startActivity(intent);
			}
		});
	}

	/**
	 * 设置banner图片
	 */
	private void initBanner() {
		viewPager.setAdapter(null);
		group.removeAllViews();
		imgIdArray = new ArrayList<Bitmap>();
		
        try {
			JSONObject jsonObject = JunctionHttp.getadBanner(Junction.hid);
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
						
								handler.post(new Runnable() {							
								@Override
								public void run() {
									//设置Adapter
						      		viewPager.setAdapter(imageadapter);
						      		//设置监听，主要是设置点点的背景
						      		viewPager.setOnPageChangeListener(Junction_List.this);
						      		//设置ViewPager的默认项, 设置为长度的100倍，这样子开始就能往左滑动
						      		viewPager.setCurrentItem((mImageViews.length) * 100);
									
								}
							});
							}
							
						
					});
		}
		//将点点加入到ViewGroup中
		tips = new ImageView[jsonArray.length()];
		for(int i=0; i<tips.length; i++){
			ImageView imageView = new ImageView(this);
	    	imageView.setLayoutParams(new LayoutParams(DensityUtil.dip2px(getApplicationContext(), 10),DensityUtil.dip2px(getApplicationContext(), 10)));
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
			tips = new ImageView[1];
			for(int i=0; i<tips.length; i++){
				ImageView imageView = new ImageView(this);
		    	imageView.setLayoutParams(new LayoutParams(DensityUtil.dip2px(getApplicationContext(), 10),DensityUtil.dip2px(getApplicationContext(), 10)));
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
			mImageViews = new ImageView[1];
			for(int i=0; i<mImageViews.length; i++){
				ImageView imageView = new ImageView(this);
				mImageViews[i] = imageView;
				//bitmaps[i] = BitmapDecode.readBitMap(getApplicationContext(), imgIdArray[i]);
				imageView.setImageBitmap(BitmapDecode.readBitMap(getApplicationContext(), R.drawable.default_banner));
			e.printStackTrace();
			}
		}
      		imageadapter=new ImageAdapter();
      		
      		
	}

	/**
	 * 
	 */
	private void initlist() {
		JSONObject jsonObject;
		try {
			jsonObject = JunctionHttp.getItemlist(Junction.hid,page);

			Log.v(TAG, jsonObject.toString());
			if (jsonObject.getInt("ret") == 0) {
				String img_urlString = jsonObject.getJSONObject("data")
						.getString("base");
				JSONArray jsonArray = jsonObject.getJSONObject("data")
						.getJSONArray("list");
				if(page==1)
				listItems.clear();
				for (int i = 0; i < jsonArray.length(); i++) {
					final HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("ItemTitle",
							jsonArray.getJSONObject(i).getString("title")); // 文字
					loader.downloadImage("http://" + img_urlString
							+ jsonArray.getJSONObject(i).getString("icon"),
							true, new AsyncImageLoader.ImageCallback() {
								@Override
								public void onImageLoaded(Bitmap bitmap,
										String imageUrl) {
									if (bitmap != null) {
										map.put("ItemImage", bitmap);
									} else {
										// 下载失败，设置默认图片
									}
								}
							});
					map.put("ItemHead",
							jsonArray.getJSONObject(i).getString("head"));
					//map.put("itembutton", "1分钟");
					itemids[i] = jsonArray.getJSONObject(i).getInt("itemId");
					listItems.add(map);

				}
			}else if(jsonObject.getInt("ret")==-3){
				Toast.makeText(getApplicationContext(), "没有更多结果了", Toast.LENGTH_SHORT).show();;

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

	@Override
	public void onLoad() {
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {

				try {
					
					initlist();
					page++;
				} catch (Exception e) {
					Toast.makeText(Junction_List.this, R.string.neterror, Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
				listItemAdapter.notifyDataSetChanged();
				listView.complateLoad();
			}
		}, 1000);
		
	}  
  

	}


