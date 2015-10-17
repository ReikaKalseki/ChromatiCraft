/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.DragonAPI.IO.DirectResourceManager;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaMystcraftHelper;

import com.xcompwiz.mystcraft.api.word.DrawableWord;
import com.xcompwiz.mystcraft.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.world.IAgeController;


public class MystPages {

	public static void registerPages() {
		for (int i = 0; i < Symbols.list.length; i++) {
			Symbols p = Symbols.list[i];
			ReikaMystcraftHelper.getAPI().getWordAPI().registerWord(p.getID(), p.word);
		}

		for (int i = 0; i < Pages.list.length; i++) {
			Pages p = Pages.list[i];
			ReikaMystcraftHelper.registerAgeSymbol(p);
			ChromatiCraft.logger.log("Registering custom MystCraft page '"+p.name+"'");
		}
	}

	public static enum Pages implements IAgeSymbol {

		PYLONS("Crystal Pylons", 50, Symbols.STRUCTURE, Symbols.ENERGY),
		STRUCTURES("Buried Structures", 200, Symbols.STRUCTURE, Symbols.CIVILIZATION),
		PLANTS("Chroma Plants", 20, Symbols.MATERIAL, Symbols.HERBAL),
		ORES("Chroma Ores", 10, Symbols.MATERIAL, Symbols.MINERAL),
		CRYSTALS("Cave Crystals", 80, Symbols.MATERIAL, Symbols.MINERAL, Symbols.ENERGY),
		TREES("Dye Trees", 5, Symbols.HERBAL, Symbols.ENERGY),
		DENSE("Dense Generation", 400, Symbols.UPGRADE);

		public final String name;
		public final int instability;

		private final HashMap<Integer, Boolean> dimCache = new HashMap();

		private final ArrayList<Symbols> icons;

		private static final Pages[] list = values();

		private Pages(String s, int ins, Symbols... icons) {
			name = s;
			instability = ins;

			this.icons = ReikaJavaLibrary.makeListFromArray(icons);
			this.icons.add(0, Symbols.BASE);
		}

		public boolean existsInWorld(World age) {
			Boolean b = dimCache.get(age.provider.dimensionId);
			if (b == null) {
				b = ReikaMystcraftHelper.isMystAge(age) && ReikaMystcraftHelper.isSymbolPresent(age, this.identifier());
				dimCache.put(age.provider.dimensionId, b);
			}
			return b;
		}
		/*
		@SideOnly(Side.CLIENT)
		public void render() {
			for (Symbols s : icons) {
				ReikaTextureHelper.bindTexture(ChromatiCraft.class, s.texture);
				Tessellator v5 = Tessellator.instance;
				v5.startDrawingQuads();
				v5.addVertexWithUV(0, 0, 0, 0, 0);
				v5.addVertexWithUV(1, 0, 0, 1, 0);
				v5.addVertexWithUV(1, 1, 0, 1, 1);
				v5.addVertexWithUV(0, 1, 0, 0, 1);
				v5.draw();
			}
		}
		 */

		@Override
		public void registerLogic(IAgeController age, long seed) {

		}

		@Override
		public int instabilityModifier(int count) {
			return count*instability;
		}

		@Override
		public String identifier() {
			return "chrc_"+this.name().toLowerCase();
		}

		@Override
		public String displayName() {
			return name;
		}

		@Override
		public String[] getPoem() {
			ArrayList<String> li = new ArrayList();
			for (Symbols s : icons) {
				li.add(s.getID());
			}
			return li.toArray(new String[li.size()]);
		}

	}

	public static enum Symbols {
		BASE("basic"),
		STRUCTURE("structure"),
		ENERGY("energy"),
		CIVILIZATION("civilization"),
		MATERIAL("material"),
		MINERAL("mineral"),
		HERBAL("herbal"),
		UPGRADE("upgrade");

		//private final String texture;

		private final DrawableWord word;

		private static final Symbols[] list = values();

		private Symbols(String s) {
			//texture = "Textures/MystPage/"+s+".png";
			String texture = "Reika/ChromatiCraft/Textures/mystpages.png";

			word = new DrawableWord().setImageSource(DirectResourceManager.getResource(texture));
			word.addDrawComponent(this.ordinal(), 0);
		}

		public String getID() {
			return "chrc_"+this.name().toLowerCase();
		}
	}
}
