/**
 * AirportTrackActivity.java
 * 
 * @Description: 
 * 
 * @File: AirportTrackActivity.java
 * 
 * @Package nlsde.junction.home.function
 * 
 * @Author chaos
 * 
 * @Date 2014-12-1下午12:37:20
 * 
 * @Version V1.0
 */
package nlsde.junction.home.function;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import nlsde.junction.R;
import nlsde.junction.home.Junction;
import nlsde.junction.net.JunctionHttp;
import nlsde.tools.DensityUtil;

import cn.w.song.widget.navigation.PressNavigationBar;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * @author chaos
 *
 */
public class AirportTrackActivity extends ActivityGroup {

	private LinearLayout containLayout;
	private View linearLayout;
	private  LocalActivityManager manager;
	private Button button;
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
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.airporttrack);
		containLayout =(LinearLayout)findViewById(R.id.airport_container);
		button= (Button)findViewById(R.id.btn_title_left);
		manager=getLocalActivityManager();
		linearLayout =LayoutInflater.from(getApplicationContext()).inflate(R.layout.airport_subway, null);
		PressNavigationBar pressNavigationBar = (PressNavigationBar) findViewById(R.id.navigationbartest);
		/* 动态部署数据 */
		String[] text = { "", "" };
		containLayout.addView(linearLayout);
		int[] textSize = { DensityUtil.dip2px(this, 5), DensityUtil.dip2px(this, 5)};//单位sp
		int[] textColor = { Color.WHITE, Color.WHITE};
		int[] image = { R.drawable.left,
				R.drawable.right,
				 };//未被选择样式（图片）
		int[] imageSelected = { R.drawable.left_click,
				R.drawable.right_click };//被选择样式（图片）
		List<Map<String, Object>> pressBarList = new LinkedList<Map<String, Object>>();
		for (int i = 0; i < image.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("text", text[i]);
			map.put("textSize", textSize[i]);
			map.put("textColor", textColor[i]);
			map.put("image", image[i]);
			map.put("imageSelected", imageSelected[i]);
			pressBarList.add(map);
		}
		
		/* "按下效果导航栏"添加子组件  */
		pressNavigationBar.addChild(pressBarList);
		
		/* "按下效果导航栏"添加监视 */
		pressNavigationBar.setPressNavigationBarListener(new PressNavigationBar.PressNavigationBarListener() {
		  /**
            *@params position 被选位置
            *@params view 为导航栏
            *@params event 移动事件
            */
			@Override
			public void onNavigationBarClick(int position, View view,
					MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:// 按下去时
					
					if (position==0) {
						containLayout.removeAllViews();
						containLayout.addView(linearLayout);
					}else {
						Intent intent= new Intent(getApplicationContext(), Airport_bus_list.class);
//						JSONObject jsonObject_bus;
//						
//							try {
//								jsonObject_bus = JunctionHttp.getAirportBus(Junction.hid,0);
//							
//							if (jsonObject_bus.getInt("ret")==0) {
//							intent.putExtra("bus", jsonObject_bus.toString());
//						}} catch (Exception e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
						containLayout.removeAllViews();
						containLayout.addView(manager.startActivity(  
	                            "PAGE_0",  
	                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))  
	                            .getDecorView());  
					}
					
					
					
					
					break;
				case MotionEvent.ACTION_MOVE://移动中
					
					break;
				case MotionEvent.ACTION_UP:// 抬手时	
					
					break;
				}
			}
		});
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				AirportTrackActivity.this.finish();
				
			}
		});
	}
	/* (non-Javadoc)
	 * @see android.app.ActivityGroup#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	
}
