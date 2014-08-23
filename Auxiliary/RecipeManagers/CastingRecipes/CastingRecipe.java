package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes;

import Reika.ChromatiCraft.Auxiliary.RecipeManagers.RecipesCastingTable.RecipeType;
import Reika.ChromatiCraft.Magic.ElementTag;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.RuneShape;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.TileEntityCastingTable;
import Reika.ChromatiCraft.TileEntity.TileEntityItemStand;
import Reika.DragonAPI.Instantiable.RecipePattern;
import Reika.DragonAPI.Instantiable.WorldLocation;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;


public class CastingRecipe {

	private final ItemStack out;
	public final RecipeType type;
	private IRecipe recipe;

	protected CastingRecipe(ItemStack main, ItemStack out, IRecipe recipe) {
		this(main, out, RecipeType.CRAFTING);
		this.recipe = recipe;
	}

	private CastingRecipe(ItemStack main, ItemStack out, RecipeType type) {
		this.out = out;
		this.type = type;
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

	public boolean match(TileEntityCastingTable table) {
		ItemStack[] items = new ItemStack[9];
		for (int i = 0; i < 9; i++)
			items[i] = table.getStackInSlot(i);
		RecipePattern ic = new RecipePattern(items);
		return recipe.matches(ic, null);
	}

	public static class TempleCastingRecipe extends CastingRecipe {

		private final RuneShape runes = new RuneShape();

		protected TempleCastingRecipe(ItemStack main, ItemStack out) {
			this(main, out, RecipeType.TEMPLE);
		}

		private TempleCastingRecipe(ItemStack main, ItemStack out, RecipeType type) {
			super(main, out, type);
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

		protected MultiBlockCastingRecipe(ItemStack main, ItemStack out) {
			this(main, out, RecipeType.MULTIBLOCK);
		}

		private MultiBlockCastingRecipe(ItemStack main, ItemStack out, RecipeType type) {
			super(main, out, type);
			this.main = main;
		}

		public ItemStack getMainInput() {
			return main.copy();
		}

		protected void addAuxItem(ItemStack is, int dx, int dz) {
			inputs.put(Arrays.asList(dx, dz), is);
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
						//ReikaJavaLibrary.pConsole(key+": "+is+" & "+at);
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
	}

	public static class PylonRecipe extends MultiBlockCastingRecipe {

		private final ElementTagCompound elements = new ElementTagCompound();

		protected PylonRecipe(ItemStack main, ItemStack out) {
			super(main, out, RecipeType.PYLON);
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

}
