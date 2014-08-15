/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base.TileEntity;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Base.TileEntityBase;
import Reika.DragonAPI.Interfaces.RenderFetcher;
import Reika.DragonAPI.Interfaces.TextureFetcher;

import li.cil.oc.api.network.Visibility;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileEntityChromaticBase extends TileEntityBase implements RenderFetcher {

	public final TextureFetcher getRenderer() {
		if (ChromaTiles.TEList[this.getIndex()].hasRender())
			return null;//ChromatiRenderList.getRenderForMachine(ChromatiTiles.TEList[this.getIndex()]);
		else
			return null;
	}

	@Override
	public final boolean canUpdate() {
		return !ChromatiCraft.instance.isLocked();
	}

	@Override
	public final Block getTileEntityBlockID() {
		return ChromaTiles.TEList[this.getIndex()].getBlock();
	}

	public final int getIndex() {
		return this.getTile().ordinal();
	}

	@Override
	protected final String getTEName() {
		return ChromaTiles.TEList[this.getIndex()].getName();
	}

	public abstract ChromaTiles getTile();

	public int getTextureState(ForgeDirection side) {
		return 0;
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT)
	{
		super.writeSyncTag(NBT);

	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT)
	{
		super.readSyncTag(NBT);

	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

	}

	public boolean isThisTE(Block id, int meta) {
		return id == this.getTileEntityBlockID() && meta == this.getIndex();
	}

	@Override
	public final String getName() {
		return this.getTEName();
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		ChromaTiles r = ChromaTiles.TEList[this.getIndex()];
		return pass == 0 || (r.renderInPass1() && pass == 1);
	}

	@Override
	public int getRedstoneOverride() {
		return 0;
	}

	@Override
	protected final Visibility getOCNetworkVisibility() {
		return Visibility.Network;//this.getMachine().isPipe() ? Visibility.Neighbors : Visibility.Network;
	}
}