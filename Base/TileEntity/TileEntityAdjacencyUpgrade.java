/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base.TileEntity;

import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Auxiliary.Interfaces.NBTTile;
import Reika.ChromatiCraft.Auxiliary.Interfaces.SneakPop;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntitySparkleFX;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.TileEntityBase;
import Reika.DragonAPI.Instantiable.Effects.EntityBlurFX;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.MultiblockControllerFinder;
import Reika.DragonAPI.ModInteract.DeepInteract.TransvectorHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;



public abstract class TileEntityAdjacencyUpgrade extends TileEntityWirelessPowered implements NBTTile, SneakPop {

	public static final int MAX_TIER = 8;

	private int tier;

	private boolean particles = true;
	private int soundtick = 0;

	public final int getTier() {
		return tier;
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return e == this.getColor() ? 6000+(1+tier)*(1+tier)*1000 : 0;
	}

	@Override
	protected int getReceiveRange(CrystalElement e) {
		return e == this.getColor() ? 16 : 0;
	}

	public final boolean hasSufficientEnergy() {
		return energy.getValue(this.getColor()) >= this.getConsumedEnergy();
	}

	public final int getConsumedEnergy() {
		return ChromaOptions.POWEREDACCEL.getState() ? Math.max(1, ReikaMathLibrary.intpow2(tier+1, 2)/4) : 0;
	}

	@Override
	public final void updateEntity(World world, int x, int y, int z, int meta) {
		if (!this.canRun(world, x, y, z)) {
			soundtick = 0;
			return;
		}

		if (particles && world.isRemote) {
			this.spawnParticles(world, x, y, z);
		}

		soundtick++;

		float f = 1+(1+this.getTier())/(float)MAX_TIER;
		int l = (int)(221/f);
		if (soundtick%l == 0)
			ChromaSounds.DRONE.playSoundAtBlock(world, x, y, z, 0.25F, f);

		if (ChromaOptions.POWEREDACCEL.getState()) {
			CrystalElement e = this.getColor();
			if (this.getTicksExisted()%8 == 0) {
				if (energy.getValue(e) < this.getMaxStorage(e))
					this.requestEnergy(this.getColor(), this.getMaxStorage(e)-energy.getValue(e));
			}

			if (this.hasSufficientEnergy()) {
				if (this.getTicksExisted()%8 == 0)
					energy.subtract(e, this.getConsumedEnergy());
			}
			else
				return;
		}

		if (this.ticksIndividually()) {
			long time = System.nanoTime();
			for (int i = 0; i < 6; i++) {
				ForgeDirection dir = dirs[i];
				EffectResult res = this.tickDirection(world, x, y, z, dir, time);
				if (res.didAction) {
					if (world.isRemote && rand.nextInt(4) == 0)
						this.spawnActionParticles(world, x, y, z, dir);
				}
				if (!res.shouldContinue)
					break;
			}
		}
		else {
			this.doCollectiveTick(world, x, y, z);
		}
	}

	protected final TileEntity getEffectiveTileOnSide(ForgeDirection dir) {
		TileEntity te = this.getAdjacentTileEntity(dir);
		if (ModList.THAUMICTINKER.isLoaded())
			te = TransvectorHandler.getRelayedTile(te);
		if (te != null && MultiblockControllerFinder.instance.isMultiblockTile(te))
			return MultiblockControllerFinder.instance.getController(te);
		return te;
	}

	@SideOnly(Side.CLIENT)
	private void spawnActionParticles(World world, int x, int y, int z, ForgeDirection dir) {
		double o = 0.0625;
		double px = x+dir.offsetX-o+rand.nextDouble()*(1+2*o);
		double py = y+dir.offsetY-o+rand.nextDouble()*(1+2*o);
		double pz = z+dir.offsetZ-o+rand.nextDouble()*(1+2*o);
		EntityBlurFX fx = new EntityCCBlurFX(world, px, py, pz);
		fx.setRapidExpand().setAlphaFading().setLife(ReikaRandomHelper.getRandomBetween(8, 40)).setColor(this.getColor().getColor());
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles(World world, int x, int y, int z) {
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

	protected abstract EffectResult tickDirection(World world, int x, int y, int z, ForgeDirection dir, long startTime);

	public boolean canRun(World world, int x, int y, int z) {
		return !this.hasRedstoneSignal();//world.getBlockPowerInput(x, y, z) < 15;
	}

	protected boolean ticksIndividually() {
		return true;
	}

	protected void doCollectiveTick(World world, int x, int y, int z) {

	}

	public abstract CrystalElement getColor();

	@Override
	public final void setDataFromItemStackTag(ItemStack is) {
		super.setDataFromItemStackTag(is);
		if (ChromaItems.ADJACENCY.matchWith(is)) {
			if (is.stackTagCompound != null) {
				tier = is.stackTagCompound.getInteger("tier");
			}
		}
	}

	@Override
	public final void getTagsToWriteToStack(NBTTagCompound NBT) {
		super.getTagsToWriteToStack(NBT);
		NBT.setInteger("tier", this.getTier());
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		tier = NBT.getInteger("tier");

		energy.readFromNBT("energy", NBT);
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setInteger("tier", tier);
	}

	@Override
	public final void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		NBT.setBoolean("particle", particles);
	}

	@Override
	public final void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		particles = NBT.getBoolean("particle");
	}

