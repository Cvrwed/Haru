package cc.unknown.utils.vector;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;

public class CustomVec3 extends Vec3 {
    
    public CustomVec3(double x, double y, double z) {
        super((x == -0.0D) ? 0.0D : x, 
              (y == -0.0D) ? 0.0D : y, 
              (z == -0.0D) ? 0.0D : z);
    }

    public CustomVec3(Vec3i vec3i) {
        super(vec3i);
    }
    
    public CustomVec3(Entity entity) {
        this(entity.posX, entity.posY, entity.posZ);
    }
    
    public double distanceTo(final Vec3 vec) {
        final double d0 = vec.xCoord - this.xCoord;
        final double d1 = vec.yCoord - this.yCoord;
        final double d2 = vec.zCoord - this.zCoord;
        return MathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);
    }
    
    public double distanceTo(final EntityPlayer vec) {
        return distanceTo(new Vec3(vec.posX, vec.posY, vec.posZ));
    }
}
