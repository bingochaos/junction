/**
 * AboutActivity.java
 * 
 * @Description: 
 * 
 * @File: AboutActivity.java
 * 
 * @Package nlsde.junction.more
 * 
 * @Author chaos
 * 
 * @Date 2015-1-27下午5:45:36
 * 
 * @Version V1.0
 */
package nlsde.junction.more;


import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import nlsde.junction.R;
import nlsde.junction.topbar.TopBar;
import nlsde.junction.topbar.TopBarClickListener;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

/**
 * @author chaos
 *
 */
public class AboutActivity extends Activity implements TopBarClickListener{

	private TopBar topBar;
	private TextView app_versionTextView;
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
		setContentView(R.layout.guanyu);
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		topBar = (TopBar)findViewById(R.id.topbar_about);
		topBar.setTopBarClickListener(this);
		topBar.setLeftButton(R.drawable.fanhui);
		app_versionTextView= (TextView)findViewById(R.id.app_version);
		
		try {
			app_versionTextView.setText("枢纽通 v"+getVersionName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	  private String getVersionName() throws Exception
	   {
	           // 获取packagemanager的实例
	           PackageManager packageManager = getPackageManager();
	           // getPackageName()是你当前类的包名，0代表是获取版本信息
	           PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),0);
	           String version = packInfo.versionName;
	           return version;
	   }
	/* (non-Javadoc)
	 * @see nlsde.junction.topbar.TopBarClickListener#leftBtnClick()
	 */
	@Override
	public void leftBtnClick() {
		this.finish();
		topBar.setLeftButton(R.drawable.fanhui_click);
		
	}
	/* (non-Javadoc)
	 * @see nlsde.junction.topbar.TopBarClickListener#rightBtnClick()
	 */
	@Override
	public void rightBtnClick() {
		// TODO Auto-generated method stub
		
	}

}
