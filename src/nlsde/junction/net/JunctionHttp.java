/**
 * JunctionHttp.java
 * 
 * @Description: 
 * 
 * @File: JunctionHttp.java
 * 
 * @Package nlsde.junction.net
 * 
 * @Author chaos
 * 
 * @Date 2014-11-28下午4:53:32
 * 
 * @Version V1.0
 */
package nlsde.junction.net;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.baidu.mapapi.model.LatLng;

/**
 * @author chaos
 * 
 */
public class JunctionHttp {

	private static String initurl = "http://WWW.tanzhibat.com:8040/TrHub/init";//http://115.29.149.25:8040/TrHub/init
	// 基础URL
	public static String base_url = "http://115.29.149.25:8040/TrHub/V0";// http://data.tanzhibat.com:8040/
	private static String token; // 超时协议
	private static String curVer; // 当前版本
	private static String apkUrl; // APK 地址
	private static int force; // 是否需要强制升级 1表示需要强制升级
	public static String ver;
	public static String mac;
	// 1.1枢纽列表
	private static String hublist_url = "/hubList";
	// 1.2请求距当前经纬度最近的枢纽
	private static String hubnearby_url = "/hubNearby";
	// 2.1 请求首页banner，返回图片URL和相应链接
	private static String mainbanner_url = "/mainBanner";
	// 3.1请求枢纽圈banner，返回图片URL和条目ID
	private static String adbanner_url = "/adBanner";
	// 4.1请求条目列表，包括各条目的itemId、标题、小图、描述摘要
	private static String itemlist_url = "/itemList";
	// 4.2请求条目详细信息，包括标题、发布时间、浏览量、大图、详细内容
	private static String iteminfo_url = "/itemInfo";
	// 5.1请求某枢纽的公交线路列表，基础数据包括线路名称、始发站与终点站、首末车时间
	private static String buslist_url = "/busList";
	// 5.2请求某线路车站列表
	private static String statlist_url = "/transInfo";
	// 5.3请求某线路实时信息，返回即将到达该枢纽站的最近3辆车的预计到达时间
	private static String busstatus_url = "/busStatus";
	// 6.1请求公共自行车网点列表，基础数据包括网点编号、网点名称、网点地址、经纬度
	private static String bikelist_url = "/bikeList";
	// 6.2请求某网点实时数据，包括数据发布时间、锁车器总数、剩余车数量信息
	private static String bikestatus_url = "/bikeStatus";
	// 7.1请求机场大巴信息，基础数据包括线路名称、始发站与终点站、首末车时间
	private static String airportbus_url = "/airportBus";
	// 8.1请求机场快轨信息，基础数据包括线路名称、始发站与终点站、首末车时间
	private static String airportexp_url = "/airportExp";
	// 9.1请求长途汽车列表，基础数据包括车次、线路名称、始发站名称、目的地名称、座位数、总里程数
	private static String coachlist_url = "/coachBasic";
	// 9.2请求长途汽车到达站的车站列表，返回数据包括列表中显示的caption字段以及选中后回填至首页的name字段
	private static String coachstate_url = "/coachStat";
	// 9.3请求长途汽车详细信息，数据包括发车时间、车次、线路名称、票价、座位数、剩余票数、公里数
	private static String coachInfo_url = "/coachInfo";
	// 请求室内定位结果，返回mapId和(lat,lon)经纬度信息以及时间戳
	private static String location_url = "/location";
	// 10.1根据室内定位结果中的mapId，请求切换地图时的floorId等信息
	private static String map2floor_url = "/map2Floor";
	// 10.2请求室内地图楼层列表，返回切换地图时的floorId等信息
	private static String floorlist_url = "/floorList";
	// 10.3请求POI详细信息（当地图上点击POI区域高亮显示时）
	private static String poiSelected_url = "/poiSelected";
	// 10.4请求商家详细信息
	private static String shopinfo_url = "/shopInfo";
	// 10.5根据poiId请求切换地图时的floorId以及路线规划时的mapName
	private static String poi2map_url = "/poi2Floor";
	// 11.1根据请求的关键字，返回搜索结果，列表中的条目有3种类型
	private static String hubsearch_url = "/hubSearch";
	// 11.2室内路线规划中，起点或终点搜索POI时，根据关键字，返回搜索结果的名称、中心点坐标和楼层信息
	private static String poisearch_url = "/poiSearch";

