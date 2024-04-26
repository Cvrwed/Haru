package cc.unknown.mixin.mixins.entity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.mojang.authlib.GameProfile;

import cc.unknown.Haru;
import cc.unknown.event.impl.move.HitSlowDownEvent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.potion.Potion;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.ForgeHooks;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends MixinEntityLivingBase {

	@Shadow
    public abstract GameProfile getGameProfile();
    
    @Shadow
    public abstract ItemStack getHeldItem();

    @Shadow
    public abstract void onCriticalHit(Entity p_onCriticalHit_1_);

    @Shadow
    public abstract void onEnchantmentCritical(Entity p_onEnchantmentCritical_1_);

    @Shadow
    public abstract void triggerAchievement(StatBase p_triggerAchievement_1_);

    @Shadow
    public abstract ItemStack getCurrentEquippedItem();

    @Shadow
    public abstract void destroyCurrentEquippedItem();

    @Shadow
    public abstract void addStat(StatBase p_addStat_1_, int p_addStat_2_);

    @Shadow
    public abstract void addExhaustion(float p_addExhaustion_1_);
    
    @Overwrite
    public void attackTargetEntityWithCurrentItem(Entity targetEntity) {
        if (ForgeHooks.onPlayerAttackTarget(((EntityPlayer) (Object) this), targetEntity)) {
            if (targetEntity.canAttackWithItem()
                    && !targetEntity.hitByEntity((EntityLivingBase) (Object)this)) {
                float f = (float) this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
                float f1 = 0.0F;
                if (targetEntity instanceof EntityLivingBase) {
                    f1 = EnchantmentHelper.func_152377_a(this.getHeldItem(),
                            ((EntityLivingBase) targetEntity).getCreatureAttribute());
                } else {
                    f1 = EnchantmentHelper.func_152377_a(this.getHeldItem(), EnumCreatureAttribute.UNDEFINED);
                }

                int i = EnchantmentHelper.getKnockbackModifier((EntityPlayer)(Object) this);
                if (this.isSprinting()) {
                    ++i;
                }

                if (f > 0.0F || f1 > 0.0F) {
                    boolean flag = this.fallDistance > 0.0F && !this.onGround && !this.isOnLadder() && !this.isInWater()
                            && !this.isPotionActive(Potion.blindness) && this.ridingEntity == null
                            && targetEntity instanceof EntityLivingBase;
                    if (flag && f > 0.0F) {
                        f *= 1.5F;
                    }

                    f += f1;
                    boolean flag1 = false;
                    int j = EnchantmentHelper.getFireAspectModifier((EntityPlayer)(Object)this);
                    if (targetEntity instanceof EntityLivingBase && j > 0
                            && !targetEntity.isBurning()) {
                        flag1 = true;
                        targetEntity.setFire(1);
                    }

                    double d0 = targetEntity.motionX;
                    double d1 = targetEntity.motionY;
                    double d2 = targetEntity.motionZ;
                    boolean flag2 = targetEntity
                            .attackEntityFrom(DamageSource.causePlayerDamage(((EntityPlayer) (Object) this)), f);
                    if (flag2) {
                        if (i > 0) {
                            targetEntity.addVelocity(-MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0F) * (float)i * 0.5F, 0.1D, MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F) * (float)i * 0.5F);

                            HitSlowDownEvent e = new HitSlowDownEvent(0.6, false);
                            Haru.instance.getEventBus().post(e);
                            this.motionX *= e.getSlowDown();
                            this.motionZ *= e.getSlowDown();
                            this.setSprinting(e.isSprint());
                        }

                        if (targetEntity instanceof EntityPlayerMP
                                && targetEntity.velocityChanged) {
                            ((EntityPlayerMP) targetEntity).playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(targetEntity));
                            targetEntity.velocityChanged = false;
                            targetEntity.motionX = d0;
                            targetEntity.motionY = d1;
                            targetEntity.motionZ = d2;
                        }

                        if (flag) {
                            this.onCriticalHit(targetEntity);
                        }

                        if (f1 > 0.0F) {
                            this.onEnchantmentCritical(targetEntity);
                        }

                        if (f >= 18.0F) {
                            this.triggerAchievement(AchievementList.overkill);
                        }

                        this.setLastAttacker(targetEntity);
                        if (targetEntity instanceof EntityLivingBase) {
                            EnchantmentHelper.applyThornEnchantments((EntityLivingBase) (Object)targetEntity, (EntityLivingBase) (Object) this);
                        }

                        EnchantmentHelper.applyArthropodEnchantments((EntityLivingBase) (Object)this, targetEntity);
                        ItemStack itemstack = this.getCurrentEquippedItem();
                        Entity entity = targetEntity;
                        if (targetEntity instanceof EntityDragonPart) {
                            IEntityMultiPart ientitymultipart = ((EntityDragonPart) targetEntity).entityDragonObj;
                            if (ientitymultipart instanceof EntityLivingBase) {
                                entity = (EntityLivingBase) ientitymultipart;
                            }
                        }

                        if (itemstack != null && entity instanceof EntityLivingBase) {
                            itemstack.hitEntity((EntityLivingBase) entity, ((EntityPlayer) (Object) this));
                            if (itemstack.stackSize <= 0) {
                                this.destroyCurrentEquippedItem();
                            }
                        }

                        if (targetEntity instanceof EntityLivingBase) {
                            this.addStat(StatList.damageDealtStat, Math.round(f * 10.0F));
                            if (j > 0) {
                                targetEntity.setFire(j * 4);
                            }
                        }

                        this.addExhaustion(0.3F);
                    } else if (flag1) {
                        targetEntity.extinguish();
                    }
                }
            }

        }
    }
}
