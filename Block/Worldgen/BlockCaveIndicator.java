/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.ChromatiCraft.Block.Worldgen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Registry.ChromaISBRH;
import Reika.ChromatiCraft.Registry.ChromaSounds;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.KeySignature;
import Reika.DragonAPI.Libraries.MathSci.ReikaMusicHelper.MusicKey;
import Reika.DragonAPI.Libraries.World.ReikaWorldHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCaveIndicator extends Block {

	public static IIcon topIcon;
	public static IIcon innerTexture;
	public static IIcon innerTexture_Active;

	public BlockCaveIndicator(Material mat) {
		super(mat);
		this.setCreativeTab(ChromatiCraft.tabChromaGen);
		this.setHardness(Blocks.stone.blockHardness);
		this.setResistance(Blocks.stone.blockResistance/3F);
	}

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z) == 1 ? 10 : 0;
	}

	@Override
	public IIcon getIcon(int s, int meta) {
		return s == 1 ? topIcon : Blocks.stone.getIcon(0, 0);
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int s) {
		return this.getIcon(s, world.getBlockMetadata(x, y, z));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister ico) {
		innerTexture = ico.registerIcon("chromaticraft:caveindicator_inner_inactive");
		innerTexture_Active = ico.registerIcon("chromaticraft:caveindicator_inner");
		topIcon = ico.registerIcon("chromaticraft:caveindicator_top");
	}

	@Override
	public int getRenderType() {
		return ChromaISBRH.caveIndicator.getRenderID();
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		world.setBlockMetadataWithNotify(x, y, z, 0, 3);
	}

	@Override
	public void onFallenUpon(World world, int x, int y, int z, Entity e, float dist) {
		this.trigger(world, x, y, z);
	}

	@Override
	public void onEntityWalking(World world, int x, int y, int z, Entity e) {
		this.trigger(world, x, y, z);
	}

	private void trigger(World world, int x, int y, int z) {
		if (world.isRemote)
			return;
		float f = (float)MusicKey.C5.getInterval(KeySignature.C.getScale().get(world.rand.nextInt(7)).ordinal()).getRatio(MusicKey.C5);
		ChromaSounds.DING_HI.playSoundAtBlock(world, x, y, z, 0.5F, f);
		if (world.getBlockMetadata(x, y, z) == 0) {
			world.setBlockMetadataWithNotify(x, y, z, 1, 3);
			world.markBlockForUpdate(x, y, z);
			world.func_147451_t(x, y, z);
			ReikaWorldHelper.causeAdjacentUpdates(world, x, y, z);
		}
		else if (world instanceof WorldServer) {
			ReikaWorldHelper.cancelScheduledTick((WorldServer)world, x, y, z, this);
		}
		world.scheduleBlockUpdate(x, y, z, this, 100+world.rand.nextInt(200));
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess iba, int x, int y, int z, int side) {
		return iba.getBlockMetadata(x, y, z) > 0 ? 15 : 0;
	}

	@Override
	public Item getItemDropped(int id, Random r, int fortune) {
		return Blocks.stone.getItemDropped(id, r, fortune);
	}

	@Override
	public int getDamageValue(World world, int x, int y, int z) {
		return 0;
	}

	@Override
	public boolean canSilkHarvest() {
		return true;
	}
}
