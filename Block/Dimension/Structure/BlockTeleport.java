package Reika.ChromatiCraft.Block.Dimension.Structure;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
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
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


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
	public int getLightOpacity(IBlockAccess world, int x, int y, int z) {
		return 0;
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
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
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
			this.getTileEntity(world, x, y, z).facing = ReikaPlayerAPI.getDirectionFromPlayerLook(ep, false);
			this.getTileEntity(world, x, y, z).destination = new BlockVector(-346-x, 4-y, 778-z, ForgeDirection.EAST);
			ReikaJavaLibrary.pConsole(((TileEntityTeleport)world.getTileEntity(x, y, z)).destination);
			return true;
		}
		return false;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e) {
		this.getTileEntity(world, x, y, z).teleport(e);
	}

	private TileEntityTeleport getTileEntity(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		if (meta == 0) {
			return (TileEntityTeleport)world.getTileEntity(x, y, z);
		}
		else {
			switch(meta) {
				case 1:
					return this.getTileEntity(world, x, y+1, z);
				case 2:
					return this.getTileEntity(world, x, y-1, z);
				case 3:
					return this.getTileEntity(world, x+1, y, z); //right, EW plane
				case 4:
					return this.getTileEntity(world, x-1, y, z); //left, EW plane
				case 5:
					return this.getTileEntity(world, x, y, z+1); //right, NS plane
				case 6:
					return this.getTileEntity(world, x, y, z-1); //left, NS plane
			}
		}
		return null;
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
		return tag;
	}

	public static class TileEntityTeleport extends TileEntity {

		/** Is a relative position! */
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
			double nx = xCoord+destination.xCoord+dx;
			double ny = yCoord+destination.yCoord+dy;
			double nz = zCoord+destination.zCoord+dz;
			e.setVelocity(0, 0, 0);
			if (e instanceof EntityPlayer) {
				if (!e.worldObj.isRemote) {
					e.rotationYaw += yaw;
					((EntityPlayer)e).rotationYawHead += yaw;
					((EntityPlayer)e).prevRotationYawHead += yaw;
					e.prevRotationYaw += yaw;
					((EntityPlayer)e).setPositionAndUpdate(nx, ny, nz);
					byte a = (byte)(MathHelper.floor_float(((EntityPlayer)e).rotationYawHead*256.0F/360.0F));
					((EntityPlayerMP)e).playerNetServerHandler.sendPacket(new S19PacketEntityHeadLook(e, a));
				}/*
				else {/
					//e.setPosition(nx, ny, nz);
					e.rotationYaw += yaw;
					((EntityPlayer)e).rotationYawHead += yaw;
					((EntityPlayer)e).prevRotationYawHead += yaw;
					e.prevRotationYaw += yaw;
					this.setRenderPos((EntityPlayer)e);
				}*/
			}
			else {
				if (!e.worldObj.isRemote)
					e.setLocationAndAngles(nx, ny, nz, e.rotationYaw+yaw, e.rotationPitch);
			}
		}

		private float getYaw(Entity e) {
			int rel = ReikaDirectionHelper.getRelativeAngle(facing, destination.direction);
			if (rel > 180)
				rel = rel-360;
			return rel;
		}

		@SideOnly(Side.CLIENT)
		private void setRenderPos(EntityPlayer ep) {
			RenderManager rm = RenderManager.instance;
			if (ep.getUniqueID().equals(Minecraft.getMinecraft().thePlayer.getUniqueID())) {
				//RenderManager.renderPosX = RenderManager.instance.viewerPosX = nx;
				//RenderManager.renderPosY = RenderManager.instance.viewerPosY = ny;
				//RenderManager.renderPosZ = RenderManager.instance.viewerPosZ = nz;
				rm.cacheActiveRenderInfo(rm.worldObj, rm.renderEngine, Minecraft.getMinecraft().fontRenderer, ep, rm.field_147941_i, rm.options, 0);
			}
		}

	}



}
