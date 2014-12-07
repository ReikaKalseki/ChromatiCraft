/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ItemOnRightClick;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Block.BlockEnderTNT.TileEntityEnderTNT;
import Reika.ChromatiCraft.Block.BlockRangeLamp.TileEntityRangedLamp;
import Reika.ChromatiCraft.Container.ContainerAutoEnchanter;
import Reika.ChromatiCraft.Container.ContainerBookPages;
import Reika.ChromatiCraft.Container.ContainerCastingTable;
import Reika.ChromatiCraft.Container.ContainerCrystalBrewer;
import Reika.ChromatiCraft.Container.ContainerCrystalCharger;
import Reika.ChromatiCraft.Container.ContainerCrystalFurnace;
import Reika.ChromatiCraft.Container.ContainerCrystalTank;
import Reika.ChromatiCraft.Container.ContainerInventoryLinker;
import Reika.ChromatiCraft.Container.ContainerItemCollector;
import Reika.ChromatiCraft.Container.ContainerItemFabricator;
import Reika.ChromatiCraft.Container.ContainerMiner;
import Reika.ChromatiCraft.Container.ContainerSpawnerProgrammer;
import Reika.ChromatiCraft.Container.ContainerTelePump;
import Reika.ChromatiCraft.GUI.GuiAbilitySelect;
import Reika.ChromatiCraft.GUI.GuiAspectFormer;
import Reika.ChromatiCraft.GUI.GuiAutoEnchanter;
import Reika.ChromatiCraft.GUI.GuiCastingTable;
import Reika.ChromatiCraft.GUI.GuiCrystalBrewer;
import Reika.ChromatiCraft.GUI.GuiCrystalCharger;
import Reika.ChromatiCraft.GUI.GuiCrystalFurnace;
import Reika.ChromatiCraft.GUI.GuiCrystalTank;
import Reika.ChromatiCraft.GUI.GuiEnderTNT;
import Reika.ChromatiCraft.GUI.GuiInventoryLinker;
import Reika.ChromatiCraft.GUI.GuiItemCollector;
import Reika.ChromatiCraft.GUI.GuiItemFabricator;
import Reika.ChromatiCraft.GUI.GuiLampController;
import Reika.ChromatiCraft.GUI.GuiMiner;
import Reika.ChromatiCraft.GUI.GuiOneSlot;
import Reika.ChromatiCraft.GUI.GuiRangedLamp;
import Reika.ChromatiCraft.GUI.GuiRitualTable;
import Reika.ChromatiCraft.GUI.GuiSpawnerProgrammer;
import Reika.ChromatiCraft.GUI.GuiTelePump;
import Reika.ChromatiCraft.GUI.Book.GuiAbilityDesc;
import Reika.ChromatiCraft.GUI.Book.GuiBookPages;
import Reika.ChromatiCraft.GUI.Book.GuiCastingRecipe;
import Reika.ChromatiCraft.GUI.Book.GuiChromaInfo;
import Reika.ChromatiCraft.GUI.Book.GuiMachineDescription;
import Reika.ChromatiCraft.GUI.Book.GuiNavigation;
import Reika.ChromatiCraft.GUI.Book.GuiResourceItem;
import Reika.ChromatiCraft.GUI.Book.GuiRitual;
import Reika.ChromatiCraft.GUI.Book.GuiStructure;
import Reika.ChromatiCraft.GUI.Book.GuiToolDescription;
import Reika.ChromatiCraft.ModInterface.TileEntityAspectFormer;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalCharger;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalTank;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityItemCollector;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityLampController;
import Reika.ChromatiCraft.TileEntity.Acquisition.TileEntityCollector;
import Reika.ChromatiCraft.TileEntity.Acquisition.TileEntityItemFabricator;
import Reika.ChromatiCraft.TileEntity.Acquisition.TileEntityMiner;
import Reika.ChromatiCraft.TileEntity.Acquisition.TileEntityTeleportationPump;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityAutoEnchanter;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityCrystalFurnace;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntitySpawnerReprogrammer;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCrystalBrewer;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityRitualTable;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityRift;
import Reika.DragonAPI.Base.ContainerBasicStorage;
import Reika.DragonAPI.Base.CoreContainer;
import Reika.DragonAPI.Base.OneSlotContainer;
import Reika.DragonAPI.Base.OneSlotMachine;
import Reika.DragonAPI.Interfaces.GuiController;
import Reika.DragonAPI.Interfaces.InertIInv;
import cpw.mods.fml.common.network.IGuiHandler;

public class ChromaGuiHandler implements IGuiHandler {

