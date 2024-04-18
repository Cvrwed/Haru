package cc.unknown.command.commands;

import java.util.concurrent.atomic.AtomicBoolean;

import cc.unknown.Haru;
import cc.unknown.command.Command;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.event.impl.network.PacketEvent.Type;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;

public class TransactionCommand extends Command {

    private AtomicBoolean toggle = new AtomicBoolean(false);

    public TransactionCommand() {
        Haru.instance.getEventBus().register(this);
    }

    @Override
    public void onExecute(String[] args) {
        toggle.set(!toggle.get());
    }

    @EventLink
    public void onPacket(PacketEvent e) {
        if (!toggle.get()) return;
        if (e.getType() == Type.RECEIVE && e.getPacket() instanceof S32PacketConfirmTransaction) {
            this.sendChat(getColor("Red") + " [Transaction ID]: " + getColor("White") + ((S32PacketConfirmTransaction) e.getPacket()).getActionNumber());
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

	@Override
	public String getAlias() {
		return "s32";
	}
}