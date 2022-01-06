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
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Ability.AbilityHelper;
import Reika.ChromatiCraft.Base.ItemBlockChangingWand;
import Reika.ChromatiCraft.Block.BlockCrystalTank.CrystalTankAuxTile;
import Reika.ChromatiCraft.Items.Tools.ItemInventoryLinker;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Storage.TileEntityCrystalTank;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker.ProgressiveBreaker;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.AbstractSearch.PropagationCondition;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Interfaces.Block.SemiUnbreakable;
import Reika.DragonAPI.Libraries.ReikaInventoryHelper;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.ReikaChiselHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.TinkerBlockHandler;

public class ItemTransitionWand extends ItemBlockChangingWand {

	private static HashMap<Integer, BlockReplace> breakers = new HashMap();

	private static final int MAX_DEPTH = 18;
	private static final int MAX_DEPTH_BOOST = 24;

	public ItemTransitionWand(int index) {
		super(index);
		this.addEnergyCost(CrystalElement.GRAY, 2);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		if (ep.isSneaking())
			is.stackTagCompound.removeTag("bbox");
		else
			ep.openGui(ChromatiCraft.instance, ChromaGuis.TRANSITION.ordinal(), world, 0, 0, 0);
		return is;
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int s, float a, float b, float c) {
		if (!world.isRemote) {
			if (ep.isSneaking()) {
				this.setStoredItem(is, this.parseItemStack(world, x, y, z));
			}
			else {
				ItemStack store = this.getStoredItem(is);
				if (store == null)
					return false;
				Block id = Block.getBlockFromItem(store.getItem());
				int meta = store.getItemDamage();
				if (id == null || id == Blocks.air)
					return false;
				TransitionMode mode = this.getMode(is);
				if (id == world.getBlock(x, y, z) && meta == world.getBlockMetadata(x, y, z) && mode != TransitionMode.VOLUMETRIC)
					return false;
				if (mode == TransitionMode.VOLUMETRIC) {
					if (!this.setOrGetBlockBox(is, x, y, z))
						return false;
				}
				int depth = mode == TransitionMode.VOLUMETRIC || mode == TransitionMode.COLUMN ? Integer.MAX_VALUE : this.getDepth(ep, world, x, y, z);
				ProgressiveBreaker br = ProgressiveRecursiveBreaker.instance.addCoordinateWithReturn(world, x, y, z, depth);
				br.call = this;
				br.drops = false;
				br.extraSpread = false; //DO NOT USE
				//br.tickRate = 2;
				br.player = ep;
				br.silkTouch = EnchantmentHelper.getSilkTouchModifier(ep);
				BlockReplace brp = new BlockReplace(ep, id, meta, mode);
				brp.silkTouch = br.silkTouch;
				br.causeUpdates = false;
				if (mode == TransitionMode.VOLUMETRIC) {
					br.bounds = this.getStoredBox(is);
					br.isOmni = true;
					br.pathTracking = true;
				}
				if (mode == TransitionMode.COLUMN) {
					br.bounds = new BlockBox(x, 0, z, x, 256, z);
				}
				is.stackTagCompound.removeTag("bbox");
				breakers.put(br.hashCode(), brp);
			}
		}
		return true;
	}

	@Override
	public boolean canSpreadOn(World world, int x, int y, int z, Block b, int meta) {
		return true;
	}

	private ItemStack parseItemStack(World world, int x, int y, int z) {
		Block b = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		if (b instanceof BlockLeaves)
			meta = meta&3;
		else if (b == ChromaBlocks.TANK.getBlockInstance())
			meta -= meta%2;
		return new ItemStack(b, 1, meta);
	}

	@Override
	public int getDepth(EntityPlayer ep, World world, int x, int y, int z) {
		return canUseBoostedEffect(ep) ? MAX_DEPTH_BOOST : MAX_DEPTH;
	}

	private boolean setOrGetBlockBox(ItemStack is, int x, int y, int z) {
		BlockBox bb = this.getStoredBox(is);
		if (bb == null) {
			if (is.stackTagCompound == null)
				is.stackTagCompound = new NBTTagCompound();
			NBTTagCompound tag = is.stackTagCompound.getCompoundTag("bbox");
			if (tag.hasNoTags()) {
				tag.setInteger("minx", x);
				tag.setInteger("miny", y);
				tag.setInteger("minz", z);
			}
			else {
				tag.setInteger("maxx", x);
				tag.setInteger("maxy", y);
				tag.setInteger("maxz", z);
			}
			is.stackTagCompound.setTag("bbox", tag);
		}
		bb = this.getStoredBox(is);
		return bb != null;
	}

	private BlockBox getStoredBox(ItemStack is) {
		if (is.stackTagCompound != null) {
			NBTTagCompound tag = is.stackTagCompound.getCompoundTag("bbox");
			if (tag.func_150296_c().size() == 6) {
				return BlockBox.readFromNBT(tag);
			}
		}
		return null;
	}

