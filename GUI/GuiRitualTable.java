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

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.AbilityAPI.Ability;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityRitualTable;
import Reika.DragonAPI.Instantiable.GUI.ImagedGuiButton;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;

public class GuiRitualTable extends GuiChromability {

	private final TileEntityRitualTable tile;

	public GuiRitualTable(EntityPlayer ep, TileEntityRitualTable te) {
		super(ep);
		tile = te;
	}

	@Override
	public void initGui() {
		super.initGui();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		String tex = "Textures/GUIs/ability.png";
		buttonList.add(new ImagedGuiButton(2, j+8, k+8, 50, 50, 0, 193, tex, ChromatiCraft.class));
	}

	@Override
	protected void actionPerformed(GuiButton b) {
		if (b.id == 2) {
			Ability a = this.getActiveAbility();
			if (a.isAvailableToPlayer(player)) {
				ReikaPacketHelper.sendDataPacket(ChromatiCraft.packetChannel, ChromaPackets.ABILITYCHOOSE.ordinal(), tile, Chromabilities.getAbilityInt(a));
				player.closeScreen();
			}
			else {
				ChromaSounds.ERROR.playSound(player);
			}
			return;
		}
		super.actionPerformed(b);
	}

}
