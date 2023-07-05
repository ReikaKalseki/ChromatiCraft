/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Auxiliary;

import java.util.Collection;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Auxiliary.Interfaces.FocusAcceleratable;
import Reika.ChromatiCraft.Auxiliary.Interfaces.NBTTile;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Block.BlockPylonStructure;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Rendering.ColorBlendList;
import Reika.DragonAPI.Interfaces.TileEntity.BreakAction;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class TileEntityFocusCrystal extends TileEntityChromaticBase implements NBTTile, BreakAction {

	private static ColorBlendList turboBlend = new ColorBlendList(70);

	static {
		int n = 3;
		for (int i = 0; i < n; i++)
			turboBlend.addColor(CrystalTier.EXQUISITE.getRenderColor(0));
		turboBlend.addColor(0x7010ff);
		turboBlend.addColor(0x7010ff);
		for (int i = 0; i < n; i++)
			turboBlend.addColor(CrystalTier.EXQUISITE.getRenderColor(0));
	}

	public static enum CrystalTier {
		FLAWED(0.0625F), //total 1.5x
		DEFAULT(0.125F), //total 2x
		REFINED(0.375F), //total 4x
		EXQUISITE(0.875F), //total 8x
		TURBOCHARGED(1.375F), //total 12x
		;

		public final float efficiencyFactor;

		public static final CrystalTier[] tierList = values();

		private CrystalTier(float f) {
			efficiencyFactor = f;
		}

		public String getTextureSuffix() {
			switch(this) {
				case FLAWED:
					return "_cracked";
				case EXQUISITE:
				case TURBOCHARGED:
					return "_sparkle";
				default:
					return "";
			}
		}

		public boolean useOrganizedModel() {
			return this.ordinal() >= REFINED.ordinal();
		}

		public int getRenderColor(float tick) {
			switch(this) {
				case FLAWED:
					return 0x30e040;
				case DEFAULT:
				case REFINED:
					return 0xe06060;
				case EXQUISITE:
					return 0x22aaff;
				case TURBOCHARGED:
					return turboBlend.getColor(tick);
			}
			return 0xffffff;
		}

		public ItemStack getCraftedItem() {
			ItemStack is = ChromaTiles.FOCUSCRYSTAL.getCraftedProduct();
			is.stackTagCompound = new NBTTagCompound();
			is.stackTagCompound.setInteger("tier", this.ordinal());
			return is;
		}

		public String getDisplayName() {
			switch(this) {
				case DEFAULT:
					return "";
				case TURBOCHARGED:
					return EXQUISITE.getDisplayName();
				default:
					return ReikaStringParser.capFirstChar(this.name());
			}
		}

		public boolean isMaxPower() {
			return this.ordinal() >= EXQUISITE.ordinal();
		}

		public int getEffectiveOrdinal() {
			return this == TURBOCHARGED ? EXQUISITE.getEffectiveOrdinal() : this.ordinal();
		}
	}

	public static interface FocusLocation {

		Coordinate relativeLocation();

	}

	private static class SimpleFocusLocation implements FocusLocation {

		public final Coordinate offset;

		private SimpleFocusLocation(FocusAcceleratable src, TileEntityFocusCrystal te) {
			offset = new Coordinate(te).offset(new Coordinate((TileEntity)src).negate());
		}

		@Override
		public Coordinate relativeLocation() {
			return offset;
		}

	}

	public static class FocusConnection {

		public final FocusLocation relativeLocation;
		public final Coordinate target;
		public final Class tileClass;

		private FocusConnection(FocusLocation loc, TileEntity te) {
			relativeLocation = loc;
			target = new Coordinate(te);
			tileClass = te.getClass();
		}

	}

	private FocusConnection connection;
	private CrystalTier tier = CrystalTier.FLAWED;

	public static float getSummedFocusFactorDirect(FocusAcceleratable f, Collection<Coordinate> locations) {
		TileEntity te = (TileEntity)f;
		return getSummedFocusFactorDirect(f, te.worldObj, te.xCoord, te.yCoord, te.zCoord, locations);
	}

	public static float getSummedFocusFactor(FocusAcceleratable f, Collection<FocusLocation> locations) {
		TileEntity te = (TileEntity)f;
		return getSummedFocusFactor(f, te.worldObj, te.xCoord, te.yCoord, te.zCoord, locations);
	}

	public static float getSummedFocusFactorDirect(FocusAcceleratable acc, World world, int x, int y, int z, Collection<Coordinate> locations) {
		float sum = 1;
		for (Coordinate c : locations) {
			c = c.offset(x, y, z);
			if (ChromaTiles.getTile(world, c.xCoord, c.yCoord, c.zCoord) == ChromaTiles.FOCUSCRYSTAL) {
				TileEntityFocusCrystal te = (TileEntityFocusCrystal)c.getTileEntity(world);
				sum += te.getTier().efficiencyFactor;
				te.addConnection(acc, true);
			}
		}
		return sum;
	}

	public static float getSummedFocusFactor(FocusAcceleratable acc, World world, int x, int y, int z, Collection<FocusLocation> locations) {
		float sum = 1;
		for (FocusLocation f : locations) {
			Coordinate c = f.relativeLocation().offset(x, y, z);
			if (ChromaTiles.getTile(world, c.xCoord, c.yCoord, c.zCoord) == ChromaTiles.FOCUSCRYSTAL) {
				TileEntityFocusCrystal te = (TileEntityFocusCrystal)c.getTileEntity(world);
				sum += te.getTier().efficiencyFactor;
				te.addConnection(acc, true);
			}
		}
		return sum;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.FOCUSCRYSTAL;
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		super.onFirstTick(world, x, y, z);
		if (world.getBlock(x, y-1, z) == ChromaBlocks.PYLONSTRUCT.getBlockInstance())
			BlockPylonStructure.triggerAddCheck(world, x, y-1, z);
		//this.scanConnections(world, x, y, z);
	}

	/*
	private void scanConnections(World world, int x, int y, int z) {
		connections.clear();

		for (int i = 0; i < FocusLocation.list.length; i++) {
			FocusLocation loc = FocusLocation.list[i];
			Coordinate c = loc.relativeLocation.negate().offset(x, y, z);
			TileEntity te = c.getTileEntity(world);
			if (te instanceof FocusAcceleratable) {
				connections.add(new FocusConnection(loc, te));
			}
		}
	}
	 */
	public void addConnection(FocusAcceleratable src, boolean sync) {
		connection = new FocusConnection(new SimpleFocusLocation(src, this), (TileEntity)src);
		//ReikaJavaLibrary.pConsole("Add connection"+src);
		if (sync)
			this.syncAllData(true);
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (this.getTier().isMaxPower() && world.isRemote) {
			this.doParticles(world, x, y, z);
		}

		if (connection != null && this.getTicksExisted()%8 == 0) {
			if (this.getConnection().target.getTileEntity(world) instanceof FocusAcceleratable) {

			}
			else {
				connection = null;
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void doParticles(World world, int x, int y, int z) {
		if (rand.nextInt(2+Minecraft.getMinecraft().gameSettings.particleSetting) == 0) {
			double hr = 0.825;
			double vr = 0.375;
			double px = ReikaRandomHelper.getRandomPlusMinus(x+0.5, hr/2);
			double pz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, hr/2);
			double py = ReikaRandomHelper.getRandomPlusMinus(y+0.375, vr/2);
			int l = 6+rand.nextInt(6);
			int c = this.getTier().getRenderColor(this.getTicksExisted()+ReikaRenderHelper.getPartialTickTime()+System.identityHashCode(this));
			EntityFX fx = new EntityCCBlurFX(world, px, py, pz).setIcon(ChromaIcons.FLARE).setLife(l).setColor(c);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	public CrystalTier getTier() {
		return tier;
	}

	public FocusConnection getConnection() {
		return connection;
	}

	@Override
	public void getTagsToWriteToStack(NBTTagCompound NBT) {
		this.writeOwnerData(NBT);
		NBT.setInteger("tier", tier.ordinal());
	}

	@Override
	public void setDataFromItemStackTag(ItemStack is) {
		this.readOwnerData(is);
		if (is != null && is.stackTagCompound != null) {
			tier = CrystalTier.tierList[is.stackTagCompound.getInteger("tier")];
		}
		else {
			tier = CrystalTier.FLAWED;
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		NBT.setInteger("tier", this.getTier().ordinal());
		if (connection != null) {
			connection.target.writeToNBT("connection", NBT);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		tier = CrystalTier.tierList[NBT.getInteger("tier")];
		if (NBT.hasKey("connection") && worldObj != null) {
			Coordinate c = Coordinate.readFromNBT("connection", NBT);
			TileEntity te = c.getTileEntity(worldObj);
			if (te instanceof FocusAcceleratable)
				this.addConnection((FocusAcceleratable)te, false);
		}
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return ReikaAABBHelper.getBlockAABB(this).expand(4, 2, 4);
	}

	@Override
	public void breakBlock() {
		if (worldObj.getBlock(xCoord, yCoord-1, zCoord) == ChromaBlocks.PYLONSTRUCT.getBlockInstance()) {
			BlockPylonStructure.triggerAddCheck(worldObj, xCoord, yCoord-1, zCoord);
		}
		if (connection != null) {
			TileEntity tgt = connection.target.getTileEntity(worldObj);
			if (tgt instanceof FocusAcceleratable) {
				((FocusAcceleratable)tgt).recountFocusCrystals();
			}
		}
	}

	@Override
	public void addTooltipInfo(List li, boolean shift) {

	}

}
