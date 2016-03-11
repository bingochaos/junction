package cn.com.navia.sdk.bean;

/**
 * Created by gaojie on 15-2-27.
 */
public class RetVal_UpdateItem {

    private int id;
    private int building_id;
    private String name;
    private int version;
    private int available;

    public RetVal_UpdateItem() {
    }

    public RetVal_UpdateItem(int id, int building_id, String name, int version, int available) {
        this.id = id;
        this.building_id = building_id;
        this.name = name;
        this.version = version;
        this.available = available;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBuilding_id() {
        return building_id;
    }

    public void setBuilding_id(int building_id) {
        this.building_id = building_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }
}
