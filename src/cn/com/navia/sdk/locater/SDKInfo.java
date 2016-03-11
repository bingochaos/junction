package cn.com.navia.sdk.locater;

import android.content.Context;
import android.text.TextUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cn.com.navia.sdk.bean.RetVal_LatestList;
import cn.com.navia.sdk.bean.RetVal_LatestVersion;
import cn.com.navia.sdk.bean.RetVal_UpdateItem;
import cn.com.navia.sdk.bean.SpectrumInfo;
import cn.com.navia.sdk.utils.JsonUtils;
import cn.com.navia.sdk.utils.LocaterUtil;
import cn.com.navia.sdk.utils.NaviaSpecDaoHelper;
import cn.com.navia.sdk.utils.NetUtil;
import cn.edu.buaa.nlsde.wlan.util.AbsSDKLogger;
import cn.edu.buaa.nlsde.wlan.util.FileUtil;

/**
 * SDKInfo
 */
public class SDKInfo extends AbsSDKLogger {

    private static final String NAVIA_DIR = "navia";
    private static final String NAVIA_DIR_SPECS = "specs";


    static final int UPDATE_STATE_NO = 0;
    static final int UPDATE_STATE_ING = 1;
    static final int UPDATE_STATE_OK = 2;
    static final int UPDATE_STATE_SHOULD = 3;

    private static Context ctx;
    private Logger logger = LoggerFactory.getLogger(getClass());

    private Map<Integer, SpectrumInfo> localZipSpecs = new LinkedHashMap<Integer, SpectrumInfo>();
    private List<SpectrumInfo> latestSpecs = new ArrayList<SpectrumInfo>();

    private String sdkHost;

    private File workDir;
    private File specsDir;

    private String mobileModel;
    private String appKey;
    private int updateState;

    private NaviaSpecDaoHelper sqLiteHelper;


    private SDKInfo(Context ctx, String sdkHost, String appKey) {
        setCtx(ctx);

        if (!TextUtils.isEmpty(sdkHost)) {
            this.sdkHost = sdkHost;
        }
        File mWorkDir = new File(getCtx().getFilesDir(), NAVIA_DIR);
        if (!mWorkDir.exists()) {
            boolean mkdirs = mWorkDir.mkdirs();
            logger.info("mkdirs:{}=>{}", mWorkDir.toString(), mkdirs);
        }
        this.workDir = mWorkDir;
        this.specsDir = new File(mWorkDir, NAVIA_DIR_SPECS);

        this.setAppKey(appKey);
        this.mobileModel = android.os.Build.MODEL;

        sqLiteHelper = new NaviaSpecDaoHelper(ctx);

        // load spectrum data
        loadSpecsData();
    }

    // public

    /**
     * getInstance
     *
     * @param ctx
     * @param sdkHost
     * @param appKey
     * @return
     */
    public static SDKInfo createInstance(Context ctx, String sdkHost, String appKey) {
        SDKInfo sdkInfo = new SDKInfo(ctx, sdkHost, appKey);
        return sdkInfo;
    }

    /**
     * 下载频谱
     *
     * @param buildingId
     * @param latestVer
     * @return
     * @throws IOException
     */
    public boolean downSpectrum(int buildingId, int latestVer) throws IOException {
        boolean f = false;
        try {
            SpectrumInfo spectrumInfo = this.downSpectrumInfo(buildingId, latestVer);
            if (spectrumInfo != null) {
                // 保存最新的频谱数据到本地数据库
                f = saveSpecsData(spectrumInfo);
            }
        } catch (URISyntaxException e) {
            logger.error("downSpectrumInfo: build:{} ver:{}",buildingId, latestVer, e);
        }
        return f;
    }

