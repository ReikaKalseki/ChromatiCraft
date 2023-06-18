package Reika.ChromatiCraft.Base;

import java.util.ArrayList;
import java.util.Collection;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.google.common.base.Strings;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Rendering.ReikaGuiAPI;

public abstract class GuiLetterSearchable<E> extends GuiChromaBase {

	protected int index = 0;
	protected ArrayList<E> list = new ArrayList();
	protected ArrayList<E> filteredList = new ArrayList();
	private String filterString = null;
	//private HashMap<Character, Integer> charIndex = new HashMap();

	public GuiLetterSearchable(Container c, EntityPlayer ep, TileEntityChromaticBase te) {
		super(c, ep, te);
		this.buildList(ep);
	}

	protected abstract String getString(E val);
	//protected abstract boolean isIndexable(E val);

	protected abstract Collection<E> getAllEntries(EntityPlayer ep);
	protected abstract void sortEntries(ArrayList<E> li);

	private void buildList(EntityPlayer ep) {
		list.addAll(this.getAllEntries(ep));
		this.sortEntries(list);
		char c = 0;
		for (int i = 0; i < list.size(); i++) {
			E a = list.get(i);/*
			if (this.isIndexable(a)) {
				char c2 = this.getString(a).toLowerCase(Locale.ENGLISH).charAt(0);
				if (c2 != c) {
					c = c2;
					charIndex.put(c2, i);
				}
			}*/
		}
		this.resetFilter();
	}

	protected void resetFilter() {
		this.index = 0;
		this.filteredList = new ArrayList(this.list);
	}

	protected final E getActive() {
		return this.filteredList.isEmpty() ? null : filteredList.get(index);
	}
	/*
	protected final char getAlphaKeyAt(int idx) {
		return this.getString(list.get(idx)).toLowerCase(Locale.ENGLISH).charAt(0);
	}

	protected final int getLastIndexOfPreviousLetter() {
		int idx = index;
		char at = this.getAlphaKeyAt(idx);
		char next = at;
		while (next == at && idx > 0) {
			idx--;
			at = this.getAlphaKeyAt(idx);
		}
		return idx;
	}

	protected final int getFirstIndexOfNextLetter() {
		int idx = index;
		char at = this.getAlphaKeyAt(idx);
		char next = at;
		while (next == at) {
			idx++;
			if (idx == list.size())
				idx = 0;
			at = this.getAlphaKeyAt(idx);
		}
		return idx;
	}
	 */
	protected final void decrIndex() {/*
		if (GuiScreen.isShiftKeyDown())
			index = this.getLastIndexOfPreviousLetter();
		else */if (index > 0)
			index--;
	}

	protected final void incrIndex() {/*
		if (GuiScreen.isShiftKeyDown())
			index = this.getFirstIndexOfNextLetter();
		else */if (index < this.filteredList.size()-1)
			index++;
	}

	@Override
	protected final void keyTyped(char c, int idx) {
		if (idx == Keyboard.KEY_HOME) {
			index = 0;
			ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, player, 0.5F, 0.6F);
		}/*
		else if (idx == Keyboard.KEY_UP) {
			index = this.getLastIndexOfPreviousLetter();
			ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, player, 0.5F, 0.85F);
		}
		else if (idx == Keyboard.KEY_DOWN) {
			index = this.getFirstIndexOfNextLetter();
			ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, player, 0.5F, 0.85F);
		}
		else if (Character.isLetter(c)) {
			Integer seek = charIndex.get(c);
			if (seek != null) {
				if (this.getAlphaKeyAt(index) == c && index < list.size()-1 && this.getAlphaKeyAt(index+1) == c)
					index++;
				else
					index = seek;
				ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, player, 0.5F, 0.7F);
			}
		}*/
		else if (Character.isLetter(c) || Character.isDigit(c) || ",./?<>;:'\\\"[]{}|+=_-)(*&^%$#@!~`".contains(String.valueOf(c))) {
			this.addToSearch(c);
		}
		else if (idx == Keyboard.KEY_BACK && !this.filterString.isEmpty()) {
			this.filterString = this.filterString.substring(0, this.filterString.length()-1);
			if (this.filterString.isEmpty())
				this.filterString = null;
			this.updateFilter();
		}
		else {
			super.keyTyped(c, idx);
		}
	}

	private void addToSearch(char c) {
		String sc = String.valueOf(c);
		if (Strings.isNullOrEmpty(filterString)) {
			filterString = sc;
		}
		else {
			filterString = this.filterString+sc;
		}
		this.updateFilter();
	}

	private void updateFilter() {
		this.resetFilter();
		this.filteredList.removeIf(e -> !this.getString(e).contains(filterString));
		if (this.filteredList.isEmpty())
			this.filteredList = new ArrayList(this.list);
		ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, player, 0.5F, 0.5F);
	}

	protected final String getFilterString() {
		return this.filterString;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);
	}

	protected void drawSearch() {
		if (!Strings.isNullOrEmpty(filterString)) {
			int j = (width - xSize) / 2;
			int k = (height - ySize) / 2;

			GL11.glDisable(GL11.GL_BLEND);
			BlendMode.DEFAULT.apply();
			GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.25F);
			ReikaTextureHelper.bindTexture(ChromatiCraft.class, this.getFullTexturePath());
			float z = zLevel;
			//zLevel = ?;
			this.drawTexturedModalRect(j, k, 0, 0, xSize, ySize);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

			GL11.glPushMatrix();
			ReikaGuiAPI.instance.drawCenteredStringNoShadow(fontRendererObj, filterString, j+xSize/2, k+ySize/2, 0xffffff);
			GL11.glPopMatrix();
			zLevel = z;
		}
	}

}
