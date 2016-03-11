/**
 * SubWayActivity.java
 * 
 * @Description: 
 * 
 * @File: SubWayActivity.java
 * 
 * @Package nlsde.junction.home.function
 * 
 * @Author chaos
 * 
 * @Date 2014-11-28上午9:51:50
 * 
 * @Version V1.0
 */
package nlsde.junction.home.function;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import nlsde.junction.R;
import nlsde.junction.home.Junction;
import nlsde.junction.topbar.TopBar;
import nlsde.junction.topbar.TopBarClickListener;
import nlsde.tools.BitmapDecode;
import nlsde.tools.ScaleImageView;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * @author chaos
 *
 */
public class SubWayActivity extends Activity implements TopBarClickListener{

//	private ScaleImageView scaleImageView;
	private TopBar topBar;
	private Bitmap bm;
	private WebView webView;
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
		setContentView(R.layout.subway);
		topBar = (TopBar)findViewById(R.id.topbar_subway);
		topBar.setLeftButton(R.drawable.fanhui);
		topBar.setTopBarClickListener(this);
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		//scaleImageView= (ScaleImageView)findViewById(R.id.subwaymap);
//		switch (Junction.hid) {
//		case 1:
//			bm = BitmapDecode.readBitMap(getApplicationContext(), R.drawable.subwaydzm);
//			break;
//		case 2:
//			bm = BitmapDecode.readBitMap(getApplicationContext(), R.drawable.subwaysh);
//			break;
//		default:
//			break;
//		}
//		
//		BitmapDrawable bd = new BitmapDrawable(this.getResources(), bm);
//		scaleImageView.setImageDrawable(bd);
		webView=(WebView)findViewById(R.id.subwaymap);
		 webView.getSettings().setJavaScriptEnabled(true);  
		webView.loadUrl("file:///android_asset/ditie.html");///http://www.baidu.com
		webView.setWebViewClient(new WebViewClient(){

			@Override
			public void onPageFinished(WebView view, String url) {
			//	Toast.makeText(getApplicationContext(), R.string.neterror, Toast.LENGTH_SHORT).show();
				super.onPageFinished(view, url);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				Toast.makeText(getApplicationContext(), R.string.neterror, Toast.LENGTH_SHORT).show();
				super.onReceivedError(view, errorCode, description, failingUrl);
			}
			
		});
	}
	
	@Override
	public void leftBtnClick() {
		topBar.setLeftButton(R.drawable.fanhui_click);
		
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
		webView.destroy();
//		BitmapDrawable bd = (BitmapDrawable)scaleImageView.getDrawable();
//		scaleImageView.setBackgroundResource(0);//别忘了把背景设为null，避免onDraw刷新背景时候出现used a recycled bitmap错误
//		bd.setCallback(null);
//		bd.getBitmap().recycle();
		super.onDestroy();
	}

	
}
