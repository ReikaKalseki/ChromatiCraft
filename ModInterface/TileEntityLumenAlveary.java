/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.visnet.VisNetHandler;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityRelayPowered;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Auxiliary.Trackers.ReflectiveFailureTracker;
import Reika.DragonAPI.Instantiable.InertItem;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.BeeGene;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Fertility;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Flowering;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Speeds;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Territory;
import Reika.DragonAPI.ModInteract.Bees.ReikaBeeHelper;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeHousingInventory;
import forestry.api.apiculture.IBeeListener;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.core.EnumTemperature;
import forestry.api.core.IClimateControlled;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IIndividual;
import forestry.api.multiblock.IAlvearyComponent;
import forestry.api.multiblock.IAlvearyComponent.BeeListener;
import forestry.api.multiblock.IAlvearyComponent.BeeModifier;
import forestry.api.multiblock.IAlvearyController;
import forestry.api.multiblock.IMultiblockComponent;
import forestry.api.multiblock.IMultiblockController;
import forestry.api.multiblock.IMultiblockLogicAlveary;
import forestry.api.multiblock.MultiblockManager;

//@SmartStrip
@Strippable(value={"forestry.api.multiblock.IAlvearyComponent", "forestry.api.multiblock.IAlvearyComponent$BeeModifier",
		"forestry.api.multiblock.IAlvearyComponent$BeeListener", "forestry.api.apiculture.IBeeModifier", "forestry.api.apiculture.IBeeListener"})
public class TileEntityLumenAlveary extends TileEntityRelayPowered implements IAlvearyComponent, BeeModifier, BeeListener, IBeeModifier, IBeeListener {

	private static final HashSet<AlvearyEffect> effectSet = new HashSet();
	private static final HashSet<AlvearyEffect> continualSet = new HashSet();
	private static final AutomationEffect automation;

	private static Method tickMethod;
	private static Field tempField;
	private static Field humidField;

	private Object logic;

	@ModDependent(ModList.THAUMCRAFT)
	private AspectList aspects;
	private static final int VIS_LIMIT = 200;

	/** Relative to minX,Y,Z */
	private Coordinate relativeLocation;

	private int lightningTicks;
	private String movePrincess;

	private boolean canWork;

	private EntityItem renderItem;

	private boolean multipleBoosters = false;

	static {
		if (ModList.FORESTRY.isLoaded()) {
			try {
				Class c = Class.forName("forestry.core.multiblock.MultiblockControllerBase");
				tickMethod = c.getDeclaredMethod("updateMultiblockEntity");
				tickMethod.setAccessible(true);

				c = Class.forName("forestry.apiculture.multiblock.AlvearyController");
				tempField = c.getDeclaredField("tempChange");
				tempField.setAccessible(true);
				humidField = c.getDeclaredField("humidChange");
				humidField.setAccessible(true);
			}
			catch (Exception e) {
				ChromatiCraft.logger.logError("Could not fetch Alveary tick() method");
				e.printStackTrace();
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.FORESTRY, e);
			}
		}

		new AccelerationEffectI();
		new AccelerationEffectII();
		new AccelerationEffectIII();
		new LightningProductionEffect();
		new HistoryRewriteEffect();
		new ExplorationEffect();
		new GeneticFluxEffect();
		new GeneticStabilityEffect();
		new GeneticImprovementEffect();
		new TemperatureMatchingEffect();
		new HumidityMatchingEffect();
		new GeneticRepairEffectI();
		new GeneticRepairEffectII();
		automation = new AutomationEffect();