	public ItemStack getStoredItem(ItemStack tool) {
		if (tool.stackTagCompound != null) {
			NBTTagCompound tag = tool.stackTagCompound.getCompoundTag("stored");
			ItemStack ret = ItemStack.loadItemStackFromNBT(tag);
			return ret;
		}
		return null;
	}

	public void setStoredItem(ItemStack tool, ItemStack tostore) {
		if (tool.stackTagCompound == null)
			tool.stackTagCompound = new NBTTagCompound();
		NBTTagCompound tag = new NBTTagCompound();
		tostore.writeToNBT(tag);
		tool.stackTagCompound.setTag("stored", tag);
	}

	@Override
	public void onPreBreak(ProgressiveBreaker b, World world, int x, int y, int z, Block id, int meta) {
		BlockReplace r = breakers.get(b.hashCode());
		if (r != null) {
			boolean exists = world.getPlayerEntityByName(r.player.getCommandSenderName()) != null;
			if (exists) {
				if (this.isUpgradingTank(world, x, y, z, id, meta, r)) {
					CrystalTankAuxTile te = (CrystalTankAuxTile)world.getTileEntity(x, y, z);
					TileEntityCrystalTank con = te.getTankController();
					if (con != null) {
						con.pauseCapacityUpdate();
					}
				}
				ArrayList<ItemStack> li = id.getDrops(world, x, y, z, meta, 0);
				if (r.silkTouch) {
					ItemStack is = ReikaBlockHelper.getSilkTouch(world, x, y, z, id, meta, r.player, true);
					if (is != null) {
						li.clear();
						li.add(is);
					}
				}
				for (ItemStack is : li) {
					r.drops.add(is);
				}
			}
			else {
				b.terminate();
			}
		}
	}

	@Override
	public void onPostBreak(ProgressiveBreaker b, World world, int x, int y, int z, Block id, int meta) {
		BlockReplace r = breakers.get(b.hashCode());
		if (r != null) {
			boolean exists = world.getPlayerEntityByName(r.player.getCommandSenderName()) != null;
			if (exists) {
				world.setBlock(x, y, z, r.place, r.placeM, 3);
				ReikaSoundHelper.playPlaceSound(world, x, y, z, r.place);
				if (this.isUpgradingTank(world, x, y, z, id, meta, r)) {
					CrystalTankAuxTile te = (CrystalTankAuxTile)world.getTileEntity(x, y, z);
					TileEntityCrystalTank con = te.getTankController();
					if (con != null) {
						con.unpauseCapacityUpdate();
					}
				}
				this.drainPlayer(r.player);
				if (!r.player.capabilities.isCreativeMode) {
					if (!ReikaPlayerAPI.findAndDecrItem(r.player, r.place, r.placeM)) {
						if (ModList.CHISEL.isLoaded()) {
							int ret = ReikaChiselHandler.getChiselableSource(r.player.inventory, r.place, r.placeM);
							if (ret != -1) {
								ReikaInventoryHelper.decrStack(ret, r.player.inventory, 1);
							}
						}
					}
				}
			}
			else {
				b.terminate();
			}
		}
	}

	@Override
	public boolean canBreak(ProgressiveBreaker b, World world, int x, int y, int z, Block id, int meta) {
		BlockReplace r = breakers.get(b.hashCode());
		if (world.getTileEntity(x, y, z) != null) {
			if (this.isUpgradingTank(world, x, y, z, id, meta, r) || (ModList.TINKERER.isLoaded() && TinkerBlockHandler.getInstance().isSmelteryBlock(id)))
				;
			else
				return false;
		}
		if (!r.player.capabilities.isCreativeMode) {
			if (ReikaBlockHelper.isUnbreakable(world, x, y, z, id, meta, r.player))
				return false;
			if (id instanceof SemiUnbreakable)
				if (((SemiUnbreakable)id).isUnbreakable(world, x, y, z, meta))
					return false;
		}
		if (r != null) {
			boolean exists = world.getPlayerEntityByName(r.player.getCommandSenderName()) != null;
			if (exists) {
				if (this.sufficientEnergy(r.player) && this.playerHas(r.player, r.place, r.placeM)) {
					boolean perm = world.isRemote || (ReikaPlayerAPI.playerCanBreakAt((WorldServer)world, x, y, z, (EntityPlayerMP)r.player));
					switch(r.mode) {
						case CONTIGUOUS:
							return perm;
						case AIRONLY:
							return perm && ReikaWorldHelper.checkForAdjNonCube(world, x, y, z) != null;
						case VOLUMETRIC:
							return perm;
						case COLUMN:
							return perm;
					}
				}
			}
		}
		return false;
	}

	private boolean isUpgradingTank(World world, int x, int y, int z, Block id, int meta, BlockReplace r) {
		return id == ChromaBlocks.TANK.getBlockInstance() && r != null && r.place == id && id.damageDropped(meta) != id.damageDropped(r.placeM);
	}

