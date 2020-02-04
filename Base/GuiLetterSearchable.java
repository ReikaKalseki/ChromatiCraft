package Reika.ChromatiCraft.Base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;

public abstract class GuiLetterSearchable<E> extends GuiChromaBase {

	protected int index = 0;
	protected ArrayList<E> list = new ArrayList();
	private HashMap<Character, Integer> charIndex = new HashMap();

	public GuiLetterSearchable(Container c, EntityPlayer ep, TileEntityChromaticBase te) {
		super(c, ep, te);
		this.buildList(ep);
	}

	protected abstract String getString(E val);
	protected abstract boolean isIndexable(E val);

	protected abstract Collection<E> getAllEntries(EntityPlayer ep);
	protected abstract void sortEntries(ArrayList<E> li);

	private void buildList(EntityPlayer ep) {
		list.addAll(this.getAllEntries(ep));
		this.sortEntries(list);
		char c = 0;
		for (int i = 0; i < list.size(); i++) {
			E a = list.get(i);
			if (this.isIndexable(a)) {
				char c2 = this.getString(a).toLowerCase(Locale.ENGLISH).charAt(0);
				if (c2 != c) {
					c = c2;
					charIndex.put(c2, i);
				}
			}
		}
	}

	protected final E getActive() {
		return list.get(index);
	}

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

	protected final void decrIndex() {
		if (GuiScreen.isShiftKeyDown())
			index = this.getLastIndexOfPreviousLetter();
		else if (index > 0)
			index--;
	}

	protected final void incrIndex() {
		if (GuiScreen.isShiftKeyDown())
			index = this.getFirstIndexOfNextLetter();
		else if (index < list.size()-1)
			index++;
	}

	@Override
	protected final void keyTyped(char c, int idx) {
		if (idx == Keyboard.KEY_HOME) {
			index = 0;
			ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, player, 0.5F, 0.6F);
		}
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
		}
		else {
			super.keyTyped(c, idx);
		}
	}

}
