/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Magic.ElementTag;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.RuneShape;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.TileEntityCastingTable;
import Reika.ChromatiCraft.TileEntity.TileEntityItemStand;
import Reika.DragonAPI.Instantiable.RecipePattern;
import Reika.DragonAPI.Instantiable.Data.WorldLocation;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;


public class CastingRecipe {

	private final ItemStack out;
	public final RecipeType type;
	private IRecipe recipe;

	public CastingRecipe(ItemStack out, IRecipe recipe) {
		this(out, RecipeType.CRAFTING, recipe);
	}

	private CastingRecipe(ItemStack out, RecipeType type, IRecipe recipe) {
		this.out = out;
		this.type = type;
		this.recipe = recipe;
	}

	public final ItemStack getOutput() {
		return out.copy();
	}

	public void drawInParticlesTo(World world, int x, int y, int z) {

	}

	public final int getExperience() {
		return type.experience;
	}

	public int getDuration() {
		return 5;
	}

	public ItemStack[] getArrayForDisplay() {
		ItemStack[] iss = new ItemStack[9];
		ReikaRecipeHelper.copyRecipeToItemStackArray(iss, recipe);
		return iss;
	}

	public boolean usesItem(ItemStack is) {
		return ReikaItemHelper.listContainsItemStack(ReikaRecipeHelper.getAllItemsInRecipe(recipe), is);
	}

	protected static final ItemStack getShard(CrystalElement e) {
		return ChromaItems.SHARD.getStackOfMetadata(e.ordinal());
	}

	public boolean match(TileEntityCastingTable table) {
		if (recipe == null)
			return true;
		ItemStack[] items = new ItemStack[9];
		for (int i = 0; i < 9; i++)
			items[i] = table.getStackInSlot(i);
		RecipePattern ic = new RecipePattern(items);
		return recipe.matches(ic, null);
	}

	public static class TempleCastingRecipe extends CastingRecipe {

		private final RuneShape runes = new RuneShape();

		public TempleCastingRecipe(ItemStack out, IRecipe recipe) {
			this(out, RecipeType.TEMPLE, recipe);
		}

		private TempleCastingRecipe(ItemStack out, RecipeType type, IRecipe recipe) {
			super(out, type, recipe);
		}

		protected boolean matchRunes(World world, int x, int y, int z) {
			return runes.matchAt(world, x, y, z, 0, 0, 0);
		}

		protected CastingRecipe addRune(CrystalElement color, int rx, int ry, int rz) {
			runes.addRune(color, rx, ry, rz);
			return this;
		}

		@Override
		public boolean match(TileEntityCastingTable table) {
			return super.match(table) && this.matchRunes(table.worldObj, table.xCoord, table.yCoord, table.zCoord);
		}

		@Override
		public int getDuration() {
			return 20;
		}

	}

	public static class MultiBlockCastingRecipe extends TempleCastingRecipe {

		private final HashMap<List<Integer>, ItemStack> inputs = new HashMap();
		private final ItemStack main;

		protected MultiBlockCastingRecipe(ItemStack out, ItemStack main) {
			this(out, main, RecipeType.MULTIBLOCK);
		}

		private MultiBlockCastingRecipe(ItemStack out, ItemStack main, RecipeType type) {
			super(out, type, null);
			this.main = main;
		}

		public ItemStack getMainInput() {
			return main.copy();
		}

		protected void addAuxItem(ItemStack is, int dx, int dz) {
			inputs.put(Arrays.asList(dx, dz), is);
		}

		public HashMap<List<Integer>, ItemStack> getAuxItems() {
			HashMap map = new HashMap();
			map.putAll(inputs);
			return map;
		}

