package de.mennomax.astikorcarts.entity;

import com.google.common.collect.ImmutableList;

import de.mennomax.astikorcarts.init.Items;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class PlowCartEntity extends AbstractDrawnInventoryEntity implements IInventoryChangedListener
{
    public PlowCartEntity(EntityType<? extends Entity> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
        this.spacing = 2.0D;
        this.initInventory(3);
        this.inventory.addListener(this);
    }

    private static final DataParameter<Boolean> PLOWING = EntityDataManager.<Boolean>createKey(PlowCartEntity.class, DataSerializers.BOOLEAN);
    private static final double BLADEOFFSET = 1.7D;
    private static final ImmutableList<DataParameter<ItemStack>> TOOLS = ImmutableList.of(
        EntityDataManager.createKey(PlowCartEntity.class, DataSerializers.ITEMSTACK),
        EntityDataManager.createKey(PlowCartEntity.class, DataSerializers.ITEMSTACK),
        EntityDataManager.createKey(PlowCartEntity.class, DataSerializers.ITEMSTACK)
    );

    public boolean getPlowing()
    {
        return this.dataManager.get(PLOWING);
    }

    @Override
    public void pulledTick()
    {
        super.pulledTick();
        PlayerEntity player = null;
        if(this.getPulling().getControllingPassenger() instanceof PlayerEntity) {
            player = (PlayerEntity) this.getPulling().getControllingPassenger();
        } else if(this.getPulling() instanceof PlayerEntity) {
            player = (PlayerEntity) this.getPulling();
        }
        if (!this.world.isRemote && this.dataManager.get(PLOWING) && player != null)
        {
            if (this.prevPosX != this.posX || this.prevPosZ != this.posZ)
            {
                for (int i = 0; i < this.inventory.getSizeInventory(); i++)
                {
                    if(inventory.getStackInSlot(i) != ItemStack.EMPTY)
                    {
                        float offset = 38.0F+i*-38.0F;
                        double blockPosX = this.posX + MathHelper.sin((this.rotationYaw-offset) * 0.017453292F) * BLADEOFFSET;
                        double blockPosZ = this.posZ - MathHelper.cos((this.rotationYaw-offset) * 0.017453292F) * BLADEOFFSET;
                        BlockPos blockPos = new BlockPos(blockPosX, this.posY - 0.5D, blockPosZ);
                        BlockPos upPos = blockPos.up();
                        Material upMaterial = this.world.getBlockState(upPos).getMaterial();
                        if (upMaterial == Material.AIR)
                        {
//                            handleTool(blockPos, i, player);
                        }
//                        else if (upMaterial == Material.PLANTS || upMaterial == Material.VINE)
//                        {
//                            this.world.destroyBlock(upPos, false);
//                            handleTool(blockPos, i, player);
//                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean processInitialInteract(PlayerEntity player, Hand hand)
    {
        if (!this.world.isRemote)
        {
//            if (player.isSneaking())
//            {
//                player.openGui(AstikorCarts.instance, 1, this.world, this.getEntityId(), 0, 0);
//            }
//            else
//            {
                this.dataManager.set(PLOWING, !this.dataManager.get(PLOWING));
//            }
        }
        return true;
    }

//    @SuppressWarnings("unchecked")
//    @Override
//    protected void readEntityFromNBT(NBTTagCompound compound)
//    {
//        super.readEntityFromNBT(compound);
//        dataManager.set(PLOWING, compound.getBoolean("Plowing"));
//        for(int i = 0; i < TOOLS.length; i++)
//        {
//            this.dataManager.set(TOOLS[i], this.inventory.getStackInSlot(i));
//        }
//    }
//
//    @Override
//    protected void writeEntityToNBT(NBTTagCompound compound)
//    {
//        super.writeEntityToNBT(compound);
//        compound.setBoolean("Plowing", dataManager.get(PLOWING));
//    }
//
//    @SuppressWarnings("unchecked")
//    @Override
//    protected void entityInit()
//    {
//        super.entityInit();
//        this.dataManager.register(PLOWING, false);
//        for(int i = 0; i < TOOLS.length; i++)
//        {
//            this.dataManager.register(TOOLS[i], ItemStack.EMPTY);
//        }
//    }
    
//    private void handleTool(BlockPos pos, int slot, EntityPlayer player)
//    {
//        IBlockState state = this.world.getBlockState(pos);
//        Block block = state.getBlock();
//        ItemStack itemstack = this.inventory.getStackInSlot(slot);
//        if (itemstack.getItem() instanceof ItemHoe)
//        {
//            if (block == Blocks.GRASS || block == Blocks.GRASS_PATH)
//            {
//                this.world.setBlockState(pos, Blocks.FARMLAND.getDefaultState(), 11);
//                damageAndUpdateOnBreak(slot, itemstack, player);
//            }
//            
//            else if (block == Blocks.DIRT)
//            {
//                switch (state.getValue(BlockDirt.VARIANT))
//                {
//                case DIRT:
//                    this.world.setBlockState(pos, Blocks.FARMLAND.getDefaultState(), 11);
//                    damageAndUpdateOnBreak(slot, itemstack, player);
//                    break;
//                case COARSE_DIRT:
//                    this.world.setBlockState(pos, Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT), 11);
//                    damageAndUpdateOnBreak(slot, itemstack, player);
//                    break;
//                default:
//                    break;
//                }
//            }
//        }
//        else if (itemstack.getItem() instanceof ItemSpade)
//        {
//            if (block == Blocks.GRASS)
//            {
//                this.world.setBlockState(pos, Blocks.GRASS_PATH.getDefaultState());
//                damageAndUpdateOnBreak(slot, itemstack, player);
//            }
//        }
//    }
    
//    @SuppressWarnings("unchecked")
//    private void damageAndUpdateOnBreak(int slot, ItemStack itemstack, PlayerEntity player)
//    {
//        itemstack.damageItem(1, player);
//        if (itemstack.isEmpty())
//        {
//            this.dataManager.set(TOOLS[slot], ItemStack.EMPTY);
//        }
//    }
    public ItemStack getTool(int i)
    {
        return this.dataManager.get(TOOLS.get(i));
    }
    
    @Override
    public Item getCartItem()
    {
        return Items.PLOWCART;
    }
    
    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(PLOWING, false);
        for(DataParameter<ItemStack> param : TOOLS) {
            this.dataManager.register(param, new ItemStack(net.minecraft.item.Items.COBBLESTONE));
        }
    }
    
    @Override
    protected void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
    }
    
    @Override
    protected void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
    }

    @Override
    public void onInventoryChanged(IInventory invBasic)
    {
        for(int i = 0; i < TOOLS.size(); i++)
        {
            this.dataManager.set(TOOLS.get(i), this.inventory.getStackInSlot(i));
        }
    }
    
}
