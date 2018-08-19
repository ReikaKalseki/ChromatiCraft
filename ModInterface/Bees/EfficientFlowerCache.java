/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2018
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface.Bees;

import java.util.Collection;
import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.HashSetFactory;
import Reika.DragonAPI.Interfaces.BlockCheck;
import Reika.DragonAPI.ModInteract.Bees.ReikaBeeHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.FlowerManager;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeModifier;
import forestry.apiculture.HasFlowersCache;


public class EfficientFlowerCache extends HasFlowersCache {

	private long lastScanTick;
	private int searchCooldown;
	private Coordinate flowerCoord;

	private int id;

	private String cachedFlowerType;
	private BlockBox cachedTerritory;

	private final BlockCheck flowerMatch;
	private FlowerSearch search;

	private static int lastAssignedID;

	private static final HashSet<String> blacklistedAcceptanceCaching = new HashSet();
	private static final MultiMap<String, BlockKey> cachedAcceptedBlocks = new MultiMap(new HashSetFactory()).setNullEmpty();
	private static final MutableBlockKey blockKey = new MutableBlockKey();

	public EfficientFlowerCache() {
		id = lastAssignedID%128;
		lastAssignedID++;
		flowerMatch = new FlowerMatch();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		NBTTagCompound tag = nbt.getCompoundTag("hasFlowerCache");
		//ReikaJavaLibrary.pConsole("Reading from tag: "+tag);

		flowerCoord = Coordinate.readFromNBT("loc", tag);
		searchCooldown = tag.getInteger("age");
		lastScanTick = tag.getLong("lastTick");
		id = tag.getInteger("id");

		cachedFlowerType = tag.getString("cachedFlower");
		if (cachedFlowerType.isEmpty())
			cachedFlowerType = null;
		cachedTerritory = tag.hasKey("territory") ? BlockBox.readFromNBT(tag.getCompoundTag("territory")) : null;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		NBTTagCompound tag = new NBTTagCompound();
		if (flowerCoord != null)
			flowerCoord.writeToNBT("loc", tag);
		tag.setInteger("age", searchCooldown);
		tag.setLong("lastTick", lastScanTick);
		tag.setInteger("id", id);

		if (cachedFlowerType != null)
			tag.setString("cachedFlower", cachedFlowerType);
		if (cachedTerritory != null) {
			NBTTagCompound dat = new NBTTagCompound();
			cachedTerritory.writeToNBT(dat);
			tag.setTag("territory", dat);
		}
		nbt.setTag("hasFlowerCache", tag);

		//ReikaJavaLibrary.pConsole("Writing to tag: "+tag);
	}

	@Override
	public boolean hasFlowers(IBee bee, IBeeHousing ibh) {
		World world = ibh.getWorld();
		long time = world.getTotalWorldTime();
		if (time <= lastScanTick)
			return flowerCoord != null;

		IBeeGenome ibg = bee.getGenome();

		if (cachedFlowerType == null || cachedTerritory == null || time%256 == id) {
			cachedFlowerType = ibg.getFlowerProvider().getFlowerType();
			if (cachedTerritory == null || time%512 == id) {
				cachedTerritory = this.calcTerritory(ibg, ibh);
			}
		}

		if (this.isStillValid(world, time))
			return true;

		if (searchCooldown >= 550)
			this.findFlower(world, false);
		else
			searchCooldown++;

		lastScanTick = time;
		return flowerCoord != null;
	}

	public void forceUpdate(TileEntityLumenAlveary te) {
		if (te.worldObj.isRemote)
			return;
		if (flowerCoord != null)
			return;
		if (!te.isAlvearyComplete() || !te.hasQueen())
			return;
		IBeeHousing ibh = te.getMultiblockLogic().getController();
		IBee bee = ReikaBeeHelper.getBee(ibh.getBeeInventory().getQueen());
		if (bee == null)
			return;
		IBeeGenome ibg = bee.getGenome();
		cachedFlowerType = ibg.getFlowerProvider().getFlowerType();
		cachedTerritory = this.calcTerritory(ibg, ibh);
		this.findFlower(te.worldObj, true);
	}

	private BlockBox calcTerritory(IBeeGenome ibg, IBeeHousing ibh) {
		int[] base = ibg.getTerritory();
		IBeeModifier ibm = BeeManager.beeRoot.createBeeHousingModifier(ibh);
		float f = ibm.getTerritoryModifier(ibg, 1F);
		float fx = 1.5F*f*base[0];
		float fy = 1.5F*f*base[1];
		float fz = 1.5F*f*base[2];
		ChunkCoordinates cc = ibh.getCoordinates();
		int nx = Math.round(cc.posX-fx);
		int px = Math.round(cc.posX+fx);
		int ny = Math.round(cc.posY-fy);
		int py = Math.round(cc.posY+fy);
		int nz = Math.round(cc.posZ-fz);
		int pz = Math.round(cc.posZ+fz);
		return new BlockBox(nx, ny, nz, px, py, pz);
	}

