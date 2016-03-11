package nlsde.junction.more;

import java.util.ArrayList;
import java.util.HashMap;

import com.umeng.analytics.MobclickAgent;

import nlsde.junction.R;
import nlsde.junction.home.Junction;
import nlsde.junction.list.List_All;
import nlsde.junction.topbar.TopBar;
import nlsde.junction.topbar.TopBarClickListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class IntroductionList extends Activity implements TopBarClickListener{

	private TopBar topBar;
	private Intent intent;
	private ListView listView;
	private ArrayList<HashMap<String, Object>>   listItems;    
	private SimpleAdapter listItemAdapter;  
	private String[] shuniumingStrings={"东直门","四惠"};//,"宋家庄"
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.introductionlist);
		initView();
		
	}
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
	private void initView() {
		topBar = (TopBar)findViewById(R.id.topbar_moreinfo_introductionlist);
		listView = (ListView)findViewById(R.id.introduction_list);
		topBar.setLeftButton(R.drawable.fanhui);
		topBar.setTitle("枢纽简介");
		topBar.setTopBarClickListener(this);
		listView = (ListView)findViewById(R.id.introduction_list);
		listItems = new ArrayList<HashMap<String, Object>>();
        for(int i=0;i<2;i++)    {   
            HashMap<String, Object> map = new HashMap<String, Object>();   
            map.put("shuniuming",shuniumingStrings[i]);//文字      
            listItems.add(map);   
        }   
        //生成适配器的Item和动态数组对应的元素   
        listItemAdapter = new SimpleAdapter(this,listItems,   // listItems数据源    
                R.layout.introduction_shuniu_item,  //ListItem的XML布局实现   
                new String[] { "shuniuming"},     //动态数组与ImageItem对应的子项          
                new int[ ] { R.id.shuniuming}      //list_item.xml布局文件里面的一个ImageView的ID,一个TextView 的ID   
        );   
		listView.setAdapter(listItemAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int poistion,
					long arg3) {
				switch (poistion) {
				case 0:
					intent= new Intent(IntroductionList.this,Introduction.class); 
					intent.putExtra("shuniuming", 1);
					startActivity(intent);
					break;
				case 1:
					intent= new Intent(IntroductionList.this,Introduction.class); 
					intent.putExtra("shuniuming", 2);
					startActivity(intent);
					break;
				case 2:
				
					break;
				default:
					break;
				}
				
			}
		});
		
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
