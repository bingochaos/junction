/**
 * SearchActivity.java
 * 
 * @Description: 
 * 
 * @File: SearchActivity.java
 * 
 * @Package nlsde.junction.home.function
 * 
 * @Author chaos
 * 
 * @Date 2014-12-22下午2:59:52
 * 
 * @Version V1.0
 */
package nlsde.junction.home.indoormap;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import nlsde.junction.R;
import nlsde.junction.home.Junction;
import nlsde.junction.home.function.BusActivity2;
import nlsde.junction.home.function.IndoorMap2;
import nlsde.junction.list.Ad;
import nlsde.junction.list.Junction_List;
import nlsde.junction.net.JunctionHttp;
import nlsde.junction.searchbar.BusSearch;
import nlsde.tools.AsyncImageLoader;
import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author chaos
 *
 */
public class SearchActivity extends Activity implements OnClickListener{


	private Button backbutton,cancelbutton,searchButton;
	private EditText search_keyEditText;
	private ListView searchresult;
	private ArrayList<HashMap<String, Object>>   listItems;    //存放文字、图片信息
	private SimpleAdapter listItemAdapter;           //适配器    
	Intent intent;
	private int type=0;
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
		setContentView(R.layout.search);
		initView();
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		intent = getIntent();
		if(intent.hasExtra("type")){
			switch (intent.getExtras().getInt("type")) {
			case 1:
				type=1;
				search_keyEditText.setText("洗手间");
				loadlist();
				break;
			case 2:
				type=1;
				search_keyEditText.setText("售票窗口");
				loadlist();
				break;
			default:
				break;
			}
			
		}
	}

	/**
	 * 
	 */
	private void initView() {
		backbutton = (Button) findViewById(R.id.search_back);
		cancelbutton = (Button) findViewById(R.id.search_cancel);
		searchButton = (Button) findViewById(R.id.search_start);
		search_keyEditText = (EditText) findViewById(R.id.search_key);
		search_keyEditText
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_SEARCH
								|| (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
							loadlist();
							return true;
						}
						return false;
					}
				});
		backbutton.setOnClickListener(this);
		cancelbutton.setOnClickListener(this);
		searchButton.setOnClickListener(this);
		searchresult = (ListView) findViewById(R.id.indoormap_search_result);
		listItems = new ArrayList<HashMap<String, Object>>();
		// for(int i=0;i<10;i++) {
		// HashMap<String, Object> map = new HashMap<String, Object>();
		// map.put("name", "item: "+i);
		// map.put("floor", 1111);
		// listItems.add(map);
		// }
		// 生成适配器的Item和动态数组对应的元素
		listItemAdapter = new SimpleAdapter(this, listItems, // listItems数据源
				R.layout.search_result_item, // ListItem的XML布局实现
				new String[] { "name", "floor" }, // 动态数组与ImageItem对应的子项
				new int[] { R.id.search_result_name, R.id.search_result_floor } // list_item.xml布局文件里面的一个ImageView的ID,一个TextView
																				// 的ID
		);
		// listItemAdapter.setViewBinder(new ViewBinder() {
		//
		// @Override
		// public boolean setViewValue(View view, Object data,
		// String textRepresentation) {
		// if (view instanceof ImageView && data instanceof Bitmap) {
		// ImageView i = (ImageView) view;
		// i.setImageBitmap((Bitmap) data);
		// return true;
		// }
		// return false;
		// }
		// }
		// );

		searchresult.setAdapter(listItemAdapter);
		listItemAdapter.notifyDataSetChanged();
		searchresult.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map = listItems.get(position);
				Log.v(SearchActivity.class.getSimpleName(), map.get("type")
						+ "");
				Log.v(SearchActivity.class.getSimpleName(), map.toString());
				if (Integer.parseInt(map.get("type").toString()) == 1) {
					Log.v(SearchActivity.class.getSimpleName(), map.toString());
					Intent intent_map = new Intent(SearchActivity.this,
							IndoorMap2.class);
					intent_map.putExtra("poiId", map.get("poiId").toString());
					if (type == 1)
						intent_map.putExtra("toilet", type);
					startActivity(intent_map);
					SearchActivity.this.finish();
				} else if (Integer.parseInt(map.get("type").toString()) == 2) {
					Log.v(SearchActivity.class.getSimpleName(), map.toString());
					Intent intent_shop = new Intent(SearchActivity.this,
							ShopDetailActivity.class);
					intent_shop.putExtra("shopId",
							Integer.parseInt(map.get("shopId").toString()));
					intent_shop.putExtra("poiId", map.get("poiId").toString());
					if (type == 1)
						intent_shop.putExtra("toilet", type);
					startActivity(intent_shop);
					SearchActivity.this.finish();
				} else if (Integer.parseInt(map.get("type").toString()) == 3) {
					Log.v(SearchActivity.class.getSimpleName(), map.toString());
					Intent intent_bus = new Intent(SearchActivity.this,
							BusActivity2.class);
					intent_bus.putExtra("updown", map.get("updown").toString());
					intent_bus.putExtra("route", map.get("route").toString());
					intent_bus.putExtra("poiId", map.get("poiId").toString());
					if (type == 1)
						intent_bus.putExtra("toilet", type);
					startActivity(intent_bus);
					SearchActivity.this.finish();
				}
			}
		});

	}
	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.search_back:
			this.finish();
			break;
		case R.id.search_cancel:
			search_keyEditText.setText("");
			break;
		case R.id.search_start:
			loadlist();
			break;
		default:
			break;
		}
		
	}
	/**
	 * 
	 */
	private void loadlist() {
		String kw = search_keyEditText.getText().toString();
		try {
			JSONObject jsonObject =JunctionHttp.getHubSearch(Junction.hid,kw);

			if (jsonObject.getInt("ret") == 0) {
				JSONArray jsonArray = jsonObject.getJSONObject("data")
						.getJSONArray("list");
				listItems.clear();
				for (int i = 0; i < jsonArray.length(); i++) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("type",
							jsonArray.getJSONObject(i).getInt("type")); 
					
					map.put("name",
							jsonArray.getJSONObject(i).getString("caption"));
					map.put("floor",
							jsonArray.getJSONObject(i).getString("info")); 
					map.put("poiId",
							jsonArray.getJSONObject(i).getString("poiId")); 
					map.put("floorId",
							jsonArray.getJSONObject(i).getString("floorId")); 
					if(jsonArray.getJSONObject(i).getInt("type")==2){
						map.put("shopId",
								jsonArray.getJSONObject(i).getString("shopId")); 
						 
					}else if(jsonArray.getJSONObject(i).getInt("type")==3){
						map.put("route",
								jsonArray.getJSONObject(i).getString("route")); 
						map.put("updown",
								jsonArray.getJSONObject(i).getString("updown"));
					}
					
					

					listItems.add(map);
				}
			}else if(jsonObject.getInt("ret") == -5){
				Toast.makeText(getApplicationContext(), "无查询结果", Toast.LENGTH_SHORT).show();
			}
			listItemAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), R.string.neterror, Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		
	}
	

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
//		Intent intent = new Intent(SearchActivity.this,IndoorMap2.class);
//		startActivity(intent);
		super.onDestroy();
	}

}