	public static final ChromaGuiHandler instance = new ChromaGuiHandler();
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		ChromaGuis gui = ChromaGuis.guiList[id];
		switch(gui) {
		case LINK:
			return new ContainerInventoryLinker(player, world);
		case BOOKPAGES:
			return new ContainerBookPages(player);
		case TILE:
			TileEntity te = world.getTileEntity(x, y, z);

			if (te instanceof TileEntityAutoEnchanter)
				return new ContainerAutoEnchanter(player, (TileEntityAutoEnchanter)te);
			if (te instanceof TileEntityCollector)
				return null;//return new ContainerCollector(player, (TileEntityCollector)te);
			if (te instanceof TileEntitySpawnerReprogrammer)
				return new ContainerSpawnerProgrammer(player, (TileEntitySpawnerReprogrammer)te);
			if (te instanceof TileEntityCrystalBrewer)
				return new ContainerCrystalBrewer(player, (TileEntityCrystalBrewer) te);
			if (te instanceof TileEntityCastingTable)
				return new ContainerCastingTable(player, te);
			if (te instanceof TileEntityCrystalCharger)
				return new ContainerCrystalCharger(player, (TileEntityCrystalCharger) te);
			if (te instanceof TileEntityCrystalFurnace)
				return new ContainerCrystalFurnace(player, (TileEntityCrystalFurnace) te);
			if (te instanceof TileEntityItemCollector)
				return new ContainerItemCollector(player, (TileEntityItemCollector) te);
			if (te instanceof TileEntityItemFabricator)
				return new ContainerItemFabricator(player, (TileEntityItemFabricator) te);
			if (te instanceof TileEntityTeleportationPump)
				return new ContainerTelePump(player, (TileEntityTeleportationPump) te);
			if (te instanceof TileEntityMiner)
				return new ContainerMiner(player, (TileEntityMiner) te);
			if (te instanceof TileEntityCrystalTank)
				return new ContainerCrystalTank(player, (TileEntityCrystalTank) te);

			if (te instanceof ItemOnRightClick)
				return null;
			if (te instanceof TileEntityRift)
				return null;
			if (te instanceof OneSlotMachine)
				return new OneSlotContainer(player, te);
			if (te instanceof GuiController)
				return new CoreContainer(player, te);
			if (te instanceof IInventory && !(te instanceof InertIInv))
				return new ContainerBasicStorage(player, te);
			break;
		default:
			break;
		}
		return null;
	}

	//returns an instance of the Gui you made earlier
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		ChromaGuis gui = ChromaGuis.guiList[id];
		switch(gui) {
		case LINK:
			return new GuiInventoryLinker(player, world);
		case TILE:
			TileEntity te = world.getTileEntity(x, y, z);

			if (te instanceof TileEntityAutoEnchanter)
				return new GuiAutoEnchanter(player, (TileEntityAutoEnchanter)te);
			if (te instanceof TileEntityCollector)
				return null;//return new GuiCollector(player, (TileEntityCollector)te);
			if (te instanceof TileEntitySpawnerReprogrammer)
				return new GuiSpawnerProgrammer(player, (TileEntitySpawnerReprogrammer)te);
			if (te instanceof TileEntityCrystalBrewer)
				return new GuiCrystalBrewer(player, (TileEntityCrystalBrewer) te);
			if (te instanceof TileEntityCastingTable)
				return new GuiCastingTable(player, (TileEntityCastingTable) te);
			if (te instanceof TileEntityCrystalCharger)
				return new GuiCrystalCharger(player, (TileEntityCrystalCharger) te);
			if (te instanceof TileEntityRitualTable)
				return new GuiRitualTable(player, (TileEntityRitualTable) te);
			if (te instanceof TileEntityCrystalFurnace)
				return new GuiCrystalFurnace(player, (TileEntityCrystalFurnace) te);
			if (te instanceof TileEntityItemCollector)
				return new GuiItemCollector(player, (TileEntityItemCollector) te);
			if (te instanceof TileEntityItemFabricator)
				return new GuiItemFabricator(player, (TileEntityItemFabricator) te);
			if (te instanceof TileEntityTeleportationPump)
				return new GuiTelePump(player, (TileEntityTeleportationPump) te);
			if (te instanceof TileEntityAspectFormer)
				return new GuiAspectFormer(player, (TileEntityAspectFormer) te);
			if (te instanceof TileEntityMiner)
				return new GuiMiner(player, (TileEntityMiner) te);
			if (te instanceof TileEntityLampController)
				return new GuiLampController(player, (TileEntityLampController) te);
			if (te instanceof TileEntityRangedLamp)
				return new GuiRangedLamp(player, (TileEntityRangedLamp) te);
			if (te instanceof TileEntityCrystalTank)
				return new GuiCrystalTank(player, (TileEntityCrystalTank) te);
			if (te instanceof TileEntityEnderTNT)
				return new GuiEnderTNT(player, (TileEntityEnderTNT) te);

			if (te instanceof OneSlotMachine) {
				return new GuiOneSlot(player, (TileEntityChromaticBase)te);
			}/*
			if (te instanceof IInventory && !(te instanceof InertIInv))
				return new GuiBasicStorage(player, (RotaryCraftTileEntity)te);
			 */
			break;
		case ABILITY:
			return new GuiAbilitySelect(player);
		case BOOKNAV:
			return new GuiNavigation(player);
		case BOOKPAGES:
			return new GuiBookPages(player);
		case MACHINEDESC:
			return new GuiMachineDescription(player, ChromaResearch.researchList[x]);
		case TOOLDESC:
			return new GuiToolDescription(player, ChromaResearch.researchList[x]);
		case RESOURCEDESC:
			return new GuiResourceItem(player, ChromaResearch.researchList[x]);
		case RECIPE:
			return new GuiCastingRecipe(player, ChromaResearch.researchList[x].getCraftingRecipes());
		case RITUAL:
			return new GuiRitual(player, ChromaResearch.researchList[x].getAbility());
		case ABILITYDESC:
			return new GuiAbilityDesc(player, ChromaResearch.researchList[x]);
		case INFO:
			return new GuiChromaInfo(player, ChromaResearch.researchList[x]);
		case STRUCTURE:
			return new GuiStructure(player, ChromaResearch.researchList[x]);
		default:
			break;
		}
		return null;
	}
}
