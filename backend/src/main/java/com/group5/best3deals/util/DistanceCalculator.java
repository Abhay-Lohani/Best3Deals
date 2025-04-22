package com.group5.best3deals.util;

public class DistanceCalculator {

    private static final double EARTH_RADIUS_KM = 6371.0; // Earth's radius in kilometers
    private static final int HALF_CIRCLE = 2; // Used in Haversine formula
    private static final int RIGHT_ANGLE = 1; // Represents a right angle
    private static final int HALF_ANGLE_DIVISOR = 2; // Used for half-angle calculations

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double sinLat = Math.sin(latDistance / HALF_ANGLE_DIVISOR);
        double sinLon = Math.sin(lonDistance / HALF_ANGLE_DIVISOR);
        double cosLat1 = Math.cos(Math.toRadians(lat1));
        double cosLat2 = Math.cos(Math.toRadians(lat2));

        double a = sinLat * sinLat + cosLat1 * cosLat2 * sinLon * sinLon;
        double c = HALF_CIRCLE * Math.atan2(Math.sqrt(a), Math.sqrt(RIGHT_ANGLE - a));

        return EARTH_RADIUS_KM * c; // Returns distance in kilometers
    }
}
