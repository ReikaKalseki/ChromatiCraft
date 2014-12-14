/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools.Wands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Base.ItemWandBase;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.BlockArray;
import Reika.DragonAPI.Instantiable.Data.Coordinate;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class ItemBuilderWand extends ItemWandBase {

	public static final int RANGE = 6;

	public ItemBuilderWand(int index) {
		super(index);
		this.addEnergyCost(CrystalElement.BROWN, 10);
		this.addEnergyCost(CrystalElement.BLACK, 5);
		this.addEnergyCost(CrystalElement.WHITE, 10);
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int s, float f1, float f2, float f3) {
		if (this.sufficientEnergy(ep)) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[s];
			Block b = world.getBlock(x, y, z);
			int m = world.getBlockMetadata(x, y, z);
			boolean flag = false;
			ArrayList<Coordinate> li = this.getCoordinatesFor(world, x, y, z, dir);
			Collections.sort(li, new ProximitySorter(x, y, z));
			for (Coordinate c : li) {
				if (!this.placeBlockAt(world, c.xCoord, c.yCoord, c.zCoord, b, m, ep))
					break;
				else
					flag = true;
			}
			if (flag) {
				ReikaSoundHelper.playPlaceSound(world, x, y, z, b);
				return true;
			}
		}
		return false;
	}

	public static ArrayList<Coordinate> getCoordinatesFor(World world, int x, int y, int z, ForgeDirection dir) {
		int dx = x+dir.offsetX;
		int dy = y+dir.offsetY;
		int dz = z+dir.offsetZ;
		ArrayList<ForgeDirection> rights = ReikaDirectionHelper.getPerpendicularDirections(dir);
		ForgeDirection d1 = rights.get(0);
		ForgeDirection d2 = rights.get(1);

		Block b = world.getBlock(x, y, z);
		int m = world.getBlockMetadata(x, y, z);

		BlockArray base = new BlockArray();
		base.maxDepth = RANGE*2;
		base.recursiveAddWithBoundsMetadata(world, x, y, z, b, m, x-RANGE, y-RANGE, z-RANGE, x+RANGE, y+RANGE, z+RANGE);

		ArrayList<Coordinate> li = new ArrayList();
		for (int i = -d1.offsetX*RANGE-d2.offsetX*RANGE; i <= d1.offsetX*RANGE+d2.offsetX*RANGE; i++) {
			for (int j = -d1.offsetY*RANGE-d2.offsetY*RANGE; j <= d1.offsetY*RANGE+d2.offsetY*RANGE; j++) {
				for (int k = -d1.offsetZ*RANGE-d2.offsetZ*RANGE; k <= d1.offsetZ*RANGE+d2.offsetZ*RANGE; k++) {
					int ddx = dx+i;
					int ddy = dy+j;
					int ddz = dz+k;
					int dmx = x+i;
					int dmy = y+j;
					int dmz = z+k;
					Block db = world.getBlock(dmx, dmy, dmz);
					int dm = world.getBlockMetadata(dmx, dmy, dmz);
					if (db == b && dm == m && ReikaWorldHelper.softBlocks(world, ddx, ddy, ddz)) {
						if (base.hasBlock(dmx, dmy, dmz)) {
							if (ReikaWorldHelper.lineOfSight(world, ddx+0.5, ddy+0.5, ddz+0.5, dx+0.5, dy+0.5, dz+0.5)) {
								li.add(new Coordinate(ddx, ddy, ddz));
							}
						}
					}
				}
			}
		}
		return li;
	}

	private boolean placeBlockAt(World world, int x, int y, int z, Block b, int m, EntityPlayer ep) {
		if (!ReikaPlayerAPI.playerHasOrIsCreative(ep, b, m)) {
			return false;
		}
		world.setBlock(x, y, z, b, m, 3);
		this.drainPlayer(ep);
		int slot = ReikaInventoryHelper.locateInInventory(b, m, ep.inventory.mainInventory);
		if (slot != -1) {
			ReikaInventoryHelper.decrStack(slot, ep.inventory.mainInventory);
		}
		return true;
	}

	public static class ProximitySorter implements Comparator<Coordinate> {

		private final int posX;
		private final int posY;
		private final int posZ;

		private ProximitySorter(int x, int y, int z) {
			posX = x;
			posY = y;
			posZ = z;
		}

		@Override
		public int compare(Coordinate o1, Coordinate o2) {
			return (int)Math.signum(o1.getDistanceTo(posX, posY, posZ)-o2.getDistanceTo(posX, posY, posZ));
		}

	}

}
