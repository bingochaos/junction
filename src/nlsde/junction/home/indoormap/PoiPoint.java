/**
 * PoiPoint.java
 * 
 * @Description: 
 * 
 * @File: PoiPoint.java
 * 
 * @Package nlsde.junction.home.indoormap
 * 
 * @Author chaos
 * 
 * @Date 2015-1-10下午5:55:07
 * 
 * @Version V1.0
 */
package nlsde.junction.home.indoormap;

import java.io.Serializable;

/**
 * @author chaos
 *
 */
public class PoiPoint implements Serializable{

	private String caption;
	private String clon;
	private String clat;
	private String mapName;
	private String floorId;

	/**
	 * @param caption
	 * @param clon
	 * @param clat
	 * @param mapName
	 * @param floorId
	 */
	public PoiPoint(String caption, String clon, String clat, String mapName,
			String floorId) {
		super();
		this.caption = caption;
		this.clon = clon;
		this.clat = clat;
		this.mapName = mapName;
		this.floorId = floorId;
	}
	
	/**
	 * 
	 */
	public PoiPoint() {
		super();
	}




	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PoiPoint [caption=" + caption + ", clon=" + clon + ", clat="
				+ clat + ", mapName=" + mapName + ", floorId=" + floorId + "]";
	}

	/**
	 * @return the caption
	 */
	public String getCaption() {
		return caption;
	}
	/**
	 * @param caption the caption to set
	 */
	public void setCaption(String caption) {
		this.caption = caption;
	}
	/**
	 * @return the clon
	 */
	public String getClon() {
		return clon;
	}
	/**
	 * @param clon the clon to set
	 */
	public void setClon(String clon) {
		this.clon = clon;
	}
	/**
	 * @return the clat
	 */
	public String getClat() {
		return clat;
	}
	/**
	 * @param clat the clat to set
	 */
	public void setClat(String clat) {
		this.clat = clat;
	}
	/**
	 * @return the mapName
	 */
	public String getMapName() {
		return mapName;
	}
	/**
	 * @param mapName the mapName to set
	 */
	public void setMapName(String mapName) {
		this.mapName = mapName;
	}
	/**
	 * @return the floorId
	 */
	public String getFloorId() {
		return floorId;
	}
	/**
	 * @param floorId the floorId to set
	 */
	public void setFloorId(String floorId) {
		this.floorId = floorId;
	}
}
