/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI.Book;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import Reika.ChromatiCraft.Auxiliary.ChromaStructures.Structures;
import Reika.ChromatiCraft.Base.BlockModelledChromaTile;
import Reika.ChromatiCraft.Base.GuiBookSection;
import Reika.ChromatiCraft.Entity.EntityChromaEnderCrystal;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockKey;
import Reika.DragonAPI.Instantiable.Data.Immutable.Coordinate;
import Reika.DragonAPI.Instantiable.Data.Maps.ItemHashMap;
import Reika.DragonAPI.Instantiable.GUI.CustomSoundGuiButton;
import Reika.DragonAPI.Instantiable.Rendering.StructureRenderer;
import Reika.DragonAPI.Instantiable.Rendering.StructureRenderer.BlockChoiceHook;
import Reika.DragonAPI.Instantiable.Rendering.StructureRenderer.BlockRenderHook;
import Reika.DragonAPI.Instantiable.Rendering.StructureRenderer.EntityRender;
import Reika.DragonAPI.Interfaces.Registry.TreeType;
import Reika.DragonAPI.Libraries.ReikaEntityHelper;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaItemHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaTreeHelper;
import Reika.DragonAPI.ModRegistry.ModWoodList;

public class GuiStructure extends GuiBookSection {

	private int mode = 0;
	private int tick = 0;

	private final FilledBlockArray array;
	private final StructureRenderer render;

