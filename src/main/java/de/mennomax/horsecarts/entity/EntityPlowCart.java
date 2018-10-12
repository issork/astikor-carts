package de.mennomax.horsecarts.entity;

import de.mennomax.horsecarts.config.ModConfig;
import de.mennomax.horsecarts.init.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityPlowCart extends EntityCart
{
    private boolean plowing = false;
    private static final double BLADEOFFSET = 1.2D;

    public EntityPlowCart(World worldIn)
    {
        super(worldIn);
        this.setSize(1.5F, 1.4F);
        this.stepHeight = 1.2F;
        this.offsetFactor = 2.4D;
    }

    @Override
    public boolean canPull(Entity pullingIn)
    {
        String[] canPullArray = ModConfig.plowcart.canPull;
        for (int i = 0; i < canPullArray.length; i++)
        {
            if (canPullArray[i].equals(pullingIn instanceof EntityPlayer ? "minecraft:player" : EntityList.getKey(pullingIn).toString()))
            {
                return true;
            }
        }
        return false;
    }

    public boolean getPlowing()
    {
        return plowing;
    }

    public void setPlowing(boolean plowingIn)
    {
        this.plowing = plowingIn;
    }

    @Override
    public void onDestroyed(DamageSource source)
    {
        if (!source.isCreativePlayer())
        {
            this.world.spawnEntity(new EntityItem(this.world, this.posX, this.posY + 1.0F, this.posZ, new ItemStack(ModItems.plowcart)));
        }

    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        if (this.prevPosX != this.posX && this.prevPosZ != this.posZ)
        {
            BlockPos pos = new BlockPos(this.getPositionVector().x - this.getLookVec().x * BLADEOFFSET, this.getPositionVector().y - 1.0, this.getPositionVector().z - this.getLookVec().z * BLADEOFFSET);
            IBlockState iblockstate = this.world.getBlockState(pos);
            Material topMaterial = this.world.getBlockState(pos.up()).getMaterial();
            Block block = iblockstate.getBlock();
            if (this.getPlowing() && (topMaterial == Material.AIR || topMaterial == Material.PLANTS || topMaterial == Material.VINE))
            {
                if (block == Blocks.GRASS || block == Blocks.GRASS_PATH)
                {
                    this.setBlock(this.world, pos, Blocks.FARMLAND.getDefaultState().withProperty(BlockFarmland.MOISTURE, 7));
                }
                if (block == Blocks.DIRT)
                {
                    switch ((BlockDirt.DirtType) iblockstate.getValue(BlockDirt.VARIANT))
                    {
                    case DIRT:
                        this.setBlock(this.world, pos, Blocks.FARMLAND.getDefaultState().withProperty(BlockFarmland.MOISTURE, 7));
                        break;
                    case COARSE_DIRT:
                        this.setBlock(this.world, pos, Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT));
                        break;
                    default:
                        break;
                    }
                }
            }
        }
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
    {
        this.plowing = !this.plowing;
        return true;
    }

    protected void setBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!worldIn.isRemote)
        {
            worldIn.setBlockState(pos, state, 11);
        }
    }
}
