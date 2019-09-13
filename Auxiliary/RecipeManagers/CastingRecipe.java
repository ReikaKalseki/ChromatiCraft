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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.CastingAPI.FXCallback;
import Reika.ChromatiCraft.API.CrystalElementProxy;
import Reika.ChromatiCraft.API.Interfaces.CastingRecipeViewer.APICastingRecipe;
import Reika.ChromatiCraft.API.Interfaces.CastingRecipeViewer.LumenRecipe;
import Reika.ChromatiCraft.API.Interfaces.CastingRecipeViewer.MultiRecipe;
import Reika.ChromatiCraft.API.Interfaces.CastingRecipeViewer.RuneRecipe;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.Interfaces.CoreRecipe;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Special.ConfigRecipe;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.ItemElementCalculator;
import Reika.ChromatiCraft.Magic.RuneShape;
import Reika.ChromatiCraft.Magic.RuneShape.RuneViewer;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaResearchManager;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingAuto;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityItemStand;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Instantiable.Data.KeyedItemStack;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Instantiable.Recipe.ItemMatch;
import Reika.DragonAPI.Instantiable.Recipe.RecipePattern;
import Reika.DragonAPI.Libraries.ReikaRecipeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class CastingRecipe implements APICastingRecipe {

	private final ItemStack out;
	public final RecipeType type;
	private IRecipe recipe;
	private ChromaResearch fragment;

	public FXCallback effectCallback;

	protected CastingRecipe(ItemStack out, IRecipe recipe) {
		this(out, RecipeType.CRAFTING, recipe);
	}

	public final int getTier() {
		return type.ordinal();
	}

	private CastingRecipe(ItemStack out, RecipeType type, IRecipe recipe) {
		this.out = out;
		this.type = type;
		this.recipe = recipe;
	}

	public final void setFragment(ChromaResearch r) {
		if (r != fragment) {
			if (fragment == null)
				fragment = r;
			else
				throw new IllegalStateException("Cannot change the research type of a recipe once initialized!");
		}
	}

	public final ChromaResearch getFragment() {
		return fragment;
	}

	public final ItemStack getOutput() {
		return ReikaItemHelper.getSizedItemStack(out, Math.max(out.stackSize, this.getNumberProduced()));
	}

	@SideOnly(Side.CLIENT)
	public ItemStack getOutputForDisplay() {
		return this.getOutput();
	}

	@SideOnly(Side.CLIENT)
	public ItemStack getOutputForDisplay(ItemStack center) {
		return this.getOutputForDisplay();
	}

	protected int getNumberProduced() {
		return 1;
	}

	public void onRecipeTick(TileEntityCastingTable te) {
		if (effectCallback != null && te.worldObj.isRemote) {
			effectCallback.onEffectTick(te, this, this.getOutput());
		}
	}

	public ChromaSounds getSoundOverride(TileEntityCastingTable te, int craftSoundTimer) {
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

	public boolean crafts(ItemStack is) {
		if (is == null)
			return false;
		if (is.stackTagCompound != null) {
			is = is.copy();
			ChromaTiles c = ChromaTiles.getTileByCraftedItem(is);
			if (c != null) {
				if (c.isLumenTile())
					is.stackTagCompound.removeTag("energy");
			}
		}
		return ReikaItemHelper.matchStacks(is, out) && ItemStack.areItemStackTagsEqual(is, out);
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

	public boolean canRunRecipe(TileEntity te, EntityPlayer ep) {
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

	public void onCrafted(TileEntityCastingTable te, EntityPlayer ep, ItemStack output, int amount) {
		if (!te.worldObj.isRemote) {
			ChromaResearchManager.instance.givePlayerRecipe(ep, this);
			te.giveRecipe(ep, this);
			ep.addExperience(this.getExperience()*amount/4);
		}
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
		return this instanceof CoreRecipe ? Integer.MAX_VALUE : Math.max(1, this.getTypicalCraftedAmount()*3/4);
	}

	/** Multiplicative factor. Return zero to make all over-threshold yield zero XP */
	public float getPenaltyMultiplier() {
		return 0.75F;
	}

	public final int getIDCode() {
		int flag = this.getClass().getName().hashCode();
		flag = flag ^ out.getItem().getClass().getName().hashCode();
		flag = flag ^ Math.max(out.stackSize, this.getNumberProduced());
		flag = flag ^ out.getItemDamage();
		flag = flag ^ (out.stackTagCompound != null ? out.stackTagCompound.hashCode() : 0);
		return flag;
	}

	public final String getIDString() {
		String flag = this.getClass().getName();
		flag = flag + " for " + out.getItem().getClass().getName();
		flag = flag + " x " + Math.max(out.stackSize, this.getNumberProduced());
		flag = flag + " @ " + out.getItemDamage();
		flag = flag + " * " + (out.stackTagCompound != null ? out.stackTagCompound.toString() : 0);
		flag = flag + " from " + this.getIngredientsID();
		return flag;
	}

	protected String getIngredientsID() {
		return ReikaRecipeHelper.toDeterministicString(recipe);
	}

	public NBTTagCompound getOutputTag(EntityPlayer ep, NBTTagCompound input) {
		return null;
	}

	public NBTTagCompound handleNBTResult(TileEntityCastingTable te, EntityPlayer ep, NBTTagCompound originalCenter, NBTTagCompound tag) {
		return tag;
	}

	public ItemStack getCentralLeftover(ItemStack is) {
		return null;
	}

	public float getAutomationCostFactor(TileEntityCastingAuto ae, TileEntityCastingTable te, ItemStack is) {
		return 1;
	}

	public int getEnhancedTableAccelerationFactor() {
		return 4;
	}

	public boolean canBeStacked() {
		return true;
	}

	public final float getRecipeStackedTimeFactor(TileEntityCastingTable te, int stack) {
		float f = this.getConsecutiveStackingTimeFactor(te);
		double t = 0;
		for (int i = 0; i < stack; i++) {
			t += Math.pow(f, i);
		}
		return (float)t;
	}

	protected float getConsecutiveStackingTimeFactor(TileEntityCastingTable te) {
		return 0.9375F;
	}

	public float[] getHarmonics() {
		return null;
	}

	public final void setOwner(ItemStack crafted, EntityPlayer ep) {
		if (crafted.getItem() instanceof ItemChromaTool) {
			((ItemChromaTool)crafted.getItem()).setOwner(crafted, ep);
		}
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof CastingRecipe && o.getClass() == this.getClass() && this.matchRecipeData((CastingRecipe)o);
	}

	protected boolean matchRecipeData(CastingRecipe cr) {
		return recipe.equals(cr.recipe);
	}

	public final void validate() {
		try {
			if (out == null)
				throw new IllegalArgumentException("Null output");
			if (out.getItem() == null)
				throw new IllegalArgumentException("Null-item output");
			if (this instanceof MultiBlockCastingRecipe) {
				MultiBlockCastingRecipe mr = (MultiBlockCastingRecipe)this;
				if (mr.main == null)
					throw new IllegalArgumentException("Null central item");
				if (mr.main.getItem() == null)
					throw new IllegalArgumentException("Null-item central item");
			}
			else {
				if (recipe == null)
					throw new IllegalArgumentException("Null recipe");
			}

			this.getIDString(); //will trigger failures as a last resort to catch anything missed
		}
		catch (Exception e) {
			throw new RegistrationException(ChromatiCraft.instance, "Invalid casting recipe was going to be added: "+this.getClass(), e);
		}

		if (this instanceof TempleCastingRecipe) {
			if (!MultiBlockCastingRecipe.class.isAssignableFrom(this.getClass()) && ((TempleCastingRecipe)this).runes.isEmpty()) {
				ChromatiCraft.logger.log("WARNING! Recipe "+this.toString()+" contains no runes!");
			}
		}
	}

	public boolean isModded() {
		return !this.getClass().getName().contains("CastingRecipes.Special") && !ReikaItemHelper.getRegistrantMod(this.getOutput()).equals(ModList.CHROMATICRAFT.modLabel);
	}

	public boolean isShapeless() {
		return recipe instanceof ShapelessRecipes || recipe instanceof ShapelessOreRecipe;
	}

	public boolean canBeSimpleAutomated() {
		return false;
	}

	public static class TempleCastingRecipe extends CastingRecipe implements RuneRecipe {

		private static final ArrayList<Coordinate> runeRing = new ArrayList();
		private static final HashMap<Coordinate, CrystalElement> allRunes = new HashMap();

		static {
			runeRing.add(new Coordinate(-2, -1, -2));
			runeRing.add(new Coordinate(-1, -1, -2));
			runeRing.add(new Coordinate(0, -1, -2));
			runeRing.add(new Coordinate(1, -1, -2));
			runeRing.add(new Coordinate(2, -1, -2));
			runeRing.add(new Coordinate(2, -1, -1));
			runeRing.add(new Coordinate(2, -1, 0));
			runeRing.add(new Coordinate(2, -1, 1));
			runeRing.add(new Coordinate(2, -1, 2));
			runeRing.add(new Coordinate(1, -1, 2));
			runeRing.add(new Coordinate(0, -1, 2));
			runeRing.add(new Coordinate(-1, -1, 2));
			runeRing.add(new Coordinate(-2, -1, 2));
			runeRing.add(new Coordinate(-2, -1, 1));
			runeRing.add(new Coordinate(-2, -1, 0));
			runeRing.add(new Coordinate(-2, -1, -1));
		}

		private final RuneShape runes = new RuneShape();

		public TempleCastingRecipe(ItemStack out, IRecipe recipe) {
			this(out, RecipeType.TEMPLE, recipe);
		}

		private TempleCastingRecipe(ItemStack out, RecipeType type, IRecipe recipe) {
			super(out, type, recipe);
		}

		public boolean requiresTuningKey() {
			return false;
		}

		protected final boolean matchRunes(TileEntityCastingTable te) {
			//runes.place(world, x, y, z);
			//ReikaJavaLibrary.pConsole(this.getOutput().getDisplayName());
			return runes.matchAt(te.worldObj, te.xCoord, te.yCoord, te.zCoord);
		}

		@Override
		public boolean canRunRecipe(TileEntity te, EntityPlayer ep) {
			if (te != null && this.requiresTuningKey()) {
				if (!this.checkForTuningKey(te, ep)) {
					return false;
				}
			}
			return super.canRunRecipe(te, ep);
		}

		private boolean checkForTuningKey(TileEntity te, EntityPlayer ep) {
			HashMap<Coordinate, CrystalElement> key = this.calculateTuningKey(ep);
			HashMap<Coordinate, CrystalElement> map = ((TileEntityCastingTable)te).getCurrentTuningMap();
			return key.equals(map);
		}

		private final HashMap<Coordinate, CrystalElement> calculateTuningKey(EntityPlayer ep) {
			HashMap<Coordinate, CrystalElement> map = new HashMap();
			long seed = ep.getUniqueID().getLeastSignificantBits() ^ ep.getUniqueID().getLeastSignificantBits();
			if (ep.capabilities.isCreativeMode)
				seed ^= ep.getUniqueID().hashCode();
			Random rand = new Random(seed);
			rand.nextBoolean();
			rand.nextBoolean();
			int n = 12;//8+rand.nextInt(5);
			int i = 0;
			ArrayList<Coordinate> li = new ArrayList(TileEntityCastingTable.getTuningKeys());
			Collections.sort(li);
			Collections.shuffle(li, rand);
			for (Coordinate c : li) {
				map.put(c, CrystalElement.elements[rand.nextInt(16)]);
				i++;
				if (i >= n)
					break;
			}
			return map;
		}

		protected TempleCastingRecipe addRuneRingRune(CrystalElement e) {
			Coordinate c = runeRing.get(e.ordinal());
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
				if (e != color) {
					if (this instanceof ConfigRecipe.Rune || this instanceof ConfigRecipe.Multi || this instanceof ConfigRecipe.Pylon) {
						throw new IllegalArgumentException("Rune conflict @ "+x+", "+y+", "+z+": "+color+"("+color.ordinal()+") over "+e+"("+e.ordinal()+");");
					}
					else {
						throw new RegistrationException(ChromatiCraft.instance, "Rune conflict @ "+x+", "+y+", "+z+": "+color+"("+color.ordinal()+") over "+e+"("+e.ordinal()+"); map:=\n"+this.getRuneMap(y));
					}
				}
			}
			allRunes.put(c, color);
		}

		private String getRuneMap(int y) {
			StringBuilder sb = new StringBuilder();
			for (int i = -5; i <= 5; i++) {
				for (int k = -5; k <= 5; k++) {
					Coordinate c = new Coordinate(k, y, i);
					CrystalElement e = allRunes.get(c);
					if (e == null && runeRing.contains(c))
						e = CrystalElement.elements[runeRing.indexOf(c)];
					sb.append("[");
					sb.append(e != null ? e.ordinal()+(e.ordinal() < 10 ? " " : "") : (i == 0 && k == 0 ? (y == 0 ? "TB" : "CT") : "XX"));
					sb.append("]");
				}
				sb.append("\n");
			}
			return sb.toString();
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

		public final Map<List<Integer>, CrystalElementProxy> getRunePositions() {
			HashMap<List<Integer>, CrystalElementProxy> map = new HashMap();
			Map<Coordinate, CrystalElement> rv = this.getRunes().getRunes();
			for (Coordinate c : rv.keySet()) {
				map.put(c.asIntList(), rv.get(c).getAPIProxy());
			}
			return map;
		}

		@Override
		public boolean match(TileEntityCastingTable table) {
			return super.match(table) && this.matchRunes(table);
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

		@Override
		protected boolean matchRecipeData(CastingRecipe cr) {
			return super.matchRecipeData(cr) && runes.equals(((TempleCastingRecipe)cr).runes);
		}

	}

	public static class MultiBlockCastingRecipe extends TempleCastingRecipe implements MultiRecipe {

		private static final Comparator<List<Integer>> auxItemPosSorter = new Comparator<List<Integer>>() {

			@Override
			public int compare(List<Integer> o1, List<Integer> o2) {
				return Integer.compare(o1.get(1), o2.get(1))*10+Integer.compare(o1.get(0), o2.get(0)); //z is more important for display
			}

		};

		private final HashMap<List<Integer>, ItemMatch> inputs = new HashMap();
		private final ItemStack main;

		public MultiBlockCastingRecipe(ItemStack out, ItemStack main) {
			this(out, main, RecipeType.MULTIBLOCK);
		}

		private MultiBlockCastingRecipe(ItemStack out, ItemStack main, RecipeType type) {
			super(out, type, null);
			this.main = main;
		}

		public final ItemStack getMainInput() {
			return main.copy();
		}

		protected final MultiBlockCastingRecipe addAuxItem(Block b, int dx, int dz) {
			return this.addAuxItem(new ItemStack(b), dx, dz);
		}

		protected final MultiBlockCastingRecipe addAuxItem(Item i, int dx, int dz) {
			return this.addAuxItem(new ItemStack(i), dx, dz);
		}

		protected final MultiBlockCastingRecipe addAuxItem(ItemStack is, int dx, int dz) {
			return this.addAuxItem(new ItemMatch(is), dx, dz);
		}

		protected final MultiBlockCastingRecipe addAuxItem(Fluid f, int dx, int dz) {
			return this.addAuxItem(new ItemMatch(f), dx, dz);
		}

		protected final MultiBlockCastingRecipe addAuxItem(String s, int dx, int dz) {
			return this.addAuxItem(new ItemMatch(s), dx, dz);
		}

		private MultiBlockCastingRecipe addAuxItem(ItemMatch is, int dx, int dz) {
			if (dx == 0 && dz == 0)
				throw new RegistrationException(ChromatiCraft.instance, "Tried adding an item to the center of a recipe "+this+": "+is);
			inputs.put(Arrays.asList(dx, dz), is);
			return this;
		}

		public Map<List<Integer>, ItemMatch> getAuxItems() {
			return Collections.unmodifiableMap(inputs);
		}

		public final Map<List<Integer>, Set<KeyedItemStack>> getInputItems() {
			HashMap<List<Integer>, Set<KeyedItemStack>> map = new HashMap();
			for (List<Integer> li : inputs.keySet()) {
				map.put(li, inputs.get(li).getItemList());
			}
			return map;
		}

		public final HashMap<WorldLocation, ItemMatch> getOtherInputs(World world, int x, int y, int z) {
			HashMap<WorldLocation, ItemMatch> map = new HashMap();
			for (List<Integer> li : inputs.keySet()) {
				ItemMatch is = inputs.get(li).copy();
				int dx = li.get(0);
				int dz = li.get(1);
				int dy = y+(Math.abs(dx) != 4 && Math.abs(dz) != 4 ? 0 : 1);
				WorldLocation loc = new WorldLocation(world, x+dx, dy, z+dz);
				map.put(loc, is);
			}
			return map;
		}

		public int getRequiredCentralItemCount() {
			return 1;
		}

		protected final void addAuxItems(MultiBlockCastingRecipe r) {
			for (Entry<List<Integer>, ItemMatch> e : r.getAuxItems().entrySet()) {
				Set<KeyedItemStack> set = e.getValue().getItemList();
				ItemStack is = set.iterator().next().getItemStack();
				this.addAuxItem(is, e.getKey().get(0), e.getKey().get(1));
			}
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
			if (ReikaItemHelper.matchStacks(main, ctr) && this.isValidCentralNBT(main) && main.stackSize >= this.getRequiredCentralItemCount()) {
				HashMap<List<Integer>, TileEntityItemStand> stands = table.getOtherStands();
				//ReikaJavaLibrary.pConsole(stands.size(), this instanceof RepeaterTurboRecipe);
				if (stands.size() != 24)
					return false;
				//ReikaJavaLibrary.pConsole(stands.keySet());
				for (List key : stands.keySet()) {
					ItemStack at = (stands.get(key).getStackInSlot(0));
					ItemMatch is = inputs.get(key);
					//ReikaJavaLibrary.pConsole(key+": "+is+" & "+at+" * "+this.getOutput(), this.getOutput().getDisplayName().endsWith("ter"));
					if (is == null && at != null) {
						return false;
					}
					else if (is != null && !is.match(at)) {
						//ReikaJavaLibrary.pConsole(key+": "+is+" & "+at+" * "+this.getOutput());
						return false;
					}
				}
				//ReikaJavaLibrary.pConsole(this.matchRunes(table.worldObj, table.xCoord, table.yCoord, table.zCoord));
				if (this.matchRunes(table)) {
					return true;
				}
			}
			return false;
		}

		protected boolean isValidCentralNBT(ItemStack is) {
			return this.getMainInput().stackTagCompound == null || ItemStack.areItemStackTagsEqual(this.getMainInput(), is);
		}

		@Override
		public int getDuration() {
			return 100;
		}

		@Override
		public boolean usesItem(ItemStack is) {
			if (ReikaItemHelper.matchStacks(is, main) && this.isValidCentralNBT(is))
				return true;
			for (List<Integer> key : inputs.keySet()) {
				ItemMatch item = inputs.get(key);
				if (item.match(is))
					return true;
			}
			return false;
		}

		@Override
		protected String getIngredientsID() {
			TreeMap<List<Integer>, ItemMatch> map = new TreeMap(auxItemPosSorter);
			map.putAll(inputs);
			return main+" & "+map;
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
		@SideOnly(Side.CLIENT)
		public ItemHashMap<Integer> getItemCounts() {
			ItemHashMap<Integer> map = new ItemHashMap();
			ItemStack[] items = this.getArrayForDisplay();
			map.put(items[4], 1);
			Collection<ItemStack> c = new ArrayList();
			for (ItemMatch m : inputs.values()) {
				c.add(m.getCycledItem());
			}
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
			for (ItemMatch is : inputs.values()) {
				for (KeyedItemStack ks : is.getItemList()) {
					tag.addButMinimizeWith(ItemElementCalculator.instance.getValueForItem(ks.getItemStack()));
				}
			}
			return tag;
		}

		@Override
		public final Collection<ItemStack> getAllInputs() {
			Collection<ItemStack> c = new ArrayList();
			c.add(main);
			for (ItemMatch m : inputs.values()) {
				for (KeyedItemStack ks : m.getItemList()) {
					c.add(ks.getItemStack());
				}
			}
			return c;
		}

		@Override
		protected boolean matchRecipeData(CastingRecipe cr) {
			return ItemStack.areItemStacksEqual(main, ((MultiBlockCastingRecipe)cr).main) && inputs.equals(((MultiBlockCastingRecipe)cr).inputs);
		}
	}

	public static class PylonCastingRecipe extends MultiBlockCastingRecipe implements LumenRecipe {

		private final ElementTagCompound elements = new ElementTagCompound();

		public PylonCastingRecipe(ItemStack out, ItemStack main) {
			super(out, main, RecipeType.PYLON);
		}

		public ElementTagCompound getRequiredAura() {
			return elements.copy();
		}

		protected CastingRecipe addAuraRequirement(CrystalElementProxy e, int amt) {
			return this.addAuraRequirement(CrystalElement.getFromAPI(e), amt);
		}

		protected CastingRecipe addAuraRequirement(CrystalElement e, int amt) {
			elements.addValueToColor(e, amt);
			return this;
		}

		protected CastingRecipe addAuraRequirement(ElementTagCompound e) {
			elements.add(e);
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

		public int getEnergyCost(CrystalElementProxy e) {
			return elements.getValue(CrystalElement.getFromAPI(e));
		}

		@Override
		public boolean canBeStacked() {
			return false;
		}

		@Override
		public boolean requiresTuningKey() {
			return false;
		}

		@Override
		protected String getIngredientsID() {
			return super.getIngredientsID()+" % "+elements;
		}

		@Override
		protected boolean matchRecipeData(CastingRecipe cr) {
			return super.matchRecipeData(cr) && elements.equals(((PylonCastingRecipe)cr).elements);
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

	public static class RecipeComparator implements Comparator<CastingRecipe> {

		@Override
		public int compare(CastingRecipe o1, CastingRecipe o2) {
			return this.getIndex(o1)-this.getIndex(o2);
		}

		private int getIndex(CastingRecipe r) {
			int flags = 0;

			if (r.fragment != null) {
				flags += 10000000*r.fragment.sectionIndex();
				flags += 1000000*r.fragment.level.ordinal();
				flags += 10000*r.fragment.ordinal();
			}
			flags += 1000*r.type.ordinal();
			flags += 100*r.getOutput().getItemDamage();
			flags += 1*r.getNumberProduced();

			return flags;
		}

	}

	public static class RecipeNameComparator implements Comparator<CastingRecipe> {

		@Override
		public int compare(CastingRecipe o1, CastingRecipe o2) {
			return o1.getOutput().getDisplayName().compareToIgnoreCase(o2.getOutput().getDisplayName());
		}

	}

}
