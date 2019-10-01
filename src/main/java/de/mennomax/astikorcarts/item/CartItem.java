package de.mennomax.astikorcarts.item;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

public class CartItem extends ModItem {

    public CartItem(String name) {
        super(new Properties().maxStackSize(1).group(ItemGroup.TRANSPORTATION), name);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        RayTraceResult result = rayTrace(worldIn, playerIn, FluidMode.ANY);
        if (result.getType() == Type.MISS) {
            return new ActionResult<>(ActionResultType.PASS, itemstack);
        } else {
            Vec3d lookVec = playerIn.getLook(1.0F);
            List<Entity> list = worldIn.getEntitiesInAABBexcluding(playerIn, playerIn.getBoundingBox().expand(lookVec.scale(5.0D)).grow(5.0D), EntityPredicates.NOT_SPECTATING.and(Entity::canBeCollidedWith));
            if (!list.isEmpty()) {
                Vec3d eyePos = playerIn.getEyePosition(1.0F);
                for (Entity entity : list) {
                    AxisAlignedBB axisalignedbb = entity.getBoundingBox().grow(entity.getCollisionBorderSize());
                    if (axisalignedbb.contains(eyePos)) {
                        return new ActionResult<>(ActionResultType.PASS, itemstack);
                    }
                }
            }

            if (result.getType() == Type.BLOCK) {
                Entity cart = ForgeRegistries.ENTITIES.getValue(this.getRegistryName()).create(worldIn);
                cart.setPosition(result.getHitVec().x, result.getHitVec().y, result.getHitVec().z);
                cart.rotationYaw = (playerIn.rotationYaw + 180) % 360;
                if (!worldIn.isCollisionBoxesEmpty(cart, cart.getBoundingBox().grow(0.1F, -0.1F, 0.1F))) {
                    return new ActionResult<>(ActionResultType.FAIL, itemstack);
                } else {
                    if (!worldIn.isRemote()) {
                        worldIn.addEntity(cart);
                    }
                    if (!playerIn.abilities.isCreativeMode) {
                        itemstack.shrink(1);
                    }
                    playerIn.addStat(Stats.ITEM_USED.get(this));
                    return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
                }
            } else {
                return new ActionResult<>(ActionResultType.PASS, itemstack);
            }
        }
    }

}
