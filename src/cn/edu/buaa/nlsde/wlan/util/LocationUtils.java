/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.buaa.nlsde.wlan.util;

import java.util.ArrayList;
import java.util.List;

import cn.edu.buaa.nlsde.wlan.beans.IPSData;
import cn.edu.buaa.nlsde.wlan.beans.IPSMsg;
import cn.edu.buaa.nlsde.wlan.beans.IPSMsg.Origin;
import cn.edu.buaa.nlsde.wlan.beans.LocationInfo;

/**
 *
 * @author lawson
 */
public class LocationUtils {

    public static IPSMsg convert2IPSMsg(Origin origin, List<LocationInfo> infos) {
        IPSMsg msg = new IPSMsg(1, origin);
        IPSMsg.Body body = msg.new Body(1);
        List<IPSData> datas = new ArrayList<IPSData>(infos.size());
        for (LocationInfo li : infos) {
            if (!li.getPlaceList().isEmpty()) {
                IPSData d = new IPSData(li.getDevMac(), li.getMapid(), System.currentTimeMillis(), li.getX(), li.getY(), li.getServTime().getTime(), li.getRadius());
                datas.add(d);
            }
        }
        body.setData(datas);
        msg.setBody(body);
        return msg;
    }

}
