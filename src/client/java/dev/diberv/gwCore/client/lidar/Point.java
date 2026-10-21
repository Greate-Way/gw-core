package dev.diberv.gwCore.client.lidar;

import java.util.List;

public class Point {
    private List<Point> points;

    public float r = 1f;
    public float g = 0f;
    public float b = 0f;

    public float x;
    public float y;
    public float z;

    public Point(List<Point> points, float x, float y, float z) {
        this.z = z;
        this.y = y;
        this.x = x;
        this.points = points;
    }

    public Point(List<Point> points, float r, float g, float b, float x, float y, float z) {
        this.b = b;
        this.g = g;
        this.r = r;
        this.x = x;
        this.y = y;
        this.z = z;
        this.points = points;
    }


    public void destroy() {
        points.remove(this);
    }

}
