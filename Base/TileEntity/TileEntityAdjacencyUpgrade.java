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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;

import com.google.common.base.Strings;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.API.ChromatiAPI;
import Reika.ChromatiCraft.API.CrystalElementAccessor;
import Reika.ChromatiCraft.API.CrystalElementAccessor.CrystalElementProxy;
import Reika.ChromatiCraft.API.Interfaces.AdjacencyCheckHandler;
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
import Reika.DragonAPI.Instantiable.Data.Maps.NestedMap;
import Reika.DragonAPI.Instantiable.Effects.EntityBlurFX;
import Reika.DragonAPI.Instantiable.GUI.GuiItemDisplay;
import Reika.DragonAPI.Instantiable.GUI.GuiItemDisplay.GuiIconDisplay;
import Reika.DragonAPI.Instantiable.GUI.GuiItemDisplay.GuiStackDisplay;
import Reika.DragonAPI.Interfaces.Registry.TileEnum;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.MultiblockControllerFinder;
import Reika.DragonAPI.ModInteract.DeepInteract.TransvectorHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;



public abstract class TileEntityAdjacencyUpgrade extends TileEntityWirelessPowered implements NBTTile, SneakPop {

	public static final int MAX_TIER = 8;

	private static final NestedMap<CrystalElement, String, AdjacencyEffectDescription> effectMap = new NestedMap();
	private static final EnumMap<CrystalElement, AdjacencyCheckHandlerImpl> adjacencyChecks = new EnumMap(CrystalElement.class);

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

	protected static int getAdjacentUpgrade(TileEntityBase core, CrystalElement color) {
		Integer ret = getAdjacentUpgrades(core).get(color);
		return ret != null ? ret.intValue() : 0;
	}

	protected static int getAdjacentUpgrade(World world, int x, int y, int z, CrystalElement color) {
		Integer ret = getAdjacentUpgrades(world, x, y, z).get(color);
		return ret != null ? ret.intValue() : 0;
	}

