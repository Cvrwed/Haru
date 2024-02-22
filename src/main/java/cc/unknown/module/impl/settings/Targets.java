package cc.unknown.module.impl.settings;

import java.util.ArrayList;
import java.util.Comparator;

import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.impl.other.AntiBot;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.player.CombatUtil;
import cc.unknown.utils.player.FriendUtil;
import cc.unknown.utils.player.PlayerUtil;
import cc.unknown.utils.player.RotationUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;

public class Targets extends Module {

	private static BooleanValue friends = new BooleanValue("Target Friends", true);
	private static BooleanValue teams = new BooleanValue("Target Teams", false);
	private static BooleanValue bots = new BooleanValue("Target Bots", false);
	public static BooleanValue invis = new BooleanValue("Target Invisibles", true);
	public static BooleanValue naked = new BooleanValue("Target Un-Armor", true);
	private static SliderValue fov = new SliderValue("Fov", 180, 0, 360, 1);
	public static SliderValue multiTarget = new SliderValue("Multi Target", 0, 0, 4, 1);
	public static SliderValue distance = new SliderValue("Distance", 3.5, 0, 7, 0.1);
	private static ModeValue sortMode = new ModeValue("Priority", "Distance", "Distance", "Fov", "Angle", "Health", "Armor", "Best", "Unknown");

	public Targets() {
		super("Targets", ModuleCategory.Settings);
		this.registerSetting(friends, teams, bots, invis, naked, fov, multiTarget, distance, sortMode);
		onEnable();
	}

	@Override
	public boolean canBeEnabled() {
		return false;
	}
    
	public static EntityPlayer getTarget() {
		ArrayList<EntityPlayer> entities = new ArrayList<>();
		for (Entity entity : mc.theWorld.loadedEntityList) {
			if (entity instanceof EntityPlayer && entity != mc.thePlayer) {
				EntityPlayer player = (EntityPlayer) entity;
				if (isValidTarget(player)) {
					entities.add(player);
				}
			}
		}

		if (entities != null && entities.size() > multiTarget.getInputToInt()) {
			switch (sortMode.getMode()) {
			case "Distance": {
				entities.sort((entity1, entity2) -> (int) (entity1.getDistanceToEntity(mc.thePlayer) * 1000 - entity2.getDistanceToEntity(mc.thePlayer) * 1000));
				}
			break;
			case "Fov": {
				entities.sort(Comparator.comparingDouble(entity -> (RotationUtil.getDistanceAngles(mc.thePlayer.rotationPitch, RotationUtil.getRotations(entity)[0]))));
			}
			break;
			case "Angle": {
				entities.sort((entity1, entity2) -> {
					float[] rot1 = RotationUtil.getRotations(entity1);
					float[] rot2 = RotationUtil.getRotations(entity2);
					return (int) ((mc.thePlayer.rotationYaw - rot1[0]) - (mc.thePlayer.rotationYaw - rot2[0]));
				});}
			break;
			case "Health": {
				entities.sort((entity1, entity2) -> (int) (entity1.getHealth() - entity2.getHealth()));
				}
			break;
			case "Armor": {
				entities.sort(Comparator.comparingInt(entity -> (entity instanceof EntityPlayer ? ((EntityPlayer) entity).inventory.getTotalArmorValue() : (int) entity.getHealth())));
				}
			break;
			case "Best": {
				entities.sort((entity1, entity2) -> (int) (isBestTarget(entity1) - isBestTarget(entity2)));
				}
			break;
			case "Unknown": {
				entities.sort((entity1, entity2) -> (int) (isUnknownTarget(entity1) - isUnknownTarget(entity2)));
				}
			break;
			}

			return entities.get(multiTarget.getInputToInt());
		}

		return null;
	}

	public static boolean isValidTarget(EntityPlayer ep) {
		if (ep == mc.thePlayer && ep.isDead) {
			return false;
		}

		if (!(mc.thePlayer.getDistanceToEntity(ep) < distance.getInput())) {
			return false;
		}
		
		if (!friends.isToggled() && isAFriend(ep)) {
			return false;
		}

		if (!teams.isToggled() && isATeamMate(ep)) {
			return false;
		}
		
		if (!bots.isToggled() && AntiBot.bot(ep)) {
			return false;
		}
			
		if (!invis.isToggled() && ep.isInvisible()) {
			return false;
		}

		if (!naked.isToggled() && !PlayerUtil.isPlayerNaked(ep)) {
			return false;
		}

		if (!PlayerUtil.fov(ep, fov.getInputToFloat())) {
			return false;
		}

	      return true;
	}

	public static double getDistanceToEntity(EntityLivingBase entity) {
		Vec3 playerVec = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(),
				mc.thePlayer.posZ);
		double yDiff = mc.thePlayer.posY - entity.posY;
		double targetY = yDiff > 0 ? entity.posY + entity.getEyeHeight()
				: -yDiff < mc.thePlayer.getEyeHeight() ? mc.thePlayer.posY + mc.thePlayer.getEyeHeight() : entity.posY;
		Vec3 targetVec = new Vec3(entity.posX, targetY, entity.posZ);
		return playerVec.distanceTo(targetVec) - 0.3F;
	}

	public static boolean isAFriend(Entity entity) {
		if (entity == mc.thePlayer)
			return true;

		for (Entity en : FriendUtil.friends)
			if (en.equals(entity))
				return true;
		return false;
	}

	public static boolean isATeamMate(Entity entity) {
		EntityPlayer teamMate = (EntityPlayer) entity;
		if (mc.thePlayer.isOnSameTeam((EntityLivingBase) entity) || mc.thePlayer.getDisplayName().getUnformattedText().startsWith(teamMate.getDisplayName().getUnformattedText().substring(0, 2)))
			return true;
		return false;
	}

	private static double isBestTarget(Entity entity) {
		if (entity instanceof EntityLivingBase) {
			double distance = mc.thePlayer.getDistanceToEntity(entity);
			double health = ((EntityLivingBase) entity).getHealth();
			double hurtTime = 10.0;
			if (entity instanceof EntityPlayer) {
				hurtTime = ((EntityPlayer) entity).hurtTime;
			}

			return distance * 2.0 + health + hurtTime * 4.0;
		} else {
			return 1000.0;
		}
	}

	private static double isUnknownTarget(Entity entity) {
		if (!(entity instanceof EntityLivingBase) || (!CombatUtil.couldHit(entity, 1.0F, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, 180.0F, 180.0F) || distance.getInput() != 3.0) && distance.getInput() == 3.0) {
			return 1000.0;
		} else {
			double distance = mc.thePlayer.getDistanceToEntity(entity);
			double hurtTime = ((EntityLivingBase) entity).hurtTime * 6;
			return hurtTime + distance;
		}
	}
}
