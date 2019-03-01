/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Render.Entity;

import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Models.ModelTunnelNuker;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

public class RenderTunnelNuker extends RendererLivingEntity {

	public RenderTunnelNuker() {
		super(new ModelTunnelNuker(), 0.125F);
	}

	@Override
	public void doRender(Entity e, double par2, double par4, double par6, float par8, float ptick) {
		//ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/tunnelnuker.png");
		super.doRender(e, par2, par4, par6, par8, ptick);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity e) {
		return null;
	}

	@Override
	protected void bindEntityTexture(Entity e) {
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, "Textures/TunnelNuker.png");
	}

	@Override
	protected boolean func_110813_b(EntityLivingBase e) {
		return false;
	}

}
