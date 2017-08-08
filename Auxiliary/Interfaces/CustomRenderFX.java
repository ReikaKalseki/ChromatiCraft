/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Interfaces;

import Reika.ChromatiCraft.Render.ParticleEngine.RenderMode;
import Reika.ChromatiCraft.Render.ParticleEngine.TextureMode;


public interface CustomRenderFX {

	RenderMode getRenderMode();

	TextureMode getTexture();

}
