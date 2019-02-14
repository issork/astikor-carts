package de.mennomax.astikoorcarts.entity;

import de.mennomax.astikoorcarts.AstikoorCarts;
import de.mennomax.astikoorcarts.config.ModConfig;
import de.mennomax.astikoorcarts.init.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityPlowCart extends AbstractDrawnInventory implements IInventoryChangedListener
{
    private static final DataParameter<Boolean> PLOWING = EntityDataManager.<Boolean>createKey(EntityPlowCart.class, DataSerializers.BOOLEAN);
    private static final double BLADEOFFSET = 1.7D;
    @SuppressWarnings("rawtypes")
    private static final DataParameter[] TOOLS = {
            EntityDataManager.<ItemStack>createKey(EntityPlowCart.class, DataSerializers.ITEM_STACK),
            EntityDataManager.<ItemStack>createKey(EntityPlowCart.class, DataSerializers.ITEM_STACK),
            EntityDataManager.<ItemStack>createKey(EntityPlowCart.class, DataSerializers.ITEM_STACK)
    };
    
    public EntityPlowCart(World worldIn)
    {
        super(worldIn);
        this.setSize(1.5F, 1.4F);
        this.offsetFactor = 2.4D;
        this.inventory = new InventoryBasic(this.getName(), true, 3);
        this.inventory.addInventoryChangeListener(this);
    }

    @Override
    public boolean canPull(Entity pullingIn)
    {
        String[] canPullArray = ModConfig.plowCart.canPull;
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
        return this.dataManager.get(PLOWING);
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        EntityPlayer player = this.pulling != null && this.pulling.getControllingPassenger() instanceof EntityPlayer ? (EntityPlayer) this.pulling.getControllingPassenger() : (this.pulling instanceof EntityPlayer ? (EntityPlayer) this.pulling : null);
        if (!this.world.isRemote && this.dataManager.get(PLOWING) && player != null)
        {
            if (this.prevPosX != this.posX || this.prevPosZ != this.posZ)
            {
                for (int i = 0; i < this.inventory.getSizeInventory(); i++)
                {
                    if(inventory.getStackInSlot(i) != ItemStack.EMPTY)
                    {
                        float offset = -38.0F+i*38.0F;
                        double blockPosX = this.posX + MathHelper.sin((this.rotationYaw-offset) * 0.017453292F) * BLADEOFFSET;
                        double blockPosZ = this.posZ - MathHelper.cos((this.rotationYaw-offset) * 0.017453292F) * BLADEOFFSET;
                        BlockPos blockPos = new BlockPos(blockPosX, this.posY - 0.5D, blockPosZ);
                        BlockPos upPos = blockPos.up();
                        Material upMaterial = this.world.getBlockState(upPos).getMaterial();
                        if (upMaterial == Material.AIR)
                        {
                            handleTool(blockPos, i, player);
                        }
                        else if (upMaterial == Material.PLANTS || upMaterial == Material.VINE)
                        {
                            this.world.destroyBlock(upPos, false);
                            handleTool(blockPos, i, player);
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public Item getCartItem()
    {
        return ModItems.PLOWCART;
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
    {
        if (!this.world.isRemote)
        {
            if (player.isSneaking())
            {
                player.openGui(AstikoorCarts.instance, 1, this.world, this.getEntityId(), 0, 0);
            }
            else
            {
                this.dataManager.set(PLOWING, !this.dataManager.get(PLOWING));
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        dataManager.set(PLOWING, compound.getBoolean("Plowing"));
        for(int i = 0; i < TOOLS.length; i++)
        {
            this.dataManager.set(TOOLS[i], this.inventory.getStackInSlot(i));
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        compound.setBoolean("Plowing", dataManager.get(PLOWING));
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void entityInit()
    {
        super.entityInit();
        this.dataManager.register(PLOWING, false);
        for(int i = 0; i < TOOLS.length; i++)
        {
            this.dataManager.register(TOOLS[i], ItemStack.EMPTY);
        }
    }
    
    private void handleTool(BlockPos pos, int slot, EntityPlayer player)
    {
        IBlockState state = this.world.getBlockState(pos);
        Block block = state.getBlock();
        ItemStack itemstack = this.inventory.getStackInSlot(slot);
        if (itemstack.getItem() instanceof ItemHoe)
        {
            if (block == Blocks.GRASS || block == Blocks.GRASS_PATH)
            {
                this.world.setBlockState(pos, Blocks.FARMLAND.getDefaultState(), 11);
                itemstack.damageItem(1, player);
            }
            
            else if (block == Blocks.DIRT)
            {
                switch (state.getValue(BlockDirt.VARIANT))
                {
                case DIRT:
                    this.world.setBlockState(pos, Blocks.FARMLAND.getDefaultState(), 11);
                    itemstack.damageItem(1, player);
                    break;
                case COARSE_DIRT:
                    this.world.setBlockState(pos, Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT), 11);
                    itemstack.damageItem(1, player);
                    break;
                default:
                    break;
                }
            }
        }
        else if (itemstack.getItem() instanceof ItemSpade)
        {
            if (block == Blocks.GRASS)
            {
                this.world.setBlockState(pos, Blocks.GRASS_PATH.getDefaultState());
                itemstack.damageItem(1, player);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onInventoryChanged(IInventory invBasic)
    {
        for(int i = 0; i < TOOLS.length; i++)
        {
            if (this.dataManager.get(TOOLS[i]) != invBasic.getStackInSlot(i))
            {
                this.dataManager.set(TOOLS[i], this.inventory.getStackInSlot(i));
            }
        }
    }

    @SuppressWarnings("unchecked")
    public ItemStack getTool(int i)
    {
        return (ItemStack) this.dataManager.get(TOOLS[i]);
    }
}
