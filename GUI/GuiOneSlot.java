/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI;

import net.minecraft.entity.player.EntityPlayer;
import Reika.ChromatiCraft.Base.GuiChromaBase;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.DragonAPI.Base.OneSlotContainer;

public class GuiOneSlot extends GuiChromaBase {

	public GuiOneSlot(EntityPlayer ep, TileEntityChromaticBase te) {
		super(new OneSlotContainer(ep, te), ep, te);
	}

	@Override
	public String getGuiTexture() {
		return "oneslot";
	}

}