	public GuiStructure(EntityPlayer ep, ChromaResearch r) {
		super(ChromaGuis.STRUCTURE, ep, r, 256, 220, false);

		array = page.getStructure().getStructureForDisplay();
		if (page.name().toLowerCase(Locale.ENGLISH).contains("casting")) {
			array.setBlock(array.getMidX(), array.getMinY()+1, array.getMidZ(), ChromaTiles.TABLE.getBlock(), ChromaTiles.TABLE.getBlockMetadata());
			if (page == ChromaResearch.CASTING2 || page == ChromaResearch.CASTING3) {
				for (int i = -4; i <= 4; i += 2) {
					for (int k = -4; k <= 4; k += 2) {
						if (i != 0 || k != 0) {
							int dx = array.getMidX()+i;
							int dz = array.getMidZ()+k;
							int dy = array.getMinY()+1+(Math.abs(i) != 4 && Math.abs(k) != 4 ? 0 : 1);
							array.setBlock(dx, dy, dz, ChromaTiles.STAND.getBlock(), ChromaTiles.STAND.getBlockMetadata());
						}
					}
				}
			}
		}
		if (page == ChromaResearch.TREE || page == ChromaResearch.BOOSTTREE) {
			array.setBlock(array.getMidX()-1, array.getMaxY(), array.getMidZ(), ChromaTiles.POWERTREE.getBlock(), ChromaTiles.POWERTREE.getBlockMetadata());
		}
		if (page == ChromaResearch.INFUSION) {
			array.setBlock(array.getMidX(), array.getMinY()+2, array.getMidZ(), ChromaTiles.INFUSER.getBlock(), ChromaTiles.INFUSER.getBlockMetadata());
		}
		if (page == ChromaResearch.MINIPYLON) {
			array.setBlock(array.getMidX(), array.getMinY()+6, array.getMidZ(), ChromaTiles.PERSONAL.getBlock(), ChromaTiles.PERSONAL.getBlockMetadata());
		}
		if (page == ChromaResearch.PYLON) {
			array.setBlock(array.getMidX(), array.getMinY()+1, array.getMidZ(), Blocks.air);
		}
		if (page == ChromaResearch.PYLON || page == ChromaResearch.PYLONTURBO) {
			array.setBlock(array.getMidX(), array.getMinY()+9, array.getMidZ(), ChromaTiles.PYLON.getBlock(), ChromaTiles.PYLON.getBlockMetadata());
		}
		if (page == ChromaResearch.PYLONBROADCAST) {
			array.setBlock(array.getMidX(), array.getMinY()+10, array.getMidZ(), ChromaTiles.PYLON.getBlock(), ChromaTiles.PYLON.getBlockMetadata());
		}
		if (page == ChromaResearch.CLOAKTOWER) {
			array.setBlock(array.getMidX(), array.getMinY()+5, array.getMidZ(), ChromaTiles.CLOAKING.getBlock(), ChromaTiles.CLOAKING.getBlockMetadata());
		}
		render = new StructureRenderer(array);
		if (page == ChromaResearch.PYLON || page == ChromaResearch.PYLONTURBO) {
			render.addOverride(array.getMidX(), array.getMinY()+9, array.getMidZ(), ChromaTiles.PYLON.getCraftedProduct());
		}
		else if (page == ChromaResearch.PYLONBROADCAST) {
			render.addOverride(array.getMidX(), array.getMinY()+10, array.getMidZ(), ChromaTiles.PYLON.getCraftedProduct());
		}
		else if (page == ChromaResearch.MINIPYLON) {
			render.addOverride(array.getMidX(), array.getMinY()+6, array.getMidZ(), ChromaTiles.PERSONAL.getCraftedProduct());
		}
		else if (page == ChromaResearch.CLOAKTOWER) {
			render.addOverride(array.getMidX(), array.getMinY()+5, array.getMidZ(), ChromaTiles.CLOAKING.getCraftedProduct());
		}
		else if (page == ChromaResearch.TREE || page == ChromaResearch.BOOSTTREE) {
			render.addOverride(array.getMidX()-1, array.getMaxY(), array.getMidZ(), ChromaTiles.POWERTREE.getCraftedProduct());
		}
		else if (page == ChromaResearch.BEACONSTRUCT) {
			render.addOverride(array.getMidX(), array.getMinY()+1, array.getMidZ(), ChromaTiles.BEACON.getCraftedProduct());
		}
		else if (page == ChromaResearch.PROGLINKSTRUCT) {
			render.addOverride(array.getMidX(), array.getMaxY(), array.getMidZ(), ChromaTiles.PROGRESSLINK.getCraftedProduct());
		}
		else if (page.name().contains("METEOR")) {
			ItemStack is = ChromaTiles.METEOR.getCraftedProduct();
			if (page == ChromaResearch.METEOR2) {
				is.stackTagCompound = null;
			}
			if (page == ChromaResearch.METEOR2) {
				is.stackTagCompound = new NBTTagCompound();
				is.stackTagCompound.setInteger("tier", 1);
			}
			if (page == ChromaResearch.METEOR3) {
				is.stackTagCompound = new NBTTagCompound();
				is.stackTagCompound.setInteger("tier", 2);
			}
			render.addOverride(array.getMidX(), array.getMaxY()-2, array.getMidZ(), is);
			render.addOverride(ChromaTiles.METEOR.getCraftedProduct(), is);
		}
		else if (page == ChromaResearch.MINIREPEATER) {
			//render.addOverride(array.getMidX(), array.getMaxY(), array.getMidZ(), ChromaTiles.WEAKREPEATER.getCraftedProduct());
			render.addBlockHook(Blocks.log, new LogRenderHook());
			render.addBlockHook(Blocks.log2, new LogRenderHook());
			for (int i = 0; i < ModWoodList.woodList.length; i++) {
				ModWoodList tree = ModWoodList.woodList[i];
				if (tree.exists()) {
					render.addBlockHook(tree.getBlock(), new LogRenderHook());
				}
			}
		}
		else if (page == ChromaResearch.PORTALSTRUCT) {
			render.addOverride(new ItemStack(Blocks.bedrock), ChromaItems.ENDERCRYSTAL.getStackOfMetadata(1));

			render.addEntityRender(-5, -1, -9, createCrystalRender());
			render.addEntityRender(-9, -1, -5, createCrystalRender());
			render.addEntityRender(5, -1, -9, createCrystalRender());
			render.addEntityRender(9, -1, -5, createCrystalRender());
			render.addEntityRender(-5, -1, 9, createCrystalRender());
			render.addEntityRender(-9, -1, 5, createCrystalRender());
			render.addEntityRender(5, -1, 9, createCrystalRender());
			render.addEntityRender(9, -1, 5, createCrystalRender());
		}

		if (page != ChromaResearch.CAVERN) {
			render.addBlockHook(ChromaBlocks.RUNE.getBlockInstance(), new RuneRenderHook());
		}

		if (page == ChromaResearch.GATESTRUCT) {
			render.addOverride(array.getMidX(), array.getMinY()+1, array.getMidZ(), ChromaTiles.TELEPORT.getCraftedProduct());
		}

		if (page == ChromaResearch.DATATOWER) {
			render.addOverride(array.getMidX(), array.getMaxY(), array.getMidZ(), ChromaTiles.DATANODE.getCraftedProduct());
			render.addOverride(array.getMidX(), array.getMaxY()-1, array.getMidZ(), ChromaTiles.DATANODE.getCraftedProduct());
			render.addOverride(array.getMidX(), array.getMaxY()-2, array.getMidZ(), ChromaTiles.DATANODE.getCraftedProduct());
			render.addOverride(array.getMidX(), array.getMaxY()-3, array.getMidZ(), ChromaTiles.DATANODE.getCraftedProduct());
			render.addOverride(array.getMidX(), array.getMaxY()-4, array.getMidZ(), ChromaTiles.DATANODE.getCraftedProduct());
		}

		if (page == ChromaResearch.CASTING2 || page == ChromaResearch.CASTING3) {
			for (int i = -4; i <= 4; i += 2) {
				for (int k = -4; k <= 4; k += 2) {
					if (i != 0 || k != 0) {
						int dx = array.getMidX()+i;
						int dz = array.getMidZ()+k;
						int dy = array.getMinY()+1+(Math.abs(i) != 4 && Math.abs(k) != 4 ? 0 : 1);
						render.addOverride(dx, dy, dz, ChromaTiles.STAND.getCraftedProduct());
					}
				}
			}
			render.addOverride(new ItemStack(ChromaTiles.STAND.getBlock(), ChromaTiles.STAND.getBlockMetadata()), ChromaTiles.STAND.getCraftedProduct());
		}

		render.addRenderHook(ChromaTiles.PYLON.getCraftedProduct(), new PylonRenderHook());
	}

