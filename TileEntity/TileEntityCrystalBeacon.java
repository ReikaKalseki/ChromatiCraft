package Reika.ChromatiCraft.TileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import Reika.ChromatiCraft.Base.TileEntity.TileEntityChromaticBase;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityGlobeFX;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityCrystalBeacon extends TileEntityChromaticBase {

	@Override
	public ChromaTiles getTile() {
		return ChromaTiles.BEACON;
	}

	@Override
	public void updateEntity(World world, int x, int y, int z, int meta) {
		if (world.isRemote)
			this.spawnParticles(world, x, y, z);
	}

	@SideOnly(Side.CLIENT)
	private void spawnParticles(World world, int x, int y, int z) {
		double ang = Math.toRadians((this.getTicksExisted()*4)%360);
		for (int i = 0; i < CrystalElement.elements.length; i++) {
			CrystalElement e = CrystalElement.elements[i];
			//int da = 120;
			//int n = 360/da;
			//for (int i = 0; i < 360; i += da) {
			double r = 0.75;
			double a = ang+i*10;
			double dx = x+0.5+r*Math.sin(a);
			double dz = z+0.5+r*Math.cos(a);
			double dy = y+(((this.getTicksExisted()+i*20)/4)%80)/40D;

			EntityGlobeFX fx = new EntityGlobeFX(e, world, dx, dy, dz, 0, 0, 0);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
		//}
	}

	@Override
	protected void animateWithTick(World world, int x, int y, int z) {

	}

}
