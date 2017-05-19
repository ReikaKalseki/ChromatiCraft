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
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Base.BlockChromaTiered;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Worldgen.ControllableOreVein;
import Reika.DragonAPI.Instantiable.Worldgen.ControllableOreVein.BlockExcludingOreVein;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.RotaryCraft.API.ItemFetcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockTieredOre extends BlockChromaTiered {

	public static final int ARR_LENGTH = 15;

	private final IIcon[] overlay = new IIcon[ARR_LENGTH];
	private final IIcon[] back = new IIcon[ARR_LENGTH];
	private final IIcon[] geode = new IIcon[ARR_LENGTH];
	private final IIcon[] geodestone = new IIcon[ARR_LENGTH];

	public BlockTieredOre(Material mat) {
		super(mat);
		this.setHardness(4);
		this.setResistance(5);
	}

	public static enum TieredOres {

		INFUSED(Blocks.stone, ProgressStage.CRYSTALS,			1, 4, 12),
		STONES(Blocks.stone, ProgressStage.RUNEUSE,				1, 4, 8),
		BINDING(Blocks.stone, ProgressStage.CHARGE,				1, 4, 8),
		FOCAL(Blocks.stone, ProgressStage.MULTIBLOCK,			1, 4, 8),
		TELEPORT(Blocks.stone, ProgressStage.END,				1, 2, 8),
		WATERY(Blocks.stone, ProgressStage.OCEAN,				2, 2, 8),
		FIRAXITE(Blocks.stone, ProgressStage.NETHER,			1, 2, 8),
		LUMA(Blocks.stone, ProgressStage.STRUCTCOMPLETE,		1, 1, 8),
		ECHO(Blocks.stone, ProgressStage.CTM,					2, 1, 8),
		FIRESTONE(Blocks.netherrack, ProgressStage.LINK,		1, 4, 16),
		THERMITE(Blocks.netherrack, ProgressStage.END,			1, 4, 16),
		RESO(Blocks.end_stone, ProgressStage.ABILITY,			1, 8, 8),
		SPACERIFT(Blocks.end_stone, ProgressStage.KILLDRAGON,	1, 12, 8),
		RAINBOW(Blocks.stone, ProgressStage.USEENERGY,			1, 2, 8),
		AVOLITE(Blocks.stone, ProgressStage.POWERCRYSTAL,		2, 1, 18);

		public final ProgressStage level;
		public final Block genBlock;

		public final int veinSize;
		public final int veinCount;
		public final int genChance;

		public static final TieredOres[] list = values();

		private TieredOres(Block b, ProgressStage lvl, int gc, int vc, int vs) {
			level = lvl;
			genBlock = b;

			genChance = gc;
			veinCount = vc;
			veinSize = vs;
		}

		public boolean generate(World world, int x, int z, Random r) {
			int y = this.ordinal() >= FIRESTONE.ordinal() ? r.nextInt(128) : r.nextBoolean() ? r.nextInt(32) : r.nextInt(64);
			if (world.provider.dimensionId == ExtraChromaIDs.DIMID.getValue())
				y = r.nextInt(200);
			ControllableOreVein ore = new BlockExcludingOreVein(ChromaBlocks.TIEREDORE.getBlockInstance(), this.ordinal(), veinSize, new BlockKey(ChromaBlocks.CLIFFSTONE.getBlockInstance()));
			ore.target = genBlock;
			return ore.generate(world, r, x, y, z);
		}

		public boolean renderAsGeode() {
			return this == BINDING || this == FOCAL || this == TELEPORT || this == FIRAXITE || this == THERMITE || this == SPACERIFT;
		}
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs c, List li) {
		for (int i = 0; i < TieredOres.list.length; i++) {
			li.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public Collection<ItemStack> getHarvestResources(World world, int x, int y, int z, int fortune, EntityPlayer player) {
		ArrayList li = new ArrayList();
		int n = 1;
		if (ItemFetcher.isPlayerHoldingBedrockPick(player))
			fortune = 5;
		switch(TieredOres.list[world.getBlockMetadata(x, y, z)]) {
			case INFUSED:
				n = Math.min(16, 1+rand.nextInt(5)*(1+rand.nextInt(1+fortune)));
				for (int i = 0; i < n; i++)
					li.add(ChromaStacks.chromaDust.copy());
				break;
			case STONES:
				n = Math.min(4, 1+fortune/2);
				for (int i = 0; i < n; i++)
					li.add(ChromaItems.ELEMENTAL.getStackOfMetadata(rand.nextInt(16)));
				break;
			case BINDING:
				n = Math.min(8, 1+rand.nextInt(3)*(1+rand.nextInt(1+fortune/2)));
				for (int i = 0; i < n; i++)
					li.add(ChromaStacks.bindingCrystal.copy());
				break;
			case FOCAL:
				n = Math.min(32, 1+rand.nextInt(6)*(1+rand.nextInt(1+fortune*3/2)));
				for (int i = 0; i < n; i++)
					li.add(ChromaStacks.focusDust.copy());
				break;
			case TELEPORT:
				n = Math.min(32, 1+rand.nextInt(1+fortune)+fortune*fortune/2);
				for (int i = 0; i < n; i++)
					li.add(ChromaStacks.enderDust.copy());
				break;
			case WATERY:
				n = Math.min(32, 1+rand.nextInt(6)+rand.nextInt(1+3*fortune));
				for (int i = 0; i < n; i++)
					li.add(ChromaStacks.waterDust.copy());
				break;
			case FIRAXITE:
				n = Math.min(32, 1+rand.nextInt(8)+rand.nextInt(1+fortune)*4);
				for (int i = 0; i < n; i++)
					li.add(ChromaStacks.firaxite.copy());
				break;
			case LUMA:
				n = Math.min(32, (1+fortune)*(1+4*rand.nextInt(5)));
				for (int i = 0; i < n; i++)
					li.add(ChromaStacks.lumaDust.copy());
				break;
			case ECHO:
				n = Math.min(32, (1+fortune*fortune/2)*(1+rand.nextInt(8)+rand.nextInt(8)));
				for (int i = 0; i < n; i++)
					li.add(ChromaStacks.echoCrystal.copy());
				break;
			case FIRESTONE:
				n = 1+rand.nextInt(6)*(1+fortune/2);
				for (int i = 0; i < n; i++)
					li.add(ChromaStacks.fireEssence.copy());
				break;
			case THERMITE:
				n = 1+rand.nextInt(1+fortune*fortune);
				for (int i = 0; i < n; i++)
					li.add(ChromaStacks.thermiticCrystal.copy());
				break;
			case RESO:
				n = 1+fortune*4;
				for (int i = 0; i < n; i++)
					li.add(ChromaStacks.resocrystal.copy());
				break;
			case SPACERIFT:
				n = 1+fortune+rand.nextInt(1+fortune)*3/2;
				for (int i = 0; i < n; i++)
					li.add(ChromaStacks.spaceDust.copy());
				break;
			case RAINBOW:
				n = 1+fortune+rand.nextInt(1+fortune)/2;
				for (int i = 0; i < n; i++)
					li.add(ChromaStacks.lumenGem.copy());
				break;
			case AVOLITE:
				n = 1+rand.nextInt(1+fortune/2)/2;
				for (int i = 0; i < n; i++)
					li.add(ChromaStacks.avolite.copy());
				break;
		}
		return li;
	}

	@Override
	public ProgressStage getProgressStage(int meta) {
		return TieredOres.list[meta].level;
	}

	@Override
	public Collection<ItemStack> getNoHarvestResources(World world, int x, int y, int z, int fortune, EntityPlayer player) {
		ArrayList li = new ArrayList();
		Block b = this.getDisguise(world.getBlockMetadata(x, y, z));
		Collection<ItemStack> drops = b.getDrops(world, x, y, z, 0, fortune);
		if (EnchantmentHelper.getSilkTouchModifier(player)) {
			drops.clear();
			drops.add(new ItemStack(b));
		}
		li.addAll(drops);
		return li;
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < ARR_LENGTH; i++) {
			String s = "chromaticraft:ore/tier_"+i;
			back[i] = ico.registerIcon(s+"_underlay");
			overlay[i] = ico.registerIcon(s+"_overlay");
			geode[i] = ico.registerIcon(s+"_geode");
		}

		for (int i = 0; i < geodestone.length; i++) {
			geodestone[i] = ico.registerIcon("chromaticraft:ore/geodestone/"+i);
		}

		blockIcon = Blocks.stone.getIcon(0, 0);
	}

	public IIcon getOverlay(int meta) {
		return overlay[meta];
	}

	public IIcon getBacking(int meta) {
		return TieredOres.list[meta].renderAsGeode() ? this.getGeodeIcon(meta) : back[meta];
	}

	public IIcon getGeodeIcon(int meta) {
		return geode[meta];
	}

	public IIcon getGeodeStoneIcon(int i) {
		return geodestone[i];
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return this.getBacking(meta);
	}

	@Override
	public int getRenderType() {
		return ChromatiCraft.proxy.oreRender;
	}

	public Block getDisguise(int meta) {
		return TieredOres.list[meta].genBlock;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int dx, int dy, int dz, int s) {
		ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[s];
		Block b = world.getBlock(dx, dy, dz);
		if (b.isOpaqueCube())
			return false;
		switch(dir) {
			case EAST:
			case WEST:
			case SOUTH:
			case NORTH:
			case UP:
			case DOWN:
			default:
				return true;
		}
	}

	@Override
	protected ItemStack getWailaDisguise(int meta) {
		return new ItemStack(TieredOres.list[meta].genBlock);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer eff)
	{
		ReikaRenderHelper.spawnDropParticles(world, x, y, z, Blocks.stone, 0);
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final boolean addHitEffects(World world, MovingObjectPosition tg, EffectRenderer eff)
	{
		ReikaRenderHelper.spawnDropParticles(world, tg.blockX, tg.blockY, tg.blockZ, Blocks.stone, 0, 1);
		return true;
	}

}
