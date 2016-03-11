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
import nlsde.junction.net.JunctionHttp;
import android.app.Activity;
import android.content.Intent;
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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class PointChooseActivivy extends Activity implements OnClickListener {

	private Button mylocationButton, mapButton;
	private Intent navigateIntent, resultIntent;
	private Button backbutton, cancelbutton, searchButton;
	private EditText search_keyEditText;
	private ListView searchresult;
	private ArrayList<HashMap<String, Object>> listItems; // 存放文字、图片信息
	private SimpleAdapter listItemAdapter; // 适配器
	private int type;
	private PoiPoint startPoint, stopPoint;
	private boolean ismylocation;

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
		setContentView(R.layout.indoormap2_choosepoint);
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		navigateIntent = getIntent();
		type = navigateIntent.getExtras().getInt("choosepoint_type");
		ismylocation = navigateIntent.getExtras().getBoolean("mylocation");
		startPoint = (PoiPoint) navigateIntent.getSerializableExtra("start");
		stopPoint = (PoiPoint) navigateIntent.getSerializableExtra("stop");
		resultIntent = new Intent();
		initview();
	}

	/**
	 * 
	 */
	private void initview() {
		mylocationButton = (Button) findViewById(R.id.indoormap2_choosepoint_mylocation);
		mapButton = (Button) findViewById(R.id.indoormap2_choosepoint_choosefrommap);
		mylocationButton.setOnClickListener(this);
		mapButton.setOnClickListener(this);
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
		searchresult = (ListView) findViewById(R.id.indoormap_point_search_result);
		listItems = new ArrayList<HashMap<String, Object>>();
		listItemAdapter = new SimpleAdapter(this, listItems, // listItems数据源
				R.layout.search_result_item, // ListItem的XML布局实现
				new String[] { "caption", "info" }, // 动态数组与ImageItem对应的子项
				new int[] { R.id.search_result_name, R.id.search_result_floor } // list_item.xml布局文件里面的一个ImageView的ID,一个TextView
																				// 的ID
		);
		searchresult.setAdapter(listItemAdapter);
		listItemAdapter.notifyDataSetChanged();
		searchresult.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map = listItems.get(position);
				Log.v(PointChooseActivivy.class.getSimpleName(),
						map.get("type") + "");
				Log.v(PointChooseActivivy.class.getSimpleName(), map.toString());
				Intent intent = new Intent();
				intent.putExtra("floorId", map.get("floorId").toString());
				intent.putExtra("mapName", map.get("mapName").toString());
				intent.putExtra("clon", map.get("clon").toString());
				intent.putExtra("clat", map.get("clat").toString());
				intent.putExtra("info", map.get("info").toString());
				intent.putExtra("caption", map.get("caption").toString());
				setResult(type, intent);
				PointChooseActivivy.this.finish();
			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.indoormap2_choosepoint_mylocation:
			if (ismylocation == false) {
				Toast.makeText(getApplicationContext(), "当前位置不可用",
						Toast.LENGTH_LONG).show();
			} else {
				setResult(type + 2);
				this.finish();
			}

			break;
		case R.id.indoormap2_choosepoint_choosefrommap:
			Intent intent_mapIntent = new Intent(PointChooseActivivy.this,
					IndoorMap2.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("start", startPoint);
			bundle.putSerializable("stop", stopPoint);
			intent_mapIntent.putExtras(bundle);
			intent_mapIntent.putExtra("choosepoint_type", type);
			startActivity(intent_mapIntent);
			this.finish();
			break;
		case R.id.search_back:
			setResult(0);
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
			JSONObject jsonObject = JunctionHttp.getPoiSearch(Junction.hid, kw);

			if (jsonObject.getInt("ret") == 0) {
				JSONArray jsonArray = jsonObject.getJSONObject("data")
						.getJSONArray("list");
				listItems.clear();
				for (int i = 0; i < jsonArray.length(); i++) {
					HashMap<String, Object> map = new HashMap<String, Object>();

					map.put("caption",
							jsonArray.getJSONObject(i).getString("caption"));
					map.put("floorId",
							jsonArray.getJSONObject(i).getString("floorId"));
					map.put("mapName",
							jsonArray.getJSONObject(i).getString("mapName"));
					map.put("info", jsonArray.getJSONObject(i)
							.getString("info"));
					map.put("clon", jsonArray.getJSONObject(i)
							.getString("clon"));
					map.put("clat", jsonArray.getJSONObject(i)
							.getString("clat"));

					listItems.add(map);
				}
			} else if (jsonObject.getInt("ret") == -5) {
				Toast.makeText(getApplicationContext(), "没有结果",
						Toast.LENGTH_LONG).show();
			}
			listItemAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
