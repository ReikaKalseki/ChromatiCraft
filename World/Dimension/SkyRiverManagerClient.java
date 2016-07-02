package Reika.ChromatiCraft.World.Dimension;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.DragonAPI.Instantiable.Data.Immutable.DecimalPosition;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SkyRiverManagerClient {

	private static boolean allowClientSkyRiverMovement = false;

	@SideOnly(Side.CLIENT)
	public static void handleSkyRiverMovementClient() {
		if (!allowClientSkyRiverMovement) {
			return;
		}

		World w = Minecraft.getMinecraft().theWorld;
		if (w == null || w.provider.dimensionId != ExtraChromaIDs.DIMID.getValue())
			return;
		EntityPlayer pl = Minecraft.getMinecraft().thePlayer;
		SkyRiverGenerator.RiverPoint closest = SkyRiverGenerator.getClosestPoint(pl, 16, false);
		if (closest != null) {
			if (SkyRiverGenerator.isWithinSkyRiver(pl, closest)) {
				SkyRiverManager.movePlayer(pl, closest);
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

}
