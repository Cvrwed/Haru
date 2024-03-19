package cc.unknown.command.commands;

import cc.unknown.Haru;
import cc.unknown.command.Command;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.packet.PacketEvent;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.util.EnumChatFormatting;

public class TransactionCommand extends Command {

	private boolean toggle = false;

	public TransactionCommand() {
		super("transaction");
		Haru.instance.getEventBus().register(this);
	}

	@Override
	public void onExecute(String alias, String[] args) {
		toggle = !toggle;
	}

	@EventLink
	public void onPacket(PacketEvent e) {
		if (!toggle) return;
		if (e.isReceive() && e.getPacket() instanceof S32PacketConfirmTransaction) {
			PlayerUtil.send(EnumChatFormatting.RED + " [Transaction ID]: " + EnumChatFormatting.RESET + ((S32PacketConfirmTransaction) e.getPacket()).getActionNumber());
		}
	}
}
