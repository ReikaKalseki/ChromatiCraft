package Reika.ChromatiCraft.Auxiliary;

import Reika.ChromatiCraft.API.Interfaces.RangeUpgradeable;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityAdjacencyUpgrade;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.AOE.Effect.TileEntityRangeBoost;
import Reika.DragonAPI.Base.TileEntityBase;

public class RangeTracker {

	private final int baseRange;
	private final int stepSpeed;

	private int currentRange;

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

	private int getMaxUpgradedRange(RangeUpgradeable te) {
		int max = baseRange;
		int boost = TileEntityAdjacencyUpgrade.getAdjacentUpgrade((TileEntityBase)te, CrystalElement.LIME);
		if (boost > 0) {
			double fac = TileEntityRangeBoost.getFactor(boost-1);
			max = (int)(max*fac);
		}
		return max;
	}

	public void initialize(RangeUpgradeable te) {
		currentRange = this.getMaxUpgradedRange(te);
	}

	public boolean update(RangeUpgradeable te) {
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

}
