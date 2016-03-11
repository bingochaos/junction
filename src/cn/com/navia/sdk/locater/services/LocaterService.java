package cn.com.navia.sdk.locater.services;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.com.navia.sdk.SDKManager;
import cn.com.navia.sdk.bean.SpectrumInfo;
import cn.com.navia.sdk.exceptions.LocaterException;
import cn.com.navia.sdk.locater.LocaterManager;
import cn.com.navia.sdk.locater.LocaterManagerFactory;
import cn.com.navia.sdk.locater.SDKInfo;
import cn.com.navia.sdk.utils.LocaterUtil;
import cn.edu.buaa.nlsde.wlan.beans.LocationInfo;

public class LocaterService extends IntentService {
    private static Logger logger = LoggerFactory.getLogger(LocaterService.class);

    private ExecutorService singleThreadPool = Executors.newSingleThreadExecutor();
    private SDKInfo sdkInfo;

    private LocaterManager locManager;

    private Messenger cmdMsger;
    private CMDHandler cmdHander;

    private static Messenger uiMsger;

    // public static int LOCATER_NOTSTART = 1;
    // public static int LOCATER_START = 2;
    // public static int LOCATER_PAUSE = 3;
    // public static int LOCATER_SHUTDOWN = 4;
    // public static int LOCATER_DESTORY = 5;

    private static Status status;

    static class CMDHandler extends Handler {

        private SoftReference<LocaterService> serviceRef;

        public CMDHandler(SoftReference<LocaterService> weakReference) {
            serviceRef = weakReference;
        }

        @Override
        public void handleMessage(final Message msg) {
            logger.info("cmd:{}", msg);
            final LocaterService locaterService = serviceRef.get();

            if (locaterService == null) {
                logger.warn("serviceRef->[LocaterService] was be cleared");
                return;
            }

            if (msg.what == Action.SHOW_LATEST_SPECS.getId()) {
                final Integer flag = (Integer) msg.obj;
                if (flag != null) {
                    Runnable showTarget = new Runnable() {
                        @Override
                        public void run() {
                            List<SpectrumInfo> latestSpectrums = null;
                            try {
                                latestSpectrums = locaterService.showLatestSpectrums(flag);
                                LocaterUtil.send2Msg(uiMsger, MsgType.SERVER_SPECS.getId(), latestSpectrums);
                            } catch (IOException e) {
                                logger.warn("showLatestSpectrums:{}", e.getMessage());
                                LocaterUtil.send2Msg(uiMsger, MsgType.DOWNLOAD_ERROR.getId(), e.getMessage());
                            }
                        }
                    };
                    locaterService.singleThreadPool.execute(showTarget);
                }

            } else if (msg.what == Action.DOWN_SPECT.getId()) {

                final int[] d = (int[]) msg.obj;

                Runnable downTarget = new Runnable() {
                    @Override
                    public void run() {
                        boolean down = false;
                        try {
                            down = locaterService.sdkInfo.downSpectrum(d[0], d[1]);
                            LocaterUtil.send2Msg(uiMsger, MsgType.DOWNLOAD_SUCCESS.getId(), down);
                        } catch (IOException e) {
                            logger.warn("downSpectrum:{}=>{}", d[0], e.getMessage());
                            LocaterUtil.send2Msg(uiMsger, MsgType.DOWNLOAD_ERROR.getId(), e.getMessage());
                        }
                    }
                };

                locaterService.singleThreadPool.execute(downTarget);
            } else if (msg.what == Action.LOAD_LOCAL_SPECS.getId()) {

                //load local specs
                locaterService.sdkInfo.loadSpecsData();
                Map<Integer, SpectrumInfo> zipSpecs = locaterService.sdkInfo.getLocalZipSpecs();
                LocaterUtil.send2Msg(uiMsger, MsgType.LOCAL_SUCCESS.getId(), zipSpecs);

            } else if (msg.what == Action.INIT.getId()) {

                //init
                try {
                    locaterService.initLocater();

                    LocaterUtil.send2Msg(uiMsger, MsgType.INIT_SUCCESS.getId(), true);
                } catch (Exception e) {
                    e.printStackTrace();
                    LocaterUtil.send2Msg(uiMsger, MsgType.INIT_ERROR.getId(), e.getMessage());
                }
            } else if (msg.what == Action.DESTROY.getId()) {

                //destroy
                locaterService.destroy();
            } else if (msg.what == Action.START.getId()) {

                //start
                try {
                    int buildingId = (Integer) msg.obj;
                    locaterService.start(buildingId);
                    LocaterUtil.send2Msg(uiMsger, MsgType.START_SUCCESS.getId(), true);
                } catch (LocaterException e) {
                    e.printStackTrace();
                    LocaterUtil.send2Msg(uiMsger, MsgType.START_ERROR.getId(), e.getMessage());
                }

            } else if (msg.what == Action.STOP.getId()) {

                //stop
                locaterService.stop();
            }
        }
    }

