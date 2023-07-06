/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.TileEntity.AOE.Effect;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade;
import Reika.ChromatiCraft.Magic.Interfaces.LumenConsumer;
import Reika.ChromatiCraft.Registry.AdjacencyUpgrades;
import Reika.ChromatiCraft.Registry.ChromaOptions;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.GUI.GuiItemDisplay;
import Reika.DragonAPI.Instantiable.GUI.GuiItemDisplay.GuiStackDisplay;


public class TileEntityEfficiencyUpgrade extends TileEntityAdjacencyUpgrade {

	private static double[] factor = {
			0.9375,
			0.875,
			0.75,
			0.625,
			0.5,
			0.25,
			0.125,
			0.0625,
	};

	private static final LumenEfficiencyEffect lumenEfficiency = new LumenEfficiencyEffect();

	private static class LumenEfficiencyEffect extends SpecificAdjacencyEffect {

		private final ArrayList<ItemStack> items = new ArrayList();

		private LumenEfficiencyEffect() {
			super(CrystalElement.BLACK);
		}

		@Override
		public String getDescription() {
			return "Improves lumen efficiency usage";
		}

		@Override
		public void getRelevantItems(ArrayList<GuiItemDisplay> li) {
			for (ItemStack is : items)
				li.add(new GuiStackDisplay(is));
		}

		@Override
		protected boolean isActive() {
			return !items.isEmpty();
		}

	}

	public static void loadTileList() {
		for (int i = 0; i < ChromaTiles.TEList.length; i++) {
			ChromaTiles r = ChromaTiles.TEList[i];
			if (r == ChromaTiles.ADJACENCY || r.isDummiedOut())
				continue;
			if (LumenConsumer.class.isAssignableFrom(r.getTEClass()) && !r.isRepeater()) {
				LumenConsumer lc = (LumenConsumer)r.createTEInstanceForRender(0);
				if (lc.allowsEfficiencyBoost())
					lumenEfficiency.items.add(r.getCraftedProduct());
			}
		}
		if (ChromaOptions.POWEREDACCEL.getState()) {
			for (int i = 0; i < 16; i++) {
				if (AdjacencyUpgrades.upgrades[i].isImplemented())
					lumenEfficiency.items.add(AdjacencyUpgrades.upgrades[i].getStackOfTier(2));
			}
		}
	}

	@Override
	protected EffectResult tickDirection(World world, int x, int y, int z, ForgeDirection dir, long startTime) {
		return EffectResult.STOP;
	}

	@Override
	public CrystalElement getColor() {
		return CrystalElement.BLACK;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	public static double getCostFactor(int tier) {
		return factor[tier];
	}

}
