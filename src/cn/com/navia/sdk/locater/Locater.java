/* 
 * Locater.java
 *
 * create on 2013-10-28,14:19:51
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package cn.com.navia.sdk.locater;

import android.net.wifi.ScanResult;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import cn.com.navia.sdk.bean.META;
import cn.com.navia.sdk.bean.SpectrumInfo;
import cn.com.navia.sdk.exceptions.LocaterException;
import cn.com.navia.sdk.locater.services.Status;
import cn.com.navia.sdk.utils.JsonUtils;
import cn.com.navia.sdk.utils.LocaterUtil;
import cn.edu.buaa.nlsde.wlan.beans.LocationInfo;
import cn.edu.buaa.nlsde.wlan.beans.PhoneWifiMessage;
import cn.edu.buaa.nlsde.wlan.beans.UdpServIn;
import cn.edu.buaa.nlsde.wlan.calculate.LocationEngine;
import cn.edu.buaa.nlsde.wlan.init.config.ParaConfig;
import cn.edu.buaa.nlsde.wlan.resource.LocationTable;
import cn.edu.buaa.nlsde.wlan.resource.PhoneSpectrum;
import cn.edu.buaa.nlsde.wlan.util.AbsSDKLogger;
import cn.edu.buaa.nlsde.wlan.util.FileUtil;

//import cn.edu.buaa.nlsde.wlan.util.LogUtil;

public class Locater extends AbsSDKLogger {
    private static Status status;

    private META metaConfig;
    private ParaConfig paraConfig;

    private LocationEngine engine = new LocationEngine();
    private ExecutorService ses = Executors.newSingleThreadExecutor();

    private LocaterThread locThr;

    public META getMetaConfig() {
        return metaConfig;
    }

    public ParaConfig getParaConfig() {
        return paraConfig;
    }

    Locater(SDKInfo sdkInfo, SpectrumInfo spectrumInfo) throws IOException, LocaterException {
        logger.info("creating Locater ...");

        int buildingId = spectrumInfo.getUpdateItem().getBuilding_id();

        File specZipFile = spectrumInfo.getSpecFile(sdkInfo.getSpecsDir());
        File specUnZipDir = LocaterUtil.getSpecUnZipDir(specZipFile);


        logger.info("unzipSpecs:{}=>{}", specZipFile, specUnZipDir);
        LocaterUtil.unzipSpecs(specZipFile, LocaterUtil.getSpecUnZipDir(specZipFile));

        logger.info("loading resources:{}", specUnZipDir);
        loadResources(buildingId, specUnZipDir);

        status = Status.INITED;
    }

    /**
     * 初始化配置
     *
     * @return 0：初始化成功； <BR>
     * 1：类对应文件读取失败； <BR>
     * 2：model文件读取失败； <BR>
     * @throws IOException
     */
    public void loadResources(int buildingId, File specUnZipFile) throws IOException {
        File[] locs = specUnZipFile.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.contains("loc");
            }
        });

        // location files
        for (File locFile : locs) {
            boolean ok = LocationTable.addLocation(locFile);// 读取位置表数据
            logger.info("load location file:{}=>{}", locFile, ok);
        }

        // spec files
        File[] specs = specUnZipFile.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.contains("spec");
            }
        });
        for (File specFile : specs) {
            boolean ok = PhoneSpectrum.addSpectrum(specFile);
            logger.info("load spectrum file:{}=>{}", specFile, ok);
        }

        // paraConfig.json
        paraConfig = ParaConfig.readConfig(new File(specUnZipFile, "paraConfig.json"));
        logger.info("paraConfig:{}", paraConfig);

        //META.json
        metaConfig = JsonUtils.parse(FileUtil.readContent(new File(specUnZipFile, "META.json")), META.class);
        logger.info("metaConfig:{}", metaConfig);

    }

    public void start(final BlockingQueue<List<ScanResult>> inQ, LocationCallback callback) {
        if(locThr == null){
            locThr = new LocaterThread(this, inQ, callback);
            ses.execute(locThr);
           // locThr.start();

        }
        logger.info("start locater Task:{}", locThr);
    }



    /**
     * 定位计算入口
     *
     * @param udp 手机发送的数据
     * @return 定位结果集合
     * @throws Exception
     */
    public List<LocationInfo> calcLocation(UdpServIn udp) throws Exception {
        ArrayList<PhoneWifiMessage> pmessage = udp.toPhoneMsgs();
        List<LocationInfo> list = engine.findPhoneLocation(pmessage);
        return list;
    }

    public void shutdown() {
        if (status != Status.DESTROY) {
            logger.warn("shutdown");

            metaConfig = null;
            paraConfig = null;

            ses.shutdownNow();
            status = Status.DESTROY;
           // locThr.destroy();
        }

    }
}
