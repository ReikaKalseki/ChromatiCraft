package Reika.ChromatiCraft;

import Reika.ChromatiCraft.Registry.ChromaGuis;
import Reika.DragonAPI.Auxiliary.TickRegistry.TickHandler;
import Reika.DragonAPI.Auxiliary.TickRegistry.TickType;

import net.minecraft.client.Minecraft;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class ChromaTriggerHandler implements TickHandler {

	public static final ChromaTriggerHandler instance = new ChromaTriggerHandler();
	private final Minecraft mc = Minecraft.getMinecraft();

	private ChromaTriggerHandler() {

	}

	private boolean triggerKeys;

	@Override
	public void tick(Object... tickData) {
		if (this.triggerKeys()) {
			if (mc.thePlayer.capabilities.isCreativeMode) //for now
				mc.thePlayer.openGui(ChromatiCraft.instance, ChromaGuis.ABILITY.ordinal(), mc.theWorld, 0, 0, 0);
		}
	}

	private boolean triggerKeys() {
		boolean flag = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && Keyboard.isKeyDown(Keyboard.KEY_LMENU);
		boolean ret = flag && !triggerKeys;
		triggerKeys = flag;
		return ret;
	}

	@Override
	public TickType getType() {
		return TickType.CLIENT;
	}

	@Override
	public Phase getPhase() {
		return Phase.START;
	}

	@Override
	public String getLabel() {
		return "Chroma Trigger";
	}

}
