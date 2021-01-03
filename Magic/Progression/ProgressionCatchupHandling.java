package Reika.ChromatiCraft.Magic.Progression;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.RecipeManagers.CastingRecipe.RecipeType;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.TileEntity.Recipe.TileEntityCastingTable;
import Reika.DragonAPI.Base.TileEntityBase;
import Reika.DragonAPI.Instantiable.RayTracer;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaRenderHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ProgressionCatchupHandling {

	public static final ProgressionCatchupHandling instance = new ProgressionCatchupHandling();

	private final RayTracer LOS = RayTracer.getVisualLOS();

	private int progressPacketCooldown = 0;

	private ProgressionCatchupHandling() {

	}

	public void attemptSync(TileEntityBase te, double maxDist, ProgressStage p) {
		this.attemptSync(te, maxDist, p, false);
	}

	public void attemptSync(TileEntityBase te, double maxDist, ProgressStage p, boolean ignoreTime) {
		if (ignoreTime || te.getTicksExisted()%10 == 0) {
			if (progressPacketCooldown > 0)
				progressPacketCooldown--;
			if (progressPacketCooldown < 20)
				this.triggerProgressCatchup(te, maxDist, p);
		}
	}

	@SideOnly(Side.CLIENT)
	private void triggerProgressCatchup(TileEntity te, double maxDist, ProgressStage p) {
		EntityPlayer ep = Minecraft.getMinecraft().thePlayer;
		if (ProgressionLinking.instance.hasLinkedPlayers(ep) && ProgressionManager.instance.canStepPlayerTo(ep, p)) {
			double dist = ep.getDistanceSq(te.xCoord+0.5, te.yCoord+0.5, te.zCoord+0.5);
			if (dist <= maxDist*maxDist) {
				AxisAlignedBB box = ReikaAABBHelper.getBlockAABB(te);
				if (ReikaRenderHelper.renderFrustrum.isBoundingBoxInFrustum(box)) {
					LOS.setOrigins(te.xCoord+0.5, te.yCoord+0.5, te.zCoord+0.5, ep.posX, ep.posY, ep.posZ);
					if (LOS.isClearLineOfSight(te.worldObj)) {
						ReikaPacketHelper.sendPacketToServer(ChromatiCraft.packetChannel, ChromaPackets.PROGSYNC.ordinal(), te, p.ordinal());
						progressPacketCooldown++;
					}
				}
			}
		}
	}

	public static enum CastingProgressSyncTriggers {

		FOCUS(ProgressStage.FOCUSCRYSTAL),
		MULTI(ProgressStage.MULTIBLOCK),
		RUNES(ProgressStage.RUNEUSE),
		TUNED(ProgressStage.TUNECAST),
		;

		public final ProgressStage progress;

		private CastingProgressSyncTriggers(ProgressStage p) {
			progress = p;
		}

		public boolean isValid(TileEntityCastingTable te) {
			switch(this) {
				case FOCUS:
					return te.getAccelerationFactor() > 1;
				case MULTI:
					return te.isAtLeast(RecipeType.MULTIBLOCK);
				case RUNES:
					return te.isAtLeast(RecipeType.TEMPLE) && te.hasRunes();
				case TUNED:
					return te.isTuned();
			}
			return false;
		}

		public static Collection<CastingProgressSyncTriggers> getTriggers() {
			return Collections.unmodifiableCollection(Arrays.asList(values()));
		}

	}

}
