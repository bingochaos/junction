/**
 * ShopDetailActivity.java
 * 
 * @Description: 
 * 
 * @File: ShopDetailActivity.java
 * 
 * @Package nlsde.junction.home.indoormap
 * 
 * @Author chaos
 * 
 * @Date 2014-12-22下午3:07:43
 * 
 * @Version V1.0
 */
package nlsde.junction.home.indoormap;

import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import nlsde.junction.R;
import nlsde.junction.home.function.IndoorMap2;
import nlsde.junction.net.JunctionHttp;
import nlsde.junction.topbar.TopBar;
import nlsde.junction.topbar.TopBarClickListener;
import nlsde.tools.AsyncImageLoader;
import nlsde.tools.BitmapDecode;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author chaos
 *
 */
public class ShopDetailActivity extends Activity implements TopBarClickListener{

	private AsyncImageLoader loader;
	private Intent intent;
	private TopBar topBar;
	private ImageView shop_back,shop_logo;
	private TextView shop_name,shop_time,shop_location,shop_phone,shop_detail,shop_surrond,shop_special;
	private	String poiId,floorId;
	private int shopid;
	private Bitmap bitmapback;
	private RelativeLayout shoplocationLayout;
	private LinearLayout shop_bodyLayout;
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
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.shop);
		loader = new AsyncImageLoader(getApplicationContext());
		// 将图片缓存至外部文件中
		loader.setCache2File(true); // false
		// 设置外部缓存文件夹
		loader.setCachedDir(this.getCacheDir().getAbsolutePath());
		intent= getIntent();
		poiId = intent .getExtras().getString("poiId");
		shopid = intent.getExtras().getInt("shopId");
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		initView();
		getdetail(shopid);
	}
	/**
	 * @param shopid2
	 */
	private void getdetail(int shopid2) {
		try {
			JSONObject jsonObject = JunctionHttp.getShopInfo(shopid2);
			loader.downloadImage("http://" + jsonObject.getJSONObject("data")
					.getString("logo"),
					true, new AsyncImageLoader.ImageCallback() {
						@Override
						public void onImageLoaded(Bitmap bitmap,
								String imageUrl) {
							if (bitmap != null) {
								shop_logo.setImageBitmap(bitmap);
							} else {
								// 下载失败，设置默认图片
							}
						}
					});
			shop_name.setText(jsonObject.getJSONObject("data").getString("name"));
			shop_time.setText(jsonObject.getJSONObject("data").getString("info"));
			shop_location.setText(jsonObject.getJSONObject("data").getString("addr"));
			shop_phone.setText(jsonObject.getJSONObject("data").getString("tele"));
			shop_detail.setText(jsonObject.getJSONObject("data").getString("intro"));
		} catch (Exception e) {
			shop_bodyLayout.removeAllViews();
			Toast.makeText(getApplicationContext(), R.string.neterror, Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		
	}
	/**
	 * 
	 */
	private void initView() {
		topBar = (TopBar)findViewById(R.id.topbar_shop);
		topBar.setLeftButton(R.drawable.fanhui);
		topBar.setTopBarClickListener(this);
		shop_back = (ImageView)findViewById(R.id.shop_back);
		shop_logo = (ImageView )findViewById(R.id.shop_logo);
		shop_name = (TextView)findViewById(R.id.shop_name);
		shop_time = (TextView)findViewById(R.id.shop_time);
		shop_location= (TextView)findViewById(R.id.shop_locationdetail);
		shop_phone = (TextView)findViewById(R.id.shop_phonenumber);
		shop_detail = (TextView)findViewById(R.id.shop_detail);
//		shop_surrond = (TextView)findViewById(R.id.shop_phoneme);
//		shop_special = (TextView)findViewById(R.id.shop_special);
		bitmapback=BitmapDecode.readBitMap(getApplicationContext(), R.drawable.tu);
		shop_back.setBackgroundDrawable(new BitmapDrawable(bitmapback));
		shop_bodyLayout = (LinearLayout)findViewById(R.id.shop_body);
		shoplocationLayout = (RelativeLayout)findViewById(R.id.shop_location);
		
		shoplocationLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ShopDetailActivity.this,IndoorMap2.class);
				intent.putExtra("poiId", poiId);
				startActivity(intent);
				ShopDetailActivity.this.finish();
				
			}
		});
	}
	
	@Override
	public void leftBtnClick() {
		this.finish();
		
		
	}

	@Override
	public void rightBtnClick() {
		// TODO Auto-generated method stub
		
	}
	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		if(bitmapback!=null){
			bitmapback.recycle();
		}
		super.onDestroy();
	}

}
