/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.buaa.nlsde.wlan.resource;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.edu.buaa.nlsde.wlan.util.FileUtil;
//import cn.edu.buaa.nlsde.wlan.util.LogUtil;

/**
 *
 * @author lawson
 */
public class ClusterClassMatchTable {

    private static HashMap<String, HashMap> cluster_map = new HashMap<String, HashMap>();

    public static boolean readClusterMatchTable(File cluster_match_table_file) throws Exception {
        	List<String> data_list = FileUtil.readFile(cluster_match_table_file);
        if (data_list.size() < 1) {
            //LogUtil.error("ClusterMatchTable File is empty:"
                    //+ cluster_match_table_file.getPath());
            return false;
        }

        for (int i = 1; i < data_list.size(); i++) {
            String data = data_list.get(i);
            addItem(data);
        }
        return true;
    }

    private static void addItem(String data) {
        String[] items = data.split(",");
        String ap_mac = items[0].trim();
        int cluster_num = Integer.parseInt(items[1].trim());
        ArrayList<String> points = new ArrayList<String>();
        int i = 2;
        while (i < items.length) {
            String point = items[i].trim();
            points.add(point);
            i++;
        }
        HashMap<Integer, ArrayList> cluster_m;
        if (cluster_map.containsKey(ap_mac)) {
            cluster_m = cluster_map.get(ap_mac);
        } else {
            cluster_m = new HashMap<Integer, ArrayList>();
        }
        cluster_m.put(cluster_num, points);
        cluster_map.put(ap_mac, cluster_m);
    }

    /**
     * 检测是否是重载类对应表
     *
     * @return 如果之前存在类对应表数据，返回true,否则返回false
     */
    public static boolean isReload() {
        return !cluster_map.isEmpty();
    }

    /**
     * 清空类对应表数据
     */
    public static void resetClassMatchTable() {
        cluster_map = new HashMap<String, HashMap>();
    }

    public static ArrayList<String> getPointsByMacnNum(String ap_mac, int cluster_num) {
        //LogUtil.info("size=" + cluster_map.size());
        //LogUtil.info("mac=" + ap_mac + ",cluster=" + cluster_num);
        if (cluster_map.containsKey(ap_mac)) {
            HashMap<Integer, ArrayList> cluster_m = cluster_map.get(ap_mac);
            if (cluster_m.containsKey(cluster_num)) {
                return cluster_m.get(cluster_num);
            }
        }
        return new ArrayList<String>();
    }

    /*public static void main(String[] args) {
     String path = "D:/LocationProject/WlanLocationResourcesBD/resource/MatchTable/cluster_match_table_g11_20140624.csv";
     try {
     ClusterClassMatchTable.readClusterMatchTable(new File(path));
     ArrayList result = ClusterClassMatchTable.getPointsByMacnNum("00:23:CD:00:0F:02", 1);
     System.out.println("");
     } catch (Exception ex) {
     Logger.getLogger(ClusterClassMatchTable.class.getName()).log(Level.SEVERE, null, ex);
     }
     }*/
}
