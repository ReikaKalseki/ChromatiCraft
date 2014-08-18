/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity;

import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityCrystalPlant extends TileEntity {

	private final Random random = new Random();

	private int growthTick = 2;

	public boolean renderPod() {
		return growthTick <= 1;
	}

	public boolean emitsLight() {
		return growthTick == 0;
	}

	public void grow() {
		if (growthTick > 0) {
			growthTick--;
			this.updateLight();
			for (int i = 2; i < 6; i++) {
				if (ReikaRandomHelper.doWithChance(25)) {
					ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
					int dx = xCoord+dir.offsetX;
					int dy = yCoord+dir.offsetY;
					int dz = zCoord+dir.offsetZ;
					Block id = worldObj.getBlock(dx, dy, dz);
					int meta = worldObj.getBlockMetadata(dx, dy, dz);
					if (id == ChromaBlocks.PLANT.getBlockInstance() && meta == this.getColor().ordinal()) {
						TileEntityCrystalPlant te = (TileEntityCrystalPlant)worldObj.getTileEntity(dx, dy, dz);
						te.grow();
					}
				}
			}
		}
	}

	public void updateLight() {
		worldObj.func_147479_m(xCoord, yCoord, zCoord);
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public void harvest() {
		growthTick = 2;
		int rand = random.nextInt(20);
		int num = 0;
		if (rand == 0) {
			num = 2;
		}
		else if (rand < 5) {
			num = 1;
		}
		int meta = this.getColor().ordinal();
		for (int i = 0; i < num; i++)
			ReikaItemHelper.dropItem(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5, ChromaItems.SEED.getStackOfMetadata(meta+16));
		if (ChromaOptions.CRYSTALFARM.getState() && ReikaRandomHelper.doWithChance(2))
			ReikaItemHelper.dropItem(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5, ChromaItems.SHARD.getStackOfMetadata(meta));
		this.updateLight();
	}

	public boolean canHarvest() {
		return growthTick == 0;
	}

	public ReikaDyeHelper getColor() {
		return ReikaDyeHelper.getColorFromDamage(worldObj.getBlockMetadata(xCoord, yCoord, zCoord));
	}

	@Override
	public boolean canUpdate()
	{
		return false;
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
	{
		this.readFromNBT(pkt.field_148860_e);
	}

	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeToNBT(nbt);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT)
	{
		super.readFromNBT(NBT);
		growthTick = NBT.getInteger("growth");
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT)
	{
		super.writeToNBT(NBT);

		NBT.setInteger("growth", growthTick);
	}

}
