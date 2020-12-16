/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dimension;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.Interfaces.MinerBlock;
import Reika.ChromatiCraft.Auxiliary.Interfaces.DecoType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Render.ISBRH.DimensionDecoRenderer;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.ChromatiCraft.World.Dimension.DimensionTuningManager;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaPotionHelper;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockDimensionDeco extends Block implements MinerBlock {

	private static final ArrayList<IIcon>[] icons = new ArrayList[16];

	private static final Random rand = new Random();

	public static enum DimDecoTypes implements DecoType {
		MIASMA(1),
		FLOATSTONE(2),
		AQUA(1),
		LIFEWATER(1),
		LATTICE(1),
		GEMSTONE(3),
		CRYSTALLEAF(3),
		OCEANSTONE(2),
		CLIFFGLASS(2),
		GLOWCAVE(10);

		public final int numIcons;

		public static DimDecoTypes[] list = values();

		private DimDecoTypes(int n) {
			numIcons = n;
		}

		public ItemStack getItem() {
			return new ItemStack(ChromaBlocks.DIMGEN.getBlockInstance(), 1, this.ordinal());
		}

		public boolean hasBlockRender() {
			return this == FLOATSTONE || this == GEMSTONE || this == CRYSTALLEAF || this == OCEANSTONE || this == CLIFFGLASS || this == GLOWCAVE;
		}

		public List<IIcon> getIcons(IBlockAccess iba, int x, int y, int z, int pass, Random rand) {
			List<IIcon> li = this.getItemIcons(pass);
			if (this == GLOWCAVE) {
				li.clear();
				if (pass == 1) {
					//int idx = new Coordinate(x, y, z).hashCode();
					//idx = ((idx%16)+16)%16;
					int idx = rand.nextInt(16);
					li.add(icons[this.ordinal()].get(2+idx));
				}
			}
			return li;
		}

		public List<IIcon> getItemIcons(int pass) {
			if (icons[this.ordinal()] == null) {
				ChromatiCraft.logger.logError("Dimension Deco Type "+this+" is missing icons!");
				return ReikaJavaLibrary.makeListFrom(Blocks.bedrock.blockIcon, Blocks.emerald_block.blockIcon, Blocks.obsidian.blockIcon, Blocks.mob_spawner.blockIcon);
			}
			List<IIcon> li = new ArrayList();
			int idx = 0;
			for (IIcon ico : icons[this.ordinal()]) {
				if (this.renderIconInPass(idx, pass)) {
					li.add(ico);
				}
				idx++;
			}
			return li;
		}

		private boolean renderIconInPass(int idx, int pass) {
			switch(this) {
				case CRYSTALLEAF:
				case CLIFFGLASS:
					return idx >= 1 ? pass == 1 : pass == 0;
				case MIASMA:
				case LIFEWATER:
					return pass == 1;
				case GLOWCAVE:
					return idx == pass*2;
				default:
					return pass == 0;
			}
		}

		public boolean requiresPickaxe() {
			return this == FLOATSTONE || this == GEMSTONE || this == OCEANSTONE || this == CLIFFGLASS;
		}

		public boolean canSilkTouch() {
			switch(this) {
				//case FLOATSTONE:
				case MIASMA:
				case LATTICE:
				case LIFEWATER:
				case AQUA:
					return false;
				default:
					return true;
			}
		}

		private void addDrops(World world, int x, int y, int z, int metadata, int fortune, ArrayList<ItemStack> li, EntityPlayer ep) {
			int n = 1;
			switch(this) {
				case GLOWCAVE:
					n = ReikaRandomHelper.getRandomBetween(1, 6); //no break
				default:
					//float f = ep == null ? 1 : DimensionTuningManager.instance.getTunedDropRates(ep);
					if (ep != null)
						n = DimensionTuningManager.instance.getTunedDropCount(ep, n, 1, this.getMaxDrops());
					for (int i = 0; i < n; i++)
						li.add(ChromaItems.DIMGEN.getStackOfMetadata(this.ordinal()));
			}
		}

		private int getMaxDrops() {
			switch(this) {
				case AQUA:
					return 24;
				case CRYSTALLEAF:
					return 12;
				case FLOATSTONE:
					return 3;
				case GLOWCAVE:
					return 18;
				case OCEANSTONE:
					return 2;
				default:
					return 1;
			}
		}

		public boolean hasSecondaryIcons() {
			switch(this) {
				case GLOWCAVE:
					return true;
				default:
					return false;
			}
		}
	}

	public BlockDimensionDeco(Material mat) {
		super(mat);
		this.setCreativeTab(ChromatiCraft.tabChromaGen);
		this.setResistance(5);
		this.setHardness(0.75F);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess iba, int x, int y, int z, int s)
	{
		ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[s];
		return iba.getBlock(x, y, z) != this || iba.getBlockMetadata(x, y, z) != iba.getBlockMetadata(x-dir.offsetX, y-dir.offsetY, z-dir.offsetZ);
	}

	@Override
	public float getPlayerRelativeBlockHardness(EntityPlayer ep, World world, int x, int y, int z) {
		if (!DimensionTuningManager.TuningThresholds.DECOHARVEST.isSufficientlyTuned(ep))
			return -1;
		return super.getPlayerRelativeBlockHardness(ep, world, x, y, z);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor(int meta) {
		if (meta == DimDecoTypes.GEMSTONE.ordinal() && Minecraft.getMinecraft().theWorld != null) {
			EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
			return getGemStoneColor(Minecraft.getMinecraft().theWorld, MathHelper.floor_double(ep.posX), MathHelper.floor_double(ep.posY), MathHelper.floor_double(ep.posZ));
		}
		return 0xffffff;
	}

	@Override
	public int colorMultiplier(IBlockAccess iba, int x, int y, int z) {
		int meta = iba.getBlockMetadata(x, y, z);
		if (meta == DimDecoTypes.GEMSTONE.ordinal() || meta == DimDecoTypes.CRYSTALLEAF.ordinal()) {
			return getGemStoneColor(iba, x, y, z);
		}
		return 0xffffff;
	}

	public static int getGemStoneColor(IBlockAccess iba, int x, int y, int z) {
		return ReikaColorAPI.getModifiedHue(0xff0000, (x+z*3/2)*4);
	}

	@Override
	public int getLightValue(IBlockAccess iba, int x, int y, int z) {
		int meta = iba.getBlockMetadata(x, y, z);
		if (meta == DimDecoTypes.GEMSTONE.ordinal()) {
			return 0;
		}
		if (meta == DimDecoTypes.CRYSTALLEAF.ordinal()) {
			return 0;
		}
		if (meta == DimDecoTypes.CLIFFGLASS.ordinal()) {
			return 12;
		}
		return 0;
	}

	@Override
	public int getMixedBrightnessForBlock(IBlockAccess iba, int x, int y, int z) {
		int meta = iba.getBlockMetadata(x, y, z);
		if (meta == DimDecoTypes.GEMSTONE.ordinal() || meta == DimDecoTypes.OCEANSTONE.ordinal()) {
			return iba.getLightBrightnessForSkyBlocks(x, y, z, 15);
		}
		return super.getMixedBrightnessForBlock(iba, x, y, z);
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		if (icons[meta] == null) {
			ChromatiCraft.logger.logError("Dimension Deco Type "+DimDecoTypes.list[meta]+" is missing icons!");
			return Blocks.bedrock.blockIcon;
		}
		return icons[meta].get(0);
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < DimDecoTypes.list.length; i++) {
			DimDecoTypes deco = DimDecoTypes.list[i];
			//if (deco.hasBlockRender()) {
			int n = deco.numIcons;
			icons[i] = new ArrayList();
			for (int k = 0; k < n; k++) {
				icons[i].add(ico.registerIcon("chromaticraft:dimgen/"+deco.name().toLowerCase(Locale.ENGLISH)+"/layer_"+k));
				if (deco.hasSecondaryIcons()) {
					icons[i].add(ico.registerIcon("chromaticraft:dimgen/"+deco.name().toLowerCase(Locale.ENGLISH)+"/layer_"+k+"b"));
				}
			}
			//}
		}
	}

	@Override
	public final int getRenderType() {
		return ChromatiCraft.proxy.dimgenRender;
	}

	@Override
	public final boolean isOpaqueCube() {
		return false;
	}

	@Override
	public final boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public final int getRenderBlockPass() {
		return 1;
	}
	/*
	@Override
	public Item getItemDropped(int meta, Random r, int fortune) {
		return ChromaItems.DIMGEN.getItemInstance();
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

	@Override
	public int quantityDropped(Random rand) {
		return 1;
	}
	 */
	@Override
	public boolean canSilkHarvest(World world, EntityPlayer player, int x, int y, int z, int metadata) {
		return DimDecoTypes.list[metadata].canSilkTouch();
	}

	@Override
	public final boolean canRenderInPass(int pass) {
		DimensionDecoRenderer.renderPass = pass;
		return pass <= 1;
	}

	@Override
	public boolean canHarvestBlock(EntityPlayer ep, int meta) {
		return DimDecoTypes.list[meta].requiresPickaxe() ? super.canHarvestBlock(ep, meta) : true;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		return DimDecoTypes.list[meta].hasBlockRender() ? ReikaAABBHelper.getBlockAABB(x, y, z) : null;
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		ArrayList<ItemStack> li = new ArrayList();
		DimDecoTypes.list[metadata].addDrops(world, x, y, z, metadata, fortune, li, harvesters.get());
		return li;
	}

	@Override
	public boolean isMineable(int meta) {
		return true;
	}

	@Override
	public ArrayList<ItemStack> getHarvestItems(World world, int x, int y, int z, int meta, int fortune) {
		return this.getDrops(world, x, y, z, meta, fortune);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random r) {
		int meta = world.getBlockMetadata(x, y, z);
		if (meta == DimDecoTypes.GEMSTONE.ordinal()) {
			int n = r.nextInt(5);
			int c = this.getGemStoneColor(world, x, y, z);
			int l = ReikaRandomHelper.getRandomBetween(10, 40);
			double o = 0.03125;
			for (int i = 0; i < n; i++) {
				double px = ReikaRandomHelper.getRandomBetween(x-o, x+1+o);
				double py = ReikaRandomHelper.getRandomBetween(y-o, y+1+o);
				double pz = ReikaRandomHelper.getRandomBetween(z-o, z+1+o);
				while (new DecimalPosition(px, py, pz).sharesBlock(x, y, z)) {
					px = ReikaRandomHelper.getRandomBetween(x-o, x+1+o);
					py = ReikaRandomHelper.getRandomBetween(y-o, y+1+o);
					pz = ReikaRandomHelper.getRandomBetween(z-o, z+1+o);
				}
				float s = 1+r.nextFloat();
				EntityFX fx = new EntityCCBlurFX(world, px, py, pz).setIcon(ChromaIcons.FLARE).setColor(c).setLife(l).setRapidExpand().setScale(s);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}
	}

	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int s, float a, float b, float c, int meta) {
		if (meta == DimDecoTypes.GEMSTONE.ordinal() && world.isRemote) {
			this.doBreakFX(world, x, y, z);
		}
		return meta;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block old, int oldmeta) {
		if (oldmeta == DimDecoTypes.GEMSTONE.ordinal() && world.isRemote) {
			this.doBreakFX(world, x, y, z);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean addHitEffects(World world, MovingObjectPosition target, EffectRenderer f) {
		int meta = world.getBlockMetadata(target.blockX, target.blockY, target.blockZ);
		if (meta == DimDecoTypes.GEMSTONE.ordinal()) {
			this.doBreakFX(world, target.blockX, target.blockY, target.blockZ);
			return true;
		}
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer f) {
		if (meta == DimDecoTypes.GEMSTONE.ordinal()) {
			this.doBreakFX(world, x, y, z);
			return true;
		}
		return false;
	}

	@SideOnly(Side.CLIENT)
	private void doBreakFX(World world, int x, int y, int z) {
		int n = 16+rand.nextInt(32);
		int c = this.getGemStoneColor(world, x, y, z);
		int l = ReikaRandomHelper.getRandomBetween(10, 40);
		double o = 0.03125;
		for (int i = 0; i < n; i++) {
			double px = ReikaRandomHelper.getRandomBetween(x-o, x+1+o);
			double py = ReikaRandomHelper.getRandomBetween(y-o, y+1+o);
			double pz = ReikaRandomHelper.getRandomBetween(z-o, z+1+o);
			while (new DecimalPosition(px, py, pz).sharesBlock(x, y, z)) {
				px = ReikaRandomHelper.getRandomBetween(x-o, x+1+o);
				py = ReikaRandomHelper.getRandomBetween(y-o, y+1+o);
				pz = ReikaRandomHelper.getRandomBetween(z-o, z+1+o);
			}
			float s = 1+rand.nextFloat();
			EntityFX fx = new EntityCCBlurFX(world, px, py, pz).setIcon(ChromaIcons.FLARE).setColor(c).setLife(l).setRapidExpand().setScale(s);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z)
	{
		int meta = world.getBlockMetadata(x, y, z);
		if (meta == DimDecoTypes.GEMSTONE.ordinal()) {
			return false;
		}
		return super.canCreatureSpawn(type, world, x, y, z);
	}

	@Override
	public MineralCategory getCategory() {
		return MineralCategory.MISC_UNDERGROUND_VALUABLE;
	}

	@Override
	public Block getReplacedBlock(World world, int x, int y, int z) {
		return Blocks.air;
	}

	@Override
	public boolean allowSilkTouch(int meta) {
		return DimDecoTypes.list[meta].canSilkTouch();
	}

	@Override
	public boolean isBeaconBase(IBlockAccess world, int x, int y, int z, int beaconX, int beaconY, int beaconZ) {
		return world.getBlockMetadata(x, y, z) == DimDecoTypes.FLOATSTONE.ordinal();
	}
	/*
	@SideOnly(Side.CLIENT)
	public static void setGlowCaveAnimationData(TextureAtlasSprite icon) {
		if (icon.animationMetadata == null) {
			ChromatiCraft.logger.logError("Animation "+icon.getIconName()+" has no data?!");
		}
		else {
			String s = icon.getIconName();
			int idx = Integer.parseInt(s.substring(s.lastIndexOf('/')+1));
			icon.animationMetadata = new ShuffledIconControl(idx, icon.animationMetadata, ShuffledIconControl.ANIMATION_SECTIONS_GLOWCAVE, ShuffledIconControl.ANIMATION_SECTION_LENGTH_GLOWCAVE);
		}
	}*/

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e) {
		super.onEntityCollidedWithBlock(world, x, y, z, e);
		if (e instanceof EntityLivingBase) {
			EntityLivingBase elb = (EntityLivingBase)e;
			int meta = world.getBlockMetadata(x, y, z);
			switch(DimDecoTypes.list[meta]) {
				case AQUA:
					elb.extinguish();
					//replenish thirst for enviromine?
					break;
				case LIFEWATER:
					if (elb instanceof EntityMob && ((EntityMob)elb).getCreatureAttribute() == EnumCreatureAttribute.UNDEAD)
						elb.attackEntityFrom(DamageSource.magic, 4);
					else
						elb.heal(2); //1 heart per tick
					break;
				case MIASMA:
					ArrayList<PotionEffect> map = new ArrayList();
					for (Object o : elb.getActivePotionEffects()) {
						PotionEffect ef = (PotionEffect)o;
						Potion p = Potion.potionTypes[ef.getPotionID()];
						if (ReikaPotionHelper.isBadEffect(p)) {

						}
						else {
							int time = Math.max(20*60*20, ef.getDuration());
							PotionEffect repl = new PotionEffect(p.id, time, ef.getAmplifier());
							repl.setCurativeItems(ef.getCurativeItems());
							map.add(repl);
						}
					}
					for (PotionEffect ef : map) {
						elb.removePotionEffect(ef.getPotionID());
						elb.addPotionEffect(ef);
					}
					break;
				default:
					break;
			}
		}
	}

}
