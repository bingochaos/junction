package cn.edu.buaa.nlsde.wlan.cluster;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.nio.charset.Charset;
import com.csvreader.CsvReader;

//以无向图G为入口，得出任意两点之间的路径长度length[i][j]，路径path[i][j][k]，
//途中无连接得点距离用0表示，点自身也用0表示
public class Floyd {

	public static Map<String, Integer> stringToInterger = new HashMap<String, Integer>(); // <30026016,0>
	public static Map<Integer, String> intergerToString = new HashMap<Integer, String>(); // <0,30026016>
	private static Map<String, String> stringToString = new HashMap<String, String>(); // <30006007,G11_39>
	private static int pointNum = 100; // 节点数
	public int[][] length = null; // 任意两点之间路径长度
	public int[][][] path = null; // 任意两点之间的路径

	public Floyd(int[][] G) {
		int MAX = 10000;
		int row = G.length; // 图G的行数
		int[][] spot = new int[row][row];// 定义任意两点之间经过的点
		int[] onePath = new int[row]; // 记录一条路径
		length = new int[row][row];
		path = new int[row][row][];
		for (int i = 0; i < row; i++) // 处理图两点之间的路径
		{
			for (int j = 0; j < row; j++) {
				if (G[i][j] == 0) {
					G[i][j] = MAX;// 没有路径的两个点之间的路径为默认最大
				}
				if (i == j) {
					G[i][j] = 0; // 本身的路径长度为0
				}
			}
		}
		for (int i = 0; i < row; i++) // 初始化为任意两点之间没有路径
		{
			for (int j = 0; j < row; j++) {
				spot[i][j] = -1;
			}
		}
		for (int i = 0; i < row; i++) // 假设任意两点之间的没有路径
		{
			onePath[i] = -1;
		}
		for (int v = 0; v < row; ++v) {
			for (int w = 0; w < row; ++w) {
				length[v][w] = G[v][w];
			}
		}
		for (int u = 0; u < row; ++u) // 检查的所有节点必须放在最外层循环
		{
			for (int v = 0; v < row; ++v) {
				for (int w = 0; w < row; ++w) {
					if (length[v][w] > length[v][u] + length[u][w]) {
						length[v][w] = length[v][u] + length[u][w];// 如果存在更短路径则取更短路径
						spot[v][w] = u; // 把经过的点加入
					}
				}
			}
		}
		for (int i = 0; i < row; i++) { // 求出所有的路径
			int[] point = new int[1];
			for (int j = 0; j < row; j++) {
				point[0] = 0;
				onePath[point[0]++] = i;
				outputPath(spot, i, j, onePath, point);
				path[i][j] = new int[point[0]];
				for (int s = 0; s < point[0]; s++) {
					path[i][j][s] = onePath[s];
				}
			}
		}
	}

	void outputPath(int[][] spot, int i, int j, int[] onePath, int[] point) {
		// 输出i// 到j// 的路径的实际代码，point[]记录一条路径的长度
		if (i == j) {
			return;
		}
		if (spot[i][j] == -1) {
			onePath[point[0]++] = j;
		} // System.out.print(" "+j+" ");
		else {
			outputPath(spot, i, spot[i][j], onePath, point);
			outputPath(spot, spot[i][j], j, onePath, point);
		}
	}

	// public static void readCoordinateToNumber(String filename) {
	// try {
	// ArrayList<String[]> fileList = new ArrayList<String[]>();
	// CsvReader reader = new CsvReader(filename,',',Charset.forName("SJIS"));
	// reader.readHeaders();
	// while(reader.readRecord()){
	// fileList.add(reader.getValues());
	// }
	// reader.close();
	// for (int row = 0; row < fileList.size(); row++) {
	// stringToString.put(fileList.get(row)[1], fileList.get(row)[0]);
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	public static int[][] loadDistanceMatrix(String filename) {
		// readCoordinateToNumber("d:/data/G11.csv");
		int[][] data = null;
		try {
			ArrayList<String[]> fileList = new ArrayList<String[]>();
			CsvReader reader = new CsvReader(filename, ',',
					Charset.forName("SJIS"));
			reader.readHeaders();
			while (reader.readRecord()) {
				fileList.add(reader.getValues());
			}
			reader.close();
			data = new int[pointNum][pointNum]; // 46个点
			// System.out.println(fileList.size()-1);
			// System.out.println(data.length);
			for (int i = 0; i < data.length; i++) {
				for (int j = 0; j < data.length; j++) {
					data[i][j] = 0;
				}
			}
			int count = 0;
			for (int row = 0; row < fileList.size(); row++) {
				String startNode = fileList.get(row)[0];
				String endNode = fileList.get(row)[2];
				Integer start = stringToInterger.get(startNode);
				if (start == null) {
					start = count++;
					stringToInterger.put(startNode, start);
					intergerToString.put(start, startNode);
				}
				Integer end = stringToInterger.get(endNode);
				if (end == null) {
					end = count++;
					stringToInterger.put(endNode, end);
					intergerToString.put(end, endNode);
				}
				int distance = Integer.valueOf(fileList.get(row)[5]);
				data[start][end] = distance;
				data[end][start] = distance;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
}
