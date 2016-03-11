package nlsde.junction.home.indoormap;

import nlsde.junction.R;
import nlsde.junction.home.function.IndoorMap2;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

public class NavigateActivivy extends Activity implements 
		OnClickListener {
	private Button exchangeButton,search,back;
	private TextView startTextView, stopTextView;
	private ListView historyListView;
	private Intent intent_choosepoint, intent_point;
	private PoiPoint startPoint, stopPoint, mylocation;
	private final static int NAVIGATE_TYPE_NONE = 0;
	private final static int NAVIGATE_TYPE_Start = 1;
	private final static int NAVIGATE_TYPE_Stop = 2;
	private final static int NAVIGATE_TYPE_ALL = 3;
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
		setContentView(R.layout.indoormap_navigate);
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		initView();
	}

	private void initView() {
		startPoint = new PoiPoint();
		stopPoint = new PoiPoint();
		mylocation = new PoiPoint();
		intent_choosepoint = new Intent(NavigateActivivy.this,
				PointChooseActivivy.class);
		intent_point = getIntent();
		search = (Button)findViewById(R.id.indoormap2_navigate_search);
		search.setOnClickListener(this);
		back = (Button)findViewById(R.id.indoormap2_navigate_back);
		back.setOnClickListener(this);
		exchangeButton = (Button) findViewById(R.id.indoormap_navigate_exchange);
		exchangeButton.setOnClickListener(this);
		startTextView = (TextView) findViewById(R.id.indoormap_naviaget_mystart);
		startTextView.setOnClickListener(this);
		stopTextView = (TextView) findViewById(R.id.indoormap_naviaget_myend);
		stopTextView.setOnClickListener(this);
		historyListView = (ListView) findViewById(R.id.indoormap_naviaget_history);
		intent_point = getIntent();
		Load(intent_point);
	}

	/**
	 * @param intent_point2
	 */
	private void Load(Intent intent_point2) {
		switch (intent_point2.getExtras().getInt("navigatetype")) {
		case NAVIGATE_TYPE_NONE:

			break;
		case NAVIGATE_TYPE_Start:
			startPoint = (PoiPoint) intent_point2.getSerializableExtra("start");
			startTextView.setText(startPoint.getCaption());
			break;
		case NAVIGATE_TYPE_Stop:
			stopPoint = (PoiPoint) intent_point2.getSerializableExtra("stop");
			stopTextView.setText(stopPoint.getCaption());
			break;
		case NAVIGATE_TYPE_ALL:
			startPoint = (PoiPoint) intent_point2.getSerializableExtra("start");
			stopPoint = (PoiPoint) intent_point2.getSerializableExtra("stop");
			if (startPoint.getCaption() != null)
				startTextView.setText(startPoint.getCaption());
			if (stopPoint.getCaption() != null)
				stopTextView.setText(stopPoint.getCaption());
			break;
		default:
			break;
		}

	}



	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.indoormap_naviaget_mystart:
			intent_choosepoint.putExtra("choosepoint_type", 1);// 1起点
			if(mylocation.getCaption()==null){
			intent_choosepoint.putExtra("mylocation", false);}
			else {
				intent_choosepoint.putExtra("mylocation", true);
			}// 1起点
			Bundle bundle_start = new Bundle();
			bundle_start.putSerializable("start", startPoint);
			bundle_start.putSerializable("stop", stopPoint);
			
			intent_choosepoint.putExtras(bundle_start);
			startActivityForResult(intent_choosepoint, 1);

			break;
		case R.id.indoormap_naviaget_myend:
			intent_choosepoint.putExtra("choosepoint_type", 2);// 2终点
			if(mylocation.getCaption()==null){
				intent_choosepoint.putExtra("mylocation", false);}
				else {
					intent_choosepoint.putExtra("mylocation", true);
				}
			Bundle bundle_end = new Bundle();
			bundle_end.putSerializable("start", startPoint);
			bundle_end.putSerializable("stop", stopPoint);
			
			intent_choosepoint.putExtras(bundle_end);
			startActivityForResult(intent_choosepoint, 2);
			break;
		case R.id.indoormap_navigate_exchange:
			if (startPoint.getCaption() != null
					&& stopPoint.getCaption() != null) {
				PoiPoint point = null;
				point = startPoint;
				startPoint = stopPoint;
				stopPoint = point;
				startTextView.setText(startPoint.getCaption());
				stopTextView.setText(stopPoint.getCaption());
			} else if (startPoint.getCaption() != null) {
				stopPoint = startPoint;
				startPoint = new PoiPoint();
				startTextView.setText("选择起点");
				stopTextView.setText(stopPoint.getCaption());
			} else if (stopPoint.getCaption() != null) {
				startPoint = stopPoint;
				stopPoint = new PoiPoint();
				stopTextView.setText("选择终点");
				startTextView.setText(startPoint.getCaption());
				
				
			}

			break;
		case R.id.indoormap2_navigate_back:
		    setResult(4);
			this.finish();
			break;
		case R.id.indoormap2_navigate_search:
			String startString = startPoint.toString();
			String stopString = stopPoint.toString();
			//System.out.println(startString.equals(stopString));
			if (startPoint.getFloorId() == null || stopPoint.getFloorId() == null) {
				Toast.makeText(getApplicationContext(), "请先选择正确的起点与终点",
						Toast.LENGTH_LONG).show();
			} else if (startPoint.toString().equals(stopPoint.toString())) {
				Toast.makeText(getApplicationContext(), "无法对同一个POI进行路径规划",
						Toast.LENGTH_LONG).show();
//			}
//				else if(!startPoint.getMapName().equals(stopPoint.getMapName())){
//				Toast.makeText(getApplicationContext(), "尚未收录跨楼层路线，敬请期待！", Toast.LENGTH_SHORT).show()	;
			}
					else {
				Intent intent = new Intent(NavigateActivivy.this, IndoorMap2.class);
				intent.putExtra("choosepoint_type", 5);
				Bundle bundle = new Bundle();
				bundle.putSerializable("start", startPoint);
				bundle.putSerializable("stop", stopPoint);
				
				intent.putExtras(bundle);
				startActivity(intent);
				this.finish();

			}
			break;
		default:
			break;
		}

	}

	/*
	 * 
	 * 0 none 1start 2end 3 startmylocation 4 end mylocation
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) {
		case 0:

			break;
		case 1:
			startPoint.setCaption(data.getExtras().getString("caption")+" "+data.getExtras().getString("info"));
			startPoint.setClat(data.getExtras().getString("clat"));
			startPoint.setClon(data.getExtras().getString("clon"));
			startPoint.setFloorId(data.getExtras().getString("floorId"));
			startPoint.setMapName(data.getExtras().getString("mapName"));
			startTextView.setText(data.getExtras().getString("caption")+" "+data.getExtras().getString("info"));
			break;
		case 2:
			stopPoint.setCaption(data.getExtras().getString("caption")+" "+data.getExtras().getString("info"));
			stopPoint.setClat(data.getExtras().getString("clat"));
			stopPoint.setClon(data.getExtras().getString("clon"));
			stopPoint.setFloorId(data.getExtras().getString("floorId"));
			stopPoint.setMapName(data.getExtras().getString("mapName"));
			stopTextView.setText(data.getExtras().getString("caption")+" "+data.getExtras().getString("info"));
			break;
		case 3:
			startPoint = mylocation;
			startTextView.setText(R.string.wodeweizhi);
			break;
		case 4:
			stopPoint = mylocation;
			stopTextView.setText(R.string.wodeweizhi);
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
