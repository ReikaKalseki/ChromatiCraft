/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.World.Dimension.Structure.Laser;

import java.io.IOException;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Block.BlockChromaDoor;
import Reika.ChromatiCraft.Block.BlockChromaDoor.TileEntityChromaDoor;
import Reika.ChromatiCraft.Block.Dimension.Structure.BlockLaserEffector;
import Reika.ChromatiCraft.Block.Dimension.Structure.BlockLaserEffector.LaserEffectType;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.LaserPuzzleGenerator;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.StructureExport;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.StructureExport.NBTCallback;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.StructureExport.PlacementCallback;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper.CubeDirections;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;


public class LaserLevel extends StructurePiece implements PlacementCallback, NBTCallback {

	private final String name;
	private static final String PATH = "Structure Data/Laser";

	private StructureExport data;

	public boolean isSolved = false;

	private Coordinate doorLocation;

	public LaserLevel(LaserPuzzleGenerator s, String name) {
		super(s);

		data = new StructureExport(name, PATH, ChromatiCraft.class).addIgnoredBlock(new BlockKey(Blocks.stone)).addIgnoredBlock(new BlockKey(Blocks.air));
		this.name = name;
		try {
			data.load();
			BlockBox box = data.getBounds();
			Coordinate origin = new Coordinate(box.minX, box.minY, box.minZ).negate();
			data.offset(origin);
		}
		catch (IOException e) {
			throw new RuntimeException("Could not load structure data for laser level '"+name+"'", e);
		}
	}

	public int getLengthX() {
		return data.getBounds().getSizeX();
	}

	public int getLengthZ() {
		return data.getBounds().getSizeZ();
	}

	public BlockBox getBounds() {
		return data.getBounds();
	}

