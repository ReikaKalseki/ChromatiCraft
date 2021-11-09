package Reika.ChromatiCraft.ModInterface.ThaumCraft;

import net.minecraft.entity.player.EntityPlayer;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.GUI.GuiAbilitySelect;
import Reika.ChromatiCraft.Registry.ChromaPackets;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.ChromatiCraft.Registry.Chromabilities;
import Reika.DragonAPI.Instantiable.IO.PacketTarget;
import Reika.DragonAPI.Libraries.IO.ReikaPacketHelper;
import Reika.DragonAPI.Libraries.IO.ReikaSoundHelper;


public class GuiAbilityFocus extends GuiAbilitySelect {

	public GuiAbilityFocus(EntityPlayer ep) {
		super(ep);
	}

	@Override
	protected void selectAbility() {
		if (ability != null && Chromabilities.playerHasAbility(player, ability)) {
			ReikaPacketHelper.sendStringIntPacket(ChromatiCraft.packetChannel, ChromaPackets.ABILITYFOCUS.ordinal(), PacketTarget.server, ability.getID(), data);
			ReikaSoundHelper.playClientSound(ChromaSounds.GUICLICK, player, 0.75F, 1);
			player.closeScreen();
		}
	}

}
