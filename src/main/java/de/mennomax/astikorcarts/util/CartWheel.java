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
    private float offsetX;
    private float offsetZ;
    private float circumference;
    private double posX;
    private double posZ;
    private double prevPosX;
    private double prevPosZ;
    private float distanceTravelled;
    private AbstractDrawnEntity cart;

    public CartWheel(AbstractDrawnEntity cartIn, float offsetXIn, float offsetZIn, float circumferenceIn) {
        this.cart = cartIn;
        this.offsetX = offsetXIn;
        this.offsetZ = offsetZIn;
        this.circumference = circumferenceIn;
        this.posX = this.prevPosX = cartIn.posX;
        this.posZ = this.prevPosZ = cartIn.posZ;
    }

    public CartWheel(AbstractDrawnEntity cartIn, float offsetX) {
        this(cartIn, offsetX, 0.0F, (float) ((10 * Math.PI * 2) / 16));
    }

    public void tick(double lookX, double lookZ) {
        this.rotation += this.rotationIncrement;
        this.prevPosX = this.posX;
        this.prevPosZ = this.posZ;
        this.posX = cart.posX + lookX * offsetZ + MathHelper.sin((float) Math.toRadians(cart.rotationYaw - 90)) * this.offsetX;
        this.posZ = cart.posZ + lookZ * offsetZ - MathHelper.cos((float) Math.toRadians(cart.rotationYaw - 90)) * this.offsetX;
        double dx = posX - prevPosX;
        double dz = posZ - prevPosZ;
        this.distanceTravelled = (float) Math.sqrt(dx * dx + dz * dz);
        double dxNormalized = dx/distanceTravelled;
        double dzNormalized = dz/distanceTravelled;
        boolean travelledForward = Math.sqrt((dxNormalized-lookX) * (dxNormalized-lookX) + (dzNormalized-lookZ) * (dzNormalized-lookZ)) < 1;
        if(this.distanceTravelled > 0.2) {
            BlockPos blockpos = new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(cart.posY - 0.2F), MathHelper.floor(this.posZ));
            BlockState blockstate = cart.world.getBlockState(blockpos);
            if (!blockstate.addRunningEffects(cart.world, blockpos, cart)) {
                if (blockstate.getRenderType() != BlockRenderType.INVISIBLE) {
                    cart.world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, blockstate).setPos(blockpos), this.posX, cart.posY, this.posZ, dx, this.distanceTravelled, dz);
                }
            }
        }
        this.rotationIncrement = (travelledForward ? this.distanceTravelled : -this.distanceTravelled) * this.circumference * 0.2F;
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
