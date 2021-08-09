package Reika.ChromatiCraft.Block.Dimension;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaIcons;
import Reika.ChromatiCraft.Render.Particle.EntityCCBlurFX;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Rendering.ReikaColorAPI;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class BlockVoidCave extends Block {

	public BlockVoidCave(Material mat) {
		super(mat);
		this.setResistance(900000);
		this.setBlockUnbreakable();
		this.setCreativeTab(ChromatiCraft.tabChromaGen);
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		blockIcon = ico.registerIcon("chromaticraft:dimgen/voidcave");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		int meta = world.getBlockMetadata(x, y, z);
		for (int i = 2; i < 6; i++) {
			if ((meta & (1 << (i-2))) != 0) {
				ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
				for (int n = 0; n < 16; n++) {
					double dx = x+0.5+dir.offsetX*0.5;
					double dy = ReikaRandomHelper.getRandomPlusMinus(y+0.5, 0.0625);
					double dz = z+0.5+dir.offsetZ*0.5;
					if (dir.offsetX == 0) {
						dx = ReikaRandomHelper.getRandomBetween(x, x+1D);
					}
					if (dir.offsetZ == 0) {
						dz = ReikaRandomHelper.getRandomBetween(z, z+1D);
					}
					double v = ReikaRandomHelper.getRandomBetween(0.04, 0.05);
					double vx = dir.offsetX*v;
					double vz = dir.offsetZ*v;
					float g = (float)ReikaRandomHelper.getRandomBetween(0.04, 0.07);
					int l = ReikaRandomHelper.getRandomBetween(60, 180);
					float s = (float)ReikaRandomHelper.getRandomBetween(1.5, 3);
					int base = ReikaColorAPI.mixColors(0xffffff, 0x22aaff, rand.nextFloat()*0.5F);
					base = ReikaColorAPI.getModifiedHue(base, ReikaRandomHelper.getRandomPlusMinus(ReikaColorAPI.getHue(base), 30));
					int c = ReikaColorAPI.getColorWithBrightnessMultiplier(base, (float)ReikaRandomHelper.getRandomBetween(0.5, 1));
					EntityCCBlurFX fx = new EntityCCBlurFX(world, dx, dy, dz, vx, 0, vz);
					fx.setGravity(g).setLife(l).setScale(s).setColor(c).setColliding().setRapidExpand().setAlphaFading().forceIgnoreLimits();
					fx.setIcon(ChromaIcons.FADE_LIQUID);
					Minecraft.getMinecraft().effectRenderer.addEffect(fx);
				}
			}
		}
	}

}
