/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Interfaces.Registry.OreType;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaOreHelper;
import Reika.DragonAPI.ModRegistry.ModOreList;

/** Specifies the magic elements present in each "fundamental" item. */
public class ItemMagicRegistry {

	public static final ItemMagicRegistry instance = new ItemMagicRegistry();

	private final HashMap<KeyedItemStack, ElementTagCompound> data = new HashMap();
	private final HashMap<Fluid, ElementTagCompound> fluidData = new HashMap();
	private final HashMap<OreType, ElementTagCompound> cachedOreNames = new HashMap();

	private ItemMagicRegistry() {
		this.addElement("stone", CrystalElement.RED, 1);
		this.addElement("stone", CrystalElement.BROWN, 2);
		this.addElement("cobblestone", CrystalElement.BROWN, 1);

		this.addElement("logWood", CrystalElement.GREEN, 2);
		this.addElement("plankWood", CrystalElement.GREEN, 1);
		this.addElement("stickWood", CrystalElement.GREEN, 1);
		this.addElement("slabWood", CrystalElement.GREEN, 1);
		this.addElement("stairWood", CrystalElement.GREEN, 1);
		this.addElement("stairWood", CrystalElement.LIME, 1);
		this.addElement("treeSapling", CrystalElement.GREEN, 1);
		this.addElement("treeLeaves", CrystalElement.GREEN, 1);

		this.addElement("flower", CrystalElement.GREEN, 1);
		this.addElement("seed", CrystalElement.GREEN, 1);
		this.addElement("seed", CrystalElement.GRAY, 1);

		this.addElement(Blocks.dirt, CrystalElement.GREEN, 1);
		this.addElement(Blocks.dirt, CrystalElement.BROWN, 1);

		this.addElement(new ItemStack(Blocks.dirt, 1, 2), CrystalElement.BROWN, 1);
		this.addElement(new ItemStack(Blocks.dirt, 1, 2), CrystalElement.GREEN, 1);

		this.addOreElement(ReikaOreHelper.DIAMOND, CrystalElement.BROWN, 6);
		this.addOreElement(ReikaOreHelper.DIAMOND, CrystalElement.PURPLE, 4);
		this.addOreElement(ReikaOreHelper.DIAMOND, CrystalElement.WHITE, 1);
		this.addOreElement(ReikaOreHelper.EMERALD, CrystalElement.BROWN, 6);
		this.addOreElement(ReikaOreHelper.EMERALD, CrystalElement.PURPLE, 4);
		this.addOreElement(ReikaOreHelper.EMERALD, CrystalElement.WHITE, 1);
		this.addOreElement(ReikaOreHelper.QUARTZ, CrystalElement.BROWN, 4);
		this.addOreElement(ReikaOreHelper.QUARTZ, CrystalElement.YELLOW, 2);
		this.addOreElement(ReikaOreHelper.IRON, CrystalElement.BROWN, 3);
		this.addOreElement(ReikaOreHelper.GOLD, CrystalElement.BROWN, 4);
		this.addOreElement(ReikaOreHelper.GOLD, CrystalElement.PURPLE, 2);
		this.addOreElement(ReikaOreHelper.COAL, CrystalElement.YELLOW, 3);
		this.addOreElement(ReikaOreHelper.COAL, CrystalElement.ORANGE, 3);
		this.addOreElement(ReikaOreHelper.REDSTONE, CrystalElement.YELLOW, 2);
		this.addOreElement(ReikaOreHelper.REDSTONE, CrystalElement.LIME, 2);
		this.addOreElement(ModOreList.CERTUSQUARTZ, CrystalElement.BROWN, 3);
		this.addOreElement(ModOreList.CERTUSQUARTZ, CrystalElement.WHITE, 2);
		this.addOreElement(ModOreList.APATITE, CrystalElement.BROWN, 2);
		this.addOreElement(ModOreList.APATITE, CrystalElement.WHITE, 1);
		this.addOreElement(ModOreList.SULFUR, CrystalElement.BROWN, 2);
		this.addOreElement(ModOreList.SULFUR, CrystalElement.ORANGE, 2);
		this.addOreElement(ModOreList.NIKOLITE, CrystalElement.BROWN, 2);
		this.addOreElement(ModOreList.NIKOLITE, CrystalElement.YELLOW, 2);
		this.addOreElement(ModOreList.NIKOLITE, CrystalElement.LIME, 2);
		this.addOreElement(ModOreList.NIKOLITE, CrystalElement.GRAY, 1);
		this.addOreElement(ModOreList.CINNABAR, CrystalElement.BROWN, 3);
		this.addOreElement(ModOreList.CINNABAR, CrystalElement.CYAN, 1);

		this.addOreElement(ModOreList.COPPER, CrystalElement.BROWN, 2);
		this.addOreElement(ModOreList.TIN, CrystalElement.BROWN, 2);
		this.addOreElement(ModOreList.SILVER, CrystalElement.BROWN, 3);
		this.addOreElement(ModOreList.LEAD, CrystalElement.BROWN, 3);
		this.addOreElement(ModOreList.LEAD, CrystalElement.RED, 1);
		this.addOreElement(ModOreList.NICKEL, CrystalElement.BROWN, 2);
		this.addOreElement(ModOreList.TITANIUM, CrystalElement.BROWN, 3);
		this.addOreElement(ModOreList.TITANIUM, CrystalElement.RED, 1);
		this.addOreElement(ModOreList.ALUMINUM, CrystalElement.BROWN, 2);
		this.addOreElement(ModOreList.ZINC, CrystalElement.BROWN, 2);
		this.addOreElement(ModOreList.URANIUM, CrystalElement.BROWN, 4);
		this.addOreElement(ModOreList.URANIUM, CrystalElement.GRAY, 3);
		this.addOreElement(ModOreList.URANIUM, CrystalElement.YELLOW, 3);
		this.addOreElement(ModOreList.IRIDIUM, CrystalElement.BROWN, 8);
		this.addOreElement(ModOreList.IRIDIUM, CrystalElement.WHITE, 2);
		this.addOreElement(ModOreList.IRIDIUM, CrystalElement.PURPLE, 8);
		this.addOreElement(ModOreList.PLATINUM, CrystalElement.BROWN, 6);
		this.addOreElement(ModOreList.PLATINUM, CrystalElement.WHITE, 1);
		this.addOreElement(ModOreList.PLATINUM, CrystalElement.PURPLE, 6);
		this.addOreElement(ModOreList.RUBY, CrystalElement.BROWN, 6);
		this.addOreElement(ModOreList.RUBY, CrystalElement.PURPLE, 5);
		this.addOreElement(ModOreList.RUBY, CrystalElement.WHITE, 2);
		this.addOreElement(ModOreList.DRACONIUM, CrystalElement.BROWN, 6);
		this.addOreElement(ModOreList.DRACONIUM, CrystalElement.WHITE, 1);
		this.addOreElement(ModOreList.DRACONIUM, CrystalElement.PURPLE, 6);
		this.addOreElement(ModOreList.AMETHYST, CrystalElement.BROWN, 6);
		this.addOreElement(ModOreList.AMETHYST, CrystalElement.WHITE, 2);
		this.addOreElement(ModOreList.AMETHYST, CrystalElement.PURPLE, 6);
		this.addOreElement(ModOreList.AMETHYST, CrystalElement.MAGENTA, 3);

		this.addOreElement(ModOreList.INFUSEDAIR, CrystalElement.BLACK, 4);
		this.addOreElement(ModOreList.INFUSEDAIR, CrystalElement.WHITE, 2);
		this.addOreElement(ModOreList.INFUSEDEARTH, CrystalElement.BLACK, 2);
		this.addOreElement(ModOreList.INFUSEDEARTH, CrystalElement.GREEN, 4);
		this.addOreElement(ModOreList.INFUSEDWATER, CrystalElement.BLACK, 2);
		this.addOreElement(ModOreList.INFUSEDWATER, CrystalElement.CYAN, 4);
		this.addOreElement(ModOreList.INFUSEDFIRE, CrystalElement.BLACK, 2);
		this.addOreElement(ModOreList.INFUSEDFIRE, CrystalElement.ORANGE, 4);
		this.addOreElement(ModOreList.INFUSEDENTROPY, CrystalElement.BLACK, 2);
		this.addOreElement(ModOreList.INFUSEDENTROPY, CrystalElement.LIGHTGRAY, 4);
		this.addOreElement(ModOreList.INFUSEDORDER, CrystalElement.BLACK, 2);
		this.addOreElement(ModOreList.INFUSEDORDER, CrystalElement.WHITE, 4);

		this.addOreElement(ModOreList.FLUORITE, CrystalElement.WHITE, 2);
		this.addOreElement(ModOreList.CALCITE, CrystalElement.WHITE, 2);

		this.addElement(Blocks.sand, CrystalElement.BROWN, 1);
		this.addElement(Blocks.grass, CrystalElement.GREEN, 1);
		this.addElement(Blocks.grass, CrystalElement.BROWN, 1);
		this.addElement(Items.clay_ball, CrystalElement.BROWN, 1);
		this.addElement(Items.clay_ball, CrystalElement.CYAN, 1);
		this.addElement(Blocks.end_stone, CrystalElement.BLACK, 1);
		this.addElement(Blocks.end_stone, CrystalElement.BROWN, 1);
		this.addElement(Blocks.gravel, CrystalElement.BROWN, 1);
		this.addElement(Blocks.mycelium, CrystalElement.BROWN, 1);
		this.addElement(Blocks.mycelium, CrystalElement.GREEN, 1);
		this.addElement(Blocks.soul_sand, CrystalElement.BROWN, 2);
		this.addElement(Blocks.soul_sand, CrystalElement.ORANGE, 1);
		this.addElement(Blocks.netherrack, CrystalElement.BROWN, 2);
		this.addElement(Blocks.netherrack, CrystalElement.ORANGE, 2);
		this.addElement(Blocks.mossy_cobblestone, CrystalElement.BROWN, 2);
		this.addElement(Blocks.mossy_cobblestone, CrystalElement.GREEN, 2);
		this.addElement(Blocks.obsidian, CrystalElement.ORANGE, 2);
		this.addElement(Blocks.obsidian, CrystalElement.BROWN, 5);
		this.addElement(Blocks.obsidian, CrystalElement.RED, 5);
		this.addElement(Blocks.sandstone, CrystalElement.BROWN, 2);
		this.addElement("glowstone", CrystalElement.BLUE, 4);

		this.addElement(Blocks.tallgrass, CrystalElement.GREEN, 2);
		this.addElement(Blocks.waterlily, CrystalElement.GREEN, 3);
		this.addElement(Blocks.waterlily, CrystalElement.CYAN, 3);
		this.addElement(Blocks.vine, CrystalElement.GREEN, 3);
		this.addElement(Items.wheat_seeds, CrystalElement.GREEN, 1);
		this.addElement(Items.melon_seeds, CrystalElement.GREEN, 1);
		this.addElement(Items.pumpkin_seeds, CrystalElement.GREEN, 1);
		this.addElement(Items.nether_wart, CrystalElement.GREEN, 1);
		this.addElement(Items.nether_wart, CrystalElement.ORANGE, 1);
		this.addElement(Items.nether_wart, CrystalElement.BLACK, 1);
		this.addElement(Items.reeds, CrystalElement.GREEN, 2);
		this.addElement(Items.reeds, CrystalElement.CYAN, 2);

		this.addElement(Blocks.red_flower, CrystalElement.GREEN, 1);
		this.addElement(Blocks.yellow_flower, CrystalElement.GREEN, 1);
		this.addElement(Blocks.cactus, CrystalElement.GREEN, 2);
		this.addElement(Blocks.cactus, CrystalElement.CYAN, 1);
		this.addElement(Blocks.brown_mushroom, CrystalElement.GREEN, 1);
		this.addElement(Blocks.red_mushroom, CrystalElement.GREEN, 1);

		this.addElement(Items.apple, CrystalElement.GREEN, 1);
		this.addElement(Items.carrot, CrystalElement.GREEN, 1);
		this.addElement(Items.carrot, CrystalElement.BROWN, 1);
		this.addElement(Items.potato, CrystalElement.GREEN, 1);
		this.addElement(Items.potato, CrystalElement.BROWN, 1);

		this.addElement(Blocks.ice, CrystalElement.CYAN, 2);
		this.addElement(Blocks.ice, CrystalElement.GRAY, 1);
		this.addElement(Blocks.packed_ice, CrystalElement.CYAN, 2);
		this.addElement(Blocks.packed_ice, CrystalElement.GRAY, 1);
		this.addElement(Items.snowball, CrystalElement.CYAN, 1);
		this.addElement(Blocks.web, CrystalElement.LIGHTGRAY, 1);
		this.addElement(Blocks.web, CrystalElement.LIME, 1);
		this.addElement(Items.flint, CrystalElement.BROWN, 1);

		this.addElement(Items.string, CrystalElement.GREEN, 1);
		this.addElement(Items.magma_cream, CrystalElement.ORANGE, 3);
		this.addElement(Items.magma_cream, CrystalElement.RED, 4);
		this.addElement(Items.magma_cream, CrystalElement.GREEN, 1);
		this.addElement(Items.slime_ball, CrystalElement.CYAN, 2);
		this.addElement(Items.slime_ball, CrystalElement.PINK, 2);
		this.addElement(Items.slime_ball, CrystalElement.GREEN, 1);
		this.addElement(Items.leather, CrystalElement.GREEN, 1);
		this.addElement(Items.rotten_flesh, CrystalElement.GREEN, 1);
		this.addElement(Items.feather, CrystalElement.GREEN, 1);
		this.addElement(Items.bone, CrystalElement.GREEN, 1);
		this.addElement(Items.egg, CrystalElement.GREEN, 1);
		this.addElement(Items.spider_eye, CrystalElement.GREEN, 1);
		this.addElement(Items.gunpowder, CrystalElement.GREEN, 1);
		this.addElement(Items.gunpowder, CrystalElement.ORANGE, 3);
		this.addElement(Blocks.wool, CrystalElement.GREEN, 1);
		this.addElement(Items.fish, CrystalElement.GREEN, 1);
		this.addElement(Items.fish, CrystalElement.CYAN, 1);
		this.addElement(Items.chicken, CrystalElement.GREEN, 1);
		this.addElement(Items.porkchop, CrystalElement.GREEN, 1);
		this.addElement(Items.beef, CrystalElement.GREEN, 1);
		this.addElement(Items.blaze_rod, CrystalElement.ORANGE, 4);
		this.addElement(Items.blaze_rod, CrystalElement.GREEN, 1);
		this.addElement(Items.blaze_rod, CrystalElement.PINK, 2);
		this.addElement(Items.saddle, CrystalElement.PURPLE, 1);
		this.addElement(Items.ender_pearl, CrystalElement.LIME, 4);
		this.addElement(Items.ender_pearl, CrystalElement.GREEN, 1);
		this.addElement(Items.ender_pearl, CrystalElement.BLACK, 4);
		this.addElement(Items.ghast_tear, CrystalElement.BLACK, 1);
		this.addElement(Items.ghast_tear, CrystalElement.GREEN, 1);

		this.addElement(new ItemStack(Items.skull, 1, 0), CrystalElement.PINK, 4);
		this.addElement(new ItemStack(Items.skull, 1, 1), CrystalElement.PINK, 4);
		this.addElement(new ItemStack(Items.skull, 1, 1), CrystalElement.ORANGE, 2);
		this.addElement(new ItemStack(Items.skull, 1, 2), CrystalElement.PINK, 4);
		this.addElement(new ItemStack(Items.skull, 1, 3), CrystalElement.GREEN, 3);
		this.addElement(new ItemStack(Items.skull, 1, 4), CrystalElement.PINK, 4);
		this.addElement(new ItemStack(Items.skull, 1, 4), CrystalElement.YELLOW, 2);

		this.addElement("rodBlizz", CrystalElement.WHITE, 4);
		this.addElement("rodBlizz", CrystalElement.GREEN, 1);
		this.addElement("rodBlizz", CrystalElement.PINK, 2);

		this.addElement("rodBlitz", CrystalElement.GRAY, 4);
		this.addElement("rodBlitz", CrystalElement.GREEN, 1);
		this.addElement("rodBlitz", CrystalElement.PINK, 2);

		this.addElement("rodBasalz", CrystalElement.BROWN, 4);
		this.addElement("rodBasalz", CrystalElement.GREEN, 1);
		this.addElement("rodBasalz", CrystalElement.PINK, 2);

		this.addElement(Items.nether_star, CrystalElement.PINK, 8);
		this.addElement(Items.nether_star, CrystalElement.BLACK, 4);

		this.addElement(Items.golden_apple, CrystalElement.MAGENTA, 8);
		this.addElement(Items.golden_apple, CrystalElement.BROWN, 2);

		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement e = CrystalElement.elements[i];
			ItemStack shard = ChromaItems.SHARD.getStackOfMetadata(i);
			ItemStack shard2 = ChromaItems.SHARD.getStackOfMetadata(i+16);
			ItemStack berry = ChromaItems.BERRY.getStackOfMetadata(i);
			ItemStack crystal = new ItemStack(ChromaBlocks.CRYSTAL.getBlockInstance(), 1, i);
			ItemStack seed = ChromaItems.SEED.getStackOfMetadata(i);
			ItemStack dye = ChromaItems.DYE.getStackOfMetadata(i);
			ItemStack vdye = new ItemStack(Items.dye, 1, i);
			ItemStack leaf = ChromaBlocks.DYELEAF.getStackOfMetadata(i);
			this.addElement(seed, e, 1);
			this.addElement(crystal, e, 10);
			this.addElement(berry, e, 1);
			this.addElement(shard, e, 4);
			this.addElement(shard2, e, 6);
			this.addElement(vdye, e, 1);
			this.addElement(dye, e, 1);
			this.addElement(leaf, e, 1);
		}

