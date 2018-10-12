package de.mennomax.horsecarts.item;

import de.mennomax.horsecarts.AstikoorCarts;
import de.mennomax.horsecarts.entity.EntityCart;
import de.mennomax.horsecarts.init.ModCreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class CartItem extends Item
{
    public CartItem(String name)
    {
        this.setRegistryName(AstikoorCarts.MODID, name);
        this.setUnlocalizedName(this.getRegistryName().toString());
        setCreativeTab(ModCreativeTabs.astikoor);
        setMaxStackSize(1);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        if (handIn == EnumHand.MAIN_HAND)
        {
            Vec3d vec3d = new Vec3d(playerIn.posX, playerIn.posY + playerIn.getEyeHeight(), playerIn.posZ);
            Vec3d vec3d1 = new Vec3d(playerIn.getLookVec().x * 5.0 + vec3d.x, playerIn.getLookVec().y * 5.0 + vec3d.y, playerIn.getLookVec().z * 5.0 + vec3d.z);

            RayTraceResult result = worldIn.rayTraceBlocks(vec3d, vec3d1, false);
            if (result != null)
            {
                if (result.typeOfHit == Type.BLOCK)
                {
                    if (!worldIn.isRemote)
                    {
                        EntityCart cart = newCart(worldIn);
                        cart.setPosition(result.hitVec.x, result.hitVec.y, result.hitVec.z);
                        cart.rotationYaw = (playerIn.rotationYaw + 180) % 360;
                        worldIn.spawnEntity(cart);

                        if (!playerIn.capabilities.isCreativeMode)
                        {
                            itemstack.shrink(1);
                        }
                    }
                    return new ActionResult<ItemStack>(EnumActionResult.PASS, itemstack);
                }
            }
        }
        return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
    }

    public abstract EntityCart newCart(World worldIn);
}
