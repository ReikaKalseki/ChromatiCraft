/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface.Bees;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

import com.google.common.base.Strings;
import com.mojang.authlib.GameProfile;

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

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityRelayPowered;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.ModInterface.Bees.ChromaBeeHelpers.ConditionalProductBee;
import Reika.ChromatiCraft.ModInterface.Bees.EffectAlleles.CrystalEffect;
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
import Reika.DragonAPI.Interfaces.TileEntity.CopyableSettings;
import Reika.DragonAPI.Interfaces.TileEntity.GuiController;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.BeeGene;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Fertility;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Flowering;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Life;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Speeds;
import Reika.DragonAPI.ModInteract.Bees.BeeAlleleRegistry.Territory;
import Reika.DragonAPI.ModInteract.Bees.DummyEffectData;
import Reika.DragonAPI.ModInteract.Bees.ReikaBeeHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.ForestryMultiblockControllerHandling;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaThaumHelper;

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
import forestry.api.apiculture.IBeekeepingLogic;
import forestry.api.core.EnumTemperature;
import forestry.api.core.IClimateControlled;
import forestry.api.core.INBTTagable;
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
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.visnet.VisNetHandler;

//@SmartStrip
@Strippable(value={"forestry.api.multiblock.IAlvearyComponent", "forestry.api.multiblock.IAlvearyComponent$BeeModifier",
		"forestry.api.multiblock.IAlvearyComponent$BeeListener", "forestry.api.apiculture.IBeeModifier", "forestry.api.apiculture.IBeeListener",
		"thaumcraft.api.aspects.IEssentiaTransport", "thaumcraft.api.aspects.IAspectContainer"})
