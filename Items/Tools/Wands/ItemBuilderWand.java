/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools.Wands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.ItemWandBase;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Auxiliary.Trackers.KeyWatcher;
import Reika.DragonAPI.Auxiliary.Trackers.KeyWatcher.Key;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class ItemBuilderWand extends ItemWandBase {

	private static final int RANGE = 6;
	private static final int RANGE_BOOST = 12;

	public ItemBuilderWand(int index) {
		super(index);
		this.addEnergyCost(CrystalElement.BROWN, 10);
		this.addEnergyCost(CrystalElement.BLACK, 5);
		this.addEnergyCost(CrystalElement.WHITE, 10);
	}

	@Override
	public boolean onItemUseFirst(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int s, float f1, float f2, float f3) {
		if (!world.isRemote && this.sufficientEnergy(ep)) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[s];
			Block b = world.getBlock(x, y, z);
			int m = world.getBlockMetadata(x, y, z);
			boolean flag = false;
			ArrayList<Coordinate> li = this.getCoordinatesFor(world, x, y, z, dir, ep);
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

	public static int getRange(EntityPlayer ep) {
		return canUseBoostedEffect(ep) ? RANGE_BOOST : RANGE;
	}

	public static ArrayList<Coordinate> getCoordinatesFor(World world, int x, int y, int z, ForgeDirection dir, EntityPlayer ep) {
		int dx = x+dir.offsetX;
		int dy = y+dir.offsetY;
		int dz = z+dir.offsetZ;
		ArrayList<ForgeDirection> rights = ReikaDirectionHelper.getPerpendicularDirections(dir);
		ForgeDirection d1 = rights.get(0);
		ForgeDirection d2 = rights.get(1);

		Block b = world.getBlock(x, y, z);
		int m = world.getBlockMetadata(x, y, z);

		int r = getRange(ep);

		int rx = KeyWatcher.instance.isKeyDown(ep, Key.LCTRL) ? 0 : r;
		int ry = ep.isSneaking() ? 0 : r;
		int rz = KeyWatcher.instance.isKeyDown(ep, Key.LCTRL) ? 0 : r;
		boolean fill = !KeyWatcher.instance.isKeyDown(ep, Key.TAB);
		boolean fill2 = !KeyWatcher.instance.isKeyDown(ep, Key.TILDE);
		int edge = KeyWatcher.instance.isKeyDown(ep, Key.END) ? ep.isSneaking() ? 2 : 1 : 0;

		BlockArray base = new BlockArray();
		base.maxDepth = 2*r;
		base.recursiveAddWithBoundsMetadata(world, x, y, z, b, m, x-rx, y-ry, z-rz, x+rx, y+ry, z+rz);

		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int minZ = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;
		int maxZ = Integer.MIN_VALUE;

		ArrayList<Coordinate> li = new ArrayList();
		for (int i = -d1.offsetX*rx-d2.offsetX*r; i <= d1.offsetX*rx+d2.offsetX*rx; i++) {
			for (int j = -d1.offsetY*ry-d2.offsetY*ry; j <= d1.offsetY*ry+d2.offsetY*ry; j++) {
				for (int k = -d1.offsetZ*rz-d2.offsetZ*rz; k <= d1.offsetZ*rz+d2.offsetZ*rz; k++) {
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
								minX = Math.min(minX, ddx);
								maxX = Math.max(maxX, ddx);
								minY = Math.min(minY, ddy);
								maxY = Math.max(maxY, ddy);
								minZ = Math.min(minZ, ddz);
								maxZ = Math.max(maxZ, ddz);
							}
						}
					}
				}
			}
		}
		if (!fill || !fill2) {
			HashSet<Coordinate> cp = new HashSet(li);
			Iterator<Coordinate> it = li.iterator();
			while (it.hasNext()) {
				Coordinate c = it.next();
				int n = 0;
				for (int i = -d1.offsetX-d2.offsetX; i <= d1.offsetX+d2.offsetX; i++) {
					for (int j = -d1.offsetY-d2.offsetY; j <= d1.offsetY+d2.offsetY; j++) {
						for (int k = -d1.offsetZ-d2.offsetZ; k <= d1.offsetZ+d2.offsetZ; k++) {
							if (cp.contains(c.offset(i, j, k))) {
								n++;
							}
						}
					}
				}
				if (n > 6) {
					if (!fill)
						it.remove();
				}
				else {
					if (!fill2)
						it.remove();
				}
			}
		}
		if (edge > 0) {
			Iterator<Coordinate> it = li.iterator();
			while (it.hasNext()) {
				Coordinate c = it.next();
				if (edge == 1) {
					if (c.xCoord != minX && c.xCoord != maxX) {
						it.remove();
					}
				}
				else if (edge == 2) {
					if (c.yCoord != minY && c.yCoord != maxY) {
						it.remove();
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
		if (!this.sufficientEnergy(ep))
			return false;
		world.setBlock(x, y, z, b, m, 3);
		if (!ep.capabilities.isCreativeMode) {
			this.drainPlayer(ep);
			ReikaPlayerAPI.findAndDecrItem(ep, b, m);
		}
		return true;
	}

	private static class ProximitySorter implements Comparator<Coordinate> {

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
