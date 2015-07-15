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

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.TileEntity.Plants.TileEntityCrystalPlant;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Interfaces.CustomCropHandler;
import Reika.DragonAPI.Interfaces.ModEntry;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

public class CrystalPlantHandler implements CustomCropHandler {

	@Override
	public int getHarvestedMeta(World world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z);
	}

	@Override
	public boolean isCrop(Block id, int meta) {
		return ModList.CHROMATICRAFT.isLoaded() && id == ChromaBlocks.PLANT.getBlockInstance();
	}

	@Override
	public boolean isRipeCrop(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		return ModList.CHROMATICRAFT.isLoaded() && te instanceof TileEntityCrystalPlant && ((TileEntityCrystalPlant)te).canHarvest();
	}

	@Override
	public void makeRipe(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (ModList.CHROMATICRAFT.isLoaded() && te instanceof TileEntityCrystalPlant) {
			((TileEntityCrystalPlant)te).makeRipe();
		}
	}

	@Override
	public boolean isSeedItem(ItemStack is) {
		return ModList.CHROMATICRAFT.isLoaded() && ChromaItems.SEED.matchWith(is);
	}

	@Override
	public ArrayList<ItemStack> getAdditionalDrops(World world, int x, int y, int z, Block id, int meta, int fortune) {
		if (ModList.CHROMATICRAFT.isLoaded() && ChromaOptions.CRYSTALFARM.getState() && ReikaRandomHelper.doWithChance(0.01)) {
			ArrayList li = new ArrayList();
			ItemStack shard = ChromaItems.SHARD.getStackOfMetadata(world.getBlockMetadata(x, y, z));
			li.add(shard);
			return li;
		}
		return null;
	}

	@Override
	public void editTileDataForHarvest(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (ModList.CHROMATICRAFT.isLoaded() && te instanceof TileEntityCrystalPlant) {
			((TileEntityCrystalPlant)te).harvest(false);
		}
	}

	@Override
	public boolean initializedProperly() {
		return ModList.CHROMATICRAFT.isLoaded() && ChromaBlocks.PLANT.getBlockInstance() != null;
	}

	@Override
	public ArrayList<ItemStack> getDropsOverride(World world, int x, int y, int z, Block id, int meta, int fortune) {
		return null;
	}

	@Override
	public int getGrowthState(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (ModList.CHROMATICRAFT.isLoaded() && te instanceof TileEntityCrystalPlant) {
			return ((TileEntityCrystalPlant)te).getGrowthState();
		}
		else {
			return 0;
		}
	}

	@Override
	public ModEntry getMod() {
		return ModList.CHROMATICRAFT;
	}

	@Override
	public int getColor() {
		return 0x00ff00;
	}

	@Override
	public String getEnumEntryName() {
		return "BLOOM";
	}

	@Override
	public boolean isTileEntity() {
		return true;
	}

}
