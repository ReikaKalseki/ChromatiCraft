/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.ItemBlock;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.ForgeEventFactory;

import Reika.ChromatiCraft.Auxiliary.Interfaces.TieredItem;
import Reika.ChromatiCraft.Auxiliary.Render.ChromaFontRenderer;
import Reika.ChromatiCraft.Base.BlockChromaTiered;
import Reika.ChromatiCraft.Block.Worldgen.BlockTieredOre;
import Reika.ChromatiCraft.Block.Worldgen.BlockTieredPlant;
import Reika.ChromatiCraft.Block.Worldgen.BlockTieredPlant.TieredPlants;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Base.BlockTieredResource;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockChromaTiered extends ItemBlock implements TieredItem {

	public ItemBlockChromaTiered(Block b) {
		super(b);
		hasSubtypes = true;
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int s, float a, float b, float c) {
		BlockTieredResource block = (BlockTieredResource)field_150939_a;
		//if (!block.isPlayerSufficientTier(world, x, y, z, ep)) {
		//	;//return false;
		//}
		return super.onItemUse(is, ep, world, x, y, z, s, a, b, c);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {

		BlockKey bk = this.getWaterPlaced(is);
		if (bk == null)
			return is;

		MovingObjectPosition movingobjectposition = this.getMovingObjectPositionFromPlayer(world, ep, true);

		if (movingobjectposition == null) {
			return is;
		}
		else {
			if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
				int i = movingobjectposition.blockX;
				int j = movingobjectposition.blockY;
				int k = movingobjectposition.blockZ;

				if (!world.canMineBlock(ep, i, j, k))
					return is;

				if (!ep.canPlayerEdit(i, j, k, movingobjectposition.sideHit, is))
					return is;

				if (world.getBlock(i, j, k).getMaterial() == Material.water && world.getBlockMetadata(i, j, k) == 0 && world.isAirBlock(i, j+1, k)) {
					// special case for handling block placement with water lilies
					BlockSnapshot blocksnapshot = BlockSnapshot.getBlockSnapshot(world, i, j+1, k);
					world.setBlock(i, j+1, k, bk.blockID, bk.metadata, 3);
					if (ForgeEventFactory.onPlayerBlockPlace(ep, blocksnapshot, ForgeDirection.UP).isCanceled()) {
						blocksnapshot.restore(true, false);
						return is;
					}

					if (!ep.capabilities.isCreativeMode)
						--is.stackSize;
				}
			}

			return is;
		}
	}

	private BlockKey getWaterPlaced(ItemStack is) {
		if (field_150939_a == ChromaBlocks.TIEREDPLANT.getBlockInstance() && TieredPlants.list[is.getItemDamage()].isWaterPlaced()) {
			return new BlockKey(field_150939_a, is.getItemDamage());
		}
		return null;
	}

	@Override
	public int getMetadata(int meta) {
		return meta;
	}

	@Override
	public IIcon getIconFromDamage(int meta)
	{
		return field_150939_a.getIcon(0, meta);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public FontRenderer getFontRenderer(ItemStack is) {
		return null;
	}

	@Override
	public final String getItemStackDisplayName(ItemStack is) {
		String name = ChromaBlocks.getEntryByID(field_150939_a).getMultiValuedName(is.getItemDamage());
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT && DragonAPICore.hasGameLoaded()) {
			name = this.getDisguiseName(is, name);
			//name = ModList.NEI.isLoaded() && DragonAPICore.hasGameLoaded() ? ObfuscatedNameHandler.registerName(name, is) : name;
		}
		return name;
	}

	@SideOnly(Side.CLIENT)
	private String getDisguiseName(ItemStack is, String name) {
		BlockChromaTiered bc = (BlockChromaTiered)field_150939_a;
		boolean tier = bc.getProgressStage(is.getItemDamage()).isPlayerAtStage(Minecraft.getMinecraft().thePlayer);
		if (field_150939_a instanceof BlockTieredOre) {
			BlockTieredOre bt = (BlockTieredOre)field_150939_a;
			return tier ? name : bt.getDisguise(is.getItemDamage()).getLocalizedName();
		}
		else {
			return tier ? name : ChromaFontRenderer.FontType.OBFUSCATED.id+name;
		}
	}

	@SideOnly(Side.CLIENT)
	private boolean obfuscate(ItemStack is) {
		BlockChromaTiered bc = (BlockChromaTiered)field_150939_a;
		boolean tier = bc.getProgressStage(is.getItemDamage()).isPlayerAtStage(Minecraft.getMinecraft().thePlayer);
		if (field_150939_a instanceof BlockTieredOre) {
			return false;
		}
		else {
			return !tier;
		}
	}

	@Override
	public ProgressStage getDiscoveryTier(ItemStack is) {
		return ((BlockChromaTiered)field_150939_a).getProgressStage(is.getItemDamage());
	}

	@Override
	public boolean isTiered(ItemStack is) {
		return true;
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
	{
		boolean flag = field_150939_a instanceof BlockTieredPlant ? ((BlockTieredPlant)field_150939_a).canPlaceAt(world, x, y, z, stack) : true;
		return flag && super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata);
	}

}
