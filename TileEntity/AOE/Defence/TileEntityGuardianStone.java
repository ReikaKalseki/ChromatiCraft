/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.AOE.Defence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Auxiliary.GuardianStoneManager;
import Reika.ChromatiCraft.Auxiliary.ProtectionZone;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class TileEntityGuardianStone extends TileEntityChromaticBase {

	public static final int RANGE = calculateRange();

	private final ArrayList<String> extraPlayers = new ArrayList();
	private ProtectionZone zone;



	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < extraPlayers.size(); i++) {
			//NBTTagString sg = new NBTTagString("Player"+String.valueOf(i));
			NBTTagString sg = new NBTTagString(extraPlayers.get(i));
			list.appendTag(sg);
			//ReikaJavaLibrary.pConsole(sg.data);
		}
		//ReikaJavaLibrary.pConsole("WRITE:  "+list);
		NBT.setTag("players", list);

		if (zone != null) {
			NBT.setTag("zone", zone.writeToNBT());
		}
	}

	private static int calculateRange() {
		return Math.min(Math.max(ChromaOptions.GUARDIAN.getValue(), 8), 64);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);
		NBTTagList list = NBT.getTagList("players", NBTTypes.STRING.ID);
		//ReikaJavaLibrary.pConsole("READ:  "+list);
		extraPlayers.clear();
		for (int i = 0; i < list.tagCount(); i++) {
			String sg = list.getStringTagAt(i);
			extraPlayers.add(sg);
		}

		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT && NBT.hasKey("zone")) {
			zone = ProtectionZone.readFromNBT(NBT.getCompoundTag("zone"));
		}
	}

	public void addPlayer(String name) {
		if (!extraPlayers.contains(name))
			extraPlayers.add(name);
	}

	public void addPlayer(EntityPlayer ep) {
		this.addPlayer(ep.getCommandSenderName());
	}

	public void removePlayer(String name) {
		extraPlayers.remove(name);
	}

	public void removePlayer(EntityPlayer ep) {
		this.removePlayer(ep.getCommandSenderName());
	}

	public boolean isPlayerInList(String name) {
		return extraPlayers.contains(name);
	}

	public boolean isPlayerInList(EntityPlayer ep) {
		return this.isPlayerInList(ep.getCommandSenderName());
	}

	public List<String> getExtraPlayers() {
		return Collections.unmodifiableList(extraPlayers);
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (placer != null) {
			if (world.isRemote) {
				if (zone != null) {
					this.doAreaParticles(world, x, y, z);
				}
			}
			else if (this.getTicksExisted() == 0 || this.getZone() == null) {
				zone = GuardianStoneManager.instance.addZone(world, x, y, z, this.getPlacer(), RANGE);
				this.syncAllData(true);
			}
		}
	}

	private void doAreaParticles(World world, int x, int y, int z) {
		double minX = zone.originX-zone.range;
		double minY = ChromaOptions.GUARDCHUNK.getState() ? 0 : zone.originY-zone.range;
		double minZ = zone.originZ-zone.range;
		double maxX = zone.originX+1+zone.range;
		double maxY = ChromaOptions.GUARDCHUNK.getState() ? 256 : zone.originY+1+zone.range;
		double maxZ = zone.originZ+1+zone.range;
		double px = ReikaRandomHelper.getRandomBetween(minX, maxX);
		double py = ReikaRandomHelper.getRandomBetween(minY, maxY);
		double pz = ReikaRandomHelper.getRandomBetween(minZ, maxZ);
		switch(dirs[rand.nextInt(6)]) {
			case DOWN:
				py = minY;
				break;
			case UP:
				py = maxY;
				break;
			case WEST:
				px = minX;
				break;
			case EAST:
				px = maxX;
				break;
			case NORTH:
				pz = minZ;
				break;
			case SOUTH:
				pz = maxZ;
				break;
			default:
				break;
		}
		EntityBlurFX fx = new EntityBlurFX(world, px, py, pz);
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	public ProtectionZone getZone() {
		return zone;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public int getRedstoneOverride() {
		return 0;
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.GUARDIAN;
	}

}
