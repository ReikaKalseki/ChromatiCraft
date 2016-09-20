package Reika.ChromatiCraft.ModInterface;

import java.lang.reflect.Method;
import java.util.HashSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
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
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.Bees.AlleleRegistry.BeeGene;
import Reika.DragonAPI.ModInteract.Bees.AlleleRegistry.Fertility;
import Reika.DragonAPI.ModInteract.Bees.AlleleRegistry.Flowering;
import Reika.DragonAPI.ModInteract.Bees.AlleleRegistry.Speeds;
import Reika.DragonAPI.ModInteract.Bees.AlleleRegistry.Territory;
import Reika.DragonAPI.ModInteract.Bees.ReikaBeeHelper;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.apiculture.IBeeListener;
import forestry.api.apiculture.IBeeModifier;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.EnumTolerance;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IIndividual;
import forestry.api.multiblock.IAlvearyComponent;
import forestry.api.multiblock.IAlvearyComponent.BeeListener;
import forestry.api.multiblock.IAlvearyComponent.BeeModifier;
import forestry.api.multiblock.IAlvearyController;
import forestry.api.multiblock.IMultiblockController;
import forestry.api.multiblock.IMultiblockLogicAlveary;
import forestry.api.multiblock.MultiblockManager;

@Strippable(value={"forestry.api.multiblock.IAlvearyComponent", "forestry.api.multiblock.IAlvearyComponent.BeeModifier",
		"forestry.api.multiblock.IAlvearyComponent.BeeListener", "forestry.api.apiculture.IBeeModifier", "forestry.api.apiculture.IBeeListener"})
public class TileEntityLumenAlveary extends TileEntityRelayPowered implements IAlvearyComponent, BeeModifier, BeeListener, IBeeModifier, IBeeListener {

	private static final HashSet<AlvearyEffect> effectSet = new HashSet();

	private static Method tickMethod;

	private final Object logic = ModList.FORESTRY.isLoaded() ? MultiblockManager.logicFactory.createAlvearyLogic() : null;

	/** Relative to minX,Y,Z */
	private Coordinate relativeLocation;

	private int lightningTicks;

	static {
		if (ModList.FORESTRY.isLoaded()) {
			try {
				Class c = Class.forName("forestry.core.multiblock.MultiblockControllerBase");
				tickMethod = c.getDeclaredMethod("updateMultiblockEntity");
				tickMethod.setAccessible(true);
			}
			catch (Exception e) {
				ChromatiCraft.logger.logError("Could not fetch Alveary tick() method");
				e.printStackTrace();
				ReflectiveFailureTracker.instance.logModReflectiveFailure(ModList.FORESTRY, e);
			}
		}

		effectSet.add(new AccelerationEffectI());
		effectSet.add(new AccelerationEffectII());
		effectSet.add(new AccelerationEffectIII());
		effectSet.add(new LightningProductionEffect());
		effectSet.add(new HistoryRewriteEffect());
		effectSet.add(new GeneticFluxEffect());
		effectSet.add(new GeneticStabilityEffect());
		effectSet.add(new GeneticImprovementEffect());
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
		return relativeLocation != null;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);

