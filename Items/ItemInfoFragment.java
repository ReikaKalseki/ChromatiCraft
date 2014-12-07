/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ItemChromaMulti;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaResearchManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemInfoFragment extends ItemChromaMulti {

	public ItemInfoFragment(int tex) {
		super(tex);
		this.setMaxStackSize(1);
		this.setCreativeTab(ChromatiCraft.tabChromaFragments);
	}

	@Override
	public void onUpdate(ItemStack is, World world, Entity ep, int slot, boolean held) {
		if (is.getItemDamage() == 0 && ep instanceof EntityPlayer) {
			this.programShardAndGiveData(is, (EntityPlayer)ep);
		}
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		int dmg = is.getItemDamage();
		if (dmg == 0) {
			li.add(EnumChatFormatting.ITALIC.toString()+"Blank");
		}
		else {
			ChromaResearch r = ChromaResearch.researchList[dmg];
			EnumChatFormatting format = null;
			boolean read = r.playerCanRead(ep);
			boolean has = ChromaResearchManager.instance.playerHasFragment(ep, r);
			if (has)
				format = EnumChatFormatting.GREEN;
			else if (read)
				format = EnumChatFormatting.LIGHT_PURPLE;
			else
				format = EnumChatFormatting.RESET;
			String pre = ""+format.toString();
			String title = r.getParent().getTitle()+": "+r.getTitle();
			String s = read || has ? pre+" "+title : pre+" "+EnumChatFormatting.OBFUSCATED.toString()+title;
			li.add(s);
		}
	}

	@Override
	protected boolean isMetaInCreative(int meta) {
		return meta == 0 || !ChromaResearch.researchList[meta].isParent();
	}

	@Override
	protected boolean incrementTextureIndexWithMeta() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack is, int pass) {
		if (is.getItemDamage() == 0)
			return false;
		ChromaResearch r = this.getResearch(is);
		return ChromaResearchManager.instance.canPlayerStepTo(Minecraft.getMinecraft().thePlayer, r);
	}

	public static ChromaResearch getResearch(ItemStack is) {
		return ChromaResearch.researchList[is.getItemDamage()];
	}

	/*
	@Override
	public boolean hasCustomEntity(ItemStack stack)
	{
		return true;
	}

	@Override
	public Entity createEntity(World world, Entity location, ItemStack itemstack)
	{
		return new EntityInfoFragment((EntityItem)location);
	}

	public static class EntityInfoFragment extends EntityItem {

		public EntityInfoFragment(EntityItem ei) {
			super(ei.worldObj, ei.posX, ei.posY, ei.posZ, ei.getEntityItem());
			motionX = ei.motionX;
			motionY = ei.motionY;
			motionZ = ei.motionZ;
			lifespan = ei.lifespan;
			age = ei.age;
			delayBeforeCanPickup = ei.delayBeforeCanPickup;
		}

		public EntityInfoFragment(World world) {
			super(world);
		}

		@Override
		public boolean isEntityInvulnerable()
		{
			return true;
		}

		@Override
		public void onCollideWithPlayer(EntityPlayer ep)
		{
			if (this.getEntityItem().getItemDamage() == 0)
				this.getEntityItem().setItemDamage(ChromaResearch.instance.getRandomNextResearchFor(ep).ID);
			super.onCollideWithPlayer(ep);
		}

	}*/

	public static void programShardAndGiveData(ItemStack is, EntityPlayer ep) {
		ChromaResearch r = ChromaResearchManager.instance.getRandomNextResearchFor(ep);
		if (r != null) {
			is.setItemDamage(r.ordinal());
			ChromaResearchManager.instance.givePlayerFragment(ep, getResearch(is));
		}
	}

}
