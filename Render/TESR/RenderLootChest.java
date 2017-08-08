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

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.ChromaRenderBase;
import Reika.ChromatiCraft.Block.Worldgen.BlockLootChest.TileEntityLootChest;
import Reika.ChromatiCraft.Models.ModelLootChest;
import Reika.DragonAPI.Interfaces.TileEntity.RenderFetcher;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

public class RenderLootChest extends ChromaRenderBase {

	private final ModelLootChest model = new ModelLootChest();

	@Override
	public String getImageFileName(RenderFetcher te) {
		return null;
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double par2, double par4, double par6, float par8) {
		TileEntityLootChest te = (TileEntityLootChest)tile;
		GL11.glPushMatrix();
		GL11.glTranslated(par2, par4, par6);

		int meta = te.getBlockMetadata();
		String tex = "Textures/TileEntity/lootchest.png";
		ReikaTextureHelper.bindTexture(ChromatiCraft.class, tex);
		float f1 = te.prevLidAngle+(te.lidAngle-te.prevLidAngle)*par8;
		f1 = 1.0F-f1;
		f1 = 1.0F-f1*f1*f1;
		float rot = 0;
		GL11.glTranslated(-0.5, -0.5, -0.5);

		GL11.glPushMatrix();
		switch(meta%8) {
			case 2:
				rot = 180;
				GL11.glTranslated(2, 0, 1);
				break;
			case 0:
				rot = -90;
				GL11.glTranslated(1, 0, 0);
				break;
			case 1:
				rot = 90;
				GL11.glTranslated(1, 0, 2);
				break;
			case 3:
				rot = 0;
				GL11.glTranslated(0, 0, 1);
				break;
		}

		GL11.glRotatef(rot, 0.0F, 1.0F, 0.0F);
		model.chestLid.rotateAngleX = -(f1*(float)Math.PI/2.0F);
		if (MinecraftForgeClient.getRenderPass() == 0 || !te.hasWorldObj())
			this.renderModel(te, model);
		GL11.glPopMatrix();

		if (meta >= 8) {
			GL11.glPushMatrix();
			switch(meta%8) {
				case 2:
					GL11.glTranslated(1, 0, -1);
					break;
				case 0:
					GL11.glTranslated(1, 0, 2);
					GL11.glRotated(180, 0, 1, 0);
					break;
				case 1:
					GL11.glTranslated(2, 0, 3);
					GL11.glRotated(180, 0, 1, 0);
					break;
				case 3:
					GL11.glTranslated(0, 0, 0);
					break;
			}
			GL11.glTranslated(1/256D, 0, -1/256D);
			ReikaRenderHelper.renderEnchantedModel(te, model, null, rot);
			GL11.glPopMatrix();
		}

		GL11.glPopMatrix();
	}

}
