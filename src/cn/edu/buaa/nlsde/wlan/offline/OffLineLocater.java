package cn.edu.buaa.nlsde.wlan.offline;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.edu.buaa.nlsde.wlan.beans.LocationInfo;
import cn.edu.buaa.nlsde.wlan.beans.PhoneWifiMessage;
import cn.edu.buaa.nlsde.wlan.beans.UdpServIn;
import cn.edu.buaa.nlsde.wlan.calculate.LocationEngine;
import cn.edu.buaa.nlsde.wlan.datapre.PhoneReceiveDataProcesser;
import cn.edu.buaa.nlsde.wlan.processer.WlanData;
import cn.edu.buaa.nlsde.wlan.resource.PhoneSpectrum;

public class OffLineLocater {

	private static String LogFile = "offline.txt";
	private static OutputStreamWriter logWriter;
	private WlanData wd = new WlanData();
	private PhoneReceiveDataProcesser rdprocesser = new PhoneReceiveDataProcesser();

	public static boolean LOG_ENABLE=true;
	
	
	/**
	 * 参数为是否开启日志, 默认开启
	 *  logEnable
	 * @param logEnable
	 */
	public OffLineLocater(boolean logEnable) {
		LOG_ENABLE = logEnable;	
	}
	
		public static void appendLog(String log) {
		try {
			
			if(!LOG_ENABLE){
				return ;
			}
			
			long curTime = System.currentTimeMillis();
			String timeStr = new SimpleDateFormat("MM-dd_HH:mm:ss.SSS", Locale.CHINA)
					.format(new Date(curTime));

			if (logWriter == null) {
				//TODO Environment.getExternalStorageDirectory();
				File sdcard = null;
				File directory = new File(sdcard.getAbsolutePath()
						+ "/WifiLocation");
				if (!directory.exists()) {
					directory.mkdirs();
				}

				File file = new File(directory, LogFile);
				if (file.exists()) {

					long lastModified = file.lastModified();
					timeStr = new SimpleDateFormat("MM-dd_HH:mm", Locale.CHINA) .format(new Date(lastModified));

					boolean renameTo = file.renameTo(new File(directory, timeStr+"."+LogFile));

					if (renameTo) {
						file = new File(directory, LogFile);
					}
				}

				logWriter = new OutputStreamWriter(new FileOutputStream(file,
						true));
			}

			logWriter.write(timeStr + ":" + log + "\r\n");
			logWriter.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 定位计算入口
	 * 
	 * @param udp
	 *            手机发送的数据
	 * @return 定位结果集合
	 * @throws Exception
	 */
	LocationEngine engine = new LocationEngine();
	public List<LocationInfo> calcLocation(UdpServIn udp) throws Exception {
		ArrayList<PhoneWifiMessage> pmessage = udp.toPhoneMsgs();
//		for (PhoneWifiMessage msg : pmessage) {
//			// 前面已经检查， 该处则不需要。
//			// if (checkUseful(msg)) {
//			rdprocesser.dataStoreProcess(msg);
//			// }
//		}
//		PhoneReceiveDataProcesser rdp = new PhoneReceiveDataProcesser();
//		rdp.dataPrepare(wd);

		
		List<LocationInfo> list = engine.findPhoneLocation(pmessage);
		return list;
	}
	/**
	 * 定位计算入口
	 * 
	 * @param udp
	 *            手机发送的数据
	 * @return 定位结果集合
	 * @throws Exception
	 */
	
	/*public List<LocationInfo> calcLocation0(UdpServIn udp) throws Exception {
		ArrayList<PhoneWifiMessage> pmessage = udp.toPhoneMsgs();
		for (PhoneWifiMessage msg : pmessage) {
			// 前面已经检查， 该处则不需要。
			// if (checkUseful(msg)) {
			rdprocesser.dataStoreProcess(msg);
			// }
		}
		PhoneReceiveDataProcesser rdp = new PhoneReceiveDataProcesser();
		rdp.dataPrepare(wd);

		LocationEngine engine = new LocationEngine(wd);
		List<LocationInfo> list = engine.findPhoneLocation();
		return list;
	}*/

	private boolean checkUseful(PhoneWifiMessage msg) {
		return PhoneSpectrum.ap_macs.contains(msg.getBssid());
	}

}
