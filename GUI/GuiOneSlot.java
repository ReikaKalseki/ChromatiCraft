/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI;

import Reika.ChromatiCraft.Base.GuiChromaBase;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.DragonAPI.Base.OneSlotContainer;

import net.minecraft.entity.player.EntityPlayer;

public class GuiOneSlot extends GuiChromaBase {

	public GuiOneSlot(EntityPlayer ep, TileEntityChromaticBase te) {
		super(new OneSlotContainer(ep, te), te);
	}

	@Override
	public String getGuiTexture() {
		return "oneslot";
	}

}
