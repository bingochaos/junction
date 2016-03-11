package cn.com.navia.sdk.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.stanfy.gsonxml.GsonXml;
import com.stanfy.gsonxml.GsonXmlBuilder;

/**
 * Created by gaojie on 15-2-27.
 */
public class JsonUtils {

    public static <T>T parseXml(String xml, Class<T> clazz){
        T t = null;
        if(!TextUtils.isEmpty(xml)){
            GsonXml gsonXml  = new GsonXmlBuilder().create();
            t = gsonXml.fromXml(xml, clazz);
        }
        return t;
    }

    /**
     * json字符串到对象
     * @param clazz
     * @param json
     * @return
     */
    public static <T>T  parse(String json, Class<T> clazz){
        T t = null;
        if (!TextUtils.isEmpty(json) ) {
            t = new Gson().fromJson(json, clazz);
        }
        return t;
    }
}
