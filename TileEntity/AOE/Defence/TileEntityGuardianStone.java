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
import java.util.HashSet;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Auxiliary.GuardianStoneManager;
import Reika.ChromatiCraft.Auxiliary.HoldingChecks;
import Reika.ChromatiCraft.Auxiliary.ProtectionZone;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
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
				if (zone != null && HoldingChecks.MANIPULATOR.isClientHolding()) {
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
		double minX0 = zone.originX-zone.range;
		double minY0 = ChromaOptions.GUARDCHUNK.getState() ? 0 : zone.originY-zone.range;
		double minZ0 = zone.originZ-zone.range;
		double maxX0 = zone.originX+1+zone.range;
		double maxY0 = ChromaOptions.GUARDCHUNK.getState() ? 256 : zone.originY+1+zone.range;
		double maxZ0 = zone.originZ+1+zone.range;
		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		double minX = Math.max(ep.posX-60, minX0);
		double minY = Math.max(ep.posY-40, minY0);
		double minZ = Math.max(ep.posZ-60, minZ0);
		double maxX = Math.min(ep.posX+60, maxX0);
		double maxY = Math.min(ep.posY+40, maxY0);
		double maxZ = Math.min(ep.posZ+60, maxZ0);
		int n = (int)Math.ceil((maxX-minX)*(maxY-minY)*(maxZ-minZ)/2048D);
		for (int i = 0; i < n; i++) {
			double px = ReikaRandomHelper.getRandomBetween(minX, maxX);
			double py = ReikaRandomHelper.getRandomBetween(minY, maxY);
			double pz = ReikaRandomHelper.getRandomBetween(minZ, maxZ);
			boolean side = rand.nextInt(8) > 0;
			boolean edge = rand.nextInt(3) == 0;
			boolean line = rand.nextInt(8) == 0;
			HashSet<ForgeDirection> set = new HashSet();
			if (minX == minX0)
				set.add(ForgeDirection.WEST);
			if (maxX == maxX0)
				set.add(ForgeDirection.EAST);
			if (minZ == minZ0)
				set.add(ForgeDirection.NORTH);
			if (maxZ == maxZ0)
				set.add(ForgeDirection.SOUTH);
			if (minY == minY0)
				set.add(ForgeDirection.DOWN);
			if (maxY == maxY0)
				set.add(ForgeDirection.UP);
			if (line) {
				int signX = rand.nextBoolean() ? 1 : -1;
				int signY = rand.nextBoolean() ? 1 : -1;
				int signZ = rand.nextBoolean() ? 1 : -1;
				double f = rand.nextDouble();
				px = zone.originX+0.5+signX*(maxX-zone.originX-0.5)*f;
				py = zone.originY+0.5+signY*(maxY-zone.originY-0.5)*f;
				pz = zone.originZ+0.5+signZ*(maxZ-zone.originZ-0.5)*f;
				px = MathHelper.clamp_double(px, minX, maxX);
				py = MathHelper.clamp_double(py, minY, maxY);
				pz = MathHelper.clamp_double(pz, minZ, maxZ);
			}
			else if (side && !set.isEmpty()) {
				ForgeDirection face = ReikaJavaLibrary.getRandomCollectionEntry(rand, set);
				switch(face) {
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
				if (edge) {
					ForgeDirection face2 = dirs[rand.nextInt(6)];
					while (face2 == face || face2 == face.getOpposite()) {
						face2 = dirs[rand.nextInt(6)];
					}
					switch(face2) {
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
				}
			}
			float s = 1+rand.nextFloat()*1.5F;
			int l = ReikaRandomHelper.getRandomBetween(5, 15);
			if (line)
				l *= 8;
			else if (edge)
				l *= 4;
			EntityBlurFX fx = new EntityBlurFX(world, px, py, pz).setScale(s).setLife(l);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
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
