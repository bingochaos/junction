package cn.edu.buaa.nlsde.wlan.algorithm;

import java.util.ArrayList;
import java.util.List;

import cn.edu.buaa.nlsde.wlan.beans.PhoneWifiMessage;

public class APGroupInfo {
	private int status;
	private List<PhoneWifiMessage> correct_ap;
	private List<PhoneWifiMessage> abnormal_ap;
	private List<PhoneWifiMessage> uncertain_ap;
	
	public APGroupInfo() {
		status = 1;
		correct_ap = new ArrayList<PhoneWifiMessage>();
		abnormal_ap = new ArrayList<PhoneWifiMessage>();
		uncertain_ap = new ArrayList<PhoneWifiMessage>();
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public List<PhoneWifiMessage> getCorrect_ap() {
		return correct_ap;
	}

	public void setCorrect_ap(List<PhoneWifiMessage> correct_ap) {
		this.correct_ap = correct_ap;
	}

	public List<PhoneWifiMessage> getAbnormal_ap() {
		return abnormal_ap;
	}

	public void setAbnormal_ap(List<PhoneWifiMessage> abnormal_ap) {
		this.abnormal_ap = abnormal_ap;
	}

	public List<PhoneWifiMessage> getUncertain_ap() {
		return uncertain_ap;
	}

	public void setUncertain_ap(List<PhoneWifiMessage> uncertain_ap) {
		this.uncertain_ap = uncertain_ap;
	}
	
}
