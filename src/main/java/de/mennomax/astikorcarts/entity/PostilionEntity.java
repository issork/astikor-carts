package de.mennomax.astikorcarts.entity;

import de.mennomax.astikorcarts.AstikorCarts;
import de.mennomax.astikorcarts.init.Entities;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.util.HandSide;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.Collections;

public class PostilionEntity extends LivingEntity {
    public PostilionEntity(final EntityType<? extends PostilionEntity> type, final World world) {
        super(type, world);
    }

    public PostilionEntity(final World world) {
        super(Entities.POSTILION, world);
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.setSilent(true);
        this.setNoGravity(true);
        this.setInvulnerable(true);
    }

    @Override
    public void tick() {
        super.tick();
        final Entity mount = this.getRidingEntity();
        if (mount == null) {
            this.remove();
            return;
        }
        final AbstractDrawnEntity drawn = (this.world.isRemote ? AstikorCarts.CLIENTPULLMAP : AstikorCarts.SERVERPULLMAP).get(mount);
        if (drawn == null) {
            this.remove();
            return;
        }
        final Entity coachman = drawn.getControllingPassenger();
        if (coachman instanceof LivingEntity) {
            this.rotationYaw = coachman.rotationYaw;
            this.prevRotationYaw = this.rotationYaw;
            this.rotationPitch = coachman.rotationPitch * 0.5F;
            this.moveForward = ((LivingEntity) coachman).moveForward;
            this.moveStrafing = 0.0F;
        }
    }

    @Override
    public Iterable<ItemStack> getArmorInventoryList() {
        return Collections.emptyList();
    }

    @Override
    public ItemStack getItemStackFromSlot(final EquipmentSlotType slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemStackToSlot(final EquipmentSlotType slot, final ItemStack stack) {
    }

    @Override
    public HandSide getPrimaryHand() {
        return HandSide.RIGHT;
    }

    @Override
    public void onKillCommand() {
        this.remove();
    }

    @Override
    public boolean isImmuneToExplosions() {
        return true;
    }

    @Override
    public PushReaction getPushReaction() {
        return PushReaction.IGNORE;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean canBeAttackedWithItem() {
        return false;
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    public boolean canBeHitWithPotion() {
        return false;
    }

    @Override
    public boolean attackable() {
        return false;
    }

    @Override
    public void onStruckByLightning(final LightningBoltEntity bolt) {
    }

    @Override
    protected void collideWithEntity(final Entity entity) {
    }

    @Override
    protected void collideWithNearbyEntities() {
    }

    @Override
    protected void doWaterSplashEffect() {
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
