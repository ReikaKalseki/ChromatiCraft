/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Block.BlockActiveChroma.TileEntityChroma;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Interfaces.BlockCheck.TileEntityCheck;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class ChromaCheck implements TileEntityCheck {

	public final CrystalElement color;

	public ChromaCheck(CrystalElement e) {
		color = e;
	}

	@Override
	public boolean matchInWorld(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		if (b == ChromaBlocks.CHROMA.getBlockInstance()) {
			TileEntityChroma te = (TileEntityChroma)world.getTileEntity(x, y, z);
			return te.getElement() == color && te.getBerryCount() == TileEntityChroma.BERRY_SATURATION;
		}
		return false;
	}

	@Override
	public boolean match(Block b, int meta) {
		return b == ChromaBlocks.CHROMA.getBlockInstance();
	}

	@Override
	public void place(World world, int x, int y, int z) {
		world.setBlock(x, y, z, ChromaBlocks.CHROMA.getBlockInstance());
		TileEntityChroma te = (TileEntityChroma)world.getTileEntity(x, y, z);
		te.activate(color, TileEntityChroma.BERRY_SATURATION);
	}

	@Override
	public ItemStack asItemStack() {
		return new ItemStack(ChromaBlocks.CHROMA.getBlockInstance());
	}

	@Override
	public ItemStack getDisplay() {
		return this.asItemStack();
	}

	@Override
	public BlockKey asBlockKey() {
		return new BlockKey(ChromaBlocks.CHROMA.getBlockInstance());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public TileEntity getTileEntity() {
		TileEntityChroma te = new TileEntityChroma();
		te.worldObj = Minecraft.getMinecraft().theWorld;
		te.activate(color, TileEntityChroma.BERRY_SATURATION);
		return te;
	}

}
