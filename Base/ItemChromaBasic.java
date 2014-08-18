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
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.DragonAPI.Interfaces.IndexedItemSprites;

import java.util.Random;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class ItemChromaBasic extends Item implements IndexedItemSprites {

	protected Random par5Random = new Random();

	private int index;

	public ItemChromaBasic(int tex) {
		super();
		maxStackSize = 64;
		this.setIndex(tex);
		this.setCreativeTab(this.isAvailableInCreativeMode() ? this.getCreativePage() : null);
	}

	private boolean isAvailableInCreativeMode() {
		if (ChromatiCraft.instance.isLocked())
			return false;
		return true;
	}

	public ItemChromaBasic(int tex, int max) {
		super();
		maxStackSize = max;
		if (max == 1);
		hasSubtypes = true;
		if (this.isAvailableInCreativeMode())
			this.setCreativeTab(ChromatiCraft.tabChromaItems);
		else
			this.setCreativeTab(null);
		this.setIndex(tex);
	}

	protected CreativeTabs getCreativePage() {
		return ChromatiCraft.tabChromaItems;
	}

	public int getItemSpriteIndex(ItemStack item) {
		return index;
	}

	public void setIndex(int a) {
		index = a;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final void registerIcons(IIconRegister ico) {}

	@Override
	public void onCreated(ItemStack is, World world, EntityPlayer ep) {
		this.checkAchievements(ep, is);
	}

	private void checkAchievements(EntityPlayer player, ItemStack item) {

	}

	public Class getTextureReferenceClass() {
		return ChromatiCraft.class;
	}

	@Override
	public String getTexture(ItemStack is) {
		int i = this.getItemSpriteIndex(is)%256;
		return i > 0 ? "/Reika/ChromatiCraft/Textures/Items/items"+i+".png" : "/Reika/ChromatiCraft/Textures/Items/items.png";
	}

	@Override
	public String getItemStackDisplayName(ItemStack is) {
		ChromaItems ir = ChromaItems.getEntry(is);
		return ir.hasMultiValuedName() ? ir.getMultiValuedName(is.getItemDamage()) : ir.getBasicName();
	}

}
