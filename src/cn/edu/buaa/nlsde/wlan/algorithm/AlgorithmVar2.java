package cn.edu.buaa.nlsde.wlan.algorithm;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.buaa.nlsde.wlan.beans.LocationInfo;
import cn.edu.buaa.nlsde.wlan.beans.PhoneWifiMessage;
import cn.edu.buaa.nlsde.wlan.offline.OffLineLocater;
import cn.edu.buaa.nlsde.wlan.resource.LocationTable;
import cn.edu.buaa.nlsde.wlan.resource.SpectrumItem;

public class AlgorithmVar2 {
	private static final int range = 5;
	public String locationID;
	public double locationVAR;
	public AlgorithmVar2() {
		locationID = null;
		locationVAR = 0;
	}
	
	public static boolean total_match(List<PhoneWifiMessage> to_be_judged_ap,
			ArrayList<SpectrumItem> wifi_search_map,List<PhoneWifiMessage> matched_ap) {
		boolean res = true;
		int size = wifi_search_map.size();
		int count = 0;
		for(int i=0;i<size;i++) {
			String mac = wifi_search_map.get(i).getAP_MAC();
			double rssi = wifi_search_map.get(i).getRSSIMean();
			for(int j=0;j<to_be_judged_ap.size();j++) {
				PhoneWifiMessage message = to_be_judged_ap.get(j);
				if(message.getBssid().equals(mac)) {
					if(Math.abs(message.getRssi()-rssi)<=range) {
						matched_ap.add(message);
						count++;
					}
				}
			}
		}
		if(count!=to_be_judged_ap.size()) {
			res = false;
		}
		return res;
	}	
	public LocationInfo getLocation(List<PhoneWifiMessage> to_be_judged_ap) throws Exception {
		OffLineLocater.appendLog(to_be_judged_ap.toString());
		SpectrumTransform sigleton = SpectrumTransform.getInstance();
		HashMap<String, ArrayList<SpectrumItem>> spectrum = sigleton.spectrum;
		Unit2 unit = new Unit2(to_be_judged_ap,spectrum);
		String location = unit.location;
		if(location == null) {
			return null;
		}
		HashMap<String, PhoneWifiMessage> apdata_map = new HashMap<String, PhoneWifiMessage>();
		for (int m = 0; m < to_be_judged_ap.size(); m++) {
			apdata_map.put(to_be_judged_ap.get(m).getBssid(), to_be_judged_ap.get(m));
		}
		LocationInfo info = initLocation(apdata_map);
		String dev_mac = to_be_judged_ap.get(0).getPhoneMac();
		info.setDevMac(dev_mac);
		String location_with_mapid = location+"#"+unit.mapID;
		ArrayList<String> list = new ArrayList<String>();
		list.add(location_with_mapid);
		info.setPlaceList(list);
		info.setMapid(Integer.parseInt(unit.mapID));
		info.setX(LocationTable.getPointByLocId(location_with_mapid).getX());
		info.setY(LocationTable.getPointByLocId(location_with_mapid).getY());
		info.setRadius(4);
		/********************自检，重新计算**************************/
		if(to_be_judged_ap.size()>4) {
			List<PhoneWifiMessage> matched_ap = new ArrayList<PhoneWifiMessage>();
			boolean match_tag = total_match(to_be_judged_ap, spectrum.get(location), matched_ap);
			if(!match_tag) {
				if(matched_ap.size()<5) {
					locationID = location_with_mapid;
					locationVAR = unit.var;
					List<String> location_list = info.getPlaceList();
					OffLineLocater.appendLog("location_list:" +location_list.toString());
					return info;
				}
				Unit2 unit2 = new Unit2(matched_ap,spectrum);
				location = unit2.location;
				if(location == null) {
					locationID = location_with_mapid;
					locationVAR = unit.var;
					List<String> location_list = info.getPlaceList();
					OffLineLocater.appendLog("location_list:" +location_list.toString());
					return info;
				}
				else {
					location_with_mapid = location+"#"+unit2.mapID;
					ArrayList<String> list2 = new ArrayList<String>();
					list2.add(location_with_mapid);
					info.setPlaceList(list2);
					info.setMapid(Integer.parseInt(unit2.mapID));
					info.setX(LocationTable.getPointByLocId(location_with_mapid).getX());
					info.setY(LocationTable.getPointByLocId(location_with_mapid).getY());
					info.setRadius(4);
					locationID = location_with_mapid;
					locationVAR = unit2.var;
				}
			}
			else {
				locationID = location_with_mapid;
				locationVAR = unit.var;
			}
		}
		else {
			return null;
		}
		/*******************************************************/	
		List<String> location_list = info.getPlaceList();
		OffLineLocater.appendLog("location_list:" +location_list.toString());
		return info;
	}
	private LocationInfo initLocation(HashMap<String, PhoneWifiMessage> apdata_map) {
		LocationInfo info = new LocationInfo();
		Date serv_t = new Date(0);
		Date ap_t = new Date(0);
		for (Map.Entry<String, PhoneWifiMessage> ap_entry : apdata_map.entrySet()) {
			PhoneWifiMessage msg = ap_entry.getValue();
			ap_t = msg.getScanTime().after(ap_t) ? msg.getScanTime() : ap_t;
			serv_t = msg.getSendTime().after(serv_t) ? msg.getSendTime() : serv_t;
		}
		info.setApTime(ap_t);
		info.setServTime(serv_t);
		return info;
	}
}

