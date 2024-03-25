package cc.unknown.command.commands;

import cc.unknown.Haru;
import cc.unknown.command.Command;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.packet.PacketEvent;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;

public class TransactionCommand extends Command {

	private boolean toggle = false;

	public TransactionCommand() {
		Haru.instance.getEventBus().register(this);
	}

	@Override
	public void onExecute(String[] args) {
		toggle = !toggle;
	}

	@EventLink
	public void onPacket(PacketEvent e) {
		if (!toggle) return;
		if (e.isReceive() && e.getPacket() instanceof S32PacketConfirmTransaction) {
			PlayerUtil.send(getRed() + " [Transaction ID]: " + getWhite() + ((S32PacketConfirmTransaction) e.getPacket()).getActionNumber());
		}
	}

	@Override
	public String getName() {
		return "transaction";
	}

	@Override
	public String getSyntax() {
		return ".transaction";
	}

	@Override
	public String getDesc() {
		return "Show server transaction IDs";
	}
}
