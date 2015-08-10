/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicitrior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Auxiliary.Interfaces.StructureData;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator;
import Reika.ChromatiCraft.Block.BlockChromaDoor;
import Reika.ChromatiCraft.Block.BlockChromaDoor.TileEntityChromaDoor;
import Reika.ChromatiCraft.Block.Dimension.Structure.BlockTeleport.Deactivate;
import Reika.ChromatiCraft.Block.Dimension.Structure.BlockTeleport.DeactivateOneOf;
import Reika.ChromatiCraft.Block.Dimension.Structure.BlockTeleport.RerouteIf;
import Reika.ChromatiCraft.Block.Dimension.Structure.BlockTeleport.TeleportTriggerAction;
import Reika.ChromatiCraft.Block.Dimension.Structure.BlockTeleport.TileEntityTeleport;
import Reika.ChromatiCraft.Block.Worldgen.BlockLootChest;
import Reika.ChromatiCraft.Block.Worldgen.BlockLootChest.TileEntityLootChest;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Items.Tools.ItemDoorKey;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityTransportWindow;
import Reika.ChromatiCraft.World.Dimension.Structure.NonEuclid.NonEuclidLayer1;
import Reika.ChromatiCraft.World.Dimension.Structure.NonEuclid.NonEuclidLayer2;
import Reika.ChromatiCraft.World.Dimension.Structure.NonEuclid.NonEuclidLayer3;
import Reika.ChromatiCraft.World.Dimension.Structure.NonEuclid.NonEuclidLayer4;
import Reika.ChromatiCraft.World.Dimension.Structure.NonEuclid.NonEuclidLayer5;
import Reika.ChromatiCraft.World.Dimension.Structure.NonEuclid.NonEuclidLayer6;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockVector;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache.TileCallback;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;

public class NonEuclideanGenerator extends DimensionStructureGenerator {

	private final NonEuclidLayer1 layer1 = new NonEuclidLayer1();
	private final NonEuclidLayer2 layer2 = new NonEuclidLayer2();
	private final NonEuclidLayer3 layer3 = new NonEuclidLayer3();
	private final NonEuclidLayer4 layer4 = new NonEuclidLayer4();
	private final NonEuclidLayer5 layer5 = new NonEuclidLayer5();
	private final NonEuclidLayer6 layer6 = new NonEuclidLayer6();

	private UUID door;

