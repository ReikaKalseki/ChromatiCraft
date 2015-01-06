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
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import Reika.ChromatiCraft.ChromaClient;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Base.BlockChromaTile;
import Reika.DragonAPI.Libraries.ReikaAABBHelper;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaParticleHelper;
import Reika.DragonAPI.Libraries.Registry.ReikaPlantHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockDecoPlant extends BlockChromaTile implements IPlantable {

	private final IIcon[] front_icons = new IIcon[16];
	private final IIcon[] back_icons = new IIcon[16];

	public BlockDecoPlant(Material xMaterial) {
		super(xMaterial);
		stepSound = soundTypeGrass;
		blockHardness = 0;
		blockResistance = 0.5F;
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		switch(world.getBlockMetadata(x, y, z)) {
		case 0:
			return 15;
		default:
			return 0;
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
		return ReikaAABBHelper.getBlockAABB(x, y, z);
	}

	@Override
	public final boolean canPlaceBlockAt(World world, int x, int y, int z) {
		return super.canPlaceBlockAt(world, x, y, z) && this.canBlockStay(world, x, y, z);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block par5)
	{
		super.onNeighborBlockChange(world, x, y, z, par5);
		this.checkFlowerChange(world, x, y, z);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random r) {
		this.checkFlowerChange(world, x, y, z);
	}

	private final void checkFlowerChange(World world, int x, int y, int z) {
		if (!this.canBlockStay(world, x, y, z)) {
			this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			world.setBlock(x, y, z, Blocks.air, 0, 2);
		}
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		switch(world.getBlockMetadata(x, y, z)) {
		case 0:
			return ReikaPlantHelper.LILYPAD.canPlantAt(world, x, y, z);
		default:
			boolean light = world.getFullBlockLightValue(x, y, z) >= 8 || world.canBlockSeeTheSky(x, y, z);
			return ReikaPlantHelper.FLOWER.canPlantAt(world, x, y, z) && light;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random r) {
		switch(world.getBlockMetadata(x, y, z)) {
		case 0:
			double rx = ReikaRandomHelper.getRandomPlusMinus(x+0.5, 0.1875);
			double rz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, 0.1875);
			double ry = y+0.25+r.nextDouble()*0.75;
			ReikaParticleHelper.FLAME.spawnAt(world, rx, ry, rz);
		}
	}

	@Override
	public int getRenderType() {
		return ChromatiCraft.proxy.plantRender2;
	}

	@Override
	public boolean canRenderInPass(int pass) {
		ChromaClient.plant2.renderPass = pass;
		return pass <= 0;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	public IIcon getOverlay(int meta) {
		return front_icons[meta];
	}

	public IIcon getBacking(int meta) {
		return back_icons[meta];
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return this.getBacking(meta);
	}

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < 16; i++) {
			front_icons[i] = ico.registerIcon("chromaticraft:plant/decoplant_"+i+"_front");
			back_icons[i] = ico.registerIcon("chromaticraft:plant/decoplant_"+i+"_back");
		}
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, int x, int y, int z) {
		int meta = world.getBlockMetadata(x, y, z);
		switch(meta) {
		case 0:
			return EnumPlantType.Water;
		default:
			return EnumPlantType.Plains;
		}
	}

	@Override
	public Block getPlant(IBlockAccess world, int x, int y, int z) {
		return this;
	}

	@Override
	public int getPlantMetadata(IBlockAccess world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity e) {
		int meta = world.getBlockMetadata(x, y, z);
		switch(meta) {
		case 0:
			e.setFire(2);
		default:

		}
	}

}
