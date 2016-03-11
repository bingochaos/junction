package cn.edu.buaa.nlsde.wlan.cluster;

import java.io.File;
import java.util.ArrayList;

public class Clustereds {
	private static int mapNums;
	private static String distancePath;
	private static String areaPath;
	private static Clustered[] c;

	public static boolean initial(String distance_path, String area_path) {
		distancePath = distance_path;
		areaPath = area_path;
		mapNums = getFilesNum();
		c = new Clustered[mapNums + 1];
		for (int i = 1; i < mapNums + 1; i++) {
			String str1 = distancePath + "/" + i + ".csv";
			String str2 = areaPath + "/" + i + ".csv";
			c[i] = new Clustered(String.valueOf(i), str1, str2);
		}
		return true;
	}

	public static int getFilesNum() {
		String path = distancePath;
		File file = new File(path);
		String[] files = file.list();
		// System.out.println(files.length);
		return files.length;
	}

	public static ArrayList<ArrayList<String>> getUnion(ArrayList<String> al) {
		String str = al.get(0);
		String[] strs = str.split("#");
		int k = Integer.valueOf(strs[1]);
		return c[k].getUnion(al);
	}

	/*
	 * public static void main(String[] args) { String folderPath = "d:/data/";
	 * String distancePath = "distance"; String areaPath =
	 * "pointCorrespondArea"; Clustereds.initial(); ArrayList<String> al = new
	 * ArrayList<String>(); al.add("30046007#2"); al.add("30042009#2");
	 * al.add("30047011#2"); al.add("30047014#2"); al.add("30045014#2");
	 * ArrayList<ArrayList<String>> all = Clustereds.getUnion(al); for (int i =
	 * 0; i < all.size(); i++) { System.out.println(i); ArrayList<String> all1 =
	 * all.get(i); for (int j = 0; j < all1.size(); j++) {
	 * System.out.println(all1.get(j)); } } }
	 */
}
