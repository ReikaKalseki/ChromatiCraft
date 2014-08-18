/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaAux;
import Reika.ChromatiCraft.Auxiliary.GuardianStoneManager;
import Reika.ChromatiCraft.Base.TileEntity.FluidEmitterChromaticBase;
import Reika.ChromatiCraft.Base.TileEntity.FluidIOChromaticBase;
import Reika.ChromatiCraft.Base.TileEntity.FluidReceiverChromaticBase;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Magic.CrystalNetworkTile;
import Reika.ChromatiCraft.Magic.CrystalNetworker;
import Reika.ChromatiCraft.Magic.CrystalReceiver;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.TileEntity.TileEntityGuardianStone;
import Reika.ChromatiCraft.TileEntity.TileEntityRift;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.BlockTEBase;
import Reika.DragonAPI.Base.TileEntityBase;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.DartItemHandler;

import java.util.List;
import java.util.Random;

import mcp.mobius.waila.api.IWailaBlock;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class BlockChromaTile extends BlockTEBase implements IWailaBlock {

	private static final Random par5Random = new Random();

	private final IIcon[][] icons = new IIcon[16][6];

	public BlockChromaTile(Material par2Material) {
		super(par2Material);
		this.setCreativeTab(null);
		blockHardness = 5;
		blockResistance = 10;
	}

	@Override
	public final boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public final TileEntity createTileEntity(World world, int meta) {
		TileEntity te = ChromaTiles.createTEFromIDAndMetadata(this, meta);
		return te;
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return icons[meta][0];
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		int num = ChromaTiles.getTilesForBlock(this).size();
		for (int i = 0; i < num; i++) {
			for (int k = 0; k < 6; k++) {
				icons[i][k] = ico.registerIcon("chromaticraft:tile/"+i+"_"+k);
			}
		}
	}

	@Override
	public final boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z)
	{
		return false;
	}

	@Override
	public final boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int side, float par7, float par8, float par9) {
		super.onBlockActivated(world, x, y, z, ep, side, par7, par8, par9);
		if (ChromatiCraft.instance.isLocked())
			return false;
		world.markBlockForUpdate(x, y, z);
		TileEntity te = world.getTileEntity(x, y, z);
		ChromaTiles m = ChromaTiles.getTile(world, x, y, z);
		ItemStack is = ep.getCurrentEquippedItem();

		if (ModList.DARTCRAFT.isLoaded() && DartItemHandler.getInstance().isWrench(is)) {
			ep.setCurrentItemOrArmor(0, null);
			ep.playSound("random.break", 1, 1);
			ep.attackEntityFrom(DamageSource.inWall, 2);
			ReikaChatHelper.write("Your tool has shattered into a dozen pieces.");
			return true;
		}
		if (ep.isSneaking() && !m.hasSneakActions())
			return false;
		if (is != null && ChromaItems.isRegistered(is) && ChromaItems.getEntry(is).overridesRightClick(is)) {
			return false;
		}

		if (te != null && ChromaAux.hasGui(world, x, y, z, ep) && ((TileEntityBase)te).isPlayerAccessible(ep)) {
			ep.openGui(ChromatiCraft.instance, ChromaGuis.TILE.ordinal(), world, x, y, z);
			return true;
		}

		((TileEntityBase)te).syncAllData(true);
		return false;
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
		}
		if (m.hasNBTVariants()) {
			NBTMachine nb = (NBTMachine)tile;
			NBTTagCompound nbt = nb.getTagsToWriteToStack();
			//core.stackTagCompound = nbt;
		}*/
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
			}
			if (m.hasNBTVariants()) {
				NBTTagCompound nbt = ((NBTMachine)te).getTagsToWriteToStack();
				is.stackTagCompound = (NBTTagCompound)(nbt != null ? nbt.copy() : null);
			}*/
			li = ReikaJavaLibrary.makeListFrom(is);
			ReikaItemHelper.dropItems(world, x+par5Random.nextDouble(), y+par5Random.nextDouble(), z+par5Random.nextDouble(), li);
		}
	}

	@Override
	public final void breakBlock(World world, int x, int y, int z, Block par5, int par6) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof IInventory)
			ReikaItemHelper.dropInventory(world, x, y, z);
		if (te instanceof TileEntityRift) {
			((TileEntityRift)te).resetOther();
		}
		if (te instanceof TileEntityGuardianStone) {
			GuardianStoneManager.instance.removeAreasForStone((TileEntityGuardianStone)te);
		}
		if (te instanceof CrystalNetworkTile) {
			((CrystalNetworkTile)te).removeFromCache();
		}
		if (te instanceof CrystalReceiver) {
			CrystalNetworker.instance.breakPaths((CrystalReceiver)te);
		}
		super.breakBlock(world, x, y, z, par5, par6);
	}

	public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
		return ChromaTiles.getTileFromIDandMetadata(this, accessor.getMetadata()).getCraftedProduct();
	}

	public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		World world = acc.getWorld();
		MovingObjectPosition mov = acc.getPosition();
		if (mov != null) {
			int x = mov.blockX;
			int y = mov.blockY;
			int z = mov.blockZ;
			currenttip.add(EnumChatFormatting.WHITE+this.getPickBlock(mov, world, x, y, z).getDisplayName());
		}
		return currenttip;
	}

	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		TileEntityChromaticBase te = (TileEntityChromaticBase)acc.getTileEntity();
		te.syncAllData(false);
		if (te instanceof FluidIOChromaticBase) {
			FluidIOChromaticBase liq = (FluidIOChromaticBase)te;
			Fluid in = liq.getFluidInInput();
			Fluid out = liq.getFluidInOutput();
			int amtin = liq.getInputLevel();
			int amtout = liq.getOutputLevel();
			String input = in != null ? String.format("%d/%d mB of %s", amtin, liq.getCapacity(), in.getLocalizedName()) : "Empty";
			String output = out != null ? String.format("%d/%d mB of %s", amtout, liq.getCapacity(), out.getLocalizedName()) : "Empty";
			currenttip.add("Input Tank: "+input);
			currenttip.add("Output Tank: "+output);
		}
		else if (te instanceof FluidReceiverChromaticBase) {
			FluidReceiverChromaticBase liq = (FluidReceiverChromaticBase)te;
			Fluid in = liq.getContainedFluid();
			int amt = liq.getLevel();
			String input = in != null ? String.format("%d/%d mB of %s", amt, liq.getCapacity(), in.getLocalizedName()) : "Empty";
			currenttip.add("Tank: "+input);
		}
		else if (te instanceof FluidEmitterChromaticBase) {
			FluidEmitterChromaticBase liq = (FluidEmitterChromaticBase)te;
			Fluid in = liq.getContainedFluid();
			int amt = liq.getLevel();
			String input = in != null ? String.format("%d/%d mB of %s", amt, liq.getCapacity(), in.getLocalizedName()) : "Empty";
			currenttip.add("Tank: "+input);
		}
		else if (te instanceof IFluidHandler) {
			FluidTankInfo[] tanks = ((IFluidHandler)te).getTankInfo(ForgeDirection.UP);
			if (tanks != null) {
				for (int i = 0; i < tanks.length; i++) {
					FluidTankInfo info = tanks[i];
					FluidStack fs = info.fluid;
					String input = fs != null ? String.format("%d/%d mB of %s", fs.amount, info.capacity, fs.getFluid().getLocalizedName(fs)) : "Empty";
					currenttip.add("Tank "+i+": "+input);
				}
			}
		}
		/*
		if (te.getMachine().isEnchantable()) {
			if (((EnchantableMachine)te).hasEnchantments()) {
				currenttip.add("Enchantments: ");
				ArrayList<Enchantment> li = ((EnchantableMachine)te).getValidEnchantments();
				for (int i = 0; i < li.size(); i++) {
					Enchantment e = li.get(i);
					int level = ((EnchantableMachine)te).getEnchantment(e);
					if (level > 0)
						currenttip.add("  "+EnumChatFormatting.LIGHT_PURPLE.toString()+e.getTranslatedName(level));
				}
			}
		}*/
		return currenttip;
	}

	public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		String s1 = EnumChatFormatting.ITALIC.toString();
		String s2 = EnumChatFormatting.BLUE.toString();
		currenttip.add(s2+s1+"ChromatiCraft");
		return currenttip;
	}

}
