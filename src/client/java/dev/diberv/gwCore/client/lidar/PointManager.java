package dev.diberv.gwCore.client.lidar;

import java.util.ArrayList;
import java.util.List;

public class PointManager {
    private static List<Point> points = new ArrayList<>();


    public void addPoints(Point point) {
        points.add(point);
    }

    public static List<Point> getPoints() {
        return List.copyOf(points);
    }

    public void clearPoints() {
        points.clear();
    }

    public void delPoint(Point point) {
        points.remove(point);
    }
}
