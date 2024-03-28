package cc.unknown.module.impl.combat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.move.LivingUpdateEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.client.Cold;
import cc.unknown.utils.player.CombatUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.world.World;

public class AutoRod extends Module {

	private Cold pushTimer = new Cold(0);
	private Cold rodPullTimer = new Cold(0);

    private boolean rodInUse = false;
    private int switchBack = -1;
    private final String[] healthSubstrings = {"hp", "health", "❤", "lives"};

    private final BooleanValue checkEnemy = new BooleanValue("Check enemy", true);
    private final BooleanValue ignoreOnEnemyLowHealth = new BooleanValue("Ignore enemy low health", true);
    private final BooleanValue healthFromScoreboard = new BooleanValue("Health from scoreboard", false);
    private final BooleanValue absorption = new BooleanValue("Absorption", false);
    private final SliderValue activationDistance = new SliderValue("Activation distance", 8, 1, 20, 1);
    private final SliderValue enemiesNearby = new SliderValue("Multi Target", 1, 1, 5, 1);
    private final SliderValue playerHealth = new SliderValue("Player health", 5, 1, 20, 1);
    private final SliderValue enemyHealth = new SliderValue("Enemy health", 5, 1, 20, 1);
    private final SliderValue escapeHealth = new SliderValue("Escape health", 10, 1, 20, 1);
    private final SliderValue pushDelay = new SliderValue("Push delay", 100, 50, 1000, 1);
    private final SliderValue pullbackDelay = new SliderValue("Pullback delay", 500, 50, 1000, 1);
    private final BooleanValue usingItem = new BooleanValue("Using item", false);

	public AutoRod() {
		super("AutoRod", ModuleCategory.Combat);
		this.registerSetting(checkEnemy, ignoreOnEnemyLowHealth, healthFromScoreboard, absorption, activationDistance,
				enemiesNearby, playerHealth, enemyHealth, escapeHealth, pushDelay, pullbackDelay, usingItem);
	}

	@EventLink
    public void onUpdate(LivingUpdateEvent e) {
        if (mc == null || mc.thePlayer == null)
            return;

        boolean usingRod = (mc.thePlayer.isUsingItem() && mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() == Items.fishing_rod) || rodInUse;

        if (usingRod) {
            if (rodPullTimer.hasTimeElapsed(pullbackDelay.getInputToLong(), false)) {
                if (switchBack != -1 && mc.thePlayer.inventory.currentItem != switchBack) {
                    mc.thePlayer.inventory.currentItem = switchBack;
                    mc.playerController.updateController();
                } else {
                    mc.thePlayer.stopUsingItem();
                }
                switchBack = -1;
                rodInUse = false;
                pushTimer.reset();
            }
        } else {
            boolean rod = false;
            if (checkEnemy.isToggled() && getHealth(mc.thePlayer, healthFromScoreboard.isToggled(), absorption.isToggled()) >= playerHealth.getInput()) {
                Entity facingEntity = mc.objectMouseOver != null ? mc.objectMouseOver.entityHit : null;
                List<Entity> nearbyEnemies = getAllNearbyEnemies();

                if (facingEntity == null) {
                    facingEntity = CombatUtil.instance.rayCast(activationDistance.getInput(), entity -> CombatUtil.instance.canTarget(entity, true));
                }

                if (!usingItem.isToggled()) {
                    if (mc.thePlayer.getItemInUse() == null && mc.thePlayer.isUsingItem())
                        return;
                }

                if (CombatUtil.instance.canTarget(facingEntity, true)) {
                    if (nearbyEnemies.size() <= enemiesNearby.getInput()) {
                        if (ignoreOnEnemyLowHealth.isToggled()) {
                            if (getHealth((EntityPlayer) facingEntity, healthFromScoreboard.isToggled(), absorption.isToggled()) >= enemyHealth.getInput()) {
                                rod = true;
                            }
                        } else {
                            rod = true;
                        }
                    }
                }
            } else if (getHealth(mc.thePlayer, healthFromScoreboard.isToggled(), absorption.isToggled()) <= escapeHealth.getInput()) {
                rod = true;
            } else if (!checkEnemy.isToggled()) {
                rod = true;
            }

            if (rod && pushTimer.hasTimeElapsed(pushDelay.getInputToLong(), false)) {
                if (mc.thePlayer.getHeldItem() == null || mc.thePlayer.getHeldItem().getItem() != Items.fishing_rod) {
                    int rodSlot = findRod(36, 45);

                    if (rodSlot == -1)
                        return;

                    switchBack = mc.thePlayer.inventory.currentItem;
                    mc.thePlayer.inventory.currentItem = rodSlot - 36;
                    mc.playerController.updateController();
                }

                rod();
            }
        }
    }

    private void rod() {
        int rodSlot = findRod(36, 45);
        if (rodSlot != -1) {
            mc.thePlayer.inventory.currentItem = rodSlot - 36;
            mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.inventoryContainer.getSlot(rodSlot).getStack());
            rodInUse = true;
            rodPullTimer.reset();
        }
    }

    private int findRod(int startSlot, int endSlot) {
        for (int i = startSlot; i < endSlot; i++) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getStack() != null && mc.thePlayer.inventoryContainer.getSlot(i).getStack().getItem() == Items.fishing_rod) {
                return i;
            }
        }
        return -1;
    }

    private List<Entity> getAllNearbyEnemies() {
        if (mc == null || mc.thePlayer == null)
            return null;

        return (mc.theWorld.loadedEntityList.stream()
        		.filter(entity -> CombatUtil.instance.canTarget(entity, true))
                .filter(entity -> CombatUtil.instance.getDistanceToEntityBox(entity) < activationDistance.getInput()).collect(Collectors.toCollection(ArrayList::new)));
    }
    
    public float getHealth(EntityLivingBase entity, boolean fromScoreboard, boolean absorption) {
        if (fromScoreboard && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            World world = player.getEntityWorld();
            Scoreboard scoreboard = world.getScoreboard();
            ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(2);
            
            if (objective != null && objective.getDisplayName() != null && containsSubstring(objective.getDisplayName())) {
                Score score = scoreboard.getValueFromObjective(player.getName(), objective);
                int scoreboardHealth = score.getScorePoints();
                
                if (scoreboardHealth > 0)
                    return scoreboardHealth;
            }
        }

        float health = entity.getHealth();
        
        if (absorption)
            health += entity.getAbsorptionAmount();
        
        return health > 0 ? health : 20.0f;
    }


    private boolean containsSubstring(String displayName) {
        for (String substring : healthSubstrings) {
            if (displayName.contains(substring))
                return true;
        }
        return false;
    }
}
