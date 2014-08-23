/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft;

import Reika.ChromatiCraft.Base.CrystalBlock;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.TileEntity.TileEntityAutoEnchanter;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalPlant;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalPylon;
import Reika.ChromatiCraft.TileEntity.TileEntitySpawnerReprogrammer;
import Reika.DragonAPI.Auxiliary.PacketTypes;
import Reika.DragonAPI.Interfaces.IPacketHandler;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper.PacketObj;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ChromatiPackets implements IPacketHandler {

	protected ChromaPackets pack;

	public void handleData(PacketObj packet, World world, EntityPlayer ep) {
		DataInputStream inputStream = packet.getDataIn();
		int control = Integer.MIN_VALUE;
		int len;
		int[] data = new int[0];
		int x = 0;
		int y = 0;
		int z = 0;
		String stringdata = null;
		//System.out.print(packet.length);
		try {
			PacketTypes packetType = packet.getType();
			switch(packetType) {
			case SOUND:
				control = inputStream.readInt();
				ChromaSounds s = ChromaSounds.soundList[control];
				double sx = inputStream.readDouble();
				double sy = inputStream.readDouble();
				double sz = inputStream.readDouble();
				float v = inputStream.readFloat();
				float p = inputStream.readFloat();
				ReikaSoundHelper.playSound(s, sx, sy, sz, v, p);
				return;
			case STRING:
				stringdata = packet.readString();
				control = inputStream.readInt();
				pack = ChromaPackets.getPacket(control);
				break;
			case DATA:
				control = inputStream.readInt();
				pack = ChromaPackets.getPacket(control);
				len = pack.numInts;
				if (pack.hasData()) {
					data = new int[len];
					for (int i = 0; i < len; i++)
						data[i] = inputStream.readInt();
				}
				break;
			case UPDATE:
				control = inputStream.readInt();
				pack = ChromaPackets.getPacket(control);
				break;
			case FLOAT:
				break;
			case SYNC:
				String name = packet.readString();
				x = inputStream.readInt();
				y = inputStream.readInt();
				z = inputStream.readInt();
				int value = inputStream.readInt();
				ReikaPacketHelper.updateTileEntityData(world, x, y, z, name, value);
				return;
			case TANK:
				String tank = packet.readString();
				x = inputStream.readInt();
				y = inputStream.readInt();
				z = inputStream.readInt();
				int level = inputStream.readInt();
				ReikaPacketHelper.updateTileEntityTankData(world, x, y, z, tank, level);
				return;
			case RAW:
				control = inputStream.readInt();
				pack = ChromaPackets.getPacket(control);
				len = 1;
				data = new int[len];
				for (int i = 0; i < len; i++)
					data[i] = inputStream.readInt();
				break;
			}
			if (packetType.hasCoordinates()) {
				x = inputStream.readInt();
				y = inputStream.readInt();
				z = inputStream.readInt();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			return;
		}
		TileEntity tile = world.getTileEntity(x, y, z);
		switch (pack) {
		case ENCHANTER:
			Enchantment e = Enchantment.enchantmentsList[data[0]];
			boolean incr = data[1] > 0;
			TileEntityAutoEnchanter ench = (TileEntityAutoEnchanter)tile;
			if (incr) {
				ench.incrementEnchantment(e);
			}
			else {
				ench.decrementEnchantment(e);
			}
			break;
		case SPAWNERPROGRAM:
			TileEntitySpawnerReprogrammer prog = (TileEntitySpawnerReprogrammer)tile;
			prog.setMobType(stringdata);
			break;
		case CRYSTALEFFECT:
			Block b = world.getBlock(x, y, z);
			if (b instanceof CrystalBlock) {
				CrystalBlock cb = (CrystalBlock)b;
				cb.updateEffects(world, x, y, z);
			}
			break;
		case PLANTUPDATE:
			TileEntityCrystalPlant te = (TileEntityCrystalPlant)tile;
			te.updateLight();
			break;
		case ABILITY:
			ArrayList<Integer> li = new ArrayList();
			for (int i = 1; i < data.length; i++) {
				li.add(data[i]);
			}
			Chromabilities.abilities[data[0]].trigger(ep, li);
			break;
		case PYLONATTACK:
			((TileEntityCrystalPylon)tile).particleAttack(data[0], data[1], data[2]);
			break;
		}
	}

}
