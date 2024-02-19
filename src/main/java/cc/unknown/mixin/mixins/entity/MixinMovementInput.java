package cc.unknown.mixin.mixins.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.util.MovementInput;

@Mixin(MovementInput.class)
public class MixinMovementInput {

    @Shadow 
    public float moveStrafe;

    @Shadow
    public float moveForward;

    @Shadow
    public boolean jump;

    @Shadow
    public boolean sneak;

    @Shadow
    public void updatePlayerMoveState() {
    }

}