package cn.com.navia.sdk.locater;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import cn.com.navia.sdk.bean.SpectrumInfo;
import cn.com.navia.sdk.exceptions.LocaterException;
import cn.com.navia.sdk.locater.services.LocaterService;
import cn.com.navia.sdk.locater.services.Status;
import cn.edu.buaa.nlsde.wlan.beans.LocationInfo;
import cn.edu.buaa.nlsde.wlan.util.AbsSDKLogger;

/**
 * 初始化 LocaterManager
 *
 * @author gaojie
 */
public class LocaterManager extends AbsSDKLogger {
    private BlockingQueue<List<ScanResult>> inQ = new LinkedBlockingQueue<List<ScanResult>>();
    private ExecutorService wifiScanSinglePool = Executors.newSingleThreadExecutor();

    private Object scanLock = new Object();

    private Locater locater;
    private LocaterService locaterService;
    private WifiManager wifiManager;
    private SpectrumInfo spectrumInfo;

    private boolean scan;
    private BroadcastReceiver wifiScanResultReceiver;

    private Status status;

    private LocationCallback callback;

    LocaterManager(SDKInfo sdkInfo, int buildingId, LocaterService service) throws IOException, LocaterException {
        this.locaterService = service;
        this.spectrumInfo = sdkInfo.getLocalZipSpecs().get(buildingId);
        if (spectrumInfo == null) {
            throw new LocaterException("spectrumInfo buildingId:" + buildingId + " was't exist!");
        }

        this.locater = new Locater(sdkInfo, spectrumInfo);
        logger.info("create Locater ok");

        wifiManager = (WifiManager) service.getSystemService(Context.WIFI_SERVICE);
        initWifiScan(wifiManager);
        logger.info("initWifiScan ok");

        callback = new LocationCallback() {
            @Override
            public void onLocation(LocationInfo info) {
                locaterService.sendLocation(info);
            }
        };

        status = Status.INITED;
    }

    public boolean isRunning() {
        return scan;
    }

    private void initWifiScan(final WifiManager wifiManager) {
        // start wifi
        wifiScanResultReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                List<ScanResult> results = wifiManager.getScanResults();

                //add ScanResult to inQ
                boolean offer = inQ.offer(results);

                logger.info("offer inQ:{} size:{}=>{}", inQ.size(), results.size(), offer);
            }
        };

        locaterService.registerReceiver(wifiScanResultReceiver, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        Runnable command = new Runnable() {
            @Override
            public void run() {
                while (true && !Thread.currentThread().isInterrupted()) {

                    while (!scan) {
                        synchronized (scanLock) {
                            try {
                                scanLock.wait();
                            } catch (InterruptedException e) {
                                logger.warn("was Interrupted in wating");
                            }
                        }
                    }

                    logger.info("scan wifi ...");
                    wifiManager.startScan();

                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException e) {
                    }
                }
            }
        };
        this.wifiScanSinglePool.execute(command);
    }

    public void start() {
        logger.info("start ...");
        if (status == Status.INITED || status == Status.STOPED) {
            while (!scan) {
                synchronized (scanLock) {
                    scan = true;
                    scanLock.notifyAll();
                }
            }
            status = Status.RUNGING;
        }
        locater.start(inQ, callback);
    }

    public void stop() {
        logger.info("stop ...");
        if (status == Status.RUNGING) {
            while (scan) {
                synchronized (scanLock) {
                    scan = false;
                }
            }
            status = Status.STOPED;
        }

    }

    public void destroy() {
        logger.info("destroy ...");


        if(status != Status.DESTROY){
            stop();

            logger.info("unregisterReceiver WifiScanReceiver");
            locaterService.unregisterReceiver(wifiScanResultReceiver);

            logger.info("wifiScanSinglePool shutdown");
            List<Runnable> list = wifiScanSinglePool.shutdownNow();
            for (Runnable run : list) {
                logger.warn("task:{} destroy", run);
            }

            //locater shutdown
            locater.shutdown();


            status = Status.DESTROY;
        }

    }
}
