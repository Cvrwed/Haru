package cc.unknown.mixin.mixins.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import cc.unknown.Haru;
import cc.unknown.event.impl.player.AttackEvent;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

@Mixin(PlayerControllerMP.class)
public class MixinPlayerControllerMP {

	@Inject(method = "attackEntity", at = @At("HEAD"))
    public void onAttackEntity(EntityPlayer playerIn, Entity targetEntity, CallbackInfo ci) {
    	Haru.instance.getEventBus().post(new AttackEvent(playerIn, targetEntity));

    }
}