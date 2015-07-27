package Reika.ChromatiCraft.Block.Dimension.Structure;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockVector;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;


@Strippable(value="mcp.mobius.waila.api.IWailaDataProvider")
public class BlockTeleport extends BlockContainer implements IWailaDataProvider {

	private static boolean debug = ReikaObfuscationHelper.isDeObfEnvironment() && DragonAPICore.isReikasComputer();

	public BlockTeleport(Material mat) {
		super(mat);

		this.setResistance(60000);
		this.setBlockUnbreakable();
		this.setCreativeTab(ChromatiCraft.tabChromaGen);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityTeleport();
	}

	@Override
	public int getRenderType() {
		return debug ? 0 : -1;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public boolean canCollideCheck(int meta, boolean boat) {
		return debug;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		if (debug) {
			((TileEntityTeleport)world.getTileEntity(x, y, z)).facing = ReikaPlayerAPI.getDirectionFromPlayerLook(ep, false);
			((TileEntityTeleport)world.getTileEntity(x, y, z)).destination = new BlockVector(-346, 4, 778, ForgeDirection.EAST);
			ReikaJavaLibrary.pConsole(((TileEntityTeleport)world.getTileEntity(x, y, z)).destination);
			return true;
		}
		return false;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e) {
		((TileEntityTeleport)world.getTileEntity(x, y, z)).teleport(e);
	}

	@Override
	@ModDependent(ModList.WAILA)
	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return new ItemStack(Blocks.air);
	}

	@Override
	@ModDependent(ModList.WAILA)
	public List<String> getWailaHead(ItemStack itemStack, List<String> tip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return tip;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public List<String> getWailaBody(ItemStack itemStack, List<String> tip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return tip;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public List<String> getWailaTail(ItemStack itemStack, List<String> tip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return tip;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
		return null;
	}

	public static class TileEntityTeleport extends TileEntity {

		public BlockVector destination;
		public ForgeDirection facing;

		@Override
		public boolean canUpdate() {
			return false;
		}

		private void teleport(Entity e) {
			if (destination == null) {
				if (debug)
					ChromatiCraft.logger.logError("Could not teleport "+e+"; null destination!");
				return;
			}
			double dx = e.posX-xCoord;
			double dy = e.posY-yCoord;
			double dz = e.posZ-zCoord;
			float yaw = this.getYaw(e);
			ReikaJavaLibrary.pConsole(yaw);
			if (yaw == 90) {
				double sx = dx;
				dx = dz;
				dz = sx;
			}
			else if (yaw == 180) {
				dx = -dx;
				dz = -dz;
			}
			else if (yaw == -90) {
				double sx = dx;
				dx = dz;
				dz = sx;
			}
			if (e instanceof EntityPlayer) {
				if (!e.worldObj.isRemote)
					((EntityPlayer)e).setPositionAndUpdate(destination.xCoord+dx, destination.yCoord+dy, destination.zCoord+dz);
				e.rotationYaw += yaw;
				e.prevRotationYaw += yaw;
			}
			else {
				if (!e.worldObj.isRemote)
					e.setLocationAndAngles(destination.xCoord+dx, destination.yCoord+dy, destination.zCoord+dz, e.rotationYaw+yaw, e.rotationPitch);
			}
		}

		private float getYaw(Entity e) {
			int rel = ReikaDirectionHelper.getRelativeAngle(facing, destination.direction);
			if (rel > 180)
				rel = rel-360;
			return rel;
		}

	}



}