		public HashMap<WorldLocation, ItemStack> getOtherInputs(World world, int x, int y, int z) {
			HashMap<WorldLocation, ItemStack> map = new HashMap();
			for (List<Integer> li : inputs.keySet()) {
				ItemStack is = inputs.get(li);
				int dx = li.get(0);
				int dz = li.get(1);
				int dy = y+(Math.abs(dx) != 4 && Math.abs(dz) != 4 ? 0 : 1);
				WorldLocation loc = new WorldLocation(world, x+dx, dy, z+dz);
				map.put(loc, is);
			}
			return map;
		}

		@Override
		public boolean match(TileEntityCastingTable table) {
			ItemStack main = table.getStackInSlot(4);
			for (int i = 0; i < 9; i++) {
				if (i != 4) {
					if (table.getStackInSlot(i) != null) //maybe make use IRecipe?
						return false;
				}
			}
			ItemStack ctr = this.getMainInput();
			if (ReikaItemHelper.matchStacks(main, ctr) && ItemStack.areItemStackTagsEqual(main, ctr)) {
				HashMap<List<Integer>, TileEntityItemStand> stands = table.getOtherStands();
				for (List key : stands.keySet()) {
					ItemStack at = (stands.get(key).getStackInSlot(0));
					ItemStack is = inputs.get(key);
					if (!ReikaItemHelper.matchStacks(at, is) || !ItemStack.areItemStackTagsEqual(at, is)) {
						//ReikaJavaLibrary.pConsole(key+": "+is+" & "+at+" * "+this.getOutput());
						return false;
					}
				}
				if (this.matchRunes(table.worldObj, table.xCoord, table.yCoord, table.zCoord)) {
					return true;
				}
			}
			return false;
		}

		@Override
		public int getDuration() {
			return 100;
		}

		@Override
		public boolean usesItem(ItemStack is) {
			if (ReikaItemHelper.matchStacks(is, main) && ItemStack.areItemStackTagsEqual(is, main))
				return true;
			for (List<Integer> key : inputs.keySet()) {
				ItemStack item = inputs.get(key);
				if (ReikaItemHelper.matchStacks(is, item) && ItemStack.areItemStackTagsEqual(is, item))
					return true;
			}
			return false;
		}

		@Override
		public ItemStack[] getArrayForDisplay() {
			ItemStack[] iss = new ItemStack[9];
			iss[4] = main;
			return iss;
		}
	}

	public static class PylonRecipe extends MultiBlockCastingRecipe {

		private final ElementTagCompound elements = new ElementTagCompound();

		public PylonRecipe(ItemStack out, ItemStack main) {
			super(out, main, RecipeType.PYLON);
		}

		public ElementTagCompound getRequiredAura() {
			return elements.copy();
		}

		protected CastingRecipe addAuraRequirement(CrystalElement e, int amt) {
			return this.addAuraRequirement(new ElementTag(e, amt));
		}

		protected CastingRecipe addAuraRequirement(ElementTag e) {
			elements.maximizeWith(e);
			return this;
		}

		protected CastingRecipe addAuraRequirement(ElementTagCompound e) {
			elements.maximizeWith(e);
			return this;
		}

		@Override
		public boolean match(TileEntityCastingTable table) {
			return super.match(table);
		}

		@Override
		public int getDuration() {
			return 400;
		}
	}

	public static enum RecipeType {
		CRAFTING(5, 250),
		TEMPLE(40, 2000),
		MULTIBLOCK(200, 15000),
		PYLON(500, Integer.MAX_VALUE);

		public final int experience;
		public final int levelUp;

		public static final RecipeType[] typeList = values();

		private RecipeType(int xp, int lvl) {
			experience = xp;
			levelUp = lvl;
		}

		public int getRequiredXP() {
			return this == CRAFTING ? 0 : typeList[this.ordinal()-1].levelUp;
		}

		public RecipeType next() {
			return this == PYLON ? this : typeList[this.ordinal()+1];
		}

		public boolean isAtLeast(RecipeType r) {
			return this.ordinal() >= r.ordinal();
		}

		public boolean isMoreThan(RecipeType r) {
			return this.ordinal() > r.ordinal();
		}
	}

}