	@Override
	public final void drop() {
		ItemStack is = ChromaItems.ADJACENCY.getStackOf(this.getColor());
		is.stackTagCompound = new NBTTagCompound();
		this.getTagsToWriteToStack(is.stackTagCompound);
		ReikaItemHelper.dropItem(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5, is);
		this.delete();
	}

	public final boolean canDrop(EntityPlayer ep) {
		return ep.getUniqueID().equals(placerUUID);
	}

	@Override
	public boolean allowMining(EntityPlayer ep) {
		return true;
	}

	@Override
	public final ChromaTiles getTile() {
		return ChromaTiles.ADJACENCY;
	}

	public static int getAdjacentUpgrade(TileEntityBase core, CrystalElement color) {
		Integer ret = getAdjacentUpgrades(core).get(color);
		return ret != null ? ret.intValue() : 0;
	}

	public static HashMap<CrystalElement, Integer> getAdjacentUpgrades(TileEntityBase core) {
		HashMap<CrystalElement, Integer> set = new HashMap();
		for (int i = 0; i < 6; i++) {
			TileEntity te = core.getAdjacentTileEntity(ForgeDirection.VALID_DIRECTIONS[i]);
			if (te != null && ModList.THAUMICTINKER.isLoaded())
				te = TransvectorHandler.getRelayedTile(te);
			if (te instanceof TileEntityAdjacencyUpgrade) {
				TileEntityAdjacencyUpgrade ta = (TileEntityAdjacencyUpgrade)te;
				if (ta.canRun(ta.worldObj, ta.xCoord, ta.yCoord, ta.zCoord) && (!ChromaOptions.POWEREDACCEL.getState() || ta.energy.containsAtLeast(ta.getColor(), 100))) {
					Integer get = set.get(ta.getColor());
					int has = get != null ? get.intValue() : 0;
					set.put(ta.getColor(), Math.max(1+ta.getTier(), has));
				}
			}
		}
		return set;
	}

	public static HashMap<CrystalElement, Integer> getAdjacentUpgrades(World world, int x, int y, int z) {
		HashMap<CrystalElement, Integer> set = new HashMap();
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			TileEntity te = world.getTileEntity(dx, dy, dz);
			if (te != null && ModList.THAUMICTINKER.isLoaded())
				te = TransvectorHandler.getRelayedTile(te);
			if (te instanceof TileEntityAdjacencyUpgrade) {
				TileEntityAdjacencyUpgrade ta = (TileEntityAdjacencyUpgrade)te;
				if (ta.canRun(ta.worldObj, ta.xCoord, ta.yCoord, ta.zCoord) && (!ChromaOptions.POWEREDACCEL.getState() || ta.energy.containsAtLeast(ta.getColor(), 100))) {
					Integer get = set.get(ta.getColor());
					int has = get != null ? get.intValue() : 0;
					set.put(ta.getColor(), Math.max(1+ta.getTier(), has));
				}
			}
		}
		return set;
	}

	public static void buildTileEffectCache() {/*
		try {
			Field f = TileEntity.class.getDeclaredField("classToNameMap");
			f.setAccessible(true);
			Map<Class<? extends TileEntity>, String> map = (Map<Class<? extends TileEntity>, String>)f.get(null);
			for (CrystalElement e : CrystalElement.elements) {
				TileEntityAdjacencyUpgrade te = AdjacencyUpgrades.upgrades[e.ordinal()].createTEInstanceForRender();
				te.buildTileEffectCache(map.keySet());
			}
		}
		catch (Exception e) {
			throw new RegistrationException(ChromatiCraft.instance, "Could not build Tile Adjacency effect cache!", e);
		}*/
	}

	//protected abstract void buildTileEffectCache(Collection<Class<? extends TileEntity>> c);

	protected static enum EffectResult {
		ACTION(true, true),
		FINAL_ACTION(true, false),
		CONTINUE(false, true),
		STOP(false, false);

		public boolean didAction;
		public boolean shouldContinue;

		private EffectResult(boolean act, boolean con) {
			didAction = act;
			shouldContinue = con;
		}
	}

}
