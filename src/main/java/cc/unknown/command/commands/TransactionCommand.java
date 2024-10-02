package cc.unknown.command.commands;

import java.util.concurrent.atomic.AtomicBoolean;

import cc.unknown.Haru;
import cc.unknown.command.Command;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.netty.PacketEvent;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;

public class TransactionCommand extends Command {
	
	public TransactionCommand() {
		super("Transaction", "Show server transaction IDs", "s32", ".transaction");
        Haru.instance.getEventBus().register(this);
	}

    private AtomicBoolean toggle = new AtomicBoolean(false);

    @Override
    public void onExecute(String[] args) {
        toggle.set(!toggle.get());
    }

    @EventLink
    public void onPacket(PacketEvent e) {
        if (!toggle.get()) return;
        if (e.getPacket() instanceof S32PacketConfirmTransaction) {
            this.sendChat(getColor("Red") + " [Transaction ID]: " + getColor("White") + ((S32PacketConfirmTransaction) e.getPacket()).getActionNumber());
        }
    }
}