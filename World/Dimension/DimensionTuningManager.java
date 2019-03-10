package Reika.ChromatiCraft.World.Dimension;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

import Reika.ChromatiCraft.Registry.ExtraChromaIDs;

public class DimensionTuningManager {

	private static final String NBT_TAG = "dimensionTuning";

	public static final DimensionTuningManager instance = new DimensionTuningManager();

	private DimensionTuningManager() {

	}

	public void tunePlayer(EntityPlayer ep, int amt) {
		ep.getEntityData().setInteger(NBT_TAG, amt);
	}

	public int getPlayerTuning(EntityPlayer ep) {
		return ep.getEntityData().getInteger(NBT_TAG);
	}

	public float getTunedDropRates(EntityPlayer ep) {
		int tune = this.getPlayerTuning(ep);
		if (tune <= 0) {
			return -1;
		}
		if (tune <= 16) {
			return 0;
		}
		if (tune <= 64) {
			return (tune-16)/48F;
		}
		if (tune <= 576) {
			return 1+(tune-64)/256F; //up to 3x
		}
		else {
			return 2-23+24*(float)Math.pow(tune/576F, 0.04);
		}
	}

	public int getTunedDropCount(EntityPlayer ep, int base, int min, int max) {
		if (ep.worldObj.provider.dimensionId != ExtraChromaIDs.DIMID.getValue())
			return base;
		int drops = (int)(base*this.getTunedDropRates(ep));
		return MathHelper.clamp_int(drops, min, max);
	}

	public static enum TuningThresholds {
		STRUCTURES(192),
		STRUCTUREBIOMES(96),
		SKYRIVER(384, 256),
		FARREGIONS(512),
		DECOHARVEST(4),
		CHESTS(16);

		public final int minimumTuning;
		public final int minimumEffect;

		private TuningThresholds(int t) {
			this(t, 0);
		}

		private TuningThresholds(int t, int e) {
			minimumTuning = t;
			minimumEffect = e;
		}

		public float getTuningFraction(EntityPlayer ep) {
			int amt = instance.getPlayerTuning(ep);
			if (amt < minimumEffect)
				return 0;
			if (amt >= minimumTuning)
				return 1;
			return (amt-minimumEffect)/(float)(minimumTuning-minimumEffect);
		}

		public boolean isSufficientlyTuned(EntityPlayer ep) {
			return instance.getPlayerTuning(ep) >= minimumTuning;
		}
	}

}
