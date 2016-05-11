/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface;

import java.lang.reflect.Field;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.DragonAPI.ModList;
import cpw.mods.fml.common.event.FMLInterModComms;

public class TreeCapitatorHandler {

	public static void register() {
		try {
			Class c = Class.forName("bspkrs.treecapitator.Strings");
			String[] fields = {"OAK", "SPRUCE", "BIRCH" , "JUNGLE"};
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("modID", "ChromatiCraft");
			NBTTagList treeList = new NBTTagList();
			for (int i = 0; i < fields.length; i++) {
				NBTTagCompound tree = new NBTTagCompound();
				Field f = c.getField(fields[i]);
				String sg = (String)f.get(null);
				tree.setString("treeName", sg);
				//tree.setString("logs", String.format("%d", Blocks.log.blockID));
				tree.setString("leaves", String.format("%d; %d", ChromaBlocks.DYELEAF.getBlockInstance(), ChromaBlocks.DECAY.getBlockInstance()));
				tree.setInteger("maxHorLeafBreakDist", 5);
				tree.setBoolean("requireLeafDecayCheck", false);
				treeList.appendTag(tree);
			}
			nbt.setTag("trees", treeList);
			FMLInterModComms.sendMessage("TreeCapitator", "ThirdPartyModConfig", nbt);
			ChromatiCraft.logger.log("Adding "+ModList.TREECAPITATOR.getDisplayName()+" support");
		}
		catch (Exception e) {
			ChromatiCraft.logger.logError("Could not interface with "+ModList.TREECAPITATOR.getDisplayName()+"!");
			e.printStackTrace();
		}
	}

}
