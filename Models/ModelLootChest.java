/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Models;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;

import Reika.ChromatiCraft.Base.ChromaModelBase;
import Reika.DragonAPI.Instantiable.Rendering.LODModelPart;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelLootChest extends ChromaModelBase
{
	/** The chest lid in the chest's model. */
	private LODModelPart chestLid;
	/** The model of the bottom of the chest. */
	private LODModelPart chestBelow;
	/** The chest's knob in the chest model. */
	private LODModelPart chestKnob;

	public ModelLootChest() {
		chestLid = (LODModelPart)(new LODModelPart(this, 0, 0)).setTextureSize(64, 64);
		chestLid.addBox(0.0F, -5.0F, -14.0F, 14, 5, 14);
		chestLid.setRotationPoint(1, 7, 15);

		chestKnob = (LODModelPart)(new LODModelPart(this, 0, 0)).setTextureSize(64, 64);
		chestKnob.addBox(-1.0F, -2.0F, -15.0F, 2, 4, 1);
		chestKnob.setRotationPoint(8, 7, 15);

		chestBelow = (LODModelPart)(new LODModelPart(this, 0, 19)).setTextureSize(64, 64);
		chestBelow.addBox(0.0F, 0.0F, 0.0F, 14, 10, 14);
		chestBelow.setRotationPoint(1, 6, 1);
	}

	@Override
	public void renderAll(TileEntity te, ArrayList li) {
		chestLid.render(te, f5);
		chestKnob.render(te, f5);
		chestBelow.render(te, f5);
	}

	public void setLidRotation(float f) {
		chestLid.rotateAngleX = f;
		chestKnob.rotateAngleX = f;
	}
}
