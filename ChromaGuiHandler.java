/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Auxiliary.Interfaces.CastingAutomationBlock;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ItemOnRightClick;
import Reika.ChromatiCraft.Base.ChromaBookGui;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Block.BlockEnderTNT.TileEntityEnderTNT;
import Reika.ChromatiCraft.Block.BlockHeatLamp.TileEntityHeatLamp;
import Reika.ChromatiCraft.Block.BlockRouterNode.RouterFilter;
import Reika.ChromatiCraft.Block.Decoration.BlockRangedLamp.TileEntityRangedLamp;
import Reika.ChromatiCraft.Block.Dimension.Structure.BlockStructureDataStorage.TileEntityStructurePassword;
import Reika.ChromatiCraft.Block.Relay.BlockRelayFilter.TileEntityRelayFilter;
import Reika.ChromatiCraft.Container.ContainerAuraPouch;
import Reika.ChromatiCraft.Container.ContainerAutoEnchanter;
import Reika.ChromatiCraft.Container.ContainerBookEmpties;
import Reika.ChromatiCraft.Container.ContainerBookPages;
import Reika.ChromatiCraft.Container.ContainerBulkMover;
import Reika.ChromatiCraft.Container.ContainerCastingAuto;
import Reika.ChromatiCraft.Container.ContainerCastingTable;
import Reika.ChromatiCraft.Container.ContainerCrystalBrewer;
import Reika.ChromatiCraft.Container.ContainerCrystalCharger;
import Reika.ChromatiCraft.Container.ContainerCrystalFurnace;
import Reika.ChromatiCraft.Container.ContainerCrystalTank;
import Reika.ChromatiCraft.Container.ContainerEnchantDecomposer;
import Reika.ChromatiCraft.Container.ContainerFluidRelay;
import Reika.ChromatiCraft.Container.ContainerInventoryTicker;
import Reika.ChromatiCraft.Container.ContainerItemBurner;
import Reika.ChromatiCraft.Container.ContainerItemCollector;
import Reika.ChromatiCraft.Container.ContainerItemFabricator;
import Reika.ChromatiCraft.Container.ContainerItemInserter;
import Reika.ChromatiCraft.Container.ContainerItemWithFilter;
import Reika.ChromatiCraft.Container.ContainerMiner;
import Reika.ChromatiCraft.Container.ContainerNetworkItemTransporter;
import Reika.ChromatiCraft.Container.ContainerRouterFilter;
import Reika.ChromatiCraft.Container.ContainerSpawnerProgrammer;
import Reika.ChromatiCraft.Container.ContainerStructurePassword;
import Reika.ChromatiCraft.Container.ContainerTelePump;
import Reika.ChromatiCraft.GUI.GuiAbilitySelect;
import Reika.ChromatiCraft.GUI.GuiAuraPouch;
import Reika.ChromatiCraft.GUI.GuiBulkMover;
import Reika.ChromatiCraft.GUI.GuiEnderBucket;
import Reika.ChromatiCraft.GUI.GuiFlightWand;
import Reika.ChromatiCraft.GUI.GuiItemBurner;
import Reika.ChromatiCraft.GUI.GuiItemWithFilter;
import Reika.ChromatiCraft.GUI.GuiLoreKeyAssembly;
import Reika.ChromatiCraft.GUI.GuiOneSlot;
import Reika.ChromatiCraft.GUI.GuiTeleportAbility;
import Reika.ChromatiCraft.GUI.GuiTransitionWand;
import Reika.ChromatiCraft.GUI.Book.GuiAbilityDesc;
import Reika.ChromatiCraft.GUI.Book.GuiBasicInfo;
import Reika.ChromatiCraft.GUI.Book.GuiBookEmpties;
import Reika.ChromatiCraft.GUI.Book.GuiBookPages;
import Reika.ChromatiCraft.GUI.Book.GuiCastingRecipe;
import Reika.ChromatiCraft.GUI.Book.GuiCraftableDesc;
import Reika.ChromatiCraft.GUI.Book.GuiCraftingRecipe;
import Reika.ChromatiCraft.GUI.Book.GuiFragmentRecovery;
import Reika.ChromatiCraft.GUI.Book.GuiMachineDescription;
import Reika.ChromatiCraft.GUI.Book.GuiNavigation;
import Reika.ChromatiCraft.GUI.Book.GuiNotes;
import Reika.ChromatiCraft.GUI.Book.GuiPackChanges;
import Reika.ChromatiCraft.GUI.Book.GuiPoolRecipe;
import Reika.ChromatiCraft.GUI.Book.GuiProgressByLevel;
import Reika.ChromatiCraft.GUI.Book.GuiProgressTree;
import Reika.ChromatiCraft.GUI.Book.GuiRitual;
import Reika.ChromatiCraft.GUI.Book.GuiStructure;
import Reika.ChromatiCraft.GUI.Book.GuiToolDescription;
import Reika.ChromatiCraft.GUI.Tile.GuiBiomeChanger;
import Reika.ChromatiCraft.GUI.Tile.GuiCastingAuto;
import Reika.ChromatiCraft.GUI.Tile.GuiCrystalMusic;
import Reika.ChromatiCraft.GUI.Tile.GuiCrystalTank;
import Reika.ChromatiCraft.GUI.Tile.GuiEnderTNT;
import Reika.ChromatiCraft.GUI.Tile.GuiFluidRelay;
import Reika.ChromatiCraft.GUI.Tile.GuiHeatLamp;
import Reika.ChromatiCraft.GUI.Tile.GuiLampController;
import Reika.ChromatiCraft.GUI.Tile.GuiLumenAlveary;
import Reika.ChromatiCraft.GUI.Tile.GuiParticleSpawner;
import Reika.ChromatiCraft.GUI.Tile.GuiRangedLamp;
import Reika.ChromatiCraft.GUI.Tile.GuiRelayFilter;
import Reika.ChromatiCraft.GUI.Tile.GuiRitualTable;
import Reika.ChromatiCraft.GUI.Tile.GuiRouterFilter;
import Reika.ChromatiCraft.GUI.Tile.GuiTeleportGate;
import Reika.ChromatiCraft.GUI.Tile.Inventory.GuiAutoEnchanter;
import Reika.ChromatiCraft.GUI.Tile.Inventory.GuiCastingTable;
import Reika.ChromatiCraft.GUI.Tile.Inventory.GuiCrystalBrewer;
import Reika.ChromatiCraft.GUI.Tile.Inventory.GuiCrystalCharger;
import Reika.ChromatiCraft.GUI.Tile.Inventory.GuiCrystalFurnace;
import Reika.ChromatiCraft.GUI.Tile.Inventory.GuiEnchantDecomposer;
import Reika.ChromatiCraft.GUI.Tile.Inventory.GuiInventoryTicker;
import Reika.ChromatiCraft.GUI.Tile.Inventory.GuiItemCollector;
import Reika.ChromatiCraft.GUI.Tile.Inventory.GuiItemFabricator;
import Reika.ChromatiCraft.GUI.Tile.Inventory.GuiItemInserter;
import Reika.ChromatiCraft.GUI.Tile.Inventory.GuiMiner;
import Reika.ChromatiCraft.GUI.Tile.Inventory.GuiNetworkItemTransporter;
import Reika.ChromatiCraft.GUI.Tile.Inventory.GuiSpawnerProgrammer;
import Reika.ChromatiCraft.GUI.Tile.Inventory.GuiStructurePassword;
import Reika.ChromatiCraft.GUI.Tile.Inventory.GuiTelePump;
import Reika.ChromatiCraft.ModInterface.AE.ContainerMEDistributor;
import Reika.ChromatiCraft.ModInterface.AE.ContainerPatternCache;
import Reika.ChromatiCraft.ModInterface.AE.ContainerRemoteTerminal;
import Reika.ChromatiCraft.ModInterface.AE.GuiMEDistributor;
import Reika.ChromatiCraft.ModInterface.AE.GuiPatternCache;
import Reika.ChromatiCraft.ModInterface.AE.GuiRemoteTerminal;
import Reika.ChromatiCraft.ModInterface.AE.TileEntityMEDistributor;
import Reika.ChromatiCraft.ModInterface.AE.TileEntityPatternCache;
import Reika.ChromatiCraft.ModInterface.Bees.TileEntityLumenAlveary;
import Reika.ChromatiCraft.ModInterface.ThaumCraft.ContainerFluxMaker;
import Reika.ChromatiCraft.ModInterface.ThaumCraft.GuiAspectFormer;
import Reika.ChromatiCraft.ModInterface.ThaumCraft.GuiFluxMaker;
import Reika.ChromatiCraft.ModInterface.ThaumCraft.TileEntityAspectFormer;
import Reika.ChromatiCraft.ModInterface.ThaumCraft.TileEntityFluxMaker;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.TileEntity.TileEntityBiomePainter;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityItemCollector;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityItemInserter;
import Reika.ChromatiCraft.TileEntity.AOE.TileEntityLampController;
import Reika.ChromatiCraft.TileEntity.Acquisition.TileEntityCollector;
import Reika.ChromatiCraft.TileEntity.Acquisition.TileEntityItemFabricator;
import Reika.ChromatiCraft.TileEntity.Acquisition.TileEntityMiner;
import Reika.ChromatiCraft.TileEntity.Acquisition.TileEntityTeleportationPump;
import Reika.ChromatiCraft.TileEntity.Auxiliary.TileEntityCrystalCharger;
import Reika.ChromatiCraft.TileEntity.Decoration.TileEntityCrystalMusic;
import Reika.ChromatiCraft.TileEntity.Decoration.TileEntityParticleSpawner;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityAutoEnchanter;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityCrystalFurnace;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityEnchantDecomposer;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntityInventoryTicker;
import Reika.ChromatiCraft.TileEntity.Processing.TileEntitySpawnerReprogrammer;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCrystalBrewer;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityRitualTable;
import Reika.ChromatiCraft.TileEntity.Storage.TileEntityCrystalTank;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityFluidRelay;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityNetworkItemTransporter;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityRift;
import Reika.ChromatiCraft.TileEntity.Transport.TileEntityTeleportGate;
import Reika.DragonAPI.Base.CoreContainer;
import Reika.DragonAPI.Base.OneSlotContainer;
import Reika.DragonAPI.Base.OneSlotMachine;
import Reika.DragonAPI.Interfaces.TileEntity.GuiController;