	private static EntityRender createCrystalRender() {
		return new EntityRender(new EntityChromaEnderCrystal(Minecraft.getMinecraft().theWorld), ReikaEntityHelper.getEntityRenderer(EntityEnderCrystal.class));
	}

	@Override
	public void initGui() {
		super.initGui();
		render.resetRotation();
		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		buttonList.add(new CustomSoundGuiButton(0, j+185, k-2, 20, 20, "3D", this));
		buttonList.add(new CustomSoundGuiButton(1, j+205, k-2, 20, 20, "2D", this));
		buttonList.add(new CustomSoundGuiButton(4, mode == 1 ? j+125 : j+165, k-2, 20, 20, "N#", this));


		if (mode == 1) {
			buttonList.add(new CustomSoundGuiButton(2, j+165, k-2, 20, 20, "+", this));
			buttonList.add(new CustomSoundGuiButton(3, j+145, k-2, 20, 20, "-", this));
		}
	}

	@Override
	protected void actionPerformed(GuiButton b) {
		super.actionPerformed(b);

		if (b.id == 0) {
			mode = 0;
			render.reset();
			this.initGui();
		}
		else if (b.id == 1) {
			mode = 1;
			this.initGui();
		}
		else if (b.id == 2) {
			render.incrementStepY();
			this.initGui();
		}
		else if (b.id == 3) {
			render.decrementStepY();
			this.initGui();
		}
		else if (b.id == 4 ) {
			mode = 2;
			this.initGui();
		}
	}

