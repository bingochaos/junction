/**
 * FloorSelectActivity.java
 * 
 * @Description: 
 * 
 * @File: FloorSelectActivity.java
 * 
 * @Package nlsde.junction.home.indoormap
 * 
 * @Author chaos
 * 
 * @Date 2014-12-22下午3:05:22
 * 
 * @Version V1.0
 */
package nlsde.junction.home.indoormap;

import java.util.ArrayList;
import java.util.HashMap;

import nlsde.junction.R;
import nlsde.junction.home.Junction;
import nlsde.junction.net.JunctionHttp;
import nlsde.tools.RefreshableView;
import nlsde.tools.RefreshableView.PullToRefreshListener;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

/**
 * @author chaos
 *
 */
public class FloorSelectActivity extends Activity {

	private Button back,floorButton;
	private RefreshableView refreshableView;
	private ListView listView;
	private ArrayList<HashMap<String, Object>>   listItems;    //存放文字、图片信息
	private SimpleAdapter listItemAdapter;  //适配器
	private String floornum;
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
		setContentView(R.layout.floorselect);
		floornum= getIntent().getExtras().getString("floor");
		
		initview();
		PushAgent.getInstance(getApplicationContext()).onAppStart();
	}
	private void initview() {

		back =(Button)findViewById(R.id.floorselect_back);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				setResult(2);
				FloorSelectActivity.this.finish();
				
			}
		});
		floorButton=(Button)findViewById(R.id.floorselect_choosefloor);
		floorButton.setText(floornum);
		refreshableView= (RefreshableView)findViewById(R.id.refreshable_view_floorselect);
		refreshableView.setOnRefreshListener(new PullToRefreshListener() {
		@Override
		public void onRefresh() {
			loadlist();
			refreshableView.finishRefreshing();
		}
	}, 0);
		listView = (ListView)findViewById(R.id.floor_select);
		listItems = new ArrayList<HashMap<String, Object>>();
		loadlist();
        //生成适配器的Item和动态数组对应的元素   
        listItemAdapter = new SimpleAdapter(this,listItems,   // listItems数据源    
                R.layout.indoormap2_flooritem,  //ListItem的XML布局实现   
                new String[] {"floornum","floorname","floordetail"},     //动态数组与ImageItem对应的子项          
                new int[ ] {R.id.floornum,R.id.floorname,R.id.floordetail}      //list_item.xml布局文件里面的一个ImageView的ID,一个TextView 的ID   
        ); 
        listItemAdapter.notifyDataSetChanged();
		listView.setAdapter(listItemAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				listItems.get(arg2).get("bus_startstation");
				Intent intent=new Intent();
			    intent.putExtra("floorId",listItems.get(arg2).get("floorId").toString());
			    intent.putExtra("mapName",listItems.get(arg2).get("mapName").toString());
			    intent.putExtra("label",listItems.get(arg2).get("floornum").toString());
			    setResult(RESULT_OK, intent);
			    FloorSelectActivity.this.finish();
			}
		});
	}
	/**
	 * 
	 */
	protected void loadlist() {
		try {
			JSONObject jsonObject=JunctionHttp.getFloorList(Junction.hid);
			if(jsonObject.getInt("ret")==0)
			{
				listItems.clear();
				JSONArray jsonArray= jsonObject.getJSONObject("data").getJSONArray("list");
				Log.v("indoormap", jsonObject.toString());
	        for(int i=0;i<jsonArray.length();i++)    {   
	            HashMap<String, Object> map = new HashMap<String, Object>();   
	            map.put("floornum", jsonArray.getJSONObject(i).getString("label"));
	            map.put("floorname", jsonArray.getJSONObject(i).getString("name")); 
	            map.put("floordetail",jsonArray.getJSONObject(i).getString("info"));
	            map.put("floorId",jsonArray.getJSONObject(i).getString("floorId"));
	            map.put("mapName",jsonArray.getJSONObject(i).getString("mapName"));
	            listItems.add(map);   
	        }   
	        
			}
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	

}
