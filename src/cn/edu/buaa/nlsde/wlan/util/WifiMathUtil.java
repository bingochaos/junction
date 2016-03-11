/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.buaa.nlsde.wlan.util;

import java.util.ArrayList;
import java.util.Date;

import cn.edu.buaa.nlsde.wlan.beans.LocationUnionUnit;
import cn.edu.buaa.nlsde.wlan.beans.PhoneWifiMessage;

/**
 *
 * @author lawson
 */
public class WifiMathUtil {
	
	
	public static int getMHZ(int frequency) {
		if (frequency > 5000)
			return 1;
		else {
			return 0;
		}
	}
	
	public static byte getChanel(int frequency) {// 根据频率计算信道
		if (frequency < 2410) {
			return 0x00;
		} else if (frequency >= 2410 && frequency < 2415) {
			return 0x01;
		} else if (frequency >= 2415 && frequency < 2420) {
			return 0x02;
		} else if (frequency >= 2420 && frequency < 2425) {
			return 0x03;
		} else if (frequency >= 2425 && frequency < 2430) {
			return 0x04;
		} else if (frequency >= 2430 && frequency < 2435) {
			return 0x05;
		} else if (frequency >= 2435 && frequency < 2440) {
			return 0x06;
		} else if (frequency >= 2440 && frequency < 2445) {
			return 0x07;
		} else if (frequency >= 2445 && frequency < 2450) {
			return 0x08;
		} else if (frequency >= 2450 && frequency < 2455) {
			return 0x09;
		} else if (frequency >= 2455 && frequency < 2460) {
			return 0x10;
		} else if (frequency >= 2460 && frequency < 2465) {
			return 0x0A;
		} else if (frequency >= 2465 && frequency < 2470) {
			return 0x0B;
		} else if (frequency >= 2470 && frequency < 2475) {
			return 0x0C;
		} else if (frequency >= 5735 && frequency < 5755) {
			return (byte) 0x95;
		} else if (frequency >= 5755 && frequency < 5775) {
			return (byte) 0x99;
		} else if (frequency >= 5775 && frequency < 5795) {
			return (byte) 0x9d;
		} else if (frequency >= 5795 && frequency < 5815) {
			return (byte) 0xA1;
		} else if (frequency >= 5815 && frequency < 5835) {
			return (byte) 0xA5;
		}
		return 0;
	}

	public static void insertSortScanT(ArrayList<PhoneWifiMessage> rec_phone_buff_datas,
			PhoneWifiMessage wfmsg, int keep_time) {
		Date msg_date = wfmsg.getScanTime();
		if (rec_phone_buff_datas.isEmpty()) {// 之前链表为空则直接添加
			rec_phone_buff_datas.add(wfmsg);
			return;
		}

		PhoneWifiMessage oldest_msg = rec_phone_buff_datas.get(0);
		if (DateUtil.dateMargin(oldest_msg.getScanTime(), msg_date) < 0
				&& keep_time > 0) {
			// 最新数据的时间比缓存中最旧的时间还早，则不加入该数据
			return;
		}

		boolean has_added = false;
		for (int i = 0; i < rec_phone_buff_datas.size(); i++) {
			PhoneWifiMessage msg = rec_phone_buff_datas.get(i);
			long margin = DateUtil.dateMargin(msg.getScanTime(), msg_date);
			if (margin > keep_time * 1000 && keep_time > 0) {
				// 最新数据时间比最缓存中最旧的时间的时间差大于keep_time，删除最旧数据
				rec_phone_buff_datas.remove(0);
				i--;
			} else {
				if (margin < 0) {
					rec_phone_buff_datas.add(i, msg);
					has_added = true;
					break;
				}
			}
		}
		if (!has_added) {
			rec_phone_buff_datas.add(wfmsg);
		}
	}


	/*
	 * 按照freq降序排列
	 */
	public static void insertSortFreqUnionSet(
			ArrayList<LocationUnionUnit> sorted_set, LocationUnionUnit loc) {
		boolean has_added = false;
		for (int i = 0; i < sorted_set.size(); i++) {
			LocationUnionUnit now = sorted_set.get(i);
			if (loc.getFreq() > now.getFreq()) {
				sorted_set.add(i, loc);
				has_added = true;
				break;
			}
		}
		if (!has_added) {
			sorted_set.add(loc);
		}
	}

	/**
	 * 按照avg_prob降序排列
	 * 
	 * @param sorted_set
	 * @param loc
	 */
	public static void insertSortProbUnionSet(
			ArrayList<LocationUnionUnit> sorted_set, LocationUnionUnit loc) {
		boolean has_added = false;
		for (int i = 0; i < sorted_set.size(); i++) {
			LocationUnionUnit now = sorted_set.get(i);
			if (loc.getAvgProb() > now.getAvgProb()) {
				sorted_set.add(i, loc);
				has_added = true;
				break;
			}
		}
		if (!has_added) {
			sorted_set.add(loc);
		}
	}

}
