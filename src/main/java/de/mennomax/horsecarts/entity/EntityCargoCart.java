package de.mennomax.horsecarts.entity;

import javax.annotation.Nullable;

import de.mennomax.horsecarts.AstikoorCarts;
import de.mennomax.horsecarts.config.ModConfig;
import de.mennomax.horsecarts.handler.PacketHandler;
import de.mennomax.horsecarts.init.ModItems;
import de.mennomax.horsecarts.packets.SPacketCargoLoad;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class EntityCargoCart extends EntityCart implements IInventoryChangedListener
{
    public int load;
    public InventoryBasic cargo = new InventoryBasic(this.getName(), true, 54);
    protected IItemHandler itemHandler = new InvWrapper(this.cargo);

    public EntityCargoCart(World worldIn)
    {
        super(worldIn);
        this.setSize(1.5F, 1.4F);
        this.stepHeight = 1.2F;
        this.offsetFactor = 2.4D;
        this.cargo.addInventoryChangeListener(this);
    }

    @Override
    public boolean canPull(Entity pullingIn)
    {
        String[] canPullArray = ModConfig.cargocart.canPull;
        for (int i = 0; i < canPullArray.length; i++)
        {
            if (canPullArray[i].equals(pullingIn instanceof EntityPlayer ? "minecraft:player" : EntityList.getKey(pullingIn).toString()))
            {
                return true;
            }
        }
        return false;
    }

    public int getLoad()
    {
        return this.load;
    }

    public void setLoad(int loadIn)
    {
        this.load = loadIn;
    }

    @Override
    public void onDestroyed(DamageSource source)
    {
        if (!source.isCreativePlayer())
            this.world.spawnEntity(new EntityItem(this.world, this.posX, this.posY + 1.0F, this.posZ, new ItemStack(ModItems.CARGOCART)));
        for (int i = 0; i < this.cargo.getSizeInventory(); i++)
        {
            ItemStack stack = this.cargo.getStackInSlot(i);
            world.spawnEntity(new EntityItem(this.world, this.posX, this.posY + 1.0F, this.posZ, stack));
        }
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
    {
        {
            if (!this.world.isRemote)
            {
                if (player.isSneaking())
                {
                    player.openGui(AstikoorCarts.instance, 0, world, this.getEntityId(), 0, 0);
                }
                else
                {
                    player.startRiding(this);
                }

            }
        }
        return true;
    }

    @Override
    public double getMountedYOffset()
    {
        return 0.62D;
    }

    @Override
    public void updatePassenger(Entity passenger)
    {
        if (this.isPassenger(passenger))
        {
            Vec3d vec3d = (new Vec3d((double) -0.68D, 0.0D, 0.0D)).rotateYaw(-this.rotationYaw * 0.017453292F - ((float) Math.PI / 2F));
            passenger.setPosition(this.posX + vec3d.x, this.posY + this.getMountedYOffset() + passenger.getYOffset(), this.posZ + vec3d.z);
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound)
    {
        NBTTagList nbttaglist = compound.getTagList("Items", 10);

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
            this.cargo.setInventorySlotContents(nbttagcompound.getByte("Slot") & 255, new ItemStack(nbttagcompound));
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        NBTTagList nbttaglist = new NBTTagList();
        for (int i = 0; i < this.cargo.getSizeInventory(); ++i)
        {
            ItemStack itemstack = this.cargo.getStackInSlot(i);
            if (!itemstack.isEmpty())
            {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Slot", (byte) i);
                itemstack.writeToNBT(nbttagcompound);
                nbttaglist.appendTag(nbttagcompound);
            }
        }
        compound.setTag("Items", nbttaglist);
    }

    @Override
    public void onInventoryChanged(IInventory invBasic)
    {
        if (!this.world.isRemote)
        {
            int lastload = this.load;
            int tempload = 0;
            for (int i = 0; i < this.cargo.getSizeInventory(); i++)
            {
                if (!this.cargo.getStackInSlot(i).isEmpty())
                {
                    tempload++;
                }
            }
            if (tempload > 31)
                this.load = 4;
            else if (tempload > 16)
                this.load = 3;
            else if (tempload > 8)
                this.load = 2;
            else if (tempload > 3)
                this.load = 1;
            else
                this.load = 0;
            if (this.load != lastload)
            {
                ((WorldServer) this.world).getEntityTracker().sendToTracking(this, PacketHandler.INSTANCE.getPacketFrom(new SPacketCargoLoad(this.load, this.getEntityId())));
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nullable
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable net.minecraft.util.EnumFacing facing)
    {
        System.out.println(capability);
        if (capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) itemHandler;
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, @Nullable net.minecraft.util.EnumFacing facing)
    {
        return capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }
}
