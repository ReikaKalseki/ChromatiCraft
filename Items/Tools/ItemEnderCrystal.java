/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.fluids.BlockFluidBase;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Entity.EntityChromaEnderCrystal;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ItemEnderCrystal extends ItemChromaTool {

	public ItemEnderCrystal(int tex) {
		super(tex);
		MinecraftForge.EVENT_BUS.register(this);
		hasSubtypes = true;
	}

	@Override
	public final void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) //Adds the metadata blocks to the creative inventory
	{
		for (int i = 0; i < 2; i++) {
			ItemStack item = new ItemStack(par1, 1, i);
			par3List.add(item);
		}
	}/*

	@Override
	public String getItemStackDisplayName(ItemStack is) {
		return "Ender Crystal";
	}*/

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		if (this.canPickUpCrystal(is)) {
			li.add("Can pick up an Ender Crystal");
		}
		else if (this.canPlaceCrystal(is)) {
			li.add("Contains 1 Ender Crystal");
		}
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int side, float par8, float par9, float par10) {
		if (!this.canPlaceCrystal(is))
			return false;
		if (!ReikaWorldHelper.softBlocks(world, x, y, z) && ReikaWorldHelper.getMaterial(world, x, y, z) != Material.water && ReikaWorldHelper.getMaterial(world, x, y, z) != Material.lava) {
			if (side == 0)
				--y;
			if (side == 1)
				++y;
			if (side == 2)
				--z;
			if (side == 3)
				++z;
			if (side == 4)
				--x;
			if (side == 5)
				++x;
			if (!ReikaWorldHelper.softBlocks(world, x, y, z) && ReikaWorldHelper.getMaterial(world, x, y, z) != Material.water && ReikaWorldHelper.getMaterial(world, x, y, z) != Material.lava)
				return false;
		}
		if (!ep.canPlayerEdit(x, y, z, 0, is))
			return false;
		else if (!this.checkSpace(world, x, y, z))
			return false;
		else
		{
			if (!ep.capabilities.isCreativeMode)
				is.setItemDamage(0);
			//world.setBlock(x, y, z, RedstoneBlocks.WIRE.getBlock());
			if (!world.isRemote) {
				world.setBlock(x, y, z, Blocks.bedrock);
				EntityChromaEnderCrystal cry = new EntityChromaEnderCrystal(world);
				world.spawnEntityInWorld(cry);
				cry.setPosition(x+0.5, y+1, z+0.5);
			}
			ReikaSoundHelper.playPlaceSound(world, x, y, z, Blocks.bedrock);
		}
		return true;
	}

	@SubscribeEvent
	public void captureCrystal(EntityInteractEvent ev) {
		Entity e = ev.target;
		EntityPlayer ep = ev.entityPlayer;
		if (e instanceof EntityEnderCrystal) {
			ItemStack is = ep.getCurrentEquippedItem();
			if (is != null && is.getItem() == this && this.canPickUpCrystal(is)) {
				if (!ep.capabilities.isCreativeMode)
					is.setItemDamage(1);
				e.setDead();
				int x = MathHelper.floor_double(e.posX);
				int y = MathHelper.floor_double(e.posY);
				int z = MathHelper.floor_double(e.posZ);
				Block id = ep.worldObj.getBlock(x, y-1, z);
				if (id == Blocks.bedrock)
					ep.worldObj.setBlockToAir(x, y-1, z);
				ReikaSoundHelper.playBreakSound(ep.worldObj, x, y, z, Blocks.bedrock);
				if (ep.worldObj.isRemote) {
					for (int i = 0; i < 6; i++)
						ReikaRenderHelper.spawnDropParticles(ep.worldObj, x, y, z, Blocks.bedrock, 0);
				}
			}
		}
	}

	public boolean canPickUpCrystal(ItemStack is) {
		return is.getItemDamage() == 0;
	}

	public boolean canPlaceCrystal(ItemStack is) {
		return is.getItemDamage() == 1;
	}

	private boolean checkSpace(World world, int x, int y, int z) {
		for (int i = -1; i <= 1; i++) {
			for (int k = -1; k <= 1; k++) {
				for (int j = 0; j < 3; j++) {
					int dx = x+i;
					int dy = y+j;
					int dz = z+k;
					if (!ReikaWorldHelper.softBlocks(world, dx, dy, dz))
						return false;
					Block b = world.getBlock(dx, dy, dz);
					if (b instanceof BlockFluidBase || b instanceof BlockLiquid)
						return false;
				}
			}
		}
		return true;
	}

}
