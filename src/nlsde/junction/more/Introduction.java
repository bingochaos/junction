package nlsde.junction.more;

import nlsde.junction.R;
import nlsde.junction.topbar.TopBar;
import nlsde.junction.topbar.TopBarClickListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;

public class Introduction extends Activity implements TopBarClickListener{

	private TopBar topBar;
	private WebView webView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.introduction);
		topBar = (TopBar )findViewById(R.id.topbar_moreinfo_introdction);
		topBar.setLeftButton(R.drawable.fanhui);
		topBar.setTopBarClickListener(this);
		webView = (WebView)findViewById(R.id.introduction_webview);
		Intent intent =getIntent();
		if(intent.hasExtra("shuniuming")){
			switch (intent.getExtras().getInt("shuniuming")) {
			case 1:
				topBar.setTitle("东直门简介");
				webView.loadUrl("file:///android_asset/shuniujianjie/dzm.html");
				break;
			case 2:
				topBar.setTitle("四惠简介");
				webView.loadUrl("file:///android_asset/shuniujianjie/sh.html");
				break;
			default:
				break;
			}
		}
	}
	@Override
	public void leftBtnClick() {
		this.finish();
		topBar.setLeftButton(R.drawable.fanhui_click);
		
	}
	@Override
	public void rightBtnClick() {
		// TODO Auto-generated method stub
		
	}

}
