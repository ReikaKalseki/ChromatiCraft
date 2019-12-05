package Reika.ChromatiCraft.Magic;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Magic.Lore.LoreManager;
import Reika.ChromatiCraft.Magic.Progression.ChromaResearchManager;
import Reika.ChromatiCraft.Magic.Progression.ProgressAccess;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;

public enum ElementBufferCapacityBoost {

	ALLCOLORS(10000,			ProgressStage.ALLCOLORS),
	DIMENSION(2F,				ProgressStage.DIMENSION),
	TURBOCHARGE(1.5F,			ProgressStage.TURBOCHARGE),
	CTM(2F,						ProgressStage.CTM),
	TOWER(100000, 1F,			ProgressStage.TOWER),
	LORECOMPLETE(200000, 1.2F,	LoreManager.instance);

	public final int increaseLinear;
	public final float increaseFactor;
	public final int maxValueIfNotPresent;

	private final ProgressAccess requirement;
	private ElementBufferCapacityBoost dependency;
	private ItemStack ingredient;

	private static final String NBT_TAG = "BufferBoosts";

	public static final ElementBufferCapacityBoost[] list = values();

	private ElementBufferCapacityBoost(int clamp, ProgressAccess req) {
		this(0, 1, clamp, req);
	}

	private ElementBufferCapacityBoost(float fac, ProgressAccess req) {
		this(0, fac, req);
	}

	private ElementBufferCapacityBoost(int lin, float fac, ProgressAccess req) {
		this(lin, fac, Integer.MAX_VALUE, req);
	}

	private ElementBufferCapacityBoost(int lin, float fac, int max, ProgressAccess req) {
		increaseFactor = fac;
		increaseLinear = lin;
		maxValueIfNotPresent = max;
		requirement = req;
	}

	public boolean playerHas(EntityPlayer ep) {
		return this.isAvailableToPlayer(ep) && this.isTagPresent(ep);
	}

	public boolean isGrantedAutomatically() {
		return this == ALLCOLORS;
	}

	private NBTTagList getTag(EntityPlayer ep) {
		NBTTagCompound NBT = ChromaResearchManager.instance.getRootNBTTag(ep);
		NBTTagList tag = NBT.getTagList(NBT_TAG, NBTTypes.STRING.ID);
		if (tag == null || tag.tagList.isEmpty()) {
			NBT.setTag(NBT_TAG, tag);
		}
		return tag;
	}

	private boolean isTagPresent(EntityPlayer ep) {
		NBTTagList li = this.getTag(ep);
		if (li == null || li.tagList.isEmpty())
			return false;
		return false;
	}

	public void give(EntityPlayer ep) {
		if (this.isAvailableToPlayer(ep))
			this.doGive(ep);
	}

	private void doGive(EntityPlayer ep) {
		this.getTag(ep).appendTag(new NBTTagString(this.name()));
		//ChromatiCraft.logger.log("Player "+ep.getCommandSenderName()+" just upgraded their element buffer with "+this+"; capacity is now "+PlayerElementBuffer.instance.getPlayerMaximumCap(ep));
	}

	public boolean isAvailableToPlayer(EntityPlayer ep) {
		boolean flag = requirement.playerHas(ep) && (dependency == null || dependency.playerHas(ep));
		if (flag && this.isGrantedAutomatically())
			this.doGive(ep);
		return flag;
	}

	public ItemStack getIngredient() {
		return ingredient != null ? ingredient.copy() : null;
	}

	public static ArrayList<ElementBufferCapacityBoost> getAvailableBoosts(EntityPlayer ep) {
		ArrayList<ElementBufferCapacityBoost> li = new ArrayList();
		for (ElementBufferCapacityBoost e : list) {
			if (e.isAvailableToPlayer(ep) && !e.playerHas(ep)) {
				li.add(e);
			}
		}
		return li;
	}

	public static int calculateCap(int base, EntityPlayer ep) {
		for (ElementBufferCapacityBoost e : list) {
			if (e.playerHas(ep)) {
				base *= e.increaseFactor;
				base += e.increaseLinear;
			}
			else {
				base = Math.min(base, e.maxValueIfNotPresent);
			}
		}
		return base;
	}

	static {
		for (int i = 1; i <= CTM.ordinal(); i++) {
			list[i].dependency = list[i-1];
		}
		LORECOMPLETE.dependency = TOWER;

		DIMENSION.ingredient = ChromaStacks.glowcavedust;
		TURBOCHARGE.ingredient = ChromaStacks.boostroot;
		CTM.ingredient = ChromaStacks.echoCrystal;
		TOWER.ingredient = ChromaStacks.unknownFragments;
	}

}