		this.addElement(ChromaStacks.crystalPowder, CrystalElement.BLACK, 1);
		this.addElement(ChromaStacks.crystalPowder, CrystalElement.WHITE, 1);

		this.addFluids();
	}

	private void addFluids() {


		this.addElement(FluidRegistry.WATER, CrystalElement.CYAN, 3);

		this.addElement(FluidRegistry.LAVA, CrystalElement.CYAN, 2);
		this.addElement(FluidRegistry.LAVA, CrystalElement.ORANGE, 3);

		this.addElement(ChromatiCraft.luma, CrystalElement.CYAN, 1);
		this.addElement(ChromatiCraft.luma, CrystalElement.WHITE, 4);
		this.addElement(ChromatiCraft.luma, CrystalElement.BLACK, 4);

		this.addElement(ChromatiCraft.lumen, CrystalElement.CYAN, 2);
		this.addElement(ChromatiCraft.lumen, CrystalElement.WHITE, 2);
		this.addElement(ChromatiCraft.lumen, CrystalElement.BLACK, 4);
		this.addElement(ChromatiCraft.lumen, CrystalElement.MAGENTA, 3);

		this.addElement(FluidRegistry.getFluid("ender"), CrystalElement.CYAN, 1);
		this.addElement(FluidRegistry.getFluid("ender"), CrystalElement.PINK, 1);
		this.addElement(FluidRegistry.getFluid("ender"), CrystalElement.LIME, 5);
		this.addElement(FluidRegistry.getFluid("ender"), CrystalElement.BLACK, 5);

		this.addElement(FluidRegistry.getFluid("oil"), CrystalElement.CYAN, 2);
		this.addElement(FluidRegistry.getFluid("oil"), CrystalElement.YELLOW, 4);
		this.addElement(FluidRegistry.getFluid("oil"), CrystalElement.BROWN, 4);

		this.addElement(FluidRegistry.getFluid("essence"), CrystalElement.CYAN, 3);
		this.addElement(FluidRegistry.getFluid("essence"), CrystalElement.BLACK, 4);

		this.addElement(FluidRegistry.getFluid("poison"), CrystalElement.CYAN, 2);
		this.addElement(FluidRegistry.getFluid("poison"), CrystalElement.PINK, 3);

		this.addElement(FluidRegistry.getFluid("milk"), CrystalElement.CYAN, 2);
		this.addElement(FluidRegistry.getFluid("milk"), CrystalElement.GREEN, 3);

		this.addElement(FluidRegistry.getFluid("liquidessence"), CrystalElement.CYAN, 2);
		this.addElement(FluidRegistry.getFluid("liquidessence"), CrystalElement.BLACK, 3);
		this.addElement(FluidRegistry.getFluid("liquidessence"), CrystalElement.PURPLE, 2);
	}

	public void addPostload() {
		if (ModList.BOP.isLoaded()) {
			String[] arr = {
					"plants/0-8,12-15",
					"mushrooms/0-5",
					"willow",
					"ivy",
					"treeMoss",
					"flowerVine",
					"wisteria",
					"lilyBop/0-2",
					"foliage/0-5,7-15",
					"coral1/11-15",
					"coral2/8",
					"moss",
			};
			for (int i = 0; i < arr.length; i++) {
				for (ItemStack is : ReikaItemHelper.parseMultiRangedMeta(ModList.BOP, arr[i])) {
					this.addElement(is, CrystalElement.GREEN, 1);
				}
			}
		}

		if (ModList.TWILIGHT.isLoaded()) {
			String[] arr = {
					"tile.TFPlant/3,4,8-11,13,14",
					"tile.HugeLilyPad",
					"tile.HugeWaterLily",
					"tile.TrollVidr",
					"tile.UnripeTrollBer",
					"tile.TrollBer",
			};
			for (int i = 0; i < arr.length; i++) {
				for (ItemStack is : ReikaItemHelper.parseMultiRangedMeta(ModList.TWILIGHT, arr[i])) {
					this.addElement(is, CrystalElement.GREEN, 1);
				}
			}
		}
	}

	private void addOreElement(OreType ore, CrystalElement e, int amt) {
		for (String s : ore.getOreDictNames()) {
			this.addElement(s, e, amt*2);
		}
		this.addElement(ore.getProductOreDictName(), e, amt);
		ElementTagCompound tag = cachedOreNames.get(ore);
		if (tag == null) {
			tag = new ElementTagCompound();
			cachedOreNames.put(ore, tag);
		}
		tag.setTag(e, amt);
	}

	public ElementTagCompound getValueForOreType(OreType ore) {
		ElementTagCompound tag = cachedOreNames.get(ore);
		return tag != null ? tag.copy() : null;
	}

	private void addElement(String s, CrystalElement e, int amt) {
		Collection<ItemStack> li = this.getOres(s);
		for (ItemStack is : li) {
			this.addElement(is, e, amt);
		}
	}

	private Collection<ItemStack> getOres(String s) {
		ArrayList<ItemStack> li = new ArrayList(OreDictionary.getOres(s));
		/*
		ArrayList<ItemStack> li2 = new ArrayList();
		for (ItemStack is : li) {
			if (is.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
				int n = ?;
				for (int i = 0; i < n; i++) {
					ItemStack is2 = is.copy();
					is2.setItemDamage(i);
					li2.add(is2);
				}
			}
			else {
				li2.add(is);
			}
		}
		return li2;
		 */
		return li;
	}

	private void addElement(Fluid f, CrystalElement e, int amt) {
		if (f == null)
			return;
		ElementTagCompound tag = fluidData.get(f);
		if (tag == null) {
			tag = new ElementTagCompound();
			fluidData.put(f, tag);
		}
		tag.addTag(e, amt);
	}

	private void addElement(Block b, CrystalElement e, int amt) {
		this.addElement(new ItemStack(b), e, amt);
	}

	private void addElement(Item b, CrystalElement e, int amt) {
		this.addElement(new ItemStack(b), e, amt);
	}

	private void addElement(ItemStack is, CrystalElement e, int amt) {
		KeyedItemStack ks = new KeyedItemStack(is).setSimpleHash(true).lock();//.setSized(true);
		ElementTagCompound tag = data.get(ks);
		if (tag == null) {
			tag = new ElementTagCompound();
			data.put(ks, tag);
		}
		tag.addTag(e, amt);
	}

	public ElementTagCompound getItemValue(ItemStack is) {
		return this.getItemValue(new KeyedItemStack(is).setSimpleHash(true));
	}

	public ElementTagCompound getItemValue(KeyedItemStack is) {
		ElementTagCompound tag = data.get(is);
		return tag != null ? tag.copy() : null;
	}

	public ArrayList<ItemStack> getAllRegisteredItems() {
		ArrayList<ItemStack> li = new ArrayList();
		for (KeyedItemStack item : data.keySet()) {
			li.add(item.getItemStack());
		}
		return li;
	}

	public Collection<KeyedItemStack> keySet() {
		return Collections.unmodifiableCollection(data.keySet());
	}

	public ElementTagCompound getFluidValue(Fluid f) {
		ElementTagCompound tag = fluidData.get(f);
		return tag != null ? tag.copy() : null;
	}

}
