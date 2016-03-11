package nlsde.junction.home.function;

import java.util.ArrayList;
import java.util.HashMap;

import nlsde.junction.R;
import nlsde.junction.home.Junction;
import nlsde.junction.net.JunctionHttp;
import nlsde.junction.topbar.TopBar;
import nlsde.junction.topbar.TopBarClickListener;

import org.json.JSONArray;
import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class CoachInfoActivity extends Activity implements TopBarClickListener,
		OnClickListener {

	private Intent intent;
	private int daySeq;
	private String dest;
	private TopBar topBar;
	private TextView day1, day2, day3;
	private TextView pubtimeTextView;
	private Button refreshButton;
	private ListView listView;
	private ArrayList<HashMap<String, Object>> listItems;
	private SimpleAdapter listItemAdapter;
	 private ProgressDialog dialog;  
	 private LinearLayout linerlLayout;
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
		setContentView(R.layout.coach_info);
		intent = getIntent();
		daySeq = intent.getExtras().getInt("currentDay");
		dest = intent.getExtras().getString("end");
		initView();
		loadList(daySeq, dest);
	}

	/**
	 * 
	 */
	private void initView() {

		linerlLayout = (LinearLayout)findViewById(R.id.coach_info_body);
		topBar = (TopBar) findViewById(R.id.topbar_coach_info);
		topBar.setTopBarClickListener(this);
		topBar.setLeftButton(R.drawable.fanhui);

		day1 = (TextView) findViewById(R.id.coach_info_day1);
		day2 = (TextView) findViewById(R.id.coach_info_day2);
		day3 = (TextView) findViewById(R.id.coach_info_day3);
		day1.setOnClickListener(this);
		day2.setOnClickListener(this);
		day3.setOnClickListener(this);
		pubtimeTextView = (TextView) findViewById(R.id.coach_info_pubtime);

		refreshButton = (Button) findViewById(R.id.coach_info_refresh);
		refreshButton.setOnClickListener(this);

		listView = (ListView) findViewById(R.id.coach_info_list);
		// 生成适配器的Item和动态数组对应的元素
		listItems = new ArrayList<HashMap<String, Object>>();
		listItemAdapter = new SimpleAdapter(this, listItems, // listItems数据源
				R.layout.coach_info_item, // ListItem的XML布局实现
				new String[] { "time", "schId", "routeName", "start2dest",
						"price", "seats", "rest" }, // 动态数组与ImageItem对应的子项
				new int[] { R.id.coach_info_time, R.id.coach_info_schId,
						R.id.coach_info_routeName, R.id.coach_info_start2dest,
						R.id.coach_info_price, R.id.coach_info_seats,
						R.id.coach_info_rest } // list_item.xml布局文件里面的一个ImageView的ID,一个TextView
												// 的ID
		);
		listView.setAdapter(listItemAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

			}
		});
		switch (daySeq) {
		case 1:
			day1.setBackgroundColor(0xff6bc5e8);
			day1.setTextColor(0xffffffff);
			day2.setBackgroundColor(0xffffffff);
			day2.setTextColor(0xff8e8e8e);
			day3.setBackgroundColor(0xffffffff);
			day3.setTextColor(0xff8e8e8e);

			break;
		case 2:
			day1.setBackgroundColor(0xffffffff);
			day1.setTextColor(0xff8e8e8e);
			day2.setBackgroundColor(0xff6bc5e8);
			day2.setTextColor(0xffffffff);
			day3.setBackgroundColor(0xffffffff);
			day3.setTextColor(0xff8e8e8e);

			break;
		case 3:
			day1.setBackgroundColor(0xffffffff);
			day1.setTextColor(0xff8e8e8e);
			day2.setBackgroundColor(0xffffffff);
			day2.setTextColor(0xff8e8e8e);
			day3.setBackgroundColor(0xff6bc5e8);
			day3.setTextColor(0xffffffff);
			break;
		default:
			break;
		}
	}

	private JSONArray jsonArray;
	private JSONObject jsonObject;

	/**
	 * 
	 */
	private void loadList(int daySeq, String dest) {
		 dialog = ProgressDialog.show(this, null, "列表正在加载，请稍候...", true, false);  
		try {
			jsonObject = JunctionHttp.getCoachInfo(Junction.hid, daySeq, dest);
			if (jsonObject.getInt("ret") == -7) {
				JunctionHttp.init();
				jsonObject = JunctionHttp.getCoachInfo(Junction.hid, daySeq,
						dest);
			} else if (jsonObject.getInt("ret") == -3) {
				Toast.makeText(getApplicationContext(), "无查询结果",
						Toast.LENGTH_SHORT).show();
			} else {
				topBar.setTitle(jsonObject.getJSONObject("data").getString(
						"start")
						+ "--"
						+ jsonObject.getJSONObject("data").getString("dest"));
				day1.setText(jsonObject.getJSONObject("data").getString("day1"));
				day2.setText(jsonObject.getJSONObject("data").getString("day2"));
				day3.setText(jsonObject.getJSONObject("data").getString("day3"));
				pubtimeTextView.setText(jsonObject.getJSONObject("data")
						.getString("pubTime"));
				jsonArray = jsonObject.getJSONObject("data").getJSONArray(
						"list");
				if(jsonArray.length()==0){
					Toast.makeText(getApplicationContext(), "无查询结果",
							Toast.LENGTH_SHORT).show();
				}
				listItems.clear();
				for (int i = 0; i < jsonArray.length(); i++) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("time",
							jsonArray.getJSONObject(i).getString("drvTime"));
					map.put("schId",
							jsonArray.getJSONObject(i).getString("schId"));
					map.put("routeName",
							jsonArray.getJSONObject(i).getString("routeName"));
					map.put("start2dest", jsonObject.getJSONObject("data")
							.getString("start")
							+ "--"
							+ jsonObject.getJSONObject("data")
									.getString("dest"));
					map.put("price",
							jsonArray.getJSONObject(i).getString("price"));
					map.put("seats",
							jsonArray.getJSONObject(i).getString("seats"));
					map.put("rest", jsonArray.getJSONObject(i)
							.getString("rest"));
					listItems.add(map);
				}
				
				listItemAdapter.notifyDataSetChanged();
				listView.setSelection(0);
			}

		} catch (Exception e) {
			linerlLayout.removeAllViews();
			Toast.makeText(getApplicationContext(), R.string.neterror,
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		
         //启动一个处理loading业务的线程  
		 new LoadingThread(this).start();  
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

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.coach_info_day1:
			day1.setBackgroundColor(0xff6bc5e8);
			day1.setTextColor(0xffffffff);
			day2.setBackgroundColor(0xffffffff);
			day2.setTextColor(0xff8e8e8e);
			day3.setBackgroundColor(0xffffffff);
			day3.setTextColor(0xff8e8e8e);
			daySeq = 1;
			loadList(daySeq, dest);
			break;
		case R.id.coach_info_day2:
			day1.setBackgroundColor(0xffffffff);
			day1.setTextColor(0xff8e8e8e);
			day2.setBackgroundColor(0xff6bc5e8);
			day2.setTextColor(0xffffffff);
			day3.setBackgroundColor(0xffffffff);
			day3.setTextColor(0xff8e8e8e);
			daySeq = 2;
			loadList(daySeq, dest);
			break;
		case R.id.coach_info_day3:
			day1.setBackgroundColor(0xffffffff);
			day1.setTextColor(0xff8e8e8e);
			day2.setBackgroundColor(0xffffffff);
			day2.setTextColor(0xff8e8e8e);
			day3.setBackgroundColor(0xff6bc5e8);
			day3.setTextColor(0xffffffff);
			daySeq = 3;
			loadList(daySeq, dest);
			break;
		case R.id.coach_info_refresh:
			// refreshButton.setBackgroundColor(0xff8e8e8e);
			loadList(daySeq, dest);
			break;
		default:
			break;
		}

	}

	private class LoadingThread extends Thread {
		private CoachInfoActivity activity;

		public LoadingThread(CoachInfoActivity act) {
			activity = act;
		}

		public void run() {
			
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			activity.mHandler.sendEmptyMessage(0);
		}
	}

	// 处理跳转到主Activity
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (dialog.isShowing())
				dialog.dismiss();
		}
	};
}
