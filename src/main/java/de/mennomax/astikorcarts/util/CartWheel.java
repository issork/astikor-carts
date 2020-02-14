package de.mennomax.astikorcarts.util;

import de.mennomax.astikorcarts.entity.AbstractDrawnEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public final class CartWheel {
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

    public void tick() {
        this.rotation += this.rotationIncrement;
        this.prevPosX = this.posX;
        this.prevPosZ = this.posZ;
        final float yaw = (float) Math.toRadians(this.cart.rotationYaw);
        final float nx = -MathHelper.sin(yaw);
        final float nz = MathHelper.cos(yaw);
        this.posX = this.cart.posX + nx * this.offsetZ - nz * this.offsetX;
        this.posZ = this.cart.posZ + nz * this.offsetZ + nx * this.offsetX;
        final double dx = this.posX - this.prevPosX;
        final double dz = this.posZ - this.prevPosZ;
        final float distanceTravelled = (float) Math.sqrt(dx * dx + dz * dz);
        final double dxNormalized = dx / distanceTravelled;
        final double dzNormalized = dz / distanceTravelled;
        final float travelledForward = MathHelper.signum(dxNormalized * nx + dzNormalized * nz);
        if (distanceTravelled > 0.2) {
            final BlockPos blockpos = new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.cart.posY - 0.2F), MathHelper.floor(this.posZ));
            final BlockState blockstate = this.cart.world.getBlockState(blockpos);
            if (!blockstate.addRunningEffects(this.cart.world, blockpos, this.cart)) {
                if (blockstate.getRenderType() != BlockRenderType.INVISIBLE) {
                    this.cart.world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, blockstate).setPos(blockpos), this.posX, this.cart.posY, this.posZ, dx, distanceTravelled, dz);
                }
            }
        }
        this.rotationIncrement = travelledForward * distanceTravelled * this.circumference * 0.2F;
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
