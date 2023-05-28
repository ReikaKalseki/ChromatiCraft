/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
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
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.fluids.FluidStack;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.Interfaces.LinkerCallback;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedChromaticBase;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.ItemElementCalculator;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityCCFloatingSeedsFX;
import Reika.DragonAPI.APIPacketHandler.PacketIDs;
import Reika.DragonAPI.DragonAPIInit;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Instantiable.Effects.EntityFloatingSeedsFX;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.ReikaFluidHelper;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.MathSci.ReikaPhysicsHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaRedstoneHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class TileEntityItemInserter extends InventoriedChromaticBase implements LinkerCallback {

	public static final String DROP_TAG = "item_inserter_dropped";

	public static final int TARGETS = 6;
	public static final int EXPORT_SLOTS = 9;

	private final Coordinate[] targets = new Coordinate[TARGETS];
	private final HashMap<Coordinate, InsertionType> locations = new HashMap();
	private final ItemHashMap<ArrayList<Coordinate>> routing = new ItemHashMap();
	private boolean[][] connections = new boolean[6][6];
	private int maxCoord = 0;
	public boolean omniMode = false;
	//private ItemStack pendingOutput;

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (!world.isRemote) {
			for (int i = this.getSizeInventory()-1; i >= TARGETS; i--) {
				if (inv[i] == null && inv[i-1] != null) {
					inv[i] = inv[i-1];
					inv[i-1] = null;
				}
				else if (inv[i] != null && inv[i-1] != null && ReikaItemHelper.areStacksCombinable(inv[i], inv[i-1], inv[i].getMaxStackSize())) {
					inv[i].stackSize = inv[i].stackSize+inv[i-1].stackSize;
					inv[i-1] = null;
				}
			}
		}
		if (inv[TARGETS] != null) {
			if (world.isRemote)
				this.doJammedFX(world, x, y, z);
			return;
		}
		int slot = this.getTicksExisted()%targets.length;
		if (!world.isRemote && inv[slot] != null && (omniMode || inv[slot].stackSize > 1) && !ReikaRedstoneHelper.isPoweredOnSide(world, x, y, z, dirs[slot])) {
			Coordinate c = this.sendItem(slot);
			if (c != null) {
				ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.INSERTERACTION.ordinal(), this, 64, Item.getIdFromItem(inv[slot].getItem()), inv[slot].getItemDamage(), c.xCoord, c.yCoord, c.zCoord);
				ReikaInventoryHelper.decrStack(slot, inv);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void doJammedFX(World world, int x, int y, int z) {
		double px = ReikaRandomHelper.getRandomBetween(x, x+1D);
		double pz = ReikaRandomHelper.getRandomBetween(z, z+1D);
		int l = ReikaRandomHelper.getRandomBetween(5, 9);
		if (rand.nextInt(3) > 0) {
			l = ReikaRandomHelper.getRandomBetween(8, 12);
			switch(rand.nextInt(4)) {
				case 0:
					px = x;
					break;
				case 1:
					px = x+1;
					break;
				case 2:
					pz = z;
					break;
				case 3:
					pz = z+1;
					break;
			}
		}
		double py = y+1;
		EntityCCBlurFX fx = new EntityCCBlurFX(world, px, py, pz);
		fx.setIcon(ChromaIcons.FADE_RAY).setRapidExpand().setColor(0xff0000).setLife(l).setScale(0.9F).setGravity(-(float)ReikaRandomHelper.getRandomBetween(0.125, 0.375));
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
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
			EntityFloatingSeedsFX fx = new EntityCCFloatingSeedsFX(worldObj, px, py, pz, -angs[2]+270, angs[1]-90, ChromaIcons.CENTER);
			fx.angleVelocity = 2.5;
			fx.particleVelocity = v;
			fx.freedom = 10;
			int c = tag == null || tag.isEmpty() ? 0x22aaff : ReikaJavaLibrary.getRandomCollectionEntry(rand, tag.elementSet()).getColor();
			AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(x, y, z).expand(dx, dy, dz);
			fx.setColor(c).setLife(100).setScale(2.5F).markDestination(x, y, z).setNoSlowdown().bound(box, false, false);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		this.rebuildCache();
	}

	@Override
	protected void onPreSlotSet(int slot, ItemStack is) {
		if (!omniMode && !ReikaItemHelper.matchStacks(is, inv[slot])) {
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
		return slot == this.getSizeInventory()-1;
	}

	@Override
	public int getSizeInventory() {
		return TARGETS+EXPORT_SLOTS;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		if (slot >= targets.length)
			return false;
		if (ReikaFluidHelper.getFluidForItem(is) != null)
			if (is.stackSize > 1 || inv[slot] != null)
				return false;
		return omniMode ? targets[slot] != null : ReikaItemHelper.matchStacks(inv[slot], is);
	}

	private Coordinate sendItem(int slot) {
		Coordinate c;
		ItemStack is = inv[slot];
		if (omniMode) {
			c = this.isLinkEnabled(slot, slot) ? targets[slot] : null;
		}
		else {
			ArrayList<Coordinate> li = routing.get(is);
			if (li == null || li.isEmpty())
				return null;
			c = li.get(rand.nextInt(li.size()));
			while (c.getBlock(worldObj) == Blocks.air && !li.isEmpty()) {
				li.remove(c);
				if (!li.isEmpty()) {
					c = li.get(rand.nextInt(li.size()));
				}
				else {
					c = null;
				}
			}
		}
		if (c == null)
			return null;
		ItemStack put = ReikaItemHelper.getSizedItemStack(is, 1);
		ItemStack left = locations.get(c).send(worldObj, c, put, this.getPlacer());
		if (left == null || left.stackSize < put.stackSize || ReikaItemHelper.matchStacks(left, is.getItem().getContainerItem(is))) {
			if (left != null) {
				if (ReikaInventoryHelper.addOrSetStack(left, inv, TARGETS)) {
					inv[slot] = left.copy();
				}
				else {
					ReikaItemHelper.dropItem(worldObj, xCoord+0.5, yCoord+1.25, zCoord+0.5, left);
				}
			}
			return c;
		}
		else {
			return null;
		}
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		for (int i = 0; i < targets.length; i++) {
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

		NBT.setBoolean("uselast", omniMode);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		for (int i = 0; i < targets.length; i++) {
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

		omniMode = NBT.getBoolean("uselast");
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

		private ItemStack send(World world, Coordinate c, ItemStack is, EntityPlayer ep) {
			switch(this) {
				case INVENTORY:
					TileEntity te = c.getTileEntity(world);
					if (ReikaInventoryHelper.isAutomatableInventory(te)) {
						if (ReikaInventoryHelper.addToIInv(is, (IInventory)te))
							return null;
					}
					return is;
				case RIGHTCLICK: {
					ItemStack orig = is.copy();
					ItemStack hold = ep.getCurrentEquippedItem();
					FluidStack fs = ReikaFluidHelper.getFluidForItem(is);
					ep.setCurrentItemOrArmor(0, is);
					boolean flag = false;
					if (is.getItem() instanceof ItemBucket && c.offset(0, 1, 0).isEmpty(world) && is.stackSize == 1) {
						flag = ((ItemBucket)is.getItem()).tryPlaceContainedLiquid(world, c.xCoord, c.yCoord+1, c.zCoord);
						if (flag) {
							ep.setCurrentItemOrArmor(0, new ItemStack(Items.bucket, is.stackSize, 0));
						}
					}
					else if (fs != null && fs.getFluid().canBePlacedInWorld() && c.offset(0, 1, 0).isEmpty(world) && is.stackSize == 1) {
						flag = is.getItem().onItemUse(is, ep, world, c.xCoord, c.yCoord+1, c.zCoord, 1, 0, 0, 0);
					}
					else {
						flag = c.getBlock(world).onBlockActivated(world, c.xCoord, c.yCoord, c.zCoord, ep, 1, 0, 0, 0);
					}
					ItemStack ret = ep.getCurrentEquippedItem();
					ep.setCurrentItemOrArmor(0, hold);
					return /*flag ? */ret;
				}
				case LEFTCLICK: {
					ItemStack hold = ep.getCurrentEquippedItem();
					ItemStack orig = is.copy();
					ep.setCurrentItemOrArmor(0, is);
					c.getBlock(world).onBlockClicked(world, c.xCoord, c.yCoord, c.zCoord, ep);
					ItemStack ret = ep.getCurrentEquippedItem();
					ep.setCurrentItemOrArmor(0, hold);
					return ret;
				}
				case ENTITY:
					TileEntityItemCollector.haltCollection = true;
					EntityItem ei = ReikaItemHelper.dropItem(world, c.xCoord+0.5, c.yCoord+c.getBlock(world).getBlockBoundsMaxY()+0.125, c.zCoord+0.5, is);
					ei.motionX = ei.motionY = ei.motionZ = 0;
					ei.delayBeforeCanPickup = 0;
					ei.lifespan = Integer.MAX_VALUE;
					ei.getEntityData().setBoolean(DROP_TAG, true);
					MinecraftForge.EVENT_BUS.post(new ItemTossEvent(ei, ep));
					TileEntityItemCollector.haltCollection = false;
					return null;
			}
			return is;
		}

		public InsertionType next() {
			return this.ordinal() != list.length-1 ? list[this.ordinal()+1] : list[0];
		}
	}


}
