package cn.edu.buaa.nlsde.wlan.postprocess;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.edu.buaa.nlsde.wlan.calculate.LSM;
import cn.edu.buaa.nlsde.wlan.beans.IPSDataPoint;
import cn.edu.buaa.nlsde.wlan.beans.UdpRecvAP;
import cn.edu.buaa.nlsde.wlan.resource.PhoneSpectrum;

/**
 * @author Administrator
 * 
 */
public class CheckWifiData {
	static String abc = "CheckWifiData";
	private static Map<String, Map<String, LinkedList<UdpRecvAP>>> checkData = new HashMap<String, Map<String, LinkedList<UdpRecvAP>>>();
	private static IPSDataPoint prePoint = null;

	public static double checkVelocity(IPSDataPoint p) {
		if (prePoint == null) {
			prePoint = p;
			return 0;
		}
		return calcVelocity(prePoint, p);
	}

	private static double calcVelocity(IPSDataPoint d1, IPSDataPoint d2) {
		double v = 0d;
		IPSDataPoint p1 = d1 ;
		IPSDataPoint p2 = d2 ;

		float x_last = p1.getP_x();
		float y_last = p1.getP_y();
		float x = p2.getP_x();
		float y = p2.getP_y();

		long intervalTime = Math.abs((p1.getP_t() - p2.getP_t()) / 1000);

		if (intervalTime > 0) {
			// s
			double len = Math.sqrt(Math.pow(x - x_last, 2)
					+ Math.pow(y - y_last, 2));
			v = len / intervalTime;
		}

		return v;
	}

	/**
	 * 婊ゆ尝
	 * 
	 * @param msg
	 * @return
	 */
	private static boolean checkUnusualWave(UdpRecvAP msg) {

		Map<String, LinkedList<UdpRecvAP>> map = checkData.get(abc);

		if (map == null) {
			map = new HashMap<String, LinkedList<UdpRecvAP>>();
			checkData.put(abc, map);
		}

		boolean retval = true;
		// init
		LinkedList<UdpRecvAP> apHisQueue = map.get(msg.getBssid());
		if (apHisQueue == null) {
			apHisQueue = new LinkedList<UdpRecvAP>();
			map.put(msg.getBssid(), apHisQueue);
		}
		
		apHisQueue.addFirst(msg);
		// remove last
		UdpRecvAP first = apHisQueue.getFirst();
		UdpRecvAP last = null;
		while ((last = apHisQueue.getLast()) != null
				&& interval(first, last) > 5000) {
			apHisQueue.removeLast();
		}

		int size = apHisQueue.size();

		if (size > 3) {
			retval = lsm(msg, retval, apHisQueue, size);
		}
		if(!retval){
			//remove;
			checkData.remove(abc);
		}
		return retval;
	}

	private static boolean lsm(UdpRecvAP msg, boolean retval,
			LinkedList<UdpRecvAP> apHisQueue, int size) {
		
		float[] y = new float[size];
		float[] x = new float[size];

		long lastTime = apHisQueue.getLast().getScanTime();
		int i = 0;
		for (int j = size - 1; j >= 0; j--) {
			UdpRecvAP e = apHisQueue.get(j);
			x[i] = (e.getScanTime() - lastTime);
			y[i] = e.getRssi();
			i++;
		}
		float[] lsm = LSM.getLsm(x, y);

		float nextRSSI = lsm[0] + (lsm[1] * (x[i - 1] + 800));

		float lastRSSI = y[0];
		float newRSSI = y[y.length - 1];

		if (-80 < nextRSSI && nextRSSI < -30) {
			if (lastRSSI - nextRSSI < 6) {
				float incrRSSI = (nextRSSI - newRSSI);
				if (incrRSSI > 5) {
					nextRSSI -= incrRSSI / 2;
				}
				msg.setRssi((short) nextRSSI);
				retval = true;
			}  
		} 
		return retval;
	}

	private static int interval(UdpRecvAP m1, UdpRecvAP m2) {
		return Math.abs((int) (m1.getScanTime() - m2.getScanTime()));
	}

	private static short avgRssi(LinkedList<UdpRecvAP> apHisQueue) {
		int max = 0;
		for (int j = 0; j < apHisQueue.size(); j++) {
			UdpRecvAP UdpRecvAP = apHisQueue.get(j);
			max += UdpRecvAP.getRssi();
		}
		return (short) (max / apHisQueue.size());
	}

	public static boolean avgCheckRssi(LinkedList<UdpRecvAP> mac_list) {
		int count = 1;
		int last_rssi = mac_list.get(0).getRssi();// 涓婁竴涓暟鍊�
		for (int i = 1; i < mac_list.size(); i++) {
			int new_rssi = mac_list.get(i).getRssi();// 鏂版暟鍊�
			if (Math.abs(new_rssi - last_rssi) <= 6) {
				count++;
			}
			last_rssi = new_rssi;
		}
		return (count == mac_list.size() && count >= 2);

	}

	public static void checkUsefuls(List<UdpRecvAP> pmessages) {
		Set<String> macKeys = new HashSet<String>();
		Iterator<UdpRecvAP> iterator = pmessages.iterator();
		while (iterator.hasNext()) {
			UdpRecvAP msg = iterator.next();
			if (!checkUseful(msg)) {
				iterator.remove();
				continue;
			}
			// 濡傛灉宸茬稉娣诲姞浜� 杩斿洖false, 鍓囧埅闄ゅ凡缍撴湁鐨勫厓绱�
			boolean add = macKeys.add(msg.getBssid());
			if (!add) {
				iterator.remove();
			}
		}
	}

	/**
	 * 妫�煡
	 * 
	 * @param msg
	 * @return
	 */
	private static boolean checkUseful(UdpRecvAP msg) {

//		Log.i(TAG, "check: ap"+msg.toString());
//		// rssi
//		if (msg.getRssi() < -80) {
//			return false;
//		}

		// bssid
		String bssid = msg.getBssid();
		boolean bssidIsValid = PhoneSpectrum.ap_macs.contains(bssid);
		if (!bssidIsValid) {
			bssid = bssid.replaceFirst("\\w{1}$", "0");
			bssidIsValid = PhoneSpectrum.ap_macs.contains(bssid);
			if (bssidIsValid) {
				msg.setBssid(bssid);
			}
		}
		if (bssidIsValid) {
			// 杩囨护娉㈠姩澶х殑ap
			bssidIsValid = checkUnusualWave(msg);
		}
		return bssidIsValid;
	}

}
