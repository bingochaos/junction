package nlsde.junction;

import java.util.ArrayList;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import nlsde.tools.BitmapDecode;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

public class GuideActivity extends Activity implements OnPageChangeListener{
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

	// 定义ViewPager对象
    private ViewPager viewPager;

    // 定义ViewPager适配器
    private ViewPagerAdapter vpAdapter;

    // 定义一个ArrayList来存放View
    private ArrayList<View> views;

    // 定义各个界面View对象
    private View view1, view2;
    
    private ImageView imageView1,imageView2;
   // 定义底部小点图片
    private ImageView pointImage1, pointImage2;
    //定义开始按钮对象
    private Button startBt;
            
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_main);
            PushAgent.getInstance(getApplicationContext()).onAppStart();
            initView();
            
            initData();
    }

    /**
     * 初始化组件
     */
    private void initView() {
            //实例化各个界面的布局对象 
            LayoutInflater mLi = LayoutInflater.from(this);
            view1 = mLi.inflate(R.layout.guide_view1, null);
            view2 = mLi.inflate(R.layout.guide_view2, null);
            imageView1 = (ImageView)view1.findViewById(R.id.guide1);
            imageView1.setImageBitmap(BitmapDecode.decodeSampledBitmapFromResource(getResources(),
            		 R.drawable.loginscreen1,
            		 getWindowManager().getDefaultDisplay().getWidth(),
            		 getWindowManager().getDefaultDisplay().getHeight()));
            imageView2 = (ImageView)view2.findViewById(R.id.guide2);
            imageView2.setImageBitmap(BitmapDecode.decodeSampledBitmapFromResource(getResources(),
           		 R.drawable.loginscreen2,
           		getWindowManager().getDefaultDisplay().getWidth(),
        		getWindowManager().getDefaultDisplay().getHeight()));
            // 实例化ViewPager
            viewPager = (ViewPager) findViewById(R.id.viewpager);

            // 实例化ArrayList对象
            views = new ArrayList<View>();

            // 实例化ViewPager适配器
            vpAdapter = new ViewPagerAdapter(views);
            
            //实例化开始按钮
            startBt = (Button) view2.findViewById(R.id.startapp);
            // 实例化底部小点图片对象
            pointImage1 = (ImageView) findViewById(R.id.page0);
            pointImage2 = (ImageView) findViewById(R.id.page1);
    }

    /**
     * 初始化数据
     */
    private void initData() {
    	//将要分页显示的View装入数组中                
            views.add(view1);
            views.add(view2);    
            // 设置监听
            viewPager.setOnPageChangeListener(this);
            // 设置适配器数据
            viewPager.setAdapter(vpAdapter);

                     
                                                            
            // 给开始按钮设置监听
            startBt.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                             startbutton();
                    }
            });
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
                    
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
            
    }

    @Override
    public void onPageSelected(int position) {
    	switch (position) {
        case 0:
                pointImage1.setImageDrawable(getResources().getDrawable(R.drawable.loginscreendot_selected));
                pointImage2.setImageDrawable(getResources().getDrawable(R.drawable.loginscreendot));
                break;
        case 1:
                pointImage1.setImageDrawable(getResources().getDrawable(R.drawable.loginscreendot));
                pointImage2.setImageDrawable(getResources().getDrawable(R.drawable.loginscreendot_selected));
                break;
    	}
    }
    
    /**
     * 相应按钮点击事件
     */
    private void startbutton() {  
          Intent intent = new Intent();
            intent.setClass(GuideActivity.this,Main.class);
            startActivity(intent);
            this.finish();
  }
}
