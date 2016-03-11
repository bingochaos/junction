package cn.com.navia.sdk.locater;

import android.net.wifi.ScanResult;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import cn.com.navia.sdk.bean.META;
import cn.com.navia.sdk.locater.services.Status;
import cn.edu.buaa.nlsde.wlan.beans.IPSDataPoint;
import cn.edu.buaa.nlsde.wlan.beans.LocationInfo;
import cn.edu.buaa.nlsde.wlan.beans.PhoneAcc;
import cn.edu.buaa.nlsde.wlan.beans.UdpRecvAP;
import cn.edu.buaa.nlsde.wlan.beans.UdpServIn;
import cn.edu.buaa.nlsde.wlan.postprocess.CheckWifiData;
import cn.edu.buaa.nlsde.wlan.util.DateUtil;
import cn.edu.buaa.nlsde.wlan.util.WifiMathUtil;

/**
 * Created by gaojie on 15-3-2.
 */
public class LocaterThread extends Thread {

    private final META metaConfig;
    private Logger logger = LoggerFactory.getLogger(getClass());

    private final LocationCallback callback;
    private final BlockingQueue<List<ScanResult>> inQ;
    private final Locater loc;
    private Status status;

    public LocaterThread(final Locater loc, final BlockingQueue<List<ScanResult>> inQ,
                         final LocationCallback callback) {
        this.loc = loc;
        this.inQ = inQ;
        this.callback = callback;

        metaConfig = this.loc.getMetaConfig();

    }

    @Override
    public void run() {
        while (true && inQ != null) {

            try {
                logger.info("Poll scan");
                List<ScanResult> results = null;

                while ((results = inQ.poll(3, TimeUnit.SECONDS)) == null) {
                    logger.warn("Poll result is NULL");
                    continue;
                }

                logger.info("Poll scan.size:{}", results.size());

                List<UdpRecvAP> udpRecvAPs = new ArrayList<UdpRecvAP>();
                for (ScanResult result : results) {
                    // 手机定位存储数据
                    udpRecvAPs.add(new UdpRecvAP(result.SSID.toUpperCase(), result.BSSID.toUpperCase(),
                            (short) WifiMathUtil.getChanel(result.frequency), (byte) WifiMathUtil
                            .getMHZ(result.frequency), (short) result.level, DateUtil.getCurTime()
                            .getTime()));
                }

                logger.info("check:{}", udpRecvAPs.size());

                // 检查可用性
                CheckWifiData.checkUsefuls(udpRecvAPs);
                UdpRecvAP[] udpRecvAPs2 = new UdpRecvAP[udpRecvAPs.size()];

                UdpServIn udpServIn = new UdpServIn(null, "", udpRecvAPs.toArray(udpRecvAPs2), new PhoneAcc(),
                        0f, (short) 0f, DateUtil.getCurTime().getTime());

                // 定位部分
                try {

                    logger.info("calcLocation");
                    List<LocationInfo> locations = loc.calcLocation(udpServIn);

                    if (!locations.isEmpty()) {

                        LocationInfo locInfo = locations.get(0);

                        META.Update.Building.Floor floor = null;

                        List<META.Update.Building.Floor> floors = metaConfig.getUpdate().getBuilding().getFloors();
                        for (META.Update.Building.Floor _floor : floors) {
                            if (_floor.getMapId() == locInfo.getMapid()) {
                                floor = _floor;
                                break;
                            }
                        }


                        if (floor != null) {
                            locInfo.setFloor(floor);
                        }


                        IPSDataPoint p = new IPSDataPoint(locInfo.getX(), locInfo.getY(), locInfo.getApTime().getTime(), 0);

                        //double velocity = CheckWifiData.checkVelocity(p);
                        //logger.info("checkVelocity:{}=>{}", locInfo.getDevMac(), velocity);

                        logger.info("p:{}", p);
                        callback.onLocation(locInfo);
                    } else {
                        logger.info("no locations");

                    }
                } catch (Exception e) {
                    logger.error("calcLocation:{}", e.getMessage(), e);
                }
            } catch (InterruptedException iex) {
                logger.error("locThr:{} Interrupted", getName());
                break;
            } catch (Exception e) {
                logger.error("locThr:{} Exception", getName(), e);
            }
        }
    }
}
