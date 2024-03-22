package cc.unknown.command.commands;

import java.util.HashMap;
import java.util.List;

import cc.unknown.Haru;
import cc.unknown.command.Command;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.packet.PacketEvent;
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
import net.minecraft.util.EnumChatFormatting;

@SuppressWarnings("unused")
public class GameCommand extends Command {
	
	final HashMap<String, Item> hashMap;
	private boolean joining;
	private Item item;
	private int number;
	private int delay;
	private int stage;
	private boolean foundItem;
	private boolean foundGame;
	private boolean foundLobby;
	
	/* Credits to Moshi - Blossom Dev */
	
	public GameCommand() {
		this.hashMap = new HashMap<>();
		Haru.instance.getEventBus().register(this);
	}

	@Override
	public void onExecute(String[] args) {
	    if (args.length < 2) {
            PlayerUtil.send(EnumChatFormatting.RED + " Syntax Error.");
	    }
	      this.hashMap.put("sw", Items.bow); // skywars
	      this.hashMap.put("tsw", Items.arrow); //team skywars
	      this.hashMap.put("bw", Items.bed); // bedwars
	      this.hashMap.put("arena", Items.diamond_sword); // arenapvp
	      String gameName = args[0];
	      int lobby = Integer.parseInt(args[1]);
	      if (!this.hashMap.containsKey(gameName)) {
	    	  PlayerUtil.send(EnumChatFormatting.RED + " Syntax Error.");
	      }
	      startJoining(this.hashMap.get(gameName), lobby);
          PlayerUtil.send(EnumChatFormatting.YELLOW + " Have a coffee while I try to get you into the mini-game.");
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
		return "This is only for UniverseCraft, it automatically enters the selected minigame.";
	}

	@EventLink
	public void onPacket(PacketEvent e) {
		if (e.isReceive() && PlayerUtil.inGame()) {
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
}
