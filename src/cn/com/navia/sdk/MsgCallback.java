package cn.com.navia.sdk;

import java.util.List;

import cn.com.navia.sdk.bean.SpectrumInfo;

public interface MsgCallback {
	public void onLatestSpectrums(List<SpectrumInfo> infos);
}
