package nlsde.junction.animation;

import org.json.JSONObject;

import com.baidu.mapapi.model.LatLng;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import nlsde.junction.R;
import nlsde.junction.home.Junction;
import nlsde.junction.net.JunctionHttp;
import nlsde.tools.BitmapDecode;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class AnimActivity extends Activity {

	private ImageView imageView;
	private LocationManager locationManager;
	private LocationListener locationListener;
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
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(android.R.style.Theme_Translucent_NoTitleBar);
		setContentView(R.layout.animate);
		PushAgent.getInstance(getApplicationContext()).onAppStart();
		imageView = (ImageView) findViewById(R.id.loading_back);
		BitmapDrawable background = new BitmapDrawable(BitmapDecode.readBitMap(
				getApplicationContext(), R.drawable.locationing));
		imageView.setBackgroundDrawable(background);
		locationListener = new LocationListener() {

			@Override
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderEnabled(String arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProviderDisabled(String arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLocationChanged(Location location) {
				try {
					JSONObject jsonObject = JunctionHttp
							.gethubnearby(new LatLng(location.getLatitude(),
									location.getLongitude()));
					if (jsonObject.getInt("ret") == 0) {
						Log.v("location", jsonObject.getJSONObject("data")
								.toString());
						Junction.hid = jsonObject.getJSONObject("data").getInt(
								"hid");
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		};
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
				0, locationListener);
		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(2000);
					finish();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}).start();
	}

}
