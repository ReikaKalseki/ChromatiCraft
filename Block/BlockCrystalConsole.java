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

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.NBTTile;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Render.ISBRH.ConsoleRenderer;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalConsole;
import Reika.DragonAPI.Interfaces.Block.ConnectedTextureGlass;
import Reika.DragonAPI.Interfaces.TileEntity.BreakAction;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class BlockCrystalConsole extends BlockContainer implements ConnectedTextureGlass {

	private final ArrayList<Integer> allDirs = new ArrayList();
	private final IIcon[] edges = new IIcon[10];

	private IIcon side;
	private IIcon screen;
	private IIcon invalid;

	public BlockCrystalConsole(Material mat) {
		super(mat);
		this.setHardness(1);
		this.setResistance(6000);

		for (int i = 1; i < 10; i++) {
			allDirs.add(i);
		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		TileEntityCrystalConsole te = (TileEntityCrystalConsole)world.getTileEntity(x, y, z);

		ForgeDirection dir = te.getConsoleFace();
		if (s == dir.ordinal()) {
			int idx = this.getClickedButton(te, dir, a, c);
			if (idx >= 0) {
				if (idx >= 100) {
					if (idx >= 200) {
						idx -= 200;
						te = (TileEntityCrystalConsole)te.getRightBlock();
					}
					else {
						idx -= 100;
						te = (TileEntityCrystalConsole)te.getLeftBlock();
					}
				}
				if (ReikaMathLibrary.isValueInsideBoundsIncl(0.5, 0.6875, b)) {
					te.toggle(idx, true);
				}
				else if (ReikaMathLibrary.isValueInsideBoundsIncl(0.3125, 0.5, b)) {
					te.toggle(idx, false);
				}
			}
			return true;
		}
		return false;
	}

	private int getClickedButton(TileEntityCrystalConsole te, ForgeDirection dir, float a, float c) {
		double d = dir.offsetX == 0 ? a : c;
		boolean pos = dir.offsetX+dir.offsetZ > 0;
		if (pos) {
			d = 1-d;
		}
		if (dir == ForgeDirection.NORTH)
			d = 1-d;
		if (dir.offsetX+dir.offsetZ == 1) {
			ReikaJavaLibrary.pConsole(d);
		}
		//ReikaJavaLibrary.pConsole(d);
		if (ReikaMathLibrary.isValueInsideBoundsIncl(0.15625, 0.34375, d)) {
			return 0;
		}
		else if (ReikaMathLibrary.isValueInsideBoundsIncl(0.40625, 0.59375, d)) {
			return 1;
		}
		else if (ReikaMathLibrary.isValueInsideBoundsIncl(0.65625, 0.84375, d)) {
			return 2;
		}
		else if (ReikaMathLibrary.isValueInsideBoundsIncl(0.90625, 1, d)) {
			if (pos) {
				TileEntity te2 = te.getRightBlock();
				if (te2 instanceof TileEntityCrystalConsole && ((TileEntityCrystalConsole)te2).getSlotCount() == 4) {
					return 200;
				}
			}
			else {
				if (te.getSlotCount() == 4) {
					return 3;
				}
			}
		}
		else if (ReikaMathLibrary.isValueInsideBoundsIncl(0, 0.09375, d)) {
			if (pos) {
				if (te.getSlotCount() == 4) {
					return 0;
				}
			}
			else {
				TileEntity te2 = te.getLeftBlock();
				if (te2 instanceof TileEntityCrystalConsole && ((TileEntityCrystalConsole)te2).getSlotCount() == 4) {
					return 103;
				}
			}
		}
		return -1;
	}

	@Override
	public final ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		int meta = world.getBlockMetadata(target.blockX, target.blockY, target.blockZ);
		ChromaTiles m = ChromaTiles.getTileFromIDandMetadata(this, meta);
		if (m == null)
			return null;
		TileEntity tile = world.getTileEntity(target.blockX, target.blockY, target.blockZ);
		ItemStack core = m.getCraftedProduct();/*
		if (m.isEnchantable()) {
			HashMap<Enchantment, Integer> ench = ((EnchantableMachine)tile).getEnchantments();
			ReikaEnchantmentHelper.applyEnchantments(core, ench);
		}*/
		if (m.hasNBTVariants()) {
			NBTTile nb = (NBTTile)tile;
			NBTTagCompound nbt = new NBTTagCompound();
			nb.getTagsToWriteToStack(nbt);
			core.stackTagCompound = nbt.hasNoTags() ? null : (NBTTagCompound)nbt.copy();
		}
		return core;
	}

	@Override
	public final boolean canSilkHarvest(World world, EntityPlayer player, int x, int y, int z, int metadata)
	{
		return false;
	}

	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean harvest)
	{
		if (this.canHarvest(world, player, x, y, z))
			this.harvestBlock(world, player, x, y, z, 0);
		return world.setBlockToAir(x, y, z);
	}

	private boolean canHarvest(World world, EntityPlayer ep, int x, int y, int z) {
		if (ep.capabilities.isCreativeMode)
			return false;
		return true;
	}

	@Override
	public void harvestBlock(World world, EntityPlayer ep, int x, int y, int z, int meta)
	{
		if (!this.canHarvest(world, ep, x, y, z))
			return;
		TileEntity te = world.getTileEntity(x, y, z);
		ChromaTiles m = ChromaTiles.getTile(world, x, y, z);
		if (m != null) {
			ItemStack is = m.getCraftedProduct();
			List li;
			/*
			if (m.isEnchantable()) {
				HashMap<Enchantment,Integer> map = ((EnchantableMachine)te).getEnchantments();
				ReikaEnchantmentHelper.applyEnchantments(is, map);
			}*/
			if (m.hasNBTVariants()) {
				NBTTagCompound nbt = new NBTTagCompound();
				((NBTTile)te).getTagsToWriteToStack(nbt);
				is.stackTagCompound = (NBTTagCompound)(!nbt.hasNoTags() ? nbt.copy() : null);
			}
			li = ReikaJavaLibrary.makeListFrom(is);
			ReikaItemHelper.dropItems(world, x+0.5, y+0.5, z+0.5, li);
		}
	}

	@Override
	public final void breakBlock(World world, int x, int y, int z, Block par5, int par6) {
		TileEntityCrystalConsole te = (TileEntityCrystalConsole)world.getTileEntity(x, y, z);
		ReikaItemHelper.dropInventory(world, x, y, z);
		((BreakAction)te).breakBlock();
		super.breakBlock(world, x, y, z, par5, par6);
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return s <= 1 ? side : screen;
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int s) {
		TileEntityCrystalConsole te = (TileEntityCrystalConsole)world.getTileEntity(x, y, z);
		if (!te.isValid())
			return invalid;
		if (s == te.getConsoleFace().ordinal()) {
			return screen;
		}
		else {
			return side;
		}
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		side = ico.registerIcon("chromaticraft:console/side");
		screen = ico.registerIcon("chromaticraft:console/screen");
		invalid = ico.registerIcon("chromaticraft:console/invalid");

		for (int i = 0; i < 10; i++) {
			edges[i] = ico.registerIcon("chromaticraft:console/glass_"+i);
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityCrystalConsole();
	}

	@Override
	public int getRenderType() {
		return ChromatiCraft.proxy.consoleRender;
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean canRenderInPass(int pass) {
		ConsoleRenderer.renderPass = pass;
		return pass <= 1;
	}

	public ArrayList<Integer> getEdgesForFace(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
		ArrayList<Integer> li = new ArrayList();
		li.addAll(allDirs);

		TileEntityCrystalConsole te = (TileEntityCrystalConsole)world.getTileEntity(x, y, z);
		if (face != te.getConsoleFace())
			li.remove(new Integer(5));

		if (face.offsetX != 0) { //test YZ
			//sides; removed if have adjacent on side
			if (world.getBlock(x, y, z+1) == this)
				li.remove(new Integer(2));
			if (world.getBlock(x, y, z-1) == this)
				li.remove(new Integer(8));
			if (world.getBlock(x, y+1, z) == this)
				li.remove(new Integer(4));
			if (world.getBlock(x, y-1, z) == this)
				li.remove(new Integer(6));

			//Corners; only removed if have adjacent on side AND corner
			if (world.getBlock(x, y+1, z+1) == this && !li.contains(4) && !li.contains(2))
				li.remove(new Integer(1));
			if (world.getBlock(x, y-1, z-1) == this && !li.contains(6) && !li.contains(8))
				li.remove(new Integer(9));
			if (world.getBlock(x, y+1, z-1) == this && !li.contains(4) && !li.contains(8))
				li.remove(new Integer(7));
			if (world.getBlock(x, y-1, z+1) == this && !li.contains(2) && !li.contains(6))
				li.remove(new Integer(3));
		}
		if (face.offsetY != 0) { //test XZ
			//sides; removed if have adjacent on side
			if (world.getBlock(x, y, z+1) == this)
				li.remove(new Integer(2));
			if (world.getBlock(x, y, z-1) == this)
				li.remove(new Integer(8));
			if (world.getBlock(x+1, y, z) == this)
				li.remove(new Integer(4));
			if (world.getBlock(x-1, y, z) == this)
				li.remove(new Integer(6));

			//Corners; only removed if have adjacent on side AND corner
			if (world.getBlock(x+1, y, z+1) == this && !li.contains(4) && !li.contains(2))
				li.remove(new Integer(1));
			if (world.getBlock(x-1, y, z-1) == this && !li.contains(6) && !li.contains(8))
				li.remove(new Integer(9));
			if (world.getBlock(x+1, y, z-1) == this && !li.contains(4) && !li.contains(8))
				li.remove(new Integer(7));
			if (world.getBlock(x-1, y, z+1) == this && !li.contains(2) && !li.contains(6))
				li.remove(new Integer(3));
		}
		if (face.offsetZ != 0) { //test XY
			//sides; removed if have adjacent on side
			if (world.getBlock(x, y+1, z) == this)
				li.remove(new Integer(4));
			if (world.getBlock(x, y-1, z) == this)
				li.remove(new Integer(6));
			if (world.getBlock(x+1, y, z) == this)
				li.remove(new Integer(2));
			if (world.getBlock(x-1, y, z) == this)
				li.remove(new Integer(8));

			//Corners; only removed if have adjacent on side AND corner
			if (world.getBlock(x+1, y+1, z) == this && !li.contains(2) && !li.contains(4))
				li.remove(new Integer(1));
			if (world.getBlock(x-1, y-1, z) == this && !li.contains(8) && !li.contains(6))
				li.remove(new Integer(9));
			if (world.getBlock(x+1, y-1, z) == this && !li.contains(2) && !li.contains(6))
				li.remove(new Integer(3));
			if (world.getBlock(x-1, y+1, z) == this && !li.contains(4) && !li.contains(8))
				li.remove(new Integer(7));
		}
		return li;
	}

	public IIcon getIconForEdge(IBlockAccess world, int x, int y, int z, int edge) {
		return edges[edge];
	}

	public IIcon getIconForEdge(int itemMeta, int edge) {
		return edges[edge];
	}

	@Override
	public boolean renderCentralTextureForItem(int meta) {
		return true;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess iba, int x, int y, int z, int side) {
		ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[side];
		return iba.getBlock(x, y, z) != this;
	}
}
