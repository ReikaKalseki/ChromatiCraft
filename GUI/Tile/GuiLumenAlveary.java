/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2018
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI.Tile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.entity.player.EntityPlayer;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.GuiChromaBase;
import Reika.ChromatiCraft.ModInterface.Bees.TileEntityLumenAlveary;
import Reika.ChromatiCraft.ModInterface.Bees.TileEntityLumenAlveary.AlvearyEffect;
import Reika.ChromatiCraft.ModInterface.Bees.TileEntityLumenAlveary.LumenAlvearyEffect;
import Reika.ChromatiCraft.ModInterface.Bees.TileEntityLumenAlveary.PoweredAlvearyEffect;
import Reika.ChromatiCraft.ModInterface.Bees.TileEntityLumenAlveary.VisAlvearyEffect;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.Base.CoreContainer;
import Reika.DragonAPI.Instantiable.Data.CircularDivisionRenderer.ColorCallback;
import Reika.DragonAPI.Instantiable.Data.CircularDivisionRenderer.IntColorCallback;
import Reika.DragonAPI.Instantiable.Data.Proportionality;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.CollectionType;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.MapDeterminator;
import Reika.DragonAPI.Instantiable.Data.Maps.MultiMap.SortedDeterminator;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaStringParser;

import thaumcraft.api.aspects.Aspect;


public class GuiLumenAlveary extends GuiChromaBase {

	private static final Collection<AlvearyEffect> allEffects = new ArrayList();
	private static final Object BASIC_KEY = new Object();

	private final TileEntityLumenAlveary tile;
	private final ArrayList<AlvearyEffect> activeEffects = new ArrayList();
	private final AlvearyEffectControlSet controls = new AlvearyEffectControlSet(AlvearyEffect.class);
	private Categories currentCategory = null;
	private Object selectedKey;

	static {
		allEffects.addAll(TileEntityLumenAlveary.getEffectSet());
	}

	public GuiLumenAlveary(EntityPlayer ep, TileEntityLumenAlveary te) {
		super(new CoreContainer(ep, te), ep, te);
		tile = te;

		this.setData();
	}

	private AlvearyEffectControlSet getControlSet(Class<? extends AlvearyEffect> c) {
		AlvearyEffectControlSet set = controls;
		AlvearyEffectControlSet child = (AlvearyEffectControlSet)set.children.get(c);
		while (child != null) {
			set = child;
			child = (AlvearyEffectControlSet)set.children.get(c);
		}
		return set;
	}

	private AlvearyEffectControlSet getCurrentControlSet() {
		return currentCategory == null ? controls : this.getControlSet(currentCategory.classRef);
	}

	private void loadControllers() {
		controls.children.clear();
		controls.controls.clear();
		Collection<AlvearyEffect> c = new ArrayList(allEffects);

		AlvearyEffectControlSet bset = new AlvearyEffectControlSet(AlvearyEffect.class);
		LumenAlvearyEffectControlSet lset = new LumenAlvearyEffectControlSet();
		controls.addChild(AlvearyEffect.class, bset);
		controls.addChild(LumenAlvearyEffect.class, lset);
		Iterator<AlvearyEffect> it = c.iterator();
		while(it.hasNext()) {
			AlvearyEffect ae = it.next();
			if (ae instanceof LumenAlvearyEffect) {
				LumenAlvearyEffect lae = (LumenAlvearyEffect)ae;
				lset.addControl(lae.color, lae);
				it.remove();
			}
		}
		if (ModList.THAUMCRAFT.isLoaded()) {
			VisAlvearyEffectControlSet vset = new VisAlvearyEffectControlSet();
			controls.addChild(VisAlvearyEffect.class, vset);
			it = c.iterator();
			while(it.hasNext()) {
				AlvearyEffect ae = it.next();
				if (ae instanceof VisAlvearyEffect) {
					VisAlvearyEffect vae = (VisAlvearyEffect)ae;
					vset.addControl(vae.aspect, vae);
					it.remove();
				}
			}
		}
		for (AlvearyEffect ae : c) {
			bset.addControl(BASIC_KEY, ae);
		}
	}

