package cc.unknown.module.impl.player;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.SafeWalkEvent;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.Module;

import static cc.unknown.utils.helpers.MathHelper.wrapAngleTo90_float;

import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.player.CombatUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.util.MathHelper;

public class BridgeAssist extends Module {
	
    private boolean waitingForAim;
    private boolean gliding;
    private long startWaitTime;
    private final float[] godbridgePos = {75.6f, -315, -225, -135, -45, 0, 45, 135, 225, 315};
    private final float[] moonwalkPos = {79.6f, -340, -290, -250, -200, -160, -110, -70, -20, 0, 20, 70, 110, 160, 200, 250, 290, 340};
    private final float[] breezilyPos = {79.9f, -360, -270, -180, -90, 0, 90, 180, 270, 360};
    private final float[] normalPos = {78f, -315, -225, -135, -45, 0, 45, 135, 225, 315};
    private double speedYaw, speedPitch;
    private float waitingForYaw, waitingForPitch;
    
    private ModeValue assistMode = new ModeValue("Mode", "Basic", "God bridge", "Moon walk", "Breezily", "Basic");
    private SliderValue assistChance = new SliderValue("Assist range", 38, 1, 40, 1);
    private SliderValue speedAngle = new SliderValue("Angle speed", 50, 1, 100, 1);
    private SliderValue waitFor = new SliderValue("Wait time", 70, 0, 200, 1);
    private BooleanValue onSneak = new BooleanValue("Only sneaking", false);
    private BooleanValue onlySafe = new BooleanValue("Safewalk", true);
    private BooleanValue safeIn = new BooleanValue("AirSafe", false);
    
	public BridgeAssist() {
		super("BridgeAssist", ModuleCategory.Player);
		this.registerSetting(assistMode, assistChance, waitFor, speedAngle, onSneak, onlySafe, safeIn);
	}
	
    @Override
    public void onEnable() {
        this.waitingForAim = false;
        this.gliding = false;
        super.onEnable();
    }
    
    @EventLink
    public void onSafe(SafeWalkEvent e) {    	
    	if (onlySafe.isToggled() && mc.thePlayer.onGround) {
    		e.setSaveWalk(true);
    	} else if (safeIn.isToggled() && PlayerUtil.playerOverAir()) {
    		e.setSaveWalk(true);
    	}
    }
    
    @EventLink
    public void onRender(Render3DEvent e) {
        if (!PlayerUtil.inGame() || (!PlayerUtil.playerOverAir() && mc.thePlayer.onGround) || (onSneak.isToggled() && !mc.thePlayer.isSneaking())) {
            return;
        }

        if (gliding) {
            float yaw = MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw);
            float pitch = wrapAngleTo90_float(mc.thePlayer.rotationPitch);
            double d0 = Math.abs(yaw - speedYaw);
            double d1 = Math.abs(yaw + speedYaw);
            double d2 = Math.abs(pitch - speedPitch);
            double d3 = Math.abs(pitch + speedPitch);

            if (speedYaw > d0 || speedYaw > d1 || speedPitch > d2 || speedPitch > d3) {
                mc.thePlayer.rotationYaw = waitingForYaw;
                mc.thePlayer.rotationPitch = waitingForPitch;
            } else {
                mc.thePlayer.rotationYaw += (mc.thePlayer.rotationYaw < waitingForYaw) ? speedYaw : -speedYaw;
                mc.thePlayer.rotationPitch += (mc.thePlayer.rotationPitch < waitingForPitch) ? speedPitch : -speedPitch;
            }

            if (mc.thePlayer.rotationYaw == waitingForYaw && mc.thePlayer.rotationPitch == waitingForPitch) {
                gliding = false;
                waitingForAim = false;
            }
            return;
        }

        if (!waitingForAim) {
            waitingForAim = true;
            startWaitTime = System.currentTimeMillis();
            return;
        }

        if (System.currentTimeMillis() - startWaitTime < waitFor.getInput()) {
            return;
        }

        float yaw = MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw);
        float pitch = wrapAngleTo90_float(mc.thePlayer.rotationPitch);
        float range = (float) assistChance.getInput();

        float[] positions = null;

        switch (assistMode.getMode()) {
            case "God bridge":
                positions = godbridgePos;
                break;
            case "Moon walk":
                positions = moonwalkPos;
                break;
            case "Breezily":
                positions = breezilyPos;
                break;
            case "Basic":
                positions = normalPos;
                break;
        }

        if (positions != null && positions.length > 0 && positions[0] >= pitch - range && positions[0] <= pitch + range) {
            for (int k = 1; k < positions.length; k++) {
                if (positions[k] >= yaw - range && positions[k] <= yaw + range) {
                    CombatUtil.instance.aimAt(positions[0], positions[k], mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, speedAngle.getInput());
                    waitingForAim = false;
                    return;
                }
            }
        }
        waitingForAim = false;
    }
}
