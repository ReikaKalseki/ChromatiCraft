/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Aura;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.Maps.BlockMap;

public class BaseAura {

	private static final HashMap<BiomeGenBase, ElementTagCompound> biomeAura = new HashMap();
	private static final BlockMap<ElementTagCompound> blockAura = new BlockMap();

	static {
		addBiomeAura(CrystalElement.CYAN, 5, Type.OCEAN);
		addBiomeAura(CrystalElement.ORANGE, 5, Type.NETHER);

		addBlockAura(Blocks.bedrock, CrystalElement.RED, 10);
	}

	private static void addBlockAura(Block b, CrystalElement e, int amt) {
		addBlockAura(b, -1, e, amt);
	}

	private static void addBlockAura(Block b, int meta, CrystalElement e, int amt) {
		ElementTagCompound tag = blockAura.get(b, meta);
		if (tag == null) {
			tag = new ElementTagCompound();
			blockAura.put(b, meta, tag);
		}
		tag.addValueToColor(e, amt);
	}

	private static void addBiomeAura(CrystalElement e, int amt, BiomeDictionary.Type type) {
		addBiomeAura(e, amt, BiomeDictionary.getBiomesForType(type));
	}

	private static void addBiomeAura(CrystalElement e, int amt, BiomeGenBase... biomes) {
		for (int i = 0; i < biomes.length; i++) {
			ElementTagCompound tag = biomeAura.get(biomes[i]);
			if (tag == null) {
				tag = new ElementTagCompound();
				biomeAura.put(biomes[i], tag);
			}
			tag.addValueToColor(e, amt);
		}
	}

	public static ElementTagCompound getBiomeBase(BiomeGenBase biome) {
		ElementTagCompound tag = new ElementTagCompound();
		Type[] types = BiomeDictionary.getTypesForBiome(biome);
		for (int i = 0; i < types.length; i++) {
			ElementTagCompound tag2 = biomeAura.get(types[i]);
			if (tag2 != null) {
				tag.addTag(tag2);
			}
		}
		return tag;
	}

	public static ElementTagCompound getBlockBase(Block b, int meta) {
		ElementTagCompound tag = blockAura.get(b, meta);
		return tag != null ? tag : new ElementTagCompound();
	}

	public static ElementTagCompound getBaseAura(World world, int x, int y, int z) {
		ElementTagCompound tag = new ElementTagCompound();
		BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
		tag.addTag(getBiomeBase(biome));
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			if (dy > 0 && dy < 256)
				tag.addTag(getBlockBase(world.getBlock(dx, dy, dz), world.getBlockMetadata(dx, dy, dz)));
		}
		return tag;
	}

}