	public int getFullLengthX() {
		return this.getLengthX()+7;
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		Coordinate pos = new Coordinate(x, y, z-this.getLengthZ()/2);
		data.offset(pos);
		int dr = 1;
		int w = 7;
		int w2 = w+dr;
		BlockBox def = data.getBounds();
		int[] r = {2, 2, 2, 3, w, w, w, w, w-1, w-2, w-4, 0, -3, -5};
		for (int i = 0; i < r.length; i++) {
			int r1 = r[i]-1;
			int r2 = r1+dr;
			int rb = i >= 4 ? r2 : r1;
			BlockBox box = data.getBounds().expand(r1, 0, rb).offset(new Coordinate(0, i-1, 0));
			for (int dx = def.minX-w; dx <= def.maxX+w; dx++) {
				for (int dz = def.minZ-w2; dz <= def.maxZ+w2; dz++) {
					int dy = y+i-1;
					boolean wall = i == 0 || i == r.length-1 || !box.isBlockInside(dx, dy, dz);
					if (wall) {
						world.setBlock(dx, dy, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
					}
					else {
						world.setBlock(dx, dy, dz, Blocks.air);
					}
				}
			}
		}

		data.placeCallback = this;
		data.setExtraNBTTag("level", name);
		data.setExtraNBTTag("uid", parent.id.toString());
		data.addNBTOverride("dir", this);
		data.place(world);

		for (int i = 3; i <= 6; i++) {
			int dy = y+i;
			int d = i == 6 ? 1 : 2;
			for (int k = -d; k <= d; k++) {
				world.setBlock(def.minX-w, dy, z+k, Blocks.air);
				world.setBlock(def.maxX+w, dy, z+k, ChromaBlocks.DOOR.getBlockInstance(), BlockChromaDoor.getMetadata(false, false, false, true));
			}
		}
		doorLocation = new Coordinate(def.maxX+w, y+3, z);

		int xc = x+this.getLengthX()/2;
		int zp = z-this.getLengthZ()/2-3;

		for (int dz = zp; dz >= zp-4; dz--) {
			for (int dx = def.minX; dx <= def.maxX; dx++) {
				world.setBlock(dx, y+3, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
			}
			for (int dx = def.minX+1; dx <= def.maxX-1; dx++) {
				if (Math.abs(dx-xc) > 1 || dz != zp-1)
					world.setBlock(dx, y+4, dz, Blocks.stonebrick);
				else
					world.setBlock(dx, y+4, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
			}

			world.setBlock(def.minX-1, y+3, dz, Blocks.stone_brick_stairs, 0);
			world.setBlock(def.maxX+1, y+3, dz, Blocks.stone_brick_stairs, 1);
			world.setBlock(def.minX, y+4, dz, Blocks.stone_brick_stairs, 0);
			world.setBlock(def.maxX, y+4, dz, Blocks.stone_brick_stairs, 1);
		}

		for (int dx = def.minX-1; dx <= def.maxX+1; dx++) {
			for (int i = 3; i <= 4; i++) {
				int dz = zp;
				int dy = y+i;
				world.setBlock(dx, dy, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);

				if (Math.abs(dx-xc) > 2) {
					world.setBlock(dx, dy, dz-4, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
				}
				else {
					world.setBlock(dx, dy, dz-4, Blocks.stonebrick);
				}
			}
		}

		parent.generateDataTile(xc, y+4, zp, "level", name);

		/*
		for (int dx = def.minX-w; dx <= def.maxX+w; dx++) {
			for (int dz = def.minZ-w; dz <= def.maxZ+w; dz++) {
				int dy = y+2;
				BlockKey bk = world.getBlock(dx, dy, dz);
				if (bk != null && bk.blockID == ChromaBlocks.STRUCTSHIELD.getBlockInstance()) {
					BlockKey bk2 = world.getBlock(dx, dy+1, dz);
					if (bk2 == null) {
						boolean flag = true;
						for (int i = 2; i <= 6; i++) {
							ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
							int ddx = dx+dir.offsetX;
							int ddz = dz+dir.offsetZ;
							BlockKey adj = world.getBlock(ddx, dy+1, ddz);
							if (adj != null) {
								flag = false;
								break;
							}
						}
						if (flag) {
							world.setBlock(dx, dy, dz, Blocks.stonebrick);
						}
					}
				}
			}
		}
		 */

		for (int dx = def.minX-w+1; dx <= def.maxX+w-1; dx++) {
			for (int dz = def.minZ-w2+2; dz <= def.maxZ+w2-2; dz++) {
				if (dx != def.minX-w && dx != def.maxX+w && dz != def.minZ-w2 && dz != def.maxZ+w2) {
					boolean flag = false;
					if (dx > def.minX-w+1 && dx < def.maxX+w-1) {
						if (dx > def.maxX+3 || dx < def.minX-3) {
							flag = true;
						}
						if (dz > def.maxZ+3 || dz < def.minZ-3) {
							flag = true;
						}
					}
					if (Math.abs(dz-z) <= 1 && (dx == def.minX-w+1 || dx == def.maxX+w-1)) {
						flag = true;
					}
					if (flag) {
						world.setBlock(dx, y+2, dz, Blocks.stonebrick);
					}
				}
			}
		}

		for (int dx = def.minX+1; dx <= def.maxX-1; dx++) {
			int dz = def.maxZ+2;
			world.setBlock(dx, y+1, dz, Blocks.air);
		}

		world.setBlock(def.minX-2, y+1, def.minZ-2, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata);
		world.setBlock(def.maxX+2, y+1, def.minZ-2, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata);
		world.setBlock(def.minX-2, y+1, def.maxZ+2, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata);
		world.setBlock(def.maxX+2, y+1, def.maxZ+2, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata);

		world.setBlock(def.minX-5, y+2, z, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata);
		world.setBlock(def.maxX+5, y+2, z, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata);
		world.setBlock(xc, y+2, def.maxZ+5, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata);

		world.setBlock(def.minX-w, y+4, def.maxZ+w2-3, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata);
		world.setBlock(def.minX-w, y+5, def.maxZ+w2-5, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata);
		world.setBlock(def.maxX+w, y+4, def.maxZ+w2-3, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata);
		world.setBlock(def.maxX+w, y+5, def.maxZ+w2-5, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata);

		world.setBlock(def.minX-w, y+4, def.minZ-w2+3, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata);
		world.setBlock(def.minX-w, y+5, def.minZ-w2+5, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata);
		world.setBlock(def.maxX+w, y+4, def.minZ-w2+3, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata);
		world.setBlock(def.maxX+w, y+5, def.minZ-w2+5, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata);

		world.setBlock(def.minX-w+2, y+4, def.maxZ+w2, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata);
		world.setBlock(def.minX-w+4, y+5, def.maxZ+w2, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata);
		world.setBlock(def.maxX+w-2, y+4, def.maxZ+w2, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata);
		world.setBlock(def.maxX+w-4, y+5, def.maxZ+w2, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata);

		world.setBlock(def.minX-w+7, y+5, def.minZ-w2, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata);
		world.setBlock(def.maxX+w-7, y+5, def.minZ-w2, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata);

		world.setBlock(xc+1, y+4, zp-1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata);
		world.setBlock(xc-1, y+4, zp-1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata);

		world.setBlock(xc+1, y+11, z, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata);
		world.setBlock(xc, y+11, z, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
		world.setBlock(xc-1, y+11, z, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.LIGHT.metadata);
	}

	@Override
	public void onPlace(Coordinate c, BlockKey bk, NBTTagCompound data) {
		if (bk.blockID == ChromaBlocks.LASEREFFECT.getBlockInstance()) {
			if (bk.metadata == LaserEffectType.EMITTER.ordinal()) {
				((LaserPuzzleGenerator)parent).addEmitter(name, c);
			}
			else if (bk.metadata == LaserEffectType.TARGET.ordinal() || bk.metadata == LaserEffectType.TARGET_THRU.ordinal()) {
				((LaserPuzzleGenerator)parent).addTarget(name, c);
			}
			/*
			else if (!LaserEffectType.list[bk.metadata].isOmniDirectional()) {
				if (data != null && data.getBoolean("free")) {
					int dir = ReikaRandomHelper.getSafeRandomInt(CubeDirections.list.length);

				}
			}
			 */
		}
	}

	public String getName() {
		return name;
	}

	public NBTBase getOverriddenValue(Coordinate c, BlockKey bk, String key, NBTBase original, NBTTagCompound data) {
		if (key.equals("dir")) {
			if (!LaserEffectType.list[bk.metadata].isOmniDirectional() && (BlockLaserEffector.LaserEffectTile.PARTIAL_ROTATEABILITY ? data.getBoolean("free") : bk.metadata != LaserEffectType.EMITTER.ordinal() && bk.metadata != LaserEffectType.TARGET.ordinal() && bk.metadata != LaserEffectType.TARGET_THRU.ordinal())) {
				int dir = ReikaRandomHelper.getSafeRandomInt(CubeDirections.list.length);
				return new NBTTagInt(dir);
			}
		}
		return original;
	}

	public void applyDoorState(World world) {
		TileEntityChromaDoor te = (TileEntityChromaDoor)doorLocation.getTileEntity(world);
		if (te == null) {
			ChromatiCraft.logger.log("No door for laser puzzle room "+name+" @ "+doorLocation+"?");
			return;
		}
		if (isSolved) {
			te.open(0);
		}
		else {
			te.close();
		}
	}

}
