package cn.edu.buaa.nlsde.wlan.beans;

import java.util.Date;

public class UdpRecvAP {
	private String ssid;
	private String bssid;
	private short channel;
	private byte freq;
	private short rssi;
	private long scanTime;

	public UdpRecvAP() {

	}

	public UdpRecvAP(String ssid, String bssid, short channel, byte freq,
			short rssi, long scanTime) {
		super();
		this.ssid = ssid;
		this.bssid = bssid;
		this.channel = channel;
		this.freq = freq;
		this.rssi = rssi;
		this.scanTime = scanTime;
	}

	@Override
	public String toString() {
		return "UdpRecvAP [ssid=" + ssid + ", bssid=" + bssid + ", channel="
				+ channel + ", freq=" + freq + ", rssi=" + rssi + ", scanTime="
				+ new Date(scanTime) + "]";
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public String getBssid() {
		return bssid;
	}

	public void setBssid(String bssid) {
		this.bssid = bssid;
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

	public long getScanTime() {
		return scanTime;
	}

	public void setScanTime(long scanTime) {
		this.scanTime = scanTime;
	}

}
