/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.CrystalElementProxy;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.Interfaces.CoreRecipe;
import Reika.ChromatiCraft.Magic.ElementTag;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.RuneShape;
import Reika.ChromatiCraft.Magic.RuneShape.RuneViewer;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaResearchManager;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Registry.ItemElementCalculator;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityItemStand;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.BlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Instantiable.Recipe.RecipePattern;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class CastingRecipe {

	private final ItemStack out;
	public final RecipeType type;
	private IRecipe recipe;
	private ChromaResearch fragment;

	protected CastingRecipe(ItemStack out, IRecipe recipe) {
		this(out, RecipeType.CRAFTING, recipe);
	}

	private CastingRecipe(ItemStack out, RecipeType type, IRecipe recipe) {
		this.out = out;
		this.type = type;
		this.recipe = recipe;
	}

	public final void setFragment(ChromaResearch r) {
		if (fragment == null)
			fragment = r;
		else
			throw new IllegalStateException("Cannot change the research type of a recipe once initialized!");
	}

	public final ChromaResearch getFragment() {
		return fragment;
	}

	public final ItemStack getOutput() {
		return ReikaItemHelper.getSizedItemStack(out, Math.max(out.stackSize, this.getNumberProduced()));
	}

	public int getNumberProduced() {
		return 1;
	}

	public void onRecipeTick(TileEntityCastingTable te) {

	}

	public ChromaSounds getSoundOverride(int craftSoundTimer) {
		return null;
	}

	public int getExperience() {
		return type.experience;
	}

	public int getDuration() {
		return 5;
	}

	@SideOnly(Side.CLIENT)
	public ItemStack[] getArrayForDisplay() {
		return ReikaRecipeHelper.getPermutedRecipeArray(recipe);
	}

	protected final List<ItemStack>[] getRecipeArray() {
		return ReikaRecipeHelper.getRecipeArray(recipe);
	}

	public ItemStack[] getBasicRecipeArray() {
		List<ItemStack>[] lia = this.getRecipeArray();
		ItemStack[] out = new ItemStack[9];
		for (int i = 0; i < lia.length; i++) {
			List<ItemStack> li = lia[i];
			out[i] = li.get(0).copy();
			if (out[i].getItemDamage() == OreDictionary.WILDCARD_VALUE)
				out[i].setItemDamage(0);
		}
		return out;
	}

	public Object[] getInputArray() {
		return ReikaRecipeHelper.getInputArrayCopy(recipe);
	}

	public boolean usesItem(ItemStack is) {
		return ReikaItemHelper.listContainsItemStack(ReikaRecipeHelper.getAllItemsInRecipe(recipe), is, true);
	}
	/*
	@Override
	@SideOnly(Side.CLIENT)
	public String getTitle() {
		return this.getOutput().getDisplayName();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getShortDesc() {
		return "A new item to craft";
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack getIcon() {
		return this.getOutput();
	}
	 */
	protected static final ItemStack getShard(CrystalElement e) {
		return ChromaItems.SHARD.getStackOfMetadata(e.ordinal());
	}

	protected static final ItemStack getChargedShard(CrystalElement e) {
		return ChromaItems.SHARD.getStackOfMetadata(e.ordinal()+16);
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

	protected void getRequiredProgress(Collection<ProgressStage> c) {
		c.add(ProgressStage.CRYSTALS);
	}

	public boolean canRunRecipe(EntityPlayer ep) {
		if (fragment != null && !ChromaResearchManager.instance.playerHasFragment(ep, fragment))
			return false;
		Collection<ProgressStage> c = new ArrayList();
		this.getRequiredProgress(c);
		for (ProgressStage p : c) {
			if (!p.isPlayerAtStage(ep))
				return false;
		}
		return true;
	}

	public void onCrafted(TileEntityCastingTable te, EntityPlayer ep) {
		ChromaResearchManager.instance.givePlayerRecipe(ep, this);
		te.giveRecipe(ep, this);
	}

	@Override
	public String toString() {
		return super.toString()+" _ "+type+" > "+out.getDisplayName();
	}

	@SideOnly(Side.CLIENT)
	public ItemHashMap<Integer> getItemCounts() {
		ItemHashMap<Integer> map = new ItemHashMap();
		ItemStack[] items = this.getArrayForDisplay();
		for (int i = 0; i < 9; i++) {
			ItemStack is = items[i];
			if (is != null) {
				Integer num = map.get(is);
				int n = num != null ? num.intValue() : 0;
				map.put(is, n+1);
			}
		}
		return map;
	}

	public ElementTagCompound getInputElements() {
		ElementTagCompound tag = new ElementTagCompound();
		tag.addButMinimizeWith(ItemElementCalculator.instance.getIRecipeTotal(recipe));
		return tag;
	}

	public boolean isIndexed() {
		return true;
	}

	public Collection<ItemStack> getAllInputs() {
		Collection<ItemStack> c = new ArrayList();
		List<ItemStack>[] o = this.getRecipeArray();
		for (int i = 0; i < 9; i++) {
			if (o[i] != null) {
				c.addAll(o[i]);
			}
		}
		return c;
	}

	/** This is "per ItemStack", and is number of cycles (so total crafted number = amt crafted * this) */
	public int getTypicalCraftedAmount() {
		return this instanceof CoreRecipe ? Integer.MAX_VALUE : 1;
	}

	/** This is "per ItemStack", and is number of cycles (so total crafted number = amt crafted * this) */
	public int getPenaltyThreshold() {
		return this instanceof CoreRecipe ? Integer.MAX_VALUE : this.getTypicalCraftedAmount()*3/4;
	}

	/** Return zero to make all over-threshold yield zero XP */
	public float getPenaltyMultiplier() {
		return 1;
	}

	public final int getIDCode() {
		int flag = this.getClass().getName().hashCode();
		flag = flag ^ out.getItem().getClass().getName().hashCode();
		flag = flag ^ Math.max(out.stackSize, this.getNumberProduced());
		flag = flag ^ out.getItemDamage();
		flag = flag ^ (out.stackTagCompound != null ? out.stackTagCompound.hashCode() : 0);
		return flag;
	}

	public NBTTagCompound getOutputTag(NBTTagCompound input) {
		return null;
	}

	public static class TempleCastingRecipe extends CastingRecipe {

		private static final BlockArray runeRing = new BlockArray();
		private static final HashMap<Coordinate, CrystalElement> allRunes = new HashMap();

		static {
			runeRing.addBlockCoordinate(-2, -1, -2);
			runeRing.addBlockCoordinate(-1, -1, -2);
			runeRing.addBlockCoordinate(0, -1, -2);
			runeRing.addBlockCoordinate(1, -1, -2);
			runeRing.addBlockCoordinate(2, -1, -2);
			runeRing.addBlockCoordinate(2, -1, -1);
			runeRing.addBlockCoordinate(2, -1, 0);
			runeRing.addBlockCoordinate(2, -1, 1);
			runeRing.addBlockCoordinate(2, -1, 2);
			runeRing.addBlockCoordinate(1, -1, 2);
			runeRing.addBlockCoordinate(0, -1, 2);
			runeRing.addBlockCoordinate(-1, -1, 2);
			runeRing.addBlockCoordinate(-2, -1, 2);
			runeRing.addBlockCoordinate(-2, -1, 1);
			runeRing.addBlockCoordinate(-2, -1, 0);
			runeRing.addBlockCoordinate(-2, -1, -1);
		}

		private final RuneShape runes = new RuneShape();

		public TempleCastingRecipe(ItemStack out, IRecipe recipe) {
			this(out, RecipeType.TEMPLE, recipe);
		}

		private TempleCastingRecipe(ItemStack out, RecipeType type, IRecipe recipe) {
			super(out, type, recipe);
		}

		protected boolean matchRunes(World world, int x, int y, int z) {
			//runes.place(world, x, y, z);
			//ReikaJavaLibrary.pConsole(this.getOutput().getDisplayName());
			return runes.matchAt(world, x, y, z, 0, 0, 0);
		}

		protected TempleCastingRecipe addRuneRingRune(CrystalElement e) {
			Coordinate c = runeRing.getNthBlock(e.ordinal());
			return this.addRune(e, c.xCoord, c.yCoord, c.zCoord);
		}

		protected TempleCastingRecipe addRune(int color, int rx, int ry, int rz) {
			return this.addRune(CrystalElement.elements[color], rx, ry, rz);
		}

		protected TempleCastingRecipe addRune(CrystalElementProxy color, int rx, int ry, int rz) {
			return this.addRune(CrystalElement.getFromAPI(color), rx, ry, rz);
		}

		protected TempleCastingRecipe addRune(CrystalElement color, int rx, int ry, int rz) {
			this.verifyRune(color, rx, ry, rz);
			runes.addRune(color, rx, ry, rz);
			return this;
		}

		private void verifyRune(CrystalElement color, int x, int y, int z) {
			Coordinate c = new Coordinate(x, y, z);
			CrystalElement e = allRunes.get(c);
			if (e != null) {
				if (e != color)
					throw new RegistrationException(ChromatiCraft.instance, "Rune conflict @ "+x+", "+y+", "+z+": "+e+" & "+color);
			}
			allRunes.put(c, color);
		}

		protected CastingRecipe addRunes(RuneViewer view) {
			Map<Coordinate, CrystalElement> map = view.getRunes();
			for (Coordinate c : map.keySet())
				runes.addRune(map.get(c), c.xCoord, c.yCoord, c.zCoord);
			return this;
		}

		public RuneViewer getRunes() {
			return runes.getView();
		}

		@Override
		public boolean match(TileEntityCastingTable table) {
			return super.match(table) && this.matchRunes(table.worldObj, table.xCoord, table.yCoord, table.zCoord);
		}

		@Override
		public int getDuration() {
			return 20;
		}

		@Override
		protected void getRequiredProgress(Collection<ProgressStage> c) {
			super.getRequiredProgress(c);
			c.add(ProgressStage.RUNEUSE);
		}

		@Override
		public ElementTagCompound getInputElements() {
			ElementTagCompound tag = super.getInputElements();
			for (CrystalElement e : runes.getView().getRunes().values()) {
				tag.addValueToColor(e, 1);
			}
			return tag;
		}

		public static RuneViewer getAllRegisteredRunes() {
			return new RuneShape(allRunes).getView();
		}

	}

	public static class MultiBlockCastingRecipe extends TempleCastingRecipe {

		private final HashMap<List<Integer>, ItemStack> inputs = new HashMap();
		private final ItemStack main;

		public MultiBlockCastingRecipe(ItemStack out, ItemStack main) {
			this(out, main, RecipeType.MULTIBLOCK);
		}

		private MultiBlockCastingRecipe(ItemStack out, ItemStack main, RecipeType type) {
			super(out, type, null);
			this.main = main;
		}

		public ItemStack getMainInput() {
			return main.copy();
		}

		protected MultiBlockCastingRecipe addAuxItem(Block b, int dx, int dz) {
			return this.addAuxItem(new ItemStack(b), dx, dz);
		}

		protected MultiBlockCastingRecipe addAuxItem(Item i, int dx, int dz) {
			return this.addAuxItem(new ItemStack(i), dx, dz);
		}

		protected MultiBlockCastingRecipe addAuxItem(ItemStack is, int dx, int dz) {
			inputs.put(Arrays.asList(dx, dz), is);
			return this;
		}

		public Map<List<Integer>, ItemStack> getAuxItems() {
			return Collections.unmodifiableMap(inputs);
		}

		public HashMap<WorldLocation, ItemStack> getOtherInputs(World world, int x, int y, int z) {
			HashMap<WorldLocation, ItemStack> map = new HashMap();
			for (List<Integer> li : inputs.keySet()) {
				ItemStack is = inputs.get(li).copy();
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
			//ReikaJavaLibrary.pConsole(ctr.stackTagCompound+":"+main.stackTagCompound, this instanceof RepeaterTurboRecipe);
			if (ReikaItemHelper.matchStacks(main, ctr) && (ctr.stackTagCompound == null || ItemStack.areItemStackTagsEqual(main, ctr))) {
				HashMap<List<Integer>, TileEntityItemStand> stands = table.getOtherStands();
				//ReikaJavaLibrary.pConsole(stands.size(), this instanceof RepeaterTurboRecipe);
				if (stands.size() != 24)
					return false;
				//ReikaJavaLibrary.pConsole(stands.keySet());
				for (List key : stands.keySet()) {
					ItemStack at = (stands.get(key).getStackInSlot(0));
					ItemStack is = inputs.get(key);
					//ReikaJavaLibrary.pConsole(key+": "+is+" & "+at+" * "+this.getOutput(), this.getOutput().getDisplayName().endsWith("ter"));
					if (!ReikaItemHelper.matchStacks(at, is) || (is != null && is.stackTagCompound != null && !ItemStack.areItemStackTagsEqual(at, is))) {
						//ReikaJavaLibrary.pConsole(key+": "+is+" & "+at+" * "+this.getOutput());
						return false;
					}
				}
				//ReikaJavaLibrary.pConsole(this.matchRunes(table.worldObj, table.xCoord, table.yCoord, table.zCoord));
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
			if (ReikaItemHelper.matchStacks(is, main) && (main.stackTagCompound == null || ItemStack.areItemStackTagsEqual(is, main)))
				return true;
			for (List<Integer> key : inputs.keySet()) {
				ItemStack item = inputs.get(key);
				if (ReikaItemHelper.matchStacks(is, item) && (item.stackTagCompound == null || ItemStack.areItemStackTagsEqual(is, item)))
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

		@Override
		protected void getRequiredProgress(Collection<ProgressStage> c) {
			super.getRequiredProgress(c);
			c.add(ProgressStage.MULTIBLOCK);
		}

		@Override
		public ItemHashMap<Integer> getItemCounts() {
			ItemHashMap<Integer> map = new ItemHashMap();
			ItemStack[] items = this.getArrayForDisplay();
			map.put(items[4], 1);
			Collection<ItemStack> c = this.getAuxItems().values();
			for (ItemStack is : c) {
				Integer num = map.get(is);
				int n = num != null ? num.intValue() : 0;
				map.put(is, n+1);
			}
			return map;
		}

		@Override
		public ElementTagCompound getInputElements() {
			ElementTagCompound tag = super.getInputElements();
			for (ItemStack is : inputs.values()) {
				tag.addButMinimizeWith(ItemElementCalculator.instance.getValueForItem(is));
			}
			return tag;
		}

		@Override
		public Collection<ItemStack> getAllInputs() {
			Collection<ItemStack> c = new ArrayList();
			c.add(main);
			c.addAll(inputs.values());
			return c;
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

		protected CastingRecipe addAuraRequirement(CrystalElementProxy e, int amt) {
			return this.addAuraRequirement(CrystalElement.getFromAPI(e), amt);
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

		@Override
		protected void getRequiredProgress(Collection<ProgressStage> c) {
			super.getRequiredProgress(c);
			c.add(ProgressStage.PYLON);
			c.add(ProgressStage.REPEATER);
		}

		@Override
		public ElementTagCompound getInputElements() {
			ElementTagCompound tag = super.getInputElements();
			for (CrystalElement e : elements.elementSet()) {
				tag.addValueToColor(e, Math.max(2, elements.getValue(e)/10000));
			}
			return tag;
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
