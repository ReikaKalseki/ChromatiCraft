/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;

public class BlockModelledChromaTile extends BlockChromaTile {

	public BlockModelledChromaTile(Material par2Material) {
		super(par2Material);
	}

	@Override
	public final int getRenderType() {
		return -1;
	}

	@Override
	public final boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public final boolean isOpaqueCube() {
		return false;
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("chromaticraft:transparent");
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		AxisAlignedBB box = this.getCollisionBoundingBoxFromPool(world, x, y, z);
		return box != null ? box : ReikaAABBHelper.getBlockAABB(x, y, z).contract(0.4, 0.4, 0.4);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)  {
		ChromaTiles m = ChromaTiles.getTile(world, x, y, z);
		if (m == null)
			return ReikaAABBHelper.getBlockAABB(x, y, z);
		if (m.isIntangible())
			return null;
		TileEntityChromaticBase te = (TileEntityChromaticBase)world.getTileEntity(x, y, z);
		AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x+m.getMinX(te), y+m.getMinY(te), z+m.getMinZ(te), x+m.getMaxX(te), y+m.getMaxY(te), z+m.getMaxZ(te));
		//if (te.isFlipped) {
		//	box = AxisAlignedBB.getBoundingBox(x+m.getMinX(te), y+(1-m.getMaxY(te)), z+m.getMinZ(te), x+m.getMaxX(te), y+(1-m.getMinY(te)), z+m.getMaxZ(te));
		//}
		this.setBounds(box, x, y, z);
		return box;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e) {
		ChromaTiles c = ChromaTiles.getTile(world, x, y, z);
		if (e instanceof EntityLivingBase) {
			if (c == ChromaTiles.CHARGER && ((EntityLivingBase)e).getHealth() > 1) {
				e.attackEntityFrom(DamageSource.generic, 0.25F);
			}
		}
	}

}