	// 0.1 初始化
	public static void init() throws Exception {
		HashMap<String, String> rawParams = new HashMap<String, String>();

		rawParams.put("ver", ver + "");
		rawParams.put("mac", mac + "");
		JSONObject jsonObject = new JSONObject(HttpUtil.postRequest(initurl,
				rawParams));
		token = jsonObject.getJSONObject("data").getString("token");
		base_url = "http://"
				+ jsonObject.getJSONObject("data").getString("base");
		curVer = jsonObject.getJSONObject("data").getString("curVer");
		apkUrl = "http://"
				+ jsonObject.getJSONObject("data").getString("apkUrl");
		force = jsonObject.getJSONObject("data").getInt("force");

	}

	// 1.1
	public static JSONObject gethublist() throws Exception {
		HashMap<String, String> rawParams = new HashMap<String, String>();
		rawParams.put("token", token);
		return new JSONObject(HttpUtil.postRequest(base_url + hublist_url,
				rawParams));
	}

	// 1.2
	public static JSONObject gethubnearby(LatLng latLng) throws Exception {
		HashMap<String, String> rawParams = new HashMap<String, String>();
		rawParams.put("lon", latLng.longitude + "");
		rawParams.put("lat", latLng.latitude + "");
		rawParams.put("token", token);
		return new JSONObject(HttpUtil.postRequest(base_url + hubnearby_url,
				rawParams));
	}

	// 2.1
	public static JSONObject getmainBanner(int hid) throws Exception {
		HashMap<String, String> rawParams = new HashMap<String, String>();
		rawParams.put("hid", hid + "");
		rawParams.put("token", token);
		return new JSONObject(HttpUtil.postRequest(base_url + mainbanner_url,
				rawParams));
	}

	// 3.1
	public static JSONObject getadBanner(int hid) throws JSONException,
			Exception {
		HashMap<String, String> rawParams = new HashMap<String, String>();
		rawParams.put("hid", hid + "");
		rawParams.put("token", token);
		return new JSONObject(HttpUtil.postRequest(base_url + adbanner_url,
				rawParams));
	}

	// 4.1
	public static JSONObject getItemlist(int hid,int page) throws Exception {
		HashMap<String, String> rawParams = new HashMap<String, String>();
		rawParams.put("hid", hid + "");
		rawParams.put("token", token);
		rawParams.put("page", page+"");
		return new JSONObject(HttpUtil.postRequest(base_url + itemlist_url,
				rawParams));
	}

	// 4.2
	public static JSONObject getIteminfo(int itemid) throws Exception {
		HashMap<String, String> rawParams = new HashMap<String, String>();
		rawParams.put("itemId", itemid + "");
		rawParams.put("token", token);
		return new JSONObject(HttpUtil.postRequest(base_url + iteminfo_url,
				rawParams));
	}

	// 5.1
	public static JSONObject getBuslist(int hid, int page) throws Exception {
		HashMap<String, String> rawParams = new HashMap<String, String>();
		rawParams.put("hid", hid + "");
		rawParams.put("page", page + "");
		rawParams.put("token", token);
		return new JSONObject(HttpUtil.postRequest(base_url + buslist_url,
				rawParams));
	}

	// 5.3
	public static JSONObject getStatlist(int hid, String updown, String route)
			throws Exception {
		HashMap<String, String> rawParams = new HashMap<String, String>();
		rawParams.put("hid", hid + "");
		rawParams.put("updown", updown + "");
		rawParams.put("route", route + "");
		rawParams.put("token", token);
		return new JSONObject(HttpUtil.postRequest(base_url + statlist_url,
				rawParams));
	}

	// 5.3
	public static JSONObject getBusstatus(int hid, String updown, String route)
			throws Exception {
		HashMap<String, String> rawParams = new HashMap<String, String>();
		rawParams.put("hid", hid + "");
		rawParams.put("updown", updown + "");
		rawParams.put("route", route + "");
		rawParams.put("token", token);
		return new JSONObject(HttpUtil.postRequest(base_url + busstatus_url,
				rawParams));
	}

	// 6.1
	public static JSONObject getBikelist(int hid) throws Exception {
		HashMap<String, String> rawParams = new HashMap<String, String>();
		rawParams.put("hid", hid + "");
		rawParams.put("token", token);
		return new JSONObject(HttpUtil.postRequest(base_url + bikelist_url,
				rawParams));
	}

	// 6.2
	public static JSONObject getBikeStatus(String statCode) throws Exception {
		HashMap<String, String> rawParams = new HashMap<String, String>();
		rawParams.put("statCode", statCode);
		rawParams.put("token", token);
		return new JSONObject(HttpUtil.postRequest(base_url + bikestatus_url,
				rawParams));
	}

