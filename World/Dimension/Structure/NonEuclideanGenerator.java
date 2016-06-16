/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Base.StructureData;
import Reika.ChromatiCraft.Block.BlockChromaDoor;
import Reika.ChromatiCraft.Block.BlockChromaDoor.TileEntityChromaDoor;
import Reika.ChromatiCraft.Block.Dimension.Structure.NonEuclid.BlockTeleport.Deactivate;
import Reika.ChromatiCraft.Block.Dimension.Structure.NonEuclid.BlockTeleport.DeactivateOneOf;
import Reika.ChromatiCraft.Block.Dimension.Structure.NonEuclid.BlockTeleport.RerouteIf;
import Reika.ChromatiCraft.Block.Dimension.Structure.NonEuclid.BlockTeleport.SameFacing;
import Reika.ChromatiCraft.Block.Dimension.Structure.NonEuclid.BlockTeleport.TeleportTriggerAction;
import Reika.ChromatiCraft.Block.Dimension.Structure.NonEuclid.BlockTeleport.TileEntityTeleport;
import Reika.ChromatiCraft.Block.Dimension.Structure.NonEuclid.BlockTeleport.TriggerCriteria;
import Reika.ChromatiCraft.Block.Worldgen.BlockLootChest;
import Reika.ChromatiCraft.Block.Worldgen.BlockLootChest.TileEntityLootChest;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Items.Tools.ItemDoorKey;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityTransportWindow;
import Reika.ChromatiCraft.World.Dimension.Structure.NonEuclid.NonEuclidEntrance;
import Reika.ChromatiCraft.World.Dimension.Structure.NonEuclid.NonEuclidLayer1;
import Reika.ChromatiCraft.World.Dimension.Structure.NonEuclid.NonEuclidLayer2;
import Reika.ChromatiCraft.World.Dimension.Structure.NonEuclid.NonEuclidLayer3;
import Reika.ChromatiCraft.World.Dimension.Structure.NonEuclid.NonEuclidLayer4;
import Reika.ChromatiCraft.World.Dimension.Structure.NonEuclid.NonEuclidLayer5;
import Reika.ChromatiCraft.World.Dimension.Structure.NonEuclid.NonEuclidLayer6;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockVector;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache.TileCallback;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

public class NonEuclideanGenerator extends DimensionStructureGenerator {

	private final NonEuclidLayer1 layer1 = new NonEuclidLayer1();
	private final NonEuclidLayer2 layer2 = new NonEuclidLayer2();
	private final NonEuclidLayer3 layer3 = new NonEuclidLayer3();
	private final NonEuclidLayer4 layer4 = new NonEuclidLayer4();
	private final NonEuclidLayer5 layer5 = new NonEuclidLayer5();
	private final NonEuclidLayer6 layer6 = new NonEuclidLayer6();

	private UUID door;
	private Coordinate doorLoc;

	private final HashSet<Coordinate> portals = new HashSet();

	private final MultiMap<Coordinate, TriggerCriteria> criteria = new MultiMap();
	private final HashMap<Coordinate, HashMap<Coordinate, TeleportTriggerAction>> actions = new HashMap();

