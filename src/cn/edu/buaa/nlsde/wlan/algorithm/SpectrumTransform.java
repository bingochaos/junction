package cn.edu.buaa.nlsde.wlan.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.edu.buaa.nlsde.wlan.resource.PhoneSpectrum;
import cn.edu.buaa.nlsde.wlan.resource.SpectrumItem;

public class SpectrumTransform {
	    private static SpectrumTransform uniqueInstance = null;
	    public static HashMap<String, ArrayList<SpectrumItem>> spectrum;
	 
	    private SpectrumTransform() {
	       // Exists only to defeat instantiation.
	    	spectrum = transform(PhoneSpectrum.wifi_search_map);
	    }
	 
	    public static SpectrumTransform getInstance() {
	       if (uniqueInstance == null) {
	           uniqueInstance = new SpectrumTransform();
	       }
	       return uniqueInstance;
	    }
	    // Other methods...
		public static HashMap<String, ArrayList<SpectrumItem>> transform(HashMap<String, ArrayList<SpectrumItem>> wifi_search_map) {
			HashMap<String, ArrayList<SpectrumItem>> res = new HashMap<String, ArrayList<SpectrumItem>>();
			for(Map.Entry<String, ArrayList<SpectrumItem>> entry:wifi_search_map.entrySet()) {
				ArrayList<SpectrumItem> tmp_list = entry.getValue();
				for(int i=0;i<tmp_list.size();i++) {
					SpectrumItem tmp_item = tmp_list.get(i);
					String tmp_location = tmp_item.getPosiID();
					if(!res.containsKey(tmp_location)) {
						ArrayList<SpectrumItem> tmp_value = new ArrayList<SpectrumItem>();
						tmp_value.add(tmp_item);
						res.put(tmp_location, tmp_value);
					}
					else {
						ArrayList<SpectrumItem> tmp_value = new ArrayList<SpectrumItem>(res.get(tmp_location));
						tmp_value.add(tmp_item);
						res.put(tmp_location, tmp_value);
					}
				}
			}
			return res;
		}
}
