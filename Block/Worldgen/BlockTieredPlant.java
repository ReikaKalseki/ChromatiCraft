/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Worldgen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromaClient;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.Interfaces.LoadRegistry;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.PlantDropManager;
import Reika.ChromatiCraft.Base.BlockChromaTiered;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.RotaryCraft.API.ItemFetcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class BlockTieredPlant extends BlockChromaTiered implements IPlantable, LoadRegistry {

	public static final int ARR_LENGTH = 7;

	private final IIcon[] front_icons = new IIcon[ARR_LENGTH];
	private final IIcon[] back_icons = new IIcon[ARR_LENGTH];

	public BlockTieredPlant(Material mat) {
		super(mat);
		this.setHardness(0);
		this.setResistance(2);
		stepSound = soundTypeGrass;
	}

	public static enum TieredPlants {

		FLOWER(ProgressStage.CRYSTALS, 0x33aaff),
		CAVE(ProgressStage.RUNEUSE, 0xffffff),
		LILY(ProgressStage.CHARGE, 0xff00ff),
		BULB(ProgressStage.MULTIBLOCK, 0x00ffff),
		DESERT(ProgressStage.PYLON, 0xffcc33),
		POD(ProgressStage.ALLOY, 0x827C1F),
		ROOT(ProgressStage.TURBOCHARGE, 0x871D00);

		public final ProgressStage level;
		public final int color;

		public static final TieredPlants[] list = values();

		private TieredPlants(ProgressStage lvl, int c/*, ItemStack is*/) {
			level = lvl;
			color = c;
			//drop = is;
		}

		public Coordinate generate(World world, int x, int z, Random r) {
			int y = 0;
			switch(this) {
				case BULB:
					y = world.provider.getAverageGroundLevel();
					for (int i = 0; i < 32; i++) {
						int dy = y+i;
						Block b = world.getBlock(x, dy+1, z);
						int meta = world.getBlockMetadata(x, dy+1, z);
						if (world.getBlock(x, dy, z) == Blocks.air && ReikaBlockHelper.isLeaf(b, meta)) {
							return new Coordinate(x, dy, z);
						}
					}
					break;
				case CAVE:
					for (int i = 0; i < 64; i++) {
						int dy = y+i;
						Block b = world.getBlock(x, dy+1, z);
						int meta = world.getBlockMetadata(x, dy+1, z);
						boolean flag = b == Blocks.stone || b.isReplaceableOreGen(world, x, dy+1, z, Blocks.stone) || ReikaBlockHelper.isOre(b, meta);
						if (flag && world.getBlock(x, dy, z) == Blocks.air) {
							return new Coordinate(x, dy, z);
						}
					}
					break;
				case FLOWER:
					y = world.getTopSolidOrLiquidBlock(x, z)-1;
					for (int i = -8; i < 8; i++) {
						int dy = y+i;
						boolean ground = world.getBlock(x, dy, z) == Blocks.dirt || world.getBlock(x, dy, z) == Blocks.grass;
						if (ground && world.getBlock(x, dy+1, z) == Blocks.air) {
							return new Coordinate(x, dy+1, z);
						}
					}
					break;
				case LILY:
					y = world.getTopSolidOrLiquidBlock(x, z);
					for (int i = -8; i < 38; i++) {
						int dy = y+i;
						boolean water = world.getBlock(x, dy, z) == Blocks.water && world.getBlockMetadata(x, dy, z) == 0;
						if (water && world.getBlock(x, dy+1, z) == Blocks.air && world.canBlockSeeTheSky(x, dy+1, z)) {
							return new Coordinate(x, dy+1, z);
						}
					}
					break;
				case DESERT:
					y = world.getTopSolidOrLiquidBlock(x, z)-1;
					for (int i = -8; i < 8; i++) {
						int dy = y+i;
						boolean ground = world.getBlock(x, dy, z) == Blocks.sand;
						if (ground && world.getBlock(x, dy+1, z) == Blocks.air) {
							return new Coordinate(x, dy+1, z);
						}
					}
					break;
				case POD: {
					x = ReikaMathLibrary.bitRound(x, 4)+7+rand.nextInt(2);
					z = ReikaMathLibrary.bitRound(z, 4)+7+rand.nextInt(2);
					Coordinate c = ReikaWorldHelper.findTreeNear(world, x, world.getTopSolidOrLiquidBlock(x, z), z, 7);
					if (c != null) {
						int ymin = c.yCoord;
						int ymax = c.yCoord;
						Coordinate c2 = c;
						while (ReikaBlockHelper.isWood(c2.getBlock(world), c2.getBlockMetadata(world))) {
							c2 = c2.offset(0, -1, 0);
							ymin--;
						}
						c2 = c;
						while (ReikaBlockHelper.isWood(c2.getBlock(world), c2.getBlockMetadata(world))) {
							c2 = c2.offset(0, 1, 0);
							ymax++;
						}
						ymax--;
						ymin++;
						int gy = ReikaRandomHelper.getRandomBetween(ymin, ymax);
						for (ForgeDirection dir : ReikaDirectionHelper.getRandomOrderedDirections(true)) {
							c2 = new Coordinate(c.xCoord, gy, c.zCoord).offset(dir, 1);
							if (c2.softBlock(world)) {
								return c2;
							}
						}
					}
					break;
				}
				case ROOT: {
					x = ReikaMathLibrary.bitRound(x, 4)+7+rand.nextInt(2);
					z = ReikaMathLibrary.bitRound(z, 4)+7+rand.nextInt(2);
					Coordinate c = ReikaWorldHelper.findTreeNear(world, x, world.getTopSolidOrLiquidBlock(x, z), z, 7);
					if (c != null) {
						while (ReikaBlockHelper.isWood(c.getBlock(world), c.getBlockMetadata(world))) {
							c = c.offset(0, -1, 0);
						}
						c = c.offset(0, 1, 0);
						for (ForgeDirection dir : ReikaDirectionHelper.getRandomOrderedDirections(true)) {
							Coordinate c2 = c.offset(dir, 1);
							if (c2.softBlock(world)) {
								return c2;
							}
						}
					}
					break;
				}
			}
			return null;
		}

		public int getGenerationCount() {
			switch(this) {
				case POD:
					return 6;
				case ROOT:
					return 3;
				case LILY:
					return 1;
				default:
					return 2;
			}
		}

		public int getGenerationChance() {
			switch(this) {
				case CAVE:
					return 2;
				case POD:
				case ROOT:
					return 4;
				default:
					return 5;
			}
		}

		public Block getBlock() {
			return ChromaBlocks.TIEREDPLANT.getBlockInstance();
		}

		public boolean isWaterPlaced() {
			return this == LILY;
		}

		private void registerDrops(BlockTieredPlant b) {
			PlantDropManager.instance.registerDrops(b, this.ordinal(), this.getDrop());
		}

		public ItemStack getDrop() {
			switch (this) {
				case FLOWER: return ChromaStacks.auraDust.copy();
				case CAVE: return ChromaStacks.purityDust.copy();
				case LILY: return ChromaStacks.elementDust.copy();
				case BULB: return ChromaStacks.resonanceDust.copy();
				case DESERT: return ChromaStacks.beaconDust.copy();
				case POD: return ChromaStacks.glowbeans.copy();
				case ROOT: return ChromaStacks.boostroot.copy();
			}
			return null;
		}
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		TieredPlants p = TieredPlants.list[meta];
		if (p == TieredPlants.POD) {
			float maxX = 0.75F;
			float maxY = 0.75F;
			float maxZ = 0.75F;
			float minX = 0.25F;
			float minY = 0.25F;
			float minZ = 0.25F;

			if (ReikaBlockHelper.isWood(world, x, y-1, z))
				minY = 0;
			if (ReikaBlockHelper.isWood(world, x, y+1, z))
				maxY = 1;
			if (ReikaBlockHelper.isWood(world, x-1, y, z))
				minX = 0;
			if (ReikaBlockHelper.isWood(world, x+1, y, z))
				maxX = 1;
			if (ReikaBlockHelper.isWood(world, x, y, z-1))
				minZ = 0;
			if (ReikaBlockHelper.isWood(world, x, y, z+1))
				maxZ = 1;

			this.setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
		}
		else {
			this.setBlockBounds(0, 0, 0, 1, 1, 1);
		}
	}

	public void onLoad() {
		for (int i = 0; i < TieredPlants.list.length; i++) {
			TieredPlants.list[i].registerDrops(this);
		}
	}

	@Override
	public final int getLightValue(IBlockAccess iba, int x, int y, int z) {
		int l = !(iba instanceof World && !((World)iba).isRemote) ? 4 : 0;
		return ModList.COLORLIGHT.isLoaded() ? ReikaColorAPI.getPackedIntForColoredLight(TieredPlants.list[iba.getBlockMetadata(x, y, z)].color, l) : l;
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs c, List li) {
		for (int i = 0; i < TieredPlants.list.length; i++) {
			li.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public Collection<ItemStack> getHarvestResources(World world, int x, int y, int z, int fortune, EntityPlayer player) {
		ArrayList<ItemStack> li = new ArrayList();
		int n = 1;
		if (ItemFetcher.isPlayerHoldingBedrockPick(player))
			fortune = 5;
		fortune = Math.max(fortune, EnchantmentHelper.getLootingModifier(player));
		TieredPlants p = TieredPlants.list[world.getBlockMetadata(x, y, z)];
		ItemStack is = p.getDrop();
		switch(p) {
			case FLOWER:
				n = 1+fortune*rand.nextInt(8);
				break;
			case CAVE:
				n = (1+rand.nextInt(3))*(1+fortune*(1+rand.nextInt(4)));
				break;
			case LILY:
				n = 4*(1+fortune*fortune/2);
				break;
			case BULB:
				n = 1+fortune*4+2*rand.nextInt(9);
				break;
			case DESERT:
				n = (1+fortune*fortune)+2*rand.nextInt(5);
				break;
			case POD:
				n = 2+rand.nextInt(1+fortune*3/2);
				break;
			case ROOT:
				n = 1+rand.nextInt(1+fortune)/2;
				break;
		}
		for (int i = 0; i < n; i++) {
			li.add(is.copy());
		}
		return li;
	}

	@Override
	public ProgressStage getProgressStage(int meta) {
		return TieredPlants.list[meta].level;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
	{
		if (this.isPlayerSufficientTier(world, x, y, z, Minecraft.getMinecraft().thePlayer)) {
			return super.getSelectedBoundingBoxFromPool(world, x, y, z);
		}
		else {
			return AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0);
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public int getRenderType() {
		return ChromatiCraft.proxy.plantRender;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean canRenderInPass(int pass) {
		ChromaClient.plant.renderPass = pass;
		return pass <= 1;
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random r) {
		if (world.getTotalWorldTime()%2 == 0 && this.isPlayerSufficientTier(world, x, y, z, Minecraft.getMinecraft().thePlayer)) {
			int meta = world.getBlockMetadata(x, y, z);
			switch(meta) {
				case 0: {
					double vx = ReikaRandomHelper.getRandomPlusMinus(0, 0.005);
					double vz = ReikaRandomHelper.getRandomPlusMinus(0, 0.005);
					int g = r.nextInt(255);
					EntityBlurFX fx = new EntityBlurFX(world, x+0.5, y+0.95, z+0.5, vx, 0, vz).setGravity(0.015F).setColor(0, g, 255).setScale(2);
					Minecraft.getMinecraft().effectRenderer.addEffect(fx);
					break;
				}
				case 1: {
					float g = 0.02F+0.005F*r.nextFloat();
					EntityBlurFX fx = new EntityBlurFX(world, x+0.5, y+0.65, z+0.5, 0, 0, 0).setGravity(g).setScale(2);
					Minecraft.getMinecraft().effectRenderer.addEffect(fx);
					break;
				}
				case 2: {
					float g = 0.04F+0.01F*r.nextFloat();
					int red = r.nextInt(255);
					EntityBlurFX fx = new EntityBlurFX(world, x+0.5, y+0.375, z+0.5, 0, 0, 0).setColor(red, 0, 255).setGravity(-g).setScale(4);
					Minecraft.getMinecraft().effectRenderer.addEffect(fx);
					break;
				}
				case 3: {
					float g = 0.02F+0.005F*r.nextFloat();
					int gr = r.nextInt(255);
					double px = x+rand.nextDouble();
					double pz = z+rand.nextDouble();
					EntityBlurFX fx = new EntityBlurFX(world, px, y+0.75, pz, 0, 0, 0).setColor(0, 255, gr).setGravity(g).setScale(2);
					Minecraft.getMinecraft().effectRenderer.addEffect(fx);
					break;
				}
				case 4: {
					float g = 0.02F+0.005F*r.nextFloat();
					int gr = r.nextInt(255);
					double px = ReikaRandomHelper.getRandomPlusMinus(x+0.5, 0.25);
					double pz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, 0.25);
					EntityBlurFX fx = new EntityBlurFX(world, px, y+0.75, pz, 0, 0, 0).setColor(255, gr, 0).setGravity(-g).setScale(2);
					Minecraft.getMinecraft().effectRenderer.addEffect(fx);
					break;
				}
				case 5: {

					break;
				}
				case 6: {

					break;
				}
			}
		}
	}

	public IIcon getOverlay(int meta) {
		return front_icons[meta];
	}

	public IIcon getBacking(int meta) {
		return back_icons[meta];
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return this.getBacking(meta);
	}

	/*

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int s) {
		return this.isPlayerSufficientTier(world, x, y, z, Minecraft.getMinecraft().thePlayer) ? this.getIcon(s, world.getBlockMetadata(x, y, z)) : ChromaIcons.TRANSPARENT.getIcon();
	}*/

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < ARR_LENGTH; i++) {
			front_icons[i] = ico.registerIcon("chromaticraft:plant/tierplant_"+i+"_front");
			back_icons[i] = ico.registerIcon("chromaticraft:plant/tierplant_"+i+"_back");
		}
	}

	public boolean canPlaceAt(World world, int x, int y, int z, ItemStack is) {
		return this.isValidLocation(world, x, y, z, is.getItemDamage());
	}

	protected boolean canPlaceBlockOn(World world, int x, int y, int z, TieredPlants p) {
		Block b = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		switch(p) {
			case FLOWER:
				return b == Blocks.grass || b == Blocks.dirt || b == Blocks.farmland;
			case BULB:
				return b.getMaterial() == Material.leaves;
			case CAVE:
				return b == Blocks.stone || ReikaBlockHelper.isOre(b, meta);
			case LILY:
				return b == Blocks.water || b == Blocks.flowing_water;
			case DESERT:
				return b == Blocks.sand;
			case POD:
				return ReikaBlockHelper.isWood(b, meta);
			case ROOT:
				return b == Blocks.grass || b == Blocks.dirt;
		}
		return false;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block b) {
		super.onNeighborBlockChange(world, x, y, z, b);
		this.checkAndDropBlock(world, x, y, z);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random r) {
		this.checkAndDropBlock(world, x, y, z);
	}

	protected void checkAndDropBlock(World world, int x, int y, int z)
	{
		if (!this.canBlockStay(world, x, y, z)) {
			this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			world.setBlock(x, y, z, getBlockById(0), 0, 2);
		}
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		return this.isValidLocation(world, x, y, z, world.getBlockMetadata(x, y, z));
	}

	private boolean isValidLocation(World world, int x, int y, int z, int meta) {
		TieredPlants p = TieredPlants.list[meta];
		switch(p) {
			case FLOWER:
			case LILY:
			case DESERT:
			case ROOT:
				return this.canPlaceBlockOn(world, x, y-1, z, p);
			case BULB:
			case CAVE:
				return this.canPlaceBlockOn(world, x, y+1, z, p);
			case POD:
				return this.canPlaceBlockOn(world, x, y+1, z, p) || this.canPlaceBlockOn(world, x+1, y, z, p) || this.canPlaceBlockOn(world, x-1, y, z, p) || this.canPlaceBlockOn(world, x, y, z+1, p) || this.canPlaceBlockOn(world, x, y, z-1, p);
		}
		return false;
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		switch(TieredPlants.list[meta]) {
			case FLOWER:
			case BULB:
				return EnumPlantType.Plains;
			case CAVE:
				return EnumPlantType.Cave;
			case LILY:
				return EnumPlantType.Water;
			case DESERT:
				return EnumPlantType.Desert;
			case ROOT:
			case POD:
				return EnumPlantType.Plains;
		}
		return null;
	}

	@Override
	public Block getPlant(IBlockAccess world, int x, int y, int z) {
		return this;
	}

	@Override
	public int getPlantMetadata(IBlockAccess world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z);
	}

	@Override
	protected ItemStack getWailaDisguise(int meta) {
		return new ItemStack(Blocks.air);
	}
}
