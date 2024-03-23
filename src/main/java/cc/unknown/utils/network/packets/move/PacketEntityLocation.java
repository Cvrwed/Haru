package cc.unknown.utils.network.packets.move;

import net.minecraft.entity.Entity;

public class PacketEntityLocation {
    private final Entity entity;
    private final double x;
    private final double y;
    private final double z;
    private final long time;

    public PacketEntityLocation(Entity entity, double x, double y, double z) {
        this.entity = entity;
        this.x = x;
        this.y = y;
        this.z = z;
        this.time = System.nanoTime();
    }

    public Entity getEntity() {
        return entity;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public long getTime() {
        return time;
    }
}