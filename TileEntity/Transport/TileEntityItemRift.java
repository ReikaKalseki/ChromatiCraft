/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Transport;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockOre;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemNameTag;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.Linkable;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityRelayPowered;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.ItemElementCalculator;
import Reika.ChromatiCraft.Magic.Network.PylonFinder;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Interfaces.TileEntity.SidePlacedTile;
import Reika.DragonAPI.Libraries.ReikaDirectionHelper;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaArrayHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.BCPipeHandler;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityItemRift extends TileEntityRelayPowered implements SidePlacedTile, Linkable<Coordinate> {

	private static final ElementTagCompound required = ElementTagCompound.of(CrystalElement.LIME);

	private Coordinate otherEnd;
	private ForgeDirection facing;
	private boolean isEmitting;
	private boolean isFunctioning;

	private boolean[] filterSet = ReikaArrayHelper.getTrueArray(ItemCategory.values().length);

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		super.updateEntity(world, x, y, z, meta);
		if (!world.isRemote && isEmitting && this.hasValidConnection()) {
			boolean wasFunctioning = isFunctioning;
			isFunctioning = false;
			IInventory src = this.getAttachment();
			if (src != null) {
				ChromaTiles c = ChromaTiles.getTile(world, otherEnd.xCoord, otherEnd.yCoord, otherEnd.zCoord);
				if (c == this.getTile()) {
					TileEntityItemRift tile = (TileEntityItemRift)otherEnd.getTileEntity(world);
					IInventory tgt = tile.getAttachment();
					if (tgt != null) {
						ItemStack moved = this.transferItems(src, tgt, this.getFacing().getOpposite(), Math.min(tgt.getInventoryStackLimit(), this.getMaxTransferRate(energy.getValue(CrystalElement.LIME))));
						if (moved != null) {
							energy.subtract(CrystalElement.LIME, this.getConsumedEnergy(moved.stackSize));
							ElementTagCompound tag = ItemElementCalculator.instance.getValueForItem(moved);
							int flags = ReikaArrayHelper.booleanToBitflags(tag.flagSet());
							int dir = this.getFacing().getOpposite().ordinal();
							int dd = otherEnd.getTaxicabDistanceTo(x, y, z);
							ReikaPacketHelper.sendDataPacketWithRadius(ChromatiCraft.packetChannel, ChromaPackets.ITEMRIFTMOVE.ordinal(), this, 64, flags, tag.getTotalEnergy(), dir, dd);
							isFunctioning = true;
						}
					}
				}
			}
			if (isFunctioning != wasFunctioning) {
				//ReikaJavaLibrary.pConsole(wasFunctioning+" > "+isFunctioning);
				this.syncAllData(false);
				if (this.hasValidConnection()) {
					TileEntityItemRift te = (TileEntityItemRift)this.getConnection().getTileEntity(world);
					te.isFunctioning = isFunctioning;
					te.syncAllData(false);
				}
			}
		}
		if (world.isRemote && this.hasValidConnection() && isEmitting) {
			this.spawnParticles(world, x, y, z);
		}
	}

	@SideOnly(Side.CLIENT)
	public void doMoveParticles(int flags, int total, ForgeDirection dir, int dist) {
		ArrayList<Integer> li = new ArrayList();
		if (total == 0) {
			li.add(0x22aaff);
		}
		else {
			boolean[] arr = ReikaArrayHelper.booleanFromBitflags(flags, 16);
			for (int i = 0; i < 16; i++) {
				if (arr[i]) {
					li.add(CrystalElement.elements[i].getColor());
				}
			}
		}
		int n = total == 0 ? 2 : total;
		for (int i = 0; i < n; i++) {
			int color = ReikaJavaLibrary.getRandomListEntry(rand, li);
			double px = xCoord+0.5-0.5*dir.offsetX;
			double py = yCoord+0.5-0.5*dir.offsetY;
			double pz = zCoord+0.5-0.5*dir.offsetZ;
			double r = 0.1875;//0.125;
			if (dir.offsetX == 0)
				px = ReikaRandomHelper.getRandomPlusMinus(px, r);
			if (dir.offsetY == 0)
				py = ReikaRandomHelper.getRandomPlusMinus(py, r);
			if (dir.offsetZ == 0)
				pz = ReikaRandomHelper.getRandomPlusMinus(pz, r);
			double v = 0.075;
			EntityCCBlurFX fx = new EntityCCBlurFX(worldObj, px, py, pz, dir.offsetX*v, dir.offsetY*v, dir.offsetZ*v);
			int l = dist*18;
			fx.setIcon(ChromaIcons.FADE_CLOUD).setAlphaFading().setNoSlowdown().setColor(color).setScale(2.5F).setLife(l);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	private IInventory getAttachment() {
		TileEntity te = this.getAdjacentTileEntity(this.getFacing());
		return te instanceof IInventory ? (IInventory)te : null;
	}

	private static int getMaxTransferRate(int energy) {
		return Math.max(1, (int)Math.sqrt(energy/10));
	}

	private static int getConsumedEnergy(int moved) {
		return 10*moved*moved;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.ITEMRIFT;
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles(World world, int x, int y, int z) {
		ForgeDirection dir = this.getFacing().getOpposite();
		ForgeDirection left = ReikaDirectionHelper.getLeftBy90(dir);
		int len = 1+otherEnd.getTaxicabDistanceTo(x, y, z);
		double r = 0.25;
		int n = Math.max(1, len/2-1);
		int[] c = {CrystalElement.LIME.getColor(), 0x22aaff, CrystalElement.YELLOW.getColor()};
		for (int f = 0; f < c.length; f++) {
			for (int i = 0; i < n; i++) {
				double d = (this.getTicksExisted()/2D+i*len*9D/n)%(len*9)/9D-0.5+f*1.25;
				if (d >= len-0.5)
					d -= len;
				double ang = Math.toRadians((this.getTicksExisted()*12D+f*360D/c.length)%360);
				double sin = r*Math.sin(ang);
				double cos = r*Math.cos(ang);
				double px = x+0.5+dir.offsetX*d+left.offsetX*cos;
				double py = y+0.5+dir.offsetY*d+sin;
				double pz = z+0.5+dir.offsetZ*d+left.offsetZ*cos;
				EntityCCBlurFX fx = new EntityCCBlurFX(world, px, py, pz, 0, 0, 0);
				fx.setRapidExpand().setColor(c[f]);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
		}

		for (int i = 0; i <= 1; i++) {
			double d = (this.getTicksExisted()*4)%(len*16)/32D-0.5+i*2;
			double px = x+0.5+d*dir.offsetX;
			double py = y+0.5+d*dir.offsetY;
			double pz = z+0.5+d*dir.offsetZ;
			//EntitySparkleFX fx = new EntitySparkleFX(world, px, py, pz, 0, 0, 0).setScale(1).setLife(15);
			EntityCCBlurFX fx = new EntityCCBlurFX(world, px, py, pz, 0, 0, 0);
			fx.setIcon(ChromaIcons.FADE_GENTLE).setScale(1.25F).setLife(15).setRapidExpand().setAlphaFading();
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	private static ItemStack transferItems(IInventory src, IInventory tgt, ForgeDirection move, int rate) {
		//ReikaJavaLibrary.pConsole(src+" >> "+tgt);
		ItemStack total = null;
		int[] from = src instanceof ISidedInventory ? ((ISidedInventory)src).getAccessibleSlotsFromSide(move.ordinal()) :  ReikaArrayHelper.getLinearArray(src.getSizeInventory());
		for (int i = 0; i < from.length; i++) {
			int slotfrom = from[i];
			ItemStack in = src.getStackInSlot(slotfrom);
			if (in != null && (total == null || (ReikaItemHelper.matchStacks(total, in, true) && ItemStack.areItemStackTagsEqual(total, in)))) {
				boolean extract = src instanceof ISidedInventory ? ((ISidedInventory)src).canExtractItem(slotfrom, in, move.ordinal()) : true;
				if (extract) {
					ItemStack in2 = ReikaItemHelper.getSizedItemStack(in, Math.min(in.stackSize, rate));
					int added = ReikaInventoryHelper.addStackAndReturnCount(in2, tgt, move.getOpposite());
					if (added > 0) {
						in.stackSize -= added;
						if (total == null)
							total = ReikaItemHelper.getSizedItemStack(in2, added);
						else
							total.stackSize += added;
						if (in.stackSize <= 0) {
							src.setInventorySlotContents(slotfrom, null);
							break;
						}
					}
				}
			}
		}
		return total;
	}

	public final void reset() {
		otherEnd = null;
	}

	public final void resetOther() {
		if (otherEnd == null)
			return;
		ChromaTiles m = ChromaTiles.getTile(worldObj, otherEnd.xCoord, otherEnd.yCoord, otherEnd.zCoord);
		if (m == this.getTile()) {
			TileEntityItemRift te = (TileEntityItemRift)otherEnd.getTileEntity(worldObj);
			te.reset();
		}
	}

	private final boolean canConnectTo(World world, int x, int y, int z) {
		int dx = x-xCoord;
		int dy = y-yCoord;
		int dz = z-zCoord;

		//ReikaJavaLibrary.pConsole(isEmitting ? Arrays.toString(source) : Arrays.toString(target));

		if (!ReikaMathLibrary.nBoolsAreTrue(1, dx != 0, dy != 0, dz != 0))
			return false;

		ForgeDirection dir = null;

		if (dx > 0)
			dir = ForgeDirection.EAST;
		if (dx < 0)
			dir = ForgeDirection.WEST;
		if (dy > 0)
			dir = ForgeDirection.UP;
		if (dy < 0)
			dir = ForgeDirection.DOWN;
		if (dz > 0)
			dir = ForgeDirection.SOUTH;
		if (dz < 0)
			dir = ForgeDirection.NORTH;

		if (dir == null)
			return false;
		if (!this.isValidDirection(dir))
			return false;

		TileEntity te = world.getTileEntity(x, y, z);
		if (te instanceof TileEntityItemRift) {
			if (((TileEntityItemRift)te).isEmitting == isEmitting)
				return false;
			for (int i = 1; i < Math.abs(dx+dy+dz); i++) {
				int xi = xCoord+dir.offsetX*i;
				int yi = yCoord+dir.offsetY*i;
				int zi = zCoord+dir.offsetZ*i;
				if (!this.isPassableBlock(world, xi, yi, zi)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	private boolean isPassableBlock(World world, int x, int y, int z) {
		//return ReikaWorldHelper.softBlocks(world, x, y, z) || RayTracer.getTransparentBlocks().contains(BlockKey.getAt(world, x, y, z)) || (ModList.BCTRANSPORT.isLoaded() && world.getBlock(x, y, z) == BCPipeHandler.getInstance().pipeID);
		return PylonFinder.isBlockPassable(world, x, y, z) || (ModList.BCTRANSPORT.isLoaded() && world.getBlock(x, y, z) == BCPipeHandler.getInstance().pipeID);
	}

	private boolean isValidDirection(ForgeDirection dir) {
		return true;
	}

	public final boolean tryConnect(World world, int x, int y, int z) {
		if (otherEnd != null)
			return false;
		if (!this.canConnectTo(world, x, y, z))
			return false;
		if (x == xCoord && y == yCoord && z == zCoord)
			return false;
		otherEnd = new Coordinate(x, y, z);
		return true;
	}

	@Override
	public Coordinate getConnection() {
		return otherEnd;
	}

	public final boolean hasValidConnection() {
		if (otherEnd == null)
			return false;
		ChromaTiles m = ChromaTiles.getTile(worldObj, otherEnd.xCoord, otherEnd.yCoord, otherEnd.zCoord);
		return m == this.getTile() && this.canConnectTo(worldObj, otherEnd.xCoord, otherEnd.yCoord, otherEnd.zCoord);
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setBoolean("emit", isEmitting);
		NBT.setBoolean("func", isFunctioning);

		if (otherEnd != null)
			otherEnd.writeToNBT("endpoint", NBT);

		NBT.setInteger("dir", this.getFacing().ordinal());
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		isEmitting = NBT.getBoolean("emit");
		isFunctioning = NBT.getBoolean("func");

		otherEnd = Coordinate.readFromNBT("endpoint", NBT);

		facing = dirs[NBT.getInteger("dir")];
	}

	public ForgeDirection getFacing() {
		return facing != null ? facing : ForgeDirection.UP;
	}

	@Override
	public void placeOnSide(int s) {
		facing = ForgeDirection.VALID_DIRECTIONS[s].getOpposite();
	}

	public void flip() {
		this.flip(true);
		ChromaSounds.BOUNCE.playSoundAtBlock(this);
	}

	private void flip(boolean other) {
		isEmitting = !isEmitting;
		this.syncAllData(false);
		if (other && otherEnd != null) {
			TileEntity te = otherEnd.getTileEntity(worldObj);
			if (te instanceof TileEntityItemRift) {
				((TileEntityItemRift)te).flip(false);
			}
		}
	}

	@Override
	public boolean isEmitting() {
		return isEmitting;
	}

	public boolean isFunctioning() {
		return isFunctioning;
	}

	@Override
	public boolean checkLocationValidity() {
		ForgeDirection dir = this.getFacing();
		int dx = xCoord+dir.offsetX;
		int dy = yCoord+dir.offsetY;
		int dz = zCoord+dir.offsetZ;
		Block b = worldObj.getBlock(dx, dy, dz);
		return b instanceof BlockChest || b.isSideSolid(worldObj, dx, dy, dz, dir.getOpposite()) || ReikaBlockHelper.getBlockEdgeGap(b, worldObj, dx, dy, dz, dir.getOpposite()) <= 0.0625;
	}

	public void drop() {
		ReikaItemHelper.dropItem(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5, this.getTile().getCraftedProduct());
		this.delete();
	}

	@Override
	protected boolean canReceiveFrom(CrystalElement e, ForgeDirection dir) {
		return dir != this.getFacing() && dir != this.getFacing().getOpposite() && this.isAcceptingColor(e);
	}

	@Override
	public ElementTagCompound getRequiredEnergy() {
		return required;
	}

	@Override
	public boolean isAcceptingColor(CrystalElement e) {
		return e == CrystalElement.LIME;
	}

	@Override
	public int getMaxStorage(CrystalElement e) {
		return 60000;
	}

	@Override
	public void breakBlock() {
		this.resetOther();
	}

	private static enum ItemCategory {
		RAW("Raw Materials", Blocks.redstone_ore),
		TOOLS("Tools", Items.diamond_pickaxe),
		ARMOR("Armor", Items.leather_helmet),
		FOOD("Food", Items.apple),
		MISC("Misc", Blocks.dragon_egg),
		;

		private final ItemStack displayItem;
		public final String displayName;

		private ItemCategory(String s, Item i) {
			this(s, new ItemStack(i));
		}

		private ItemCategory(String s, Block i) {
			this(s, new ItemStack(i));
		}

		private ItemCategory(String s, ItemStack i) {
			displayItem = i;
			displayName = s;
		}

		private static ItemCategory getCategory(ItemStack is) {
			Item i = is.getItem();
			if (ReikaBlockHelper.isOre(is) || Block.getBlockFromItem(i) instanceof BlockOre)
				return RAW;
			if (i instanceof ItemFood)
				return FOOD;
			if (i instanceof ItemArmor)
				return ARMOR;
			if (i instanceof ItemTool || i instanceof ItemPotion || i instanceof ItemNameTag || i instanceof ItemFlintAndSteel || i instanceof ItemEnchantedBook || i instanceof ItemWritableBook || i.isItemTool(is) || i.isMap())
				return TOOLS;
			return MISC;
		}
	}

}