	@Override
	protected int getMaxSubpage() {
		return 0;
	}

	@Override
	protected PageType getGuiLayout() {
		return PageType.STRUCT;
	}

	@Override
	public final void drawScreen(int mx, int my, float f) {
		super.drawScreen(mx, my, f);

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;

		String s = "("+array.getSizeX()+"x"+array.getSizeY()+"x"+array.getSizeZ()+")";
		fontRendererObj.drawString(s, j+6, k+10, 0xffffff);

		tick++;

		//FilledBlockArray arr = page.getStructure().getStructureForDisplay();

		switch(mode) {
			case 0:
				this.draw3d(j, k, f);
				break;
			case 1:
				this.drawSlice(j, k);
				break;
			case 2:
				this.drawTally(j, k);
				break;
		}
	}

	private void drawSlice(int j, int k) {
		render.drawSlice(j, k);
	}

	private void drawTally(int j, int k) {
		ItemHashMap<Integer> map = array.tally();
		ItemHashMap<Integer> map2 = null;
		Structures diff = this.getDifferenceTally();
		if (diff != null) {
			map2 = ItemHashMap.subtract(map, diff.getStructureForDisplay().tally());
		}
		int i = 0;
		int n = 8;
		List<ItemStack> c = new ArrayList(map.keySet());
		Collections.sort(c, ReikaItemHelper.comparator);
		for (ItemStack is : c) {
			int dx = j+10+(i/n)*(map2 != null ? 65 : 50);
			int dy = k+30+(i%n)*22;
			ItemStack is2 = is.copy();
			if (ChromaBlocks.CHROMA.match(is)) {
				is2 = ChromaItems.BUCKET.getStackOfMetadata(0);
			}
			else if (ChromaBlocks.ENDER.match(is) || Block.getBlockFromItem(is.getItem()) == FluidRegistry.getFluid("ender").getBlock()) {
				is2 = ChromaItems.BUCKET.getStackOfMetadata(1);
			}
			else if (ChromaBlocks.LUMA.match(is)) {
				is2 = ChromaItems.BUCKET.getStackOfMetadata(3);
			}
			else if (ChromaBlocks.RUNE.match(is)) {
				is2 = ChromaBlocks.RUNE.getStackOfMetadata(getElementByTick());
			}
			else if (page == ChromaResearch.PORTALSTRUCT && Block.getBlockFromItem(is.getItem()) == Blocks.bedrock) {
				is2 = ChromaItems.ENDERCRYSTAL.getStackOfMetadata(1);
			}
			else if ((page == ChromaResearch.TREE || page == ChromaResearch.BOOSTTREE) && Block.getBlockFromItem(is.getItem()) == ChromaBlocks.PYLON.getBlockInstance()) {
				is2 = ChromaTiles.POWERTREE.getCraftedProduct();
			}
			else if (page == ChromaResearch.CLOAKTOWER && Block.getBlockFromItem(is.getItem()) == ChromaBlocks.TILEMODELLED2.getBlockInstance()) {
				is2 = ChromaTiles.CLOAKING.getCraftedProduct();
			}
			else if (page.name().contains("METEOR") && Block.getBlockFromItem(is.getItem()) == ChromaTiles.METEOR.getBlock()) {
				is2 = ChromaTiles.METEOR.getCraftedProduct();
				if (page == ChromaResearch.METEOR1) {
					is2.stackTagCompound = new NBTTagCompound();
					is2.stackTagCompound.setInteger("tier", 0);
				}
				else if (page == ChromaResearch.METEOR2) {
					is2.stackTagCompound = new NBTTagCompound();
					is2.stackTagCompound.setInteger("tier", 1);
				}
				else if (page == ChromaResearch.METEOR3) {
					is2.stackTagCompound = new NBTTagCompound();
					is2.stackTagCompound.setInteger("tier", 2);
				}
			}
			else if (page.name().toLowerCase(Locale.ENGLISH).contains("casting")) {
				if (ChromaBlocks.getEntryByID(ChromaTiles.STAND.getBlock()).match(is2) && is2.getItemDamage() == ChromaTiles.STAND.getBlockMetadata()) {
					is2 = ChromaTiles.STAND.getCraftedProduct();
				}
			}
			if (ChromaBlocks.PYLON.match(is2)) {
				is2 = ChromaTiles.getTileFromIDandMetadata(Block.getBlockFromItem(is2.getItem()), is2.getItemDamage()).getCraftedProduct();
			}
			if (Block.getBlockFromItem(is2.getItem()) instanceof BlockModelledChromaTile) {
				is2 = ChromaTiles.getTileFromIDandMetadata(Block.getBlockFromItem(is2.getItem()), is2.getItemDamage()).getCraftedProduct();
			}
			api.drawItemStackWithTooltip(itemRender, fontRendererObj, is2, dx, dy);
			String s = String.valueOf(map.get(is));
			fontRendererObj.drawString(s, dx+20, dy+5, 0xffffff);
			if (map2 != null) {
				int dx2 = dx+20+fontRendererObj.getStringWidth(s)+fontRendererObj.getCharWidth(' ');
				Integer get = map2.get(is);
				if (get == null)
					get = 0;
				s = "("+get+")";
				fontRendererObj.drawString(s, dx2, dy+5, 0x22ff22);
			}
			i++;
		}
	}

