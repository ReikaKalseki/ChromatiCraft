/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Dimension.Structure;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class BlockShiftLock extends Block {

	private IIcon[] icons = new IIcon[2];

	public BlockShiftLock(Material mat) {
		super(mat);
		this.setResistance(60000);
		this.setBlockUnbreakable();
		this.setCreativeTab(ChromatiCraft.tabChromaGen);
	}
	/*
	@Override
	public int damageDropped(int meta) {
		return meta;
	}
	 */
	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < 2; i++) {
			icons[i] = ico.registerIcon("chromaticraft:dimstruct/shiftlock_"+i);
		}
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return icons[meta];
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		ItemStack is = ep.getCurrentEquippedItem();
		if (is != null && ReikaItemHelper.matchStackWithBlock(is, this))
			return false;
		if (ep.capabilities.isCreativeMode) {
			/*
			if (ChromaItems.SHARD.matchWith(is)) {
				te.addColor(CrystalElement.elements[is.getItemDamage()%16]);
			}
			else if (is == null && ep.isSneaking()) {
				te.colors.clear();
			}
			else if (is != null && ReikaItemHelper.matchStackWithBlock(is, Blocks.obsidian)) {
				world.setBlockMetadataWithNotify(x, y, z, 1, 3);
			}
			 */
		}
		world.markBlockForUpdate(x, y, z);
		//ReikaJavaLibrary.pConsole(Arrays.deepToString(keyCodes));
		return true;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return 0;//ChromatiCraft.proxy.shiftLockRender;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z) == 1 ? null : ReikaAABBHelper.getBlockAABB(x, y, z);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block b, int meta) {
		super.breakBlock(world, x, y, z, b, meta);
	}

}
