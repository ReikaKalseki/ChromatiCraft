package Reika.ChromatiCraft.Auxiliary;

import java.lang.reflect.Field;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.network.NetHandlerPlayClient;

/** This class is a temporary workaround until the PlayerControllerMP class can be ASM'ed directly */
public class ControllableReachPlayer extends PlayerControllerMP {

	public ControllableReachPlayer(PlayerControllerMP ep) {
		super(Minecraft.getMinecraft(), getHandler(ep)); //Minecraft, NetHandlerPlayClient
		try {
			Field f = PlayerControllerMP.class.getDeclaredField("currentGameType");
			f.setAccessible(true);
			Object o = f.get(ep);
			f.set(this, o);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static NetHandlerPlayClient getHandler(PlayerControllerMP ep) {
		NetHandlerPlayClient handler = null;
		try {
			Field f = PlayerControllerMP.class.getDeclaredField("netClientHandler");
			f.setAccessible(true);
			handler = (NetHandlerPlayClient)f.get(ep);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return handler;
	}

	@Override
	public float getBlockReachDistance()
	{
		float prev = super.getBlockReachDistance();
		return Math.max(prev, AbilityVariables.playerReach);
	}

}
