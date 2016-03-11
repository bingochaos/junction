package cn.com.navia.sdk.locater;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import cn.com.navia.sdk.bean.SpectrumInfo;
import cn.com.navia.sdk.exceptions.LocaterException;
import cn.com.navia.sdk.locater.services.LocaterService;
import cn.com.navia.sdk.utils.NetUtil;


/**
 * LocaterManagerFactory
 *
 * @author gaojie
 */
public class LocaterManagerFactory {

    private static Logger logger = LoggerFactory.getLogger(LocaterManagerFactory.class);

    private static Map<SDKInfo, LocaterManager> managerMap = new LinkedHashMap<SDKInfo, LocaterManager>();

    public static LocaterManager getLocaterManager(SDKInfo sdkInfo, int buildingId, LocaterService locaterService) throws LocaterException {
        LocaterManager locManager = managerMap.get(sdkInfo);
//		if (locManager == null) {
//			// init manager
//			logger.info("initialLocaterManager");
        try {
            locManager = initialLocaterManager(sdkInfo, buildingId, locaterService);
        } catch (IOException e) {
            throw new LocaterException("initialLocaterManager", e);
        }
//			managerMap.put(sdkInfo, locManager);
//		}
        return locManager;
    }

    //TODO destroy Locater Manager
    public static void destoryLocaterManager() {

    }

    /**
     * initialManager
     *
     * @param sdkInfo
     * @return
     * @throws LocaterException
     * @throws IOException
     */
    private static LocaterManager initialLocaterManager(SDKInfo sdkInfo, int buildingId, LocaterService service) throws LocaterException, IOException {
        LocaterManager locManager = new LocaterManager(sdkInfo, buildingId, service);
        logger.info("Create LocaterManager:{}", locManager);
        return locManager;
    }

//	/**
//	 * 下载最新的频谱库
//	 * @param sdkInfo
//	 * @return
//	 * @throws IOException 
//	 * @throws URISyntaxException 
//	 */
//	private static Map<Integer, File> downloadUpdate(SDKInfo sdkInfo, Map<Integer, Integer> latestVersionMap) throws IOException, URISyntaxException {
//		//http://gaojie:8088/get_update_file/<buildingId>?ver=<version>&m_model=<mobile_model>&appKey=111
//		Map<Integer, File> retval = new LinkedHashMap<Integer, File>();
//		for(Entry<Integer, Integer>  e: latestVersionMap.entrySet()){
//			Integer buildingId = e.getKey();
//			Integer latestVer = e.getValue();
//
//			String uri = "http://"+sdkInfo.getSdkHost()+"/get_update_file/"+buildingId;
//			
//			File f = NetUtil.downloadFile(uri, sdkInfo.getWorkDir(), new BasicNameValuePair("ver", latestVer+""),
//					new BasicNameValuePair("m_model", sdkInfo.getMobileModel()),
//					new BasicNameValuePair("appKey", sdkInfo.getAppKey())
//					);
//			logger.info("down update:{}=>{}", uri, f);
//			retval.put(buildingId, f);
//		}
//		return retval;
//	}
//	

    /**
     * 获取服务器最新版本号
     *
     * @param sdkInfo
     * @throws IOException
     * @throws URISyntaxException
     */
    private static Map<Integer, Integer> getLatestVersion(SDKInfo sdkInfo, int force) throws IOException, URISyntaxException {
        int latestVer = 0;
        /**
         * ### get_update_latest_version 获取最新版本频谱版本
         http://gaojie:8088/get_update_latest_version/<buildingId>?f=1&appKey=111&m_model=Nexus 5
         */
        Map<Integer, Integer> latestVersion = new LinkedHashMap<Integer, Integer>();

        for (Entry<Integer, SpectrumInfo> entry : sdkInfo.getLocalZipSpecs().entrySet()) {
            Integer buildingId = entry.getKey();
            SpectrumInfo spectrumInfo = entry.getValue();
            int localVer = spectrumInfo.getUpdateItem().getVersion();

            String url = "http://" + sdkInfo.getSdkHost() + "/get_update_latest_version/" + buildingId;

            String verInfo = NetUtil.httpGET(url,
                    new BasicNameValuePair("f", force+""),
                    new BasicNameValuePair("appKey", sdkInfo.getAppKey()),
                    new BasicNameValuePair("m_model", sdkInfo.getMobileModel())
            );

            logger.info("getLatestVersion:{}=>{}", url, verInfo);
            if (verInfo != null) {
                JsonObject jsonObject = new Gson().fromJson(verInfo, JsonObject.class);
                if (jsonObject.get("c").getAsInt() == 0) {
                    latestVer = jsonObject.getAsJsonObject("d").get("version").getAsInt(); //retJson.getJSONObject("d").getInt("version");
                    if (localVer < latestVer) {
                        //put latest version
                        latestVersion.put(buildingId, latestVer);
                    }
                }
            }
        }
        return latestVersion;
    }
}



