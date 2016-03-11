/**
 * MarkerInfo.java
 * 
 * @Description: 
 * 
 * @File: MarkerInfo.java
 * 
 * @Package nlsde.baidu
 * 
 * @Author chaos
 * 
 * @Date 2014-12-2下午9:08:58
 * 
 * @Version V1.0
 */
package nlsde.baidu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chaos
 *
 */
public class MarkerInfo implements Serializable
{

	private static final long serialVersionUID = -758459502806858414L;  
    /** 
     * 精度 
     */  
    private double latitude;  
    /** 
     * 纬度 
     */  
    private double longitude;  
    /** 
     * 图片ID，真实项目中可能是图片路径 
     */  
    private int imgId;  
    /** 
     * 商家名称 
     */  
    private String name;  
    /** 
     * 距离 
     */  
    private String distance;  
    /** 
     * 赞数量 
     */  
    private int hid;  
  
    public static List<MarkerInfo> infos = new ArrayList<MarkerInfo>();  
  
    public MarkerInfo()  
    {  
    }  
  
    public MarkerInfo(double latitude, double longitude, int imgId, String name,  
            String distance, int hid)  
    {  
        super();  
        this.latitude = latitude;  
        this.longitude = longitude;  
        this.imgId = imgId;  
        this.name = name;  
        this.distance = distance;  
        this.hid = hid;  
    }  

    public double getLatitude()  
    {  
        return latitude;  
    }  
  
    public void setLatitude(double latitude)  
    {  
        this.latitude = latitude;  
    }  
  
    public double getLongitude()  
    {  
        return longitude;  
    }  
  
    public void setLongitude(double longitude)  
    {  
        this.longitude = longitude;  
    }  
  
    public String getName()  
    {  
        return name;  
    }  
  
    public int getImgId()  
    {  
        return imgId;  
    }  
  
    public void setImgId(int imgId)  
    {  
        this.imgId = imgId;  
    }  
  
    public void setName(String name)  
    {  
        this.name = name;  
    }  
  
    public String getDistance()  
    {  
        return distance;  
    }  
  
    public void setDistance(String distance)  
    {  
        this.distance = distance;  
    }  
  
    public int getHid()  
    {  
        return hid;  
    }  
  
    public void setHid(int zan)  
    {  
        this.hid = zan;  
    }  
  
}  
