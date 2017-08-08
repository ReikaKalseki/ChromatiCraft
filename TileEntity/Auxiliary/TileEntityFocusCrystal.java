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
import java.util.Collections;
import java.util.HashSet;

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
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class TileEntityFocusCrystal extends TileEntityChromaticBase implements NBTTile {

	public static enum CrystalTier {
		FLAWED(0.0625F),
		DEFAULT(0.125F),
		REFINED(0.375F),
		EXQUISITE(0.875F);

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
					return "_sparkle";
				default:
					return "";
			}
		}

		public boolean useOrganizedModel() {
			return this == REFINED || this == EXQUISITE;
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
			return this == DEFAULT ? "" : ReikaStringParser.capFirstChar(this.name());
		}
	}

	public static enum FocusLocation {

		N1(-1, 1, -3),
		N2(1, 1, -3),
		E1(3, 1, -1),
		E2(3, 1, 1),
		S1(1, 1, 3),
		S2(-1, 1, 3),
		W1(-3, 1, 1),
		W2(-3, 1, -1);

		public final Coordinate relativeLocation;

		public static final FocusLocation[] list = values();

		private FocusLocation(int x, int y, int z) {
			relativeLocation = new Coordinate(x, y, z);
		}

	}

	public static class FocusConnection {

		public final FocusLocation location;
		public final Coordinate target;
		public final Class tileClass;

		private FocusConnection(FocusLocation loc, TileEntity te) {
			location = loc;
			target = new Coordinate(te);
			tileClass = te.getClass();
		}

	}

	private final HashSet<FocusConnection> connections = new HashSet();
	private CrystalTier tier = CrystalTier.FLAWED;

	public static float getSummedFocusFactor(TileEntity te) {
		return getSummedFocusFactor(te.worldObj, te.xCoord, te.yCoord, te.zCoord);
	}

	public static float getSummedFocusFactor(World world, int x, int y, int z) {
		float sum = 1;
		for (int i = 0; i < FocusLocation.list.length; i++) {
			Coordinate c = FocusLocation.list[i].relativeLocation.offset(x, y, z);
			if (ChromaTiles.getTile(world, c.xCoord, c.yCoord, c.zCoord) == ChromaTiles.FOCUSCRYSTAL) {
				TileEntityFocusCrystal te = (TileEntityFocusCrystal)c.getTileEntity(world);
				if (te.canFunction())
					sum += te.getTier().efficiencyFactor;
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

		this.scanConnections(world, x, y, z);
	}

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

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (this.getTier() == CrystalTier.EXQUISITE && world.isRemote) {
			this.doParticles(world, x, y, z);
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
			int c = this.getTier().getRenderColor(this.getTicksExisted());
			EntityFX fx = new EntityBlurFX(world, px, py, pz).setIcon(ChromaIcons.FLARE).setLife(l).setColor(c);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	public boolean canFunction() {
		return true;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	public CrystalTier getTier() {
		return tier;
	}

	public Collection<FocusConnection> getConnections() {
		return Collections.unmodifiableCollection(connections);
	}

	@Override
	public void getTagsToWriteToStack(NBTTagCompound NBT) {
		NBT.setInteger("tier", tier.ordinal());
	}

	@Override
	public void setDataFromItemStackTag(ItemStack is) {
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
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		tier = CrystalTier.tierList[NBT.getInteger("tier")];
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return ReikaAABBHelper.getBlockAABB(this).expand(4, 2, 4);
	}

}