	private void setData() {
		this.loadControllers();

		activeEffects.clear();
		activeEffects.addAll(tile.getSelectedEffects());
		Collections.sort(activeEffects, TileEntityLumenAlveary.effectSorter);

		controls.setActiveStates(activeEffects);
	}

	@Override
	protected void mouseClicked(int x, int y, int button) {
		super.mouseClicked(x, y, button);

		AlvearyEffectControlSet set = this.getCurrentControlSet();
		Proportionality buttons = set.getCurrentButtonSet(selectedKey);
		Object hover = buttons.getClickedSection(x, y);

		if (set.isTopLevel()) {
			AlvearyEffectControlSet cat = (AlvearyEffectControlSet)hover;
			selectedKey = null;
			ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, player, 1, 1);
			if (cat != null) {
				Categories c = cat.getCategory();
				if (c.isValid()) {
					currentCategory = c;
				}
			}
			else {
				currentCategory = null;
			}
		}
		else if (selectedKey != null) {
			AlvearyEffectControl e = (AlvearyEffectControl)hover;
			if (e != null) {
				e.isActive = !e.isActive;
				ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, player, 1, 1);
				ReikaPacketHelper.sendStringIntPacket(ChromatiCraft.packetChannel, ChromaPackets.ALVEARYEFFECT.ordinal(), tile, e.effect.ID, e.isActive ? 1 : 0);
			}
			else {//if (GuiScreen.isShiftKeyDown()) {
				selectedKey = null;
				ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, player, 1, 1);
			}
		}
		else {
			if (hover != null) {
				selectedKey = hover;
				ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, player, 1, 1);
			}
			else {
				ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, player, 1, 1);
				currentCategory = null;
			}
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int a, int b) {
		super.drawGuiContainerBackgroundLayer(f, a, b);
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		int x = j+xSize/2;
		int y = k+ySize/2;
		int r = 64;

		AlvearyEffectControlSet set = this.getCurrentControlSet();
		Proportionality buttons = set.getCurrentButtonSet(selectedKey);

		String ttip = null;
		Object hover = buttons.getClickedSection(a, b);
		if (set.isTopLevel()) {
			AlvearyEffectControlSet cat = (AlvearyEffectControlSet)hover;
			if (cat != null) {
				ttip = cat.type.getSimpleName().replace("Alveary", "")+"s";
				if (ttip.equals("Effects"))
					ttip = "BasicEffects";
				ttip = ReikaStringParser.splitCamelCase(ttip);
			}
		}
		else if (selectedKey != null) {
			AlvearyEffectControl e = (AlvearyEffectControl)hover;
			if (e != null) {
				e.isHovered = true;
				ttip = e.getTooltip();
			}
		}
		else {
			if (hover != null) {
				Collection<AlvearyEffectControl> c = set.controls.get(hover);
				for (AlvearyEffectControl ae2 : c) {
					ae2.isSetHovered = true;
				}
				String pre = set.getKeyName(hover);
				if (pre == null)
					pre = "";
				else
					pre = pre+": ";
				ttip = String.valueOf(pre+c.size()+" Effects");
			}
		}
		int key = System.identityHashCode(set.type);
		if (selectedKey != null)
			key ^= System.identityHashCode(selectedKey);
		buttons.setGeometry(x, y, r, key%360);
		buttons.resetColors();
		buttons.render();
		controls.resetHover();

		if (ttip != null) {
			List<String> li = new ArrayList();
			for (String s : ttip.split("\\\n")) {
				li.add(s);
			}
			api.drawSplitTooltipAt(fontRendererObj, li, a, b);
		}

		float lf = GL11.glGetFloat(GL11.GL_LINE_WIDTH);
		GL11.glLineWidth(4);
		api.drawCircle(x, y, r, 0);
		GL11.glLineWidth(lf);
	}

	@Override
	public String getGuiTexture() {
		return "alveary";
	}

	private static enum Categories {
		BASIC(AlvearyEffect.class),
		LUMEN(LumenAlvearyEffect.class),
		VIS(VisAlvearyEffect.class);

		public final Class<? extends AlvearyEffect> classRef;

		private static final Categories[] list = values();

		private Categories(Class<? extends AlvearyEffect> c) {
			classRef = c;
		}

		protected static Categories getByType(Class c) {
			for (Categories cg : list) {
				if (cg.classRef == c)
					return cg;
			}
			return null;
		}

		public boolean isValid() {
			switch(this) {
				case VIS:
					return ModList.THAUMCRAFT.isLoaded();
				default:
					return true;
			}
		}
	}

	private static class AlvearyEffectControl implements ColorCallback, Comparable<AlvearyEffectControl> {

		protected final AlvearyEffect effect;
		protected final int index;

		protected boolean isActive = true;
		protected boolean isHovered = false;
		protected boolean isSetHovered = false;

		private AlvearyEffectControl(AlvearyEffect l, int idx) {
			effect = l;
			index = idx;
		}

		public final String getTooltip() {
			String base = effect.getDescription()+"\nEnabled: "+isActive;
			if (effect instanceof PoweredAlvearyEffect) {
				PoweredAlvearyEffect pef = (PoweredAlvearyEffect)effect;
				base = base+"\n"+pef.getResource()+": "+pef.getCost()+"/cycle";
			}
			return base;
		}

		@Override
		public final int compareTo(AlvearyEffectControl o) {
			return TileEntityLumenAlveary.effectSorter.compare(effect, o.effect);
		}

		public int getColor(Object key) {
			return ReikaColorAPI.getColorWithBrightnessMultiplier(0xffffff, this.getBrightnessModifier());
		}

		protected final float getBrightnessModifier() {
			float base = isActive ? 0.9375F : 0.375F;
			double mod = isActive ? 240D : 400D;
			return (float)(base+0.0625F*Math.sin(System.currentTimeMillis()/mod+effect.hashCode()));
		}

	}

	private static class LumenAlvearyEffectControl extends AlvearyEffectControl {

		private final LumenAlvearyEffect effect;

		private final int hue;

		//private boolean isColorHovered = false;

		private LumenAlvearyEffectControl(LumenAlvearyEffect l, int idx) {
			super(l, idx);
			effect = l;
			hue = ReikaColorAPI.getHue(l.color.getColor())-45+15*index;
		}

		@Override
		public int getColor(Object key) {
			//int c = ReikaColorAPI.getModifiedHue(effect.color.getColor(), hue);
			int c = effect.color.getColor();
			c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, this.getBrightnessModifier());
			if (isHovered) {
				return ReikaColorAPI.mixColors(c, 0xffffff, 0.625F);
			}
			else if (isSetHovered) {
				return ReikaColorAPI.mixColors(c, 0xffffff, 0.8F);
			}
			else {
				return c;
			}
		}

	}

	//@ModDependent(ModList.THAUMCRAFT)
	private static class VisAlvearyEffectControl extends AlvearyEffectControl {

		private final VisAlvearyEffect effect;

		private VisAlvearyEffectControl(VisAlvearyEffect l, int idx) {
			super(l, idx);
			effect = l;
		}

		@Override
		public int getColor(Object key) {
			int c = effect.aspect.getColor();
			c = ReikaColorAPI.getColorWithBrightnessMultiplier(c, this.getBrightnessModifier());
			float f = isHovered ? 0.625F : isSetHovered ? 0.8F : 1;
			return ReikaColorAPI.mixColors(c, 0xffffff, f);
		}

	}

	private static class AlvearyEffectControlSet<K, V extends AlvearyEffectControl, E extends AlvearyEffect> implements Comparable<AlvearyEffectControlSet> {

		private final MultiMap<K, V> controls = new MultiMap(CollectionType.LIST, this.getDeterminator());
		private final HashMap<Class, AlvearyEffectControlSet> children = new HashMap();

		private final Proportionality<K> buttonsGlobal = new Proportionality(this.getDeterminator());
		private final Proportionality<AlvearyEffectControlSet> childButtons = new Proportionality(new SortedDeterminator());
		private final HashMap<K, Proportionality<V>> buttonsLocal = new HashMap();

		protected final Class type;

		protected AlvearyEffectControlSet(Class<E> c) {
			type = c;
			buttonsGlobal.drawSeparationLines = true;
			childButtons.drawSeparationLines = true;
		}

		public final Categories getCategory() {
			return Categories.getByType(this.type);
		}

		protected final void addControl(K k, E e) {
			V v = this.constructControl(k, e, controls.get(k).size());
			this.controls.addValue(k, v);
			buttonsGlobal.addValue(k, 10);
			buttonsGlobal.addColorRenderer(k, new IntColorCallback(this.getColorForKey(k)));
			Proportionality<V> p = this.buttonsLocal.get(k);
			if (p == null) {
				p = new Proportionality(this.getDeterminator());
				p.drawSeparationLines = true;
				this.buttonsLocal.put(k, p);
			}
			p.addValue(v, 10);
		}

		protected final void addChild(Class c, AlvearyEffectControlSet s) {
			children.put(c, s);
			this.childButtons.addValue(s, 10);
			this.childButtons.addColorRenderer(s, new IntColorCallback(s.getColor()));
		}

		private MapDeterminator getDeterminator() {
			return this.isSorted() ? new SortedDeterminator() : null;
		}

		protected boolean isSorted() {
			return false;
		}

		public String getKeyName(Object o) {
			return null;
		}

		protected int getColor() {
			return 0xffffff;
		}

		protected int getColorForKey(K k) {
			return 0xffffff;
		}

		protected V constructControl(K k, E e, int idx) {
			AlvearyEffectControl ret = new AlvearyEffectControl(e, idx);
			return (V)ret;
		}

		public final void setActiveStates(Collection<AlvearyEffect> c) {
			for (AlvearyEffectControl e : this.controls.allValues(false)) {
				e.isActive = c.contains(e.effect);
			}
			for (AlvearyEffectControlSet ch : children.values()) {
				ch.setActiveStates(c);
			}
		}

		public void resetHover() {
			for (AlvearyEffectControl e : this.controls.allValues(false)) {
				e.isHovered = e.isSetHovered = false;
			}
			for (AlvearyEffectControlSet ch : children.values()) {
				ch.resetHover();
			}
		}

		public Proportionality getCurrentButtonSet(Object key) {
			if (this.isTopLevel()) {
				return this.childButtons;
			}
			return key == null ? this.buttonsGlobal : this.buttonsLocal.get(key);
		}

		public final boolean isTopLevel() {
			return this.controls.isEmpty() && !this.children.isEmpty();
		}

		@Override
		public final int compareTo(AlvearyEffectControlSet o) {
			//return type.getName().compareToIgnoreCase(o.type.getName());
			//return ReikaJavaLibrary
			return Integer.compare(this.getIndex(), o.getIndex());
		}

		private final int getIndex() {
			return this.getCategory().ordinal();
		}

	}

	private static class LumenAlvearyEffectControlSet extends AlvearyEffectControlSet<CrystalElement, LumenAlvearyEffectControl, LumenAlvearyEffect> {

		protected LumenAlvearyEffectControlSet() {
			super(LumenAlvearyEffect.class);
		}

		@Override
		protected LumenAlvearyEffectControl constructControl(CrystalElement k, LumenAlvearyEffect e, int idx) {
			LumenAlvearyEffectControl ret = new LumenAlvearyEffectControl(e, idx);
			return ret;
		}

		@Override
		public String getKeyName(Object o) {
			return ((CrystalElement)o).displayName;
		}

		@Override
		protected int getColorForKey(CrystalElement k) {
			return k.getColor();
		}

		@Override
		protected int getColor() {
			return 0x22aaff;
		}

		@Override
		protected boolean isSorted() {
			return true;
		}

	}

	private static class VisAlvearyEffectControlSet extends AlvearyEffectControlSet<Aspect, VisAlvearyEffectControl, VisAlvearyEffect> {

		protected VisAlvearyEffectControlSet() {
			super(VisAlvearyEffect.class);
		}

		@Override
		protected VisAlvearyEffectControl constructControl(Aspect k, VisAlvearyEffect e, int idx) {
			VisAlvearyEffectControl ret = new VisAlvearyEffectControl(e, idx);
			return ret;
		}

		@Override
		public String getKeyName(Object o) {
			return ((Aspect)o).getName();
		}

		@Override
		protected int getColorForKey(Aspect k) {
			return k.getColor();
		}

		@Override
		protected int getColor() {
			return Aspect.MAGIC.getColor();
		}

	}

}
