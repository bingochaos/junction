/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.buaa.nlsde.wlan.resource;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import cn.edu.buaa.nlsde.wlan.beans.LocationPoint;
import cn.edu.buaa.nlsde.wlan.util.FileUtil;
//import cn.edu.buaa.nlsde.wlan.util.LogUtil;

/**
 *
 * @author lawson
 */
public class LocationTable {
	private static HashMap<String, LocationPoint> location_point_map = new HashMap<String, LocationPoint>();
    private static HashMap<String, LocationPoint> xy_point_map = new HashMap<String, LocationPoint>();

    
    /**
     * 读取location数据
     * @param location_table_file
     * @return
     * @throws IOException 
     * @throws Exception
     */
    public static boolean addLocation(File location_table_file) throws IOException  {
    	List<String> data_list = FileUtil.readFile(location_table_file );
      	if (data_list.size() < 1) {
            return false;
        }
      	
        for (int i = 1; i < data_list.size(); i++) {
            String data = data_list.get(i);
            String[] items = data.split(",");

            String location = items[0].trim();
            int mapid = Integer.parseInt(items[1].trim());
            String name = items[2].trim();
            float x = Float.parseFloat(items[3].trim());
            float y = Float.parseFloat(items[4].trim());

            LocationPoint point = new LocationPoint();
            point.setLocation(location);
            point.setMapid(mapid);
            String loc_id = location + "#" + mapid;
            point.setLocId(loc_id);
            point.setName(name);
            point.setX(x);
            point.setY(y);

            location_point_map.put(loc_id, point);
            xy_point_map.put(x + "#" + y + "#" + mapid, point);

        }
        return true;
    }

    public static LocationPoint getPointByLocId(String locid) {
        if (location_point_map.containsKey(locid)) {
            return location_point_map.get(locid);
        } else {
            return new LocationPoint();
        }
    }

    public static LocationPoint getPointByXY(String XY) {
        if (xy_point_map.containsKey(XY)) {
            return xy_point_map.get(XY);
        } else {
            return new LocationPoint();
        }
    }

    /**
<<<<<<< HEAD
     * 妫�祴鏄惁鏄噸杞界被瀵瑰簲琛�
=======
     * 检测是否是重载类对应表
>>>>>>> 3625bfae963f7a1697264e64c6bd37a46ec585d4
     *
     * @return 如果之前存在类对应表数据，返回true,否则返回false
     */
    public static boolean isReload() {
        return !location_point_map.isEmpty() && !xy_point_map.isEmpty();
    }

    /**
     * 清空类对应表数据
     */
    public static void resetLocationTable() {
        location_point_map = new HashMap<String, LocationPoint>();
        xy_point_map = new HashMap<String, LocationPoint>();
    }
}
