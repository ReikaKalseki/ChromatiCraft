/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.AOE;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.AcceleratorBlacklist.BlacklistReason;
import Reika.ChromatiCraft.Auxiliary.Interfaces.NBTTile;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Render.Particle.EntitySparkleFX;
import Reika.ChromatiCraft.TileEntity.TileEntityPowerTree;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityAuraInfuser;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityRitualTable;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityAccelerator extends TileEntityChromaticBase implements NBTTile {

	public static final long MAX_LAG = calculateLagLimit();

	public static final int MAX_TIER = 7;

	private int tier;
	private boolean particles = true;

	private static List<Class<? extends TileEntity>> blacklist = new ArrayList<Class<? extends TileEntity>>();

	static {
		addEntry("icbm.sentry.turret.Blocks.TileTurret", ModList.ICBM, BlacklistReason.BUGS); //by request
		addEntry("bluedart.tile.decor.TileForceTorch", ModList.DARTCRAFT, BlacklistReason.CRASH); //StackOverflow
		addEntry("com.sci.torcherino.tile.TileTorcherino", null, BlacklistReason.CRASH); //StackOverflow

		addEntry(TileEntityCastingTable.class);
		addEntry(TileEntityRitualTable.class);
		addEntry(TileEntityAuraInfuser.class);
		addEntry(TileEntityCrystalPylon.class);
		addEntry(TileEntityPowerTree.class);
	}

	public static void addEntry(Class<? extends TileEntity> cl) {
		blacklist.add(cl);
	}

	private static long calculateLagLimit() {
		return Math.max(ChromaOptions.TILELAG.getValue(), 100000L);
	}

	private static void addEntry(String name, ModList mod, BlacklistReason r) {
		Class cl;
		if (mod != null && !mod.isLoaded())
			return;
		try {
			cl = Class.forName(name);
			ChromatiCraft.logger.log("TileEntity \""+name+"\" has been blacklisted from the TileEntity Accelerator, because "+r.message);
			blacklist.add(cl);
		}
		catch (ClassNotFoundException e) {
			ChromatiCraft.logger.logError("Could not add "+name+" to the Accelerator blacklist: Class not found!");
		}
	}

	public static int getAccelFromTier(int tier) {
		return ReikaMathLibrary.intpow2(2, tier+1)-1;
	}

	public int getAccel() {
		return getAccelFromTier(this.getTier());
	}

	public int getTier() {
		return tier;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (particles && world.isRemote) {
			this.spawnParticles(world, x, y, z, meta);
		}

		long time = System.nanoTime();
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			TileEntity te = this.getAdjacentTileEntity(dir);
			if (this.canAccelerate(te)) {
				int max = this.getAccel();
				for (int k = 0; k < max && !te.isInvalid(); k++) {
					te.updateEntity();
					if (System.nanoTime()-time >= MAX_LAG)
						return;
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles(World world, int x, int y, int z, int meta) {
		int p = this.getTier() > 0 ? (this.getTier()+1)/2 : rand.nextBoolean() ? 1 : 0;
		for (int i = 0; i < p; i++) {
			double dx = rand.nextDouble();
			double dy = rand.nextDouble();
			double dz = rand.nextDouble();
			double v = 0.125;
			double vx = v*(dx-0.5);
			double vy = v*(dy-0.5);
			double vz = v*(dz-0.5);
			//ReikaParticleHelper.FLAME.spawnAt(world, x-0.5+dx*2, y-0.5+dy*2, z-0.5+dz*2, v*(-1+dx*2), v*(-1+dy*2), v*(-1+dz*2));
			//Minecraft.getMinecraft().effectRenderer.addEffect(new EntitySparkleFX(world, x+0.5, y+0.5, z+0.5, vx, vy, vz));
			//Minecraft.getMinecraft().effectRenderer.addEffect(new EntitySparkleFX(world, x+0.5, y+0.5, z+0.5, vx, vy, vz));

			dx = x-2+dx*4;
			dy = y-2+dy*4;
			dz = z-2+dz*4;

			vx = -(dx-x)/8;
			vy = -(dy-y)/8;
			vz = -(dz-z)/8;
			Minecraft.getMinecraft().effectRenderer.addEffect(new EntitySparkleFX(world, dx+0.5, dy+0.5, dz+0.5, vx, vy, vz));
		}
	}

	private boolean canAccelerate(TileEntity te) {
		if (te == null)
			return false;
		if (te instanceof TileEntityAccelerator)
			return false;
		if (!te.canUpdate() || te.isInvalid())
			return false;
		String s = te.getClass().getSimpleName().toLowerCase();
		if (s.contains("conduit") || s.contains("wire") || s.contains("cable")) //almost always part of a network object
			return false;
		if (s.contains("solar") || s.contains("windmill") || s.contains("watermill")) //power exploit
			return false;
		if (s.contains("windturbine") || s.contains("wind turbine") || s.contains("windmill") || s.contains("wind mill")) //power exploit
			return false;
		if (s.contains("watermill") || s.contains("water mill")) //power exploit
			return false;
		if (te.getClass().getCanonicalName().contains("appeng")) //AE will crash
			return false;
		if (blacklist.contains(te.getClass()))
			return false;
		return true;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public int getRedstoneOverride() {
		return 0;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.ACCELERATOR;
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		tier = NBT.getInteger("tier");
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("tier", tier);
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		NBT.setBoolean("particle", particles);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		particles = NBT.getBoolean("particle");
	}

	public void setDataFromItemStackTag(ItemStack is) {
		if (ChromaItems.PLACER.matchWith(is)) {
			if (is.stackTagCompound != null) {
				tier = is.stackTagCompound.getInteger("tier");
			}
		}
	}

	@Override
	public void getTagsToWriteToStack(NBTTagCompound NBT) {
		NBT.setInteger("tier", this.getTier());
	}

}
