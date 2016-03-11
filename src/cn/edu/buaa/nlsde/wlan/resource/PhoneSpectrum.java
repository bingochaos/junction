package cn.edu.buaa.nlsde.wlan.resource;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.edu.buaa.nlsde.wlan.util.FileUtil;

public class PhoneSpectrum extends Spectrum {
	// key1:mapid,key2:positionid#ap_mac,value:spectrumitem
	public static HashMap<String, HashMap<String, SpectrumItem>> wifi_map = new HashMap<String, HashMap<String, SpectrumItem>>();
	// key:ap_mac,value:该ap在各个位置点的信号强度
	public static HashMap<String, ArrayList<SpectrumItem>> wifi_search_map = new HashMap<String, ArrayList<SpectrumItem>>();// wifi索引,包含的位置list
	public static HashMap<String, Set<String>> choosen_ap = new HashMap<String, Set<String>>();// 每个位置点表现较好的AP
	public static ArrayList<String> in_array;
	public static Set<String> ap_macs = new HashSet<String>();

	/**
	 * Creates a new instance of WifiLibrary
	 */
	public PhoneSpectrum() {
	}

	/**
<<<<<<< HEAD
	 * 妫�祴鏄惁鏄噸杞介璋�
=======
	 * 检测是否是重载频谱
>>>>>>> 3625bfae963f7a1697264e64c6bd37a46ec585d4
	 *
	 * @return 如果原先存在频谱数据，返回true,否则返回false
	 */
	public static boolean isReload() {
		if (wifi_map.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 清空频谱数据
	 */
	public static void resetSpectrum() {
		wifi_map = new HashMap<String, HashMap<String, SpectrumItem>>();
		wifi_search_map = new HashMap<String, ArrayList<SpectrumItem>>();// wifi索引,包含的位置list
		choosen_ap = new HashMap<String, Set<String>>();// 每个位置点表现较好的AP
	}
	
	
	public static boolean addSpectrum(File file) throws IOException {
		List<String> in_array = FileUtil.readFile(file);

		for (int i = 1; i < in_array.size(); i++) {
			String line = in_array.get(i);
			if(line == null || line.trim().length() < 1){
				continue;
			}
			String[] items = line.split(",");
			String posi_id = items[0].trim();
			String map_id = items[1].trim();
			int direction = Integer.parseInt(items[2].trim());
			String mac = items[3].trim().toUpperCase();
			int mode = Integer.parseInt(items[4].trim());
			int avg = Integer.parseInt(items[5].trim());
			int median = Integer.parseInt(items[6].trim());
			float std = Float.parseFloat(items[7].trim());
			int rank = Integer.parseInt(items[8].trim());

			SpectrumItem wli = new SpectrumItem();
			wli.setPosiID(posi_id);
			wli.setMapID(map_id);
			wli.setDirection(direction);
			wli.setAP_MAC(mac);
			ap_macs.add(mac);
			wli.setRSSIMean(avg);
			wli.setRSSIMedian(median);
			wli.setRSSIMode(mode);
			wli.setRSSIStd(std);
			wli.setRank(rank);

			updateWifiMap(wli);
			updateSearchMap(wli);

			Set<String> mac_list;
			if (choosen_ap.containsKey(wli.getPosiID() + "#" + wli.getMapID())) {
				mac_list = choosen_ap.get(wli.getPosiID() + "#" + wli.getMapID());
			} else {
				mac_list = new HashSet<String>();
			}
			mac_list.add(mac);
			choosen_ap.put(wli.getPosiID() + "#" + wli.getMapID(), mac_list);

			// posi_floor_pair.put(posi_id, map_id);
		}
		in_array = null;// 释放内存
		return true;
	}

	private static void updateWifiMap(SpectrumItem wli) {
		HashMap<String, SpectrumItem> in_area_map;
		if (wifi_map.containsKey(wli.getMapID())) {
			in_area_map = wifi_map.get(wli.getMapID());
		} else {
			in_area_map = new HashMap<String, SpectrumItem>();
		}
		String posi_ap = wli.getPosiID() + "#" + wli.getAP_MAC();
		in_area_map.put(posi_ap, wli);
		wifi_map.put(wli.getMapID(), in_area_map);
	}

	private static void updateSearchMap(SpectrumItem wli) {
		ArrayList<SpectrumItem> place_list;
		if (wifi_search_map.containsKey(wli.getAP_MAC())) {
			place_list = wifi_search_map.get(wli.getAP_MAC());
		} else {
			place_list = new ArrayList<SpectrumItem>();
		}
		place_list.add(wli);
		wifi_search_map.put(wli.getAP_MAC(), place_list);
	}

	
}
