/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
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
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ProgressionTrigger;
import Reika.ChromatiCraft.Magic.Interfaces.NaturalNetworkTile;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.TileEntity.TileEntityPersonalCharger;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntitySkypeater;
import Reika.ChromatiCraft.TileEntity.Technical.TileEntityDimensionCore;
import Reika.ChromatiCraft.TileEntity.Technical.TileEntityStructControl;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.TileEntityBase;
import Reika.DragonAPI.Interfaces.Block.SemiUnbreakable;
import Reika.DragonAPI.Interfaces.TileEntity.PlayerBreakHook;
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
		color = te instanceof TileEntityPersonalCharger ? ((TileEntityPersonalCharger)te).getColor().getColor() : color;
		int b = te instanceof TileEntityStructControl ? ((TileEntityStructControl)te).getBrightness() : 15;
		b = te instanceof TileEntitySkypeater ? 0 : b;
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
			case PERSONAL:
			case SKYPEATER:
				return null;
			default:
				return this.getBlockAABB(x, y, z);
		}
	}

	@Override
	public float getPlayerRelativeBlockHardness(EntityPlayer ep, World world, int x, int y, int z) {
		TileEntityBase te = (TileEntityBase)world.getTileEntity(x, y, z);
		if (te instanceof NaturalNetworkTile)
			return -1;
		else if (te instanceof PlayerBreakHook) {
			if (!((PlayerBreakHook)te).isBreakable(ep))
				return -1;
		}
		if (te instanceof TileEntityStructControl) {
			return super.getPlayerRelativeBlockHardness(ep, world, x, y, z)*32;
		}
		else if (te instanceof TileEntityDimensionCore) {
			return super.getPlayerRelativeBlockHardness(ep, world, x, y, z)*8;
		}
		return super.getPlayerRelativeBlockHardness(ep, world, x, y, z);
	}

	@Override
	public IIcon getIcon(IBlockAccess iba, int x, int y, int z, int s) {
		return this.getIcon(s, iba.getBlockMetadata(x, y, z));
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
			case 7:
				return ChromaIcons.TRANSPARENT.getIcon();
			case 8:
				return ChromaIcons.BROADCAST.getIcon();
			case 9:
				return ChromaIcons.WEAKREPEATER.getIcon();
			case 11:
				return ChromaIcons.TRANSPARENT.getIcon();
		}
		return Blocks.stone.getIcon(0, 0);
	}
	/*
	@Override
	public int colorMultiplier(IBlockAccess iba, int x, int y, int z) {
		TileEntityBase te = (TileEntityBase)iba.getTileEntity(x, y, z);
		return te instanceof TileEntityCrystalBroadcaster ? ((TileEntityCrystalBroadcaster)te).getActiveColor().getColor() : 0xffffff;
	}
	 */
	@Override
	public ProgressStage[] getTriggers(EntityPlayer ep, World world, int x, int y, int z) {
		ChromaTiles c = ChromaTiles.getTile(world, x, y, z);
		boolean pylon = c == ChromaTiles.PYLON && ((TileEntityCrystalPylon)world.getTileEntity(x, y, z)).canConduct();
		return pylon ? new ProgressStage[]{ProgressStage.PYLON} : null;
	}

	@Override
	public boolean isUnbreakable(World world, int x, int y, int z, int meta) {
		TileEntityBase te = (TileEntityBase)world.getTileEntity(x, y, z);
		if (te instanceof NaturalNetworkTile)
			return true;
		else if (te instanceof PlayerBreakHook) {
			return !((PlayerBreakHook)te).isBreakable(null);
		}
		return false;
	}
	/*
	@Override
	public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return ChromaTiles.getTile(world, x, y, z) == ChromaTiles.WEAKREPEATER ? Blocks.planks.getFlammability(world, x, y, z, face) : 0;
	}

	@Override
	public boolean isFlammable(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return this.getFlammability(world, x, y, z, face) > 0;
	}

	@Override
	public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		return ChromaTiles.getTile(world, x, y, z) == ChromaTiles.WEAKREPEATER ? Blocks.planks.getFireSpreadSpeed(world, x, y, z, face) : 0;
	}
	 */
	@Override
	public boolean isFireSource(World world, int x, int y, int z, ForgeDirection side) {
		return ChromaTiles.getTile(world, x, y, z) == ChromaTiles.WEAKREPEATER && side == ForgeDirection.UP;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection dir) {
		ChromaTiles c = ChromaTiles.getTile(world, x, y, z);
		switch(c) {
			case REPEATER:
			case COMPOUND:
			case WEAKREPEATER:
				return dir == ForgeDirection.UP;
			default:
				return false;
		}
	}

}
