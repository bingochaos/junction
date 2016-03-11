package cn.edu.buaa.nlsde.wlan.algorithm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.edu.buaa.nlsde.wlan.beans.LocationInfo;
import cn.edu.buaa.nlsde.wlan.beans.PhoneWifiMessage;
import cn.edu.buaa.nlsde.wlan.calculate.PhoneFirstRoundLocation;
import cn.edu.buaa.nlsde.wlan.offline.OffLineLocater;

public class BasicAlgorithm {
	
	private String file = "C://Users//chen//Desktop//wlan测试//5w2//5w2//88_32_9B_27_6D_7C.20141021145822.csv";
	
	private String[] header;
	private List<Integer[]> form = new ArrayList<Integer[]>();
	private Map<String, PhoneWifiMessage> map = new HashMap<String, PhoneWifiMessage>();

	public BasicAlgorithm(List<PhoneWifiMessage> list)
			throws Exception {
		
		Set<String> location_set = new HashSet<String>();
		List<List<String>> table = new ArrayList<List<String>>();
		List<Integer[]> group = new ArrayList<Integer[]>();

		for (int i = 0; i < list.size(); i++)
			map.put(list.get(i).getBssid(), list.get(i));

		for (int i = 0; i < 5; i++) {
			for (int j = i + 1; j < 5; j++) {
				for (int k = j + 1; k < 5; k++) {
					List<PhoneWifiMessage> sub_list = new ArrayList<PhoneWifiMessage>();
					sub_list.add(list.get(i));
					sub_list.add(list.get(j));
					sub_list.add(list.get(k));
					String dev_mac = sub_list.get(0).getPhoneMac();
					HashMap<String, PhoneWifiMessage> apdata_map = new HashMap<String, PhoneWifiMessage>();
					for (int m = 0; m < sub_list.size(); m++)
						apdata_map.put(sub_list.get(m).getBssid(),
								sub_list.get(m));
					PhoneFirstRoundLocation first = new PhoneFirstRoundLocation();
					LocationInfo info1 = first
							.getPlaceSets(dev_mac, apdata_map);
					if (info1.getPlaceList() == null) {
						continue;
					}
					List<String> location_list = info1.getPlaceList();
					location_set.addAll(location_list);
					table.add(location_list);
					Integer[] integer = new Integer[3];
					integer[0] = i;
					integer[1] = j;
					integer[2] = k;
					group.add(integer);

				}
			}
		}

		List<String> location_list = new ArrayList<String>(location_set);
		header = new String[location_list.size() + 5];

		for (int i = 0; i < header.length; i++) {
			if (i < 5)
				header[i] = list.get(i).getBssid();
			else
				header[i] = location_list.get(i - 5);
		}

		for (int i = 0; i < table.size(); i++) {
			Integer[] integer = new Integer[header.length];
			Integer[] group_integer = group.get(i);
			for (int m = 0; m < header.length; m++)
				integer[m] = 0;
			integer[group_integer[0]] = 1;
			integer[group_integer[1]] = 1;
			integer[group_integer[2]] = 1;
			List<String> table_row = table.get(i);
			for (int q = 0; q < location_list.size(); q++) {
				if (table_row.contains(location_list.get(q)))
					integer[q + 5] = 1;
			}
			form.add(integer);
		}
	}