	@Override
	protected void calculate(int chunkX, int chunkZ, Random rand) {
		Block b = ChromaBlocks.SPECIALSHIELD.getBlockInstance();
		Block b2 = ChromaBlocks.STRUCTSHIELD.getBlockInstance();

		door = UUID.randomUUID();

		int x = chunkX;
		posY = 10+rand.nextInt(80);
		int z = chunkZ;

		entryX = x+31;
		entryZ = z+64;

		//-11, 0, -12
		layer1.generate(world, rand, x, posY, z, b, b2);
		layer2.generate(world, rand, x, posY, z, b, b2);
		layer3.generate(world, rand, x, posY, z, b, b2);
		layer4.generate(world, rand, x, posY, z, b, b2);
		layer5.generate(world, rand, x, posY, z, b, b2);
		layer6.generate(world, rand, x, posY, z, b, b2);

		this.generateAir(world, rand, x, posY, z);

		this.addDynamicStructure(new NonEuclidEntrance(this), entryX, entryZ);

		this.placeWindow(x+9, posY+2, z+44, ForgeDirection.WEST, x+3, posY+2, z+52);
		this.placeWindow(x+3, posY+2, z+52, ForgeDirection.EAST, x+9, posY+2, z+44);

		this.createPortal(x+39, posY+1, z+52, ForgeDirection.NORTH, new BlockVector(-16, 0, 8, ForgeDirection.NORTH));
		this.createPortal(x+23, posY+1, z+62, ForgeDirection.SOUTH, new BlockVector(16, 0, -8, ForgeDirection.SOUTH));
		this.createPortal(x+31, posY+1, z+48, ForgeDirection.NORTH, new BlockVector(-8, 0, 12, ForgeDirection.NORTH), new Coordinate(-8, 0, 14), DeactivateOneOf.instance, new Coordinate(8, 0, 4), DeactivateOneOf.instance);

		RerouteIf newLoc1 = new RerouteIf(new BlockVector(-2, 0, 12, ForgeDirection.EAST));
		RerouteIf newLoc2 = new RerouteIf(new BlockVector(-2, 0, 4, ForgeDirection.EAST));
		RerouteIf newLoc3 = new RerouteIf(new BlockVector(-2, 0, -4, ForgeDirection.EAST));
		this.createPortal(x+24, posY+1, z+33, ForgeDirection.WEST, new BlockVector(11, 0, 8, ForgeDirection.EAST), new Coordinate(0, 0, 0), newLoc1, new Coordinate(0, 0, 8), newLoc1, new Coordinate(0, 0, 16), newLoc1);
		this.createPortal(x+24, posY+1, z+41, ForgeDirection.WEST, new BlockVector(11, 0, 0, ForgeDirection.EAST), new Coordinate(0, 0, -8), newLoc2, new Coordinate(0, 0, 0), newLoc2, new Coordinate(0, 0, 8), newLoc2);
		this.createPortal(x+24, posY+1, z+49, ForgeDirection.WEST, new BlockVector(11, 0, -8, ForgeDirection.EAST), new Coordinate(0, 0, -16), newLoc3, new Coordinate(0, 0, -8), newLoc3, new Coordinate(0, 0, 0), newLoc3);

		this.createPortal(x+9, posY+1, z+4, ForgeDirection.NORTH, new BlockVector(12, 0, 0, ForgeDirection.NORTH), SameFacing.instance);
		this.createPortal(x+21, posY+1, z+6, ForgeDirection.NORTH, new BlockVector(12, 0, -2, ForgeDirection.SOUTH), SameFacing.instance);
		this.createPortal(x+33, posY+1, z+6, ForgeDirection.SOUTH, new BlockVector(12, 0, -2, ForgeDirection.NORTH), SameFacing.instance);
		this.createPortal(x+45, posY+1, z+6, ForgeDirection.SOUTH, new BlockVector(-36, 0, 0, ForgeDirection.SOUTH), SameFacing.instance);

		this.createPortal(x+10, posY+1, z+20, ForgeDirection.EAST, new BlockVector(10, 0, 25, ForgeDirection.WEST));
		this.createPortal(x+22, posY+1, z+20, ForgeDirection.WEST, new BlockVector(-6, 0, 21, ForgeDirection.SOUTH));
		this.createPortal(x+16, posY+1, z+34, ForgeDirection.SOUTH, new BlockVector(14, 0, -7, ForgeDirection.NORTH), SameFacing.instance/*, new Coordinate(6, 0, -14), DeactivateOneOf.instance, new Coordinate(-6, 0, -14), DeactivateOneOf.instance*/);

		this.createPortal(x+9, posY+1, z+12, ForgeDirection.SOUTH, new BlockVector(36, 0, 0, ForgeDirection.SOUTH), new Coordinate(36, 0, 0), Deactivate.instance);

		this.createPortal(x+13, posY+1, z+16, ForgeDirection.WEST, new BlockVector(8, 0, 37, ForgeDirection.WEST), SameFacing.instance);

		this.createPortal(x+41, posY+1, z+18, ForgeDirection.NORTH, new BlockVector(-4, 0, 27, ForgeDirection.EAST));
		this.createPortal(x+41, posY+1, z+31, ForgeDirection.SOUTH, new BlockVector(-6, 0, 10, ForgeDirection.EAST));

		this.createPortal(x+7, posY+1, z+35, ForgeDirection.EAST, new BlockVector(24, 0, 13, ForgeDirection.NORTH));
		this.createPortal(x+18, posY+1, z+50, ForgeDirection.NORTH, new BlockVector(-4, 0, 0, ForgeDirection.NORTH));

		this.createPortal(x+45, posY+1, z+12, ForgeDirection.SOUTH, new BlockVector(0, 0, 0, ForgeDirection.SOUTH)); //trigger for key portal


		this.placeCore(x+53, posY+2, z+16);

		this.placeKey(x+13, posY+1, z+12, rand);

		this.placeDoor(x+50, posY+1, z+16, ForgeDirection.EAST);

		this.createBreakable(x+52, posY+1, z+13);
		this.createBreakable(x+52, posY+1, z+19);
		this.createBreakable(x+52, posY+2, z+13);
		this.createBreakable(x+52, posY+2, z+19);
		this.createBreakable(x+52, posY+3, z+13);
		this.createBreakable(x+52, posY+3, z+19);
		this.createBreakable(x+53, posY+1, z+13);
		this.createBreakable(x+53, posY+1, z+19);
		this.createBreakable(x+53, posY+2, z+13);
		this.createBreakable(x+53, posY+2, z+19);
		this.createBreakable(x+53, posY+3, z+13);
		this.createBreakable(x+53, posY+3, z+19);
		this.createBreakable(x+54, posY+1, z+13);
		this.createBreakable(x+54, posY+1, z+19);
		this.createBreakable(x+54, posY+2, z+13);
		this.createBreakable(x+54, posY+2, z+19);
		this.createBreakable(x+54, posY+3, z+13);
		this.createBreakable(x+54, posY+3, z+19);
		this.createBreakable(x+56, posY+1, z+15);
		this.createBreakable(x+56, posY+1, z+16);
		this.createBreakable(x+56, posY+1, z+17);
		this.createBreakable(x+56, posY+2, z+15);
		this.createBreakable(x+56, posY+2, z+16);
		this.createBreakable(x+56, posY+2, z+17);
		this.createBreakable(x+56, posY+3, z+15);
		this.createBreakable(x+56, posY+3, z+16);
		this.createBreakable(x+56, posY+3, z+17);
	}

