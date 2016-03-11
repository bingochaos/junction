/**
 * BikeMarkerInfo.java
 * 
 * @Description: 
 * 
 * @File: BikeMarkerInfo.java
 * 
 * @Package nlsde.junction.home.function
 * 
 * @Author chaos
 * 
 * @Date 2014-12-4下午2:09:21
 * 
 * @Version V1.0
 */
package nlsde.junction.home.function;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import nlsde.baidu.MarkerInfo;

/**
 * @author Chaos
 *
 */
public class BikeMarkerInfo implements Serializable {
	
	
	private static final long serialVersionUID = -758459502806858500L; 

    /** 
     * 精度 
     */  
    private double latitude;  
    /** 
     * 纬度 
     */  
    private double longitude;  
    /** 
     * 图片ID，真实项目中可能是图片路径 
     */  
    private int imgId;  
    /** 
     * 网点位置
     */  
    private String bike_net_location;  
    /** 
     * 锁车器数 
     */  
    private int bike_total;  
    /** 
     * 剩余 
     */  
    private int bike_residual;  
    /** 
     * 网点位置细节 
     */  
    private String bike_net_info; 
    /** 
     * 网点编号 
     */ 
    private int number;
    private String StateCode;  



	public static List<BikeMarkerInfo> infos = new ArrayList<BikeMarkerInfo>();  
  
    public BikeMarkerInfo()  
    {  
    }
	/**
	 * @param latitude
	 * @param longitude
	 * @param imgId
	 * @param bike_net_location
	 * @param bike_total
	 * @param bike_residual
	 * @param bike_net_info
	 * @param number
	 */
	public BikeMarkerInfo(double latitude, double longitude, int imgId,
			String bike_net_location, int bike_total, int bike_residual,
			String bike_net_info, int number) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.imgId = imgId;
		this.bike_net_location = bike_net_location;
		this.bike_total = bike_total;
		this.bike_residual = bike_residual;
		this.bike_net_info = bike_net_info;
		this.number = number;
	}
	/**
	 * @return the number
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * @param string the number to set
	 */
	public void setNumber(int string) {
		this.number = string;
	}
	/**
	 * @return the stateCode
	 */
	public String getStateCode() {
		return StateCode;
	}
	/**
	 * @param stateCode the stateCode to set
	 */
	public void setStateCode(String stateCode) {
		StateCode = stateCode;
	}
	/**
	 * @param latitude
	 * @param longitude
	 * @param imgId
	 * @param bike_net_location
	 * @param bike_total
	 * @param bike_residual
	 * @param bike_net_info
	 * @param number
	 * @param stateCode
	 */
	public BikeMarkerInfo(double latitude, double longitude, int imgId,
			String bike_net_location, int bike_total, int bike_residual,
			String bike_net_info, int number, String stateCode) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.imgId = imgId;
		this.bike_net_location = bike_net_location;
		this.bike_total = bike_total;
		this.bike_residual = bike_residual;
		this.bike_net_info = bike_net_info;
		this.number = number;
		StateCode = stateCode;
	}
	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	/**
	 * @return the imgId
	 */
	public int getImgId() {
		return imgId;
	}

	/**
	 * @param imgId the imgId to set
	 */
	public void setImgId(int imgId) {
		this.imgId = imgId;
	}

	/**
	 * @return the bike_net_location
	 */
	public String getBike_net_location() {
		return bike_net_location;
	}

	/**
	 * @param bike_net_location the bike_net_location to set
	 */
	public void setBike_net_location(String bike_net_location) {
		this.bike_net_location = bike_net_location;
	}

	/**
	 * @return the bike_total
	 */
	public int getBike_total() {
		return bike_total;
	}

	/**
	 * @param bike_total the bike_total to set
	 */
	public void setBike_total(int bike_total) {
		this.bike_total = bike_total;
	}

	/**
	 * @return the bike_residual
	 */
	public int getBike_residual() {
		return bike_residual;
	}

	/**
	 * @param bike_residual the bike_residual to set
	 */
	public void setBike_residual(int bike_residual) {
		this.bike_residual = bike_residual;
	}

	/**
	 * @return the bike_net_info
	 */
	public String getBike_net_info() {
		return bike_net_info;
	}

	/**
	 * @param bike_net_info the bike_net_info to set
	 */
	public void setBike_net_info(String bike_net_info) {
		this.bike_net_info = bike_net_info;
	}


  
}
