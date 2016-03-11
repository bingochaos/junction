package cn.com.navia.sdk.locater;

import cn.edu.buaa.nlsde.wlan.beans.LocationInfo;

public interface LocationCallback {
    void onLocation(LocationInfo info);
}
