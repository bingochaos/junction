/*
 * Config.java
 *
 * Created on 2013年1月11日, 上午7:17
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package cn.edu.buaa.nlsde.wlan.init.config;


/**
 * 
 * @author lawson
 */
public class ProcesserConfig {

	private static String root_path;
	private static String location_table_path;
	private static String phone_spectrum_path;
	private static String distance_path;

	public static void printOut() {
		System.out.println("phone_spectrum_path=" + phone_spectrum_path);
		System.out.println("location_table_path=" + location_table_path);
		System.out.println("distance_path=" + distance_path);
	}

	/**
	 * @return the location_table_path
	 */
	public static String getLocationTablePath() {
		return location_table_path;
	}

	/**
	 * @param aLocation_table_path
	 *            the location_table_path to set
	 */
	public static void setLocationTablePath(String aLocation_table_path) {
		location_table_path = aLocation_table_path;
	}

	public static String getRootPath() {
		return root_path;
	}

	public static void setRootPath(String aroot_path) {
		root_path = aroot_path;
	}

	public static String getPhoneSpectrumPath() {
		return phone_spectrum_path;
	}

	public static void setPhoneSpectrumPath(String spectrum_path) {
		ProcesserConfig.phone_spectrum_path = spectrum_path;
	}

	public static String getDistancePath() {
		return distance_path;
	}

	public static void setDistancePath(String distance_path) {
		ProcesserConfig.distance_path = distance_path;
	}

}
