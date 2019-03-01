/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dimension.Structure.Pinball;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.BlockDimensionStructureTile;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.DimensionStructureType;
import Reika.ChromatiCraft.Base.TileEntity.StructureBlockTile;
import Reika.ChromatiCraft.Entity.EntityTNTPinball;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.World.Dimension.Structure.PinballGenerator;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;


public class BlockPinballTile extends BlockDimensionStructureTile {

	public BlockPinballTile(Material mat) {
		super(mat);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		PinballRerouteType e = PinballRerouteType.list[world.getBlockMetadata(x, y, z)];
		e.onInteract(world, x, y, z, ep);
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		switch(PinballRerouteType.list[meta]) {
			case TARGET:
				return new PinballTargetTile();
			case FLIPFLOP:
				return new FlipFlopTile();
			case PAD:
				return new TileBouncePad();
			default:
				return new PinballTile();
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
		//PinballEffectorRenderer.renderPass = pass;
		return pass == 0;		//return pass <= 1;
	}

	@Override
	public int getRenderType() {
		return -1;//ChromatiCraft.proxy.PinballeffectRender;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess iba, int x, int y, int z) {
		this.setBlockBounds(0, 0, 0, 1, 1, 1);
	}

	@Override
	public int getLightValue(IBlockAccess iba, int x, int y, int z) {
		PinballTile te = (PinballTile)iba.getTileEntity(x, y, z);
		return 0;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block b) {
		if (DragonAPICore.isReikasComputer() && ReikaObfuscationHelper.isDeObfEnvironment()) {
			if (world.isBlockIndirectlyGettingPowered(x, y, z)) {
				if (world.getBlockMetadata(x, y, z) == PinballRerouteType.EMITTER.ordinal()) {
					PinballTile te = (PinballTile)world.getTileEntity(x, y, z);
					te.fire();
					return;
				}
			}
		}
	}

	public static enum PinballRerouteType {

		EMITTER(),
		TARGET(),
		PAD(),
		FLIPFLOP(),
		BRAKE(),
		BOOSTER();

		public static final PinballRerouteType[] list = values();

		public boolean affectPulse(World world, int x, int y, int z, EntityTNTPinball e) {
			PinballTile te = (PinballTile)world.getTileEntity(x, y, z);
			if (te instanceof PinballTargetTile) {
				if ((e.getDirection() == te.facing || this.isOmniDirectional()))
					((PinballTargetTile)te).trigger(true, true, true);
			}
			switch(this) {
				case EMITTER:
					return true;
				case TARGET:
					return true;
				case BOOSTER:
					e.setSpeed(0.5);
					return false;
				case BRAKE:
					return false;
				case FLIPFLOP:
					FlipFlopTile f = (FlipFlopTile)te;
					e.bounce(f.state);
					f.flip();
					return false;
				case PAD:
					((TileBouncePad)te).onImpact(e);
					e.bounce(true);
					return false;
				default:
					return false;
			}
		}

		public boolean isOmniDirectional() {
			return false;
		}

		public void onInteract(World world, int x, int y, int z, EntityPlayer ep) {
			PinballTile te = (PinballTile)world.getTileEntity(x, y, z);
			if (DragonAPICore.isReikasComputer() && ReikaObfuscationHelper.isDeObfEnvironment()) {
				ItemStack is = ep.getCurrentEquippedItem();
				if (!this.isOmniDirectional() && is != null && is.getItem() == Items.diamond) {
					te.rotateable = !te.rotateable;
					world.markBlockForUpdate(x, y, z);
					return;
				}
				else if (te instanceof PinballTargetTile && is != null && is.getItem() == Items.glowstone_dust) {
					((PinballTargetTile)te).trigger(!((PinballTargetTile)te).isTriggered(), true, false);
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

	public static class TileBouncePad extends PinballTile {

		public int lightTick;

		@Override
		public boolean canUpdate() {
			return true;
		}

		@Override
		public void updateEntity() {
			if (lightTick > 0) {
				lightTick--;
			}
		}

		public void onImpact(EntityTNTPinball e) {
			ChromaSounds.BOUNCE.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord);
			lightTick = 8;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}

		@Override
		public void readFromNBT(NBTTagCompound tag) {
			super.readFromNBT(tag);
			lightTick = tag.getInteger("light");
		}

		@Override
		public void writeToNBT(NBTTagCompound tag) {
			super.writeToNBT(tag);
			tag.setInteger("light", lightTick);
		}

	}

	public static class FlipFlopTile extends PinballTile {

		private boolean state;

		public boolean getState() {
			return state;
		}

		public void flip() {
			state = !state;
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.click", 0.4F, 0.5F);
		}

		@Override
		public void readFromNBT(NBTTagCompound tag) {
			super.readFromNBT(tag);
			state = tag.getBoolean("state");
		}

		@Override
		public void writeToNBT(NBTTagCompound tag) {
			super.writeToNBT(tag);
			tag.setBoolean("state", state);
		}

	}

	public static class PinballTargetTile extends PinballTile {

		private boolean triggered = false;

		public boolean isTriggered() {
			return triggered;
		}

		public void trigger(boolean set, boolean doFX, boolean triggerCompletion) {
			triggered = set;
			if (doFX) {
				/*
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
				 */
			}
			if (triggerCompletion && !worldObj.isRemote) {
				PinballGenerator gen = this.getGenerator();
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

	public static class PinballTile extends StructureBlockTile<PinballGenerator> {

		protected ForgeDirection facing = ForgeDirection.NORTH;

		private boolean rotateable = true;

		protected String level = "none";

		@Override
		public boolean canUpdate() {
			return false;
		}

		@Override
		public void readFromNBT(NBTTagCompound tag) {
			super.readFromNBT(tag);
			facing = ForgeDirection.VALID_DIRECTIONS[tag.getInteger("dir")];

			rotateable = tag.getBoolean("free");

			level = tag.getString("level");
		}

		@Override
		public void writeToNBT(NBTTagCompound tag) {
			super.writeToNBT(tag);
			tag.setInteger("dir", facing.ordinal());

			tag.setBoolean("free", rotateable);

			tag.setString("level", level);
		}

		@Override
		public DimensionStructureType getType() {
			return DimensionStructureType.PINBALL;
		}

		public ForgeDirection getFacing() {
			return facing;
		}

		public void rotate() {
			if (this.isRotateable() && !this.arePinballsInPlay()) {
				facing = ReikaDirectionHelper.getRightBy90(facing);
				ReikaSoundHelper.playSoundAtBlock(worldObj, xCoord, yCoord, zCoord, "random.click", 0.4F, 0.5F);
				worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
		}

		private boolean arePinballsInPlay() {
			PinballGenerator gen = this.getGenerator();
			if (gen == null)
				return false;
			return gen.areBallsInPlay(level);
		}

		public boolean isRotateable() {
			return rotateable;
		}

		public void fire() {
			if (!worldObj.isRemote) {
				EntityTNTPinball ea = new EntityTNTPinball(worldObj, xCoord, yCoord, zCoord, facing, 0.125);
				worldObj.spawnEntityInWorld(ea);
			}
		}

	}

}
