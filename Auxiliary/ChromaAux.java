/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import java.awt.Color;
import java.util.Collection;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import Reika.ChromatiCraft.ChromaGuiHandler;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Entity.EntityBallLightning;
import Reika.ChromatiCraft.Magic.CrystalTarget;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.ChromatiCraft.World.Dimension.WorldProviderChroma;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.BloodMagicHandler;
import WayofTime.alchemicalWizardry.api.soulNetwork.SoulNetworkHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

public class ChromaAux {

	public static final Color[] sideColors = {Color.CYAN, Color.BLUE, Color.YELLOW, Color.BLACK, new Color(255, 120, 0), Color.MAGENTA};
	public static final String[] sideColorNames = {"CYAN", "BLUE", "YELLOW", "BLACK", "ORANGE", "MAGENTA"};

	public static void interceptChunkPopulation(int cx, int cz, World world, IChunkProvider generator, IChunkProvider loader) {
		if (world.provider.dimensionId == ExtraChromaIDs.DIMID.getValue()) {
			((WorldProviderChroma)world.provider).getChunkGenerator().onPopulationHook(generator, loader, cx, cz);
		}
		else {
			GameRegistry.generateWorld(cx, cz, world, generator, loader);
		}
	}

	public static final boolean hasGui(World world, int x, int y, int z, EntityPlayer ep) {
		ChromaTiles m = ChromaTiles.getTile(world, x, y, z);
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			Object GUI = ChromaGuiHandler.instance.getClientGuiElement(0, ep, world, x, y, z);
			if (GUI != null)
				return true;
		}
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
			Object GUI = ChromaGuiHandler.instance.getServerGuiElement(0, ep, world, x, y, z);
			if (GUI != null)
				return true;
		}
		return false;
	}

	public static int get4SidedMetadataFromPlayerLook(EntityLivingBase ep) {
		int i = MathHelper.floor_double((ep.rotationYaw * 4F) / 360F + 0.5D);
		while (i > 3)
			i -= 4;
		while (i < 0)
			i += 4;
		switch (i) {
			case 0:
				return 2;
			case 1:
				return 1;
			case 2:
				return 3;
			case 3:
				return 0;
		}
		return -1;
	}

	public static int get6SidedMetadataFromPlayerLook(EntityLivingBase ep) {
		if (MathHelper.abs(ep.rotationPitch) < 60) {
			int i = MathHelper.floor_double((ep.rotationYaw * 4F) / 360F + 0.5D);
			while (i > 3)
				i -= 4;
			while (i < 0)
				i += 4;
			switch (i) {
				case 0:
					return 2;
				case 1:
					return 1;
				case 2:
					return 3;
				case 3:
					return 0;
			}
		}
		else { //Looking up/down
			if (ep.rotationPitch > 0)
				return 4; //set to up
			else
				return 5; //set to down
		}
		return -1;
	}

	public static int get2SidedMetadataFromPlayerLook(EntityLivingBase ep) {
		int i = MathHelper.floor_double((ep.rotationYaw * 4F) / 360F + 0.5D);
		while (i > 3)
			i -= 4;
		while (i < 0)
			i += 4;

		switch (i) {
			case 0:
				return 0;
			case 1:
				return 1;
			case 2:
				return 0;
			case 3:
				return 1;
		}
		return -1;
	}

	public static boolean shouldSetFlipped(World world, int x, int y, int z) {
		boolean softBelow = ReikaWorldHelper.softBlocks(world, x, y-1, z);
		boolean softAbove = ReikaWorldHelper.softBlocks(world, x, y+1, z);
		if (!softAbove && softBelow) {
			return true;
		}
		return false;
	}

	public static String getMessage(String tag) {
		return StatCollector.translateToLocal("message."+tag);
	}

	public static void writeMessage(String tag) {
		ReikaChatHelper.writeString(getMessage(tag));
	}

	public static void spawnInteractionBallLightning(World world, int x, int y, int z, CrystalElement e) {
		if (!world.isRemote && ChromaOptions.BALLLIGHTNING.getState()) {
			int dx = ReikaRandomHelper.getRandomPlusMinus(x, 16);
			int dz = ReikaRandomHelper.getRandomPlusMinus(z, 16);
			double dy = world.getTopSolidOrLiquidBlock(dx, dz)+ReikaRandomHelper.getSafeRandomInt(8);//ReikaRandomHelper.getRandomPlusMinus(y+0.5, 16);
			world.spawnEntityInWorld(new EntityBallLightning(world, e, dx+0.5, dy+0.5, dz+0.5).setNoDrops());
		}
	}

	public static boolean requiresSpecialSpawnEnforcement(EntityLiving e) {
		String name = e.getClass().getName().toLowerCase();
		return name.contains("lycanite");
	}

	public static void doPylonAttack(EntityLivingBase e, float amt, boolean taperNew) {
		ChromaSounds.DISCHARGE.playSound(e.worldObj, e.posX, e.posY, e.posZ, 1, 1);

		if (e instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer)e;
			ProgressStage.SHOCK.stepPlayerTo(ep);
			//DO NOT UNCOMMENT, AS ALLOWS DISCOVERY OF ALL COLORS BEFORE PREREQ//ProgressionManager.instance.setPlayerDiscoveredColor(ep, color, true);
			if (ModList.BLOODMAGIC.isLoaded()) {
				int drain = 5000;
				if (BloodMagicHandler.getInstance().isPlayerWearingFullBoundArmor(ep)) {
					amt *= 10; //counter the 90% reduction
					drain = 50000;
				}
				SoulNetworkHandler.syphonFromNetwork(ep.getCommandSenderName(), drain);
			}

			if (taperNew) {
				if (e.ticksExisted < 600) {
					amt = 1; //1/2 heart for first 30s
				}
				else if (e.ticksExisted <= 1000) {
					amt = 1+(e.ticksExisted-600)/100; //increase by 1/2 heart every 5 seconds, up to 2.5 hearts at 50 seconds
				}
			}
		}

		float last = e.getHealth();

		e.attackEntityFrom(ChromatiCraft.pylon, amt);

		if (e.getHealth() > last-amt) {
			if (amt >= last) { //kill
				e.setHealth(0.1F);
				e.attackEntityFrom(ChromatiCraft.pylon, Float.MAX_VALUE);
			}
			else
				e.setHealth(last-amt);
		}
	}

	public static float getIslandBias(float originalBias, float dx, float dz) {
		float dist = MathHelper.sqrt_double(dx*dx+dz*dz);
		return 50+50*MathHelper.sin(dist*0.0625F); //is 100 at spawn
	}

	public static MultiMap<DecimalPosition, CrystalElement> getBeamColorMixes(Collection<CrystalTarget> c) {
		MultiMap<DecimalPosition, CrystalElement> map = new MultiMap(new MultiMap.ListFactory());
		for (CrystalTarget t : c) {
			DecimalPosition loc = new DecimalPosition(t.location).offset(t.offsetX, t.offsetY, t.offsetZ);
			map.addValue(loc, t.color);
		}
		return map;
	}
}
