/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.GUI.Tile;

import java.util.Iterator;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.API.AbilityAPI.Ability;
import Reika.ChromatiCraft.Auxiliary.CustomSoundGuiButton.CustomSoundImagedGuiButton;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.GUI.GuiChromability;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaResearch;
import Reika.ChromatiCraft.Registry.ChromaResearchManager.ResearchLevel;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityRitualTable;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;

public class GuiRitualTable extends GuiChromability {

	private final TileEntityRitualTable tile;

	public GuiRitualTable(EntityPlayer ep, TileEntityRitualTable te) {
		super(ep);
		tile = te;

		ySize = 224;

		if (!te.isFullyEnhanced()) {
			Iterator<Ability> it = abilities.iterator();
			while (it.hasNext()) {
				Ability a = it.next();
				if (a instanceof Chromabilities) {
					ChromaResearch r = ChromaResearch.getPageFor((Chromabilities)a);
					if (r.level.ordinal() >= ResearchLevel.CTM.ordinal()) {
						it.remove();
					}
					else {
						ProgressStage[] p = r.getRequiredProgress();
						for (int i = 0; i < p.length; i++) {
							ProgressStage ps = p[i];
							if (ps.isGatedAfter(ProgressStage.DIMENSION)) {
								it.remove();
								break;
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void initGui() {
		super.initGui();

		int j = (width - xSize) / 2;
		int k = (height - ySize) / 2;
		String tex = "Textures/GUIs/ability3.png";
		buttonList.add(new CustomSoundImagedGuiButton(2, j+8, k+8, 50, 50, 0, 193, tex, ChromatiCraft.class, this));
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

	@Override
	protected String getBackTexture(Ability a) {
		return Chromabilities.playerHasAbility(player, a) ? "Textures/GUIs/ability3.png" : "Textures/GUIs/ability4.png";
	}

	@Override
	protected String getButtonTexture() {
		return "Textures/GUIs/ability3.png";
	}

}
