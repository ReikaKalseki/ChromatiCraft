package Reika.ChromatiCraft.World.Dimension;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraftforge.client.IRenderHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ChromaCloudRenderer extends IRenderHandler {

	public static final ChromaCloudRenderer instance = new ChromaCloudRenderer();

	private ChromaCloudRenderer() {

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(float partialTicks, WorldClient world, Minecraft mc) {
		mc.renderGlobal.renderCloudsFancy(partialTicks);
	}

}
