/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Plants;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class TileEntityCrystalPlant extends TileEntity {

	private static final Random random = new Random();

	private int flags;
	private boolean sterile;

	private int growthTick = 2;
	private long lastShardTick = -1;

	public boolean renderPod() {
		return growthTick <= 1;
	}

	public boolean emitsLight() {
		return growthTick == 0;
	}

	public void grow() {
		if (growthTick > 0) {
			growthTick--;
			if (this.isPure()) {
				for (int i = 2; i < 6; i++) {
					if (ReikaRandomHelper.doWithChance(this.is(Modifier.PRIMAL) ? 80 : this.is(Modifier.BOOSTED) ? 30 : 5)) {
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
		else if (!this.is(Modifier.PRIMAL)) {
			this.tryGrowPrimal();
		}
		this.updateLight();
	}

	private void tryGrowPrimal() {
		if (random.nextInt(60) > 0)
			return;
		for (int i = 2; i < 6; i++) {
			boolean flag = false;
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = xCoord+dir.offsetX;
			int dy = yCoord+dir.offsetY;
			int dz = zCoord+dir.offsetZ;
			Block id = worldObj.getBlock(dx, dy, dz);
			int meta = worldObj.getBlockMetadata(dx, dy, dz);
			if (id == ChromaBlocks.PLANT.getBlockInstance() && meta == this.getColor().ordinal()) {
				TileEntityCrystalPlant te = (TileEntityCrystalPlant)worldObj.getTileEntity(dx, dy, dz);
				if (te.is(Modifier.BOOSTED)) {
					flag = true;
				}
			}
			if (!flag)
				return;
		}
		flags |= Modifier.PRIMAL.flag;
	}

	public void makeRipe() {
		while(!this.canHarvest())
			this.grow();
	}

	public void updateLight() {
		worldObj.func_147479_m(xCoord, yCoord, zCoord);
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	public void harvest(boolean drops) {
		growthTick = 2;
		if (drops) {
			ArrayList<ItemStack> li = this.getDrops();
			ReikaItemHelper.dropItems(worldObj, xCoord+0.5, yCoord+0.5, zCoord+0.5, li);
		}
		this.updateLight();
	}

	public ArrayList<ItemStack> getDrops() {
		ArrayList<ItemStack> li = new ArrayList();
		if (this.isSterile())
			return li;
		if (this.isPure() || random.nextInt(4) == 0) {
			int meta = this.getColor().ordinal();
			if (this.isPure() && !this.is(Modifier.PRIMAL)) {
				int rand = random.nextInt(20);
				int num = 0;
				if (rand == 0) {
					num = 2;
				}
				else if (rand < 5) {
					num = 1;
				}
				int smeta = meta+Modifier.IMPURE.flag;
				if (this.isPure() && random.nextInt(this.is(Modifier.BOOSTED) ? 4 : 400) == 0)
					smeta = meta+Modifier.BOOSTED.flag;
				for (int i = 0; i < num; i++) {
					li.add(ChromaItems.SEED.getStackOfMetadata(smeta));
				}
				if (random.nextInt(10) == 0) {
					ItemStack deco = ChromaItems.SEED.getStackOfMetadata(smeta);
					deco.stackTagCompound = new NBTTagCompound();
					deco.stackTagCompound.setBoolean("sterile", true);
					li.add(deco);
				}
			}
			long time = worldObj.getTotalWorldTime();
			if (ChromaOptions.CRYSTALFARM.getState()) {
				if (time-lastShardTick >= (this.is(Modifier.PRIMAL) ? 60 : this.is(Modifier.BOOSTED) ? 150 : 600)) {
					if (ReikaRandomHelper.doWithChance(this.is(Modifier.PRIMAL) ? 10 : this.is(Modifier.BOOSTED) ? 5 : 2)) {
						li.add(ChromaItems.SHARD.getStackOfMetadata(meta));
						lastShardTick = time;
					}
				}
			}
		}
		return li;
	}

	public boolean canHarvest() {
		return growthTick == 0;
	}

	public CrystalElement getColor() {
		return CrystalElement.elements[worldObj.getBlockMetadata(xCoord, yCoord, zCoord)];
	}

	@Override
	public boolean canUpdate() {
		return false;
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		this.readFromNBT(pkt.field_148860_e);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeToNBT(nbt);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, nbt);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);
		growthTick = NBT.getInteger("growth");
		lastShardTick = NBT.getLong("shard");
		flags = NBT.getInteger("flags");
		sterile = NBT.getBoolean("sterile");
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);

		NBT.setInteger("growth", growthTick);
		NBT.setLong("shard", lastShardTick);
		NBT.setInteger("flags", flags);
		NBT.setBoolean("sterile", sterile);
	}

	public int getGrowthState() {
		return growthTick;
	}

	public void setStates(ItemStack item) {
		int meta = item.getItemDamage();
		for (int i = 0; i < Modifier.list.length; i++) {
			Modifier m = Modifier.list[i];
			if (m.present(meta)) {
				flags |= m.flag;
			}
		}
		sterile = item.stackTagCompound != null && item.stackTagCompound.getBoolean("sterile");
	}

	public boolean is(Modifier m) {
		return m.present(flags);
	}

	public boolean isPure() {
		return !this.is(Modifier.IMPURE);
	}

	public boolean isSterile() {
		return sterile;
	}

	public static enum Modifier {
		IMPURE("Impure"),
		BOOSTED("Enriched"),
		PRIMAL("Primal");

		private final int flag;
		public final String displayName;

		public static final Modifier[] list = values();

		private Modifier(String s) {
			flag = 1 << (this.ordinal()+4);
			displayName = s;
		}

		public boolean present(int flags) {
			return (flags & flag) != 0;
		}

		public static Modifier getFromFlag(int flags) {
			for (int i = 0; i < list.length; i++) {
				if (list[i].present(flags))
					return list[i];
			}
			return null;
		}

		public boolean showsInCreative() {
			return this != PRIMAL;
		}
	}

}
