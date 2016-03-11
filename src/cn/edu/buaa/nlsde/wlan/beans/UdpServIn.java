package cn.edu.buaa.nlsde.wlan.beans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class UdpServIn {
	private String idFV = null; // identifierForVendor of iOS device
	private String phoneMac = null;
	private UdpRecvAP[] wifis = null;
	private PhoneAcc acc = null;
	private Float v = null;
	private Short ori = null;
	private long sendTime;

	public UdpServIn() {
	}

	public UdpServIn(String idFV, String phoneMac, UdpRecvAP[] wifis,
			PhoneAcc acc, Float v, Short ori, long sendTime) {
		this.idFV = idFV;
		this.phoneMac = phoneMac;
		this.wifis = wifis;
		this.acc = acc;
		this.v = v;
		this.ori = ori;
		this.sendTime = sendTime;
	}

	@Override
	public String toString() {
		return "UdpServIn [idFV=" + idFV + ", phoneMac=" + phoneMac
				+ ", wifis=" + Arrays.toString(wifis) + ", acc=" + acc + ", v="
				+ v + ", ori=" + ori + ", sendTime=" + new Date(sendTime) + "]";
	}

	public String getIdFV() {
		return idFV;
	}

	public void setIdFV(String idFV) {
		this.idFV = idFV;
	}

	public String getPhoneMac() {
		return phoneMac;
	}

	public void setPhoneMac(String phoneMac) {
		this.phoneMac = phoneMac;
	}

	public UdpRecvAP[] getWifis() {
		return wifis;
	}

	public void setWifis(UdpRecvAP[] wifis) {
		this.wifis = wifis;
	}

	public PhoneAcc getAcc() {
		return acc;
	}

	public void setAcc(PhoneAcc acc) {
		this.acc = acc;
	}

	public Float getV() {
		return v;
	}

	public void setV(Float v) {
		this.v = v;
	}

	public Short getOri() {
		return ori;
	}

	public void setOri(Short ori) {
		this.ori = ori;
	}

	public long getSendTime() {
		return sendTime;
	}

	public void setSendTime(long sendTime) {
		this.sendTime = sendTime;
	}

	public ArrayList<PhoneWifiMessage> toPhoneMsgs() {
		ArrayList<PhoneWifiMessage> msgs = new ArrayList<PhoneWifiMessage>();
		for (int i = 0; i < wifis.length; i++) {
			PhoneWifiMessage pwm = new PhoneWifiMessage();
			pwm.setIdFV(idFV);
			pwm.setPhoneMac(phoneMac);
			pwm.setAcc(acc);
			pwm.setV(v);
			pwm.setOri(ori);
			pwm.setSendTime(sendTime);
			UdpRecvAP revap = wifis[i];
			pwm.setBssid(revap.getBssid());
			pwm.setChannel(revap.getChannel());
			pwm.setFreq(revap.getFreq());
			pwm.setRssi(revap.getRssi());
			pwm.setScanTime(revap.getScanTime());
			pwm.setSsid(revap.getSsid());
			msgs.add(pwm);
		}
		return msgs;
	}
}
