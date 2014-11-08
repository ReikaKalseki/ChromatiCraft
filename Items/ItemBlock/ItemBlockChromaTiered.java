/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.ItemBlock;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.Interfaces.TieredItem;
import Reika.ChromatiCraft.Base.BlockChromaTiered;
import Reika.ChromatiCraft.Block.BlockTieredOre;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.Base.BlockTieredResource;
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
	public int getMetadata(int meta) {
		return meta;
	}

	@Override
	public IIcon getIconFromDamage(int meta)
	{
		return field_150939_a.getIcon(0, meta);
	}

	@Override
	public String getItemStackDisplayName(ItemStack is) {
		String name = ChromaBlocks.getEntryByID(field_150939_a).getMultiValuedName(is.getItemDamage());
		if (field_150939_a instanceof BlockTieredOre && FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			name = this.getDisguiseName(is, name);
		}
		return name;
	}

	@SideOnly(Side.CLIENT)
	private String getDisguiseName(ItemStack is, String name) {
		BlockTieredOre bt = (BlockTieredOre)field_150939_a;
		boolean tier = ProgressionManager.instance.isPlayerAtStage(Minecraft.getMinecraft().thePlayer, bt.getProgressStage(is.getItemDamage()));
		return tier ? name : bt.getDisguise().getLocalizedName();
	}

	@Override
	public ProgressStage getDiscoveryTier(ItemStack is) {
		return ((BlockChromaTiered)field_150939_a).getProgressStage(is.getItemDamage());
	}

	@Override
	public boolean isTiered(ItemStack is) {
		return true;
	}

}
