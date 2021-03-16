/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base.TileEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.Interfaces.OwnedTile;
import Reika.ChromatiCraft.Auxiliary.Render.ChromaRenderList;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Base.TileEntityBase;
import Reika.DragonAPI.Interfaces.TextureFetcher;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.ReikaNBTHelper;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;

import li.cil.oc.api.network.Visibility;

public abstract class TileEntityChromaticBase extends TileEntityBase implements RenderFetcher {

	protected final HashSet<UUID> owners = new HashSet();

	public final TextureFetcher getRenderer() {
		if (this.getTile().hasRender())
			return ChromaRenderList.getRenderForMachine(this.getTile());
		else
			return null;
	}

	@Override
	protected final void onSetPlacer(EntityPlayer ep) {
		this.addOwner(ep);
	}

	public final void addOwner(EntityPlayer ep) {
		owners.add(ep.getUniqueID());
	}

	@Override
	public final boolean allowTickAcceleration() {
		return this.getTile().allowsAcceleration();
	}

	@Override
	public final boolean canUpdate() {
		return this.isTickingTE() && !ChromatiCraft.instance.isLocked();
	}

	protected boolean isTickingTE() {
		return true;
	}

	@Override
	public final Block getTileEntityBlockID() {
		return this.getTile().getBlock();
	}

	public final int getIndex() {
		return this.getTile().ordinal();
	}

	@Override
	protected final String getTEName() {
		return ChromaTiles.TEList[this.getIndex()].getName();
	}

	public abstract ChromaTiles getTile();

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		NBTTagList li = new NBTTagList();
		for (UUID uid : owners) {
			li.appendTag(new NBTTagString(uid.toString()));
		}
		NBT.setTag("owners", li);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		owners.clear();
		NBTTagList li = NBT.getTagList("owners", NBTTypes.STRING.ID);
		for (Object o : li.tagList) {
			NBTTagString tag = (NBTTagString)o;
			UUID uid = UUID.fromString(tag.func_150285_a_());
			owners.add(uid);
		}
	}

	public boolean isThisTE(Block id, int meta) {
		return id == this.getTileEntityBlockID() && meta == this.getIndex();
	}

	@Override
	public final boolean shouldRenderInPass(int pass) {
		ChromaTiles r = ChromaTiles.TEList[this.getIndex()];
		return pass == 0 || (r.renderInPass1() && pass == 1);
	}

	@Override
	public int getRedstoneOverride() {
		return 0;
	}

	@Override
	@ModDependent(ModList.OPENCOMPUTERS)
	public final Visibility getOCNetworkVisibility() {
		return Visibility.Network;//this.getMachine().isPipe() ? Visibility.Neighbors : Visibility.Network;
	}

	public final boolean isOwnedByPlayer(EntityPlayer ep) {
		return this.isOwnedByPlayer(ep.getUniqueID());
	}

	public final boolean isOwnedByPlayer(UUID id) {
		return owners.isEmpty() || owners.contains(id);
	}

	public final Collection<EntityPlayer> getOwners(boolean allowFake) {
		Collection<EntityPlayer> c = new ArrayList();
		for (UUID uid : owners) {
			EntityPlayer ep = worldObj.func_152378_a(uid);
			if (ep != null && (allowFake || !ReikaPlayerAPI.isFake(ep))) {
				c.add(ep);
			}
		}
		return c;
	}

	public boolean onlyAllowOwnersToUse() {
		return false;
	}

	public boolean onlyAllowOwnersToMine() {
		return true;
	}

	public boolean renderModelsInPass1() {
		return false;
	}

	protected final void writeOwnerData(NBTTagCompound NBT) {
		if (this instanceof OwnedTile)
			ReikaNBTHelper.writeCollectionToNBT(owners, NBT, "owners", ReikaNBTHelper.UUIDConverter.instance);
	}

	protected final void readOwnerData(ItemStack is) {
		if (ChromaItems.PLACER.matchWith(is)) {
			if (is.getItemDamage() == this.getTile().ordinal()) {
				if (is.stackTagCompound != null) {
					if (this instanceof OwnedTile) {
						ReikaNBTHelper.readCollectionFromNBT(owners, is.stackTagCompound, "owners", ReikaNBTHelper.UUIDConverter.instance);
					}
				}
			}
		}
	}
}
