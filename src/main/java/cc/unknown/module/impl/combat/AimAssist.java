package cc.unknown.module.impl.combat;

import java.util.concurrent.ThreadLocalRandom;

import org.lwjgl.input.Mouse;

import cc.unknown.Haru;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.LivingUpdateEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.DoubleSliderValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.misc.ClickUtil;
import cc.unknown.utils.player.CombatUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

@Register(name = "AimAssist", category = Category.Combat)
public class AimAssist extends Module {
    private SliderValue speedYaw = new SliderValue("Speed Yaw", 45, 5, 100, 1);
    private SliderValue complimentYaw = new SliderValue("Compliment Yaw", 15, 2, 97, 1);
    private BooleanValue verticalCheck = new BooleanValue("Vertical Check", false);
    private DoubleSliderValue pitchRand = new DoubleSliderValue("Pitch Rand Deg", 0.1, 1.2, 0.1, 4, 0.1);
    private BooleanValue clickAim = new BooleanValue("Click Aim", true);
    private BooleanValue gcdFix = new BooleanValue("GCD Fix", false);
    private BooleanValue center = new BooleanValue("Instant", false);
    private BooleanValue rayCast = new BooleanValue("Not behind blocks", true);
    private BooleanValue disableWhen = new BooleanValue("Disable while breaking blocks", false);
    private BooleanValue weaponOnly = new BooleanValue("Weapon Only", false);
    private float[] facing;

    /* Gracias nitrohell por la mejora del pitch <- translate it im lazy*/ 

    public AimAssist() {
        this.registerSetting(speedYaw, complimentYaw, verticalCheck, pitchRand, clickAim, gcdFix, center, rayCast, disableWhen,
                weaponOnly);
    }

    @EventLink
    public void onUpdate(LivingUpdateEvent e) {
        if (mc.thePlayer == null || mc.currentScreen != null || !mc.inGameHasFocus)
            return;

        if (disableWhen.isToggled() && mc.objectMouseOver != null) {
            BlockPos p = mc.objectMouseOver.getBlockPos();
            if (p != null) {
                Block bl = mc.theWorld.getBlockState(p).getBlock();
                if (bl != Blocks.air && !(bl instanceof BlockLiquid) && bl instanceof Block) {
                    return;
                }
            }
        }

        if (!weaponOnly.isToggled() || PlayerUtil.isHoldingWeapon()) {
            AutoClick clicker = (AutoClick) Haru.instance.getModuleManager().getModule(AutoClick.class);
            if ((clickAim.isToggled() && ClickUtil.instance.isClicking()) || (Mouse.isButtonDown(0) && clicker != null && !clicker.isEnabled()) || !clickAim.isToggled()) {
                Entity enemy = getEnemy();
                if (enemy != null) {
                    if (center.isToggled()) {
                        CombatUtil.instance.aim(enemy, 0.0f);
                    }

                    double fovEntity = PlayerUtil.fovFromEntity(enemy);
                    double compliment = fovEntity * (ThreadLocalRandom.current().nextDouble(complimentYaw.getInput() - 1.47328, complimentYaw.getInput() + 2.48293) / 100);
                    float val = (float) (-(compliment + fovEntity / (101.0D - (float) ThreadLocalRandom.current().nextDouble(speedYaw.getInput() - 4.723847, speedYaw.getInput()))));
                    double pitchRandMin = pitchRand.getInputMin();
                    double pitchRandMax = pitchRand.getInputMax();
                    double pitchRandValue = pitchRandMin + (Math.random() * (pitchRandMax - pitchRandMin));
                    double pitchVariation = ThreadLocalRandom.current().nextDouble(-pitchRandValue, pitchRandValue);
                    this.facing = CombatUtil.instance.getTargetRotations(enemy);
                    float targetYaw = this.facing[0];
                    float targetPitch = this.facing[1];

                    if (fovEntity > 1.0D || fovEntity < -1.0D) {
                        mc.thePlayer.rotationYaw += val;
                    }

                    if (gcdFix.isToggled()) {
                        if (mc.thePlayer.rotationYaw < targetYaw || mc.thePlayer.rotationYaw > targetYaw) {
                            mc.thePlayer.rotationYaw += val;
                        }
                    }

                    if (verticalCheck.isToggled()) {
                        if (gcdFix.isToggled()) {
                            if (mc.thePlayer.rotationPitch < targetPitch || mc.thePlayer.rotationPitch > targetPitch) {
                                double pitchLimit = 90.0;
                                double currentPitch = mc.thePlayer.rotationPitch;
                                double newPitch = currentPitch + pitchVariation;
                                if (newPitch > pitchLimit) {
                                    pitchVariation = pitchLimit - currentPitch;
                                } else if (newPitch < -pitchLimit) {
                                    pitchVariation = -pitchLimit - currentPitch;
                                }
                                mc.thePlayer.rotationPitch += pitchVariation;
                            }
                        } else {
                            double pitchLimit = 90.0;
                            double currentPitch = mc.thePlayer.rotationPitch;
                            double newPitch = currentPitch + pitchVariation;
                            if (newPitch > pitchLimit) {
                                pitchVariation = pitchLimit - currentPitch;
                            } else if (newPitch < -pitchLimit) {
                                pitchVariation = -pitchLimit - currentPitch;
                            }
                            mc.thePlayer.rotationPitch += pitchVariation;
                        }
                    }
                }

                try {
                	if (rayCast.isToggled()) {
                		mc.thePlayer.canEntityBeSeen(enemy);
                	}
                } catch (NullPointerException ex) {
                	
                }
            }
        }
    }

    private Entity getEnemy() {
        return CombatUtil.instance.getTarget();
    }
}
