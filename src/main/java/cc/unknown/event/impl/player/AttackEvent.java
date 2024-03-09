package cc.unknown.event.impl.player;

import cc.unknown.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

@Getter
@Setter
@AllArgsConstructor
public class AttackEvent extends Event {
	private EntityPlayer playerIn;
    private Entity target;

}