public class TileEntityLumenAlveary extends TileEntityRelayPowered implements GuiController, IAlvearyComponent, BeeModifier, BeeListener,
IBeeModifier, IBeeListener, CopyableSettings<TileEntityLumenAlveary>, IEssentiaTransport, IAspectContainer {

	private static final HashMap<String, AlvearyEffect> effectSet = new HashMap();
	private static final HashSet<AlvearyEffect> continualSet = new HashSet();
	private static final HashSet<AlvearyEffect> clientSet = new HashSet();
	private static final GeneticRepairEffect geneRepair2;
	private static final AutomationEffect automation;
	private static final PlayerRestrictionEffect playerOnlyEffects;
	private static final InfiniteSightEffect infiniteRange;
	//private static final EffectIntensificationEffect intensification;
	private static final OmnipresentEffectEffect omnipresence;
	private static final EnumMap<CrystalElement, ElementalBoostEffect> colorEffects = new EnumMap(CrystalElement.class);

	public static final Comparator<AlvearyEffect> effectSorter = new Comparator<AlvearyEffect>(){
		@Override
		public int compare(AlvearyEffect o1, AlvearyEffect o2) {
			if (o1 instanceof LumenAlvearyEffect && o2 instanceof LumenAlvearyEffect) {
				LumenAlvearyEffect l1 = (LumenAlvearyEffect)o1;
				LumenAlvearyEffect l2 = (LumenAlvearyEffect)o2;
				return Integer.compare(100000*l1.color.ordinal()+l1.requiredEnergy, 100000*l2.color.ordinal()+l2.requiredEnergy);
			}
			else if (o1 instanceof VisAlvearyEffect && o2 instanceof VisAlvearyEffect) {
				VisAlvearyEffect v1 = (VisAlvearyEffect)o1;
				VisAlvearyEffect v2 = (VisAlvearyEffect)o2;
				if (v1.aspect == v2.aspect) {
					return Integer.compare(v1.requiredVis, v2.requiredVis);
				}
				else {
					return v1.aspect.getLocalizedDescription().compareToIgnoreCase(v2.aspect.getLocalizedDescription());
				}
			}
			else if (o1 instanceof LumenAlvearyEffect) {
				return Integer.MAX_VALUE;
			}
			else if (o2 instanceof LumenAlvearyEffect) {
				return Integer.MIN_VALUE;
			}
			else if (o1 instanceof VisAlvearyEffect) {
				return Integer.MAX_VALUE;
			}
			else if (o2 instanceof VisAlvearyEffect) {
				return Integer.MIN_VALUE;
			}
			return o1.getDescription().compareToIgnoreCase(o2.getDescription());
		}
	};

	private static Field tempField;
	private static Field humidField;
	private static Field flowerCacheField;
	private static Class beeLogicClass;
	private static Class fakeLogicClass;

	private Object logic;

	@ModDependent(ModList.THAUMCRAFT)
	private AspectList aspects;
	private static final int VIS_LIMIT = 200;

	/** Relative to minX,Y,Z */
	private Coordinate relativeLocation;

	private int lightningTicks;

	private String movePrincess;
	private String moveDrone;

	private final HashSet<String> selectedEffects = new HashSet();
	private final Collection<AlvearyEffect> activeEffects = new HashSet();
	private EfficientFlowerCache flowerCache;

	private EntityItem renderItem;

	private boolean multipleBoosters = false;

	private IAlleleBeeSpecies cachedQueen;
	private boolean canWork;

	static {
		if (ModList.FORESTRY.isLoaded()) {
			try {
				Class c = Class.forName("forestry.apiculture.multiblock.AlvearyController");
				tempField = c.getDeclaredField("tempChange");
				tempField.setAccessible(true);
				humidField = c.getDeclaredField("humidChange");
				humidField.setAccessible(true);

				beeLogicClass = Class.forName("forestry.apiculture.BeekeepingLogic");
				flowerCacheField = beeLogicClass.getDeclaredField("hasFlowersCache");
				flowerCacheField.setAccessible(true);

				fakeLogicClass = Class.forName("forestry.apiculture.FakeBeekeepingLogic");
			}
			catch (Exception e) {
				ChromatiCraft.logger.logError("Could not fetch alveary internal methods!");
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
		new GeneticImmutabilityEffect();
		new GeneticImprovementEffect();
		new TemperatureMatchingEffect();
		new HumidityMatchingEffect();
		new GeneticRepairEffectI();
		geneRepair2 = new GeneticRepairEffectII();
		new EternalEffect();
		//new SuppressionEffect(); //not implementable
		automation = new AutomationEffect();
		playerOnlyEffects = new PlayerRestrictionEffect();
		for (int i = 0; i < 16; i++) {
			CrystalElement e = CrystalElement.elements[i];
			colorEffects.put(e, new ElementalBoostEffect(e));
		}
		infiniteRange = new InfiniteSightEffect();
		//intensification = new EffectIntensificationEffect();
		omnipresence = new OmnipresentEffectEffect();

		if (ModList.THAUMCRAFT.isLoaded()) {
			new ProductionBoostEffect();
			new NoProductionEffect();
			new EnhancedEffectEffect();
			new MutationBoostEffect();
			new FloweringBoostEffect();
			new RainBoostEffect();
		}
	}

	public static String getEffectsAsString() {
		StringBuilder sb = new StringBuilder();
		ArrayList<AlvearyEffect> li = new ArrayList(effectSet.values());
		Collections.sort(li, effectSorter);
		for (AlvearyEffect ae : li) {
			sb.append(ae.getDescription());
			sb.append(" - ");
			if (ae instanceof LumenAlvearyEffect) {
				sb.append(((LumenAlvearyEffect)ae).color.displayName);
				sb.append(" (");
				sb.append(((LumenAlvearyEffect)ae).requiredEnergy);
				sb.append(" L/cycle)");
			}
			else if (ae instanceof VisAlvearyEffect) {
				sb.append(((VisAlvearyEffect)ae).aspect.getLocalizedDescription());
				sb.append(" (");
				sb.append(((VisAlvearyEffect)ae).requiredVis);
				sb.append(" cv/cycle)");
			}
			sb.append("\n");
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

		this.resetSelectedEffects();
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
				if (!world.isRemote && this.getTicksExisted()%8 == 0) {
					canWork = this.calcCanWork();
					this.calcSpecies();
				}

				if (world.isRemote) {
					this.doParticles(world, x, y, z);

					for (AlvearyEffect ae : clientSet) {
						if (selectedEffects.contains(ae.ID) && ae.isActive(this)) {
							if (ae.tickRate() == 1 || this.getTicksExisted()%ae.tickRate() == 0)
								ae.clientTick(this);
						}
					}

					if (!canWork && cachedQueen != null && this.getTicksExisted()%32 == 0) {
						this.syncAllData(false);
					}
				}
				if (this.hasQueen()) {
					if (this.getTicksExisted()%8 == 0)
						this.replaceFlowerCacher();
					activeEffects.clear();
					if (this.canQueenWork()) {
						for (AlvearyEffect ae : effectSet.values()) {
							if (selectedEffects.contains(ae.ID) && ae.isActive(this)) {
								if (ae.tickRate() == 1 || this.getTicksExisted()%ae.tickRate() == 0)
									if (ae.tick(this))
										activeEffects.add(ae);
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
							if (selectedEffects.contains(ae.ID) && ae.isActive(this)) {
								if (ae.tickRate() == 1 || this.getTicksExisted()%ae.tickRate() == 0)
									if (ae.tick(this))
										activeEffects.add(ae);
							}
						}
					}
					//ReikaBeeHelper.ageBee(this.worldObj, this.getQueenItem(), 0.8F);
				}
				else if (!Strings.isNullOrEmpty(movePrincess)) {
					if (this.canRunAutomation()) {
						if (this.cycleBees(movePrincess, EnumBeeType.PRINCESS)) {
							movePrincess = null;
							this.drainEnergy(automation.color, automation.requiredEnergy);
						}
					}
				}
				if (!Strings.isNullOrEmpty(moveDrone)) {
					if (this.canRunAutomation()) {
						if (this.cycleBees(moveDrone, EnumBeeType.DRONE)) {
							moveDrone = null;
							this.drainEnergy(automation.color, automation.requiredEnergy);
						}
					}
				}
			}
			else if (!world.isRemote) {
				canWork = false;
			}
		}
	}

	private boolean canRunAutomation() {
		return this.isEffectSelectedAndActive(automation) && energy.containsAtLeast(automation.color, automation.requiredEnergy);
	}

	/** Replace with one that will never scan multiple times in the same tick, and has more advanced anti-lag behavior */
	private void replaceFlowerCacher() {
		if (flowerCache == null) {
			flowerCache = new EfficientFlowerCache();
		}
		if (worldObj != null && this.isTickingNaturally()) {
			IBeekeepingLogic bkl = this.getMultiblockLogic().getController().getBeekeepingLogic();
			if (bkl.getClass() == beeLogicClass) {
				try {
					INBTTagable o = (INBTTagable)flowerCacheField.get(bkl);
					if (!(o instanceof EfficientFlowerCache)) {
						//NBTTagCompound tag = new NBTTagCompound();
						//o.writeToNBT(tag);
						//eff.readFromNBT(tag);
						flowerCacheField.set(bkl, flowerCache);
					}
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public EfficientFlowerCache getFlowerCache() {
		this.replaceFlowerCacher();
		return flowerCache;
	}

	public Collection<AlvearyEffect> getActiveEffects() {
		return Collections.unmodifiableCollection(activeEffects);
	}

	public Collection<AlvearyEffect> getSelectedEffects() {
		HashSet<AlvearyEffect> set = new HashSet();
		for (String id : selectedEffects) {
			set.add(AlvearyEffect.getEffectByID(id));
		}
		return set;
	}

	public void setEffectSelectionState(String id, boolean active) {
		if (active) {
			selectedEffects.add(id);
		}
		else {
			selectedEffects.remove(id);
		}
	}

	public boolean isEffectSelected(AlvearyEffect e) {
		return selectedEffects.contains(e.ID);
	}

	public boolean isEffectSelectedAndActive(AlvearyEffect e) {
		return selectedEffects.contains(e.ID) && e.isActive(this);
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
	public ElementTagCompound getRequiredEnergy() {
		ElementTagCompound tag = new ElementTagCompound();
		if (!this.isAlvearyComplete())
			return tag;
		for (AlvearyEffect ae : effectSet.values()) {
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
		relativeLocation = new Coordinate(this).offset(new Coordinate(minCoord).negate());
		this.updateRenderItem();
		this.syncAllData(true);
	}

	@ModDependent(ModList.FORESTRY)
	private void validateStructure(IMultiblockController controller) {
		boolean flag = false;
		multipleBoosters = false;
		for (IMultiblockComponent com : controller.getComponents()) {
			if (com instanceof TileEntityLumenAlveary && com != this) {
				multipleBoosters = true;
				this.syncAllData(true);
				break;
			}
		}
	}

	@Override
	@ModDependent(ModList.FORESTRY)
	public void onMachineBroken() {
		ReikaWorldHelper.causeAdjacentUpdates(worldObj, xCoord, yCoord, zCoord);
		this.triggerBlockUpdate();
		relativeLocation = null;
		this.updateRenderItem();
		this.syncAllData(true);
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
		for (AlvearyEffect ae : effectSet.values()) {
			ae.consumeEnergy(this, amount);
			ae.onProductionTick(this);
		}
		this.updateRenderItem();
	}

	@Override
	@ModDependent(ModList.FORESTRY)
	public void onQueenDeath() {
		IAlleleBeeSpecies bee = this.getSpecies();
		if (bee == null)
			ChromatiCraft.logger.logError("Alveary called onQueenDeath with a null queen!");
		if (bee != null)
			movePrincess = bee.getUID();
		moveDrone = movePrincess;
		if (energy.containsAtLeast(geneRepair2.color, geneRepair2.requiredEnergy)) {
			if (geneRepair2.repairQueen(this))
				energy.subtract(geneRepair2.color, geneRepair2.requiredEnergy);
		}
		//ReikaJavaLibrary.pConsole("Marked to cycle "+movePrincess);
		cachedQueen = null;
		canWork = false;
	}

	public void forceCycleBees() {
		if (movePrincess != null)
			this.cycleBees(movePrincess, EnumBeeType.PRINCESS);
		if (moveDrone != null)
			this.cycleBees(moveDrone, EnumBeeType.DRONE);
	}

	private boolean cycleBees(String species, EnumBeeType seek) {
		//ReikaJavaLibrary.pConsole("Cycling "+movePrincess);
		IBeeHousing ibh = this.getBeeHousing();
		IBeeHousingInventory ibhi = ibh.getBeeInventory();
		ISidedInventory inv = (ISidedInventory)ibhi;
		ItemStack drone = ibhi.getDrone();
		boolean flag2 = false;
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
					if (type != seek)
						continue;
					IAlleleBeeSpecies sp = (IAlleleBeeSpecies)ReikaBeeHelper.getSpecies(in);
					if (type == EnumBeeType.PRINCESS && ibhi.getQueen() == null && sp.getUID().equals(species)) {
						ibhi.setQueen(in);
						inv.setInventorySlotContents(i, null);
						flag2 = true;
					}
					else if (type == EnumBeeType.DRONE && ReikaItemHelper.areStacksCombinable(drone, in, inv.getInventoryStackLimit())) {
						int amt = Math.min(drone.getMaxStackSize()-drone.stackSize, in.stackSize);
						drone.stackSize += amt;
						ibhi.setDrone(drone);
						in.stackSize -= amt;
						if (in.stackSize == 0)
							inv.setInventorySlotContents(i, null);
						flag2 = true;
					}
					else if (type == EnumBeeType.DRONE && drone == null && sp.getUID().equals(species)) {
						ibhi.setDrone(in);
						inv.setInventorySlotContents(i, null);
						flag2 = true;
					}
				}
			}
			if (flag2)
				break;
		}
		return flag2;
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
		for (AlvearyEffect ae : effectSet.values()) {
			if (selectedEffects.contains(ae.ID) && ae.isActive(this)) {
				f *= ae.territoryFactor(this);
			}
		}
		return f;
	}

	@Override
	@ModDependent(ModList.FORESTRY)
	public float getMutationModifier(IBeeGenome genome, IBeeGenome mate, float current) {
		float f = 1;
		for (AlvearyEffect ae : effectSet.values()) {
			if (selectedEffects.contains(ae.ID) && ae.isActive(this)) {
				f *= ae.mutationFactor(this);
			}
		}
		return f;
	}

	@Override
	@ModDependent(ModList.FORESTRY)
	public float getLifespanModifier(IBeeGenome genome, IBeeGenome mate, float current) {
		float f = 1;
		for (AlvearyEffect ae : effectSet.values()) {
			if (selectedEffects.contains(ae.ID) && ae.isActive(this)) {
				f *= ae.lifespanFactor(this);
			}
		}
		return f;
	}

	@Override
	@ModDependent(ModList.FORESTRY)
	public float getProductionModifier(IBeeGenome genome, float current) {
		float f = 1;
		for (AlvearyEffect ae : effectSet.values()) {
			if (selectedEffects.contains(ae.ID) && ae.isActive(this)) {
				f *= ae.productionFactor(this);
			}
		}
		return f;
	}

	@Override
	@ModDependent(ModList.FORESTRY)
	public float getFloweringModifier(IBeeGenome genome, float current) {
		float f = 1;
		for (AlvearyEffect ae : effectSet.values()) {
			if (selectedEffects.contains(ae.ID) && ae.isActive(this)) {
				f *= ae.pollinationFactor(this);
			}
		}
		return f;
	}

	@Override
	@ModDependent(ModList.FORESTRY)
	public float getGeneticDecay(IBeeGenome genome, float current) {
		float f = 1;
		for (AlvearyEffect ae : effectSet.values()) {
			if (selectedEffects.contains(ae.ID) && ae.isActive(this)) {
				f *= ae.decayFactor(this);
			}
		}
		return f;
	}

	@Override
	@ModDependent(ModList.FORESTRY)
	public boolean isSealed() {
		for (AlvearyEffect ae : effectSet.values()) {
			if (selectedEffects.contains(ae.ID) && ae.isActive(this)) {
				if (ae.isSealed(this))
					return true;
			}
		}
		return false;
	}

	@Override
	@ModDependent(ModList.FORESTRY)
	public boolean isSelfLighted() {
		for (AlvearyEffect ae : effectSet.values()) {
			if (selectedEffects.contains(ae.ID) && ae.isActive(this)) {
				if (ae.isSelfLit(this))
					return true;
			}
		}
		return false;
	}

	@Override
	@ModDependent(ModList.FORESTRY)
	public boolean isSunlightSimulated() {
		for (AlvearyEffect ae : effectSet.values()) {
			if (selectedEffects.contains(ae.ID) && ae.isActive(this)) {
				if (ae.isSkySimulated(this))
					return true;
			}
		}
		return false;
	}

	@Override
	public boolean isHellish() {
		for (AlvearyEffect ae : effectSet.values()) {
			if (selectedEffects.contains(ae.ID) && ae.isActive(this)) {
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
	@Deprecated
	private void tickAlveary() {
		IAlvearyController iac = this.getMultiblockLogic().getController();
		ForestryMultiblockControllerHandling.tickMultiblock(iac, this);
	}

	@ModDependent(ModList.FORESTRY)
	private IBeeHousing getBeeHousing() {
		return this.getMultiblockLogic().getController();
	}

	@ModDependent(ModList.FORESTRY)
	public IBeeGenome getBeeGenome() {
		ItemStack is = this.getQueenItem();
		return is != null ? (IBeeGenome)ReikaBeeHelper.getGenome(is) : null;
	}

	public boolean hasQueen() {
		return ModList.FORESTRY.isLoaded() && this.getQueenItem() != null;// && ReikaBeeHelper.getBeeRoot().getType(this.getQueenItem()) == EnumBeeType.QUEEN;
	}

	@ModDependent(ModList.FORESTRY)
	public ItemStack getQueenItem() {
		IBeeHousing ibh = this.getMultiblockLogic().getController();
		if (ibh == null)
			return null;
		ItemStack is = ibh.getBeeInventory().getQueen();
		return is;
	}

	@ModDependent(ModList.FORESTRY)
	public boolean canQueenWork() {
		return canWork;
	}

	@ModDependent(ModList.FORESTRY)
	private boolean calcCanWork() {
		return this.hasQueen() && this.isAlvearyComplete() && this.getMultiblockLogic().getController().getBeekeepingLogic().canWork();
	}

	@ModDependent(ModList.FORESTRY)
	public IAlleleBeeSpecies getSpecies() {
		this.validateCachedQueen();
		return cachedQueen;
	}

	@ModDependent(ModList.FORESTRY)
	private void validateCachedQueen() {
		ItemStack is = this.getQueenItem();
		if (is == null)
			cachedQueen = null;
		else if (cachedQueen == null)
			this.calcSpecies();
	}

	@ModDependent(ModList.FORESTRY)
	private void calcSpecies() {
		IAlleleBeeSpecies type = this.getQueenItem() != null ? this.getBeeGenome().getPrimary() : null;
		boolean flag = type != cachedQueen;
		cachedQueen = type;
		if (flag)
			this.syncAllData(false);
	}

	@Override
	protected void readSyncTag(NBTTagCompound data) {
		super.readSyncTag(data);

		canWork = data.getBoolean("canWork");
		String queen = data.getString("queen");
		cachedQueen = queen.isEmpty() ? null : (IAlleleBeeSpecies)AlleleManager.alleleRegistry.getAllele(queen);
	}

	@Override
	protected void writeSyncTag(NBTTagCompound data) {
		super.writeSyncTag(data);

		data.setBoolean("canWork", canWork);
		data.setString("queen", cachedQueen != null ? cachedQueen.getUID() : "");
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		if (ModList.FORESTRY.isLoaded())
			this.getMultiblockLogic().readFromNBT(data);

		movePrincess = data.getString("move");
		if (movePrincess.isEmpty())
			movePrincess = null;
		moveDrone = data.getString("movedr");
		if (moveDrone.isEmpty())
			moveDrone = null;

		if (ModList.THAUMCRAFT.isLoaded()) {
			aspects.readFromNBT(data);
		}

		if (ModList.FORESTRY.isLoaded())
			this.getFlowerCache().readFromNBT(data);

		if (data.hasKey("effectsel"))
			ReikaNBTHelper.readCollectionFromNBT(selectedEffects, data, "effectsel");
	}

	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		if (ModList.FORESTRY.isLoaded())
			this.getMultiblockLogic().writeToNBT(data);

		data.setString("move", !Strings.isNullOrEmpty(movePrincess) ? movePrincess : "");
		data.setString("movedr", !Strings.isNullOrEmpty(moveDrone) ? moveDrone : "");

		if (ModList.THAUMCRAFT.isLoaded()) {
			aspects.writeToNBT(data);
		}

		if (ModList.FORESTRY.isLoaded())
			this.getFlowerCache().writeToNBT(data);

		ReikaNBTHelper.writeCollectionToNBT(selectedEffects, data, "effectsel");
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
		renderItem = this.isAlvearyComplete() && this.getQueenItem() != null ? new InertItem(worldObj, this.getQueenItem()) : null;
	}

	public boolean effectsOnlyOnPlayers() {
		return this.isEffectSelectedAndActive(playerOnlyEffects);
	}

	public boolean hasInfiniteAwareness() {
		return this.isEffectSelectedAndActive(infiniteRange);
	}

	public boolean isIgnoble() {
		return this.getQueenItem() != null && !ReikaBeeHelper.isPristine(this.getQueenItem());
	}
	/*
	public boolean hasIntensification() {
		return this.isEffectSelectedAndActive(intensification);
	}
	 */
	public boolean hasOmnipresence() {
		return this.isEffectSelectedAndActive(omnipresence);
	}

	public boolean isColorBoosted(CrystalElement e) {
		return colorEffects.get(e).isActive(this);
	}

	@Override
	public int getPacketDelay() {
		return super.getPacketDelay()*4;
	}

	public static final Collection<AlvearyEffect> getEffectSet() {
		return Collections.unmodifiableCollection(effectSet.values());
	}

	public static final Collection<? extends AlvearyEffect> getEffectSet(Class<? extends AlvearyEffect> c) {
		ArrayList<AlvearyEffect> li = new ArrayList();
		for (AlvearyEffect ae : effectSet.values()) {
			if (c.isAssignableFrom(ae.getClass())) {
				li.add(ae);
			}
		}
		return li;
	}

	@Override
	public boolean copySettingsFrom(TileEntityLumenAlveary te) {
		selectedEffects.clear();
		selectedEffects.addAll(te.selectedEffects);
		this.syncAllData(true);
		return true;
	}

	public void clearSelectedEffects() {
		selectedEffects.clear();
	}

	public void resetSelectedEffects() {
		this.clearSelectedEffects();

		for (AlvearyEffect ae : effectSet.values()) {
			if (ae.isOnByDefault())
				selectedEffects.add(ae.ID);
		}
	}

	public static abstract class AlvearyEffect {

		public final String ID;

		protected AlvearyEffect(String id) {
			ID = id;
			effectSet.put(ID, this);
			if (this.worksWhenBeesDoNot()) {
				continualSet.add(this);
			}
			if (this.ticksOnClient()) {
				clientSet.add(this);
			}
		}

		public static AlvearyEffect getEffectByID(String id) {
			return effectSet.get(id);
		}

		public abstract String getDescription();

		protected abstract void consumeEnergy(TileEntityLumenAlveary te, int amount);

		protected abstract boolean isActive(TileEntityLumenAlveary te);

		protected boolean tick(TileEntityLumenAlveary te) {
			return true;
		}

		@SideOnly(Side.CLIENT)
		protected void clientTick(TileEntityLumenAlveary te) {

		}

		protected boolean ticksOnClient() {
			return false;
		}

		public boolean isOnByDefault() {
			return true;
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

	public static abstract class PoweredAlvearyEffect extends AlvearyEffect {

		protected PoweredAlvearyEffect(String id) {
			super(id);
		}

		public abstract String getResource();

		public abstract int getCost();

	}

	public static abstract class VisAlvearyEffect extends PoweredAlvearyEffect {

		public final Aspect aspect;
		public final int requiredVis;

		protected VisAlvearyEffect(String id, Aspect a, int amt) {
			super(id);
			aspect = a;
			requiredVis = amt;
		}

		@Override
		public final String getResource() {
			return aspect.getName();
		}

		@Override
		public final int getCost() {
			return requiredVis;
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

		@Override
		public boolean isOnByDefault() {
			return false;
		}

	}

	private static class ProductionBoostEffect extends VisAlvearyEffect {

		private ProductionBoostEffect() {
			super("visprodboost", Aspect.ORDER, 4);
		}

		@Override
		protected float productionFactor(TileEntityLumenAlveary te) {
			return 2;
		}

		@Override
		public String getDescription() {
			return "Production Boost";
		}

		@Override
		public boolean isOnByDefault() {
			return true;
		}

	}

	private static class NoProductionEffect extends VisAlvearyEffect {

		private NoProductionEffect() {
			super("visprodstop", Aspect.ENTROPY, 1);
		}

		@Override
		protected float productionFactor(TileEntityLumenAlveary te) {
			return 0;
		}

		@Override
		public String getDescription() {
			return "Production Nullification";
		}

	}

	private static class EnhancedEffectEffect extends VisAlvearyEffect {

		private EnhancedEffectEffect() {
			super("viseffectboost", Aspect.AIR, 20);
		}

		@Override
		public String getDescription() {
			return "Effect Expansion";
		}

		@Override
		protected boolean tick(TileEntityLumenAlveary te) {
			IBeeGenome ibg = te.getBeeGenome();
			if (ibg != null) {
				ibg.getEffect().doEffect(ibg, new DummyEffectData(), te.getBeeHousing());
			}
			return true;
		}

	}

	private static class MutationBoostEffect extends VisAlvearyEffect {

		private MutationBoostEffect() {
			super("vismutationboost", Aspect.ENTROPY, 4);
		}

		@Override
		public String getDescription() {
			return "Genetic Instability";
		}

		@Override
		protected float mutationFactor(TileEntityLumenAlveary te) {
			return 1.5F;
		}

	}

	private static class FloweringBoostEffect extends VisAlvearyEffect {

		private FloweringBoostEffect() {
			super("visflowerboost", Aspect.EARTH, 2);
		}

		@Override
		public String getDescription() {
			return "Gardener";
		}

		@Override
		protected float pollinationFactor(TileEntityLumenAlveary te) {
			return 1.5F;
		}

	}

	private static class RainBoostEffect extends VisAlvearyEffect {

		private RainBoostEffect() {
			super("visrainboost", Aspect.WATER, 1);
		}

		@Override
		public String getDescription() {
			return "Precipitative Enhancement";
		}

		@Override
		protected float productionFactor(TileEntityLumenAlveary te) {
			return te.worldObj.isRaining() ? 1.2F : 1;
		}

	}

	public static abstract class LumenAlvearyEffect extends PoweredAlvearyEffect {

		public final CrystalElement color;
		public final int requiredEnergy;

		protected LumenAlvearyEffect(String id, CrystalElement e, int amt) {
			super(id);
			color = e;
			requiredEnergy = amt;
		}

		@Override
		public final String getResource() {
			return color.displayName;
		}

		@Override
		public final int getCost() {
			return requiredEnergy;
		}

		protected boolean consumeOnTick() {
			return true;
		}

		@Override
		protected boolean isActive(TileEntityLumenAlveary te) {
			return te.energy.containsAtLeast(color, requiredEnergy);
		}

		@Override
		protected final void consumeEnergy(TileEntityLumenAlveary te, int amount) {
			int amt = amount*requiredEnergy;
			if (te.energy.containsAtLeast(color, amt)) {
				te.drainEnergy(color, amt);
			}
		}

		@Override
		public boolean isOnByDefault() {
			return false;
		}

	}

	private static class AutomationEffect extends LumenAlvearyEffect {


		private AutomationEffect() {
			super("automate", CrystalElement.GREEN, 100);
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

		@Override
		public boolean isOnByDefault() {
			return true;
		}
	}

	private static abstract class AccelerationEffect extends LumenAlvearyEffect {

		private final int tickRate;

		private AccelerationEffect(int ticks, int cost) {
			super("accel_"+ticks, CrystalElement.LIGHTBLUE, cost);
			tickRate = ticks;
		}

		@Override
		protected final boolean tick(TileEntityLumenAlveary te) {
			if (te.worldObj.isRemote)
				return true;
			IBeekeepingLogic bkl = te.getMultiblockLogic().getController().getBeekeepingLogic();
			//max of 16 ticks per tick, so no massive changes (eg 2 prod cycles), and no ingame time passage,
			//and thus only possible change to canWork is queen death, and even that is called by canWork
			boolean work = bkl.canWork();
			for (int i = 0; work && te.canQueenWork() && i < tickRate; i++) {
				//te.tickAlveary();
				bkl.doWork();
			}
			return true;
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
			super("lightning", CrystalElement.YELLOW, 100);
		}

		@Override
		protected boolean tick(TileEntityLumenAlveary te) {
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

			return true;
		}

		@Override
		public String getDescription() {
			return "Lightning Production";
		}

	}

	private static class HistoryRewriteEffect extends LumenAlvearyEffect {

		private static final double mateRewriteChance = 0.01;

		private HistoryRewriteEffect() {
			super("historyrewrite", CrystalElement.GRAY, 40);
		}

		@Override
		protected boolean tick(TileEntityLumenAlveary te) {
			if (ReikaRandomHelper.doWithChance(mateRewriteChance)) {
				ItemStack queen = te.getQueenItem();
				if (queen != null) {
					IIndividual ii = AlleleManager.alleleRegistry.getIndividual(queen);
					if (ii instanceof IBee) {
						ReikaBeeHelper.setBeeMate((IBee)ii, (IBee)ii);
					}
				}
			}
			return true;
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
			super("genebalance", CrystalElement.WHITE, 80);
		}

		@Override
		protected boolean tick(TileEntityLumenAlveary te) {
			IBeeHousing ibh = te.getBeeHousing();
			IBeeGenome ibg = te.getBeeGenome();
			if (ibg == null)
				return false;
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
			return true;
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
			super("genestable", CrystalElement.WHITE, 20);
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

		@Override
		public boolean isOnByDefault() {
			return true;
		}

	}

	private static class GeneticImmutabilityEffect extends LumenAlvearyEffect {

		private GeneticImmutabilityEffect() {
			super("genelock", CrystalElement.WHITE, 600);
		}

		@Override
		protected float mutationFactor(TileEntityLumenAlveary te) {
			return 0;
		}

		@Override
		protected float decayFactor(TileEntityLumenAlveary te) {
			return 0F;
		}

		@Override
		public String getDescription() {
			return "Genetic Immutability";
		}

	}

	private static class GeneticFluxEffect extends LumenAlvearyEffect {

		private GeneticFluxEffect() {
			super("geneflux", CrystalElement.BLACK, 60);
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
			super("explore", CrystalElement.LIME, 120);
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
			super("geneboost", CrystalElement.BLACK, 120);
		}

		@Override
		protected boolean tick(TileEntityLumenAlveary te) {
			if (ReikaRandomHelper.doWithChance(geneImprovementChance)) {
				ItemStack queen = te.getBeeHousing().getBeeInventory().getQueen();
				if (queen != null && AlleleManager.alleleRegistry.getIndividual(queen) instanceof IBee) {
					IBeeGenome ibg = te.getBeeGenome();
					if (ibg == null)
						return false;
					EnumBeeChromosome gene = EnumBeeChromosome.values()[rand.nextInt(EnumBeeChromosome.values().length)];
					if (this.canImprove(gene, ibg)) {
						this.improveGene(gene, ibg, queen);
					}
				}
			}
			return true;
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
			super("tempmatch", CrystalElement.ORANGE, 80);
		}

		@Override
		protected int tickRate() {
			return 1;
		}

		@Override
		public String getDescription() {
			return "Dynamic Temperature";
		}

		@Override
		protected boolean tick(TileEntityLumenAlveary te) {
			IAlvearyController ac = te.getMultiblockLogic().getController();
			IClimateControlled cc = (IClimateControlled)ac;
			IAlleleBeeSpecies queen = te.getSpecies();
			if (queen == null)
				return false;
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
					return false;
				}
			}
			return true;
		}

		@Override
		protected boolean isHellish(TileEntityLumenAlveary te) {
			return te.getSpecies() != null && te.getSpecies().getTemperature() == EnumTemperature.HELLISH;
		}

		@Override
		protected boolean worksWhenBeesDoNot() {
			return false;
		}

		@Override
		public boolean isOnByDefault() {
			return true;
		}

	}

	private static class HumidityMatchingEffect extends LumenAlvearyEffect {

		private HumidityMatchingEffect() {
			super("humidmatch", CrystalElement.CYAN, 80);
		}

		@Override
		protected int tickRate() {
			return 1;
		}

		@Override
		public String getDescription() {
			return "Dynamic Humidity";
		}

		@Override
		protected boolean tick(TileEntityLumenAlveary te) {
			IAlvearyController ac = te.getMultiblockLogic().getController();
			IClimateControlled cc = (IClimateControlled)ac;
			IAlleleBeeSpecies queen = te.getSpecies();
			if (queen == null)
				return false;
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
					return false;
				}
			}
			return true;
		}

		@Override
		protected boolean worksWhenBeesDoNot() {
			return false;
		}

		@Override
		public boolean isOnByDefault() {
			return true;
		}

	}

	private static abstract class GeneticRepairEffect extends LumenAlvearyEffect {

		private final int tier;

		private GeneticRepairEffect(int tier) {
			super("repair_"+tier, CrystalElement.MAGENTA, 100*ReikaMathLibrary.intpow2(24, tier-1));
			this.tier = tier;
		}

		@Override
		public final String getDescription() {
			return "Genetic Repair "+tier;
		}

		@Override
		protected final void onProductionTick(TileEntityLumenAlveary te) {
			if (this.doRepair(te)) {
				this.repairQueen(te);
			}
		}

		private boolean repairQueen(TileEntityLumenAlveary te) {
			ItemStack is = te.getQueenItem();
			if (ReikaBeeHelper.isPristine(is))
				return false;
			ReikaBeeHelper.setPristine(is, true);
			te.getBeeHousing().getBeeInventory().setQueen(is);
			return true;
		}

		protected abstract boolean doRepair(TileEntityLumenAlveary te);

		@Override
		public final boolean isOnByDefault() {
			return true;
		}

	}

	private static class GeneticRepairEffectI extends GeneticRepairEffect {

		private GeneticRepairEffectI() {
			super(1);
		}

		@Override
		protected boolean doRepair(TileEntityLumenAlveary te) {
			return te.rand.nextInt(40) == 0;
		}
	}

	private static class GeneticRepairEffectII extends GeneticRepairEffect {

		private GeneticRepairEffectII() {
			super(2);
		}

		@Override
		protected boolean doRepair(TileEntityLumenAlveary te) {
			return true;
		}
	}

	private static class EternalEffect extends LumenAlvearyEffect {

		private EternalEffect() {
			super("inflife", CrystalElement.RED, 600);
		}

		@Override
		public String getDescription() {
			return "Eternal Life";
		}
		/*
		@Override
		protected void tick(TileEntityLumenAlveary te) {
			if (te.hasQueen()) {
				ItemStack is = te.getQueenItem();
				if (ReikaBeeHelper.getBeeRoot().getType(is) == EnumBeeType.QUEEN) {
					Life l = (Life)ReikaBeeHelper.getGeneEnum(EnumBeeChromosome.LIFESPAN, ReikaBeeHelper.getBee(is).getGenome());
					if (l == CrystalBees.superLife || true)
						ReikaBeeHelper.rejuvenateBee(te.getMultiblockLogic().getController(), is);
				}
			}
		}*/

		@Override
		protected float lifespanFactor(TileEntityLumenAlveary te) {
			Life l = (Life)ReikaBeeHelper.getGeneEnum(EnumBeeChromosome.LIFESPAN, ReikaBeeHelper.getBee(te.getQueenItem()).getGenome());
			if (l == CrystalBees.superLife)
				return 10000;
			return 1;
		}

	}

	private static class PlayerRestrictionEffect extends LumenAlvearyEffect {

		private PlayerRestrictionEffect() {
			super("playeronly", CrystalElement.LIGHTGRAY, 20);
		}

		@Override
		public String getDescription() {
			return "Effect Restriction";
		}

	}

	private static class ElementalBoostEffect extends LumenAlvearyEffect {

		private final CrystalElement color;

		private ElementalBoostEffect(CrystalElement e) {
			super("elem_"+e.name().toLowerCase(Locale.ENGLISH)+"_boost", e, 240);
			color = e;
		}

		@Override
		public String getDescription() {
			return color.displayName+" Boost";
		}

		@Override
		protected boolean isActive(TileEntityLumenAlveary te) {
			return super.isActive(te) && te.getSpecies() == CrystalBees.getElementalBee(color);
		}

	}

	private static class InfiniteSightEffect extends LumenAlvearyEffect {

		private InfiniteSightEffect() {
			super("infsight", CrystalElement.BLUE, 600);
		}

		@Override
		public String getDescription() {
			return "Infinite View";
		}

		@Override
		protected boolean isActive(TileEntityLumenAlveary te) {
			return super.isActive(te) && te.getSpecies() instanceof ConditionalProductBee;
		}

	}
	/*
	private static class EffectIntensificationEffect extends LumenAlvearyEffect {

		private EffectIntensificationEffect() {
			super(CrystalElement.PURPLE, 750);
		}

		@Override
		public String getDescription() {
			return "Effect Intensification";
		}

		@Override
		protected boolean isActive(TileEntityLumenAlveary te) {
			return super.isActive(te) && te.hasQueen() && te.getBeeGenome().getEffect() instanceof CrystalEffect;
		}

	}
	 */
	private static class OmnipresentEffectEffect extends LumenAlvearyEffect {

		private OmnipresentEffectEffect() {
			super("omnipresent", CrystalElement.PURPLE, 1200);
		}

		@Override
		public String getDescription() {
			return "Omnipresence";
		}

		@Override
		protected boolean isActive(TileEntityLumenAlveary te) {
			return super.isActive(te) && te.hasQueen() && te.getBeeGenome().getEffect() instanceof CrystalEffect;
		}

	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public boolean isConnectable(ForgeDirection face) {
		return false;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public boolean canInputFrom(ForgeDirection face) {
		return false;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public boolean canOutputTo(ForgeDirection face) {
		return false;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public void setSuction(Aspect aspect, int amount) {

	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public Aspect getSuctionType(ForgeDirection face) {
		return null;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public int getSuctionAmount(ForgeDirection face) {
		return 0;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public int takeEssentia(Aspect aspect, int amount, ForgeDirection face) {
		return 0;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public int addEssentia(Aspect aspect, int amount, ForgeDirection face) {
		return this.addToContainer(aspect, amount);
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public Aspect getEssentiaType(ForgeDirection face) {
		return null;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public int getEssentiaAmount(ForgeDirection face) {
		return 0;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public int getMinimumSuction() {
		return 0;
	}

	@Override
	@ModDependent(ModList.THAUMCRAFT)
	public boolean renderExtendedTube() {
		return false;
	}

	@Override
	public AspectList getAspects() {
		return aspects.copy();
	}

	@Override
	public void setAspects(AspectList al) {

	}

	@Override
	public boolean doesContainerAccept(Aspect tag) {
		return tag.isPrimal();
	}

	@Override
	public int addToContainer(Aspect aspect, int amount) {
		if (!aspect.isPrimal())
			return 0;
		int space = VIS_LIMIT-aspects.getAmount(aspect);
		int add = Math.min(space, amount);
		if (add > 0) {
			aspects.add(aspect, add);
		}
		return add;
	}

	@Override
	public boolean takeFromContainer(Aspect tag, int amount) {
		return false;
	}

	@Override
	public boolean takeFromContainer(AspectList ot) {
		return false;
	}

	@Override
	public boolean doesContainerContainAmount(Aspect tag, int amount) {
		return aspects.getAmount(tag) >= amount;
	}

	@Override
	public boolean doesContainerContain(AspectList ot) {
		return ReikaThaumHelper.aspectListContains(aspects, ot);
	}

	@Override
	public int containerContains(Aspect tag) {
		return aspects.getAmount(tag);
	}

}
