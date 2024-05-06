package cc.unknown.command.commands;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import cc.unknown.Haru;
import cc.unknown.command.Command;
import cc.unknown.command.Flips;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.network.PacketEvent;
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

@Flips(name = "Game", alias = "join", desc = "It automatically enters the selected minigame.", syntax = ".game <mini game> <lobby>")
public class GameCommand extends Command {

    private HashMap<String, Item> hashMap = new HashMap<>();
    private boolean joining;
    private Item item;
    private int number;
    protected int delay;
    private int stage;
    private boolean foundItem;
    protected boolean foundGame;
    protected boolean foundLobby;

    public GameCommand() {
        init();
        Haru.instance.getEventBus().register(this);
    }

    /**
     * Executes the game command based on the provided arguments.
     *
     * @param args The arguments for the command.
     */
    @Override
    public void onExecute(String[] args) {
        AtomicReference<String> message = new AtomicReference<>("");

        if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            clearChat();
            message.set(getList());
        } else {
            if (args.length < 2 || args.length == 0) {
                message.set(getColor("Red") + " Syntax Error. Use: " + syntax);
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

        this.sendChat(message.get());
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
                        PacketUtil.sendQueue((Packet<INetHandlerPlayServer>) new C08PacketPlayerBlockPlacement(player.getHeldItem()));
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
                                    PacketUtil.sendQueue((Packet<INetHandlerPlayServer>) new C0EPacketClickWindow(container.inventorySlots.windowId, i, 0, 0, slot, (short) 1));
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
                                    PacketUtil.sendQueue((Packet<INetHandlerPlayServer>) new C0EPacketClickWindow(container.inventorySlots.windowId, i, 0, 0, slot, (short) 1));
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

    /**
     * Initializes the hashMap with game names and items.
     */
    private void init() {
        this.hashMap.put("sw", Items.bow);
        this.hashMap.put("tsw", Items.arrow);
        this.hashMap.put("bw", Items.bed);
        this.hashMap.put("tnt", Items.gunpowder);
        this.hashMap.put("pgames", Items.cake);
        this.hashMap.put("arena", Items.diamond_sword);
    }

    /**
     * Gets the list of available games.
     *
     * @return The list of available games.
     */
    private String getList() {
        return "\n" +
                getColor("Green") + " - " + getColor("White") + "sw" + getColor("Gray") + " (Skywars)        \n" +
                getColor("Green") + " - " + getColor("White") + "tsw" + getColor("Gray") + " (Team Skywars)  \n" +
                getColor("Green") + " - " + getColor("White") + "tnt" + getColor("Gray") + " (Tnt Tag)       \n" +
                getColor("Green") + " - " + getColor("White") + "bw" + getColor("Gray") + " (Bedwars)        \n" +
                getColor("Green") + " - " + getColor("White") + "pgames" + getColor("Gray") + " (Party Games)\n" +
                getColor("Green") + " - " + getColor("White") + "arena" + getColor("Gray") + " (Arenapvp)    \n";
    }

    /**
     * Starts the joining process for a game.
     *
     * @param name  The item associated with the game.
     * @param lobby The lobby number.
     */
    private void startJoining(Item name, int lobby) {
        joining = true;
        item = name;
        number = lobby;
        delay = 0;
        stage = 0;
        foundItem = foundGame = foundLobby = false;
    }
}
