/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.CustomHitbox;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ItemCollision;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.TileEntity.AOE.Defence.TileEntityAvoLaser;
import Reika.ChromatiCraft.TileEntity.Auxiliary.TileEntityCrystalCharger;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
	public final void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(x, y, z);
		ChromaTiles m = ChromaTiles.getTile(world, x, y, z);
		if (m != null) {
			TileEntityChromaticBase te = (TileEntityChromaticBase)world.getTileEntity(x, y, z);
			box = AxisAlignedBB.getBoundingBox(x+m.getMinX(te), y+m.getMinY(te), z+m.getMinZ(te), x+m.getMaxX(te), y+m.getMaxY(te), z+m.getMaxZ(te));
			if (m.providesCustomHitbox()) {
				box = ((CustomHitbox)world.getTileEntity(x, y, z)).getHitbox();
			}
			else if (m.isIntangible()) {
				box = ReikaAABBHelper.getBlockAABB(x, y, z).contract(0.375, 0.375, 0.375);
			}
		}
		this.setBounds(box, x, y, z);
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		ChromaTiles m = ChromaTiles.getTile(world, x, y, z);
		if (m != null && m.providesCustomHitbox()) {
			AxisAlignedBB box = ((CustomHitbox)world.getTileEntity(x, y, z)).getHitbox();
			this.setBounds(box, x, y, z);
			return box;
		}
		AxisAlignedBB box = this.getCollisionBoundingBoxFromPool(world, x, y, z);
		box = box != null ? box : ReikaAABBHelper.getBlockAABB(x, y, z).contract(0.375, 0.375, 0.375);
		this.setBounds(box, x, y, z);
		return box;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)  {
		ChromaTiles m = ChromaTiles.getTile(world, x, y, z);
		if (m == null)
			return ReikaAABBHelper.getBlockAABB(x, y, z);
		if (m.providesCustomHitbox()) {
			AxisAlignedBB box = ((CustomHitbox)world.getTileEntity(x, y, z)).getHitbox();
			this.setBounds(box, x, y, z);
			if (!m.isIntangible())
				return box;
		}
		if (m.isIntangible()) {
			return null;
		}
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
		TileEntityChromaticBase te = (TileEntityChromaticBase)world.getTileEntity(x, y, z);
		if (!e.isDead && e instanceof EntityItem && te instanceof ItemCollision) {
			if (((ItemCollision)te).onItemCollision((EntityItem)e)) {
				e.setDead();
				return;
			}
		}
		if (e instanceof EntityLivingBase) {
			if (c == ChromaTiles.CHARGER && ((EntityLivingBase)e).getHealth() > 1) {
				if (!((TileEntityCrystalCharger)te).getEnergy().isEmpty())
					e.attackEntityFrom(DamageSource.generic, 0.25F);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer eff) {
		return ReikaRenderHelper.addModelledBlockParticles("/Reika/ChromatiCraft/Textures/TileEntity/", world, x, y, z, this, eff, ReikaJavaLibrary.makeListFrom(new double[]{0,0,1,1}), ChromatiCraft.class);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final boolean addHitEffects(World world, MovingObjectPosition tg, EffectRenderer eff)
	{
		return ReikaRenderHelper.addModelledBlockParticles("/Reika/ChromatiCraft/Textures/TileEntity/", world, tg, this, eff, ReikaJavaLibrary.makeListFrom(new double[]{0,0,1,1}), ChromatiCraft.class);
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection dir) {
		ChromaTiles c = ChromaTiles.getTile(world, x, y, z);
		if (c == null)
			return false;
		switch(c) {
			case FARMER:
				return dir == ForgeDirection.UP || dir == ForgeDirection.DOWN;
			case LAMP:
				return dir == ForgeDirection.DOWN;
			case AVOLASER:
				return dir == ((TileEntityAvoLaser)world.getTileEntity(x, y, z)).getFacing().getOpposite();
			default:
				return super.isSideSolid(world, x, y, z, dir);
		}
	}

}
