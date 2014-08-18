/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import Reika.ChromatiCraft.ChromaGuiHandler;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Libraries.IO.ReikaChatHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

import java.awt.Color;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class ChromaAux {

	public static final Color[] sideColors = {Color.CYAN, Color.BLUE, Color.YELLOW, Color.BLACK, new Color(255, 120, 0), Color.MAGENTA};
	public static final String[] sideColorNames = {"CYAN", "BLUE", "YELLOW", "BLACK", "ORANGE", "MAGENTA"};

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
}
