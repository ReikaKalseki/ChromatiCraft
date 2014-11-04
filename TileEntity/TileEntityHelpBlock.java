/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity;

import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.GUI.GuiHelp;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.BlockInWorldGui.TileEntityInWorldGui;
import Reika.DragonAPI.Instantiable.GUI.InWorldGui;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityHelpBlock extends TileEntityChromaticBase {

	private final InWorldGui gui = new GuiHelp();

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.HELP;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		world.setBlock(x, y+1, z, ChromaBlocks.GUI.getBlockInstance());
		world.setBlock(x-1, y, z, ChromaBlocks.GUI.getBlockInstance());
		world.setBlock(x-1, y+1, z, ChromaBlocks.GUI.getBlockInstance());
		world.setBlock(x+1, y, z, ChromaBlocks.GUI.getBlockInstance());
		world.setBlock(x+1, y+1, z, ChromaBlocks.GUI.getBlockInstance());
		world.setBlock(x-1, y+2, z, ChromaBlocks.GUI.getBlockInstance());
		world.setBlock(x, y+2, z, ChromaBlocks.GUI.getBlockInstance());
		world.setBlock(x+1, y+2, z, ChromaBlocks.GUI.getBlockInstance());

		TileEntityInWorldGui te = (TileEntityInWorldGui)world.getTileEntity(x, y+1, z);
		te.setGui(gui, 0.33F, 0.33F, 0.33F, 0.33F);
		te = (TileEntityInWorldGui)world.getTileEntity(x-1, y, z);
		te.setGui(gui, 0F, 0.66F, 0.33F, 0.33F);
		te = (TileEntityInWorldGui)world.getTileEntity(x-1, y+1, z);
		te.setGui(gui, 0F, 0.33F, 0.33F, 0.33F);
		te = (TileEntityInWorldGui)world.getTileEntity(x+1, y, z);
		te.setGui(gui, 0.66F, 0.66F, 0.33F, 0.33F);
		te = (TileEntityInWorldGui)world.getTileEntity(x+1, y+1, z);
		te.setGui(gui, 0.66F, 0.33F, 0.33F, 0.33F);

		te = (TileEntityInWorldGui)world.getTileEntity(x+1, y+2, z);
		te.setGui(gui, 0.66F, 0F, 0.33F, 0.33F);
		te = (TileEntityInWorldGui)world.getTileEntity(x, y+2, z);
		te.setGui(gui, 0.33F, 0F, 0.33F, 0.33F);
		te = (TileEntityInWorldGui)world.getTileEntity(x-1, y+2, z);
		te.setGui(gui, 0F, 0F, 0.33F, 0.33F);

		gui.displayHeight = 3;
		gui.displayWidth = 3;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@SideOnly(Side.CLIENT)
	public void renderGui(float ptick) {
		gui.render(ptick);
	}

}
