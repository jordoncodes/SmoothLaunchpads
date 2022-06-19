package itzjordon.smoothlaunchpads.util;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class LocationUtils {

    public static Location lookAt(Location originalLoc, Location toLook) {
        originalLoc = originalLoc.clone();

        double distX = toLook.getX() - originalLoc.getX();
        double distY = toLook.getY() - originalLoc.getY();
        double distZ = toLook.getZ() - originalLoc.getZ();

        if (distX != 0) {
            if (distX < 0) {
                originalLoc.setYaw((float) (1.5 * Math.PI));
            } else {
                originalLoc.setYaw((float) (0.5 * Math.PI));
            }
            originalLoc.setYaw(originalLoc.getYaw() - (float) Math.atan(distZ / distX));
        } else if (distZ < 0) {
            originalLoc.setYaw((float) Math.PI);
        }

        double dxz = Math.sqrt(Math.pow(distX, 2) + Math.pow(distZ, 2));

        originalLoc.setPitch((float) -Math.atan(distY / dxz));

        originalLoc.setYaw(-originalLoc.getYaw() * 180f / (float) Math.PI);
        originalLoc.setPitch(originalLoc.getPitch() * 180f / (float) Math.PI);

        return originalLoc;
    }

    public static Location move(Location loc, Vector offset) {
        float ryaw = -loc.getYaw() / 180f * (float) Math.PI;
        float rpitch = loc.getPitch() / 180f * (float) Math.PI;
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        z -= offset.getX() * Math.sin(ryaw);
        z += offset.getY() * Math.cos(ryaw) * Math.sin(rpitch);
        z += offset.getZ() * Math.cos(ryaw) * Math.cos(rpitch);
        x += offset.getX() * Math.cos(ryaw);
        x += offset.getY() * Math.sin(rpitch) * Math.sin(ryaw);
        x += offset.getZ() * Math.sin(ryaw) * Math.cos(rpitch);
        y += offset.getY() * Math.cos(rpitch);
        y -= offset.getZ() * Math.sin(rpitch);
        return new Location(loc.getWorld(), x, y, z, loc.getYaw(), loc.getPitch());
    }

    public static Location lookAtAndMove(Location originalLoc, Location toLook, Vector offset) {
        originalLoc = originalLoc.clone();

        double distX = toLook.getX() - originalLoc.getX();
        double distY = toLook.getY() - originalLoc.getY();
        double distZ = toLook.getZ() - originalLoc.getZ();

        if (distX != 0) {
            if (distX < 0) {
                originalLoc.setYaw((float) (1.5 * Math.PI));
            } else {
                originalLoc.setYaw((float) (0.5 * Math.PI));
            }
            originalLoc.setYaw(originalLoc.getYaw() - (float) Math.atan(distZ / distX));
        } else if (distZ < 0) {
            originalLoc.setYaw((float) Math.PI);
        }

        double dxz = Math.sqrt(Math.pow(distX, 2) + Math.pow(distZ, 2));

        originalLoc.setPitch((float) -Math.atan(distY / dxz));

//        originalLoc.setYaw(-originalLoc.getYaw() * 180f / (float) Math.PI);
//        originalLoc.setPitch(originalLoc.getPitch() * 180f / (float) Math.PI);
//
//
//        float rYaw = -originalLoc.getYaw() / 180f * (float) Math.PI;
//        float rPitch = originalLoc.getPitch() / 180f * (float) Math.PI;
        float rYaw = originalLoc.getYaw();
        float rPitch = originalLoc.getPitch();
        double x = originalLoc.getX();
        double y = originalLoc.getY();
        double z = originalLoc.getZ();
        z -= offset.getX() * Math.sin(rYaw);
        z += offset.getY() * Math.cos(rYaw) * Math.sin(rPitch);
        z += offset.getZ() * Math.cos(rYaw) * Math.cos(rPitch);
        x += offset.getX() * Math.cos(rYaw);
        x += offset.getY() * Math.sin(rPitch) * Math.sin(rYaw);
        x += offset.getZ() * Math.sin(rYaw) * Math.cos(rPitch);
        y += offset.getY() * Math.cos(rPitch);
        y -= offset.getZ() * Math.sin(rPitch);
        return new Location(originalLoc.getWorld(), x, y, z, originalLoc.getYaw(), originalLoc.getPitch());
    }
}
