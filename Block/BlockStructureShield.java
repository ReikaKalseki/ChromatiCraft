package Reika.ChromatiCraft.Block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;

public class BlockStructureShield extends Block {

	private static final IIcon[] icons = new IIcon[8];

	public BlockStructureShield(Material mat) {
		super(mat);
		this.setHardness(2);
		this.setResistance(10);
		this.setCreativeTab(ChromatiCraft.tabChroma);
		stepSound = soundTypeStone;
	}

	@Override
	public float getBlockHardness(World world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z) >= 8 ? -1 : super.getBlockHardness(world, x, y, z);
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return icons[meta&7];
	}

	@Override
	public int damageDropped(int meta) {
		return 0;
	}

	@Override
	public Item getItemDropped(int meta, Random r, int fortune) {
		return super.getItemDropped(meta, r, fortune);
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < 8; i++) {
			icons[i] = ico.registerIcon("chromaticraft:basic/shield_"+i);
		}
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
	{
		return world.getBlockMetadata(x, y, z) < 8;
	}

}
