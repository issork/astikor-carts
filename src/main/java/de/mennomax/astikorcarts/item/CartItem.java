package de.mennomax.astikorcarts.item;

import de.mennomax.astikorcarts.AstikorCarts;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

public final class CartItem extends Item {
    public CartItem(final Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(final World world, final PlayerEntity player, final Hand hand) {
        final ItemStack stack = player.getHeldItem(hand);
        final RayTraceResult result = rayTrace(world, player, FluidMode.ANY);
        if (result.getType() == Type.MISS) {
            return new ActionResult<>(ActionResultType.PASS, stack);
        } else {
            final Vec3d lookVec = player.getLook(1.0F);
            final List<Entity> list = world.getEntitiesInAABBexcluding(player, player.getBoundingBox().expand(lookVec.scale(5.0D)).grow(5.0D), EntityPredicates.NOT_SPECTATING.and(Entity::canBeCollidedWith));
            if (!list.isEmpty()) {
                final Vec3d eyePos = player.getEyePosition(1.0F);
                for (final Entity entity : list) {
                    final AxisAlignedBB axisalignedbb = entity.getBoundingBox().grow(entity.getCollisionBorderSize());
                    if (axisalignedbb.contains(eyePos)) {
                        return new ActionResult<>(ActionResultType.PASS, stack);
                    }
                }
            }

            if (result.getType() == Type.BLOCK) {
                final Entity cart = ForgeRegistries.ENTITIES.getValue(this.getRegistryName()).create(world);
                cart.setPosition(result.getHitVec().x, result.getHitVec().y, result.getHitVec().z);
                cart.rotationYaw = (player.rotationYaw + 180) % 360;
                if (!world.isCollisionBoxesEmpty(cart, cart.getBoundingBox().grow(0.1F, -0.1F, 0.1F))) {
                    return new ActionResult<>(ActionResultType.FAIL, stack);
                } else {
                    if (!world.isRemote()) {
                        world.addEntity(cart);
                        world.playSound(null, cart.posX, cart.posY, cart.posZ, AstikorCarts.SoundEvents.CART_PLACED.get(), SoundCategory.BLOCKS, 0.75F, 0.8F);
                    }
                    if (!player.abilities.isCreativeMode) {
                        stack.shrink(1);
                    }
                    player.addStat(Stats.ITEM_USED.get(this));
                    return new ActionResult<>(ActionResultType.SUCCESS, stack);
                }
            } else {
                return new ActionResult<>(ActionResultType.PASS, stack);
            }
        }
    }
}
