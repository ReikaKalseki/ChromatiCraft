/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dimension.Structure.LightPanel;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.BlockDimensionStructure;
import Reika.ChromatiCraft.World.Dimension.Structure.LightPanel.LightType;


public class BlockLightPanel extends BlockDimensionStructure {

	private final IIcon[] icons = new IIcon[1+2*LightType.list.length];

	public BlockLightPanel(Material mat) {
		super(mat);
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		icons[0] = ico.registerIcon("chromaticraft:dimstruct/lightpanel");
		icons[1] = ico.registerIcon("chromaticraft:dimstruct/lightpanel_green_0");
		icons[2] = ico.registerIcon("chromaticraft:dimstruct/lightpanel_green_1");
		icons[3] = ico.registerIcon("chromaticraft:dimstruct/lightpanel_red_0");
		icons[4] = ico.registerIcon("chromaticraft:dimstruct/lightpanel_red_1");
		icons[5] = ico.registerIcon("chromaticraft:dimstruct/lightpanel_blue_0");
		icons[6] = ico.registerIcon("chromaticraft:dimstruct/lightpanel_blue_1");
		//icons[7] = ico.registerIcon("chromaticraft:dimstruct/lightpanel_yellow_0");
		//icons[8] = ico.registerIcon("chromaticraft:dimstruct/lightpanel_yellow_1");
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z)%2 == 1 ? 15 : 0;
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return icons[s <= 1 ? 0 : 1+meta];
	}

	public static void activate(World world, int x, int y, int z, boolean active) {
		int meta = world.getBlockMetadata(x, y, z);
		meta -= meta%2;
		meta = active ? meta+1 : meta;
		world.setBlockMetadataWithNotify(x, y, z, meta, 3);
	}

}
