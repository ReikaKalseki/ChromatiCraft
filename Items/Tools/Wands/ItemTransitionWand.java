/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Items.Tools.Wands;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ItemWandBase;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker.BreakerCallback;
import Reika.DragonAPI.Auxiliary.ProgressiveRecursiveBreaker.ProgressiveBreaker;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Interfaces.Block.SemiUnbreakable;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.World.ReikaBlockHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

public class ItemTransitionWand extends ItemWandBase implements BreakerCallback {

	private static HashMap<Integer, BlockReplace> breakers = new HashMap();

	private static final int MAX_DEPTH = 18;
	private static final int MAX_DEPTH_BOOST = 24;

	public ItemTransitionWand(int index) {
		super(index);
		this.addEnergyCost(CrystalElement.GRAY, 2);
		this.addEnergyCost(CrystalElement.YELLOW, 1);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer ep) {
		ep.openGui(ChromatiCraft.instance, ChromaGuis.TRANSITION.ordinal(), world, 0, 0, 0);
		return is;
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer ep, World world, int x, int y, int z, int s, float a, float b, float c) {
		if (!world.isRemote) {
			if (ep.isSneaking()) {
				this.setStoredItem(is, ReikaBlockHelper.getWorldBlockAsItemStack(world, x, y, z));
			}
			else {
				ItemStack store = this.getStoredItem(is);
				if (store == null)
					return false;
				Block id = Block.getBlockFromItem(store.getItem());
				int meta = store.getItemDamage();
				if (id == null)
					return false;
				TransitionMode mode = this.getMode(is);
				if (id == world.getBlock(x, y, z) && meta == world.getBlockMetadata(x, y, z) && mode != TransitionMode.VOLUMETRIC)
					return false;
				if (mode == TransitionMode.VOLUMETRIC) {
					if (!this.setOrGetBlockBox(is, x, y, z))
						return false;
				}
				int depth = mode == TransitionMode.VOLUMETRIC ? Integer.MAX_VALUE : getDepth(ep);
				ProgressiveBreaker br = ProgressiveRecursiveBreaker.instance.addCoordinateWithReturn(world, x, y, z, depth);
				br.call = this;
				br.drops = false;
				//br.extraSpread = true;
				//br.tickRate = 2;
				br.player = ep;
				br.silkTouch = EnchantmentHelper.getSilkTouchModifier(ep);
				BlockReplace brp = new BlockReplace(ep, id, meta, mode);
				brp.silkTouch = br.silkTouch;
				if (mode == TransitionMode.VOLUMETRIC) {
					br.bounds = this.getStoredBox(is);
					br.isOmni = true;
					br.pathTracking = true;
				}
				is.stackTagCompound.removeTag("bbox");
				breakers.put(br.hashCode(), brp);
			}
		}
		return true;
	}

	public static int getDepth(EntityPlayer ep) {
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
	public void onBreak(ProgressiveBreaker b, World world, int x, int y, int z, Block id, int meta) {
		BlockReplace r = breakers.get(b.hashCode());
		if (r != null) {
			boolean exists = world.getPlayerEntityByName(r.player.getCommandSenderName()) != null;
			if (exists) {
				this.drainPlayer(r.player);
				ReikaPlayerAPI.findAndDecrItem(r.player, r.place, r.placeM);
				world.setBlock(x, y, z, r.place, r.placeM, 3);
				ReikaSoundHelper.playPlaceSound(world, x, y, z, r.place);
				ArrayList<ItemStack> li = id.getDrops(world, x, y, z, meta, 0);
				if (r.silkTouch && ReikaBlockHelper.attemptSilkTouch(world, x, y, z)) {
					li.clear();
					ItemStack is = ReikaBlockHelper.getSilkTouch(world, x, y, z, id, meta, r.player, true);
					if (is != null)
						li.add(is);
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
	public boolean canBreak(ProgressiveBreaker b, World world, int x, int y, int z, Block id, int meta) {
		BlockReplace r = breakers.get(b.hashCode());
		if (!r.player.capabilities.isCreativeMode) {
			if (ReikaBlockHelper.isUnbreakable(world, x, y, z, id, meta, r.player))
				return false;
			if (id instanceof SemiUnbreakable)
				if (((SemiUnbreakable)id).isUnbreakable(world, x, y, z, meta))
					return false;
		}
		if (world.getTileEntity(x, y, z) != null)
			return false;
		if (r != null) {
			boolean exists = world.getPlayerEntityByName(r.player.getCommandSenderName()) != null;
			if (exists) {
				if (this.sufficientEnergy(r.player) && ReikaPlayerAPI.playerHasOrIsCreative(r.player, r.place, r.placeM)) {
					boolean perm = world.isRemote || ReikaPlayerAPI.playerCanBreakAt((WorldServer)world, x, y, z, (EntityPlayerMP)r.player);
					switch(r.mode) {
						case CONTIGUOUS:
							return perm;
						case AIRONLY:
							return perm && ReikaWorldHelper.checkForAdjNonCube(world, x, y, z) != null;
						case VOLUMETRIC:
							return perm;
						default:
							return false;
					}
				}
			}
		}
		return false;
	}

	@Override
	public void onFinish(ProgressiveBreaker b) {
		BlockReplace r = breakers.get(b.hashCode());
		EntityPlayer ep = r.player;
		if (!ep.capabilities.isCreativeMode) {
			for (ItemStack is : r.drops) {
				if (MinecraftForge.EVENT_BUS.post(new EntityItemPickupEvent(ep, new EntityItem(ep.worldObj, ep.posX, ep.posY, ep.posZ, is)))) {
					continue;
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
		VOLUMETRIC("Volumetric");

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


}
