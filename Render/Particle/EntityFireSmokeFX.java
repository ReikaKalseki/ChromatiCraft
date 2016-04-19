package Reika.ChromatiCraft.Render.Particle;

import net.minecraft.world.World;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;


public class EntityFireSmokeFX extends EntityBlurFX {

	public final int startColor;

	public EntityFireSmokeFX(World world, double x, double y, double z, int color) {
		super(world, x, y, z);
		startColor = color;
		this.setBasicBlend();
		this.setIcon(ChromaIcons.CLOUDGROUP_TRANS_BLUR);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		int c = ReikaColorAPI.mixColors(0x000000, startColor, Math.max(0, -0.1875F+particleAge/(float)particleMaxAge));
		this.setColor(c);
	}

}
