/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dimension.Structure;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.DimensionStructureType;
import Reika.ChromatiCraft.Base.TileEntity.StructureBlockTile;
import Reika.ChromatiCraft.Entity.EntityLaserPulse;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.ISBRH.LaserEffectorRenderer;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.World.Dimension.Structure.LaserPuzzleGenerator;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper.CubeDirections;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;


public class BlockLaserEffector extends BlockContainer {

	public BlockLaserEffector(Material mat) {
		super(mat);

		this.setResistance(60000);
		this.setBlockUnbreakable();
		this.setCreativeTab(ChromatiCraft.tabChromaGen);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		LaserEffectType e = LaserEffectType.list[world.getBlockMetadata(x, y, z)];
		e.onInteract(world, x, y, z, ep);
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		switch(LaserEffectType.list[meta]) {
			case PRISM:
				return new PrismTile();
			case TARGET:
			case TARGET_THRU:
				return new TargetTile();
			default:
				return new LaserEffectTile();
		}
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
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
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	public boolean canRenderInPass(int pass) {
		LaserEffectorRenderer.renderPass = pass;
		return pass <= 1;
	}

	@Override
	public int getRenderType() {
		return ChromatiCraft.proxy.lasereffectRender;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess iba, int x, int y, int z) {
		this.setBlockBounds(0, 0, 0, 1, 0.5F, 1);
	}

	@Override
	public int getLightValue(IBlockAccess iba, int x, int y, int z) {
		LaserEffectTile te = (LaserEffectTile)iba.getTileEntity(x, y, z);
		return te instanceof TargetTile && ((TargetTile)te).isTriggered() ? (ModList.COLORLIGHT.isLoaded() ? ReikaColorAPI.getPackedIntForColoredLight(te.getRenderColor(), 15) : 15) : 0;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block b) {
		if (DragonAPICore.isReikasComputer() && ReikaObfuscationHelper.isDeObfEnvironment()) {
			if (world.isBlockIndirectlyGettingPowered(x, y, z)) {
				if (world.getBlockMetadata(x, y, z) == LaserEffectType.EMITTER.ordinal()) {
					LaserEffectTile te = (LaserEffectTile)world.getTileEntity(x, y, z);
					te.fire();
					return;
				}
			}
		}
	}

	public static enum LaserEffectType {
		EMITTER(),
		TARGET(),
		TARGET_THRU(),
		MIRROR(),
		DOUBLEMIRROR(),
		SLITMIRROR(),
		REFRACTOR(),
		ONEWAY(),
		SPLITTER(),
		PRISM(),
		COLORIZER(),
		POLARIZER();

		public static final LaserEffectType[] list = values();

		public boolean affectPulse(World world, int x, int y, int z, EntityLaserPulse e) {
			LaserEffectTile te = (LaserEffectTile)world.getTileEntity(x, y, z);
			if (te instanceof TargetTile) {
				if (e.color.matchColor(te.color) && (e.direction == te.facing || this.isOmniDirectional()))
					((TargetTile)te).trigger(true, true, true);
			}
			switch(this) {
				case COLORIZER:
					e.color.intersect(te.color);
					return e.color.isBlack();
				case EMITTER:
					return true;
				case MIRROR: {
					if (!ReikaMathLibrary.isValueInsideBoundsIncl(3, 5, Math.abs(e.direction.ordinal()-te.facing.ordinal())))
						return true;
					//ReikaJavaLibrary.pConsole(e.direction+" & "+te.facing+" ("+Math.abs(e.direction.ordinal()-te.facing.ordinal())+")");
					e.reflect(te.facing);
					EntityLaserPulse eb = new EntityLaserPulse(world, x, y, z, e.direction, e.color, e.getLevel());
					if (!world.isRemote)
						world.spawnEntityInWorld(eb);
					return true;
					//return false;
				}
				case DOUBLEMIRROR: {
					CubeDirections dir1 = e.direction.getRotation(true, 2);
					if (dir1 == te.facing || dir1 == te.facing.getOpposite())
						return true;
					CubeDirections dir = te.facing;
					if (!ReikaMathLibrary.isValueInsideBoundsIncl(2, 6, Math.abs(e.direction.ordinal()-te.facing.ordinal())))
						dir = dir.getOpposite();
					//ReikaJavaLibrary.pConsole(e.direction+" & "+te.facing+" > "+dir+" ("+Math.abs(e.direction.ordinal()-te.facing.ordinal())+")");
					e.reflect(dir);
					EntityLaserPulse eb = new EntityLaserPulse(world, x, y, z, e.direction, e.color, e.getLevel());
					if (!world.isRemote)
						world.spawnEntityInWorld(eb);
					return true;
					//return false;
				}
				case SLITMIRROR: {
					CubeDirections dir = e.direction.getRotation(true, 2);
					//ReikaJavaLibrary.pConsole(e.direction+" & "+te.facing+" > "+dir+" ("+(dir != te.facing)+" & "+(dir != te.facing.getOpposite())+")");
					if (dir != te.facing && dir != te.facing.getOpposite()) {
						return DOUBLEMIRROR.affectPulse(world, x, y, z, e);
					}
					return false;
				}
				case ONEWAY:
					return e.direction != te.facing;
				case POLARIZER:
					return e.direction != te.facing && e.direction != te.facing.getOpposite();
				case PRISM: {
					if (e.direction != te.facing.getOpposite()) {
						boolean b1 = e.direction == te.facing.getRotation(true, 2);
						boolean b2 = e.direction == te.facing;
						boolean b3 = e.direction == te.facing.getRotation(false, 2);
						ColorData dat = new ColorData(e.color.red && b1, e.color.green && b2, e.color.blue && b3);
						//ReikaJavaLibrary.pConsole(dat.red+":"+dat.green+":"+dat.blue+" & "+b1+","+b2+","+b3);
						/*
						if (!dat.isBlack()) {
							EntityLaserPulse eb = new EntityLaserPulse(world, x, y, z, te.facing.getOpposite(), dat);
							if (!world.isRemote) {
								world.spawnEntityInWorld(eb);
							}
						}
						 */
						((PrismTile)te).addPulse(dat);
						return true;
					}
					EntityLaserPulse e1 = e.color.red ? new EntityLaserPulse(world, x, y, z, e.direction.getRotation(true, 2), new ColorData(true, false, false), e.getLevel()) : null;
					EntityLaserPulse e2 = e.color.green ? new EntityLaserPulse(world, x, y, z, e.direction, new ColorData(false, true, false), e.getLevel()) : null;
					EntityLaserPulse e3 = e.color.blue ? new EntityLaserPulse(world, x, y, z, e.direction.getRotation(false, 2), new ColorData(false, false, true), e.getLevel()) : null;
					if (!world.isRemote) {
						if (e1 != null)
							world.spawnEntityInWorld(e1);
						if (e2 != null)
							world.spawnEntityInWorld(e2);
						if (e3 != null)
							world.spawnEntityInWorld(e3);
					}
					return true;
				}
				case REFRACTOR:
					int d = (int)MathHelper.wrapAngleTo180_double(e.direction.angle-te.facing.angle);
					if (d == 90) {
						e.refract(false);
					}
					else if (d == -45) {
						e.refract(true);
					}
					else {
						return true;
					}
					return false;
				case SPLITTER: {
					if (e.direction == te.facing) {
						EntityLaserPulse e1 = new EntityLaserPulse(world, x, y, z, e.direction.getRotation(true), e.color, e.getLevel());
						EntityLaserPulse e2 = new EntityLaserPulse(world, x, y, z, e.direction.getRotation(false), e.color, e.getLevel());
						if (!world.isRemote) {
							world.spawnEntityInWorld(e1);
							world.spawnEntityInWorld(e2);
						}
					}
					else if (e.direction == te.facing.getOpposite().getRotation(true) || e.direction == te.facing.getOpposite().getRotation(false)) {
						e.setDirection(te.facing.getOpposite());
						return false;
					}
					return true;
				}
				case TARGET:
					return true;
				case TARGET_THRU:
					return false;
				default:
					return false;
			}
		}

		public boolean isOmniDirectional() {
			return this == TARGET_THRU || this == COLORIZER;
		}

		public void onInteract(World world, int x, int y, int z, EntityPlayer ep) {
			LaserEffectTile te = (LaserEffectTile)world.getTileEntity(x, y, z);
			if (DragonAPICore.isReikasComputer() && ReikaObfuscationHelper.isDeObfEnvironment()) {
				ItemStack is = ep.getCurrentEquippedItem();
				if (ChromaItems.SHARD.matchWith(is)) {
					int dmg = is.getItemDamage()%16;
					if (dmg == CrystalElement.RED.ordinal()) {
						te.color.red = !te.color.red;
					}
					else if (dmg == CrystalElement.GREEN.ordinal()) {
						te.color.green = !te.color.green;
					}
					else if (dmg == CrystalElement.BLUE.ordinal()) {
						te.color.blue = !te.color.blue;
					}
					world.markBlockForUpdate(x, y, z);
					return;
				}
				else if (!this.isOmniDirectional() && is != null && is.getItem() == Items.diamond) {
					te.rotateable = !te.rotateable;
					world.markBlockForUpdate(x, y, z);
					return;
				}
				else if (te instanceof TargetTile && is != null && is.getItem() == Items.glowstone_dust) {
					((TargetTile)te).trigger(!((TargetTile)te).isTriggered(), true, false);
					return;
				}
				else if (te instanceof PrismTile && is != null && is.getItem() == Items.emerald) {
					((PrismTile)te).timer++;
					return;
				}
				if (this == EMITTER && !ep.isSneaking()) {
					te.fire();
					return;
				}
			}
			if (!this.isOmniDirectional())
				te.rotate();
		}
	}

	public static class TargetTile extends LaserEffectTile {

		private boolean triggered = false;

		public boolean isTriggered() {
			return triggered;
		}

		public void trigger(boolean set, boolean doFX, boolean triggerCompletion) {
			triggered = set;
			if (doFX) {
				if (set) {
					ChromaSounds.CAST.playSoundAtBlock(this);
					for (int i = 0; i < 32; i++) {
						double x = ReikaRandomHelper.getRandomPlusMinus(xCoord+0.5, 0.75);
						double y = ReikaRandomHelper.getRandomPlusMinus(yCoord+0.5, 0.5);
						double z = ReikaRandomHelper.getRandomPlusMinus(zCoord+0.5, 0.75);
						int l = ReikaRandomHelper.getRandomBetween(8, 30);
						EntityFX fx = new EntityBlurFX(worldObj, x, y, z).setColor(this.getRenderColor()).setLife(l);
						Minecraft.getMinecraft().effectRenderer.addEffect(fx);
					}
				}
				else {
					ChromaSounds.ERROR.playSoundAtBlock(this);
				}
			}
			if (triggerCompletion && !worldObj.isRemote) {
				LaserPuzzleGenerator gen = this.getGenerator();
				if (gen != null) {
					gen.completeTrigger(level, worldObj, new Coordinate(this), set);
				}
			}
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

		@Override
		public void readFromNBT(NBTTagCompound tag) {
			super.readFromNBT(tag);
			triggered = tag.getBoolean("trigger");
		}

		@Override
		public void writeToNBT(NBTTagCompound tag) {
			super.writeToNBT(tag);
			tag.setBoolean("trigger", triggered);
		}

	}

	public static class PrismTile extends LaserEffectTile {

		private ColorData nextPulse = new ColorData(false);
		private int timer;
		private int timerLength = 2; //4 for the complex filter puzzle, as well as maybe complex prisms

		public void addPulse(ColorData dat) {
			nextPulse.add(dat);
			timer = timerLength;
		}

		@Override
		public void updateEntity() {
			timer--;
			if (timer == 0 && !nextPulse.isBlack()) {
				EntityLaserPulse e = new EntityLaserPulse(worldObj, xCoord, yCoord, zCoord, facing, nextPulse, level);
				if (!worldObj.isRemote) {
					worldObj.spawnEntityInWorld(e);
				}
				nextPulse = new ColorData(false);
			}
		}

		@Override
		public boolean canUpdate() {
			return true;
		}

		@Override
		public void readFromNBT(NBTTagCompound tag) {
			super.readFromNBT(tag);
			timerLength = tag.getInteger("timer");
		}

		@Override
		public void writeToNBT(NBTTagCompound tag) {
			super.writeToNBT(tag);
			tag.setInteger("timer", timerLength);
		}

	}

	public static class LaserEffectTile extends StructureBlockTile<LaserPuzzleGenerator> {

		protected CubeDirections facing = CubeDirections.NORTH;
		protected ColorData color = new ColorData(true);

		private boolean rotateable = true;

		protected String level = "none";

		public static final boolean PARTIAL_ROTATEABILITY = false;

		@Override
		public boolean canUpdate() {
			return false;
		}

		@Override
		public void readFromNBT(NBTTagCompound tag) {
			super.readFromNBT(tag);
			color = new ColorData(true);
			color.readFromNBT(tag);
			facing = CubeDirections.list[tag.getInteger("dir")];

			rotateable = tag.getBoolean("free");

			level = tag.getString("level");
		}

		@Override
		public void writeToNBT(NBTTagCompound tag) {
			super.writeToNBT(tag);
			color.writeToNBT(tag);
			tag.setInteger("dir", facing.ordinal());

			tag.setBoolean("free", rotateable);

			tag.setString("level", level);
		}

		@Override
		public DimensionStructureType getType() {
			return DimensionStructureType.LASER;
		}

		public CubeDirections getFacing() {
			return facing;
		}

		public int getRenderColor() {
			return color.getRenderColor();
		}

		public void rotate() {
			if (this.isRotateable() && !this.areLasersInPlay()) {
				facing = facing.getRotation(true);
				ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.click", 0.4F, 0.5F);
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		}

		private boolean areLasersInPlay() {
			LaserPuzzleGenerator gen = this.getGenerator();
			if (gen == null)
				return false;
			return gen.areLasersInPlay(level);
		}

		public boolean isRotateable() {
			return PARTIAL_ROTATEABILITY ? rotateable : !(this instanceof TargetTile) && this.getBlockMetadata() != LaserEffectType.EMITTER.ordinal();
		}

		public void fire() {
			if (!worldObj.isRemote) {
				EntityLaserPulse ea = new EntityLaserPulse(worldObj, xCoord, yCoord, zCoord, facing, color, level);
				worldObj.spawnEntityInWorld(ea);
			}
		}

	}

	public static class ColorData {

		public boolean red;
		public boolean green;
		public boolean blue;

		public ColorData(boolean on) {
			this(on, on, on);
		}

		public ColorData(boolean red, boolean green, boolean blue) {
			this.red = red;
			this.green = green;
			this.blue = blue;
		}

		public void intersect(ColorData dat) {
			red = red && dat.red;
			green = green && dat.green;
			blue = blue && dat.blue;
		}

		public void add(ColorData dat) {
			red = red || dat.red;
			green = green || dat.green;
			blue = blue || dat.blue;
		}

		public boolean isBlack() {
			return !red && !green && !blue;
		}

		public boolean matchColor(ColorData o) {
			return o.red == red && o.green == green && o.blue == blue;
		}

		public int getRenderColor() {
			return ReikaColorAPI.RGBtoHex(red ? 255 : 0, green ? 255 : 0, blue ? 255 : 0);
		}

		public void readFromNBT(NBTTagCompound tag) {
			red = tag.getBoolean("red");
			green = tag.getBoolean("green");
			blue = tag.getBoolean("blue");
		}

		public void writeToNBT(NBTTagCompound tag) {
			tag.setBoolean("red", red);
			tag.setBoolean("green", green);
			tag.setBoolean("blue", blue);
		}

		public void writeBuf(ByteBuf data) {
			data.writeBoolean(red);
			data.writeBoolean(green);
			data.writeBoolean(blue);
		}

		public void readBuf(ByteBuf data) {
			red = data.readBoolean();
			green = data.readBoolean();
			blue = data.readBoolean();
		}

		@Override
		public boolean equals(Object o) {
			return o instanceof ColorData && this.matchColor((ColorData)o);
		}

		@Override
		public int hashCode() {
			return this.getRenderColor();
		}

		public ColorData copy() {
			return new ColorData(red, green, blue);
		}

		@Override
		public String toString() {
			return red+"/"+green+"/"+blue+" : "+Integer.toHexString(this.getRenderColor()&0xffffff);
		}

	}

}
