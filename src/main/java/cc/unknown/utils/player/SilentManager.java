package cc.unknown.utils.player;

import cc.unknown.Haru;
import cc.unknown.event.impl.api.EventLink;
import cc.unknown.event.impl.move.PreUpdateEvent;
import cc.unknown.event.impl.move.SilentEvent;
import cc.unknown.event.impl.player.JumpEvent;
import cc.unknown.event.impl.player.LookEvent;
import cc.unknown.event.impl.player.StrafeEvent;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.utils.Loona;
import net.minecraft.util.MathHelper;

public class SilentManager implements Loona {
	
    private float yaw = 0;
    private float pitch = 0;
    private boolean modified;
    private boolean doMovementFix;
    private boolean doJumpFix;
    private float speed = 10f;

    @EventLink
    public void onTick(TickEvent e) {
        SilentEvent rotation = new SilentEvent(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, speed);
        Haru.instance.getEventBus().post(rotation);

        if(!modified && !rotation.hasBeenModified()) {
            yaw = mc.thePlayer.rotationYaw;
            pitch = mc.thePlayer.rotationPitch;
            return;
        }

        doMovementFix = rotation.isDoMovementFix();
        doJumpFix = rotation.isDoJumpFix();
        speed = rotation.getSpeed();

        float yawDiff = RotationUtil.getYawDifference(rotation.getYaw(), yaw);
        if(Math.abs(yawDiff) > speed)
            yawDiff = (speed * (yawDiff > 0 ? 1 : -1))/2f;
        yaw = MathHelper.wrapAngleTo180_float(yaw + yawDiff);

        float pitchDiff = RotationUtil.getYawDifference(rotation.getPitch(), pitch); //prevpitch
        if(Math.abs(pitchDiff) > speed/2f)
            pitchDiff =  (speed/2f * (pitchDiff > 0 ? 1 : -1))/2f;
        pitch = MathHelper.wrapAngleTo180_float(pitch + pitchDiff);

        modified = rotation.hasBeenModified() || (Math.abs(RotationUtil.getYawDifference(mc.thePlayer.rotationYaw, yaw)) > speed);
    }

    @EventLink
    public void onPre(PreUpdateEvent e) {
        if(!modified)
            return;
        e.setYaw(yaw);
        e.setPitch(pitch);
    }

    @EventLink
    public void onLook(LookEvent e) {
        if(!modified)
            return;
        e.setYaw(yaw);
        e.setPitch(pitch);
    }

    @EventLink
    public void onStrafe(StrafeEvent e) {
        if(!modified || !doMovementFix)
            return;
        e.setYaw(yaw);
    }

    @EventLink
    public void onJump(JumpEvent e) {
    	if(!modified || !doJumpFix)
            return;
        e.setYaw(yaw);
    }
}