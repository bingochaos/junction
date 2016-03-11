/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.buaa.nlsde.wlan.processer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import cn.edu.buaa.nlsde.wlan.beans.PhoneWifiMessage;

/**
 * 
 * @author lawson
 */
public class WlanData {

	private static List<PhoneWifiMessage> rec_phone_buff_datas = Collections
			.synchronizedList(new ArrayList<PhoneWifiMessage>());// 接收数据缓存队列
	private static HashSet<String> update_ap_macs = new HashSet<String>();
	private static HashSet<String> update_phone_macs = new HashSet<String>();
	private HashMap<String, HashMap<Integer, ArrayList<PhoneWifiMessage>>> phone_property_map;

	// private static ArrayList<WifiMessage> rec_buff_datas = new ArrayList();

	/*
	 * static { rec_buff_datas = Collections.synchronizedList(new
	 * ArrayList<WifiMessage>());//接收数据缓存队列 update_macs = new HashSet<String>();
	 * }
	 */
	public WlanData() {
		phone_property_map = new HashMap<String, HashMap<Integer, ArrayList<PhoneWifiMessage>>>();
	}

	public static ArrayList<PhoneWifiMessage> getRecPhoneBuffDatasCopy() {
		ArrayList<PhoneWifiMessage> datas = new ArrayList<PhoneWifiMessage>();
		for (PhoneWifiMessage msg : rec_phone_buff_datas) {
			datas.add(msg);
		}
		return datas;
	}

	public static void setRecPhoneBuffDatas(
			List<PhoneWifiMessage> rec_phone_buff_datas) {
		WlanData.rec_phone_buff_datas = rec_phone_buff_datas;
	}

	/**
	 * @return the update_macs
	 */
	public static HashSet<String> getUpdateAPMacs() {
		return update_ap_macs;
	}

	public static void resetUpdateAPMacs() {
		update_ap_macs = new HashSet<String>();
	}

	public static boolean add2UpdateAPMacs(String ap_mac) {
		if (update_ap_macs.contains(ap_mac)) {
			return true;
		} else {
			update_ap_macs.add(ap_mac);
			return false;
		}
	}

	/**
	 * @return the update_macs
	 */
	public static HashSet<String> getUpdatePhoneMacs() {
		return update_phone_macs;
	}

	public static void resetUpdatePhoneMacs() {
		update_phone_macs = new HashSet<String>();
	}

	public static boolean add2UpdatePhoneMacs(String ap_mac) {
		if (update_phone_macs.contains(ap_mac)) {
			return true;
		} else {
			update_phone_macs.add(ap_mac);
			return false;
		}
	}

	public HashMap<String, HashMap<Integer, ArrayList<PhoneWifiMessage>>> getPhonePropertyMap() {
		return phone_property_map;
	}

	public void setPhonePropertyMap(
			HashMap<String, HashMap<Integer, ArrayList<PhoneWifiMessage>>> property_map) {
		this.phone_property_map = property_map;
	}
}
