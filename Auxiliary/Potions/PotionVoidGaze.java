/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Auxiliary.Potions;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.StatCollector;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Base.ChromaPotion;
import Reika.ChromatiCraft.Registry.ChromaResearchManager;
import Reika.ChromatiCraft.Registry.ChromaResearchManager.ResearchLevel;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.DragonAPI.Libraries.IO.ReikaRenderHelper;


public class PotionVoidGaze extends ChromaPotion {

	public PotionVoidGaze(int id) {
		super(id, false, 0x61008E, 2);
	}

	@Override
	public String getName() {
		return StatCollector.translateToLocal("chromapotion.voidgaze");
	}

	@Override
	public void performEffect(EntityLivingBase e, int level) {
		if (e.worldObj.isRemote) {
			PotionEffect p = e.getActivePotionEffect(this);
			if (p.getDuration() == 1)
				VoidGazeLevels.list[level].onFinish((EntityPlayer)e);
		}
	}

	@Override
	public boolean isReady(int tick, int level) {
		return true;
	}

	public static enum VoidGazeLevels {
		PYLONXRAY(),
		CAVEPARTICLES(),
		FACEFLIP();

		public static VoidGazeLevels[] list = values();

		public static int getAppliedLevel(EntityPlayer ep) {
			for (int i = list.length-1; i >= 0; i--) {
				if (list[i].canApply(ep))
					return i;
			}
			return -1;
		}

		public void onStart(EntityPlayer e) {
			switch(this) {
				case FACEFLIP:
					if (e.worldObj.isRemote)
						ReikaRenderHelper.rerenderAllChunks();
					break;
				default:
					break;
			}
		}

		public void onFinish(EntityPlayer e) {
			switch(this) {
				case FACEFLIP:
					if (e.worldObj.isRemote)
						ReikaRenderHelper.rerenderAllChunks();
					break;
				default:
					break;
			}
		}

		public boolean canApply(EntityPlayer ep) {
			switch(this) {
				case PYLONXRAY:
					return ProgressStage.PYLON.isPlayerAtStage(ep);
				case CAVEPARTICLES:
					return ProgressStage.RUNEUSE.isPlayerAtStage(ep);
				case FACEFLIP:
					return ChromaResearchManager.instance.getPlayerResearchLevel(ep).isAtLeast(ResearchLevel.MULTICRAFT) && ProgressStage.END.isPlayerAtStage(ep) && ProgressStage.CHROMA.isPlayerAtStage(ep) && ProgressionManager.instance.hasPlayerDiscoveredAGeneratedStructure(ep) && ep.worldObj.provider.dimensionId != ExtraChromaIDs.DIMID.getValue();
				default:
					return false;
			}
		}

		public boolean isActiveOnPlayer(EntityPlayer ep) {
			PotionEffect pot = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(ChromatiCraft.voidGaze);
			return pot != null && pot.getAmplifier() >= this.ordinal() && pot.getDuration() >= 2;
		}
	}

}
