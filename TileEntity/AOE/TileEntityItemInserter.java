/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.AOE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.LinkerCallback;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedChromaticBase;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.ItemElementCalculator;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Render.Particle.EntityFloatingSeedsFX;
import Reika.DragonAPI.APIPacketHandler.PacketIDs;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaRedstoneHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class TileEntityItemInserter extends InventoriedChromaticBase implements LinkerCallback {

	private final Coordinate[] targets = new Coordinate[this.getSizeInventory()];
	private final HashMap<Coordinate, InsertionType> locations = new HashMap();
	private final ItemHashMap<ArrayList<Coordinate>> routing = new ItemHashMap();
	private boolean[][] connections = new boolean[6][6];
	private int maxCoord = 0;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (world.isRemote)
			return;
		int slot = this.getTicksExisted()%this.getSizeInventory();
		if (inv[slot] != null && inv[slot].stackSize > 1 && !ReikaRedstoneHelper.isPoweredOnSide(world, x, y, z, dirs[slot])) {
			Coordinate c = this.sendItem(inv[slot]);
			if (c != null) {
				ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.INSERTERACTION.ordinal(), this, 64, Item.getIdFromItem(inv[slot].getItem()), inv[slot].getItemDamage(), c.xCoord, c.yCoord, c.zCoord);
				ReikaInventoryHelper.decrStack(slot, inv);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void sendItemClientside(int id, int meta, int x, int y, int z) {
		ItemStack is = new ItemStack(Item.getItemById(id), 1, meta);
		double dx = x-xCoord;
		double dy = y-yCoord;
		double dz = z-zCoord;
		double dd = ReikaMathLibrary.py3d(dx, dy, dz);
		double v = 0.125;
		double vx = v*dx/dd;
		double vy = v*dy/dd;
		double vz = v*dz/dd;
		double[] angs = ReikaPhysicsHelper.cartesianToPolar(dx, dy, dz);
		int n = 6+rand.nextInt(16);
		ElementTagCompound tag = ItemElementCalculator.instance.getValueForItem(is);
		for (int i = 0; i < n; i++) {
			double px = xCoord+rand.nextDouble();//+0.5;
			double py = yCoord+rand.nextDouble();//0.5;
			double pz = zCoord+rand.nextDouble();//0.5;
			EntityFloatingSeedsFX fx = new EntityFloatingSeedsFX(worldObj, px, py, pz, -angs[2]+270, angs[1]-90);
			fx.angleVelocity = 2.5;
			fx.particleVelocity = v;
			fx.freedom = 10;
			int c = tag == null || tag.isEmpty() ? 0x22aaff : ReikaJavaLibrary.getRandomCollectionEntry(tag.elementSet()).getColor();
			AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(x, y, z).expand(dx, dy, dz);
			fx.setColor(c).setLife(100).setScale(2.5F).setIcon(ChromaIcons.CENTER).markDestination(x, y, z).setNoSlowdown().bound(box);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		this.rebuildCache();
	}

	@Override
	protected void onPreSlotSet(int slot, ItemStack is) {
		if (!ReikaItemHelper.matchStacks(is, inv[slot])) {
			for (int i = 0; i < 6; i++) {
				connections[slot][i] = false;
			}
		}
	}

	@Override
	protected void onSlotSet(int slot, ItemStack is) {
		this.rebuildCache();
	}

	private void rebuildCache() {
		routing.clear();
		for (int i = 0; i < targets.length; i++) {
			ItemStack is = inv[i];
			if (is != null) {
				ArrayList<Coordinate> li = routing.get(is);
				if (li == null) {
					li = new ArrayList();
					routing.put(is, li);
				}
				/*
				li.add(targets[i]);
				 */
				for (int k = 0; k < targets.length; k++) {
					Coordinate c = targets[k];
					if (c != null && this.isLinkEnabled(i, k)) {
						li.add(c);
					}
				}
			}
		}
		this.syncAllData(false);
		ReikaPacketHelper.sendDataPacketWithRadius(DragonAPIInit.packetChannel, PacketIDs.GUIRELOAD.ordinal(), this, 12);
	}

	public boolean isLinkEnabled(int i, int k) {
		return connections[i][k];
	}

	public void toggleConnection(int i, int k) {
		connections[i][k] = !connections[i][k];
		this.rebuildCache();
		//ReikaJavaLibrary.pConsole("Connection "+i+","+k+" set to "+connections[i][k]);
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.INSERTER;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public void linkTo(World world, int x, int y, int z) {
		if (world.provider.dimensionId == worldObj.provider.dimensionId) {
			if (this.isLinkable(world, x, y, z)) {
				this.addCoordinate(x, y, z);
			}
		}
	}

	private boolean isLinkable(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (te == null)
			return true;
		String c = te.getClass().getName().toLowerCase(Locale.ENGLISH);
		if (c.endsWith("manualkineticgenerator"))
			return false;
		if (c.endsWith("engineclockwork") && c.contains("forestry"))
			return false;
		if (c.endsWith("crank") && c.contains("appeng"))
			return false;
		return true;
	}

	private void addCoordinate(int x, int y, int z) {
		if (maxCoord >= inv.length)
			return;
		Coordinate c = new Coordinate(x, y, z);
		if (locations.containsKey(c))
			return;
		locations.put(c, InsertionType.INVENTORY);
		targets[maxCoord] = c;
		maxCoord++;
		this.rebuildCache();
	}

	public void removeCoordinate(int slot) {
		Coordinate c = targets[slot];
		if (c != null) {
			targets[slot] = null;
			locations.remove(c);
			if (inv[slot] != null) {
				routing.get(inv[slot]).remove(c);
			}
		}
		for (int i = 0; i < 6; i++) {
			connections[i][slot] = false;
		}
		maxCoord = slot;
		this.rebuildCache();
	}

	public InsertionType getInsertionType(int slot) {
		Coordinate c = targets[slot];
		return c != null ? locations.get(c) : null;
	}

	public Coordinate getLink(int slot) {
		return targets[slot];
	}

	public void setInsertionType(int slot, InsertionType type) {
		Coordinate c = targets[slot];
		if (c != null) {
			locations.put(c, type);
		}
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack is, int side) {
		return false;
	}

	@Override
	public int getSizeInventory() {
		return 6;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		return ReikaItemHelper.matchStacks(inv[slot], is);
	}

	public Coordinate sendItem(ItemStack is) {
		ArrayList<Coordinate> li = routing.get(is);
		if (li == null || li.isEmpty())
			return null;
		Coordinate c = li.get(rand.nextInt(li.size()));
		while (c.getBlock(worldObj) == Blocks.air && !li.isEmpty()) {
			li.remove(c);
			if (!li.isEmpty()) {
				c = li.get(rand.nextInt(li.size()));
			}
			else {
				c = null;
			}
		}
		return c != null ? (locations.get(c).send(worldObj, c, ReikaItemHelper.getSizedItemStack(is, 1), this.getPlacer()) ? c : null) : null;
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		for (int i = 0; i < this.getSizeInventory(); i++) {
			Coordinate c = targets[i];
			if (c != null) {
				c.writeToNBT("loc_"+i, NBT);
			}
		}

		NBTTagList li = new NBTTagList();
		for (Coordinate c : locations.keySet()) {
			InsertionType type = locations.get(c);
			NBTTagCompound tag = new NBTTagCompound();
			c.writeToNBT("loc", tag);
			tag.setInteger("type", type.ordinal());
			li.appendTag(tag);
		}
		NBT.setTag("locs", li);

		for (int i = 0; i < 6; i++) {
			byte n = ReikaArrayHelper.booleanToByteBitflags(connections[i]);
			NBT.setByte("conn_"+i, n);
		}

		NBT.setInteger("maxc", maxCoord);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		for (int i = 0; i < this.getSizeInventory(); i++) {
			if (NBT.hasKey("loc_"+i)) {
				targets[i] = Coordinate.readFromNBT("loc_"+i, NBT);
			}
			else {
				targets[i] = null;
			}
		}

		locations.clear();
		NBTTagList li = NBT.getTagList("locs", NBTTypes.COMPOUND.ID);
		for (NBTTagCompound tag : ((List<NBTTagCompound>)li.tagList)) {
			Coordinate c = Coordinate.readFromNBT("loc", tag);
			InsertionType type = InsertionType.list[tag.getInteger("type")];
			locations.put(c, type);
		}

		for (int i = 0; i < 6; i++) {
			byte n = NBT.getByte("conn_"+i);
			connections[i] = ReikaArrayHelper.booleanFromByteBitflags(n, 6);
		}

		maxCoord = NBT.getInteger("maxc");
	}

	public static enum InsertionType {
		INVENTORY("Inventory"),
		RIGHTCLICK("Right-Click"),
		LEFTCLICK("Left-Click"),
		ENTITY("Entity");

		public final String displayName;

		private static final InsertionType[] list = values();

		private InsertionType(String s) {
			displayName = s;
		}

		private boolean send(World world, Coordinate c, ItemStack is, EntityPlayer ep) {
			switch(this) {
				case INVENTORY:
					TileEntity te = c.getTileEntity(world);
					if (te instanceof IInventory) {
						return ReikaInventoryHelper.addToIInv(is, (IInventory)te);
					}
					return false;
				case RIGHTCLICK: {
					int orig = is.stackSize;
					ItemStack hold = ep.getCurrentEquippedItem();
					ep.setCurrentItemOrArmor(0, is);
					boolean flag = c.getBlock(world).onBlockActivated(world, c.xCoord, c.yCoord, c.zCoord, ep, 1, 0, 0, 0);
					ItemStack ret = ep.getCurrentEquippedItem();
					ep.setCurrentItemOrArmor(0, hold);
					return /*flag && */(ret == null || ret.stackSize < orig);
				}
				case LEFTCLICK: {
					ItemStack hold = ep.getCurrentEquippedItem();
					int orig = is.stackSize;
					ep.setCurrentItemOrArmor(0, is);
					c.getBlock(world).onBlockClicked(world, c.xCoord, c.yCoord, c.zCoord, ep);
					ItemStack ret = ep.getCurrentEquippedItem();
					ep.setCurrentItemOrArmor(0, hold);
					return ret == null || ret.stackSize < orig;
				}
				case ENTITY:
					EntityItem ei = ReikaItemHelper.dropItem(world, c.xCoord+0.5, c.yCoord+c.getBlock(world).getBlockBoundsMaxY()+0.125, c.zCoord+0.5, is);
					ei.motionX = ei.motionY = ei.motionZ = 0;
					ei.delayBeforeCanPickup = 0;
					ei.lifespan = Integer.MAX_VALUE;
					MinecraftForge.EVENT_BUS.post(new ItemTossEvent(ei, ep));
					return true;
			}
			return false;
		}

		public InsertionType next() {
			return this.ordinal() != list.length-1 ? list[this.ordinal()+1] : list[0];
		}
	}

}
