package cn.com.navia.sdk.bean;

import android.test.AndroidTestCase;

import cn.com.navia.sdk.utils.JsonUtils;

/**
 * Created by gaojie on 15-2-27.
 */
public class METATest extends AndroidTestCase {

    public void testJsonParse(){
        String json= "{\"c\":0,\"d\":[{\"id\":5,\"building_id\":1,\"name\":\"盈都11C\",\"version\":1,\"available\":1},{\"id\":4,\"building_id\":3,\"name\":\"北航频谱v1.0\",\"version\":2,\"available\":0},{\"id\":2,\"building_id\":6,\"name\":\"东直门频谱v2.0\",\"version\":2,\"available\":0}],\"m\":null}";
        RetVal_LatestList list =  JsonUtils.parse(json, RetVal_LatestList.class);
        System.out.println(list);
    }
}
