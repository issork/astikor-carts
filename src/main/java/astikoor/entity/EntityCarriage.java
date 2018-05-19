package astikoor.entity;

import javax.vecmath.Vector2d;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

//TODO Properly implement carts rotation.
public class EntityCarriage extends EntityRiddenCart
{
    public float axisYawRadian;
    
    public EntityCarriage(World worldIn)
    {
        super(worldIn);
        this.setSize(1.5F, 1.4F);
        this.stepHeight = 1.0F;
        this.offsetFactor = 3.0D;
        this.axisYawRadian = 0.0F;
    }
    
    @Override
    public void handleRotation(Vec3d targetVecIn)
    {
        super.handleRotation(targetVecIn);
        Vec3d axisVec = this.getAxisVec();
        this.axisYawRadian = (float) (-Math.atan2(axisVec.x - this.pulling.posX, axisVec.z - this.pulling.posZ) + Math.PI);
    }
    
    public Vec3d getAxisVec()
    {
        return this.getPositionVector().add(new Vec3d(-1.0, 0.0, 0.0).rotateYaw((float) Math.toRadians(this.rotationYaw)));
    }
    
    @Override
    public Vec3d getTargetVec()
    {
        return this.pulling.getPositionVector().add(new Vec3d(1.0, 0.0, 0.0).rotateYaw(-this.axisYawRadian));
    }
}
