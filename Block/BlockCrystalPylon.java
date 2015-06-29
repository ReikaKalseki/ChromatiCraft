/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ProgressionTrigger;
import Reika.ChromatiCraft.Magic.Interfaces.NaturalCrystalSource;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.TileEntity.TileEntityDimensionCore;
import Reika.ChromatiCraft.TileEntity.TileEntityStructControl;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.TileEntityBase;
import Reika.DragonAPI.Interfaces.SemiUnbreakable;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;

public class BlockCrystalPylon extends BlockCrystalTile implements ProgressionTrigger, SemiUnbreakable {

	public BlockCrystalPylon(Material mat) {
		super(mat);
		//this.setBlockUnbreakable();
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	public final int getLightValue(IBlockAccess iba, int x, int y, int z) {
		TileEntity te = iba.getTileEntity(x, y, z);
		int color = te instanceof TileEntityCrystalPylon ? ((TileEntityCrystalPylon)te).getColor().getColor() : 0xffffff;
		color = te instanceof TileEntityDimensionCore ? ((TileEntityDimensionCore)te).getColor().getColor() : color;
		int b = te instanceof TileEntityStructControl ? ((TileEntityStructControl)te).getBrightness() : 15;
		return ModList.COLORLIGHT.isLoaded() ? ReikaColorAPI.getPackedIntForColoredLight(color, b) : b;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		ChromaTiles c = ChromaTiles.getTile(world, x, y, z);
		if (c == null)
			return null;
		switch(c) {
		case PYLON:
		case STRUCTCONTROL:
		case AURAPOINT:
		case DIMENSIONCORE:
			return null;
		default:
			return this.getBlockAABB(x, y, z);
		}
	}

	@Override
	public float getPlayerRelativeBlockHardness(EntityPlayer ep, World world, int x, int y, int z) {
		TileEntityBase te = (TileEntityBase)world.getTileEntity(x, y, z);
		if (te instanceof NaturalCrystalSource)
			return -1;
		else if (te instanceof TileEntityStructControl) {
			return ((TileEntityStructControl)te).isBreakable() ? super.getPlayerRelativeBlockHardness(ep, world, x, y, z)*32 : -1;
		}
		else if (te instanceof TileEntityDimensionCore) {
			return super.getPlayerRelativeBlockHardness(ep, world, x, y, z)*8;
		}
		return te.isPlacer(ep) ? super.getPlayerRelativeBlockHardness(ep, world, x, y, z) : -1;
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		switch(meta) {
		case 0:
			return ChromaIcons.TRANSPARENT.getIcon();
		case 1:
			return ChromaIcons.REPEATER.getIcon();
		case 2:
			return ChromaIcons.MULTIREPEATER.getIcon();
		case 3:
			return ChromaIcons.TRANSPARENT.getIcon();
		case 4:
			return ChromaIcons.CHROMA.getIcon();
		case 5:
			return ChromaIcons.TRANSPARENT.getIcon();
		case 6:
			//return ChromaIcons.GUARDIANOUTER.getIcon();
			return ChromaIcons.TRANSPARENT.getIcon();
		}
		return Blocks.stone.getIcon(0, 0);
	}

	@Override
	public ProgressStage[] getTriggers(EntityPlayer ep, World world, int x, int y, int z) {
		ChromaTiles c = ChromaTiles.getTile(world, x, y, z);
		boolean pylon = c == ChromaTiles.PYLON && ((TileEntityCrystalPylon)world.getTileEntity(x, y, z)).canConduct();
		return pylon ? new ProgressStage[]{ProgressStage.PYLON} : null;
	}

	@Override
	public boolean isUnbreakable(World world, int x, int y, int z, int meta) {
		return ChromaTiles.getTile(world, x, y, z) == ChromaTiles.PYLON;
	}

}
