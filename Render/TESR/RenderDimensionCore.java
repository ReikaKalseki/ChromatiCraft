/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.TESR;

import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.MinecraftForgeClient;

import Reika.ChromatiCraft.Base.RenderLocusPoint;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityLocusPoint;
import Reika.ChromatiCraft.Registry.ChromaShaders;
import Reika.ChromatiCraft.TileEntity.Technical.TileEntityDimensionCore;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;

public class RenderDimensionCore extends RenderLocusPoint {

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

	@Override
	protected void doOtherRendering(TileEntityLocusPoint tile, float par8) {
		TileEntityDimensionCore te = (TileEntityDimensionCore)tile;
		if (te.isInWorld() && MinecraftForgeClient.getRenderPass() == 1) {
			ChromaShaders.DIMCORE.clearOnRender = true;
			ChromaShaders.DIMCORE.setIntensity(1);
			ChromaShaders.DIMCORE.getShader().addFocus(tile);
			double dist = Minecraft.getMinecraft().thePlayer.getDistance(tile.xCoord+0.5, tile.yCoord+0.5, tile.zCoord+0.5);
			HashMap<String, Object> map = new HashMap();
			map.put("distance", dist*dist);
			map.put("coreRed", te.getColor().getRed());
			map.put("coreGreen", te.getColor().getGreen());
			map.put("coreBlue", te.getColor().getBlue());
			float f = 0;
			if (dist <= 8) {
				f = 1;
			}
			else if (dist <= 40) {
				f = 1-(float)((dist-8D)/32D);
			}
			f = 1;
			ChromaShaders.DIMCORE.getShader().modifyLastCompoundFocus(f, map);
		}
	}

}
