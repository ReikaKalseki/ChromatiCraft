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

import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.Accelerator;
import Reika.ChromatiCraft.API.AcceleratorBlacklist.BlacklistReason;
import Reika.ChromatiCraft.API.CustomAcceleration;
import Reika.ChromatiCraft.Auxiliary.Interfaces.NBTTile;
import Reika.ChromatiCraft.Auxiliary.Interfaces.SneakPop;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Render.Particle.EntitySparkleFX;
import Reika.ChromatiCraft.TileEntity.TileEntityPowerTree;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityAuraInfuser;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingAuto;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityRitualTable;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityAccelerator extends TileEntityChromaticBase implements NBTTile, SneakPop, Accelerator {

	public static final long MAX_LAG = calculateLagLimit();

	public static final int MAX_TIER = 7;

	private int tier;
	private boolean particles = true;
	private int soundtick = 0;

	private static HashMap<Class<? extends TileEntity>, Acceleration> actions = new HashMap();

	private static final Acceleration blacklistKey = new Acceleration() {
		@Override
		public void tick(TileEntity te, int factor) {}
	};

	static {
		blacklistTile("icbm.sentry.turret.Blocks.TileTurret", ModList.ICBM, BlacklistReason.BUGS); //by request
		blacklistTile("bluedart.tile.decor.TileForceTorch", ModList.DARTCRAFT, BlacklistReason.CRASH); //StackOverflow
		blacklistTile("com.sci.torcherino.tile.TileTorcherino", null, BlacklistReason.CRASH); //StackOverflow

		blacklistTile("mrtjp.projectred.integration.Timer", ModList.PROJRED, BlacklistReason.BUGS);
		blacklistTile("mrtjp.projectred.integration.Sequencer", ModList.PROJRED, BlacklistReason.BUGS);
		blacklistTile("mrtjp.projectred.integration.Repeater", ModList.PROJRED, BlacklistReason.BUGS);
		blacklistTile("mrtjp.projectred.integration.StateCell", ModList.PROJRED, BlacklistReason.BUGS);

		blacklistTile(TileEntityCastingTable.class);
		blacklistTile(TileEntityCastingAuto.class);
		blacklistTile(TileEntityRitualTable.class);
		blacklistTile(TileEntityAuraInfuser.class);
		blacklistTile(TileEntityCrystalPylon.class);
		blacklistTile(TileEntityPowerTree.class);
	}

	public static void blacklistTile(Class<? extends TileEntity> cl) {
		actions.put(cl, blacklistKey);
	}

	public static void customizeTile(Class c, Acceleration a) {
		actions.put(c, a);
	}

	private static void blacklistTile(String name, ModList mod, BlacklistReason r) {
		Class cl;
		if (mod != null && !mod.isLoaded())
			return;
		try {
			cl = Class.forName(name);
			ChromatiCraft.logger.log("TileEntity \""+name+"\" has been blacklisted from the TileEntity Accelerator, because "+r.message);
			actions.put(cl, blacklistKey);
		}
		catch (ClassNotFoundException e) {
			ChromatiCraft.logger.logError("Could not add "+name+" to the Accelerator blacklist: Class not found!");
		}
	}

	private static long calculateLagLimit() {
		return Math.max(ChromaOptions.TILELAG.getValue(), 100000L);
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

		if (world.getBlockPowerInput(x, y, z) == 15) {
			soundtick = 0;
			return;
		}

		if (particles && world.isRemote) {
			this.spawnParticles(world, x, y, z, meta);
		}

		soundtick++;

		float f = 1+this.getTier()/(float)MAX_TIER;
		int l = (int)(221/f);
		if (soundtick%l == 0)
			ChromaSounds.DRONE.playSoundAtBlock(world, x, y, z, 0.25F, f);

		long time = System.nanoTime();
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = dirs[i];
			TileEntity te = this.getAdjacentTileEntity(dir);
			Acceleration a = this.getAccelerate(te);
			if (a != blacklistKey) {
				int max = this.getAccel();
				if (a != null) {
					a.tick(te, max);
				}
				else {
					for (int k = 0; k < max; k++) {
						te.updateEntity();
						if (System.nanoTime()-time >= MAX_LAG)
							return;
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles(World world, int x, int y, int z, int meta) {
		int p2 = Minecraft.getMinecraft().gameSettings.particleSetting;
		if (rand.nextInt(1+p2) == 0) {
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
	}

	private Acceleration getAccelerate(TileEntity te) {
		if (te == null)
			return blacklistKey;
		if (te instanceof TileEntityAccelerator)
			return blacklistKey;
		if (!te.canUpdate() || te.isInvalid())
			return blacklistKey;
		Class c = te.getClass();
		Acceleration a = actions.get(c);
		if (a != null)
			return a;
		String s = c.getSimpleName().toLowerCase();
		if (s.contains("conduit") || s.contains("wire") || s.contains("cable")) { //almost always part of a network object
			actions.put(c, blacklistKey);
			return blacklistKey;
		}
		if (s.contains("solar") || s.contains("windmill") || s.contains("watermill")) { //power exploit
			actions.put(c, blacklistKey);
			return blacklistKey;
		}
		if (s.contains("windturbine") || s.contains("wind turbine") || s.contains("windmill") || s.contains("wind mill")) { //power exploit
			actions.put(c, blacklistKey);
			return blacklistKey;
		}
		if (s.contains("watermill") || s.contains("water mill")) { //power exploit
			actions.put(c, blacklistKey);
			return blacklistKey;
		}
		if (te.getClass().getCanonicalName().contains("appeng")) { //AE will crash
			actions.put(c, blacklistKey);
			return blacklistKey;
		}
		return null;
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

	@Override
	public void drop() {
		ItemStack is = this.getTile().getCraftedProduct();
		is.stackTagCompound = new NBTTagCompound();
		this.getTagsToWriteToStack(is.stackTagCompound);
		ReikaItemHelper.dropItem(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5, is);
		this.delete();
	}

	@Override
	public int getAccelerationFactor() {
		return this.getAccel();
	}

	public static abstract class Acceleration {

		protected abstract void tick(TileEntity te, int factor);

	}

	private static final class APIAcceleration extends Acceleration {

		private final CustomAcceleration accel;

		private APIAcceleration(CustomAcceleration acc) {
			accel = acc;
		}

		@Override
		protected void tick(TileEntity te, int factor) {
			accel.tick(factor);
		}

	}

}
