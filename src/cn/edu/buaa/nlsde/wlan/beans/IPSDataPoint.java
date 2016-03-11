package cn.edu.buaa.nlsde.wlan.beans;

import cn.com.navia.sdk.bean.META;
import cn.edu.buaa.nlsde.wlan.util.CoordinateUtil;

public class IPSDataPoint {


    private CoordinateUtil.LatLng latLng;
    private float p_x;
    private float p_y;
    private long p_t;
    private float r; // 误差半径

    
    
    
    public IPSDataPoint(float p_x, float p_y, long p_t, float r) {
        this.p_x = p_x;
        this.p_y = p_y;
        this.p_t = p_t;
        this.r = r;
    }

    public IPSDataPoint(LocationInfo locInfo, META.Update.Building.Floor floor){
        this.p_x = locInfo.getX();
        this.p_y = locInfo.getY();
        this.p_t = locInfo.getApTime().getTime();

        this.latLng = CoordinateUtil.convertLatLng(floor, this.p_x, this.p_y);
    }

    public CoordinateUtil.LatLng getLatLng() {
        return latLng;
    }

    public float getP_x() {
        return p_x;
    }

    public void setP_x(float p_x) {
        this.p_x = p_x;
    }

    public float getP_y() {
        return p_y;
    }

    public void setP_y(float p_y) {
        this.p_y = p_y;
    }

    public long getP_t() {
        return p_t;
    }

    public void setP_t(long p_t) {
        this.p_t = p_t;
    }

    public float getR() {
        return r;
    }

    public void setR(float r) {
        this.r = r;
    }
}