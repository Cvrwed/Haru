package cc.unknown.module.impl.other;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import cc.unknown.Haru;
import cc.unknown.event.impl.EventLink;
import cc.unknown.event.impl.other.MouseEvent;
import cc.unknown.module.impl.Module;
import cc.unknown.module.impl.api.Category;
import cc.unknown.module.impl.api.Register;
import cc.unknown.module.setting.impl.ModeValue;
import cc.unknown.utils.player.FriendUtil;
import cc.unknown.utils.player.PlayerUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

@Register(name = "Midclick", category = Category.Other)
public class MidClick extends Module {

    private AtomicBoolean x = new AtomicBoolean(false);
    private AtomicInteger prevSlot = new AtomicInteger(0);
    private Robot bot;
    private AtomicInteger pearlEvent = new AtomicInteger(4);
    private ModeValue mode = new ModeValue("Mode", "Add/Remove friend", "Add/Remove friend", "Throw pearl");
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public MidClick() {
        this.registerSetting(mode);
    }

    @Override
    public void onEnable() {
        try {
            this.bot = new Robot();
        } catch (AWTException x) {
            this.disable();
        }
    }

    @EventLink
    public void onMouse(MouseEvent e) {
        if (mc.currentScreen != null)
            return;

        if (pearlEvent.get() < 4) {
            if (pearlEvent.get() == 3)
                mc.thePlayer.inventory.currentItem = prevSlot.get();
            pearlEvent.incrementAndGet();
        }

        if (!x.get() && e.getButton() == 2) {
            if (mode.is("Add/Remove friend") && mc.objectMouseOver.entityHit instanceof EntityPlayer) {
                EntityPlayer playerHit = (EntityPlayer) mc.objectMouseOver.entityHit;
                if (!FriendUtil.instance.isAFriend(playerHit)) {
                    FriendUtil.instance.addFriend(playerHit);
                    if (Haru.instance.getHudConfig() != null) {
                        Haru.instance.getHudConfig().saveHud();
                    }
                    PlayerUtil.send(EnumChatFormatting.GRAY + playerHit.getName() + " was added to your friends.");
                } else {
                    FriendUtil.instance.removeFriend(playerHit);
                    if (Haru.instance.getHudConfig() != null) {
                        Haru.instance.getHudConfig().saveHud();
                    }
                    PlayerUtil.send(EnumChatFormatting.GRAY + playerHit.getName() + " was removed from your friends.");
                }
            }

            if (mode.is("Throw pearl")) {
                for (int s = 0; s <= 8; s++) {
                    ItemStack item = mc.thePlayer.inventory.getStackInSlot(s);
                    if (item != null && item.getItem() instanceof ItemEnderPearl) {
                        prevSlot.set(mc.thePlayer.inventory.currentItem);
                        mc.thePlayer.inventory.currentItem = s;
                        executorService.execute(() -> {
                            bot.mousePress(InputEvent.BUTTON3_MASK);
                            bot.mouseRelease(InputEvent.BUTTON3_MASK);
                        });
                        pearlEvent.set(0);
                        x.set(true);
                        return;
                    }
                }
            }
        }
        x.set(e.getButton() == 2);
    }
}