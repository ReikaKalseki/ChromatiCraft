package Reika.ChromatiCraft.Block.Dimension;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.Interfaces.MinerBlock;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Registry.ChromaISBRH;
import Reika.ChromatiCraft.World.Dimension.DimensionTuningManager;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Base.BlockReplaceOnBreak;

public class BlockBedrockCrack extends BlockReplaceOnBreak implements MinerBlock {

	private static final IIcon[] crackOverlay = new IIcon[10];

	public BlockBedrockCrack(Material mat) {
		super(mat);
		this.setCreativeTab(ChromatiCraft.tabChromaGen);
		this.setResistance(Blocks.bedrock.blockResistance);
		this.setHardness(12);
	}
	/*
	@Override
	public void harvestBlock(World world, EntityPlayer ep, int x, int y, int z, int meta) {
		super.harvestBlock(world, ep, x, y, z, meta);
	}*/

	@Override
	public float getBlockHardness(World world, int x, int y, int z) {
		float f = super.getBlockHardness(world, x, y, z);
		float f2 = 1F-(9-world.getBlockMetadata(x, y, z))/9F*0.5F;
		return f2*f;
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		return this.getDrops(world, x, y, z, metadata, fortune, harvesters.get());
	}

	private ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune, EntityPlayer ep) {
		ArrayList<ItemStack> li = new ArrayList();
		int n = 1+Math.min(metadata, DragonAPICore.rand.nextInt(1+metadata+fortune));
		if (ep != null)
			n = DimensionTuningManager.instance.getTunedDropCount(ep, n, 1, Integer.MAX_VALUE);
		for (int i = 0; i < n; i++)
			li.add(ChromaStacks.bedrockloot.copy());
		if (metadata == 9) {
			n = ep != null ? DimensionTuningManager.instance.getTunedDropCount(ep, n, 1, 3) : 1;
			for (int i = 0; i < n; i++)
				li.add(ChromaStacks.bedrockloot2.copy());
		}
		else {

		}
		return li;
	}

	@Override
	public Block getBlockReplacedWith(EntityPlayer ep, int x, int y, int z, int oldMeta, boolean willHarvest) {
		return willHarvest && oldMeta > 0 ? this : Blocks.bedrock;
	}

	@Override
	public int getMetaReplacedWith(EntityPlayer ep, int x, int y, int z, int oldMeta, boolean willHarvest) {
		return Math.max(0, oldMeta-1);
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = Blocks.bedrock.blockIcon;
		for (int i = 0; i < crackOverlay.length; i++) {
			String s = "chromaticraft:dimgen/bedrockloot/"+i;
			crackOverlay[i] = ico.registerIcon(s);
		}
	}

	public static IIcon getCrackOverlay(int meta) {
		return crackOverlay[meta%10];
	}

	public static IIcon getCrackOverlay(IBlockAccess world, int x, int y, int z) {
		return getCrackOverlay(world.getBlockMetadata(x, y, z));
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public final int getRenderBlockPass() {
		return 1;
	}

	@Override
	public final boolean canRenderInPass(int pass) {
		ChromaISBRH.bedrockCrack.setRenderPass(pass);
		return true;
	}

	@Override
	public int getRenderType() {
		return ChromaISBRH.bedrockCrack.getRenderID();
	}

	@Override
	public boolean isMineable(int meta) {
		return true;
	}

	@Override
	public boolean allowSilkTouch(int meta) {
		return false;
	}

	@Override
	public ArrayList<ItemStack> getHarvestItems(World world, int x, int y, int z, int meta, int fortune) {
		ArrayList<ItemStack> li = new ArrayList();
		if (meta == 9) {
			li.add(ChromaStacks.bedrockloot2);
		}
		int m = meta;
		while (m >= 0) {
			li.addAll(this.getDrops(world, x, y, z, m, fortune));
			m--;
		}
		return li;
	}

	@Override
	public MineralCategory getCategory() {
		return MineralCategory.MISC_UNDERGROUND_VALUABLE;
	}

	@Override
	public Block getReplacedBlock(World world, int x, int y, int z) {
		return Blocks.bedrock;
	}

}
