package Reika.ChromatiCraft.API.Interfaces;

import java.lang.reflect.Method;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;

public class OrePings {

	public static interface OrePingDelegate {

		public boolean match(Block b, int meta);

		public int getColor();

		public boolean isVisible(EntityPlayer ep);

		public IIcon getIcon();

	}

	public static void addBlockForOrePing(Block b, OrePingDelegate delegate) {
		addBlockForOrePing(b, -1, delegate);
	}

	public static void addBlockForOrePing(Block b, int meta, OrePingDelegate delegate) {
		try {
			Class c = Class.forName("Reika.ChromatiCraft.Auxiliary.Render.OreOverlayRenderer");
			Method m = c.getMethod("addBlockDelegate", Block.class, int.class, OrePingDelegate.class);
			m.invoke(null, b, meta, delegate);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}