	private boolean isStillValid(World world, long time) {
		return flowerCoord != null && (time%128 == id || (cachedTerritory.isBlockInside(flowerCoord) && checkFlowerAcceptance(cachedFlowerType, world, flowerCoord.xCoord, flowerCoord.yCoord, flowerCoord.zCoord)));
	}

	private void findFlower(World world, boolean runFullScan) {

		if (runFullScan) {
			//long t = System.nanoTime();
			flowerCoord = cachedTerritory.findBlock(world, flowerMatch);

			//double t2 = (System.nanoTime()-t)/1000D;
			//ReikaJavaLibrary.pConsole("Took "+t2+" us to check "+cachedTerritory+" for flower "+cachedFlowerType+"; coord = "+flowerCoord);
		}
		else {
			if (search == null) {
				search = new FlowerSearch();
			}
			if (search.tick(world)) {
				searchCooldown = 0;
				flowerCoord = search.getCoordinate();
				search = null;
			}
			else {

			}
		}
	}

	private static boolean checkFlowerAcceptance(String type, World world, int x, int y, int z) {
		if (!blacklistedAcceptanceCaching.contains(type)) {
			blockKey.read(world, x, y, z);
			Collection<BlockKey> c = cachedAcceptedBlocks.get(type);
			if (c != null && c.contains(blockKey))
				return true;
		}
		if (FlowerManager.flowerRegistry.isAcceptedFlower(type, world, x, y, z)) {
			if (!blacklistedAcceptanceCaching.contains(type)) {
				blockKey.read(world, x, y, z);
				if (blockKey.block.hasTileEntity(blockKey.metadata)) { //since cannot check TE data, skip any bees that have TE-type flowers
					blacklistedAcceptanceCaching.add(type);
				}
				else {
					cachedAcceptedBlocks.addValue(type, blockKey.asBlockKey());
				}
			}
			return true;
		}
		return false;
	}

	private class FlowerMatch implements BlockCheck {

		@Override
		public boolean matchInWorld(World world, int x, int y, int z) {
			return checkFlowerAcceptance(cachedFlowerType, world, x, y, z);
		}

		@Override
		public boolean match(Block b, int meta) {return false;}

		@Override
		public boolean match(BlockCheck bc) {return false;}

		@Override
		public void place(World world, int x, int y, int z, int flags) {}

		@Override
		public ItemStack asItemStack() {return null;}

		@Override
		@SideOnly(Side.CLIENT)
		public ItemStack getDisplay() {return null;}

		@Override
		public BlockKey asBlockKey() {return null;}

	}

	private class FlowerSearch {

		private static final int TICKSTEP = 512;

		private int readX;
		private int readY;
		private int readZ;

		private Coordinate found = null;

		private FlowerSearch() {
			readX = cachedTerritory.minX;
			readY = cachedTerritory.minY;
			readZ = cachedTerritory.minZ;
		}

		private boolean tick(World world) {
			for (int i = 0; i < TICKSTEP; i++) {
				if (checkFlowerAcceptance(cachedFlowerType, world, readX, readY, readZ)) {
					found = new Coordinate(readX, readY, readZ);
					return true;
				}
				else {
					this.updateReadPosition();
					if (readY > cachedTerritory.maxY) {
						return true;
					}
				}
			}
			return false;
		}

		private void updateReadPosition() {
			boolean flag1 = false;
			boolean flag2 = false;
			readX++;
			if (readX > cachedTerritory.maxX) {
				readX = cachedTerritory.minX;
				flag1 = true;
			}
			if (flag1) {
				readZ++;
				if (readZ > cachedTerritory.maxZ) {
					readZ = cachedTerritory.minZ;
					flag2 = true;
				}
				if (flag2) {
					readY++;
				}
			}
		}

		private Coordinate getCoordinate() {
			return found;
		}

	}

	private static class MutableBlockKey {

		private Block block;
		private int metadata;

		private void read(World world, int x, int y, int z) {
			block = world.getBlock(x, y, z);
			metadata = world.getBlockMetadata(x, y, z);
		}

		@Override
		public int hashCode() {
			return block.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof BlockKey) {
				BlockKey b = (BlockKey)o;
				return b.blockID == block && (!this.hasMetadata() || !b.hasMetadata() || b.metadata == metadata);
			}
			else if (o instanceof MutableBlockKey) {
				MutableBlockKey b = (MutableBlockKey)o;
				return b.block == block && (!this.hasMetadata() || !b.hasMetadata() || b.metadata == metadata);
			}
			return false;
		}

		private boolean hasMetadata() {
			return metadata >= 0 && metadata != OreDictionary.WILDCARD_VALUE;
		}

		private BlockKey asBlockKey() {
			return new BlockKey(block, metadata);
		}

	}

}
