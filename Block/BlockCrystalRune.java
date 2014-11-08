/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Auxiliary.Interfaces.ProgressionTrigger;
import Reika.ChromatiCraft.Base.BlockDyeTypes;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Render.Particle.EntityRuneFX;
import Reika.ChromatiCraft.TileEntity.TileEntityCrystalRepeater;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCrystalRune extends BlockDyeTypes implements ProgressionTrigger {

	public BlockCrystalRune(Material par2Material) {
		super(par2Material);
		blockHardness = 2;
		blockResistance = 5;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase e, ItemStack is) {
		int meta = world.getBlockMetadata(x, y, z);
		ReikaDyeHelper dye = ReikaDyeHelper.getColorFromDamage(meta);
		if (world.isRemote)
			ReikaParticleHelper.spawnColoredParticles(world, x, y, z, dye, 256);

		for (int k = 0; k < 6; k++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[k];
			for (int i = 1; i <= 5; i++) {
				TileEntity te = world.getTileEntity(x+dir.offsetX*i, y+dir.offsetY*i, z+dir.offsetZ*i);
				if (te instanceof TileEntityCrystalRepeater) {
					((TileEntityCrystalRepeater)te).validateStructure();
				}
			}
		}

		super.onBlockAdded(world, x, y, z);
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block id, int meta) {
		ReikaDyeHelper dye = ReikaDyeHelper.getColorFromDamage(meta);
		if (world.isRemote)
			ReikaParticleHelper.spawnColoredParticles(world, x, y, z, dye, 256);
		if (world.getBlock(x, y-1, z) == ChromaBlocks.PYLONSTRUCT.getBlockInstance()) {
			((BlockPylonStructure)ChromaBlocks.PYLONSTRUCT.getBlockInstance()).triggerBreakCheck(world, x, y-1, z);
		}

		for (int k = 0; k < 6; k++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[k];
			for (int i = 1; i <= 5; i++) {
				TileEntity te = world.getTileEntity(x+dir.offsetX*i, y+dir.offsetY*i, z+dir.offsetZ*i);
				if (te instanceof TileEntityCrystalRepeater) {
					((TileEntityCrystalRepeater)te).validateStructure();
				}
			}
		}

		super.breakBlock(world, x, y, z, id, meta);
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random r) {
		super.randomDisplayTick(world, x, y, z, r);
		int meta = world.getBlockMetadata(x, y, z);
		ReikaDyeHelper dye = ReikaDyeHelper.getColorFromDamage(meta);
		ReikaParticleHelper.spawnColoredParticles(world, x, y, z, dye, 8);
		//this.runeParticles(world, x, y, z, meta, r);
	}

	@SideOnly(Side.CLIENT)
	private void runeParticles(World world, int x, int y, int z, int meta, Random rand) {
		double r = 0.75;
		double dx = ReikaRandomHelper.getRandomPlusMinus(0, r);
		double dy = rand.nextDouble();
		double dz = ReikaRandomHelper.getRandomPlusMinus(0, r);
		while (ReikaMathLibrary.py3d(dx, 0, dz) < 0.65) {
			dx = ReikaRandomHelper.getRandomPlusMinus(0, r);
			dz = ReikaRandomHelper.getRandomPlusMinus(0, r);
		}

		CrystalElement e = CrystalElement.elements[meta];
		Minecraft.getMinecraft().effectRenderer.addEffect(new EntityRuneFX(world, x+dx+0.5, y+dy+0.5, z+dz+0.5, e));
	}

	@Override
	public String getIconFolder() {
		return "runes/backpng/";
	}

	@Override
	public boolean useNamedIcons() {
		return false;
	}

	@Override
	public int getRenderType() {
		return ChromatiCraft.proxy.runeRender;
	}

	@Override
	public ProgressStage[] getTriggers(EntityPlayer ep, World world, int x, int y, int z) {
		boolean use = ReikaWorldHelper.checkForAdjBlock(world, x, y, z, ChromaBlocks.PYLONSTRUCT.getBlockInstance()) != null;
		return use ? new ProgressStage[]{ProgressStage.RUNEUSE} : null;
	}

}
