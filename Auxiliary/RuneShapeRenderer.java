package Reika.ChromatiCraft.Auxiliary;

import java.util.Map;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import Reika.ChromatiCraft.Magic.RuneShape;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Data.Coordinate;
import Reika.DragonAPI.Libraries.IO.ReikaGuiAPI;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RuneShapeRenderer {

	public static final RuneShapeRenderer instance = new RuneShapeRenderer();

	private final RenderBlocks render = new RenderBlocks();
	private final RenderItem itemrender = new RenderItem();

	private RuneShapeRenderer() {

	}

	public void render(RuneShape s) {
		Map<Coordinate, CrystalElement> map = s.getData();
		for (Coordinate c : map.keySet()) {
			CrystalElement e = map.get(c);
			int meta = e.ordinal();
			double x = 0;//(int)(System.currentTimeMillis()/40%32);
			double y = 0;
			double z = 0;
			GL11.glPushMatrix();
			//render.renderBlockAsItem(ChromaBlocks.RUNE.getBlockInstance(), meta, 1);
			ReikaGuiAPI.instance.drawItemStack(itemrender, new ItemStack(ChromaBlocks.RUNE.getBlockInstance(), 1, meta), 0, 0);
			GL11.glPopMatrix();
		}
	}

}
