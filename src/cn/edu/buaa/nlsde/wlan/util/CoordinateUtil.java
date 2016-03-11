package cn.edu.buaa.nlsde.wlan.util;


import cn.com.navia.sdk.bean.META;

/**
 *  坐标工具
 * @author gaojie
 *
 */
public class CoordinateUtil {

    /**
     * 横坐标（单位：deg/m） 0.0000117361 纵坐标(deg/m) 0.000009025
     */

    // 东直门横坐标比例
    public static final double DZMLngDegMRate = 0.0000117361;
    // 纵坐标
    public static final double DZMLatDegMRate = 0.000009025;
    // 东直门原点(纬度, 经度)
    public static final double[] DZMOLatLng = new double[] { 39.94022273, 116.4287226 };


    /**
     * x y 本地坐标点
     *
     * @author gaojie
     *
     */
    public static class Local {
        private double x;
        private double y;

        private LatLng latlng;

        public LatLng toLatLng(META.Update.Building.Floor floor) {
            LatLng oLatLng = new LatLng(floor.getoLat(), floor.getoLng());

            double[] convertLatLng = CoordinateUtil.convertLatLng(
                    new double[] {oLatLng.getLat(), oLatLng.getLng() },
                    floor.getLatdegM(), floor.getLngdegM(),
                    new double[] { x, y }, floor.getAngle()
            );

            latlng = new LatLng(convertLatLng[0], convertLatLng[1]);

            return this.latlng;
        }

        public Local(double x, double y) {
            super();
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }
    }


    public static class LatLng {
        private double lat;
        private double lng;

        private Local local;

        public Local toLocal(META.Update.Building.Floor floor, double angle) {

            double[] oLatLng = new double[] { floor.getoLat(), floor.getoLng() };
            double[] xyLatLng = new double[] { lat, lng };

            double[] convertXY = CoordinateUtil.convertXY(oLatLng, floor.getLatdegM(), floor.getLngdegM(), xyLatLng,
                    floor.getAngle());

            local = new Local(convertXY[0], convertXY[1]);
            return local;
        }

        public   LatLng(double lat, double lng) {
            super();
            this.lat = lat;
            this.lng = lng;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public void setLng(double lng) {
            this.lng = lng;
        }

    }
    /**
     * 相对坐标转经纬度.
     *
     * @return
     */
    @Deprecated
    public static double[] DZMconvertLatLng(double x, double y) {
        return convertLatLng(DZMOLatLng, DZMLatDegMRate, DZMLngDegMRate, new double[] { x, y });
    }

    public static LatLng convertLatLng(META.Update.Building.Floor floor, double x, double y) {
        Local local = new CoordinateUtil.Local(x, y);
        return local.toLatLng(floor);
    }

    /**
     * 经纬度转换相对坐标
     */
    public static double[] DZMconvertXY(double[] xyLatLng) {
        return convertXY(DZMOLatLng, DZMLatDegMRate, DZMLngDegMRate, xyLatLng);
    }

    /**
     * 转化点(x,y)到经纬度
     * @param oLatLng
     * @param dzmLatDegMRate
     * @param dzmLngDegMRate
     * @param xy
     * @return  [0:纬度 1:经度]
     */
    public static double[] convertLatLng(double[] oLatLng, double dzmLatDegMRate, double dzmLngDegMRate, double[] xy) {
        double[] retval = new double[] {
                // 纬度
                dzmLatDegMRate * xy[1] + oLatLng[0],
                // 经度
                dzmLngDegMRate * xy[0] + oLatLng[1] };
        return retval;
    }

    public static double[] convertLatLng(double[] oLatLng, double dzmLatDegMRate, double dzmLngDegMRate, double[] xy,
                                         double angle) {

        /**
         * x = x * cos（a）+ y* sin（a） Y =-x * sin（a）+ y* cos（a）
         */

        double cosA = Math.cos(angle);
        double sinA = Math.sin(angle);

        double X = xy[0] * cosA + xy[1] * sinA;
        double Y = -xy[0] * sinA + xy[1] * cosA;

        double[] retval = new double[] {
                // 纬度
                dzmLatDegMRate * Y + oLatLng[0],
                // 经度
                dzmLngDegMRate * X + oLatLng[1] };

        return retval;
    }

    @Deprecated
    public static double[] convertXY(double[] oLatLng, double dzmLatDegMRate, double dzmLngDegMRate, double[] xyLatLng) {
        double[] retval = new double[] {
                // x==lng
                (xyLatLng[1] - oLatLng[1]) / dzmLngDegMRate,
                // y==lat
                (xyLatLng[0] - oLatLng[0]) / dzmLatDegMRate };
        return retval;
    }

    public static double[] convertXY(double[] oLatLng, Double latdegm, Double lngdegm, double[] xyLatLng, double angle) {
        double[] xy = new double[] {
                // x==lng
                (xyLatLng[1] - oLatLng[1]) / lngdegm,
                // y==lat
                (xyLatLng[0] - oLatLng[0]) / latdegm };

        /**
         * x = x * cos（a）+ y* sin（a） Y =-x * sin（a）+ y* cos（a）
         */
        double cosA = Math.cos(angle);
        double sinA = Math.sin(angle);

        double X = xy[0] * cosA + xy[1] * sinA;
        double Y = -xy[0] * sinA + xy[1] * cosA;

        return new double[] { X, Y };
    }
}
