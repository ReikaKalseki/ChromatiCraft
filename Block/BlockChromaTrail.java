/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Block.Decoration.BlockEtherealLight;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Math.Spline;
import Reika.DragonAPI.Instantiable.Math.Spline.BasicVariablePoint;
import Reika.DragonAPI.Instantiable.Math.Spline.SplineType;
import Reika.DragonAPI.Instantiable.ParticleController.ListOfPositionsController;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class BlockChromaTrail extends BlockContainer {

	public BlockChromaTrail(Material mat) {
		super(mat);

		this.setHardness(0);
		this.setResistance(60000);
		this.setCreativeTab(ChromatiCraft.tabChromaDeco);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileChromaTrail();
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return ChromaIcons.TRANSPARENT.getIcon();
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public Item getItemDropped(int meta, Random rand, int fortune) {
		return null;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess iba, int x, int y, int z) {
		float s = 0.125F;
		this.setBlockBounds(0.5F-s, 0.5F-s, 0.5F-s, 0.5F+s, 0.5F+s, 0.5F+s);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		ItemStack is = ep.getCurrentEquippedItem();
		if (ChromaItems.TOOL.matchWith(is)) {
			for (Coordinate loc : ((TileChromaTrail)world.getTileEntity(x, y, z)).getFullPath()) {
				TileEntity te = loc.getTileEntity(world);
				loc.setBlock(world, Blocks.air);
			}
			return true;
		}
		return false;
	}

	public static class TileChromaTrail extends TileEntity {

		private Coordinate nextLocation;
		private Coordinate prevLocation;

		private int pathIndex;
		private List<DecimalPosition> overallPath;

		@Override
		public void updateEntity() {
			if (worldObj.isRemote) {
				this.doParticles(worldObj, xCoord, yCoord, zCoord);
			}
		}

		public void setData(Coordinate prev, Coordinate next) {
			prevLocation = prev;
			nextLocation = next;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

		@SideOnly(Side.CLIENT)
		private void doParticles(World world, int x, int y, int z) {
			int t = (int)(world.getTotalWorldTime()%12);
			if (t < 6) {
				if (overallPath == null)
					overallPath = this.calcSpline();
				//overallPath.update();
				int l = 13;//20;
				//int l2 = l*64;
				//SplineMotionController s = new SplineMotionController(l2, overallPath).setTick(pathIndex*l2/l);
				ListOfPositionsController s = new ListOfPositionsController(l, overallPath);
				float f = 1.5F*((6-t)/6F);
				EntityBlurFX fx = new EntityBlurFX(world, x+0.5, y+0.5, z+0.5).setLife(l*3/2).setScale(f).setColliding();
				fx.setAlphaFading().setRapidExpand().setPositionController(s).setColorController(BlockEtherealLight.colorController);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}

		private List<DecimalPosition> calcSpline() {
			ArrayList<Coordinate> path = this.getFullPath();
			pathIndex = path.indexOf(new Coordinate(this));
			Spline s = new Spline(SplineType.CENTRIPETAL);
			for (Coordinate c : path) {
				s.addPoint(new BasicVariablePoint(new DecimalPosition(c), 0.75, 0.0625));
			}
			List<DecimalPosition> all = s.get(128, false);
			int size = all.size()/path.size();
			int idx = pathIndex*size;
			return all.subList(idx, idx+size);
		}

		private ArrayList<Coordinate> getFullPath() {
			ArrayList<Coordinate> path = new ArrayList();
			path.add(new Coordinate(this));
			if (prevLocation != null) {
				Coordinate c = prevLocation;
				TileEntity te = c.getTileEntity(worldObj);
				while (te instanceof TileChromaTrail) {
					path.add(0, c);
					TileChromaTrail tt = (TileChromaTrail)te;
					if (tt.prevLocation != null) {
						c = tt.prevLocation;
						te = c.getTileEntity(worldObj);
					}
					else {
						te = null;
					}
				}
			}
			if (nextLocation != null) {
				Coordinate c = nextLocation;
				TileEntity te = c.getTileEntity(worldObj);
				while (te instanceof TileChromaTrail) {
					path.add(c);
					TileChromaTrail tt = (TileChromaTrail)te;
					if (tt.nextLocation != null) {
						c = tt.nextLocation;
						te = c.getTileEntity(worldObj);
					}
					else {
						te = null;
					}
				}
			}
			return path;
		}

		@Override
		public void writeToNBT(NBTTagCompound tag) {
			super.writeToNBT(tag);

			if (nextLocation != null)
				nextLocation.writeToNBT("next", tag);
			if (prevLocation != null)
				prevLocation.writeToNBT("prev", tag);
		}

		@Override
		public void readFromNBT(NBTTagCompound tag) {
			super.readFromNBT(tag);

			nextLocation = Coordinate.readFromNBT("next", tag);
			prevLocation = Coordinate.readFromNBT("prev", tag);
		}

		@Override
		public final Packet getDescriptionPacket() {
			NBTTagCompound NBT = new NBTTagCompound();
			this.writeToNBT(NBT);
			S35PacketUpdateTileEntity pack = new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, NBT);
			return pack;
		}

		@Override
		public final void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity p)  {
			this.readFromNBT(p.field_148860_e);
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

	}

}
