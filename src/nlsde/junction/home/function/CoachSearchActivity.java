package nlsde.junction.home.function;

import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import nlsde.junction.R;
import nlsde.junction.home.Junction;
import nlsde.junction.home.indoormap.SearchActivity;
import nlsde.junction.net.JunctionHttp;
import nlsde.junction.topbar.TopBar;
import nlsde.junction.topbar.TopBarClickListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CoachSearchActivity extends Activity implements
		TopBarClickListener, OnClickListener {
	private TopBar topBar;
	private TextView startStation, stopStation, day1, day2, day3;
	private Button searchButton;
	private int currentDay=1;
	private LinearLayout linearLayout;
	private RelativeLayout locationlayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.coach_search);
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		initView();
		initData();
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
	private void initData() {
		try {
			JSONObject jsonObject = JunctionHttp.getCoachList(Junction.hid);
			if (jsonObject.getInt("ret") == 0) {
				startStation.setText(jsonObject.getJSONObject("data")
						.getString("start"));
				day1.setText(jsonObject.getJSONObject("data").getString("day1"));
				day2.setText(jsonObject.getJSONObject("data").getString("day2"));
				day3.setText(jsonObject.getJSONObject("data").getString("day3"));
			}else if(jsonObject.getInt("ret")==-7){
				JunctionHttp.init();
				JSONObject jsonObject_again = JunctionHttp.getCoachList(Junction.hid);
				if (jsonObject_again.getInt("ret") == 0) {
					startStation.setText(jsonObject_again.getJSONObject("data")
							.getString("start"));
					day1.setText(jsonObject_again.getJSONObject("data").getString("day1"));
					day2.setText(jsonObject_again.getJSONObject("data").getString("day2"));
					day3.setText(jsonObject_again.getJSONObject("data").getString("day3"));
			}
		}
		else {
			Toast.makeText(getApplicationContext(), "网络状况不佳，请稍后再试", Toast.LENGTH_LONG).show();
			this.finish(); 
		}
		}catch (Exception e) {
			linearLayout.removeAllViews();
			Toast.makeText(getApplicationContext(), R.string.neterror,
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}

	}

	private void initView() {
		topBar = (TopBar) findViewById(R.id.topbar_coach_search);
		topBar.setLeftButton(R.drawable.fanhui);
		topBar.setTitle("长途汽车");
		topBar.setTopBarClickListener(this);

		startStation = (TextView) findViewById(R.id.coach_search_start);
		stopStation = (TextView) findViewById(R.id.coach_search_end);
		stopStation.setOnClickListener(this);

		day1 = (TextView) findViewById(R.id.coach_search_day1);
		day2 = (TextView) findViewById(R.id.coach_search_day2);
		day3 = (TextView) findViewById(R.id.coach_search_day3);
		day1.setOnClickListener(this);
		day2.setOnClickListener(this);
		day3.setOnClickListener(this);

		searchButton = (Button) findViewById(R.id.coach_search_search);
		searchButton.setOnClickListener(this);

		locationlayout = (RelativeLayout)findViewById(R.id.coach_location);
		locationlayout .setOnClickListener(this);
		linearLayout = (LinearLayout)findViewById(R.id.coach_body);
	}

	@Override
	public void leftBtnClick() {
		topBar.setLeftButton(R.drawable.fanhui_click);
		this.finish();

	}

	@Override
	public void rightBtnClick() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.coach_search_end:
			Intent intent_endIntent = new Intent(CoachSearchActivity.this,CoachListActivity.class);
			startActivityForResult(intent_endIntent, 1);
			break;
		case R.id.coach_search_day1:
			day1.setBackgroundColor(0xff6bc5e8);
			day1.setTextColor(0xffffffff);
			day2.setBackgroundColor(0xffffffff);
			day2.setTextColor(0xff8e8e8e);
			day3.setBackgroundColor(0xffffffff);
			day3.setTextColor(0xff8e8e8e);
			currentDay=1;
			break;
		case R.id.coach_search_day2:
			day1.setBackgroundColor(0xffffffff);
			day1.setTextColor(0xff8e8e8e);
			day2.setBackgroundColor(0xff6bc5e8);
			day2.setTextColor(0xffffffff);
			day3.setBackgroundColor(0xffffffff);
			day3.setTextColor(0xff8e8e8e);
			currentDay=2;
			break;
		case R.id.coach_search_day3:
			day1.setBackgroundColor(0xffffffff);
			day1.setTextColor(0xff8e8e8e);
			day2.setBackgroundColor(0xffffffff);
			day2.setTextColor(0xff8e8e8e);
			day3.setBackgroundColor(0xff6bc5e8);
			day3.setTextColor(0xffffffff);
			currentDay=3;
			break;
		case R.id.coach_search_search:
			if(stopStation.getText().equals(""))
				Toast.makeText(getApplicationContext(), "请先选择有效终点", Toast.LENGTH_SHORT).show();
			else {
				Intent searchIntent = new Intent(CoachSearchActivity.this,CoachInfoActivity.class);
			//searchIntent.putExtra("start", startStation.getText());
			searchIntent.putExtra("end", stopStation.getText());
			searchIntent.putExtra("currentDay", currentDay);
			startActivity(searchIntent);
			}
			
			break;
		case R.id.coach_location:
			Intent intent_indoormap= new Intent(CoachSearchActivity.this, IndoorMap2.class);
			startActivity(intent_indoormap);
			Intent intent_toilet =new Intent(CoachSearchActivity.this,SearchActivity.class);
			intent_toilet.putExtra("type", 2);
			startActivity(intent_toilet);
			break;
		default:
			break;
		}

	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case 1:
			stopStation.setText(data.getExtras().getString("end"));
			break;

		default:
			break;
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}


	

}