class Unit2 {
	private final static double protect_region = -100;
	String location = null;
	double var = 0;
	String mapID = null;
	Unit2(List<PhoneWifiMessage> to_be_judged_ap,HashMap<String, ArrayList<SpectrumItem>> wifi_search_map) {
		double minVar = Double.MAX_VALUE;
		ArrayList<String> ap_mac = new ArrayList<String>();
		ArrayList<Double> ap_rssi = new ArrayList<Double>();
		for(int i=0;i<to_be_judged_ap.size();i++) {
			PhoneWifiMessage tmp_message = to_be_judged_ap.get(i);
			ap_mac.add(tmp_message.getBssid());
			ap_rssi.add((double)tmp_message.getRssi());
		}
		for(Map.Entry<String, ArrayList<SpectrumItem>> entry:wifi_search_map.entrySet()) {
			String key = entry.getKey();
			ArrayList<SpectrumItem> value = new ArrayList<SpectrumItem>(entry.getValue());
			ArrayList<String> total_mac = new ArrayList<String>();
			ArrayList<Double> total_rssi = new ArrayList<Double>();
			String tmp_mapID = value.get(0).getMapID();
			MyType quota = new MyType();
			for(int i=0;i<value.size();i++) {
				SpectrumItem tmp_message = value.get(i);
				total_mac.add(tmp_message.getAP_MAC());
				total_rssi.add((double)tmp_message.getRSSIMean());
			}
			ArrayList<Double> diff = new ArrayList<Double>(computeDiff(ap_mac, ap_rssi, total_mac, total_rssi,quota));
			double tmp = 0;
			if(quota.num_in_spectrum>0&&quota.num_of_mutation>0) {
				tmp = Variance(diff)*(ap_mac.size()/(double)quota.num_in_spectrum)*(quota.num_of_mutation/(double)quota.num_in_spectrum);
			}
			else if(quota.num_in_spectrum>0&&quota.num_of_mutation == 0) {
				tmp = Variance(diff)*(ap_mac.size()/quota.num_in_spectrum);
			}
			else {
				tmp = Double.MAX_VALUE;
			}
			
			int match_size = diff.size();
			/********************设置对ap的个数要求**************************/
//			if(match_size>4&&match_size>(ap_mac.size()-2)) {
			if(match_size>4) {
				if(tmp<minVar) {
					minVar = tmp;
					location = key;
					mapID = tmp_mapID;
				}
			}
			/**********************************************************/
		}
		var = minVar;
	}
	double Variance(ArrayList<Double> diff) {
		double res = 0;
		double mean = 0;
		int size = diff.size();
		for(int i=0;i<size;i++) {
			mean+=diff.get(i);
		}
		mean/=size;
		for(int i=0;i<size;i++) {
			res+=Math.pow(diff.get(i)-mean,2);
		}
		res/=size;
		return res;
	}
	ArrayList<Double> computeDiff(ArrayList<String> ap_mac,ArrayList<Double> ap_rssi,ArrayList<String> total_mac,ArrayList<Double> total_rssi,MyType quota) {
		ArrayList<Double> res = new ArrayList<Double>();
		int size = ap_mac.size();
		for(int i=0;i<size;i++) {
			String mac = ap_mac.get(i);
			double rssi = ap_rssi.get(i);
			if(total_mac.contains(mac)) {
				int index = total_mac.indexOf(mac);
				double tmp_rssi = total_rssi.get(index);
				double diff = tmp_rssi-rssi;
				res.add(diff);
				int tmp = quota.num_in_spectrum;
				quota.num_in_spectrum = tmp+1;
				if(Math.abs(diff)>10) {
					int tmp2 = quota.num_of_mutation;
					quota.num_of_mutation = tmp2+1;
				}
			}
//			else {
////				res.add(protect_region-rssi);
//				int tmp = quota.num;
//				quota.num = tmp+1;
//			}
		}
		return res;
	}
}

class MyType {
	int num_in_spectrum;
	int num_of_mutation;
	MyType() {
		num_in_spectrum = 0;
		num_of_mutation = 0;
	}
}

