package Reika.ChromatiCraft.World.Dimension.Structure.PistonTape;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Base.StructurePiece;
import Reika.ChromatiCraft.Block.Dimension.Structure.Laser.BlockLaserEffector.ColorData;
import Reika.ChromatiCraft.Block.Dimension.Structure.Laser.BlockLaserEffector.EmitterTile;
import Reika.ChromatiCraft.Block.Dimension.Structure.Laser.BlockLaserEffector.LaserEffectType;
import Reika.ChromatiCraft.Block.Dimension.Structure.Laser.BlockLaserEffector.TargetTile;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.World.Dimension.Structure.PistonTapeGenerator;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache;
import Reika.DragonAPI.Instantiable.Worldgen.ChunkSplicedGenerationCache.TileCallback;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper.CubeDirections;

public class TapeStage extends StructurePiece implements TileCallback {

	private final Coordinate emitterColumnBase;
	private final Coordinate targetColumnBase;

	private final DoorKey door;

	private boolean isActive = false;

	public TapeStage(PistonTapeGenerator gen, DoorKey d, int x, int y, int z, int x2, int y2, int z2) {
		super(gen);
		door = d;
		emitterColumnBase = new Coordinate(x, y, z);
		targetColumnBase = new Coordinate(x2, y2, z2);
	}

	public void setActive(World world, boolean active) {
		isActive = active;

		EmitterTile te1 = this.getEmitter(world, 0);
		EmitterTile te2 = this.getEmitter(world, 1);
		EmitterTile te3 = this.getEmitter(world, 2);

		te1.keepFiring = isActive;
		te2.keepFiring = isActive;
		te3.keepFiring = isActive;
	}

	private EmitterTile getEmitter(World world, int i) {
		return (EmitterTile)emitterColumnBase.offset(0, i, 0).getTileEntity(world);
	}

	private TargetTile getTarget(World world, int i) {
		return (TargetTile)targetColumnBase.offset(0, i, 0).getTileEntity(world);
	}

	@Override
	public void generate(ChunkSplicedGenerationCache world, int x, int y, int z) {
		int mx = (emitterColumnBase.xCoord+targetColumnBase.xCoord)/2;
		int mz = (emitterColumnBase.zCoord+targetColumnBase.zCoord)/2;

		int xm = Math.min(emitterColumnBase.xCoord, targetColumnBase.xCoord);
		int xp = Math.max(emitterColumnBase.xCoord, targetColumnBase.xCoord);
		int zm = Math.min(emitterColumnBase.zCoord, targetColumnBase.zCoord);
		int zp = Math.max(emitterColumnBase.zCoord, targetColumnBase.zCoord);

		int h = 4;

		for (int i = 0; i <= h; i++) {
			if (i <= 2) {
				world.setTileEntity(emitterColumnBase.xCoord, emitterColumnBase.yCoord+i, emitterColumnBase.zCoord, ChromaBlocks.LASEREFFECT.getBlockInstance(), LaserEffectType.EMITTER.ordinal(), this);
				world.setTileEntity(targetColumnBase.xCoord, targetColumnBase.yCoord+i, targetColumnBase.zCoord, ChromaBlocks.LASEREFFECT.getBlockInstance(), LaserEffectType.TARGET.ordinal(), this);

				world.setBlock(mx, y+i, mz-1, ChromaBlocks.PISTONBIT.getBlockInstance(), 0);
				world.setBlock(mx, y+i, mz, ChromaBlocks.PISTONBIT.getBlockInstance(), 2);
				world.setBlock(mx, y+i, mz+1, ChromaBlocks.PISTONBIT.getBlockInstance(), 4);
			}

			world.setBlock(xm-1, y+i, zm-1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
			world.setBlock(xp+1, y+i, zp+1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
			world.setBlock(xm-1, y+i, zp+1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
			world.setBlock(xp+1, y+i, zm-1, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
		}

		for (int dx = xm-1; dx <= xp+1; dx++) {
			for (int dz = zm-1; dz <= zp+1; dz++) {
				world.setBlock(dx, y-1, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
				world.setBlock(dx, y+h+1, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.STONE.metadata);
			}
		}
	}

	public boolean isCorrect(World world) {
		return this.getTarget(world, 0).isTriggered() && this.getTarget(world, 1).isTriggered() && this.getTarget(world, 2).isTriggered();
	}

	@Override
	public void onTilePlaced(World world, int x, int y, int z, TileEntity te) {
		if (te instanceof EmitterTile) {
			((EmitterTile)te).renderAsFullBlock = true;
			((EmitterTile)te).silent = true;
			((EmitterTile)te).speedFactor = 1.25;
			((EmitterTile)te).setDirection(CubeDirections.SOUTH);
		}
		else if (te instanceof TargetTile) {
			((TargetTile)te).renderAsFullBlock = true;
			((TargetTile)te).autoReset = PistonTapeData.STEP_DURATION;
			((TargetTile)te).setDirection(CubeDirections.SOUTH);

			int dy = te.yCoord-targetColumnBase.yCoord;
			ColorData c = new ColorData(true, true, true);
			switch(dy) {
				case 0:
					c = door.color1;
					break;
				case 1:
					c = door.color2;
					break;
				case 2:
					c = door.color3;
					break;
			}
			((TargetTile)te).setColor(c);
		}
	}

	public void tick(World world) {
		if (isActive) {
			door.setOpen(world, this.isCorrect(world));
		}
	}

}