	private void generateAir(ChunkSplicedGenerationCache world, Random rand, int x, int posY, int z) {
		HashSet<Coordinate> set = world.getLocationsOf(new BlockKey(ChromaBlocks.SPECIALSHIELD.getBlockInstance(), BlockType.CLOAK.metadata));
		for (Coordinate c : set) {
			for (int i = 1; i <= 3; i++) {
				int dx = c.xCoord;
				int dy = c.yCoord+i;
				int dz = c.zCoord;
				if (!world.hasBlock(dx, dy, dz))
					world.setBlock(dx, dy, dz, Blocks.air);
			}
		}
	}

	private void createBreakable(int x, int y, int z) {
		world.setBlock(x, y, z, ChromaBlocks.SPECIALSHIELD.getBlockInstance(), BlockType.STONE.metadata);
		this.addBreakable(x, y, z);
	}

	private void createPortal(int x, int y, int z, ForgeDirection dir, BlockVector bv, Object... ts) {
		PortalPlace p = new PortalPlace(id, dir, bv);
		ArrayList li = ReikaJavaLibrary.makeListFrom(ts);
		while (!li.isEmpty()) {
			Object o = li.remove(0);
			if (o instanceof TriggerCriteria) {
				this.addCriteria(x, y, z, (TriggerCriteria)o);
			}
			else if (o instanceof Coordinate) {
				this.addAction(x, y, z, (Coordinate)o, (TeleportTriggerAction)li.remove(0));
			}
		}

		world.setTileEntity(x, y, z, ChromaBlocks.TELEPORT.getBlockInstance(), 0, p);

		ForgeDirection left = ReikaDirectionHelper.getLeftBy90(dir);
		for (int d = -1; d <= 1; d++) {
			if (d != 0) {
				world.setBlock(x+d*left.offsetX, y, z+d*left.offsetZ, ChromaBlocks.TELEPORT.getBlockInstance(), 1);
			}
			world.setBlock(x+d*left.offsetX, y+1, z+d*left.offsetZ, ChromaBlocks.TELEPORT.getBlockInstance(), 1);
			world.setBlock(x+d*left.offsetX, y+2, z+d*left.offsetZ, ChromaBlocks.TELEPORT.getBlockInstance(), 1);
		}

		portals.add(new Coordinate(x, y, z));
	}

	private void addCriteria(int x, int y, int z, TriggerCriteria c) {
		criteria.addValue(new Coordinate(x, y, z), c);
	}

	private void addAction(int x, int y, int z, Coordinate rel, TeleportTriggerAction act) {
		Coordinate loc = new Coordinate(x, y, z);
		HashMap<Coordinate, TeleportTriggerAction> map = actions.get(loc);
		if (map == null) {
			map = new HashMap();
			actions.put(loc, map);
		}
		map.put(rel, act);
	}

	private void placeWindow(int x, int y, int z, ForgeDirection dir, int dx, int dy, int dz) {
		WindowPlace wp = new WindowPlace(dir, new Coordinate(dx, dy, dz));
		world.setTileEntity(x, y, z, ChromaTiles.WINDOW.getBlock(), ChromaTiles.WINDOW.getBlockMetadata(), wp);
	}

	private void placeKey(int x, int y, int z, Random rand) {
		world.setTileEntity(x, y, z, ChromaBlocks.LOOTCHEST.getBlockInstance(), BlockLootChest.getMeta(ForgeDirection.EAST), new LootChestCallback(door, rand));
	}

