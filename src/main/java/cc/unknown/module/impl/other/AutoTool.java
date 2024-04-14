package cc.unknown.module.impl.other;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.lwjgl.input.Mouse;

import com.google.common.util.concurrent.AtomicDouble;

import cc.unknown.Haru;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.render.Render3DEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.impl.combat.AutoClick;
import cc.unknown.module.setting.impl.BooleanValue;
import cc.unknown.utils.client.Cold;
import cc.unknown.utils.player.PlayerUtil;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.BlockPos;

@Register(name = "AutoTool", category = Category.Other)
public class AutoTool extends Module {
    private final BooleanValue hotkeyBack = new BooleanValue("Hotkey back", true);
    private Block previousBlock;
    private boolean isWaiting;
    private int previousSlot;
    private boolean mining;
    private Cold timer = new Cold(0);

    public AutoTool() {
        this.registerSetting(hotkeyBack);
    }
    
    @EventLink
    public void onRender(Render3DEvent e) {
        BlockPos lookingAtBlock = mc.objectMouseOver.getBlockPos();
        Block stateBlock = mc.theWorld.getBlockState(lookingAtBlock).getBlock();
        AutoClick clicker = (AutoClick) Haru.instance.getModuleManager().getModule(AutoClick.class);
        if (!PlayerUtil.inGame() || mc.currentScreen != null || !Mouse.isButtonDown(0)) {
            if (mining) {
                finishMining();
            }
            isWaiting = false;
            return;
        }

        if (clicker.isEnabled() && !clicker.getBreakBlocks().isToggled() || lookingAtBlock == null || stateBlock == Blocks.air || stateBlock instanceof BlockLiquid) {
            return;
        }

        int maxDelay = 0; 

        if (maxDelay > 0) {
            if (previousBlock != stateBlock || !mining) {
                previousBlock = stateBlock;
                isWaiting = true;
                timer.hasTimeElapsed((long) ThreadLocalRandom.current().nextDouble(0, maxDelay + 0.01), true);
            } else {
                if (isWaiting) {
                    isWaiting = false;
                    previousSlot = mc.thePlayer.inventory.currentItem;
                    mining = true;
                    hotkeyToFastest();
                }
            }
        } else {
            if (!mining) {
                mc.thePlayer.inventory.currentItem = previousSlot;
                mining = true;
            }
            hotkeyToFastest();
        }
    }

    private void finishMining() {
        if (hotkeyBack.isToggled()) mc.thePlayer.inventory.currentItem = previousSlot;
        mining = false;
    }

    private void hotkeyToFastest() {
        AtomicInteger index = new AtomicInteger(-1);
        AtomicDouble speed = new AtomicDouble(1.0);

        IntStream.rangeClosed(0, 8)
                .forEach(slot -> {
                    ItemStack itemInSlot = mc.thePlayer.inventory.getStackInSlot(slot);
                    if (itemInSlot != null &&
                            (itemInSlot.getItem() instanceof ItemTool || itemInSlot.getItem() instanceof ItemShears)) {
                        BlockPos p = mc.objectMouseOver.getBlockPos();
                        Block bl = mc.theWorld.getBlockState(p).getBlock();
                        double digSpeed = itemInSlot.getItem().getDigSpeed(itemInSlot, bl.getDefaultState());
                        if (digSpeed > speed.get()) {
                            speed.set(digSpeed);
                            index.set(slot);
                        }
                    }
                });

        if (index.get() != -1 && speed.get() > 1.1 && speed.get() != 0) {
            mc.thePlayer.inventory.currentItem = index.get();
            mining = false;
        }
    }
}