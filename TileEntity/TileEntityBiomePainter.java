/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.BiomeBlacklist.BiomeConnection;
import Reika.ChromatiCraft.Base.ChromaDimensionBiome;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Render.Particle.EntityCenterBlurFX;
import Reika.ChromatiCraft.World.BiomeGlowingCliffs;
import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Instantiable.CubeRotation;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Interfaces.TileEntity.GuiController;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Java.ReikaObfuscationHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityBiomePainter extends TileEntityChromaticBase implements GuiController {

	private static final Collection<BiomeConnection> blacklist = new ArrayList();
	private static final MultiMap<BiomeGenBase, BiomeGenBase> availableBiomes = new MultiMap();

	public static final int RANGE = 64;

	public final CubeRotation rotation = new CubeRotation().randomize(rand);

	private BiomeGenBase placedBiome;
	public boolean safeMode = false;

	public BiomeGenBase getNaturalBiomeAt(int dx, int dz) {
		return ReikaWorldHelper.getNaturalGennedBiomeAt(worldObj, xCoord+dx-RANGE, zCoord+dz-RANGE);
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.BIOMEPAINTER;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		//ReikaJavaLibrary.pConsole(world.getBiomeGenForCoords(x, z)+"/"+ReikaWorldHelper.getNaturalGennedBiomeAt(world, x, z), Side.SERVER);
		if (world.isRemote) {
			this.doParticles(world, x, y, z);
		}
	}

	@Override
	protected void onFirstTick(World world, int x, int y, int z) {
		placedBiome = world.getBiomeGenForCoords(x, z);
	}

	@SideOnly(Side.CLIENT)
	private void doParticles(World world, int x, int y, int z) {
		double px = x+0.0625+rand.nextDouble()*0.875;
		double py = y+0.0625+rand.nextDouble()*0.875;
		double pz = z+0.0625+rand.nextDouble()*0.875;
		double vx = ReikaRandomHelper.getRandomPlusMinus(0, 0.03125);
		double vy = ReikaRandomHelper.getRandomPlusMinus(0, 0.03125);
		double vz = ReikaRandomHelper.getRandomPlusMinus(0, 0.03125);
		float g = (float)ReikaRandomHelper.getRandomPlusMinus(0, 0.03125);
		EntityFX fx = new EntityCenterBlurFX(world, px, py, pz, vx, vy, vz).setGravity(g).setColor(rand.nextInt(0xffffff));
		Minecraft.getMinecraft().effectRenderer.addEffect(fx);
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	public void changeBiomeAt(int dx, int dz, BiomeGenBase biome) {
		if (this.canChangeBiomeAt(dx, dz, worldObj.getBiomeGenForCoords(dx, dz))) {
			if (!worldObj.isRemote) {
				//ReikaJavaLibrary.pConsole(xCoord+dx);
				ReikaWorldHelper.setBiomeForXZ(worldObj, dx, dz, biome, true);
				//ReikaJavaLibrary.pConsole("Setting biome "+biome+" @ "+dx+", "+dz);
			}
			else {
				int id = biome != null ? biome.biomeID : -1;
				//ReikaJavaLibrary.pConsole("Setting biome "+biome+" with ID "+id+" @ "+dx+", "+dz);
				ReikaPacketHelper.sendPacketToServer(ChromatiCraft.packetChannel, ChromaPackets.BIOMEPAINT.ordinal(), this, dx, dz, id);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound NBT) {
		super.writeToNBT(NBT);
	}

	@Override
	public void readFromNBT(NBTTagCompound NBT) {
		super.readFromNBT(NBT);
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		NBT.setBoolean("safe", safeMode);
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		safeMode = NBT.getBoolean("safe");
	}

	static {
		blacklist.add(new ChromaBiomeBlacklist());
	}

	public boolean canChangeBiomeAt(int dx, int dz, BiomeGenBase from) {
		return !safeMode || from == placedBiome;
	}

	public static void buildBiomeList() {
		availableBiomes.clear();
		for (int i = 0; i < BiomeGenBase.biomeList.length; i++) {
			BiomeGenBase b = BiomeGenBase.biomeList[i];
			if (b != null) {
				for (int k = 0; k < BiomeGenBase.biomeList.length; k++) {
					BiomeGenBase b2 = BiomeGenBase.biomeList[k];
					if (b2 != null) {
						if (isValid(b, b2))
							availableBiomes.addValue(b, b2);
					}
				}
			}
		}
	}

	private static boolean isValid(BiomeGenBase b, BiomeGenBase b2) {
		for (BiomeConnection bc : blacklist) {
			if (!bc.isLegalTransition(b, b2))
				return false;
		}
		return true;
	}

	public static void addBiomeConnection(BiomeConnection bc) {
		blacklist.add(bc);
	}

	public static HashSet<BiomeGenBase> getValidBiomesFor(BiomeGenBase in) {
		return new HashSet(availableBiomes.get(in));
	}

	public static Collection<BiomeGenBase> getValidBiomes() {
		return Collections.unmodifiableCollection(availableBiomes.allValues(false));
	}

	private static class ChromaBiomeBlacklist implements BiomeConnection {

		@Override
		public boolean isLegalTransition(BiomeGenBase in, BiomeGenBase out) {
			return (DragonAPICore.isReikasComputer() && ReikaObfuscationHelper.isDeObfEnvironment()) || (this.isAccessibleBiome(in) && this.isAccessibleBiome(out));
		}

		private boolean isAccessibleBiome(BiomeGenBase in) {
			if (in == ChromatiCraft.rainbowforest)
				;//return false;
			if (in instanceof ChromaDimensionBiome)
				return false;
			if (in instanceof BiomeGlowingCliffs)
				return false;
			return true;
		}



	}

}
