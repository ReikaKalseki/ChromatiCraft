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
import java.util.HashMap;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.API.Interfaces.CustomRangeUpgrade;
import Reika.ChromatiCraft.API.Interfaces.CustomRangeUpgrade.RangeUpgradeable;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.GUI.GuiItemDisplay;
import Reika.DragonAPI.Instantiable.GUI.GuiItemDisplay.GuiStackDisplay;


public class TileEntityRangeBoost extends TileEntityAdjacencyUpgrade {

	private static double[] factors = {
			1.03125,
			1.0625,
			1.125,
			1.25,
			1.5,
			2,
			3,
			4
	};

	private static final HashMap<Class, RangeEffect> specialInteractions = new HashMap();

	private static final BasicRangeEffect basicRangeUpgradeable = new BasicRangeEffect();

	public static double getFactor(int tier) {
		return factors[tier];
	}

	public static void customizeTile(Class c, CustomRangeUpgrade h) {
		specialInteractions.put(c, new CustomRangeEffect(h));
	}

	public static void addBasicHandling(Class<? extends RangeUpgradeable> c, ItemStack... items) {
		specialInteractions.put(c, basicRangeUpgradeable);
		for (ItemStack is : items)
			basicRangeUpgradeable.items.add(is);
	}

	@Override
	protected EffectResult tickDirection(World world, int x, int y, int z, ForgeDirection dir, long startTime) {
		TileEntity te = this.getEffectiveTileOnSide(dir);
		RangeEffect c = specialInteractions.get(te);
		if (c != null) {
			c.upgradeRange(te, this.getRangeFactor());
			return EffectResult.ACTION;
		}
		return EffectResult.CONTINUE;
	}

	public double getRangeFactor() {
		return factors[this.getTier()];
	}

	@Override
	public CrystalElement getColor() {
		return CrystalElement.LIME;
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

	private static class BasicRangeEffect extends RangeEffect {

		private final ArrayList<ItemStack> items = new ArrayList();

		private BasicRangeEffect() {
			super();
		}

		@Override
		public String getDescription() {
			return "Expands range";
		}

		@Override
		public void getRelevantItems(ArrayList<GuiItemDisplay> li) {
			for (ItemStack is : items) {
				li.add(new GuiStackDisplay(is));
			}
		}

		@Override
		protected boolean isActive() {
			return true;
		}

		@Override
		protected void upgradeRange(TileEntity te, double r) {
			((RangeUpgradeable)te).upgradeRange(r);
		}

	}

	private static class CustomRangeEffect extends RangeEffect {

		private CustomRangeUpgrade effect;

		protected CustomRangeEffect(CustomRangeUpgrade e) {
			super();
			effect = e;
		}

		@Override
		public String getDescription() {
			return effect.getDescription();
		}

		@Override
		public void getRelevantItems(ArrayList<GuiItemDisplay> li) {
			for (ItemStack is : effect.getItems()) {
				li.add(new GuiStackDisplay(is));
			}
		}

		@Override
		protected boolean isActive() {
			return true;
		}

		@Override
		protected void upgradeRange(TileEntity te, double r) {
			effect.upgradeRange(te, r);
		}

	}

	private static abstract class RangeEffect extends SpecificAdjacencyEffect {

		protected RangeEffect() {
			super(CrystalElement.LIME);
		}

		protected abstract void upgradeRange(TileEntity te, double r);

	}

}