	private boolean playerHas(EntityPlayer ep, Block b, int m) {
		if (ReikaPlayerAPI.playerHasOrIsCreative(ep, b, m))
			return true;
		if (ModList.CHISEL.isLoaded()) {
			int ret = ReikaChiselHandler.getChiselableSource(ep.inventory, b, m);
			if (ret != -1)
				return true;
		}
		return false;
	}

	@Override
	public void onFinish(ProgressiveBreaker b) {
		BlockReplace r = breakers.get(b.hashCode());
		EntityPlayer ep = r.player;
		if (!ep.capabilities.isCreativeMode) {
			for (ItemStack is : r.drops) {
				if (ItemInventoryLinker.tryLinkItem(ep, is)) {
					continue;
				}
				if (Chromabilities.MEINV.enabledOn(ep)) {
					int added = AbilityHelper.instance.addStackToMESystem(ep, is);
					if (added > 0) {
						if (added >= is.stackSize) {
							continue;
						}
						else {
							is.stackSize -= added;
						}
					}
				}
				ReikaPlayerAPI.addOrDropItem(is, ep);
			}
		}
		breakers.remove(b.hashCode());
	}

	private static class BlockReplace {
		private final EntityPlayer player;
		private final Block place;
		private final int placeM;
		private final TransitionMode mode;
		private ArrayList<ItemStack> drops = new ArrayList();
		private boolean silkTouch;

		private BlockReplace(EntityPlayer ep, Block b, int meta, TransitionMode m) {
			place = b;
			placeM = meta;
			player = ep;
			mode = m;
		}
	}

	public static enum TransitionMode {
		CONTIGUOUS("Contiguous"),
		AIRONLY("Exposed Contiguous"),
		VOLUMETRIC("Volumetric"),
		COLUMN("Columnar");

		public final String desc;

		public static final TransitionMode[] list = values();

		private TransitionMode(String s) {
			desc = s;
		}
	}

	public void setMode(ItemStack is, TransitionMode mode) {
		if (is.stackTagCompound == null)
			is.stackTagCompound = new NBTTagCompound();
		is.stackTagCompound.setInteger("mode", mode.ordinal());
	}

	public TransitionMode getMode(ItemStack is) {
		if (is.stackTagCompound != null) {
			int idx = is.stackTagCompound.getInteger("mode");
			return TransitionMode.list[idx];
		}
		return TransitionMode.CONTIGUOUS;
	}

	@Override
	public void getSpreadBlocks(World world, int x, int y, int z, BlockArray arr, EntityPlayer ep, ItemStack is) {
		final BlockKey bk = BlockKey.getAt(world, x, y, z);
		switch(this.getMode(is)) {
			case CONTIGUOUS:
				arr.recursiveAddWithBoundsMetadata(world, x, y, z, bk.blockID, bk.metadata, x-32, y-32, z-32, x+32, y+32, z+32);
				break;
			case AIRONLY:
				PropagationCondition pc = new PropagationCondition() {

					@Override
					public boolean isValidLocation(World world, int x, int y, int z, Coordinate from) {
						return bk.matchInWorld(world, x, y, z) && ReikaWorldHelper.isExposedToAir(world, x, y, z);
					}

				};
				arr.recursiveAddCallbackWithBounds(world, x, y, z, x-32, y-32, z-32, x+32, y+32, z+32, pc);
				break;
			case VOLUMETRIC:
				BlockBox box = this.getStoredBox(is);
				if (box == null && is.stackTagCompound != null) {
					NBTTagCompound tag = is.stackTagCompound.getCompoundTag("bbox");
					if (tag.func_150296_c().size() == 3) {
						int x0 = tag.getInteger("minx");
						int y0 = tag.getInteger("miny");
						int z0 = tag.getInteger("minz");
						box = new BlockBox(x0, y0, z0, x, y, z);
					}
				}
				if (box != null) {
					for (int dx = box.minX; dx <= box.maxX; dx++) {
						for (int dz = box.minZ; dz <= box.maxZ; dz++) {
							for (int dy = box.minY; dy <= box.maxY; dy++) {
								if (world.getBlock(dx, dy, dz) != Blocks.air)
									arr.addBlockCoordinate(dx, dy, dz);
							}
						}
					}
				}
				break;
			case COLUMN:
				arr.addBlockCoordinate(x, y, z);
				int d = 1;
				boolean flag = true;
				while (flag) {
					flag = false;
					if (bk.matchInWorld(world, x, y+d, z)) {
						arr.addBlockCoordinate(x, y+d, z);
						flag = true;
					}
					d++;
				}
				d = 1;
				flag = true;
				while (flag) {
					flag = false;
					if (bk.matchInWorld(world, x, y-d, z)) {
						arr.addBlockCoordinate(x, y-d, z);
						flag = true;
					}
					d++;
				}
				break;
		}
	}


}
