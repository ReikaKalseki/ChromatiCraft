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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.TileEntity.TileEntityAutoEnchanter;
import Reika.ChromatiCraft.TileEntity.TileEntitySpawnerReprogrammer;
import Reika.DragonAPI.Auxiliary.PacketTypes;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;

public abstract class ChromatiPackets implements IPacketHandler {

	protected PacketTypes packetType;
	protected ChromaPackets pack;

	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
		this.process(packet, (EntityPlayer)player);
	}

	public abstract void process(Packet250CustomPayload packet, EntityPlayer ep);

	public void handleData(Packet250CustomPayload packet, World world) {
		DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
		int control = Integer.MIN_VALUE;
		int len;
		int[] data = new int[0];
		int x = 0;
		int y = 0;
		int z = 0;
		String stringdata = null;
		//System.out.print(packet.length);
		try {
			packetType = PacketTypes.getPacketType(inputStream.readInt());
			switch(packetType) {
			case SOUND:
				ChromaSounds.playSoundPacket(inputStream);
				return;
			case STRING:
				control = inputStream.readInt();
				pack = ChromaPackets.getPacket(control);
				stringdata = Packet.readString(inputStream, Short.MAX_VALUE);
				break;
			case DATA:
				control = inputStream.readInt();
				pack = ChromaPackets.getPacket(control);
				len = pack.numInts;
				data = new int[len];
				for (int i = 0; i < len; i++)
					data[i] = inputStream.readInt();
				break;
			case UPDATE:
				control = inputStream.readInt();
				pack = ChromaPackets.getPacket(control);
				break;
			case FLOAT:
				break;
			case SYNC:
				x = inputStream.readInt();
				y = inputStream.readInt();
				z = inputStream.readInt();
				String name = Packet.readString(inputStream, Short.MAX_VALUE);
				int value = inputStream.readInt();
				ReikaPacketHelper.updateTileEntityData(world, x, y, z, name, value);
				return;
			case TANK:
				x = inputStream.readInt();
				y = inputStream.readInt();
				z = inputStream.readInt();
				String tank = Packet.readString(inputStream, Short.MAX_VALUE);
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
			if (packetType != PacketTypes.RAW) {
				x = inputStream.readInt();
				y = inputStream.readInt();
				z = inputStream.readInt();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			return;
		}
		TileEntity tile = world.getBlockTileEntity(x, y, z);
		switch (pack) {
		case REACH:
			if (world.isRemote) {
				;//Minecraft.getMinecraft().thePlayer.setReachDistance(data[0]); //set reach with ASM'd method
			}
			break;
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
			prog.setMobID(data[0]);
			break;
		}
	}

}
