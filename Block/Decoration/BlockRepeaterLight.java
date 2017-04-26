package Reika.ChromatiCraft.Block.Decoration;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.ChromatiCraft.Render.Particle.EntityRuneFX;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class BlockRepeaterLight extends Block {

	public static final ChromaTiles[] MODELS = {
		ChromaTiles.REPEATER, ChromaTiles.COMPOUND, ChromaTiles.BROADCAST, ChromaTiles.WEAKREPEATER
	};

	public static int getMetadataFor(ChromaTiles c) {
		for (int i = 0; i < MODELS.length; i++) {
			if (MODELS[i] == c)
				return i;
		}
		return -1;
	}

	public BlockRepeaterLight(Material mat) {
		super(mat);

		this.setResistance(60000);
		this.setHardness(2);
		this.setCreativeTab(ChromatiCraft.tabChromaDeco);
		stepSound = new SoundType("stone", 1.0F, 0.5F);
		this.setLightLevel(1);
		this.setLightOpacity(0);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		for (int i = 0; i < 8; i++) {
			double px = ReikaRandomHelper.getRandomPlusMinus(x+0.5, 0.675);
			double py = ReikaRandomHelper.getRandomPlusMinus(y+0.5, 0.675);
			double pz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, 0.675);
			EntityBlurFX fx = new EntityBlurFX(world, px, py, pz).setColor(0xffffff);
			fx.setLife(10).setGravity(0).setAlphaFading();
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
		if (MODELS[world.getBlockMetadata(x, y, z)] == ChromaTiles.COMPOUND && rand.nextInt(8) == 0) {
			EntityRuneFX fx = new EntityRuneFX(world, x+0.5, y+0.5, z+0.5, CrystalElement.randomElement()).setScale(5).setFading().setGravity(0);
			Minecraft.getMinecraft().effectRenderer.addEffect(fx);
		}
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return MODELS[meta].getBlock().getIcon(s, MODELS[meta].getBlockMetadata());
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess iba, int x, int y, int z, int s) {
		return super.shouldSideBeRendered(iba, x, y, z, s) && iba.getBlock(x, y, z) != this;
	}

}
