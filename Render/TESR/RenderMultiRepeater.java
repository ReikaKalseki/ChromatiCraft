/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.TESR;

import net.minecraft.tileentity.TileEntity;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCompoundRepeater;
import Reika.ChromatiCraft.TileEntity.Networking.TileEntityCrystalRepeater;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;

public class RenderMultiRepeater extends RenderCrystalRepeater {

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		super.renderTileEntityAt(tile, par2, par4, par6, par8);
	}

	@Override
	protected int getHaloRenderColor(TileEntityCrystalRepeater te) {
		//CrystalElement e = ((TileEntityCompoundRepeater)te).getRenderColorWithOffset(-8);
		//CrystalElement next = CrystalElement.elements[(e.ordinal()+1)%16];
		//float f = te.getTicksExisted()%32/32F;
		//ReikaJavaLibrary.pConsole(f+" from "+e+" to "+next);
		//return CrystalElement.getBlendedColor(te.getTicksExisted(), 32);//ReikaColorAPI.mixColors(next.getColor(), e.getColor(), f);

		float mod = 32F;
		TileEntityCompoundRepeater tc = (TileEntityCompoundRepeater)te;
		int tick = (1+(int)((tc.getColorCycleTick()/(double)mod)%16))%16;
		CrystalElement e1 = CrystalElement.elements[tick];
		CrystalElement e2 = CrystalElement.elements[(tick+1)%16];
		float mix = (float)(tc.getColorCycleTick()%(double)mod)/mod;
		mix = Math.min(mix*2, 1);
		int c1 = e1.getColor();
		int c2 = e2.getColor();
		int color = ReikaColorAPI.mixColors(c2, c1, mix);
		return color;
	}

}
