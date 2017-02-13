/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Models;

import java.util.ArrayList;

import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Base.ChromaModelBase;
import Reika.DragonAPI.Instantiable.Rendering.LODModelPart;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;


public abstract class ModelFocusCrystalBase extends ChromaModelBase {

	protected final void prepareOuterRing() {
		GL11.glTranslated(-0.625, 0.03125, 0);
		GL11.glRotated(-25, 0, 0, 1);
	}

	protected final void prepareInnerRing() {
		GL11.glTranslated(-0.625, 0.0875, 0);
		GL11.glRotated(-25, 0, 0, 1);
	}

	protected final void renderPart(LODModelPart part, TileEntity te, ArrayList li) {
		float t = (float)li.get(0);
		int c = (int)li.get(1);
		double n = 6+3*Math.cos(((long)System.identityHashCode(te)*(long)System.identityHashCode(part))%(Math.PI*2));
		float f = 0.75F+(float)(0.25*Math.sin((((long)System.identityHashCode(part)+(long)System.identityHashCode(te)+(double)t)/n)%(Math.PI*2)));
		c = ReikaColorAPI.mixColors(c, 0xffffff, f);
		float r = ReikaColorAPI.getRed(c)/255F;
		float g = ReikaColorAPI.getGreen(c)/255F;
		float b = ReikaColorAPI.getBlue(c)/255F;
		//GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
		GL11.glColor3f(r, g, b);
		part.render(te, f5);
		//GL11.glPopAttrib();
	}

}
