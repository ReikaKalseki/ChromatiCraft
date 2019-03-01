/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
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

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.oredict.OreDictionary;

import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Block.BlockPylonStructure.StoneTypes;
import Reika.ChromatiCraft.Block.Worldgen.BlockStructureShield;
import Reika.ChromatiCraft.Items.ItemTieredResource;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.ItemElementCalculator;
import Reika.ChromatiCraft.ModInterface.ThaumCraft.ChromaAspectManager;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Registry.ItemMagicRegistry;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Libraries.ReikaPlayerAPI;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.ModInteract.ItemHandlers.BloodMagicHandler;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;

public class FabricationRecipes {

	private static final FabricationRecipes instance = new FabricationRecipes();

	private final HashMap<KeyedItemStack, FabricationRecipe> data = new HashMap();
	private final HashMap<String, FabricationRecipe> fluidRecipes = new HashMap();

	public static final float SCALE = 0.8F;

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
			tag = this.processTag(tag);
			k.setSimpleHash(true);
			data.put(k, new FabricationRecipe(k.getItemStack(), tag));
			max = Math.max(max, tag.getMaximumValue());
		}

		for (int i = 0; i < ChromaItems.TIERED.getNumberMetadatas(); i++) {
			ItemStack is = ChromaItems.TIERED.getStackOfMetadata(i);
			ElementTagCompound tag = ItemElementCalculator.instance.getValueForItem(is);
			if (tag != null && !tag.isEmpty()) {
				tag = this.processTag(tag);
				ProgressStage level = ((ItemTieredResource)is.getItem()).getDiscoveryTier(is);
				float scale = 5;
				if (level.isGatedAfter(ProgressStage.CTM))
					scale = 50;
				else if (level.isGatedAfter(ProgressStage.STRUCTCOMPLETE))
					scale = 30;
				else if (level.isGatedAfter(ProgressStage.DIMENSION))
					scale = 20;
				else if (level.isGatedAfter(ProgressStage.PYLON) || level.isGatedAfter(ProgressStage.ABILITY))
					scale = 10;
				tag.scale(scale);
				this.addRecipe(is, tag, ProgressStage.CTM);
			}
		}

		ElementTagCompound tag = new ElementTagCompound();
		tag.addTag(CrystalElement.RED, 5000);
		tag.addTag(CrystalElement.BROWN, 500);
		tag.addTag(CrystalElement.BLACK, 500);
		this.addRecipe(ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockStructureShield.BlockType.STONE.ordinal()), tag, ProgressStage.ANYSTRUCT);
		this.addRecipe(ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockStructureShield.BlockType.COBBLE.ordinal()), tag, ProgressStage.ANYSTRUCT);
		this.addRecipe(ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockStructureShield.BlockType.CRACK.ordinal()), tag, ProgressStage.ANYSTRUCT);
		this.addRecipe(ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockStructureShield.BlockType.CRACKS.ordinal()), tag, ProgressStage.ANYSTRUCT);

		ElementTagCompound tag2 = tag.copy();
		tag2.addTag(CrystalElement.WHITE, 500);
		this.addRecipe(ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockStructureShield.BlockType.GLASS.ordinal()), tag2, ProgressStage.ANYSTRUCT);

		tag2 = tag.copy();
		tag2.addTag(CrystalElement.WHITE, 500);
		tag2.addTag(CrystalElement.BLUE, 500);
		this.addRecipe(ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockStructureShield.BlockType.LIGHT.ordinal()), tag2, ProgressStage.ANYSTRUCT);

		tag2 = tag.copy();
		tag2.addTag(CrystalElement.GREEN, 500);
		this.addRecipe(ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockStructureShield.BlockType.MOSS.ordinal()), tag2, ProgressStage.ANYSTRUCT);

		tag2 = tag.copy();
		tag2.addTag(CrystalElement.BLUE, 500);
		this.addRecipe(ChromaBlocks.STRUCTSHIELD.getStackOfMetadata(BlockStructureShield.BlockType.CLOAK.ordinal()), tag2, ProgressStage.ANYSTRUCT);

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

		if (ModList.BOTANIA.isLoaded()) {
			ItemStack item = ReikaItemHelper.lookupItem(ModList.BOTANIA, "manaResource", 5);
			if (item != null) {
				tag = new ElementTagCompound();
				tag.addValueToColor(CrystalElement.PINK, 40000);
				tag.addValueToColor(CrystalElement.BLACK, 20000);
				tag.addValueToColor(CrystalElement.GREEN, 20000);
				this.addRecipe(item, tag);
			}
		}

		if (ModList.BLOODMAGIC.isLoaded()) {
			Item item = BloodMagicHandler.getInstance().demonShardID;
			if (item != null) {
				tag = new ElementTagCompound();
				tag.addValueToColor(CrystalElement.PINK, 80000);
				tag.addValueToColor(CrystalElement.BLACK, 20000);
				tag.addValueToColor(CrystalElement.MAGENTA, 40000);
				tag.addValueToColor(CrystalElement.LIGHTGRAY, 20000);
				this.addRecipe(new ItemStack(item), tag);
			}

			item = BloodMagicHandler.getInstance().resourceID;
			if (item != null) {
				tag = new ElementTagCompound();
				tag.addValueToColor(CrystalElement.PINK, 20000);
				tag.addValueToColor(CrystalElement.BLACK, 5000);
				tag.addValueToColor(CrystalElement.MAGENTA, 10000);
				tag.addValueToColor(CrystalElement.LIGHTGRAY, 5000);
				this.addRecipe(new ItemStack(item, 1, BloodMagicHandler.BLUE_SHARD_META), tag);
				this.addRecipe(new ItemStack(item, 1, BloodMagicHandler.RED_SHARD_META), tag);
			}
		}

		if (ModList.THAUMICTINKER.isLoaded()) {
			ElementTagCompound nether = new ElementTagCompound();
			nether.addValueToColor(CrystalElement.ORANGE, 25000);
			nether.addValueToColor(CrystalElement.PINK, 25000);
			nether.addValueToColor(CrystalElement.BLACK, 10000);

			ElementTagCompound end = new ElementTagCompound();
			end.addValueToColor(CrystalElement.LIGHTGRAY, 25000);
			end.addValueToColor(CrystalElement.PINK, 25000);
			end.addValueToColor(CrystalElement.BLACK, 10000);

			this.addRecipe(ReikaItemHelper.lookupItem(ModList.THAUMICTINKER, "kamiResource", 6), nether);
			this.addRecipe(ReikaItemHelper.lookupItem(ModList.THAUMICTINKER, "kamiResource", 7), end);
		}

		if (ModList.APPENG.isLoaded()) {
			Block item = GameRegistry.findBlock(ModList.APPENG.modLabel, "tile.BlockSkyStone");
			if (item != null) {
				tag = new ElementTagCompound();
				tag.addValueToColor(CrystalElement.BROWN, 500);
				tag.addValueToColor(CrystalElement.LIME, 200);
				tag.addValueToColor(CrystalElement.WHITE, 100);
				this.addRecipe(new ItemStack(item, 1, 0), tag);
			}
		}

		if (ModList.FORBIDDENMAGIC.isLoaded()) {
			ItemStack item = ReikaItemHelper.lookupItem("ForbiddenMagic:NetherShard:1");
			if (item != null) {
				tag = new ElementTagCompound();
				tag.addValueToColor(CrystalElement.PINK, 500);
				tag.addValueToColor(CrystalElement.ORANGE, 1000);
				tag.addValueToColor(CrystalElement.LIGHTGRAY, 100);
				this.addRecipe(item, tag);
			}
		}

		if (ModList.TWILIGHT.isLoaded()) {
			ItemStack item = ReikaItemHelper.lookupItem("TwilightForest:item.charmOfLife1");
			if (item != null) {
				tag = new ElementTagCompound();
				tag.addValueToColor(CrystalElement.MAGENTA, 2500);
				tag.addValueToColor(CrystalElement.BLACK, 1000);
				this.addRecipe(item, tag, ProgressStage.TWILIGHT);
			}

			item = ReikaItemHelper.lookupItem("TwilightForest:tile.TFFirefly");
			if (item != null) {
				tag = new ElementTagCompound();
				tag.addValueToColor(CrystalElement.BLUE, 300);
				tag.addValueToColor(CrystalElement.GREEN, 60);
				this.addRecipe(item, tag, ProgressStage.TWILIGHT);
			}

			item = ReikaItemHelper.lookupItem(/*"TwilightForest:item.carminite"*/"TwilightForest:item.borerEssence");
			if (item != null) {
				tag = new ElementTagCompound();
				tag.addValueToColor(CrystalElement.PINK, 500);
				tag.addValueToColor(CrystalElement.BROWN, 100);
				tag.addValueToColor(CrystalElement.LIGHTGRAY, 50);
				this.addRecipe(item, tag, ProgressStage.TWILIGHT);
			}
		}

	}

	private void addOreDictRecipe(String ore, ElementTagCompound tag) {
		ArrayList<ItemStack> li = OreDictionary.getOres(ore);
		for (ItemStack is : li) {
			this.addRecipe(is, tag.copy());
		}
	}

	public ElementTagCompound processTag(ElementTagCompound tag) {
		tag = tag.copy();
		tag.scale(INITFACTOR);
		tag.power(POWER);
		tag.scale(FACTOR);
		return tag;
	}

	private FabricationRecipe addRecipe(ItemStack is, ElementTagCompound tag, ProgressStage... progress) {
		FabricationRecipe f = new FabricationRecipe(is, tag);
		for (ProgressStage p : progress)
			f.addProgress(p);
		data.put(new KeyedItemStack(is).setSimpleHash(true), f);
		max = Math.max(max, tag.getMaximumValue());
		return f;
	}

	private FabricationRecipe addFluidRecipe(ItemStack is, Fluid fluid, ElementTagCompound tag, ProgressStage... progress) {
		FabricationRecipe f = new FabricationRecipe(is, tag);
		for (ProgressStage p : progress)
			f.addProgress(p);
		fluidRecipes.put(fluid.getName(), f);
		if (fluid.getBlock() != null)
			f.displayItem = new ItemStack(fluid.getBlock());
		max = Math.max(max, tag.getMaximumValue());
		return f;
	}

	public Collection<ItemStack> getItemsFabricableWith(ElementTagCompound tag) {
		Collection<ItemStack> items = new ArrayList();
		for (KeyedItemStack ks : data.keySet()) {
			ElementTagCompound val = data.get(ks).getCost().scale(SCALE);
			if (tag.containsAtLeast(val))
				items.add(ks.getItemStack());
		}
		return items;
	}

	public boolean isItemFabricable(ItemStack is) {
		KeyedItemStack ks = new KeyedItemStack(is).setSimpleHash(true);
		return data.containsKey(ks);
	}

	public boolean isItemFabricable(ItemStack is, ElementTagCompound tag) {
		KeyedItemStack ks = new KeyedItemStack(is).setSimpleHash(true);
		return data.containsKey(ks) && tag.containsAtLeast(this.getItemCost(ks));
	}

	public FabricationRecipe getItemRecipe(ItemStack is) {
		return data.get(new KeyedItemStack(is).setSimpleHash(true));
	}

	private ElementTagCompound getItemCost(KeyedItemStack is) {
		FabricationRecipe tag = data.get(is);
		return tag != null ? tag.getCost().scale(1/SCALE) : null;
	}

	public int getMaximumCost() {
		return max;
	}

	public Collection<KeyedItemStack> getFabricableItems() {
		return Collections.unmodifiableCollection(data.keySet());
	}

	public static class FabricationRecipe {

		private final ItemStack item;
		private final ElementTagCompound energy;
		private Collection<ProgressStage> progress;

		private ItemStack displayItem;

		/*
		private FabricationRecipe(ItemStack is) {
			this(is, tag);
		}*/

		private FabricationRecipe(ItemStack is, ElementTagCompound tag) {
			item = is;
			energy = tag;
			displayItem = item;
		}

		private FabricationRecipe addProgress(ProgressStage p) {
			if (progress == null)
				progress = new HashSet();
			progress.add(p);
			return this;
		}

		@SideOnly(Side.CLIENT)
		public ItemStack getDisplay() {
			return displayItem.copy();
		}

		public ItemStack getItem() {
			return item.copy();
		}

		public ElementTagCompound getCost() {
			return energy.copy();
		}

		public Collection<ProgressStage> getProgress() {
			return progress != null ? Collections.unmodifiableCollection(progress) : new ArrayList();
		}

		public boolean hasProgress(EntityPlayer ep) {
			if (progress == null || progress.isEmpty())
				return true;
			if (ep == null || ReikaPlayerAPI.isFake(ep))
				return false;
			for (ProgressStage p : progress)
				if (!p.isPlayerAtStage(ep))
					return false;
			return true;
		}

	}

	public FabricationRecipe getOrCreateFluidRecipe(ItemStack is, Fluid f) {
		if (fluidRecipes.containsKey(f.getName()))
			return fluidRecipes.get(f.getName());
		ElementTagCompound ftag = ItemMagicRegistry.instance.getFluidValue(f);
		if (ftag == null)
			return null;
		ElementTagCompound tag = FabricationRecipes.recipes().processTag(ftag).scale(1/FabricationRecipes.SCALE);
		return this.addFluidRecipe(is, f, tag);
	}

}
