package cn.edu.buaa.nlsde.wlan.algorithm;

import java.util.List;

import cn.edu.buaa.nlsde.wlan.beans.LocationInfo;
import cn.edu.buaa.nlsde.wlan.beans.PhoneWifiMessage;

public interface IAlgorithm {

	public abstract LocationInfo getLocation(List<PhoneWifiMessage> to_be_judged_ap) throws Exception;

}