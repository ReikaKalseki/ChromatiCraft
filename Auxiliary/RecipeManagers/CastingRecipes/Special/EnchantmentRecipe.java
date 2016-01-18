package Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipes.Special;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.MultiBlockCastingRecipe;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Libraries.ReikaEnchantmentHelper;


public final class EnchantmentRecipe extends MultiBlockCastingRecipe {

	private static final HashMap<Coordinate, CrystalElement> runeMap = new HashMap();

	public final ChromaResearch parent;

	public final Enchantment enchantment;
	public final int level;

	public EnchantmentRecipe(ChromaResearch r, ItemStack ctr, ItemStack side, ItemStack top, ItemStack bottom, Enchantment e, int lvl) {
		super(ctr, ctr);

		this.addAuxItem(top, 0, -2);
		this.addAuxItem(bottom, 0, 2);
		this.addAuxItem(side, 2, 0);
		this.addAuxItem(side, -2, 0);

		this.addAuxItem(ChromaItems.ELEMENTAL.getStackOf(CrystalElement.PURPLE), -2, -2);
		this.addAuxItem(ChromaItems.ELEMENTAL.getStackOf(CrystalElement.BLACK), 2, 2);

		this.addAuxItem(ChromaStacks.bindingCrystal, -2, 2);
		this.addAuxItem(ChromaStacks.bindingCrystal, 2, -2);

		for (Coordinate c : runeMap.keySet()) {
			this.addRune(runeMap.get(c), c.xCoord, c.yCoord, c.zCoord);
		}

		enchantment = e;
		level = lvl;

		parent = r;
		if (parent == null)
			throw new RegistrationException(ChromatiCraft.instance, "No parent for enchantment recipe "+ctr.getDisplayName()+" {"+e.getName()+" "+lvl+"}");

		this.setFragment(ChromaResearch.ENCHANTING);
	}

	@Override
	public NBTTagCompound getOutputTag(NBTTagCompound input) {
		NBTTagCompound tag = input != null ? (NBTTagCompound)input.copy() : new NBTTagCompound();
		//ReikaJavaLibrary.pConsole("Adding "+enchantment+" "+level+" to "+input);
		ReikaEnchantmentHelper.addEnchantment(tag, enchantment, level, false);
		return tag;
	}

	@Override
	protected boolean isValidCentralNBT(ItemStack is) {
		return super.isValidCentralNBT(is) && ReikaEnchantmentHelper.getEnchantmentLevel(enchantment, is) < level;
	}

	@Override
	public int getDuration() {
		return 12*super.getDuration();
	}

	public static Map<Coordinate, CrystalElement> getEnchantingRunes() {
		return Collections.unmodifiableMap(runeMap);
	}

	static {
		runeMap.put(new Coordinate(4, -1, -4), CrystalElement.PURPLE);
		runeMap.put(new Coordinate(-4, -1, 4), CrystalElement.BLACK);
	}

}
