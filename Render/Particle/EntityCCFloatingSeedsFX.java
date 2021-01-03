package Reika.ChromatiCraft.Render.Particle;

import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.DragonAPI.Instantiable.Effects.EntityFloatingSeedsFX;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;


public class EntityCCFloatingSeedsFX extends EntityFloatingSeedsFX {

	private float cyclescale;

	public EntityCCFloatingSeedsFX(World world, double x, double y, double z, double windAng, double climbAng, ChromaIcons c) {
		super(world, x, y, z, windAng, climbAng, c.getIcon());
	}

	public EntityCCFloatingSeedsFX(World world, double x, double y, double z, double windAng, double climbAng) {
		super(world, x, y, z, windAng, climbAng, ChromaIcons.FADE.getIcon());
		this.setIcon();
	}

	private void setIcon() {
		IIcon ico = ChromaIcons.FADE.getIcon();
		switch(rand.nextInt(4)) {
			case 1:
				ico = ChromaIcons.BIGFLARE.getIcon();
				break;
			case 2:
				ico = ChromaIcons.SPARKLEPARTICLE.getIcon();
				this.setBasicBlend();
				this.enableAlphaTest();
				break;
			case 3:
				ico = ChromaIcons.CENTER.getIcon();
				break;
		}
		this.setParticleIcon(ico);
	}

	public final EntityCCFloatingSeedsFX setIcon(ChromaIcons c) {
		super.setIcon(c.getIcon());
		if (c.isTransparent()) {
			this.setBasicBlend();
		}
		else {
			this.setAdditiveBlend();
		}
		return this;
	}

	public final EntityCCFloatingSeedsFX setCyclingColor(float scale) {
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
