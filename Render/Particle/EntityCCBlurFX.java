package Reika.ChromatiCraft.Render.Particle;

import net.minecraft.world.World;

import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Effects.EntityBlurFX;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;


public class EntityCCBlurFX extends EntityBlurFX {

	private float cyclescale;

	public EntityCCBlurFX(World world, double x, double y, double z) {
		this(world, x, y, z, 0, 0, 0);
	}

	public EntityCCBlurFX(World world, double x, double y, double z, double vx, double vy, double vz) {
		this(CrystalElement.WHITE, world, x, y, z, vx, vy, vz);
	}

	public EntityCCBlurFX(CrystalElement e, World world, double x, double y, double z, double vx, double vy, double vz) {
		super(world, x, y, z, vx, vy, vz, ChromaIcons.FADE.getIcon());
		this.setIcon(ChromaIcons.FADE);
		this.setColor(e.getColor());
	}

	public final EntityCCBlurFX setIcon(ChromaIcons c) {
		super.setIcon(c.getIcon());
		if (c.isTransparent()) {
			this.setBasicBlend();
		}
		else {
			this.setAdditiveBlend();
		}
		return this;
	}

	public final EntityCCBlurFX setCyclingColor(float scale) {
		cyclescale = scale;
		return this;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		if (cyclescale > 0) {
			int age = Math.max(particleAge, 1);
			//CrystalElement e = CrystalElement.elements[(int)((age*cyclescale)%16)];
			int c = CrystalElement.getBlendedColor((int)(age*cyclescale), 5);
			particleRed = ReikaColorAPI.getRed(c)/255F;
			particleGreen = ReikaColorAPI.getGreen(c)/255F;
			particleBlue = ReikaColorAPI.getBlue(c)/255F;
		}
	}

}
