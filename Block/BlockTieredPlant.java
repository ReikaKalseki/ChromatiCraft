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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import Reika.ChromatiCraft.ChromaClient;
import Reika.ChromatiCraft.ChromatiCraft;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager;
import Reika.ChromatiCraft.Auxiliary.ProgressionManager.ProgressStage;
import Reika.ChromatiCraft.Render.Particle.EntityBlurFX;
import Reika.DragonAPI.Base.BlockTieredResource;
import Reika.DragonAPI.Libraries.Java.ReikaRandomHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class BlockTieredPlant extends BlockTieredResource {

	private final IIcon[] front_icons = new IIcon[16];
	private final IIcon[] back_icons = new IIcon[16];

	public BlockTieredPlant(Material mat) {
		super(mat);
		this.setCreativeTab(ChromatiCraft.tabChroma);
		this.setHardness(0);
		this.setResistance(2);
		stepSound = soundTypeGrass;
	}

	public static enum TieredPlants {

		FLOWER(ProgressStage.RUNEUSE),
		CAVE(ProgressStage.CRYSTALS),
		LILY(ProgressStage.PYLON),
		BULB(ProgressStage.MULTIBLOCK);

		public final ProgressStage level;

		public static final TieredPlants[] list = values();

		private TieredPlants(ProgressStage lvl) {
			level = lvl;
		}

		public boolean generate(World world, int x, int z, Random r) {

			return false;
		}

		public int getGenerationCount() {
			return 3;
		}

		public int getGenerationChance() {
			return 5;
		}
	}

	@Override
	public void getSubBlocks(Item item, CreativeTabs c, List li) {
		for (int i = 0; i < ProgressStage.values().length; i++) {
			li.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	protected Collection<ItemStack> getHarvestResources(World world, int x, int y, int z, int fortune) {
		ArrayList<ItemStack> li = new ArrayList();
		return li;
	}

	private ProgressStage getProgressStage(IBlockAccess world, int x, int y, int z) {
		return ProgressStage.CRYSTALS;//ProgressStage.values()[world.getBlockMetadata(x, y, z)]; //Testing
	}

	@Override
	public boolean isPlayerSufficientTier(IBlockAccess world, int x, int y, int z, EntityPlayer ep) {
		return ProgressionManager.instance.isPlayerAtStage(ep, this.getProgressStage(world, x, y, z));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public final AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
	{
		if (this.isPlayerSufficientTier(world, x, y, z, Minecraft.getMinecraft().thePlayer)) {
			return super.getSelectedBoundingBoxFromPool(world, x, y, z);
		}
		else {
			return AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0);
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public int getRenderType() {
		return ChromatiCraft.proxy.plantRender;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean canRenderInPass(int pass) {
		ChromaClient.plant.renderPass = pass;
		return pass <= 0;
	}

	@Override
	public int getRenderBlockPass() {
		return 0;
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random r) {
		if (world.getTotalWorldTime()%2 == 0 && this.isPlayerSufficientTier(world, x, y, z, Minecraft.getMinecraft().thePlayer)) {
			int meta = world.getBlockMetadata(x, y, z);
			switch(meta) {
			case 0: {
				double vx = ReikaRandomHelper.getRandomPlusMinus(0, 0.005);
				double vz = ReikaRandomHelper.getRandomPlusMinus(0, 0.005);
				int g = r.nextInt(255);
				EntityBlurFX fx = new EntityBlurFX(world, x+0.5, y+0.95, z+0.5, vx, 0, vz).setGravity(0.015F).setColor(0, g, 255).setScale(2);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
				break;
			}
			case 1: {
				float g = 0.02F+0.005F*r.nextFloat();
				EntityBlurFX fx = new EntityBlurFX(world, x+0.5, y+0.65, z+0.5, 0, 0, 0).setGravity(g).setScale(2);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
				break;
			}
			case 2: {
				float g = 0.04F+0.01F*r.nextFloat();
				int red = r.nextInt(255);
				EntityBlurFX fx = new EntityBlurFX(world, x+0.5, y+0.375, z+0.5, 0, 0, 0).setColor(red, 0, 255).setGravity(-g).setScale(4);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
				break;
			}
			case 3: {
				float g = 0.02F+0.005F*r.nextFloat();
				int gr = r.nextInt(255);
				double px = ReikaRandomHelper.getRandomPlusMinus(x+0.5, 0.25);
				double pz = ReikaRandomHelper.getRandomPlusMinus(z+0.5, 0.25);
				EntityBlurFX fx = new EntityBlurFX(world, px, y+0.75, pz, 0, 0, 0).setColor(255, gr, 0).setGravity(g).setScale(2);
				Minecraft.getMinecraft().effectRenderer.addEffect(fx);
				break;
			}
			}
		}
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

	/*

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int s) {
		return this.isPlayerSufficientTier(world, x, y, z, Minecraft.getMinecraft().thePlayer) ? this.getIcon(s, world.getBlockMetadata(x, y, z)) : ChromaIcons.TRANSPARENT.getIcon();
	}*/

	@Override
	public void registerBlockIcons(IIconRegister ico) {
		for (int i = 0; i < 16; i++) {
			front_icons[i] = ico.registerIcon("chromaticraft:plant/tierplant_"+i+"_front");
			back_icons[i] = ico.registerIcon("chromaticraft:plant/tierplant_"+i+"_back");
		}
	}

}