	private Structures getDifferenceTally() {
		switch(page.getStructure()) {
			case CASTING2:
				return Structures.CASTING1;
			case CASTING3:
				return Structures.CASTING2;
			case METEOR2:
				return Structures.METEOR1;
			case METEOR3:
				return Structures.METEOR2;
			case PYLONBROADCAST:
			case PYLONTURBO:
				return Structures.PYLON;
			case RITUAL2:
				return Structures.RITUAL;
			case TREE_BOOSTED:
				return Structures.TREE;
			case WIRELESSPEDESTAL2:
				return Structures.WIRELESSPEDESTAL;
			default:
				return null;
		}
	}

	private void draw3d(int j, int k, float ptick) {
		if (Mouse.isButtonDown(0) && tick > 2) {
			render.rotate(0.25*Mouse.getDY(), 0.25*Mouse.getDX(), 0);
		}
		else if (Mouse.isButtonDown(1)) {
			render.resetRotation();
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			render.rotate(0, 0.75, 0);
		}
		else if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			render.rotate(0, -0.75, 0);
		}
		else if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			render.rotate(-0.75, 0, 0);
		}
		else if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			render.rotate(0.75, 0, 0);
		}

		render.draw3D(j, k, ptick, true);
	}

	private static int getElementByTick() {
		return (int)((System.currentTimeMillis()/4000)%16);
	}

	private static class PylonRenderHook implements BlockRenderHook {

		@Override
		public double getScale() {
			return 2;
		}

		@Override
		public int getOffsetX() {
			return -4;
		}

		@Override
		public int getOffsetY() {
			return -6;
		}

	}

	private static class RuneRenderHook implements BlockChoiceHook {

		@Override
		public ItemStack getBlock(Coordinate pos, int meta) {
			return new BlockKey(ChromaBlocks.RUNE.getBlockInstance(), getElementByTick()).asItemStack();
		}

	}

	private static class LogRenderHook implements BlockChoiceHook {

		@Override
		public ItemStack getBlock(Coordinate pos, int meta) {
			ArrayList<TreeType> li = ReikaJavaLibrary.makeListFromArray(ReikaTreeHelper.treeList);
			for (int i = 0; i < ModWoodList.woodList.length; i++) {
				ModWoodList tree = ModWoodList.woodList[i];
				if (tree.exists()) {
					li.add(tree);
				}
			}
			int tick = (int)((System.currentTimeMillis()/1000)%li.size());
			return li.get(tick).getItem();
		}

	}

}
