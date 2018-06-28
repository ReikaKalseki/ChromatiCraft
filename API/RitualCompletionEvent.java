package Reika.ChromatiCraft.API;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;


public class RitualCompletionEvent extends PlayerEvent {

	public final String abilityID;

	public RitualCompletionEvent(EntityPlayer ep, String id) {
		super(ep);

		abilityID = id;
	}

}
