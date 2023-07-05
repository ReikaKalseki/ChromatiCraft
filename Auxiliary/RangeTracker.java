package Reika.ChromatiCraft.Auxiliary;

import net.minecraft.nbt.NBTTagCompound;

import Reika.ChromatiCraft.API.Interfaces.CustomRangeUpgrade.RangeUpgradeable;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.AOE.Effect.TileEntityRangeBoost;
import Reika.DragonAPI.Base.TileEntityBase;

public class RangeTracker {

	protected final int baseRange;
	private final int stepSpeed;

	protected int currentRange;

	public RangeTracker(int r) {
		this(r, 1);
	}

	public RangeTracker(int r, int s) {
		baseRange = r;
		stepSpeed = s;
		currentRange = baseRange;
	}

	public int getRange() {
		return currentRange;
	}

	protected final int getMaxUpgradedRange(RangeUpgradeable te) {
		int max = baseRange;
		int boost = TileEntityAdjacencyUpgrade.getAdjacentUpgrade((TileEntityBase)te, CrystalElement.LIME);
		//ReikaJavaLibrary.pConsole(te+", "+FMLCommonHandler.instance().getEffectiveSide()+", "+boost);
		if (boost > 0) {
			double fac = TileEntityRangeBoost.getFactor(boost-1);
			max = (int)(max*fac);
		}
		return max;
	}

	public void initialize(RangeUpgradeable te) {
		currentRange = this.getMaxUpgradedRange(te);
	}

	public final boolean update(RangeUpgradeable te) {
		if (((TileEntityBase)te).worldObj.isRemote)
			return false;
		int max = this.getMaxUpgradedRange(te);
		if (max > currentRange) {
			currentRange = Math.min(max, currentRange+stepSpeed);
			return true;
		}
		else if (max < currentRange) {
			currentRange = max;
			return true;
		}
		return false;
	}

	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("current", currentRange);
	}

	public void readFromNBT(NBTTagCompound tag) {
		currentRange = tag.getInteger("current");
	}

	public static final class ConfigurableRangeTracker extends RangeTracker {

		private final int minimumRange;

		private int configuredRange;

		public ConfigurableRangeTracker(int r, int min) {
			super(r);
			minimumRange = min;
			configuredRange = baseRange;
		}

		public ConfigurableRangeTracker(int r, int s, int min) {
			super(r, s);
			minimumRange = min;
			configuredRange = baseRange;
		}

		@Override
		public void initialize(RangeUpgradeable te) {
			currentRange = configuredRange;
		}

		public boolean decrement(RangeUpgradeable te, int amt) {
			return this.setRange(Math.max(configuredRange-amt, minimumRange));
		}

		public boolean increment(RangeUpgradeable te, int amt) {
			return this.setRange(Math.min(configuredRange+amt, currentRange));
		}

		private boolean setRange(int r) {
			if (r != configuredRange) {
				configuredRange = r;
				return true;
			}
			return false;
		}

		@Override
		public int getRange() {
			return Math.min(super.getRange(), configuredRange);
		}

		@Override
		public void writeToNBT(NBTTagCompound tag) {
			super.writeToNBT(tag);
			tag.setInteger("range", configuredRange);
		}

		@Override
		public void readFromNBT(NBTTagCompound tag) {
			super.readFromNBT(tag);
			configuredRange = tag.getInteger("range");
		}

	}

}
