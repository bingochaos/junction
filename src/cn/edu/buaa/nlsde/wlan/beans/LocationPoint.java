/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.buaa.nlsde.wlan.beans;

/**
 *
 * @author lawson
 */
public class LocationPoint {

    private String location;
    private int mapid;
    private String loc_id;
    private String name;
    private float x;
    private float y;

    public LocationPoint() {
        x = -1.0f;
        y = -1.0f;
        location = "no such point";
    }

    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return the mapid
     */
    public int getMapid() {
        return mapid;
    }

    /**
     * @param mapid the mapid to set
     */
    public void setMapid(int mapid) {
        this.mapid = mapid;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the x
     */
    public float getX() {
        return x;
    }

    /**
     * @param x the x to set
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
     * @param y the y to set
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * @return the loc_id
     */
    public String getLocId() {
        return loc_id;
    }

    /**
     * @param loc_id the loc_id to set
     */
    public void setLocId(String loc_id) {
        this.loc_id = loc_id;
    }
}
