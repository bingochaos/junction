package cn.edu.buaa.nlsde.wlan.beans;

import java.util.Date;

public class PhoneWifiMessage extends WifiMessage {
	private String idFV = null; // identifierForVendor of iOS device
	private String phoneMac = null;
	private PhoneAcc acc = null;
	private Float v = null;
	private Short ori = null;
	private Date sendTime;
	private Date scanTime;

	@Override
	public String toString() {
		return String
				.format("PhoneWifiMessage [idFV=%s, phoneMac=%s, ori=%s, sendTime=%s, ssid=%s, bssid=%s, freq=%s, rssi=%s, scanTime=%s]\n",
						idFV, phoneMac, ori, sendTime, ssid, bssid, freq, rssi,
						scanTime);
	}

	public String getIdFV() {
		return idFV;
	}

	public void setIdFV(String idFV) {
		this.idFV = idFV;
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

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(long sendTime) {
		this.sendTime = new Date(sendTime);
	}

	public Date getScanTime() {
		return scanTime;
	}

	public void setScanTime(long scanTime) {
		this.scanTime = new Date(scanTime);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (!(other instanceof PhoneWifiMessage))
			return false;
		final PhoneWifiMessage message = (PhoneWifiMessage) other;
		if (!getBssid().equals(message.getBssid()))
			return false;
		if (!getSendTime().equals(message.getSendTime()))
			return false;
		if (!getScanTime().equals(message.getScanTime()))
			return false;
		if (!(getChannel() == message.getChannel()))
			return false;
		if (!(getFreq() == message.getFreq()))
			return false;
		if (!(getRssi() == message.getRssi()))
			return false;
		return true;
	}

}
