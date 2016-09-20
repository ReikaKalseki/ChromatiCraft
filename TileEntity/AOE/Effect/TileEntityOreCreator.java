/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.AOE.Effect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Interfaces.Registry.OreType;
import Reika.DragonAPI.Interfaces.Registry.OreType.OreLocation;
import Reika.DragonAPI.Interfaces.Registry.OreType.OreRarity;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaOreHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.RailcraftHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.TinkerBlockHandler;
import Reika.DragonAPI.ModRegistry.ModOreList;


public class TileEntityOreCreator extends TileEntityAdjacencyUpgrade {

	private static final ArrayList<OreType>[][] oreLists = new ArrayList[OreRarity.values().length][3];

	private static final OreRarity[] rarities = {
		OreRarity.EVERYWHERE,
		OreRarity.EVERYWHERE,
		OreRarity.COMMON,
		OreRarity.AVERAGE,
		OreRarity.SCATTERED,
		OreRarity.SCARCE,
		OreRarity.SCARCE,
		OreRarity.RARE,
	};

	public static void initOreMap() {
		for (int k = 0; k < 3; k++) {
			OreLocation loc = OreLocation.list[k];
			for (int i = 0; i < oreLists.length; i++) {
				oreLists[i][k] = new ArrayList();
			}
			for (int i = 0; i < ReikaOreHelper.oreList.length; i++) {
				ReikaOreHelper ore = ReikaOreHelper.oreList[i];
				OreRarity r = ore.getRarity();
				EnumSet<OreLocation> locs = ore.getOreLocations();
				if (locs.contains(loc)) {
					oreLists[r.ordinal()][k].add(ore);
				}
			}
			for (int i = 0; i < ModOreList.oreList.length; i++) {
				ModOreList ore = ModOreList.oreList[i];
				if (ore.existsInGame()) {
					OreRarity r = ore.getRarity();
					EnumSet<OreLocation> locs = ore.getOreLocations();
					if (locs.contains(loc)) {
						oreLists[r.ordinal()][k].add(ore);
					}
				}
			}
		}
	}

	private static boolean isItemStackGenerationPermitted(ItemStack is) {
		if (ModList.TINKERER.isLoaded() && ReikaItemHelper.matchStackWithBlock(is, TinkerBlockHandler.getInstance().gravelOreID))
			return false;
		Block b = Block.getBlockFromItem(is.getItem());
		if (b == null)
			return false;
		if (b.getClass().getName().startsWith("shukaro.artifice")) //artifice ore variants
			return false;
		if (ModList.RAILCRAFT.isLoaded() && RailcraftHandler.getInstance().isDarkOre(b, is.getItemDamage()))
			return false;
		return true;
	}

	@Override
	protected boolean tickDirection(World world, int x, int y, int z, ForgeDirection dir, long startTime) {
		if (world.isRemote)
			return false;
		int dx = x+dir.offsetX;
		int dy = y+dir.offsetY;
		int dz = z+dir.offsetZ;
		Block b = world.getBlock(dx, dy, dz);
		OreLocation idx = this.getOreListIndex(world, dx, dy, dz, b);
		if (idx != null) {
			OreType type = this.tryCreateOre(world, dx, dy, dz, idx);
			if (type != null) {

			}
		}
		return true;
	}

	private OreType tryCreateOre(World world, int x, int y, int z, OreLocation loc) {
		int tier = this.getTier();
		if (ReikaRandomHelper.doWithChance(this.getOreChance(tier))) {
			OreRarity max = this.getMaxSpawnableRarity(tier);
			int list = rand.nextInt(1+max.ordinal());
			ArrayList<OreType>[] arr = oreLists[list];
			ArrayList<OreType> li = arr[loc.ordinal()];
			if (li.isEmpty())
				return null;
			OreType ore = li.get(rand.nextInt(li.size()));
			this.doCreateOre(world, x, y, z, ore);
			return ore;
		}
		return null;
	}

	private void doCreateOre(World world, int x, int y, int z, OreType ore) {
		Collection<ItemStack> li = ore.getAllOreBlocks();
		ItemStack is = ReikaJavaLibrary.getRandomCollectionEntry(li);
		if (!this.isItemStackGenerationPermitted(is))
			return;
		BlockKey b = ReikaItemHelper.getWorldBlockFromItem(is);
		if (b.blockID == Blocks.air)
			return;
		world.setBlock(x, y, z, b.blockID, b.metadata, 3);
		ReikaSoundHelper.playPlaceSound(world, x, y, z, b.blockID);
		ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.ORECREATE.ordinal(), world, x, y, z, Block.getIdFromBlock(b.blockID), b.metadata);
	}

	public static void doOreCreationFX(World world, int x, int y, int z, int id, int meta) {
		Block b = Block.getBlockById(id);
		ItemStack is = new ItemStack(b, 1, meta);
		ReikaSoundHelper.playPlaceSound(world, x, y, z, b);
		for (int i = 0; i < 2; i++)
			ReikaRenderHelper.spawnDropParticles(world, x, y, z, b, meta);
	}

	private OreLocation getOreListIndex(World world, int x, int y, int z, Block b) {
		if (b.isReplaceableOreGen(world, x, y, z, Blocks.stone))
			return OreLocation.OVERWORLD;
		if (b.isReplaceableOreGen(world, x, y, z, Blocks.netherrack))
			return OreLocation.NETHER;
		if (b.isReplaceableOreGen(world, x, y, z, Blocks.end_stone))
			return OreLocation.END;
		return null;
	}

	public static double getOreChance(int tier) {
		return 0.5/Math.pow(4, 8-tier);
	}

	public static OreRarity getMaxSpawnableRarity(int tier) {
		return rarities[tier];
	}

	@Override
	public CrystalElement getColor() {
		return CrystalElement.BROWN;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

}
