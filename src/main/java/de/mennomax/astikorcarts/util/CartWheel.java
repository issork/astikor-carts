package de.mennomax.astikorcarts.util;

import de.mennomax.astikorcarts.entity.AbstractDrawnEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class CartWheel {

    private float rotation;
    private float rotationIncrement;
    private final float offsetX;
    private final float offsetZ;
    private final float circumference;
    private double posX;
    private double posZ;
    private double prevPosX;
    private double prevPosZ;
    private final AbstractDrawnEntity cart;

    public CartWheel(final AbstractDrawnEntity cartIn, final float offsetXIn, final float offsetZIn, final float circumferenceIn) {
        this.cart = cartIn;
        this.offsetX = offsetXIn;
        this.offsetZ = offsetZIn;
        this.circumference = circumferenceIn;
        this.posX = this.prevPosX = cartIn.posX;
        this.posZ = this.prevPosZ = cartIn.posZ;
    }

    public CartWheel(final AbstractDrawnEntity cartIn, final float offsetX) {
        this(cartIn, offsetX, 0.0F, (float) (10 * Math.PI * 2 / 16));
    }

    public void tick(final double lookX, final double lookZ) {
        this.rotation += this.rotationIncrement;
        this.prevPosX = this.posX;
        this.prevPosZ = this.posZ;
        this.posX = this.cart.posX + lookX * this.offsetZ + MathHelper.sin((float) Math.toRadians(this.cart.rotationYaw - 90)) * this.offsetX;
        this.posZ = this.cart.posZ + lookZ * this.offsetZ - MathHelper.cos((float) Math.toRadians(this.cart.rotationYaw - 90)) * this.offsetX;
        final double dx = this.posX - this.prevPosX;
        final double dz = this.posZ - this.prevPosZ;
        final float distanceTravelled = (float) Math.sqrt(dx * dx + dz * dz);
        final double dxNormalized = dx / distanceTravelled;
        final double dzNormalized = dz / distanceTravelled;
        final boolean travelledForward = Math.sqrt((dxNormalized - lookX) * (dxNormalized - lookX) + (dzNormalized - lookZ) * (dzNormalized - lookZ)) < 1;
        if (distanceTravelled > 0.2) {
            final BlockPos blockpos = new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.cart.posY - 0.2F), MathHelper.floor(this.posZ));
            final BlockState blockstate = this.cart.world.getBlockState(blockpos);
            if (!blockstate.addRunningEffects(this.cart.world, blockpos, this.cart)) {
                if (blockstate.getRenderType() != BlockRenderType.INVISIBLE) {
                    this.cart.world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, blockstate).setPos(blockpos), this.posX, this.cart.posY, this.posZ, dx, distanceTravelled, dz);
                }
            }
        }
        this.rotationIncrement = (travelledForward ? distanceTravelled : -distanceTravelled) * this.circumference * 0.2F;
    }

    public void clearIncrement() {
        this.rotationIncrement = 0.0F;
    }

    public float getRotation() {
        return this.rotation;
    }

    public float getRotationIncrement() {
        return this.rotationIncrement;
    }

}
