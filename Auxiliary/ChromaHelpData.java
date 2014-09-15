package Reika.ChromatiCraft.Auxiliary;

import java.util.HashMap;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import Reika.DragonAPI.Instantiable.Data.CoordinateData;

public class ChromaHelpData {

	private static final HashMap<PlayerEntry, HelpEntry> data = new HashMap();

	private static PlayerEntry getPlayerEntry(EntityPlayer ep, CoordinateData dat) {
		return new PlayerEntry(ep.getUniqueID(), dat);
	}

	//public static boolean hasEntryFor(EntityPlayer ep, CoordinateData dat) {
	//	return data.containsKey(getPlayerEntry(ep, dat));
	//}

	public static HelpEntry getEntryFor(EntityPlayer ep, CoordinateData dat) {
		return data.get(getPlayerEntry(ep, dat));
	}

	private static class PlayerEntry {

		public final UUID uid;
		public final CoordinateData data;

		public PlayerEntry(UUID id, CoordinateData dat) {
			uid = id;
			data = dat;
		}

		@Override
		public boolean equals(Object o) {
			if (o instanceof PlayerEntry) {
				PlayerEntry p = (PlayerEntry)o;
				return p.uid.equals(uid) && p.data.equals(data);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return uid.hashCode()^data.hashCode();
		}

	}

	public static class HelpEntry {

		public void render(RenderGameOverlayEvent evt) {

		}

		public String getTitle() {
			return "Test";
		}

	}

}