	protected static HashMap<CrystalElement, Integer> getAdjacentUpgrades(TileEntityBase core) {
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

	protected static HashMap<CrystalElement, Integer> getAdjacentUpgrades(World world, int x, int y, int z) {
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

	public static AdjacencyCheckHandler createAdjacencyCheckHandler(CrystalElementProxy color, String desc, ItemStack... items) {
		return getOrCreateAdjacencyCheckHandler(CrystalElement.elements[color.ordinal()], desc, items);
	}

	public static AdjacencyCheckHandlerImpl getOrCreateAdjacencyCheckHandler(CrystalElement color, String desc, TileEnum te) {
		return getOrCreateAdjacencyCheckHandler(color, desc, te.getCraftedProduct());
	}

	public static AdjacencyCheckHandlerImpl getOrCreateAdjacencyCheckHandler(CrystalElement color, String desc, ItemStack... items) {
		AdjacencyCheckHandlerImpl check = adjacencyChecks.get(color);
		if (check == null) {
			check = new AdjacencyCheckHandlerImpl(color);
			adjacencyChecks.put(color, check);
		}
		registerEffectDescription(color, desc).addItems(items);
		return check;
	}

	public static AdjacencyEffectDescription registerEffectDescription(CrystalElement color, String desc) {
		return registerEffectDescription(color, desc, 0);
	}

	public static AdjacencyEffectDescription registerEffectDescription(CrystalElement color, String desc, int index) {
		AdjacencyEffectDescription ef = effectMap.get(color, desc);
		if (ef == null) {
			ef = new AdjacencyEffectDescription(color, desc).setOrderIndex(index);
			effectMap.put(color, desc, ef);
		}
		return ef;
	}

	public static final class AdjacencyCheckHandlerImpl implements AdjacencyCheckHandler {

		public final CrystalElement color;

		protected AdjacencyCheckHandlerImpl(CrystalElement e) {
			color = e;
		}

		public int getAdjacentUpgradeTier(World world, int x, int y, int z) {
			Integer get = TileEntityAdjacencyUpgrade.getAdjacentUpgrades(world, x, y, z).get(color);
			return get != null ? get.intValue() : 0;
		}

		public double getFactorSimple(World world, int x, int y, int z) {
			int tier = this.getAdjacentUpgradeTier(world, x, y, z);
			return tier > 0 ? ChromatiAPI.getAPI().adjacency().getFactor(CrystalElementAccessor.getByIndex(color.ordinal()), tier) : 1;
		}

		public int getAdjacentUpgrade(TileEntityBase te) {
			return TileEntityAdjacencyUpgrade.getAdjacentUpgrade(te, color);
		}

		public int getAdjacentUpgrade(World world, int x, int y, int z) {
			return TileEntityAdjacencyUpgrade.getAdjacentUpgrade(world, x, y, z, color);
		}
	}

	public static final class AdjacencyEffectDescription implements Comparable<AdjacencyEffectDescription> {

		public final CrystalElement color;
		public final String description;
		private final ArrayList<GuiItemDisplay> items = new ArrayList();

		private int ordering;

		protected AdjacencyEffectDescription(CrystalElement e, String s) {
			color = e;
			description = s;
		}

		public AdjacencyEffectDescription setOrderIndex(int index) {
			ordering = index;
			return this;
		}

		public AdjacencyEffectDescription addIcons(IIcon... set) {
			for (IIcon is : set)
				items.add(new GuiIconDisplay(is));
			Collections.sort(items);
			return this;
		}

		public AdjacencyEffectDescription addItems(Collection<ItemStack> c) {
			for (ItemStack is : c) {
				if (is.getItem() == null)
					throw new IllegalArgumentException("Null item!");
				items.add(new GuiStackDisplay(is));
			}
			Collections.sort(items);
			return this;
		}

		public AdjacencyEffectDescription addItems(ItemStack... set) {
			for (ItemStack is : set) {
				if (is.getItem() == null)
					throw new IllegalArgumentException("Null item!");
				items.add(new GuiStackDisplay(is));
			}
			Collections.sort(items);
			return this;
		}

		public AdjacencyEffectDescription addDisplays(Collection<GuiItemDisplay> set) {
			for (GuiItemDisplay is : set) {
				if (!is.isEmpty() && !items.contains(is))
					items.add(is);
			}
			Collections.sort(items);
			return this;
		}

		public AdjacencyEffectDescription addDisplays(GuiItemDisplay... set) {
			for (GuiItemDisplay is : set) {
				if (!is.isEmpty() && !items.contains(is))
					items.add(is);
			}
			Collections.sort(items);
			return this;
		}

		public final List<GuiItemDisplay> getRelevantItems() {
			return Collections.unmodifiableList(items);
		}

		@Override
		public String toString() {
			return description+": "+items;
		}

		@Override
		public int compareTo(AdjacencyEffectDescription o) {
			if (o.ordering != ordering)
				return Integer.compare(ordering, o.ordering);
			if (Strings.isNullOrEmpty(description) && Strings.isNullOrEmpty(o.description))
				return 0;
			else if (Strings.isNullOrEmpty(description))
				return -1;
			else if (Strings.isNullOrEmpty(o.description))
				return 1;
			else
				return description.compareToIgnoreCase(o.description);
		}

	}

	protected static abstract class BasicAdjacencyInterface {

		protected abstract Collection<GuiItemDisplay> getRelevantItems();

		public abstract String getDescription();

		@Override
		public String toString() {
			return this.getDescription()+": "+this.getRelevantItems().toString();
		}

	}

	public static Collection<CrystalElement> getSpecificEffectColors() {
		return Collections.unmodifiableCollection(effectMap.keySet());
	}

	public static Collection<AdjacencyEffectDescription> getSpecificEffects(CrystalElement e) {
		Collection<AdjacencyEffectDescription> c = effectMap.getAllValuesIn(e);
		if (c == null)
			return new ArrayList();
		ArrayList ret = new ArrayList(c);
		ret.addAll(c);
		ret.addAll(c);
		Collections.sort((List)ret);
		return ret;
	}

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
