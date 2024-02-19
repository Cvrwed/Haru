package cc.unknown.module.impl.other;

import java.util.ArrayList;

import cc.unknown.Haru;
import cc.unknown.event.impl.api.EventLink;
import cc.unknown.event.impl.move.UpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class AntiBot extends Module {
	private final static ModeValue mode = new ModeValue("Mode", "Hypixel", "Hypixel", "Advanced", "Matrix", "Checks Only");
    private final BooleanValue remove = new BooleanValue("Remove Bots", false);
    private final static BooleanValue tab = new BooleanValue("TabList Check", false);
    private final static BooleanValue name = new BooleanValue("Invalid Check", false);
    private final BooleanValue sound = new BooleanValue("Sound Check",true);
    private static ArrayList<Entity> bots = new ArrayList<>();

    public AntiBot() {
        super("AntiBot", ModuleCategory.Other);
        this.registerSetting(mode, tab, name, sound, remove);
    }

    @EventLink
    public void onUpdate(UpdateEvent event) {
        if (!PlayerUtil.inGame()) return;
        try {
            switch (mode.getMode()) {
                case "Hypixel":
                    for (Entity entity : mc.theWorld.loadedEntityList) {
                        if (entity instanceof EntityPlayer) {
                            if (entity != mc.thePlayer && !((EntityPlayer) entity).isSpectator()) {
                                if (bot(entity)) {
                                    if (remove.isToggled()) {
                                        mc.theWorld.removeEntity(entity);
                                    }
                                    bots.add(entity);
                                }
                            } else {
                                bots.remove(entity);
                            }
                        }
                    }
                    break;
                case "Advanced":
                    mc.theWorld.playerEntities.forEach(player -> {
                        if (player != mc.thePlayer) {
                            if (mc.thePlayer.getDistanceSq(player.posX, mc.thePlayer.posY, player.posZ) > 200) {
                                bots.remove(player);
                            }

                            if (player.ticksExisted < 5 || player.isInvisible() || mc.thePlayer.getDistanceSq(player.posX, mc.thePlayer.posY, player.posZ) > 100 * 100) {
                                if (!bots.contains(player)) {
                                    if (remove.isToggled()) {
                                        mc.theWorld.removeEntity(player);
                                    }
                                    bots.add(player);
                                }
                            }
                        }
                    });
                    break;
                case "Matrix":
                    if (mc.thePlayer.ticksExisted > 110) {
                        for (final Entity entity : mc.theWorld.loadedEntityList) {
                            if (entity instanceof EntityPlayer && entity != mc.thePlayer && entity.getCustomNameTag() == "" && !bots.contains(entity)) {
                                bots.add(entity);
                                if (remove.isToggled()) {
                                    mc.theWorld.removeEntity(entity);
                                }
                            }
                        }
                    } else {
                        bots = new ArrayList<Entity>();
                    }
                    break;
                case "Checks Only":
                    for (final Entity entity : mc.theWorld.loadedEntityList) {
                        if (entity instanceof EntityPlayer && entity != mc.thePlayer) {
                            if ((alreadyTablist((EntityPlayer) entity) && tab.isToggled()) || (invalidName(entity) && name.isToggled())) {
                                if (sound.isToggled() && entity.doesEntityNotTriggerPressurePlate()) {
                                    if (remove.isToggled()) {
                                        mc.theWorld.removeEntity(entity);
                                    }
                                }
                            }
                        }
                    }
            }
        } catch (Exception e) {

        }
    }

    public static boolean bot(Entity entity) {
        if (Haru.instance.getModuleManager().getModule(AntiBot.class).isEnabled()) {
            if (mode.is("Checks Only")) {
                return (alreadyTablist((EntityPlayer) entity) && tab.isToggled()) || (invalidName(entity) && name.isToggled());
            } else {
                return bots.contains(entity);
            }
        } else {
            return false;
        }
    }

    @Override
    public void onDisable() {
        if (!PlayerUtil.inGame()) return;
        bots.clear();
    }

    static boolean alreadyTablist(final EntityPlayer entity) {
        return mc.getNetHandler().getPlayerInfoMap().stream().filter((player) -> player != null && entity != null && player.getDisplayName() != null && entity.getDisplayName() != null && player.getDisplayName().getUnformattedText().equals(entity.getDisplayName().getUnformattedText())).count() > 0;
    }

    static boolean invalidName(final Entity e) {
        return e.getName().contains("-") || e.getName().contains("/") || e.getName().contains("|") || e.getName().contains("<") || e.getName().contains(">") || e.getName().contains("\u0e22\u0e07") || e.getName().equals("");
    }
}