	// 7.1
	public static JSONObject getAirportBus(int hid, int page) throws Exception {
		HashMap<String, String> rawParams = new HashMap<String, String>();
		rawParams.put("hid", hid + "");
		rawParams.put("page", page + "");
		rawParams.put("token", token);
		return new JSONObject(HttpUtil.postRequest(base_url + airportbus_url,
				rawParams));
	}

	// 8.1
	public static JSONObject getAirportExp(int hid) throws Exception {
		HashMap<String, String> rawParams = new HashMap<String, String>();
		rawParams.put("hid", hid + "");
		rawParams.put("token", token);
		return new JSONObject(HttpUtil.postRequest(base_url + airportexp_url,
				rawParams));
	}

	// 9.1
	public static JSONObject getCoachList(int hid) throws Exception {
		HashMap<String, String> rawParams = new HashMap<String, String>();
		rawParams.put("hid", hid + "");
		rawParams.put("token", token);
		return new JSONObject(HttpUtil.postRequest(base_url + coachlist_url,
				rawParams));
	}

	// 9.2
	public static JSONObject getCoachState(int hid,String kw,int page) throws Exception {
		HashMap<String, String> rawParams = new HashMap<String, String>();
		rawParams.put("hid", hid + "");
		rawParams.put("kw", kw);
		rawParams.put("page", page+"");
		rawParams.put("token", token);
		return new JSONObject(HttpUtil.postRequest(base_url + coachstate_url,
				rawParams));
	}

	// 9.3
	public static JSONObject getCoachInfo(int hid,int daySeq,String dest) throws Exception {
		HashMap<String, String> rawParams = new HashMap<String, String>();
		rawParams.put("hid", hid + "");
		rawParams.put("daySeq", daySeq + "");
		rawParams.put("dest", dest + "");
		rawParams.put("token", token);
		return new JSONObject(HttpUtil.postRequest(base_url + coachInfo_url,
				rawParams));
	}

	// 10.1
	public static JSONObject getMap2Floor(String mapid) throws Exception {
		HashMap<String, String> rawParams = new HashMap<String, String>();
		rawParams.put("mapId", mapid );
		rawParams.put("token", token);
		return new JSONObject(HttpUtil.postRequest(base_url + map2floor_url,
				rawParams));
	}

	// public static JSONObject getLocationHttp(String mac) throws Exception {
	// HashMap<String, String> rawParams = new HashMap<String, String>();
	// rawParams.put("mac", mac + "");
	// rawParams.put("token", token);
	// return new JSONObject(HttpUtil.postRequest(base_url + location_url,
	// rawParams));
	// }

	// 10.2
	public static JSONObject getFloorList(int hid) throws Exception {
		HashMap<String, String> rawParams = new HashMap<String, String>();
		rawParams.put("hid", hid + "");
		rawParams.put("token", token);
		return new JSONObject(HttpUtil.postRequest(base_url + floorlist_url,
				rawParams));
	}

	// 10.3
	public static JSONObject getPoiSelected(String poiid) throws Exception {
		HashMap<String, String> rawParams = new HashMap<String, String>();
		rawParams.put("poiId", poiid + "");
		rawParams.put("token", token);
		return new JSONObject(HttpUtil.postRequest(base_url + poiSelected_url,
				rawParams));
	}

	// 10.4
	public static JSONObject getShopInfo(int shopid) throws Exception {
		HashMap<String, String> rawParams = new HashMap<String, String>();
		rawParams.put("shopId", shopid + "");
		rawParams.put("token", token);
		return new JSONObject(HttpUtil.postRequest(base_url + shopinfo_url,
				rawParams));
	}

	// 10.5
	public static JSONObject getpoi2floor(String poiid) throws Exception {
		HashMap<String, String> rawParams = new HashMap<String, String>();
		rawParams.put("poiId", poiid);
		rawParams.put("token", token);
		return new JSONObject(HttpUtil.postRequest(base_url + poi2map_url,
				rawParams));
	}

	// 11.1
	public static JSONObject getHubSearch(int hid, String kw) throws Exception {
		HashMap<String, String> rawParams = new HashMap<String, String>();
		rawParams.put("hid", hid + "");
		rawParams.put("token", token);
		rawParams.put("kw", kw);
		return new JSONObject(HttpUtil.postRequest(base_url + hubsearch_url,
				rawParams));
	}

	// 11.2
	public static JSONObject getPoiSearch(int hid, String kw) throws Exception {
		HashMap<String, String> rawParams = new HashMap<String, String>();
		rawParams.put("hid", hid + "");
		rawParams.put("token", token);
		rawParams.put("kw", kw);
		return new JSONObject(HttpUtil.postRequest(base_url + poisearch_url,
				rawParams));
	}
}
