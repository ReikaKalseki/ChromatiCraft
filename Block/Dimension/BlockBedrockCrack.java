package Reika.ChromatiCraft.Block.Dimension;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;

public class BlockBedrockCrack extends Block {

	public static final IIcon[] crackOverlay = new IIcon[10];

	public BlockBedrockCrack(Material mat) {
		super(mat);
		this.setCreativeTab(ChromatiCraft.tabChromaGen);
		this.setResistance(Blocks.bedrock.blockResistance);
		this.setHardness(12);
	}

	@Override
	public void harvestBlock(World world, EntityPlayer ep, int x, int y, int z, int meta) {
		super.harvestBlock(world, ep, x, y, z, meta);
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < crackOverlay.length; i++) {
			String s = "chromaticraft/dimgen/glowcave"+i;
			crackOverlay[i] = ico.registerIcon(s);
		}
	}

	public static IIcon getCrackOverlay(IBlockAccess world, int x, int y, int z) {
		int idx = new Coordinate(x, y, z).hashCode();
		int len = crackOverlay.length;
		idx = ((idx%len)+len)%len;
		return crackOverlay[idx];
	}

}
