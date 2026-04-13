package iriro.saferoute.service;

import iriro.saferoute.dto.RiskPointDto;
import iriro.saferoute.dto.RoutePointDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GeoFilterService {
    private static final double EARTH_RADIUS = 6371000;

    /** 폴리라인(선분)과 점 사이 최단거리(m) */
    public double getMinDistance(List<RoutePointDto> routePoints, double latitude, double longitude) {
        if (routePoints == null || routePoints.isEmpty()) {
            return Double.MAX_VALUE;
        }
        if (routePoints.size() == 1) {
            RoutePointDto p = routePoints.get(0);
            return distanceMeter(p.getLatitude(), p.getLongitude(), latitude, longitude);
        }
        double min = Double.MAX_VALUE;
        for (int i = 0; i < routePoints.size() - 1; i++) {
            RoutePointDto a = routePoints.get(i);
            RoutePointDto b = routePoints.get(i + 1);
            min = Math.min(min, distancePointToSegmentMeters(
                    a.getLatitude(), a.getLongitude(), b.getLatitude(), b.getLongitude(),
                    latitude, longitude));
        }
        return min;
    }

    private double distancePointToSegmentMeters(
            double lat1, double lng1, double lat2, double lng2,
            double latP, double lngP) {
        double refLat = (lat1 + lat2 + latP) / 3.0;
        double mPerLat = 111000.0;
        double mPerLng = 111000.0 * Math.cos(Math.toRadians(refLat));
        double bx = (lng2 - lng1) * mPerLng;
        double by = (lat2 - lat1) * mPerLat;
        double px = (lngP - lng1) * mPerLng;
        double py = (latP - lat1) * mPerLat;
        double c2 = bx * bx + by * by;
        if (c2 < 1e-12) {
            return distanceMeter(lat1, lng1, latP, lngP);
        }
        double t = (px * bx + py * by) / c2;
        double projX, projY;
        if (t <= 0) {
            projX = 0;
            projY = 0;
        } else if (t >= 1) {
            projX = bx;
            projY = by;
        } else {
            projX = t * bx;
            projY = t * by;
        }
        double dx = px - projX;
        double dy = py - projY;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public int getSequence(List<RoutePointDto> routePoints, RiskPointDto riskZone) {
        double minDistance = Double.MAX_VALUE;
        int sequence = 0;
        double riskLat = riskZone.getLatitude();
        double riskLng = riskZone.getLongitude();
        for (RoutePointDto point : routePoints) {
            double d = distanceMeter(point.getLatitude(), point.getLongitude(), riskLat, riskLng);
            if (minDistance > d) {
                minDistance = d;
                sequence = point.getSequence();
            }
        }
        return sequence;
    }

    public double distanceMeter(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }

    // 연속된 경로 점 간의 각도 계산
    public double calculateAngle(RoutePointDto prev, RoutePointDto curr, RoutePointDto next) {
        double v1x = prev.getLongitude() - curr.getLongitude();
        double v1y = prev.getLatitude() - curr.getLatitude();
        double v2x = next.getLongitude() - curr.getLongitude();
        double v2y = next.getLatitude() - curr.getLatitude();

        double dot = v1x * v2x + v1y * v2y;
        double mag1 = Math.sqrt(v1x * v1x + v1y * v1y);
        double mag2 = Math.sqrt(v2x * v2x + v2y * v2y);

        if (mag1 == 0 || mag2 == 0) {
            return 180.0;
        }

        double cos = dot / (mag1 * mag2);
        cos = Math.max(-1.0, Math.min(1.0, cos));

        return Math.toDegrees(Math.acos(cos));
    }
}
