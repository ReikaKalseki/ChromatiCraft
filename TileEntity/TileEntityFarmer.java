/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ComplexAOE;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityRelayPowered;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.ChromatiCraft.TileEntity.Auxiliary.TileEntityFunctionRelay;
import Reika.ChromatiCraft.TileEntity.Auxiliary.TileEntityFunctionRelay.RelayFunctions;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Effects.EntityBlurFX;
import Reika.DragonAPI.Interfaces.Registry.CropType;
import Reika.DragonAPI.Interfaces.Registry.CropType.CropMethods;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaCropHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModRegistry.ModCropList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityFarmer extends TileEntityRelayPowered implements ComplexAOE {

	private static final WeightedRandom<Coordinate>[] coordinateRand = new WeightedRandom[4];

	static {
		for (int i = 0; i < 4; i++) {
			WeightedRandom<Coordinate> wr = new WeightedRandom();
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i+2];
			ForgeDirection left = ReikaDirectionHelper.getLeftBy90(dir);
			for (int r = 0; r < 16; r++) {
				for (int a = -r; a <= r; a++) {
					int dx = r*dir.offsetX+a*left.offsetX;
					int dz = r*dir.offsetZ+a*left.offsetZ;
					double wt = 100;
					if (Math.abs(a) > 2 && Math.abs(a) >= r/2)
						wt -= (Math.abs(a)/3D)*10;
					if (r > 8)
						wt *= 1-((r-8)/8D);
					if (wt > 0)
						wr.addEntry(new Coordinate(dx, 0, dz), wt);
				}
			}
			coordinateRand[i] = wr;
		}
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (!world.isRemote && !this.hasRedstoneSignal() && this.getEnergy(CrystalElement.GREEN) >= 200) {
			int n = this.getNumberAttempts();
			for (int i = 0; i < n; i++) {
				Coordinate c = this.getRandomPosition(world, x, y, z);
				if (c != null && this.operateAt(world, c, true)) {
					this.sendParticles(c);
					break;
				}
			}
		}
	}

	private boolean operateAt(World world, Coordinate c, boolean allowRelays) {
		if (c == null)
			return false;
		Object o = this.getCropOrRelayAt(world, c);
		if (allowRelays && o instanceof TileEntityFunctionRelay) {
			return this.operateAt(world, ((TileEntityFunctionRelay)o).getRandomCoordinate(), false);
		}
		else if (o instanceof CropType) {
			CropType crop = (CropType)o;
			if (crop.isRipe(world, c.xCoord, c.yCoord, c.zCoord)) {
				int fortune = this.getFortune();
				ArrayList<ItemStack> li = crop.getDrops(world, c.xCoord, c.yCoord, c.zCoord, fortune);
				if (fortune < 3) {
					CropMethods.removeOneSeed(crop, li);
				}
				ReikaItemHelper.dropItems(world, c.xCoord+0.5, c.yCoord+0.5, c.zCoord+0.5, li);
				crop.setHarvested(world, c.xCoord, c.yCoord, c.zCoord);
				ReikaSoundHelper.playBreakSound(world, c.xCoord, c.yCoord, c.zCoord, c.getBlock(world));
				this.drainEnergy(CrystalElement.GREEN, 200);
				this.drainEnergy(CrystalElement.PURPLE, 50);
				return true;
			}
		}
		else if (o instanceof Block) {
			Block b = (Block)o;
			int meta = c.getBlockMetadata(world);
			int fortune = this.getFortune();
			ArrayList<ItemStack> li = b.getDrops(world, c.xCoord, c.yCoord, c.zCoord, fortune, meta);
			ReikaItemHelper.dropItems(world, c.xCoord+0.5, c.yCoord+0.5, c.zCoord+0.5, li);
			c.setBlock(world, Blocks.air);
			ReikaSoundHelper.playBreakSound(world, c.xCoord, c.yCoord, c.zCoord, b);
			this.drainEnergy(CrystalElement.GREEN, 200);
			this.drainEnergy(CrystalElement.PURPLE, 50);
			return true;
		}
		return false;
	}

	private int getNumberAttempts() {
		return Math.max(1, this.getEnergy(CrystalElement.GREEN)/2500);
	}

	private void sendParticles(Coordinate c) {
		ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.FARMERHARVEST.ordinal(), this, 48, c.xCoord, c.yCoord, c.zCoord);
	}

	@SideOnly(Side.CLIENT)
	public void doParticles(int tx, int ty, int tz) {
		double v = 0.15;
		double vx = v*(tx-xCoord);
		double vy = v*(ty-yCoord);
		double vz = v*(tz-zCoord);
		EntityBlurFX fx = new EntityCCBlurFX(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5, vx, vy, vz).setColor(0, 192, 0);
		fx.setScale(4).setLife(10).forceIgnoreLimits();
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	private int getFortune() {
		return this.getEnergy(CrystalElement.PURPLE)/1000;
	}

	private Object getCropOrRelayAt(World world, Coordinate c) {
		Block b = c.getBlock(world);
		if ((b == Blocks.cactus || b == Blocks.reeds) && c.offset(0, -1, 0).getBlock(world) == b)
			return b;
		int meta = c.getBlockMetadata(world);
		if (ChromaTiles.getTileFromIDandMetadata(b, meta) == ChromaTiles.FUNCTIONRELAY) {
			TileEntityFunctionRelay te = (TileEntityFunctionRelay)c.getTileEntity(world);
			return te.getFunctionType() == RelayFunctions.HARVEST ? te : null;
		}
		CropType type = ReikaCropHelper.getCrop(b);
		if (type == null)
			type = ModCropList.getModCrop(b, meta);
		return type;
	}

	private Coordinate getRandomPosition(World world, int x, int y, int z) {/*
		ForgeDirection dir = this.getFacing();
		ForgeDirection left = ReikaDirectionHelper.getLeftBy90(dir);
		int r = rand.nextInt(16);
		int sp = ReikaRandomHelper.getRandomPlusMinus(0, r);//r/2
		int dx = x+r*dir.offsetX+sp*left.offsetX;//ReikaRandomHelper.getRandomPlusMinus(x, r);
		int dz = z+r*dir.offsetZ+sp*left.offsetZ;//ReikaRandomHelper.getRandomPlusMinus(z, r);
		int dy = ReikaWorldHelper.findTopBlockBelowY(world, dx, y, dz);//Math.min(y, world.getTopSolidOrLiquidBlock(x, z));
		return new Coordinate(dx, dy, dz);*/
		Coordinate pos = coordinateRand[this.getFacing().ordinal()-2].getRandomEntry();
		int dy = ReikaWorldHelper.findTopBlockBelowY(world, pos.xCoord, y, pos.zCoord);//Math.min(y, world.getTopSolidOrLiquidBlock(x, z));
		return pos.setY(dy);
	}
	/*
	@Override
	public void onPathBroken(CrystalElement e) {

	}

	@Override
	public int getReceiveRange() {
		return 24;
	}

	@Override
	public boolean isConductingElement(CrystalElement e) {
		return e == CrystalElement.GREEN || e == CrystalElement.PURPLE;
	}

	@Override
	public int maxThroughput() {
		return 500;
	}

	@Override
	public boolean canConduct() {
		return true;
	}
	 */
	@Override
	public int getMaxStorage(CrystalElement e) {
		switch(e) {
			case GREEN:
				return 10000;
			case PURPLE:
				return 5000;
			default:
				return 0;
		}
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.FARMER;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}
	/*
	@Override
	public boolean canExtractItem(int slot, ItemStack is, int side) {
		return true;
	}

	@Override
	public int getSizeInventory() {
		return 2;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		return true;
	}*/

	@Override
	protected boolean canReceiveFrom(CrystalElement e, ForgeDirection dir) {
		return this.isAcceptingColor(e);
	}

	@Override
	public ElementTagCompound getRequiredEnergy() {
		ElementTagCompound tag = new ElementTagCompound();
		tag.addTag(CrystalElement.GREEN, this.getMaxStorage(CrystalElement.GREEN)-energy.getValue(CrystalElement.GREEN));
		tag.addTag(CrystalElement.PURPLE, this.getMaxStorage(CrystalElement.PURPLE)-energy.getValue(CrystalElement.PURPLE));
		return tag;
	}

	@Override
	public boolean isAcceptingColor(CrystalElement e) {
		return e == CrystalElement.GREEN || e == CrystalElement.PURPLE;
	}

	public ForgeDirection getFacing() {
		switch(this.getBlockMetadata()) {
			case 0:
				return ForgeDirection.WEST;
			case 1:
				return ForgeDirection.EAST;
			case 2:
				return ForgeDirection.NORTH;
			case 3:
				return ForgeDirection.SOUTH;
			default:
				return ForgeDirection.UNKNOWN;
		}
	}

	@Override
	public Collection<Coordinate> getPossibleRelativePositions() {
		return coordinateRand[3].getValues();
	}

	@Override
	public double getNormalizedWeight(Coordinate c) {
		return coordinateRand[3].getNormalizedWeight(c);
	}

}
