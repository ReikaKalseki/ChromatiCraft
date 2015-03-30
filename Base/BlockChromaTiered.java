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

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Base.BlockTieredResource;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class BlockChromaTiered extends BlockTieredResource implements IWailaDataProvider {

	public BlockChromaTiered(Material mat) {
		super(mat);
		this.setCreativeTab(ChromatiCraft.tabChroma);
	}

	public final ProgressStage getProgressStage(IBlockAccess world, int x, int y, int z) {
		return this.getProgressStage(world.getBlockMetadata(x, y, z));
	}

	public abstract ProgressStage getProgressStage(int meta);

	@Override
	public final boolean isPlayerSufficientTier(IBlockAccess world, int x, int y, int z, EntityPlayer ep) {
		return this.getProgressStage(world, x, y, z).isPlayerAtStage(ep);
	}

	@Override
	@ModDependent(ModList.WAILA)
	public final ItemStack getWailaStack(IWailaDataAccessor acc, IWailaConfigHandler cfg) {
		MovingObjectPosition mov = acc.getPosition();
		return FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT && this.isClientSufficient(acc.getWorld(), mov.blockX, mov.blockY, mov.blockZ) ? null : new ItemStack(Blocks.air);
	}

	@SideOnly(Side.CLIENT)
	private boolean isClientSufficient(World world, int x, int y, int z) {
		return this.isPlayerSufficientTier(world, x, y, z, Minecraft.getMinecraft().thePlayer);
	}

	@Override
	@ModDependent(ModList.WAILA)
	public final List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor acc, IWailaConfigHandler config) {
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

	@Override
	@ModDependent(ModList.WAILA)
	public final List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor acc, IWailaConfigHandler config) {

		return currenttip;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public final List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor acc, IWailaConfigHandler config) {
		MovingObjectPosition mov = acc.getPosition();
		String s1 = EnumChatFormatting.ITALIC.toString();
		String s2 = EnumChatFormatting.BLUE.toString();
		String s = FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT && this.isClientSufficient(acc.getWorld(), mov.blockX, mov.blockY, mov.blockZ) ? "Minecraft" : "ChromatiCraft";
		currenttip.add(s2+s1+s);
		return currenttip;
	}

	@Override
	@ModDependent(ModList.WAILA)
	public final NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
		return tag;
	}

}
