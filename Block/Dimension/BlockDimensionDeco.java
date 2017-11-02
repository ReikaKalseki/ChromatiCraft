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
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
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
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
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
		CLIFFGLASS(2);

		public final int numIcons;

		public static DimDecoTypes[] list = values();

		private DimDecoTypes(int n) {
			numIcons = n;
		}

		public ItemStack getItem() {
			return new ItemStack(ChromaBlocks.DIMGEN.getBlockInstance(), 1, this.ordinal());
		}

		public boolean hasBlockRender() {
			return this == FLOATSTONE || this == GEMSTONE || this == CRYSTALLEAF || this == OCEANSTONE || this == CLIFFGLASS;
		}

		public List<IIcon> getIcons(int pass) {
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
			if (this == CRYSTALLEAF || this == CLIFFGLASS) {
				return idx >= 1 ? pass == 1 : pass == 0;
			}
			if (this == MIASMA || this == LIFEWATER)
				return pass == 1;
			return pass == 0;
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
		return icons[meta].get(0);
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < DimDecoTypes.list.length; i++) {
			int n = DimDecoTypes.list[i].numIcons;
			icons[i] = new ArrayList();
			for (int k = 0; k < n; k++) {
				icons[i].add(ico.registerIcon("chromaticraft:dimgen/"+i+"_layer_"+k));
			}
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

	@Override
	public Item getItemDropped(int meta, Random r, int fortune) {
		return ChromaItems.DIMGEN.getItemInstance();
	}

	@Override
	public int damageDropped(int meta) {
		return meta;
	}

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
				EntityFX fx = new EntityBlurFX(world, px, py, pz).setColor(c).setLife(l).setRapidExpand().setIcon(ChromaIcons.FLARE).setScale(s);
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
			EntityFX fx = new EntityBlurFX(world, px, py, pz).setColor(c).setLife(l).setRapidExpand().setIcon(ChromaIcons.FLARE).setScale(s);
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

}
