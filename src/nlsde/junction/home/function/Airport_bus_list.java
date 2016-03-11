/**
 * Airport_bus_list.java
 * 
 * @Description: 
 * 
 * @File: Airport_bus_list.java
 * 
 * @Package nlsde.junction.home.function
 * 
 * @Author chaos
 * 
 * @Date 2014-12-16下午2:34:28
 * 
 * @Version V1.0
 */
package nlsde.junction.home.function;

import java.util.ArrayList;
import java.util.HashMap;

import nlsde.junction.R;
import nlsde.junction.home.Junction;
import nlsde.junction.net.JunctionHttp;
import nlsde.junction.searchbar.ListViewLoadMore;
import nlsde.junction.searchbar.ListViewLoadMore.IsLoadingListener;
import nlsde.tools.RefreshableView;
import nlsde.tools.RefreshableView.PullToRefreshListener;

import org.json.JSONArray;
import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

/**
 * @author Administrator
 * 
 */
public class Airport_bus_list extends Activity implements IsLoadingListener {

	final static String TAG = Airport_bus_list.class.getSimpleName();

	private SearchView mSearchView;

	private RefreshableView refreshableView;
	private ArrayList<HashMap<String, Object>> listItems;
	private SimpleAdapter listItemAdapter; // 适配器
	private ListViewLoadMore busListView;
	private Intent intent;
	private JSONObject intentJsonObject, jsonObject;
	private JSONArray jsonArray;
	private int page = 1;
	private Handler handler = new Handler();
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
		setContentView(R.layout.airport_bus_list);
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		listItems = new ArrayList<HashMap<String, Object>>();

		try {
			loadlist();
		} catch (Exception e1) {
			Toast.makeText(Airport_bus_list.this, R.string.neterror,
					Toast.LENGTH_SHORT).show();
			e1.printStackTrace();
		}

		busListView = (ListViewLoadMore) findViewById(R.id.search_list_view_airport);
		refreshableView = (RefreshableView) findViewById(R.id.refreshable_view_airport);
		// refreshableView.finishRefreshing();
		busListView.setOnLoadingListener(this);

		// 生成适配器的Item和动态数组对应的元素
		listItemAdapter = new SimpleAdapter(this, listItems, // listItems数据源
				R.layout.bus_list_item, // ListItem的XML布局实现
				new String[] { "bus_num", "bus_start", "bus_stop",
						"bus_updown", "bus_startstation", "bus_stopstation",
						"real" }, // 动态数组与ImageItem对应的子项
				new int[] { R.id.bus_num, R.id.bus_start, R.id.bus_stop,
						R.id.bus_list_updown, R.id.bus_startstation,
						R.id.bus_stopstation, R.id.shishi } // list_item.xml布局文件里面的一个ImageView的ID,一个TextView
															// 的ID
		);
		busListView.setAdapter(listItemAdapter);
		listItemAdapter.notifyDataSetChanged();
		busListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Log.v("item", listItems.get(arg2).get("bus_num") + "");
				Intent intent = new Intent(Airport_bus_list.this,
						Airport_bus.class);
				intent.putExtra("bus_num", listItems.get(arg2).get("bus_num")
						+ "");
				intent.putExtra("updown", listItems.get(arg2).get("updown")
						+ "");
				intent.putExtra("bus_startstation",
						listItems.get(arg2).get("bus_startstation") + "");
				intent.putExtra("bus_stopstation",
						listItems.get(arg2).get("bus_stopstation") + "");
				intent.putExtra("bus_stoptime",
						listItems.get(arg2).get("bus_stop") + "");
				intent.putExtra("bus_starttime",
						listItems.get(arg2).get("bus_start") + "");
				intent.putExtra("real", listItems.get(arg2).get("real") + "");
			//	intent.putExtra("extra", listItems.get(arg2).get("extra") + "");
				intent.putExtra("poiId", listItems.get(arg2).get("poiId") + "");
				intent.putExtra("addr", listItems.get(arg2).get("addr") + "");
				startActivity(intent);

			}
		});
		refreshableView.setOnRefreshListener(new PullToRefreshListener() {
			@Override
			public void onRefresh() {
				try {
					page = 1;
					loadlist();
				} catch (Exception e) {
					Toast.makeText(Airport_bus_list.this, R.string.neterror,
							Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
				refreshableView.finishRefreshing();
			}
		}, 1);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see nlsde.junction.searchbar.ListViewLoadMore.IsLoadingListener#onLoad()
	 */
	@Override
	public void onLoad() {
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {

				try {
					loadlist();

				} catch (Exception e) {
					Toast.makeText(Airport_bus_list.this, R.string.neterror,
							Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
				listItemAdapter.notifyDataSetChanged();
				busListView.complateLoad();
			}
		}, 1000);

	}

	/**
	 * @throws Exception
	 * 
	 */
	protected void loadlist() throws Exception {
		jsonObject = JunctionHttp.getAirportBus(Junction.hid, page);
		if(jsonObject.getInt("ret")==-7){
			JunctionHttp.init();
			jsonObject = JunctionHttp.getAirportBus(Junction.hid, page);
		}
		
		
		jsonArray = jsonObject.getJSONObject("data").getJSONArray("list");
		if (jsonArray.length() > 0) {
			if(page==1){
				listItems.clear();
			}
			page++;
			for (int i = 0; i < jsonArray.length(); i++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("bus_num", jsonArray.getJSONObject(i)
						.getString("route"));
				map.put("bus_startstation", jsonArray.getJSONObject(i)
						.getString("start"));
				map.put("bus_stopstation", jsonArray.getJSONObject(i)
						.getString("end"));
				map.put("bus_start",
						jsonArray.getJSONObject(i).getString("first"));
				map.put("bus_stop", jsonArray.getJSONObject(i)
						.getString("last"));
				map.put("updown", jsonArray.getJSONObject(i)
						.getString("updown"));
//				if (jsonArray.getJSONObject(i).getInt("updown") == 1)
//					map.put("bus_updown", R.drawable.bus_zuojiantou);
//				else {
//					
//				}
				map.put("bus_updown", R.drawable.bus_youjiantou);
				if (jsonArray.getJSONObject(i).getInt("real") == 1)
					map.put("real", R.drawable.shishi);
				else {
					map.put("real", null);
				}
				listItems.add(map);
			}

		}
	}
}