	public void readFile() throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = br.readLine();
		header = line.split(",");
		while ((line = br.readLine()) != null) {
			String[] str = line.split(",");
			Integer[] array = getIntegerArray(str);
			form.add(array);
		}
		br.close();
	}

	public Integer[] getIntegerArray(String[] str) {
		Integer[] ret = new Integer[str.length];
		for (int i = 0; i < str.length; i++) {
			ret[i] = Integer.parseInt(str[i]);
		}
		return ret;
	}

	/**
	 * 按列求交集,默认的表为初始读入表格 返回共同的点集
	 * 
	 * @return
	 */
	public Set<String> getIntersectionLocation() {
		Set<String> ret = new HashSet<String>();
		Integer[] bool = form.get(0);
		for (int i = 5; i < header.length; i++) {
			for (int j = 1; j < form.size(); j++) {
				bool[i] &= form.get(j)[i];
			}
		}
		for (int i = 5; i < header.length; i++) {
			if (1 == bool[i])
				ret.add(header[i]);
		}
		return ret;
	}

	/**
	 * 按列求交集
	 * 
	 * @param form为经过筛选过的表
	 * @return 返回共同的点集
	 */
	public Set<String> getIntersectionLocation(List<Integer[]> form) {
		Set<String> ret = new HashSet<String>();
		Integer[] bool = form.get(0);
		for (int i = 5; i < header.length; i++) {
			for (int j = 1; j < form.size(); j++) {
				bool[i] &= form.get(j)[i];
			}
		}
		for (int i = 5; i < header.length; i++) {
			if (1 == bool[i])
				ret.add(header[i]);
		}
		return ret;
	}

	/**
	 * 得到不包括abnormal_ap结果的点集
	 * 
	 * @param abnormal_ap
	 *            异常ap数组
	 * @return
	 */
	public Set<String> getLocationExcludeAbnormalAP(List<String> abnormal_ap) {
		Set<String> ret = new HashSet<String>();
		for (int i = 0; i < abnormal_ap.size(); i++) {
			// 每次排除一个异常AP,计算剩余AP的共集。
			String abnormal_ap_name = abnormal_ap.get(i);
			List<Integer[]> new_form = new ArrayList<Integer[]>();
			int j = 0;
			for (j = 0; j < 5; j++) {
				if (abnormal_ap_name.equals(header[j]))
					break;
			}
			for (int k = 0; k < form.size(); k++) {
				if (0 == form.get(k)[j])
					new_form.add(form.get(k));
			}
			Set<String> set = getIntersectionLocation(new_form);
			ret.addAll(set);
		}
		return ret;
	}

	/**
	 * 得到不包括pair_abnormal_ap结果的点集
	 * 
	 * @param pair_abnormal_ap
	 * @return
	 */
	public Set<String> getLocationExcludePairAbnormalAP(
			List<String[]> pair_abnormal_ap) {
		Set<String> ret = new HashSet<String>();
		for (int i = 0; i < pair_abnormal_ap.size(); i++) {
			// 每次排除一对异常AP,计算剩余AP的共集。
			String[] abnormal_ap_name = pair_abnormal_ap.get(i);
			List<Integer[]> new_form = new ArrayList<Integer[]>();
			int j = 0;
			for (j = 0; j < 5; j++) {
				if (abnormal_ap_name[0].equals(header[j]))
					break;
			}
			int m = 0;
			for (m = 0; m < 5; m++) {
				if (abnormal_ap_name[1].equals(header[m]))
					break;
			}
			for (int k = 0; k < form.size(); k++) {
				if (0 == form.get(k)[j] && 0 == form.get(k)[m])
					new_form.add(form.get(k));
			}
			Set<String> set = getIntersectionLocation(new_form);
			ret.addAll(set);
		}
		return ret;
	}

	/**
	 * 
	 * @return 返回缺少的组合数
	 */
	public List<Integer[]> getLostGroup() {
		List<Integer[]> ret = new ArrayList<Integer[]>();
		Set<String> sub_collection = getCurrentCombination();
		Set<String> full_collection = generateCombination();
		Set<String> set = getComplementSet(full_collection, sub_collection);
		List<String> array = new ArrayList<String>(set);
		for (int i = 0; i < array.size(); i++) {
			String[] str = array.get(i).split(",");
			Integer[] integer = getIntegerArray(str);
			ret.add(integer);
		}
		return ret;
	}

	/**
	 * 得到表现异常的ap
	 * 
	 * @return
	 */
	public List<String> getAbnormalAP() {
		List<String> ret = new ArrayList<String>();
		List<Integer[]> lost_group = getLostGroup();
		Integer[] bool = lost_group.get(0);
		for (int i = 0; i < 5; i++) {
			for (int j = 1; j < lost_group.size(); j++) {
				bool[i] &= lost_group.get(j)[i];
			}
		}
		for (int i = 0; i < 5; i++) {
			if (1 == bool[i])
				ret.add(header[i]);
		}
		return ret;
	}

	public List<String[]> getPairAbnormalAP() {
		List<String[]> ret = new ArrayList<String[]>();
		List<Integer[]> lost_group = getLostGroup();
		for (int i = 0; i < 5; i++) {
			for (int j = i + 1; j < 5; j++) {
				// i,j为c(5,2)排列组合
				Integer[] bool = lost_group.get(0);
				int flag = bool[i] | bool[j];
				for (int k = 1; k < lost_group.size(); k++) {
					flag &= lost_group.get(k)[i] | lost_group.get(k)[j];
				}
				if (1 == flag) {
					String[] str = new String[2];
					str[0] = header[i];
					str[1] = header[j];
					ret.add(str);
				}
			}
		}
		return ret;
	}

	public void outputCorrectAP(List<String> array) {
//		//OffLineLocater.appendLog("The correct AP is");
//		for (int i = 0; i < array.size(); i++)
//			//OffLineLocater.appendLog(array.get(i));
	}

	public void outputAbnormalAP(List<String> array) {
//		//OffLineLocater.appendLog("The abnormal AP is");
//		for (int i = 0; i < array.size(); i++)
//			//OffLineLocater.appendLog(array.get(i));
	}

	public void outputUncertainAP(List<String> array) {
//		//OffLineLocater.appendLog("The uncertain AP is");
//		for (int i = 0; i < array.size(); i++)
//			//OffLineLocater.appendLog(array.get(i));
	}

	public Set<String> generateCombination() {
		Set<String> ret = new HashSet<String>();
		for (int i = 0; i < 5; i++) {
			for (int j = i + 1; j < 5; j++) {
				for (int k = j + 1; k < 5; k++) {
					Integer[] integer = new Integer[5];
					for (int m = 0; m < 5; m++)
						integer[m] = 0;
					integer[i] = 1;
					integer[j] = 1;
					integer[k] = 1;
					String str = integer[0].toString();
					for (int m = 1; m < 5; m++)
						str += "," + integer[m];
					ret.add(str);
				}
			}
		}
		return ret;
	}

	public Set<String> getCurrentCombination() {
		Set<String> ret = new HashSet<String>();
		for (int i = 0; i < form.size(); i++) {
			Integer[] integer = form.get(i);
			String str = integer[0].toString();
			for (int j = 1; j < 5; j++)
				str += "," + integer[j];
			ret.add(str);
		}
		return ret;
	}

	public Set<String> getComplementSet(Set<String> full_collection,
			Set<String> sub_collection) {
		full_collection.removeAll(sub_collection);
		return full_collection;
	}

	public Set<String> getAllAP() {
		Set<String> ret = new HashSet<String>();
		for (int i = 0; i < 5; i++)
			ret.add(header[i]);
		return ret;
	}

	public List<String> getRestOfAP(Set<String> all_ap, List<String> ap) {
		Set<String> ap_set = new HashSet<String>(ap);
		Set<String> all_ap_copy = new HashSet<String>();
		all_ap_copy.addAll(all_ap);
		all_ap_copy.removeAll(ap_set);
		List<String> ret = new ArrayList<String>(all_ap_copy);
		return ret;
	}

	public List<PhoneWifiMessage> getPhoneWifiMessage(List<String> list) {
		List<PhoneWifiMessage> ret = new ArrayList<PhoneWifiMessage>();
		for (int i = 0; i < list.size(); i++)
			ret.add(map.get(list.get(i)));
		return ret;
	}

	/**
	 * 得到ap可靠信息
	 */
	public APGroupInfo getAPGroupInfo() {
		APGroupInfo info = new APGroupInfo();
		int group_num = form.size();
		Set<String> results = new HashSet<String>();
		Set<String> all_ap = getAllAP();
		List<String> abnormal_ap = new ArrayList<String>();
		List<String> correct_ap = new ArrayList<String>();
		List<String> uncertain_ap = new ArrayList<String>();
		switch (group_num) {
		case 0:
		case 1:
			////OffLineLocater.appendLog("2.");
			////OffLineLocater.appendLog("This group of AP is unreliable");
			info.setStatus(0);
			break;
		case 10:
			results = getIntersectionLocation();
			if (!results.isEmpty()) {
				////OffLineLocater.appendLog("1.1.");
				correct_ap.addAll(all_ap);
				outputCorrectAP(correct_ap);
				info.setCorrect_ap(getPhoneWifiMessage(correct_ap));
			} else {
				uncertain_ap.addAll(all_ap);
				for (int i = 0; i < uncertain_ap.size(); i++) {
					List<String> excluded_ap = new ArrayList<String>();
					excluded_ap.add(uncertain_ap.get(i));
					Set<String> set = getLocationExcludeAbnormalAP(excluded_ap);
					if (!set.isEmpty()) {
						abnormal_ap.add(uncertain_ap.get(i));
					}
				}
				if (abnormal_ap.size() == 1) {
//					//OffLineLocater.appendLog("1.2.1.1.");
//					//OffLineLocater.appendLog("This group of AP is unreliable");
					info.setStatus(0);
				} else {
					////OffLineLocater.appendLog("1.2.1.2.");
					outputUncertainAP(abnormal_ap);
					info.setUncertain_ap(getPhoneWifiMessage(abnormal_ap));
					correct_ap = getRestOfAP(all_ap, abnormal_ap);
					outputCorrectAP(correct_ap);
					info.setCorrect_ap(getPhoneWifiMessage(correct_ap));
				}
			}
			break;
		default:
			uncertain_ap = getAbnormalAP();
			if (!uncertain_ap.isEmpty()) {
				if (uncertain_ap.size() == 1) {
					results = getLocationExcludeAbnormalAP(uncertain_ap);
					if (!results.isEmpty()) {
						//OffLineLocater.appendLog("3.1.1.1.");
						abnormal_ap.addAll(uncertain_ap);
						outputAbnormalAP(abnormal_ap);
						info.setAbnormal_ap(getPhoneWifiMessage(abnormal_ap));
					} else {
						//OffLineLocater.appendLog("3.1.1.2.");
						//OffLineLocater.appendLog("This group of AP is unreliable");
						info.setStatus(0);
					}
				} else {
					//OffLineLocater.appendLog("3.1.2.");
					outputUncertainAP(uncertain_ap);
					info.setUncertain_ap(getPhoneWifiMessage(uncertain_ap));
				}
			}

			else {
				List<String[]> pair_abnormal_ap = getPairAbnormalAP();
				if (!pair_abnormal_ap.isEmpty()) {
					if (pair_abnormal_ap.size() == 1) {
						//OffLineLocater.appendLog("3.1.2.");
						abnormal_ap.add(pair_abnormal_ap.get(0)[0]);
						abnormal_ap.add(pair_abnormal_ap.get(0)[1]);
						correct_ap = getRestOfAP(all_ap, abnormal_ap);
						outputAbnormalAP(abnormal_ap);
						info.setAbnormal_ap(getPhoneWifiMessage(abnormal_ap));
						outputCorrectAP(correct_ap);
						info.setCorrect_ap(getPhoneWifiMessage(correct_ap));
					} else {
						//OffLineLocater.appendLog("3.2.2.");
						List<String> all_ap_list = new ArrayList<String>(all_ap);
						Map<String, Integer> ap_count = new HashMap<String, Integer>();
						for (int i = 0; i < all_ap_list.size(); i++)
							ap_count.put(all_ap_list.get(i), 0);
						for (int i = 0; i < pair_abnormal_ap.size(); i++)
							for (int j = 0; j < 2; j++) {
								ap_count.put(
										pair_abnormal_ap.get(i)[j],
										ap_count.get(pair_abnormal_ap.get(i)[j]) + 1);
							}
						int min_count = 10;
						for (int i = 0; i < all_ap_list.size(); i++)
							if (ap_count.get(all_ap_list.get(i)) < min_count)
								min_count = ap_count.get(all_ap_list.get(i));
						int min_count_ap_nums = 0;
						for (int i = 0; i < all_ap_list.size(); i++)
							if (ap_count.get(all_ap_list.get(i)) == min_count)
								min_count_ap_nums++;
						if (min_count_ap_nums == 1) {
							for (int i = 0; i < all_ap_list.size(); i++)
								if (ap_count.get(all_ap_list.get(i)) == min_count)
									correct_ap.add(all_ap_list.get(i));
							outputCorrectAP(correct_ap);
							info.setCorrect_ap(getPhoneWifiMessage(correct_ap));
						}
						else {
							//OffLineLocater.appendLog("This group of AP is unreliable");
							info.setStatus(0);
						}
					}
				} else {
					//OffLineLocater.appendLog("3.3.");
					//OffLineLocater.appendLog("This group of AP is unreliable");
					info.setStatus(0);
				}
			}
			break;
		}
		return info;
	}

	public static void main(String[] args) throws Exception {
	}
}
