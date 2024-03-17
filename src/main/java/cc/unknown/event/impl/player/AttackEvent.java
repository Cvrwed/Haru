package cc.unknown.event.impl.player;

import cc.unknown.event.Event;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class AttackEvent extends Event {

	private EntityPlayer playerIn;
	private Entity target;

    /**
     * Constructs a new AttackEvent with the specified player and target.
     *
     * @param playerIn The player initiating the attack.
     * @param target The entity being attacked.
     */
    public AttackEvent(EntityPlayer playerIn, Entity target) {
        this.playerIn = playerIn;
        this.target = target;
    }

    /**
     * Gets the player who initiated the attack.
     *
     * @return The player initiating the attack.
     */
    public EntityPlayer getPlayerIn() {
        return playerIn;
    }

    /**
     * Sets the player who initiated the attack.
     *
     * @param playerIn The player initiating the attack.
     */
    public void setPlayerIn(EntityPlayer playerIn) {
        this.playerIn = playerIn;
    }

    /**
     * Gets the entity being attacked.
     *
     * @return The entity being attacked.
     */
    public Entity getTarget() {
        return target;
    }

    /**
     * Sets the entity being attacked.
     *
     * @param target The entity being attacked.
     */
    public void setTarget(Entity target) {
        this.target = target;
    }
}
