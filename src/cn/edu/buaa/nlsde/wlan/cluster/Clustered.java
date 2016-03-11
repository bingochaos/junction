package cn.edu.buaa.nlsde.wlan.cluster;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.nio.charset.Charset;
import com.csvreader.CsvReader;

public class Clustered {

	private Map<String, String> pointToArea = new HashMap<String, String>();
	private Floyd floyd;
	private String mapId;
	private int pointNum = 0;

	public Clustered(String mapId, String distancePath, String areaPath) {
		readPointToArea(areaPath);
		int[][] data = Floyd.loadDistanceMatrix(distancePath);
		floyd = new Floyd(data);
		this.mapId = mapId;
	}

	public void readPointToArea(String filename) {
		try {
			ArrayList<String[]> fileList = new ArrayList<String[]>();
			CsvReader reader = new CsvReader(filename, ',',
					Charset.forName("SJIS"));
			reader.readHeaders();
			while (reader.readRecord()) {
				fileList.add(reader.getValues());
			}
			reader.close();
			for (int i = 0; i < fileList.size(); i++) {
				String point = fileList.get(i)[0];
				String area = fileList.get(i)[1];
				pointToArea.put(point, area);
			}
			pointNum = fileList.size();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public ArrayList<Point> addFillPoint(ArrayList<Point> al) {
		ArrayList<Point> ret = new ArrayList<Point>();
		String myArea = al.get(0).getArea();
		for (int i = 0; i < al.size(); i++)
			ret.add(al.get(i));
		for (int i = 0; i < al.size(); i++) {
			int start = Floyd.stringToInterger.get(al.get(i).getCoordinate());
			// System.out.println(i);
			for (int j = 0; j < al.size(); j++) {
				if (j == i)
					continue;
				int end = Floyd.stringToInterger.get(al.get(j).getCoordinate());
				// System.out.println("start:"+start);
				// System.out.println("end:"+end);
				// System.out.println(floyd.path[start][end].length);
				for (int k = 1; k < floyd.path[start][end].length - 1; k++) {
					// System.out.println(k);
					String s = Floyd.intergerToString
							.get(floyd.path[start][end][k]);
					if (!isExist(ret, s)) {
						String area = pointToArea.get(s);
						if (myArea.equals(area)) {
							Point p = new Point(s, area);
							ret.add(p);
						}
					}
				}
			}
		}
		return ret;
	}

	public boolean isExist(ArrayList<Point> al, String s) {
		for (int i = 0; i < al.size(); i++) {
			Point p = al.get(i);
			String str = p.getCoordinate();
			if (str.equals(s))
				return true;
		}
		return false;
	}

	public ArrayList<String> excludeMapId(ArrayList<String> al) {
		ArrayList<String> ret = new ArrayList<String>();
		for (int i = 0; i < al.size(); i++) {
			String[] strArray = al.get(i).split("#");
			ret.add(strArray[0]);
		}
		return ret;
	}

	public ArrayList<String> includeMapId(ArrayList<String> al) {
		ArrayList<String> ret = new ArrayList<String>();
		for (int i = 0; i < al.size(); i++) {
			String str = al.get(i) + "#" + mapId;
			ret.add(str);
		}
		return ret;
	}

	public ArrayList<ArrayList<String>> getUnion(
			ArrayList<String> alContainsMapId) {
		ArrayList<String> al = excludeMapId(alContainsMapId);
		ArrayList<ArrayList<Point>> ret = new ArrayList<ArrayList<Point>>();
		ArrayList<ArrayList<String>> strRet = new ArrayList<ArrayList<String>>();
		// ArrayList<ArrayList<String>> strRetContainsMapId = new
		// ArrayList<ArrayList<String>>();
		ArrayList<Point> pointAl = getStrToPoint(al);
		ArrayList<ArrayList<Point>> areaAl = getAreaUnion(pointAl);
		for (int i = 0; i < areaAl.size(); i++) {
			ArrayList<Point> al1 = areaAl.get(i);
			ArrayList<ArrayList<Point>> al2 = getProperFormat(al1);
			ArrayList<ArrayList<Point>> al3 = getDistanceUnion(al2);
			for (int j = 0; j < al3.size(); j++) {
				ArrayList<Point> al4 = al3.get(j);
				ArrayList<Point> al5 = addFillPoint(al4);
				ret.add(al5);
			}
		}
		for (int i = 0; i < ret.size(); i++) {
			ArrayList<Point> al6 = ret.get(i);
			ArrayList<String> strAl = new ArrayList<String>();
			for (int j = 0; j < al6.size(); j++) {
				strAl.add(al6.get(j).getCoordinate() + "#" + mapId);
			}
			strRet.add(strAl);
		}
		return strRet;
	}

	public ArrayList<Point> getStrToPoint(ArrayList<String> al) {
		ArrayList<Point> ret = new ArrayList<Point>();
		for (int i = 0; i < al.size(); i++) {
			String coordinate = al.get(i);
			String area = pointToArea.get(coordinate);
			Point p = new Point(coordinate, area);
			ret.add(p);
		}
		return ret;
	}

	public ArrayList<ArrayList<Point>> getProperFormat(ArrayList<Point> al) {
		ArrayList<ArrayList<Point>> ret = new ArrayList<ArrayList<Point>>();
		for (int i = 0; i < al.size(); i++) {
			ArrayList<Point> al1 = new ArrayList<Point>();
			al1.add(al.get(i));
			ret.add(al1);
		}
		return ret;
	}

	/**
	 * 根据区域划分为不同的子集
	 * 
	 * @param all
	 * @return
	 */
	public ArrayList<ArrayList<Point>> getAreaUnion(ArrayList<Point> all) {
		Map<String, ArrayList<Point>> areaSet = new HashMap<String, ArrayList<Point>>();
		ArrayList<ArrayList<Point>> ret = new ArrayList<ArrayList<Point>>();
		for (int i = 0; i < all.size(); i++) {
			String area = all.get(i).getArea();
			ArrayList<Point> al = areaSet.get(area);
			if (al == null) {
				al = new ArrayList<Point>();
				areaSet.put(area, al);
			}
			al.add(all.get(i));
		}

		Set<String> keys = areaSet.keySet();
		for (String area : keys) {
			ArrayList<Point> al1 = areaSet.get(area);
			ret.add(al1);
		}
		return ret;
	}

	public boolean isNeighbour(Point p1, Point p2) {
		String area1 = p1.getArea();
		String area2 = p2.getArea();
		if (!area1.equals(area2))
			return false;
		String coordinate1 = p1.getCoordinate();
		String coordinate2 = p2.getCoordinate();
		int start = Floyd.stringToInterger.get(coordinate1);
		int end = Floyd.stringToInterger.get(coordinate2);
		if (floyd.length[start][end] <= 8)
			return true;
		return false;
	}

	/**
	 * 聚簇的标准，如果两个点在8m以内则认为属于一个簇
	 * 
	 * @param all
	 * @return
	 */
	public ArrayList<ArrayList<Point>> getDistanceUnion(
			ArrayList<ArrayList<Point>> all) {// 根据坐标聚集，8m以内算连续
		int i = 0;
		while (i < all.size()) {
			boolean adjustment = false;
			ArrayList<Point> cmp1 = all.get(i);
			for (int j = 0; j < all.size(); j++) {
				if (j == i)
					continue;
				ArrayList<Point> cmp2 = all.get(j);
				for (Point p1 : cmp1) {
					for (Point p2 : cmp2) {
						// System.out.println("judge");
						if (isNeighbour(p1, p2)) {// 如果存在邻近点
							// System.out.println("yes");
							for (int m = 0; m < cmp2.size(); m++)
								cmp1.add(cmp2.get(m));
							all.remove(cmp2);
							adjustment = true;// 调整过
							break;
						} else
							adjustment = false;// 未调整过
					}
					if (adjustment)
						break;
				}
				if (adjustment)
					break;
			}
			if (!adjustment)
				i++;
		}
		return all;
	}

}