    ;

    public LocaterService() {
        super(LocaterService.class.getCanonicalName());
    }

    public void destroy () {
        stop ();
        if (locManager != null) {
            locManager.destroy();
            locManager = null;
        }
        status = Status.DESTROY;
    }

    public void stop () {
        if (status == Status.RUNGING) {
            status = Status.STOPING;
            locManager.stop();
            status = Status.STOPED;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        logger.info("onCreate");

        cmdHander = new CMDHandler(new SoftReference<LocaterService>(this));
        cmdMsger = new Messenger(cmdHander);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        logger.info("onStart");

    }

    @Override
    public IBinder onBind(final Intent intent) {
        logger.info("onBind");
        uiMsger = (Messenger) intent.getExtras().get(SDKManager.INTENT_EXTRA_MESSENGER);
        //initLocater();
        return cmdMsger.getBinder();
    }

    @Override
    public void onRebind(Intent intent) {
        logger.info("onRebind");
        if (status == null || status == Status.DESTROY) {
            onBind(intent);
        }
        super.onRebind(intent);
    }

    @Override
    public void onDestroy() {
        this.destroy ();
        super.onDestroy();
    }

    private List<SpectrumInfo> showLatestSpectrums(int force) throws IOException {
        return sdkInfo.listLatestUpdates(force);
    }

    private void start(int buildingId) throws LocaterException {
        try {
            logger.info("start BuildingId:{}", buildingId);

            if (status == Status.INITED || status == Status.STOPED) {
                status = Status.STARTING;
                if (locManager == null) {
                    locManager = LocaterManagerFactory.getLocaterManager(sdkInfo, buildingId, this);
                    logger.info("LocaterManagerFactory.getLocaterManager:{}", locManager);
                }
                locManager.start();
                status = Status.RUNGING;
                logger.info("locManager.start ok");
            }
        } catch (LocaterException e) {
            logger.error("LocaterException :{} ", e.getMessage());

            throw e;
        }
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        // destroy
        logger.info("unbindService");
        super.unbindService(conn);
    }

    private void initLocater() throws NameNotFoundException {
        logger.info("initLocater...");
        if (status == null || status == Status.DESTROY) {
            Bundle metaData = this.getPackageManager().getServiceInfo(
                                        new ComponentName(getApplicationContext(), getClass()),
                                        PackageManager.GET_META_DATA).metaData;

            String host = null, appKey = null;
            if (metaData != null) {
                host = metaData.getString("SDK_SERVER");
                appKey = metaData.getString("APP_KEY");
            }
            sdkInfo = SDKInfo.createInstance(this, host, appKey);

            status = Status.INITED;
        }
        logger.info("initLocater ok");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        logger.info("onHandleIntent:{}", intent);
    }

    public void sendLocation(LocationInfo info) {
        LocaterUtil.send2Msg(uiMsger, MsgType.LOCATION.getId(), info);
    }
}
