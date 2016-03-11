/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.buaa.nlsde.wlan.beans;

import java.util.Date;
import java.util.List;

import cn.com.navia.sdk.bean.META;
import cn.edu.buaa.nlsde.wlan.util.CoordinateUtil;

/**
 * 定位结果存储类型
 *
 * @author lawson
 */
public class LocationInfo {

	private String dev_mac;                       // device_mac
	private Date serv_time;                       // 服务器时间
	private Date ap_time;                         // ap时间
	private float x;                              // 定位位置横坐标（单位：m）
	private float y;                              // 定位位置纵坐标（单位：m）
	private int mapid;                            // 定位楼层id
	private float radius;                         // 定位精度范围
	private int c_type = 0;                       // 坐标系类型 0:直角坐标系,1:84坐标系
	private List<LocationUnionUnit> place_units;  // 定位位置点集合
	private List<String> place_list;              // 定位位置点集合，location#mapid

    public META.Update.Building.Floor getFloor() {
        return floor;
    }

    public void setFloor(META.Update.Building.Floor floor) {
        this.floor = floor;
    }

    private META.Update.Building.Floor floor;


    public CoordinateUtil.LatLng getLatLng( ){
         return floor != null ? CoordinateUtil.convertLatLng(floor, getX(), getY()) : null;
    }

	/**
	 * @return the dev_mac
	 */
	public String getDevMac() {
		return dev_mac;
	}

	/**
	 * @param dev_mac
	 *            the dev_mac to set
	 */
	public void setDevMac(String dev_mac) {
		this.dev_mac = dev_mac;
	}

	/**
	 * @return the serv_time
	 */
	public Date getServTime() {
		return serv_time;
	}

	/**
	 * @param serv_time
	 *            the serv_time to set
	 */
	public void setServTime(Date serv_time) {
		this.serv_time = serv_time;
	}

	/**
	 * @return the ap_time
	 */
	public Date getApTime() {
		return ap_time;
	}

	/**
	 * @param ap_time
	 *            the ap_time to set
	 */
	public void setApTime(Date ap_time) {
		this.ap_time = ap_time;
	}

	/**
	 * @return the x
	 */
	public float getX() {
		return x;
	}

	/**
	 * @param x
	 *            the x to set
	 */
	public void setX(float x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public float getY() {
		return y;
	}

	/**
	 * @param y
	 *            the y to set
	 */
	public void setY(float y) {
		this.y = y;
	}

	/**
	 * @return the mapid
	 */
	public int getMapid() {
		return mapid;
	}

	/**
	 * @param mapid
	 *            the mapid to set
	 */
	public void setMapid(int mapid) {
		this.mapid = mapid;
	}

	/**
	 * @return the radius
	 */
	public float getRadius() {
		return radius;
	}

	/**
	 * @param radius
	 *            the radius to set
	 */
	public void setRadius(float radius) {
		this.radius = radius;
	}

	/**
	 * @return the c_type
	 */
	public int getCType() {
		return c_type;
	}

	/**
	 * @param c_type
	 *            the c_type to set
	 */
	public void setCType(int c_type) {
		this.c_type = c_type;
	}

	public List<LocationUnionUnit> getPlaceUnits() {
		return place_units;
	}

	public void setPlaceUnits(List<LocationUnionUnit> place_units) {
		this.place_units = place_units;
	}

	/**
	 * @return the place_list
	 */
	public List<String> getPlaceList() {
		return place_list;
	}

	/**
	 * @param place_list
	 *            the place_list to set
	 */
	public void setPlaceList( List<String> place_list) {
		this.place_list = place_list;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LocationInfo [dev_mac=");
		builder.append(dev_mac);
		builder.append(", serv_time=");
		builder.append(serv_time);
		builder.append(", ap_time=");
		builder.append(ap_time);
		builder.append(", x=");
		builder.append(x);
		builder.append(", y=");
		builder.append(y);
		builder.append(", map_id=");
		builder.append(mapid);
		builder.append(", radius=");
		builder.append(radius);
		builder.append(", c_type=");
		builder.append(c_type);
		builder.append(", place_list=[");
		for (int i = 0; i < place_list.size(); i++) {
			builder.append(place_list.get(i)).append(",");
		}
		builder.append("]]");
		return builder.toString();
	}

}
