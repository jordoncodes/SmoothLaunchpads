package itzjordon.smoothlaunchpads.util;

import org.bukkit.util.Vector;
 
public class VelocityUtil {
    public static Vector velocityForLaunchpad(Vector from, Vector to, int heightGain) {
        double gravity = 0.115;
        int difference = to.getBlockY() - from.getBlockY();
        double distHorizontal = Math.sqrt(squaredDist(from, to));
        double max = Math.max(heightGain, (difference + heightGain));
        double a = -distHorizontal * distHorizontal / (4 * max);
        double c = -difference;
        double s = -distHorizontal / (2 * a) - Math.sqrt(distHorizontal * distHorizontal - 4 * a * c) / (2 * a);
        double vy = Math.sqrt(max * gravity);
        double vh = vy / s;
        int dx = to.getBlockX() - from.getBlockX();
        int dz = to.getBlockZ() - from.getBlockZ();
        double mag = Math.sqrt(dx * dx + dz * dz);
        double dirx = dx / mag;
        double dirz = dz / mag;
        double vx = vh * dirx;
        double vz = vh * dirz;
        return new Vector(vx, vy, vz);
    }

    private static double squaredDist(Vector from, Vector to) {
        double dx = to.getBlockX() - from.getBlockX();
        double dz = to.getBlockZ() - from.getBlockZ();
        return dx * dx + dz * dz;
    }
}