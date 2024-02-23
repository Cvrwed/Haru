package cc.unknown.module.impl.other;

import java.util.ArrayList;
import java.util.function.Function;

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
	private final ModeValue mode = new ModeValue("Mode", "Advanced", "Advanced", "Matrix",
			"Checks Only");
	private final BooleanValue remove = new BooleanValue("Remove Bots", false);
	private final BooleanValue tab = new BooleanValue("TabList Check", false);
	private final BooleanValue name = new BooleanValue("Invalid Check", false);
	private final BooleanValue sound = new BooleanValue("Sound Check", true);
	private static ArrayList<Entity> bots = new ArrayList<>();

	public AntiBot() {
		super("AntiBot", ModuleCategory.Other);
		this.registerSetting(mode, tab, name, sound, remove);
	}

	@EventLink
	public void onUpdate(UpdateEvent e) {
		if (!PlayerUtil.inGame()) return;
		switch (mode.getMode()) {
        case "Advanced":
            mc.theWorld.playerEntities.stream().filter(player -> player != mc.thePlayer).forEach(player -> {
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
            });
			break;
		case "Matrix":
            if (mc.thePlayer.ticksExisted > 110) {
                mc.theWorld.loadedEntityList.stream().filter(entity -> entity instanceof EntityPlayer && entity != mc.thePlayer && entity.getCustomNameTag().isEmpty() && !bots.contains(entity)).forEach(entity -> {
                	bots.add(entity);
                	if (remove.isToggled()) {
                		mc.theWorld.removeEntity(entity);
                	}
                });
            } else {
            	bots.clear();
            }
			break;
		case "Checks Only":
            mc.theWorld.loadedEntityList.stream().filter(entity -> entity instanceof EntityPlayer && entity != mc.thePlayer).forEach(entity -> {
                if ((alreadyTablist((EntityPlayer) entity) && tab.isToggled()) || (invalidName.apply(entity) && name.isToggled())) {
                    if (sound.isToggled() && entity.doesEntityNotTriggerPressurePlate()) {
                        if (remove.isToggled()) {
                            mc.theWorld.removeEntity(entity);
                        }
                    }
                }
            });
		}
	}

	public static boolean bot(Entity entity) {
		if (Haru.instance.getModuleManager().getModule(AntiBot.class).isEnabled()) {
			return bots.contains(entity);
		}
		return false;
	}

	@Override
	public void onDisable() {
		if (!PlayerUtil.inGame())
			return;
		bots.clear();
	}

	private boolean alreadyTablist(final EntityPlayer entity) {
	    return mc.getNetHandler().getPlayerInfoMap().stream().anyMatch(en -> en != null && entity != null && en.getDisplayName() != null && entity.getDisplayName() != null && en.getDisplayName().getUnformattedText().equals(entity.getDisplayName().getUnformattedText()));
	}

	private Function<Entity, Boolean> invalidName = e -> e.getName().contains("-") || e.getName().contains("/") || e.getName().contains("|") || e.getName().contains("<") || e.getName().contains(">") || e.getName().contains("\u0e22\u0e07") || e.getName().isEmpty();
}
