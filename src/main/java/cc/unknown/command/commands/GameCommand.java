package cc.unknown.command.commands;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import cc.unknown.Haru;
import cc.unknown.command.Command;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.network.PacketEvent;
import cc.unknown.event.impl.network.PacketEvent.Type;
import cc.unknown.event.impl.player.TickEvent;
import cc.unknown.utils.network.PacketUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;

@SuppressWarnings("unused")
public class GameCommand extends Command {

	private HashMap<String, Item> hashMap = new HashMap<>();
	private boolean joining;
	private Item item;
	private int number;
	private int delay;
	private int stage;
	private boolean foundItem;
	private boolean foundGame;
	private boolean foundLobby;

	public GameCommand() {
        init();
		Haru.instance.getEventBus().register(this);
	}

	@Override
	public void onExecute(String[] args) {
	    AtomicReference<String> message = new AtomicReference<>("");

	    if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
	    	clearChat();
	        message.set(getList());
	    } else {
	        if (args.length < 2 || args.length == 0) {
	            message.set(getColor("Red") + " Syntax Error. Use: " + getSyntax());
	            return;
	        }

	        String gameName = args[0];
	        int lobby;

	        if (!this.hashMap.containsKey(gameName)) {
	            message.set(getColor("Red") + " Invalid game. Use: .game list");
	            return;
	        }

	        if (!args[1].matches("\\d+")) {
	            message.set(getColor("Red") + " Invalid number.");
	            return;
	        }

	        lobby = Integer.parseInt(args[1]);

	        if (lobby == 0) {
	            message.set(getColor("Red") + " Invalid lobby.");
	            return;
	        }

	        startJoining(this.hashMap.get(gameName), lobby);
	        message.set(getColor("Yellow") + " Have a coffee while I try to get you into the mini-game.");
	    }

	    PlayerUtil.send(message.get());
	}

	@Override
	public String getName() {
		return "game";
	}

	@Override
	public String getSyntax() {
		return ".game <mini game> <lobby>";
	}

	@Override
	public String getDesc() {
		return "It automatically enters the selected minigame.";
	}
	
    private void init() {
        this.hashMap.put("sw", Items.bow);
        this.hashMap.put("tsw", Items.arrow);
        this.hashMap.put("bw", Items.bed);
        this.hashMap.put("pgames", Items.cake);
        this.hashMap.put("arena", Items.diamond_sword);
    }

	private String getList() {
        return getColor("Green") + " - " + getColor("White") + "sw" + getColor("Gray") + " (Skywars)        \n" +
                getColor("Green") + " - " + getColor("White") + "tsw" + getColor("Gray") + " (Team Skywars)  \n" +
                getColor("Green") + " - " + getColor("White") + "bw" + getColor("Gray") + " (Bedwars)        \n" +
                getColor("Green") + " - " + getColor("White") + "pgames" + getColor("Gray") + " (Party Games)\n" +
                getColor("Green") + " - " + getColor("White") + "arena" + getColor("Gray") + " (Arenapvp)    \n";
     }
	
	@EventLink
	public void onPacket(PacketEvent e) {
		if (e.getType() == Type.RECEIVE && PlayerUtil.inGame()) {
			if (e.getPacket() instanceof S08PacketPlayerPosLook)
				this.joining = false;
			if (this.stage == 2 && e.getPacket() instanceof S2DPacketOpenWindow)
				this.stage = 3;
			if (this.stage >= 3 && e.getPacket() instanceof S2EPacketCloseWindow)
				this.stage = 0;
		}
	}

	@EventLink
	public void onTick(TickEvent e) {
		if (PlayerUtil.inGame()) {

			if (mc.currentScreen instanceof GuiChat || PlayerUtil.isMoving()) {
				this.joining = false;
				return;
			}
			
			if (!this.joining)
				return;

			EntityPlayerSP player = mc.thePlayer;
			
			switch (this.stage) {

			case 0:
				if (!this.foundItem && player.inventoryContainer.getSlot(36).getHasStack()) {
					PacketUtil.send((Packet<INetHandlerPlayServer>) new C08PacketPlayerBlockPlacement(player.getHeldItem()));
					this.stage++;
				}
				break;
			case 1:
				if (mc.currentScreen instanceof GuiContainer) {
					GuiContainer container = (GuiContainer) mc.currentScreen;
					List<ItemStack> inventory = container.inventorySlots.getInventory();
					for (int i = 0; i < inventory.size(); i++) {
						ItemStack slot = inventory.get(i);
						if (slot != null)
							if (slot.getItem() == this.item) {
								PacketUtil.send((Packet<INetHandlerPlayServer>) new C0EPacketClickWindow(container.inventorySlots.windowId, i, 0, 0, slot, (short) 1));
								this.stage++;
								break;
							}
					}
				}
				break;
			case 3:
				if (mc.currentScreen instanceof GuiContainer) {
					GuiContainer container = (GuiContainer) mc.currentScreen;
					List<ItemStack> inventory = container.inventorySlots.getInventory();
					for (int i = 0; i < inventory.size(); i++) {
						ItemStack slot = inventory.get(i);
						if (slot != null)
							if (slot.stackSize == this.number) {
								PacketUtil.send((Packet<INetHandlerPlayServer>) new C0EPacketClickWindow(container.inventorySlots.windowId, i, 0, 0, slot, (short) 1));
								this.stage++;
								break;
							}
					}
				}
				break;
			case 4:
				if (player.ticksExisted % 11 == 0)
					this.stage = 3;
				break;
			}
		}
	}
	
	private void startJoining(Item name, int lobby) {
		joining = true;
		item = name;
		number = lobby;
		delay = 0;
		stage = 0;
		foundItem = foundGame = foundLobby = false;
	}

	@Override
	public String getAlias() {
		return "join";
	}
}
