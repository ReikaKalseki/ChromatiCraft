/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft;

import java.util.Random;

import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import Reika.ChromatiCraft.Registry.ChromaItems;
import Reika.ChromatiCraft.Render.ColorizableSlimeModel;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ChromaClientEventController {

	public static final ChromaClientEventController instance = new ChromaClientEventController();

	protected static final Random rand = new Random();

	private boolean editedSlimeModel = false;

	private ChromaClientEventController() {

	}

	@SubscribeEvent
	public void slimeColorizer(RenderLivingEvent.Pre ev) {
		EntityLivingBase e = ev.entity;
		RendererLivingEntity r = ev.renderer;
		if (!editedSlimeModel && e.getClass() == EntitySlime.class) {
			r.mainModel = new ColorizableSlimeModel(16);
			editedSlimeModel = true;
			ChromatiCraft.instance.logger.log("Overriding Slime Renderer Core Model.");
		}
	}

	@SubscribeEvent
	public void drawHighlight(DrawBlockHighlightEvent evt) {
		if (evt.currentItem != null && ChromaItems.EXCAVATOR.matchWith(evt.currentItem)) {

		}
	}
}
