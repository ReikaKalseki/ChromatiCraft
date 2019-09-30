/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dimension.Structure.Laser;

import java.util.List;
import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.BlockDimensionStructureTile;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.DimensionStructureType;
import Reika.ChromatiCraft.Base.TileEntity.StructureBlockTile;
import Reika.ChromatiCraft.Entity.EntityLaserPulse;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.ISBRH.LaserEffectorRenderer;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.World.Dimension.Structure.LaserPuzzleGenerator;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.RGBColorData;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper.CubeDirections;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class BlockLaserEffector extends BlockDimensionStructureTile {

	public static IIcon baseTexture;

	public BlockLaserEffector(Material mat) {
		super(mat);
	}

	@Override
	public boolean onRightClicked(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
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
			case EMITTER:
				return new EmitterTile();
			default:
				return new LaserEffectTile();
		}
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
		if (((LaserEffectTile)iba.getTileEntity(x, y, z)).renderAsFullBlock)
			this.setBlockBounds(0, 0, 0, 1, 1, 1);
		else
			this.setBlockBounds(0, 0, 0, 1, 0.5F, 1);
	}

	@Override
	public int getLightValue(IBlockAccess iba, int x, int y, int z) {
		LaserEffectTile te = (LaserEffectTile)iba.getTileEntity(x, y, z);
		return te instanceof TargetTile && ((TargetTile)te).isTriggered() ? (ModList.COLORLIGHT.isLoaded() ? ReikaColorAPI.getPackedIntForColoredLight(te.getRenderColor(), 15) : 15) : 2;
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

	private static Entity currentCollisionEntity;

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return currentCollisionEntity instanceof EntityPlayer ? super.getCollisionBoundingBoxFromPool(world, x, y, z): null;
	}

	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask, List li, Entity e) {
		currentCollisionEntity = e;
		super.addCollisionBoxesToList(world, x, y, z, mask, li, e);
		currentCollisionEntity = null;
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		baseTexture = ico.registerIcon("chromaticraft:dimstruct/laser_block_base");
		for (LaserEffectType e : LaserEffectType.list) {
			e.frontTexture = ico.registerIcon("chromaticraft:dimstruct/laser_front/"+e.name().toLowerCase(Locale.ENGLISH)+"_back");
			e.frontOverlay = ico.registerIcon("chromaticraft:dimstruct/laser_front/"+e.name().toLowerCase(Locale.ENGLISH)+"_front");
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

		public IIcon frontTexture;
		public IIcon frontOverlay;

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
					te.fireParticle(e.direction, e.color, e.getLevel());
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
					te.fireParticle(e.direction, e.color, e.getLevel());
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
						RGBColorData dat = new RGBColorData(e.color.red && b1, e.color.green && b2, e.color.blue && b3);
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
					if (e.color.red)
						te.fireParticle(e.direction.getRotation(true, 2), new RGBColorData(true, false, false), e.getLevel());
					if (e.color.green)
						te.fireParticle(e.direction, new RGBColorData(false, true, false), e.getLevel());
					if (e.color.blue)
						te.fireParticle(e.direction.getRotation(false, 2), new RGBColorData(false, false, true), e.getLevel());
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
						te.fireParticle(e.direction.getRotation(true), e.color, e.getLevel());
						te.fireParticle(e.direction.getRotation(false), e.color, e.getLevel());
					}
					else if (e.direction == te.facing.getOpposite().getRotation(true) || e.direction == te.facing.getOpposite().getRotation(false)) {
						e.setDirection(te.facing.getOpposite(), true);
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
				else if (!this.isOmniDirectional() && is != null && is.getItem() == Items.apple) {
					te.rotate(false);
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
			if (!this.isOmniDirectional() && !world.isRemote)
				te.rotate(ep.isSneaking());
		}
	}

	public static class EmitterTile extends LaserEffectTile {

		public boolean keepFiring = false;

		@Override
		public void updateEntity() {
			if (keepFiring) {
				if (DragonAPICore.rand.nextInt(4) == 0)
					this.fire();
			}
		}

		@Override
		public boolean canUpdate() {
			return true;
		}

		@Override
		public void readFromNBT(NBTTagCompound tag) {
			super.readFromNBT(tag);
			keepFiring = tag.getBoolean("firing");
		}

		@Override
		public void writeToNBT(NBTTagCompound tag) {
			super.writeToNBT(tag);
			tag.setBoolean("firing", keepFiring);
		}

	}

	public static class TargetTile extends LaserEffectTile {

		private boolean triggered = false;
		public int autoReset = 0;
		private int resetTick;

		public boolean isTriggered() {
			return triggered;
		}

		@Override
		public void updateEntity() {
			if (resetTick > 0) {
				resetTick--;
				if (resetTick == 0) {
					this.reset();
				}
			}
		}

		private void reset() {
			this.trigger(false, false, false);
		}

		@Override
		public boolean canUpdate() {
			return true;
		}

		public void trigger(boolean set, boolean doFX, boolean triggerCompletion) {
			triggered = set;
			if (doFX) {
				if (set) {
					ChromaSounds.CAST.playSoundAtBlock(this);
					if (worldObj.isRemote)
						this.doFXClient();
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
			if (autoReset > 0) {
				resetTick = autoReset;
			}
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

		@SideOnly(Side.CLIENT)
		private void doFXClient() {
			for (int i = 0; i < 32; i++) {
				double x = ReikaRandomHelper.getRandomPlusMinus(xCoord+0.5, 0.75);
				double y = ReikaRandomHelper.getRandomPlusMinus(yCoord+0.5, 0.5);
				double z = ReikaRandomHelper.getRandomPlusMinus(zCoord+0.5, 0.75);
				int l = ReikaRandomHelper.getRandomBetween(8, 30);
				EntityFX fx = new EntityBlurFX(worldObj, x, y, z).setColor(this.getRenderColor()).setLife(l);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}

		@Override
		public void readFromNBT(NBTTagCompound tag) {
			super.readFromNBT(tag);
			triggered = tag.getBoolean("trigger");
			autoReset = tag.getInteger("reset");
		}

		@Override
		public void writeToNBT(NBTTagCompound tag) {
			super.writeToNBT(tag);
			tag.setBoolean("trigger", triggered);
			tag.setInteger("reset", autoReset);
		}

		@Override
		public boolean shouldRenderInPass(int pass) {
			return pass <= 1;
		}

	}

	public static class PrismTile extends LaserEffectTile {

		private RGBColorData nextPulse = RGBColorData.black();
		private int timer;
		private int timerLength = 2; //4 for the complex filter puzzle, as well as maybe complex prisms

		public void addPulse(RGBColorData dat) {
			nextPulse.add(dat);
			timer = timerLength;
		}

		@Override
		public void updateEntity() {
			timer--;
			if (timer == 0 && !nextPulse.isBlack()) {
				this.fireParticle(facing, nextPulse, level);
				nextPulse = RGBColorData.black();
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
		protected RGBColorData color = RGBColorData.white();

		private boolean rotateable = true;
		private boolean fixed = false;
		private int rotateableDifficulty = 0;

		public boolean renderAsFullBlock = false;

		public boolean silent = false;
		public double speedFactor = 1;

		protected String level = "none";

		public static final boolean PARTIAL_ROTATEABILITY = false;

		@Override
		public boolean canUpdate() {
			return false;
		}

		public final void setDirection(CubeDirections dir) {
			facing = dir;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

		public final void setColor(RGBColorData c) {
			color = c;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

		@Override
		public void readFromNBT(NBTTagCompound tag) {
			super.readFromNBT(tag);
			color = RGBColorData.white();
			color.readFromNBT(tag);
			facing = CubeDirections.list[tag.getInteger("dir")];

			rotateable = tag.getBoolean("free");
			fixed = tag.getBoolean("fixed");
			rotateableDifficulty = tag.getInteger("mindiff");

			level = tag.getString("level");

			renderAsFullBlock = tag.getBoolean("fullblock");

			silent = tag.getBoolean("silent");
			speedFactor = tag.getDouble("speed");
		}

		@Override
		public void writeToNBT(NBTTagCompound tag) {
			super.writeToNBT(tag);
			color.writeToNBT(tag);
			tag.setInteger("dir", facing.ordinal());

			tag.setBoolean("free", rotateable);
			tag.setBoolean("fixed", fixed);
			tag.setInteger("mindiff", rotateableDifficulty);

			tag.setString("level", level);

			tag.setBoolean("fullblock", renderAsFullBlock);

			tag.setBoolean("silent", silent);
			tag.setDouble("speed", speedFactor);
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

		public void rotate(boolean rev) {
			if (this.isRotateable() && !this.areLasersInPlay()) {
				facing = facing.getRotation(!rev);
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
			if (rotateableDifficulty > 0) {
				if (ChromaOptions.getStructureDifficulty() < rotateableDifficulty)
					return false;
			}
			return PARTIAL_ROTATEABILITY ? rotateable : !(this instanceof TargetTile) && (this.getBlockMetadata() != LaserEffectType.EMITTER.ordinal() || DragonAPICore.isReikasComputer()) && !fixed;
		}

		public final void fire() {
			if (!worldObj.isRemote) {
				EntityLaserPulse ea = new EntityLaserPulse(worldObj, xCoord, yCoord, zCoord, facing, color, level);
				this.affect(ea);
				worldObj.spawnEntityInWorld(ea);
			}
		}

		protected final void fireParticle(CubeDirections dir, RGBColorData clr, String lvl) {
			if (!worldObj.isRemote) {
				EntityLaserPulse ea = new EntityLaserPulse(worldObj, xCoord, yCoord, zCoord, dir, clr, lvl);
				this.affect(ea);
				worldObj.spawnEntityInWorld(ea);
			}
		}

		protected final void affect(EntityLaserPulse ea) {
			ea.silentImpact = silent;
			ea.setSpeedFactor(speedFactor);
		}

	}

}
