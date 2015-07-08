package io.github.netdex.mingame.physics;

import java.awt.geom.Rectangle2D;

public class PhysExtensions {

	public static Vector getIntersectionDepth(Rectangle2D rectA, Rectangle2D rectB)
    {
        // Calculate half sizes.
        double halfWidthA = rectA.getWidth() / 2.0;
        double halfHeightA = rectA.getHeight() / 2.0;
        double halfWidthB = rectB.getWidth() / 2.0;
        double halfHeightB = rectB.getHeight() / 2.0;

        // Calculate centers.
        Vector centerA = new Vector(rectA.getX() + halfWidthA, rectA.getY() + halfHeightA);
        Vector centerB = new Vector(rectB.getX() + halfWidthB, rectB.getY() + halfHeightB);

        // Calculate current and minimum-non-intersecting distances between centers.
        double distanceX = centerA.x - centerB.x;
        double distanceY = centerA.y - centerB.y;
        double minDistanceX = halfWidthA + halfWidthB;
        double minDistanceY = halfHeightA + halfHeightB;

        // If we are not intersecting at all, return (0, 0).
        if (Math.abs(distanceX) >= minDistanceX || Math.abs(distanceY) >= minDistanceY)
            return new Vector(0,0);

        // Calculate and return intersection depths.
        double depthX = distanceX > 0 ? minDistanceX - distanceX : -minDistanceX - distanceX;
        double depthY = distanceY > 0 ? minDistanceY - distanceY : -minDistanceY - distanceY;
        return new Vector(depthX, depthY);
    }
}
