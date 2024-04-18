package cc.unknown.module.impl.player;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.LivingEvent;
import cc.unknown.event.impl.move.PreMotionEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.player.PlayerUtil;
import cc.unknown.utils.player.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityFireball;

@Register(name = "AntiFireball", category = Category.Player)
public class AntiFireball extends Module {

    private SliderValue range = new SliderValue("Range", 8.0, 3.0, 6.0, 0.5);
    private BooleanValue silentSwing = new BooleanValue("Silent Swing", false);
    private EntityFireball fireball;
    
    public AntiFireball() {
    	this.registerSetting(range, silentSwing);	
    }

    @EventLink
    public void onLiving(LivingEvent e) {
        if (mc.currentScreen != null) {
            fireball = null;
            return;
        }
        
        fireball = this.getFireball();
    }
    
    @EventLink
    public void onPre(PreMotionEvent e) {
        if (!PlayerUtil.inGame()) {
            return;
        }
        
        if (mc.thePlayer.capabilities.isFlying) {
            return;
        }
        
        if (fireball != null) {
        	attackEntity(fireball, !silentSwing.isToggled());
            float[] rotations = RotationUtil.instance.getRotations(fireball, e.getYaw(), e.getPitch());
            e.setYaw(rotations[0]);
            e.setPitch(rotations[1]);
        }
    }
    
    private EntityFireball getFireball() {
        for (final Entity entity : mc.theWorld.loadedEntityList) {
            if (!(entity instanceof EntityFireball)) {
                continue;
            }

            if (mc.thePlayer.getDistanceSqToEntity(entity) > range.getInput() * range.getInput()) {
                continue;
            }

            return (EntityFireball) entity;
        }
        return null;
    }
    
    private void attackEntity(Entity e, boolean clientSwing) {
        if (clientSwing) {
            mc.thePlayer.swingItem();
        }
        mc.playerController.attackEntity(mc.thePlayer, e);
    }
}
