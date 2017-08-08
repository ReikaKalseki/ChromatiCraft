/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary;

import java.util.HashMap;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderWorldEvent;
import net.minecraftforge.common.MinecraftForge;
import Reika.DragonAPI.Instantiable.Data.Immutable.BlockBox;
import Reika.DragonAPI.Instantiable.Rendering.TessellatorVertexList;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class WorldBoxHelper {

	private final HashMap<EntityPlayer, BlockBox> data = new HashMap();

	public static final WorldBoxHelper instance = new WorldBoxHelper();

	private WorldBoxHelper() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	public void addBox(EntityPlayer ep, int x1, int y1, int z1, int x2, int y2, int z2) {
		BlockBox box = new BlockBox(x1, y1, z1, x2, y2, z2);
		data.put(ep, box);
	}

	public void removeBox(EntityPlayer ep) {
		data.remove(ep);
	}

	@SubscribeEvent
	public void renderBoxes(RenderWorldEvent.Post evt) {
		if (evt.pass == 1) {
			for (EntityPlayer ep : data.keySet()) {
				BlockBox box = data.get(ep);
				Tessellator v5 = Tessellator.instance;
				v5.setColorRGBA(255, 127, 255, 127);
				TessellatorVertexList li = new TessellatorVertexList();
				li.addVertex(box.minX, box.minY, box.minZ);
				li.addVertex(box.maxX, box.minY, box.minZ);
				li.addVertex(box.maxX, box.minY, box.maxZ);
				li.addVertex(box.minX, box.minY, box.maxZ);

				li.addVertex(box.minX, box.maxY, box.minZ);
				li.addVertex(box.maxX, box.maxY, box.minZ);
				li.addVertex(box.maxX, box.maxY, box.maxZ);
				li.addVertex(box.minX, box.maxY, box.maxZ);
				ReikaRenderHelper.prepareGeoDraw(true);
				v5.startDrawingQuads();
				li.render();
				v5.draw();
				ReikaRenderHelper.exitGeoDraw();
			}
		}
	}

}
