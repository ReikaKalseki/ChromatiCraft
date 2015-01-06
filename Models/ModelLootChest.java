/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
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
	public LODModelPart chestLid = (LODModelPart)(new LODModelPart(this, 0, 0)).setTextureSize(64, 64);
	/** The model of the bottom of the chest. */
	public LODModelPart chestBelow;
	/** The chest's knob in the chest model. */
	public LODModelPart chestKnob;

	public ModelLootChest()
	{
		chestLid.addBox(0.0F, -5.0F, -14.0F, 14, 5, 14, 0.0F);
		chestLid.rotationPointX = 1.0F;
		chestLid.rotationPointY = 7.0F;
		chestLid.rotationPointZ = 15.0F;
		chestKnob = (LODModelPart)(new LODModelPart(this, 0, 0)).setTextureSize(64, 64);
		chestKnob.addBox(-1.0F, -2.0F, -15.0F, 2, 4, 1, 0.0F);
		chestKnob.rotationPointX = 8.0F;
		chestKnob.rotationPointY = 7.0F;
		chestKnob.rotationPointZ = 15.0F;
		chestBelow = (LODModelPart)(new LODModelPart(this, 0, 19)).setTextureSize(64, 64);
		chestBelow.addBox(0.0F, 0.0F, 0.0F, 14, 10, 14, 0.0F);
		chestBelow.rotationPointX = 1.0F;
		chestBelow.rotationPointY = 6.0F;
		chestBelow.rotationPointZ = 1.0F;
	}

	@Override
	public void renderAll(TileEntity te, ArrayList li)
	{
		chestKnob.rotateAngleX = chestLid.rotateAngleX;
		chestLid.render(te, f5);
		chestKnob.render(te, f5);
		chestBelow.render(te, f5);
	}
}
