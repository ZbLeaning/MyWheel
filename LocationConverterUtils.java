package com.imooc.demo.demo;



/**
 * @author cxsz-hp16
 * @Title: LocationConverterUtils
 * @ProjectName ibosspro
 * @Description: 地图坐标系转换
 * @date 2018/12/1110:49
 */
public class LocationConverterUtils {
    private static final double LAT_OFFSET_0(double x, double y) {
        return -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
    }

    private static final double LAT_OFFSET_1(double x, double y) {
        return (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x * Math.PI)) * 2.0 / 3.0;
    }

    private static final double LAT_OFFSET_2(double x, double y) {
        return (20.0 * Math.sin(y * Math.PI) + 40.0 * Math.sin(y / 3.0 * Math.PI)) * 2.0 / 3.0;
    }

    private static final double LAT_OFFSET_3(double x, double y) {
        return (160.0 * Math.sin(y / 12.0 * Math.PI) + 320 * Math.sin(y * Math.PI / 30.0)) * 2.0 / 3.0;
    }

    private static final double LON_OFFSET_0(double x, double y) {
        return 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
    }

    private static final double LON_OFFSET_1(double x, double y) {
        return (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x * Math.PI)) * 2.0 / 3.0;
    }

    private static final double LON_OFFSET_2(double x, double y) {
        return (20.0 * Math.sin(x * Math.PI) + 40.0 * Math.sin(x / 3.0 * Math.PI)) * 2.0 / 3.0;
    }

    private static final double LON_OFFSET_3(double x, double y) {
        return (150.0 * Math.sin(x / 12.0 * Math.PI) + 300.0 * Math.sin(x / 30.0 * Math.PI)) * 2.0 / 3.0;
    }

    private static double RANGE_LON_MAX = 137.8347;
    private static double RANGE_LON_MIN = 72.004;
    private static double RANGE_LAT_MAX = 55.8271;
    private static double RANGE_LAT_MIN = 0.8293;

    private static double jzA  = 6378245.0;
    private static double jzEE = 0.00669342162296594323;

    public static double transformLat(double x, double y) {
        double ret = LAT_OFFSET_0(x, y);
        ret += LAT_OFFSET_1(x, y);
        ret += LAT_OFFSET_2(x, y);
        ret += LAT_OFFSET_3(x, y);
        return ret;
    }

    public static double transformLon(double x, double y) {
        double ret = LON_OFFSET_0(x, y);
        ret += LON_OFFSET_1(x, y);
        ret += LON_OFFSET_2(x, y);
        ret += LON_OFFSET_3(x, y);
        return ret;
    }

    public static boolean outOfChina(double lat, double lon) {
        if (lon < RANGE_LON_MIN || lon > RANGE_LON_MAX)
            return true;
        if (lat < RANGE_LAT_MIN || lat > RANGE_LAT_MAX)
            return true;
        return false;
    }

    public static Point gcj02Encrypt(double ggLat, double ggLon) {
        Point resPoint = new Point();
        double mgLat;
        double mgLon;
        if (outOfChina(ggLat, ggLon)) {
            resPoint.setLat(ggLat);
            resPoint.setLon(ggLon);
            return resPoint;
        }
        double dLat = transformLat(ggLon - 105.0, ggLat - 35.0);
        double dLon = transformLon(ggLon - 105.0, ggLat - 35.0);
        double radLat = ggLat / 180.0 * Math.PI;
        double magic = Math.sin(radLat);
        magic = 1 - jzEE * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((jzA * (1 - jzEE)) / (magic * sqrtMagic) * Math.PI);
        dLon = (dLon * 180.0) / (jzA / sqrtMagic * Math.cos(radLat) * Math.PI);
        mgLat = ggLat + dLat;
        mgLon = ggLon + dLon;

        resPoint.setLat(mgLat);
        resPoint.setLon(mgLon);
        return resPoint;
    }

    public static Point gcj02Decrypt(double gjLat, double gjLon) {
        Point gPt = gcj02Encrypt(gjLat, gjLon);
        double dLon = gPt.getLon() - gjLon;
        double dLat = gPt.getLat() - gjLat;
        return new Point(gjLat - dLat, gjLon - dLon);
    }

    public static Point bd09Decrypt(double bdLat, double bdLon) {
        double x = bdLon - 0.0065, y = bdLat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * Math.PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * Math.PI);
        return new Point(z * Math.sin(theta), z * Math.cos(theta));
    }

    public static Point bd09Encrypt(double ggLat, double ggLon) {
        Point bdPt = new Point();
        double x = ggLon, y = ggLat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * Math.PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * Math.PI);
        bdPt.setLon(z * Math.cos(theta) + 0.0065);
        bdPt.setLat(z * Math.sin(theta) + 0.006);
        return bdPt;
    }

    /**
     * @param location 世界标准地理坐标(WGS-84)
     * @return 中国国测局地理坐标（GCJ-02）<火星坐标>
     * @brief 世界标准地理坐标(WGS-84) 转换成 中国国测局地理坐标（GCJ-02）<火星坐标>
     *
     * ####只在中国大陆的范围的坐标有效，以外直接返回世界标准坐标
     */
    public static Point wgs84ToGcj02(Point location) {
        return gcj02Encrypt(location.getLat(), location.getLon());
    }

    /**
     * @param location 中国国测局地理坐标（GCJ-02）
     * @return 世界标准地理坐标（WGS-84）
     * @brief 中国国测局地理坐标（GCJ-02） 转换成 世界标准地理坐标（WGS-84）
     *
     * ####此接口有1－2米左右的误差，需要精确定位情景慎用
     */
    public static Point gcj02ToWgs84(Point location) {
        return gcj02Decrypt(location.getLat(), location.getLon());
    }

    /**
     * @param location 世界标准地理坐标(WGS-84)
     * @return 百度地理坐标（BD-09)
     * @brief 世界标准地理坐标(WGS-84) 转换成 百度地理坐标（BD-09)
     */
    public static Point wgs84ToBd09(Point location) {
        Point gcj02Pt = gcj02Encrypt(location.getLat(), location.getLon());
        return bd09Encrypt(gcj02Pt.getLat(), gcj02Pt.getLon());
    }

    /**
     * @param location 中国国测局地理坐标（GCJ-02）<火星坐标>
     * @return 百度地理坐标（BD-09)
     * @brief 中国国测局地理坐标（GCJ-02）<火星坐标> 转换成 百度地理坐标（BD-09)
     */
    public static Point gcj02ToBd09(Point location) {
        return bd09Encrypt(location.getLat(), location.getLon());
    }

    /**
     * @param location 百度地理坐标（BD-09)
     * @return 中国国测局地理坐标（GCJ-02）<火星坐标>
     * @brief 百度地理坐标（BD-09) 转换成 中国国测局地理坐标（GCJ-02）<火星坐标>
     */
    public static Point bd09ToGcj02(Point location) {
        return bd09Decrypt(location.getLat(), location.getLon());
    }

    /**
     * @param location 百度地理坐标（BD-09)
     * @return 世界标准地理坐标（WGS-84）
     * @brief 百度地理坐标（BD-09) 转换成 世界标准地理坐标（WGS-84）
     *
     * ####此接口有1－2米左右的误差，需要精确定位情景慎用
     */
    public static Point bd09ToWgs84(Point location) {
        Point gcj02 = bd09ToGcj02(location);
        return gcj02Decrypt(gcj02.getLat(), gcj02.getLon());
    }
}