	private void placeDoor(int x, int y, int z, ForgeDirection dir) {
		Block b = ChromaBlocks.DOOR.getBlockInstance();

		DoorCallback doorCallback = new DoorCallback(door);

		int m = BlockChromaDoor.getMetadata(false, false, true, true);

		ForgeDirection left = ReikaDirectionHelper.getLeftBy90(dir);
		for (int d = -1; d <= 1; d++) {
			world.setTileEntity(x+d*left.offsetX, y, z+d*left.offsetZ, b, m, doorCallback);
			world.setTileEntity(x+d*left.offsetX, y+1, z+d*left.offsetZ, b, m, doorCallback);
			world.setTileEntity(x+d*left.offsetX, y+2, z+d*left.offsetZ, b, m, doorCallback);
		}

		doorLoc = new Coordinate(x, y, z);
	}

	@Override
	protected int getCenterXOffset() {
		return 0;
	}

	@Override
	protected int getCenterZOffset() {
		return 0;
	}

	@Override
	public StructureData createDataStorage() {
		return null;
	}

	@Override
	protected void clearCaches() {
		door = null;
		doorLoc = null;
		portals.clear();
		criteria.clear();
		actions.clear();
	}

	public Collection<Coordinate> getPortalLocations() {
		return Collections.unmodifiableSet(portals);
	}

	public HashMap<Coordinate, TeleportTriggerAction> getActions(int x, int y, int z) {
		return actions.get(new Coordinate(x, y, z));
	}

	public void removeAction(int x, int y, int z, Coordinate rel) {
		HashMap<Coordinate, TeleportTriggerAction> map = actions.get(new Coordinate(x, y, z));
		if (map != null) {
			map.remove(rel);
		}
	}

	public Collection<TriggerCriteria> getCriteria(int x, int y, int z) {
		return criteria.get(new Coordinate(x, y, z));
	}

	@Override
	public boolean hasBeenSolved(World world) {
		return BlockChromaDoor.isOpen(world, doorLoc.xCoord, doorLoc.yCoord, doorLoc.zCoord);
	}

	private static class WindowPlace implements TileCallback {

		private final ForgeDirection direction;
		private final Coordinate other;

		private WindowPlace(ForgeDirection dir, Coordinate c) {
			direction = dir;
			other = c;
		}

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
			if (te instanceof TileEntityTransportWindow) {
				((TileEntityTransportWindow)te).setFacing(direction);
				((TileEntityTransportWindow)te).renderBackPane = false;
				((TileEntityTransportWindow)te).renderTexture = false;
				((TileEntityTransportWindow)te).setUnmineable(true);

				if (other.getBlock(world) != ChromaTiles.WINDOW.getBlock() || other.getBlockMetadata(world) != ChromaTiles.WINDOW.getBlockMetadata())
					other.setBlock(world, ChromaTiles.WINDOW.getBlock(), ChromaTiles.WINDOW.getBlockMetadata());
				TileEntity te2 = other.getTileEntity(world);
				if (te2 instanceof TileEntityTransportWindow) {
					((TileEntityTransportWindow)te).linkTo((TileEntityTransportWindow)te2);
				}
			}
		}

	}

	private static class PortalPlace implements TileCallback {

		private final BlockVector location;
		private final ForgeDirection direction;
		private final UUID id;

		private PortalPlace(UUID uid, ForgeDirection dir, BlockVector bv) {
			id = uid;
			location = bv;
			direction = dir;
		}

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
			if (te instanceof TileEntityTeleport) {
				TileEntityTeleport tp = (TileEntityTeleport)te;
				tp.destination = location;
				tp.facing = direction;
				tp.uid = id;
			}
		}

	}

	private static class LootChestCallback implements TileCallback {

		private final UUID uid;
		private final Random rand;

		private LootChestCallback(UUID id, Random r) {
			uid = id;
			rand = r;
		}

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
			if (te instanceof TileEntityLootChest) {
				TileEntityLootChest tc = (TileEntityLootChest)te;
				ItemStack key = ChromaItems.KEY.getStackOf();
				((ItemDoorKey)ChromaItems.KEY.getItemInstance()).setID(key, uid);
				tc.setInventorySlotContents(rand.nextInt(tc.getSizeInventory()), key);
			}
		}

	}

	private static class DoorCallback implements TileCallback {

		private final UUID uid;

		private DoorCallback(UUID id) {
			uid = id;
		}

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
			if (te instanceof TileEntityChromaDoor) {
				((TileEntityChromaDoor)te).bindUUID(null, uid, 0);
			}
		}

	}

}
