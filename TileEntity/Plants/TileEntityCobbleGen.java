/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Plants;

import java.util.Collection;
import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaAux;
import Reika.ChromatiCraft.Auxiliary.Interfaces.OperationInterval;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityMagicPlant;
import Reika.ChromatiCraft.Block.BlockPylonStructure.StoneTypes;
import Reika.ChromatiCraft.Block.Worldgen.BlockCliffStone.Variants;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.ItemElementCalculator;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityRuneFX;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.BlockMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.CollectionType;
import Reika.DragonAPI.Instantiable.Effects.EntityBlurFX;
import Reika.DragonAPI.Instantiable.Effects.EntityFluidFX;
import Reika.DragonAPI.Instantiable.ParticleController.AttractiveMotionController;
import Reika.DragonAPI.Interfaces.MotionController;
import Reika.DragonAPI.Interfaces.Block.FluidBlockSurrogate;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class TileEntityCobbleGen extends TileEntityMagicPlant implements OperationInterval {

	private final MultiMap<String, Coordinate> fluidLocations = new MultiMap(CollectionType.HASHSET);

	private final StepTimer areaScan = new StepTimer(100);
	//private final StepTimer growthTimer = new StepTimer(7200);

	public static final int XZ_RANGE = 4;
	public static final int Y_RANGE = 5;
	public static final int RANDOM_SCANS = 2;

	private FluidMix activeRecipe = null;
	private int recipeTick = 0;
	private Coordinate primaryLocation = null;
	private Coordinate secondaryLocation = null;

	private OutputModifier modifier;

	private MotionController particleMotion;

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.COBBLEGEN;
	}

	@Override
	public ForgeDirection getGrowthDirection() {
		return ForgeDirection.DOWN;
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		areaScan.setTick(areaScan.getCap()-1);
		this.doScan(world, x, y, z);
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

		NBT.setInteger("recipeTick", recipeTick);
		NBT.setInteger("recipe", activeRecipe != null ? activeRecipe.ordinal() : -1);

		NBT.setInteger("modifier", modifier != null ? modifier.ordinal() : -1);

		if (primaryLocation != null)
			primaryLocation.writeToNBT("loc1", NBT);
		if (secondaryLocation != null)
			secondaryLocation.writeToNBT("loc2", NBT);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

		recipeTick = NBT.getInteger("recipeTick");
		int r = NBT.getInteger("recipe");
		activeRecipe = r >= 0 ? FluidMix.list[r] : null;

		int m = NBT.getInteger("modifier");
		modifier = m >= 0 ? OutputModifier.list[m] : null;

		if (NBT.hasKey("loc2"))
			primaryLocation = Coordinate.readFromNBT("loc1", NBT);
		if (NBT.hasKey("loc1"))
			secondaryLocation = Coordinate.readFromNBT("loc2", NBT);
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (!world.isRemote) {
			this.doScan(world, x, y, z);
		}
		else {
			this.doParticles(world, x, y, z);
		}

		if (recipeTick > 0) {
			this.doRecipeTick(world, x, y, z);
		}
		else {
			if (!world.isRemote) {
				for (int i = 0; i < FluidMix.list.length; i++) {
					FluidMix f = FluidMix.list[i];
					Coordinate primary = this.getFluid(world, x, y, z, f.primaryFluid);
					Coordinate secondary = this.getFluid(world, x, y, z, f.secondaryFluid);
					if (primary != null && secondary != null) {
						recipeTick = f.duration/3;
						activeRecipe = f;
						primaryLocation = primary;
						secondaryLocation = secondary;
						break;
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void doParticles(World world, int x, int y, int z) {
		if (modifier != null) {
			modifier.doParticles(world, x, y, z, this);
		}
	}

	private void doRecipeTick(World world, int x, int y, int z) {
		if (!world.isRemote) {
			recipeTick--;
		}
		if (recipeTick == 0) {
			this.craft(world, x, y, z);
		}
		else {
			if (world.isRemote) {
				this.doRecipeParticles(world, x, y, z);
			}
			else {
				FluidStack f1 = this.getFluidAtBlock(world, primaryLocation.xCoord, primaryLocation.yCoord, primaryLocation.zCoord);
				FluidStack f2 = this.getFluidAtBlock(world, secondaryLocation.xCoord, secondaryLocation.yCoord, secondaryLocation.zCoord);
				if (f1 != null && f1.getFluid().getName().equals(activeRecipe.primaryFluid) && f1.amount >= activeRecipe.requiredPrimaryAmount && f2 != null && f2.getFluid().getName().equals(activeRecipe.secondaryFluid) && f2.amount >= activeRecipe.requiredSecondaryAmount) {

				}
				else {
					this.terminateCrafting(world, x, y, z, false);
				}
			}
		}
	}

	private void craft(World world, int x, int y, int z) {
		int num = Math.min(64, ReikaMathLibrary.intpow2(2, this.getAccelerationPlants()));
		ItemStack is = ReikaItemHelper.getSizedItemStack(activeRecipe.output, num);
		if (modifier != null)
			is = modifier.getOutput(is);
		EntityItem ei = ReikaItemHelper.dropItem(world, x+0.5, y+0.125, z+0.5, is);
		ei.lifespan = 300;
		ei.motionX = ei.motionZ = ei.motionY = 0;

		if (activeRecipe.consumePrimaryFluid > 0) {
			Block b1 = primaryLocation.getBlock(world);
			if (b1 instanceof FluidBlockSurrogate) {
				FluidBlockSurrogate fb = (FluidBlockSurrogate)b1;
				Fluid f = FluidRegistry.getFluid(activeRecipe.primaryFluid);
				if (fb.supportsQuantization(world, primaryLocation.xCoord, primaryLocation.yCoord, primaryLocation.zCoord)) {
					fb.drain(world, primaryLocation.xCoord, primaryLocation.yCoord, primaryLocation.zCoord, f, activeRecipe.requiredPrimaryAmount, true);
				}
				else if (ReikaRandomHelper.doWithChance(activeRecipe.consumePrimaryFluid)) {
					fb.drain(world, primaryLocation.xCoord, primaryLocation.yCoord, primaryLocation.zCoord, f, 1000, true);
				}
			}
			else if (ReikaRandomHelper.doWithChance(activeRecipe.consumePrimaryFluid)) {
				primaryLocation.setBlock(world, Blocks.air);
			}
		}

		if (activeRecipe.consumeSecondaryFluid > 0) {
			Block b2 = secondaryLocation.getBlock(world);
			if (b2 instanceof FluidBlockSurrogate) {
				FluidBlockSurrogate fb = (FluidBlockSurrogate)b2;
				Fluid f = FluidRegistry.getFluid(activeRecipe.secondaryFluid);
				if (fb.supportsQuantization(world, secondaryLocation.xCoord, secondaryLocation.yCoord, secondaryLocation.zCoord)) {
					fb.drain(world, secondaryLocation.xCoord, secondaryLocation.yCoord, secondaryLocation.zCoord, f, activeRecipe.requiredSecondaryAmount, true);
				}
				else if (ReikaRandomHelper.doWithChance(activeRecipe.consumeSecondaryFluid)) {
					fb.drain(world, secondaryLocation.xCoord, secondaryLocation.yCoord, secondaryLocation.zCoord, f, 1000, true);
				}
			}
			else if (ReikaRandomHelper.doWithChance(activeRecipe.consumeSecondaryFluid)) {
				secondaryLocation.setBlock(world, Blocks.air);
			}
		}

		ReikaSoundHelper.playSoundAtBlock(world, x, y, z, "random.fizz", 0.75F, 2F);
		this.terminateCrafting(world, x, y, z, true);
	}

	private void terminateCrafting(World world, int x, int y, int z, boolean success) {
		ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.COBBLEGENEND.ordinal(), this, 64, activeRecipe.ordinal(), success ? 1 : 0);
		activeRecipe = null;
		recipeTick = 0;
		primaryLocation = null;
		secondaryLocation = null;
	}

	@SideOnly(Side.CLIENT)
	public void endCraftingFX(World world, int x, int y, int z, int recipe, boolean success) {
		FluidMix f = FluidMix.list[recipe];
		double v = success ? 0.0625 : 0.375;
		ElementTagCompound tag = ItemElementCalculator.instance.getValueForItem(f.output);
		int n = 5;
		int a = rand.nextInt(n);
		for (int i = a; i < 360; i += n) {
			EntityBlurFX fx;
			double[] vel = ReikaPhysicsHelper.polarToCartesian(v, 0, i);
			if (success) {
				fx = new EntityCCBlurFX(world, x+0.5, y+0.125, z+0.5, vel[0], -0.125, vel[2]).setGravity(-0.125F);
			}
			else {
				fx = new EntityCCBlurFX(world, x+0.5, y+0.125, z+0.5, vel[0], 0, vel[2]).setIcon(ChromaIcons.SPARKLE).setNoSlowdown();
			}
			int c = tag == null || tag.isEmpty() ? 0x22aaff : ReikaJavaLibrary.getRandomCollectionEntry(rand, tag.elementSet()).getColor();
			fx.setColor(c).setRapidExpand();
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@SideOnly(Side.CLIENT)
	private void doRecipeParticles(World world, int x, int y, int z) {
		if (activeRecipe == null || primaryLocation == null || secondaryLocation == null)
			return;
		double px = ReikaRandomHelper.getRandomPlusMinus(primaryLocation.xCoord+0.5, 0.35);
		double pz = ReikaRandomHelper.getRandomPlusMinus(primaryLocation.zCoord+0.5, 0.35);
		EntityFluidFX fx1 = new EntityFluidFX(world, px, primaryLocation.yCoord+0.85, pz, FluidRegistry.getFluid(activeRecipe.primaryFluid));

		px = ReikaRandomHelper.getRandomPlusMinus(secondaryLocation.xCoord+0.5, 0.35);
		pz = ReikaRandomHelper.getRandomPlusMinus(secondaryLocation.zCoord+0.5, 0.35);
		EntityFluidFX fx2 = new EntityFluidFX(world, px, secondaryLocation.yCoord+0.85, pz, FluidRegistry.getFluid(activeRecipe.secondaryFluid));

		fx1.setMotionController(new AttractiveMotionController(xCoord+0.5, yCoord-0.375, zCoord+0.5, 0.0625/24D, ReikaRandomHelper.getRandomPlusMinus(0.155, 0.005), ReikaRandomHelper.getRandomPlusMinus(0.98, 0.005)));
		fx2.setMotionController(new AttractiveMotionController(xCoord+0.5, yCoord-0.375, zCoord+0.5, 0.0625/24D, ReikaRandomHelper.getRandomPlusMinus(0.155, 0.005), ReikaRandomHelper.getRandomPlusMinus(0.98, 0.005)));

		fx1.setLife(70);
		fx2.setLife(70);

		Minecraft.getMinecraft().effectRenderer.addEffect(fx1);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx2);

		EntityRuneFX fx = null;
		float g = (float)ReikaRandomHelper.getRandomPlusMinus(0.0625, 0.03125);
		float s = (float)ReikaRandomHelper.getRandomPlusMinus(1.25, 0.25);
		if (rand.nextBoolean()) {
			CrystalElement e = ChromaAux.getRune(FluidRegistry.getFluid(rand.nextBoolean() ? activeRecipe.primaryFluid : activeRecipe.secondaryFluid));
			fx = new EntityRuneFX(world, x+rand.nextDouble(), y+rand.nextDouble(), z+rand.nextDouble(), e).setGravity(g).setScale(s);
		}
		else {
			g = -g;
			int dy = y-1;
			while (dy > 0 && world.getBlock(x, dy, z).isAir(world, x, dy, z))
				dy--;
			ElementTagCompound tag = ItemElementCalculator.instance.getValueForItem(activeRecipe.output);
			CrystalElement e = tag != null && !tag.isEmpty() ? ReikaJavaLibrary.getRandomCollectionEntry(rand, tag.elementSet()) : null;
			if (e != null)
				fx = new EntityRuneFX(world, x+rand.nextDouble(), dy+1, z+rand.nextDouble(), e).setGravity(g).setScale(s);
		}
		if (fx != null)
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	private Coordinate getFluid(World world, int x, int y, int z, String f) {
		Collection<Coordinate> li = fluidLocations.get(f);
		boolean flag = !li.isEmpty();
		Iterator<Coordinate> it = li.iterator();
		while (it.hasNext()) {
			Coordinate c = it.next();
			FluidStack at = this.getFluidAtBlock(world, c.xCoord, c.yCoord, c.zCoord);
			if (at != null && at.getFluid().getName().equals(f)) {
				return c;
			}
			else {
				it.remove();
			}
		}
		if (li.isEmpty())
			areaScan.setTick(areaScan.getCap()-1);
		return null;
	}

	private void doScan(World world, int x, int y, int z) {
		areaScan.update();
		if (areaScan.checkCap()) {
			fluidLocations.clear();
			for (int i = -XZ_RANGE; i <= XZ_RANGE; i++) {
				for (int k = -XZ_RANGE; k <= XZ_RANGE; k++) {
					int dx = x+i;
					int dz = z+k;
					this.scanPosition(world, dx, y, dz);
				}
			}
		}
		else {
			for (int i = 0; i < RANDOM_SCANS; i++) {
				int dx = ReikaRandomHelper.getRandomPlusMinus(x, XZ_RANGE);
				int dz = ReikaRandomHelper.getRandomPlusMinus(z, XZ_RANGE);
				this.scanPosition(world, dx, y, dz);
			}
		}

		modifier = this.getModifier(world, x, y, z);
	}

	private OutputModifier getModifier(World world, int x, int y, int z) {
		for (int i = 1; i < Y_RANGE; i++) {
			BlockKey bk = BlockKey.getAt(world, x, y-i, z);
			OutputModifier mod = OutputModifier.getByBlock(bk);
			if (mod != null) {
				return mod;
			}
			else if (bk.blockID.isOpaqueCube())
				return null;
		}
		return null;
	}

	private void scanPosition(World world, int dx, int y, int dz) {
		int dy = this.getYPosition(world, dx, y, dz);
		if (dy != -1) {
			FluidStack f = this.getFluidAtBlock(world, dx, dy, dz);
			if (f != null) {
				fluidLocations.addValue(f.getFluid().getName(), new Coordinate(dx, dy, dz));
			}
		}
	}

	private FluidStack getFluidAtBlock(World world, int dx, int dy, int dz) {
		Block b = world.getBlock(dx, dy, dz);
		if (ReikaWorldHelper.isLiquidSourceBlock(world, dx, dy, dz)) {
			Fluid f = FluidRegistry.lookupFluidForBlock(b);
			return f != null ? new FluidStack(f, 1000) : null;
		}
		else if (b instanceof FluidBlockSurrogate) {
			FluidBlockSurrogate fb = (FluidBlockSurrogate)b;
			Fluid f = fb.getFluid(world, dx, dy, dz);
			return f != null ? new FluidStack(f, fb.drain(world, dx, dy, dz, f, 1000, false)) : null;
		}
		return null;
	}

	private int getYPosition(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		if (!b.isAir(world, x, y, z) && ChromaTiles.getTileFromIDandMetadata(b, world.getBlockMetadata(x, y, z)) != this.getTile())
			return -1;
		int d = 0;
		while (y > 0 && (b.isAir(world, x, y, z) || ChromaTiles.getTileFromIDandMetadata(b, world.getBlockMetadata(x, y, z)) == this.getTile())) {
			y--;
			d++;
			b = world.getBlock(x, y, z);
		}
		return d <= Y_RANGE ? y : -1;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	private static enum FluidMix {
		COBBLESTONE(FluidRegistry.WATER, FluidRegistry.LAVA, new ItemStack(Blocks.cobblestone), 10, 20, 0),
		CRYSTALSTONE("chroma", "lava", ChromaBlocks.PYLONSTRUCT.getStackOfMetadata(StoneTypes.SMOOTH.ordinal()), 50, 25F, 50F),
		//ALLOY1("iron.molten", "lava", ChromaBlocks.PYLONSTRUCT.getStackOfMetadata(StoneTypes.SMOOTH.ordinal()), 10, 10F, 20F),
		CLIFFSTONE("luma", "lava", ChromaBlocks.CLIFFSTONE.getStackOfMetadata(Variants.STONE.getMeta(false, false)), 120, 0F, 10F),
		ENDSTONE("ender", "lava", new ItemStack(Blocks.end_stone), 80, 1F, 20F),
		;

		private final ItemStack output;
		private final String primaryFluid;
		private final String secondaryFluid;
		private final int duration;
		private final float consumePrimaryFluid;
		private final float consumeSecondaryFluid;

		private final int requiredPrimaryAmount;
		private final int requiredSecondaryAmount;

		private static final FluidMix[] list = values();

		private FluidMix(Fluid f1, Fluid f2, ItemStack is, int t, float c1, float c2) {
			this(f1.getName(), f2.getName(), is, t, c1, c2);
		}

		private FluidMix(String f1, String f2, ItemStack is, int t, float c1, float c2) {
			primaryFluid = f1;
			secondaryFluid = f2;
			output = is;
			duration = t;
			consumePrimaryFluid = c1;
			consumeSecondaryFluid = c2;

			requiredPrimaryAmount = Math.max(1, (int)(1000*consumePrimaryFluid/100D));
			requiredSecondaryAmount = Math.max(1, (int)(1000*consumeSecondaryFluid/100D));
		}
	}

	private static enum OutputModifier {
		COBBLESMELT(ChromaTiles.HEATLILY),
		;

		private final BlockKey block;

		private static final OutputModifier[] list = values();
		private static final BlockMap<OutputModifier> map = new BlockMap();

		private OutputModifier(ChromaTiles c) {
			this(new BlockKey(c.getBlock(), c.getBlockMetadata()));
		}

		public static OutputModifier getByBlock(BlockKey bk) {
			return map.get(bk);
		}

		private OutputModifier(BlockKey b) {
			block = b;
		}

		private ItemStack getOutput(ItemStack in) {
			switch(this) {
				case COBBLESMELT:
					if (ReikaItemHelper.matchStackWithBlock(in, Blocks.cobblestone))
						return new ItemStack(Blocks.stone, in.stackSize, 0);
					break;
				default:
					break;
			}
			return in;
		}

		@SideOnly(Side.CLIENT)
		private void doParticles(World world, int x, int y, int z, TileEntityCobbleGen te) {
			switch(this) {
				case COBBLESMELT:
					double a = 0;//Math.toRadians((this.getTicksExisted()*2)%360);
					int n = 3;
					int sp = 360/n;
					double r = 0.75+0.25*Math.sin(te.getTicksExisted()/10D);
					for (int i = 0; i < 360; i += sp) {
						double ri = Math.toRadians(i);
						double dx = x+0.5+r*Math.sin(a+ri);
						double dy = y-4;
						double dz = z+0.5+r*Math.cos(a+ri);
						EntityFX fx = new EntityCCBlurFX(CrystalElement.ORANGE, world, dx, dy, dz, 0, 0.1875, 0).setIcon(ChromaIcons.TRIDOT).setScale(2.5F).setNoSlowdown().setLife(25);
						Minecraft.getMinecraft().effectRenderer.addEffect(fx);
					}
					break;
				default:
					break;
			}
		}

		static {
			for (int i = 0; i < list.length; i++) {
				BlockKey b = list[i].block;
				map.put(b, list[i]);
			}
		}
	}

	@Override
	public float getOperationFraction() {
		return activeRecipe == null ? 0 : recipeTick/(float)activeRecipe.duration;
	}

	@Override
	public OperationState getState() {
		return activeRecipe != null ? OperationState.RUNNING : OperationState.INVALID;
	}

	@Override
	public boolean isPlantable(World world, int x, int y, int z) {
		return (world.getBlock(x, y+1, z).isSideSolid(world, x, y+1, z, ForgeDirection.DOWN) && world.getBlock(x, y+1, z).getMaterial().isSolid()) || ChromaTiles.getTile(world, x, y+1, z) == ChromaTiles.PLANTACCEL;
	}

}
