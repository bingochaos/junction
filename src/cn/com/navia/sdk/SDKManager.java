package cn.com.navia.sdk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Messenger;
import android.os.RemoteException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Handler;

import cn.com.navia.sdk.locater.services.Action;
import cn.com.navia.sdk.locater.services.LocaterService;
import cn.com.navia.sdk.utils.LocaterUtil;

public class SDKManager implements ISDKManager {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public static final String LOCATER_BUILDINGID = "buildingId";
	public static final String LOCATER_ACTION = "LOCATER_ACTION";


	public static final int DOWNLOAD_NORMAL = 0;
	public static final int DOWNLOAD_FORCE = 1;
	
	public static final String INTENT_EXTRA_MESSENGER = "messenger";
	

	private static SDKManager sdkManager;
	private Intent locaterServiceIntent;
	private Messenger cmdMsger0;

	private int serviceStatus;
	private int locaterStatus;
	private Context context;

	public static ISDKManager getInstance(Context context, String sdkServer, Handler handler) {
		if (sdkManager != null) {
			sdkManager.destroy();
			sdkManager = null;
		}
        sdkManager = new SDKManager();
        sdkManager.context = context;
        sdkManager.locaterServiceIntent = new Intent(context, LocaterService.class);
        sdkManager.locaterServiceIntent.putExtra(INTENT_EXTRA_MESSENGER, new Messenger(handler));

		return sdkManager;
	}

	@Override
	public boolean init( ) {

		
		boolean bindService = context.bindService(locaterServiceIntent, new ServiceConnection() {
			@Override
			public void onServiceDisconnected(ComponentName name) {
				logger.info("ComponentName {}", name);
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				logger.info("onServiceConnected {}", name);
				cmdMsger0 = new Messenger(service);
				LocaterUtil.send2Msg(cmdMsger0, Action.INIT.getId() , 0);
			}
		}, Context.BIND_AUTO_CREATE);

		return bindService;
	}

	@Override
	public boolean startLocater(int buildingId) {
		return LocaterUtil.send2Msg(cmdMsger0, Action.START.getId(), buildingId);
	}

	@Override
	public void stopLocater() {
		LocaterUtil.send2Msg(cmdMsger0, Action.STOP.getId(), 1);
	}

	@Override
	public boolean destroy() {
		stopLocater();
		destoryLocater();

//		context.unbindService(new ServiceConnection() {
//			@Override
//			public void onServiceDisconnected(ComponentName name) {
//				logger.info("onServiceDisconnected:{}", name);
//			}
//
//			@Override
//			public void onServiceConnected(ComponentName name, IBinder service) {
//				logger.info("onServiceConnected:{}", name);
//			}
//		});
		return true;
	}

	private void destoryLocater() {
		LocaterUtil.send2Msg(cmdMsger0, Action.DESTROY.getId(), 1);
	}

	@Override
	public void loadLocalUpdates() throws RemoteException {
		LocaterUtil.send2Msg(cmdMsger0, Action.LOAD_LOCAL_SPECS.getId(), 1);
	}

	@Override
	public void showLatestUpdates(int flag) throws RemoteException {
		LocaterUtil.send2Msg(cmdMsger0, Action.SHOW_LATEST_SPECS.getId(), flag);
	}

	@Override
	public void downSpectrum(int buildingId, int ver) throws RemoteException {
		LocaterUtil.send2Msg(cmdMsger0, Action.DOWN_SPECT.getId(), new int[]{buildingId, ver});
	}

}
