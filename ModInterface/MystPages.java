/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.ModInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import com.xcompwiz.mystcraft.api.hook.SymbolValuesAPI;
import com.xcompwiz.mystcraft.api.hook.WordAPI;
import com.xcompwiz.mystcraft.api.symbol.BlockCategory;
import com.xcompwiz.mystcraft.api.symbol.BlockDescriptor;
import com.xcompwiz.mystcraft.api.symbol.IAgeSymbol;
import com.xcompwiz.mystcraft.api.symbol.ModifierUtils;
import com.xcompwiz.mystcraft.api.word.DrawableWord;
import com.xcompwiz.mystcraft.api.world.AgeDirector;

import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.World.Nether.LavaRiverGenerator;
import Reika.DragonAPI.IO.DirectResourceManager;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaMystcraftHelper;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaMystcraftHelper.APISegment;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaMystcraftHelper.MystcraftPageRegistry;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class MystPages implements MystcraftPageRegistry {

	public static final MystPages instance = new MystPages();

	private MystPages() {

	}

	@Override
	public void register() {
		for (int i = 0; i < Symbols.list.length; i++) {
			Symbols p = Symbols.list[i];
			WordAPI api = ReikaMystcraftHelper.getAPI(APISegment.WORD);
			if (api != null)
				api.registerWord(p.getID(), p.word);
		}

		for (int i = 0; i < Pages.list.length; i++) {
			Pages p = Pages.list[i];
			ReikaMystcraftHelper.setPageRank(p, p.itemRank);
			ReikaMystcraftHelper.setRandomAgeWeight(p, p.randomWeight);
			ReikaMystcraftHelper.registerAgeSymbol(p);
			ChromatiCraft.logger.log("Registering custom MystCraft page '"+p.name+"'");
		}

		SymbolValuesAPI api = ReikaMystcraftHelper.getAPI(APISegment.SYMBOLVALUES);
		if (api != null) {
			for (int i = 0; i < Pages.list.length; i++) {
				Pages p = Pages.list[i];
				api.setSymbolIsPurchasable(p, p.purchasable());
			}
		}
	}

	public static enum Pages implements IAgeSymbol {

		PYLONS("Crystal Pylons", 				50, 	4, 0.25F,		Symbols.STRUCTURE, Symbols.ENERGY),
		STRUCTURES("Buried Structures", 		200, 	4, 0.125F,		Symbols.STRUCTURE, Symbols.CIVILIZATION),
		PLANTS("Chroma Plants", 				20, 	3, 0.75F,		Symbols.MATERIAL, Symbols.HERBAL),
		ORES("Chroma Ores", 					10, 	3, 0.75F,		Symbols.MATERIAL, Symbols.MINERAL),
		CRYSTALS("Cave Crystals", 				80, 	2, 0.5F,		Symbols.MATERIAL, Symbols.MINERAL, Symbols.ENERGY),
		TREES("Dye Trees", 						5, 		1, 1F,			Symbols.HERBAL, Symbols.ENERGY),
		DENSE("Dense Generation", 				400, 	6, 0.03125F,	Symbols.UPGRADE),
		LOSSY("Lumen Loss",						-80,	4, 0.0625F,		Symbols.ENERGY, Symbols.CIVILIZATION, Symbols.UPGRADE, Symbols.INVERSION),
		BUFFERDRAIN("Energy Drain",				-150,	4, 0.125F,		Symbols.ENERGY, Symbols.PLAYER, Symbols.UPGRADE, Symbols.INVERSION),
		HOSTILE("Hostile Aura",					-40,	5, 0.03125F,	Symbols.ENERGY, Symbols.PLAYER, Symbols.MINERAL, Symbols.INVERSION),
		CORRUPTED("Corrupted Aura",				-400,	5, 0.03125F,	Symbols.MATERIAL, Symbols.UPGRADE, Symbols.INVERSION),
		LAVARIVER("Sky Rivers",					2,		4, 0.0625F,		Symbols.STRUCTURE, Symbols.MATERIAL, Symbols.MINERAL),
		VIOLENTPYLONS("Excessive Discharge",	-60,	4, 0.0625F,		Symbols.STRUCTURE, Symbols.PLAYER, Symbols.UPGRADE, Symbols.INVERSION),
		UNSTABLEPYLONS("Pylon Destabilization",	-200,	5, 0.015625F,	Symbols.ENERGY, Symbols.UPGRADE, Symbols.INVERSION);

		public final String name;
		public final int instability;

		private final int itemRank;
		private final float randomWeight;

		private final HashMap<Integer, Boolean> dimCache = new HashMap();

		private final ArrayList<Symbols> icons;

		private static final Pages[] list = values();

		private Pages(String s, int ins, int r, float w, Symbols... icons) {
			name = s;
			instability = ins;

			itemRank = r;
			randomWeight = w;

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
		public void registerLogic(AgeDirector age, long seed) {
			switch(this) {
				case LAVARIVER:
					BlockDescriptor b1 = ModifierUtils.popBlockMatching(age, BlockCategory.STRUCTURE);
					BlockDescriptor b2 = ModifierUtils.popBlockMatching(age, BlockCategory.FLUID);
					age.registerInterface(new LavaRiverGenerator(seed, b1 != null ? new BlockKey(b1.block, b1.metadata) : null, b2 != null ? new BlockKey(b2.block, b2.metadata) : null));
					break;
				default:
					break;
			}
		}

		@Override
		public int instabilityModifier(int count) {
			return count == 1 ? instability : 0;
		}

		@Override
		public String identifier() {
			return "chrc_"+this.name().toLowerCase(Locale.ENGLISH);
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

		public boolean purchasable() {
			switch(this) {
				case PLANTS:
				case ORES:
				case TREES:
				case CRYSTALS:
					return true;
				default:
					return false;
			}
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
		UPGRADE("upgrade"),
		PLAYER("player"),
		INVERSION("invert");
		//private final String texture;

		private final DrawableWord word;

		private static final Symbols[] list = values();

		private Symbols(String s) {

			word = new DrawableWord();
			if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
				this.setTex(word);
			}
			word.addDrawComponent(this.ordinal(), 0);
		}

		@SideOnly(Side.CLIENT)
		private void setTex(DrawableWord word) {
			//texture = "Textures/MystPage/"+s+".png";
			String texture = "Reika/ChromatiCraft/Textures/mystpages.png";
			word.setImageSource(DirectResourceManager.getResource(texture));
		}

		public String getID() {
			return "chrc_"+this.name().toLowerCase(Locale.ENGLISH);
		}
	}
}
