/**
 * List_All.java
 * 
 * @Description: 枢纽列表
 * 
 * @File: List_All.java
 * 
 * @Package nlsde.junction.list
 * 
 * @Author chaos
 * 
 * @Date 2014-10-24下午4:00:43
 * 
 * @Version V1.0
 */
package nlsde.junction.list;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import nlsde.baidu.BaseMap;
import nlsde.junction.R;
import nlsde.junction.home.Junction;
import nlsde.junction.net.JunctionHttp;
import nlsde.junction.topbar.TopBar;
import nlsde.junction.topbar.TopBarClickListener;
import nlsde.tools.RefreshableView;
import nlsde.tools.RefreshableView.PullToRefreshListener;
import android.R.string;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * @author chaos
 *
 */
public class List_All extends ListActivity implements TopBarClickListener{

	private static String TAG = List_All.class.getSimpleName();
	private TopBar topBar;
	private RefreshableView refreshableView;
	private ArrayList<HashMap<String, Object>>   listItems;    //存放文字、图片信息
	private SimpleAdapter listItemAdapter;           //适配器    
	private JSONObject junctionlist_json;
	private int[] shuniutu={R.drawable.dzm001,R.drawable.sh1};//,R.drawable.sjz1
	private String[] shuniumingStrings={"",""};//,"宋家庄"
	private ListView listView;
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
		setTheme(android.R.style.Theme_Translucent_NoTitleBar);
		setContentView(R.layout.shuniuzhan);
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		initView();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		topBar = (TopBar)findViewById(R.id.topbar_shuniuzhan);
		topBar.setTopBarClickListener(this);
		topBar.setLeftButton(R.drawable.fanhui);
		topBar.setTitle("枢纽站");
		topBar.setRightButton(R.drawable.shiwaiditu2);
		
		listView = this.getListView();
		refreshableView = (RefreshableView) findViewById(R.id.refreshable_view);
		refreshableView.setOnRefreshListener(new PullToRefreshListener() {
			@Override
			public void onRefresh() {
//				try {
//					junctionlist_json = JunctionHttp.gethublist();
//					if(junctionlist_json.getInt("ret")==0){
//						Log.v(TAG, junctionlist_json.toString());
//						if(junctionlist_json.getJSONObject("data").getJSONArray("list")!=null)
//						{
//							JSONArray junctionlist_Array = junctionlist_json.getJSONObject("data").getJSONArray("list");
//							listItems.clear();
//							for(int i=0;i<junctionlist_Array.length();i++)    {   
//					            HashMap<String, Object> map = new HashMap<String, Object>();   
//					            map.put("shuniutu",  R.drawable.dongzhimen);    //图片
//					            map.put("shuniuming",junctionlist_Array.getJSONObject(i).getString("name"));//文字      
//					            listItems.add(map);   
//					        } 
//							
//						}
//					}
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				refreshableView.finishRefreshing();
			}
		}, 0);

		
		listItems = new ArrayList<HashMap<String, Object>>();
        for(int i=0;i<2;i++)    {   
            HashMap<String, Object> map = new HashMap<String, Object>();   
            map.put("shuniutu", shuniutu[i]);    //图片
            map.put("shuniuming",shuniumingStrings[i]);//文字      
            listItems.add(map);   
        }   
        //生成适配器的Item和动态数组对应的元素   
        listItemAdapter = new SimpleAdapter(this,listItems,   // listItems数据源    
                R.layout.shuniu_item,  //ListItem的XML布局实现   
                new String[] {"shuniutu", "shuniuming"},     //动态数组与ImageItem对应的子项          
                new int[ ] {R.id.shuniutu, R.id.shuniuming}      //list_item.xml布局文件里面的一个ImageView的ID,一个TextView 的ID   
        );   
		this.setListAdapter(listItemAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int poistion,
					long arg3) {
				switch (poistion) {
				case 0:
					Junction.hid=1;
					List_All.this.finish();
					break;
				case 1:
					Junction.hid=2;
					List_All.this.finish();
					break;
				case 2:
					Junction.hid=3;
					List_All.this.finish();
					break;
				default:
					break;
				}
				
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
		topBar.setRightButton(R.drawable.shiwiditu2_click);
		Intent intent = new Intent(List_All.this,BaseMap.class);
		try {
			junctionlist_json = JunctionHttp.gethublist();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(junctionlist_json!=null){
		intent.putExtra("junction", junctionlist_json.toString());}
		startActivity(intent);
		this.finish();
		
	}
	
	
}
