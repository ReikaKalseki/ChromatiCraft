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
import Reika.ChromatiCraft.Container.ContainerAutoEnchanter;
import Reika.ChromatiCraft.Container.ContainerCastingTable;
import Reika.ChromatiCraft.Container.ContainerCrystalBrewer;
import Reika.ChromatiCraft.Container.ContainerCrystalCharger;
import Reika.ChromatiCraft.Container.ContainerCrystalFurnace;
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
import Reika.ChromatiCraft.GUI.GuiChromaBook;
import Reika.ChromatiCraft.GUI.GuiCrystalBrewer;
import Reika.ChromatiCraft.GUI.GuiCrystalCharger;
import Reika.ChromatiCraft.GUI.GuiCrystalFurnace;
import Reika.ChromatiCraft.GUI.GuiInventoryLinker;
import Reika.ChromatiCraft.GUI.GuiItemCollector;
import Reika.ChromatiCraft.GUI.GuiItemFabricator;
import Reika.ChromatiCraft.GUI.GuiMiner;
import Reika.ChromatiCraft.GUI.GuiOneSlot;
import Reika.ChromatiCraft.GUI.GuiRitualTable;
import Reika.ChromatiCraft.GUI.GuiSpawnerProgrammer;
import Reika.ChromatiCraft.GUI.GuiTelePump;
import Reika.ChromatiCraft.ModInterface.TileEntityAspectFormer;
import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.ChromatiCraft.TileEntity.TileEntityAutoEnchanter;
import Reika.ChromatiCraft.TileEntity.TileEntityCastingTable;
import Reika.ChromatiCraft.TileEntity.TileEntityCollector;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalBrewer;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalCharger;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalFurnace;
import Reika.ChromatiCraft.TileEntity.TileEntityItemCollector;
import Reika.ChromatiCraft.TileEntity.TileEntityItemFabricator;
import Reika.ChromatiCraft.TileEntity.TileEntityMiner;
import Reika.ChromatiCraft.TileEntity.TileEntityRift;
import Reika.ChromatiCraft.TileEntity.TileEntityRitualTable;
import Reika.ChromatiCraft.TileEntity.TileEntitySpawnerReprogrammer;
import Reika.ChromatiCraft.TileEntity.TileEntityTeleportationPump;
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
		case ABILITY:
			return null;
		case HANDBOOK:
			return null;
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

			if (te instanceof OneSlotMachine) {
				return new GuiOneSlot(player, (TileEntityChromaticBase)te);
			}/*
			if (te instanceof IInventory && !(te instanceof InertIInv))
				return new GuiBasicStorage(player, (RotaryCraftTileEntity)te);
			 */
			break;
		case ABILITY:
			return new GuiAbilitySelect(player);
		case HANDBOOK:
			return new GuiChromaBook(player);
		}
		return null;
	}
}
