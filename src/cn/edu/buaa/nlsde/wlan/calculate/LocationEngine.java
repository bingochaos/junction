/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.buaa.nlsde.wlan.calculate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;
import cn.edu.buaa.nlsde.wlan.algorithm.AlgorithmVar;
import cn.edu.buaa.nlsde.wlan.algorithm.IAlgorithm;
import cn.edu.buaa.nlsde.wlan.beans.LocationInfo;
import cn.edu.buaa.nlsde.wlan.beans.PhoneWifiMessage;
import cn.edu.buaa.nlsde.wlan.processer.WlanData;

/**
 * 定位引擎
 * 
 * @author lawson
 */
public class LocationEngine {

	// private WlanData wd;
	private String TAG = this.getClass().getSimpleName();

	// 算法适配
	private IAlgorithm algo = new AlgorithmVar();

	// public LocationEngine(WlanData wd) {
	// this.wd = wd;
	// }

	public List<LocationInfo> findPhoneLocation0(WlanData wd) throws Exception {
		List<LocationInfo> ret_list = new ArrayList<LocationInfo>();
		HashMap<String, HashMap<Integer, ArrayList<PhoneWifiMessage>>> property_map = wd.getPhonePropertyMap();
		for (Map.Entry<String, HashMap<Integer, ArrayList<PhoneWifiMessage>>> dev_entry : property_map.entrySet()) {
			String dev_mac = dev_entry.getKey();
			HashMap<Integer, ArrayList<PhoneWifiMessage>> promap = property_map.get(dev_mac);
			List<PhoneWifiMessage> msg_list = new ArrayList<PhoneWifiMessage>();
			Object[] properties = promap.keySet().toArray();
			Arrays.sort(properties);

			for (int i = 0; i < properties.length; i++) {
				int property = (Integer) properties[i];
				if (property > 3)
					break;
				ArrayList<PhoneWifiMessage> msgs = promap.get(property);
				msg_list.addAll(msgs);
			}

			LocationInfo info = algo.getLocation(msg_list);
			if (info != null && info.getPlaceList() != null) {
				long now = new Date().getTime();
				long old = info.getServTime().getTime();
				ret_list.add(info);
				Log.i(TAG, "calc_time: " + (now - old) + " ms, Mac=" + info.getDevMac());
			} else {
				// LogUtil.info("No location result.Mac=" + info.getDevMac());
			}
		}
		return ret_list;
	}

	public List<LocationInfo> findPhoneLocation(List<PhoneWifiMessage> msg_list) throws Exception {
		List<LocationInfo> ret_list = new ArrayList<LocationInfo>();
		LocationInfo info = algo.getLocation(msg_list);
		if (info != null && info.getPlaceList() != null) {
			long now = new Date().getTime();
			long old = info.getServTime().getTime();
			ret_list.add(info);
			Log.i(TAG, "calc_time: " + (now - old) + " ms, Mac=" + info.getDevMac());
		} 
//		else {
//			 LogUtil.info("No location result.Mac=" + info.getDevMac());
//		}
		return ret_list;
	}

}
