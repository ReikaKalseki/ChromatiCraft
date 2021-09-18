/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Decoration;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ChromaFX;
import Reika.ChromatiCraft.Auxiliary.CrystalMusicManager;
import Reika.ChromatiCraft.Base.CrystalTypeBlock;
import Reika.ChromatiCraft.Block.Dimension.Structure.Music.BlockMusicMemory;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.DragonAPI.Interfaces.Block.SemiUnbreakable;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockMusicTrigger extends Block implements SemiUnbreakable {

	private static final Random rand = new Random();

	private final IIcon[] icons = new IIcon[2];

	public BlockMusicTrigger(Material mat) {
		super(mat);

		this.setHardness(6);
		this.setResistance(60000);
		this.setCreativeTab(ChromatiCraft.tabChromaGen);
	}

	public boolean isUnbreakable(World world, int x, int y, int z, int meta) {
		return world.provider.dimensionId == ExtraChromaIDs.DIMID.getValue();
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return s <= 1 ? icons[0] : icons[1];
	}

	@Override
	public final void onNeighborBlockChange(World world, int x, int y, int z, Block b) {
		if (world.isBlockIndirectlyGettingPowered(x, y, z)) {
			this.ping(world, x, y, z, world.getBlockPowerInput(x, y, z)/4);
		}
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		icons[0] = ico.registerIcon("chromaticraft:dimstruct/musictrigger");
		icons[1] = ico.registerIcon("chromaticraft:dimstruct/musictrigger_side");
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer ep, int s, float a, float b, float c) {
		if (s > 1) {
			int idx = this.getIndex(s, a, b, c);
			if (idx >= 0) {
				this.ping(world, x, y, z, idx);
			}
		}
		return true;
	}

	private void ping(World world, int x, int y, int z, int idx) {
		Block bk = world.getBlock(x, y+1, z);
		if (bk instanceof CrystalTypeBlock) {
			int meta = world.getBlockMetadata(x, y+1, z);
			CrystalElement e = CrystalElement.elements[meta];
			float p = CrystalMusicManager.instance.getScaledDing(e, idx);
			CrystalTypeBlock.ding(world, x, y, z, e, p);
			if (world.provider.dimensionId == ExtraChromaIDs.DIMID.getValue())
				BlockMusicMemory.ping(world, x, y, z, e, idx);
			if (world.isRemote) {
				this.createParticle(world, x, y+1, z, e);
			}

		}
	}

	@SideOnly(Side.CLIENT)
	public static void createParticle(World world, int x, int y, int z, CrystalElement e) {
		double v = ReikaRandomHelper.getRandomPlusMinus(0.125, 0.0625);
		ChromaFX.doElementalParticle(world, x+0.5, y+0.5, z+0.5, e, 4, v, 20);

		for (int i = 0; i < 12; i++) {
			world.getBlock(x, y, z).randomDisplayTick(world, x, y, z, rand);
		}
	}

	private int getIndex(int s, float a, float b, float c) { //0-3 or -1 for none
		double m1a = 0.125;
		double m1b = 0.4375;
		double m2a = 0.5625;
		double m2b = 0.875;

		if (s == 2 || s == 5) {
			a = 1-a;
			c = 1-c;
		}

		if (s == 4 || s == 5) { //a == 0 or 1
			if (ReikaMathLibrary.isValueInsideBoundsIncl(m1a, m1b, c) && ReikaMathLibrary.isValueInsideBoundsIncl(m2a, m2b, b))
				return 0;
			if (ReikaMathLibrary.isValueInsideBoundsIncl(m2a, m2b, c) && ReikaMathLibrary.isValueInsideBoundsIncl(m2a, m2b, b))
				return 1;
			if (ReikaMathLibrary.isValueInsideBoundsIncl(m2a, m2b, c) && ReikaMathLibrary.isValueInsideBoundsIncl(m1a, m1b, b))
				return 2;
			if (ReikaMathLibrary.isValueInsideBoundsIncl(m1a, m1b, c) && ReikaMathLibrary.isValueInsideBoundsIncl(m1a, m1b, b))
				return 3;
		}
		else if (s == 2 || s == 3) { //c == 0 or 1
			if (ReikaMathLibrary.isValueInsideBoundsIncl(m1a, m1b, a) && ReikaMathLibrary.isValueInsideBoundsIncl(m2a, m2b, b))
				return 0;
			if (ReikaMathLibrary.isValueInsideBoundsIncl(m2a, m2b, a) && ReikaMathLibrary.isValueInsideBoundsIncl(m2a, m2b, b))
				return 1;
			if (ReikaMathLibrary.isValueInsideBoundsIncl(m2a, m2b, a) && ReikaMathLibrary.isValueInsideBoundsIncl(m1a, m1b, b))
				return 2;
			if (ReikaMathLibrary.isValueInsideBoundsIncl(m1a, m1b, a) && ReikaMathLibrary.isValueInsideBoundsIncl(m1a, m1b, b))
				return 3;
		}
		return -1;
	}

}