	@Override
	protected void calculate(int chunkX, int chunkZ, CrystalElement e, Random rand) {
		Block b = ChromaBlocks.SPECIALSHIELD.getBlockInstance();
		Block b2 = ChromaBlocks.STRUCTSHIELD.getBlockInstance();

		door = UUID.randomUUID();

		int x = chunkX;
		posY = 200;
		int z = chunkZ;

		//-11, 0, -12
		layer1.generate(world, rand, x, posY, z, b, b2);
		layer2.generate(world, rand, x, posY, z, b, b2);
		layer3.generate(world, rand, x, posY, z, b, b2);
		layer4.generate(world, rand, x, posY, z, b, b2);
		layer5.generate(world, rand, x, posY, z, b, b2);
		layer6.generate(world, rand, x, posY, z, b, b2);

		this.placeWindow(x+9, posY+2, z+44, ForgeDirection.WEST, 9, 2, 52);
		this.placeWindow(x+3, posY+2, z+52, ForgeDirection.EAST, 3, 2, 44);


		this.createPortal(x+39, posY+1, z+52, ForgeDirection.NORTH, new BlockVector(-16, 0, 8, ForgeDirection.NORTH));
		this.createPortal(x+23, posY+1, z+62, ForgeDirection.SOUTH, new BlockVector(16, 0, -8, ForgeDirection.SOUTH));
		this.createPortal(x+31, posY+1, z+48, ForgeDirection.NORTH, new BlockVector(-8, 0, 12, ForgeDirection.NORTH), new Coordinate(-8, 0, 14), DeactivateOneOf.instance, new Coordinate(8, 0, 4), DeactivateOneOf.instance);

		RerouteIf newLoc1 = new RerouteIf(new BlockVector(-1, 0, 12, ForgeDirection.EAST));
		RerouteIf newLoc2 = new RerouteIf(new BlockVector(-1, 0, 4, ForgeDirection.EAST));
		RerouteIf newLoc3 = new RerouteIf(new BlockVector(-1, 0, -4, ForgeDirection.EAST));
		this.createPortal(x+24, posY+1, z+33, ForgeDirection.WEST, new BlockVector(11, 0, 8, ForgeDirection.EAST), new Coordinate(0, 0, 0), newLoc1, new Coordinate(0, 0, 8), newLoc1, new Coordinate(0, 0, 16), newLoc1);
		this.createPortal(x+24, posY+1, z+41, ForgeDirection.WEST, new BlockVector(11, 0, 0, ForgeDirection.EAST), new Coordinate(0, 0, -8), newLoc2, new Coordinate(0, 0, 0), newLoc2, new Coordinate(0, 0, 8), newLoc2);
		this.createPortal(x+24, posY+1, z+49, ForgeDirection.WEST, new BlockVector(11, 0, -8, ForgeDirection.EAST), new Coordinate(0, 0, -16), newLoc3, new Coordinate(0, 0, -8), newLoc3, new Coordinate(0, 0, 0), newLoc3);

		this.createPortal(x+9, posY+1, z+4, ForgeDirection.NORTH, new BlockVector(12, 0, 0, ForgeDirection.NORTH));
		this.createPortal(x+21, posY+1, z+6, ForgeDirection.NORTH, new BlockVector(12, 0, -2, ForgeDirection.SOUTH));
		this.createPortal(x+33, posY+1, z+6, ForgeDirection.SOUTH, new BlockVector(12, 0, -2, ForgeDirection.NORTH));
		this.createPortal(x+45, posY+1, z+6, ForgeDirection.SOUTH, new BlockVector(-36, 0, 0, ForgeDirection.NORTH));

		this.createPortal(x+10, posY+1, z+20, ForgeDirection.EAST, new BlockVector(10, 0, 25, ForgeDirection.WEST));
		this.createPortal(x+22, posY+1, z+20, ForgeDirection.WEST, new BlockVector(-6, 0, 21, ForgeDirection.SOUTH));
		this.createPortal(x+16, posY+1, z+34, ForgeDirection.SOUTH, new BlockVector(14, 0, -7, ForgeDirection.NORTH));

		this.createPortal(x+9, posY+1, z+12, ForgeDirection.SOUTH, new BlockVector(36, 0, 0, ForgeDirection.SOUTH), new Coordinate(36, 0, 0), Deactivate.instance);

		this.createPortal(x+13, posY+1, z+16, ForgeDirection.WEST, new BlockVector(8, 0, 53, ForgeDirection.WEST));

		this.createPortal(x+41, posY+1, z+18, ForgeDirection.NORTH, new BlockVector(-4, 0, 27, ForgeDirection.EAST));
		this.createPortal(x+41, posY+1, z+31, ForgeDirection.SOUTH, new BlockVector(-6, 0, 10, ForgeDirection.EAST));

		this.createPortal(x+7, posY+1, z+35, ForgeDirection.EAST, new BlockVector(24, 0, 13, ForgeDirection.NORTH));
		this.createPortal(x+18, posY+1, z+50, ForgeDirection.NORTH, new BlockVector(-4, 0, 0, ForgeDirection.NORTH));

		this.createPortal(x+45, posY+1, z+10, ForgeDirection.SOUTH, new BlockVector(0, 0, 0, ForgeDirection.SOUTH), new Coordinate(0, 0, 0), Deactivate.instance); //trigger for key portal


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

	private void createBreakable(int x, int y, int z) {
		world.setBlock(x, y, z, ChromaBlocks.SPECIALSHIELD.getBlockInstance(), BlockType.STONE.metadata);
		this.addBreakable(x, y, z);
	}

	private void createPortal(int x, int y, int z, ForgeDirection dir, BlockVector bv, Object... ts) {
		PortalPlace p = new PortalPlace(dir, bv);
		for (int i = 0; i < ts.length; i += 2) {
			p.addAction((Coordinate)ts[i], (TeleportTriggerAction)ts[i+1]);
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

		int m = BlockChromaDoor.getMetadata(false, false, true, false);

		ForgeDirection left = ReikaDirectionHelper.getLeftBy90(dir);
		for (int d = -1; d <= 1; d++) {
			world.setTileEntity(x+d*left.offsetX, y, z+d*left.offsetZ, b, m, doorCallback);
			world.setTileEntity(x+d*left.offsetX, y+1, z+d*left.offsetZ, b, m, doorCallback);
			world.setTileEntity(x+d*left.offsetX, y+2, z+d*left.offsetZ, b, m, doorCallback);
		}
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
		private final HashMap<Coordinate, TeleportTriggerAction> actions = new HashMap();

		private PortalPlace(ForgeDirection dir, BlockVector bv) {
			location = bv;
			direction = dir;
		}

		private PortalPlace addAction(Coordinate c, TeleportTriggerAction t) {
			actions.put(c, t);
			return this;
		}

		@Override
		public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
			if (te instanceof TileEntityTeleport) {
				TileEntityTeleport tp = (TileEntityTeleport)te;
				tp.destination = location;
				tp.facing = direction;

				for (Coordinate c : actions.keySet()) {
					TeleportTriggerAction t = actions.get(c);
					tp.putAction(c, t);
				}
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
				((TileEntityChromaDoor)te).bindUUID(uid);
			}
		}

	}

}
