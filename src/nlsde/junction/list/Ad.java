/**
 * Ad.java
 * 
 * @Description: 
 * 
 * @File: Ad.java
 * 
 * @Package nlsde.junction.list
 * 
 * @Author chaos
 * 
 * @Date 2014-12-3下午3:55:42
 * 
 * @Version V1.0
 */
package nlsde.junction.list;

import org.json.JSONObject;

import com.baidu.platform.comapi.map.j;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import nlsde.junction.R;
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
import android.text.method.ScrollingMovementMethod;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author chaos
 *
 */
public class Ad extends Activity implements TopBarClickListener{

	private Intent intent;
	private int itemid;
	private JSONObject jsonObject;
	private TextView title,body,time,pv;
	private ImageView imageView;
	private  AsyncImageLoader  loader; 
	private TopBar topBar;
	private Bitmap banner;
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
		setContentView(R.layout.ad);
		initview();
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		topBar.setLeftButton(R.drawable.fanhui);
		topBar.setTitle("枢纽圈");
		topBar.setTopBarClickListener(this);
		loader = new AsyncImageLoader(getApplicationContext());
		 //将图片缓存至外部文件中  
        loader.setCache2File(true); //false  
        //设置外部缓存文件夹  
        loader.setCachedDir(this.getCacheDir().getAbsolutePath());  
		intent=getIntent();
		itemid = intent.getExtras().getInt("itemId");
		try {
			jsonObject = JunctionHttp.getIteminfo(itemid);
			if(jsonObject.getInt("ret")==0)
			{
				title.setText(jsonObject.getJSONObject("data").getString("title"));
				body.setText(jsonObject.getJSONObject("data").getString("body"));
				body.setMovementMethod(ScrollingMovementMethod.getInstance()) ; 
				loader.downloadImage("http://"+jsonObject.getJSONObject("data").getString("pic"),  
						new AsyncImageLoader.ImageCallback() {  
                    @Override  
                    public void onImageLoaded(Bitmap bitmap, String imageUrl) {  
                        if(bitmap != null){  
                    
                    		BitmapDrawable bd = new BitmapDrawable(Ad.this.getResources(), bitmap);
                    		imageView.setBackgroundDrawable(bd);
                          //  imageView.setImageBitmap(bitmap);
                        }else{  
                            //下载失败，设置默认图片  
                        }  
                    }
				});
				time.setText("发布时间："+jsonObject.getJSONObject("data").getString("pubTime"));
				pv.setText("浏览量  "+jsonObject.getJSONObject("data").getString("pv"));
				
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	/**
	 * 
	 */
	private void initview() {
		topBar = (TopBar)findViewById(R.id.ad_topbar);
		title = (TextView)findViewById(R.id.ad_title);
		body = (TextView)findViewById(R.id.ad_body);
		time = (TextView)findViewById(R.id.time);
		pv = (TextView)findViewById(R.id.pv);
		imageView = (ImageView)findViewById(R.id.ad_img);
		
	}

	@Override
	public void leftBtnClick() {
		this.finish();
		
	}
	
	@Override
	public void rightBtnClick() {
		// TODO Auto-generated method stub
		
	}

}
