package Reika.ChromatiCraft.Magic.Network;

import net.minecraft.tileentity.TileEntity;
import Reika.ChromatiCraft.Magic.CrystalTarget;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;


public class TargetData {

	public final Class targetClass;
	public final double targetWidth;
	public final DecimalPosition position;

	public TargetData(CrystalTarget tg) {
		position = new DecimalPosition(tg.location).offset(tg.offsetX, tg.offsetY, tg.offsetZ);
		targetWidth = tg.endWidth;
		TileEntity te = tg.location.getTileEntity();
		targetClass = te != null ? te.getClass() : void.class;
	}

	@Override
	public int hashCode() {
		return position.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof TargetData && ((TargetData)o).position.equals(position);
	}

}
