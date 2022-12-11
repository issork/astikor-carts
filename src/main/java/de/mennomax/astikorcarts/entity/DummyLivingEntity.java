package de.mennomax.astikorcarts.entity;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

public abstract class DummyLivingEntity extends LivingEntity {
    protected DummyLivingEntity(final EntityType<? extends LivingEntity> type, final Level world) {
        super(type, world);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.setSilent(true);
        this.setNoGravity(true);
        this.setInvulnerable(true);
        this.setInvisible(true);
    }

    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return Collections.emptyList();

    }

    @Override
    public ItemStack getItemBySlot(final EquipmentSlot slotIn) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(final EquipmentSlot slotIn, final ItemStack stack) {
    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @Override
    public boolean ignoreExplosion() {
        return true;
    }

    @Override
    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean isEffectiveAi() {
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public boolean isIgnoringBlockTriggers() {
        return true;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean attackable() {
        return false;
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    public boolean canAttackType(final EntityType<?> type) {
        return false;
    }

    @Override
    public boolean canAttack(final LivingEntity living) {
        return false;
    }

    @Override
    public boolean isAffectedByPotions() {
        return false;
    }

    @Override
    public void kill() {
        this.discard();
    }

    @Override
    public void push(Entity p_21294_) {
    }

    @Override
    protected void pushEntities() {
    }

    @Override
    protected void doWaterSplashEffect() {
    }

    @Override
    public boolean addEffect(final MobEffectInstance effect, @Nullable Entity entity) {
        return false;
    }

    @Override
    protected void updateEffectVisibility() {
        super.updateEffectVisibility();
    }

    @Override
    protected void updateInvisibilityStatus() {
        this.setInvisible(true);
    }
}
