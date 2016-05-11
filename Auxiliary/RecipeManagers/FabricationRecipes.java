/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import Reika.ChromatiCraft.Block.BlockPylonStructure.StoneTypes;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.ModInterface.ChromaAspectManager;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Registry.ItemMagicRegistry;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

public class FabricationRecipes {

	private static final FabricationRecipes instance = new FabricationRecipes();

	private final HashMap<KeyedItemStack, ElementTagCompound> data = new HashMap();
	private static final float SCALE = 0.8F;

	public static final float INITFACTOR = 0.5F;
	public static final int FACTOR = 400;
	public static final int POWER = 5;

	private int max;

	public static FabricationRecipes recipes() {
		return instance;
	}

	private FabricationRecipes() {
		Collection<KeyedItemStack> c = ItemMagicRegistry.instance.keySet();
		for (KeyedItemStack k : c) {
			ElementTagCompound tag = ItemMagicRegistry.instance.getItemValue(k);
			tag.scale(INITFACTOR);
			tag.power(POWER);
			tag.scale(FACTOR);
			k.setSimpleHash(true);
			data.put(k, tag);
			max = Math.max(max, tag.getMaximumValue());
		}

		ElementTagCompound tag = new ElementTagCompound();
		tag.addTag(CrystalElement.RED, 5000);
		tag.addTag(CrystalElement.BROWN, 500);
		tag.addTag(CrystalElement.BLACK, 500);
		this.addRecipe(ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockStructureShield.BlockType.STONE.ordinal()), tag);
		this.addRecipe(ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockStructureShield.BlockType.COBBLE.ordinal()), tag);
		this.addRecipe(ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockStructureShield.BlockType.CRACK.ordinal()), tag);
		this.addRecipe(ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockStructureShield.BlockType.CRACKS.ordinal()), tag);

		ElementTagCompound tag2 = tag.copy();
		tag2.addTag(CrystalElement.WHITE, 500);
		this.addRecipe(ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockStructureShield.BlockType.GLASS.ordinal()), tag2);

		tag2 = tag.copy();
		tag2.addTag(CrystalElement.WHITE, 500);
		tag2.addTag(CrystalElement.BLUE, 500);
		this.addRecipe(ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockStructureShield.BlockType.LIGHT.ordinal()), tag2);

		tag2 = tag.copy();
		tag2.addTag(CrystalElement.GREEN, 500);
		this.addRecipe(ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockStructureShield.BlockType.MOSS.ordinal()), tag2);

		tag2 = tag.copy();
		tag2.addTag(CrystalElement.BLUE, 500);
		this.addRecipe(ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockStructureShield.BlockType.CLOAK.ordinal()), tag2);

		tag = new ElementTagCompound();
		tag.addTag(CrystalElement.BROWN, 500);
		tag.addTag(CrystalElement.BLACK, 10000);
		tag.addTag(CrystalElement.YELLOW, 25000);
		this.addRecipe(ChromaBlocks.PYLONSTRUCT.getStackOfMetadata(StoneTypes.GLOWBEAM.ordinal()), tag);
		this.addRecipe(ChromaBlocks.PYLONSTRUCT.getStackOfMetadata(StoneTypes.GLOWCOL.ordinal()), tag);
		this.addRecipe(ChromaBlocks.PYLONSTRUCT.getStackOfMetadata(StoneTypes.FOCUS.ordinal()), tag);

		if (ModList.THAUMCRAFT.isLoaded()) {
			ItemStack item = ReikaItemHelper.lookupItem(ModList.THAUMCRAFT, "ItemEldritchObject", 3);
			if (item != null) {
				tag = new ElementTagCompound();
				HashSet<CrystalElement> set = new HashSet();
				set.addAll(ChromaAspectManager.instance.getColorsForAspect(Aspect.AIR, true));
				set.addAll(ChromaAspectManager.instance.getColorsForAspect(Aspect.FIRE, true));
				set.addAll(ChromaAspectManager.instance.getColorsForAspect(Aspect.WATER, true));
				set.addAll(ChromaAspectManager.instance.getColorsForAspect(Aspect.ENTROPY, true));
				set.addAll(ChromaAspectManager.instance.getColorsForAspect(Aspect.EARTH, true));
				set.addAll(ChromaAspectManager.instance.getColorsForAspect(Aspect.ORDER, true));
				for (CrystalElement e : set)
					tag.addTag(e, 1000000);
				tag.addTag(CrystalElement.PINK, 100000);
				tag.addTag(CrystalElement.BLACK, 100000);
				this.addRecipe(item, tag);
			}
		}

	}

	private void addRecipe(ItemStack is, ElementTagCompound tag) {
		data.put(new KeyedItemStack(is).setSimpleHash(true), tag);
		max = Math.max(max, tag.getMaximumValue());
	}

	public Collection<ItemStack> getItemsFabricableWith(ElementTagCompound tag) {
		Collection<ItemStack> items = new ArrayList();
		for (KeyedItemStack ks : data.keySet()) {
			ElementTagCompound val = data.get(ks).copy().scale(SCALE);
			if (tag.containsAtLeast(val))
				items.add(ks.getItemStack());
		}
		return items;
	}

	public boolean isItemFabricable(ItemStack is, ElementTagCompound tag) {
		KeyedItemStack ks = new KeyedItemStack(is);
		return data.containsKey(ks) && tag.containsAtLeast(this.getItemCost(ks));
	}

	public ElementTagCompound getItemCost(ItemStack is) {
		return this.getItemCost(new KeyedItemStack(is).setSimpleHash(true));
	}

	private ElementTagCompound getItemCost(KeyedItemStack is) {
		ElementTagCompound tag = data.get(is);
		return tag != null ? tag.copy().scale(1/SCALE) : null;
	}

	public int getMaximumCost() {
		return max;
	}

	public Collection<KeyedItemStack> getFabricableItems() {
		return Collections.unmodifiableCollection(data.keySet());
	}

}
