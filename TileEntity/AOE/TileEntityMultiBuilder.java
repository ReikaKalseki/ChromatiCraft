/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.AOE;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import com.google.common.collect.Sets;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaFX;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.APIPacketHandler.PacketIDs;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Interfaces.TileEntity.LocationCached;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class TileEntityMultiBuilder extends TileEntityChromaticBase implements LocationCached {

	private static final Collection<WorldLocation> cache = Sets.newConcurrentHashSet();

	private Collection<RenderBeam> renderBeams = new ArrayList();

	private BlockBox bounds;
	private ArrayRegion region = new ArrayRegion();

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.MULTIBUILDER;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		if (bounds == null)
			bounds = BlockBox.block(this).expand(ForgeDirection.UP, 8).expand(ForgeDirection.DOWN, 1);
		WorldLocation loc = new WorldLocation(this);
		if (!cache.contains(loc))
			cache.add(loc);
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	public void expand(ForgeDirection dir) {
		if (worldObj.isRemote)
			return;
		bounds = bounds.expand(Math.abs(dir.offsetX), 0, Math.abs(dir.offsetZ));//bounds.expand(dir, 1);
		this.syncAllData(false);
	}

	public void contract(ForgeDirection dir) {
		if (worldObj.isRemote)
			return;
		bounds = bounds.contract(Math.abs(dir.offsetX), 0, Math.abs(dir.offsetZ));//bounds.contract(dir, 1);
		this.syncAllData(false);
	}

	public void expandArea(ForgeDirection dir) {
		if (worldObj.isRemote)
			return;
		if (dir.offsetX != 0)
			region.radiusX++;
		if (dir.offsetZ != 0)
			region.radiusZ++;
		this.syncAllData(false);
	}

	public void contractArea(ForgeDirection dir) {
		if (worldObj.isRemote)
			return;
		if (dir.offsetX != 0 && region.radiusX > 0)
			region.radiusX--;
		if (dir.offsetZ != 0 && region.radiusZ > 0)
			region.radiusZ--;
		this.syncAllData(false);
	}

	public void cycleShape() {
		if (worldObj.isRemote)
			return;
		region.shape = region.shape.next();
		this.syncAllData(false);
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		if (bounds != null) {
			NBTTagCompound tag = new NBTTagCompound();
			bounds.writeToNBT(tag);
			NBT.setTag("bounds", tag);
		}
		NBTTagCompound tag = new NBTTagCompound();
		region.writeToNBT(tag);
		NBT.setTag("region", tag);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		bounds = NBT.hasKey("bounds") ? BlockBox.readFromNBT(NBT.getCompoundTag("bounds")) : null;
		region.readFromNBT(NBT.getCompoundTag("region"));
	}

	@Override
	public void breakBlock() {
		WorldLocation loc = new WorldLocation(this);
		cache.remove(loc);
	}

	private void checkAndBuild(World world, int x, int y, int z, Block b, int meta, EntityPlayer ep, ItemStack is) {
		if (world.isRemote)
			return;
		if (world.provider.dimensionId == worldObj.provider.dimensionId && bounds.isBlockInsideExclusive(x, y, z)) {
			for (Point p : region.getRegions(xCoord, zCoord, bounds)) {
				if (p.x != 0 || p.y != 0) {
					int dx = x+bounds.getSizeX()*p.x;
					int dy = y;
					int dz = z+bounds.getSizeZ()*p.y;
					if (this.buildBlock((WorldServer)world, dx, dy, dz, b, meta, (EntityPlayerMP)ep, is))
						break;
				}
			}
		}
	}

	private void checkAndBreak(World world, int x, int y, int z, Block b, int meta, EntityPlayer ep) {
		if (world.isRemote)
			return;
		if (world.provider.dimensionId == worldObj.provider.dimensionId && bounds.isBlockInsideExclusive(x, y, z)) {
			for (Point p : region.getRegions(xCoord, zCoord, bounds)) {
				if (p.x != 0 || p.y != 0) {
					int dx = x+bounds.getSizeX()*p.x;
					int dy = y;
					int dz = z+bounds.getSizeZ()*p.y;
					this.mineBlock((WorldServer)world, dx, dy, dz, b, meta, (EntityPlayerMP)ep);
				}
			}
		}
	}

	private boolean buildBlock(WorldServer world, int x, int y, int z, Block b, int meta, EntityPlayerMP ep, ItemStack is) {
		if (ReikaWorldHelper.softBlocks(world, x, y, z) && ReikaPlayerAPI.playerCanBreakAt(world, x, y, z, ep)) {
			if (is != null && is.stackSize > 1) {
				world.setBlock(x, y, z, b, meta, 3);
				b.onBlockPlacedBy(world, x, y, z, ep, is);
				b.onPostBlockPlaced(world, x, y, z, meta);
				ReikaSoundHelper.playBreakSound(world, x, y, z, b);
				ReikaPacketHelper.sendDataPacketWithRadius(DragonAPIInit.packetChannel, PacketIDs.BREAKPARTICLES.ordinal(), world, x, y, z, 128, Block.getIdFromBlock(b), meta);
				if (!ep.capabilities.isCreativeMode)
					is.stackSize--;
				return false;
			}
		}
		return true;
	}

	private void mineBlock(WorldServer world, int x, int y, int z, Block b, int meta, EntityPlayerMP ep) {
		if (new BlockKey(b, meta).matchInWorld(world, x, y, z) && ReikaPlayerAPI.playerCanBreakAt(world, x, y, z, ep)) {
			if (ep.capabilities.isCreativeMode) {
				world.setBlock(x, y, z, Blocks.air);
				ReikaSoundHelper.playBreakSound(world, x, y, z, b);
				ReikaPacketHelper.sendDataPacketWithRadius(DragonAPIInit.packetChannel, PacketIDs.BREAKPARTICLES.ordinal(), world, x, y, z, 128, Block.getIdFromBlock(b), meta);
			}
			else
				ReikaWorldHelper.dropAndDestroyBlockAt(world, x, y, z, ep, true, true);
		}
	}

	public BlockBox getBounds() {
		return bounds;
	}

	@SideOnly(Side.CLIENT)
	public Collection<Point> getRegionsForRender() {
		Collection<Point> li = new ArrayList();
		for (Point p : region.getRegions(xCoord, zCoord, bounds)) {
			li.add(p);
		}
		return li;
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return bounds != null ? ReikaAABBHelper.scaleAABB(bounds.asAABB(), 1+region.radiusX, 1, 1+region.radiusZ) : super.getRenderBoundingBox();
	}

	@SideOnly(Side.CLIENT)
	public void renderBeams(float ptick, HashMap<DecimalPosition, Integer> columns) {
		Iterator<RenderBeam> it = renderBeams.iterator();
		while (it.hasNext()) {
			RenderBeam b = it.next();
			if (b.render(ptick))
				it.remove();
		}
		if (rand.nextInt(10) == 0) {
			ArrayList<DecimalPosition> li = new ArrayList(columns.keySet());
			DecimalPosition pos1 = li.get(rand.nextInt(li.size()));
			DecimalPosition pos2 = li.get(rand.nextInt(li.size()));
			while (pos2.equals(pos1)) {
				pos2 = li.get(rand.nextInt(li.size()));
			}
			renderBeams.add(new RenderBeam(pos1, pos2, columns.get(pos1), columns.get(pos2), rand));
		}
	}

	public static void placeBlock(World world, int x, int y, int z, Block b, int meta, EntityPlayer ep, ItemStack is) {
		Iterator<WorldLocation> it = cache.iterator();
		while (it.hasNext()) {
			WorldLocation loc = it.next();
			if (world.provider.dimensionId == loc.dimensionID) {
				TileEntity te = loc.getTileEntity(world);
				if (te instanceof TileEntityMultiBuilder) {
					((TileEntityMultiBuilder)te).checkAndBuild(world, x, y, z, b, meta, ep, is);
				}
				else {
					it.remove();
					ChromatiCraft.logger.logError("Incorrect tile ("+te+") @ "+loc+" in Multi Builder cache!?");
				}
			}
		}
	}

	public static void breakBlock(World world, int x, int y, int z, Block b, int meta, EntityPlayer ep) {
		Iterator<WorldLocation> it = cache.iterator();
		while (it.hasNext()) {
			WorldLocation loc = it.next();
			if (world.provider.dimensionId == loc.dimensionID) {
				TileEntity te = loc.getTileEntity(world);
				if (te instanceof TileEntityMultiBuilder) {
					if (x != te.xCoord || y != te.yCoord || z != te.zCoord)
						((TileEntityMultiBuilder)te).checkAndBreak(world, x, y, z, b, meta, ep);
				}
				else {
					it.remove();
					ChromatiCraft.logger.logError("Incorrect tile ("+te+") @ "+loc+" in Multi Builder cache!?");
				}
			}
		}
	}

	public static void clearCache() {
		cache.clear();
	}

	private static class ArrayRegion {

		private RegionShape shape = RegionShape.RECTANGLE;
		private int radiusX = 2;
		private int radiusZ = 2;

		private ArrayRegion() {

		}

		public void writeToNBT(NBTTagCompound tag) {
			tag.setInteger("shape", shape.ordinal());
			tag.setInteger("rx", radiusX);
			tag.setInteger("rz", radiusZ);
		}

		public void readFromNBT(NBTTagCompound tag) {
			shape = RegionShape.list[tag.getInteger("shape")];
			radiusX = tag.getInteger("rx");
			radiusZ = tag.getInteger("rz");
		}

		private Collection<Point> getRegions(int x, int z, BlockBox zone) {
			ArrayList<Point> li = new ArrayList();
			for (int i = -radiusX; i <= radiusX; i++) {
				for (int k = -radiusZ; k <= radiusZ; k++) {
					//int dx = x+i*zone.getSizeX();
					//int dz = z+k*zone.getSizeZ();
					if (shape.isPointInShape(i, k, radiusX, radiusZ))
						li.add(new Point(i, k));
				}
			}
			return li;
		}

	}

	private static enum RegionShape {
		RECTANGLE(),
		DIAMOND(),
		ELLIPSE(),
		HEXAGON(),
		OCTAGON(),
		//TRIANGLE1(),
		//TRIANGLE2(), //180 deg rotated
		;

		private static final RegionShape[] list = values();

		private boolean isPointInShape(int i, int k, int radiusX, int radiusZ) {
			switch(this) {
				case RECTANGLE:
					return true;
				case ELLIPSE:
					return ReikaMathLibrary.isPointInsideEllipse(i, 0, k, radiusX, 0, radiusZ);
				case DIAMOND:
					return Math.abs(i)+Math.abs(k) < Math.max(radiusX, radiusZ);
				case HEXAGON:
					return ReikaMathLibrary.isPointInsidePolygon(i, k, 6, radiusX, radiusZ);
				case OCTAGON:
					return ReikaMathLibrary.isPointInsidePolygon(i, k, 8, radiusX, radiusZ);
					//case TRIANGLE1:
					//	break;
					//case TRIANGLE2:
					//	break;
			}
			return false;
		}

		public RegionShape next() {
			return list[(this.ordinal()+1)%list.length];
		}
	}

	private static class RenderBeam {

		private final DecimalPosition pos1;
		private final DecimalPosition pos2;
		private final int color1;
		private final int color2;

		private int age;
		private final int maxAge;

		private RenderBeam(DecimalPosition p1, DecimalPosition p2, int c1, int c2, Random rand) {
			pos1 = p1;
			pos2 = p2;

			color1 = c1;
			color2 = c2;

			maxAge = 10+rand.nextInt(41);
		}

		@SideOnly(Side.CLIENT)
		private boolean render(float ptick) {
			age++;

			float f = (float)Math.sin(Math.toRadians(180D*age/maxAge));
			int c1 = ReikaColorAPI.getColorWithBrightnessMultiplier(color1, f);
			int c2 = ReikaColorAPI.getColorWithBrightnessMultiplier(color2, f);

			ChromaFX.renderBeam(pos1.xCoord, pos1.yCoord, pos1.zCoord, pos2.xCoord, pos2.yCoord, pos2.zCoord, ptick, 255, 0.125, c1, c2);

			return age >= maxAge;
		}

	}

}
