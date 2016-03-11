package cn.edu.buaa.nlsde.wlan.cluster;

public class Point {

    private String coordinate;
    private String area;

    public Point() {
    }

    public Point(String coordinate, String area) {
        this.coordinate = coordinate;
        this.area = area;
    }

    public String getCoordinate() {
        return this.coordinate;
    }

    public String getArea() {
        return this.area;
    }
}
