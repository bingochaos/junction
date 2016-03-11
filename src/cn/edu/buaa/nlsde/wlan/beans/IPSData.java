/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.buaa.nlsde.wlan.beans;

/**
 *
 * @author gaojie
 */
public class IPSData {

    private String mac;
    private int p_z; // map id
    private IPSDataPoint point;
    private long st;

    public IPSData(String mac, int p_z, long st, float p_x, float p_y, long p_t, float r) {
        this.mac = mac;
        this.p_z = p_z;
        this.st = st;
        this.point = new IPSDataPoint(p_x, p_y, p_t, r);

    }

   

    public int getP_z() {
        return p_z;
    }

    public void setP_z(int p_z) {
        this.p_z = p_z;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public IPSDataPoint getPoint() {
        return point;
    }

    public void setPoint(IPSDataPoint point) {
        this.point = point;
    }

    public long getSt() {
        return st;
    }

    public void setSt(long st) {
        this.st = st;
    }

}
