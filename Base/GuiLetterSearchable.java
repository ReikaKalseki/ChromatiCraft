package Reika.ChromatiCraft.Base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import com.google.common.base.Strings;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.Instantiable.GUI.CustomSoundGuiButton.CustomSoundImagedGuiButton;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;
import Reika.DragonAPI.Libraries.Java.ReikaGLHelper.BlendMode;
import Reika.DragonAPI.Libraries.Rendering.ReikaGuiAPI;
import Reika.DragonAPI.ModInteract.DeepInteract.NEIIntercept.KeyConsumingGui;

public abstract class GuiLetterSearchable<E> extends GuiChromaBase implements KeyConsumingGui {

	protected int index = 0;
	protected ArrayList<E> list = new ArrayList();
	protected ArrayList<E> filteredList = new ArrayList();
	private String filterString = null;
	//private HashMap<Character, Integer> charIndex = new HashMap();

	private float searchFadeTick;
	private long lastMillis;

	private boolean noResultsFound = false;

	private boolean searchActive = false;

	public GuiLetterSearchable(Container c, EntityPlayer ep, TileEntityChromaticBase te) {
		super(c, ep, te);
		this.buildList(ep);
	}

	@Override
	public void initGui() {
		super.initGui();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		buttonList.add(new CustomSoundImagedGuiButton(-600, j+this.getSearchButtonX(), k+this.getSearchButtonY(), 10, 10, 70, 66, "Textures/GUIs/buttons.png", ChromatiCraft.class, this).setTooltip("Search..."));
	}

	protected int getSearchButtonX() {
		return 3;
	}

	protected int getSearchButtonY() {
		return 3;
	}

	@Override
	protected void actionPerformed(GuiButton b) {
		if (b.id == -600) {
			searchActive = true;
		}
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
		this.resetFilter(true);
	}

	protected void resetFilter(boolean clearFilter) {
		this.index = 0;
		this.filteredList = new ArrayList(this.list);
		noResultsFound = false;
		if (clearFilter) {
			filterString = null;
		}
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
	public final boolean consumeKey(char c, int keyCode) {
		return this.handleKey(c, keyCode);
	}

	@Override
	protected final void keyTyped(char c, int idx) {
		if (!this.isSearchActive() || !this.handleKey(c, idx))
			super.keyTyped(c, idx);
	}

	private boolean handleKey(char c, int idx) {
		if (!this.searchActive)
			return false;
		if (idx == Keyboard.KEY_HOME) {
			index = 0;
			ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, player, 0.5F, 0.67F);
			return true;
		}
		else if (isSearchableCharacter(c)) {
			this.addToSearch(c);
			return true;
		}
		else if (idx == Keyboard.KEY_BACK && !Strings.isNullOrEmpty(filterString)) {
			this.filterString = this.filterString.substring(0, this.filterString.length()-1);
			if (this.filterString.isEmpty())
				this.filterString = null;
			this.updateFilter();
			return true;
		}
		else if ((idx == Keyboard.KEY_RETURN || idx == Keyboard.KEY_NUMPADENTER) && !Strings.isNullOrEmpty(filterString)) {
			E selected = this.getActive();
			this.resetFilter(true);
			this.index = this.list.indexOf(selected);
			ReikaSoundHelper.playClientSound(ChromaSounds.CAST, player, 0.5F, 1.5F);
			this.onSelected(selected);
			searchActive = false;
			return true;
		}
		else if (idx == Keyboard.KEY_ESCAPE && !Strings.isNullOrEmpty(filterString)) {
			this.resetFilter(true);
			searchActive = false;
			return true;
		}
		else if (idx == Keyboard.KEY_UP || idx == Keyboard.KEY_RIGHT) {
			this.incrIndex();
			ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, player, 0.5F, 1F);
			return true;
		}
		else if (idx == Keyboard.KEY_DOWN || idx == Keyboard.KEY_LEFT) {
			this.decrIndex();
			ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, player, 0.5F, 1F);
			return true;
		}
		else {
			return false;
		}
	}

	public static boolean isSearchableCharacter(char c) {
		return Character.isLetter(c) || Character.isDigit(c) || " ,./?<>;:'\\\"[]{}|+=_-)(*&^%$#@!~`".contains(String.valueOf(c));
	}

	private void addToSearch(char c) {
		String sc = String.valueOf(c).toLowerCase(Locale.ENGLISH);
		if (Strings.isNullOrEmpty(filterString)) {
			filterString = sc;
		}
		else {
			filterString = this.filterString+sc;
		}
		this.updateFilter();
	}

	private void updateFilter() {
		this.resetFilter(false);
		if (!Strings.isNullOrEmpty(filterString)) {
			filteredList.clear();
			ArrayList<E> start = new ArrayList(list);
			ArrayList<E> contains = new ArrayList(list);
			start.removeIf(e -> !this.getString(e).toLowerCase(Locale.ENGLISH).startsWith(filterString));
			contains.removeIf(e -> !this.getString(e).toLowerCase(Locale.ENGLISH).contains(filterString));
			contains.removeAll(start);
			this.filteredList.addAll(start);
			this.filteredList.addAll(contains);
		}
		if (this.filteredList.isEmpty()) {
			this.filteredList = new ArrayList(this.list);
			this.noResultsFound = true;
		}
		ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, player, 0.5F, 0.67F);
	}

	protected final String getFilterString() {
		return this.filterString;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);
	}

	protected void drawSearch() {
		if (this.isSearchActive() && !Strings.isNullOrEmpty(filterString)) {
			long ms = System.currentTimeMillis()-lastMillis;
			searchFadeTick = Math.min(1, searchFadeTick+ms/150F);
			GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
			GL11.glEnable(GL11.GL_BLEND);
			BlendMode.DEFAULT.apply();
			GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.67F*this.searchFadeTick);
			ReikaTextureHelper.bindTexture(ChromatiCraft.class, this.getFullTexturePath());
			this.drawTexturedModalRect(0, 0, 0, 0, xSize, ySize);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

			GL11.glPushMatrix();
			double s = 2;
			GL11.glScaled(s, s, s);
			ReikaGuiAPI.instance.drawCenteredStringNoShadow(fontRendererObj, filterString, (int)(xSize/(2*s)), (int)(ySize/(2*s)), this.noResultsFound ? 0xffa0a0 : 0xa0ffa0);
			GL11.glPopMatrix();
			GL11.glPopAttrib();
		}
		else {
			searchFadeTick = 0;
		}
		lastMillis = System.currentTimeMillis();
	}

	protected boolean isSearchActive() {
		return true;
	}

	protected void onSelected(E obj) {

	}

}
