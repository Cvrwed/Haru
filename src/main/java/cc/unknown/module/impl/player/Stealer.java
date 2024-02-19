package cc.unknown.module.impl.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;

import cc.unknown.event.impl.api.EventLink;
import cc.unknown.event.impl.render.Render2DEvent;
import cc.unknown.module.Module;
import cc.unknown.module.impl.ModuleCategory;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.module.setting.impl.DoubleSliderValue;
import cc.unknown.utils.client.AdvancedTimer;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerPlayer;

public class Stealer extends Module {
    
    private final DoubleSliderValue firstDelay = new DoubleSliderValue("Open delay", 250, 450, 0, 1000, 1);
    private final DoubleSliderValue delay = new DoubleSliderValue("Delay", 150, 250, 0, 1000, 1);
    private final DoubleSliderValue closeDelay = new DoubleSliderValue("Close delay", 150, 250, 0, 1000, 1);
    private final BooleanValue autoClose = new BooleanValue("Auto Close", true);
    private boolean inChest;
    private final AdvancedTimer delayTimer = new AdvancedTimer(0);
    private final AdvancedTimer closeTimer = new AdvancedTimer(0);
    private ArrayList<Slot> sortedSlots;
    private ContainerChest chest;

    public Stealer() {
        super("Stealer", ModuleCategory.Player);
        this.registerSetting(firstDelay, delay, closeDelay, autoClose);

    }

    @EventLink
    public void onRender2D(Render2DEvent e) {
        if ((mc.currentScreen != null) && (mc.thePlayer.inventoryContainer != null) && (mc.thePlayer.inventoryContainer instanceof ContainerPlayer) && (mc.currentScreen instanceof GuiChest)) {
            if (!inChest) {
                chest = (ContainerChest) mc.thePlayer.openContainer;
                delayTimer.setCooldown((long) ThreadLocalRandom.current().nextDouble(firstDelay.getInputMin(),
                        firstDelay.getInputMax() + 0.01));
                delayTimer.start();
                generatePath(chest);
                inChest = true;
            }
            
            if (inChest && !sortedSlots.isEmpty()) {
                if (delayTimer.hasFinished()) {
                    mc.playerController.windowClick(mc.thePlayer.openContainer.windowId, sortedSlots.get(0).s, 0, 1,
                            mc.thePlayer);
                    delayTimer.setCooldown((long) ThreadLocalRandom.current().nextDouble(delay.getInputMin(),
                            delay.getInputMax() + 0.01));
                    delayTimer.start();
                    sortedSlots.remove(0);
                }
            }
            
            if (sortedSlots.isEmpty() && autoClose.isToggled()) {
                if (closeTimer.firstFinish()) {
                    mc.thePlayer.closeScreen();
                    inChest = false;
                } else {
                    closeTimer.setCooldown((long) ThreadLocalRandom.current().nextDouble(closeDelay.getInputMin(),
                            closeDelay.getInputMax() + 0.01));
                    closeTimer.start();
                }
            }
        } else {
            inChest = false;
        }
    }

    private void generatePath(ContainerChest chest) {
        ArrayList<Slot> slots = new ArrayList<Slot>();
        for (int i = 0; i < chest.getLowerChestInventory().getSizeInventory(); i++) {
            if (chest.getInventory().get(i) != null)
                slots.add(new Slot(i));
        }
        Slot[] ss = sort(slots.toArray(new Slot[slots.size()]));
        ArrayList<Slot> newSlots = new ArrayList<Slot>();
        Collections.addAll(newSlots, ss);
        this.sortedSlots = newSlots;
    }

    private Slot[] sort(Slot[] in) {
        if (in == null || in.length == 0) {
            return in;
        }
        Slot[] out = new Slot[in.length];
        Slot current = in[ThreadLocalRandom.current().nextInt(0, in.length)];
        for (int i = 0; i < in.length; i++) {
            if (i == in.length - 1) {
                out[in.length - 1] = Arrays.stream(in).filter(p -> !p.visited).findAny().orElseGet(null);
                break;
            }
            Slot finalCurrent = current;
            out[i] = finalCurrent;
            finalCurrent.visit();
            Slot next = Arrays.stream(in).filter(p -> !p.visited)
                    .min(Comparator.comparingDouble(p -> p.getDistance(finalCurrent))).get();
            current = next;
        }
        return out;
    }

    class Slot {
        final int x;
        final int y;
        final int s;
        boolean visited;

        Slot(int s) {
            this.x = (s + 1) % 10;
            this.y = s / 9;
            this.s = s;
        }

        public double getDistance(Slot s) {
            return Math.abs(this.x - s.x) + Math.abs(this.y - s.y);
        }

        public void visit() {
            visited = true;
        }
    }

}