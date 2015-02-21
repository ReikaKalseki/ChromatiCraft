package Reika.ChromatiCraft.ModInterface;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.wands.WandCap;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaDescriptions;
import Reika.ChromatiCraft.Auxiliary.ChromaStacks;
import Reika.ChromatiCraft.Block.BlockTieredOre.TieredOres;
import Reika.DragonAPI.ModInteract.DeepInteract.ReikaThaumHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class TieredOreCap extends WandCap {

	private static final HashMap<TieredOres, WandType> items = new HashMap();

	public static void registerAll() {
		for (TieredOres t : items.keySet()) {
			WandCap cap = new TieredOreCap(t);
			cap.setTexture(new ResourceLocation("chromaticraft", "wandcap_"+t.name().toLowerCase()));
		}
	}

	private TieredOreCap(TieredOres t) {
		super("chromawcap_"+t.name().toLowerCase(), 0.8F, items.get(t).aspects, 0.5F, items.get(t).item, 120);
	}

	private static class WandType {

		private final ArrayList<Aspect> aspects = new ArrayList();
		private final ItemStack item;
		private final ItemStack raw;

		private WandType(ItemStack is, ItemStack raw, Aspect... as) {
			item = is;
			this.raw = raw;
			for (Aspect a : as)
				this.addAspect(a);
		}

		private WandType addAspect(Aspect a) {
			aspects.add(a);
			return this;
		}

	}

	static {
		items.put(TieredOres.FIRAXITE, new WandType(ChromaStacks.firaxiteCap, ChromaStacks.firaxite, Aspect.FIRE, Aspect.ORDER, Aspect.MAGIC, Aspect.CRYSTAL));
		items.put(TieredOres.WATERY, new WandType(ChromaStacks.waterCap, ChromaStacks.waterDust, Aspect.WATER, Aspect.ORDER, Aspect.MAGIC, Aspect.CRYSTAL));
		items.put(TieredOres.END, new WandType(ChromaStacks.endCap, ChromaStacks.enderDust, Aspect.ENTROPY, Aspect.ELDRITCH, Aspect.ORDER, Aspect.MAGIC, Aspect.CRYSTAL));
	}

	public static void addRecipes() {
		int i = -2;
		for (TieredOres t : items.keySet()) {
			WandType wt = items.get(t);
			AspectList al = new AspectList();
			al.add(Aspect.ORDER, 20);
			Aspect a = wt.aspects.get(0);
			al.add(a, 50);
			Object[] recipe = {
					"AAA", "A A", 'A', wt.raw
			};
			ShapedArcaneRecipe ir = ThaumcraftApi.addArcaneCraftingRecipe("", wt.item, al, recipe);
			String id = "TIEREDCAP_"+t.name();
			String desc = "Novel caps";
			if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
				ReikaThaumHelper.addArcaneRecipeBookEntryViaXML(id, desc, "chromaticraft", ir, 2, i, ChromatiCraft.class, ChromaDescriptions.getParentPage()+"thaum.xml");
			i++;
		}
	}

}
