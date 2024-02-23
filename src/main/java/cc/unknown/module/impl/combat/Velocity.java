package cc.unknown.module.impl.combat;

import cc.unknown.event.impl.api.EventLink;
import cc.unknown.event.impl.move.PreUpdateEvent;
import cc.unknown.event.impl.packet.PacketEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.module.setting.impl.SliderValue;
import cc.unknown.utils.helpers.MathHelper;
import cc.unknown.utils.player.MoveUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

public class Velocity extends Module {

	private ModeValue mode = new ModeValue("Mode", "Packet", "Packet", "Tick");
	public SliderValue horizontal = new SliderValue("Horizontal", 90, -100, 100, 1);
	public SliderValue vertical = new SliderValue("Vertical", 100, -100, 100, 1);
	public SliderValue chance = new SliderValue("Chance", 100, 0, 100, 1);
	private BooleanValue onlyCombat = new BooleanValue("Only combat", false);
	private BooleanValue onlyGround = new BooleanValue("Only ground", false);
	
	public Velocity() {
		super("Velocity", ModuleCategory.Combat);
		this.registerSetting(mode, horizontal, vertical, chance, onlyCombat, onlyGround);
	}

	@EventLink
	public void onPacket(PacketEvent e) {
        if (shouldIgnoreVelocity() || applyChance()) {
            return;
        }
		
        if (e.isReceive()) {
        	if (mode.is("Packet") && e.getPacket() instanceof S12PacketEntityVelocity) {
            	final S12PacketEntityVelocity s12 = (S12PacketEntityVelocity) e.getPacket();            	
            	if (s12.getEntityID() == mc.thePlayer.getEntityId()) {
            		
            		if (horizontal.getInput() == 0) {
            			e.setCancelled(true);

            			if (vertical.getInput() != 0) {
            				mc.thePlayer.motionY = s12.getMotionY() / 8000.0D;
            			}
            			return;
            		}

            		s12.motionX *= horizontal.getInput() / 100;
            		s12.motionY *= vertical.getInput() / 100;
            		s12.motionZ *= horizontal.getInput() / 100;

            		e.setPacket(s12);
                }
        	}
        }
	}
	
    @EventLink
    public void onPre(PreUpdateEvent e) {
    	if (PlayerUtil.inGame()) {
    		if (mode.is("Tick") && mc.thePlayer.hurtTime == 10 - MathHelper.randomInt(3, 4)) {
	        	MoveUtil.stop();
	        }
    	}
    }
	
    private boolean shouldIgnoreVelocity() {
        return (onlyCombat.isToggled() && (mc.objectMouseOver == null || mc.objectMouseOver.entityHit == null)) || (onlyGround.isToggled() && mc.thePlayer.onGround);
    }
    
    private boolean applyChance() {
        return chance.getInput() != 100.0D && Math.random() >= chance.getInput() / 100.0D;
    }
}
