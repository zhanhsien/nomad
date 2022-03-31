package main.kiwitor.nomad.util;

import main.kiwitor.nomad.model.Coordinates;

public class LocationUtils {
    private final static int RADIUS = 6371;

    public static double getDistance(Coordinates origin, Coordinates destination) {
        double lat1 = degreeToRadian(origin.getLatitude());
        double lat2 = degreeToRadian(destination.getLatitude());
        double dLatitude = degreeToRadian(destination.getLatitude() - origin.getLatitude());
        double dLongitude = degreeToRadian(destination.getLongitude() - origin.getLongitude());

        //a = ((cord length between 2 points) / 2)^2
        double a = Math.pow(Math.sin(dLatitude / 2), 2) +
                (Math.pow(Math.sin(dLongitude / 2), 2) * Math.cos(lat1) * Math.cos(lat2));
        //c = angular distance in radians
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return RADIUS * c;
    }

    private static double degreeToRadian(double degree) {
        return degree * Math.PI / 180;
    }
}
