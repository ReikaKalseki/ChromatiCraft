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

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.FilledBlockArray;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ChromaStructures {

	public static enum Structures {
		PYLON(),
		CASTING1(),
		CASTING2(),
		CASTING3(),
		RITUAL(),
		INFUSION(),
		TREE(),
		REPEATER(),
		COMPOUND(),
		CAVERN(),
		BURROW(),
		OCEAN();

		@SideOnly(Side.CLIENT)
		public FilledBlockArray getStructureForDisplay() {
			World w = Minecraft.getMinecraft().theWorld;
			switch(this) {
			case PYLON:
				return getPylonStructure(w, 0, 0, 0, CrystalElement.elements[(int)(System.currentTimeMillis()/4000)%16]);
			case CASTING1:
				return getCastingLevelOne(w, 0, 0, 0);
			case CASTING2:
				return getCastingLevelTwo(w, 0, 0, 0);
			case CASTING3:
				return getCastingLevelThree(w, 0, 0, 0);
			case RITUAL:
				return getRitualStructure(w, 0, 0, 0);
			case INFUSION:
				return getInfusionStructure(w, 0, 0, 0);
			case TREE:
				return getTreeStructure(w, 0, 0, 0);
			case REPEATER:
				return getRepeaterStructure(w, 0, 0, 0, CrystalElement.elements[(int)(System.currentTimeMillis()/4000)%16]);
			case COMPOUND:
				return getCompoundRepeaterStructure(w, 0, 0, 0);
			case CAVERN:
				return getCavernStructure(w, 0, 0, 0);
			case BURROW:
				return getBurrowStructure(w, 0, 0, 0);
			case OCEAN:
				return getOceanStructure(w, 0, 0, 0);
			}
			return null;
		}

		public String getDisplayName() {
			return StatCollector.translateToLocal("chromastruct."+this.name().toLowerCase());
		}

		public boolean isNatural() {
			switch(this) {
			case PYLON:
			case CAVERN:
			case BURROW:
			case OCEAN:
				return true;
			default:
				return false;
			}
		}
	}

	public static FilledBlockArray getCavernStructure(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);

		x -= 7;
		y -= 2; //offset compensation
		z -= 5;

		array.setBlock(x+6, y+1, z+9, ChromaBlocks.RUNE.getBlockInstance(), CrystalElement.RED.ordinal());
		array.setBlock(x+6, y+2, z+9, ChromaBlocks.CRYSTAL.getBlockInstance(), CrystalElement.RED.ordinal());

		array.setBlock(x+12, y+1, z+7, ChromaBlocks.RUNE.getBlockInstance(), CrystalElement.YELLOW.ordinal());
		array.setBlock(x+12, y+2, z+7, ChromaBlocks.CRYSTAL.getBlockInstance(), CrystalElement.YELLOW.ordinal());

		array.setBlock(x+6, y+1, z+1, ChromaBlocks.RUNE.getBlockInstance(), CrystalElement.BLACK.ordinal());
		array.setBlock(x+6, y+2, z+1, ChromaBlocks.CRYSTAL.getBlockInstance(), CrystalElement.BLACK.ordinal());

		array.setBlock(x+8, y+1, z+1, ChromaBlocks.RUNE.getBlockInstance(), CrystalElement.WHITE.ordinal());
		array.setBlock(x+8, y+2, z+1, ChromaBlocks.CRYSTAL.getBlockInstance(), CrystalElement.WHITE.ordinal());

		array.setBlock(x+8, y+1, z+9, ChromaBlocks.RUNE.getBlockInstance(), CrystalElement.BLUE.ordinal());
		array.setBlock(x+8, y+2, z+9, ChromaBlocks.CRYSTAL.getBlockInstance(), CrystalElement.BLUE.ordinal());

		array.setBlock(x+12, y+1, z+3, ChromaBlocks.RUNE.getBlockInstance(), CrystalElement.BROWN.ordinal());
		array.setBlock(x+12, y+2, z+3, ChromaBlocks.CRYSTAL.getBlockInstance(), CrystalElement.BROWN.ordinal());

		array.setBlock(x+1, y+1, z+5, ChromaBlocks.RUNE.getBlockInstance(), CrystalElement.GREEN.ordinal());
		array.setBlock(x+1, y+2, z+5, ChromaBlocks.CRYSTAL.getBlockInstance(), CrystalElement.GREEN.ordinal());

		//Air
		array.setBlock(x, y, z, Blocks.air);
		array.setBlock(x+2, y+1, z+5, Blocks.air);
		array.setBlock(x+2, y+2, z+5, Blocks.air);
		array.setBlock(x+2, y+3, z+5, Blocks.air);
		array.setBlock(x+3, y+1, z+4, Blocks.air);
		array.setBlock(x+3, y+1, z+5, Blocks.air);
		array.setBlock(x+3, y+1, z+6, Blocks.air);
		array.setBlock(x+3, y+2, z+4, Blocks.air);
		array.setBlock(x+3, y+2, z+5, Blocks.air);
		array.setBlock(x+3, y+2, z+6, Blocks.air);
		array.setBlock(x+3, y+3, z+4, Blocks.air);
		array.setBlock(x+3, y+3, z+5, Blocks.air);
		array.setBlock(x+3, y+3, z+6, Blocks.air);
		array.setBlock(x+4, y+1, z+3, Blocks.air);
		array.setBlock(x+4, y+1, z+4, Blocks.air);
		array.setBlock(x+4, y+1, z+5, Blocks.air);
		array.setBlock(x+4, y+1, z+6, Blocks.air);
		array.setBlock(x+4, y+1, z+7, Blocks.air);
		array.setBlock(x+4, y+2, z+3, Blocks.air);
		array.setBlock(x+4, y+2, z+4, Blocks.air);
		array.setBlock(x+4, y+2, z+5, Blocks.air);
		array.setBlock(x+4, y+2, z+6, Blocks.air);
		array.setBlock(x+4, y+2, z+7, Blocks.air);
		array.setBlock(x+4, y+3, z+3, Blocks.air);
		array.setBlock(x+4, y+3, z+4, Blocks.air);
		array.setBlock(x+4, y+3, z+5, Blocks.air);
		array.setBlock(x+4, y+3, z+6, Blocks.air);
		array.setBlock(x+4, y+3, z+7, Blocks.air);
		array.setBlock(x+4, y+4, z+5, Blocks.air);
		array.setBlock(x+5, y+1, z+3, Blocks.air);
		array.setBlock(x+5, y+1, z+4, Blocks.air);
		array.setBlock(x+5, y+1, z+5, Blocks.air);
		array.setBlock(x+5, y+1, z+6, Blocks.air);
		array.setBlock(x+5, y+1, z+7, Blocks.air);
		array.setBlock(x+5, y+2, z+3, Blocks.air);
		array.setBlock(x+5, y+2, z+4, Blocks.air);
		array.setBlock(x+5, y+2, z+5, Blocks.air);
		array.setBlock(x+5, y+2, z+6, Blocks.air);
		array.setBlock(x+5, y+2, z+7, Blocks.air);
		array.setBlock(x+5, y+3, z+3, Blocks.air);
		array.setBlock(x+5, y+3, z+4, Blocks.air);
		array.setBlock(x+5, y+3, z+5, Blocks.air);
		array.setBlock(x+5, y+3, z+6, Blocks.air);
		array.setBlock(x+5, y+3, z+7, Blocks.air);
		array.setBlock(x+5, y+4, z+4, Blocks.air);
		array.setBlock(x+5, y+4, z+5, Blocks.air);
		array.setBlock(x+5, y+4, z+6, Blocks.air);
		array.setBlock(x+6, y+1, z+2, Blocks.air);
		array.setBlock(x+6, y+1, z+3, Blocks.air);
		array.setBlock(x+6, y+1, z+4, Blocks.air);
		array.setBlock(x+6, y+1, z+5, Blocks.air);
		array.setBlock(x+6, y+1, z+6, Blocks.air);
		array.setBlock(x+6, y+1, z+7, Blocks.air);
		array.setBlock(x+6, y+1, z+8, Blocks.air);
		array.setBlock(x+6, y+2, z+2, Blocks.air);
		array.setBlock(x+6, y+2, z+3, Blocks.air);
		array.setBlock(x+6, y+2, z+4, Blocks.air);
		array.setBlock(x+6, y+2, z+5, Blocks.air);
		array.setBlock(x+6, y+2, z+6, Blocks.air);
		array.setBlock(x+6, y+2, z+7, Blocks.air);
		array.setBlock(x+6, y+2, z+8, Blocks.air);
		array.setBlock(x+6, y+3, z+2, Blocks.air);
		array.setBlock(x+6, y+3, z+3, Blocks.air);
		array.setBlock(x+6, y+3, z+4, Blocks.air);
		array.setBlock(x+6, y+3, z+5, Blocks.air);
		array.setBlock(x+6, y+3, z+6, Blocks.air);
		array.setBlock(x+6, y+3, z+7, Blocks.air);
		array.setBlock(x+6, y+3, z+8, Blocks.air);
		array.setBlock(x+6, y+4, z+4, Blocks.air);
		array.setBlock(x+6, y+4, z+5, Blocks.air);
		array.setBlock(x+6, y+4, z+6, Blocks.air);
		array.setBlock(x+7, y+1, z+2, Blocks.air);
		array.setBlock(x+7, y+1, z+3, Blocks.air);
		array.setBlock(x+7, y+1, z+4, Blocks.air);
		array.setBlock(x+7, y+1, z+5, Blocks.air);
		array.setBlock(x+7, y+1, z+6, Blocks.air);
		array.setBlock(x+7, y+1, z+7, Blocks.air);
		array.setBlock(x+7, y+1, z+8, Blocks.air);
		array.setBlock(x+7, y+2, z+2, Blocks.air);
		array.setBlock(x+7, y+2, z+3, Blocks.air);
		array.setBlock(x+7, y+2, z+4, Blocks.air);
		array.setBlock(x+7, y+2, z+6, Blocks.air);
		array.setBlock(x+7, y+2, z+7, Blocks.air);
		array.setBlock(x+7, y+2, z+8, Blocks.air);
		array.setBlock(x+7, y+3, z+2, Blocks.air);
		array.setBlock(x+7, y+3, z+3, Blocks.air);
		array.setBlock(x+7, y+3, z+4, Blocks.air);
		array.setBlock(x+7, y+3, z+5, Blocks.air);
		array.setBlock(x+7, y+3, z+6, Blocks.air);
		array.setBlock(x+7, y+3, z+7, Blocks.air);
		array.setBlock(x+7, y+3, z+8, Blocks.air);
		array.setBlock(x+7, y+4, z+4, Blocks.air);
		array.setBlock(x+7, y+4, z+5, Blocks.air);
		array.setBlock(x+7, y+4, z+6, Blocks.air);
		array.setBlock(x+8, y+1, z+2, Blocks.air);
		array.setBlock(x+8, y+1, z+3, Blocks.air);
		array.setBlock(x+8, y+1, z+4, Blocks.air);
		array.setBlock(x+8, y+1, z+5, Blocks.air);
		array.setBlock(x+8, y+1, z+6, Blocks.air);
		array.setBlock(x+8, y+1, z+7, Blocks.air);
		array.setBlock(x+8, y+1, z+8, Blocks.air);
		array.setBlock(x+8, y+2, z+2, Blocks.air);
		array.setBlock(x+8, y+2, z+3, Blocks.air);
		array.setBlock(x+8, y+2, z+4, Blocks.air);
		array.setBlock(x+8, y+2, z+5, Blocks.air);
		array.setBlock(x+8, y+2, z+6, Blocks.air);
		array.setBlock(x+8, y+2, z+7, Blocks.air);
		array.setBlock(x+8, y+2, z+8, Blocks.air);
		array.setBlock(x+8, y+3, z+2, Blocks.air);
		array.setBlock(x+8, y+3, z+3, Blocks.air);
		array.setBlock(x+8, y+3, z+4, Blocks.air);
		array.setBlock(x+8, y+3, z+5, Blocks.air);
		array.setBlock(x+8, y+3, z+6, Blocks.air);
		array.setBlock(x+8, y+3, z+7, Blocks.air);
		array.setBlock(x+8, y+3, z+8, Blocks.air);
		array.setBlock(x+8, y+4, z+4, Blocks.air);
		array.setBlock(x+8, y+4, z+5, Blocks.air);
		array.setBlock(x+8, y+4, z+6, Blocks.air);
		array.setBlock(x+9, y+1, z+3, Blocks.air);
		array.setBlock(x+9, y+1, z+4, Blocks.air);
		array.setBlock(x+9, y+1, z+5, Blocks.air);
		array.setBlock(x+9, y+1, z+6, Blocks.air);
		array.setBlock(x+9, y+1, z+7, Blocks.air);
		array.setBlock(x+9, y+2, z+3, Blocks.air);
		array.setBlock(x+9, y+2, z+4, Blocks.air);
		array.setBlock(x+9, y+2, z+5, Blocks.air);
		array.setBlock(x+9, y+2, z+6, Blocks.air);
		array.setBlock(x+9, y+2, z+7, Blocks.air);
		array.setBlock(x+9, y+3, z+3, Blocks.air);
		array.setBlock(x+9, y+3, z+4, Blocks.air);
		array.setBlock(x+9, y+3, z+5, Blocks.air);
		array.setBlock(x+9, y+3, z+6, Blocks.air);
		array.setBlock(x+9, y+3, z+7, Blocks.air);
		array.setBlock(x+9, y+4, z+4, Blocks.air);
		array.setBlock(x+9, y+4, z+5, Blocks.air);
		array.setBlock(x+9, y+4, z+6, Blocks.air);
		array.setBlock(x+10, y+1, z+3, Blocks.air);
		array.setBlock(x+10, y+1, z+4, Blocks.air);
		array.setBlock(x+10, y+1, z+5, Blocks.air);
		array.setBlock(x+10, y+1, z+6, Blocks.air);
		array.setBlock(x+10, y+1, z+7, Blocks.air);
		array.setBlock(x+10, y+2, z+3, Blocks.air);
		array.setBlock(x+10, y+2, z+4, Blocks.air);
		array.setBlock(x+10, y+2, z+5, Blocks.air);
		array.setBlock(x+10, y+2, z+6, Blocks.air);
		array.setBlock(x+10, y+2, z+7, Blocks.air);
		array.setBlock(x+10, y+3, z+3, Blocks.air);
		array.setBlock(x+10, y+3, z+4, Blocks.air);
		array.setBlock(x+10, y+3, z+5, Blocks.air);
		array.setBlock(x+10, y+3, z+6, Blocks.air);
		array.setBlock(x+10, y+3, z+7, Blocks.air);
		array.setBlock(x+10, y+4, z+5, Blocks.air);
		array.setBlock(x+11, y+1, z+4, Blocks.air);
		array.setBlock(x+11, y+1, z+5, Blocks.air);
		array.setBlock(x+11, y+1, z+6, Blocks.air);
		array.setBlock(x+11, y+2, z+4, Blocks.air);
		array.setBlock(x+11, y+2, z+5, Blocks.air);
		array.setBlock(x+11, y+2, z+6, Blocks.air);
		array.setBlock(x+11, y+3, z+4, Blocks.air);
		array.setBlock(x+11, y+3, z+5, Blocks.air);
		array.setBlock(x+11, y+3, z+6, Blocks.air);
		array.setBlock(x+12, y+1, z+4, Blocks.air);
		array.setBlock(x+12, y+1, z+5, Blocks.air);
		array.setBlock(x+12, y+1, z+6, Blocks.air);
		array.setBlock(x+12, y+2, z+4, Blocks.air);
		array.setBlock(x+12, y+2, z+5, Blocks.air);
		array.setBlock(x+12, y+2, z+6, Blocks.air);
		array.setBlock(x+12, y+3, z+4, Blocks.air);
		array.setBlock(x+12, y+3, z+5, Blocks.air);
		array.setBlock(x+12, y+3, z+6, Blocks.air);
		array.setBlock(x+13, y+1, z+5, Blocks.air);
		array.setBlock(x+13, y+2, z+5, Blocks.air);

		//Shielding
		Block shield = ChromaBlocks.STRUCTSHIELD.getBlockInstance();
		array.setBlock(x+0, y+1, z+5, shield, 8);
		array.setBlock(x+0, y+2, z+5, shield, 8);
		array.setBlock(x+1, y+0, z+5, shield, 8);
		array.setBlock(x+1, y+1, z+4, shield, 8);
		array.setBlock(x+1, y+1, z+6, shield, 8);
		array.setBlock(x+1, y+2, z+4, shield, 8);
		array.setBlock(x+1, y+2, z+6, shield, 8);
		array.setBlock(x+1, y+3, z+5, shield, 8);
		array.setBlock(x+2, y+0, z+5, shield, 8);
		array.setBlock(x+2, y+1, z+4, shield, 8);
		array.setBlock(x+2, y+1, z+6, shield, 8);
		array.setBlock(x+2, y+2, z+4, shield, 8);
		array.setBlock(x+2, y+2, z+6, shield, 8);
		array.setBlock(x+2, y+3, z+4, shield, 8);
		array.setBlock(x+2, y+3, z+6, shield, 8);
		array.setBlock(x+2, y+4, z+5, shield, 8);
		array.setBlock(x+3, y+0, z+4, shield, 8);
		array.setBlock(x+3, y+0, z+5, shield, 8);
		array.setBlock(x+3, y+0, z+6, shield, 8);
		array.setBlock(x+3, y+1, z+3, shield, 8);
		array.setBlock(x+3, y+1, z+7, shield, 8);
		array.setBlock(x+3, y+2, z+3, shield, 8);
		array.setBlock(x+3, y+2, z+7, shield, 8);
		array.setBlock(x+3, y+3, z+3, shield, 8);
		array.setBlock(x+3, y+3, z+7, shield, 8);
		array.setBlock(x+3, y+4, z+4, shield, 8);
		array.setBlock(x+3, y+4, z+5, shield, 8);
		array.setBlock(x+3, y+4, z+6, shield, 8);
		array.setBlock(x+4, y+0, z+3, shield, 8);
		array.setBlock(x+4, y+0, z+4, shield, 8);
		array.setBlock(x+4, y+0, z+5, shield, 8);
		array.setBlock(x+4, y+0, z+6, shield, 8);
		array.setBlock(x+4, y+0, z+7, shield, 8);
		array.setBlock(x+4, y+1, z+2, shield, 8);
		array.setBlock(x+4, y+1, z+8, shield, 8);
		array.setBlock(x+4, y+2, z+2, shield, 8);
		array.setBlock(x+4, y+2, z+8, shield, 8);
		array.setBlock(x+4, y+3, z+2, shield, 8);
		array.setBlock(x+4, y+3, z+8, shield, 8);
		array.setBlock(x+4, y+4, z+3, shield, 8);
		array.setBlock(x+4, y+4, z+4, shield, 8);
		array.setBlock(x+4, y+4, z+6, shield, 8);
		array.setBlock(x+4, y+4, z+7, shield, 8);
		array.setBlock(x+4, y+5, z+5, shield, 8);
		array.setBlock(x+5, y+0, z+3, shield, 8);
		array.setBlock(x+5, y+0, z+4, shield, 8);
		array.setBlock(x+5, y+0, z+5, shield, 8);
		array.setBlock(x+5, y+0, z+6, shield, 8);
		array.setBlock(x+5, y+0, z+7, shield, 8);
		array.setBlock(x+5, y+1, z+1, shield, 8);
		array.setBlock(x+5, y+1, z+2, shield, 8);
		array.setBlock(x+5, y+1, z+8, shield, 8);
		array.setBlock(x+5, y+1, z+9, shield, 8);
		array.setBlock(x+5, y+2, z+1, shield, 8);
		array.setBlock(x+5, y+2, z+2, shield, 8);
		array.setBlock(x+5, y+2, z+8, shield, 8);
		array.setBlock(x+5, y+2, z+9, shield, 8);
		array.setBlock(x+5, y+3, z+2, shield, 8);
		array.setBlock(x+5, y+3, z+8, shield, 8);
		array.setBlock(x+5, y+4, z+3, shield, 8);
		array.setBlock(x+5, y+4, z+7, shield, 8);
		array.setBlock(x+5, y+5, z+4, shield, 8);
		array.setBlock(x+5, y+5, z+5, shield, 8);
		array.setBlock(x+5, y+5, z+6, shield, 8);
		array.setBlock(x+6, y+0, z+1, shield, 8);
		array.setBlock(x+6, y+0, z+2, shield, 8);
		array.setBlock(x+6, y+0, z+3, shield, 8);
		array.setBlock(x+6, y+0, z+4, shield, 8);
		array.setBlock(x+6, y+0, z+5, shield, 8);
		array.setBlock(x+6, y+0, z+6, shield, 8);
		array.setBlock(x+6, y+0, z+7, shield, 8);
		array.setBlock(x+6, y+0, z+8, shield, 8);
		array.setBlock(x+6, y+0, z+9, shield, 8);
		array.setBlock(x+6, y+1, z+0, shield, 8);
		array.setBlock(x+6, y+1, z+10, shield, 8);
		array.setBlock(x+6, y+2, z+0, shield, 8);
		array.setBlock(x+6, y+2, z+10, shield, 8);
		array.setBlock(x+6, y+3, z+1, shield, 8);
		array.setBlock(x+6, y+3, z+9, shield, 8);
		array.setBlock(x+6, y+4, z+2, shield, 8);
		array.setBlock(x+6, y+4, z+3, shield, 8);
		array.setBlock(x+6, y+4, z+7, shield, 8);
		array.setBlock(x+6, y+4, z+8, shield, 8);
		array.setBlock(x+6, y+5, z+4, shield, 8);
		array.setBlock(x+6, y+5, z+5, shield, 8);
		array.setBlock(x+6, y+5, z+6, shield, 8);
		array.setBlock(x+7, y+0, z+2, shield, 8);
		array.setBlock(x+7, y+0, z+3, shield, 8);
		array.setBlock(x+7, y+0, z+4, shield, 8);
		array.setBlock(x+7, y+0, z+5, shield, 8);
		array.setBlock(x+7, y+0, z+6, shield, 8);
		array.setBlock(x+7, y+0, z+7, shield, 8);
		array.setBlock(x+7, y+0, z+8, shield, 8);
		array.setBlock(x+7, y+1, z+1, shield, 8);
		array.setBlock(x+7, y+1, z+9, shield, 8);
		array.setBlock(x+7, y+2, z+1, shield, 8);
		array.setBlock(x+7, y+2, z+9, shield, 8);
		array.setBlock(x+7, y+3, z+1, shield, 8);
		array.setBlock(x+7, y+3, z+9, shield, 8);
		array.setBlock(x+7, y+4, z+2, shield, 8);
		array.setBlock(x+7, y+4, z+3, shield, 8);
		array.setBlock(x+7, y+4, z+7, shield, 8);
		array.setBlock(x+7, y+4, z+8, shield, 8);
		array.setBlock(x+7, y+5, z+4, shield, 8);
		array.setBlock(x+7, y+5, z+5, shield, 8);
		array.setBlock(x+7, y+5, z+6, shield, 8);
		array.setBlock(x+8, y+0, z+1, shield, 8);
		array.setBlock(x+8, y+0, z+2, shield, 8);
		array.setBlock(x+8, y+0, z+3, shield, 8);
		array.setBlock(x+8, y+0, z+4, shield, 8);
		array.setBlock(x+8, y+0, z+5, shield, 8);
		array.setBlock(x+8, y+0, z+6, shield, 8);
		array.setBlock(x+8, y+0, z+7, shield, 8);
		array.setBlock(x+8, y+0, z+8, shield, 8);
		array.setBlock(x+8, y+0, z+9, shield, 8);
		array.setBlock(x+8, y+1, z+0, shield, 8);
		array.setBlock(x+8, y+1, z+10, shield, 8);
		array.setBlock(x+8, y+2, z+0, shield, 8);
		array.setBlock(x+8, y+2, z+10, shield, 8);
		array.setBlock(x+8, y+3, z+1, shield, 8);
		array.setBlock(x+8, y+3, z+9, shield, 8);
		array.setBlock(x+8, y+4, z+2, shield, 8);
		array.setBlock(x+8, y+4, z+3, shield, 8);
		array.setBlock(x+8, y+4, z+7, shield, 8);
		array.setBlock(x+8, y+4, z+8, shield, 8);
		array.setBlock(x+8, y+5, z+4, shield, 8);
		array.setBlock(x+8, y+5, z+5, shield, 8);
		array.setBlock(x+8, y+5, z+6, shield, 8);
		array.setBlock(x+9, y+0, z+3, shield, 8);
		array.setBlock(x+9, y+0, z+4, shield, 8);
		array.setBlock(x+9, y+0, z+5, shield, 8);
		array.setBlock(x+9, y+0, z+6, shield, 8);
		array.setBlock(x+9, y+0, z+7, shield, 8);
		array.setBlock(x+9, y+1, z+1, shield, 8);
		array.setBlock(x+9, y+1, z+2, shield, 8);
		array.setBlock(x+9, y+1, z+8, shield, 8);
		array.setBlock(x+9, y+1, z+9, shield, 8);
		array.setBlock(x+9, y+2, z+1, shield, 8);
		array.setBlock(x+9, y+2, z+2, shield, 8);
		array.setBlock(x+9, y+2, z+8, shield, 8);
		array.setBlock(x+9, y+2, z+9, shield, 8);
		array.setBlock(x+9, y+3, z+2, shield, 8);
		array.setBlock(x+9, y+3, z+8, shield, 8);
		array.setBlock(x+9, y+4, z+3, shield, 8);
		array.setBlock(x+9, y+4, z+7, shield, 8);
		array.setBlock(x+9, y+5, z+4, shield, 8);
		array.setBlock(x+9, y+5, z+5, shield, 8);
		array.setBlock(x+9, y+5, z+6, shield, 8);
		array.setBlock(x+10, y+0, z+3, shield, 8);
		array.setBlock(x+10, y+0, z+4, shield, 8);
		array.setBlock(x+10, y+0, z+5, shield, 8);
		array.setBlock(x+10, y+0, z+6, shield, 8);
		array.setBlock(x+10, y+0, z+7, shield, 8);
		array.setBlock(x+10, y+1, z+2, shield, 8);
		array.setBlock(x+10, y+1, z+8, shield, 8);
		array.setBlock(x+10, y+2, z+2, shield, 8);
		array.setBlock(x+10, y+2, z+8, shield, 8);
		array.setBlock(x+10, y+3, z+2, shield, 8);
		array.setBlock(x+10, y+3, z+8, shield, 8);
		array.setBlock(x+10, y+4, z+3, shield, 8);
		array.setBlock(x+10, y+4, z+4, shield, 8);
		array.setBlock(x+10, y+4, z+6, shield, 8);
		array.setBlock(x+10, y+4, z+7, shield, 8);
		array.setBlock(x+10, y+5, z+5, shield, 8);
		array.setBlock(x+11, y+0, z+4, shield, 8);
		array.setBlock(x+11, y+0, z+5, shield, 8);
		array.setBlock(x+11, y+0, z+6, shield, 8);
		array.setBlock(x+11, y+1, z+3, shield, 8);
		array.setBlock(x+11, y+1, z+7, shield, 8);
		array.setBlock(x+11, y+2, z+3, shield, 8);
		array.setBlock(x+11, y+2, z+7, shield, 8);
		array.setBlock(x+11, y+3, z+3, shield, 8);
		array.setBlock(x+11, y+3, z+7, shield, 8);
		array.setBlock(x+11, y+4, z+4, shield, 8);
		array.setBlock(x+11, y+4, z+5, shield, 8);
		array.setBlock(x+11, y+4, z+6, shield, 8);
		array.setBlock(x+12, y+0, z+3, shield, 8);
		array.setBlock(x+12, y+0, z+4, shield, 8);
		array.setBlock(x+12, y+0, z+5, shield, 8);
		array.setBlock(x+12, y+0, z+6, shield, 8);
		array.setBlock(x+12, y+0, z+7, shield, 8);
		array.setBlock(x+12, y+1, z+2, shield, 8);
		array.setBlock(x+12, y+1, z+8, shield, 8);
		array.setBlock(x+12, y+2, z+2, shield, 8);
		array.setBlock(x+12, y+2, z+8, shield, 8);
		array.setBlock(x+12, y+3, z+3, shield, 8);
		array.setBlock(x+12, y+3, z+7, shield, 8);
		array.setBlock(x+12, y+4, z+4, shield, 8);
		array.setBlock(x+12, y+4, z+5, shield, 8);
		array.setBlock(x+12, y+4, z+6, shield, 8);
		array.setBlock(x+13, y+0, z+5, shield, 8);
		array.setBlock(x+13, y+1, z+3, shield, 8);
		array.setBlock(x+13, y+1, z+4, shield, 8);
		array.setBlock(x+13, y+1, z+6, shield, 8);
		array.setBlock(x+13, y+1, z+7, shield, 8);
		array.setBlock(x+13, y+2, z+3, shield, 8);
		array.setBlock(x+13, y+2, z+4, shield, 8);
		array.setBlock(x+13, y+2, z+6, shield, 8);
		array.setBlock(x+13, y+2, z+7, shield, 8);
		array.setBlock(x+13, y+3, z+4, shield, 8);
		array.setBlock(x+13, y+3, z+5, shield, 8);
		array.setBlock(x+13, y+3, z+6, shield, 8);
		return array;
	}

	public static FilledBlockArray getBurrowStructure(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);

		y -= 11;
		x -= 8;
		z -= 5;

		Block b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();

		array.setBlock(x+0, y+2, z+3, b, 9);
		array.setBlock(x+0, y+3, z+3, b, 9);
		array.setBlock(x+0, y+5, z+2, b, 9);
		array.setBlock(x+0, y+5, z+4, b, 9);
		array.setBlock(x+0, y+6, z+2, b, 9);
		array.setBlock(x+0, y+6, z+3, b, 9);
		array.setBlock(x+0, y+6, z+4, b, 9);
		array.setBlock(x+0, y+7, z+3, b, 9);
		array.setBlock(x+1, y+1, z+2, b, 12);
		array.setBlock(x+1, y+1, z+3, b, 12);
		array.setBlock(x+1, y+1, z+4, b, 12);
		array.setBlock(x+1, y+2, z+2, b, 9);
		array.setBlock(x+1, y+2, z+3, Blocks.chest, 5);
		array.setBlock(x+1, y+2, z+4, b, 12);
		array.setBlock(x+1, y+3, z+2, b, 9);
		array.setBlock(x+1, y+3, z+3, b, 11);
		array.setBlock(x+1, y+3, z+4, b, 9);
		array.setBlock(x+1, y+4, z+2, b, 9);
		array.setBlock(x+1, y+4, z+3, b, 9);
		array.setBlock(x+1, y+4, z+4, b, 9);
		array.setBlock(x+1, y+5, z+1, b, 9);
		array.setBlock(x+1, y+5, z+2, b, 9);
		array.setBlock(x+1, y+5, z+3, b, 9);
		array.setBlock(x+1, y+5, z+4, b, 9);
		array.setBlock(x+1, y+5, z+5, b, 9);
		array.setBlock(x+1, y+6, z+1, b, 9);
		array.setBlock(x+1, y+6, z+2, b, 9);
		array.setBlock(x+1, y+6, z+3, Blocks.chest, 5);
		array.setBlock(x+1, y+6, z+4, b, 9);
		array.setBlock(x+1, y+6, z+5, b, 9);
		array.setBlock(x+1, y+7, z+2, b, 9);
		array.setBlock(x+1, y+7, z+3, b, 11);
		array.setBlock(x+1, y+7, z+4, b, 9);
		array.setBlock(x+1, y+7, z+5, b, 9);
		array.setBlock(x+1, y+8, z+3, b, 9);
		array.setBlock(x+2, y+0, z+2, b, 12);
		array.setBlock(x+2, y+0, z+3, b, 9);
		array.setBlock(x+2, y+0, z+4, b, 12);
		array.setBlock(x+2, y+1, z+1, b, 12);
		array.setBlock(x+2, y+1, z+5, b, 12);
		array.setBlock(x+2, y+2, z+1, b, 12);
		array.setBlock(x+2, y+2, z+5, b, 12);
		array.setBlock(x+2, y+3, z+1, b, 9);
		array.setBlock(x+2, y+3, z+5, b, 12);
		array.setBlock(x+2, y+4, z+1, b, 9);
		array.setBlock(x+2, y+4, z+2, b, 9);
		array.setBlock(x+2, y+4, z+3, b, 9);
		array.setBlock(x+2, y+4, z+4, b, 9);
		array.setBlock(x+2, y+4, z+5, b, 9);
		array.setBlock(x+2, y+5, z+0, b, 9);
		array.setBlock(x+2, y+5, z+1, b, 9);
		array.setBlock(x+2, y+5, z+5, b, 9);
		array.setBlock(x+2, y+5, z+6, b, 9);
		array.setBlock(x+2, y+6, z+0, b, 9);
		array.setBlock(x+2, y+6, z+1, b, 9);
		array.setBlock(x+2, y+6, z+5, b, 9);
		array.setBlock(x+2, y+6, z+6, b, 9);
		array.setBlock(x+2, y+7, z+1, b, 9);
		array.setBlock(x+2, y+7, z+5, b, 9);
		array.setBlock(x+2, y+8, z+2, b, 9);
		array.setBlock(x+2, y+8, z+3, b, 9);
		array.setBlock(x+2, y+8, z+4, b, 9);
		array.setBlock(x+3, y+0, z+2, b, 12);
		array.setBlock(x+3, y+0, z+3, b, 9);
		array.setBlock(x+3, y+0, z+4, b, 9);
		array.setBlock(x+3, y+1, z+1, b, 12);
		array.setBlock(x+3, y+1, z+5, b, 12);
		array.setBlock(x+3, y+2, z+0, b, 9);
		array.setBlock(x+3, y+2, z+1, Blocks.chest, 3);
		array.setBlock(x+3, y+2, z+5, Blocks.chest, 2);
		array.setBlock(x+3, y+2, z+6, b, 9);
		array.setBlock(x+3, y+3, z+0, b, 9);
		array.setBlock(x+3, y+3, z+1, b, 11);
		array.setBlock(x+3, y+3, z+5, b, 11);
		array.setBlock(x+3, y+3, z+6, b, 9);
		array.setBlock(x+3, y+4, z+1, b, 9);
		array.setBlock(x+3, y+4, z+2, b, 9);
		array.setBlock(x+3, y+4, z+3, b, 9);
		array.setBlock(x+3, y+4, z+4, b, 9);
		array.setBlock(x+3, y+4, z+5, b, 9);
		array.setBlock(x+3, y+5, z+1, b, 9);
		array.setBlock(x+3, y+5, z+5, b, 9);
		array.setBlock(x+3, y+6, z+0, b, 9);
		array.setBlock(x+3, y+6, z+1, Blocks.chest, 3);
		array.setBlock(x+3, y+6, z+5, Blocks.chest, 2);
		array.setBlock(x+3, y+6, z+6, b, 9);
		array.setBlock(x+3, y+7, z+0, b, 9);
		array.setBlock(x+3, y+7, z+1, b, 11);
		array.setBlock(x+3, y+7, z+5, b, 11);
		array.setBlock(x+3, y+7, z+6, b, 9);
		array.setBlock(x+3, y+8, z+1, b, 9);
		array.setBlock(x+3, y+8, z+2, b, 9);
		array.setBlock(x+3, y+8, z+3, b, 9);
		array.setBlock(x+3, y+8, z+4, b, 9);
		array.setBlock(x+3, y+8, z+5, b, 9);
		array.setBlock(x+4, y+0, z+2, b, 9);
		array.setBlock(x+4, y+0, z+3, b, 12);
		array.setBlock(x+4, y+0, z+4, b, 12);
		array.setBlock(x+4, y+1, z+1, b, 12);
		array.setBlock(x+4, y+1, z+5, b, 12);
		array.setBlock(x+4, y+2, z+1, b, 9);
		array.setBlock(x+4, y+2, z+5, b, 9);
		array.setBlock(x+4, y+3, z+1, b, 12);
		array.setBlock(x+4, y+3, z+5, b, 12);
		array.setBlock(x+4, y+4, z+1, b, 9);
		array.setBlock(x+4, y+4, z+2, b, 9);
		array.setBlock(x+4, y+4, z+3, b, 9);
		array.setBlock(x+4, y+4, z+4, b, 9);
		array.setBlock(x+4, y+4, z+5, b, 9);
		array.setBlock(x+4, y+5, z+0, b, 9);
		array.setBlock(x+4, y+5, z+1, b, 9);
		array.setBlock(x+4, y+5, z+5, b, 9);
		array.setBlock(x+4, y+5, z+6, b, 9);
		array.setBlock(x+4, y+6, z+0, b, 9);
		array.setBlock(x+4, y+6, z+1, b, 9);
		array.setBlock(x+4, y+6, z+5, b, 9);
		array.setBlock(x+4, y+6, z+6, b, 9);
		array.setBlock(x+4, y+7, z+1, b, 9);
		array.setBlock(x+4, y+7, z+5, b, 9);
		array.setBlock(x+4, y+8, z+2, b, 9);
		array.setBlock(x+4, y+8, z+3, b, 9);
		array.setBlock(x+4, y+8, z+4, b, 9);
		array.setBlock(x+5, y+1, z+1, b, 9);
		array.setBlock(x+5, y+1, z+2, b, 9);
		array.setBlock(x+5, y+1, z+3, b, 9);
		array.setBlock(x+5, y+1, z+4, b, 9);
		array.setBlock(x+5, y+1, z+5, b, 9);
		array.setBlock(x+5, y+2, z+1, b, 9);
		array.setBlock(x+5, y+2, z+5, b, 9);
		array.setBlock(x+5, y+3, z+1, b, 9);
		array.setBlock(x+5, y+3, z+5, b, 9);
		array.setBlock(x+5, y+4, z+2, b, 9);
		array.setBlock(x+5, y+4, z+3, b, 3);
		array.setBlock(x+5, y+4, z+4, b, 9);
		array.setBlock(x+5, y+5, z+1, b, 9);
		array.setBlock(x+5, y+5, z+5, b, 9);
		array.setBlock(x+5, y+6, z+1, b, 9);
		array.setBlock(x+5, y+6, z+5, b, 9);
		array.setBlock(x+5, y+7, z+1, b, 9);
		array.setBlock(x+5, y+7, z+5, b, 9);
		array.setBlock(x+5, y+8, z+1, b, 9);
		array.setBlock(x+5, y+8, z+5, b, 9);
		array.setBlock(x+6, y+2, z+2, b, 12);
		array.setBlock(x+6, y+2, z+3, b, 9);
		array.setBlock(x+6, y+2, z+4, b, 9);
		array.setBlock(x+6, y+3, z+2, b, 9);
		array.setBlock(x+6, y+3, z+3, b, 12);
		array.setBlock(x+6, y+3, z+4, b, 12);
		array.setBlock(x+6, y+4, z+3, b, 9);
		array.setBlock(x+6, y+5, z+2, b, 9);
		array.setBlock(x+6, y+5, z+3, b, 9);
		array.setBlock(x+6, y+5, z+4, b, 9);
		array.setBlock(x+6, y+6, z+1, b, 9);
		array.setBlock(x+6, y+6, z+5, b, 9);
		array.setBlock(x+6, y+7, z+1, b, 9);
		array.setBlock(x+6, y+7, z+5, b, 9);
		array.setBlock(x+6, y+8, z+1, Blocks.stone);
		array.setBlock(x+6, y+8, z+5, Blocks.stone);
		array.setBlock(x+7, y+6, z+1, Blocks.stone);
		array.setBlock(x+7, y+6, z+2, Blocks.stone);
		array.setBlock(x+7, y+6, z+3, Blocks.stone);
		array.setBlock(x+7, y+6, z+4, Blocks.stone);
		array.setBlock(x+7, y+7, z+1, Blocks.stone);
		array.setBlock(x+7, y+7, z+5, Blocks.stone);
		array.setBlock(x+7, y+8, z+1, Blocks.stone);
		array.setBlock(x+7, y+8, z+5, Blocks.stone);
		array.setBlock(x+8, y+7, z+2, Blocks.stone);
		array.setBlock(x+8, y+7, z+3, Blocks.stone);
		array.setBlock(x+8, y+7, z+4, Blocks.stone);

		//Covering
		array.setBlock(x+7, y+10, z+5, Blocks.grass);
		array.setBlock(x+7, y+11, z+2, Blocks.grass);
		array.setBlock(x+7, y+11, z+3, Blocks.grass);
		array.setBlock(x+7, y+11, z+4, Blocks.grass);
		array.setBlock(x+8, y+8, z+2, Blocks.dirt);
		array.setBlock(x+8, y+8, z+3, Blocks.dirt);
		array.setBlock(x+8, y+8, z+4, Blocks.dirt);
		array.setBlock(x+8, y+9, z+2, Blocks.dirt);
		array.setBlock(x+8, y+9, z+3, Blocks.grass);
		array.setBlock(x+8, y+9, z+4, Blocks.dirt);
		array.setBlock(x+8, y+10, z+1, Blocks.grass);
		array.setBlock(x+8, y+10, z+2, Blocks.grass);
		array.setBlock(x+8, y+10, z+4, Blocks.grass);
		array.setBlock(x+8, y+10, z+5, Blocks.grass);
		array.setBlock(x+9, y+10, z+1, Blocks.grass);
		array.setBlock(x+9, y+10, z+2, Blocks.grass);
		array.setBlock(x+9, y+10, z+3, Blocks.grass);
		array.setBlock(x+9, y+10, z+4, Blocks.grass);
		array.setBlock(x+9, y+10, z+5, Blocks.grass);
		array.setBlock(x+7, y+9, z+1, Blocks.dirt);
		array.setBlock(x+7, y+9, z+5, Blocks.dirt);
		array.setBlock(x+7, y+10, z+1, Blocks.dirt);
		array.setBlock(x+6, y+9, z+1, Blocks.dirt);
		array.setBlock(x+6, y+9, z+5, Blocks.dirt);
		array.setBlock(x+6, y+10, z+2, Blocks.dirt);
		array.setBlock(x+6, y+10, z+3, Blocks.dirt);
		array.setBlock(x+6, y+10, z+4, Blocks.dirt);
		array.setBlock(x+6, y+11, z+2, Blocks.grass);
		array.setBlock(x+6, y+11, z+3, Blocks.grass);
		array.setBlock(x+6, y+11, z+4, Blocks.grass);
		array.setBlock(x+5, y+9, z+1, Blocks.dirt);
		array.setBlock(x+5, y+9, z+5, Blocks.dirt);
		array.setBlock(x+5, y+10, z+2, Blocks.dirt);
		array.setBlock(x+5, y+10, z+3, Blocks.dirt);
		array.setBlock(x+5, y+10, z+4, Blocks.dirt);
		array.setBlock(x+5, y+11, z+2, Blocks.grass);
		array.setBlock(x+5, y+11, z+3, Blocks.grass);
		array.setBlock(x+5, y+11, z+4, Blocks.grass);
		array.setBlock(x+4, y+9, z+2, Blocks.dirt);
		array.setBlock(x+4, y+9, z+3, Blocks.dirt);
		array.setBlock(x+4, y+9, z+4, Blocks.dirt);

		array.setBlock(x+3, y+1, z+3, Blocks.torch, 5);
		array.setBlock(x+3, y+5, z+3, Blocks.torch, 5);

		//Air
		array.setBlock(x+2, y+1, z+2, Blocks.air);
		array.setBlock(x+2, y+1, z+3, Blocks.air);
		array.setBlock(x+2, y+1, z+4, Blocks.air);
		array.setBlock(x+2, y+2, z+2, Blocks.air);
		array.setBlock(x+2, y+2, z+3, Blocks.air);
		array.setBlock(x+2, y+2, z+4, Blocks.air);
		array.setBlock(x+2, y+3, z+2, Blocks.air);
		array.setBlock(x+2, y+3, z+3, Blocks.air);
		array.setBlock(x+2, y+3, z+4, Blocks.air);
		array.setBlock(x+2, y+5, z+2, Blocks.air);
		array.setBlock(x+2, y+5, z+3, Blocks.air);
		array.setBlock(x+2, y+5, z+4, Blocks.air);
		array.setBlock(x+2, y+6, z+2, Blocks.air);
		array.setBlock(x+2, y+6, z+3, Blocks.air);
		array.setBlock(x+2, y+6, z+4, Blocks.air);
		array.setBlock(x+2, y+7, z+2, Blocks.air);
		array.setBlock(x+2, y+7, z+3, Blocks.air);
		array.setBlock(x+2, y+7, z+4, Blocks.air);
		array.setBlock(x+3, y+1, z+2, Blocks.air);
		array.setBlock(x+3, y+1, z+4, Blocks.air);
		array.setBlock(x+3, y+2, z+2, Blocks.air);
		array.setBlock(x+3, y+2, z+3, Blocks.air);
		array.setBlock(x+3, y+2, z+4, Blocks.air);
		array.setBlock(x+3, y+3, z+2, Blocks.air);
		array.setBlock(x+3, y+3, z+3, Blocks.air);
		array.setBlock(x+3, y+3, z+4, Blocks.air);
		array.setBlock(x+3, y+5, z+2, Blocks.air);
		array.setBlock(x+3, y+5, z+4, Blocks.air);
		array.setBlock(x+3, y+6, z+2, Blocks.air);
		array.setBlock(x+3, y+6, z+3, Blocks.air);
		array.setBlock(x+3, y+6, z+4, Blocks.air);
		array.setBlock(x+3, y+7, z+2, Blocks.air);
		array.setBlock(x+3, y+7, z+3, Blocks.air);
		array.setBlock(x+3, y+7, z+4, Blocks.air);
		array.setBlock(x+4, y+1, z+2, Blocks.air);
		array.setBlock(x+4, y+1, z+3, Blocks.air);
		array.setBlock(x+4, y+1, z+4, Blocks.air);
		array.setBlock(x+4, y+2, z+2, Blocks.air);
		array.setBlock(x+4, y+2, z+3, Blocks.air);
		array.setBlock(x+4, y+2, z+4, Blocks.air);
		array.setBlock(x+4, y+3, z+2, Blocks.air);
		array.setBlock(x+4, y+3, z+3, Blocks.air);
		array.setBlock(x+4, y+3, z+4, Blocks.air);
		array.setBlock(x+4, y+5, z+2, Blocks.air);
		array.setBlock(x+4, y+5, z+3, Blocks.air);
		array.setBlock(x+4, y+5, z+4, Blocks.air);
		array.setBlock(x+4, y+6, z+2, Blocks.air);
		array.setBlock(x+4, y+6, z+3, Blocks.air);
		array.setBlock(x+4, y+6, z+4, Blocks.air);
		array.setBlock(x+4, y+7, z+2, Blocks.air);
		array.setBlock(x+4, y+7, z+3, Blocks.air);
		array.setBlock(x+4, y+7, z+4, Blocks.air);
		array.setBlock(x+5, y+2, z+2, Blocks.air);
		array.setBlock(x+5, y+2, z+3, Blocks.air);
		array.setBlock(x+5, y+2, z+4, Blocks.air);
		array.setBlock(x+5, y+3, z+2, Blocks.air);
		array.setBlock(x+5, y+3, z+3, Blocks.air);
		array.setBlock(x+5, y+3, z+4, Blocks.air);
		array.setBlock(x+5, y+5, z+2, Blocks.air);
		array.setBlock(x+5, y+5, z+3, Blocks.air);
		array.setBlock(x+5, y+5, z+4, Blocks.air);
		array.setBlock(x+5, y+6, z+2, Blocks.air);
		array.setBlock(x+5, y+6, z+3, Blocks.air);
		array.setBlock(x+5, y+6, z+4, Blocks.air);
		array.setBlock(x+5, y+7, z+2, Blocks.air);
		array.setBlock(x+5, y+7, z+3, Blocks.air);
		array.setBlock(x+5, y+7, z+4, Blocks.air);
		array.setBlock(x+5, y+8, z+2, Blocks.air);
		array.setBlock(x+5, y+8, z+3, Blocks.air);
		array.setBlock(x+5, y+8, z+4, Blocks.air);
		array.setBlock(x+5, y+9, z+2, Blocks.air);
		array.setBlock(x+5, y+9, z+3, Blocks.air);
		array.setBlock(x+5, y+9, z+4, Blocks.air);
		array.setBlock(x+6, y+6, z+2, Blocks.air);
		array.setBlock(x+6, y+6, z+3, Blocks.air);
		array.setBlock(x+6, y+6, z+4, Blocks.air);
		array.setBlock(x+6, y+7, z+2, Blocks.air);
		array.setBlock(x+6, y+7, z+3, Blocks.air);
		array.setBlock(x+6, y+7, z+4, Blocks.air);
		array.setBlock(x+6, y+8, z+2, Blocks.air);
		array.setBlock(x+6, y+8, z+3, Blocks.air);
		array.setBlock(x+6, y+8, z+4, Blocks.air);
		array.setBlock(x+6, y+9, z+2, Blocks.air);
		array.setBlock(x+6, y+9, z+3, Blocks.air);
		array.setBlock(x+6, y+9, z+4, Blocks.air);
		array.setBlock(x+7, y+7, z+2, Blocks.air);
		array.setBlock(x+7, y+7, z+3, Blocks.air);
		array.setBlock(x+7, y+7, z+4, Blocks.air);
		array.setBlock(x+7, y+8, z+2, Blocks.air);
		array.setBlock(x+7, y+8, z+3, Blocks.air);
		array.setBlock(x+7, y+8, z+4, Blocks.air);
		array.setBlock(x+7, y+9, z+2, Blocks.air);
		array.setBlock(x+7, y+9, z+3, Blocks.air);
		array.setBlock(x+7, y+9, z+4, Blocks.air);
		array.setBlock(x+7, y+10, z+2, Blocks.air);
		array.setBlock(x+7, y+10, z+3, Blocks.air);
		array.setBlock(x+7, y+10, z+4, Blocks.air);

		//Water pit, if cannot stop it genning under lakes
		//array.setBlock(x+7, y+5, z+3, Blocks.air);
		//array.setBlock(x+7, y+6, z+3, Blocks.air);

		//Entry Blocks
		array.setBlock(x+8, y+10, z+3, Blocks.air);
		array.setBlock(x+8, y+11, z+3, Blocks.air);
		array.setBlock(x+8, y+11, z+2, Blocks.air);
		array.setBlock(x+8, y+11, z+4, Blocks.air);

		return array;
	}

	public static FilledBlockArray getOceanStructure(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);
		return array;
	}

	public static FilledBlockArray getTreeStructure(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);
		Block b = ChromaBlocks.PYLONSTRUCT.getBlockInstance();
		for (int i = 0; i <= 12; i++) {
			int dy = y-i;
			if (i == 0) {
				array.setBlock(x, dy, z-1, Blocks.glass);
				array.setBlock(x+1, dy, z, Blocks.glass);
				array.setBlock(x+1, dy, z-1, Blocks.glass);
			}
			else {
				int meta = (i == 3 || i == 5 || i == 7 || i == 9) ? 15 : 11;
				array.setBlock(x, dy, z, b, meta);
				array.setBlock(x, dy, z-1, b, meta);
				array.setBlock(x+1, dy, z, b, meta);
				array.setBlock(x+1, dy, z-1, b, meta);
			}

			if (i > 1) {
				array.addBlock(x-1, dy, z, Blocks.air);
				array.addBlock(x-1, dy, z-1, Blocks.air);
				array.addBlock(x-1, dy, z-2, Blocks.air);
				array.addBlock(x-1, dy, z+1, Blocks.air);
				array.addBlock(x+2, dy, z, Blocks.air);
				array.addBlock(x+2, dy, z-1, Blocks.air);
				array.addBlock(x+2, dy, z+1, Blocks.air);
				array.addBlock(x+2, dy, z-2, Blocks.air);
				array.addBlock(x, dy, z-2, Blocks.air);
				array.addBlock(x+1, dy, z-2, Blocks.air);
				array.addBlock(x, dy, z+1, Blocks.air);
				array.addBlock(x+1, dy, z+1, Blocks.air);

				Block b2 = ChromaBlocks.POWERTREE.getBlockInstance();
				array.addBlock(x-1, dy, z, b2);
				array.addBlock(x-1, dy, z-1, b2);
				array.addBlock(x-1, dy, z-2, b2);
				array.addBlock(x-1, dy, z+1, b2);
				array.addBlock(x+2, dy, z, b2);
				array.addBlock(x+2, dy, z-1, b2);
				array.addBlock(x+2, dy, z+1, b2);
				array.addBlock(x+2, dy, z-2, b2);
				array.addBlock(x, dy, z-2, b2);
				array.addBlock(x+1, dy, z-2, b2);
				array.addBlock(x, dy, z+1, b2);
				array.addBlock(x+1, dy, z+1, b2);
			}
		}

		array.setBlock(x-1, y-1, z, b, 14);
		array.setBlock(x-1, y-1, z-1, b, 14);

		array.setBlock(x+2, y-1, z, b, 14);
		array.setBlock(x+2, y-1, z-1, b, 14);

		array.setBlock(x, y-1, z-2, b, 14);
		array.setBlock(x+1, y-1, z-2, b, 14);

		array.setBlock(x, y-1, z+1, b, 14);
		array.setBlock(x+1, y-1, z+1, b, 14);

		return array;
	}

	public static FilledBlockArray getInfusionStructure(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);
		Block b = ChromaBlocks.PYLONSTRUCT.getBlockInstance();
		double r = 0.6;
		for (int i = 0; i < 360; i += 15) {
			int dx = MathHelper.floor_double(x+0.5+r*Math.sin(Math.toRadians(i)));
			int dz = MathHelper.floor_double(z+0.5+r*Math.cos(Math.toRadians(i)));
			array.setBlock(dx, y-1, dz, b, 12);
		}

		r = 2;
		for (int i = 0; i < 360; i += 15) {
			int dx = MathHelper.floor_double(x+0.5+r*Math.sin(Math.toRadians(i)));
			int dz = MathHelper.floor_double(z+0.5+r*Math.cos(Math.toRadians(i)));
			array.setBlock(dx, y-1, dz, ChromaBlocks.CHROMA.getBlockInstance(), 0);
			array.setBlock(dx, y-2, dz, b, 0);
		}

		r = 3.2;
		for (int i = 0; i < 360; i += 15) {
			int dx = MathHelper.floor_double(x+0.5+r*Math.sin(Math.toRadians(i)));
			int dz = MathHelper.floor_double(z+0.5+r*Math.cos(Math.toRadians(i)));
			array.setBlock(dx, y-1, dz, b, 12);
		}

		//ReikaJavaLibrary.pConsole(array);
		return array;
	}

	public static FilledBlockArray getRitualStructure(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);
		Block b = ChromaBlocks.PYLONSTRUCT.getBlockInstance();
		for (int i = -5; i <= 5; i++) {
			for (int k = -5; k <= 5; k++) {
				for (int j = 1; j <= 3; j++)
					array.setEmpty(x+i, y+j, z+k, true, true);
			}
		}

		for (int i = -5; i <= 5; i++) {
			for (int k = -5; k <= 5; k++) {
				array.setBlock(x+i, y, z+k, b, 0);
			}
		}

		for (int i = -4; i <= 4; i++) {
			for (int k = -4; k <= 4; k++) {
				array.setBlock(x+i, y+1, z+k, b, 0);
			}
		}

		for (int i = -4; i <= 4; i++) {
			array.setBlock(x-4, y+1, z+i, b, 1);
			array.setBlock(x+4, y+1, z+i, b, 1);
			array.setBlock(x+i, y+1, z-4, b, 1);
			array.setBlock(x+i, y+1, z+4, b, 1);
		}

		for (int i = -3; i <= 3; i++) {
			array.setBlock(x-3, y+2, z+i, b, 1);
			array.setBlock(x+3, y+2, z+i, b, 1);
			array.setBlock(x+i, y+2, z-3, b, 1);
			array.setBlock(x+i, y+2, z+3, b, 1);
		}

		array.setBlock(x+2, y+2, z+2, b, 2);
		array.setBlock(x-2, y+2, z+2, b, 2);
		array.setBlock(x+2, y+2, z-2, b, 2);
		array.setBlock(x-2, y+2, z-2, b, 2);

		array.setBlock(x+2, y+3, z+2, b, 7);
		array.setBlock(x-2, y+3, z+2, b, 7);
		array.setBlock(x+2, y+3, z-2, b, 7);
		array.setBlock(x-2, y+3, z-2, b, 7);

		array.setBlock(x+3, y+2, z+3, b, 8);
		array.setBlock(x-3, y+2, z+3, b, 8);
		array.setBlock(x+3, y+2, z-3, b, 8);
		array.setBlock(x-3, y+2, z-3, b, 8);

		array.setBlock(x+4, y+1, z+4, b, 8);
		array.setBlock(x-4, y+1, z+4, b, 8);
		array.setBlock(x+4, y+1, z-4, b, 8);
		array.setBlock(x-4, y+1, z-4, b, 8);

		array.setBlock(x-1, y+1, z-1, ChromaBlocks.CHROMA.getBlockInstance());
		array.setBlock(x, y+1, z-1, ChromaBlocks.CHROMA.getBlockInstance());
		array.setBlock(x+1, y+1, z-1, ChromaBlocks.CHROMA.getBlockInstance());
		array.setBlock(x+1, y+1, z, ChromaBlocks.CHROMA.getBlockInstance());
		array.setBlock(x+1, y+1, z+1, ChromaBlocks.CHROMA.getBlockInstance());
		array.setBlock(x, y+1, z+1, ChromaBlocks.CHROMA.getBlockInstance());
		array.setBlock(x-1, y+1, z+1, ChromaBlocks.CHROMA.getBlockInstance());
		array.setBlock(x-1, y+1, z, ChromaBlocks.CHROMA.getBlockInstance());

		array.setBlock(x, y+2, z, ChromaTiles.RITUAL.getBlock(), ChromaTiles.RITUAL.getBlockMetadata());

		array.remove(x, y, z);

		return array;
	}

	public static FilledBlockArray getCastingLevelOne(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);

		for (int i = -6; i <= 6; i++) {
			for (int k = 0; k < 6; k++) {
				int dy = y+k;
				array.setEmpty(x-6, dy, z+i, true, true);
				array.setEmpty(x+6, dy, z+i, true, true);
				array.setEmpty(x+i, dy, z-6, true, true);
				array.setEmpty(x+i, dy, z+6, true, true);
			}
		}

		Block b = ChromaBlocks.PYLONSTRUCT.getBlockInstance();
		for (int i = 2; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			for (int k = 3; k <= 5; k++) {
				int dx = x+k*dir.offsetX;
				int dz = z+k*dir.offsetZ;
				array.addBlock(dx, y, dz, b, 0);
				array.addBlock(dx, y, dz, ChromaBlocks.RUNE.getBlockInstance());
			}

			int dx = x+dir.offsetX*6;
			int dz = z+dir.offsetZ*6;
			for (int k = 1; k <= 5; k++) {
				int meta2 = k == 1 ? 8 : 2;
				int dy = y+k;
				array.setBlock(dx, dy, dz, b, meta2);
			}
		}

		for (int i = -6; i <= 6; i++) {
			array.setBlock(x-6, y, z+i, b, 0);
			array.setBlock(x+6, y, z+i, b, 0);
			array.setBlock(x+i, y, z-6, b, 0);
			array.setBlock(x+i, y, z+6, b, 0);
		}

		for (int k = 1; k <= 4; k++) {
			int meta2 = k == 1 ? 0 : 2;
			int dy = y+k;
			array.setBlock(x+6, dy, z+6, b, meta2);
			array.setBlock(x-6, dy, z+6, b, meta2);
			array.setBlock(x+6, dy, z-6, b, meta2);
			array.setBlock(x-6, dy, z-6, b, meta2);
		}

		for (int k = 1; k <= 6; k++) {
			int meta2 = k == 1 || k == 5 ? 0 : (k == 6 ? 7 : 2);
			int dy = y+k;
			array.setBlock(x+6, dy, z+3, b, meta2);
			array.setBlock(x+6, dy, z-3, b, meta2);
			array.setBlock(x-6, dy, z+3, b, meta2);
			array.setBlock(x-6, dy, z-3, b, meta2);
			array.setBlock(x+3, dy, z-6, b, meta2);
			array.setBlock(x-3, dy, z-6, b, meta2);
			array.setBlock(x-3, dy, z+6, b, meta2);
			array.setBlock(x+3, dy, z+6, b, meta2);
		}

		for (int i = -5; i <= 5; i++) {
			if (i != 3 && i != -3 && i != 0) {
				int dy = Math.abs(i) < 3 ? y+6 : y+5;
				array.setBlock(x-6, dy, z+i, b, 1);
				array.setBlock(x+6, dy, z+i, b, 1);
				array.setBlock(x+i, dy, z-6, b, 1);
				array.setBlock(x+i, dy, z+6, b, 1);
			}
		}

		for (int i = -3; i <= 3; i++) {
			for (int k = 0; k <= 1; k++) {
				if (k == 0 || Math.abs(i)%2 == 1) {
					int dy = y+k;
					array.setBlock(x-3, dy, z+i, b, 0);
					array.setBlock(x+3, dy, z+i, b, 0);
					array.setBlock(x+i, dy, z-3, b, 0);
					array.setBlock(x+i, dy, z+3, b, 0);
				}
			}
		}

		array.setBlock(x-6, y+5, z-6, Blocks.coal_block);
		array.setBlock(x+6, y+5, z-6, Blocks.coal_block);
		array.setBlock(x+6, y+5, z+6, Blocks.coal_block);
		array.setBlock(x-6, y+5, z+6, Blocks.coal_block);

		array.setBlock(x, y+6, z-6, Blocks.lapis_block);
		array.setBlock(x, y+6, z+6, Blocks.lapis_block);
		array.setBlock(x+6, y+6, z, Blocks.lapis_block);
		array.setBlock(x-6, y+6, z, Blocks.lapis_block);

		return array;
	}

	public static FilledBlockArray getCastingLevelTwo(World world, int x, int y, int z) {
		FilledBlockArray array = getCastingLevelOne(world, x, y, z);

		Block b = ChromaBlocks.PYLONSTRUCT.getBlockInstance();
		for (int i = -5; i <= 5; i++) {
			for (int k = -5; k <= 5; k++) {
				int dx = x+i;
				int dz = z+k;
				array.remove(dx, y, dz);
				array.addBlock(dx, y, dz, b, 0);
				array.addBlock(dx, y, dz, ChromaBlocks.RUNE.getBlockInstance());
			}
		}

		for (int i = -5; i <= 5; i++) {
			if (i != 0 && Math.abs(i) != 3) {
				array.setBlock(x-6, y, z+i, Blocks.quartz_block, 0);
				array.setBlock(x+6, y, z+i, Blocks.quartz_block, 0);
				array.setBlock(x+i, y, z-6, Blocks.quartz_block, 0);
				array.setBlock(x+i, y, z+6, Blocks.quartz_block, 0);
			}
		}

		for (int i = -3; i <= 3; i++) {
			int dy = y+1;
			array.remove(x-3, dy, z+i);
			array.remove(x+3, dy, z+i);
			array.remove(x+i, dy, z-3);
			array.remove(x+i, dy, z+3);
		}

		for (int i = -2; i <= 2; i++) {
			array.remove(x-2, y, z+i);
			array.remove(x+2, y, z+i);
			array.remove(x+i, y, z-2);
			array.remove(x+i, y, z+2);
		}

		array.remove(x, y, z);

		array.setBlock(x-6, y+5, z-6, Blocks.redstone_block);
		array.setBlock(x+6, y+5, z-6, Blocks.redstone_block);
		array.setBlock(x+6, y+5, z+6, Blocks.redstone_block);
		array.setBlock(x-6, y+5, z+6, Blocks.redstone_block);

		array.setBlock(x, y+6, z-6, Blocks.gold_block);
		array.setBlock(x, y+6, z+6, Blocks.gold_block);
		array.setBlock(x+6, y+6, z, Blocks.gold_block);
		array.setBlock(x-6, y+6, z, Blocks.gold_block);
		return array;
	}

	public static FilledBlockArray getCastingLevelThree(World world, int x, int y, int z) {
		FilledBlockArray array = getCastingLevelTwo(world, x, y, z);
		Block b = ChromaBlocks.PYLONSTRUCT.getBlockInstance();

		for (int i = -7; i <= 7; i++) {
			array.setBlock(x-7, y, z+i, b, 0);
			array.setBlock(x+7, y, z+i, b, 0);
			array.setBlock(x+i, y, z-7, b, 0);
			array.setBlock(x+i, y, z+7, b, 0);
		}

		for (int i = -8; i <= 8; i++) {
			array.setBlock(x-8, y, z+i, Blocks.obsidian);
			array.setBlock(x+8, y, z+i, Blocks.obsidian);
			array.setBlock(x+i, y, z-8, Blocks.obsidian);
			array.setBlock(x+i, y, z+8, Blocks.obsidian);
		}

		for (int i = 1; i <= 4; i++) {
			int dy = y+i;
			if (i == 3) {
				Block b2 = ChromaBlocks.RUNE.getBlockInstance();
				array.setBlock(x-2, dy, z-8, b2);
				array.setBlock(x-6, dy, z-8, b2);
				array.setBlock(x+2, dy, z-8, b2);
				array.setBlock(x+6, dy, z-8, b2);

				array.setBlock(x-2, dy, z+8, b2);
				array.setBlock(x-6, dy, z+8, b2);
				array.setBlock(x+2, dy, z+8, b2);
				array.setBlock(x+6, dy, z+8, b2);

				array.setBlock(x-8, dy, z-2, b2);
				array.setBlock(x-8, dy, z-6, b2);
				array.setBlock(x-8, dy, z+2, b2);
				array.setBlock(x-8, dy, z+6, b2);

				array.setBlock(x+8, dy, z+6, b2);
				array.setBlock(x+8, dy, z+2, b2);
				array.setBlock(x+8, dy, z-6, b2);
				array.setBlock(x+8, dy, z-2, b2);
			}
			else {
				Block b2 = i == 4 ? ChromaTiles.REPEATER.getBlock() : b;
				int meta2 = i == 4 ? ChromaTiles.REPEATER.getBlockMetadata() : 0;
				array.setBlock(x-2, dy, z-8, b2, meta2);
				array.setBlock(x-6, dy, z-8, b2, meta2);
				array.setBlock(x+2, dy, z-8, b2, meta2);
				array.setBlock(x+6, dy, z-8, b2, meta2);

				array.setBlock(x-2, dy, z+8, b2, meta2);
				array.setBlock(x-6, dy, z+8, b2, meta2);
				array.setBlock(x+2, dy, z+8, b2, meta2);
				array.setBlock(x+6, dy, z+8, b2, meta2);

				array.setBlock(x-8, dy, z-2, b2, meta2);
				array.setBlock(x-8, dy, z-6, b2, meta2);
				array.setBlock(x-8, dy, z+2, b2, meta2);
				array.setBlock(x-8, dy, z+6, b2, meta2);

				array.setBlock(x+8, dy, z+6, b2, meta2);
				array.setBlock(x+8, dy, z+2, b2, meta2);
				array.setBlock(x+8, dy, z-6, b2, meta2);
				array.setBlock(x+8, dy, z-2, b2, meta2);
			}
		}

		for (int i = 1; i <= 3; i++) {
			int dy = y+i;
			int meta = i == 1 ? 0 : i == 2 ? 2 : 7;
			array.setBlock(x-8, dy, z-8, b, meta);
			array.setBlock(x+8, dy, z-8, b, meta);
			array.setBlock(x-8, dy, z+8, b, meta);
			array.setBlock(x+8, dy, z+8, b, meta);
		}

		array.setBlock(x-6, y+5, z-6, Blocks.glowstone);
		array.setBlock(x+6, y+5, z-6, Blocks.glowstone);
		array.setBlock(x+6, y+5, z+6, Blocks.glowstone);
		array.setBlock(x-6, y+5, z+6, Blocks.glowstone);

		array.setBlock(x, y+6, z-6, Blocks.diamond_block);
		array.setBlock(x, y+6, z+6, Blocks.diamond_block);
		array.setBlock(x+6, y+6, z, Blocks.diamond_block);
		array.setBlock(x-6, y+6, z, Blocks.diamond_block);

		array.remove(x, y, z);

		return array;
	}

	public static FilledBlockArray getPylonStructure(World world, int x, int y, int z, CrystalElement e) {
		FilledBlockArray array = new FilledBlockArray(world);
		Block b = ChromaBlocks.PYLONSTRUCT.getBlockInstance();
		for (int n = 0; n <= 9; n++) {
			int dy = y+n;
			Block b2 = n == 0 ? b : Blocks.air;
			for (int i = 2; i < 6; i++) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
				for (int k = 0; k <= 3; k++) {
					int dx = x+dir.offsetX*k;
					int dz = z+dir.offsetZ*k;
					array.setBlock(dx, dy, dz, b2, 0);
					if (dir.offsetX == 0) {
						array.setBlock(dx+dir.offsetZ, dy, dz, b2, 0);
						array.setBlock(dx-dir.offsetZ, dy, dz, b2, 0);
					}
					else if (dir.offsetZ == 0) {
						array.setBlock(dx, dy, dz+dir.offsetX, b2, 0);
						array.setBlock(dx, dy, dz-dir.offsetX, b2, 0);
					}
				}
			}
		}

		for (int i = 1; i <= 5; i++) {
			int dy = y+i;
			Block b2 = i < 5 ? b : ChromaBlocks.RUNE.getBlockInstance();
			int meta = (i == 2 || i == 3) ? 2 : (i == 4 ? 7 : 8);
			if (i == 5) //rune
				meta = e.ordinal();
			array.setBlock(x-3, dy, z+1, b2, meta);
			array.setBlock(x-3, dy, z-1, b2, meta);

			array.setBlock(x+3, dy, z+1, b2, meta);
			array.setBlock(x+3, dy, z-1, b2, meta);

			array.setBlock(x-1, dy, z+3, b2, meta);
			array.setBlock(x-1, dy, z-3, b2, meta);

			array.setBlock(x+1, dy, z+3, b2, meta);
			array.setBlock(x+1, dy, z-3, b2, meta);
		}

		for (int n = 1; n <= 7; n++) {
			int dy = y+n;
			for (int i = -1; i <= 1; i += 2) {
				int dx = x+i;
				for (int k = -1; k <= 1; k += 2) {
					int dz = z+k;
					int meta = n == 5 ? 3 : (n == 7 ? 5 : 2);
					array.setBlock(dx, dy, dz, b, meta);
				}
			}
		}

		array.setBlock(x-3, y+4, z, b, 4);
		array.setBlock(x+3, y+4, z, b, 4);
		array.setBlock(x, y+4, z-3, b, 4);
		array.setBlock(x, y+4, z+3, b, 4);


		array.setBlock(x-2, y+3, z+1, b, 1);
		array.setBlock(x-2, y+3, z-1, b, 1);

		array.setBlock(x+2, y+3, z+1, b, 1);
		array.setBlock(x+2, y+3, z-1, b, 1);

		array.setBlock(x-1, y+3, z+2, b, 1);
		array.setBlock(x-1, y+3, z-2, b, 1);

		array.setBlock(x+1, y+3, z+2, b, 1);
		array.setBlock(x+1, y+3, z-2, b, 1);

		array.remove(x, y+9, z);

		array.remove(x-3, y+6, z-1);
		array.remove(x-1, y+6, z-3);

		array.remove(x+3, y+6, z-1);
		array.remove(x+1, y+6, z-3);

		array.remove(x-3, y+6, z+1);
		array.remove(x-1, y+6, z+3);

		array.remove(x+3, y+6, z+1);
		array.remove(x+1, y+6, z+3);

		return array;
	}

	public static FilledBlockArray getRepeaterStructure(World world, int x, int y, int z, CrystalElement e) {
		FilledBlockArray array = new FilledBlockArray(world);
		Block b = ChromaBlocks.PYLONSTRUCT.getBlockInstance();
		array.setBlock(x, y, z, ChromaTiles.REPEATER.getBlock(), ChromaTiles.REPEATER.getBlockMetadata());
		array.setBlock(x, y-1, z, ChromaBlocks.RUNE.getBlockInstance(), e.ordinal());
		array.setBlock(x, y-2, z, b, 0);
		array.setBlock(x, y-3, z, b, 0);
		return array;
	}

	public static FilledBlockArray getCompoundRepeaterStructure(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);
		Block b = ChromaBlocks.PYLONSTRUCT.getBlockInstance();
		array.setBlock(x, y, z, ChromaTiles.COMPOUND.getBlock(), ChromaTiles.COMPOUND.getBlockMetadata());
		array.setBlock(x, y-1, z, b, 12);
		array.setBlock(x, y-2, z, b, 2);
		array.setBlock(x, y-3, z, b, 13);
		array.setBlock(x, y-4, z, b, 2);
		array.setBlock(x, y-5, z, b, 12);
		return array;
	}

}
