/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Artefact;

import java.lang.ref.WeakReference;
import java.util.HashSet;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import Reika.ChromatiCraft.Items.ItemUnknownArtefact.ArtefactTypes;
import Reika.ChromatiCraft.Magic.Artefact.Effects.DisplacementEffect;
import Reika.ChromatiCraft.Magic.Artefact.Effects.DrainPowerEffect;
import Reika.ChromatiCraft.Magic.Artefact.Effects.DropItemsEffect;
import Reika.ChromatiCraft.Magic.Artefact.Effects.LightningStrikeEffect;
import Reika.ChromatiCraft.ModInterface.AE.ItemShieldedCell;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;
import Reika.DragonAPI.Instantiable.Event.ScheduledTickEvent.ScheduledEvent;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;
import Reika.DragonAPI.ModInteract.DeepInteract.MESystemReader;
import Reika.DragonAPI.ModInteract.DeepInteract.MESystemReader.ItemInSystemEffect;
import Reika.DragonAPI.ModInteract.DeepInteract.MESystemReader.MESystemEffect;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.PowerMultiplier;
import appeng.api.implementations.items.IStorageCell;
import appeng.api.networking.IGrid;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.ICellInventory;
import appeng.api.storage.ICellInventoryHandler;
import appeng.api.storage.ICellProvider;
import appeng.api.storage.ICellRegistry;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.StorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import cpw.mods.fml.relauncher.Side;


public class UABombingEffects {

	public static final UABombingEffects instance = new UABombingEffects();

	public static final double TRADE_BOMBING_CHANCE = 20;

	private final WeightedRandom<UABombingEffect.BlockEffect> blockEffects = new WeightedRandom();
	private final WeightedRandom<UABombingEffect.EntityEffect> entityEffects = new WeightedRandom();

	private UABombingEffects() {
		blockEffects.addEntry(new DropItemsEffect(), 20);
		blockEffects.addEntry(new DrainPowerEffect(), 100);
		blockEffects.addEntry(new ExplodeEffect(), 2);

		entityEffects.addEntry(new LightningStrikeEffect(), 5);
		entityEffects.addEntry(new DisplacementEffect(), 50);
	}

	public void trigger(TileEntity te, IInventory inv) {
		blockEffects.getRandomEntry().trigger(inv, te);
	}

	public void trigger(Entity e) {
		entityEffects.getRandomEntry().trigger(e);
	}

	@ModDependent(ModList.APPENG)
	public MESystemEffect createMESystemEffect() {
		return new ItemInSystemEffect(ChromaItems.ARTEFACT.getStackOfMetadata(ArtefactTypes.ARTIFACT.ordinal())) {

			@Override
			public int getTickFrequency() {
				return 100;
			}

			@Override
			protected void doEffect(IGrid grid, long amt) {
				IEnergyGrid ie = (IEnergyGrid)grid.getCache(IEnergyGrid.class);
				if (ie.getStoredPower() <= 0)
					return;
				IStorageGrid isg = (IStorageGrid)grid.getCache(IStorageGrid.class);
				//ReikaJavaLibrary.pConsole("Ticking UA in ME System");
				HashSet<ICellProvider> set = MESystemReader.getAllCellContainers(isg);
				if (set != null) {
					for (ICellProvider icp : set) {
						if (icp instanceof IInventory) {
							IInventory ii = (IInventory)icp;
							for (int i = 0; i < ii.getSizeInventory(); i++) {
								ItemStack cell = ii.getStackInSlot(i);
								if (cell != null && cell.getItem() instanceof IStorageCell) {
									IStorageCell isc = (IStorageCell)cell.getItem();
									if (cell.getItem() instanceof ItemShieldedCell) {

									}
									else {
										ICellRegistry icr = AEApi.instance().registries().cell();
										IMEInventoryHandler inv = icr.getCellInventory(cell, null, StorageChannel.ITEMS);
										ICellInventoryHandler icih = (ICellInventoryHandler)inv;
										ICellInventory cellInv = icih.getCellInv();
										IItemList<IAEItemStack> items = cellInv.getAvailableItems(StorageChannel.ITEMS.createList());
										if (items.findPrecise(MESystemReader.createAEStack(ChromaItems.ARTEFACT.getStackOfMetadata(ArtefactTypes.ARTIFACT.ordinal()))) != null) {
											ie.extractAEPower(ie.getStoredPower(), Actionable.MODULATE, PowerMultiplier.ONE);
											//for (int t = 1; t <= 40; t += 5) {
											//	TickScheduler.instance.scheduleEvent(new ScheduledTickEvent(new MESystemDrain(grid)), 1);
											//}
											//ReikaJavaLibrary.pConsole("Detected unshielded UA in ME system");
										}
									}
								}
							}
						}
					}
				}
			}
		};
	}

	private static class MESystemDrain implements ScheduledEvent {

		private final WeakReference<IGrid> grid;

		private MESystemDrain(IGrid ie) {
			grid = new WeakReference(ie);
		}

		@Override
		public void fire() {
			IGrid ig = grid.get();
			if (ig != null) {
				IEnergyGrid ie = (IEnergyGrid)ig.getCache(IEnergyGrid.class);
				ReikaJavaLibrary.pConsole(ie.getStoredPower());
				ie.extractAEPower(Double.MAX_VALUE, Actionable.MODULATE, PowerMultiplier.ONE);
			}
		}

		@Override
		public boolean runOnSide(Side s) {
			return s == Side.SERVER;
		}

	}

}
