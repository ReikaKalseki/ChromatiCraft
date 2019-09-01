/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dimension.Structure.PistonTape;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Auxiliary.Interfaces.LaserPulseEffect;
import Reika.ChromatiCraft.Base.BlockDimensionStructureTile;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.DimensionStructureType;
import Reika.ChromatiCraft.Base.TileEntity.StructureBlockTile;
import Reika.ChromatiCraft.Block.BlockChromaDoor;
import Reika.ChromatiCraft.Block.Dimension.Structure.Laser.BlockLaserEffector.ColorData;
import Reika.ChromatiCraft.Entity.EntityLaserPulse;
import Reika.ChromatiCraft.Entity.EntityPistonSpline;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.World.Dimension.Structure.PistonTapeGenerator;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Math.Spline;
import Reika.DragonAPI.Instantiable.Math.Spline.BasicSplinePoint;
import Reika.DragonAPI.Instantiable.Math.Spline.SplineType;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;


public class BlockPistonTarget extends BlockDimensionStructureTile implements LaserPulseEffect {

	private final IIcon[] icons = new IIcon[3];

	public BlockPistonTarget(Material mat) {
		super(mat);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		switch(meta) {
			case 0:
				return new PistonEmitterTile();
			case 1:
				return new PistonDoorTile();
			default:
				return null;
		}
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return icons[0];
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		icons[0] = ico.registerIcon("chromaticraft:dimstruct/lightpanel");
		icons[1] = ico.registerIcon("chromaticraft:dimstruct/lightpanel_switch_off");
	}

	@Override
	public boolean onRightClicked(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		return true;
	}

	public boolean onImpact(World world, int x, int y, int z, EntityLaserPulse e) {
		if (world.getBlockMetadata(x, y, z) == 0) {
			PistonEmitterTile te = (PistonEmitterTile)world.getTileEntity(x, y, z);
			if (te.getFacing() == e.direction.getCardinal() && te.color.matchColor(e.color)) {
				te.fire();
			}
		}
		return true;
	}

	public boolean receiveSplineParticle(World world, int x, int y, int z, EntityPistonSpline e) {
		if (world.getBlockMetadata(x, y, z) == 1) {
			PistonDoorTile te = (PistonDoorTile)world.getTileEntity(x, y, z);
			if (te.color.matchColor(e.color)) {
				te.receive();
			}
		}
		return true;
	}

	public static class PistonDoorTile extends PistonTargetTile {

		private static final int DURATION = 50;

		private Coordinate door = new Coordinate(0, 0, 0);
		private int active;

		private void receive() {
			this.setActive(true);
			ChromaSounds.CAST.playSoundAtBlock(this);
		}

		private void setActive(boolean active) {
			this.active = active ? DURATION : 0;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);

			this.checkDoor();
		}

		private void checkDoor() {
			boolean open = true;
			for (Coordinate c : this.getAllTiles()) {
				PistonDoorTile te = (PistonDoorTile)c.getTileEntity(worldObj);
				if (!te.isActive()) {
					open = false;
					break;
				}
			}
			BlockChromaDoor.setOpen(worldObj, door.xCoord, door.yCoord, door.zCoord, open);
		}

		private Collection<Coordinate> getAllTiles() {
			ArrayList<Coordinate> li = new ArrayList();
			ForgeDirection left = ReikaDirectionHelper.getLeftBy90(this.getFacing());
			Coordinate root = new Coordinate(xCoord-left.offsetX*this.getSubIndex(), yCoord, zCoord-left.offsetZ*this.getSubIndex());
			for (int i = 0; i < this.getDoorBusWidth(); i++) {
				li.add(root.offset(left, i));
			}
			return li;
		}

		public boolean isActive() {
			return active > 0;
		}

		@Override
		public boolean canUpdate() {
			return true;
		}

		@Override
		public void updateEntity() {
			if (active > 0) {
				active--;
				if (active == 0)
					this.setActive(false);
			}
		}

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			door.writeToNBT("door", NBT);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			door = Coordinate.readFromNBT("door", NBT);
		}

		public void setTarget(Coordinate c) {
			door = c;
		}

	}

	public static class PistonEmitterTile extends PistonTargetTile {

		private int doorIndex;
		private Coordinate target = new Coordinate(0, 0, 0);
		private Spline path;

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			target.writeToNBT("target", NBT);
			NBT.setInteger("door", doorIndex);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			target = Coordinate.readFromNBT("target", NBT);
			doorIndex = NBT.getInteger("door");
		}

		private void fire() {
			if (path == null) {
				ChromaSounds.ERROR.playSoundAtBlock(this);
				return;
			}
			EntityPistonSpline e = new EntityPistonSpline(worldObj, path, color);
			if (!worldObj.isRemote) {
				worldObj.spawnEntityInWorld(e);
			}
		}

		public void setTarget(int door, Coordinate c) {
			doorIndex = door;
			target = c;
			path = new Spline(SplineType.CHORDAL);
			DecimalPosition p1 = new DecimalPosition(this);
			DecimalPosition p2 = new DecimalPosition(c);
			path.addPoint(new BasicSplinePoint(p1));
			int n = 3+doorIndex;
			for (int i = 0; i < n; i++) {
				double f = (i+1D)/(n+1D);
				DecimalPosition p0 = DecimalPosition.interpolate(p1, p2, f);
				double dx = ReikaRandomHelper.getRandomPlusMinus(0, 2D);
				double dy = ReikaRandomHelper.getRandomPlusMinus(0, 1.5D);
				double dz = ReikaRandomHelper.getRandomPlusMinus(0, 2D);
				DecimalPosition p = p0.offset(dx, dy, dz);
				while (!p.getCoordinate().isEmpty(worldObj)) {
					dx = ReikaRandomHelper.getRandomPlusMinus(0, 2D);
					dy = ReikaRandomHelper.getRandomPlusMinus(0, 1.5D);
					dz = ReikaRandomHelper.getRandomPlusMinus(0, 2D);
					p = p0.offset(dx, dy, dz);
				}
				path.addPoint(new BasicSplinePoint(p));
			}
			path.addPoint(new BasicSplinePoint(p2));
		}

	}

	public static abstract class PistonTargetTile extends StructureBlockTile<PistonTapeGenerator> {

		private int doorBusWidth;
		private int doorColorIndex;
		protected ColorData color = new ColorData(true);
		private ForgeDirection facing;

		@Override
		public void writeToNBT(NBTTagCompound NBT) {
			super.writeToNBT(NBT);

			color.writeToNBT(NBT);
			NBT.setInteger("index", doorColorIndex);
			NBT.setInteger("bus", doorBusWidth);
		}

		@Override
		public void readFromNBT(NBTTagCompound NBT) {
			super.readFromNBT(NBT);

			color.readFromNBT(NBT);
			doorColorIndex = NBT.getInteger("index");
			doorBusWidth = NBT.getInteger("bus");
		}

		public ForgeDirection getFacing() {
			return facing;
		}

		public int getSubIndex() {
			return doorColorIndex;
		}

		public int getDoorBusWidth() {
			return doorBusWidth;
		}

		public void setColor(ColorData c) {
			color = c;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

		public void setData(ForgeDirection dir, int idx, int w) {
			doorColorIndex = idx;
			facing = dir;
			doorBusWidth = w;
		}

		@Override
		public DimensionStructureType getType() {
			return DimensionStructureType.PISTONTAPE;
		}

	}

}
