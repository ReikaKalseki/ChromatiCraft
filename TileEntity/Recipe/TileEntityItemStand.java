/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Recipe;

import java.util.List;
import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Auxiliary.Interfaces.ItemOnRightClick;
import Reika.ChromatiCraft.Auxiliary.Interfaces.OwnedTile;
import Reika.ChromatiCraft.Base.TileEntity.InventoriedChromaticBase;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.ItemElementCalculator;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityCenterBlurFX;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.APIStripper.Strippable;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Instantiable.InertItem;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.CollectionType;
import Reika.DragonAPI.Interfaces.TileEntity.ConditionalUnbreakability;
import Reika.DragonAPI.Interfaces.TileEntity.InertIInv;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

import buildcraft.api.transport.IPipeConnection;
import buildcraft.api.transport.IPipeTile.PipeType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Strippable(value={"buildcraft.api.transport.IPipeConnection"})
public class TileEntityItemStand extends InventoriedChromaticBase implements ItemOnRightClick, OwnedTile/*, HitAction*/, InertIInv, IPipeConnection, ConditionalUnbreakability {

	private InertItem item;
	private Coordinate tile;
	private boolean locked;

	private int updateRadius = 96;

	private static final MultiMap<UUID, WorldLocation> spreadSet = new MultiMap(CollectionType.HASHSET);

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (world.isRemote) {
			if (tile != null) {
				TileEntity te = tile.getTileEntity(world);
				if (te instanceof TileEntityCastingTable && ((TileEntityCastingTable)te).getCraftingTick() > 0) {
					this.spawnCraftParticles(world, x, y, z);
				}
			}
			if (item != null) {
				this.spawnItemParticles(world, x, y, z);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnItemParticles(World world, int x, int y, int z) {
		if (rand.nextInt(2) == 0) {
			double rx = ReikaRandomHelper.getRandomPlusMinus(x+0.5, 0.375);
			double ry = ReikaRandomHelper.getRandomPlusMinus(y+0.5, 0.125);
			double rz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, 0.375);
			float gv = -(float)ReikaRandomHelper.getRandomPlusMinus(0.03125, 0.025);
			int l = ReikaRandomHelper.getRandomPlusMinus(60, 15);
			ElementTagCompound tag = ItemElementCalculator.instance.getValueForItem(inv[0]);
			CrystalElement e = tag != null ? ReikaJavaLibrary.getRandomCollectionEntry(rand, tag.elementSet()) : null;
			int r = e != null ? e.getRed() : 0;
			int g = e != null ? e.getGreen() : 96;
			int b = e != null ? e.getBlue() : 255;
			EntityFX fx = new EntityCCBlurFX(world, rx, ry, rz, 0, 0, 0).setColor(r, g, b).setGravity(gv).setLife(l);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@SideOnly(Side.CLIENT)
	private void spawnCraftParticles(World world, int x, int y, int z) {
		if (rand.nextInt(32) == 0) {
			double rx = ReikaRandomHelper.getRandomPlusMinus(x+0.5, 0.375);
			double rz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, 0.375);
			EntityFX fx = new EntityCenterBlurFX(world, rx, y, rz, 0, 0.1, 0);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		this.updateItem();
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		return false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack is, int side) {
		return false;
	}

	@Override
	public ItemStack onRightClickWith(ItemStack item, EntityPlayer ep) {
		/*
		int has = inv[0] != null ? inv[0].stackSize : 0;
		int sum = item != null ? has+item.stackSize : has;
		boolean all = this.recentClicked() && ReikaItemHelper.matchStacks(item, inv[0]) && item != null && sum <= item.getMaxStackSize();
		if (!all)
			this.dropSlot();
		/*
		if (!all && inv[0] != null) {
			ChromaSounds.ERROR.playSoundAtBlock(this);
			return item;
		}
		 *//*
		ItemStack put = item != null ? (all ? ReikaItemHelper.getSizedItemStack(item, sum) : ReikaItemHelper.getSizedItemStack(item, 1)) : null;
		inv[0] = put;
		this.updateItem();
		if (item != null) {
			if (all)
				item = null;
			else
				item.stackSize--;
		}
		  */

		if (this.isLocked())
			return item;

		if (ChromaItems.HELP.matchWith(item))
			return item;

		UUID uid = ep.getUniqueID();

		if (!spreadSet.isEmpty() && !worldObj.isRemote) {
			ItemStack ret = item == null && spreadSet.containsValueForKey(uid, new WorldLocation(this)) ? null : spreadItems(ep, item);
			spreadSet.remove(uid);
			return ret;
		}

		if (inv[0] == null) {
			if (item == null)
				return null;
			inv[0] = ReikaItemHelper.getSizedItemStack(item, 1);
			item.stackSize--;
		}
		else {
			if (ReikaItemHelper.matchStacks(inv[0], item)) {
				int add = Math.min(item.stackSize, inv[0].getMaxStackSize()-inv[0].stackSize);
				inv[0].stackSize += add;
				item.stackSize -= add;
			}
			else if (item == null) {
				this.dropSlot();
			}
			else {
				this.dropSlot();
				inv[0] = ReikaItemHelper.getSizedItemStack(item, 1);
				item.stackSize--;
			}
		}

		if (item != null && item.stackSize <= 0)
			item = null;

		ChromaSounds.ITEMSTAND.playSoundAtBlock(this);
		return item;
	}

	public void spreadItemWith(EntityPlayer ep, ItemStack is) {
		//if (is != null) {
		//if (inv[0] == null || ReikaItemHelper.matchStacks(is, inv[0])) {
		if (inv[0] == null)
			spreadSet.addValue(ep.getUniqueID(), new WorldLocation(this));
		//}
		//spreadItems(ep, is);
		//}
	}

	private static ItemStack spreadItems(EntityPlayer ep, ItemStack is) {
		if (is == null)
			return null;
		UUID uid = ep.getUniqueID();
		int n = spreadSet.get(uid).size();
		int amt = is.stackSize;
		for (WorldLocation loc : spreadSet.get(uid)) {
			TileEntityItemStand te = (TileEntityItemStand)loc.getTileEntity(ep.worldObj);
			if (te.inv[0] != null)
				amt += te.inv[0].stackSize;
		}
		int div = amt/n;
		int left = amt-div*n;
		//ReikaJavaLibrary.pConsole(amt+" by "+n+", = "+div+" leaving "+left);
		for (WorldLocation loc : spreadSet.get(uid)) {
			TileEntityItemStand te = (TileEntityItemStand)loc.getTileEntity();
			te.inv[0] = ReikaItemHelper.getSizedItemStack(is, div);
			ChromaSounds.ITEMSTAND.playSoundAtBlock(te);
			te.syncAllData(true);
		}
		return ReikaItemHelper.getSizedItemStack(is, left);
	}

	/*
	@Override
	public void onHit(World world, int x, int y, int z, EntityPlayer ep) {
		ChromaSounds.ITEMSTAND.playSoundAtBlock(this, 1, 0.875F);
		this.dropSlot();
		inv[0] = null;
		this.updateItem();
	}
	 */
	private void updateItem() {
		item = inv[0] != null ? new InertItem(worldObj, ReikaItemHelper.getSizedItemStack(inv[0], 1)) : null;
		if (worldObj != null) {
			TileEntity te = tile != null ? tile.getTileEntity(worldObj) : null;
			if (te instanceof TileEntityCastingTable) {
				((TileEntityCastingTable)te).markDirty();
			}
		}
	}

	public EntityItem getItem() {
		return item;
	}

	public void dropSlot() {
		if (inv[0] != null) {
			ReikaItemHelper.dropItem(worldObj, xCoord+0.5, yCoord+1, zCoord+0.5, inv[0]);
			inv[0] = null;
		}
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.STAND;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		this.updateItem();

		if (NBT.hasKey("table"))
			tile = Coordinate.readFromNBT("table", NBT);

		locked = NBT.getBoolean("lock");
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		if (tile != null)
			tile.writeToNBT("table", NBT);

		NBT.setBoolean("lock", locked);
	}

	public void setTable(TileEntityCastingTable te) {
		tile = te != null ? new Coordinate(te) : null;
	}

	@Override
	public boolean onlyAllowOwnersToUse() {
		return true;
	}

	@Override
	@ModDependent(ModList.BCTRANSPORT)
	public ConnectOverride overridePipeConnection(PipeType type, ForgeDirection with) {
		return ConnectOverride.DISCONNECT;
	}

	public void lock(boolean lock) {
		locked = lock;
	}

	public boolean isLocked() {
		return locked;
	}

	@Override
	public int getUpdatePacketRadius() {
		return updateRadius;
	}

	@Override
	public boolean isUnbreakable(EntityPlayer ep) {
		return this.isLocked();
	}

	@Override
	public void getTagsToWriteToStack(NBTTagCompound NBT) {
		this.writeOwnerData(NBT);
	}

	@Override
	public void setDataFromItemStackTag(ItemStack is) {
		this.readOwnerData(is);
	}

	public void syncAfterCraft() {
		updateRadius = -1;
		this.syncAllData(true);
		updateRadius = 96;
	}

	@Override
	public void addTooltipInfo(List li, ItemStack is, boolean shift) {

	}

}
