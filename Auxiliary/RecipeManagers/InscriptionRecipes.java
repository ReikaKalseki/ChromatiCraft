/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.RecipeManagers;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaFX;
import Reika.ChromatiCraft.Base.BlockChromaTile;
import Reika.ChromatiCraft.Base.ItemChromaBasic;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Block.BlockPylonStructure.StoneTypes;
import Reika.ChromatiCraft.Magic.ElementTagCompound;
import Reika.ChromatiCraft.Magic.ItemElementCalculator;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.DragonAPI.Instantiable.Data.Collections.OneWayCollections.OneWayMap;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.IO.CustomRecipeList;
import Reika.DragonAPI.Instantiable.IO.LuaBlock;
import Reika.DragonAPI.Instantiable.Rendering.ColorBlendList;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class InscriptionRecipes {

	public static final InscriptionRecipes instance = new InscriptionRecipes();

	private final OneWayMap<BlockKey, InscriptionRecipe> recipes = new OneWayMap();
	private final OneWayMap<Integer, InscriptionRecipe> recipeIDs = new OneWayMap();

	private InscriptionRecipes() {

		this.addRecipe(ChromaBlocks.PYLONSTRUCT.getBlockInstance(), StoneTypes.SMOOTH.ordinal(), ChromaTiles.PYLONLINK, 100);
	}

	private InscriptionRecipe addRecipe(Block in, ChromaTiles out, int time) {
		return this.addRecipe(new BlockKey(in), out, time);
	}

	private InscriptionRecipe addRecipe(Block in, int meta, ChromaTiles out, int time) {
		return this.addRecipe(new BlockKey(in, meta), out, time);
	}

	private InscriptionRecipe addRecipe(BlockKey in, ChromaTiles out, int time) {
		return this.addRecipe(in, new BlockKey(out.getBlock(), out.getBlockMetadata()), time);
	}

	private InscriptionRecipe addRecipe(Block in, BlockKey out, int time) {
		return this.addRecipe(new BlockKey(in), out, time);
	}

	private InscriptionRecipe addRecipe(BlockKey in, BlockKey out, int time) {
		InscriptionRecipe r = new InscriptionRecipe(in, out, time, recipes.size());
		recipes.put(in, r);
		recipeIDs.put(r.referenceIndex, r);
		return r;
	}

	public InscriptionRecipe getInscriptionRecipe(World world, int x, int y, int z) {
		return recipes.get(BlockKey.getAt(world, x, y, z));
	}

	public InscriptionRecipe getInscriptionRecipeByOutput(BlockKey out) {
		for (InscriptionRecipe p : this.getAllInscriptionRecipes()) {
			if (p.output.equals(out))
				return p;
		}
		return null;
	}

	public void loadCustomInscriptionRecipes() {
		CustomRecipeList crl = new CustomRecipeList(ChromatiCraft.instance, "inscription");
		crl.load();
		for (LuaBlock lb : crl.getEntries()) {
			Exception e = null;
			boolean flag = false;
			try {
				flag = this.addCustomRecipe(lb, crl);
			}
			catch (Exception ex) {
				e = ex;
				flag = false;
			}
			if (flag) {
				ChromatiCraft.logger.log("Loaded custom inscription recipe '"+lb.getString("type")+"'");
			}
			else {
				ChromatiCraft.logger.logError("Could not load custom inscription recipe '"+lb.getString("type")+"'");
				if (e != null)
					e.printStackTrace();
			}
		}
	}

	protected final void verifyOutputItem(ItemStack is) {
		if (is.getItem() instanceof ItemChromaBasic || is.getItem().getClass().getName().startsWith("Reika.ChromatiCraft"))
			throw new IllegalArgumentException("This item is not allowed as an output, as it is a native ChromatiCraft item with its own recipe.");
	}

	private boolean addCustomRecipe(LuaBlock lb, CustomRecipeList crl) throws Exception {
		ItemStack out = crl.parseItemString(lb.getString("output"), null, false);
		this.verifyOutputItem(out);
		ItemStack in = crl.parseItemString(lb.getString("input"), null, false);
		int time = lb.getInt("duration");
		try {
			InscriptionRecipe r = this.addCustomRecipe(new BlockKey(in), new BlockKey(out), time);
		}
		catch (Exception e) {
			throw new IllegalArgumentException("The specified item is not a block!");
		}
		return true;
	}

	public InscriptionRecipe addCustomRecipe(BlockKey in, BlockKey out, int time) {
		InscriptionRecipe r = this.addRecipe(in, out, time);
		r.isCustom = true;
		return r;
	}

	public static class InscriptionRecipe {

		public final BlockKey input;
		public final BlockKey output;
		public final int duration;
		public final int referenceIndex;

		private boolean isCustom = false;

		private InscriptionRecipe(BlockKey in, BlockKey out, int time, int idx) {
			input = in;
			output = out;
			duration = time;
			referenceIndex = idx;
		}

		public ElementTagCompound getInputElements() {
			ElementTagCompound tag = ItemElementCalculator.instance.getValueForItem(input.asItemStack());
			if (duration > 200)
				tag.addValueToColor(CrystalElement.LIGHTBLUE, 2);
			return tag;
		}

		public boolean isCustom() {
			return isCustom;
		}

		@SideOnly(Side.CLIENT)
		public void doFX(World world, int x, int y, int z) {
			ColorBlendList cbl = new ColorBlendList(2, ChromaFX.getChromaColorTiles());
			for (int i = 0; i < 32; i++) {
				double px = ReikaRandomHelper.getRandomPlusMinus(x+0.5, 0.75);
				double py = ReikaRandomHelper.getRandomPlusMinus(y+0.5, 0.75);
				double pz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, 0.75);
				int c = cbl.getRandomBlendedColor(world.rand);
				int l = ReikaRandomHelper.getRandomBetween(10, 30);
				EntityFX fx = new EntityCCBlurFX(world, px, py, pz).setColor(c).setScale(1+world.rand.nextFloat()).setLife(l);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
			}
			ReikaSoundHelper.playClientSound(ChromaSounds.CAST, x+0.5, y+0.5, z+0.5, 1, 1);
		}

		public void place(World world, int x, int y, int z, EntityPlayer ep) {
			output.place(world, x, y, z);
			if (output.blockID instanceof BlockChromaTile) {
				((TileEntityChromaticBase)world.getTileEntity(x, y, z)).setPlacer(ep);
			}
		}
	}

	public Collection<InscriptionRecipe> getAllInscriptionRecipes() {
		return Collections.unmodifiableCollection(recipes.values());
	}

	public Collection<BlockKey> getAllInputs() {
		HashSet<BlockKey> c = new HashSet();
		for (InscriptionRecipe pr : this.getAllInscriptionRecipes()) {
			c.add(pr.input);
		}
		return c;
	}

	public Collection<BlockKey> getAllOutputs() {
		HashSet<BlockKey> c = new HashSet();
		for (InscriptionRecipe pr : this.getAllInscriptionRecipes()) {
			c.add(pr.output);
		}
		return c;
	}

	public InscriptionRecipe getRecipeByID(int id) {
		return recipeIDs.get(id);
	}

}
