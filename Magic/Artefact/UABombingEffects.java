/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Artefact;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import Reika.ChromatiCraft.Magic.Artefact.Effects.DisplacementEffect;
import Reika.ChromatiCraft.Magic.Artefact.Effects.DrainPowerEffect;
import Reika.ChromatiCraft.Magic.Artefact.Effects.DropItemsEffect;
import Reika.ChromatiCraft.Magic.Artefact.Effects.LightningStrikeEffect;
import Reika.DragonAPI.Instantiable.Data.WeightedRandom;


public class UABombingEffects {

	public static final UABombingEffects instance = new UABombingEffects();

	public static final double TRADE_BOMBING_CHANCE = 20;

	private final WeightedRandom<UABombingEffect.BlockEffect> blockEffects = new WeightedRandom();
	private final WeightedRandom<UABombingEffect.EntityEffect> entityEffects = new WeightedRandom();

	private UABombingEffects() {
		blockEffects.addEntry(new DropItemsEffect(), 50);
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

}
