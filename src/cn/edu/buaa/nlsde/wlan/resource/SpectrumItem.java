/* 
 * SpectrumItem.java
 *
 * create on 2013-10-28,14:21:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package cn.edu.buaa.nlsde.wlan.resource;

/**
 *
 * @author Lei
 */
public class SpectrumItem {

	private String PosiID;
	private String MapID;
	private int Direction;
	private String AP_MAC;
	private int RSSI_Mode;
	private int RSSI_Mean;
	private int RSSI_Median;
	private float RSSI_std;
	private int rank;

	/**
	 * Creates a new instance of WifiLibraryItem
	 */
	public SpectrumItem() {
	}

	public int getRSSIMode() {
		return RSSI_Mode;
	}

	public void setRSSIMode(int rSSI_Mode) {
		RSSI_Mode = rSSI_Mode;
	}

	public int getRSSIMean() {
		return RSSI_Mean;
	}

	public void setRSSIMean(int rSSI_Mean) {
		RSSI_Mean = rSSI_Mean;
	}

	public int getRSSIMedian() {
		return RSSI_Median;
	}

	public void setRSSIMedian(int rSSI_Median) {
		RSSI_Median = rSSI_Median;
	}

	public float getRSSIStd() {
		return RSSI_std;
	}

	public void setRSSIStd(float rSSI_std) {
		RSSI_std = rSSI_std;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}

	public String getPosiID() {
		return PosiID;
	}

	public void setPosiID(String PosiID) {
		this.PosiID = PosiID;
	}

	public String getMapID() {
		return MapID;
	}

	public void setMapID(String MapID) {
		this.MapID = MapID;
	}

	public int getDirection() {
		return Direction;
	}

	public void setDirection(int Direction) {
		this.Direction = Direction;
	}

	public String getAP_MAC() {
		return AP_MAC;
	}

	public void setAP_MAC(String AP_MAC) {
		this.AP_MAC = AP_MAC;
	}

	@Override
	public SpectrumItem clone() {
		SpectrumItem clone = new SpectrumItem();
		clone.setPosiID(PosiID);
		clone.setMapID(MapID);
		clone.setDirection(Direction);
		clone.setAP_MAC(AP_MAC);
		clone.setRSSIMean(RSSI_Mean);
		clone.setRSSIMedian(RSSI_Median);
		clone.setRSSIMode(RSSI_Mode);
		clone.setRSSIStd(RSSI_std);
		clone.setRank(rank);
		return clone;
	}
}
