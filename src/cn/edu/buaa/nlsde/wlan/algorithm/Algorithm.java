package cn.edu.buaa.nlsde.wlan.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.text.TextUtils;

 

import cn.edu.buaa.nlsde.wlan.beans.LocationInfo;
import cn.edu.buaa.nlsde.wlan.beans.PhoneWifiMessage;
import cn.edu.buaa.nlsde.wlan.calculate.PhoneFirstRoundLocation;
import cn.edu.buaa.nlsde.wlan.cluster.Clustereds;
import cn.edu.buaa.nlsde.wlan.offline.OffLineLocater;
import cn.edu.buaa.nlsde.wlan.resource.LocationTable;

public class Algorithm implements IAlgorithm{
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	private Set<PhoneWifiMessage> correct_ap;
	private Set<PhoneWifiMessage> abnormal_ap;
	private Set<PhoneWifiMessage> uncertain_ap;

	public Algorithm() {
		correct_ap = new HashSet<PhoneWifiMessage>();
		abnormal_ap = new HashSet<PhoneWifiMessage>();
		uncertain_ap = new HashSet<PhoneWifiMessage>();
	}

	public void getCorrectAP(List<PhoneWifiMessage> to_be_judged_ap) throws Exception {
		List<PhoneWifiMessage> judged_ap = new ArrayList<PhoneWifiMessage>();
		Collections.sort(to_be_judged_ap, new Comparator<PhoneWifiMessage>() {
			public int compare(PhoneWifiMessage arg0, PhoneWifiMessage arg1) {
				return (int) (arg1.getRssi() - arg0.getRssi()); // 按照rssi降序排列
			}
		});
		if (to_be_judged_ap.size() < 5) {
			return;
		}
//		for (int i = 0; i < to_be_judged_ap.size(); i++) {
//			logger.info(to_be_judged_ap.get(i).getBssid() + "," + to_be_judged_ap.get(i).getRssi());
//		}
		for (int i = 0; i < 5; i++) {
			judged_ap.add(to_be_judged_ap.get(i));
		}
		to_be_judged_ap.removeAll(judged_ap);

		int round = 0;
		int wrong_count = 0;
		while (round++ < 10 && wrong_count < 3) {
//			for (int i = 0; i < judged_ap.size(); i++) {
//				logger.info(judged_ap.get(i).getBssid() + "," + judged_ap.get(i).getRssi());
//			}
			BasicAlgorithm basic_algo = new BasicAlgorithm(judged_ap);
			APGroupInfo info = basic_algo.getAPGroupInfo();
			if (info.getStatus() != 1) {
				wrong_count++;
				judged_ap.clear();
				if (to_be_judged_ap.size() < 5)
					break;
				for (int i = 0; i < 5; i++) {
					judged_ap.add(to_be_judged_ap.get(i));
				}
				to_be_judged_ap.removeAll(judged_ap);
				continue;
			} else {
				correct_ap.addAll(info.getCorrect_ap());
				abnormal_ap.addAll(info.getAbnormal_ap());
				uncertain_ap.addAll(info.getUncertain_ap());

				boolean rCorrent = judged_ap.removeAll(info.getCorrect_ap());
				boolean rAbnormal = judged_ap.removeAll(info.getAbnormal_ap());
				boolean rUncertain = judged_ap.removeAll(info.getUncertain_ap());

//				logger.info("removeAll CorrentAp:"+info.getCorrect_ap().size()+"=>"+ rCorrent);
//				logger.info("removeAll AbnormalAp:"+info.getAbnormal_ap().size()+"=>"+ rAbnormal);
//				logger.info("removeAll UncertainAp:"+info.getUncertain_ap().size()+"=>"+ rUncertain);
			}
			if (correct_ap.size() >= 4) {
				break;
			}
			if (judged_ap.size() + to_be_judged_ap.size() + uncertain_ap.size() < 5) {
				break;
			} else {
				for (int i = 0; i < to_be_judged_ap.size() && i < 5 - judged_ap.size();) {
					judged_ap.add(to_be_judged_ap.get(i));
					to_be_judged_ap.remove(i);
				}
				List<PhoneWifiMessage> uncertain_ap_list = new ArrayList<PhoneWifiMessage>(uncertain_ap);
				for (int i = 0; i < uncertain_ap_list.size() && i < 5 - judged_ap.size();) {
					judged_ap.add(uncertain_ap_list.get(i));
					uncertain_ap_list.remove(i);
				}
				uncertain_ap = new HashSet<PhoneWifiMessage>(uncertain_ap_list);
			}
		}
	}

	@Override
	public LocationInfo getLocation(List<PhoneWifiMessage> to_be_judged_ap) throws Exception {
		LocationInfo info1 = new LocationInfo();
		
		logger.info("getCorrectAP ...");
		getCorrectAP(to_be_judged_ap);
		logger.info("getCorrectAP ok");
		
		List<PhoneWifiMessage> list = new ArrayList<PhoneWifiMessage>(correct_ap);
		
		if (list.size() >= 4) {
			String dev_mac = list.get(0).getPhoneMac();
			HashMap<String, PhoneWifiMessage> apdata_map = new HashMap<String, PhoneWifiMessage>();
			for (int m = 0; m < list.size(); m++)
				apdata_map.put(list.get(m).getBssid(), list.get(m));
			
			PhoneFirstRoundLocation first = new PhoneFirstRoundLocation();
			info1 = first.getPlaceSets(dev_mac, apdata_map);
			if (info1.getPlaceList() == null) {
				logger.info("Algorithm Error");
				return null;
			}
			
			 List<String> location_list = info1.getPlaceList();

			 List<String> place = location_list; 
			 
			info1.setPlaceList((ArrayList)place);
			int map_id = Integer.parseInt(place.get(0).split("#")[1]);
			info1.setMapid(map_id);

			float[] position = getPosition(place);
			info1.setX(position[0]);
			info1.setY(position[1]);
			info1.setRadius(position[2]);
		} else {
			logger.info("list.size() < 4");
			return null;
		}
		logger.info("ap_time:"+info1.getApTime());
		logger.info("server_time:"+info1.getServTime());
		return info1;
	}

	private float[] getPosition( List<String> sets) {
		float[] result = new float[3];
		float sumx = 0;
		float sumy = 0;
		float max_r = 0;
		for (int i = 0; i < sets.size(); i++) {
			String locid = sets.get(i);
			sumx = sumx + LocationTable.getPointByLocId(locid).getX();
			sumy = sumy + LocationTable.getPointByLocId(locid).getY();
		}
		result[0] = sumx / sets.size();
		result[1] = sumy / sets.size();

		for (int i = 0; i < sets.size(); i++) {
			String locid = sets.get(i);
			float x = LocationTable.getPointByLocId(locid).getX();
			float y = LocationTable.getPointByLocId(locid).getY();
			float r = (x - result[0]) * (x - result[0]) + (y - result[1]) * (y - result[1]);
			if (r > max_r)
				max_r = r;
		}
		result[2] = (float) Math.sqrt(max_r);
		return result;
	}
}
