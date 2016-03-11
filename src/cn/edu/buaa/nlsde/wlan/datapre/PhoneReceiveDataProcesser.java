package cn.edu.buaa.nlsde.wlan.datapre;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import cn.edu.buaa.nlsde.wlan.beans.PhoneWifiMessage;
import cn.edu.buaa.nlsde.wlan.beans.WifiMessage;
import cn.edu.buaa.nlsde.wlan.init.config.ParaConfig;
import cn.edu.buaa.nlsde.wlan.processer.WlanData;
import cn.edu.buaa.nlsde.wlan.util.WifiMathUtil;

public class PhoneReceiveDataProcesser extends ReceiveDataProcesser {
	private ArrayList<PhoneWifiMessage> rec_phone_buff_datas;

	public PhoneReceiveDataProcesser() {
		rec_phone_buff_datas = new ArrayList<PhoneWifiMessage>();
	}

	@Override
	public void dataStoreProcess(WifiMessage wfmsg) {
		PhoneWifiMessage msg = (PhoneWifiMessage) wfmsg;
		rec_phone_buff_datas = WlanData.getRecPhoneBuffDatasCopy();
		WlanData.add2UpdatePhoneMacs(msg.getPhoneMac());
		WifiMathUtil.insertSortScanT(rec_phone_buff_datas, msg, ParaConfig.getInstance().getBuff_time());
		WlanData.setRecPhoneBuffDatas(rec_phone_buff_datas);
	//	Date time = rec_phone_buff_datas.get(rec_phone_buff_datas.size() - 1).getScanTime();
	}

	public void dataPrepare(WlanData wd) {
		rec_phone_buff_datas = WlanData.getRecPhoneBuffDatasCopy();
		HashMap<String, HashMap<String, ArrayList<PhoneWifiMessage>>> sorted = classifyData(
				WlanData.getUpdatePhoneMacs(), ParaConfig.getInstance().getBuff_size());
		HashMap<String, HashMap<Integer, ArrayList<PhoneWifiMessage>>> sorted_data_map = calcPriorityMap(sorted);
		wd.setPhonePropertyMap(sorted_data_map);
	}

	private HashMap<String, HashMap<String, ArrayList<PhoneWifiMessage>>> classifyData(HashSet<String> updateMacs,
			int buff_size) {
		HashMap<String, HashMap<String, ArrayList<PhoneWifiMessage>>> sorted = new HashMap<String, HashMap<String, ArrayList<PhoneWifiMessage>>>();
		for (int i = 0; i < rec_phone_buff_datas.size(); i++) {
			PhoneWifiMessage msg = rec_phone_buff_datas.get(i);
			String dev_mac = msg.getPhoneMac();
			if (!updateMacs.contains(dev_mac)) {
				continue;
			}
			String ap_mac = msg.getBssid();

			if (sorted.containsKey(dev_mac)) {
				HashMap<String, ArrayList<PhoneWifiMessage>> apdata_map = sorted.get(dev_mac);
				if (apdata_map.containsKey(ap_mac)) {
					ArrayList<PhoneWifiMessage> rssis = apdata_map.get(ap_mac);
					rssis.add(msg);
					if (rssis.size() > buff_size)
						rssis.remove(0);
				} else {
					ArrayList<PhoneWifiMessage> rssis = new ArrayList<PhoneWifiMessage>();
					rssis.add(msg);
					apdata_map.put(ap_mac, rssis);
				}
			} else {
				HashMap<String, ArrayList<PhoneWifiMessage>> apdata_map = new HashMap<String, ArrayList<PhoneWifiMessage>>();
				ArrayList<PhoneWifiMessage> rssis = new ArrayList<PhoneWifiMessage>();
				rssis.add(msg);
				apdata_map.put(ap_mac, rssis);
				sorted.put(dev_mac, apdata_map);
			}
		}
		return sorted;
	}

	private HashMap<String, HashMap<Integer, ArrayList<PhoneWifiMessage>>> calcPriorityMap(
			HashMap<String, HashMap<String, ArrayList<PhoneWifiMessage>>> sorted) {
		HashMap<String, HashMap<Integer, ArrayList<PhoneWifiMessage>>> ret = new HashMap<String, HashMap<Integer, ArrayList<PhoneWifiMessage>>>();

		for (Map.Entry<String, HashMap<String, ArrayList<PhoneWifiMessage>>> dev_entry : sorted.entrySet()) {
			String dev_mac = dev_entry.getKey();
			HashMap<String, ArrayList<PhoneWifiMessage>> ap_map = dev_entry.getValue();

			HashMap<Integer, ArrayList<PhoneWifiMessage>> promap = new HashMap<Integer, ArrayList<PhoneWifiMessage>>();
			for (Map.Entry<String, ArrayList<PhoneWifiMessage>> ap_entry : ap_map.entrySet()) {
				String ap_mac = ap_entry.getKey();
				ArrayList<PhoneWifiMessage> rssis = ap_entry.getValue();
				PhoneWifiMessage msg = rssis.get(rssis.size() - 1);
				int property = checkProperty(rssis);

				if (promap.containsKey(property)) {
					ArrayList<PhoneWifiMessage> prolist = promap.get(property);
					prolist.add(msg);
				} else {
					ArrayList<PhoneWifiMessage> prolist = new ArrayList<PhoneWifiMessage>();
					prolist.add(msg);
					promap.put(property, prolist);
				}
			}
			ret.put(dev_mac, promap);
		}

		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, HashMap<Integer, ArrayList<PhoneWifiMessage>>> dev_entry : ret.entrySet()) {
			String dev_mac = dev_entry.getKey();
			sb.append("dev_mac:").append(dev_mac).append(",");
			HashMap<Integer, ArrayList<PhoneWifiMessage>> pro_map = dev_entry.getValue();
			for (Map.Entry<Integer, ArrayList<PhoneWifiMessage>> pro_entry : pro_map.entrySet()) {
				int property = pro_entry.getKey();
				ArrayList<PhoneWifiMessage> list = pro_map.get(property);
				sb.append(property).append(":").append(list.size()).append(";");
			}
		}
		// LogUtil.info(sb.toString());
		return ret;
	}

	private int checkProperty(ArrayList<PhoneWifiMessage> rssis) {
		int combo_type = 1;
		PhoneWifiMessage newest = rssis.get(rssis.size() - 1);
		if (newest.getRssi() < -93 || newest.getRssi() > -20)
			combo_type = combo(combo_type, 7);
		if (rssis.size() >= 2) {
			PhoneWifiMessage elder_newest = rssis.get(rssis.size() - 2);
			if (elder_newest.getChannel() != newest.getChannel())
				combo_type = combo(combo_type, 5);
			else
				combo_type = combo(combo_type, 1);
		} else {
			// TODO
			// combo_type = combo(combo_type, 3);
		}
		return combo_type;
	}

	private int combo(int combo_type, int i) {
		if (combo_type == 1)
			return i;
		else
			return 6;
	}

	public void resetPhoneRecBuffDatas() {
		rec_phone_buff_datas = new ArrayList<PhoneWifiMessage>();
	}

}
