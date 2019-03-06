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
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Render.ISBRH.BedrockCrackRenderer;
import Reika.DragonAPI.DragonAPICore;

public class BlockBedrockCrack extends Block {

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
		ArrayList<ItemStack> li = new ArrayList();
		int n = 1+Math.min(metadata, DragonAPICore.rand.nextInt(1+metadata+fortune));
		for (int i = 0; i < n; i++)
			li.add(ChromaStacks.bedrockloot.copy());
		if (metadata == 9) {
			li.add(ChromaStacks.bedrockloot2.copy());
		}
		else {

		}
		return li;
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
		int meta = world.getBlockMetadata(x, y, z);
		if (willHarvest && meta > 0) {
			return world.setBlockMetadataWithNotify(x, y, z, meta-1, 3);
		}
		else {
			return world.setBlock(x, y, z, Blocks.bedrock);
		}
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
		BedrockCrackRenderer.renderPass = pass;
		return true;
	}

	@Override
	public int getRenderType() {
		return ChromatiCraft.proxy.bedrockCrackRender;
	}

}
