package com.dp.plat.file.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * GPS 地理围栏校验服务。
 *
 * <p>使用 Haversine 公式计算照片拍摄坐标与站点坐标之间的球面距离，
 * 超过围栏半径返回 {@code ABNORMAL}，否则返回 {@code NORMAL}。</p>
 */
@Component
public class GeoFenceService {

    /** 围栏状态：正常。 */
    public static final String STATUS_NORMAL = "NORMAL";
    /** 围栏状态：异常（超出围栏半径）。 */
    public static final String STATUS_ABNORMAL = "ABNORMAL";

    /** 地球平均半径（米），Haversine 公式使用。 */
    private static final double EARTH_RADIUS_METERS = 6_371_000.0;

    /**
     * 校验照片坐标是否落在站点围栏内。
     *
     * @param photoLat          照片纬度
     * @param photoLng          照片经度
     * @param siteLat           站点纬度
     * @param siteLng           站点经度
     * @param fenceRadiusMeters 围栏半径（米）
     * @return {@link #STATUS_NORMAL} 或 {@link #STATUS_ABNORMAL}；任一坐标为 null 返回 {@link #STATUS_NORMAL}（无法判定视为正常）
     */
    public String checkFence(BigDecimal photoLat, BigDecimal photoLng,
                             BigDecimal siteLat, BigDecimal siteLng,
                             double fenceRadiusMeters) {
        if (photoLat == null || photoLng == null || siteLat == null || siteLng == null) {
            return STATUS_NORMAL;
        }
        double distance = haversineMeters(photoLat.doubleValue(), photoLng.doubleValue(),
                siteLat.doubleValue(), siteLng.doubleValue());
        return distance <= fenceRadiusMeters ? STATUS_NORMAL : STATUS_ABNORMAL;
    }

    /**
     * Haversine 公式计算两点之间球面距离（米）。
     */
    private double haversineMeters(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_METERS * c;
    }
}
