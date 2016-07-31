package Reika.ChromatiCraft.World.Dimension;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SkyRiverManagerClient {

	private static boolean allowClientSkyRiverMovement = false;

	@SideOnly(Side.CLIENT)
	public static void handleSkyRiverMovementClient() {
		World w = Minecraft.getMinecraft().theWorld;
		if (w == null || w.provider.dimensionId != ExtraChromaIDs.DIMID.getValue())
			return;

		EntityPlayer pl = Minecraft.getMinecraft().thePlayer;

		SkyRiverGenerator.RiverPoint closest = SkyRiverGenerator.getClosestPoint(pl, 48, false);
		if (closest != null && closest.positionID == 1) {
			double d = closest.position.getDistanceTo(pl.posX, pl.posY, pl.posZ);
			if (d <= 48) {
				if (w.getTotalWorldTime()%111 == 0) {
					float v = 0.5F+0.5F*(float)Math.sqrt(1D-d/48D);
					ReikaSoundHelper.playClientSound(ChromaSounds.SKYRIVER, pl, v, 1);
				}
			}
		}

		if (!allowClientSkyRiverMovement) {
			return;
		}

		if (closest != null) {
			if (SkyRiverGenerator.isWithinSkyRiver(pl, closest)) {
				SkyRiverManager.movePlayer(pl, closest, doesClientTryToMove());
			}
		}
	}

	public static void handleRayUpdatePacket(NBTTagCompound cmp) {
		SkyRiverGenerator.Ray r = SkyRiverGenerator.Ray.readFromPktNBT(cmp);
		List<DecimalPosition> points = r.getPoints();
		SkyRiverGenerator.RiverPoint prev = null;
		for (int i = 1; i < points.size() - 1; i++) {
			DecimalPosition p1 = points.get(i);
			DecimalPosition pb = points.get(i + 1);
			DecimalPosition pa = points.get(i - 1);
			ChunkCoordIntPair ch = new ChunkCoordIntPair(MathHelper.floor_double(p1.xCoord) / 16, MathHelper.floor_double(p1.zCoord) / 16);
			SkyRiverGenerator.RiverPoint p = new SkyRiverGenerator.RiverPoint(i, ch, p1, pa, pb);
			if (prev != null) {
				prev.nextRiverPoint = p;
			}
			prev = p;
			synchronized (SkyRiverGenerator.clientPoints) {
				SkyRiverGenerator.clientPoints.addValue(ch, p);
			}
		}
		SkyRiverManager.debugMessage("Client> Received " + points.size() + " SkyRiver Ray-Points from Server.");
	}

	public static void handleRayClearPacket() {
		synchronized (SkyRiverGenerator.clientPoints) {
			SkyRiverGenerator.clientPoints.clear();
		}
		allowClientSkyRiverMovement = false;
		SkyRiverManager.debugMessage("Client> Cleared Client-SkyRiver.");
	}

	public static void handleClientState(int state) {
		switch (state) {
			case 0:
				handleRayClearPacket();
				break;
			case 1:
				SkyRiverManager.debugMessage("Client> StateChange: ALLOW");
				allowClientSkyRiverMovement = true;
				break;
			case 2:
				SkyRiverManager.debugMessage("Client> StateChange: DENY");
				allowClientSkyRiverMovement = false;
				break;
			default:
				break;
		}
	}

	@SideOnly(Side.CLIENT)
	private static boolean doesClientTryToMove() {
		GameSettings settings = Minecraft.getMinecraft().gameSettings;
		if (settings.keyBindBack.getIsKeyPressed())
			return true;
		if (settings.keyBindForward.getIsKeyPressed())
			return true;
		if (settings.keyBindLeft.getIsKeyPressed())
			return true;
		if (settings.keyBindRight.getIsKeyPressed())
			return true;
		return false;
	}

	public static boolean stopSlowFall() {
		return !allowClientSkyRiverMovement;
	}

}
