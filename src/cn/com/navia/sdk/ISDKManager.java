package cn.com.navia.sdk;

import android.os.RemoteException;

public interface ISDKManager {

	boolean init( );
	boolean destroy();

	boolean startLocater(int buildingId);
	void stopLocater();


	
	
	void downSpectrum(int buildingId, int ver) throws RemoteException;
	void showLatestUpdates(int downFlag) throws RemoteException;
	void loadLocalUpdates() throws RemoteException;

}
