/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.buaa.nlsde.wlan.resource;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import cn.edu.buaa.nlsde.wlan.util.FileUtil;
//import cn.edu.buaa.nlsde.wlan.util.LogUtil;

/**
 * 类对应表的相关操作
 *
 * @author lawson
 */
public class PointClassMatchTable {

    private static HashMap<Integer, String> class_location_map = new HashMap<Integer, String>();
    private static HashMap<String, Integer> location_class_map = new HashMap<String, Integer>();
    //private static HashMap<String, String> id_name_map = new HashMap<String, String>();

    public static boolean readClassMatchTable(File class_match_table_file) throws Exception {
        List<String> data_list = FileUtil.readFile(class_match_table_file);
        if (data_list.size() < 1) {
            //LogUtil.error("ClassMatchTable File is empty:"
                    //+ class_match_table_file.getPath());
            return false;
        }

        for (int i = 1; i < data_list.size(); i++) {
            String data = data_list.get(i);
            String[] items = data.split(",");
            int class_index = Integer.parseInt(items[0].trim());
            String place = items[1].trim();
            String mapid = items[2].trim();
            String place_mapid = place + "#" + mapid;
            //String place_name = items[3].trim();

            class_location_map.put(class_index, place_mapid);
            location_class_map.put(place_mapid, class_index);
            //id_name_map.put(place_mapid, place_name);
        }
        return true;
    }

    public static String getLocationByIndex(int index) {
        if (class_location_map.containsKey(index)) {
            return class_location_map.get(index);
        } else {
            return "no such place";
        }
    }

    public static int getIndexByLocation(String location) {
        if (location_class_map.containsKey(location)) {
            return location_class_map.get(location);
        } else {
            return -1;
        }
    }

    /*public static String getNameById(String place_mapid) {
     if (id_name_map.containsKey(place_mapid)) {
     return id_name_map.get(place_mapid);
     } else {
     return "no such place";
     }
     }*/
    /**
     * 检测是否是重载类对应表
     *
     * @return 如果之前存在类对应表数据，返回true,否则返回false
     */
    public static boolean isReload() {
        return !class_location_map.isEmpty() && !location_class_map.isEmpty();
    }

    /**
     * 清空类对应表数据
     */
    public static void resetClassMatchTable() {
        class_location_map = new HashMap<Integer, String>();
        location_class_map = new HashMap<String, Integer>();
    }

    /*public static void main(String[] args) {
     String path = "D:/LocationProject/WlanLocationResourcesBD/resource/MatchTable/match_table_g11_20140605.csv";
     File in_file = new File(path);
     try {
     PointClassMatchTable.readClassMatchTable(in_file);
     } catch (Exception ex) {
     Logger.getLogger(PointClassMatchTable.class.getName()).log(Level.SEVERE, null, ex);
     }
     System.out.println("");
     }*/
}