		if (this.isAlvearyComplete()) {
			if (world.isRemote) {
				this.doParticles(world, x, y, z);
			}
			if (this.hasQueen() && this.canQueenWork()) {
				for (AlvearyEffect ae : effectSet) {
					if (energy.containsAtLeast(ae.color, ae.requiredEnergy)) {
						ae.tick(this);
					}
				}
			}
		}
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
			tag.addValueToColor(ae.color, ae.requiredEnergy*10);
		}
		return tag;
	}

	@Override
	public boolean isAcceptingColor(CrystalElement e) {
		return this.getRequiredEnergy().contains(e);
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return 12000;
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
		this.triggerBlockUpdate();
		this.syncAllData(true);
		relativeLocation = new Coordinate(this).offset(new Coordinate(minCoord).negate());
	}

	@Override
	@ModDependent(ModList.FORESTRY)
	public void onMachineBroken() {
		ReikaWorldHelper.causeAdjacentUpdates(worldObj, xCoord, yCoord, zCoord);
		this.triggerBlockUpdate();
		this.syncAllData(true);
		relativeLocation = null;
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
			int amt = amount*ae.requiredEnergy;
			if (energy.containsAtLeast(ae.color, amt)) {
				this.drainEnergy(ae.color, amt);
			}
		}
	}

	@Override
	@ModDependent(ModList.FORESTRY)
	public void onQueenDeath() {

	}

	@Override
	@ModDependent(ModList.FORESTRY)
	public boolean onPollenRetrieved(IIndividual pollen) {
		return false;
	}

	@Override
	@ModDependent(ModList.FORESTRY)
	public float getTerritoryModifier(IBeeGenome genome, float f) {
		for (AlvearyEffect ae : effectSet) {
			if (energy.containsAtLeast(ae.color, ae.requiredEnergy)) {
				f *= ae.territoryFactor();
			}
		}
		return f;
	}

	@Override
	@ModDependent(ModList.FORESTRY)
	public float getMutationModifier(IBeeGenome genome, IBeeGenome mate, float f) {
		for (AlvearyEffect ae : effectSet) {
			if (energy.containsAtLeast(ae.color, ae.requiredEnergy)) {
				f *= ae.mutationFactor();
			}
		}
		return f;
	}

	@Override
	@ModDependent(ModList.FORESTRY)
	public float getLifespanModifier(IBeeGenome genome, IBeeGenome mate, float f) {
		for (AlvearyEffect ae : effectSet) {
			if (energy.containsAtLeast(ae.color, ae.requiredEnergy)) {
				f *= ae.lifespanFactor();
			}
		}
		return f;
	}

	@Override
	@ModDependent(ModList.FORESTRY)
	public float getProductionModifier(IBeeGenome genome, float f) {
		for (AlvearyEffect ae : effectSet) {
			if (energy.containsAtLeast(ae.color, ae.requiredEnergy)) {
				f *= ae.productionFactor();
			}
		}
		return f;
	}

	@Override
	@ModDependent(ModList.FORESTRY)
	public float getFloweringModifier(IBeeGenome genome, float f) {
		for (AlvearyEffect ae : effectSet) {
			if (energy.containsAtLeast(ae.color, ae.requiredEnergy)) {
				f *= ae.pollinationFactor();
			}
		}
		return f;
	}

	@Override
	@ModDependent(ModList.FORESTRY)
	public float getGeneticDecay(IBeeGenome genome, float f) {
		for (AlvearyEffect ae : effectSet) {
			if (energy.containsAtLeast(ae.color, ae.requiredEnergy)) {
				f *= ae.decayFactor();
			}
		}
		return f;
	}

	@Override
	@ModDependent(ModList.FORESTRY)
	public boolean isSealed() {
		for (AlvearyEffect ae : effectSet) {
			if (energy.containsAtLeast(ae.color, ae.requiredEnergy)) {
				if (ae.isSealed())
					return true;
			}
		}
		return false;
	}

	@Override
	@ModDependent(ModList.FORESTRY)
	public boolean isSelfLighted() {
		for (AlvearyEffect ae : effectSet) {
			if (energy.containsAtLeast(ae.color, ae.requiredEnergy)) {
				if (ae.isSelfLit())
					return true;
			}
		}
		return false;
	}

	@Override
	@ModDependent(ModList.FORESTRY)
	public boolean isSunlightSimulated() {
		for (AlvearyEffect ae : effectSet) {
			if (energy.containsAtLeast(ae.color, ae.requiredEnergy)) {
				if (ae.isSkySimulated())
					return true;
			}
		}
		return false;
	}

	@Override
	public boolean isHellish() {
		for (AlvearyEffect ae : effectSet) {
			if (energy.containsAtLeast(ae.color, ae.requiredEnergy)) {
				if (ae.isHellish())
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
		IBeeHousing ibh = this.getMultiblockLogic().getController();
		if (ibh == null)
			return null;
		ItemStack is = ibh.getBeeInventory().getQueen();
		return is != null ? ReikaBeeHelper.getGenome(is) : null;
	}

	@ModDependent(ModList.FORESTRY)
	private boolean hasQueen() {
		return this.getBeeGenome() != null;
	}

	@ModDependent(ModList.FORESTRY)
	private boolean canQueenWork() {
		return this.isAlvearyComplete() && this.getMultiblockLogic().getController().getBeekeepingLogic().canWork();
	}

	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		this.getMultiblockLogic().readFromNBT(data);
	}

	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		this.getMultiblockLogic().writeToNBT(data);
	}

	@Override
	public final void validate() {
		super.validate();
		this.getMultiblockLogic().validate(worldObj, this);
	}

	@Override
	protected void onInvalidateOrUnload(World world, int x, int y, int z, boolean invalid) {
		super.onInvalidateOrUnload(world, x, y, z, invalid);
		if (invalid) {
			this.getMultiblockLogic().invalidate(world, this);
		}
		else {
			this.getMultiblockLogic().onChunkUnload(world, this);
		}
	}

	private static abstract class AlvearyEffect {

		public final CrystalElement color;
		public final int requiredEnergy;

		protected AlvearyEffect(CrystalElement e, int amt) {
			color = e;
			requiredEnergy = amt;
		}

		protected void tick(TileEntityLumenAlveary te) {

		}

		protected float productionFactor() {
			return 1;
		}

		protected float pollinationFactor() {
			return 1;
		}

		protected float lifespanFactor() {
			return 1;
		}

		protected float mutationFactor() {
			return 1;
		}

		protected float territoryFactor() {
			return 1;
		}

		protected float decayFactor() {
			return 1;
		}

		protected boolean isSealed() {
			return false;
		}

		protected boolean isSelfLit() {
			return false;
		}

		protected boolean isSkySimulated() {
			return false;
		}

		protected boolean isHellish() {
			return false;
		}

	}

	private static abstract class AccelerationEffect extends AlvearyEffect {

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

	private static class LightningProductionEffect extends AlvearyEffect {

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

	}

	private static class HistoryRewriteEffect extends AlvearyEffect {

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

	}

	private static class GeneticBalancingEffect extends AlvearyEffect {

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

		private void balanceGene(ItemStack queen, IBeeGenome ibg, EnumBeeChromosome gene) {
			ReikaBeeHelper.setGene(queen, ibg, gene, ibg.getActiveAllele(gene), true);
		}

		private boolean canBalance(ItemStack queen, IBeeGenome ibg, EnumBeeChromosome gene) {
			if (gene == EnumBeeChromosome.HUMIDITY)
				return false;
			IAllele primary = ibg.getActiveAllele(gene);
			IAllele secondary = ibg.getInactiveAllele(gene);
			return !primary.getUID().equals(secondary.getUID());
		}

	}

	private static class GeneticStabilityEffect extends AlvearyEffect {

		private GeneticStabilityEffect() {
			super(CrystalElement.WHITE, 20);
		}

		@Override
		protected float mutationFactor() {
			return 0;
		}

		@Override
		protected float decayFactor() {
			return 0.75F;
		}

	}

	private static class GeneticFluxEffect extends AlvearyEffect {

		private GeneticFluxEffect() {
			super(CrystalElement.BLACK, 60);
		}

		@Override
		protected float mutationFactor() {
			return 4;
		}

	}

	private static class GeneticImprovementEffect extends AlvearyEffect {

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
					EnumTolerance next = ReikaBeeHelper.getOneBetterTolerance(ibg.getToleranceTemp());
					if (next == null)
						break;
					ReikaBeeHelper.setGene(queen, ibg, gene, ReikaBeeHelper.getToleranceGene(next), true);
					break;
				}
				case HUMIDITY_TOLERANCE: {
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

	}

}
