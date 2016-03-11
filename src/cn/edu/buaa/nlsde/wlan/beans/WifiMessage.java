package cn.edu.buaa.nlsde.wlan.beans;

public class WifiMessage {

	protected String bssid;
	protected String phoneMac;
	protected String ssid;
	protected short channel;
	protected byte freq;
	protected short rssi;

	public String getBssid() {
		return bssid;
	}

	public void setBssid(String ap_bssid) {
		this.bssid = ap_bssid;
	}

	public String getPhoneMac() {
		return phoneMac;
	}

	public void setPhoneMac(String dev_mac) {
		this.phoneMac = dev_mac;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public short getChannel() {
		return channel;
	}

	public void setChannel(short channel) {
		this.channel = channel;
	}

	public byte getFreq() {
		return freq;
	}

	public void setFreq(byte freq) {
		this.freq = freq;
	}

	public short getRssi() {
		return rssi;
	}

	public void setRssi(short rssi) {
		this.rssi = rssi;
	}

}
