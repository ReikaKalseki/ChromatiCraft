/*******************************************************************************
 *@author Reika Kalseki
 *
 *Copyright 2017
 *
 *All rights reserved.
 *Distribution of the software in any form is only allowed with
 *explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.Processing;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.ImmutablePair;

import net.minecraft.block.BlockEnchantmentTable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.Interfaces.EnchantableItem;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ChromaExtractable;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ChromaPowered;
import Reika.ChromatiCraft.Auxiliary.Interfaces.OperationInterval;
import Reika.ChromatiCraft.Auxiliary.Interfaces.VariableTexture;
import Reika.ChromatiCraft.Base.ChromaticEnchantment;
import Reika.ChromatiCraft.Base.ItemChromaTool;
import Reika.ChromatiCraft.Base.TileEntity.FluidReceiverInventoryBase;
import Reika.ChromatiCraft.Registry.ChromaEnchants;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Instantiable.StepTimer;
import Reika.DragonAPI.Libraries.ReikaEnchantmentHelper;
import Reika.DragonAPI.Libraries.ReikaRegistryHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.ModInteract.ItemHandlers.InfusionEnchantmentHandler;
import Reika.DragonAPI.ModInteract.ItemHandlers.TinkerToolHandler;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.Event.Result;

public class TileEntityAutoEnchanter extends FluidReceiverInventoryBase implements ChromaPowered, ChromaExtractable, OperationInterval, VariableTexture {

	private HashMap<Enchantment, Integer> selected = new HashMap();

	public static final int CHROMA_PER_LEVEL_BASE = 500;
	private static final HashMap<Enchantment, EnchantmentTier> tiers = new HashMap();
	private static final HashMap<Enchantment, Integer> boostedLevels = new HashMap();
	private static final HashSet<Enchantment> blacklist = new HashSet();
	private static final HashSet<ImmutablePair<Enchantment, Enchantment>> allowedPairs = new HashSet();

	static {
		tiers.put(Enchantment.baneOfArthropods, EnchantmentTier.WORTHLESS);
		tiers.put(Enchantment.smite, EnchantmentTier.WORTHLESS);

		tiers.put(Enchantment.knockback, EnchantmentTier.BASIC);
		tiers.put(Enchantment.punch, EnchantmentTier.BASIC);
		tiers.put(Enchantment.field_151369_A, EnchantmentTier.BASIC);
		tiers.put(ChromaEnchants.FASTSINK.getEnchantment(), EnchantmentTier.BASIC);

		tiers.put(Enchantment.fortune, EnchantmentTier.VALUABLE);
		tiers.put(Enchantment.sharpness, EnchantmentTier.VALUABLE);
		tiers.put(Enchantment.looting, EnchantmentTier.VALUABLE);
		tiers.put(Enchantment.power, EnchantmentTier.VALUABLE);
		tiers.put(Enchantment.protection, EnchantmentTier.VALUABLE);
		tiers.put(ChromaEnchants.USEREPAIR.getEnchantment(), EnchantmentTier.VALUABLE);
		tiers.put(ChromaEnchants.ENDERLOCK.getEnchantment(), EnchantmentTier.VALUABLE);
		tiers.put(ChromaEnchants.AGGROMASK.getEnchantment(), EnchantmentTier.VALUABLE);

		tiers.put(Enchantment.silkTouch, EnchantmentTier.RARE);
		tiers.put(Enchantment.infinity, EnchantmentTier.RARE);
		tiers.put(ChromaEnchants.RARELOOT.getEnchantment(), EnchantmentTier.RARE);
		tiers.put(ChromaEnchants.WEAPONAOE.getEnchantment(), EnchantmentTier.RARE);
		tiers.put(ChromaEnchants.HARVESTLEVEL.getEnchantment(), EnchantmentTier.RARE);
		tiers.put(ChromaEnchants.PHASING.getEnchantment(), EnchantmentTier.RARE);
		tiers.put(ChromaEnchants.BOSSKILL.getEnchantment(), EnchantmentTier.RARE);

		Enchantment multishot = ReikaEnchantmentHelper.getEnchantmentByName("Multishot");
		if (multishot != null)
			tiers.put(multishot, EnchantmentTier.RARE);
		Enchantment soulbound = ReikaEnchantmentHelper.getEnchantmentByName("Soulbound");
		if (soulbound != null)
			tiers.put(soulbound, EnchantmentTier.RARE);

		boostedLevels.put(Enchantment.fortune, 5);
		boostedLevels.put(Enchantment.looting, 5);
		boostedLevels.put(Enchantment.respiration, 5);
		boostedLevels.put(Enchantment.field_151370_z, 5); //luck of sea
		boostedLevels.put(Enchantment.power, 10);
		boostedLevels.put(Enchantment.sharpness, 10);
		boostedLevels.put(Enchantment.protection, 5);

		allowedPairs.add(new ImmutablePair(Enchantment.fortune, Enchantment.silkTouch));
		allowedPairs.add(new ImmutablePair(Enchantment.sharpness, Enchantment.baneOfArthropods));
		allowedPairs.add(new ImmutablePair(Enchantment.sharpness, Enchantment.smite));
	}

	private StepTimer progress = new StepTimer(40);
	public int progressTimer;

	public static Map<Enchantment, Integer> getBoostedLevels() {
		return Collections.unmodifiableMap(boostedLevels);
	}

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.ENCHANTER;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {

		if (this.canProgress()) {
			progress.update();
			if (progress.checkCap()) {
				if (!world.isRemote)
					this.applyEnchants();
			}
		}
		else {
			progress.reset();
		}
		progressTimer = progress.getTick();
	}

	public int getProgressScaled(int a) {
		return a*progressTimer / progress.getCap();
	}

	private boolean canProgress() {
		return this.isValid(inv[0]) && this.hasSufficientChroma() && this.enchanting();
	}

	private boolean hasSufficientChroma() {
		return this.getChromaLevel() >= this.getConsumedChroma();
	}

	private boolean enchanting() {
		if (selected.isEmpty())
			return false;
		for (Enchantment e : selected.keySet()) {
			int level = selected.get(e);
			if (level > 0)
				return true;
		}
		return false;
	}

	public int getChromaLevel() {
		return tank.getLevel();
	}

	public boolean addChroma(int amt) {
		if (tank.canTakeIn(amt)) {
			tank.addLiquid(amt, FluidRegistry.getFluid("chroma"));
			return true;
		}
		return false;
	}

	private boolean isValid(ItemStack is) {
		return is != null && this.isItemEnchantable(is) && this.areEnchantsValid(is);
	}

	private boolean isItemEnchantable(ItemStack is) {
		if (is.getItem() == Items.book || is.getItem() == Items.enchanted_book)
			return true;
		if (is.getItem() instanceof EnchantableItem)
			return true;
		if (is.getItem() instanceof ItemShears)
			return true;
		if (ChromaItems.BEEFRAME.matchWith(is))
			return true;
		if (ChromaItems.HELP.matchWith(is))
			return true;
		if (ModList.TINKERER.isLoaded() && (TinkerToolHandler.getInstance().isTool(is) || TinkerToolHandler.getInstance().isWeapon(is)))
			return true;
		if (Loader.isModLoaded("Backpack") && is.getItem().getClass().getName().toLowerCase(Locale.ENGLISH).contains("backpack"))
			return true;
		if (Loader.isModLoaded("EnderStorage") && is.getItem().getClass().getName().toLowerCase(Locale.ENGLISH).contains("enderpouch"))
			return true;
		if (ModList.THAUMICTINKER.isLoaded() && is.getItem().getClass().getName().toLowerCase(Locale.ENGLISH).contains("ichorpouch"))
			return true;
		if (ModList.THAUMCRAFT.isLoaded() && is.getItem().getClass().getName().toLowerCase(Locale.ENGLISH).contains("focuspouch"))
			return true;
		return is.getItem().getItemEnchantability(is) > 0;
	}

	private boolean areEnchantsValid(ItemStack is) {
		Item i = is.getItem();
		Collection<Enchantment> has = ReikaEnchantmentHelper.getEnchantments(is).keySet();
		for (Enchantment e : selected.keySet()) {
			if (!this.isEnchantValid(e, is, i, true))
				return false;
			if (!this.isCompatible(has, e))
				return false;
		}
		return true;
	}

	public static enum EnchantValidity {
		VALID("Valid"),
		WRONGITEM("Invalid for Item"),
		INCOMPATIBLEWITHEXISTING("Incompatible with Existing"),
		INCOMPATIBLEWITHSELF("Incompatible with Selected"),
		;

		public final String desc;

		private EnchantValidity(String s) {
			desc = s;
		}

		public int getTextColor() {
			switch(this) {
				case VALID:
					return 0xffffff;
				case WRONGITEM:
					return 0xfff740;
				case INCOMPATIBLEWITHEXISTING:
					return 0xffc740;
				case INCOMPATIBLEWITHSELF:
					return 0xff9090;
			}
			return 0;
		}
	}

	public EnchantValidity isCompatible(Enchantment e) {
		if (!this.isCompatible(selected.keySet(), e))
			return EnchantValidity.INCOMPATIBLEWITHSELF;
		if (inv[0] != null) {
			if (!this.isCompatible(ReikaEnchantmentHelper.getEnchantments(inv[0]).keySet(), e))
				return EnchantValidity.INCOMPATIBLEWITHEXISTING;
			if (!this.isEnchantValid(e, inv[0], inv[0].getItem(), false))
				return EnchantValidity.WRONGITEM;
		}
		return EnchantValidity.VALID;
	}

	private boolean isCompatible(Collection<Enchantment> c, Enchantment e) {
		for (Enchantment e2 : c) {
			if (!this.isCompatible(e, e2))
				return false;
		}
		return true;
	}

	private boolean isCompatible(Enchantment e, Enchantment e2) {
		return e == e2 || allowedPairs.contains(new ImmutablePair(e, e2)) || allowedPairs.contains(new ImmutablePair(e2, e)) || ReikaEnchantmentHelper.areEnchantsCompatible(e, e2);
	}

	public boolean isEnchantValid(Enchantment e, ItemStack is, Item i, boolean checkLevels) {
		if (i == Items.book || i == Items.enchanted_book) {
			if (!e.isAllowedOnBooks()) {
				return false;
			}
		}
		else if (ModList.TINKERER.isLoaded() && TinkerToolHandler.getInstance().isTool(is)) {
			if (!(e instanceof ChromaticEnchantment))
				return false;
			if (((ChromaticEnchantment)e).type != EnumEnchantmentType.all && ((ChromaticEnchantment)e).type != EnumEnchantmentType.digger) {
				return false;
			}
		}
		else if (ModList.TINKERER.isLoaded() && TinkerToolHandler.getInstance().isWeapon(is)) {
			if (!(e instanceof ChromaticEnchantment))
				return false;
			if (((ChromaticEnchantment)e).type != EnumEnchantmentType.all && ((ChromaticEnchantment)e).type != EnumEnchantmentType.weapon) {
				return false;
			}
		}

		if (checkLevels && ReikaEnchantmentHelper.getEnchantmentLevel(e, is) >= selected.get(e))
			return false;

		if (i instanceof EnchantableItem) {
			Result res = ((EnchantableItem)i).getEnchantValidity(e, is);
			switch(res) {
				case ALLOW:
					return true;
				case DEFAULT:
					break;
				case DENY:
					return false;
			}
		}

		if (ChromaItems.BEEFRAME.matchWith(is))
			if (e != Enchantment.unbreaking)
				return false;

		boolean soulbound = e.getName().toLowerCase(Locale.ENGLISH).contains("soulbound");

		if (soulbound && this.isSoulboundable(is, i))
			return true;

		if (i instanceof ItemShears)
			return e.type == EnumEnchantmentType.digger || e.type == EnumEnchantmentType.breakable || e.type == EnumEnchantmentType.all;

		return i == Items.book || i == Items.enchanted_book ? true : e.canApply(is);
	}

	private boolean isSoulboundable(ItemStack is, Item i) {
		if (ChromaItems.HELP.matchWith(is))
			return true;
		if (Loader.isModLoaded("Backpack") && i.getClass().getName().toLowerCase(Locale.ENGLISH).contains("backpack"))
			return true;
		if (Loader.isModLoaded("EnderStorage") && i.getClass().getName().toLowerCase(Locale.ENGLISH).contains("enderpouch"))
			return true;
		if (ModList.THAUMCRAFT.isLoaded() && i.getClass().getName().toLowerCase(Locale.ENGLISH).contains("focuspouch"))
			return true;
		if (ModList.THAUMICTINKER.isLoaded() && i.getClass().getName().toLowerCase(Locale.ENGLISH).contains("ichorpouch"))
			return true;
		if (i instanceof ItemChromaTool || i instanceof ItemTool || i instanceof ItemSword || i instanceof ItemShears || i instanceof ItemArmor)
			return true;
		return false;
	}

	private void applyEnchants() {
		if (inv[0].getItem() == Items.book)
			inv[0] = new ItemStack(Items.enchanted_book);
		ReikaEnchantmentHelper.removeEnchantments(inv[0], selected.keySet());
		if (inv[1] != null) {
			ReikaEnchantmentHelper.removeEnchantments(inv[1], selected.keySet());
			int dmg = 0;
			for (Entry<Enchantment, Integer> e : selected.entrySet())
				dmg += e.getValue()*ReikaRandomHelper.getRandomBetween(4D, 15D)*this.getCostFactor(e.getKey());
			inv[1].damageItem(dmg, this.getPlacer());
		}
		ReikaEnchantmentHelper.applyEnchantments(inv[0], selected);
		tank.removeLiquid(this.getConsumedChroma());
		this.syncAllData(true);
	}

	public int getConsumedChroma() {
		int total = 0;
		for (Enchantment e : selected.keySet()) {
			float level = selected.get(e);
			if (inv[1] != null)
				level = Math.max(0.25F, level-0.8F*ReikaEnchantmentHelper.getEnchantmentLevel(e, inv[1]));
			float add = level*CHROMA_PER_LEVEL_BASE*this.getCostFactor(e);
			if (this.isAssisted())
				add /= Math.min(2.5F, 1+this.getAssistPower());
			add = Math.max(add, 50*level);
			add = ReikaMathLibrary.roundToNearestX(100, Math.round(add));
			total += (int)add;
		}
		return total;
	}

	private float getCostFactor(Enchantment e) {
		EnchantmentTier t = tiers.get(e);
		if (t == null)
			t = EnchantmentTier.NORMAL;
		return t.costFactor;
	}

	public boolean setEnchantment(Enchantment e, int level) {
		this.onEnchantChanged(e);
		level = Math.min(this.getMaxEnchantmentLevel(e), level);
		if (level <= 0) {
			this.removeEnchantment(e);
			return true;
		}
		else {
			selected.put(e, level);
			return true;
		}
	}

	private void onEnchantChanged(Enchantment e) {
		progress.reset();
		progressTimer = 0;
	}

	public int getMaxEnchantmentLevel(Enchantment e) {
		if (e == Enchantment.fortune)
			return 5;
		if (e == Enchantment.looting)
			return 5;
		if (e == Enchantment.respiration)
			return 5;
		if (e == Enchantment.field_151370_z) //luck of sea
			return 5;
		if (e == Enchantment.power)
			return 10;
		if (e == Enchantment.sharpness)
			return 10;
		if (e == Enchantment.unbreaking)
			return 5;
		return e.getMaxLevel();
	}

	public void removeEnchantment(Enchantment e) {
		selected.remove(e);
		this.onEnchantChanged(e);
	}

	public boolean incrementEnchantment(Enchantment e, boolean toMax) {
		int level = this.getEnchantment(e);
		int newlevel = toMax ? this.getMaxEnchantmentLevel(e) : level+1;
		return this.setEnchantment(e, newlevel);
	}

	public void decrementEnchantment(Enchantment e, boolean toZero) {
		int level = this.getEnchantment(e);
		int newlevel = toZero ? 0 : Math.max(level-1, 0);
		this.setEnchantment(e, newlevel);
	}

	public void clearEnchantments() {
		selected.clear();
	}

	public int getEnchantment(Enchantment e) {
		return selected.containsKey(e) ? selected.get(e) : 0;
	}

	public Map<Enchantment, Integer> getEnchantments() {
		return Collections.unmodifiableMap(selected);
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	@Override
	public int getSizeInventory() {
		return 2;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return this.isValid(itemstack);
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return ReikaEnchantmentHelper.hasEnchantments(itemstack);
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	protected void writeSyncTag(NBTTagCompound NBT) {
		super.writeSyncTag(NBT);

		for (int i = 0; i < Enchantment.enchantmentsList.length; i++) {
			if (Enchantment.enchantmentsList[i] != null) {
				int lvl = this.getEnchantment(Enchantment.enchantmentsList[i]);
				NBT.setInteger(Enchantment.enchantmentsList[i].getName(), lvl);
			}
		}
	}

	@Override
	protected void readSyncTag(NBTTagCompound NBT) {
		super.readSyncTag(NBT);

		selected = new HashMap();
		for (int i = 0; i < Enchantment.enchantmentsList.length; i++) {
			if (Enchantment.enchantmentsList[i] != null) {
				int lvl = NBT.getInteger(Enchantment.enchantmentsList[i].getName());
				if (lvl > 0)
					selected.put(Enchantment.enchantmentsList[i], lvl);
			}
		}
	}

	@Override
	public int getCapacity() {
		return 12000;//6000;
	}

	@Override
	public Fluid getInputFluid() {
		return FluidRegistry.getFluid("chroma");
	}

	@Override
	public boolean canReceiveFrom(ForgeDirection from) {
		return true;
	}

	@Override
	public float getOperationFraction() {
		return !this.canProgress() ? 0 : progress.getFraction();
	}

	@Override
	public OperationState getState() {
		return this.isValid(inv[0]) && this.enchanting() ? (this.hasSufficientChroma() ? OperationState.RUNNING : OperationState.PENDING) : OperationState.INVALID;
	}

	private static enum EnchantmentTier {
		WORTHLESS(0.25F),
		BASIC(0.75F),
		NORMAL(1F),
		VALUABLE(1.5F),
		RARE(2);

		public final float costFactor;

		private EnchantmentTier(float f) {
			costFactor = f;
		}
	}

	public static boolean isBlacklisted(Enchantment e) {
		if (e.getName().toLowerCase(Locale.ENGLISH).startsWith("enchantment.molecule")) //Minechem
			return true;
		if (ModList.THAUMCRAFT.isLoaded() && InfusionEnchantmentHandler.instance.isInfusionEnchantment(e))
			return true;
		return blacklist.contains(e);
	}

	public static void blacklistEnchantment(Enchantment e) {
		if (ReikaEnchantmentHelper.isVanillaEnchant(e)) {
			ChromatiCraft.logger.logError("You cannot blacklist vanilla enchantments!");
		}
		else if (e instanceof ChromaticEnchantment) {
			ChromatiCraft.logger.logError("You cannot blacklist ChromatiCraft enchantments!");
		}
		else {
			blacklist.add(e);
			ChromatiCraft.logger.log("Received request to blacklist enchantment "+e.getName()+" from "+ReikaRegistryHelper.getActiveLoadingMod());
		}
	}

	public static boolean canPlayerGetEnchantment(Enchantment e, EntityPlayer ep) {
		return e instanceof ChromaticEnchantment ? ((ChromaticEnchantment)e).isVisibleToPlayer(ep) : true;
	}

	public boolean isAssisted() {
		return worldObj.getBlock(xCoord, yCoord+1, zCoord) instanceof BlockEnchantmentTable;
	}

	private float getAssistPower() {
		float ret = 0;
		float max = 0;

		int y = yCoord+1;
		for (int dz = -1; dz <= 1; dz++) {
			for (int dx = -1; dx <= 1; dx++) {
				if ((dz != 0 || dx != 0) && worldObj.isAirBlock(xCoord+dx, y, zCoord+dz) && worldObj.isAirBlock(xCoord+dx, y+1, zCoord+dz)) {
					ret += ForgeHooks.getEnchantPower(worldObj, xCoord+dx*2, y, zCoord+dz*2);
					ret += ForgeHooks.getEnchantPower(worldObj, xCoord+dx*2, y+1, zCoord+dz*2);
					max += 2;

					if (dx != 0 && dz != 0) {
						ret += ForgeHooks.getEnchantPower(worldObj, xCoord+dx*2, y, zCoord+dz);
						ret += ForgeHooks.getEnchantPower(worldObj, xCoord+dx*2, y+1, zCoord+dz);
						ret += ForgeHooks.getEnchantPower(worldObj, xCoord+dx, y, zCoord+dz*2);
						ret += ForgeHooks.getEnchantPower(worldObj, xCoord+dx, y+1, zCoord+dz*2);
						max += 4;
					}
				}
			}
		}
		return ret/max;
	}

	@Override
	public int getIconState(int side) {
		return side > 1 && this.isAssisted() ? 1 : 0;
	}

	@Override
	public void removeLiquid(int amt) {
		tank.removeLiquid(amt);
	}

	@Override
	public boolean hasWork() {
		return this.getState() == OperationState.RUNNING;
	}

}