    /**
     * 获取新的频谱
     *
     * @throws IOException
     */
    public List<SpectrumInfo> listLatestUpdates(int force) throws IOException {

        // http://192.168.0.21:8088/get_update_latest_list?f=1
        String uri = "http://" + this.getSdkHost() + "/get_update_latest_list";

        NameValuePair args = new BasicNameValuePair("f", (force) + "");
        try {
            String _latestList = NetUtil.httpGET(uri, args);
            logger.info("get_update_latest_list:{}", _latestList);

            RetVal_LatestList latestList0 = JsonUtils.parse(_latestList, RetVal_LatestList.class);

            if (latestList0 != null && latestList0.getC() == 0) {
                List<RetVal_UpdateItem> latestUpdates = latestList0.getD();
                for (int i = 0; i < latestUpdates.size(); i++) {
                    RetVal_UpdateItem updateItem = latestUpdates.get(i);
                    SpectrumInfo spectrumInfo = new SpectrumInfo(updateItem);
                    latestSpecs.add(spectrumInfo);
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return latestSpecs;
    }

    public void loadSpecsData() {
        List<SpectrumInfo> specInfos = getDBSpecInfos();
        for (SpectrumInfo specInfo : specInfos) {
            localZipSpecs.put(specInfo.getUpdateItem().getBuilding_id(), specInfo);
        }
    }

    // package

    /**
     * 下載SpectrumInfo
     *
     * @param buildingId
     * @param latestVer
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    SpectrumInfo downSpectrumInfo(int buildingId, int latestVer) throws IOException, URISyntaxException {
        SpectrumInfo info = null;
        String uri = "http://" + this.getSdkHost() + "/get_update_file/" + buildingId;
        File f = NetUtil.downloadFile(uri, this.specsDir, new BasicNameValuePair("ver", latestVer + ""),
                new BasicNameValuePair("m_model", this.getMobileModel()),
                new BasicNameValuePair("appKey", this.getAppKey()));

        if (f == null) {
            return info;
        }

        logger.info("spec_file: {} length:{}", f, f.length());
        if (FileUtil.exist(f)) {

            if(latestSpecs.isEmpty()){
                latestSpecs.addAll(this.listLatestUpdates (0));
            }

            for (SpectrumInfo spec : latestSpecs) {
                if (spec.getUpdateItem().getBuilding_id() == buildingId) {
                    spec.setFile(f.getName());
                    info = spec;
                    break;
                }
            }


        }

        return info;
    }

//    /**
//     * requestServerLatestVersion
//     *
//     * @return
//     * @throws IOException
//     * @throws URISyntaxException
//     */
//    Map<Integer, Integer> requestServerLatestVersion() throws IOException, URISyntaxException {
//        int latestVer = 0;
//        /**
//         * ### get_update_latest_version 获取最新版本频谱版本
//         * http://gaojie:8088/get_update_latest_version
//         * /<buildingId>?f=1&appKey=111&m_model=Nexus 5
//         */
//        Map<Integer, Integer> latestVersion = new LinkedHashMap<Integer, Integer>();
//
//        for (Entry<Integer, SpectrumInfo> entry : getLocalZipSpecs().entrySet()) {
//            Integer buildingId = entry.getKey();
//            SpectrumInfo spectrumInfo = entry.getValue();
//            int localVer = spectrumInfo.getUpdateItem().getVersion();
//
//            String url = "http://" + getSdkHost() + "/get_update_latest_version/" + buildingId;
//
//            String verInfo = NetUtil.httpGET(url, new BasicNameValuePair("f", (isForce() ? "1" : "0")),
//                    new BasicNameValuePair("appKey", getAppKey()), new BasicNameValuePair("m_model", getMobileModel()));
//
//            logger.info("getLatestVersion:{}=>{}", url, verInfo);
//
//            RetVal_LatestVersion versionInfo = JsonUtils.parse(verInfo, RetVal_LatestVersion.class);
//
//            if (versionInfo != null && versionInfo.getC() == 0) {
//                latestVer = versionInfo.getD().getVersion();
//                if (localVer < latestVer) {
//                    // put latest version
//                    latestVersion.put(buildingId, latestVer);
//                }
//            } else {
//                logger.warn("versionInfo:{}", verInfo);
//            }
//        }
//        return latestVersion;
//    }

    private boolean saveSpecsData(SpectrumInfo specInfo) {
        boolean put = false;
        // 记录到
        try {
            File spec =  specInfo.getSpecFile(specsDir);


            if (!FileUtil.exist(spec)) {
                logger.error("specs:{} is exist!!!", spec);
                return put;
            }

            logger.info("insert DB:{}", specInfo.getUpdateItem().getName());
            long insert = insertDB(specInfo);
            File zipSpecFile = new File(this.specsDir, specInfo.getFile());
            if (insert > 0) {
                localZipSpecs.put(specInfo.getUpdateItem().getBuilding_id(), specInfo);
                // delete unzip specs dir
                File specUnZipDir = LocaterUtil.getSpecUnZipDir(zipSpecFile);
                if (specUnZipDir.exists()) {
                    logger.info("deletes :{}", specUnZipDir.getName());
                    LocaterUtil.deletes(specUnZipDir);
                }
                put = true;
            }
        } catch (Exception e) {
            logger.error("saveSpecsData specInfo:{}=>{}", specInfo, e.getMessage());
        }
        return put;
    }

    private long insertDB(SpectrumInfo specInfo) {
        SpectrumInfo oldSpec = localZipSpecs.get(specInfo.getUpdateItem().getBuilding_id());
        if (oldSpec != null) {
            logger.info("delete old:{} version:{}", oldSpec.getUpdateItem().getName(), oldSpec.getUpdateItem().getName());
            sqLiteHelper.delete("BUILDING_ID = " + oldSpec.getUpdateItem().getBuilding_id());
        }
        return sqLiteHelper.insert(specInfo);
    }

    private List<SpectrumInfo> getDBSpecInfos() {
        return sqLiteHelper.query(null);
    }

    public String getMobileModel() {
        return mobileModel;
    }

    public void setMobileModel(String mobileModel) {
        this.mobileModel = mobileModel;
    }

    public String getSdkHost() {
        return sdkHost;
    }

    public File getWorkDir() {
        return workDir;
    }

    public void setWorkDir(File locaterWorkDir) {
        this.workDir = locaterWorkDir;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public int getUpdateState() {
        return updateState;
    }

    public void setUpdateState(int updateState) {
        this.updateState = updateState;
    }

    public Map<Integer, SpectrumInfo> getLocalZipSpecs() {
        return localZipSpecs;
    }

    void setLocalZipSpecs(LinkedHashMap<Integer, SpectrumInfo> localZipSpecs) {
        this.localZipSpecs = localZipSpecs;
    }

    public static Context getCtx() {
        return ctx;
    }

    public static void setCtx(Context ctx) {
        SDKInfo.ctx = ctx;
    }

    public File getSpecsDir() {
        return specsDir;
    }

    public void setSpecsDir(File specsDir) {
        this.specsDir = specsDir;
    }

    public void setSdkHost(String sdkHost) {
        this.sdkHost = sdkHost;
    }
}
