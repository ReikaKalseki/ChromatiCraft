package Reika.ChromatiCraft.Magic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.function.BiFunction;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Magic.Lore.LoreManager;
import Reika.ChromatiCraft.Magic.Progression.ProgressAccess;
import Reika.ChromatiCraft.Magic.Progression.ProgressStage;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Libraries.ReikaNBTHelper.NBTTypes;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;

public enum ElementBufferCapacityBoost {

	ALLCOLORS(6000,				ProgressStage.ALLCOLORS),
	ABILITY(30000,				ProgressStage.ABILITY),
	ALLOYS(180000,				ProgressStage.ALLOY),
	DIMENSION(300000,			ProgressStage.DIMENSION),
	TURBOCHARGE(720000,			ProgressStage.TURBOCHARGE),
	CTM(1200000,				ProgressStage.CTM),
	TOWER(1.5F,					ProgressStage.TOWER),
	LORECOMPLETE(2F,			LoreManager.instance);

	public final BiFunction<Integer, Boolean, Integer> capFunction;

	private final ProgressAccess requirement;
	private ElementBufferCapacityBoost dependency;
	private ItemStack ingredient;

	private static final String NBT_TAG = "BufferBoosts";

	public static final ElementBufferCapacityBoost[] list = values();

	private static final TreeSet<Integer> niceNumberPowers = new TreeSet();

	private ElementBufferCapacityBoost(int clamp, ProgressAccess req) {
		this((f, has) -> has ? f : Math.min(f, clamp), req);
	}

	private ElementBufferCapacityBoost(float fac, ProgressAccess req) {
		this((f, has) -> has ? roundToNiceNumber(f*fac) : f, req);
	}

	private ElementBufferCapacityBoost(BiFunction<Integer, Boolean, Integer> func, ProgressAccess req) {
		capFunction = func;
		requirement = req;
	}

	private static int roundToNiceNumber(float f) {
		int num = (int)f;
		int pow = 0;
		if (f > niceNumberPowers.last() && f < 100) {
			return niceNumberPowers.last();
		}
		else {
			while (num > niceNumberPowers.last()) {
				num /= 10;
				pow++;
			}
		}
		Integer v1 = niceNumberPowers.floor(num);
		Integer v2 = niceNumberPowers.ceiling(num);
		int v = v1 != null && Math.abs(v1-num) < Math.abs(v2-num) ? v1 : v2;
		return v*ReikaMathLibrary.intpow2(10, pow);
	}

	public void drawIcon(Tessellator v5, int x, int y, double s) {
		GL11.glColor4f(1, 1, 1, 1);
		ReikaRenderHelper.disableLighting();
		ReikaRenderHelper.disableEntityLighting();
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/infoicons.png");
		double u = 0.5+0.0625*this.ordinal();
		double v = 0.0625;
		double d = 0.0625;
		v5.startDrawingQuads();
		Tessellator.instance.setBrightness(240);
		v5.setColorOpaque_I(0xffffff);
		v5.addVertexWithUV(x, y+s, 0, u, v+d);
		v5.addVertexWithUV(x+s, y+s, 0, u+d, v+d);
		v5.addVertexWithUV(x+s, y, 0, u+d, v);
		v5.addVertexWithUV(x, y, 0, u, v);
		v5.draw();
	}

	public boolean playerHas(EntityPlayer ep) {
		return this.isAvailableToPlayer(ep) && this.isTagPresent(ep);
	}

	public boolean isGrantedAutomatically() {
		return ingredient == null;//this == ALLCOLORS || this == ABILITY || this == LORECOMPLETE;
	}

	private NBTTagList getTag(EntityPlayer ep) {
		NBTTagCompound NBT = PlayerElementBuffer.instance.getTag(ep);
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
		for (Object o : li.tagList) {
			if (((NBTTagString)o).func_150285_a_().equals(this.name()))
				return true;
		}
		return false;
	}

	public boolean give(EntityPlayer ep) {
		if (this.isAvailableToPlayer(ep))
			return this.doGive(ep);
		else
			return false;
	}

	public void remove(EntityPlayer ep) {
		NBTTagList li = this.getTag(ep);
		Iterator<Object> it = li.tagList.iterator();
		while (it.hasNext()) {
			NBTTagString tag = (NBTTagString)it.next();
			if (tag.func_150285_a_().equals(this.name())) {
				it.remove();
				PlayerElementBuffer.instance.addToPlayer(ep, CrystalElement.BLACK, 0, true);
				break;
			}
		}
	}

	private boolean doGive(EntityPlayer ep) {
		if (!this.isTagPresent(ep)) {
			this.getTag(ep).appendTag(new NBTTagString(this.name()));
			//ChromatiCraft.logger.log("Player "+ep.getCommandSenderName()+" just upgraded their element buffer with "+this+"; capacity is now "+PlayerElementBuffer.instance.getPlayerMaximumCap(ep));
			PlayerElementBuffer.instance.addToPlayer(ep, CrystalElement.BLACK, 0, true);
			return true;
		}
		return false;
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

	public static int calculateCap(EntityPlayer ep) {
		int amt = 10000000;
		for (int i = list.length-1; i >= 0; i--) {
			ElementBufferCapacityBoost e = list[i];
			amt = e.capFunction.apply(amt, e.playerHas(ep));
		}
		return amt;
	}

	static {
		for (int i = 1; i <= CTM.ordinal(); i++) {
			list[i].dependency = list[i-1];
		}
		LORECOMPLETE.dependency = TOWER;

		ALLOYS.ingredient = ChromaStacks.etherBerries;
		DIMENSION.ingredient = ChromaStacks.glowcavedust;
		TURBOCHARGE.ingredient = ChromaStacks.boostroot;
		CTM.ingredient = ChromaStacks.echoCrystal;
		TOWER.ingredient = ChromaStacks.unknownFragments;

		niceNumberPowers.add(1);
		niceNumberPowers.add(3);
		niceNumberPowers.add(6);
		niceNumberPowers.add(9);
		niceNumberPowers.add(12);
		niceNumberPowers.add(15);
		niceNumberPowers.add(18);
		niceNumberPowers.add(24);
		niceNumberPowers.add(27);
		niceNumberPowers.add(30);
		niceNumberPowers.add(36);
		niceNumberPowers.add(48);
		niceNumberPowers.add(60);
		niceNumberPowers.add(72);
		niceNumberPowers.add(90);
		niceNumberPowers.add(96);
	}

}
