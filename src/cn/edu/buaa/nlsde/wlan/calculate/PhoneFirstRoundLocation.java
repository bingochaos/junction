/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.buaa.nlsde.wlan.calculate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.buaa.nlsde.wlan.beans.LocationInfo;
import cn.edu.buaa.nlsde.wlan.beans.LocationUnionUnit;
import cn.edu.buaa.nlsde.wlan.beans.PhoneWifiMessage;
import cn.edu.buaa.nlsde.wlan.init.config.ParaConfig;
import cn.edu.buaa.nlsde.wlan.resource.LocationTable;
import cn.edu.buaa.nlsde.wlan.resource.PhoneSpectrum;
import cn.edu.buaa.nlsde.wlan.resource.SpectrumItem;

//import cn.edu.buaa.nlsde.wlan.util.LogUtil;

/**
 * 一轮定位，通过SVM划定离散的范围
 * 
 * @author lawson
 */
public class PhoneFirstRoundLocation {

	/**
	 * 获取一轮定位结果
	 * 
	 * @param dev_mac
	 *            设备id
	 * @param apdata_map
	 *            每个device采集到的信号
	 * @return 定位结果
	 * @throws IOException
	 */
	public LocationInfo getPlaceSets(String dev_mac, HashMap<String, PhoneWifiMessage> apdata_map) throws IOException {

		LocationInfo info = initLocation(apdata_map);
		info.setDevMac(dev_mac);

		ArrayList<LocationUnionUnit> places_unit = specMatch(
				apdata_map, 
				PhoneSpectrum.wifi_search_map,
				//XXX getFloatMargin
				ParaConfig.getInstance().getFloat_margin(),
				//XXX getUnion_phone_size
				ParaConfig.getInstance().getUnion_phone_size()
		);

		ArrayList<String> places = new ArrayList<String>();
		for (LocationUnionUnit unit : places_unit) {
			places.add(unit.getClassName());
		}
		if (!places.isEmpty()) {
			info.setPlaceUnits(places_unit);
			info.setPlaceList(places);
		}
		return info;
	}

	/**
	 * 初始化一个定位结果对象，主要为了获取serv_t和ap_t
	 * 
	 * @param apdata_map
	 * @return 初始化的定位结果对象
	 */
	private LocationInfo initLocation(HashMap<String, PhoneWifiMessage> apdata_map) {
		LocationInfo info = new LocationInfo();
		Date serv_t = new Date(0);
		Date ap_t = new Date(0);

		for (Map.Entry<String, PhoneWifiMessage> ap_entry : apdata_map.entrySet()) {
			// String ap_mac = ap_entry.getKey();
			PhoneWifiMessage msg = ap_entry.getValue();
			ap_t = msg.getScanTime().after(ap_t) ? msg.getScanTime() : ap_t;
			serv_t = msg.getSendTime().after(serv_t) ? msg.getSendTime() : serv_t;
		}
		info.setApTime(ap_t);
		info.setServTime(serv_t);
		return info;
	}

	private ArrayList<LocationUnionUnit> specMatch(HashMap<String, PhoneWifiMessage> apdata_map,
			HashMap<String, ArrayList<SpectrumItem>> spec_search_map, float float_range, int union_size) {

		HashMap<String, ArrayList<String>> place_count_s = new HashMap<String, ArrayList<String>>();
		HashMap<String, Integer> place_count = new HashMap<String, Integer>();

		for (Map.Entry<String, PhoneWifiMessage> ap_entry : apdata_map.entrySet()) {
			PhoneWifiMessage msg = ap_entry.getValue();
			String ap_mac = msg.getBssid();
			float rssi = msg.getRssi();
			ArrayList<String> ap_spt_plcs = getSupport(ap_mac, rssi, spec_search_map, float_range);
			place_count_s = getPlaceCount(ap_mac, ap_spt_plcs, place_count_s);
		}

		place_count = countEachPlaceSpt(place_count_s);
		List<Map.Entry<String, Integer>> infoIds = new ArrayList<Map.Entry<String, Integer>>(place_count.entrySet());
		ArrayList<LocationUnionUnit> max_count = getMaxPlaceCount(infoIds, union_size);
		return max_count;
	}

	private ArrayList<String> getSupport(String ap_mac, float rssi, HashMap<String, ArrayList<SpectrumItem>> spec_search_map,
			float float_range) {

		ArrayList<String> spt = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		sb.append("ap_mac=").append(ap_mac).append("=");
		if (spec_search_map.containsKey(ap_mac)) {
			ArrayList<SpectrumItem> items = spec_search_map.get(ap_mac);
			for (SpectrumItem item : items) {
				float place_rssi = item.getRSSIMean();
				if (Math.abs(place_rssi - rssi) <= float_range) {
					spt.add(item.getPosiID() + "#" + item.getMapID());
					String place = LocationTable.getPointByLocId(item.getPosiID() + "#" + item.getMapID()).getName();
					sb.append(place).append(",");
				}
			}

		}
		return spt;
	}

	private HashMap<String, ArrayList<String>> getPlaceCount(String ap_mac, ArrayList<String> ap_spt_plcs,
			HashMap<String, ArrayList<String>> place_count_s) {

		for (String place : ap_spt_plcs) {
			if (place_count_s.containsKey(place)) {
				ArrayList<String> mac_list = place_count_s.get(place);
				mac_list.add(ap_mac);
			} else {
				ArrayList<String> mac_list = new ArrayList<String>();
				mac_list.add(ap_mac);
				place_count_s.put(place, mac_list);
			}
		}
		return place_count_s;
	}

	private HashMap<String, Integer> countEachPlaceSpt(HashMap<String, ArrayList<String>> place_count_s) {
		HashMap<String, Integer> place_count = new HashMap<String, Integer>();
		for (Map.Entry<String, ArrayList<String>> place_entry : place_count_s.entrySet()) {
			String place = place_entry.getKey();
			int size = place_entry.getValue().size();
			place_count.put(place, size);
		}
		return place_count;
	}

	private ArrayList<LocationUnionUnit> getMaxPlaceCount(List<Map.Entry<String, Integer>> infoIds, int union_size) {
		ArrayList<LocationUnionUnit> result = new ArrayList<LocationUnionUnit>();
		Collections.sort(infoIds, new Comparator<Map.Entry<String, Integer>>() {
			@Override
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				return (o2.getValue() - o1.getValue());
			}
		});

		if (infoIds.isEmpty()) {
			return result;
		}
		int max_count = infoIds.get(0).getValue();
		if (max_count < union_size) {
			return result;
		}
		for (int i = 0; i < infoIds.size(); i++) {
			if (infoIds.get(i).getValue() == max_count) {
				LocationUnionUnit unit = new LocationUnionUnit();
				unit.setClassName(infoIds.get(i).getKey());
				unit.setFreq(infoIds.get(i).getValue());
				result.add(unit);
			}
		}
		return result;
	}
}
