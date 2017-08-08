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

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Block.BlockRouterNode;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityRouterHub;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;


public class ItemBlockRouterNode extends ItemBlockSidePlaced {

	public ItemBlockRouterNode(Block b) {
		super(b);
	}

	@Override
	public void getSubItems(Item item, CreativeTabs c, List li) {
		super.getSubItems(item, c, li);
		li.add(new ItemStack(this, 1, 1));
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer ep, List li, boolean vb) {
		super.addInformation(is, ep, li, vb);

		if (is.stackTagCompound != null) {
			Coordinate loc = Coordinate.readFromNBT("target", is.stackTagCompound);
			if (loc != null) {
				li.add("Linked to: "+loc.toString());
			}
		}
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer ep, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
		ChromaTiles c = ChromaTiles.getTile(world, x, y, z);
		if (c == ChromaTiles.ROUTERHUB) {
			stack.stackTagCompound = new NBTTagCompound();
			new Coordinate(x, y, z).writeToNBT("target", stack.stackTagCompound);
			return true;
		}

		if (stack.stackTagCompound == null)
			return false;
		if (Coordinate.readFromNBT("target", stack.stackTagCompound) == null)
			return false;
		if (!(Coordinate.readFromNBT("target", stack.stackTagCompound).getTileEntity(world) instanceof TileEntityRouterHub))
			return false;

		return super.onItemUse(stack, ep, world, x, y, z, side, hitX, hitY, hitZ);
	}

	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer ep, World world, int x, int y, int z, int side, float a, float b, float c, int metadata) {
		if (super.placeBlockAt(stack, ep, world, x, y, z, side, a, b, c, metadata)) {
			if (stack.stackTagCompound != null) {
				Coordinate loc = Coordinate.readFromNBT("target", stack.stackTagCompound);
				if (loc != null) {
					((BlockRouterNode)world.getBlock(x, y, z)).setConnection(world, x, y, z, loc);
				}
			}
			return true;
		}
		return false;
	}
}
