/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
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
import Reika.ChromatiCraft.Base.BlockDyeTypes;
import Reika.ChromatiCraft.Magic.Interfaces.CrystalSource;
import Reika.ChromatiCraft.Registry.ChromaBlocks;
import Reika.ChromatiCraft.Registry.ChromaTiles;
import Reika.ChromatiCraft.Registry.CrystalElement;
import Reika.ChromatiCraft.Registry.ExtraChromaIDs;
import Reika.ChromatiCraft.Render.Particle.EntityRuneFX;
import Reika.DragonAPI.Base.TileEntityBase;
import Reika.DragonAPI.Interfaces.Block.SemiUnbreakable;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.MathSci.ReikaMathLibrary;
import Reika.DragonAPI.Libraries.Registry.ReikaDyeHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCrystalRune extends BlockDyeTypes implements SemiUnbreakable {

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

		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			Block b = world.getBlock(dx, dy, dz);
			if (b == ChromaBlocks.PYLONSTRUCT.getBlockInstance()) {
				((BlockPylonStructure)b).triggerAddCheck(world, dx, dy, dz);
			}
		}
		if (e instanceof EntityPlayer && ReikaWorldHelper.checkForAdjBlockWithCorners(world, x, y, z, ChromaBlocks.PYLONSTRUCT.getBlockInstance()) != null) {
			if (ReikaWorldHelper.findNearBlock(world, x, y, z, 6, ChromaTiles.TABLE.getBlock(), ChromaTiles.TABLE.getBlockMetadata()))
				ProgressStage.RUNEUSE.stepPlayerTo((EntityPlayer)e);
		}
		super.onBlockAdded(world, x, y, z);
	}

	@Override
	public float getPlayerRelativeBlockHardness(EntityPlayer ep, World world, int x, int y, int z) {
		if (world.provider.dimensionId == ExtraChromaIDs.DIMID.getValue())
			return -1;
		TileEntity te = world.getTileEntity(x, y+1, z);
		if (!(te instanceof TileEntityBase))
			return super.getPlayerRelativeBlockHardness(ep, world, x, y, z);
		if (te instanceof CrystalSource)
			return -1;
		return ((TileEntityBase)te).isPlacer(ep) ? super.getPlayerRelativeBlockHardness(ep, world, x, y, z) : -1;
	}

	@Override
	public void breakBlock(World world, int x, int y, int z, Block id, int meta) {
		ReikaDyeHelper dye = ReikaDyeHelper.getColorFromDamage(meta);
		if (world.isRemote)
			ReikaParticleHelper.spawnColoredParticles(world, x, y, z, dye, 256);

		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int dx = x+dir.offsetX;
			int dy = y+dir.offsetY;
			int dz = z+dir.offsetZ;
			Block b = world.getBlock(dx, dy, dz);
			if (b == ChromaBlocks.PYLONSTRUCT.getBlockInstance()) {
				((BlockPylonStructure)b).triggerBreakCheck(world, dx, dy, dz);
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
	public boolean isUnbreakable(World world, int x, int y, int z, int meta) {
		return world.provider.dimensionId == ExtraChromaIDs.DIMID.getValue();
	}

}
