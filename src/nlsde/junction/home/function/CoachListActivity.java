package nlsde.junction.home.function;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import nlsde.junction.R;
import nlsde.junction.home.Junction;
import nlsde.junction.net.JunctionHttp;
import nlsde.junction.searchbar.BusSearch;
import nlsde.junction.searchbar.ListViewLoadMore;
import nlsde.junction.searchbar.ListViewLoadMore.IsLoadingListener;
import nlsde.junction.topbar.TopBar;
import nlsde.junction.topbar.TopBarClickListener;
import android.R.anim;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class CoachListActivity extends Activity implements TopBarClickListener,IsLoadingListener{

	private EditText search_key;
	private ListViewLoadMore listViewLoadMore;
	private Button cancel;
	private TopBar topBar;
	private ArrayList<HashMap<String, Object>>   listItems; 
	private SimpleAdapter listItemAdapter;  
	private int page =1;
	private JSONArray	jsonArray;
	private String key;
	private Handler handler=new Handler();
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
		setContentView(R.layout.coach_list);
		initView();
		PushAgent.getInstance(getApplicationContext()).onAppStart();
	}
	private void initView() {
		topBar = (TopBar)findViewById(R.id.topbar_coach_list);
		topBar.setTopBarClickListener(this);
		topBar.setLeftButton(R.drawable.fanhui);
		topBar.setTitle("车站选择");
		
		listViewLoadMore = (ListViewLoadMore)findViewById(R.id.coach_list_list);
	       //生成适配器的Item和动态数组对应的元素  	
		listItems = new ArrayList<HashMap<String, Object>>(); 
        listItemAdapter = new SimpleAdapter(this,listItems,   // listItems数据源    
              R.layout.coach_list_item ,  //ListItem的XML布局实现   
                new String[] {"caption"},     //动态数组与ImageItem对应的子项          
                new int[ ] {R.id.coach_list_name}      //list_item.xml布局文件里面的一个ImageView的ID,一个TextView 的ID   
        );   
		listViewLoadMore.setAdapter(listItemAdapter);
		listViewLoadMore.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
			
				 	Intent data=new Intent(CoachListActivity.this,CoachSearchActivity.class);  
		            data.putExtra("end",listItems.get(arg2).get("name")+"");   
		            setResult(1, data);  
		            //关闭掉这个Activity  
		            finish();  
			}
		});
		listViewLoadMore.setOnLoadingListener(this);
		search_key = (EditText)findViewById(R.id.coach_list_key);
		cancel = (Button)findViewById(R.id.coach_list_cancel);
		cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				search_key.setText("");
				
			}
		});
		TextWatcher mTextWatcher = new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {
				try {
					page=1;
					key = arg0.toString();
					loadList();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			
			}  
	       
	    };  
	    search_key.addTextChangedListener(mTextWatcher);
	    try {
	    	page=1;
	    	key="";
			loadList();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private JSONObject jsonObject;
	protected void loadList() throws Exception {
		 jsonObject= JunctionHttp.getCoachState(Junction.hid,key, page);
		if(jsonObject.getInt("ret")==-7){
			JunctionHttp.init();
			jsonObject = JunctionHttp.getCoachState(Junction.hid,key, page);
		}else if(jsonObject.getInt("ret")==-3) {
			Toast.makeText(getApplicationContext(), "没有更多结果了", Toast.LENGTH_SHORT).show();;

			//Toast.makeText(getApplicationContext(), "无查询结果", Toast.LENGTH_SHORT).show();;
		}else {
			jsonArray = jsonObject.getJSONObject("data").getJSONArray("list");
			if (jsonArray.length() > 0) {
				if(page==1){
					listItems.clear();
					page++;
				}
				
				page++;
				
				for (int i = 0; i < jsonArray.length(); i++) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("caption", jsonArray.getJSONObject(i).getString("caption"));
					map.put("name", jsonArray.getJSONObject(i).getString("name"));
					listItems.add(map);
				}
				
				listItemAdapter.notifyDataSetChanged();
			}
		}
		
		
		
	}
		

	@Override
	public void leftBtnClick() {
		topBar.setLeftButton(R.drawable.fanhui_click);
		finish();
		
	}
	@Override
	public void rightBtnClick() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onLoad() {
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {

				try {
					loadList();
					
				} catch (Exception e) {
					Toast.makeText(CoachListActivity.this, R.string.neterror, Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
				listItemAdapter.notifyDataSetChanged();
				listViewLoadMore.complateLoad();
			}
		}, 1000);
		
		
	}

}
