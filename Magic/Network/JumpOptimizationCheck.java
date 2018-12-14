package Reika.ChromatiCraft.Magic.Network;

import Reika.ChromatiCraft.Magic.Interfaces.CrystalNetworkTile;


public abstract class JumpOptimizationCheck {

	public static final JumpOptimizationCheck always = new JumpOptimizationCheck() {

		@Override
		public boolean canDirectLink(CrystalNetworkTile t1, CrystalNetworkTile t2) {
			return true;
		}

	};

	public abstract boolean canDirectLink(CrystalNetworkTile t1, CrystalNetworkTile t2);

	//public boolean canOptimizeFrom(CrystalNetworkTile te);

}
