/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.TieredItem;
import Reika.ChromatiCraft.Base.ItemCrystalBasic;
import Reika.ChromatiCraft.Entity.EntityThrownGem;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Interfaces.Item.AnimatedSpritesheet;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;

//TODO Incomplete item
public class ItemThrowableGem extends ItemCrystalBasic implements AnimatedSpritesheet, TieredItem {

	private static final int[] offsets;

	public ItemThrowableGem(int index) {
		super(index);
		this.setNoRepair();
		maxStackSize = 8;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		world.playSoundAtEntity(ep, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

		if (!world.isRemote) {
			world.spawnEntityInWorld(new EntityThrownGem(world, ep, CrystalElement.elements[is.getItemDamage()]));
		}

		if (!ep.capabilities.isCreativeMode) {
			is.stackSize--;
		}

		return is;
	}

	@Override
	protected final CreativeTabs getCreativePage() {
		return ChromatiCraft.tabChromaTools;
	}

	@Override
	public String getTexture(ItemStack is) {
		return "/Reika/ChromatiCraft/Textures/Items/throwablegems.png";
	}

	@Override
	public ProgressStage getDiscoveryTier(ItemStack is) {
		return ProgressStage.ALLCOLORS;
	}

	@Override
	public boolean isTiered(ItemStack is) {
		return true;
	}

	@Override
	public boolean useAnimatedRender(ItemStack is) {
		return true;
	}

	@Override
	public int getFrameSpeed(ItemStack is) {
		return 2;
	}

	@Override
	public int getColumn(ItemStack is) {
		return is.getItemDamage();
	}

	@Override
	public int getFrameCount(ItemStack is) {
		return 16;
	}

	@Override
	public int getFrameOffset(ItemStack is) {
		return offsets[is.getItemDamage()];
	}

	static {
		offsets = ReikaArrayHelper.getLinearArray(16);
		ReikaArrayHelper.shuffleArray(offsets);
	}

	@Override
	public int getBaseRow(ItemStack is) {
		return 0;
	}

	@Override
	public boolean verticalFrames() {
		return true;
	}

}
