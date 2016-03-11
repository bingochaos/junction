/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.edu.buaa.nlsde.wlan.beans;

/**
 * SVM方法求集合后数据存储结构
 *
 * @author lawson
 */
public class LocationUnionUnit {

	private int class_index;// 点编号
	private String class_name;// location#mapid
	private int freq;// 多少集合包含该点
	private float avg_prob;// 每个点平均每个AP的概率

	public LocationUnionUnit() {
	}

	public LocationUnionUnit(int class_index, int freq) {
		this.class_index = class_index;
		this.freq = freq;
	}

	/**
	 * @return the class_index
	 */
	public int getClassIndex() {
		return class_index;
	}

	/**
	 * @param class_index
	 *            the class_index to set
	 */
	public void setClassIndex(int class_index) {
		this.class_index = class_index;
	}

	/**
	 * @return the freq
	 */
	public int getFreq() {
		return freq;
	}

	/**
	 * @param freq
	 *            the freq to set
	 */
	public void setFreq(int freq) {
		this.freq = freq;
	}

	/**
	 * @return the class_name
	 */
	public String getClassName() {
		return class_name;
	}

	/**
	 * @param class_name
	 *            the class_name to set
	 */
	public void setClassName(String class_name) {
		this.class_name = class_name;
	}

	public float getAvgProb() {
		return avg_prob;
	}

	public void setAvgProb(float avg_prob) {
		this.avg_prob = avg_prob;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LocationUnionUnit [class_index=");
		builder.append(class_index);
		builder.append(",class_name=");
		builder.append(class_name);
		builder.append(",freq=");
		builder.append(freq);
		builder.append(",avg_prob=");
		builder.append(avg_prob);
		builder.append("]");
		return builder.toString();
	}

}