import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ChromaGuiHandler implements IGuiHandler {

	public static final ChromaGuiHandler instance = new ChromaGuiHandler();

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		ChromaGuis gui = ChromaGuis.guiList[id];
		switch(gui) {
			case ITEMWITHFILTER:
				return new ContainerItemWithFilter(player, world);
			case BOOKPAGES:
				return new ContainerBookPages(player, x);
			case BOOKEMPTIES:
				return new ContainerBookEmpties(player);
			case TILE:
				TileEntity te = world.getTileEntity(x, y, z);

				if (te instanceof TileEntityAutoEnchanter)
					return new ContainerAutoEnchanter(player, (TileEntityAutoEnchanter)te);
				if (te instanceof TileEntityCollector)
					return null;//return new ContainerCollector(player, (TileEntityCollector)te);
				if (te instanceof TileEntitySpawnerReprogrammer)
					return new ContainerSpawnerProgrammer(player, (TileEntitySpawnerReprogrammer)te);
				if (te instanceof TileEntityCrystalBrewer)
					return new ContainerCrystalBrewer(player, (TileEntityCrystalBrewer)te);
				if (te instanceof TileEntityCastingTable)
					return new ContainerCastingTable(player, te);
				if (te instanceof TileEntityCrystalCharger)
					return new ContainerCrystalCharger(player, (TileEntityCrystalCharger)te);
				if (te instanceof TileEntityCrystalFurnace)
					return new ContainerCrystalFurnace(player, (TileEntityCrystalFurnace)te);
				if (te instanceof TileEntityItemCollector)
					return new ContainerItemCollector(player, (TileEntityItemCollector)te);
				if (te instanceof TileEntityItemFabricator)
					return new ContainerItemFabricator(player, (TileEntityItemFabricator)te);
				if (te instanceof TileEntityTeleportationPump)
					return new ContainerTelePump(player, (TileEntityTeleportationPump)te);
				if (te instanceof TileEntityMiner)
					return new ContainerMiner(player, (TileEntityMiner)te);
				if (te instanceof TileEntityCrystalTank)
					return new ContainerCrystalTank(player, (TileEntityCrystalTank)te);
				if (te instanceof TileEntityInventoryTicker)
					return new ContainerInventoryTicker(player, (TileEntityInventoryTicker)te);
				if (te instanceof CastingAutomationBlock)
					return new ContainerCastingAuto((CastingAutomationBlock)te, player);
				if (te instanceof TileEntityMEDistributor)
					return new ContainerMEDistributor(player, (TileEntityMEDistributor)te);
				if (te instanceof TileEntityPatternCache)
					return new ContainerPatternCache(player, (TileEntityPatternCache)te);
				if (te instanceof TileEntityItemInserter)
					return new ContainerItemInserter(player, (TileEntityItemInserter)te);
				if (te instanceof TileEntityEnchantDecomposer)
					return new ContainerEnchantDecomposer(player, (TileEntityEnchantDecomposer)te);
				if (te instanceof TileEntityFluidRelay)
					return new ContainerFluidRelay(player, (TileEntityFluidRelay)te);
				if (te instanceof RouterFilter)
					return new ContainerRouterFilter(player, (RouterFilter)te);
				if (te instanceof TileEntityFluxMaker)
					return new ContainerFluxMaker(player, (TileEntityFluxMaker)te);
				if (te instanceof TileEntityNetworkItemTransporter)
					return new ContainerNetworkItemTransporter(player, (TileEntityNetworkItemTransporter)te);

				if (te instanceof ItemOnRightClick)
					return null;
				if (te instanceof TileEntityRift)
					return null;
				if (te instanceof OneSlotMachine)
					return new OneSlotContainer(player, te);
				if (te instanceof GuiController)
					return new CoreContainer(player, te);

				//if (te instanceof IInventory && !(te instanceof InertIInv))
				//	return new ContainerBasicStorage(player, te);
				break;
			case AURAPOUCH:
				return new ContainerAuraPouch(player);
			case REMOTETERMINAL:
				return new ContainerRemoteTerminal(player);
			case BULKMOVER:
				return new ContainerBulkMover(player);
			case BURNERINV:
				return new ContainerItemBurner(player);
			case STRUCTUREPASS:
				return new ContainerStructurePassword(player, (TileEntityStructurePassword)world.getTileEntity(x, y, z));
			default:
				return null;
		}
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		ChromaGuis gui = ChromaGuis.guiList[id];
		if (ChromaBookGui.lastGui != null && gui.isLexiconGUI() && z == 0) {
			Object ret = ChromaBookGui.lastGui;
			ChromaBookGui.lastGui = null;
			return ret;
		}
		switch(gui) {
			case ITEMWITHFILTER:
				return new GuiItemWithFilter(player, world);
			case LOREKEY:
				return new GuiLoreKeyAssembly(player);
				//case LORE:
				//	return new GuiLore(player);
			case TILE:
				TileEntity te = world.getTileEntity(x, y, z);

				if (te instanceof TileEntityAutoEnchanter)
					return new GuiAutoEnchanter(player, (TileEntityAutoEnchanter)te);
				if (te instanceof TileEntityCollector)
					return null;//return new GuiCollector(player, (TileEntityCollector)te);
				if (te instanceof TileEntitySpawnerReprogrammer)
					return new GuiSpawnerProgrammer(player, (TileEntitySpawnerReprogrammer)te);
				if (te instanceof TileEntityCrystalBrewer)
					return new GuiCrystalBrewer(player, (TileEntityCrystalBrewer)te);
				if (te instanceof TileEntityCastingTable)
					return new GuiCastingTable(player, (TileEntityCastingTable)te);
				if (te instanceof TileEntityCrystalCharger)
					return new GuiCrystalCharger(player, (TileEntityCrystalCharger)te);
				if (te instanceof TileEntityRitualTable)
					return new GuiRitualTable(player, (TileEntityRitualTable)te);
				if (te instanceof TileEntityCrystalFurnace)
					return new GuiCrystalFurnace(player, (TileEntityCrystalFurnace)te);
				if (te instanceof TileEntityItemCollector)
					return new GuiItemCollector(player, (TileEntityItemCollector)te);
				if (te instanceof TileEntityItemFabricator)
					return new GuiItemFabricator(player, (TileEntityItemFabricator)te);
				if (te instanceof TileEntityTeleportationPump)
					return new GuiTelePump(player, (TileEntityTeleportationPump)te);
				if (te instanceof TileEntityAspectFormer)
					return new GuiAspectFormer(player, (TileEntityAspectFormer)te);
				if (te instanceof TileEntityMiner)
					return new GuiMiner(player, (TileEntityMiner)te);
				if (te instanceof TileEntityLampController)
					return new GuiLampController(player, (TileEntityLampController)te);
				if (te instanceof TileEntityRangedLamp)
					return new GuiRangedLamp(player, (TileEntityRangedLamp)te);
				if (te instanceof TileEntityCrystalTank)
					return new GuiCrystalTank(player, (TileEntityCrystalTank)te);
				if (te instanceof TileEntityEnderTNT)
					return new GuiEnderTNT(player, (TileEntityEnderTNT)te);
				if (te instanceof TileEntityInventoryTicker)
					return new GuiInventoryTicker(player, (TileEntityInventoryTicker)te);
				if (te instanceof TileEntityBiomePainter)
					return new GuiBiomeChanger(player, (TileEntityBiomePainter)te);
				if (te instanceof TileEntityHeatLamp)
					return new GuiHeatLamp((TileEntityHeatLamp)te, player);
				if (te instanceof CastingAutomationBlock)
					return new GuiCastingAuto((CastingAutomationBlock)te, player);
				if (te instanceof TileEntityMEDistributor)
					return new GuiMEDistributor(player, (TileEntityMEDistributor)te);
				if (te instanceof TileEntityCrystalMusic)
					return new GuiCrystalMusic(player, (TileEntityCrystalMusic)te);
				if (te instanceof TileEntityPatternCache)
					return new GuiPatternCache(player, (TileEntityPatternCache)te);
				if (te instanceof TileEntityItemInserter)
					return new GuiItemInserter(player, (TileEntityItemInserter)te);
				if (te instanceof TileEntityEnchantDecomposer)
					return new GuiEnchantDecomposer(player, (TileEntityEnchantDecomposer)te);
				if (te instanceof TileEntityParticleSpawner)
					return new GuiParticleSpawner(player, (TileEntityParticleSpawner)te);
				if (te instanceof TileEntityTeleportGate)
					return new GuiTeleportGate(player, (TileEntityTeleportGate)te);
				if (te instanceof TileEntityFluidRelay)
					return new GuiFluidRelay(player, (TileEntityFluidRelay)te);
				if (te instanceof TileEntityRelayFilter)
					return new GuiRelayFilter(player, (TileEntityRelayFilter)te);
				if (te instanceof RouterFilter)
					return new GuiRouterFilter(player, (RouterFilter)te);
				if (te instanceof TileEntityFluxMaker)
					return new GuiFluxMaker(player, (TileEntityFluxMaker)te);
				if (te instanceof TileEntityLumenAlveary)
					return new GuiLumenAlveary(player, (TileEntityLumenAlveary)te);
				if (te instanceof TileEntityNetworkItemTransporter)
					return new GuiNetworkItemTransporter(player, (TileEntityNetworkItemTransporter)te);

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
				return new GuiBookPages(player, x);
			case BOOKEMPTIES:
				return new GuiBookEmpties(player);
			case MACHINEDESC:
				return new GuiMachineDescription(player, ChromaResearch.researchList[x]);
			case TOOLDESC:
				return new GuiToolDescription(player, ChromaResearch.researchList[x]);
			case BASICDESC:
				return new GuiCraftableDesc(player, ChromaResearch.researchList[x]);
			case CRAFTING:
				return new GuiCraftingRecipe(player, ChromaResearch.researchList[x].getVanillaRecipes(), y);
			case RECIPE:
				return new GuiCastingRecipe(player, ChromaResearch.researchList[x].getCraftingRecipes(), y, z > 0);
			case ALLOYING:
				return new GuiPoolRecipe(player, y, z > 0);
			case RITUAL:
				return new GuiRitual(player, ChromaResearch.researchList[x].getAbility());
			case ABILITYDESC:
				return new GuiAbilityDesc(player, ChromaResearch.researchList[x]);
			case INFO:
				ChromaResearch r = ChromaResearch.researchList[x];
				return r == ChromaResearch.PACKCHANGES ? new GuiPackChanges(player) : new GuiBasicInfo(player, r);
			case STRUCTURE:
				return new GuiStructure(player, ChromaResearch.researchList[x]);
			case PROGRESS:
				return new GuiProgressTree(player);
			case PROGRESSBYTIER:
				return new GuiProgressByLevel(player);
			case REFRAGMENT:
				return new GuiFragmentRecovery(player);
			case NOTES:
				return new GuiNotes(player);
			case AURAPOUCH:
				return new GuiAuraPouch(player);
			case TRANSITION:
				return new GuiTransitionWand(player);
			case TELEPORT:
				return new GuiTeleportAbility(player);
			case REMOTETERMINAL:
				return new GuiRemoteTerminal(player);
			case BULKMOVER:
				return new GuiBulkMover(player);
			case HOVER:
				return new GuiFlightWand(player);
			case BURNERINV:
				return new GuiItemBurner(player);
			case STRUCTUREPASS:
				return new GuiStructurePassword(player, (TileEntityStructurePassword)world.getTileEntity(x, y, z));
			case ENDERBUCKET:
				return new GuiEnderBucket(player);
		}
		return null;
	}
}
