package Reika.ChromatiCraft.Block.Dimension.Structure;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield;


public class BlockSpecialShield extends BlockStructureShield {

	public BlockSpecialShield(Material mat) {
		super(mat);
	}

	@Override
	public int getRenderType() {
		return ChromatiCraft.proxy.specialShieldRender;
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < BlockType.list.length; i++) {
			icons[i] = ico.registerIcon("chromaticraft:basic/specialshield_"+i);
		}
	}

}
