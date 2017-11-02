/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Magic.Network;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import Reika.ChromatiCraft.Magic.CrystalTarget;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Instantiable.Data.Immutable.WorldLocation;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;


public class TargetData {

	public final Class targetClass;
	public final double targetWidth;
	public final DecimalPosition position;
	public final WorldLocation source;

	private final AxisAlignedBB renderBox;

	public TargetData(CrystalTarget tg) {
		position = new DecimalPosition(tg.location).offset(tg.offsetX, tg.offsetY, tg.offsetZ);
		targetWidth = tg.endWidth;
		TileEntity te = tg.location.getTileEntity();
		targetClass = te != null ? te.getClass() : void.class;
		source = tg.source;

		renderBox = source.asAABB().addCoord(position.xCoord+0.5-source.xCoord, position.yCoord+0.5-source.yCoord, position.zCoord+0.5-source.zCoord).expand(1, 1, 1);
		//ReikaJavaLibrary.pConsole(source+", "+position+" > "+renderBox);
	}

	@Override
	public int hashCode() {
		return position.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof TargetData && ((TargetData)o).position.equals(position);
	}

	public boolean isRenderable() {
		//ReikaAABBHelper.renderAABB(renderBox, 0, 0, 0, 0, 0, 0, 160, 255, 255, 255, true);
		return ReikaRenderHelper.renderFrustrum.isBoundingBoxInFrustum(renderBox);
	}

}
