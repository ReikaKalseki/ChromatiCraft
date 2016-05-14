/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Base.TileEntity;

import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import Reika.ChromatiCraft.Base.DimensionStructureGenerator.DimensionStructureType;


public abstract class StructureBlockTile<S> extends TileEntity {

	public UUID uid;

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		if (uid != null)
			NBT.setString("uid", uid.toString());
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);

		if (NBT.hasKey("uid"))
			uid = UUID.fromString(NBT.getString("uid"));

		//ReikaJavaLibrary.pConsole(colors+":"+FMLCommonHandler.instance().getEffectiveSide(), worldObj != null && this.getBlockMetadata() == 0);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound NBT = new NBTTagCompound();
		this.writeToNBT(NBT);
		S35PacketUpdateTileEntity pack = new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, NBT);
		return pack;
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity p)  {
		this.readFromNBT(p.field_148860_e);
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public abstract DimensionStructureType getType();

	public final S getGenerator() {
		return (S)this.getType().getGenerator(uid);
	}

	protected void copyFrom(StructureBlockTile<S> g) {
		this.uid = g.uid;
	}

}