		if (ModList.THAUMCRAFT.isLoaded()) {
			new ProductionBoostEffect();
		}
	}

	public static String getEffectsAsString() {
		StringBuilder sb = new StringBuilder();
		for (AlvearyEffect ae : effectSet) {
			if (ae instanceof LumenAlvearyEffect) {
				sb.append(ae.getDescription());
				sb.append(" - ");
				sb.append(((LumenAlvearyEffect)ae).color.displayName);
				sb.append(" (");
				sb.append(((LumenAlvearyEffect)ae).requiredEnergy);
				sb.append(" L/cycle)");
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	public TileEntityLumenAlveary() {
		if (ModList.THAUMCRAFT.isLoaded()) {
			aspects = new AspectList();
		}
		if (ModList.FORESTRY.isLoaded()) {
			logic = MultiblockManager.logicFactory.createAlvearyLogic();
		}
	}

	@Override
	protected boolean makeRequests() {
		return this.isAlvearyComplete();
	}

	@Override
	protected boolean canReceiveFrom(CrystalElement e, ForgeDirection dir) {
		return this.isAlvearyComplete() && this.isSideOpen(dir) && this.isAcceptingColor(e);
	}

	private boolean isSideOpen(ForgeDirection dir) {
		switch(dir) {
			case DOWN:
				return relativeLocation.yCoord == 0;
			case UP:
				return relativeLocation.yCoord == 2;
			case WEST:
				return relativeLocation.xCoord == 0;
			case EAST:
				return relativeLocation.xCoord == 2;
			case NORTH:
				return relativeLocation.zCoord == 0;
			case SOUTH:
				return relativeLocation.zCoord == 2;
			default:
				return false;
		}
	}

	public Coordinate getRelativeLocation() {
		return relativeLocation;
	}

	public boolean isAlvearyComplete() {
		return relativeLocation != null && !multipleBoosters;
	}

	public boolean hasMultipleBoosters() {
		return multipleBoosters;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (ModList.FORESTRY.isLoaded()) {

			if (multipleBoosters) {
				//for (IMultiblockComponent com : this.getMultiblockLogic().getController().getComponents()) {
				//	com.onMachineBroken();
				//}
			}

			if (this.isAlvearyComplete()) {
				if (this.getTicksExisted()%4 == 0)
					canWork = this.calcCanWork();

				if (world.isRemote) {
					this.doParticles(world, x, y, z);
				}
				if (this.hasQueen()) {
					if (this.canQueenWork()) {
						for (AlvearyEffect ae : effectSet) {
							if (ae.isActive(this)) {
								if (ae.tickRate() == 1 || this.getTicksExisted()%ae.tickRate() == 0)
									ae.tick(this);
							}

							if (ModList.THAUMCRAFT.isLoaded()) {
								AspectList cost = new AspectList();
								if (ae instanceof VisAlvearyEffect) {
									VisAlvearyEffect vae = (VisAlvearyEffect)ae;
									cost.add(vae.aspect, 1);
								}
								for (Aspect a : cost.aspects.keySet()) {
									if (aspects.getAmount(a) < VIS_LIMIT)
										aspects.add(a, VisNetHandler.drainVis(world, x, y, z, a, cost.getAmount(a)));
								}
							}
						}
					}
					else {
						for (AlvearyEffect ae : continualSet) {
							if (ae.isActive(this)) {
								if (ae.tickRate() == 1 || this.getTicksExisted()%ae.tickRate() == 0)
									ae.tick(this);
							}
						}
					}
				}
				else if (movePrincess != null && !movePrincess.isEmpty()) {
					if (energy.containsAtLeast(automation.color, automation.requiredEnergy)) {
						this.cycleBees(movePrincess);
						movePrincess = null;
						this.drainEnergy(automation.color, automation.requiredEnergy);
					}
				}
			}
			else {
				canWork = false;
			}
		}
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		super.onFirstTick(world, x, y, z);
		this.updateRenderItem();
	}

	@SideOnly(Side.CLIENT)
	private void doParticles(World world, int x, int y, int z) {
		int n = 2+2*Minecraft.getMinecraft().gameSettings.particleSetting;
		if (rand.nextInt(n) == 0) {
			double o = 0.03125;
			int side = rand.nextInt(2);
			double dx = side == 0 ? rand.nextDouble()*3 : rand.nextBoolean() ? 0-o : 3+o;
			double dy = rand.nextDouble()*3.5;
			double dz = side == 1 ? rand.nextDouble()*3 : rand.nextBoolean() ? 0-o : 3+o;
			double px = x-relativeLocation.xCoord+dx;
			double py = y-relativeLocation.yCoord+dy;
			double pz = z-relativeLocation.zCoord+dz;
			int l = ReikaRandomHelper.getRandomBetween(5, 20);
			int c2 = 0xffffff;
			int c1 = 0xa07740;
			float s = 1.5F*(float)ReikaRandomHelper.getRandomPlusMinus(1, 0.25);
			EntityFX fx1 = new EntityBlurFX(world, px, py, pz).setLife(l).setColor(c1).setIcon(ChromaIcons.HEXFLARE2).setScale(s);
			EntityFX fx2 = new EntityBlurFX(world, px, py, pz).setLife(l).setColor(c2).setIcon(ChromaIcons.HEXFLARE2).setScale(s/2F);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx1);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx2);
		}
	}

	@Override
	protected ElementTagCompound getRequiredEnergy() {
		ElementTagCompound tag = new ElementTagCompound();
		if (!this.isAlvearyComplete())
			return tag;
		for (AlvearyEffect ae : effectSet) {
			if (ae instanceof LumenAlvearyEffect) {
				LumenAlvearyEffect lae = (LumenAlvearyEffect)ae;
				tag.addValueToColor(lae.color, lae.requiredEnergy*10);
			}
		}
		return tag;
	}

	@Override
	public boolean isAcceptingColor(CrystalElement e) {
		return this.getRequiredEnergy().contains(e);
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return 15000;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.ALVEARY;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public ChunkCoordinates getCoordinates() {
		return new Coordinate(this).asChunkCoordinates();
	}

	@Override
	public GameProfile getOwner() {
		return placerUUID != null ? new GameProfile(placerUUID, this.getPlacerName()) : null;
	}

	@Override
	@ModDependent(ModList.FORESTRY)
	public void onMachineAssembled(IMultiblockController controller, ChunkCoordinates minCoord, ChunkCoordinates maxCoord) {
		this.validateStructure(controller);
		this.triggerBlockUpdate();
		this.syncAllData(true);
		relativeLocation = new Coordinate(this).offset(new Coordinate(minCoord).negate());
		this.updateRenderItem();
	}

	@ModDependent(ModList.FORESTRY)
	private void validateStructure(IMultiblockController controller) {
		boolean flag = false;
		multipleBoosters = false;
		for (IMultiblockComponent com : controller.getComponents()) {
			if (com instanceof TileEntityLumenAlveary && com != this) {
				multipleBoosters = true;
				break;
			}
		}
	}

	@Override
	@ModDependent(ModList.FORESTRY)
	public void onMachineBroken() {
		ReikaWorldHelper.causeAdjacentUpdates(worldObj, xCoord, yCoord, zCoord);
		this.triggerBlockUpdate();
		this.syncAllData(true);
		relativeLocation = null;
		this.updateRenderItem();
	}

	@Override
	@ModDependent(ModList.FORESTRY)
	public IBeeListener getBeeListener() {
		return this;
	}

	@Override
	@ModDependent(ModList.FORESTRY)
	public IBeeModifier getBeeModifier() {
		return this;
	}

	@Override
	@ModDependent(ModList.FORESTRY)
	public void wearOutEquipment(int amount) {
		for (AlvearyEffect ae : effectSet) {
			ae.consumeEnergy(this, amount);
			ae.onProductionTick(this);
		}
		this.updateRenderItem();
	}

	@Override
	@ModDependent(ModList.FORESTRY)
	public void onQueenDeath() {
		movePrincess = this.getSpecies().getUID();
	}

	private void cycleBees(String species) {
		IBeeHousing ibh = this.getBeeHousing();
		IBeeHousingInventory ibhi = ibh.getBeeInventory();
		ISidedInventory inv = (ISidedInventory)ibhi;
		ItemStack drone = ibhi.getDrone();
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack in = inv.getStackInSlot(i);
			if (in != null) {
				boolean flag = false;
				for (int s = 0; s < 6; s++) {
					if (inv.canExtractItem(i, in, s)) {
						flag = true;
					}
				}
				if (flag) {
					EnumBeeType type = ReikaBeeHelper.getBeeRoot().getType(in);
					IAlleleBeeSpecies sp = (IAlleleBeeSpecies)ReikaBeeHelper.getSpecies(in);
					if (type == EnumBeeType.PRINCESS && ibhi.getQueen() == null && sp.getUID().equals(species)) {
						ibhi.setQueen(in);
						inv.setInventorySlotContents(i, null);
					}
					else if (type == EnumBeeType.DRONE && ReikaItemHelper.areStacksCombinable(drone, in, inv.getInventoryStackLimit())) {
						int amt = Math.min(drone.getMaxStackSize()-drone.stackSize, in.stackSize);
						drone.stackSize += amt;
						ibhi.setDrone(drone);
						in.stackSize -= amt;
						if (in.stackSize == 0)
							inv.setInventorySlotContents(i, null);
					}
					else if (type == EnumBeeType.DRONE && drone == null && sp.getUID().equals(species)) {
						ibhi.setDrone(in);
						inv.setInventorySlotContents(i, null);
					}
				}
			}
		}
	}

	@Override
	@ModDependent(ModList.FORESTRY)
	public boolean onPollenRetrieved(IIndividual pollen) {
		return false;
	}

	@Override
	@ModDependent(ModList.FORESTRY)
	public float getTerritoryModifier(IBeeGenome genome, float current) {
		float f = 1;
		for (AlvearyEffect ae : effectSet) {
			if (ae.isActive(this)) {
				f *= ae.territoryFactor(this);
			}
		}
		return f;
	}

	@Override
	@ModDependent(ModList.FORESTRY)
	public float getMutationModifier(IBeeGenome genome, IBeeGenome mate, float current) {
		float f = 1;
		for (AlvearyEffect ae : effectSet) {
			if (ae.isActive(this)) {
				f *= ae.mutationFactor(this);
			}
		}
		return f;
	}

	@Override
	@ModDependent(ModList.FORESTRY)
	public float getLifespanModifier(IBeeGenome genome, IBeeGenome mate, float current) {
		float f = 1;
		for (AlvearyEffect ae : effectSet) {
			if (ae.isActive(this)) {
				f *= ae.lifespanFactor(this);
			}
		}
		return f;
	}

	@Override
	@ModDependent(ModList.FORESTRY)
	public float getProductionModifier(IBeeGenome genome, float current) {
		float f = 1;
		for (AlvearyEffect ae : effectSet) {
			if (ae.isActive(this)) {
				f *= ae.productionFactor(this);
			}
		}
		return f;
	}

	@Override
	@ModDependent(ModList.FORESTRY)
	public float getFloweringModifier(IBeeGenome genome, float current) {
		float f = 1;
		for (AlvearyEffect ae : effectSet) {
			if (ae.isActive(this)) {
				f *= ae.pollinationFactor(this);
			}
		}
		return f;
	}

	@Override
	@ModDependent(ModList.FORESTRY)
	public float getGeneticDecay(IBeeGenome genome, float current) {
		float f = 1;
		for (AlvearyEffect ae : effectSet) {
			if (ae.isActive(this)) {
				f *= ae.decayFactor(this);
			}
		}
		return f;
	}

	@Override
	@ModDependent(ModList.FORESTRY)
	public boolean isSealed() {
		for (AlvearyEffect ae : effectSet) {
			if (ae.isActive(this)) {
				if (ae.isSealed(this))
					return true;
			}
		}
		return false;
	}

	@Override
	@ModDependent(ModList.FORESTRY)
	public boolean isSelfLighted() {
		for (AlvearyEffect ae : effectSet) {
			if (ae.isActive(this)) {
				if (ae.isSelfLit(this))
					return true;
			}
		}
		return false;
	}

	@Override
	@ModDependent(ModList.FORESTRY)
	public boolean isSunlightSimulated() {
		for (AlvearyEffect ae : effectSet) {
			if (ae.isActive(this)) {
				if (ae.isSkySimulated(this))
					return true;
			}
		}
		return false;
	}

	@Override
	public boolean isHellish() {
		for (AlvearyEffect ae : effectSet) {
			if (ae.isActive(this)) {
				if (ae.isHellish(this))
					return true;
			}
		}
		return false;
	}

	@Override
	@ModDependent(ModList.FORESTRY)
	public IMultiblockLogicAlveary getMultiblockLogic() {
		return (IMultiblockLogicAlveary)logic;
	}

	@ModDependent(ModList.FORESTRY)
	private void tickAlveary() {
		IAlvearyController iac = this.getMultiblockLogic().getController();
		try {
			tickMethod.invoke(iac);
		}
		catch (Exception e) {
			e.printStackTrace();
			this.writeError(e);
		}
	}

	@ModDependent(ModList.FORESTRY)
	private IBeeHousing getBeeHousing() {
		return this.getMultiblockLogic().getController();
	}

	@ModDependent(ModList.FORESTRY)
	private IBeeGenome getBeeGenome() {
		ItemStack is = this.getQueenItem();
		return is != null ? (IBeeGenome)ReikaBeeHelper.getGenome(is) : null;
	}

	public boolean hasQueen() {
		return ModList.FORESTRY.isLoaded() && this.getQueenItem() != null;
	}

	@ModDependent(ModList.FORESTRY)
	public ItemStack getQueenItem() {
		IBeeHousing ibh = this.getMultiblockLogic().getController();
		if (ibh == null)
			return null;
		ItemStack is = ibh.getBeeInventory().getQueen();
		return is != null ? is.copy() : null;
	}

	@ModDependent(ModList.FORESTRY)
	public boolean canQueenWork() {
		return canWork;
	}

	@ModDependent(ModList.FORESTRY)
	private boolean calcCanWork() {
		return this.isAlvearyComplete() && this.getMultiblockLogic().getController().getBeekeepingLogic().canWork();
	}

	@ModDependent(ModList.FORESTRY)
	private IAlleleBeeSpecies getSpecies() {
		return this.hasQueen() ? this.getBeeGenome().getPrimary() : null;
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		if (ModList.FORESTRY.isLoaded())
			this.getMultiblockLogic().readFromNBT(data);

		movePrincess = data.getString("move");

		if (ModList.THAUMCRAFT.isLoaded()) {
			aspects.readFromNBT(data);
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		if (ModList.FORESTRY.isLoaded())
			this.getMultiblockLogic().writeToNBT(data);

		if (movePrincess != null && !movePrincess.isEmpty())
			data.setString("move", movePrincess);

		if (ModList.THAUMCRAFT.isLoaded()) {
			aspects.writeToNBT(data);
		}
	}

	@Override
	public final void validate() {
		super.validate();
		if (ModList.FORESTRY.isLoaded())
			this.getMultiblockLogic().validate(worldObj, this);
	}

	@Override
	protected void onInvalidateOrUnload(World world, int x, int y, int z, boolean invalid) {
		super.onInvalidateOrUnload(world, x, y, z, invalid);
		if (ModList.FORESTRY.isLoaded()) {
			if (invalid) {
				this.getMultiblockLogic().invalidate(world, this);
			}
			else {
				this.getMultiblockLogic().onChunkUnload(world, this);
			}
		}
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(this);
		if (this.isAlvearyComplete()) {
			Coordinate c = new Coordinate(this).offset(relativeLocation.negate());
			Coordinate c2 = c.offset(3, 6, 3);
			return AxisAlignedBB.getBoundingBox(c.xCoord, c.yCoord, c.zCoord, c2.xCoord, c2.yCoord, c2.zCoord);
		}
		return box;
	}

	@SideOnly(Side.CLIENT)
	public EntityItem getRenderItem() {
		return renderItem;
	}

	@Override
	public void markDirty() {
		super.markDirty();
		this.updateRenderItem();
	}

	private void updateRenderItem() {
		renderItem = this.isAlvearyComplete() && this.hasQueen() ? new InertItem(worldObj, this.getQueenItem()) : null;
	}

	private static abstract class AlvearyEffect {

		protected AlvearyEffect() {
			effectSet.add(this);
			if (this.worksWhenBeesDoNot()) {
				continualSet.add(this);
			}
		}

		public abstract String getDescription();

		protected abstract void consumeEnergy(TileEntityLumenAlveary te, int amount);

		protected abstract boolean isActive(TileEntityLumenAlveary te);

		protected void tick(TileEntityLumenAlveary te) {

		}

		protected int tickRate() {
			return 1;
		}

		protected void onProductionTick(TileEntityLumenAlveary te) {

		}

		protected boolean worksWhenBeesDoNot() {
			return false;
		}

		protected float productionFactor(TileEntityLumenAlveary te) {
			return 1;
		}

		protected float pollinationFactor(TileEntityLumenAlveary te) {
			return 1;
		}

		protected float lifespanFactor(TileEntityLumenAlveary te) {
			return 1;
		}

		protected float mutationFactor(TileEntityLumenAlveary te) {
			return 1;
		}

		protected float territoryFactor(TileEntityLumenAlveary te) {
			return 1;
		}

		protected float decayFactor(TileEntityLumenAlveary te) {
			return 1;
		}

		protected boolean isSealed(TileEntityLumenAlveary te) {
			return false;
		}

		protected boolean isSelfLit(TileEntityLumenAlveary te) {
			return false;
		}

		protected boolean isSkySimulated(TileEntityLumenAlveary te) {
			return false;
		}

		protected boolean isHellish(TileEntityLumenAlveary te) {
			return false;
		}
	}

	private static abstract class VisAlvearyEffect extends AlvearyEffect {

		public final Aspect aspect;
		public final int requiredVis;

		protected VisAlvearyEffect(Aspect a, int amt) {
			aspect = a;
			requiredVis = amt;
		}

		protected boolean consumeOnTick() {
			return true;
		}

		@Override
		protected final boolean isActive(TileEntityLumenAlveary te) {
			return te.aspects.getAmount(aspect) >= requiredVis;
		}

		@Override
		protected final void consumeEnergy(TileEntityLumenAlveary te, int amount) {
			int amt = amount*requiredVis;
			//ReikaJavaLibrary.pConsole(amount+" x "+requiredVis+" = "+amt+", from "+te.aspects.getAmount(aspect));
			te.aspects.reduce(aspect, requiredVis);
		}

	}

	private static class ProductionBoostEffect extends VisAlvearyEffect {

		private ProductionBoostEffect() {
			super(Aspect.ORDER, 20);
		}

		@Override
		protected float productionFactor(TileEntityLumenAlveary te) {
			return 2;
		}

		@Override
		public String getDescription() {
			return "Production Boost";
		}

	}

	private static abstract class LumenAlvearyEffect extends AlvearyEffect {

		public final CrystalElement color;
		public final int requiredEnergy;

		protected LumenAlvearyEffect(CrystalElement e, int amt) {
			color = e;
			requiredEnergy = amt;
		}

		protected boolean consumeOnTick() {
			return true;
		}

		@Override
		protected final boolean isActive(TileEntityLumenAlveary te) {
			return te.energy.containsAtLeast(color, requiredEnergy);
		}

		@Override
		protected final void consumeEnergy(TileEntityLumenAlveary te, int amount) {
			int amt = amount*requiredEnergy;
			if (te.energy.containsAtLeast(color, amt)) {
				te.drainEnergy(color, amt);
			}
		}

	}

	private static class AutomationEffect extends LumenAlvearyEffect {


		private AutomationEffect() {
			super(CrystalElement.GREEN, 100);
		}

		@Override
		protected boolean consumeOnTick() {
			return false;
		}

		@Override
		public String getDescription() {
			return "Genetic Recycling";
		}

		@Override
		protected int tickRate() {
			return 4;
		}
	}

	private static abstract class AccelerationEffect extends LumenAlvearyEffect {

		private final int tickRate;

		private AccelerationEffect(int ticks, int cost) {
			super(CrystalElement.LIGHTBLUE, cost);
			tickRate = ticks;
		}

		@Override
		protected void tick(TileEntityLumenAlveary te) {
			for (int i = 0; i < tickRate; i++)
				te.tickAlveary();
		}

		@Override
		public final String getDescription() {
			return "Acceleration x"+(tickRate+1);
		}


	}

	private static class AccelerationEffectI extends AccelerationEffect {

		private AccelerationEffectI() {
			super(1, 80);
		}

	}

	private static class AccelerationEffectII extends AccelerationEffect {

		private AccelerationEffectII() {
			super(3, 480);
		}

	}

	private static class AccelerationEffectIII extends AccelerationEffect {

		private AccelerationEffectIII() {
			super(7, 1600);
		}

	}

	private static class LightningProductionEffect extends LumenAlvearyEffect {

		private LightningProductionEffect() {
			super(CrystalElement.YELLOW, 100);
		}

		@Override
		protected void tick(TileEntityLumenAlveary te) {
			if (te.getBeeHousing().canBlockSeeTheSky() && te.getBeeHousing().getBiome().canSpawnLightningBolt() && te.worldObj.isThundering()) {
				if (te.worldObj.weatherEffects.size() > 0)
					te.lightningTicks = 15;
			}

			if (te.lightningTicks > 0) {
				int n = 1+te.lightningTicks/4;
				for (int i = 0; i < n; i++)
					ReikaBeeHelper.runProductionCycle(te.getBeeHousing());
				te.lightningTicks--;
			}
		}

		@Override
		public String getDescription() {
			return "Lightning Production";
		}

	}

	private static class HistoryRewriteEffect extends LumenAlvearyEffect {

		private static final double mateRewriteChance = 0.01;

		private HistoryRewriteEffect() {
			super(CrystalElement.LIGHTGRAY, 40);
		}

		@Override
		protected void tick(TileEntityLumenAlveary te) {
			if (ReikaRandomHelper.doWithChance(mateRewriteChance)) {
				ItemStack queen = te.getBeeHousing().getBeeInventory().getQueen();
				if (queen != null) {
					IIndividual ii = AlleleManager.alleleRegistry.getIndividual(queen);
					if (ii instanceof IBee) {
						ReikaBeeHelper.setBeeMate((IBee)ii, (IBee)ii);
					}
				}
			}
		}

		@Override
		public String getDescription() {
			return "History Rewrite";
		}

	}

	private static class GeneticBalancingEffect extends LumenAlvearyEffect {

		private static final double pristineConversionChance = 0.0005;
		private static final double geneBalancingChance = 0.005;

		private GeneticBalancingEffect() {
			super(CrystalElement.WHITE, 80);
		}

		@Override
		protected void tick(TileEntityLumenAlveary te) {
			IBeeHousing ibh = te.getBeeHousing();
			IBeeGenome ibg = te.getBeeGenome();
			if (ibg == null)
				return;
			if (ReikaRandomHelper.doWithChance(pristineConversionChance)) {
				ItemStack queen = ibh.getBeeInventory().getQueen();
				if (queen != null && AlleleManager.alleleRegistry.getIndividual(queen) instanceof IBee) {
					ReikaBeeHelper.setPristine(queen, true);
				}
			}
			if (ReikaRandomHelper.doWithChance(geneBalancingChance)) {
				ItemStack queen = ibh.getBeeInventory().getQueen();
				if (queen != null && AlleleManager.alleleRegistry.getIndividual(queen) instanceof IBee) {
					EnumBeeChromosome gene = EnumBeeChromosome.values()[rand.nextInt(EnumBeeChromosome.values().length)];
					if (this.canBalance(queen, ibg, gene)) {
						this.balanceGene(queen, ibg, gene);
					}
				}
			}
		}

		@ModDependent(ModList.FORESTRY)
		private void balanceGene(ItemStack queen, IBeeGenome ibg, EnumBeeChromosome gene) {
			ReikaBeeHelper.setGene(queen, ibg, gene, ibg.getActiveAllele(gene), true);
		}

		@ModDependent(ModList.FORESTRY)
		private boolean canBalance(ItemStack queen, IBeeGenome ibg, EnumBeeChromosome gene) {
			if (gene == EnumBeeChromosome.HUMIDITY)
				return false;
			IAllele primary = ibg.getActiveAllele(gene);
			IAllele secondary = ibg.getInactiveAllele(gene);
			return !primary.getUID().equals(secondary.getUID());
		}

		@Override
		public String getDescription() {
			return "Genetic Balancing";
		}

	}

	private static class GeneticStabilityEffect extends LumenAlvearyEffect {

		private GeneticStabilityEffect() {
			super(CrystalElement.WHITE, 20);
		}

		@Override
		protected float mutationFactor(TileEntityLumenAlveary te) {
			return 0;
		}

		@Override
		protected float decayFactor(TileEntityLumenAlveary te) {
			return 0.75F;
		}

		@Override
		public String getDescription() {
			return "Genetic Stability";
		}

	}

	private static class GeneticFluxEffect extends LumenAlvearyEffect {

		private GeneticFluxEffect() {
			super(CrystalElement.BLACK, 60);
		}

		@Override
		protected float mutationFactor(TileEntityLumenAlveary te) {
			return 4;
		}

		@Override
		public String getDescription() {
			return "Genetic Flux";
		}

	}

	private static class ExplorationEffect extends LumenAlvearyEffect {

		private ExplorationEffect() {
			super(CrystalElement.LIME, 120);
		}

		@Override
		protected float territoryFactor(TileEntityLumenAlveary te) {
			return 1.5F;
		}

		@Override
		public String getDescription() {
			return "Exploration";
		}

	}

	private static class GeneticImprovementEffect extends LumenAlvearyEffect {

		private static final double geneImprovementChance = 0.002;

		private GeneticImprovementEffect() {
			super(CrystalElement.BLACK, 120);
		}

		@Override
		protected void tick(TileEntityLumenAlveary te) {
			if (ReikaRandomHelper.doWithChance(geneImprovementChance)) {
				ItemStack queen = te.getBeeHousing().getBeeInventory().getQueen();
				if (queen != null && AlleleManager.alleleRegistry.getIndividual(queen) instanceof IBee) {
					IBeeGenome ibg = te.getBeeGenome();
					if (ibg == null)
						return;
					EnumBeeChromosome gene = EnumBeeChromosome.values()[rand.nextInt(EnumBeeChromosome.values().length)];
					if (this.canImprove(gene, ibg)) {
						this.improveGene(gene, ibg, queen);
					}
				}
			}
		}

		@ModDependent(ModList.FORESTRY)
		private void improveGene(EnumBeeChromosome gene, IBeeGenome ibg, ItemStack queen) {
			switch(gene) {
				case FERTILITY:
				case FLOWERING:
				case TERRITORY:
				case SPEED:
					BeeGene g = ReikaBeeHelper.getGeneEnum(gene, ibg);
					if (g == null)
						break;
					g = g.oneBetter();
					if (g == null)
						break;
					ReikaBeeHelper.setGene(queen, ibg, gene, g.getAllele(), true);
					break;
				case TEMPERATURE_TOLERANCE: {
					if (ibg.getToleranceTemp() == EnumTolerance.NONE)
						break;
					EnumTolerance next = ReikaBeeHelper.getOneBetterTolerance(ibg.getToleranceTemp());
					if (next == null)
						break;
					ReikaBeeHelper.setGene(queen, ibg, gene, ReikaBeeHelper.getToleranceGene(next), true);
					break;
				}
				case HUMIDITY_TOLERANCE: {
					if (ibg.getToleranceHumid() == EnumTolerance.NONE)
						break;
					EnumTolerance next = ReikaBeeHelper.getOneBetterTolerance(ibg.getToleranceHumid());
					if (next == null)
						break;
					ReikaBeeHelper.setGene(queen, ibg, gene, ReikaBeeHelper.getToleranceGene(next), true);
					break;
				}
				default:
					break;
			}
		}

		@ModDependent(ModList.FORESTRY)
		private boolean canImprove(EnumBeeChromosome gene, IBeeGenome ibg) {
			switch(gene) {
				case FERTILITY:
					return ibg.getFertility() < Fertility.MAXIMUM.getAllele().getValue();
				case FLOWERING:
					return ibg.getFlowering() < Flowering.FASTEST.getAllele().getValue();
				case HUMIDITY_TOLERANCE:
					return ReikaBeeHelper.getToleranceValue(ibg.getToleranceHumid()) > 0 && ReikaBeeHelper.getToleranceValue(ibg.getToleranceHumid()) < 2;
				case SPEED:
					return ibg.getSpeed() < Speeds.FASTEST.getAllele().getValue();
				case TEMPERATURE_TOLERANCE:
					return ReikaBeeHelper.getToleranceValue(ibg.getToleranceTemp()) > 0 && ReikaBeeHelper.getToleranceValue(ibg.getToleranceTemp()) < 2;
				case TERRITORY:
					return ibg.getTerritory()[0] < Territory.LARGEST.getAllele().getValue()[0];
				case CAVE_DWELLING:
				case EFFECT:
				case FLOWER_PROVIDER:
				case HUMIDITY:
				case LIFESPAN:
				case NOCTURNAL:
				case SPECIES:
				case TOLERANT_FLYER:
				default:
					return false;
			}
		}

		@Override
		public String getDescription() {
			return "Genetic Improvement";
		}

	}

	private static class TemperatureMatchingEffect extends LumenAlvearyEffect {

		private TemperatureMatchingEffect() {
			super(CrystalElement.ORANGE, 80);
		}

		@Override
		protected int tickRate() {
			return 2;
		}

		@Override
		public String getDescription() {
			return "Dynamic Temperature";
		}

		@Override
		protected void tick(TileEntityLumenAlveary te) {
			IAlvearyController ac = te.getMultiblockLogic().getController();
			IClimateControlled cc = (IClimateControlled)ac;
			IAlleleBeeSpecies queen = te.getSpecies();
			if (queen == null)
				return;
			ChunkCoordinates loc = ac.getCoordinates();
			float cur = /*ReikaBeeHelper.getTemperatureRangeCenter(EnumTemperature.getFromBiome(ac.getBiome(), loc.posX, loc.posY, loc.posZ));*/
					ac.getBiome().getFloatTemperature(loc.posX, loc.posY, loc.posZ);
			float pref = ReikaBeeHelper.getTemperatureRangeCenter(queen.getTemperature());
			/*
			if (cur < pref) {
				cc.addTemperatureChange(1F, 0, pref-cur);
			}
			else if (cur > pref) {
				cc.addTemperatureChange(-1F, pref-cur, 0);
			}
			 */
			if (pref != cur) {
				try {
					tempField.set(ac, pref-cur);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		protected boolean isHellish(TileEntityLumenAlveary te) {
			return te.getSpecies() != null && te.getSpecies().getTemperature() == EnumTemperature.HELLISH;
		}

		@Override
		protected boolean worksWhenBeesDoNot() {
			return true;
		}

	}

	private static class HumidityMatchingEffect extends LumenAlvearyEffect {

		private HumidityMatchingEffect() {
			super(CrystalElement.CYAN, 80);
		}

		@Override
		protected int tickRate() {
			return 2;
		}

		@Override
		public String getDescription() {
			return "Dynamic Humidity";
		}

		@Override
		protected void tick(TileEntityLumenAlveary te) {
			IAlvearyController ac = te.getMultiblockLogic().getController();
			IClimateControlled cc = (IClimateControlled)ac;
			IAlleleBeeSpecies queen = te.getSpecies();
			if (queen == null)
				return;
			ChunkCoordinates loc = ac.getCoordinates();
			float cur = /*ReikaBeeHelper.getHumidityRangeCenter(EnumHumidity.getFromValue(*/ac.getBiome().rainfall/*))*/;
			float pref = ReikaBeeHelper.getHumidityRangeCenter(queen.getHumidity());
			/*
			if (cur < pref) {
				cc.addHumidityChange(1F, 0, pref-cur);
			}
			else if (cur > pref) {
				cc.addHumidityChange(-1F, pref-cur, 0);
			}
			 */
			if (pref != cur) {
				try {
					humidField.set(ac, pref-cur);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		protected boolean worksWhenBeesDoNot() {
			return true;
		}

	}

	private static abstract class GeneticRepairEffect extends LumenAlvearyEffect {

		private GeneticRepairEffect(int cost) {
			super(CrystalElement.MAGENTA, cost);
		}

		@Override
		public final String getDescription() {
			return "Genetic Repair";
		}

		@Override
		protected final void onProductionTick(TileEntityLumenAlveary te) {
			if (this.doRepair(te)) {
				ItemStack is = te.getQueenItem();
				ReikaBeeHelper.setPristine(is, true);
				te.getBeeHousing().getBeeInventory().setQueen(is);
			}
		}

		protected abstract boolean doRepair(TileEntityLumenAlveary te);

	}

	private static class GeneticRepairEffectI extends GeneticRepairEffect {

		private GeneticRepairEffectI() {
			super(100);
		}

		@Override
		protected boolean doRepair(TileEntityLumenAlveary te) {
			return te.rand.nextInt(40) == 0;
		}
	}

	private static class GeneticRepairEffectII extends GeneticRepairEffect {

		private GeneticRepairEffectII() {
			super(2400);
		}

		@Override
		protected boolean doRepair(TileEntityLumenAlveary te) {
			return true;
		}
	}

}
