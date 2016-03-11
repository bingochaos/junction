package cn.edu.buaa.nlsde.wlan.init.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class ParaConfig {
	private static ParaConfig instance;
	private int buff_time;
	private int buff_size;
	private float float_margin;
	private int union_phone_size;

	public static ParaConfig readConfig(File paramConfigJson) throws FileNotFoundException {
		JsonReader jsonReader = new JsonReader(new FileReader(paramConfigJson));
		instance = new Gson().fromJson(jsonReader, ParaConfig.class );
		return instance;
	}

	public int getBuff_time() {
		return buff_time;
	}

	public int getBuff_size() {
		return buff_size;
	}

	public float getFloat_margin() {
		return float_margin;
	}

	public int getUnion_phone_size() {
		return union_phone_size;
	}

	public static ParaConfig getInstance() {
		return instance;
	}
}
