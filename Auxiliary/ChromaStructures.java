/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import java.util.ArrayList;
import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import Reika.ChromatiCraft.Block.BlockPylonStructure.StoneTypes;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield.BlockType;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.AOE.Defence.TileEntityMeteorTower;
import Reika.ChromatiCraft.TileEntity.Auxiliary.TileEntityPylonTurboCharger.Location;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalPylon;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityRelaySource;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.Registry.ReikaTreeHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.CarpenterBlockHandler;
import Reika.DragonAPI.ModRegistry.ModWoodList;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ChromaStructures {

	public static enum Structures {
		PYLON(true),
		CASTING1(false),
		CASTING2(false),
		CASTING3(false),
		RITUAL(false),
		RITUAL2(false),
		INFUSION(false),
		TREE(false),
		TREE_BOOSTED(false),
		REPEATER(true),
		COMPOUND(false),
		CAVERN(false),
		BURROW(true),
		OCEAN(false),
		DESERT(false),
		PORTAL(false),
		PERSONAL(true),
		BROADCAST(false),
		CLOAKTOWER(false),
		PROTECT(false),
		WEAKREPEATER(false),
		METEOR1(false),
		METEOR2(false),
		METEOR3(false),
		TELEGATE(false),
		RELAY(false),
		PYLONBROADCAST(true),
		PYLONTURBO(true);

		public final boolean requiresColor;

		private Structures(boolean c) {
			requiresColor = c;
		}

		public FilledBlockArray getArray(World world, int x, int y, int z, CrystalElement e) {
			switch(this) {
				case PYLON:
					return getPylonStructure(world, x, y, z, e);
				case CASTING1:
					return getCastingLevelOne(world, x, y, z);
				case CASTING2:
					return getCastingLevelTwo(world, x, y, z);
				case CASTING3:
					return getCastingLevelThree(world, x, y, z);
				case RITUAL:
					return getRitualStructure(world, x, y, z, false, false);
				case RITUAL2:
					return getRitualStructure(world, x, y, z, true, true);
				case INFUSION:
					return getInfusionStructure(world, x, y, z);
				case TREE:
					return getTreeStructure(world, x, y, z);
				case TREE_BOOSTED:
					return getBoostedTreeStructure(world, x, y, z);
				case REPEATER:
					return getRepeaterStructure(world, x, y, z, e);
				case COMPOUND:
					return getCompoundRepeaterStructure(world, x, y, z);
				case CAVERN:
					return getCavernStructure(world, x, y, z);
				case BURROW:
					return getBurrowStructure(world, x, y, z, e);
				case OCEAN:
					return getOceanStructure(world, x, y, z);
				case DESERT:
					return getDesertStructure(world, x, y, z);
				case PORTAL:
					return getPortalStructure(world, x, y, z, false);
				case PERSONAL:
					return getPersonalStructure(world, x, y, z, e);
				case BROADCAST:
					return getBroadcastStructure(world, x, y, z);
				case CLOAKTOWER:
					return getCloakingTower(world, x, y, z);
				case PROTECT:
					return getProtectionBeaconStructure(world, x, y, z);
				case WEAKREPEATER:
					return getWeakRepeaterStructure(world, x, y, z);
				case METEOR1:
					return getMeteorTowerStructure(world, x, y, z, 0);
				case METEOR2:
					return getMeteorTowerStructure(world, x, y, z, 1);
				case METEOR3:
					return getMeteorTowerStructure(world, x, y, z, 2);
				case TELEGATE:
					return getGateStructure(world, x, y, z);
				case RELAY:
					return getBoostedRelayStructure(world, x, y, z, false);
				case PYLONBROADCAST:
					return getPylonBroadcastStructure(world, x, y, z, e);
				case PYLONTURBO:
					return getPylonTurboStructure(world, x, y, z, e);
			}
			return null;
		}

		@SideOnly(Side.CLIENT)
		public FilledBlockArray getStructureForDisplay() {
			World w = Minecraft.getMinecraft().theWorld;
			switch(this) {
				case PYLON:
					return getPylonStructure(w, 0, -9, 0, CrystalElement.elements[(int)(System.currentTimeMillis()/4000)%16]);
				case CASTING1:
					return getCastingLevelOne(w, 0, 0, 0);
				case CASTING2:
					return getCastingLevelTwo(w, 0, 0, 0);
				case CASTING3:
					return getCastingLevelThree(w, 0, 0, 0);
				case RITUAL:
					return getRitualStructure(w, 0, 0, 0, false, false);
				case RITUAL2:
					return getRitualStructure(w, 0, 0, 0, true, true);
				case INFUSION:
					return getInfusionStructure(w, 0, 0, 0);
				case TREE:
					return getTreeStructure(w, 0, 0, 0);
				case TREE_BOOSTED:
					return getBoostedTreeStructure(w, 0, 0, 0);
				case REPEATER:
					return getRepeaterStructure(w, 0, 0, 0, CrystalElement.elements[(int)(System.currentTimeMillis()/4000)%16]);
				case COMPOUND:
					return getCompoundRepeaterStructure(w, 0, 0, 0);
				case CAVERN:
					return getCavernStructure(w, 0, 0, 0);
				case BURROW:
					return getBurrowStructure(w, 0, 0, 0, CrystalElement.elements[(int)(System.currentTimeMillis()/4000)%16]);
				case OCEAN:
					return getOceanStructure(w, 0, 0, 0);
				case DESERT:
					return getDesertStructure(w, 0, 0, 0);
				case PORTAL:
					return getPortalStructure(w, 0, 0, 0, true);
				case PERSONAL:
					return getPersonalStructure(w, 0, 0, 0, CrystalElement.elements[(int)(System.currentTimeMillis()/4000)%16]);
				case BROADCAST:
					return getBroadcastStructure(w, 0, 0, 0);
				case CLOAKTOWER:
					return getCloakingTower(w, 0, 0, 0);
				case PROTECT:
					return getProtectionBeaconStructure(w, 0, 0, 0);
				case WEAKREPEATER:
					return getWeakRepeaterStructure(w, 0, 0, 0);
				case METEOR1:
					return getMeteorTowerStructure(w, 0, 0, 0, 0);
				case METEOR2:
					return getMeteorTowerStructure(w, 0, 0, 0, 1);
				case METEOR3:
					return getMeteorTowerStructure(w, 0, 0, 0, 2);
				case TELEGATE:
					return getGateStructure(w, 0, 0, 0);
				case RELAY:
					return getBoostedRelayStructure(w, 0, 0, 0, true);
				case PYLONBROADCAST:
					return getPylonBroadcastStructure(w, 0, 0, 0, CrystalElement.elements[(int)(System.currentTimeMillis()/4000)%16]);
				case PYLONTURBO:
					return getPylonTurboStructure(w, 0, 0, 0, CrystalElement.elements[(int)(System.currentTimeMillis()/4000)%16]);
			}
			return null;
		}

		public String getDisplayName() {
			return StatCollector.translateToLocal("chromastruct."+this.name().toLowerCase(Locale.ENGLISH));
		}

		public boolean isNatural() {
			switch(this) {
				case PYLON:
				case CAVERN:
				case BURROW:
				case OCEAN:
				case DESERT:
					return true;
				default:
					return false;
			}
		}
	}

	public static FilledBlockArray getCloakingTower(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);

		Block b = ChromaBlocks.PYLONSTRUCT.getBlockInstance();

		for (int i = -2; i <= 2; i++) {
			int dx = x+i;
			for (int k = -2; k <= 2; k++) {
				int dz = z+k;
				for (int j = -4; j <= 5; j++) {
					int dy = y+j;
					ArrayList<Block> li = new ArrayList();
					if (ModList.CARPENTER.isLoaded()) {
						li.add(CarpenterBlockHandler.Blocks.BLOCK.getBlock());
						li.add(CarpenterBlockHandler.Blocks.SLOPE.getBlock());
						li.add(CarpenterBlockHandler.Blocks.FENCE.getBlock());
						li.add(CarpenterBlockHandler.Blocks.STAIRS.getBlock());
					}
					array.setEmpty(dx, dy, dz, false, false, li.toArray(new Block[li.size()]));

					if (Math.abs(i) == 2 && Math.abs(k) == 2 && j < 5) {
						int m = j == 4 ? 6 : 2;
						array.setBlock(dx, dy, dz, b, m);
					}
				}

				array.setBlock(dx, y-5, dz, b, 0);
			}
		}

		for (int i = -1; i <= 1; i++) {
			for (int k = -1; k <= 1; k++) {
				int dx = x+i;
				int dz = z+k;
				int m = Math.abs(i) == 1 && Math.abs(k) == 1 ? 0 : 12;
				array.setBlock(dx, y+5, dz, b, m);

				m = Math.abs(i) == 1 && Math.abs(k) == 1 ? 8 : 12;
				array.setBlock(dx, y+6, dz, b, m);
			}
		}

		for (int i = -1; i <= 1; i++) {
			array.setBlock(x+i, y-5, z+2, b, 12);
			array.setBlock(x+i, y-5, z-2, b, 12);
			array.setBlock(x+2, y-5, z+i, b, 12);
			array.setBlock(x-2, y-5, z+i, b, 12);

			array.setBlock(x+i, y+4, z+2, b, 1);
			array.setBlock(x+i, y+4, z-2, b, 1);
			array.setBlock(x+2, y+4, z+i, b, 1);
			array.setBlock(x-2, y+4, z+i, b, 1);

			int m = i == 0 ? 12 : 0;

			array.setBlock(x+i, y+5, z+2, b, m);
			array.setBlock(x+i, y+5, z-2, b, m);
			array.setBlock(x+2, y+5, z+i, b, m);
			array.setBlock(x-2, y+5, z+i, b, m);

			array.setBlock(x-2, y-2-i, z+i, b, 0);
			array.setBlock(x-2, y+1-i, z+i, b, 0);

			array.setBlock(x+2, y-2+i, z+i, b, 0);
			array.setBlock(x+2, y+1+i, z+i, b, 0);

			array.setBlock(x+i, y-2+i, z-2, b, 0);
			array.setBlock(x+i, y+1+i, z-2, b, 0);

			array.setBlock(x+i, y-2-i, z+2, b, 0);
			array.setBlock(x+i, y+1-i, z+2, b, 0);
		}

		array.setBlock(x, y, z, ChromaTiles.CLOAKING.getBlock(), ChromaTiles.CLOAKING.getBlockMetadata());

		return array;
	}

	public static FilledBlockArray getBroadcastStructure(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);

		Block b = ChromaBlocks.PYLONSTRUCT.getBlockInstance();

		x -= 6;
		y -= 23;
		z -= 6;

		array.setBlock(x+0, y+3, z+5, b, 10);
		array.setBlock(x+0, y+3, z+6, b, 10);
		array.setBlock(x+0, y+3, z+7, b, 10);
		array.setBlock(x+1, y+3, z+5, b, 10);
		array.setBlock(x+1, y+3, z+6, b, 10);
		array.setBlock(x+1, y+3, z+7, b, 10);
		array.setBlock(x+2, y+3, z+4, b, 0);
		array.setBlock(x+2, y+3, z+5, b, 10);
		array.setBlock(x+2, y+3, z+6, b, 10);
		array.setBlock(x+2, y+3, z+7, b, 10);
		array.setBlock(x+2, y+3, z+8, b, 0);
		array.setBlock(x+3, y+2, z+5, b, 0);
		array.setBlock(x+3, y+2, z+6, b, 0);
		array.setBlock(x+3, y+2, z+7, b, 0);
		array.setBlock(x+3, y+3, z+3, b, 0);
		array.setBlock(x+3, y+3, z+4, b, 0);
		array.setBlock(x+3, y+3, z+5, b, 10);
		array.setBlock(x+3, y+3, z+6, b, 10);
		array.setBlock(x+3, y+3, z+7, b, 10);
		array.setBlock(x+3, y+3, z+8, b, 0);
		array.setBlock(x+3, y+3, z+9, b, 0);
		array.setBlock(x+3, y+4, z+4, b, 0);
		array.setBlock(x+3, y+4, z+8, b, 0);
		array.setBlock(x+3, y+5, z+4, b, 2);
		array.setBlock(x+3, y+5, z+8, b, 2);
		array.setBlock(x+3, y+6, z+4, b, 2);
		array.setBlock(x+3, y+6, z+8, b, 2);
		array.setBlock(x+3, y+7, z+4, b, 2);
		array.setBlock(x+3, y+7, z+8, b, 2);
		array.setBlock(x+3, y+8, z+4, b, 12);
		array.setBlock(x+3, y+8, z+5, b, 12);
		array.setBlock(x+3, y+8, z+6, b, 12);
		array.setBlock(x+3, y+8, z+7, b, 12);
		array.setBlock(x+3, y+8, z+8, b, 12);
		array.setBlock(x+3, y+9, z+4, b, 6);
		array.setBlock(x+3, y+9, z+5, b, 12);
		array.setBlock(x+3, y+9, z+6, b, 0);
		array.setBlock(x+3, y+9, z+7, b, 12);
		array.setBlock(x+3, y+9, z+8, b, 6);
		array.setBlock(x+3, y+10, z+5, b, 2);
		array.setBlock(x+3, y+10, z+6, b, 0);
		array.setBlock(x+3, y+10, z+7, b, 2);
		array.setBlock(x+3, y+11, z+5, b, 2);
		array.setBlock(x+3, y+11, z+6, b, 13);
		array.setBlock(x+3, y+11, z+7, b, 2);
		array.setBlock(x+3, y+12, z+5, b, 6);
		array.setBlock(x+3, y+12, z+6, b, 12);
		array.setBlock(x+3, y+12, z+7, b, 6);
		array.setBlock(x+3, y+13, z+6, b, 2);
		array.setBlock(x+3, y+14, z+6, b, 8);
		array.setBlock(x+4, y+1, z+5, b, 0);
		array.setBlock(x+4, y+1, z+6, b, 0);
		array.setBlock(x+4, y+1, z+7, b, 0);
		array.setBlock(x+4, y+2, z+4, b, 0);
		array.setBlock(x+4, y+2, z+5, b, 0);
		array.setBlock(x+4, y+2, z+6, b, 0);
		array.setBlock(x+4, y+2, z+7, b, 0);
		array.setBlock(x+4, y+2, z+8, b, 0);
		array.setBlock(x+4, y+3, z+2, b, 0);
		array.setBlock(x+4, y+3, z+3, b, 0);
		array.setBlock(x+4, y+3, z+4, b, 0);
		array.setBlock(x+4, y+3, z+5, b, 0);
		array.setBlock(x+4, y+3, z+6, b, 0);
		array.setBlock(x+4, y+3, z+7, b, 0);
		array.setBlock(x+4, y+3, z+8, b, 0);
		array.setBlock(x+4, y+3, z+9, b, 0);
		array.setBlock(x+4, y+3, z+10, b, 0);
		array.setBlock(x+4, y+4, z+3, b, 0);
		array.setBlock(x+4, y+4, z+4, b, 0);
		array.setBlock(x+4, y+4, z+5, b, 1);
		array.setBlock(x+4, y+4, z+6, b, 1);
		array.setBlock(x+4, y+4, z+7, b, 1);
		array.setBlock(x+4, y+4, z+8, b, 0);
		array.setBlock(x+4, y+4, z+9, b, 0);
		array.setBlock(x+4, y+5, z+3, b, 2);
		array.setBlock(x+4, y+5, z+4, b, 0);
		array.setBlock(x+4, y+5, z+8, b, 0);
		array.setBlock(x+4, y+5, z+9, b, 2);
		array.setBlock(x+4, y+6, z+3, b, 2);
		array.setBlock(x+4, y+6, z+4, b, 0);
		array.setBlock(x+4, y+6, z+8, b, 0);
		array.setBlock(x+4, y+6, z+9, b, 2);
		array.setBlock(x+4, y+7, z+3, b, 2);
		array.setBlock(x+4, y+7, z+4, b, 0);
		array.setBlock(x+4, y+7, z+5, b, 1);
		array.setBlock(x+4, y+7, z+6, b, 1);
		array.setBlock(x+4, y+7, z+7, b, 1);
		array.setBlock(x+4, y+7, z+8, b, 0);
		array.setBlock(x+4, y+7, z+9, b, 2);
		array.setBlock(x+4, y+8, z+3, b, 12);
		array.setBlock(x+4, y+8, z+4, b, 0);
		array.setBlock(x+4, y+8, z+5, b, 0);
		array.setBlock(x+4, y+8, z+6, b, 0);
		array.setBlock(x+4, y+8, z+7, b, 0);
		array.setBlock(x+4, y+8, z+8, b, 0);
		array.setBlock(x+4, y+8, z+9, b, 12);
		array.setBlock(x+4, y+9, z+3, b, 6);
		array.setBlock(x+4, y+9, z+4, b, 0);
		array.setBlock(x+4, y+9, z+5, b, 0);
		array.setBlock(x+4, y+9, z+6, b, 0);
		array.setBlock(x+4, y+9, z+7, b, 0);
		array.setBlock(x+4, y+9, z+8, b, 0);
		array.setBlock(x+4, y+9, z+9, b, 6);
		array.setBlock(x+4, y+10, z+4, b, 2);
		array.setBlock(x+4, y+10, z+5, b, 0);
		array.setBlock(x+4, y+10, z+6, b, 0);
		array.setBlock(x+4, y+10, z+7, b, 0);
		array.setBlock(x+4, y+10, z+8, b, 2);
		array.setBlock(x+4, y+11, z+4, b, 2);
		array.setBlock(x+4, y+11, z+5, b, 0);
		array.setBlock(x+4, y+11, z+6, b, 0);
		array.setBlock(x+4, y+11, z+7, b, 0);
		array.setBlock(x+4, y+11, z+8, b, 2);
		array.setBlock(x+4, y+12, z+4, b, 2);
		array.setBlock(x+4, y+12, z+5, b, 0);
		array.setBlock(x+4, y+12, z+6, b, 0);
		array.setBlock(x+4, y+12, z+7, b, 0);
		array.setBlock(x+4, y+12, z+8, b, 2);
		array.setBlock(x+4, y+13, z+4, b, 2);
		array.setBlock(x+4, y+13, z+5, b, 0);
		array.setBlock(x+4, y+13, z+6, b, 0);
		array.setBlock(x+4, y+13, z+7, b, 0);
		array.setBlock(x+4, y+13, z+8, b, 2);
		array.setBlock(x+4, y+14, z+4, b, 2);
		array.setBlock(x+4, y+14, z+5, b, 0);
		array.setBlock(x+4, y+14, z+6, b, 0);
		array.setBlock(x+4, y+14, z+7, b, 0);
		array.setBlock(x+4, y+14, z+8, b, 2);
		array.setBlock(x+4, y+15, z+4, b, 6);
		array.setBlock(x+4, y+15, z+5, b, 12);
		array.setBlock(x+4, y+15, z+6, b, 12);
		array.setBlock(x+4, y+15, z+7, b, 12);
		array.setBlock(x+4, y+15, z+8, b, 6);
		array.setBlock(x+4, y+16, z+5, b, 2);
		array.setBlock(x+4, y+16, z+6, b, 0);
		array.setBlock(x+4, y+16, z+7, b, 2);
		array.setBlock(x+4, y+17, z+5, b, 2);
		array.setBlock(x+4, y+17, z+6, b, 14);
		array.setBlock(x+4, y+17, z+7, b, 2);
		array.setBlock(x+4, y+18, z+5, b, 6);
		array.setBlock(x+4, y+18, z+6, b, 12);
		array.setBlock(x+4, y+18, z+7, b, 6);
		array.setBlock(x+4, y+19, z+6, b, 2);
		array.setBlock(x+4, y+20, z+6, b, 7);
		array.setBlock(x+5, y+0, z+5, b, 0);
		array.setBlock(x+5, y+0, z+6, b, 0);
		array.setBlock(x+5, y+0, z+7, b, 0);
		array.setBlock(x+5, y+1, z+4, b, 0);
		array.setBlock(x+5, y+1, z+5, b, 0);
		array.setBlock(x+5, y+1, z+6, b, 0);
		array.setBlock(x+5, y+1, z+7, b, 0);
		array.setBlock(x+5, y+1, z+8, b, 0);
		array.setBlock(x+5, y+2, z+3, b, 0);
		array.setBlock(x+5, y+2, z+4, b, 0);
		array.setBlock(x+5, y+2, z+5, b, 0);
		array.setBlock(x+5, y+2, z+6, b, 0);
		array.setBlock(x+5, y+2, z+7, b, 0);
		array.setBlock(x+5, y+2, z+8, b, 0);
		array.setBlock(x+5, y+2, z+9, b, 0);
		array.setBlock(x+5, y+3, z+0, b, 11);
		array.setBlock(x+5, y+3, z+1, b, 11);
		array.setBlock(x+5, y+3, z+2, b, 11);
		array.setBlock(x+5, y+3, z+3, b, 11);
		array.setBlock(x+5, y+3, z+4, b, 0);
		array.setBlock(x+5, y+3, z+5, b, 0);
		array.setBlock(x+5, y+3, z+6, b, 0);
		array.setBlock(x+5, y+3, z+7, b, 0);
		array.setBlock(x+5, y+3, z+8, b, 0);
		array.setBlock(x+5, y+3, z+9, b, 11);
		array.setBlock(x+5, y+3, z+10, b, 11);
		array.setBlock(x+5, y+3, z+11, b, 11);
		array.setBlock(x+5, y+3, z+12, b, 11);
		array.setBlock(x+5, y+4, z+4, b, 1);
		array.setBlock(x+5, y+4, z+5, b, 0);
		array.setBlock(x+5, y+4, z+6, b, 0);
		array.setBlock(x+5, y+4, z+7, b, 0);
		array.setBlock(x+5, y+4, z+8, b, 1);
		array.setBlock(x+5, y+5, z+5, b, 0);
		array.setBlock(x+5, y+5, z+6, b, 0);
		array.setBlock(x+5, y+5, z+7, b, 0);
		array.setBlock(x+5, y+6, z+5, b, 0);
		array.setBlock(x+5, y+6, z+7, b, 0);
		array.setBlock(x+5, y+7, z+4, b, 1);
		array.setBlock(x+5, y+7, z+5, b, 0);
		array.setBlock(x+5, y+7, z+6, b, 0);
		array.setBlock(x+5, y+7, z+7, b, 0);
		array.setBlock(x+5, y+7, z+8, b, 1);
		array.setBlock(x+5, y+8, z+3, b, 12);
		array.setBlock(x+5, y+8, z+4, b, 0);
		array.setBlock(x+5, y+8, z+5, b, 0);
		array.setBlock(x+5, y+8, z+6, b, 0);
		array.setBlock(x+5, y+8, z+7, b, 0);
		array.setBlock(x+5, y+8, z+8, b, 0);
		array.setBlock(x+5, y+8, z+9, b, 12);
		array.setBlock(x+5, y+9, z+3, b, 12);
		array.setBlock(x+5, y+9, z+4, b, 0);
		array.setBlock(x+5, y+9, z+5, b, 0);
		array.setBlock(x+5, y+9, z+6, b, 0);
		array.setBlock(x+5, y+9, z+7, b, 0);
		array.setBlock(x+5, y+9, z+8, b, 0);
		array.setBlock(x+5, y+9, z+9, b, 12);
		array.setBlock(x+5, y+10, z+3, b, 2);
		array.setBlock(x+5, y+10, z+4, b, 0);
		array.setBlock(x+5, y+10, z+5, b, 0);
		array.setBlock(x+5, y+10, z+6, b, 0);
		array.setBlock(x+5, y+10, z+7, b, 0);
		array.setBlock(x+5, y+10, z+8, b, 0);
		array.setBlock(x+5, y+10, z+9, b, 2);
		array.setBlock(x+5, y+11, z+3, b, 2);
		array.setBlock(x+5, y+11, z+4, b, 0);
		array.setBlock(x+5, y+11, z+5, b, 0);
		array.setBlock(x+5, y+11, z+6, b, 0);
		array.setBlock(x+5, y+11, z+7, b, 0);
		array.setBlock(x+5, y+11, z+8, b, 0);
		array.setBlock(x+5, y+11, z+9, b, 2);
		array.setBlock(x+5, y+12, z+3, b, 6);
		array.setBlock(x+5, y+12, z+4, b, 0);
		array.setBlock(x+5, y+12, z+5, b, 0);
		array.setBlock(x+5, y+12, z+6, b, 0);
		array.setBlock(x+5, y+12, z+7, b, 0);
		array.setBlock(x+5, y+12, z+8, b, 0);
		array.setBlock(x+5, y+12, z+9, b, 6);
		array.setBlock(x+5, y+13, z+4, b, 0);
		array.setBlock(x+5, y+13, z+5, b, 0);
		array.setBlock(x+5, y+13, z+6, b, 0);
		array.setBlock(x+5, y+13, z+7, b, 0);
		array.setBlock(x+5, y+13, z+8, b, 0);
		array.setBlock(x+5, y+14, z+4, b, 0);
		array.setBlock(x+5, y+14, z+5, b, 0);
		array.setBlock(x+5, y+14, z+6, b, 0);
		array.setBlock(x+5, y+14, z+7, b, 0);
		array.setBlock(x+5, y+14, z+8, b, 0);
		array.setBlock(x+5, y+15, z+4, b, 12);
		array.setBlock(x+5, y+15, z+5, b, 0);
		array.setBlock(x+5, y+15, z+6, b, 0);
		array.setBlock(x+5, y+15, z+7, b, 0);
		array.setBlock(x+5, y+15, z+8, b, 12);
		array.setBlock(x+5, y+16, z+4, b, 2);
		array.setBlock(x+5, y+16, z+5, b, 0);
		array.setBlock(x+5, y+16, z+6, b, 0);
		array.setBlock(x+5, y+16, z+7, b, 0);
		array.setBlock(x+5, y+16, z+8, b, 2);
		array.setBlock(x+5, y+17, z+4, b, 2);
		array.setBlock(x+5, y+17, z+5, b, 0);
		array.setBlock(x+5, y+17, z+6, b, 0);
		array.setBlock(x+5, y+17, z+7, b, 0);
		array.setBlock(x+5, y+17, z+8, b, 2);
		array.setBlock(x+5, y+18, z+4, b, 6);
		array.setBlock(x+5, y+18, z+5, b, 0);
		array.setBlock(x+5, y+18, z+6, b, 0);
		array.setBlock(x+5, y+18, z+7, b, 0);
		array.setBlock(x+5, y+18, z+8, b, 6);
		array.setBlock(x+5, y+19, z+5, b, 2);
		array.setBlock(x+5, y+19, z+6, b, 0);
		array.setBlock(x+5, y+19, z+7, b, 2);
		array.setBlock(x+5, y+20, z+5, b, 2);
		array.setBlock(x+5, y+20, z+6, b, 0);
		array.setBlock(x+5, y+20, z+7, b, 2);
		array.setBlock(x+5, y+21, z+5, b, 2);
		array.setBlock(x+5, y+21, z+6, b, 0);
		array.setBlock(x+5, y+21, z+7, b, 2);
		array.setBlock(x+5, y+22, z+5, b, 2);
		array.setBlock(x+5, y+22, z+6, b, 12);
		array.setBlock(x+5, y+22, z+7, b, 2);
		array.setBlock(x+5, y+23, z+5, b, 2);
		array.setBlock(x+5, y+23, z+7, b, 2);
		array.setBlock(x+5, y+24, z+5, b, 15);
		array.setBlock(x+5, y+24, z+6, b, 15);
		array.setBlock(x+5, y+24, z+7, b, 15);
		array.setBlock(x+5, y+25, z+6, b, 0);
		array.setBlock(x+6, y+0, z+5, b, 0);
		array.setBlock(x+6, y+0, z+7, b, 0);
		array.setBlock(x+6, y+1, z+4, b, 0);
		array.setBlock(x+6, y+1, z+5, b, 0);
		array.setBlock(x+6, y+1, z+7, b, 0);
		array.setBlock(x+6, y+1, z+8, b, 0);
		array.setBlock(x+6, y+2, z+3, b, 0);
		array.setBlock(x+6, y+2, z+4, b, 0);
		array.setBlock(x+6, y+2, z+5, b, 0);
		array.setBlock(x+6, y+2, z+7, b, 0);
		array.setBlock(x+6, y+2, z+8, b, 0);
		array.setBlock(x+6, y+2, z+9, b, 0);
		array.setBlock(x+6, y+3, z+0, b, 11);
		array.setBlock(x+6, y+3, z+1, b, 11);
		array.setBlock(x+6, y+3, z+2, b, 11);
		array.setBlock(x+6, y+3, z+3, b, 11);
		array.setBlock(x+6, y+3, z+4, b, 0);
		array.setBlock(x+6, y+3, z+5, b, 0);
		array.setBlock(x+6, y+3, z+7, b, 0);
		array.setBlock(x+6, y+3, z+8, b, 0);
		array.setBlock(x+6, y+3, z+9, b, 11);
		array.setBlock(x+6, y+3, z+10, b, 11);
		array.setBlock(x+6, y+3, z+11, b, 11);
		array.setBlock(x+6, y+3, z+12, b, 11);
		array.setBlock(x+6, y+4, z+4, b, 1);
		array.setBlock(x+6, y+4, z+5, b, 0);
		array.setBlock(x+6, y+4, z+7, b, 0);
		array.setBlock(x+6, y+4, z+8, b, 1);
		array.setBlock(x+6, y+5, z+5, b, 0);
		array.setBlock(x+6, y+5, z+7, b, 0);
		array.setBlock(x+6, y+7, z+4, b, 1);
		array.setBlock(x+6, y+7, z+5, b, 0);
		array.setBlock(x+6, y+7, z+7, b, 0);
		array.setBlock(x+6, y+7, z+8, b, 1);
		array.setBlock(x+6, y+8, z+3, b, 12);
		array.setBlock(x+6, y+8, z+4, b, 0);
		array.setBlock(x+6, y+8, z+5, b, 0);
		array.setBlock(x+6, y+8, z+7, b, 0);
		array.setBlock(x+6, y+8, z+8, b, 0);
		array.setBlock(x+6, y+8, z+9, b, 12);
		array.setBlock(x+6, y+9, z+3, b, 0);
		array.setBlock(x+6, y+9, z+4, b, 0);
		array.setBlock(x+6, y+9, z+5, b, 0);
		array.setBlock(x+6, y+9, z+7, b, 0);
		array.setBlock(x+6, y+9, z+8, b, 0);
		array.setBlock(x+6, y+9, z+9, b, 0);
		array.setBlock(x+6, y+10, z+3, b, 0);
		array.setBlock(x+6, y+10, z+4, b, 0);
		array.setBlock(x+6, y+10, z+5, b, 0);
		array.setBlock(x+6, y+10, z+7, b, 0);
		array.setBlock(x+6, y+10, z+8, b, 0);
		array.setBlock(x+6, y+10, z+9, b, 0);
		array.setBlock(x+6, y+11, z+3, b, 13);
		array.setBlock(x+6, y+11, z+4, b, 0);
		array.setBlock(x+6, y+11, z+5, b, 0);
		array.setBlock(x+6, y+11, z+7, b, 0);
		array.setBlock(x+6, y+11, z+8, b, 0);
		array.setBlock(x+6, y+11, z+9, b, 13);
		array.setBlock(x+6, y+12, z+3, b, 12);
		array.setBlock(x+6, y+12, z+4, b, 0);
		array.setBlock(x+6, y+12, z+5, b, 0);
		array.setBlock(x+6, y+12, z+7, b, 0);
		array.setBlock(x+6, y+12, z+8, b, 0);
		array.setBlock(x+6, y+12, z+9, b, 12);
		array.setBlock(x+6, y+13, z+3, b, 2);
		array.setBlock(x+6, y+13, z+4, b, 0);
		array.setBlock(x+6, y+13, z+5, b, 0);
		array.setBlock(x+6, y+13, z+7, b, 0);
		array.setBlock(x+6, y+13, z+8, b, 0);
		array.setBlock(x+6, y+13, z+9, b, 2);
		array.setBlock(x+6, y+14, z+3, b, 8);
		array.setBlock(x+6, y+14, z+4, b, 0);
		array.setBlock(x+6, y+14, z+5, b, 0);
		array.setBlock(x+6, y+14, z+7, b, 0);
		array.setBlock(x+6, y+14, z+8, b, 0);
		array.setBlock(x+6, y+14, z+9, b, 8);
		array.setBlock(x+6, y+15, z+4, b, 12);
		array.setBlock(x+6, y+15, z+5, b, 0);
		array.setBlock(x+6, y+15, z+7, b, 0);
		array.setBlock(x+6, y+15, z+8, b, 12);
		array.setBlock(x+6, y+16, z+4, b, 0);
		array.setBlock(x+6, y+16, z+5, b, 0);
		array.setBlock(x+6, y+16, z+7, b, 0);
		array.setBlock(x+6, y+16, z+8, b, 0);
		array.setBlock(x+6, y+17, z+4, b, 14);
		array.setBlock(x+6, y+17, z+5, b, 0);
		array.setBlock(x+6, y+17, z+7, b, 0);
		array.setBlock(x+6, y+17, z+8, b, 14);
		array.setBlock(x+6, y+18, z+4, b, 12);
		array.setBlock(x+6, y+18, z+5, b, 0);
		array.setBlock(x+6, y+18, z+7, b, 0);
		array.setBlock(x+6, y+18, z+8, b, 12);
		array.setBlock(x+6, y+19, z+4, b, 2);
		array.setBlock(x+6, y+19, z+5, b, 0);
		array.setBlock(x+6, y+19, z+7, b, 0);
		array.setBlock(x+6, y+19, z+8, b, 2);
		array.setBlock(x+6, y+20, z+4, b, 7);
		array.setBlock(x+6, y+20, z+5, b, 0);
		array.setBlock(x+6, y+20, z+7, b, 0);
		array.setBlock(x+6, y+20, z+8, b, 7);
		array.setBlock(x+6, y+21, z+5, b, 0);
		array.setBlock(x+6, y+21, z+7, b, 0);
		array.setBlock(x+6, y+22, z+5, b, 12);
		array.setBlock(x+6, y+22, z+7, b, 12);
		array.setBlock(x+6, y+24, z+5, b, 15);
		array.setBlock(x+6, y+24, z+7, b, 15);
		array.setBlock(x+6, y+25, z+5, b, 0);
		array.setBlock(x+6, y+25, z+7, b, 0);
		array.setBlock(x+7, y+0, z+5, b, 0);
		array.setBlock(x+7, y+0, z+6, b, 0);
		array.setBlock(x+7, y+0, z+7, b, 0);
		array.setBlock(x+7, y+1, z+4, b, 0);
		array.setBlock(x+7, y+1, z+5, b, 0);
		array.setBlock(x+7, y+1, z+6, b, 0);
		array.setBlock(x+7, y+1, z+7, b, 0);
		array.setBlock(x+7, y+1, z+8, b, 0);
		array.setBlock(x+7, y+2, z+3, b, 0);
		array.setBlock(x+7, y+2, z+4, b, 0);
		array.setBlock(x+7, y+2, z+5, b, 0);
		array.setBlock(x+7, y+2, z+6, b, 0);
		array.setBlock(x+7, y+2, z+7, b, 0);
		array.setBlock(x+7, y+2, z+8, b, 0);
		array.setBlock(x+7, y+2, z+9, b, 0);
		array.setBlock(x+7, y+3, z+0, b, 11);
		array.setBlock(x+7, y+3, z+1, b, 11);
		array.setBlock(x+7, y+3, z+2, b, 11);
		array.setBlock(x+7, y+3, z+3, b, 11);
		array.setBlock(x+7, y+3, z+4, b, 0);
		array.setBlock(x+7, y+3, z+5, b, 0);
		array.setBlock(x+7, y+3, z+6, b, 0);
		array.setBlock(x+7, y+3, z+7, b, 0);
		array.setBlock(x+7, y+3, z+8, b, 0);
		array.setBlock(x+7, y+3, z+9, b, 11);
		array.setBlock(x+7, y+3, z+10, b, 11);
		array.setBlock(x+7, y+3, z+11, b, 11);
		array.setBlock(x+7, y+3, z+12, b, 11);
		array.setBlock(x+7, y+4, z+4, b, 1);
		array.setBlock(x+7, y+4, z+5, b, 0);
		array.setBlock(x+7, y+4, z+6, b, 0);
		array.setBlock(x+7, y+4, z+7, b, 0);
		array.setBlock(x+7, y+4, z+8, b, 1);
		array.setBlock(x+7, y+5, z+5, b, 0);
		array.setBlock(x+7, y+5, z+6, b, 0);
		array.setBlock(x+7, y+5, z+7, b, 0);
		array.setBlock(x+7, y+6, z+5, b, 0);
		array.setBlock(x+7, y+6, z+7, b, 0);
		array.setBlock(x+7, y+7, z+4, b, 1);
		array.setBlock(x+7, y+7, z+5, b, 0);
		array.setBlock(x+7, y+7, z+6, b, 0);
		array.setBlock(x+7, y+7, z+7, b, 0);
		array.setBlock(x+7, y+7, z+8, b, 1);
		array.setBlock(x+7, y+8, z+3, b, 12);
		array.setBlock(x+7, y+8, z+4, b, 0);
		array.setBlock(x+7, y+8, z+5, b, 0);
		array.setBlock(x+7, y+8, z+6, b, 0);
		array.setBlock(x+7, y+8, z+7, b, 0);
		array.setBlock(x+7, y+8, z+8, b, 0);
		array.setBlock(x+7, y+8, z+9, b, 12);
		array.setBlock(x+7, y+9, z+3, b, 12);
		array.setBlock(x+7, y+9, z+4, b, 0);
		array.setBlock(x+7, y+9, z+5, b, 0);
		array.setBlock(x+7, y+9, z+6, b, 0);
		array.setBlock(x+7, y+9, z+7, b, 0);
		array.setBlock(x+7, y+9, z+8, b, 0);
		array.setBlock(x+7, y+9, z+9, b, 12);
		array.setBlock(x+7, y+10, z+3, b, 2);
		array.setBlock(x+7, y+10, z+4, b, 0);
		array.setBlock(x+7, y+10, z+5, b, 0);
		array.setBlock(x+7, y+10, z+6, b, 0);
		array.setBlock(x+7, y+10, z+7, b, 0);
		array.setBlock(x+7, y+10, z+8, b, 0);
		array.setBlock(x+7, y+10, z+9, b, 2);
		array.setBlock(x+7, y+11, z+3, b, 2);
		array.setBlock(x+7, y+11, z+4, b, 0);
		array.setBlock(x+7, y+11, z+5, b, 0);
		array.setBlock(x+7, y+11, z+6, b, 0);
		array.setBlock(x+7, y+11, z+7, b, 0);
		array.setBlock(x+7, y+11, z+8, b, 0);
		array.setBlock(x+7, y+11, z+9, b, 2);
		array.setBlock(x+7, y+12, z+3, b, 6);
		array.setBlock(x+7, y+12, z+4, b, 0);
		array.setBlock(x+7, y+12, z+5, b, 0);
		array.setBlock(x+7, y+12, z+6, b, 0);
		array.setBlock(x+7, y+12, z+7, b, 0);
		array.setBlock(x+7, y+12, z+8, b, 0);
		array.setBlock(x+7, y+12, z+9, b, 6);
		array.setBlock(x+7, y+13, z+4, b, 0);
		array.setBlock(x+7, y+13, z+5, b, 0);
		array.setBlock(x+7, y+13, z+6, b, 0);
		array.setBlock(x+7, y+13, z+7, b, 0);
		array.setBlock(x+7, y+13, z+8, b, 0);
		array.setBlock(x+7, y+14, z+4, b, 0);
		array.setBlock(x+7, y+14, z+5, b, 0);
		array.setBlock(x+7, y+14, z+6, b, 0);
		array.setBlock(x+7, y+14, z+7, b, 0);
		array.setBlock(x+7, y+14, z+8, b, 0);
		array.setBlock(x+7, y+15, z+4, b, 12);
		array.setBlock(x+7, y+15, z+5, b, 0);
		array.setBlock(x+7, y+15, z+6, b, 0);
		array.setBlock(x+7, y+15, z+7, b, 0);
		array.setBlock(x+7, y+15, z+8, b, 12);
		array.setBlock(x+7, y+16, z+4, b, 2);
		array.setBlock(x+7, y+16, z+5, b, 0);
		array.setBlock(x+7, y+16, z+6, b, 0);
		array.setBlock(x+7, y+16, z+7, b, 0);
		array.setBlock(x+7, y+16, z+8, b, 2);
		array.setBlock(x+7, y+17, z+4, b, 2);
		array.setBlock(x+7, y+17, z+5, b, 0);
		array.setBlock(x+7, y+17, z+6, b, 0);
		array.setBlock(x+7, y+17, z+7, b, 0);
		array.setBlock(x+7, y+17, z+8, b, 2);
		array.setBlock(x+7, y+18, z+4, b, 6);
		array.setBlock(x+7, y+18, z+5, b, 0);
		array.setBlock(x+7, y+18, z+6, b, 0);
		array.setBlock(x+7, y+18, z+7, b, 0);
		array.setBlock(x+7, y+18, z+8, b, 6);
		array.setBlock(x+7, y+19, z+5, b, 2);
		array.setBlock(x+7, y+19, z+6, b, 0);
		array.setBlock(x+7, y+19, z+7, b, 2);
		array.setBlock(x+7, y+20, z+5, b, 2);
		array.setBlock(x+7, y+20, z+6, b, 0);
		array.setBlock(x+7, y+20, z+7, b, 2);
		array.setBlock(x+7, y+21, z+5, b, 2);
		array.setBlock(x+7, y+21, z+6, b, 0);
		array.setBlock(x+7, y+21, z+7, b, 2);
		array.setBlock(x+7, y+22, z+5, b, 2);
		array.setBlock(x+7, y+22, z+6, b, 12);
		array.setBlock(x+7, y+22, z+7, b, 2);
		array.setBlock(x+7, y+23, z+5, b, 2);
		array.setBlock(x+7, y+23, z+7, b, 2);
		array.setBlock(x+7, y+24, z+5, b, 15);
		array.setBlock(x+7, y+24, z+6, b, 15);
		array.setBlock(x+7, y+24, z+7, b, 15);
		array.setBlock(x+7, y+25, z+6, b, 0);
		array.setBlock(x+8, y+1, z+5, b, 0);
		array.setBlock(x+8, y+1, z+6, b, 0);
		array.setBlock(x+8, y+1, z+7, b, 0);
		array.setBlock(x+8, y+2, z+4, b, 0);
		array.setBlock(x+8, y+2, z+5, b, 0);
		array.setBlock(x+8, y+2, z+6, b, 0);
		array.setBlock(x+8, y+2, z+7, b, 0);
		array.setBlock(x+8, y+2, z+8, b, 0);
		array.setBlock(x+8, y+3, z+2, b, 0);
		array.setBlock(x+8, y+3, z+3, b, 0);
		array.setBlock(x+8, y+3, z+4, b, 0);
		array.setBlock(x+8, y+3, z+5, b, 0);
		array.setBlock(x+8, y+3, z+6, b, 0);
		array.setBlock(x+8, y+3, z+7, b, 0);
		array.setBlock(x+8, y+3, z+8, b, 0);
		array.setBlock(x+8, y+3, z+9, b, 0);
		array.setBlock(x+8, y+3, z+10, b, 0);
		array.setBlock(x+8, y+4, z+3, b, 0);
		array.setBlock(x+8, y+4, z+4, b, 0);
		array.setBlock(x+8, y+4, z+5, b, 1);
		array.setBlock(x+8, y+4, z+6, b, 1);
		array.setBlock(x+8, y+4, z+7, b, 1);
		array.setBlock(x+8, y+4, z+8, b, 0);
		array.setBlock(x+8, y+4, z+9, b, 0);
		array.setBlock(x+8, y+5, z+3, b, 2);
		array.setBlock(x+8, y+5, z+4, b, 0);
		array.setBlock(x+8, y+5, z+8, b, 0);
		array.setBlock(x+8, y+5, z+9, b, 2);
		array.setBlock(x+8, y+6, z+3, b, 2);
		array.setBlock(x+8, y+6, z+4, b, 0);
		array.setBlock(x+8, y+6, z+8, b, 0);
		array.setBlock(x+8, y+6, z+9, b, 2);
		array.setBlock(x+8, y+7, z+3, b, 2);
		array.setBlock(x+8, y+7, z+4, b, 0);
		array.setBlock(x+8, y+7, z+5, b, 1);
		array.setBlock(x+8, y+7, z+6, b, 1);
		array.setBlock(x+8, y+7, z+7, b, 1);
		array.setBlock(x+8, y+7, z+8, b, 0);
		array.setBlock(x+8, y+7, z+9, b, 2);
		array.setBlock(x+8, y+8, z+3, b, 12);
		array.setBlock(x+8, y+8, z+4, b, 0);
		array.setBlock(x+8, y+8, z+5, b, 0);
		array.setBlock(x+8, y+8, z+6, b, 0);
		array.setBlock(x+8, y+8, z+7, b, 0);
		array.setBlock(x+8, y+8, z+8, b, 0);
		array.setBlock(x+8, y+8, z+9, b, 12);
		array.setBlock(x+8, y+9, z+3, b, 6);
		array.setBlock(x+8, y+9, z+4, b, 0);
		array.setBlock(x+8, y+9, z+5, b, 0);
		array.setBlock(x+8, y+9, z+6, b, 0);
		array.setBlock(x+8, y+9, z+7, b, 0);
		array.setBlock(x+8, y+9, z+8, b, 0);
		array.setBlock(x+8, y+9, z+9, b, 6);
		array.setBlock(x+8, y+10, z+4, b, 2);
		array.setBlock(x+8, y+10, z+5, b, 0);
		array.setBlock(x+8, y+10, z+6, b, 0);
		array.setBlock(x+8, y+10, z+7, b, 0);
		array.setBlock(x+8, y+10, z+8, b, 2);
		array.setBlock(x+8, y+11, z+4, b, 2);
		array.setBlock(x+8, y+11, z+5, b, 0);
		array.setBlock(x+8, y+11, z+6, b, 0);
		array.setBlock(x+8, y+11, z+7, b, 0);
		array.setBlock(x+8, y+11, z+8, b, 2);
		array.setBlock(x+8, y+12, z+4, b, 2);
		array.setBlock(x+8, y+12, z+5, b, 0);
		array.setBlock(x+8, y+12, z+6, b, 0);
		array.setBlock(x+8, y+12, z+7, b, 0);
		array.setBlock(x+8, y+12, z+8, b, 2);
		array.setBlock(x+8, y+13, z+4, b, 2);
		array.setBlock(x+8, y+13, z+5, b, 0);
		array.setBlock(x+8, y+13, z+6, b, 0);
		array.setBlock(x+8, y+13, z+7, b, 0);
		array.setBlock(x+8, y+13, z+8, b, 2);
		array.setBlock(x+8, y+14, z+4, b, 2);
		array.setBlock(x+8, y+14, z+5, b, 0);
		array.setBlock(x+8, y+14, z+6, b, 0);
		array.setBlock(x+8, y+14, z+7, b, 0);
		array.setBlock(x+8, y+14, z+8, b, 2);
		array.setBlock(x+8, y+15, z+4, b, 6);
		array.setBlock(x+8, y+15, z+5, b, 12);
		array.setBlock(x+8, y+15, z+6, b, 12);
		array.setBlock(x+8, y+15, z+7, b, 12);
		array.setBlock(x+8, y+15, z+8, b, 6);
		array.setBlock(x+8, y+16, z+5, b, 2);
		array.setBlock(x+8, y+16, z+6, b, 0);
		array.setBlock(x+8, y+16, z+7, b, 2);
		array.setBlock(x+8, y+17, z+5, b, 2);
		array.setBlock(x+8, y+17, z+6, b, 14);
		array.setBlock(x+8, y+17, z+7, b, 2);
		array.setBlock(x+8, y+18, z+5, b, 6);
		array.setBlock(x+8, y+18, z+6, b, 12);
		array.setBlock(x+8, y+18, z+7, b, 6);
		array.setBlock(x+8, y+19, z+6, b, 2);
		array.setBlock(x+8, y+20, z+6, b, 7);
		array.setBlock(x+9, y+2, z+5, b, 0);
		array.setBlock(x+9, y+2, z+6, b, 0);
		array.setBlock(x+9, y+2, z+7, b, 0);
		array.setBlock(x+9, y+3, z+3, b, 0);
		array.setBlock(x+9, y+3, z+4, b, 0);
		array.setBlock(x+9, y+3, z+5, b, 10);
		array.setBlock(x+9, y+3, z+6, b, 10);
		array.setBlock(x+9, y+3, z+7, b, 10);
		array.setBlock(x+9, y+3, z+8, b, 0);
		array.setBlock(x+9, y+3, z+9, b, 0);
		array.setBlock(x+9, y+4, z+4, b, 0);
		array.setBlock(x+9, y+4, z+8, b, 0);
		array.setBlock(x+9, y+5, z+4, b, 2);
		array.setBlock(x+9, y+5, z+8, b, 2);
		array.setBlock(x+9, y+6, z+4, b, 2);
		array.setBlock(x+9, y+6, z+8, b, 2);
		array.setBlock(x+9, y+7, z+4, b, 2);
		array.setBlock(x+9, y+7, z+8, b, 2);
		array.setBlock(x+9, y+8, z+4, b, 12);
		array.setBlock(x+9, y+8, z+5, b, 12);
		array.setBlock(x+9, y+8, z+6, b, 12);
		array.setBlock(x+9, y+8, z+7, b, 12);
		array.setBlock(x+9, y+8, z+8, b, 12);
		array.setBlock(x+9, y+9, z+4, b, 6);
		array.setBlock(x+9, y+9, z+5, b, 12);
		array.setBlock(x+9, y+9, z+6, b, 0);
		array.setBlock(x+9, y+9, z+7, b, 12);
		array.setBlock(x+9, y+9, z+8, b, 6);
		array.setBlock(x+9, y+10, z+5, b, 2);
		array.setBlock(x+9, y+10, z+6, b, 0);
		array.setBlock(x+9, y+10, z+7, b, 2);
		array.setBlock(x+9, y+11, z+5, b, 2);
		array.setBlock(x+9, y+11, z+6, b, 13);
		array.setBlock(x+9, y+11, z+7, b, 2);
		array.setBlock(x+9, y+12, z+5, b, 6);
		array.setBlock(x+9, y+12, z+6, b, 12);
		array.setBlock(x+9, y+12, z+7, b, 6);
		array.setBlock(x+9, y+13, z+6, b, 2);
		array.setBlock(x+9, y+14, z+6, b, 8);
		array.setBlock(x+10, y+3, z+4, b, 0);
		array.setBlock(x+10, y+3, z+5, b, 10);
		array.setBlock(x+10, y+3, z+6, b, 10);
		array.setBlock(x+10, y+3, z+7, b, 10);
		array.setBlock(x+10, y+3, z+8, b, 0);
		array.setBlock(x+11, y+3, z+5, b, 10);
		array.setBlock(x+11, y+3, z+6, b, 10);
		array.setBlock(x+11, y+3, z+7, b, 10);
		array.setBlock(x+12, y+3, z+5, b, 10);
		array.setBlock(x+12, y+3, z+6, b, 10);
		array.setBlock(x+12, y+3, z+7, b, 10);

		array.setBlock(x+6, y+6, z+6, ChromaTiles.COMPOUND.getBlock(), ChromaTiles.COMPOUND.getBlockMetadata());
		array.setBlock(x+6, y+23, z+6, ChromaTiles.BROADCAST.getBlock(), ChromaTiles.BROADCAST.getBlockMetadata());

		array.setBlock(x+6, y+0, z+6, b, 0);

		array.setBlock(x+6, y+1, z+6, b, 12);
		array.setBlock(x+6, y+2, z+6, b, 2);
		array.addBlock(x+6, y+2, z+6, b, StoneTypes.GLOWCOL.ordinal());
		array.setBlock(x+6, y+3, z+6, b, 13);
		array.setBlock(x+6, y+4, z+6, b, 2);
		array.addBlock(x+6, y+4, z+6, b, StoneTypes.GLOWCOL.ordinal());
		array.setBlock(x+6, y+5, z+6, b, 12);

		array.setBlock(x+6, y+24, z+6, b, 0);
		array.setBlock(x+6, y+25, z+6, b, 0);

		array.setBlock(x+6, y+20, z+6, Blocks.air);

		array.setBlock(x+3, y+4, z+5, Blocks.air);
		array.setBlock(x+3, y+4, z+6, Blocks.air);
		array.setBlock(x+3, y+4, z+7, Blocks.air);
		array.setBlock(x+3, y+5, z+5, Blocks.air);
		array.setBlock(x+3, y+5, z+6, Blocks.air);
		array.setBlock(x+3, y+5, z+7, Blocks.air);
		array.setBlock(x+3, y+6, z+5, Blocks.air);
		array.setBlock(x+3, y+6, z+6, Blocks.air);
		array.setBlock(x+3, y+6, z+7, Blocks.air);
		array.setBlock(x+3, y+7, z+5, Blocks.air);
		array.setBlock(x+3, y+7, z+6, Blocks.air);
		array.setBlock(x+3, y+7, z+7, Blocks.air);
		array.setBlock(x+4, y+5, z+5, Blocks.air);
		array.setBlock(x+4, y+5, z+6, Blocks.air);
		array.setBlock(x+4, y+5, z+7, Blocks.air);
		array.setBlock(x+4, y+6, z+5, Blocks.air);
		array.setBlock(x+4, y+6, z+6, Blocks.air);
		array.setBlock(x+4, y+6, z+7, Blocks.air);
		array.setBlock(x+5, y+4, z+3, Blocks.air);
		array.setBlock(x+5, y+4, z+9, Blocks.air);
		array.setBlock(x+5, y+5, z+3, Blocks.air);
		array.setBlock(x+5, y+5, z+4, Blocks.air);
		array.setBlock(x+5, y+5, z+8, Blocks.air);
		array.setBlock(x+5, y+5, z+9, Blocks.air);
		array.setBlock(x+5, y+6, z+3, Blocks.air);
		array.setBlock(x+5, y+6, z+4, Blocks.air);
		array.setBlock(x+5, y+6, z+6, Blocks.air);
		array.setBlock(x+5, y+6, z+8, Blocks.air);
		array.setBlock(x+5, y+6, z+9, Blocks.air);
		array.setBlock(x+5, y+7, z+3, Blocks.air);
		array.setBlock(x+5, y+7, z+9, Blocks.air);
		array.setBlock(x+5, y+23, z+6, Blocks.air);
		array.setBlock(x+6, y+4, z+3, Blocks.air);
		array.setBlock(x+6, y+4, z+9, Blocks.air);
		array.setBlock(x+6, y+5, z+3, Blocks.air);
		array.setBlock(x+6, y+5, z+4, Blocks.air);
		array.setBlock(x+6, y+5, z+8, Blocks.air);
		array.setBlock(x+6, y+5, z+9, Blocks.air);
		array.setBlock(x+6, y+6, z+3, Blocks.air);
		array.setBlock(x+6, y+6, z+4, Blocks.air);
		array.setBlock(x+6, y+6, z+5, Blocks.air);
		array.setBlock(x+6, y+6, z+7, Blocks.air);
		array.setBlock(x+6, y+6, z+8, Blocks.air);
		array.setBlock(x+6, y+6, z+9, Blocks.air);
		array.setBlock(x+6, y+7, z+3, Blocks.air);
		array.setBlock(x+6, y+7, z+9, Blocks.air);
		array.setBlock(x+6, y+23, z+5, Blocks.air);
		array.setBlock(x+6, y+23, z+7, Blocks.air);
		array.setBlock(x+7, y+4, z+3, Blocks.air);
		array.setBlock(x+7, y+4, z+9, Blocks.air);
		array.setBlock(x+7, y+5, z+3, Blocks.air);
		array.setBlock(x+7, y+5, z+4, Blocks.air);
		array.setBlock(x+7, y+5, z+8, Blocks.air);
		array.setBlock(x+7, y+5, z+9, Blocks.air);
		array.setBlock(x+7, y+6, z+3, Blocks.air);
		array.setBlock(x+7, y+6, z+4, Blocks.air);
		array.setBlock(x+7, y+6, z+6, Blocks.air);
		array.setBlock(x+7, y+6, z+8, Blocks.air);
		array.setBlock(x+7, y+6, z+9, Blocks.air);
		array.setBlock(x+7, y+7, z+3, Blocks.air);
		array.setBlock(x+7, y+7, z+9, Blocks.air);
		array.setBlock(x+7, y+23, z+6, Blocks.air);
		array.setBlock(x+8, y+5, z+5, Blocks.air);
		array.setBlock(x+8, y+5, z+6, Blocks.air);
		array.setBlock(x+8, y+5, z+7, Blocks.air);
		array.setBlock(x+8, y+6, z+5, Blocks.air);
		array.setBlock(x+8, y+6, z+6, Blocks.air);
		array.setBlock(x+8, y+6, z+7, Blocks.air);
		array.setBlock(x+9, y+4, z+5, Blocks.air);
		array.setBlock(x+9, y+4, z+6, Blocks.air);
		array.setBlock(x+9, y+4, z+7, Blocks.air);
		array.setBlock(x+9, y+5, z+5, Blocks.air);
		array.setBlock(x+9, y+5, z+6, Blocks.air);
		array.setBlock(x+9, y+5, z+7, Blocks.air);
		array.setBlock(x+9, y+6, z+5, Blocks.air);
		array.setBlock(x+9, y+6, z+6, Blocks.air);
		array.setBlock(x+9, y+6, z+7, Blocks.air);
		array.setBlock(x+9, y+7, z+5, Blocks.air);
		array.setBlock(x+9, y+7, z+6, Blocks.air);
		array.setBlock(x+9, y+7, z+7, Blocks.air);

		return array;
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

		array.setBlock(x+10, y+1, z+3, getChestGen(), getChestMeta(ForgeDirection.WEST));
		array.setBlock(x+10, y+1, z+7, getChestGen(), getChestMeta(ForgeDirection.WEST));

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
		array.setBlock(x+10, y+1, z+4, Blocks.air);
		array.setBlock(x+10, y+1, z+5, Blocks.air);
		array.setBlock(x+10, y+1, z+6, Blocks.air);
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

	public static FilledBlockArray getBurrowStructure(World world, int x, int y, int z, CrystalElement e) {
		FilledBlockArray array = new FilledBlockArray(world);

		y -= 11;
		x -= 8;
		z -= 5;

		Block b = ChromaBlocks.STRUCTSHIELD.getBlockInstance();

		//Cracking block
		array.setBlock(x+5, y+4, z+3, b, 9);

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

		array.setBlock(x+3, y+1, z+3, ChromaBlocks.LAMP.getBlockInstance(), e.ordinal());
		array.setBlock(x+3, y+5, z+3, Blocks.torch, 5);

		//Chests
		array.setBlock(x+3, y+6, z+1, getChestGen(), getChestMeta(ForgeDirection.SOUTH));
		array.setBlock(x+1, y+6, z+3, getChestGen(), getChestMeta(ForgeDirection.EAST));
		array.setBlock(x+3, y+6, z+5, getChestGen(), getChestMeta(ForgeDirection.NORTH));
		array.setBlock(x+3, y+2, z+1, getChestGen(), getChestMeta(ForgeDirection.SOUTH));
		array.setBlock(x+1, y+2, z+3, getChestGen(), getChestMeta(ForgeDirection.EAST));
		array.setBlock(x+3, y+2, z+5, getChestGen(), getChestMeta(ForgeDirection.NORTH));

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
		return OceanStructure.getOceanStructure(world, x, y, z);
	}

	public static FilledBlockArray getDesertStructure(World world, int x, int y, int z) {
		return DesertStructure.getDesertStructure(world, x, y, z);
	}

	public static Block getChestGen() {
		return ChromaBlocks.LOOTCHEST.getBlockInstance();//Blocks.chest;
	}

	public static int getChestMeta(ForgeDirection dir) {
		switch(dir) {
			case EAST:
				return 1+8;
			case WEST:
				return 0+8;
			case NORTH:
				return 2+8;
			case SOUTH:
				return 3+8;
			default:
				return 0;
		}
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
				array.addEmpty(x-1, dy, z, false, false);
				array.addEmpty(x-1, dy, z-1, false, false);
				array.addEmpty(x-1, dy, z-2, false, false);
				array.addEmpty(x-1, dy, z+1, false, false);
				array.addEmpty(x+2, dy, z, false, false);
				array.addEmpty(x+2, dy, z-1, false, false);
				array.addEmpty(x+2, dy, z+1, false, false);
				array.addEmpty(x+2, dy, z-2, false, false);
				array.addEmpty(x, dy, z-2, false, false);
				array.addEmpty(x+1, dy, z-2, false, false);
				array.addEmpty(x, dy, z+1, false, false);
				array.addEmpty(x+1, dy, z+1, false, false);

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

	public static FilledBlockArray getBoostedTreeStructure(World world, int x, int y, int z) {
		FilledBlockArray array = getTreeStructure(world, x, y, z);

		for (int dy = y-12; dy <= y-10; dy++) {
			for (int dx = x-1; dx <= x+2; dx++) {
				for (int dz = z+1; dz >= z-2; dz--) {
					if (dx == x && dz == z)
						continue;
					if (dx == x+1 && dz == z)
						continue;
					if (dx == x && dz == z-1)
						continue;
					if (dx == x+1 && dz == z-1)
						continue;
					array.addEmpty(dx, dy, dz, false, false);
				}
			}
		}

		Block b = ChromaBlocks.PYLONSTRUCT.getBlockInstance();
		for (int i = 13; i <= 16; i++) {
			int dy = y-i;
			int meta = i <= 14 ? StoneTypes.SMOOTH.ordinal() : StoneTypes.COLUMN.ordinal();
			array.setBlock(x, dy, z, b, meta);
			array.setBlock(x, dy, z-1, b, meta);
			array.setBlock(x+1, dy, z, b, meta);
			array.setBlock(x+1, dy, z-1, b, meta);
		}

		array.setBlock(x, y-13, z+1, b, StoneTypes.RESORING.ordinal());
		array.setBlock(x+1, y-13, z+1, b, StoneTypes.RESORING.ordinal());
		array.setBlock(x, y-13, z-2, b, StoneTypes.RESORING.ordinal());
		array.setBlock(x+1, y-13, z-2, b, StoneTypes.RESORING.ordinal());

		array.setBlock(x-1, y-13, z, b, StoneTypes.RESORING.ordinal());
		array.setBlock(x-1, y-13, z-1, b, StoneTypes.RESORING.ordinal());
		array.setBlock(x+2, y-13, z, b, StoneTypes.RESORING.ordinal());
		array.setBlock(x+2, y-13, z-1, b, StoneTypes.RESORING.ordinal());

		array.setBlock(x-1, y-13, z+1, b, StoneTypes.BRICKS.ordinal());
		array.setBlock(x-1, y-13, z-2, b, StoneTypes.BRICKS.ordinal());
		array.setBlock(x+2, y-13, z+1, b, StoneTypes.BRICKS.ordinal());
		array.setBlock(x+2, y-13, z-2, b, StoneTypes.BRICKS.ordinal());

		array.setBlock(x, y-13, z+2, b, StoneTypes.BRICKS.ordinal());
		array.setBlock(x+1, y-13, z+2, b, StoneTypes.BRICKS.ordinal());
		array.setBlock(x, y-13, z-3, b, StoneTypes.BRICKS.ordinal());
		array.setBlock(x+1, y-13, z-3, b, StoneTypes.BRICKS.ordinal());

		array.setBlock(x-2, y-13, z, b, StoneTypes.BRICKS.ordinal());
		array.setBlock(x-2, y-13, z-1, b, StoneTypes.BRICKS.ordinal());
		array.setBlock(x+3, y-13, z, b, StoneTypes.BRICKS.ordinal());
		array.setBlock(x+3, y-13, z-1, b, StoneTypes.BRICKS.ordinal());

		array.setBlock(x-2, y-13, z+1, b, StoneTypes.STABILIZER.ordinal());
		array.setBlock(x-1, y-13, z+2, b, StoneTypes.STABILIZER.ordinal());

		array.setBlock(x-2, y-13, z-2, b, StoneTypes.STABILIZER.ordinal());
		array.setBlock(x-1, y-13, z-3, b, StoneTypes.STABILIZER.ordinal());

		array.setBlock(x+3, y-13, z+1, b, StoneTypes.STABILIZER.ordinal());
		array.setBlock(x+2, y-13, z+2, b, StoneTypes.STABILIZER.ordinal());

		array.setBlock(x+3, y-13, z-2, b, StoneTypes.STABILIZER.ordinal());
		array.setBlock(x+2, y-13, z-3, b, StoneTypes.STABILIZER.ordinal());

		array.setBlock(x-3, y-13, z+1, b, StoneTypes.BEAM.ordinal());
		array.setBlock(x-1, y-13, z+3, b, StoneTypes.BEAM.ordinal());

		array.setBlock(x-3, y-13, z-2, b, StoneTypes.BEAM.ordinal());
		array.setBlock(x-1, y-13, z-4, b, StoneTypes.BEAM.ordinal());

		array.setBlock(x+4, y-13, z+1, b, StoneTypes.BEAM.ordinal());
		array.setBlock(x+2, y-13, z+3, b, StoneTypes.BEAM.ordinal());

		array.setBlock(x+4, y-13, z-2, b, StoneTypes.BEAM.ordinal());
		array.setBlock(x+2, y-13, z-4, b, StoneTypes.BEAM.ordinal());

		array.setBlock(x-2, y-13, z+2, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(x-2, y-13, z-3, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(x+3, y-13, z+2, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(x+3, y-13, z-3, b, StoneTypes.SMOOTH.ordinal());

		array.setBlock(x-4, y-13, z+1, b, StoneTypes.CORNER.ordinal());
		array.setBlock(x-4, y-13, z-2, b, StoneTypes.CORNER.ordinal());

		array.setBlock(x-1, y-13, z+4, b, StoneTypes.CORNER.ordinal());
		array.setBlock(x+2, y-13, z+4, b, StoneTypes.CORNER.ordinal());

		array.setBlock(x-1, y-13, z-5, b, StoneTypes.CORNER.ordinal());
		array.setBlock(x+2, y-13, z-5, b, StoneTypes.CORNER.ordinal());

		array.setBlock(x+5, y-13, z-2, b, StoneTypes.CORNER.ordinal());
		array.setBlock(x+5, y-13, z+1, b, StoneTypes.CORNER.ordinal());

		array.setBlock(x-4, y-13, z, b, StoneTypes.GLOWBEAM.ordinal());
		array.setBlock(x-4, y-13, z-1, b, StoneTypes.GLOWBEAM.ordinal());

		array.setBlock(x, y-13, z+4, b, StoneTypes.GLOWBEAM.ordinal());
		array.setBlock(x+1, y-13, z+4, b, StoneTypes.GLOWBEAM.ordinal());

		array.setBlock(x, y-13, z-5, b, StoneTypes.GLOWBEAM.ordinal());
		array.setBlock(x+1, y-13, z-5, b, StoneTypes.GLOWBEAM.ordinal());

		array.setBlock(x+5, y-13, z-1, b, StoneTypes.GLOWBEAM.ordinal());
		array.setBlock(x+5, y-13, z, b, StoneTypes.GLOWBEAM.ordinal());

		array.setBlock(x-1, y-14, z+1, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(x+2, y-14, z+1, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(x-1, y-14, z-2, b, StoneTypes.SMOOTH.ordinal());
		array.setBlock(x+2, y-14, z-2, b, StoneTypes.SMOOTH.ordinal());

		array.setBlock(x-1, y-14, z, b, StoneTypes.GROOVE1.ordinal());
		array.setBlock(x+2, y-14, z, b, StoneTypes.GROOVE1.ordinal());
		array.setBlock(x-1, y-14, z-1, b, StoneTypes.GROOVE1.ordinal());
		array.setBlock(x+2, y-14, z-1, b, StoneTypes.GROOVE1.ordinal());

		array.setBlock(x, y-14, z+1, b, StoneTypes.GROOVE2.ordinal());
		array.setBlock(x+1, y-14, z+1, b, StoneTypes.GROOVE2.ordinal());
		array.setBlock(x, y-14, z-2, b, StoneTypes.GROOVE2.ordinal());
		array.setBlock(x+1, y-14, z-2, b, StoneTypes.GROOVE2.ordinal());

		Block c = ChromaBlocks.CHROMA.getBlockInstance();

		array.setBlock(x-3, y-13, z, c, 0);
		array.setBlock(x-3, y-13, z-1, c, 0);

		array.setBlock(x, y-13, z+3, c, 0);
		array.setBlock(x+1, y-13, z+3, c, 0);

		array.setBlock(x, y-13, z-4, c, 0);
		array.setBlock(x+1, y-13, z-4, c, 0);

		array.setBlock(x+4, y-13, z-1, c, 0);
		array.setBlock(x+4, y-13, z, c, 0);

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

	public static FilledBlockArray getRitualStructure(World world, int x, int y, int z, boolean allowEnhance, boolean requireEnhance) {
		FilledBlockArray array = new FilledBlockArray(world);
		Block b = ChromaBlocks.PYLONSTRUCT.getBlockInstance();
		for (int i = -2; i <= 2; i++) {
			for (int k = -2; k <= 2; k++) {
				for (int j = 1; j <= 4; j++)
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
			if (requireEnhance) {
				array.setBlock(x-4, y+1, z+i, b, StoneTypes.GLOWBEAM.ordinal());
				array.setBlock(x+4, y+1, z+i, b, StoneTypes.GLOWBEAM.ordinal());
				array.setBlock(x+i, y+1, z-4, b, StoneTypes.GLOWBEAM.ordinal());
				array.setBlock(x+i, y+1, z+4, b, StoneTypes.GLOWBEAM.ordinal());
			}
			else {
				array.setBlock(x-4, y+1, z+i, b, 1);
				array.setBlock(x+4, y+1, z+i, b, 1);
				array.setBlock(x+i, y+1, z-4, b, 1);
				array.setBlock(x+i, y+1, z+4, b, 1);
				if (allowEnhance) {
					array.addBlock(x-4, y+1, z+i, b, StoneTypes.GLOWBEAM.ordinal());
					array.addBlock(x+4, y+1, z+i, b, StoneTypes.GLOWBEAM.ordinal());
					array.addBlock(x+i, y+1, z-4, b, StoneTypes.GLOWBEAM.ordinal());
					array.addBlock(x+i, y+1, z+4, b, StoneTypes.GLOWBEAM.ordinal());
				}
			}
		}

		for (int i = -3; i <= 3; i++) {
			array.setBlock(x-3, y+2, z+i, b, 1);
			array.setBlock(x+3, y+2, z+i, b, 1);
			array.setBlock(x+i, y+2, z-3, b, 1);
			array.setBlock(x+i, y+2, z+3, b, 1);
		}

		if (requireEnhance) {
			array.setBlock(x+2, y+2, z+2, b, StoneTypes.GLOWCOL.ordinal());
			array.setBlock(x-2, y+2, z+2, b, StoneTypes.GLOWCOL.ordinal());
			array.setBlock(x+2, y+2, z-2, b, StoneTypes.GLOWCOL.ordinal());
			array.setBlock(x-2, y+2, z-2, b, StoneTypes.GLOWCOL.ordinal());
		}
		else {
			array.setBlock(x+2, y+2, z+2, b, 2);
			array.setBlock(x-2, y+2, z+2, b, 2);
			array.setBlock(x+2, y+2, z-2, b, 2);
			array.setBlock(x-2, y+2, z-2, b, 2);
			if (allowEnhance) {
				array.addBlock(x+2, y+2, z+2, b, StoneTypes.GLOWCOL.ordinal());
				array.addBlock(x-2, y+2, z+2, b, StoneTypes.GLOWCOL.ordinal());
				array.addBlock(x+2, y+2, z-2, b, StoneTypes.GLOWCOL.ordinal());
				array.addBlock(x-2, y+2, z-2, b, StoneTypes.GLOWCOL.ordinal());
			}
		}

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

		array.setBlock(x-1, y+1, z-1, ChromaBlocks.CHROMA.getBlockInstance(), 0);
		array.setBlock(x, y+1, z-1, ChromaBlocks.CHROMA.getBlockInstance(), 0);
		array.setBlock(x+1, y+1, z-1, ChromaBlocks.CHROMA.getBlockInstance(), 0);
		array.setBlock(x+1, y+1, z, ChromaBlocks.CHROMA.getBlockInstance(), 0);
		array.setBlock(x+1, y+1, z+1, ChromaBlocks.CHROMA.getBlockInstance(), 0);
		array.setBlock(x, y+1, z+1, ChromaBlocks.CHROMA.getBlockInstance(), 0);
		array.setBlock(x-1, y+1, z+1, ChromaBlocks.CHROMA.getBlockInstance(), 0);
		array.setBlock(x-1, y+1, z, ChromaBlocks.CHROMA.getBlockInstance(), 0);

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
					array.addBlock(x-3, dy, z+i, b, 0);
					array.addBlock(x+3, dy, z+i, b, 0);
					array.addBlock(x+i, dy, z-3, b, 0);
					array.addBlock(x+i, dy, z+3, b, 0);

					array.addBlock(x-3, dy, z+i, ChromaBlocks.RUNE.getBlockInstance());
					array.addBlock(x+3, dy, z+i, ChromaBlocks.RUNE.getBlockInstance());
					array.addBlock(x+i, dy, z-3, ChromaBlocks.RUNE.getBlockInstance());
					array.addBlock(x+i, dy, z+3, ChromaBlocks.RUNE.getBlockInstance());
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

		array.addBlock(x+1, y, z, b, 0);
		array.addBlock(x-1, y, z, b, 0);
		array.addBlock(x, y, z+1, b, 0);
		array.addBlock(x, y, z-1, b, 0);

		array.addBlock(x+1, y, z, ChromaBlocks.RUNE.getBlockInstance());
		array.addBlock(x-1, y, z, ChromaBlocks.RUNE.getBlockInstance());
		array.addBlock(x, y, z+1, ChromaBlocks.RUNE.getBlockInstance());
		array.addBlock(x, y, z-1, ChromaBlocks.RUNE.getBlockInstance());

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
			array.addBlock(x-7, y, z+i, b, 0);
			array.addBlock(x+7, y, z+i, b, 0);
			array.addBlock(x+i, y, z-7, b, 0);
			array.addBlock(x+i, y, z+7, b, 0);

			array.addBlock(x-7, y, z+i, b, 12);
			array.addBlock(x+7, y, z+i, b, 12);
			array.addBlock(x+i, y, z-7, b, 12);
			array.addBlock(x+i, y, z+7, b, 12);
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
				int meta3 = i == 1 ? StoneTypes.RESORING.ordinal() : meta2;
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

				if (meta3 != meta2) {
					array.addBlock(x-2, dy, z-8, b2, meta3);
					array.addBlock(x-6, dy, z-8, b2, meta3);
					array.addBlock(x+2, dy, z-8, b2, meta3);
					array.addBlock(x+6, dy, z-8, b2, meta3);

					array.addBlock(x-2, dy, z+8, b2, meta3);
					array.addBlock(x-6, dy, z+8, b2, meta3);
					array.addBlock(x+2, dy, z+8, b2, meta3);
					array.addBlock(x+6, dy, z+8, b2, meta3);

					array.addBlock(x-8, dy, z-2, b2, meta3);
					array.addBlock(x-8, dy, z-6, b2, meta3);
					array.addBlock(x-8, dy, z+2, b2, meta3);
					array.addBlock(x-8, dy, z+6, b2, meta3);

					array.addBlock(x+8, dy, z+6, b2, meta3);
					array.addBlock(x+8, dy, z+2, b2, meta3);
					array.addBlock(x+8, dy, z-6, b2, meta3);
					array.addBlock(x+8, dy, z-2, b2, meta3);
				}
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
		y -= 9;
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

		array.addBlock(x, y, z, b, StoneTypes.STABILIZER.ordinal());
		for (int i = 1; i <= 2; i++) {
			array.addBlock(x+i, y, z, b, StoneTypes.RESORING.ordinal());
			array.addBlock(x-i, y, z, b, StoneTypes.RESORING.ordinal());
			array.addBlock(x, y, z+i, b, StoneTypes.RESORING.ordinal());
			array.addBlock(x, y, z-i, b, StoneTypes.RESORING.ordinal());
		}

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

	public static FilledBlockArray getPortalStructure(World world, int x, int y, int z, boolean display) {
		FilledBlockArray array = new FilledBlockArray(world);

		int i = x-7;
		int j = y+0;
		int k = z-7;

		Block ch = ChromaBlocks.CHROMA.getBlockInstance();
		Block b = ChromaBlocks.PYLONSTRUCT.getBlockInstance();
		Block p = ChromaBlocks.PORTAL.getBlockInstance();
		//Block er = FluidRegistry.getFluid("ender").getBlock();//ChromaBlocks.ENDER.getBlockInstance();
		Fluid er = FluidRegistry.getFluid("ender");
		Block sh = ChromaBlocks.STRUCTSHIELD.getBlockInstance();

		array.setBlock(i+0, j+2, k+1, sh, 0);
		array.setBlock(i+0, j+2, k+2, sh, 0);
		array.setBlock(i+0, j+2, k+3, sh, 0);
		array.setBlock(i+0, j+2, k+11, sh, 0);
		array.setBlock(i+0, j+2, k+12, sh, 0);
		array.setBlock(i+0, j+2, k+13, sh, 0);
		array.setBlock(i+0, j+3, k+0, b, 0);
		array.setBlock(i+0, j+3, k+1, b, 0);
		array.setBlock(i+0, j+3, k+2, b, 0);
		array.setBlock(i+0, j+3, k+3, b, 0);
		array.setBlock(i+0, j+3, k+4, b, 0);
		array.setBlock(i+0, j+3, k+5, b, 15);
		array.setBlock(i+0, j+3, k+6, b, 15);
		array.setBlock(i+0, j+3, k+7, b, 15);
		array.setBlock(i+0, j+3, k+8, b, 15);
		array.setBlock(i+0, j+3, k+9, b, 15);
		array.setBlock(i+0, j+3, k+10, b, 0);
		array.setBlock(i+0, j+3, k+11, b, 0);
		array.setBlock(i+0, j+3, k+12, b, 0);
		array.setBlock(i+0, j+3, k+13, b, 0);
		array.setBlock(i+0, j+3, k+14, b, 0);
		array.setBlock(i+0, j+4, k+0, b, 2);
		array.setBlock(i+0, j+4, k+4, b, 2);
		array.setBlock(i+0, j+4, k+10, b, 2);
		array.setBlock(i+0, j+4, k+14, b, 2);
		array.setBlock(i+0, j+5, k+0, b, 3);
		array.setBlock(i+0, j+5, k+4, b, 13);
		array.setBlock(i+0, j+5, k+10, b, 13);
		array.setBlock(i+0, j+5, k+14, b, 3);
		array.setBlock(i+0, j+6, k+0, b, 2);
		array.setBlock(i+0, j+6, k+4, b, 2);
		array.setBlock(i+0, j+6, k+10, b, 2);
		array.setBlock(i+0, j+6, k+14, b, 2);
		array.setBlock(i+0, j+7, k+0, b, 3);
		array.setBlock(i+0, j+7, k+4, b, 7);
		array.setBlock(i+0, j+7, k+5, b, 1);
		array.setBlock(i+0, j+7, k+6, b, 1);
		array.setBlock(i+0, j+7, k+7, b, 1);
		array.setBlock(i+0, j+7, k+8, b, 1);
		array.setBlock(i+0, j+7, k+9, b, 1);
		array.setBlock(i+0, j+7, k+10, b, 7);
		array.setBlock(i+0, j+7, k+14, b, 3);
		array.setBlock(i+0, j+8, k+0, b, 2);
		array.setBlock(i+0, j+8, k+14, b, 2);
		array.setBlock(i+0, j+9, k+0, b, 5);
		array.setBlock(i+0, j+9, k+14, b, 5);
		array.setBlock(i+1, j+1, k+1, sh, 0);
		array.setBlock(i+1, j+1, k+2, sh, 0);
		array.setBlock(i+1, j+1, k+3, sh, 0);
		array.setBlock(i+1, j+1, k+11, sh, 0);
		array.setBlock(i+1, j+1, k+12, sh, 0);
		array.setBlock(i+1, j+1, k+13, sh, 0);
		array.setBlock(i+1, j+2, k+0, sh, 0);
		array.setBlock(i+1, j+2, k+1, ch);
		array.setBlock(i+1, j+2, k+2, ch);
		array.setBlock(i+1, j+2, k+3, ch);
		array.setBlock(i+1, j+2, k+4, sh, 0);
		array.setBlock(i+1, j+2, k+6, sh, 0);
		array.setBlock(i+1, j+2, k+7, sh, 0);
		array.setBlock(i+1, j+2, k+8, sh, 0);
		array.setBlock(i+1, j+2, k+10, sh, 0);
		array.setBlock(i+1, j+2, k+11, ch);
		array.setBlock(i+1, j+2, k+12, ch);
		array.setBlock(i+1, j+2, k+13, ch);
		array.setBlock(i+1, j+2, k+14, sh, 0);
		array.setBlock(i+1, j+3, k+0, b, 0);
		array.setBlock(i+1, j+3, k+2, ch, 1);
		array.setBlock(i+1, j+3, k+4, b, 15);
		array.setBlock(i+1, j+3, k+5, b, 12);
		array.setBlock(i+1, j+3, k+6, b, 12);
		array.setBlock(i+1, j+3, k+7, b, 12);
		array.setBlock(i+1, j+3, k+8, b, 12);
		array.setBlock(i+1, j+3, k+9, b, 12);
		array.setBlock(i+1, j+3, k+10, b, 15);
		array.setBlock(i+1, j+3, k+12, ch, 1);
		array.setBlock(i+1, j+3, k+14, b, 0);
		array.setBlock(i+1, j+4, k+2, ch, 1);
		array.setBlock(i+1, j+4, k+12, ch, 1);
		array.setBlock(i+1, j+5, k+2, ch, 1);
		array.setBlock(i+1, j+5, k+12, ch, 1);
		array.setBlock(i+1, j+6, k+2, ch, 1);
		array.setBlock(i+1, j+6, k+12, ch, 1);
		array.setBlock(i+2, j+1, k+1, sh, 0);
		array.setBlock(i+2, j+1, k+2, sh, 0);
		array.setBlock(i+2, j+1, k+3, sh, 0);
		array.setBlock(i+2, j+1, k+6, sh, 0);
		array.setBlock(i+2, j+1, k+7, sh, 0);
		array.setBlock(i+2, j+1, k+8, sh, 0);
		array.setBlock(i+2, j+1, k+11, sh, 0);
		array.setBlock(i+2, j+1, k+12, sh, 0);
		array.setBlock(i+2, j+1, k+13, sh, 0);
		array.setBlock(i+2, j+2, k+0, sh, 0);
		array.setBlock(i+2, j+2, k+1, ch);
		array.setBlock(i+2, j+2, k+2, b, 0);
		array.setBlock(i+2, j+2, k+3, ch);
		array.setBlock(i+2, j+2, k+4, sh, 0);
		array.setBlock(i+2, j+2, k+5, sh, 0);
		array.setFluid(i+2, j+2, k+6, er);
		array.setFluid(i+2, j+2, k+7, er);
		array.setFluid(i+2, j+2, k+8, er);
		array.setBlock(i+2, j+2, k+9, sh, 0);
		array.setBlock(i+2, j+2, k+10, sh, 0);
		array.setBlock(i+2, j+2, k+11, ch);
		array.setBlock(i+2, j+2, k+12, b, 0);
		array.setBlock(i+2, j+2, k+13, ch);
		array.setBlock(i+2, j+2, k+14, sh, 0);
		array.setBlock(i+2, j+3, k+0, b, 0);
		array.setBlock(i+2, j+3, k+1, ch, 1);
		array.setBlock(i+2, j+3, k+2, b, 0);
		array.setBlock(i+2, j+3, k+3, ch, 1);
		array.setBlock(i+2, j+3, k+4, b, 15);
		array.setBlock(i+2, j+3, k+5, b, 12);
		array.setBlock(i+2, j+3, k+9, b, 12);
		array.setBlock(i+2, j+3, k+10, b, 15);
		array.setBlock(i+2, j+3, k+11, ch, 1);
		array.setBlock(i+2, j+3, k+12, b, 0);
		array.setBlock(i+2, j+3, k+13, ch, 1);
		array.setBlock(i+2, j+3, k+14, b, 0);
		array.setBlock(i+2, j+4, k+1, ch, 1);
		array.setBlock(i+2, j+4, k+2, b, 0);
		array.setBlock(i+2, j+4, k+3, ch, 1);
		array.setBlock(i+2, j+4, k+11, ch, 1);
		array.setBlock(i+2, j+4, k+12, b, 0);
		array.setBlock(i+2, j+4, k+13, ch, 1);
		array.setBlock(i+2, j+5, k+1, ch, 1);
		array.setBlock(i+2, j+5, k+2, b, 0);
		array.setBlock(i+2, j+5, k+3, ch, 1);
		array.setBlock(i+2, j+5, k+11, ch, 1);
		array.setBlock(i+2, j+5, k+12, b, 0);
		array.setBlock(i+2, j+5, k+13, ch, 1);
		array.setBlock(i+2, j+6, k+1, ch, 1);
		array.setBlock(i+2, j+6, k+2, ch);
		array.setBlock(i+2, j+6, k+3, ch, 1);
		array.setBlock(i+2, j+6, k+11, ch, 1);
		array.setBlock(i+2, j+6, k+12, ch);
		array.setBlock(i+2, j+6, k+13, ch, 1);
		array.setBlock(i+3, j+1, k+1, sh, 0);
		array.setBlock(i+3, j+1, k+2, sh, 0);
		array.setBlock(i+3, j+1, k+3, sh, 0);
		array.setBlock(i+3, j+1, k+6, sh, 0);
		array.setBlock(i+3, j+1, k+7, sh, 0);
		array.setBlock(i+3, j+1, k+8, sh, 0);
		array.setBlock(i+3, j+1, k+11, sh, 0);
		array.setBlock(i+3, j+1, k+12, sh, 0);
		array.setBlock(i+3, j+1, k+13, sh, 0);
		array.setBlock(i+3, j+2, k+0, sh, 0);
		array.setBlock(i+3, j+2, k+1, ch);
		array.setBlock(i+3, j+2, k+2, ch);
		array.setBlock(i+3, j+2, k+3, ch);
		array.setBlock(i+3, j+2, k+4, sh, 0);
		array.setBlock(i+3, j+2, k+5, sh, 0);
		array.setFluid(i+3, j+2, k+6, er);
		array.setFluid(i+3, j+2, k+7, er);
		array.setFluid(i+3, j+2, k+8, er);
		array.setBlock(i+3, j+2, k+9, sh, 0);
		array.setBlock(i+3, j+2, k+10, sh, 0);
		array.setBlock(i+3, j+2, k+11, ch);
		array.setBlock(i+3, j+2, k+12, ch);
		array.setBlock(i+3, j+2, k+13, ch);
		array.setBlock(i+3, j+2, k+14, sh, 0);
		array.setBlock(i+3, j+3, k+0, b, 0);
		array.setBlock(i+3, j+3, k+2, ch, 1);
		array.setBlock(i+3, j+3, k+4, b, 15);
		array.setBlock(i+3, j+3, k+5, b, 12);
		array.setBlock(i+3, j+3, k+9, b, 12);
		array.setBlock(i+3, j+3, k+10, b, 15);
		array.setBlock(i+3, j+3, k+12, ch, 1);
		array.setBlock(i+3, j+3, k+14, b, 0);
		array.setBlock(i+3, j+4, k+2, ch, 1);
		array.setBlock(i+3, j+4, k+12, ch, 1);
		array.setBlock(i+3, j+5, k+2, ch, 1);
		array.setBlock(i+3, j+5, k+12, ch, 1);
		array.setBlock(i+3, j+6, k+2, ch, 1);
		array.setBlock(i+3, j+6, k+12, ch, 1);
		array.setBlock(i+4, j+2, k+1, sh, 0);
		array.setBlock(i+4, j+2, k+2, sh, 0);
		array.setBlock(i+4, j+2, k+3, sh, 0);
		array.setBlock(i+4, j+2, k+6, sh, 0);
		array.setBlock(i+4, j+2, k+7, sh, 0);
		array.setBlock(i+4, j+2, k+8, sh, 0);
		array.setBlock(i+4, j+2, k+11, sh, 0);
		array.setBlock(i+4, j+2, k+12, sh, 0);
		array.setBlock(i+4, j+2, k+13, sh, 0);
		array.setBlock(i+4, j+3, k+0, b, 0);
		array.setBlock(i+4, j+3, k+1, b, 15);
		array.setBlock(i+4, j+3, k+2, b, 15);
		array.setBlock(i+4, j+3, k+3, b, 15);
		array.setBlock(i+4, j+3, k+4, b, 0);
		array.setBlock(i+4, j+3, k+5, b, 12);
		array.setBlock(i+4, j+3, k+6, b, 12);
		array.setBlock(i+4, j+3, k+7, b, 12);
		array.setBlock(i+4, j+3, k+8, b, 12);
		array.setBlock(i+4, j+3, k+9, b, 12);
		array.setBlock(i+4, j+3, k+10, b, 0);
		array.setBlock(i+4, j+3, k+11, b, 15);
		array.setBlock(i+4, j+3, k+12, b, 15);
		array.setBlock(i+4, j+3, k+13, b, 15);
		array.setBlock(i+4, j+3, k+14, b, 0);
		array.setBlock(i+4, j+4, k+0, b, 2);
		array.setBlock(i+4, j+4, k+4, b, 2);
		array.setBlock(i+4, j+4, k+10, b, 2);
		array.setBlock(i+4, j+4, k+14, b, 2);
		array.setBlock(i+4, j+5, k+0, b, 13);
		array.setBlock(i+4, j+5, k+4, b, 5);
		array.setBlock(i+4, j+5, k+10, b, 5);
		array.setBlock(i+4, j+5, k+14, b, 13);
		array.setBlock(i+4, j+6, k+0, b, 2);
		array.setBlock(i+4, j+6, k+4, b, 2);
		array.setBlock(i+4, j+6, k+10, b, 2);
		array.setBlock(i+4, j+6, k+14, b, 2);
		array.setBlock(i+4, j+7, k+0, b, 7);
		array.setBlock(i+4, j+7, k+4, b, 6);
		array.setBlock(i+4, j+7, k+5, b, 4);
		array.setBlock(i+4, j+7, k+6, b, 4);
		array.setBlock(i+4, j+7, k+7, b, 4);
		array.setBlock(i+4, j+7, k+8, b, 4);
		array.setBlock(i+4, j+7, k+9, b, 4);
		array.setBlock(i+4, j+7, k+10, b, 6);
		array.setBlock(i+4, j+7, k+14, b, 7);
		array.setBlock(i+5, j+1, k+5, b, 0);
		array.setBlock(i+5, j+1, k+6, b, 2);
		array.setBlock(i+5, j+1, k+7, b, 2);
		array.setBlock(i+5, j+1, k+8, b, 2);
		array.setBlock(i+5, j+1, k+9, b, 0);
		array.setBlock(i+5, j+2, k+2, sh, 0);
		array.setBlock(i+5, j+2, k+3, sh, 0);
		array.setBlock(i+5, j+2, k+5, b, 14);
		array.setBlock(i+5, j+2, k+6, b, 11);
		array.setBlock(i+5, j+2, k+7, b, 11);
		array.setBlock(i+5, j+2, k+8, b, 11);
		array.setBlock(i+5, j+2, k+9, b, 14);
		array.setBlock(i+5, j+2, k+11, sh, 0);
		array.setBlock(i+5, j+2, k+12, sh, 0);
		array.setBlock(i+5, j+3, k+0, b, 15);
		array.setBlock(i+5, j+3, k+1, b, 12);
		array.setBlock(i+5, j+3, k+2, b, 12);
		array.setBlock(i+5, j+3, k+3, b, 12);
		array.setBlock(i+5, j+3, k+4, b, 12);
		array.setBlock(i+5, j+3, k+10, b, 12);
		array.setBlock(i+5, j+3, k+11, b, 12);
		array.setBlock(i+5, j+3, k+12, b, 12);
		array.setBlock(i+5, j+3, k+13, b, 12);
		array.setBlock(i+5, j+3, k+14, b, 15);
		array.setBlock(i+5, j+7, k+0, b, 1);
		array.setBlock(i+5, j+7, k+4, b, 4);
		array.setBlock(i+5, j+7, k+10, b, 4);
		array.setBlock(i+5, j+7, k+14, b, 1);
		array.setBlock(i+6, j+0, k+6, p);
		array.setBlock(i+6, j+0, k+7, p);
		array.setBlock(i+6, j+0, k+8, p);
		array.setBlock(i+6, j+1, k+2, sh, 0);
		array.setBlock(i+6, j+1, k+3, sh, 0);
		array.setBlock(i+6, j+1, k+5, b, 2);
		array.setBlock(i+6, j+1, k+9, b, 2);
		array.setBlock(i+6, j+1, k+11, sh, 0);
		array.setBlock(i+6, j+1, k+12, sh, 0);
		array.setBlock(i+6, j+2, k+1, sh, 0);
		array.setFluid(i+6, j+2, k+2, er);
		array.setFluid(i+6, j+2, k+3, er);
		array.setBlock(i+6, j+2, k+4, sh, 0);
		array.setBlock(i+6, j+2, k+5, b, 10);
		array.setBlock(i+6, j+2, k+9, b, 10);
		array.setBlock(i+6, j+2, k+10, sh, 0);
		array.setFluid(i+6, j+2, k+11, er);
		array.setFluid(i+6, j+2, k+12, er);
		array.setBlock(i+6, j+2, k+13, sh, 0);
		array.setBlock(i+6, j+3, k+0, b, 15);
		array.setBlock(i+6, j+3, k+1, b, 12);
		array.setBlock(i+6, j+3, k+4, b, 12);
		array.setBlock(i+6, j+3, k+10, b, 12);
		array.setBlock(i+6, j+3, k+13, b, 12);
		array.setBlock(i+6, j+3, k+14, b, 15);
		array.setBlock(i+6, j+7, k+0, b, 1);
		array.setBlock(i+6, j+7, k+4, b, 4);
		array.setBlock(i+6, j+7, k+10, b, 4);
		array.setBlock(i+6, j+7, k+14, b, 1);
		array.setBlock(i+7, j+0, k+6, p);
		array.setBlock(i+7, j+0, k+7, p);
		array.setBlock(i+7, j+0, k+8, p);
		array.setBlock(i+7, j+1, k+2, sh, 0);
		array.setBlock(i+7, j+1, k+3, sh, 0);
		array.setBlock(i+7, j+1, k+5, b, 2);
		array.setBlock(i+7, j+1, k+9, b, 2);
		array.setBlock(i+7, j+1, k+11, sh, 0);
		array.setBlock(i+7, j+1, k+12, sh, 0);
		array.setBlock(i+7, j+2, k+1, sh, 0);
		array.setFluid(i+7, j+2, k+2, er);
		array.setFluid(i+7, j+2, k+3, er);
		array.setBlock(i+7, j+2, k+4, sh, 0);
		array.setBlock(i+7, j+2, k+5, b, 10);
		array.setBlock(i+7, j+2, k+9, b, 10);
		array.setBlock(i+7, j+2, k+10, sh, 0);
		array.setFluid(i+7, j+2, k+11, er);
		array.setFluid(i+7, j+2, k+12, er);
		array.setBlock(i+7, j+2, k+13, sh, 0);
		array.setBlock(i+7, j+3, k+0, b, 15);
		array.setBlock(i+7, j+3, k+1, b, 12);
		array.setBlock(i+7, j+3, k+4, b, 12);
		array.setBlock(i+7, j+3, k+10, b, 12);
		array.setBlock(i+7, j+3, k+13, b, 12);
		array.setBlock(i+7, j+3, k+14, b, 15);
		array.setBlock(i+7, j+7, k+0, b, 1);
		array.setBlock(i+7, j+7, k+4, b, 4);
		array.setBlock(i+7, j+7, k+10, b, 4);
		array.setBlock(i+7, j+7, k+14, b, 1);
		array.setBlock(i+8, j+0, k+6, p);
		array.setBlock(i+8, j+0, k+7, p);
		array.setBlock(i+8, j+0, k+8, p);
		array.setBlock(i+8, j+1, k+2, sh, 0);
		array.setBlock(i+8, j+1, k+3, sh, 0);
		array.setBlock(i+8, j+1, k+5, b, 2);
		array.setBlock(i+8, j+1, k+9, b, 2);
		array.setBlock(i+8, j+1, k+11, sh, 0);
		array.setBlock(i+8, j+1, k+12, sh, 0);
		array.setBlock(i+8, j+2, k+1, sh, 0);
		array.setFluid(i+8, j+2, k+2, er);
		array.setFluid(i+8, j+2, k+3, er);
		array.setBlock(i+8, j+2, k+4, sh, 0);
		array.setBlock(i+8, j+2, k+5, b, 10);
		array.setBlock(i+8, j+2, k+9, b, 10);
		array.setBlock(i+8, j+2, k+10, sh, 0);
		array.setFluid(i+8, j+2, k+11, er);
		array.setFluid(i+8, j+2, k+12, er);
		array.setBlock(i+8, j+2, k+13, sh, 0);
		array.setBlock(i+8, j+3, k+0, b, 15);
		array.setBlock(i+8, j+3, k+1, b, 12);
		array.setBlock(i+8, j+3, k+4, b, 12);
		array.setBlock(i+8, j+3, k+10, b, 12);
		array.setBlock(i+8, j+3, k+13, b, 12);
		array.setBlock(i+8, j+3, k+14, b, 15);
		array.setBlock(i+8, j+7, k+0, b, 1);
		array.setBlock(i+8, j+7, k+4, b, 4);
		array.setBlock(i+8, j+7, k+10, b, 4);
		array.setBlock(i+8, j+7, k+14, b, 1);
		array.setBlock(i+9, j+1, k+5, b, 0);
		array.setBlock(i+9, j+1, k+6, b, 2);
		array.setBlock(i+9, j+1, k+7, b, 2);
		array.setBlock(i+9, j+1, k+8, b, 2);
		array.setBlock(i+9, j+1, k+9, b, 0);
		array.setBlock(i+9, j+2, k+2, sh, 0);
		array.setBlock(i+9, j+2, k+3, sh, 0);
		array.setBlock(i+9, j+2, k+5, b, 14);
		array.setBlock(i+9, j+2, k+6, b, 11);
		array.setBlock(i+9, j+2, k+7, b, 11);
		array.setBlock(i+9, j+2, k+8, b, 11);
		array.setBlock(i+9, j+2, k+9, b, 14);
		array.setBlock(i+9, j+2, k+11, sh, 0);
		array.setBlock(i+9, j+2, k+12, sh, 0);
		array.setBlock(i+9, j+3, k+0, b, 15);
		array.setBlock(i+9, j+3, k+1, b, 12);
		array.setBlock(i+9, j+3, k+2, b, 12);
		array.setBlock(i+9, j+3, k+3, b, 12);
		array.setBlock(i+9, j+3, k+4, b, 12);
		array.setBlock(i+9, j+3, k+10, b, 12);
		array.setBlock(i+9, j+3, k+11, b, 12);
		array.setBlock(i+9, j+3, k+12, b, 12);
		array.setBlock(i+9, j+3, k+13, b, 12);
		array.setBlock(i+9, j+3, k+14, b, 15);
		array.setBlock(i+9, j+7, k+0, b, 1);
		array.setBlock(i+9, j+7, k+4, b, 4);
		array.setBlock(i+9, j+7, k+10, b, 4);
		array.setBlock(i+9, j+7, k+14, b, 1);
		array.setBlock(i+10, j+2, k+1, sh, 0);
		array.setBlock(i+10, j+2, k+2, sh, 0);
		array.setBlock(i+10, j+2, k+3, sh, 0);
		array.setBlock(i+10, j+2, k+6, sh, 0);
		array.setBlock(i+10, j+2, k+7, sh, 0);
		array.setBlock(i+10, j+2, k+8, sh, 0);
		array.setBlock(i+10, j+2, k+11, sh, 0);
		array.setBlock(i+10, j+2, k+12, sh, 0);
		array.setBlock(i+10, j+2, k+13, sh, 0);
		array.setBlock(i+10, j+3, k+0, b, 0);
		array.setBlock(i+10, j+3, k+1, b, 15);
		array.setBlock(i+10, j+3, k+2, b, 15);
		array.setBlock(i+10, j+3, k+3, b, 15);
		array.setBlock(i+10, j+3, k+4, b, 0);
		array.setBlock(i+10, j+3, k+5, b, 12);
		array.setBlock(i+10, j+3, k+6, b, 12);
		array.setBlock(i+10, j+3, k+7, b, 12);
		array.setBlock(i+10, j+3, k+8, b, 12);
		array.setBlock(i+10, j+3, k+9, b, 12);
		array.setBlock(i+10, j+3, k+10, b, 0);
		array.setBlock(i+10, j+3, k+11, b, 15);
		array.setBlock(i+10, j+3, k+12, b, 15);
		array.setBlock(i+10, j+3, k+13, b, 15);
		array.setBlock(i+10, j+3, k+14, b, 0);
		array.setBlock(i+10, j+4, k+0, b, 2);
		array.setBlock(i+10, j+4, k+4, b, 2);
		array.setBlock(i+10, j+4, k+10, b, 2);
		array.setBlock(i+10, j+4, k+14, b, 2);
		array.setBlock(i+10, j+5, k+0, b, 13);
		array.setBlock(i+10, j+5, k+4, b, 5);
		array.setBlock(i+10, j+5, k+10, b, 5);
		array.setBlock(i+10, j+5, k+14, b, 13);
		array.setBlock(i+10, j+6, k+0, b, 2);
		array.setBlock(i+10, j+6, k+4, b, 2);
		array.setBlock(i+10, j+6, k+10, b, 2);
		array.setBlock(i+10, j+6, k+14, b, 2);
		array.setBlock(i+10, j+7, k+0, b, 7);
		array.setBlock(i+10, j+7, k+4, b, 6);
		array.setBlock(i+10, j+7, k+5, b, 4);
		array.setBlock(i+10, j+7, k+6, b, 4);
		array.setBlock(i+10, j+7, k+7, b, 4);
		array.setBlock(i+10, j+7, k+8, b, 4);
		array.setBlock(i+10, j+7, k+9, b, 4);
		array.setBlock(i+10, j+7, k+10, b, 6);
		array.setBlock(i+10, j+7, k+14, b, 7);
		array.setBlock(i+11, j+1, k+1, sh, 0);
		array.setBlock(i+11, j+1, k+2, sh, 0);
		array.setBlock(i+11, j+1, k+3, sh, 0);
		array.setBlock(i+11, j+1, k+6, sh, 0);
		array.setBlock(i+11, j+1, k+7, sh, 0);
		array.setBlock(i+11, j+1, k+8, sh, 0);
		array.setBlock(i+11, j+1, k+11, sh, 0);
		array.setBlock(i+11, j+1, k+12, sh, 0);
		array.setBlock(i+11, j+1, k+13, sh, 0);
		array.setBlock(i+11, j+2, k+0, sh, 0);
		array.setBlock(i+11, j+2, k+1, ch);
		array.setBlock(i+11, j+2, k+2, ch);
		array.setBlock(i+11, j+2, k+3, ch);
		array.setBlock(i+11, j+2, k+4, sh, 0);
		array.setBlock(i+11, j+2, k+5, sh, 0);
		array.setFluid(i+11, j+2, k+6, er);
		array.setFluid(i+11, j+2, k+7, er);
		array.setFluid(i+11, j+2, k+8, er);
		array.setBlock(i+11, j+2, k+9, sh, 0);
		array.setBlock(i+11, j+2, k+10, sh, 0);
		array.setBlock(i+11, j+2, k+11, ch);
		array.setBlock(i+11, j+2, k+12, ch);
		array.setBlock(i+11, j+2, k+13, ch);
		array.setBlock(i+11, j+2, k+14, sh, 0);
		array.setBlock(i+11, j+3, k+0, b, 0);
		array.setBlock(i+11, j+3, k+2, ch, 1);
		array.setBlock(i+11, j+3, k+4, b, 15);
		array.setBlock(i+11, j+3, k+5, b, 12);
		array.setBlock(i+11, j+3, k+9, b, 12);
		array.setBlock(i+11, j+3, k+10, b, 15);
		array.setBlock(i+11, j+3, k+12, ch, 1);
		array.setBlock(i+11, j+3, k+14, b, 0);
		array.setBlock(i+11, j+4, k+2, ch, 1);
		array.setBlock(i+11, j+4, k+12, ch, 1);
		array.setBlock(i+11, j+5, k+2, ch, 1);
		array.setBlock(i+11, j+5, k+12, ch, 1);
		array.setBlock(i+11, j+6, k+2, ch, 1);
		array.setBlock(i+11, j+6, k+12, ch, 1);
		array.setBlock(i+12, j+1, k+1, sh, 0);
		array.setBlock(i+12, j+1, k+2, sh, 0);
		array.setBlock(i+12, j+1, k+3, sh, 0);
		array.setBlock(i+12, j+1, k+6, sh, 0);
		array.setBlock(i+12, j+1, k+7, sh, 0);
		array.setBlock(i+12, j+1, k+8, sh, 0);
		array.setBlock(i+12, j+1, k+11, sh, 0);
		array.setBlock(i+12, j+1, k+12, sh, 0);
		array.setBlock(i+12, j+1, k+13, sh, 0);
		array.setBlock(i+12, j+2, k+0, sh, 0);
		array.setBlock(i+12, j+2, k+1, ch);
		array.setBlock(i+12, j+2, k+2, b, 0);
		array.setBlock(i+12, j+2, k+3, ch);
		array.setBlock(i+12, j+2, k+4, sh, 0);
		array.setBlock(i+12, j+2, k+5, sh, 0);
		array.setFluid(i+12, j+2, k+6, er);
		array.setFluid(i+12, j+2, k+7, er);
		array.setFluid(i+12, j+2, k+8, er);
		array.setBlock(i+12, j+2, k+9, sh, 0);
		array.setBlock(i+12, j+2, k+10, sh, 0);
		array.setBlock(i+12, j+2, k+11, ch);
		array.setBlock(i+12, j+2, k+12, b, 0);
		array.setBlock(i+12, j+2, k+13, ch);
		array.setBlock(i+12, j+2, k+14, sh, 0);
		array.setBlock(i+12, j+3, k+0, b, 0);
		array.setBlock(i+12, j+3, k+1, ch, 1);
		array.setBlock(i+12, j+3, k+2, b, 0);
		array.setBlock(i+12, j+3, k+3, ch, 1);
		array.setBlock(i+12, j+3, k+4, b, 15);
		array.setBlock(i+12, j+3, k+5, b, 12);
		array.setBlock(i+12, j+3, k+9, b, 12);
		array.setBlock(i+12, j+3, k+10, b, 15);
		array.setBlock(i+12, j+3, k+11, ch, 1);
		array.setBlock(i+12, j+3, k+12, b, 0);
		array.setBlock(i+12, j+3, k+13, ch, 1);
		array.setBlock(i+12, j+3, k+14, b, 0);
		array.setBlock(i+12, j+4, k+1, ch, 1);
		array.setBlock(i+12, j+4, k+2, b, 0);
		array.setBlock(i+12, j+4, k+3, ch, 1);
		array.setBlock(i+12, j+4, k+11, ch, 1);
		array.setBlock(i+12, j+4, k+12, b, 0);
		array.setBlock(i+12, j+4, k+13, ch, 1);
		array.setBlock(i+12, j+5, k+1, ch, 1);
		array.setBlock(i+12, j+5, k+2, b, 0);
		array.setBlock(i+12, j+5, k+3, ch, 1);
		array.setBlock(i+12, j+5, k+11, ch, 1);
		array.setBlock(i+12, j+5, k+12, b, 0);
		array.setBlock(i+12, j+5, k+13, ch, 1);
		array.setBlock(i+12, j+6, k+1, ch, 1);
		array.setBlock(i+12, j+6, k+2, ch);
		array.setBlock(i+12, j+6, k+3, ch, 1);
		array.setBlock(i+12, j+6, k+11, ch, 1);
		array.setBlock(i+12, j+6, k+12, ch);
		array.setBlock(i+12, j+6, k+13, ch, 1);
		array.setBlock(i+13, j+1, k+1, sh, 0);
		array.setBlock(i+13, j+1, k+2, sh, 0);
		array.setBlock(i+13, j+1, k+3, sh, 0);
		array.setBlock(i+13, j+1, k+11, sh, 0);
		array.setBlock(i+13, j+1, k+12, sh, 0);
		array.setBlock(i+13, j+1, k+13, sh, 0);
		array.setBlock(i+13, j+2, k+0, sh, 0);
		array.setBlock(i+13, j+2, k+1, ch);
		array.setBlock(i+13, j+2, k+2, ch);
		array.setBlock(i+13, j+2, k+3, ch);
		array.setBlock(i+13, j+2, k+4, sh, 0);
		array.setBlock(i+13, j+2, k+6, sh, 0);
		array.setBlock(i+13, j+2, k+7, sh, 0);
		array.setBlock(i+13, j+2, k+8, sh, 0);
		array.setBlock(i+13, j+2, k+10, sh, 0);
		array.setBlock(i+13, j+2, k+11, ch);
		array.setBlock(i+13, j+2, k+12, ch);
		array.setBlock(i+13, j+2, k+13, ch);
		array.setBlock(i+13, j+2, k+14, sh, 0);
		array.setBlock(i+13, j+3, k+0, b, 0);
		array.setBlock(i+13, j+3, k+2, ch, 1);
		array.setBlock(i+13, j+3, k+4, b, 15);
		array.setBlock(i+13, j+3, k+5, b, 12);
		array.setBlock(i+13, j+3, k+6, b, 12);
		array.setBlock(i+13, j+3, k+7, b, 12);
		array.setBlock(i+13, j+3, k+8, b, 12);
		array.setBlock(i+13, j+3, k+9, b, 12);
		array.setBlock(i+13, j+3, k+10, b, 15);
		array.setBlock(i+13, j+3, k+12, ch, 1);
		array.setBlock(i+13, j+3, k+14, b, 0);
		array.setBlock(i+13, j+4, k+2, ch, 1);
		array.setBlock(i+13, j+4, k+12, ch, 1);
		array.setBlock(i+13, j+5, k+2, ch, 1);
		array.setBlock(i+13, j+5, k+12, ch, 1);
		array.setBlock(i+13, j+6, k+2, ch, 1);
		array.setBlock(i+13, j+6, k+12, ch, 1);
		array.setBlock(i+14, j+2, k+1, sh, 0);
		array.setBlock(i+14, j+2, k+2, sh, 0);
		array.setBlock(i+14, j+2, k+3, sh, 0);
		array.setBlock(i+14, j+2, k+11, sh, 0);
		array.setBlock(i+14, j+2, k+12, sh, 0);
		array.setBlock(i+14, j+2, k+13, sh, 0);
		array.setBlock(i+14, j+3, k+0, b, 0);
		array.setBlock(i+14, j+3, k+1, b, 0);
		array.setBlock(i+14, j+3, k+2, b, 0);
		array.setBlock(i+14, j+3, k+3, b, 0);
		array.setBlock(i+14, j+3, k+4, b, 0);
		array.setBlock(i+14, j+3, k+5, b, 15);
		array.setBlock(i+14, j+3, k+6, b, 15);
		array.setBlock(i+14, j+3, k+7, b, 15);
		array.setBlock(i+14, j+3, k+8, b, 15);
		array.setBlock(i+14, j+3, k+9, b, 15);
		array.setBlock(i+14, j+3, k+10, b, 0);
		array.setBlock(i+14, j+3, k+11, b, 0);
		array.setBlock(i+14, j+3, k+12, b, 0);
		array.setBlock(i+14, j+3, k+13, b, 0);
		array.setBlock(i+14, j+3, k+14, b, 0);
		array.setBlock(i+14, j+4, k+0, b, 2);
		array.setBlock(i+14, j+4, k+4, b, 2);
		array.setBlock(i+14, j+4, k+10, b, 2);
		array.setBlock(i+14, j+4, k+14, b, 2);
		array.setBlock(i+14, j+5, k+0, b, 3);
		array.setBlock(i+14, j+5, k+4, b, 13);
		array.setBlock(i+14, j+5, k+10, b, 13);
		array.setBlock(i+14, j+5, k+14, b, 3);
		array.setBlock(i+14, j+6, k+0, b, 2);
		array.setBlock(i+14, j+6, k+4, b, 2);
		array.setBlock(i+14, j+6, k+10, b, 2);
		array.setBlock(i+14, j+6, k+14, b, 2);
		array.setBlock(i+14, j+7, k+0, b, 3);
		array.setBlock(i+14, j+7, k+4, b, 7);
		array.setBlock(i+14, j+7, k+5, b, 1);
		array.setBlock(i+14, j+7, k+6, b, 1);
		array.setBlock(i+14, j+7, k+7, b, 1);
		array.setBlock(i+14, j+7, k+8, b, 1);
		array.setBlock(i+14, j+7, k+9, b, 1);
		array.setBlock(i+14, j+7, k+10, b, 7);
		array.setBlock(i+14, j+7, k+14, b, 3);
		array.setBlock(i+14, j+8, k+0, b, 2);
		array.setBlock(i+14, j+8, k+14, b, 2);
		array.setBlock(i+14, j+9, k+0, b, 5);
		array.setBlock(i+14, j+9, k+14, b, 5);

		if (display) {
			int mx = i+7;
			int mz = k+7;

			array.setBlock(mx-5, j+4, mz-9, Blocks.bedrock);
			array.setBlock(mx-9, j+4, mz-5, Blocks.bedrock);

			array.setBlock(mx+5, j+4, mz-9, Blocks.bedrock);
			array.setBlock(mx+9, j+4, mz-5, Blocks.bedrock);

			array.setBlock(mx-5, j+4, mz+9, Blocks.bedrock);
			array.setBlock(mx-9, j+4, mz+5, Blocks.bedrock);

			array.setBlock(mx+5, j+4, mz+9, Blocks.bedrock);
			array.setBlock(mx+9, j+4, mz+5, Blocks.bedrock);
		}

		return array;
	}

	public static FilledBlockArray getPersonalStructure(World world, int x, int y, int z, CrystalElement e) {
		FilledBlockArray array = new FilledBlockArray(world);

		Block b = ChromaBlocks.PYLONSTRUCT.getBlockInstance();
		Block r = ChromaBlocks.RUNE.getBlockInstance();

		for (int i = -1; i <= 1; i++) {
			for (int k = -1; k <= 1; k++) {
				array.setBlock(x+i, y, z+k, b, 0);
			}
		}
		array.setBlock(x, y, z, b, 14);

		for (int i = 1; i <= 4; i++) {
			int m = i == 4 ? 7 : 2;
			array.setBlock(x+1, y+i, z+1, b, m);
			array.setBlock(x-1, y+i, z+1, b, m);
			array.setBlock(x+1, y+i, z-1, b, m);
			array.setBlock(x-1, y+i, z-1, b, m);
		}

		array.setBlock(x+2, y, z+2, b, 12);
		array.setBlock(x-2, y, z+2, b, 12);
		array.setBlock(x+2, y, z-2, b, 12);
		array.setBlock(x-2, y, z-2, b, 12);

		array.setBlock(x+2, y+1, z+2, b, 2);
		array.setBlock(x-2, y+1, z+2, b, 2);
		array.setBlock(x+2, y+1, z-2, b, 2);
		array.setBlock(x-2, y+1, z-2, b, 2);

		array.setBlock(x+2, y+2, z+2, r, e.ordinal());
		array.setBlock(x-2, y+2, z+2, r, e.ordinal());
		array.setBlock(x+2, y+2, z-2, r, e.ordinal());
		array.setBlock(x-2, y+2, z-2, r, e.ordinal());

		for (int i = -1; i <= 1; i++) {
			array.setBlock(x+2, y, z+i, b, 11);
			array.setBlock(x-2, y, z+i, b, 11);

			array.setBlock(x+i, y, z+2, b, 10);
			array.setBlock(x+i, y, z-2, b, 10);
		}

		array.setBlock(x, y+6, z, ChromaTiles.PERSONAL.getBlock(), ChromaTiles.PERSONAL.getBlockMetadata());

		return array;
	}

	public static FilledBlockArray getProtectionBeaconStructure(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);

		Block b = ChromaBlocks.PYLONSTRUCT.getBlockInstance();

		for (int i = -2; i <= 2; i++) {
			for (int k = -2; k <= 2; k++) {
				array.setBlock(x+i, y-1, z+k, b, StoneTypes.SMOOTH.ordinal());
			}
		}

		for (int i = -3; i <= 3; i++) {
			for (int k = -3; k <= 3; k++) {
				if (i != 0 || k != 0) {
					array.setEmpty(x+i, y, z+k, false, false);
					array.setEmpty(x+i, y+1, z+k, false, false);
				}
			}
		}

		for (int i = -2; i <= 2; i++) {
			array.setBlock(x+3, y-1, z+i, b, StoneTypes.GROOVE2.ordinal());
			array.setBlock(x-3, y-1, z+i, b, StoneTypes.GROOVE2.ordinal());
			array.setBlock(x+i, y-1, z+3, b, StoneTypes.GROOVE1.ordinal());
			array.setBlock(x+i, y-1, z-3, b, StoneTypes.GROOVE1.ordinal());
		}

		array.setBlock(x-3, y-1, z-3, b, StoneTypes.CORNER.ordinal());
		array.setBlock(x+3, y-1, z-3, b, StoneTypes.CORNER.ordinal());
		array.setBlock(x-3, y-1, z+3, b, StoneTypes.CORNER.ordinal());
		array.setBlock(x+3, y-1, z+3, b, StoneTypes.CORNER.ordinal());

		array.setBlock(x-2, y, z-2, b, StoneTypes.COLUMN.ordinal());
		array.setBlock(x+2, y, z-2, b, StoneTypes.COLUMN.ordinal());
		array.setBlock(x-2, y, z+2, b, StoneTypes.COLUMN.ordinal());
		array.setBlock(x+2, y, z+2, b, StoneTypes.COLUMN.ordinal());

		array.setBlock(x-2, y+1, z-2, b, StoneTypes.FOCUS.ordinal());
		array.setBlock(x+2, y+1, z-2, b, StoneTypes.FOCUS.ordinal());
		array.setBlock(x-2, y+1, z+2, b, StoneTypes.FOCUS.ordinal());
		array.setBlock(x+2, y+1, z+2, b, StoneTypes.FOCUS.ordinal());

		array.setBlock(x, y, z, ChromaTiles.BEACON.getBlock(), ChromaTiles.BEACON.getBlockMetadata());

		array.addBlock(x+1, y, z, ChromaBlocks.ADJACENCY.getBlockInstance());
		array.addBlock(x-1, y, z, ChromaBlocks.ADJACENCY.getBlockInstance());
		array.addBlock(x, y, z+1, ChromaBlocks.ADJACENCY.getBlockInstance());
		array.addBlock(x, y, z-1, ChromaBlocks.ADJACENCY.getBlockInstance());

		return array;
	}

	public static FilledBlockArray getWeakRepeaterStructure(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);

		array.setBlock(x, y, z, ChromaTiles.WEAKREPEATER.getBlock(), ChromaTiles.WEAKREPEATER.getBlockMetadata());
		for (int i = 0; i < ReikaTreeHelper.treeList.length; i++) {
			ReikaTreeHelper tree = ReikaTreeHelper.treeList[i];
			array.addBlock(x, y-1, z, tree.getLogID(), tree.getLogMetadatas().get(0));
		}
		for (int i = 0; i < ModWoodList.woodList.length; i++) {
			ModWoodList tree = ModWoodList.woodList[i];
			if (tree.exists() && tree != ModWoodList.SLIME) {
				array.addBlock(x, y-1, z, tree.getLogID(), tree.getLogMetadatas().get(0));
			}
		}

		return array;
	}

	public static FilledBlockArray getMeteorTowerStructure(World world, int x, int y, int z, int tier) {
		FilledBlockArray array = new FilledBlockArray(world);

		Block b = ChromaBlocks.PYLONSTRUCT.getBlockInstance();

		for (int j = 12; j <= 14; j++) {
			int dy = y-j;
			for (int i = -1; i <= 1; i++) {
				for (int k = -1; k <= 1; k++) {
					array.setBlock(x+i, dy, z+k, b, 0);
				}
			}
			for (int i = -2; i <= 2; i++) {
				int ml = j == 13 ? StoneTypes.RESORING.ordinal() : StoneTypes.GROOVE1.ordinal();
				int mc = j == 13 ? StoneTypes.COLUMN.ordinal() : StoneTypes.CORNER.ordinal();
				array.setBlock(x-2, dy, z+i, b, Math.abs(i) == 2 ? mc : ml);
				array.setBlock(x+2, dy, z+i, b, Math.abs(i) == 2 ? mc : ml);
				array.setBlock(x+i, dy, z-2, b, Math.abs(i) == 2 ? mc : ml);
				array.setBlock(x+i, dy, z+2, b, Math.abs(i) == 2 ? mc : ml);
			}
		}

		int[][] cols = {{-2, -1}, {-2, 1}, {-1, 2}, {-1, -2}, {2, -1}, {2, 1}, {1, -2}, {1, 2}};

		for (int j = 2; j <= 11; j++) {
			int dy = y-j;
			for (int a = 0; a < cols.length; a++) {
				int[] col = cols[a];
				int dx = x+col[0];
				int dz = z+col[1];
				int m = j == 4 || j == 7 || j == 11 ? StoneTypes.BRICKS.ordinal() : StoneTypes.COLUMN.ordinal();
				if (j == 9 && tier == 2)
					m = StoneTypes.GLOWCOL.ordinal();
				array.setBlock(dx, dy, dz, b, m);
			}
		}

		for (int i = -1; i <= 1; i++) {
			array.setBlock(x-2, y-1, z+i, b, StoneTypes.BRICKS.ordinal());
			array.setBlock(x+2, y-1, z+i, b, StoneTypes.BRICKS.ordinal());
			array.setBlock(x+i, y-1, z-2, b, StoneTypes.BRICKS.ordinal());
			array.setBlock(x+i, y-1, z+2, b, StoneTypes.BRICKS.ordinal());
		}

		for (int i = -1; i <= 1; i++) {
			for (int k = -1; k <= 1; k++) {
				int dx = x+i;
				int dz = z+k;
				if (i != 0 || k != 0) {
					array.setBlock(dx, y, dz, b, StoneTypes.BRICKS.ordinal());
				}
			}
		}

		TileEntityMeteorTower te = new TileEntityMeteorTower();
		ItemStack is = ChromaTiles.METEOR.getCraftedProduct();
		is.stackTagCompound = new NBTTagCompound();
		is.stackTagCompound.setInteger("tier", tier);
		te.setDataFromItemStackTag(is);
		array.setBlock(x, y, z, ChromaTiles.METEOR.getBlock(), ChromaTiles.METEOR.getBlockMetadata(), te, "tier");

		for (int j = 1; j <= 2; j++) {
			int dy = y+j;
			for (int i = -1; i <= 1; i += 2) {
				for (int k = -1; k <= 1; k += 2) {
					int dx = x+i;
					int dz = z+k;
					int m = j == 1 ? StoneTypes.COLUMN.ordinal() : (tier == 0 ? StoneTypes.SMOOTH.ordinal() : StoneTypes.FOCUS.ordinal());
					array.setBlock(dx, dy, dz, b, m);
				}
			}
		}

		int[] h = {4, 7};

		for (int a = 0; a < h.length; a++) {
			int dy = y-h[a];
			int m = 0;
			switch(tier) {
				case 0:
				default:
					m = StoneTypes.SMOOTH.ordinal();
					break;
				case 1:
					m = StoneTypes.BEAM.ordinal();
					break;
				case 2:
					m = StoneTypes.GLOWBEAM.ordinal();
					break;
			}
			array.setBlock(x-2, dy, z, b, m);
			array.setBlock(x+2, dy, z, b, m);
			array.setBlock(x, dy, z-2, b, m);
			array.setBlock(x, dy, z+2, b, m);
		}

		if (tier > 0) {
			for (int j = h[0]; j <= h[1]; j++) {
				int dy = y-j;
				int m = j == h[0] || j == h[1] ? StoneTypes.SMOOTH.ordinal() : StoneTypes.STABILIZER.ordinal();
				array.setBlock(x-2, dy, z-2, b, m);
				array.setBlock(x+2, dy, z+2, b, m);
				if (tier == 2) {
					array.setBlock(x+2, dy, z-2, b, m);
					array.setBlock(x-2, dy, z+2, b, m);
				}
			}
		}

		CrystalElement e = null;
		switch(tier) {
			case 0:
			default:
				e = CrystalElement.LIME;
				break;
			case 1:
				e = CrystalElement.YELLOW;
				break;
			case 2:
				e = CrystalElement.RED;
				break;
		}

		ChromaCheck check = new ChromaCheck(e);
		array.setBlock(x, y-12, z, check);
		array.setBlock(x-1, y-12, z, check);
		array.setBlock(x+1, y-12, z, check);
		array.setBlock(x, y-12, z-1, check);
		array.setBlock(x, y-12, z+1, check);

		return array;
	}

	public static FilledBlockArray getGateStructure(World world, int x, int y, int z) {
		FilledBlockArray array = new FilledBlockArray(world);

		for (int i = -3; i <= 3; i++) {
			for (int j = 0; j <= 3; j++) {
				for (int k = -3; k <= 3; k++) {
					array.setBlock(x+i, y+j, z+k, Blocks.air);
				}
			}
		}

		int mb = StoneTypes.BRICKS.ordinal();
		int mc = StoneTypes.CORNER.ordinal();
		int mr = StoneTypes.RESORING.ordinal();
		int ms = StoneTypes.SMOOTH.ordinal();
		int ma = StoneTypes.STABILIZER.ordinal();
		int[][] metas = {
				{-2, -2, mb, mb, mb, mb, mb, -2, -2},
				{-2, mb, mb, ms, ms, ms, mb, mb, -2},
				{mb, mb, -1, -1, mr, -1, -1, mb, mb},
				{mb, ms, -1, mc, mr, mc, -1, ms, mb},
				{mb, ms, mr, mr, ma, mr, mr, ms, mb},
				{mb, ms, -1, mc, mr, mc, -1, ms, mb},
				{mb, mb, -1, -1, mr, -1, -1, mb, mb},
				{-2, mb, mb, ms, ms, ms, mb, mb, -2},
				{-2, -2, mb, mb, mb, mb, mb, -2, -2},
		};

		for (int i = 0; i < metas.length; i++) {
			for (int k = 0; k < metas.length; k++) {
				int m = metas[i][k];
				int dx = x+i-metas.length/2;
				int dz = z+k-metas.length/2;
				if (m >= 0) {
					array.setBlock(dx, y-1, dz, ChromaBlocks.PYLONSTRUCT.getBlockInstance(), m);
				}
				else if (m == -1) {
					array.setBlock(dx, y-1, dz, ChromaBlocks.STRUCTSHIELD.getBlockInstance(), BlockType.CLOAK.ordinal());
				}
			}
		}

		int[] m = {StoneTypes.COLUMN.ordinal(), StoneTypes.COLUMN.ordinal(), StoneTypes.GLOWCOL.ordinal(), StoneTypes.COLUMN.ordinal(), StoneTypes.CORNER.ordinal()};
		for (int i = 0; i < m.length; i++) {
			int meta = m[i];
			int dy = y+i;
			array.setBlock(x+4, dy, z, ChromaBlocks.PYLONSTRUCT.getBlockInstance(), meta);
			array.setBlock(x-4, dy, z, ChromaBlocks.PYLONSTRUCT.getBlockInstance(), meta);
			array.setBlock(x, dy, z+4, ChromaBlocks.PYLONSTRUCT.getBlockInstance(), meta);
			array.setBlock(x, dy, z-4, ChromaBlocks.PYLONSTRUCT.getBlockInstance(), meta);
		}

		array.setBlock(x+3, y+4, z, ChromaBlocks.PYLONSTRUCT.getBlockInstance(), StoneTypes.GLOWBEAM.ordinal());
		array.setBlock(x-3, y+4, z, ChromaBlocks.PYLONSTRUCT.getBlockInstance(), StoneTypes.GLOWBEAM.ordinal());
		array.setBlock(x, y+4, z-3, ChromaBlocks.PYLONSTRUCT.getBlockInstance(), StoneTypes.GLOWBEAM.ordinal());
		array.setBlock(x, y+4, z+3, ChromaBlocks.PYLONSTRUCT.getBlockInstance(), StoneTypes.GLOWBEAM.ordinal());

		array.setBlock(x+2, y+4, z, ChromaBlocks.PYLONSTRUCT.getBlockInstance(), StoneTypes.ENGRAVED.ordinal());
		array.setBlock(x-2, y+4, z, ChromaBlocks.PYLONSTRUCT.getBlockInstance(), StoneTypes.ENGRAVED.ordinal());
		array.setBlock(x, y+4, z-2, ChromaBlocks.PYLONSTRUCT.getBlockInstance(), StoneTypes.ENGRAVED.ordinal());
		array.setBlock(x, y+4, z+2, ChromaBlocks.PYLONSTRUCT.getBlockInstance(), StoneTypes.ENGRAVED.ordinal());

		array.setBlock(x, y, z, ChromaTiles.TELEPORT.getBlock(), ChromaTiles.TELEPORT.getBlockMetadata());

		return array;
	}

	public static FilledBlockArray getBoostedRelayStructure(World world, int x, int y, int z, boolean display) {
		FilledBlockArray array = new FilledBlockArray(world);

		for (int i = -2; i <= 2; i++) {
			for (int k = -2; k <= 2; k++) {
				array.setBlock(x+i, y-2, z+k, ChromaBlocks.PYLONSTRUCT.getBlockInstance(), StoneTypes.SMOOTH.ordinal());
			}
		}

		array.setBlock(x-1, y-2, z, ChromaBlocks.PYLONSTRUCT.getBlockInstance(), StoneTypes.GROOVE1.ordinal());
		array.setBlock(x+1, y-2, z, ChromaBlocks.PYLONSTRUCT.getBlockInstance(), StoneTypes.GROOVE1.ordinal());
		array.setBlock(x, y-2, z-1, ChromaBlocks.PYLONSTRUCT.getBlockInstance(), StoneTypes.GROOVE2.ordinal());
		array.setBlock(x, y-2, z+1, ChromaBlocks.PYLONSTRUCT.getBlockInstance(), StoneTypes.GROOVE2.ordinal());

		array.setBlock(x+1, y-2, z+1, ChromaBlocks.CHROMA.getBlockInstance(), 0);
		array.setBlock(x-1, y-2, z+1, ChromaBlocks.CHROMA.getBlockInstance(), 0);
		array.setBlock(x+1, y-2, z-1, ChromaBlocks.CHROMA.getBlockInstance(), 0);
		array.setBlock(x-1, y-2, z-1, ChromaBlocks.CHROMA.getBlockInstance(), 0);

		array.setBlock(x+1, y-3, z+1, ChromaBlocks.PYLONSTRUCT.getBlockInstance(), StoneTypes.SMOOTH.ordinal());
		array.setBlock(x-1, y-3, z+1, ChromaBlocks.PYLONSTRUCT.getBlockInstance(), StoneTypes.SMOOTH.ordinal());
		array.setBlock(x+1, y-3, z-1, ChromaBlocks.PYLONSTRUCT.getBlockInstance(), StoneTypes.SMOOTH.ordinal());
		array.setBlock(x-1, y-3, z-1, ChromaBlocks.PYLONSTRUCT.getBlockInstance(), StoneTypes.SMOOTH.ordinal());

		for (int i = -2; i <= 2; i++) {
			for (int k = -2; k <= 2; k++) {
				array.setEmpty(x+i, y-1, z+k, false, false);
			}
		}

		array.setBlock(x, y-1, z, ChromaBlocks.PYLONSTRUCT.getBlockInstance(), StoneTypes.FOCUSFRAME.ordinal());

		array.setBlock(x-2, y-1, z, ChromaBlocks.PYLONSTRUCT.getBlockInstance(), StoneTypes.BRICKS.ordinal());
		array.setBlock(x+2, y-1, z, ChromaBlocks.PYLONSTRUCT.getBlockInstance(), StoneTypes.BRICKS.ordinal());
		array.setBlock(x, y-1, z-2, ChromaBlocks.PYLONSTRUCT.getBlockInstance(), StoneTypes.BRICKS.ordinal());
		array.setBlock(x, y-1, z+2, ChromaBlocks.PYLONSTRUCT.getBlockInstance(), StoneTypes.BRICKS.ordinal());

		array.setBlock(x+2, y-1, z+2, ChromaBlocks.PYLONSTRUCT.getBlockInstance(), StoneTypes.EMBOSSED.ordinal());
		array.setBlock(x-2, y-1, z+2, ChromaBlocks.PYLONSTRUCT.getBlockInstance(), StoneTypes.EMBOSSED.ordinal());
		array.setBlock(x+2, y-1, z-2, ChromaBlocks.PYLONSTRUCT.getBlockInstance(), StoneTypes.EMBOSSED.ordinal());
		array.setBlock(x-2, y-1, z-2, ChromaBlocks.PYLONSTRUCT.getBlockInstance(), StoneTypes.EMBOSSED.ordinal());

		TileEntityRelaySource te = new TileEntityRelaySource();
		if (display)
			te.setEnhanced(true);
		array.setBlock(x, y, z, ChromaTiles.RELAYSOURCE.getBlock(), ChromaTiles.RELAYSOURCE.getBlockMetadata(), te, "enhance");
		return array;
	}

	public static FilledBlockArray getPylonBroadcastStructure(World world, int x, int y, int z, CrystalElement e) {
		FilledBlockArray array = getPylonStructure(world, x, y, z, e);
		y -= 9;

		Block b = ChromaBlocks.PYLONSTRUCT.getBlockInstance();

		array.setBlock(x, y, z, b, StoneTypes.STABILIZER.ordinal());

		for (int i = 1; i <= 2; i++) {
			array.setBlock(x+i, y, z, b, StoneTypes.RESORING.ordinal());
			array.setBlock(x-i, y, z, b, StoneTypes.RESORING.ordinal());
			array.setBlock(x, y, z+i, b, StoneTypes.RESORING.ordinal());
			array.setBlock(x, y, z-i, b, StoneTypes.RESORING.ordinal());
		}

		for (int i = -3; i <= 3; i++) {
			int m = Math.abs(i) == 3 || i == 0 ? StoneTypes.EMBOSSED.ordinal() : StoneTypes.BRICKS.ordinal();
			array.setBlock(x+i, y, z+5, b, m);
			array.setBlock(x+i, y, z-5, b, m);
			array.setBlock(x+5, y, z+i, b, m);
			array.setBlock(x-5, y, z+i, b, m);
		}


		for (int i = -2; i <= 2; i++) {
			array.setBlock(x+i, y, z+4, ChromaBlocks.CHROMA.getBlockInstance(), 0);
			array.setBlock(x+i, y, z-4, ChromaBlocks.CHROMA.getBlockInstance(), 0);
			array.setBlock(x+4, y, z+i, ChromaBlocks.CHROMA.getBlockInstance(), 0);
			array.setBlock(x-4, y, z+i, ChromaBlocks.CHROMA.getBlockInstance(), 0);

			array.setBlock(x+i, y-1, z+4, b, 0);
			array.setBlock(x+i, y-1, z-4, b, 0);
			array.setBlock(x+4, y-1, z+i, b, 0);
			array.setBlock(x-4, y-1, z+i, b, 0);
		}

		for (int i = 3; i <= 4; i++) {
			array.setBlock(x+i, y, z+3, b, StoneTypes.BRICKS.ordinal());
			array.setBlock(x+i, y, z-3, b, StoneTypes.BRICKS.ordinal());
			array.setBlock(x-i, y, z+3, b, StoneTypes.BRICKS.ordinal());
			array.setBlock(x-i, y, z-3, b, StoneTypes.BRICKS.ordinal());
			array.setBlock(x+3, y, z+i, b, StoneTypes.BRICKS.ordinal());
			array.setBlock(x-3, y, z+i, b, StoneTypes.BRICKS.ordinal());
			array.setBlock(x+3, y, z-i, b, StoneTypes.BRICKS.ordinal());
			array.setBlock(x-3, y, z-i, b, StoneTypes.BRICKS.ordinal());
		}

		for (int i = 2; i <= 3; i++) {
			array.setBlock(x+i, y, z+2, ChromaBlocks.CHROMA.getBlockInstance(), 0);
			array.setBlock(x+i, y, z-2, ChromaBlocks.CHROMA.getBlockInstance(), 0);
			array.setBlock(x-i, y, z+2, ChromaBlocks.CHROMA.getBlockInstance(), 0);
			array.setBlock(x-i, y, z-2, ChromaBlocks.CHROMA.getBlockInstance(), 0);
			array.setBlock(x+2, y, z+i, ChromaBlocks.CHROMA.getBlockInstance(), 0);
			array.setBlock(x-2, y, z+i, ChromaBlocks.CHROMA.getBlockInstance(), 0);
			array.setBlock(x+2, y, z-i, ChromaBlocks.CHROMA.getBlockInstance(), 0);
			array.setBlock(x-2, y, z-i, ChromaBlocks.CHROMA.getBlockInstance(), 0);

			array.setBlock(x+i, y-1, z+2, b, 0);
			array.setBlock(x+i, y-1, z-2, b, 0);
			array.setBlock(x-i, y-1, z+2, b, 0);
			array.setBlock(x-i, y-1, z-2, b, 0);
			array.setBlock(x+2, y-1, z+i, b, 0);
			array.setBlock(x-2, y-1, z+i, b, 0);
			array.setBlock(x+2, y-1, z-i, b, 0);
			array.setBlock(x-2, y-1, z-i, b, 0);
		}

		for (int i = 1; i <= 4; i++) {
			int m = i == 4 ? StoneTypes.MULTICHROMIC.ordinal() : StoneTypes.COLUMN.ordinal();
			array.setBlock(x-3, y+i, z-5, b, m);
			array.setBlock(x-5, y+i, z-3, b, m);
			array.setBlock(x+3, y+i, z-5, b, m);
			array.setBlock(x+5, y+i, z-3, b, m);
			array.setBlock(x-3, y+i, z+5, b, m);
			array.setBlock(x-5, y+i, z+3, b, m);
			array.setBlock(x+3, y+i, z+5, b, m);
			array.setBlock(x+5, y+i, z+3, b, m);
		}

		for (int i = 1; i <= 6; i++) {
			int m = i == 3 ? StoneTypes.GLOWCOL.ordinal() : (i == 6 ? StoneTypes.FOCUS.ordinal() : StoneTypes.COLUMN.ordinal());
			array.setBlock(x+5, y+i, z, b, m);
			array.setBlock(x-5, y+i, z, b, m);
			array.setBlock(x, y+i, z+5, b, m);
			array.setBlock(x, y+i, z-5, b, m);
		}

		return array;
	}

	public static FilledBlockArray getPylonTurboStructure(World world, int x, int y, int z, CrystalElement e) {
		FilledBlockArray array = getPylonStructure(world, x, y, z, e);
		y -= 9;

		array.setBlock(x, y+9, z,  ChromaTiles.PYLON.getBlock(), ChromaTiles.PYLON.getBlockMetadata());

		Block b = ChromaBlocks.PYLONSTRUCT.getBlockInstance();

		BlockKey[] col = new BlockKey[]{
				new BlockKey(b, StoneTypes.COLUMN.ordinal()),
				new BlockKey(b, StoneTypes.FOCUS.ordinal()),
				new BlockKey(ChromaTiles.PYLONTURBO.getBlock(), ChromaTiles.PYLONTURBO.getBlockMetadata()),
		};

		for (int l = 0; l < Location.list.length; l++) {
			Location loc = Location.list[l];
			Coordinate c = loc.position;
			for (int i = 0; i < col.length; i++) {
				array.setBlock(x+c.xCoord, y+1+i, z+c.zCoord, col[i].blockID, col[i].metadata);
			}
		}

		for (Coordinate c : TileEntityCrystalPylon.getPowerCrystalLocations()) {
			array.setBlock(x+c.xCoord, y+9+c.yCoord, z+c.zCoord, ChromaTiles.CRYSTAL.getBlock(), ChromaTiles.CRYSTAL.getBlockMetadata());
		}

		array.setBlock(x, y+1, z, ChromaTiles.PYLONTURBO.getBlock(), ChromaTiles.PYLONTURBO.getBlockMetadata());

		return array;
	}

}
